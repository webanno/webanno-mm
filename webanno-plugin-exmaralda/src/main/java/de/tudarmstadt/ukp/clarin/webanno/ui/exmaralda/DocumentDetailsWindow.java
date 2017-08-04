package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import de.uhh.lt.webanno.exmaralda.io.TeiMetadata.Description;

public class DocumentDetailsWindow extends ModalWindow {
    
    private static final long serialVersionUID = -6911254813496835955L;
    
    private final Description description;
    
    public DocumentDetailsWindow(String id, Description desc) {
        super(id);
        
        description = desc;
                
        setInitialWidth(450);
        setInitialHeight(350);
        setResizable(true);
        setWidthUnit("px");
        setHeightUnit("px");
        setTitle("Document Details");

    }
    
    @Override
    public void show(IPartialPageRequestHandler aTarget) {
        setContent(new DocumentDetailsWindowContent(getContentId(), this));
        super.show(aTarget);
    }

    public Description getDescription() {
        return description;
    }
    
    public static class DocumentDetailsWindowContent extends Panel {

        public DocumentDetailsWindowContent(String id, final DocumentDetailsWindow window) {
            super(id);
            String title = window.getDescription().title;
            add(new Label("title", StringUtils.isEmpty(title) ? "<empty>" : title));
            add(new Label("fildescxmldetails", window.getDescription().xml_string_filedesc));
            add(new Label("settingsdescxmldetails", window.getDescription().xml_string_settingdesc));
        }

        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        
    }
    
}