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
import de.uhh.lt.webanno.exmaralda.io.TeiReader;

public class TeiReaderTest{
	
	static File _temp_folder;
	
	static List<String> _test_files = Arrays.asList(
	    	"RudiVoellerWutausbruch_ISO_HIAT_neu_formatted.xml",
			"01.01.02.01.04_1_ISO_HIAT_neu_formatted.xml",
			null);

	@BeforeClass
	public static void setupTest() throws IOException {
		TemporaryFolder f = new TemporaryFolder();
		f.create();
		_temp_folder = f.getRoot();
		System.out.println("created temporary folder: " + _temp_folder.getAbsolutePath());
	}
	
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
    	_test_files.stream().filter(x -> x != null).forEach(fname -> {
    		URL fullname = ClassLoader.getSystemClassLoader().getResource(fname);
			String dname = new File(fullname.toString()).getParent();
			try {
				testReading(fname, dname, null);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
    	});
    }
    
    public static class SegPrint extends JCasAnnotator_ImplBase{
		@Override
		public void process(JCas textview) throws AnalysisEngineProcessException {
			
			System.out.println("---Annotation IDs:---");
			JCasUtil.select(textview, Annotation.class).stream().forEach(x -> {
				
				System.out.format("%d: [%d,%d] (%s)%n", x.getAddress(), x.getBegin(), x.getEnd(), x.getClass().getSimpleName());
			});

			System.out.println("---Sentences:---");
			JCasUtil.select(textview, Sentence.class).stream().forEach(x -> {
				System.out.println(x.getCoveredText());
				System.out.println("\tTokens: [ " + JCasUtil.selectCovered(Token.class, x).stream().map(t -> "'" + t.getCoveredText() + "'").collect(Collectors.joining(", ")) + " ]");
			});
			
			// get metadata
			System.out.println("---metadata:---");
			TeiMetadata meta = TeiMetadata.getFromCasSafe(textview);
			System.out.format("spantypes: %s%n", meta.spantypes);
			System.out.format("speakers: %s%n", meta.speakers);
			System.out.format("timevalues: %s%n", meta.timeline);
			System.out.format("media files: %s%n", meta.media);
			System.out.format("properties: %s%n", meta.properties);
			System.out.format("listview_timevalue_speaker_anchoroffset_index: %s%n", meta.listview_speaker_timevalue_anchoroffset_index);
			
			
			// print segments
			System.out.println("---segments:---");
			JCasUtil.select(textview, Segment.class).stream().forEach(x -> System.out.print(x.getCoveredText()));
			
			System.out.println("---speaker 0:---");
			System.out.println(JCasUtil.getView(textview, meta.speakers.get(0).id + "_", false).getDocumentText());
			
			System.out.println("---speaker 1:---");
			System.out.println(JCasUtil.getView(textview, meta.speakers.get(1).id + "_", false).getDocumentText());
			
			// print span annotations
			System.out.println("---span annotations:---");
			JCasUtil.select(textview, TEIspan.class).stream().forEach(x -> System.out.format("%s: %s [%s] (%d, %d) %n", x.getSpanType(), x.getContent(), x.getCoveredText(), JCasUtil.selectCovering(Sentence.class,  x).size(), JCasUtil.selectCovered(Sentence.class,  x).size()));

			// prepare index
			PartiturIndex pindex = new PartiturIndex(meta, textview);
			
			// 
			System.out.println("---kind of timelineview:---");
			meta.timeline.subList(0, meta.timeline.size()-1).forEach(tv -> {
				meta.speakers.stream().forEach(spk -> {
					System.out.format("%s - %s: %s%n", tv.id, spk.n, pindex.getSpeakertextForTimevalue(spk, tv));					
					meta.spantypes.stream().forEach(stype -> {
						final String contents = pindex.selectSpeakerAnnotationsForTimevalue(spk, tv, TEIspan.class)
							.stream()
							.filter(x -> stype.equals(x.getSpanType()))
							.map(x -> x.getContent())
							.collect(Collectors.joining("; "));
						System.out.format("%s - %s[%s]: %s%n", tv.id, spk.n, stype,  contents);
					});
				});
			});
			
		}    	
    }
    
    
    public void testReading(String fname, String dname, String expected) throws Exception{
		System.out.println(fname);
		System.out.println(dname);
        CollectionReaderDescription reader = createReaderDescription(
        		TeiReader.class, 
                TeiReader.PARAM_SOURCE_LOCATION, dname,
                TeiReader.PARAM_PATTERNS, fname);

//        AnalysisEngineDescription writer = createEngineDescription(
//                TeiWriter.class,
//                TeiWriter.PARAM_TARGET_LOCATION, "target/test-output/oneway",
//                TeiWriter.PARAM_FILENAME_SUFFIX, ".xml",
//                TeiWriter.PARAM_STRIP_EXTENSION, true);
        
        String dump_out = new File(_temp_folder, fname + ".txt").getAbsolutePath();
        
        AnalysisEngineDescription dumper = createEngineDescription(
        		CasDumpWriter.class,
                CasDumpWriter.PARAM_OUTPUT_FILE,  '-'); //dump_out);  //
        
        AnalysisEngineDescription printer = createEngineDescription(SegPrint.class);

        runPipeline(reader, dumper, printer);
        
        System.out.format("Dumped CAS to '%s'.", dump_out);
    }


    @Before
    public void setupLogging()
    {
        System.setProperty("org.apache.uima.logger.class", "org.apache.uima.util.impl.Log4jLogger_impl");
    }
}
