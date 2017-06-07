package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda;


import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.jcas.JCas;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.media.Source;
import org.apache.wicket.markup.html.media.video.Video;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;

import de.tudarmstadt.ukp.clarin.webanno.api.AnnotationSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.MediaService;
import de.tudarmstadt.ukp.clarin.webanno.model.Mediaresource;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.model.DocumentToMediaMapping;
import de.uhh.lt.webanno.exmaralda.type.Anchor;
import de.uhh.lt.webanno.exmaralda.type.TEIspan;
import de.uhh.lt.webanno.exmaralda.io.PartiturIndex;
import de.uhh.lt.webanno.exmaralda.io.TeiMetadata;
import de.uhh.lt.webanno.exmaralda.io.TeiMetadata.Timevalue;


@MountPath(value = "/partitur", alt = "/partitur/${" + ExmaraldaPartitur.PAGE_PARAM_PROJECT_ID + "}/${"+ ExmaraldaPartitur.PAGE_PARAM_DOCUMENT_ID + "}")
public class ExmaraldaPartitur extends WebPage {

	public static final String PAGE_PARAM_PROJECT_ID = "pId";
	public static final String PAGE_PARAM_DOCUMENT_ID = "dId";

	private static final Logger LOG = LoggerFactory.getLogger(ExmaraldaPartitur.class);

	private @SpringBean DocumentService documentService;
	
	private @SpringBean MediaService mediaService;
	
	private @SpringBean AnnotationSchemaService annotationService;


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExmaraldaPartitur() {
		this(new PageParameters().add(PAGE_PARAM_PROJECT_ID, -1).add(PAGE_PARAM_DOCUMENT_ID, -1));
	}

	public ExmaraldaPartitur(PageParameters params) {

		long pid = params.get(PAGE_PARAM_PROJECT_ID).toLong();
		long did = params.get(PAGE_PARAM_DOCUMENT_ID).toLong();

		Label l = new Label("title", String.format("%d %d: ", pid, did));
		add(l);

		/* get the data */
		SourceDocument doc;
		JCas textview;
		TeiMetadata meta;

		try {
			doc = documentService.getSourceDocument(pid, did);	
		} catch (Exception e) {
			String message = String.format("Error while retrieving document %d for project %d.", did, pid);
			LOG.error(message, e);
			setErrorMessage(message);
			return;
		}

		try {
			textview = documentService.createOrReadInitialCas(doc);
		} catch (Exception e) {
			String message = String.format("Error while retrieving cas from source document %s.", doc.getName());
			LOG.error(message, e);
			setErrorMessage(message);
			return;
		}

		try {
			meta = TeiMetadata.getFromCas(textview);
			setInfo(meta.speakers.stream().map(x -> String.format("%s: %s", x.id, x.n)).collect(Collectors.joining(", ")));
		} catch (Exception e) {
			String message = String.format("Error while retrieving TEI metadata from source document %s.", doc.getName());
			LOG.error(message, e);
			setErrorMessage(message);
			return;
		}
		
		/* set up the video */
		Video video = new Video("media");
        video.setPoster(new PackageResourceReference(getClass(), "no-video.jpg"));
		List<DocumentToMediaMapping> media_files = mediaService.listDocumentMediaMappings(pid, did);
		if(media_files.size() > 0){
			Mediaresource mfile = media_files.get(0).getMedia();
			// media_url = urlFor(new MediaResourceReference(), new PageParameters().add(MediaResourceStreamResource.PAGE_PARAM_PROJECT_ID, pid).add(MediaResourceStreamResource.PAGE_PARAM_FILE_ID, mfile.getId()));
			Source source = new Source("mediasource", new MediaResourceReference(), new PageParameters().add(MediaResourceStreamResource.PAGE_PARAM_PROJECT_ID, pid).add(MediaResourceStreamResource.PAGE_PARAM_FILE_ID, mfile.getId()));    
	        source.setDisplayType(true);
	        source.setType(mfile.getContentType());
	        video.add(source);
		}else{
			video.add(new Source("mediasource"));
		}
        add(video);
				
		/* set up the collapse buttons */
		add(new ListView<String>("collapsebuttons", meta.spantypes.stream().collect(Collectors.toList())){
			private static final long serialVersionUID = 1L;
			protected void populateItem(ListItem<String> item) {
				CheckBox button = new CheckBox("collapsebutton", new Model<Boolean>(Boolean.valueOf(true)));
				button.add(new AjaxEventBehavior("onchange") {
					private static final long serialVersionUID = 1L;
					@Override
					protected void onEvent(AjaxRequestTarget target) {
						button.setModelObject(!button.getModelObject());
						String js = String.format("console.log('%1$s'); console.log('%2$s'); showHideTier('%1$s', %2$s)", item.getModelObject(), button.getModelObject());
						target.appendJavaScript(js);
					}
				});
				item.add(button);
				item.add(new Label("collapsebuttondesc", item.getModelObject()));
			};
		});

		/* set up the partitur visualization */
		final PartiturIndex pindex = new PartiturIndex(meta, textview);
		
		LOG.info("Number of anchors: {}.", meta.timeline.size());		

		ListView<Timevalue> segments = new ListView<Timevalue>("segments", meta.timeline.subList(0, meta.timeline.size()-1)) {
			private static final long serialVersionUID = 1L;
			protected void populateItem(ListItem<Timevalue> item) {
				Timevalue tv = item.getModelObject();

				/* add link to list view */
				Label listref = new Label("listref", tv.id); // TODO: replace by internal link
				listref.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = 1L;
					@Override
					protected void onEvent(final AjaxRequestTarget target) {
//						String url = String.format("partitur/%d/%d#%s", pid, did, anchorId);
//						".../annotate...#" + item.getModelObject(),
						String url = "http://google.de";
						target.appendJavaScript(String.format("window.opener.location.href='%s'; window.blur(); window.opener.focus();", url));
					}
				});
				item.add(listref);
				/* add link to play media */
				Image mediaref = new Image("mediaref", "pbn.gif");
				mediaref.add(new AttributeModifier("title", String.format("%.3f - Click to start player", tv.interval)));
				mediaref.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = 1L;
					@Override
					protected void onEvent(final AjaxRequestTarget target) {						
						target.appendJavaScript(String.format("vidjump('%f');", tv.interval));
					}
				});
				mediaref.setOutputMarkupId(true);
				mediaref.setMarkupId(tv.id);
				item.add(mediaref);

				class DataHolder implements Serializable {
					private static final long serialVersionUID = 1L;
					public DataHolder(String html_row_class, Label col_text, Label col_textdesc) {
						this.html_row_class = html_row_class;
						this.col_text = col_text;
						this.col_textdesc = col_textdesc;
					}
					final String html_row_class; 
					final Label col_text;
					final Label col_textdesc;
				}
				

				/* add speakertexts and annotations */
				// get the segments that span this anchor
				IModel<List<DataHolder>> rowsegments = new LoadableDetachableModel<List<DataHolder>>() {
					private static final long serialVersionUID = 1L;
					protected List<DataHolder> load() {
						final List<DataHolder> labels = new LinkedList<>();
						// create a label for each speaker text
						// and all spanannotations for each speaker
						meta.speakers.stream()
						  .forEach(spk -> {
							  String speakertext = pindex.getSpeakertextForTimevalue(spk, tv);
							  if(!StringUtils.isEmpty(speakertext)){
								  Label speakertext_desc = new Label("textdesc", String.format("%s [v]", spk.n));
								  speakertext_desc.add(new AttributeModifier("class", "tlm"));
								  labels.add(new DataHolder(
										  "v",
										  speakertext_desc,
										  new Label("text", speakertext)));
							  }
							  meta.spantypes.stream().forEach(stype -> {
								  String contents = pindex.selectSpeakerAnnotationsForTimevalue(spk, tv, TEIspan.class)
										  .stream()
										  .filter(x -> stype.equals(x.getSpanType()))
										  .map(x -> x.getContent())
										  .collect(Collectors.joining("; "));
//								  if(!StringUtils.isEmpty(contents)){
									  final Label span_annotation = new Label("text", contents);
									  span_annotation.add(new AttributeModifier("class", stype));
									  final Label span_annotation_desc = new Label("textdesc", String.format("%s [%s]", spk.n, stype));
									  span_annotation_desc.add(new AttributeModifier("class", "tlo"));
									  labels.add(new DataHolder(
											  stype, 
											  span_annotation_desc,
											  span_annotation));
//								  }
							    });
						    });
						return labels;
					}
				};
				
				/* add */
				ListView<DataHolder> speakertexts = new ListView<DataHolder>("textrows", rowsegments) {
					private static final long serialVersionUID = 1L;
					@Override
					protected void populateItem(ListItem<DataHolder> subitem) {
						subitem.add(new AttributeModifier("class", subitem.getModelObject().html_row_class));
						subitem.add(new AttributeModifier("name", subitem.getModelObject().html_row_class));
						subitem.add(subitem.getModelObject().col_textdesc);
						subitem.add(subitem.getModelObject().col_text);
					}
				};
				item.add(speakertexts);
			}
		};
		add(segments);
		
		

	}
	
	private void setErrorMessage(String errormessage){
		add(new Label("info", errormessage));
		add(new Label("media", "No Media!"));
    	add(new ListView<Anchor>("segments"){
			private static final long serialVersionUID = 1L;
			@Override
			protected void populateItem(ListItem<Anchor> item) {/* nothing to do */}
    	});
		
	}


	private void setInfo(String message){
		Label l = new Label("info", message);
		add(l);
	}

}
