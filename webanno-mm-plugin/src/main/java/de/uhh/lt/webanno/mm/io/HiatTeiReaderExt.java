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
package de.uhh.lt.webanno.mm.io;

import static de.uhh.lt.webanno.mm.io.HiatTeiReaderUtils.TIMEFINDER_PATTERN;
import static de.uhh.lt.webanno.mm.io.HiatTeiReaderUtils.copyAnnotations;
import static de.uhh.lt.webanno.mm.io.HiatTeiReaderUtils.findFirstNonSpace;
import static de.uhh.lt.webanno.mm.io.HiatTeiReaderUtils.findLastNonSpace;
import static de.uhh.lt.webanno.mm.io.HiatTeiReaderUtils.logError;
import static de.uhh.lt.webanno.mm.io.HiatTeiReaderUtils.logWarning;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
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
import org.jdom2.Text;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasResourceCollectionReader_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.uhh.lt.webanno.mm.io.HiatTeiMetadata.Description;
import de.uhh.lt.webanno.mm.io.HiatTeiMetadata.Speaker;
import de.uhh.lt.webanno.mm.type.Anchor;
import de.uhh.lt.webanno.mm.type.Incident;
import de.uhh.lt.webanno.mm.type.PlayableAnchor;
import de.uhh.lt.webanno.mm.type.PlayableSegmentAnchor;
import de.uhh.lt.webanno.mm.type.Segment;
import de.uhh.lt.webanno.mm.type.TEIspan;
import de.uhh.lt.webanno.mm.type.TEIspanAkz;
import de.uhh.lt.webanno.mm.type.TEIspanEn;
import de.uhh.lt.webanno.mm.type.TEIspanGeneric;
import de.uhh.lt.webanno.mm.type.TEIspanK;
import de.uhh.lt.webanno.mm.type.TEIspanSup;
import de.uhh.lt.webanno.mm.type.Utterance;

/**
 * Reader for the HIAT TEI format
 *
 */
public class HiatTeiReaderExt extends JCasResourceCollectionReader_ImplBase {
    
    private static final Logger LOG = LoggerFactory.getLogger(HiatTeiReaderExt.class);
        
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
            return '\u02d9';
        return ' ';
    }
    
    private static String getXMLID(Element e) {
        for(Attribute a : e.getAttributes())
            if(a.getName().equals("id"))
                return a.getValue();
        return null;
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
            teiview.setDocumentText(IOUtils.toString(is, Charset.defaultCharset()));
            closeQuietly(is);

            is = new BufferedInputStream(res.getInputStream());
            read(is, textview, fname);
        }
        catch (IOException | XMLStreamException | JDOMException e) {
            LOG.error("Reading '{}' failed.", fname);
            logError(LOG, e);
            throw new CollectionException(e);
        }
        finally {
            closeQuietly(is);
        }
    }



    private void read(InputStream is, JCas textview, String source) throws IOException, XMLStreamException, JDOMException, CollectionException  {		
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(is);
        Element root = document.getRootElement();

        HiatTeiMetadata meta = readTeiHeader(textview, root);

        readTeiText(textview, meta, root, saxBuilder);
        meta.addToCas(textview);
    }

    private HiatTeiMetadata readTeiHeader(JCas textview, Element root) throws IOException  {
        Namespace ns = root.getNamespace();
        HiatTeiMetadata meta = HiatTeiMetadata.newInstance();
        meta.created_with = getClass();

        // get teiHeader Element
        Element teiheader = root.getChild("teiHeader", ns);
        if(teiheader == null) {
            logWarning(LOG, new IllegalStateException("teiHeader element not found!"));
            return null;
        }
        meta.tei_header_xml = new XMLOutputter().outputString(teiheader);
        
        /* read filedesc */
        Element filedescription_element = teiheader.getChild("fileDesc", ns);
        String filedesc_xml = new XMLOutputter().outputString(filedescription_element);
        ElementFilter filter = new ElementFilter("title");
        Iterator<Element> titles = filedescription_element.getDescendants(filter);
        String title = null;
        if(titles.hasNext()){
            Element title_element = titles.next();
            title = StringEscapeUtils.unescapeXml(title_element.getTextNormalize());
        }
        
        String settingsdesc_xml = "";
        Element profiledescription_element = teiheader.getChild("profileDesc", ns);
        if(profiledescription_element != null){
            Element settingsdescription_element = profiledescription_element.getChild("settingDesc", ns);
            if(settingsdescription_element != null){
                settingsdesc_xml = new XMLOutputter().outputString(settingsdescription_element);
            }
        }
        meta.description = new Description(title, filedesc_xml, settingsdesc_xml);
        
        /* read media */
        filter = new ElementFilter("media");
        for(Element media_element : filedescription_element.getDescendants(filter)) {
            String mimetype = media_element.getAttributeValue("mimeType");
            String url = media_element.getAttributeValue("url");
            meta.media.add(new HiatTeiMetadata.Media(meta.media.size(), mimetype, url));
        }

        /* read person descriptions */
        filter = new ElementFilter("person");
        for(Element person_element : teiheader.getDescendants(filter)) {
            String xml = new XMLOutputter().outputString(person_element);
            String id = null;
            for(Attribute a : person_element.getAttributes()) {
                if(a.getName().equals("id"))
                    id = a.getValue();
            }
            
            meta.speakers.add(
                    new HiatTeiMetadata.Speaker(
                            meta.speakers.size(),
                            id, 
                            person_element.getAttributeValue("n"), xml));  
        }     
        meta.speakers.add(0, Speaker.NARRATOR); // add the narrator as a speaker
        return meta;
    }

    private void readTeiText(JCas textview, HiatTeiMetadata meta, Element root, SAXBuilder saxBuilder) throws IOException, CollectionException  {

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
                    logWarning(LOG, e);
                }
                String sinceId = timevalue.getAttributeValue("since");
                // add to metadata
                meta.timeline.add(new HiatTeiMetadata.Timevalue(meta.timeline.size(), id, interval, sinceId));
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

        /* cleanup */
        meta.textview_speaker_id_anno_index.clear();
        
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

    void fillTextview(JCas tempview, JCas textview){
        textview.setDocumentText(tempview.getDocumentText());
        Stream<Annotation> annotations = JCasUtil.selectAll(tempview).stream().filter(a -> a instanceof Annotation).map(a -> (Annotation)a);
        copyAnnotations(annotations,textview)
            .filter(pa -> {
                if(pa.getRight() == null)
                    LOG.warn("Annotation {} was not copied for an unknown reason. Please check logs.", pa.getLeft());
                return pa != null;
            }).forEach(pa -> pa.getRight().addToIndexes(textview));

    }

    private void parseUtterances(JCas textview, HiatTeiMetadata meta, Element root, SAXBuilder saxBuilder) {
        StringBuilder text = new StringBuilder();
        int count_sentences = 1;
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
            Anchor ta = meta.addAnchorToIndex(speaker, addAnchor(textview, text, text.length(), speaker.id, id, startID, incidents_to_finish, false, count_sentences));

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
                                    speaker.id,
                                    id, 
                                    StringUtils.strip(sub_utterance.getAttributeValue("synch"), "#"),
                                    incidents_to_finish,
                                    true,
                                    count_sentences));

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
                                ta = processSegmentChild(textview, meta, speaker, text, id, unclear_elements_iter.next(), ta, incidents_to_finish, saxBuilder, count_sentences);
                                if(textoffset != text.length() && unclear_elements_iter.hasNext())
                                    text.append(' ');
                            }
                            text.append(')');
                            new Token(textview, text.length()-1, text.length()).addToIndexes();
                            text.append(' ');
                        } else {
                            int textoffset = text.length();
                            ta = processSegmentChild(textview, meta, speaker, text, id, element, ta, incidents_to_finish, saxBuilder, count_sentences);
                            if(textoffset != text.length() && ' ' != text.charAt(text.length()-1))
                                text.append(' ');
                        }
                    }

                    if(segment_annotation_textview.getBegin() < text.length()) { // did we enter the loop? if yes create a sentence annotation for the segment and a playable segment anchor
                        // some text was produced, create a sentence segment and add a new line
                        // append the segment end signature
                        while(text.length() > 0 && ' ' == text.charAt(text.length()-1))
                            text.deleteCharAt(text.length()-1);
                        char mode = getUtteranceEndSignature(segSubtype);
                        if(mode != ' ') {
                            int tb = text.length();
                            text.append(mode);
                            new Token(textview, tb, text.length()).addToIndexes(textview);
                        }
                        int te = findLastNonSpace(text); // -space and newline
                        new Sentence(textview,segment_annotation_textview.getBegin(), te).addToIndexes(textview); // end w/o space
                        segment_annotation_textview.setSentenceNumber(count_sentences++);
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
                meta.addAnchorToIndex(speaker, addAnchor(textview, text, te, speaker.id, id, endID, incidents_to_finish, false, count_sentences));  
                utterance_textview.setEnd(findLastNonSpace(text)); // -space and newline
                text.append("\n");
            } else {
                // also, create a text anchor with the end id of the utterance
                meta.addAnchorToIndex(speaker, addAnchor(textview, text, text.length(), speaker.id, id, endID, incidents_to_finish, false, count_sentences));
                utterance_textview.setEnd(findLastNonSpace(text));	
            }
            utterance_textview.addToIndexes(textview);
        }
        textview.setDocumentText(text.toString());
    }

    private void parseSpanAnnotations(JCas textview, HiatTeiMetadata meta, Element root) throws CollectionException {

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

                String ID_start = StringUtils.stripStart(span.getAttributeValue("from"), "#");;
                String ID_end = StringUtils.stripStart(span.getAttributeValue("to"), "#");
                Annotation x = meta.getElementAnnotation(spk, ID_start);
                if(x == null)
                    throw new CollectionException(new IllegalStateException(String.format("Something is wrong with referencing id %s for speaker. Cannot find text offset.", ID_start, spk)));
                int begin = x.getBegin();
                begin = findFirstNonSpace(textview.getDocumentText(), begin);
                x = meta.getElementAnnotation(spk, ID_end);
                if(x == null)
                    throw new CollectionException(new IllegalStateException(String.format("Something is wrong with referencing id %s for speaker. Cannot find text offset.", ID_start, spk)));
                int end = x.getEnd();
                end = findLastNonSpace(textview.getDocumentText(), end);
                
                String content = StringEscapeUtils.unescapeXml(span.getTextNormalize().trim());
                if("pos".equals(type)){
                    POS pos = new POS(textview, begin, end);
                    pos.setPosValue(content);
                    pos.addToIndexes(textview);
                }else if("lemma".equals(type)){
                    Lemma lemma = new Lemma(textview, begin, end);
                    lemma.setValue(content);
                    lemma.addToIndexes(textview);
                }else{
                	switch(type) {
                	case "akz":
                        TEIspanAkz span_annotation = new TEIspanAkz(textview, begin, end);
                        setTEIspanInformation(span_annotation, spk_id, ID_start, ID_end, content, type);
                        span_annotation.addToIndexes(textview);
                		break;
                	case "k":
                        TEIspanK span_annotation1 = new TEIspanK(textview, begin, end);
                        setTEIspanInformation(span_annotation1, spk_id, ID_start, ID_end, content, type);
                        span_annotation1.addToIndexes(textview);
                		break;
                	case "sup":
                        TEIspanSup span_annotation2 = new TEIspanSup(textview, begin, end);
                        setTEIspanInformation(span_annotation2, spk_id, ID_start, ID_end, content, type);
                        span_annotation2.addToIndexes(textview);
                		break;
                	case "en":
                        TEIspanEn span_annotation3 = new TEIspanEn(textview, begin, end);
                        setTEIspanInformation(span_annotation3, spk_id, ID_start, ID_end, content, type);
                        span_annotation3.addToIndexes(textview);
                		break;
                	default:
                        TEIspanGeneric span_annotation4 = new TEIspanGeneric(textview, begin, end);
                        setTEIspanInformation(span_annotation4, spk_id, ID_start, ID_end, content, type);
                        span_annotation4.addToIndexes(textview);
                		break;
                	}
                    if(!type.startsWith("morph"))
                        // keep track of the various span types
                        meta.spantypes.add(type);
                }
            }
        }
    }
    
    private void setTEIspanInformation(TEIspan span, String spk_id, String ID_start, String ID_end, String content, String type) {
        span.setSpeakerID(spk_id);
        span.setStartID(ID_start);
        span.setEndID(ID_end);
        span.setContent(content);
        span.setSpanType(type);
    }

    private void createSpeakerViews(JCas textview, HiatTeiMetadata meta) {
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
                    speakersegment.setSentenceNumber(textsegment.getSentenceNumber());
                    speakersegment.setUtteranceID(textsegment.getUtteranceID());
                    speakersegment.setSegmentType(textsegment.getSegmentType());
                    speakersegment.setSegmentSubtype(textsegment.getSegmentSubtype());
                    speakersegment.addToIndexes(speakerview);
                }


                /* add covered annotations, at least TextAnchor annotations */
                copyAnnotations(JCasUtil.selectCovered(textview, Annotation.class, textutterance).stream(), speakerview)
                    .filter(pa -> {
                        if(pa.getRight() == null)
                            LOG.warn("Annotation {} was not copied for an unknown reason. Please check logs.", pa.getLeft());
                        return pa != null;
                    })
                    .map(pa -> pa.getRight())
                    .forEach(a -> {
                        a.setBegin(a.getBegin() - textutterance.getBegin() + speakerUtterance.getBegin());
                        a.setEnd(a.getEnd() - textutterance.getBegin() + speakerUtterance.getBegin());
                        a.addToIndexes(speakerview);
                    });
                
            });
            speakerview.setDocumentText(speakerText.toString());
        }
    }

    private Anchor processSegmentChild(JCas textview, HiatTeiMetadata meta, Speaker speaker, StringBuilder text, String id, Element element, Anchor ta, Queue<Incident> incidents_to_finish, SAXBuilder saxBuilder, int count_sentences) {
        if("anchor".equals(element.getName())){
            // create a new anchor
            Anchor new_anchor = 
                    meta.addAnchorToIndex(speaker, 
                            addAnchor(textview, 
                                    text, 
                                    text.length(), 
                                    speaker.id,
                                    id, 
                                    StringUtils.strip(element.getAttributeValue("synch"), "#"),
                                    incidents_to_finish,
                                    true,
                                    count_sentences));
            return new_anchor; 
        } else if("pc".equals(element.getName())){
            String plaintext = StringEscapeUtils.unescapeXml(element.getTextNormalize());            
            if(plaintext != null && !StringUtils.isEmpty(plaintext = plaintext.trim())) {
                while(text.length() > 0 && ' ' == text.charAt(text.length()-1))
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
                text.append(StringEscapeUtils.unescapeXml(Text.normalizeString(htmltext.substring(0, child_index).trim())));
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
                                            speaker.id,
                                            id, 
                                            StringUtils.strip(html_element.getAttributeValue("synch"), "#"),
                                            incidents_to_finish,
                                            true,
                                            count_sentences));
                        }
                        htmltext = htmltext.substring(end_html+2);
                        child_index = htmltext.indexOf('<');
                    }
                } catch (JDOMException | IOException e) {
                    logWarning(LOG, e);
                }
            }
            text.append(StringEscapeUtils.unescapeXml(Text.normalizeString(htmltext.trim())));

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
                Matcher m = TIMEFINDER_PATTERN.matcher(dur);
                if(m.find()){
                    String dur_t = m.group();
                    text.append("((");
                    if(!StringUtils.isEmpty(dur_t))
                        text.append(dur_t.toLowerCase());
                    else
                        text.append(dur.toLowerCase());
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
                String desc = StringEscapeUtils.unescapeXml(description.getTextNormalize());
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

    private void parseUnattachedIncidents(JCas textview, HiatTeiMetadata meta, Element root) {
        Namespace ns = root.getNamespace();
        Element text_element = root.getChild("text", ns);
        if(text_element == null) {
            logWarning(LOG, new IllegalArgumentException("No text tag found!"));
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
                desc = StringEscapeUtils.unescapeXml(description.getTextNormalize());
            }

            // add annotation to speaker or narrator, just collect all annotations at the beginning of the document text
            JCas speakerview = HiatTeiMetadata.getSpeakerView(textview, spk);

            Incident incident_anno = new Incident(speakerview, 0, 0);
            incident_anno.setStartID(startAnchorID);
            incident_anno.setEndID(endAnchorID);
            incident_anno.setSpeakerID(spk.id);
            incident_anno.setDesc(desc);
            incident_anno.setIncidentType("nn");
            incident_anno.addToIndexes(speakerview);
        }
    }

    private Anchor addAnchor(JCas textview, CharSequence text, int positionInText, String speakerId, String utteranceId, String anchorID, Queue<Incident> incidents_to_finish, boolean add_playable_anchor, int current_sentence_no) {
        Anchor ta = new Anchor(textview, positionInText, positionInText); 
        ta.setID(anchorID);
        ta.setUtteranceID(utteranceId);
        ta.addToIndexes(textview);
        ta.setSpeakerID(speakerId);
        ta.setSentenceNumber(current_sentence_no);
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
