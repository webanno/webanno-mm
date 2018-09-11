/*
 * Copyright 2017
 * Ubiquitous Knowledge Processing (UKP) Lab and AB Language Technology
 * Technische Universität Darmstadt, Universität Hamburg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tudarmstadt.ukp.clarin.webanno.ui.project;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.MediaService;
import de.tudarmstadt.ukp.clarin.webanno.model.DocumentToMediaMapping;
import de.tudarmstadt.ukp.clarin.webanno.model.Mediaresource;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.ui.core.settings.ProjectSettingsPanel;
import de.tudarmstadt.ukp.clarin.webanno.ui.core.settings.ProjectSettingsPanelBase;

/**
 * A Panel used to add Mediafiles to the selected {@link Project}
 * 
 * 
 */
@ProjectSettingsPanel(label="Media", prio=250)
public class ProjectMediaPanel extends ProjectSettingsPanelBase {

    private final static Logger LOG = LoggerFactory.getLogger(ProjectMediaPanel.class);

    private static final long serialVersionUID = 2116717853865353733L;

    private @SpringBean DocumentService documentService;

    private @SpringBean MediaService mediaService;

    public static class ProjectMediaPanelFormModelData implements Serializable {
        private static final long serialVersionUID = -1L;

        Project project;

        ArrayList<String> documents = new ArrayList<>();
        String document_sel;

        ArrayList<String> mediafiles = new ArrayList<>();
        ArrayList<String> mediafiles_sel = new ArrayList<>();

        ArrayList<String> mediamappings = new ArrayList<>();
        ArrayList<String> mediamappings_sel = new ArrayList<>();

    }

    public ProjectMediaPanel(final String id, final IModel<Project> projectModel) {
        super(id);
        final ProjectMediaPanelFormModelData data = new ProjectMediaPanelFormModelData();
        data.project = projectModel.getObject();
        // fill media files
        mediaService.listMedia(data.project).forEach(m -> data.mediafiles.add(m.getName()));
        // fill documents
        documentService.listSourceDocuments(data.project).forEach(d -> data.documents.add(d.getName()));
        if(data.documents.isEmpty())
            data.documents.add("<no documents available>");
        data.document_sel = data.documents.get(0);
        // create the form
        add(new ProjectMediaPanelForm("mediaform", Model.of(data))); // new CompoundPropertyModel(new AnnotationLayerDetailFormModel)
    }

    private class ProjectMediaPanelForm extends Form<ProjectMediaPanelFormModelData> {

        private static final long serialVersionUID = -5715140116479339626L;

        private final FileUploadField fileUpload;
        private final TextField<String> urlUpload;

        public ProjectMediaPanelForm(String id, IModel<ProjectMediaPanelFormModelData> model) {
            super(id, model);

            ProjectMediaPanelFormModelData data = model.getObject();

            /* add media file upload opportunity */
            add(urlUpload = new TextField<String>("urlselector", new Model<>()));
            add(fileUpload = new FileUploadField("mediaselector", new Model<>()));
           
            add(new Button("upload"){
                private static final long serialVersionUID = 1L;
                @Override
                public void onSubmit()
                {
                    super.onSubmit();
                    final List<FileUpload> uploadFiles = fileUpload.getFileUploads();
                    
                    final Project project = data.project;

                    if (project.getId() == 0) {
                        error("Project not yet created, please save project details!");
                        return;
                    }

                    if (isEmpty(uploadFiles) && StringUtils.isEmpty(urlUpload.getModelObject())) {
                        error("No file selected, or URL provided please select a file first or enter a URL first.");
                        return;
                    }

                    for (FileUpload fup : uploadFiles) {
                        // create a new media resource as file
                        String fileName = fup.getClientFileName();
                        if (mediaService.existsMedia(project, fileName)) {
                            error("File '" + fileName + "' already exists!");
                            continue;
                        }
                        try {

                            Mediaresource mfile = new Mediaresource();
                            mfile.setName(fileName);
                            mfile.setProvidedAsURL(false);
                            mfile.setProject(project);
                            mfile.setContentLength(fup.getSize());
                            mfile.setMD5(fup.getMD5());
                            mfile.setContentType(fup.getContentType());

                            mediaService.uploadMedia(fup.getInputStream(), mfile);
                            data.mediafiles.add(fileName);

                            info("File [" + fileName + "] has been uploaded successfully!");
                        } catch (Exception e) {
                            error("Error while uploading file " + fileName + ": "
                                    + ExceptionUtils.getRootCauseMessage(e));
                            LOG.error(fileName + ": " + e.getMessage(), e);
                        }
                    }

                    if(!StringUtils.isEmpty(urlUpload.getModelObject())){
                        // create a new media resource as link
                        String name = urlUpload.getModelObject();
                        if (mediaService.existsMedia(project, name)) {
                            error("Resource '" + name + "' already exists!");
                        }else{
                            try {

                                Mediaresource mfile = new Mediaresource();
                                mfile.setName(name);
                                mfile.setProvidedAsURL(true);
                                mfile.setProject(project);
                                // mfile.setContentType(contentType);

                                mediaService.createMediaresource(mfile);
                                data.mediafiles.add(name);

                                info("Resource [" + name + "] has been imported successfully!");
                            } catch (Exception e) {
                                error("Error while importing resource " + name + ": "
                                        + ExceptionUtils.getRootCauseMessage(e));
                                LOG.error(name + ": " + e.getMessage(), e);
                            }
                        }
                    }
                }
            });

            final ListMultipleChoice<String> filechoice = new ListMultipleChoice<String>("mediafiles", Model.ofList(data.mediafiles_sel), data.mediafiles);
			filechoice.setMarkupId("mediachoices");
			filechoice.setOutputMarkupId(true);
			filechoice.add(new OnChangeAjaxBehavior() {
				private static final long serialVersionUID = 7751449875296166785L;
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					// forcing model update on selection such that the dblclick will work
				}
			});
            // download or show on doubleclick
            filechoice.add(new AjaxEventBehavior("dblclick") {
                private static final long serialVersionUID = 1L;
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    if (data.mediafiles_sel.size() > 0) {
                        String fname = data.mediafiles_sel.get(data.mediafiles_sel.size() - 1);
                        Mediaresource mf = mediaService.getMedia(data.project, fname);
                        long mfid = mf.getId();
                        // urlFor(new MediafileResourceReference)
                        String mediaurl = String.format("media/%d/%d", data.project.getId(), mfid);
                        target.appendJavaScript(String.format("window.open('%s')", mediaurl));
                    }
                }
            });
            add(filechoice);

            Button removeButton = new Button("remove") {
                private static final long serialVersionUID = 4053376790104708784L;
                @Override
                public void onSubmit() {
                    Project project = data.project;
                    for (String file : data.mediafiles_sel) {
                        try {
                            mediaService.removeMedia(mediaService.getMedia(project, file));
                            data.mediafiles.remove(file);
                        } catch (IOException e) {
                            error("Error while removing file " + ExceptionUtils.getRootCauseMessage(e));
                        }
                    }
                    data.mediafiles_sel.clear();
                }
            };
            // Add check to prevent accidental delete operation
            removeButton.add(new AttributeModifier("onclick",
                    "if(!confirm('Do you really want to delete this file?')) return false;"));
            add(removeButton);


            /* show the documents with currently mapped media files */
            IModel<ArrayList<String>> m = new LoadableDetachableModel<ArrayList<String>>() {
                private static final long serialVersionUID = -8939651060310517878L;

                @Override
                protected ArrayList<String> load() {
                    if (StringUtils.isEmpty(data.document_sel) || data.document_sel.startsWith("<no"))
                        return new ArrayList<String>(Arrays.asList("<select a document>"));
                    SourceDocument doc = documentService.getSourceDocument(data.project, data.document_sel);
                    data.mediamappings.clear();
                    data.mediamappings
                    .addAll(mediaService.listDocumentMediaMappings(data.project.getId(), doc)
                            .stream().map(m -> m.getMedia().getName()).collect(Collectors.toList()));
                    if (data.mediamappings.isEmpty())
                        return new ArrayList<String>(Arrays.asList("<empty>"));
                    return data.mediamappings;
                }
            };

            final ListMultipleChoice<String> mediamappings = new ListMultipleChoice<String>("mediamappings",
                    Model.ofList(data.mediamappings_sel), m);
            mediamappings.setMarkupId("mediamappings");
            mediamappings.setOutputMarkupId(true);
            add(mediamappings);

            ListChoice<String> documentselection = new ListChoice<>("documentselection", new PropertyModel<>(data, "document_sel"), data.documents);
            documentselection.add(new OnChangeAjaxBehavior() {
                private static final long serialVersionUID = 1L;
                @Override
                protected void onUpdate(AjaxRequestTarget aTarget) {
                    // the underlying model changed, so we have to add it again
                    aTarget.add(mediamappings); // update
                }
            });
            add(documentselection);

            Button addMediaMappingButton = new Button("addmapping") {
                private static final long serialVersionUID = 8791413940368513114L;

                @Override
                public void onSubmit() {
                    if(data.document_sel == null || data.document_sel.startsWith("<")){
                        error("Please upload a document first.");
                        return;
                    }
                    SourceDocument d = documentService.getSourceDocument(data.project, data.document_sel);
                    for (String mediafilename : data.mediafiles_sel) {
                        Mediaresource m = mediaService.getMedia(data.project, mediafilename);

                        if (mediaService.existsDocumentMediaMapping(data.project.getId(), m.getId(), d.getId())) {
                            error(String.format("Media mapping for '%s' and '%s' already exists!", d.getName(),
                                    m.getName()));
                            continue;
                        }

                        try {

                            DocumentToMediaMapping mapping = new DocumentToMediaMapping();
                            mapping.setMedia(m);
                            mapping.setSource_document(d);
                            mapping.setProject(data.project);
                            mediaService.createDocumentMediaMapping(mapping);

                            info(String.format("Media mapping for '%s' and '%s' has been created!", d.getName(),
                                    m.getName()));
                        } catch (Exception e) {
                            String msg = String.format("Error while creating media mapping for '%s' and '%s': %s",
                                    d.getName(), m.getName(), ExceptionUtils.getRootCauseMessage(e));
                            error(msg);
                            LOG.error(msg, e);
                        }
                    }
                    data.mediafiles_sel.clear();
                    data.mediamappings_sel.clear();
                }
            };
            add(addMediaMappingButton);

            Button removeMediaMappingButton = new Button("removemapping") {
                private static final long serialVersionUID = -3350978509743917996L;
                @Override
                public void onSubmit() {
                    if(StringUtils.isEmpty(data.document_sel) || data.document_sel.startsWith("<no"))
                        return;
                    SourceDocument d = documentService.getSourceDocument(data.project, data.document_sel);
                    data.mediamappings_sel.forEach(mname -> {
                        Mediaresource m = mediaService.getMedia(data.project, mname);
                        DocumentToMediaMapping mapping = mediaService
                                .getDocumentMediaMapping(data.project.getId(), m.getId(), d.getId());
                        mediaService.removeDocumentMediaMapping(mapping);
                        data.mediamappings.remove(m.getName());
                    });
                    data.mediafiles_sel.clear();
                }

            };
            add(removeMediaMappingButton);
        }
    }
}
