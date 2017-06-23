/*
 * Copyright 2017
 * AB Language Technology
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

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.component.CasDumpWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TeiReaderEqualityTest{
		
    @Test
    public void testReading() throws Exception {
    	TestUtils._tei_expectations.stream().filter(x -> x != null).map(x -> x.filename).forEach(fname -> {
    		URL fullname = ClassLoader.getSystemClassLoader().getResource(fname);
			String dname = new File(fullname.toString()).getParent();
			try {
				testSameContent(fname, dname);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
    	});
    }
    
    public void testSameContent(String fname, String dname) throws Exception{
		System.out.println(fname);
		System.out.println(dname);

		String dump_out1 = new File(TestUtils._temp_folder, fname + "_" + TeiReader.class.getSimpleName() + ".txt").getAbsolutePath();
		String dump_out2 = new File(TestUtils._temp_folder, fname + "_" + TeiReaderJsoup.class.getSimpleName() + ".txt").getAbsolutePath();
        
		CollectionReaderDescription reader1 = createReaderDescription(
        		TeiReader.class, 
                TeiReader.PARAM_SOURCE_LOCATION, dname,
                TeiReader.PARAM_PATTERNS, fname);
        AnalysisEngineDescription dumper1 = createEngineDescription(
        		CasDumpWriter.class,
                CasDumpWriter.PARAM_OUTPUT_FILE,  dump_out1);
        runPipeline(reader1, dumper1);
        
		CollectionReaderDescription reader2 = createReaderDescription(
				TeiReaderJsoup.class, 
				TeiReaderJsoup.PARAM_SOURCE_LOCATION, dname,
				TeiReaderJsoup.PARAM_PATTERNS, fname);
        AnalysisEngineDescription dumper2 = createEngineDescription(
        		CasDumpWriter.class,
                CasDumpWriter.PARAM_OUTPUT_FILE,  dump_out1);
        runPipeline(reader2, dumper2);
        
        Assert.assertTrue("The files differ!", FileUtils.contentEquals(new File(dump_out1), new File(dump_out2)));
    }


    @Before
    public void setupLogging()
    {
        System.setProperty("org.apache.uima.logger.class", "org.apache.uima.util.impl.Log4jLogger_impl");
    }
}
