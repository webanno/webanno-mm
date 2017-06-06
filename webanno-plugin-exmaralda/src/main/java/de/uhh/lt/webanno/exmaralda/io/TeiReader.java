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
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasResourceCollectionReader_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.uhh.lt.webanno.exmaralda.type.Anchor;
import de.uhh.lt.webanno.exmaralda.type.PlayableAnchor;
import de.uhh.lt.webanno.exmaralda.type.PlayableSegmentAnchor;
import de.uhh.lt.webanno.exmaralda.type.Segment;
import de.uhh.lt.webanno.exmaralda.type.TEIspan;
import de.uhh.lt.webanno.exmaralda.type.Utterance;
import de.uhh.lt.webanno.exmaralda.io.TeiMetadata.Speaker;

/**
 * Reader for the EXMARaLDA TEI format
 *
 */
public class TeiReader extends JCasResourceCollectionReader_ImplBase {

	private static final Logger LOG = LoggerFactory.getLogger(TeiReader.class);
	
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
			// TODO: reconsider if this is really necessary
			JCas teiview = JCasUtil.getView(textview, "tei", true);
			teiview.setDocumentText(IOUtils.toString(is));
			closeQuietly(is);

			is = new BufferedInputStream(res.getInputStream());
			readWithJSoup(is, textview, fname);
		}
		catch (IOException e) {
			LOG.error("Reading '{}' failed.", fname);
			LOG.error("ERROR: {}:{} ", e.getClass().getName(), e.getMessage());
			Throwable cause = e.getCause(); 
			while(cause != null){
				LOG.error("ERROR cause: {}:{} ", cause.getClass().getName(), cause.getMessage());
				cause = cause.getCause();
			}
			throw new CollectionException(e);
		}
		finally {
			closeQuietly(is);
		}
	}

	private void readWithJSoup(InputStream is, JCas textview, String source) throws IOException  {
		Document soup = Jsoup.parse(is, Charset.defaultCharset().name(), source);
		Elements teiheader_elements = soup.getElementsByTag("teiHeader");
		assert teiheader_elements.size() == 1 : String.format("There must be only one 'teiHeader' tag element, found %d.", teiheader_elements.size());
		Element teiheader_element = teiheader_elements.first();
		/* read metadata e.g. speaker names */
		TeiMetadata meta = readTeiHeaderJSoup(textview, teiheader_element);

		Elements text_elements = soup.getElementsByTag("text");
		assert text_elements.size() == 1 : String.format("There must be only one 'text' tag element, found %d.", text_elements.size());
		Element text_element = text_elements.first();
		/* read text */
		readTeiTextJSoup(textview, meta, text_element);

		// save metadata as bytearray to cas
		meta.addToCas(textview);
	}

	private TeiMetadata readTeiHeaderJSoup(JCas textview, Element teiheader_element) throws IOException  {
		TeiMetadata meta = TeiMetadata.newInstance();
		/* read media */
		Elements media_elements = teiheader_element.getElementsByTag("media");
		for(Element media_element : media_elements){
			String mimetype = media_element.attr("mimeType");
			String url = media_element.attr("url");
			meta.media.add(new TeiMetadata.Media(meta.media.size(), mimetype, url));
		}
		
		/* read person descriptions */
		Elements person_elements = teiheader_element.getElementsByTag("person");
		for(Element person_element : person_elements)
			meta.speakers.add(
					new TeiMetadata.Speaker(
							person_element.attr("xml:id"), 
							person_element.attr("n")));        
		return meta;
	}

	private void readTeiTextJSoup(JCas textview, TeiMetadata meta, Element text_element) throws IOException  {

		/* read timeline */
		Elements timeline = text_element.getElementsByTag("timeline");
		assert timeline.size() == 1 : String.format("There must be only one 'text' tag element, found %d.", timeline.size());
		Elements timevalues = timeline.first().getElementsByTag("when");
		if(timevalues.size() <= 0)
			LOG.warn("No 'when' tags found."); 
		for(Element timevalue : timevalues){
			String id = timevalue.attr("xml:id");
			float interval =  timevalue.hasAttr("interval") ? Float.parseFloat(timevalue.attr("interval")) : 0f;
			String sinceId = timevalue.attr("since");
			// add to metadata
			meta.timeline.add(new TeiMetadata.Timevalue(meta.timeline.size(), id, interval, sinceId));
		}

		/* read utterances (also within annotationblocks), fills textview with data */
		parseUtterances(textview, meta, text_element);
		
		/* read span annotations and add them to the textview */
		parseSpanAnnotations(textview, meta, text_element);
		
		/* create speaker views */
		createSpeakerViews(textview, meta);
		
		try {
			List<String> viewnames = new ArrayList<String>();
			Iterator<JCas> i = textview.getViewIterator();
			for(JCas j = i.next(); i.hasNext(); j = i.next()) 
				viewnames.add(j.getViewName());
			LOG.info(String.format("created %d views: %s", viewnames.size(), viewnames.toString()));
		} catch (CASException e) {
			LOG.warn("Could not get viewnames.", e);
		}

		
	}

	private void parseUtterances(JCas textview, TeiMetadata meta, Element text_element) {
		Elements utterances = text_element.getElementsByTag("u");
		if(utterances.size() <= 0)
			LOG.warn("No 'u' (utterance) tags found.");

		StringBuilder text = new StringBuilder();
		
		for(Element utterance : utterances){
			// create the utterance
			String id = utterance.attr("xml:id");
			Utterance utterance_textview = new Utterance(textview);
			utterance_textview.setBegin(text.length());
			utterance_textview.setID(id);

			// attributes like speaker, start and end might be in an annotationblock parenting the utterance
			Element annotationblock = utterance.parent();
			if(!"annotationblock".equals(annotationblock.tagName()))
				annotationblock = null;

			// resolve speaker 
			String speakerID = "";
			if(!utterance.hasAttr("who") && annotationblock != null && annotationblock.hasAttr("who"))
				speakerID = annotationblock.attr("who");
			else
				speakerID = utterance.attr("who");	
			speakerID = StringUtils.stripStart(speakerID, "#");
			Speaker speaker = meta.getSpeakerById(speakerID);
			utterance_textview.setSpeakerABBR(speaker.n);
			utterance_textview.setSpeakerID(speakerID);
			
			// resolve start id
			String startID = "";
			if(!utterance.hasAttr("start") && annotationblock != null && annotationblock.hasAttr("start"))
				startID = annotationblock.attr("start");
			else
				startID = utterance.attr("start");	
			startID = StringUtils.stripStart(startID, "#");
			utterance_textview.setStartID(startID);
			
			// resolve end id
			String endID = "";
			if(!utterance.hasAttr("end") && annotationblock != null && annotationblock.hasAttr("end"))
				endID = annotationblock.attr("end");
			else
				endID = utterance.attr("end");	
			endID = StringUtils.stripStart(endID, "#");
			utterance_textview.setEndID(endID);
						
			// each utterance might contain segments (may be empty / non-text), anchors, ...
			Elements utterance_kids = utterance.children();
						
			// create a text anchor at the beginning of each utterance, but only add it to indexes if text was produced
			Anchor ta = meta.addAnchorToIndex(speaker, addAnchor(textview, text.length(), id, startID, false));
						
			//
			for(Element sub_utterance : utterance_kids){
				String tag = sub_utterance.tagName();
				if("anchor".equals(tag)) { // found an anchor
					/* create a new anchor  
					 * textview: insert zero-length annotation (the flag!)
					 */
					ta = meta.addAnchorToIndex(speaker, 
							addAnchor(textview, 
								text.length(), 
								id, 
								StringUtils.strip(sub_utterance.attr("synch"), "#"), 
								true));
										
				} else if("seg".equals(tag)) {
					
					// prepare text segment
					// <seg xml:id="seg21" type="utterance" subtype="interrupted">
					String segID = StringUtils.strip(sub_utterance.attr("xml:id"), "#");
					String segType = sub_utterance.attr("type");
					String segSubtype = sub_utterance.attr("subtype");
					
					String last_anchor_id = ta.getID();

					Segment segment_annotation_textview = new Segment(textview);
					segment_annotation_textview.setBegin(text.length());
					segment_annotation_textview.setSegmentType(segType);
					segment_annotation_textview.setSegmentSubtype(segSubtype);
					segment_annotation_textview.setID(segID);
					
					for(Element element : sub_utterance.children()) {
						if("unclear".equals(element.tagName())){
							text.append('(');
							new Token(textview, text.length()-1, text.length()).addToIndexes();
							Iterator<Element> unclear_elements_iter = element.children().iterator();
							while(unclear_elements_iter.hasNext()){
								int textoffset = text.length();
								ta = processSegmentChild(textview, meta, speaker, text, id, unclear_elements_iter.next(), ta);
								if(textoffset != text.length() && unclear_elements_iter.hasNext())
									text.append(' ');
							}
							text.append(')');
							new Token(textview, text.length()-1, text.length()).addToIndexes();
							text.append(' ');
						} else {
							int textoffset = text.length();
							ta = processSegmentChild(textview, meta, speaker, text, id, element, ta);
							if(textoffset != text.length())
								text.append(' ');
						}
							
					}
					
					if(segment_annotation_textview.getBegin() < text.length()) { // did we enter the loop? if yes create a sentence annotation for the segment and a playable segment anchor
						// some text was produced, create a sentence segment and add a new line
						// append the segment end signature
						text.deleteCharAt(text.length()-1);
						text.append(getUtteranceEndSignature(segSubtype)).append(' ');
						new Token(textview, text.length()-1, text.length()).addToIndexes(textview);
						
						new Sentence(textview,segment_annotation_textview.getBegin(), text.length()-1).addToIndexes(textview); // end w/o space
						text.append('\n');
						
						PlayableSegmentAnchor taps = new PlayableSegmentAnchor(textview, segment_annotation_textview.getBegin(), segment_annotation_textview.getBegin()); 
						taps.setInfo(String.format("%s▶", utterance_textview.getSpeakerABBR()));
						taps.setAnchorID(last_anchor_id);
						taps.addToIndexes(textview);
					}
					
					segment_annotation_textview.setEnd(text.length());
					segment_annotation_textview.addToIndexes(textview);

				}
			}
			
			
			
			// add a newline if text was produced, also only add the playable begin text anchor of text was produced
			if(utterance_textview.getBegin() < text.length()){
				// also, create a text anchor with the end id of the utterance
				meta.addAnchorToIndex(speaker, addAnchor(textview,text.length()-2, id, endID, false));  // -space and newline
				utterance_textview.setEnd(text.length()); // -space and newline
				text.append("\n");
			}else {
				// also, create a text anchor with the end id of the utterance
				meta.addAnchorToIndex(speaker, addAnchor(textview,text.length(), id, endID, false));
				utterance_textview.setEnd(text.length());	
			}
				
			
			
			utterance_textview.addToIndexes(textview);
		}

		textview.setDocumentText(text.toString());
	}

	private Anchor processSegmentChild(JCas textview, TeiMetadata meta, Speaker speaker, StringBuilder text, String id, Element element, Anchor ta) {
		if("anchor".equals(element.tagName())){
			// create a new anchor
			return meta.addAnchorToIndex(speaker, 
					addAnchor(textview, 
						text.length(), 
						id, 
						StringUtils.strip(element.attr("synch"), "#"), 
						true));
		} else if("w".equals(element.tagName()) || "pc".equals(element.tagName())) {
			
			String type = element.attr("type");
			Token t = new Token(textview);
			t.setBegin(text.length());
			
			// String token = element.text(); woul be too easy, anchors occurr within text
			// <w xml:id="w524">ge<anchor synch="#T142"/>we<anchor synch="#T143"/>sen</w>
			String htmltext = element.html();
			int child_index = htmltext.indexOf('<');
			while(child_index >= 0){ // subelements found
				text.append(htmltext.substring(0, child_index).trim());
				htmltext = htmltext.substring(child_index);
				int end_html = htmltext.indexOf("/>");
				String htmlelement = htmltext.substring(0, end_html+2);
				// add anchor
				Document soup = Jsoup.parse(htmlelement);
				Element html_element = soup.getElementsByTag("anchor").first();
				if(html_element != null){
					ta = meta.addAnchorToIndex(speaker, 
							addAnchor(textview, 
								text.length(), 
								id, 
								StringUtils.strip(html_element.attr("synch"), "#"), 
								true));
				}
				htmltext = htmltext.substring(end_html+2);
				child_index = htmltext.indexOf('<');		
			}
			text.append(htmltext.trim());
			
			if("repair".equals(type))
				text.append('/');
			
			t.setEnd(text.length());
			t.addToIndexes(textview);

		} else if("pause".equals(element.tagName())){
			String type = element.attr("type");
			int b = text.length();
			// short 1x 
			text.append('•'); 
			if(!"short".equals(type)) // medium or long 2x
				text.append('•');
			if("long".equals(type)) // long 3x
				text.append('•');
			new Token(textview, b, text.length()).addToIndexes();
		}else if("incident".equals(element.tagName())){
			Elements descriptions = element.getElementsByTag("desc");
			assert(descriptions.size() == 1) : "";
			String description = descriptions.first().text();
			new Token(textview, text.length(), text.length()+description.length()+4).addToIndexes();
			text.append("((").append(description).append("))");
		}
		return ta;
	}

	private Anchor addAnchor(JCas textview, int positionInText, String utternanceId, String anchorID, boolean add_playable_anchor) {
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
		return ta;
	}
	
	private void parseSpanAnnotations(JCas textview, TeiMetadata meta, Element text_element) {
		Elements annotation_blocks = text_element.getElementsByTag("annotationblock");
		if(annotation_blocks.size() <= 0)
			LOG.warn("No 'annotationblock' tags found.");
		
		for(Element annotation_block : annotation_blocks){
			// search for spans
			Elements spans = annotation_block.getElementsByTag("span");

			// resolve speaker
			String spk_id = StringUtils.stripStart(annotation_block.attr("who"), "#");
			Speaker spk = meta.getSpeakerById(spk_id);
			
			for(Element span : spans){
				// get type from parent spangrp
				String type = "undefined";
				Element span_grp = span.parent();
				if("spangrp".equals(span_grp.tagName()))
					type = span_grp.attr("type");
				// keep track of the various span types
				meta.spantypes.add(type);
				String aID_start = StringUtils.stripStart(span.attr("from"), "#");;
				String aID_end = StringUtils.stripStart(span.attr("to"), "#");
				// TODO: uncouple this in order to be fault tolerant
				int begin = meta.getAnchorOffset(spk, meta.getTimevalueById(aID_start));
				int end = meta.getAnchorOffset(spk, meta.getTimevalueById(aID_end));
				String content = span.text().trim();
				TEIspan span_annotation = new TEIspan(textview, begin, end);
				span_annotation.setSpeakerID(spk_id);
				span_annotation.setStartID(aID_start);
				span_annotation.setEndID(aID_end);
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
				  for(Anchor ta : JCasUtil.selectCovered(textview, Anchor.class, textutterance)){
					  if(!textutterance.getID().equals(ta.getUtteranceID()))
						  continue;
					  final Anchor spta = new Anchor(speakerview);
					  // calculate the offset within textutterance and add offset of speakerutterance 
					  spta.setBegin(ta.getBegin() - textutterance.getBegin() + speakerUtterance.getBegin());
					  spta.setEnd(ta.getEnd() - textutterance.getBegin() + speakerUtterance.getBegin());
					  spta.setID(ta.getID());
					  spta.setUtteranceID(ta.getUtteranceID());
					  spta.addToIndexes(speakerview);
				  }
				  for(TEIspan ts : JCasUtil.selectCovered(textview, TEIspan.class, textutterance)){
					  if(!textutterance.getSpeakerID().equals(ts.getSpeakerID()))
						  continue;
					  final TEIspan spts = new TEIspan(speakerview);
					  // calculate the offset within textutterance and add offset of speakerutterance 
					  spts.setBegin(ts.getBegin() - textutterance.getBegin() + speakerUtterance.getBegin());
					  spts.setEnd(ts.getEnd() - textutterance.getBegin() + speakerUtterance.getBegin());
					  spts.setSpanType(ts.getSpanType());
					  spts.setContent(ts.getContent());
					  spts.setStartID(ts.getStartID());
					  spts.setEndID(ts.getEndID());
					  spts.setSpeakerID(ts.getSpeakerID());
					  spts.addToIndexes(speakerview);
				  }
				  // TODO: add more annotations (spangroups, etc.)
				  // if too many, consider  for(Annotation any_anno : JCasUtil.selectCovered(textview, Annotation.class, textutterance)) and setting properties via reflections					  
			  });
			speakerview.setDocumentText(speakerText.toString());
		  }
	}

}
