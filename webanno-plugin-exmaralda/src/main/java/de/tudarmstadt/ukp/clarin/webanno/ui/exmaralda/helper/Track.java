package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.helper;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.uhh.lt.webanno.exmaralda.io.TeiMetadata.Speaker;
import de.uhh.lt.webanno.exmaralda.io.TeiReaderUtils;

abstract class Track implements Serializable {
    
    private final static Map<String, Integer> CATEGORY_ORDERING;
    
    static {
        CATEGORY_ORDERING = new HashMap<>();
        CATEGORY_ORDERING.put("sup", 1);
        CATEGORY_ORDERING.put("pho", 2);
        CATEGORY_ORDERING.put("v", 3);
        CATEGORY_ORDERING.put("akz", 4);
        CATEGORY_ORDERING.put("en", 5);
        CATEGORY_ORDERING.put("nv", 6);
        CATEGORY_ORDERING.put("nn", 7);
        CATEGORY_ORDERING.put("k", 8);
    }
    
    public static Comparator<Speaker> SPEAKER_COMPARATOR = new Comparator<Speaker>(){
        @Override
        public int compare(Speaker o1, Speaker o2){
            int c = Integer.compare(o1.i, o2.i);
            
            return c;
        }
    };
    
    public static Comparator<Track> DESCRIPTION_COMPARATOR = new Comparator<Track>(){
        @Override
        public int compare(Track o1, Track o2){
            int c = SPEAKER_COMPARATOR.compare(o1.getSpeaker(), o2.getSpeaker());
            if(c != 0)
                return c;
            Integer i1 = CATEGORY_ORDERING.get(o1.getCategoryString());
            Integer i2= CATEGORY_ORDERING.get(o2.getCategoryString());
            c = Integer.compare(i1 == null ? 0 : i1, i2 == null ? 0 : i2);
            return c;
        }
    };
    
    private static final long serialVersionUID = 1L;

    public abstract String getDescription();
    
    public abstract String getCategoryString();
    
    public abstract Speaker getSpeaker();
    
}
