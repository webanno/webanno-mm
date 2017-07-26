package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.helper;

import java.io.Serializable;

public class AnnotationTrack extends Track implements Serializable {
	private static final long serialVersionUID = 1L;

	private String speakername;
	private String content;
	private String description;
	private String typ;
	private int length;
	
	public AnnotationTrack(String speakername, String annotationcontent, String annotationdescription, String annotationtyp, int annotationlength) {
		setSpeakername(speakername);
	    setContent(annotationcontent);
		setDescription(annotationdescription);
		setTyp(annotationtyp);
		setLength(annotationlength);
	}

	private void setSpeakername(String speakername){
	    this.speakername = speakername;
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
    public String getSpeakerString() {
        return getSpeakername();
    }

    private String getSpeakername(){
        return speakername;
    }

}
