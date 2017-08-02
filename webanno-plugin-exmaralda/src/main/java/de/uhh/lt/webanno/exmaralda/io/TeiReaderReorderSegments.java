package de.uhh.lt.webanno.exmaralda.io;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhh.lt.webanno.exmaralda.type.Anchor;
import de.uhh.lt.webanno.exmaralda.type.Segment;
import de.uhh.lt.webanno.exmaralda.type.TEIspan;

import static de.uhh.lt.webanno.exmaralda.io.TeiReaderUtils.*;

public class TeiReaderReorderSegments extends TeiReader {
    
    private static final Logger LOG = LoggerFactory.getLogger(TeiReader.class);
    
    @Override
    void fillTextview(JCas tempview, JCas textview) {
        
        StringBuilder text = new StringBuilder();
        Set<TEIspan> covering_annotations = new HashSet<>();
        // re-order
        JCasUtil.select(tempview, Segment.class)
                .stream()
                .map(s -> {
                    covering_annotations.addAll(JCasUtil.selectCovering(TEIspan.class, s));
                    return s;
                })
                .sorted(new Comparator<Segment>(){
                    @Override
                    public int compare(Segment o1, Segment o2){
                        int i1=0, i2=0;
                        List<Anchor> a1 = JCasUtil.selectCovered(Anchor.class, o1);
                        assert(a1.size() > 0);
                        Matcher m = TeiReaderUtils.INTFINDER_PATTERN.matcher(a1.get(0).getID());
                        if(m.find()){
                            String num = m.group();
                            i1 = Integer.parseInt(num);
                        }
                        List<Anchor> a2 = JCasUtil.selectCovered(Anchor.class, o2);
                        assert(a2.size() > 0);
                        m = TeiReaderUtils.INTFINDER_PATTERN.matcher(a2.get(0).getID());
                        if(m.find()){
                            String num = m.group();
                            i2 = Integer.parseInt(num);
                        }
                        return Integer.compare(i1, i2);
                    }
                })
                .forEach(segment -> {
                    Segment new_segment = copyAnnotation(segment, textview);
                    new_segment.setBegin(text.length());
                    text.append(segment.getCoveredText());
                    new_segment.setEnd(text.length());
                    text.append("\n");
                    new_segment.addToIndexes(textview);
                    Stream<Annotation> annotations = JCasUtil.selectCovered(Annotation.class, segment).stream();
                    Stream<Annotation> new_annotations = copyAnnotations(annotations, textview);
                    new_annotations.forEach(a -> {
                        a.setBegin(a.getBegin() - segment.getBegin() + new_segment.getBegin());
                        a.setEnd(a.getEnd() - segment.getBegin() + new_segment.getBegin());
                        a.addToIndexes(textview);
                    });            
                });
        textview.setDocumentText(text.toString());
        
        if(JCasUtil.select(tempview, Anchor.class).size() != JCasUtil.select(textview, Anchor.class).size())
            throw new RuntimeException("help");
        
        copyAnnotations(covering_annotations.stream(), textview).filter(a -> a != null).forEach(a -> {
            int length = a.getEnd() - a.getBegin();
            // TODO FIXME: find the element (currently only anchor) with the respective ID and get the begin and end offsets. 
            // make this with a proper index, see e.g.  TEIMetadata.textview_speaker_id_anno_index 
            int b = JCasUtil.select(textview, Anchor.class).stream().filter(x -> a.getStartID().equals(x.getID()) && a.getSpeakerID().equals(x.getSpeakerID()) ).findFirst().get().getBegin();
            int e = JCasUtil.select(textview, Anchor.class).stream().filter(x -> a.getEndID().equals(x.getID()) && a.getSpeakerID().equals(x.getSpeakerID()) ).findFirst().get().getEnd();
            a.setBegin(findFirstNonSpace(text, b));
            a.setEnd(findLastNonSpace(text, e));
            a.addToIndexes(textview);
            int newlength = a.getEnd() - a.getBegin();
            if(length != newlength)
                LOG.warn(String.format("new teispan length %d is different from old teispan length %d", length, newlength));
        });
    }

}
