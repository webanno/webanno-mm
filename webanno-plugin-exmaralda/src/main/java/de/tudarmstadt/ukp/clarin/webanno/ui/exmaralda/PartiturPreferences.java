package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda;
import java.io.Serializable;

import org.apache.wicket.Session;

import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;

public class PartiturPreferences implements Serializable {
    
    private static final long serialVersionUID = -1L;
    
    private static String SESSION_PARAM_NAME_PATTERN = "preferences_%d_%d";
    
    public SourceDocument document;
    
    public int partiturtablewidth;
    
    public static PartiturPreferences getDefault(SourceDocument doc){
        PartiturPreferences pref = new PartiturPreferences();
        pref.document = doc;
        pref.partiturtablewidth = 170;
        pref.save();
        return pref;
    }
    
    public static PartiturPreferences load(SourceDocument doc){
        PartiturPreferences pref = (PartiturPreferences)Session.get().getAttribute(String.format(SESSION_PARAM_NAME_PATTERN, doc.getProject().getId(), doc.getId()));
        if(pref == null)
            return getDefault(doc);
        return pref;
    }
    
    public void save(){
        Session.get().setAttribute(String.format(SESSION_PARAM_NAME_PATTERN, document.getProject().getId(), document.getId()), this);
    }
    
}