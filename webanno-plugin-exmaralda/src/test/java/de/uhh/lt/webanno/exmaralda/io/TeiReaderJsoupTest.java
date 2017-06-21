/*
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab and FG Language Technology
 * Technische Universität Darmstadt
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

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.component.CasDumpWriter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.uhh.lt.webanno.exmaralda.type.Segment;
import de.uhh.lt.webanno.exmaralda.type.TEIspan;
import de.uhh.lt.webanno.exmaralda.io.PartiturIndex;
import de.uhh.lt.webanno.exmaralda.io.TeiMetadata;

public class TeiReaderJsoupTest{
	
	@Test
	@Ignore
    public void testJSoup() throws Exception {
		String fname = "RudiVoellerWutausbruch_ISO_HIAT_formatted.xml";
		URL fullname = ClassLoader.getSystemClassLoader().getResource(fname);
		InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(fname);
        Document soup = Jsoup.parse(in, Charset.defaultCharset().name(), fullname.toString());
        Elements text_elements = soup.getElementsByTag("text");
        Assert.assertSame(text_elements.size(), 1);
        Element text_element = text_elements.first();
        Elements annotation_blocks = text_element.getElementsByTag("annotationBlock");
        Assert.assertTrue(annotation_blocks.size() == 19);
        for(Element annotation_block : annotation_blocks){
        	Elements seg_elements = annotation_block.getElementsByTag("seg");
        	for(Element seg_element : seg_elements){
	        	boolean empty = true;
	        	for(Element element : seg_element.getAllElements()){
	        		if("w".equals(element.tagName()) || "pc".equals(element.tagName())){
	        			System.out.format("%s ", element.text());
	        			empty = false;
	        		}
	        	}
	        	if(!empty)
	        		System.out.println();
        	}
        }
    }
	
    @Test
    public void testReading() throws Exception {
    	TestUtils._test_files.stream().filter(x -> x != null).forEach(fname -> {
    		URL fullname = ClassLoader.getSystemClassLoader().getResource(fname);
			String dname = new File(fullname.toString()).getParent();
			try {
				testReading(fname, dname, null);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
    	});
    }
    
    
    
    public void testReading(String fname, String dname, String expected) throws Exception{
		System.out.println(fname);
		System.out.println(dname);
        CollectionReaderDescription reader = createReaderDescription(
        		TeiReaderJsoup.class, 
        		TeiReaderJsoup.PARAM_SOURCE_LOCATION, dname,
        		TeiReaderJsoup.PARAM_PATTERNS, fname);

        
//        String dump_out = "-";
        String dump_out = new File(TestUtils._temp_folder, fname + ".txt").getAbsolutePath();
        AnalysisEngineDescription dumper = createEngineDescription(
        		CasDumpWriter.class,
                CasDumpWriter.PARAM_OUTPUT_FILE,  dump_out);
        
        AnalysisEngineDescription printer = createEngineDescription(TestUtils.SegPrint.class);

        runPipeline(reader, dumper, printer);
        
        System.out.format("Dumped CAS to '%s'.", dump_out);
    }


    @Before
    public void setupLogging()
    {
        System.setProperty("org.apache.uima.logger.class", "org.apache.uima.util.impl.Log4jLogger_impl");
    }
}
