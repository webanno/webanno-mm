package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
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

import de.tudarmstadt.ukp.clarin.webanno.model.Mediaresource;


public class SettingsWindowContent extends Panel {

    private static final long serialVersionUID = 1L;

    private class PartiturPreferenceForm extends Form<PartiturPreferences> {

        private static final long serialVersionUID = 1L;

        public PartiturPreferenceForm(String id, final SettingsWindow settingsWindow) {

            super(id, new CompoundPropertyModel<>(PartiturPreferences.load(settingsWindow.getSourceDocument())));
            
            add(new Label("info", String.format("Project %d Document %d", getModelObject().document.getProject().getId(), getModelObject().document.getId())));

            add(new TextField<Integer>("partiturtablewidth"));
                       
            List<Mediaresource> media_files = Collections.emptyList();
            
            final DropDownChoice<Mediaresource> mediaChoice = new DropDownChoice<>(
                    "mediachoice",
                    Model.of(media_files.size() > 0 ? media_files.get(0) : null), 
                    media_files,
                    new ChoiceRenderer<Mediaresource>("name", "id"));
//            mediaChoice.add(new AjaxFormComponentUpdatingBehavior("change") {
//                private static final long serialVersionUID = -7860861746085374959L;
//
//                @Override
//                protected void onUpdate(AjaxRequestTarget target) {
//                    Mediaresource m = mediaChoice.getModelObject();
//                    if(m == null)
//                        return;
//                    Source newSource = new Source("mediasource", new MediaResourceReference(), new PageParameters().add(MediaResourceStreamResource.PAGE_PARAM_PROJECT_ID, pid).add(MediaResourceStreamResource.PAGE_PARAM_FILE_ID, m.getId()));
//                    if(!m.isProvidedAsURL())
//                        newSource.setType(m.getContentType());
//                    newSource.setDisplayType(true);
//                    video.addOrReplace(newSource);
//                }
//            });
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
