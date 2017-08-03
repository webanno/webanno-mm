package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.tudarmstadt.ukp.clarin.webanno.api.MediaService;
import de.tudarmstadt.ukp.clarin.webanno.model.Mediaresource;


public class SettingsWindowContent extends Panel {

    private static final long serialVersionUID = 1L;
    
    private @SpringBean MediaService mediaService;

    private class PartiturPreferenceForm extends Form<PartiturPreferences> {

        private static final long serialVersionUID = 1L;

        public PartiturPreferenceForm(String id, final SettingsWindow settingsWindow) {

            super(id, new CompoundPropertyModel<>(PartiturPreferences.load(settingsWindow.getSourceDocument())));
            
            add(new Label("info", String.format("Project %d Document %d", getModelObject().document.getProject().getId(), getModelObject().document.getId())));

            add(new TextField<Integer>("partiturtablewidth"));

            List<Mediaresource> media_files = mediaService.listDocumentMediaMappings(getModelObject().document.getProject().getId(), getModelObject().document).stream().map(x -> x.getMedia()).collect(Collectors.toList());
            
            final DropDownChoice<Mediaresource> mediaChoice = new DropDownChoice<>(
                    "mediachoice", 
                    media_files,
                    new ChoiceRenderer<Mediaresource>("name", "id"));
            add(mediaChoice);

            add(new AjaxSubmitLink("saveButton") {
                private static final long serialVersionUID = -755759008587787147L;

                @Override
                protected void onSubmit(AjaxRequestTarget aTarget, Form<?> aForm) {
                    // save
                    getModelObject().save();
                    settingsWindow.close(aTarget); 
                }

                @Override
                protected void onError(AjaxRequestTarget aTarget, Form<?> aForm) { }
            });

            add(new AjaxLink<Void>("cancelButton") {
                private static final long serialVersionUID = 7202600912406469768L;

                @Override
                public void onClick(AjaxRequestTarget aTarget) {
                    PartiturPreferenceForm.this.detach();
                    onCancel(aTarget);
                    settingsWindow.close(aTarget);
                }
            });
        }
    }

    public SettingsWindowContent(String id, final SettingsWindow window) {
        super(id);
        add(new PartiturPreferenceForm("preferenceform", window));
    }

    protected void onCancel(AjaxRequestTarget aTarget) { }

}
