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
import java.net.URL;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.component.CasDumpWriter;
import org.junit.Before;
import org.junit.Test;

public class TeiReaderTest{
		
    @Test
    public void testReading() throws Exception {
    	TestUtils._test_files.stream().filter(x -> x != null).forEach(fname -> {
    		URL fullname = ClassLoader.getSystemClassLoader().getResource(fname);
			String dname = new File(fullname.toString()).getParent();
			try {
				//testReading(fname, dname, null);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
    	});
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
        
        String dump_out = new File(TestUtils._temp_folder, fname + ".txt").getAbsolutePath();
        
        AnalysisEngineDescription dumper = createEngineDescription(
        		CasDumpWriter.class,
                CasDumpWriter.PARAM_OUTPUT_FILE,  '-'); //dump_out);  //
        
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
