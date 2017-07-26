package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.helper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

abstract class Track implements Serializable, Comparable<Track>{
    
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
    
    private static final long serialVersionUID = 1L;

    public abstract String getDescription();
    
    public abstract String getCategoryString();
    
    public abstract String getSpeakerString();
    
    @Override
    public int compareTo(Track o) {
        int c = getSpeakerString().compareTo(o.getSpeakerString());
        if(c != 0)
            return c;
        Integer i1 = CATEGORY_ORDERING.get(getCategoryString());
        Integer i2= CATEGORY_ORDERING.get(o.getCategoryString());
        c = Integer.compare(i1 == null ? 0 : i1, i2 == null ? 0 : i2);
        return c;

    }

}
