package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import de.uhh.lt.webanno.exmaralda.io.TeiMetadata.Speaker;

public class SpeakerDetailsWindow extends ModalWindow {
    
    private static final long serialVersionUID = -6911254813496835955L;
    
    private final Speaker speaker;
    
    public SpeakerDetailsWindow(String id, Speaker spk) {
        super(id);
        
        speaker = spk;
                
        setInitialWidth(450);
        setInitialHeight(350);
        setResizable(true);
        setWidthUnit("px");
        setHeightUnit("px");
        setTitle("Speaker Details");

    }
    
    @Override
    public void show(IPartialPageRequestHandler aTarget) {
   
        
        setContent(new SpeakerDetailsWindowContent(getContentId(), this));
        
        super.show(aTarget);
    }

    

    public Speaker getSpeaker() {
        return speaker;
    }
    
    public static class SpeakerDetailsWindowContent extends Panel {

        public SpeakerDetailsWindowContent(String id, final SpeakerDetailsWindow window) {
            super(id);
            String speaker_xml = window.getSpeaker().xml_string;
            List<Pair<String, String>> key_value_pairs = parseXML(speaker_xml);
            add(new ListView<Pair<String, String>>("xmlelementcontainer", key_value_pairs) {
                private static final long serialVersionUID = 1L;
                @Override
                protected void populateItem(ListItem<Pair<String, String>> item) {
                    Pair<String, String> pair = item.getModelObject();
                    item.add(new Label("xmlelementname", pair.getKey() + ":"));
                    item.add(new Label("xmlelementtext", pair.getValue()));
                }                
            });
        }

        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        
    }
    
    public static List<Pair<String, String>> parseXML(String xml){
        List<Pair<String, String>> result = new ArrayList<>();
        try {
            Reader r = new StringReader(xml);
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(r);
            Element root = document.getRootElement();
            parseElement(root, result, "");
            r.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    private static void parseElement(Element element, List<Pair<String, String>> currentlist, String prefix){
        String key = element.getName();
        if("langKnown".equals(key)){
            parseLang(element, currentlist);
            return;
        }
        if("persName".equals(key))
            key = "Name";
        if("birth".equals(key))
            prefix = "Birth ";
        String val = element.getTextNormalize();    
        if(!StringUtils.isEmpty(val))
            currentlist.add(Pair.of(String.format("%s%s", prefix, StringUtils.capitalize(key)), val));
        for(Element child : element.getChildren())
            parseElement(child, currentlist, prefix);
    }
    
    private static void parseLang(Element langKnown, List<Pair<String, String>> currentlist){
        String key = String.format("Language (%s)", langKnown.getAttributeValue("level"));
        String val = String.format("%s (%s)", langKnown.getTextNormalize(),  langKnown.getAttributeValue("tag"));
        currentlist.add(Pair.of(StringUtils.capitalize(key), val));
        
    }
    
    

}