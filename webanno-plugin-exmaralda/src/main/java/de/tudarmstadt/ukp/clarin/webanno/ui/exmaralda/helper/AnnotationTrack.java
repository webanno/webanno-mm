package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.helper;

import java.io.Serializable;

import de.uhh.lt.webanno.exmaralda.io.HiatTeiMetadata.Speaker;

public class AnnotationTrack extends Track implements Serializable {
	private static final long serialVersionUID = 1L;

	private Speaker speaker;
	private String content;
	private String description;
	private String typ;
	private int length;
	
	public AnnotationTrack(Speaker speaker, String annotationcontent, String annotationdescription, String annotationtyp, int annotationlength) {
		setSpeaker(speaker);
	    setContent(annotationcontent);
		setDescription(annotationdescription);
		setTyp(annotationtyp);
		setLength(annotationlength);
	}

	public void setSpeaker(Speaker speaker){
	    this.speaker = speaker;
    }

    public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTyp() {
		return typ;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}
	
	@Override
	public String toString() {
		return "MyAnnotation - Typ:"+typ+" Content:"+content+" Description:"+description;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	@Override
	public String getCategoryString(){
	    return getTyp();
	}

    @Override
    public Speaker getSpeaker() {
        return speaker;
    }

}
