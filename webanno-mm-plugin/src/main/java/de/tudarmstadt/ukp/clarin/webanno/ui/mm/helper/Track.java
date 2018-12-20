package de.tudarmstadt.ukp.clarin.webanno.ui.mm.helper;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.uhh.lt.webanno.mm.io.HiatTeiReaderUtils;
import de.uhh.lt.webanno.mm.io.HiatTeiMetadata.Speaker;

abstract class Track implements Serializable {
    
    private final static int CATEGORY_ORDERING(String desc){
        switch(desc) {
            case "sup": return 1;
            case "pho": return 2;
            case "v": return 3;
            case "akz": return 4;
            case "en": return 5;
            case "nv": return 6;
            case "nn": return 7;
            case "k": return 8;
            default: return Math.abs(desc.hashCode()) + 9;
        }
    }

//    static {
//        CATEGORY_ORDERING = new HashMap<>();
//        CATEGORY_ORDERING.put("sup", 1);
//        CATEGORY_ORDERING.put("pho", 2);
//        CATEGORY_ORDERING.put("v", 3);
//        CATEGORY_ORDERING.put("akz", 4);
//        CATEGORY_ORDERING.put("en", 5);
//        CATEGORY_ORDERING.put("nv", 6);
//        CATEGORY_ORDERING.put("nn", 7);
//        CATEGORY_ORDERING.put("k", 8);
//    }
    
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
            int i1 = CATEGORY_ORDERING(o1.getCategoryString());
            int i2 = CATEGORY_ORDERING(o2.getCategoryString());

            c = i1  - i2 ;
            return c;
        }
    };
    
    private static final long serialVersionUID = 1L;

    public abstract String getDescription();
    
    public abstract String getCategoryString();
    
    public abstract Speaker getSpeaker();
    
}
