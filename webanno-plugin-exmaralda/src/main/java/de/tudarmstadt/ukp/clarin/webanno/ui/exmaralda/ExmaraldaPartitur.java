package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.media.Source;
import org.apache.wicket.markup.html.media.video.Video;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
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
import de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.helper.MyAnnotation;
import de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.helper.MyBigSegment;
import de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.helper.MySegment;
import de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.helper.MySpeaker;
import de.uhh.lt.webanno.exmaralda.io.PartiturIndex;
import de.uhh.lt.webanno.exmaralda.io.TeiMetadata;
import de.uhh.lt.webanno.exmaralda.io.TeiMetadata.Speaker;
import de.uhh.lt.webanno.exmaralda.io.TeiMetadata.Timevalue;
import de.uhh.lt.webanno.exmaralda.type.Anchor;
import de.uhh.lt.webanno.exmaralda.type.Incident;
import de.uhh.lt.webanno.exmaralda.type.TEIspan;


@MountPath(value = "/partitur", alt = "/partitur/${" + ExmaraldaPartitur.PAGE_PARAM_PROJECT_ID + "}/${"+ ExmaraldaPartitur.PAGE_PARAM_DOCUMENT_ID + "}")
public class ExmaraldaPartitur extends WebPage {

	public static final String PAGE_PARAM_PROJECT_ID = "pId";
	public static final String PAGE_PARAM_DOCUMENT_ID = "dId";
	public static final String PAGE_PARAM_TABLE_WIDTH = "width";

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
		
		int width_ = 170;
		if(params.getPosition(PAGE_PARAM_TABLE_WIDTH) > -1) 
			width_ = params.get(PAGE_PARAM_TABLE_WIDTH).toInt();
		
		int width = width_;
		if(width <= 0)
			width = Integer.MAX_VALUE;
		
	    TextField<Integer> fwidth = new TextField<Integer>("fwidth", new Model<Integer>(width_));
		
	    add((new Form<Void>("widthForm") {
	        private static final long serialVersionUID = 2445612544114726143L;

	        @Override
	        protected void onSubmit() {

	            PageParameters paramsNew = new PageParameters();
	            paramsNew.add("pId", pid);
	            paramsNew.add("dId", did);
	            paramsNew.add("width", fwidth.getModelObject());
	            setResponsePage(ExmaraldaPartitur.class, paramsNew);

	        }

	    }).add(fwidth).add(new Button("fsubmit")));


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
		final Video video = new Video("media");
        video.setPoster(new PackageResourceReference(getClass(), "no-video.jpg"));
        video.setOutputMarkupId(true);
		List<Mediaresource> media_files = mediaService.listDocumentMediaMappings(pid, doc).stream().map(x -> x.getMedia()).collect(Collectors.toList());		
		if(media_files.size() > 0){
			Mediaresource mfile = media_files.get(0);
			Source source = new Source("mediasource", new MediaResourceReference(), new PageParameters().add(MediaResourceStreamResource.PAGE_PARAM_PROJECT_ID, pid).add(MediaResourceStreamResource.PAGE_PARAM_FILE_ID, mfile.getId()));
			if(!mfile.isProvidedAsURL())
				source.setType(mfile.getContentType());
			source.setDisplayType(true);
	        video.add(source);
		}else{
			video.add(new Source("mediasource"));
		}
        add(video);  
        
        final DropDownChoice<Mediaresource> mediaChoice = new DropDownChoice<>(
                "mediachoice",
//                new PropertyModel<Mediaresource>(new Serializable() {Mediaresource s = media_files.size() > 0 ? media_files.get(0) : null;}, "s")
                new Model<Mediaresource>(media_files.size() > 0 ? media_files.get(0) : null), 
                media_files,
                new ChoiceRenderer<Mediaresource>("name", "id"));
        mediaChoice.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = -7860861746085374959L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			    Mediaresource m = mediaChoice.getModelObject();
				if(m == null)
					return;
				Source newSource = new Source("mediasource", new MediaResourceReference(), new PageParameters().add(MediaResourceStreamResource.PAGE_PARAM_PROJECT_ID, pid).add(MediaResourceStreamResource.PAGE_PARAM_FILE_ID, m.getId()));
				if(!m.isProvidedAsURL())
					newSource.setType(m.getContentType());
				newSource.setDisplayType(true);
		        video.addOrReplace(newSource);
//		        ExmaraldaPartitur.this.addOrReplace(video);					
			}
		});
        add(mediaChoice);
		
		/* set up the collapse buttons */
		add(new ListView<String>("collapsebuttons", meta.spantypes.stream().collect(Collectors.toList())){
			private static final long serialVersionUID = 1L;
			protected void populateItem(ListItem<String> item) {
				CheckBox button = new CheckBox("collapsebutton", new Model<Boolean>(Boolean.valueOf(true)));
				button.add(new AjaxEventBehavior("change") {
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
		
		// Create Segments
		List<MySegment> segmente = new ArrayList<MySegment>();
		for(Timevalue timevalue : meta.timeline.subList(0, meta.timeline.size()-1)) {			
			// needed for Listref & Mediaref
			String id = timevalue.id;
			float interval = timevalue.interval;
			
			
			List<MySpeaker> speakers = new ArrayList<MySpeaker>();
			
			for(int i = 0; i < meta.speakers.size(); i++){
				Speaker speaker = meta.speakers.get(i);
				String speakertext = pindex.getSpeakertextForTimevalue(speaker, timevalue);
				String speakername = speaker.n;
				String speakerdescription = String.format("%s [v]", speakername);
				
				JCas speakerview = TeiMetadata.getSpeakerView(textview, speaker);
				List<MyAnnotation> annotations = JCasUtil.select(speakerview, TEIspan.class).stream()
					.filter(anno -> timevalue.id.equals(anno.getStartID()))
					.filter(anno -> !StringUtils.isEmpty(anno.getContent()))
					.map(anno -> {
						int annotationlength = meta.getTimevalueById(anno.getEndID()).i - timevalue.i ; // diff: end - starts 
						MyAnnotation ma = new MyAnnotation(anno.getContent(),  String.format("%s [%s]", speaker.n, anno.getSpanType()), anno.getSpanType(), annotationlength);								
						return ma;
					})
					.collect(Collectors.toList());
				
				List<String> nvList = new ArrayList<>();
				List<String> nnList = new ArrayList<>();
				List<MyAnnotation> incidents = JCasUtil.select(speakerview, Incident.class).stream()
						.filter(anno -> timevalue.id.equals(anno.getStartID()))
						.filter(anno -> !StringUtils.isEmpty(anno.getDesc()))
						.map(anno -> {
							int annotationlength = meta.getTimevalueById(anno.getEndID()).i - timevalue.i ; // diff: end - starts
							String annotationtyp = "";
							if(!Speaker.NARRATOR.equals(speaker)) {
								annotationtyp = nvList.size() == 0 ? "nv" : "nv"+(nvList.size()+1);
								nvList.add(annotationtyp);
							} else {
								annotationtyp = nnList.size() == 0 ? "nn" : "nn"+(nnList.size()+1);
								nnList.add(annotationtyp);
							}
							return new MyAnnotation(anno.getDesc(),  String.format("%s [%s]", speaker.n, annotationtyp), annotationtyp, annotationlength);
						})
						.collect(Collectors.toList());

				// LOG.debug(incidents.toString());
				
				List<MyAnnotation> all_annotations = new ArrayList<>(annotations);
				all_annotations.addAll(incidents);
				
				if(!StringUtils.isEmpty(speakertext) || incidents.size() > 0)
					speakers.add(new MySpeaker(speakername, speakertext, speakerdescription, i, all_annotations));
				
			}
			
			MySegment ms = new MySegment(id, interval, speakers);
			segmente.add(ms);
		}
		
		// Create Big Segments
		int lastSegment = 0;
		int currentLength = 0;
		int maxLength = width;

		List<MyBigSegment> bigSegments = new ArrayList<>();
		for(MySegment mySegment : segmente) {
			
			if(currentLength + mySegment.getLength() <= maxLength) {
				currentLength += mySegment.getLength();
			} else {
				int currentSegment = segmente.indexOf(mySegment);
				bigSegments.add(new MyBigSegment(new ArrayList<MySegment>(segmente.subList(lastSegment, currentSegment))));
				
				lastSegment = currentSegment;
				currentLength = mySegment.getLength();
			}
		}
		if(lastSegment != segmente.size()) {
			bigSegments.add(new MyBigSegment(new ArrayList<MySegment>(segmente.subList(lastSegment, segmente.size()))));
		}
				
		// eine Tabelle pro BigSegment
		ListView<MyBigSegment> bigSegmentsView = new ListView<MyBigSegment>("segments", bigSegments) {

			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(ListItem<MyBigSegment> bigSegmentItem) {
				
				MyBigSegment mbs = bigSegmentItem.getModelObject();
				
				List<String> descriptions = mbs.getDescriptions();
				// kopftabelle: eine tr mit einer td für jede Description im BigSegment
				ListView<String> kopfreiheView = new ListView<String>("kopfreihe", descriptions) {

					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(ListItem<String> descriptionItem) {
												
						String description = descriptionItem.getModelObject();
						String descriptiontyp = (description.split(" ")[1].replace("[", "").replace("]", "")).replaceAll("\\d","");
						
						Label textdescription;;
						
						if(descriptiontyp.equals("akz"))
							textdescription = new Label("textdesc", "");
						else
							textdescription = new Label("textdesc", description);
						
						if(descriptiontyp.equals("v"))
							textdescription.add(new AttributeModifier("class", "tlm"));
						else if(descriptiontyp.equals("akz"))
							textdescription.add(new AttributeModifier("class", "tlo akz2"));
						else
							textdescription.add(new AttributeModifier("class", "tlo"));
						descriptionItem.add(textdescription);
						descriptionItem.add(new AttributeAppender("class", new Model<>(descriptiontyp), " "));
						descriptionItem.add(new AttributeAppender("name", new Model<>(descriptiontyp), " "));
					}
					
				};
				bigSegmentItem.add(kopfreiheView);
				
				
				// erste Reihe: ein <td> für jedes Segment in BigSegment
				ListView<MySegment> segmentRefView = new ListView<MySegment>("refs", mbs.getSegments()) {

					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(ListItem<MySegment> mySegmentItem) {
												
						MySegment ms = mySegmentItem.getModelObject();
						
						// listref und mediaref setzen
						mySegmentItem.add(createListRef(ms.getId()));
						mySegmentItem.add(createMediaRef(ms.getId(), ms.getInterval()));
					}
					
				};
				bigSegmentItem.add(segmentRefView);
				
				// nächste Reihen: ein <tr> für jeden Annotationstyp in BigSegment 		
				ListView<String> textrowsView = new ListView<String>("textrows", descriptions) {

					private static final long serialVersionUID = 1L;
					
					@Override
					protected void populateItem(ListItem<String> descriptionItem) {
						
						String description = descriptionItem.getModelObject();
						String descriptiontyp = (description.split(" ")[1].replace("[", "").replace("]", "")).replaceAll("\\d","");
						
						// ein <td> für jedes Segment in BigSegment
						ListView<MySegment> segmentTextView = new ListView<MySegment>("text", mbs.getSegments()) {

							private static final long serialVersionUID = 1L;
							int colspan = 0;
							
							@Override
							protected void populateItem(ListItem<MySegment> mySegmentItem) {
								
								MySegment ms = mySegmentItem.getModelObject();
								MyAnnotation ma = ms.getAnnotationByDescription(description);	
																
								String labelContent = ms.getTextByDescription(description);
								if(labelContent != null) {
									Label content = new Label("textcontent", labelContent);
									mySegmentItem.add(content);
									mySegmentItem.add(new AttributeAppender("class", new Model<>(descriptiontyp), " "));
									
									if(ma != null)
										mySegmentItem.add(new AttributeAppender("colspan", new Model<>(ma.getLength()), " "));
								} else {
									Label content = new Label("textcontent", "");
									mySegmentItem.add(content);
									if(descriptiontyp.equals("akz"))
										mySegmentItem.add(new AttributeAppender("class", new Model<>("akz2"), " "));
								}
								
								mySegmentItem.setVisibilityAllowed(colspan == 0);
								
								if(ma != null && colspan == 0)
									colspan = ma.getLength();								
								
								if(colspan > 0)
									colspan--;
							}
							
						};
						
						descriptionItem.add(segmentTextView);
						descriptionItem.add(new AttributeAppender("class", new Model<>(descriptiontyp), " "));
						descriptionItem.add(new AttributeAppender("name", new Model<>(descriptiontyp), " "));
						
					}
					
				};
				
				bigSegmentItem.add(textrowsView);
			}
			
		};
		add(bigSegmentsView);

	}
	
	// TODO: replace by internal link
	private Label createListRef(String id) {
		Label listref = new Label("listref", id); 
		listref.add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onEvent(final AjaxRequestTarget target) {
				String url = "http://google.de";
				target.appendJavaScript(String.format("window.opener.location.href='%s'; window.blur(); window.opener.focus();", url));
			}
		});
		return listref;
	}
	
	private Image createMediaRef(String id, float interval) {
		Image mediaref = new Image("mediaref", "pbn.gif");
		mediaref.add(new AttributeModifier("title", String.format("%.3f - Click to start player", interval)));
		mediaref.add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onEvent(final AjaxRequestTarget target) {						
				target.appendJavaScript(String.format("vidjump('%f');", interval));
			}
		});
		mediaref.setOutputMarkupId(true);
		mediaref.setMarkupId(id);
		return mediaref;
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
