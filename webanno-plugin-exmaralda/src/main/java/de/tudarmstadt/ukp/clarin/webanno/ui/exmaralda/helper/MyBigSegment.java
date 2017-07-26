package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    
    public List<String> getDescriptions() {
        
        List<Track> speaker_annotations = new ArrayList<>();
        for(MySegment segment : segments) {
            for(VerbalTrack speaker : segment.getSpeakers()) {
                speaker_annotations.add(speaker);
                speaker_annotations.addAll(speaker.getAnnotations());
            }
        }
        
        Set<String> result_set = new HashSet<String>();
        List<String> result = new ArrayList<>();
        Collections.sort(speaker_annotations);
        for(Track annotation : speaker_annotations) {
            if(result_set.contains(annotation.getDescription()))
                continue;
            result.add(annotation.getDescription());
            result_set.add(annotation.getDescription());
        }

        return result;
//      
//      int size = 0;
//      for(Integer i : speakerMap.values()) {
//          if(i > size)
//              size = i;
//      }
//      
//      String[] result2 = new String[(size+1) * 5];
////        System.out.println(result2.length);
//      for(String description : result) {
//          String[] desc = description.split(" ");
//          String speakername = desc[0];
//          String descriptiontyp = desc[1].replace("[", "").replace("]", "");
//          
//          int position = speakerMap.get(speakername) * 4;
//          
//          switch (descriptiontyp) {
//          case "sup":
//              position += 0;
//              break;
//          case "v":
//              position += 1;
//              break;
//          case "akz":
//              position += 2;
//              break;
//          case "en": 
//              position += 3;
//              break;
//          case "k":
//              position = (size+1) * 4 + speakerMap.get(speakername);
//              break;
//          }
//          
////            System.out.println(position);
//          
//          result2[position] = description;
//      }
//      
//      List<String> result3 = new ArrayList<>();
//      for(String description : result2) {
//          if(description != null)
//              result3.add(description);
//      }
//      
//      return result3;
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
        for(String description : getDescriptions()) {
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