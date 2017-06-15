/*
 * Copyright 2017
 * Language Technology
 * Universität Hamburg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.uhh.lt.webanno.exmaralda.io;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasResourceCollectionReader_ImplBase;

/**
 * Reader for the EXMARaLDA TEI format
 *
 */
public class TeiReader extends JCasResourceCollectionReader_ImplBase {

	private static final Logger LOG = LoggerFactory.getLogger(TeiReader.class);
	

	@Override
	public void getNext(JCas textview)
			throws IOException, CollectionException
	{
		Resource res = nextFile();
		String fname = res.getResolvedUri().toString();
		LOG.info("Reading '{}'", fname);
		initCas(textview, res);

		InputStream is = null;
		try {
			is = res.getInputStream();
			// TODO: reconsider if this is really necessary
			JCas teiview = JCasUtil.getView(textview, "tei", true);
			teiview.setDocumentText(IOUtils.toString(is));
			closeQuietly(is);

			is = new BufferedInputStream(res.getInputStream());
			read(is, textview, fname);
		}
		catch (IOException e) {
			LOG.error("Reading '{}' failed.", fname);
			LOG.error("ERROR: {}:{} ", e.getClass().getName(), e.getMessage());
			Throwable cause = e.getCause(); 
			while(cause != null){
				LOG.error("ERROR cause: {}:{} ", cause.getClass().getName(), cause.getMessage());
				cause = cause.getCause();
			}
			throw new CollectionException(e);
		}
		finally {
			closeQuietly(is);
		}
	}

	private void read(InputStream is, JCas textview, String source) throws IOException  {
		// TODO: read tei file
	}

}
