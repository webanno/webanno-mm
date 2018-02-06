package de.uhh.lt.webanno.exmaralda.io;

import static java.util.Arrays.asList;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.admin.CASAdminException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.internal.ResourceManagerFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.util.CasCreationUtils;
import org.junit.Assert;
import org.junit.rules.TemporaryFolder;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasResourceCollectionReader_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.uhh.lt.webanno.exmaralda.io.HiatTeiMetadata.Speaker;
import de.uhh.lt.webanno.exmaralda.type.Incident;
import de.uhh.lt.webanno.exmaralda.type.Segment;
import de.uhh.lt.webanno.exmaralda.type.TEIspanAkz;
import de.uhh.lt.webanno.exmaralda.type.TEIspanEn;
import de.uhh.lt.webanno.exmaralda.type.TEIspanGeneric;
import de.uhh.lt.webanno.exmaralda.type.TEIspanK;
import de.uhh.lt.webanno.exmaralda.type.TEIspanSup;
import de.uhh.lt.webanno.exmaralda.type.Utterance;

public class TestUtils {
    
    static List<TeiExpectation> _tei_expectations = Arrays.asList(

      new TeiExpectation(){{
          filename = "RudiVoellerWutausbruch_ISO_HIAT_neu_formatted.xml";
          speakerabbreviations = new String[]{"N","WH","RV"};
          num_utterances = 19;
          num_segments = 73;
          num_segments_for_first_utterance = 16;
          
          mediaurls = new String[]{
                  "http://hdl.handle.net/11022/0000-0000-5084-0@WEBM",
                  "file:/C:/Users/fsnv625/Desktop/RudiVoellerWutausbruch.mpg", "file:/C:/Users/fsnv625/Desktop/RudiVoellerWutausbruch.wav",
                  "file:/C:/Users/fsnv625/Desktop/RudiVoellerWutausbruch.ogg","file:/C:/Users/fsnv625/Desktop/RudiVoellerWutausbruch.webm"};
          num_when = 151;
          num_incidents = 37;
          num_teispan = 150;
          spantypes = new String[]{"sup", "akz", "en", "k"};
          
          incidenttext = "lacht in Intervallen,3s";
          incidentstartid = "T125";
          incidentendid = "T126";
          
          numk = 12;
          numsup = 11;
          numakz = 17;
          numen = 110;
      }},
//
//      new TeiExpectation(){{
//          filename = "01.01.02.01.04_1_ISO_HIAT_neu_formatted.xml";
//          speakerabbreviations = new String[]{
//                  "N",
//                  "71",
//                  "00101021",
//                  "10101020",
//                  "01010212",
//                  "01010201",
//                  "01010203",
//                  "01010205",
//                  "01010218",
//                  "01010202",
//                  "01010216",
//                  "01010299"};
////              num_utterances = 19;
//          num_segments = 53;
////              num_segments_for_first_utterance = 14;
//          
//          mediaurls = new String[]{
//                     "file:/C:/Users/fsnv625/Desktop/01.01.02.01.04_Gesamtvideo.mpg",
//                     "file:/C:/Users/fsnv625/Desktop/01.01.02.01.04.71.wav",
//                      "file:/C:/Users/fsnv625/Desktop/01.01.02.01.04_Gesamtvideo.mp4",
//                      "file:/C:/Users/fsnv625/Desktop/01.01.02.01.04_Gesamtvideo.webm"};
//          num_when = 80;
//          num_incidents = 18;
//          num_teispan = 13;
//          spantypes = new String[]{"sup", "akz"};
//          specific_segment = 3;
//          num_anchors_for_specific_segment = 0;
//          incidenttext = "Räuspern ";
//          incidentstartid ="T2";
//          incidentendid = "T3";
//      }},
        
        new TeiExpectation(){{
            filename = "RudiVoellerWutausbruch_68-89.xml";
            speakerabbreviations = new String[]{"N","WH","RV"};
            spantypes = new String[]{"sup", "akz", "en", "k"};
//                num_utterances = 7;
            num_segments = 13;
//                num_segments_for_first_utterance = 1;
            num_when = 26;
            num_incidents = 10;
              num_teispan = 27;
            
            incidenttext = "atmet ein";
            incidentstartid = "T21";
            incidentendid = "T22";
            
            numakz = 2;
            numk = 5;
            numsup = 3;
            numen = 17;
        }},
        
//        new TeiExpectation(){{
//            filename = "Schlangen_formatted.tei";
//        }},
        null);

	public static class TeiExpectation {

		String filename;

		String[] speakerabbreviations;
		int num_utterances;
		int num_segments;
		int num_segments_for_first_utterance;
		int num_teispan;
		int num_incidents;
		
		String[] mediaurls;
		int num_when;
		String[] spantypes;
		int specific_segment;
		int num_anchors_for_specific_segment;
		
		String incidenttext;
		String incidentstartid;
		String incidentendid;
		
        int numakz;
        int numk;
        int numsup;
        int numen;

		// ... TODO: add more stuff to test

		public void testCas(JCas cas) throws ClassNotFoundException, NoSuchElementException, IllegalArgumentException, IOException {
			
			System.out.println("--- testing: '" + filename +  "' ---");
			
			HiatTeiMetadata meta = HiatTeiMetadata.getFromCas(cas);

			// check that speakers exist
			if(speakerabbreviations != null) 
				Assert.assertArrayEquals(
						speakerabbreviations, 
						meta.speakers.stream().map(x -> x.n).toArray());

			// check the number of utterances
			Collection<Utterance> utterances = JCasUtil.select(cas, Utterance.class);		
			if(num_utterances != 0)
				Assert.assertEquals(
						num_utterances,
						utterances.size());

			// check number of segments
			Collection<Segment> segments = JCasUtil.select(cas, Segment.class);
			if(num_segments != 0)
				Assert.assertEquals(
						num_segments,
						segments.size()
						);
			
			// check number of segments for first utterance
			Collection<Segment> segments1 = JCasUtil.selectCovered(Segment.class, utterances.iterator().next());
			if(num_segments_for_first_utterance != 0)
				Assert.assertSame(
						num_segments_for_first_utterance,
						segments1.size()
						);

			// check that media exist
			if(mediaurls != null)
				Assert.assertArrayEquals(
						mediaurls, 
						meta.media.stream().map(x -> x.url).toArray());
			
			// check number of when elements in timeline
			if(num_when != 0)
			Assert.assertTrue(
					num_when == meta.timeline.size()
					);
			
			// check number of incidents
			List<Incident> allIncidents = meta.speakers.stream().flatMap(spk -> {
			    JCas speakerview = HiatTeiMetadata.getSpeakerView(cas, spk);
                return JCasUtil.select(speakerview, Incident.class).stream();
			}).filter(i -> !"pause".equals(i.getIncidentType())).collect(Collectors.toList());
			
			if(num_incidents != 0)
				Assert.assertEquals(num_incidents, num_incidents);
				
			// check number of akz
			Collection<TEIspanAkz> spansAkz = JCasUtil.select(cas, TEIspanAkz.class);
			if(numakz != 0)
				Assert.assertEquals(
						numakz,
						spansAkz.size());
			// check number of en
			Collection<TEIspanEn> spansEn = JCasUtil.select(cas, TEIspanEn.class);
			if(numen != 0)
				Assert.assertEquals(
						numen,
						spansEn.size());
			// check number of k
			Collection<TEIspanK> spansK = JCasUtil.select(cas, TEIspanK.class);
			if(numk != 0)
				Assert.assertEquals(
						numk,
						spansK.size());
			// check number of sup
			Collection<TEIspanSup> spansSup = JCasUtil.select(cas, TEIspanSup.class);
			if(numsup != 0)
				Assert.assertEquals(
						numsup,
						spansSup.size());
			// check number of teispans
			Collection<TEIspanGeneric> spans = JCasUtil.select(cas, TEIspanGeneric.class);
			if(num_teispan != 0)
				Assert.assertEquals(
						num_teispan,
						spans.size() + spansAkz.size() + spansK.size() + spansSup.size() + spansEn.size());
			
			// check if all spantypes exist
			if(spantypes != null) {
				HashSet<String> set = new HashSet<String>(Arrays.asList(spantypes));
				Assert.assertFalse(set.retainAll(meta.spantypes));
			}
			
			// check specific incident
			if(incidenttext != null && incidentstartid != null && incidentendid != null) {
				for(Incident incident : allIncidents) {
					if(incidenttext.equals(incident.getDesc())) {
						Assert.assertEquals(incident.getStartID(), incidentstartid);
						Assert.assertEquals(incident.getEndID(), incidentendid);
					}
				}
			}
		}
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
            HiatTeiMetadata meta = HiatTeiMetadata.getFromCasSafe(textview);
            System.out.format("spantypes: %s%n", meta.spantypes);
            System.out.format("speakers: %s%n", meta.speakers);
            System.out.format("timevalues: %s%n", meta.timeline);
            System.out.format("media files: %s%n", meta.media);
            System.out.format("properties: %s%n", meta.properties);
            System.out.format("textview_id_speaker_anchoroffset_index: %s%n", meta.textview_speaker_id_anno_index);

            // print segments
            System.out.println("---segments:---");
            JCasUtil.select(textview, Segment.class).stream().forEach(x -> System.out.print(x.getCoveredText() + " "));

            System.out.println("---speaker 0:---");
            System.out.println(HiatTeiMetadata.getSpeakerView(textview, meta.speakers.get(0)).getDocumentText());

            System.out.println("---speaker 1:---");
            System.out.println(HiatTeiMetadata.getSpeakerView(textview, meta.speakers.get(1)).getDocumentText());

            System.out.println("---narrator:---");
            JCas narrator_view = HiatTeiMetadata.getSpeakerView(textview, Speaker.NARRATOR);
            System.out.println("Textlength: " + narrator_view.getDocumentText().length());
            System.out.println(JCasUtil.selectAll(narrator_view).size() + " annotations");
            System.out.println(JCasUtil.selectAll(narrator_view).stream().map(Object::getClass).map(Class::getSimpleName).distinct().collect(Collectors.joining("; ")));

            // print span annotations
            System.out.println("---span annotations:---");
            JCasUtil.select(textview, TEIspanGeneric.class).stream().forEach(x -> {
                if(x.getBegin() < 0 || x.getEnd() > textview.getDocumentText().length())
                    throw new RuntimeException("This should never happen.");
                System.out.format("%s: %s [%s] (%d, %d) %n", x.getSpanType(), x.getContent(), x.getCoveredText(), JCasUtil.selectCovering(Sentence.class,  x).size(), JCasUtil.selectCovered(Sentence.class,  x).size());   
            });

            // prepare index
            PartiturIndex pindex = new PartiturIndex(meta, textview);

            // 
            System.out.println("---kind of timelineview:---");
            meta.timeline.subList(0, meta.timeline.size()-1).forEach(tv -> {
                meta.speakers.stream().forEach(spk -> {
                    System.out.format("%s - %s: %s%n", tv.id, spk.n, pindex.getSpeakertextForTimevalue(spk, tv));                   
                    meta.spantypes.stream().forEach(stype -> {
                        final String contents = pindex.selectSpeakerAnnotationsForTimevalue(spk, tv, TEIspanGeneric.class)
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


	private TestUtils(){ /* DO NOT INSTANTIATE */ }

	static File _temp_folder;

	public static void setupTest() throws IOException {
		TemporaryFolder f = new TemporaryFolder();
		f.create();
		_temp_folder = f.getRoot();
		System.out.println("created temporary folder: " + _temp_folder.getAbsolutePath());
	}

	public static JCas getCas(Class<? extends JCasResourceCollectionReader_ImplBase> readerclass, String fname) throws ResourceInitializationException, CollectionException, CASAdminException, IOException, CASException{

	    URL fullname = ClassLoader.getSystemClassLoader().getResource(fname);
        if(fullname == null)
            try{ fullname = new URL(fname); }catch(Exception e){throw new RuntimeException(e);};
        File f = new File(fullname.toString());
        String dname = f.getParent();

		ResourceManager resMgr = ResourceManagerFactory.newResourceManager();
		CollectionReader reader = createReader(
				readerclass, 
				JCasResourceCollectionReader_ImplBase.PARAM_SOURCE_LOCATION, dname,
				JCasResourceCollectionReader_ImplBase.PARAM_PATTERNS, f.getName());
		AggregateBuilder b = new AggregateBuilder();
		AnalysisEngine ae = b.createAggregate();
		final CAS cas = CasCreationUtils.createCas(asList(reader.getMetaData(), ae.getMetaData()), null, resMgr);
		reader.typeSystemInit(cas.getTypeSystem());

		Assert.assertTrue(reader.hasNext());
		reader.getNext(cas);

		return cas.getJCas();

	}



}
