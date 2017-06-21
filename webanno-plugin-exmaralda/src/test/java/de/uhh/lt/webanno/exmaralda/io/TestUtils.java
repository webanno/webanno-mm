package de.uhh.lt.webanno.exmaralda.io;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.junit.BeforeClass;
import org.junit.rules.TemporaryFolder;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.uhh.lt.webanno.exmaralda.io.TeiMetadata.Speaker;
import de.uhh.lt.webanno.exmaralda.type.Segment;
import de.uhh.lt.webanno.exmaralda.type.TEIspan;

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
    
	
	
	private TestUtils(){ /* DO NOT INSTANTIATE */ }
	
	static File _temp_folder;
	
	static List<String> _test_files = Arrays.asList(
	    	"RudiVoellerWutausbruch_ISO_HIAT_neu_formatted.xml",
			"01.01.02.01.04_1_ISO_HIAT_neu_formatted.xml",
			null);

	public static void setupTest() throws IOException {
		TemporaryFolder f = new TemporaryFolder();
		f.create();
		_temp_folder = f.getRoot();
		System.out.println("created temporary folder: " + _temp_folder.getAbsolutePath());
	}

}
