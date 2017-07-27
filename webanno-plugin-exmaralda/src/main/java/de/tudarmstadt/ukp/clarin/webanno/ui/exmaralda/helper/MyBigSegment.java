package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.helper;

import java.io.Serializable;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

public class MyBigSegment implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private List<MySegment> segments;
    
    public MyBigSegment(List<MySegment> segments) {
        setSegments(segments);
    }
    
    public int getSpeakerCount() {
        int result = 0;
        for(MySegment segment : segments) {
            int size = segment.getSpeakers().size();
            if(size > result)
                result = size;
        }
        return result;
    }
    
    public List<String> collectDescriptions() {
        
        SortedSet<Track> trackSegments = new TreeSet<>(Track.DESCRIPTION_COMPARATOR);
        for(MySegment segment : segments) {
            for(VerbalTrack speaker : segment.getSpeakers()) {
                if(!StringUtils.isEmpty(speaker.getText()))
                    trackSegments.add(speaker);
                trackSegments.addAll(speaker.getAnnotations());
            }
        }
        return trackSegments.stream().map(Track::getDescription).collect(Collectors.toList());
        
    }

    public List<MySegment> getSegments() {
        return segments;
    }

    public void setSegments(List<MySegment> segments) {
        this.segments = segments;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String lineSeperator = System.getProperty("line.separator");
         
        sb.append("BigSegmentStart"+lineSeperator);getClass();
        for(String description : collectDescriptions()) {
            sb.append(description+" ");
        }
        sb.append(lineSeperator);
        for(MySegment s : segments) {
            sb.append(s);
        }
        sb.append("BigSegmentEnd"+lineSeperator);
        
        return sb.toString();
    }
}