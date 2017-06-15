package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.helper;

import java.io.Serializable;
import java.util.List;

public class MySpeaker implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String text; 
	private String description;
	private int id;
	private List<MyAnnotation> annotations;
	
	public MySpeaker(String speakername, String speakertext, String speakerdescription, int speakerid, List<MyAnnotation> annotations) {
		setName(speakername);
		setText(speakertext);
		setAnnotations(annotations);
		setDescription(speakerdescription);
		setId(speakerid);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<MyAnnotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<MyAnnotation> annotations) {
		this.annotations = annotations;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MyAnnotation getAnnotationByTyp(String typ) {
		for(MyAnnotation annotation : annotations) {
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

}
