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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.ByteArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhh.lt.webanno.exmaralda.type.Anchor;
import de.uhh.lt.webanno.exmaralda.type.TranscriptionDocumentMetadata;


public class TeiMetadata implements Serializable {
		
	public final static Logger LOG = LoggerFactory.getLogger(TeiMetadata.class);  
	
	public final Map<String, ? extends Serializable> properties = new HashMap<>();
	
	public final Map<Speaker, Map<Timevalue, Integer>> listview_speaker_timevalue_anchoroffset_index = new HashMap<>();
	
	public List<Media> media = new ArrayList<>();
	
	public final List<Timevalue> timeline = new ArrayList<>();

	public final List<Speaker> speakers = new ArrayList<>();
	
	public final Set<String> spantypes = new HashSet<>();

	public static class Speaker implements Serializable {

		private static final long serialVersionUID = 1L;

		public final String id;

		public final String n;

		public Speaker(String id_, String n_){
			id = id_;
			n = n_;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Speaker)
				return id == null ? obj == null : id.equals(((Speaker)obj).id);
			return false;
		}
		
		@Override
		public int hashCode() {
			return id == null ? 0 : id.hashCode();
		}
		
		@Override
		public String toString() {
			return String.format("id:'%s' (%s)", id, n);
		}

	}

	public static class Timevalue implements Serializable {

		private static final long serialVersionUID = 1L;
		
		public final int i;

		public final String id;

		public final float interval;

		public final String sinceId;

		public Timevalue(int i_, String id_, float interval_, String sinceId_){
			i = i_;
			id = id_;
			interval = interval_;
			sinceId = sinceId_;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Timevalue)
				return id == null ? obj == null : id.equals(((Timevalue)obj).id);
			return false;
		}
		
		@Override
		public int hashCode() {
			return id == null ? 0 : id.hashCode();
		}
		
		@Override
		public String toString() {
			return String.format("id:'%s' [%.3f since '%s']", id, interval, sinceId);
		}
		
	}
	
	public static class Media implements Serializable {

		private static final long serialVersionUID = 1L;

		public final int i;

		public final String mimetype;
		
		public final String url;
		
		public Media(int i_, String mimetype_, String url_){
			i = i_;
			mimetype = mimetype_;
			url = url_;
		}
		
		public boolean isLocal(){
			return false;
		}
		
		@Override
		public String toString() {
			return String.format("%d: (%s) %s", i, mimetype, url);
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Media)
				return url == null ? obj == null : url.equals(((Media)obj).url);
			return false;
		}
		
		@Override
		public int hashCode() {
			return url == null ? 0 : url.hashCode();
		}
		
	}
	
	public Speaker getSpeakerById(String id){
		Optional<Speaker> opt =  speakers.stream().filter(spk -> id.equals(spk.id)).findFirst();
		if(!opt.isPresent())
			throw new NoSuchElementException(String.format("Speaker %s is undefined.", id));
		return opt.get();
	}
	
	public Timevalue getTimevalueById(String id){
		Optional<Timevalue> opt = timeline.stream().filter(tv -> id.equals(tv.id)).findFirst();
		if(!opt.isPresent())
			throw new NoSuchElementException(String.format("Timevalue %s is undefined.", id));
		return opt.get();
	}
	
	public Anchor addAnchorToIndex(Timevalue tv, Speaker speaker, Anchor anchor){
				
		Map<Timevalue, Integer> time_index = listview_speaker_timevalue_anchoroffset_index.get(speaker);
		if(time_index == null){
			time_index = new HashMap<>();
			listview_speaker_timevalue_anchoroffset_index.put(speaker, time_index);
		}
		Integer before = time_index.put(tv,anchor.getBegin());
		assert before == null : String.format("There was an anhor already defined for speaker %s at time %s.", speaker.id, tv.id);
		
		return anchor;
	}
	
	public Anchor addAnchorToIndex(Speaker speaker, Anchor anchor){
		Timevalue tv = getTimevalueById(anchor.getID());
		return addAnchorToIndex(tv, speaker, anchor);
	}
	
	public Integer getAnchorOffset(Speaker spk, Timevalue tv){
		Map<Timevalue, Integer> anchor_index = listview_speaker_timevalue_anchoroffset_index.get(spk);
		if(anchor_index != null)
			return anchor_index.get(tv);
		LOG.warn("No anchor for speaker {} at time {}.", spk.id, tv.id);
		return null;
	}
		
	private static final long serialVersionUID = 1L;

	private TeiMetadata(){/**/}

	public static TeiMetadata fromByteArray(byte[] bytes) throws IOException, ClassNotFoundException {		
		try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
			try(ObjectInputStream o = new ObjectInputStream(b)){
				return (TeiMetadata) o.readObject();
			}
		}
	}

	public byte[] toByteArray() throws IOException{
		try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
			try(ObjectOutputStream o = new ObjectOutputStream(b)){
				o.writeObject(this);
			}
			return b.toByteArray();
		}
	}

	public static TeiMetadata newInstance(){
		return new TeiMetadata();
	}
	
	public void addToCas(JCas cas) throws IOException{
        byte[] bytes = toByteArray();
        ByteArray ba = new ByteArray(cas, bytes.length);
        ba.copyFromArray(bytes, 0, 0, bytes.length);
        TranscriptionDocumentMetadata tdm =  new TranscriptionDocumentMetadata(cas);
        tdm.setTeiMetadataByteArray(ba);
        tdm.addToIndexes();
	}
	
	public static void addToCasSave(JCas cas) {
		try{
			addToCasSave(cas);
		}catch(Exception e){
			LOG.error("Error adding TeiMetadata to cas.", e);
		}
	}

	
	public static TeiMetadata getFromCas(JCas cas) throws ClassNotFoundException, IOException, NoSuchElementException, IllegalArgumentException {
		TranscriptionDocumentMetadata tdm = JCasUtil.selectSingle(cas, TranscriptionDocumentMetadata.class);
		ByteArray ba = tdm.getTeiMetadataByteArray();
		if(ba == null)
			throw new NoSuchElementException();
		byte[] bytes = new byte[ba.size()];
		ba.copyToArray(0, bytes, 0, bytes.length);
		TeiMetadata meta = TeiMetadata.fromByteArray(bytes);
		return meta;
	}
	
	public static TeiMetadata getFromCasSave(JCas cas) {
		try{
			return getFromCas(cas);
		}catch(Exception e){
			LOG.error("Error while reading TeiMetadata from cas.", e);
			return TeiMetadata.newInstance();
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}



}
