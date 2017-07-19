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
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOption;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
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

	private class ProjectMediaPanelData implements Serializable {

		private static final long serialVersionUID = 6236624039542636369L;

		private Project selected_project;
		private IModel<Project> selected_project_model = new PropertyModel<>(this, "selected_project");

		private String selected_document;
		private IModel<String> selected_document_model = new PropertyModel<>(this, "selected_document");

		private ArrayList<String> media_files = new ArrayList<>();
		private IModel<ArrayList<String>> media_files_model = new PropertyModel<>(this, "media_files");

		private ArrayList<String> selected_media_files = new ArrayList<>();
		private IModel<ArrayList<String>> selected_media_files_model = new PropertyModel<>(this,
				"selected_media_files");

		private ArrayList<String> media_mappings_for_document = new ArrayList<>();
		private IModel<ArrayList<String>> media_mappings_for_document_model = new PropertyModel<>(this,
				"media_mappings_for_document");

		private ArrayList<String> selected_media_mappings = new ArrayList<>();
		private IModel<ArrayList<String>> selected_media_mappings_model = new PropertyModel<>(this,
				"selected_media_mappings");

	}

	private final static Logger LOG = LoggerFactory.getLogger(ProjectMediaPanel.class);

	private static final long serialVersionUID = 2116717853865353733L;

	private @SpringBean DocumentService documentService;

	private @SpringBean MediaService mediaService;

	private final Form<?> form_mediamappings;

	private final ProjectMediaPanelData data = new ProjectMediaPanelData();

	public ProjectMediaPanel(final String id, final IModel<Project> projectModel) {
		super(id);
		data.selected_project_model.setObject(projectModel.getObject());

		add(new MediaUploadForm("mediauploadform"));
		add(form_mediamappings = new MediaMappingForm("mediamappingform", data.selected_document_model));
		add(new DocumentSelectionForm("documentselectionform"));

	}

	private List<String> getDocumentList() {
		return documentService.listSourceDocuments(data.selected_project).stream()
				.map(d -> d.getName()).collect(Collectors.toList());
	}

	private class MediaUploadForm extends Form<Void> {

		private static final long serialVersionUID = -5715140116479339626L;
		
		private final FileUploadField fileUpload;
		private final TextField<String> urlUpload;

		public MediaUploadForm(String id) {
			super(id);

			/* add media file upload opportunity */
			add(urlUpload = new TextField<String>("urlselector", new Model<>()));
			add(fileUpload = new FileUploadField("mediaselector", new Model<>()));
			
//			setMultiPart(true);
//			setMaxSize(Bytes.megabytes(500));
			
			add(new Button("upload"));
//			{
//				private static final long serialVersionUID = 1L;
//
//				@Override
//				public void onSubmit() {
//					
//				}
//			});

			data.media_files.addAll(mediaService.listMedia(data.selected_project).stream().map(m -> m.getName())
					.collect(Collectors.toList()));
			final ListMultipleChoice<String> filechoice = new ListMultipleChoice<String>("files",
					data.selected_media_files_model, data.media_files_model);
			filechoice.setMarkupId("mediachoices");
			filechoice.setOutputMarkupId(true);
			filechoice.add(new OnChangeAjaxBehavior() {
				private static final long serialVersionUID = 7751449875296166785L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					// forcing model update
				}
			});
			filechoice.add(new AjaxEventBehavior("dblclick") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onEvent(AjaxRequestTarget target) {
					if (data.selected_media_files.size() > 0) {
						String fname = data.selected_media_files.get(data.selected_media_files.size() - 1);
						Mediaresource mf = mediaService.getMedia(data.selected_project, fname);
						long mfid = mf.getId();
						// urlFor(new MediafileResourceReference)
						String mediaurl = String.format("media/%d/%d", data.selected_project.getId(), mfid);
						target.appendJavaScript(String.format("window.open('%s')", mediaurl));
					}
				}
			});
			add(filechoice);

			Button removeButton = new Button("remove") {
				private static final long serialVersionUID = 4053376790104708784L;

				@Override
				public void onSubmit() {
					Project project = data.selected_project;
					for (String file : data.selected_media_files) {
						try {
							mediaService.removeMedia(mediaService.getMedia(project, file));
							data.media_files.remove(file);
						} catch (IOException e) {
							error("Error while removing file " + ExceptionUtils.getRootCauseMessage(e));
						}
					}
					data.selected_media_files.clear();
				}
			};
			// Add check to prevent accidental delete operation
			removeButton.add(new AttributeModifier("onclick",
					"if(!confirm('Do you really want to delete this file?')) return false;"));
			add(removeButton);

		}
		
		@Override
		protected void onSubmit()
		{
		    // TODO Auto-generated method stub
		    super.onSubmit();
		    final List<FileUpload> uploadFiles = fileUpload.getFileUploads();
            final Project project = data.selected_project;

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
                    data.media_files.add(fileName);

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
                        data.media_files.add(name);

                        info("Resource [" + name + "] has been imported successfully!");
                    } catch (Exception e) {
                        error("Error while importing resource " + name + ": "
                                + ExceptionUtils.getRootCauseMessage(e));
                        LOG.error(name + ": " + e.getMessage(), e);
                    }
                }
            }
		}
	}

	private class DocumentSelectionForm extends Form<Void> {

		private static final long serialVersionUID = 4159705518182030036L;

		public DocumentSelectionForm(String id) {
			super(id);

			/* show the documents with currently mapped media files */

			Select<String> documentselection = new Select<String>("documentselection", data.selected_document_model);
			ListView<String> documentscontainer = new ListView<String>("documentscontainer", getDocumentList()) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(ListItem<String> item) {
					SelectOption<String> document = new SelectOption<String>("document",
							new Model<String>(item.getModelObject())) {
						private static final long serialVersionUID = 1L;

						@Override
						public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
							replaceComponentTagBody(markupStream, openTag, item.getModelObject());
						}
					};
					item.add(document);
				}
			};
			documentselection.setMarkupId("documentselection");
			documentselection.setOutputMarkupId(true);
			documentselection.add(documentscontainer);
			documentselection.add(new OnChangeAjaxBehavior() {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget aTarget) {
					// the underlying model changed, so we have to add it again
					aTarget.add(form_mediamappings);
				}
			});
			add(documentselection);

		}

	}

	private class MediaMappingForm extends Form<String> {

		private static final long serialVersionUID = 2179387974312830269L;

		public MediaMappingForm(String id, IModel<String> model_selected_document) {
			super(id, model_selected_document);

			data.media_mappings_for_document_model = new LoadableDetachableModel<ArrayList<String>>() {
				private static final long serialVersionUID = -8939651060310517878L;

				@Override
				protected ArrayList<String> load() {
					if (StringUtils.isEmpty(data.selected_document))
						return new ArrayList<String>(Arrays.asList("<select a document>"));
					SourceDocument doc = documentService.getSourceDocument(data.selected_project,
							data.selected_document);
					data.media_mappings_for_document.clear();
					data.media_mappings_for_document
							.addAll(mediaService.listDocumentMediaMappings(data.selected_project.getId(), doc)
									.stream().map(m -> m.getMedia().getName()).collect(Collectors.toList()));
					if (data.media_mappings_for_document.isEmpty())
						return new ArrayList<String>(Arrays.asList("<empty>"));
					return data.media_mappings_for_document;
				}
			};

			final ListMultipleChoice<String> mediamappings = new ListMultipleChoice<String>("mediamappings",
					data.selected_media_mappings_model, data.media_mappings_for_document_model);
			mediamappings.setMarkupId("mediamappings");
			mediamappings.setOutputMarkupId(true);
			add(mediamappings);

			Button addMediaMappingButton = new Button("addmapping") {
				private static final long serialVersionUID = 8791413940368513114L;

				@Override
				public void onSubmit() {
					SourceDocument d = documentService.getSourceDocument(data.selected_project, data.selected_document);
					for (String mediafilename : data.selected_media_files) {
						Mediaresource m = mediaService.getMedia(data.selected_project, mediafilename);

						if (mediaService.existsDocumentMediaMapping(data.selected_project.getId(), m.getId(), d.getId())) {
							error(String.format("Media mapping for '%s' and '%s' already exists!", d.getName(),
									m.getName()));
							continue;
						}

						try {

							DocumentToMediaMapping mapping = new DocumentToMediaMapping();
							mapping.setMedia(m);
							mapping.setSource_document(d);
							mapping.setProject(data.selected_project);
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
					data.selected_media_files.clear();
					data.selected_media_mappings.clear();
					MediaMappingForm.this.add(mediamappings);
				}
			};
			add(addMediaMappingButton);

			Button removeMediaMappingButton = new Button("removemapping") {

				private static final long serialVersionUID = -3350978509743917996L;

				@Override
				public void onSubmit() {
					SourceDocument d = documentService.getSourceDocument(data.selected_project, data.selected_document);
					data.selected_media_mappings.forEach(mname -> {
						Mediaresource m = mediaService.getMedia(data.selected_project, mname);
						DocumentToMediaMapping mapping = mediaService
								.getDocumentMediaMapping(data.selected_project.getId(), m.getId(), d.getId());
						mediaService.removeDocumentMediaMapping(mapping);
						data.media_mappings_for_document.remove(m.getName());
					});
					data.selected_media_mappings.clear();
					MediaMappingForm.this.add(mediamappings);
				}

			};
			add(removeMediaMappingButton);
		}
	}
}
