package de.tudarmstadt.ukp.clarin.webanno.ui.mm.helper;

import java.io.Serializable;
import java.util.List;

import de.uhh.lt.webanno.mm.io.HiatTeiMetadata.Speaker;

public class VerbalTrack extends Track implements Serializable {
	private static final long serialVersionUID = 1L;

	private Speaker speaker;
	private String text; 
	private String description;
	private int id;
	private List<AnnotationTrack> annotations;
	
	public VerbalTrack(Speaker speaker, String speakertext, String speakerdescription, int speakerid, List<AnnotationTrack> annotations) {
		setSpeaker(speaker);
		setText(speakertext);
		setAnnotations(annotations);
		setDescription(speakerdescription);
		setId(speakerid);
	}

	private void setSpeaker(Speaker speaker){
       this.speaker = speaker;
    }

    public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<AnnotationTrack> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<AnnotationTrack> annotations) {
		this.annotations = annotations;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AnnotationTrack getAnnotationByTyp(String typ) {
		for(AnnotationTrack annotation : annotations) {
			if(annotation.getTyp().equals(typ))
				return annotation;
		}
		return null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String getCategoryString(){
	    return "v";
	}
	
	@Override
    public Speaker getSpeaker() {
        return speaker;
    }

}
