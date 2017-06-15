package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.helper;

import java.io.Serializable;
import java.util.List;

public class MySegment implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private float interval;
	private int length;
	private List<MySpeaker> speakers;

	public MySegment(String id, float interval, List<MySpeaker> speakers) {
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

	public List<MySpeaker> getSpeakers() {
		return speakers;
	}

	public void setSpeakers(List<MySpeaker> speakers) {
		this.speakers = speakers;
		
		int segmentLength = 0;
		for(MySpeaker s : speakers) {
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
		for(MySpeaker s : speakers) {
			sb.append("Name: "+s.getName()+" Text:"+s.getText()+lineSeperator);
		}
		
		return sb.toString();
	}
	
	public MyAnnotation getAnnotationByDescription(String description) {
		String[] desc = description.split(" ");
		String speakername = desc[0];
		String typ = desc[1].replace("[", "").replace("]", "");
		
		MySpeaker speaker = getSpeakerByName(speakername);
		MyAnnotation annotation = null;
		
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
		
		MySpeaker speaker = getSpeakerByName(speakername);
		MyAnnotation annotation = null;
		
		if(speaker != null && typ.equals("v")) {
			return speaker.getText();
		} else if (speaker != null) {
			annotation = speaker.getAnnotationByTyp(typ);
		}
		
		if(annotation != null)
			return annotation.getContent();
		
		return null;
	}
	
	public MySpeaker getSpeakerByName(String name) {
		for(MySpeaker speaker : speakers) {
			if(speaker.getName().equals(name))
				return speaker;
		}
		return null;
	}

}
