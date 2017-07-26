package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.helper;

import java.io.Serializable;
import java.util.List;

public class VerbalTrack extends Track implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String text; 
	private String description;
	private int id;
	private List<AnnotationTrack> annotations;
	
	public VerbalTrack(String speakername, String speakertext, String speakerdescription, int speakerid, List<AnnotationTrack> annotations) {
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
    public String getSpeakerString() {
        return getName();
    }

}
