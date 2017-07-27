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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasResourceCollectionReader_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.uhh.lt.webanno.exmaralda.io.TeiMetadata.Speaker;
import de.uhh.lt.webanno.exmaralda.type.Anchor;
import de.uhh.lt.webanno.exmaralda.type.Incident;
import de.uhh.lt.webanno.exmaralda.type.PlayableAnchor;
import de.uhh.lt.webanno.exmaralda.type.PlayableSegmentAnchor;
import de.uhh.lt.webanno.exmaralda.type.Segment;
import de.uhh.lt.webanno.exmaralda.type.TEIspan;
import de.uhh.lt.webanno.exmaralda.type.Utterance;

/**
 * Reader for the EXMARaLDA TEI format
 *
 */
public class TeiReader extends JCasResourceCollectionReader_ImplBase {
    
    private final static Set<String> methodnamesToIgnore = new HashSet<>(Arrays.asList("getTypeIndexID", "getCoveredText"));

    private static final Logger LOG = LoggerFactory.getLogger(TeiReader.class);
    
    private static final Pattern timefinder = Pattern.compile("[0-9]+([\\.\\,][0-9]+)?.*"); 

    private static char getUtteranceEndSignature(String utteranceSubtype){
        if("declarative".equals(utteranceSubtype))
            return '.';
        if("exclamative".equals(utteranceSubtype))
            return '!';
        if("interrupted".equals(utteranceSubtype))
            return '…';
        if("interrogative".equals(utteranceSubtype))
            return '?';
        if("modeless".equals(utteranceSubtype))
            return ' ';
        return ' ';
    }

    private static void logError(Exception e){
        LOG.error("ERROR: {}:{} ", e.getClass().getName(), e.getMessage());
        Throwable cause = e.getCause(); 
        while(cause != null){
            LOG.error("ERROR cause: {}:{} ", cause.getClass().getName(), cause.getMessage());
            cause = cause.getCause();
        }
    }

    private static void logWarning(Exception e){
        LOG.warn("ERROR: {}:{} ", e.getClass().getName(), e.getMessage());
        Throwable cause = e.getCause(); 
        while(cause != null){
            LOG.warn("ERROR cause: {}:{} ", cause.getClass().getName(), cause.getMessage());
            cause = cause.getCause();
        }
    }
    
    private static String getXMLID(Element e) {
        for(Attribute a : e.getAttributes())
            if(a.getName().equals("id"))
                return a.getValue();
        return null;
    }
    
    private static int findFirstNonSpace(CharSequence chars, int start){
        for(int i = start; i < chars.length()-1; i++)
            if(!Character.isWhitespace(chars.charAt(i)))
                return i;
        return -1;
    }

    private static int findLastNonSpace(CharSequence chars, int end){
        for(int i = end; i > 0; i--)
            if(!Character.isWhitespace(chars.charAt(i-1)))
                return i;
        return -1;
    }

    private static int findLastNonSpace(CharSequence chars){
        return findLastNonSpace(chars, chars.length());
    }

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
            // store the tei sources in a separate view
            JCas teiview = JCasUtil.getView(textview, "tei", true);
            teiview.setDocumentText(IOUtils.toString(is));
            closeQuietly(is);

            is = new BufferedInputStream(res.getInputStream());
            read(is, textview, fname);
        }
        catch (IOException | XMLStreamException | JDOMException e) {
            LOG.error("Reading '{}' failed.", fname);
            logError(e);
            throw new CollectionException(e);
        }
        finally {
            closeQuietly(is);
        }
    }



    private void read(InputStream is, JCas textview, String source) throws IOException, XMLStreamException, JDOMException  {		
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(is);
        Element root = document.getRootElement();

        TeiMetadata meta = readTeiHeader(textview, root);

        readTeiText(textview, meta, root, saxBuilder);
        meta.addToCas(textview);
    }

    private TeiMetadata readTeiHeader(JCas textview, Element root) throws IOException  {
        Namespace ns = root.getNamespace();
        TeiMetadata meta = TeiMetadata.newInstance();

        // get teiHeader Element
        Element teiheader = root.getChild("teiHeader", ns);
        if(teiheader == null) {
            System.out.println("teiHeader not found!");
            return null;
        }

        /* read media */
        ElementFilter filter = new ElementFilter("media");
        for(Element media_element : root.getDescendants(filter)) {
            String mimetype = media_element.getAttributeValue("mimeType");
            String url = media_element.getAttributeValue("url");
            meta.media.add(new TeiMetadata.Media(meta.media.size(), mimetype, url));
        }

        /* read person descriptions */
        ElementFilter filter2 = new ElementFilter("person");
        for(Element person_element : root.getDescendants(filter2)) {
            String id = null;
            for(Attribute a : person_element.getAttributes()) {
                if(a.getName().equals("id"))
                    id = a.getValue();
            }

            meta.speakers.add(
                    new TeiMetadata.Speaker(
                            id, 
                            person_element.getAttributeValue("n")));  
        }     
        meta.speakers.add(0, Speaker.NARRATOR); // add the narrator as a speaker
        return meta;
    }

    private void readTeiText(JCas textview, TeiMetadata meta, Element root, SAXBuilder saxBuilder) throws IOException  {

        /* read timeline */
        ElementFilter filter = new ElementFilter("timeline");
        for(Element timeline : root.getDescendants(filter)) {

            ElementFilter filter2 = new ElementFilter("when");
            for(Element timevalue : timeline.getDescendants(filter2)) {
                String id = getXMLID(timevalue);
                Attribute a = timevalue.getAttribute("interval");
                float interval = 0f;
                try {
                    interval = a != null ? a.getFloatValue() : 0f;
                } catch (DataConversionException e) {
                    logWarning(e);
                }
                String sinceId = timevalue.getAttributeValue("since");
                // add to metadata
                meta.timeline.add(new TeiMetadata.Timevalue(meta.timeline.size(), id, interval, sinceId));
            }
        }
        
        /* store everything in temporary textview */
        JCas tempview = JCasUtil.getView(textview, "_temp", true);

        /* read utterances (also within annotationblocks), fills textview with data */
        parseUtterances(tempview, meta, root, saxBuilder);

        /* read span annotations and add them to the textview */
        parseSpanAnnotations(tempview, meta, root);
        
        /* create speaker views */
        createSpeakerViews(tempview, meta);

        /* read incidents */
        parseUnattachedIncidents(tempview, meta, root);
        
        /* reorder segments, add to textview and clean up temporary textview */
        fillTextview(tempview, textview);

        // XXX: cleanup 
//        meta.textview_speaker_id_anchoroffset_index.clear();
//        tempview.getSofa().removeFromIndexes();
        
        try {
            List<String> viewnames = new ArrayList<String>();
            Iterator<JCas> i = textview.getViewIterator();
            for(JCas j = i.next(); i.hasNext(); j = i.next()){  
                viewnames.add(j.getViewName());
            }
            LOG.info(String.format("created %d views: %s", viewnames.size(), viewnames.toString()));
        } catch (CASException e) {
            LOG.warn("Could not get viewnames.", e);
        }
    }

    private void fillTextview(JCas tempview, JCas textview){
        textview.setDocumentText(tempview.getDocumentText());
        copyAnnotations(
                JCasUtil.selectAll(tempview).stream().filter(a -> a instanceof Annotation).map(a -> (Annotation)a),
                textview).filter(a -> a != null).forEach(a -> a.addToIndexes(textview));
        
//        JCasUtil.select(tempview, Segment.class)
//                .stream()
////                .reorder
//                .forEach(segment -> {
//                    Segment new_segment = copyAnnotation(segment, textview);
////                  a.setBegin(v);
////                  a.setEnd(v);
//                    new_segment.addToIndexes(textview);
//                    
//                    Stream<Annotation> annotations = JCasUtil.selectCovered(Annotation.class, segment).stream();
//                    Stream<Annotation> new_annotations = copyAnnotations(annotations, textview);
//                    new_annotations.forEach(a -> {
////                        a.setBegin(v);
////                        a.setEnd(v);
//                        a.addToIndexes(textview);
//                        System.err.println(a.getClass().getName());
//                    });            
//                });
        

    }
    
    
    private static <T extends Annotation> Stream<T> copyAnnotations(Stream<T> annotations, JCas toCas) {
        return annotations
          .map(a -> copyAnnotation(a, toCas))
          .filter(a -> a != null);
    }
    
    private static <T extends Annotation> T copyAnnotation(T a, JCas toCas){
        try {
            @SuppressWarnings("unchecked")
            T new_a = (T)(a.getClass().getConstructor(JCas.class).newInstance(toCas));
            // collect the methods of the annotation
            Method[] methods = a.getClass().getDeclaredMethods(); 
            Arrays.stream(methods)
            .filter(m -> m.getParameterCount() == 0 && m.getReturnType() != Void.TYPE && m.getName().startsWith("get") && m.getModifiers() == Modifier.PUBLIC) // only select getter methods with no parameters and a return type
            .filter(m -> !methodnamesToIgnore.contains(m.getName()))
            .forEach(getter -> {
                // get the setter method for the corresponding getter
                String setterName = "s" + getter.getName().substring(1); // replace 'get' with 'set'
                try {
                    Method setter = a.getClass().getMethod(setterName, getter.getReturnType());
                    Object result = getter.invoke(a);
                    setter.invoke(new_a, result);
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
                    LOG.warn("Could not invoke method {}:{} to ", new_a.getClass().getSimpleName(), setterName, e);
                }catch(NoSuchMethodException | SecurityException e) {
                    /* ignore */
                }
            });
            new_a.setBegin(((Annotation)a).getBegin());
            new_a.setEnd(((Annotation)a).getEnd());
            // new_a.addToIndexes(textview);
            return new_a;
        }
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            LOG.warn("Could not copy annotation: {}", a.getClass().getName(), e);
            return null;
        }
    }

    private void parseUtterances(JCas textview, TeiMetadata meta, Element root, SAXBuilder saxBuilder) {
        StringBuilder text = new StringBuilder();
        Queue<Incident> incidents_to_finish = new LinkedList<>(); 

        ElementFilter filter = new ElementFilter("u");
        for(Element utterance : root.getDescendants(filter)) {
            String id = getXMLID(utterance);

            // create utterance
            Utterance utterance_textview = new Utterance(textview);
            utterance_textview.setBegin(text.length());
            utterance_textview.setID(id);

            // find annotationblock
            Element annotationBlock = utterance.getParentElement();
            if(annotationBlock != null && !annotationBlock.getName().equals("annotationBlock"))
                annotationBlock = null;

            // resolve speaker
            String speakerID = "";
            if(utterance.getAttribute("who") == null && annotationBlock != null && annotationBlock.getAttribute("who") != null)
                speakerID = annotationBlock.getAttributeValue("who");
            else
                speakerID = utterance.getAttributeValue("who");	
            speakerID = StringUtils.stripStart(speakerID, "#");
            Speaker speaker = meta.getSpeakerById(speakerID);
            utterance_textview.setSpeakerABBR(speaker.n);
            utterance_textview.setSpeakerID(speakerID);

            // resolve start id
            String startID = "";
            if(utterance.getAttribute("start") == null && annotationBlock != null && annotationBlock.getAttribute("start") != null)
                startID = annotationBlock.getAttributeValue("start");
            else
                startID = utterance.getAttributeValue("start");	
            startID = StringUtils.stripStart(startID, "#");
            utterance_textview.setStartID(startID);

            // resolve end id
            String endID = "";
            if(utterance.getAttribute("end") == null && annotationBlock != null && annotationBlock.getAttribute("end") != null)
                endID = annotationBlock.getAttributeValue("end");
            else
                endID = utterance.getAttributeValue("end");	
            endID = StringUtils.stripStart(endID, "#");
            utterance_textview.setEndID(endID);

            // each utterance might contain segments (may be empty / non-text), anchors, ...
            List<Element> utterance_kids = utterance.getChildren();

            // create a text anchor at the beginning of each utterance, but only add it to indexes if text was produced
            Anchor ta = meta.addAnchorToIndex(speaker, addAnchor(textview, text, text.length(), id, startID, incidents_to_finish, false));

            for(Element sub_utterance : utterance_kids){
                String tag = sub_utterance.getName();
                if("anchor".equals(tag)) { // found an anchor
                    /* create a new anchor  
                     * textview: insert zero-length annotation (the flag!)
                     */
                    ta = meta.addAnchorToIndex(speaker, 
                            addAnchor(textview,
                                    text, 
                                    text.length(), 
                                    id, 
                                    StringUtils.strip(sub_utterance.getAttributeValue("synch"), "#"),
                                    incidents_to_finish,
                                    true));

                } else if("seg".equals(tag)) {

                    // prepare text segment
                    // <seg xml:id="seg21" type="utterance" subtype="interrupted">
                    String segID = StringUtils.strip(getXMLID(sub_utterance), "#");
                    String segType = sub_utterance.getAttributeValue("type");
                    String segSubtype = sub_utterance.getAttributeValue("subtype");

                    String last_anchor_id = ta.getID();

                    Segment segment_annotation_textview = new Segment(textview);
                    segment_annotation_textview.setBegin(text.length());
                    segment_annotation_textview.setSegmentType(segType);
                    segment_annotation_textview.setSegmentSubtype(segSubtype);
                    segment_annotation_textview.setID(segID);

                    for(Element element : sub_utterance.getChildren()) {
                        if("unclear".equals(element.getName())){
                            text.append('(');
                            new Token(textview, text.length()-1, text.length()).addToIndexes();
                            Iterator<Element> unclear_elements_iter = element.getChildren().iterator();
                            while(unclear_elements_iter.hasNext()){
                                int textoffset = text.length();
                                ta = processSegmentChild(textview, meta, speaker, text, id, unclear_elements_iter.next(), ta, incidents_to_finish, saxBuilder);
                                if(textoffset != text.length() && unclear_elements_iter.hasNext())
                                    text.append(' ');
                            }
                            text.append(')');
                            new Token(textview, text.length()-1, text.length()).addToIndexes();
                            text.append(' ');
                        } else {
                            int textoffset = text.length();
                            ta = processSegmentChild(textview, meta, speaker, text, id, element, ta, incidents_to_finish, saxBuilder);
                            if(textoffset != text.length())
                                text.append(' ');
                        }
                    }

                    if(segment_annotation_textview.getBegin() < text.length()) { // did we enter the loop? if yes create a sentence annotation for the segment and a playable segment anchor
                        // some text was produced, create a sentence segment and add a new line
                        // append the segment end signature
                        while(text.length() > 0 && Character.isWhitespace(text.charAt(text.length()-1)))
                            text.deleteCharAt(text.length()-1);
                        char mode = getUtteranceEndSignature(segSubtype);
                        if(mode != ' ') {
                            int tb = text.length();
                            text.append(mode);
                            new Token(textview, tb, text.length()).addToIndexes(textview);
                        }
                        int te = findLastNonSpace(text); // -space and newline
                        new Sentence(textview,segment_annotation_textview.getBegin(), te).addToIndexes(textview); // end w/o space
                        segment_annotation_textview.setEnd(te); 
                        segment_annotation_textview.addToIndexes(textview);
                        
                        PlayableSegmentAnchor taps = new PlayableSegmentAnchor(textview, segment_annotation_textview.getBegin(), segment_annotation_textview.getBegin()); 
                        taps.setInfo(String.format("%s▶", utterance_textview.getSpeakerABBR()));
                        taps.setAnchorID(last_anchor_id);
                        taps.addToIndexes(textview);
                        
                        text.append('\n');
                    }
                }
            }

            // add a newline if text was produced, also only add the playable begin text anchor of text was produced
            if(utterance_textview.getBegin() < text.length()){
                // also, create a text anchor with the end id of the utterance
                int te = findLastNonSpace(text); // -space and newline
                meta.addAnchorToIndex(speaker, addAnchor(textview, text, te, id, endID, incidents_to_finish, false));  
                utterance_textview.setEnd(findLastNonSpace(text)); // -space and newline
                text.append("\n");
            } else {
                // also, create a text anchor with the end id of the utterance
                meta.addAnchorToIndex(speaker, addAnchor(textview, text, text.length(), id, endID, incidents_to_finish, false));
                utterance_textview.setEnd(findLastNonSpace(text));	
            }
            utterance_textview.addToIndexes(textview);
        }
        textview.setDocumentText(text.toString());
    }

    private void parseSpanAnnotations(JCas textview, TeiMetadata meta, Element root) {

        ElementFilter filter = new ElementFilter("annotationBlock");
        for(Element annotation_block : root.getDescendants(filter)) {

            // resolve speaker
            String spk_id = StringUtils.stripStart(annotation_block.getAttributeValue("who"), "#");
            Speaker spk = meta.getSpeakerById(spk_id);

            ElementFilter filter2 = new ElementFilter("span");
            for(Element span : annotation_block.getDescendants(filter2)) {
                // get type from parent spangrp
                String type = "undefined";
                Element span_grp = span.getParentElement();
                if("spanGrp".equals(span_grp.getName()))
                    type = span_grp.getAttributeValue("type");
                // keep track of the various span types
                meta.spantypes.add(type);
                String ID_start = StringUtils.stripStart(span.getAttributeValue("from"), "#");;
                String ID_end = StringUtils.stripStart(span.getAttributeValue("to"), "#");

                int begin = meta.getElementAnnotation(spk, ID_start).getBegin();
                int end = meta.getElementAnnotation(spk, ID_end).getEnd();
                String content = span.getText().trim();
                TEIspan span_annotation = new TEIspan(textview, findFirstNonSpace(textview.getDocumentText(), begin), findLastNonSpace(textview.getDocumentText(), end));
                span_annotation.setSpeakerID(spk_id);
                span_annotation.setStartID(ID_start);
                span_annotation.setEndID(ID_end);
                span_annotation.setContent(content);
                span_annotation.setSpanType(type);
                span_annotation.addToIndexes(textview);
            }
        }
    }

    private void createSpeakerViews(JCas textview, TeiMetadata meta) {
        for(Speaker spkr : meta.speakers){
            final String speakerId = spkr.id;
            // create a view for the speaker
            final JCas speakerview = JCasUtil.getView(textview, speakerId + "_", true);
            final StringBuilder speakerText = new StringBuilder();
            // get all utterances for the current speaker
            JCasUtil.select(textview, Utterance.class).stream()
            .filter(u -> speakerId.equals(u.getSpeakerID()))
            .sorted((s1,s2) -> Integer.compare(s1.getBegin(), s2.getBegin())) // ensure ordering
            .forEachOrdered(textutterance -> {
                // create a new utterance 
                final Utterance speakerUtterance = new Utterance(speakerview);
                speakerUtterance.setBegin(speakerText.length());
                speakerText.append(textutterance.getCoveredText());
                speakerUtterance.setEnd(speakerText.length());
                speakerUtterance.setID(textutterance.getID());
                speakerUtterance.setSpeakerID(speakerId);
                speakerUtterance.addToIndexes(speakerview);

                /* get all segments for the current utterance
                 * and add them to the speakerview, 
                 * also add the covered text
                 */
                for(Segment textsegment : JCasUtil.selectCovered(textview, Segment.class, textutterance)){
                    // create a segment annotation for the speaker view
                    final Segment speakersegment = new Segment(speakerview);
                    // calculate the offset within textutterance and add offset of speakerutterance 
                    speakersegment.setBegin(textsegment.getBegin() - textutterance.getBegin() + speakerUtterance.getBegin());
                    speakersegment.setEnd(textsegment.getEnd() - textutterance.getBegin() + speakerUtterance.getBegin());
                    speakersegment.setID(textsegment.getID());
                    speakersegment.setUtteranceID(textsegment.getUtteranceID());
                    speakersegment.setSegmentType(textsegment.getSegmentType());
                    speakersegment.setSegmentSubtype(textsegment.getSegmentSubtype());
                    speakersegment.addToIndexes(speakerview);
                }


                /* add covered annotations, at least TextAnchor annotations */
                copyAnnotations(JCasUtil.selectCovered(textview, Annotation.class, textutterance).stream(), speakerview).forEach(a -> {
                    a.setBegin(a.getBegin() - textutterance.getBegin() + speakerUtterance.getBegin());
                    a.setEnd(a.getEnd() - textutterance.getBegin() + speakerUtterance.getBegin());
                    a.addToIndexes(speakerview);
                });
                
//                for(Anchor ta : ){
//                    if(!textutterance.getID().equals(ta.getUtteranceID()))
//                        continue;
//                    final Anchor spta = new Anchor(speakerview);
//                    // calculate the offset within textutterance and add offset of speakerutterance 
//                    spta.setBegin(ta.getBegin() - textutterance.getBegin() + speakerUtterance.getBegin());
//                    spta.setEnd(ta.getEnd() - textutterance.getBegin() + speakerUtterance.getBegin());
//                    spta.setID(ta.getID());
//                    spta.setUtteranceID(ta.getUtteranceID());
//                    spta.addToIndexes(speakerview);
//                }
//                for(TEIspan ts : JCasUtil.selectCovered(textview, TEIspan.class, textutterance)){
//                    if(!textutterance.getSpeakerID().equals(ts.getSpeakerID()))
//                        continue;
//                    final TEIspan spts = new TEIspan(speakerview);
//                    // calculate the offset within textutterance and add offset of speakerutterance 
//                    spts.setBegin(ts.getBegin() - textutterance.getBegin() + speakerUtterance.getBegin());
//                    spts.setEnd(ts.getEnd() - textutterance.getBegin() + speakerUtterance.getBegin());
//                    spts.setSpanType(ts.getSpanType());
//                    spts.setContent(ts.getContent());
//                    spts.setStartID(ts.getStartID());
//                    spts.setEndID(ts.getEndID());
//                    spts.setSpeakerID(ts.getSpeakerID());
//                    spts.addToIndexes(speakerview);
//                }
//                for(Incident incident : JCasUtil.selectCovered(textview, Incident.class, textutterance)){
//                    if(!textutterance.getSpeakerID().equals(incident.getSpeakerID()))
//                        continue;
//                    final Incident incident_anno = new Incident(speakerview);
//                    // calculate the offset within textutterance and add offset of speakerutterance 
//                    incident_anno.setBegin(incident.getBegin() - textutterance.getBegin() + speakerUtterance.getBegin());
//                    incident_anno.setEnd(incident.getEnd() - textutterance.getBegin() + speakerUtterance.getBegin());
//
//                    incident_anno.setStartID(incident.getStartID());
//                    incident_anno.setEndID(incident.getEndID());
//                    incident_anno.setSpeakerID(incident.getSpeakerID());
//                    incident_anno.setDesc(incident.getDesc());
//                    incident_anno.setIncidentType(incident.getIncidentType());
//                    incident_anno.setIsTextual(incident.getIsTextual());
//                    incident_anno.addToIndexes(speakerview);
//                }
					  
            });
            speakerview.setDocumentText(speakerText.toString());
        }
    }

    private Anchor processSegmentChild(JCas textview, TeiMetadata meta, Speaker speaker, StringBuilder text, String id, Element element, Anchor ta, Queue<Incident> incidents_to_finish, SAXBuilder saxBuilder) {
        if("anchor".equals(element.getName())){
            // create a new anchor
            Anchor new_anchor = 
                    meta.addAnchorToIndex(speaker, 
                            addAnchor(textview, 
                                    text, 
                                    text.length(), 
                                    id, 
                                    StringUtils.strip(element.getAttributeValue("synch"), "#"),
                                    incidents_to_finish,
                                    true));
            return new_anchor; 
        } else if("pc".equals(element.getName())){
            String plaintext = element.getText();            
            if(plaintext != null && !StringUtils.isEmpty(plaintext = plaintext.trim())) {
                while(text.length() > 0 && Character.isWhitespace(text.charAt(text.length()-1)))
                    text.deleteCharAt(text.length()-1);
                int tb = text.length();
                text.append(plaintext);
                if(text.length() > tb){
                    Token token = new Token(textview, tb, text.length());
                    token.addToIndexes(textview);
                    text.append(' ');
                
                    String element_id = getXMLID(element);
                    if(element_id != null)
                        meta.addElementToIndex(element_id, speaker, token);
                }
            }
        } else if("w".equals(element.getName())) {
            String type = element.getAttributeValue("type");
            int tb = text.length();
            
            // String token = element.text(); woul be too easy, anchors occurr within text
            // <w xml:id="w524">ge<anchor synch="#T142"/>we<anchor synch="#T143"/>sen</w>
            //			String plaintext = element.getText();
            //			text.append(plaintext);
            
            String htmltext = new XMLOutputter().outputString(element.getContent());
            int child_index = htmltext.indexOf('<');
            while(child_index >= 0){ // subelements found
                text.append(htmltext.substring(0, child_index).trim());
                htmltext = htmltext.substring(child_index);
                int end_html = htmltext.indexOf("/>");
                String htmlelement = htmltext.substring(0, end_html+2);

                try {
                    InputStream stream = new ByteArrayInputStream(htmlelement.getBytes("UTF-8"));
                    Document document = saxBuilder.build(stream);
                    // add anchor
                    ElementFilter filter = new ElementFilter("anchor");
                    for(Element html_element : document.getDescendants(filter)) {
                        if(html_element != null) {
                            ta = meta.addAnchorToIndex(speaker, 
                                    addAnchor(textview, 
                                            text, 
                                            text.length(), 
                                            id, 
                                            StringUtils.strip(html_element.getAttributeValue("synch"), "#"),
                                            incidents_to_finish,
                                            true));
                        }
                        htmltext = htmltext.substring(end_html+2);
                        child_index = htmltext.indexOf('<');
                    }
                } catch (JDOMException | IOException e) {
                    logWarning(e);
                }

            }
            text.append(htmltext.trim());

            if("repair".equals(type))
                text.append('/');

            int te = findLastNonSpace(text);
            if(te > tb){
                Token token = new Token(textview, tb, te);
                token.addToIndexes(textview);
                String element_id = getXMLID(element);
                if(element_id != null)
                    meta.addElementToIndex(element_id, speaker, token);
            }

        } else if("pause".equals(element.getName())){
            String type = element.getAttributeValue("type");
            String dur = element.getAttributeValue("dur");
            int b = text.length();
            if(!StringUtils.isEmpty(type)){
                // short 1x 
                text.append('•'); 
                if(!"short".equals(type)) // medium or long 2x
                    text.append(" •");
                if("long".equals(type)) // long 3x
                    text.append(" •");
            }
            if(!StringUtils.isEmpty(dur)){
                Matcher m = timefinder.matcher(dur);
                if(m.find()){
                    String dur_t = m.group();
                    text.append("((");
                    if(!StringUtils.isEmpty(dur_t))
                        text.append(dur_t);
                    else
                        text.append(dur);
                    text.append("))");
                }
            }
            if(b != text.length() && !StringUtils.isEmpty(text.substring(b, text.length()))){
                Token pt = new Token(textview, b, text.length());
                pt.addToIndexes();
                Incident incident_anno = new Incident(textview,  b, text.length());
                incident_anno.setSpeakerID(speaker.id);
                incident_anno.setDesc(pt.getCoveredText());
                incident_anno.setIncidentType("pause");
                incident_anno.setIsTextual(true);
                incident_anno.addToIndexes(textview);
                
            }
        }else if("incident".equals(element.getName())){
            ElementFilter filter = new ElementFilter("desc");
            for(Element description : element.getDescendants(filter)) {
                String desc = description.getText();
                int b = text.length(); 
                Token token_anno = new Token(textview);
                token_anno.setBegin(text.length());
                text.append("((");
                token_anno.setEnd(text.length());
                token_anno.addToIndexes();
                
                int tb = text.length();
                text.append(desc);
                int te = findLastNonSpace(text);
                if(te > tb)
                    new Token(textview, tb, te).addToIndexes();
                
                token_anno = new Token(textview);
                token_anno.setBegin(text.length());
                text.append("))");
                token_anno.setEnd(text.length());
                token_anno.addToIndexes();

                Incident incident_anno = new Incident(textview, findFirstNonSpace(text, b), findLastNonSpace(text));
                incident_anno.setStartID(ta.getID());
                incident_anno.setSpeakerID(speaker.id);
                incident_anno.setDesc(desc);
                incident_anno.setIncidentType("nv");
                incident_anno.setIsTextual(true);
                incidents_to_finish.add(incident_anno);

                break;
            }
        }
        return ta;
    }

    private void parseUnattachedIncidents(JCas textview, TeiMetadata meta, Element root) {
        Namespace ns = root.getNamespace();
        Element text_element = root.getChild("text", ns);
        if(text_element == null) {
            logWarning(new IllegalArgumentException("No text tag found!"));
            return;
        }

        ElementFilter filter = new ElementFilter("incident");
        for(Element incident : text_element.getDescendants(filter)) {
            // get attributes
            // incidents within segments don't have a start and end tag because they are defined by the utterance and can be related to an utterance.
            // those kind of incidents are handled before in parseUtterances
            // hence, we skip incidents that don't have a start or end attribute
            // All other incidents merely describe the situation without being referenced to a particular utterance, we collect those as a collection of annotations in 
            if(incident.getAttribute("start") == null && incident.getAttribute("end") == null)
                continue;

            String startAnchorID = StringUtils.stripStart(incident.getAttributeValue("start"), "#");
            String endAnchorID = StringUtils.stripStart(incident.getAttributeValue("end"), "#");

            // resolve speaker
            Speaker spk = Speaker.NARRATOR;
            if(incident.getAttribute("who") != null){
                String spk_id = StringUtils.stripStart(incident.getAttributeValue("who"), "#");
                spk = meta.getSpeakerById(spk_id);
            }

            // get description
            ElementFilter filter2 = new ElementFilter("desc");
            String desc = "";
            for(Element description : incident.getDescendants(filter2)) {
                desc = description.getText();
            }

            // add annotation to speaker or narrator, just collect all annotations at the beginning of the document text
            JCas speakerview = TeiMetadata.getSpeakerView(textview, spk);

            Incident incident_anno = new Incident(speakerview, 0, 0);
            incident_anno.setStartID(startAnchorID);
            incident_anno.setEndID(endAnchorID);
            incident_anno.setSpeakerID(spk.id);
            incident_anno.setDesc(desc);
            incident_anno.setIncidentType("nn");
            incident_anno.addToIndexes(speakerview);
        }
    }

    private Anchor addAnchor(JCas textview, CharSequence text, int positionInText, String utternanceId, String anchorID, Queue<Incident> incidents_to_finish, boolean add_playable_anchor) {
        Anchor ta = new Anchor(textview, positionInText, positionInText); 
        ta.setID(anchorID);
        ta.setUtteranceID(utternanceId);
        ta.addToIndexes(textview);
        if(add_playable_anchor){
            PlayableAnchor tap = new PlayableAnchor(textview, positionInText, positionInText); 
            tap.setInfo(String.format("%s▶", anchorID));
            tap.setAnchorID(anchorID);
            tap.addToIndexes(textview);
        }
        while(!incidents_to_finish.isEmpty()){
            Incident incident = incidents_to_finish.poll();
            incident.setEnd(findLastNonSpace(text, ta.getBegin()));
            incident.setEndID(ta.getID());
            incident.addToIndexes();
        }
        return ta;
    }
    
}
