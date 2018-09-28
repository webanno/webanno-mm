package de.tudarmstadt.ukp.clarin.webanno.ui.mm;
import java.io.Serializable;

import org.apache.wicket.Session;

import de.tudarmstadt.ukp.clarin.webanno.model.Mediaresource;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;

public class PartiturPreferences implements Serializable {

    private static final long serialVersionUID = -1L;

    private static String SESSION_PARAM_NAME_PATTERN = "preferences_%d_%d";

    public SourceDocument document;

    public Mediaresource mediachoice;

    public int partiturtablewidth;

    public static PartiturPreferences getDefault(SourceDocument doc){
        PartiturPreferences pref = new PartiturPreferences();
        pref.document = doc;
        pref.partiturtablewidth = 170;
        pref.mediachoice = null;
        pref.save();
        return pref;
    }

    public static PartiturPreferences load(SourceDocument doc){
        PartiturPreferences pref = (PartiturPreferences)Session.get().getAttribute(String.format(SESSION_PARAM_NAME_PATTERN, doc.getProject().getId(), doc.getId()));
        if(pref == null)
            return getDefault(doc);
        return pref;

//        WebRequest req = (WebRequest)(RequestCycle.get().getRequest());
//        Cookie c = req.getCookie("asd");
//        c.getValue();

    }

    public void save(){
        Session.get().setAttribute(String.format(SESSION_PARAM_NAME_PATTERN, document.getProject().getId(), document.getId()), this);

//        WebResponse resp = (WebResponse)RequestCycle.get().getResponse();
//        Cookie cookie = new Cookie("cookieName", "cookieValue");
//        resp.addCookie(cookie);
    }

}