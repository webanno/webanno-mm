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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.wicket.Application;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.mock.MockWebRequest;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.request.resource.PartWriterCallback;
import org.apache.wicket.response.StringResponse;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.lang.Classes;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.resource.ResourceStreamWrapper;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudarmstadt.ukp.clarin.webanno.model.Mediaresource;

/**
 * Represents a streamable media resource.
 * This file is partly adapted from {@code PackageResource}
 */
public abstract class MediaResourceStreamResource extends AbstractResource
{

	private static final long serialVersionUID = 4795034023091616055L;

	private static final Logger LOG = LoggerFactory.getLogger(MediaResourceStreamResource.class);

	public static final String PAGE_PARAM_PROJECT_ID =  "pid";
	public static final String PAGE_PARAM_FILE_ID = "fid";

	/**
	 * A flag indicating whether {@code ITextResourceCompressor} can be used to compress this
	 * resource. Default is {@code false} because this resource may be used for binary data (e.g. an
	 * image). Specializations of this class should change this flag appropriately.
	 */
	private boolean compress = false;

	/**
	 * text encoding (may be null) - only makes sense for character-based resources
	 */
	private String textEncoding = null;

	/**
	 * Reads the resource buffered - the content is copied into memory
	 */
	private boolean readBuffered = true;

	/**
	 * Hidden constructor.
	 * 
	 * @param locale
	 *            The locale of the resource
	 * @param style
	 *            The style of the resource
	 * @param variation
	 *            The component's variation (of the style)
	 */
	protected MediaResourceStreamResource()
	{

	}

	/**
	 * get text encoding (intented for character-based resources)
	 *
	 * @return custom encoding or {@code null} to use default
	 */
	public String getTextEncoding()
	{
		return textEncoding;
	}

	/**
	 * set text encoding (intented for character-based resources)
	 *
	 * @param textEncoding
	 *            custom encoding or {@code null} to use default
	 */
	public void setTextEncoding(final String textEncoding)
	{
		this.textEncoding = textEncoding;
	}
	
	public abstract Mediaresource getMediaresource(PageParameters params);
	public abstract File getMediaFile(Mediaresource media) throws IOException;

	/**
	 * creates a new resource response based on the request attributes
	 * 
	 * @param attributes
	 *            current request attributes from client
	 * @return resource response for answering request
	 */
	@Override
	protected ResourceResponse newResourceResponse(Attributes attributes)
	{
		final ResourceResponse resourceResponse = new ResourceResponse();
		
		Mediaresource media = getMediaresource(attributes.getParameters());
		// bail out if media could not be found
		if (media == null){
			return sendResourceError(attributes.getParameters(), resourceResponse, 404,
				"Unable to find resource");
		}

		// redirect if media is provided as url
		if(media.isProvidedAsURL()){
			// redirect
			String url = media.getName();
			RequestCycle.get().scheduleRequestHandlerAfterCurrent(new RedirectRequestHandler(url));
			resourceResponse.setStatusCode(302);
			return resourceResponse;
		}
		

		final IResourceStream resourceStream = internalGetResourceStream(media);

		// bail out if resource stream could not be found
		if (resourceStream == null)
		{
			return sendResourceError(attributes.getParameters(), resourceResponse, 404,
				"Unable to find resource");
		}
		
		resourceResponse.setFileName(media.getName());

		// add Last-Modified header (to support HEAD requests and If-Modified-Since)
		// but since this is a dynamic resource wicket needs to send it always 
		resourceResponse.setLastModified(Time.now());  

		if (resourceResponse.dataNeedsToBeWritten(attributes))
		{
			String contentType = resourceStream.getContentType();

			if (contentType == null && Application.exists())
			{
				contentType = Application.get().getMimeType(media.getContentType());
			}

			// set Content-Type (may be null)
			resourceResponse.setContentType(contentType);

			// set content encoding (may be null)
			resourceResponse.setTextEncoding(getTextEncoding());

			// supports accept range
			resourceResponse.setAcceptRange(ContentRangeType.BYTES);

			try
			{
				// read resource data to get the content length
				InputStream inputStream = resourceStream.getInputStream();

				byte[] bytes = null;
				// send Content-Length header
				if (readBuffered)
				{
					bytes = IOUtils.toByteArray(inputStream);
					resourceResponse.setContentLength(bytes.length);
				}
				else
				{
					resourceResponse.setContentLength(resourceStream.length().bytes());
				}

				// get content range information
				RequestCycle cycle = RequestCycle.get();
				Long startbyte = cycle.getMetaData(CONTENT_RANGE_STARTBYTE);
				Long endbyte = cycle.getMetaData(CONTENT_RANGE_ENDBYTE);

				// send response body with resource data
				PartWriterCallback partWriterCallback = new PartWriterCallback(bytes != null
					? new ByteArrayInputStream(bytes) : inputStream,
					resourceResponse.getContentLength(), startbyte, endbyte);

				// If read buffered is set to false ensure the part writer callback is going to
				// close the input stream
				resourceResponse.setWriteCallback(partWriterCallback.setClose(!readBuffered));
			}
			catch (IOException e)
			{
				LOG.debug(e.getMessage(), e);
				return sendResourceError(attributes.getParameters(), resourceResponse, 500, "Unable to read resource stream");
			}
			catch (ResourceStreamNotFoundException e)
			{
				LOG.debug(e.getMessage(), e);
				return sendResourceError(attributes.getParameters(), resourceResponse, 500, "Unable to open resource stream");
			}
			finally
			{
				try
				{
					if (readBuffered)
					{
						IOUtils.close(resourceStream);
					}
				}
				catch (IOException e)
				{
					LOG.warn("Unable to close the resource stream", e);
				}
			}
		}

		if(attributes.getParameters().getPosition("dl") >= 0)
			resourceResponse.setContentDisposition(ContentDisposition.ATTACHMENT);
		
		return resourceResponse;
	}

	/**
	 * Gives a chance to modify the resource going to be written in the response
	 * 
	 * @param attributes
	 *            current request attributes from client
	 * @param original
	 *            the original response
	 * @return the processed response
	 */
	protected byte[] processResponse(final Attributes attributes, final byte[] original)
	{
		return original;
	}

	/**
	 * send resource specific error message and write log entry
	 * 
	 * @param resourceResponse
	 *            resource response
	 * @param errorCode
	 *            error code (=http status)
	 * @param errorMessage
	 *            error message (=http error message)
	 * @return resource response for method chaining
	 */
	private ResourceResponse sendResourceError(PageParameters params, ResourceResponse resourceResponse, int errorCode,
		String errorMessage)
	{
		String msg = String.format(
			"resource [params = %s]: %s (status=%d)",
			params, errorMessage, errorCode);

		LOG.warn(msg);

		resourceResponse.setError(errorCode, errorMessage);
		return resourceResponse;
	}

	/**
	 * @return whether {@link org.apache.wicket.resource.ITextResourceCompressor} can be used to
	 *         compress the resource.
	 */
	public boolean getCompress()
	{
		return compress;
	}

	/**
	 * @param compress
	 *            A flag indicating whether the resource should be compressed.
	 */
	public void setCompress(boolean compress)
	{
		this.compress = compress;
	}
	
	private IResourceStream getIResourceStream(Mediaresource media){
		
		if(media == null){
			LOG.warn("No media supplied.");
			return null;
		}
		
		InputStream in;
		try {
			File file = getMediaFile(media);
			if(file == null)
				throw new FileNotFoundException();
			in = new BufferedInputStream(new FileInputStream(file));
		} catch (IOException e) {
			LOG.error(String.format("Could not find media %s (%s) for project %s.", media.getId(), media.getName(), media.getProject().getId()), e);
			return null;
		}
		
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
				return media.getContentType();
			}
			
			@Override
			public Bytes length() {
				return Bytes.bytes(media.getContentLength());
			}
			
			@Override
			public Time lastModifiedTime() {
				return Time.valueOf(media.getTimestamp());
			}

		};
	}
	

	private IResourceStream internalGetResourceStream(Mediaresource media)
	{
		IResourceStream resourceStream = getIResourceStream(media);
		if (resourceStream != null)
		{
			resourceStream = new ProcessingResourceStream(resourceStream);
		}
		return resourceStream;
	}

	/**
	 * An IResourceStream that processes the input stream of the original IResourceStream
	 */
	private class ProcessingResourceStream extends ResourceStreamWrapper
	{
		private static final long serialVersionUID = 1L;

		private ProcessingResourceStream(IResourceStream delegate)
		{
			super(delegate);
		}

		@Override
		public InputStream getInputStream() throws ResourceStreamNotFoundException
		{
			byte[] bytes = null;
			InputStream inputStream = super.getInputStream();

			if (readBuffered)
			{
				try
				{
					bytes = IOUtils.toByteArray(inputStream);
				}
				catch (IOException iox)
				{
					throw new WicketRuntimeException(iox);
				}
				finally
				{
					IOUtils.closeQuietly(this);
				}
			}

			RequestCycle cycle = RequestCycle.get();
			Attributes attributes;
			if (cycle != null)
			{
				attributes = new Attributes(cycle.getRequest(), cycle.getResponse());
			}
			else
			{
				// use empty request and response in case of non-http thread. WICKET-5532
				attributes = new Attributes(new MockWebRequest(Url.parse("")), new StringResponse());
			}
			if (bytes != null)
			{
				byte[] processedBytes = processResponse(attributes, bytes);
				return new ByteArrayInputStream(processedBytes);
			}
			else
			{
				return inputStream;
			}
		}
	}
	


	@Override
	public String toString()
	{
		final StringBuilder result = new StringBuilder();
		result.append('[')
			.append(Classes.simpleName(getClass()))
			.append(']');
		return result.toString();
	}


	/**
	 * If the package resource should be read buffered.<br>
	 * <br>
	 * WARNING - if the stream is not read buffered compressors will not work, because they require
	 * the whole content to be read into memory.<br>
	 * ({@link org.apache.wicket.javascript.IJavaScriptCompressor}, <br>
	 * {@link org.apache.wicket.css.ICssCompressor}, <br>
	 * {@link org.apache.wicket.resource.IScopeAwareTextResourceProcessor})
	 * 
	 * @param readBuffered
	 *            if the package resource should be read buffered
	 * @return the current package resource
	 */
	public MediaResourceStreamResource readBuffered(boolean readBuffered)
	{
		this.readBuffered = readBuffered;
		return this;
	}
}
