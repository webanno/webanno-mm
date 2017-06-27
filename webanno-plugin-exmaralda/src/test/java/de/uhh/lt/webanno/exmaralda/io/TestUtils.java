package de.uhh.lt.webanno.exmaralda.io;

import static java.util.Arrays.asList;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
import de.uhh.lt.webanno.exmaralda.io.TeiMetadata.Speaker;
import de.uhh.lt.webanno.exmaralda.type.Incident;
import de.uhh.lt.webanno.exmaralda.type.Segment;
import de.uhh.lt.webanno.exmaralda.type.TEIspan;
import de.uhh.lt.webanno.exmaralda.type.Utterance;

public class TestUtils {

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
			System.out.println(TeiMetadata.getSpeakerView(textview, meta.speakers.get(0)).getDocumentText());

			System.out.println("---speaker 1:---");
			System.out.println(TeiMetadata.getSpeakerView(textview, meta.speakers.get(1)).getDocumentText());

			System.out.println("---narrator:---");
			JCas narrator_view = TeiMetadata.getSpeakerView(textview, Speaker.NARRATOR);
			System.out.println("Textlength: " + narrator_view.getDocumentText().length());
			System.out.println(JCasUtil.selectAll(narrator_view).size() + " annotations");
			System.out.println(JCasUtil.selectAll(narrator_view).stream().map(Object::getClass).map(Class::getSimpleName).distinct().collect(Collectors.joining("; ")));

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

	public static class TeiExpectation {

		String filename;

		String[] speakerabbreviations;
		int num_utterances;
		int num_segments;
		int num_segments_for_first_utterance;
		int num_teispan;
		int num_incidents;
		int num_anchors;
		
		String[] mediaurls;
		int num_when;
		String[] spantypes;

		// ... TODO: add more stuff to test

		public void testCas(JCas cas) throws ClassNotFoundException, NoSuchElementException, IllegalArgumentException, IOException {
			
			System.out.println("--- testing: '" + filename +  "' ---");
			
			TeiMetadata meta = TeiMetadata.getFromCas(cas);

			// check that speakers exist
			Assert.assertArrayEquals(
					speakerabbreviations, 
					meta.speakers.stream().map(x -> x.n).toArray());

			// check the number of utterances
			Collection<Utterance> utterances = JCasUtil.select(cas, Utterance.class);
			Assert.assertEquals(
					num_utterances,
					utterances.size()
					);

			// check number of segments
			Collection<Segment> segments = JCasUtil.select(cas, Segment.class);
			Assert.assertEquals(
					num_segments,
					segments.size()
					);
			
			// check number of segments for first utterance
			Collection<Segment> segments1 = JCasUtil.selectCovered(Segment.class, utterances.iterator().next());
			Assert.assertSame(
					num_segments_for_first_utterance,
					segments1.size()
					);

			// check that media exist
			Assert.assertArrayEquals(
					mediaurls, 
					meta.media.stream().map(x -> x.url).toArray());
			
			// check number of when elements in timeline
			Assert.assertTrue(
					num_when == meta.timeline.size()
					);
			
			// check number of incidents
			Collection<Incident> allIncidents = new ArrayList<>();
			for(Speaker spk : meta.speakers) {
				JCas speakerview = TeiMetadata.getSpeakerView(cas, spk);
				Collection<Incident> incidents = JCasUtil.select(speakerview, Incident.class);
				allIncidents.addAll(incidents);
			}
			Assert.assertEquals(num_incidents, allIncidents.size());
			
			// check number of teispans
			Collection<TEIspan> spans = JCasUtil.select(cas, TEIspan.class);
			Assert.assertEquals(
					num_teispan,
					spans.size()
					);
			
			// check if all spantypes exist
			HashSet<String> set = new HashSet<String>(Arrays.asList(spantypes));
			Assert.assertFalse(set.retainAll(meta.spantypes));
			
			// TODO: more assertions
		}

	}


	private TestUtils(){ /* DO NOT INSTANTIATE */ }

	static File _temp_folder;

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
				num_anchors = 144;
				num_teispan = 150;
				spantypes = new String[]{"sup", "akz", "en", "k"};
				// TODO: fill me
			}},

//			new TeiExpectation(){{
//				filename = "01.01.02.01.04_1_ISO_HIAT_neu_formatted.xml";
//				speakerabbreviations = new String[]{
//						"N",
//						"71",
//						"00101021",
//						"10101020",
//						"01010212",
//						"01010201",
//						"01010203",
//						"01010205",
//						"01010218",
//						"01010202",
//						"01010216",
//						"01010299"};
//				num_utterances = 19;
//				num_segments = 53;
//				num_segments_for_first_utterance = 14;
//				// TODO: fill me
//			}},

			null);



	public static void setupTest() throws IOException {
		TemporaryFolder f = new TemporaryFolder();
		f.create();
		_temp_folder = f.getRoot();
		System.out.println("created temporary folder: " + _temp_folder.getAbsolutePath());
	}

	public static JCas getCas(Class<? extends JCasResourceCollectionReader_ImplBase> readerclass, String fname) throws ResourceInitializationException, CollectionException, CASAdminException, IOException, CASException{

		URL fullname = ClassLoader.getSystemClassLoader().getResource(fname);
		String dname = new File(fullname.toString()).getParent();

		ResourceManager resMgr = ResourceManagerFactory.newResourceManager();
		CollectionReader reader = createReader(
				readerclass, 
				JCasResourceCollectionReader_ImplBase.PARAM_SOURCE_LOCATION, dname,
				JCasResourceCollectionReader_ImplBase.PARAM_PATTERNS, fname);
		AggregateBuilder b = new AggregateBuilder();
		AnalysisEngine ae = b.createAggregate();
		final CAS cas = CasCreationUtils.createCas(asList(reader.getMetaData(), ae.getMetaData()), null, resMgr);
		reader.typeSystemInit(cas.getTypeSystem());

		Assert.assertTrue(reader.hasNext());
		reader.getNext(cas);

		return cas.getJCas();

	}



}
