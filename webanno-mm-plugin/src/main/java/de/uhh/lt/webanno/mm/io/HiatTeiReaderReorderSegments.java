package de.uhh.lt.webanno.mm.io;

import static de.uhh.lt.webanno.mm.io.HiatTeiReaderUtils.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.uima.cas.Feature;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhh.lt.webanno.mm.type.Anchor;
import de.uhh.lt.webanno.mm.type.Segment;
import de.uhh.lt.webanno.mm.type.TEIspanGeneric;

public class HiatTeiReaderReorderSegments extends HiatTeiReaderExt {
    
    private static final Logger LOG = LoggerFactory.getLogger(HiatTeiReaderReorderSegments.class);
    
    @Override
    void fillTextview(JCas tempview, JCas textview) {
        
        StringBuilder text = new StringBuilder();
        Set<TEIspanGeneric> covering_annotations = new HashSet<>();
        // re-order
        JCasUtil.select(tempview, Segment.class)
                .stream()
                .map(s -> {
                    covering_annotations.addAll(JCasUtil.selectCovering(TEIspanGeneric.class, s));
                    return s;
                })
                .sorted(new Comparator<Segment>(){
                    @Override
                    public int compare(Segment o1, Segment o2){
                        int i1=0, i2=0;
                        List<Anchor> a1 = JCasUtil.selectCovered(Anchor.class, o1);
                        assert a1.size() > 0 : String.format("No anchor found in segment '%s'.", o1.getID());
                        Matcher m = HiatTeiReaderUtils.INTFINDER_PATTERN.matcher(a1.get(0).getID());
                        if(m.find()){
                            String num = m.group();
                            i1 = Integer.parseInt(num);
                        }
                        List<Anchor> a2 = JCasUtil.selectCovered(Anchor.class, o2);
                        assert a2.size() > 0 : String.format("No anchor found in segment '%s'.", o2.getID());
                        m = HiatTeiReaderUtils.INTFINDER_PATTERN.matcher(a2.get(0).getID());
                        if(m.find()){
                            String num = m.group();
                            i2 = Integer.parseInt(num);
                        }
                        return Integer.compare(i1, i2);
                    }
                }).forEach(segment -> {
                    Annotation new_segment = copyAnnotation(segment, textview);
                    new_segment.setBegin(text.length());
                    text.append(segment.getCoveredText());
                    new_segment.setEnd(text.length());
                    text.append("\n");
                    new_segment.addToIndexes(textview);
                    Stream<? extends Annotation> annotations = JCasUtil.selectCovered(Annotation.class, segment).stream();
                    Stream<Pair<? extends Annotation, ? extends Annotation>> new_annotations = copyAnnotations(annotations, textview);
                    new_annotations
                    .filter(pa -> {
                        if(pa.getRight() == null)
                            LOG.warn("Annotation {} was not copied for an unknown reason. Please check logs.", pa.getLeft());
                        return pa != null;
                    })
                    .map(pa -> pa.getRight())
                    .forEach(a -> {
                        a.setBegin(a.getBegin() - segment.getBegin() + new_segment.getBegin());
                        a.setEnd(a.getEnd() - segment.getBegin() + new_segment.getBegin());
                        a.addToIndexes(textview);
                    });
                });
        textview.setDocumentText(text.toString());

        if(JCasUtil.select(tempview, Anchor.class).size() != JCasUtil.select(textview, Anchor.class).size())
            throw new RuntimeException("help");

        copyAnnotations(covering_annotations.stream(), textview).filter(a -> a != null)
            .filter(pa -> {
                if(pa.getRight() == null)
                    LOG.warn("Annotation {} was not copied for an unknown reason. Please check logs.", pa.getLeft());
                return pa != null;
            })
            .map(pa -> pa.getRight())
            .forEach(a -> {
                int length = a.getEnd() - a.getBegin();
                // TODO FIXME: find the element (currently only anchor) with the respective ID and get the begin and end offsets.
                // make this with a proper index, see e.g.  TEIMetadata.textview_speaker_id_anno_index
                
                Feature startId_fs = a.getType().getFeatureByBaseName("StartID");
                Feature endId_fs = a.getType().getFeatureByBaseName("EndID");
                Feature speakerId_fs = a.getType().getFeatureByBaseName("SpeakerID");
                
                if(startId_fs != null && endId_fs != null && speakerId_fs != null) {
                    String start_id = a.getFeatureValueAsString(startId_fs);
                    String end_id = a.getFeatureValueAsString(endId_fs);
                    String speaker_id = a.getFeatureValueAsString(speakerId_fs);
                                
                    int b = JCasUtil.select(textview, Anchor.class).stream().filter(x -> start_id.equals(x.getID()) && speaker_id.equals(x.getSpeakerID()) ).findFirst().get().getBegin();
                    int e = JCasUtil.select(textview, Anchor.class).stream().filter(x -> end_id.equals(x.getID()) && speaker_id.equals(x.getSpeakerID()) ).findFirst().get().getEnd();
                    a.setBegin(findFirstNonSpace(text, b));
                    a.setEnd(findLastNonSpace(text, e));
                    a.addToIndexes(textview);
                    int newlength = a.getEnd() - a.getBegin();
                    if(length != newlength)
                        LOG.warn(String.format("new teispan length %d is different from old teispan length %d", length, newlength));
                }
            });
    }

}
