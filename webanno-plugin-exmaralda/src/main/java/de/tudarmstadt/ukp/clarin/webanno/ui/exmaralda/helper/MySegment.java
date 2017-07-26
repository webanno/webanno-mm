package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.helper;

import java.io.Serializable;
import java.util.List;

public class MySegment implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private float interval;
	private int length;
	private List<VerbalTrack> speakers;

	public MySegment(String id, float interval, List<VerbalTrack> speakers) {
		this.setId(id);
		this.setInterval(interval);
		this.setSpeakers(speakers);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public float getInterval() {
		return interval;
	}

	public void setInterval(float interval) {
		this.interval = interval;
	}

	public List<VerbalTrack> getSpeakers() {
		return speakers;
	}

	public void setSpeakers(List<VerbalTrack> speakers) {
		this.speakers = speakers;
		
		int segmentLength = 0;
		for(VerbalTrack s : speakers) {
			int l = s.getText().length();
			if(l > segmentLength)
				segmentLength = l;
		}
		
		this.length = segmentLength;
	}
	
	public int getLength() {
		return length;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String lineSeperator = System.getProperty("line.separator");
		 
		sb.append("ID: "+id+" Interval: "+interval+" Length: "+length+lineSeperator);
		for(VerbalTrack s : speakers) {
			sb.append("Name: "+s.getName()+" Text:"+s.getText()+lineSeperator);
		}
		
		return sb.toString();
	}
	
	public AnnotationTrack getAnnotationByDescription(String description) {
		String[] desc = description.split(" ");
		String speakername = desc[0];
		String typ = desc[1].replace("[", "").replace("]", "");
		
		VerbalTrack speaker = getSpeakerByName(speakername);
		AnnotationTrack annotation = null;
		
		if(speaker != null && typ.equals("v")) {
			return null;
		} else if (speaker != null) {
			annotation = speaker.getAnnotationByTyp(typ);
		}
		
		if(annotation != null)
			return annotation;
		
		return null;
	}

	public String getTextByDescription(String description) {
		String[] desc = description.split(" ");
		String speakername = desc[0];
		String typ = desc[1].replace("[", "").replace("]", "");
		
		VerbalTrack speaker = getSpeakerByName(speakername);
		AnnotationTrack annotation = null;
		
		if(speaker != null && typ.equals("v")) {
			return speaker.getText();
		} else if (speaker != null) {
			annotation = speaker.getAnnotationByTyp(typ);
		}
		
		if(annotation != null)
			return annotation.getContent();
		
		return null;
	}
	
	public VerbalTrack getSpeakerByName(String name) {
		for(VerbalTrack speaker : speakers) {
			if(speaker.getName().equals(name))
				return speaker;
		}
		return null;
	}

}
