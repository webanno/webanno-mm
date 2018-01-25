package de.uhh.lt.webanno.exmaralda.io;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhh.lt.webanno.exmaralda.type.Anchor;
import de.uhh.lt.webanno.exmaralda.io.HiatTeiMetadata.Speaker;
import de.uhh.lt.webanno.exmaralda.io.HiatTeiMetadata.Timevalue;

public class PartiturIndex implements Serializable {
	
	private final static Logger LOG = LoggerFactory.getLogger(PartiturIndex.class);
	
	private static final long serialVersionUID = 1L;

	final transient Map<Speaker, Map<Timevalue, Anchor>> speaker_timevalue_anchor_index;
	
	final transient JCas textview;
	final transient HiatTeiMetadata meta;
	
	public PartiturIndex(HiatTeiMetadata meta_, JCas textview_){
		
		textview = textview_;
		meta = meta_;
		
		// prepare index
		speaker_timevalue_anchor_index = meta.speakers.stream().collect(Collectors.toMap(speaker -> speaker, speaker -> {
			JCas speakerview = HiatTeiMetadata.getSpeakerView(textview, speaker);
			Map<Timevalue, Anchor> anchor2id = JCasUtil.select(speakerview, Anchor.class).stream().collect(Collectors.toMap(x -> meta.getTimevalueById(x.getID()), x -> x, (first, second) -> {
				assert first.getBegin() == second.getBegin() : String.format("Anchors should have a unique position per speaker: %s begin1=%d begin2=%d.", first.getID(), first.getBegin(), second.getBegin()) ;  
				return first;
			})); // in case of duplicate keys take the first (shouldn't matter anyway)			
			return anchor2id;
		}));
				
	}
	
	public <T extends Annotation>  Collection<T> selectSpeakerAnnotationsForTimevalue(Speaker spk, Timevalue tv, Class<T> clazz){
		final Timevalue tv_current = tv;
		final Timevalue tv_next = meta.timeline.get(tv.i+1);
		return selectSpeakerAnnotationsWithinTimevalues(spk, tv_current, tv_next, clazz);
	}
	
	public <T extends Annotation>  Collection<T> selectSpeakerAnnotationsWithinTimevalues(Speaker spk, Timevalue tv1, Timevalue tv2, Class<T> clazz){
		
		final Map<Timevalue, Anchor> anchor_index = speaker_timevalue_anchor_index.get(spk);
		
		final Anchor anchor_current = anchor_index.get(tv1);
		final Anchor anchor_next = anchor_index.get(tv2);
		
		if(anchor_current == null /* either the current anchor doesn't exist, then next anchor is a start anchor*/ || anchor_next == null /* or next anchor doesn't exist, then current anchor is an end anchor */) 
			return Collections.emptyList();
		
		JCas speakerview = HiatTeiMetadata.getSpeakerView(textview, spk);
		final String text = speakerview.getDocumentText();
		try{
			// TODO: better description of errors!
			assert anchor_current.getBegin() <= anchor_next.getBegin() : String.format("Error: speaker: %s, current_anchor: %s, next_anchor: %s", spk.id, tv1.id, tv2.id);
			assert anchor_current.getBegin() >= 0 :                      String.format("Error: speaker: %s, current_anchor: %s, next_anchor: %s", spk.id, tv1.id, tv2.id);
			assert anchor_current.getBegin() <= text.length() :          String.format("Error: speaker: %s, current_anchor: %s, next_anchor: %s", spk.id, tv1.id, tv2.id);
			assert anchor_next.getBegin() >= 0 :                         String.format("Error: speaker: %s, current_anchor: %s, next_anchor: %s", spk.id, tv1.id, tv2.id);
			assert anchor_next.getBegin() <= text.length() :             String.format("Error: speaker: %s, current_anchor: %s, next_anchor: %s", spk.id, tv1.id, tv2.id);
			assert anchor_next.getBegin() >= anchor_current.getBegin() : String.format("Error: speaker: %s, current_anchor: %s, next_anchor: %s", spk.id, tv1.id, tv2.id);
			
			return JCasUtil.selectBetween(speakerview, clazz, anchor_current, anchor_next);
			
		}catch(Exception e){
			LOG.warn(String.format("Could not retrieve annotations between '%s' and '%s'.", tv1, tv2), e);
			return Collections.emptyList(); 
		}
		
	}
	
	public int getSentencenumberForTimevalue(Speaker spk, Timevalue tv){
	    final Map<Timevalue, Anchor> anchor_index = speaker_timevalue_anchor_index.get(spk);
	    if(anchor_index == null)
	        return -1;
	    final Anchor anchor = anchor_index.get(tv);
	    if(anchor == null)
	        return -1;
	    return anchor.getSentenceNumber();
	}
	
	public String getSpeakertextForTimevalue(Speaker spk, Timevalue tv){
		final Timevalue tv_current = tv;
		final Timevalue tv_next = meta.timeline.get(tv.i+1);
		return getSpeakertextWithinTimevalues(spk, tv_current, tv_next);
	}
	
	public String getSpeakertextWithinTimevalues(Speaker spk, Timevalue tv1, Timevalue tv2){
		
		final Map<Timevalue, Anchor> anchor_index = speaker_timevalue_anchor_index.get(spk);
		
		final Anchor anchor_current = anchor_index.get(tv1);
		final Anchor anchor_next = anchor_index.get(tv2);
		
		if(anchor_current == null /* either the current anchor doesn't exist, then next anchor is a start anchor*/ || anchor_next == null /* or next anchor doesn't exist, then current anchor is an end anchor */) 
			return "";
		
		JCas speakerview = HiatTeiMetadata.getSpeakerView(textview, spk);
		final String text = speakerview.getDocumentText();
		try{
			// TODO: better description of errors!
			assert anchor_current.getBegin() <= anchor_next.getBegin() : String.format("Error: speaker: %s, current_anchor: %s, next_anchor: %s", spk.id, tv1.id, tv2.id);
			assert anchor_current.getBegin() >= 0 :                      String.format("Error: speaker: %s, current_anchor: %s, next_anchor: %s", spk.id, tv1.id, tv2.id);
			assert anchor_current.getBegin() <= text.length() :          String.format("Error: speaker: %s, current_anchor: %s, next_anchor: %s", spk.id, tv1.id, tv2.id);
			assert anchor_next.getBegin() >= 0 :                         String.format("Error: speaker: %s, current_anchor: %s, next_anchor: %s", spk.id, tv1.id, tv2.id);
			assert anchor_next.getBegin() <= text.length() :             String.format("Error: speaker: %s, current_anchor: %s, next_anchor: %s", spk.id, tv1.id, tv2.id);
			assert anchor_next.getBegin() >= anchor_current.getBegin() : String.format("Error: speaker: %s, current_anchor: %s, next_anchor: %s", spk.id, tv1.id, tv2.id);
			String textbetweenanchors = text.substring(anchor_current.getBegin(), anchor_next.getBegin()).replace("\n", "");
			return textbetweenanchors;
		}catch(Exception e){
			e.printStackTrace();
			return "ERROR: " + e.getMessage();
		}
		
	}

}
