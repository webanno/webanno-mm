package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.AjaxCallback;

public class SettingsWindow extends ModalWindow {
    
    private static final long serialVersionUID = -6911254813496835955L;
    
    private boolean closeButtonClicked;

    private transient AjaxCallback onChangeAction;
    
    private transient Runnable onChangeLocalAction;
    
    private final SourceDocument doc;

    public SettingsWindow(String id, SourceDocument doc) {
        super(id);
        this.doc = doc;
                
        setInitialWidth(600);
        setInitialHeight(450);
        setResizable(true);
        setWidthUnit("px");
        setHeightUnit("px");
        setTitle("Settings");
        setCssClassName("w_blue w_flex");
        setCloseButtonCallback((target) -> {
            closeButtonClicked = true;
            return true;
        });
    }
    
    @Override
    public void show(IPartialPageRequestHandler aTarget) {
        closeButtonClicked = false;
        
        setWindowClosedCallback((target) -> {
            if (!closeButtonClicked) {
                onConfirmInternal(target);
            }
        });
        
        setContent(new SettingsWindowContent(getContentId(), this) {
            private static final long serialVersionUID = -3434069761864809703L;

            @Override
            protected void onCancel(AjaxRequestTarget aTarget) {
                closeButtonClicked = true;
            }
        });
        
        super.show(aTarget);
    }

    public AjaxCallback getOnChangeAction() {
        return onChangeAction;
    }

    public void setOnChangeAction(AjaxCallback aOnChangeAction) {
        onChangeAction = aOnChangeAction;
    }
    
    public Runnable getOnChangeLocalAction() {
        return onChangeLocalAction;
    }

    public void setOnChangeLocalAction(Runnable onChangeLocalAction) {
        this.onChangeLocalAction = onChangeLocalAction;
    }

    protected void onConfirmInternal(AjaxRequestTarget aTarget) {
        boolean closeOk = true;
        
        if(onChangeLocalAction != null)
            onChangeLocalAction.run();
        
        // Invoke callback if one is defined
        if (onChangeAction != null)
            try {
                onChangeAction.accept(aTarget);
            }
            catch (Exception e) {
                closeOk = false;
            }
        
        
        if (closeOk)
            close(aTarget);
    }    
    
    public boolean isCloseButtonClicked() {
        return closeButtonClicked;
    }

    public void setCloseButtonClicked(boolean closeButtonClicked) {
        this.closeButtonClicked = closeButtonClicked;
    }

    public SourceDocument getSourceDocument()
    {
        return doc;
    }
}