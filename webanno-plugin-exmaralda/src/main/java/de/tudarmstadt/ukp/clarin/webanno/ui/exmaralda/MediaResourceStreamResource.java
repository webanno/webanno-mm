/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.wicket.request.Response;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.io.Streams;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.lang.Checks;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.IResourceStreamWriter;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudarmstadt.ukp.clarin.webanno.model.Mediaresource;


/**
 * THIS FILE IS LMOST A COPY OF {@link ResourceStreamResource}
 */
public abstract class MediaResourceStreamResource extends AbstractResource
{
	private static final long serialVersionUID = 1L;
	
	public static final String PAGE_PARAM_PROJECT_ID = "pId";
	
	public static final String PAGE_PARAM_FILE_ID = "fId";

	private static final Logger logger = LoggerFactory.getLogger(MediaResourceStreamResource.class);
	
	private ContentDisposition contentDisposition = ContentDisposition.INLINE;
	
	private Duration cacheDuration;
		
	/**
	 * @param contentDisposition
	 * @return this object, for chaining
	 */
	public MediaResourceStreamResource setContentDisposition(ContentDisposition contentDisposition)
	{
		this.contentDisposition = contentDisposition;
		return this;
	}

	/**
	 * @return the duration for which the resource will be cached by the browser
	 */
	public Duration getCacheDuration()
	{
		return cacheDuration;
	}

	/**
	 * @param cacheDuration
	 *            the duration for which the resource will be cached by the browser
	 * @return this object, for chaining
	 */
	public MediaResourceStreamResource setCacheDuration(Duration cacheDuration)
	{
		this.cacheDuration = cacheDuration;
		return this;
	}

	/**
	 * Lazy or dynamic initialization of the wrapped IResourceStream(Writer)
	 * @return the underlying IResourceStream. May be {@code null}.
	 * @throws IOException 
	 */
	protected IResourceStream getResourceStream(File mfile, String contentType) throws IOException{
		
		InputStream in = new BufferedInputStream(new FileInputStream(mfile));	 
		
		return new AbstractResourceStream() {

			private static final long serialVersionUID = -5503347978101130835L;

			@Override
			public InputStream getInputStream() throws ResourceStreamNotFoundException {
				return in;
			}
			
			@Override
			public void close() throws IOException {
				in.close();
			}
			
			@Override
			public String getContentType() {
				return contentType;
			}
			
			@Override
			public Bytes length() {
				return Bytes.bytes(mfile.length());
			}
			
			@Override
			public Time lastModifiedTime() {
				return Time.valueOf(new Date(mfile.lastModified()));
			}

		};
		
	}

	private IResourceStream internalGetResourceStream(File mfile, String contentType) throws IOException
	{
		final IResourceStream resourceStream = getResourceStream(mfile, contentType);
		Checks.notNull(resourceStream, "%s#getResourceStream() should not return null!", getClass().getName());
		return resourceStream;
	}
	
	public abstract Mediaresource getMediaresource(PageParameters params);
	public abstract File getFile(Mediaresource mfile) throws IOException;

	@Override
	protected ResourceResponse newResourceResponse(Attributes attributes)
	{
		final ResourceResponse data = new ResourceResponse();
		data.setLastModified(Time.now());
		
		final PageParameters params = attributes.getParameters();
		if(params == null){
			logger.warn("No parameters provided.");
			data.setError(400);
			return data;
		}

		
		final Mediaresource mfile = getMediaresource(params);
		File file = null;
		try {
			file = getFile(mfile);
		} catch (IOException e) {
			logger.warn("Unable to retrieve file.");
			data.setError(500);
			return data;
		}

		IResourceStream resourceStream_ = null;
		try {
			resourceStream_ = internalGetResourceStream(file, mfile.getContentType());
		} catch (IOException e) {
			logger.error("Could not open stream.");
			data.setError(500);
			return data;
		}
		final IResourceStream resourceStream = resourceStream_;

		data.setLastModified(Time.valueOf(mfile.getTimestamp()));
		
		if (cacheDuration != null)
		{
			data.setCacheDuration(cacheDuration);
		}

		// performance check; don't bother to do anything if the resource is still cached by client
		if (data.dataNeedsToBeWritten(attributes))
		{
			InputStream inputStream = null;
			if (resourceStream instanceof IResourceStreamWriter == false)
			{
				try
				{
					inputStream = resourceStream.getInputStream();
				}
				catch (ResourceStreamNotFoundException e)
				{
					data.setError(404);
					close(resourceStream);
				}
			}

			data.setContentDisposition(contentDisposition);
			Bytes length = resourceStream.length();
			if (length != null)
			{
				data.setContentLength(length.bytes());
			}
			data.setFileName(mfile.getName());
			data.setContentType(mfile.getContentType());
			data.setContentLength(mfile.getContentLength());

			if (resourceStream instanceof IResourceStreamWriter)
			{
				data.setWriteCallback(new WriteCallback()
				{
					@Override
					public void writeData(Attributes attributes) throws IOException
					{
						((IResourceStreamWriter)resourceStream).write(attributes.getResponse().getOutputStream());
						close(resourceStream);
					}
				});
			}
			else
			{
				final InputStream s = inputStream;
				data.setWriteCallback(new WriteCallback()
				{
					@Override
					public void writeData(Attributes attributes) throws IOException
					{
						try
						{
							final Response response = attributes.getResponse();
							Streams.copy(s, response.getOutputStream());
						}
						finally
						{
							close(resourceStream);
						}
					}
				});
			}
		}

		return data;
	}

	private void close(IResourceStream stream)
	{
		try
		{
			stream.close();
		}
		catch (IOException e)
		{
			logger.error("Couldn't close ResourceStream", e);
		}
	}
}
