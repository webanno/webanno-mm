package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.helper;

import java.io.Serializable;

public class MyAnnotation implements Serializable {
	private static final long serialVersionUID = 1L;

	private String content;
	private String description;
	private String typ;
	private int length;
	
	public MyAnnotation(String annotationcontent, String annotationdescription, String annotationtyp, int annotationlength) {
		setContent(annotationcontent);
		setDescription(annotationdescription);
		setTyp(annotationtyp);
		setLength(annotationlength);
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

}
