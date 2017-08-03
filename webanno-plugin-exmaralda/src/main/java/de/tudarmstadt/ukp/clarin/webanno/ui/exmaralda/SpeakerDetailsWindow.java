package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda;

import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

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
            add(new Label("i", window.getSpeaker().i));
            add(new Label("n", window.getSpeaker().n));
            add(new Label("id", window.getSpeaker().id));
            add(new Label("xmldetails", window.getSpeaker().xml_string));
        }

        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        
    }
    
}