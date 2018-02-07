package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.media.Source;
import org.apache.wicket.markup.html.media.video.Video;
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
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.AjaxCallback;
import de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.helper.AnnotationTrack;
import de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.helper.MyBigSegment;
import de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.helper.MySegment;
import de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.helper.VerbalTrack;
import de.uhh.lt.webanno.exmaralda.io.PartiturIndex;
import de.uhh.lt.webanno.exmaralda.io.HiatTeiMetadata;
import de.uhh.lt.webanno.exmaralda.io.HiatTeiMetadata.Speaker;
import de.uhh.lt.webanno.exmaralda.io.HiatTeiMetadata.Timevalue;
import de.uhh.lt.webanno.exmaralda.type.Anchor;
import de.uhh.lt.webanno.exmaralda.type.Incident;
import de.uhh.lt.webanno.exmaralda.type.TEIspan;
import de.uhh.lt.webanno.exmaralda.type.TEIspanAkz;
import de.uhh.lt.webanno.exmaralda.type.TEIspanEn;
import de.uhh.lt.webanno.exmaralda.type.TEIspanGeneric;
import de.uhh.lt.webanno.exmaralda.type.TEIspanK;
import de.uhh.lt.webanno.exmaralda.type.TEIspanSup;


@MountPath(value = "/partitur", alt = "/partitur/${" + ExmaraldaPartitur.PAGE_PARAM_PROJECT_ID + "}/${"+ ExmaraldaPartitur.PAGE_PARAM_DOCUMENT_ID + "}")
public class ExmaraldaPartitur extends WebPage {

	public static final String PAGE_PARAM_PROJECT_ID = "pId";
	public static final String PAGE_PARAM_DOCUMENT_ID = "dId";
	public static final String SESSION_PARAM_TABLE_WIDTH = "tablewidth";

	private static final Logger LOG = LoggerFactory.getLogger(ExmaraldaPartitur.class);

	private @SpringBean DocumentService documentService;
	
	private @SpringBean MediaService mediaService;
	
	private @SpringBean AnnotationSchemaService annotationService;
	
	private Map<String, SpeakerDetailsWindow> speaker_windows = new HashMap<>();
	
	private DocumentDetailsWindow document_window;
	
	private HiatTeiMetadata meta;
	private SourceDocument doc;
	
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
		
		/* get the data */
		try {
			doc = documentService.getSourceDocument(pid, did);	
		} catch (Exception e) {
			String message = String.format("Error while retrieving document %d for project %d.", did, pid);
			LOG.error(message, e);
			setErrorMessage(message);
			return;
		}

		final JCas textview;
		try {
		    textview = documentService.createOrReadInitialCas(doc);
		} catch (Exception e) {
			String message = String.format("Error while retrieving cas from source document %s.", doc.getName());
			LOG.error(message, e);
			setErrorMessage(message);
			return;
		}

		try {
			meta = HiatTeiMetadata.getFromCas(textview);
		} catch (Exception e) {
			String message = String.format("Error while retrieving TEI metadata from source document %s.", doc.getName());
			LOG.error(message, e);
			setErrorMessage(message);
			return;
		}
		
		setInfo(StringUtils.isEmpty(meta.description.title) ? "<empty title>" : meta.description.title);
	    
		/* set up the requirements for visualization */
        final PartiturIndex pindex = new PartiturIndex(meta, textview);
        LOG.info("Number of anchors: {}.", meta.timeline.size());
        
        /* set up modal window document */
        add(document_window = new DocumentDetailsWindow("documentdetailswindow", meta.description));
        
        /* set up modal windows for speakers */
        ListView<Speaker> speakerwindows = new ListView<Speaker>("speakerdetailswindowcontainer", meta.speakers) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void populateItem(ListItem<Speaker> item){
                SpeakerDetailsWindow speakerwindow = new SpeakerDetailsWindow("speakerdetailswindow", item.getModelObject());
                speaker_windows.put(item.getModelObject().n, speakerwindow);
                item.add(speakerwindow);
            }
        }; 
        add(speakerwindows);
        
		/* set up modal window for settings */		
		final SettingsWindow settingsWindow = new SettingsWindow("settingswindow", doc);
		settingsWindow.setOnChangeAction(new AjaxCallback() {
            private static final long serialVersionUID = 1L;
            @Override
            public void accept(AjaxRequestTarget t) throws Exception {
                PartiturPreferences pref = PartiturPreferences.load(doc);
                ExmaraldaPartitur.this.addOrReplace(createSegmentalListView(createBigSegments(pref.partiturtablewidth, textview, pindex)));
                ExmaraldaPartitur.this.addOrReplace(createVideo(pref));
                t.appendJavaScript("window.location.reload()");
            }
        });
		add(settingsWindow);

		add(new AjaxLink<Void>("showSettingswindow") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
			    settingsWindow.show(target);
			}
		});
		
		/* load preferences */
		final PartiturPreferences pref = PartiturPreferences.load(doc);
		
		/* set up the video */
        add(createVideo(pref)); 
		
		/* set up the collapse buttons */
		add(new ListView<String>("collapsebuttons", meta.spantypes.stream().collect(Collectors.toList())){
			private static final long serialVersionUID = 1L;
			protected void populateItem(ListItem<String> item) {
				CheckBox button = new CheckBox("collapsebutton", Model.of(Boolean.valueOf(true)));
				button.add(new AjaxEventBehavior("change") {
					private static final long serialVersionUID = 1L;
					@Override
					protected void onEvent(AjaxRequestTarget target) {
						button.setModelObject(!button.getModelObject());
						String js = String.format("showHideTier('%1$s', %2$s)", item.getModelObject(), button.getModelObject());
						target.appendJavaScript(js);
					}
				});
				item.add(button);
				item.add(new Label("collapsebuttondesc", item.getModelObject()));
			};
		});

		/* set up the partitur visualization */	
		add(createSegmentalListView(createBigSegments(pref.partiturtablewidth, textview, pindex)));

	}
	
	
    private Video createVideo(PartiturPreferences pref){
        final Video video = new Video("media");
        video.setPoster(new PackageResourceReference(getClass(), "no-video.jpg"));
        if(pref.mediachoice != null){
            Source source = new Source("mediasource", new MediaResourceReference(), new PageParameters().add(MediaResourceReference.PAGE_PARAM_PROJECT_ID, pref.mediachoice.getProject().getId()).add(MediaResourceReference.PAGE_PARAM_FILE_ID, pref.mediachoice.getId()));
            if(!pref.mediachoice.isProvidedAsURL())
                source.setType(pref.mediachoice.getContentType());
            source.setDisplayType(true);
            video.add(source);
        }else{
            List<Mediaresource> media_files = mediaService.listDocumentMediaMappings(doc.getProject().getId(), doc).stream().map(x -> x.getMedia()).collect(Collectors.toList());
            if(media_files.size() > 0){
                pref.mediachoice = media_files.get(0);
                return createVideo(pref);
            }
            else{
                video.add(new Source("mediasource"));
            }
        }
        return video;
    }
	
	private ListView<MyBigSegment> createSegmentalListView(List<MyBigSegment> list){
	    return  new ListView<MyBigSegment>("segments", list) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<MyBigSegment> bigSegmentItem) {
                
                MyBigSegment mbs = bigSegmentItem.getModelObject();
                
                List<String> descriptions = mbs.collectDescriptions();
                // kopftabelle: eine tr mit einer td für jede Description im BigSegment
                ListView<String> kopfreiheView = new ListView<String>("kopfreihe", descriptions) {

                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void populateItem(ListItem<String> descriptionItem) {
                                                
                        String description = descriptionItem.getModelObject();
                        String descriptiontyp = (description.split(" ")[1].replace("[", "").replace("]", "")).replaceAll("\\d","");
                        String speakername = description.split(" ")[0];
                        
                        Label textdescription = descriptiontyp.equals("akz") ? new Label("textdesc", "") : new Label("textdesc", description);
                        textdescription.add(new AjaxEventBehavior("click"){
                            private static final long serialVersionUID = 1L;
                            @Override
                            protected void onEvent(AjaxRequestTarget target){
                                SpeakerDetailsWindow window = speaker_windows.get(speakername);
                                if(window != null){
                                    speaker_windows.get(speakername).show(target);
                                    return;
                                }
                                LOG.warn("Speakerdetails window not found! {}", speakername);
                            }
                        });
                        
                        if(descriptiontyp.equals("v"))
                            textdescription.add(new AttributeModifier("class", "tlm"));
                        else if(descriptiontyp.equals("akz"))
                            textdescription.add(new AttributeModifier("class", "tlo akz2"));
                        else
                            textdescription.add(new AttributeModifier("class", "tlo"));
                        descriptionItem.add(textdescription);
                        descriptionItem.add(new AttributeAppender("class", Model.of(descriptiontyp), " "));
                        descriptionItem.add(new AttributeAppender("name", Model.of(descriptiontyp), " "));
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
                        mySegmentItem.add(createListRef(ms.getId(), ms.getSentenceNumberStart()));
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
                                AnnotationTrack ma = ms.getAnnotationByDescription(description);    
                                                                
                                String labelContent = ms.getTextByDescription(description);
                                if(labelContent != null) {
                                    Label content = new Label("textcontent", labelContent);
                                    mySegmentItem.add(content);
                                    mySegmentItem.add(new AttributeAppender("class", Model.of(descriptiontyp), " "));
                                    
                                    if(ma != null)
                                        mySegmentItem.add(new AttributeAppender("colspan", Model.of(ma.getLength()), " "));
                                } else {
                                    Label content = new Label("textcontent", "");
                                    mySegmentItem.add(content);
                                    if(descriptiontyp.equals("akz"))
                                        mySegmentItem.add(new AttributeAppender("class", Model.of("akz2"), " "));
                                }
                                
                                mySegmentItem.setVisibilityAllowed(colspan == 0);
                                
                                if(ma != null && colspan == 0)
                                    colspan = ma.getLength();                               
                                
                                if(colspan > 0)
                                    colspan--;
                            }
                        };
                        
                        descriptionItem.add(segmentTextView);
                        descriptionItem.add(new AttributeAppender("class", Model.of(descriptiontyp), " "));
                        descriptionItem.add(new AttributeAppender("name", Model.of(descriptiontyp), " "));
                    }
                };
                bigSegmentItem.add(textrowsView);
            }
            
        };
	}
	
	private Label createListRef(String id, int sentence) {
		Label listref = new Label("listref", id); 
		listref.add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onEvent(final AjaxRequestTarget target) {
				String url = String.format("annotation.html?#!p=%s&d=%s&f=%d", doc.getProject().getId(), doc.getId(), sentence);
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
	    add(new Label("info", message).add(new AjaxEventBehavior("click"){
	        private static final long serialVersionUID = 1L;
	        @Override
	        protected void onEvent(AjaxRequestTarget target){
	            document_window.show(target);
	        }
	    }));
	}
	
	private Stream<AnnotationTrack> createAnnotationTrack(JCas cas, Class<? extends Annotation> c, Speaker speaker, Timevalue t, AtomicInteger longestAnnotationLength) {
		Stream<AnnotationTrack> annotations = JCasUtil.select(cas, c).stream()
				.filter(anno -> t.id.equals(((TEIspan) anno).getStartID()))
				.filter(anno -> !StringUtils.isEmpty(((TEIspan) anno).getContent()))
				.map(anno -> {
					int annotationlength = meta.getTimevalueById(((TEIspan) anno).getEndID()).i - t.i ; // diff: end - start
					
					if(annotationlength > longestAnnotationLength.get())
						longestAnnotationLength.set(annotationlength);
					
					AnnotationTrack ma = new AnnotationTrack(speaker, ((TEIspan) anno).getContent(),  String.format("%s [%s]", speaker.n, ((TEIspan) anno).getSpanType()), ((TEIspan) anno).getSpanType(), annotationlength);								
					return ma;
				});
		return annotations;
	}
	
	private List<MyBigSegment> createBigSegments(int width, JCas textview, PartiturIndex pindex) {
	    width = width <= 0 ? Integer.MAX_VALUE : width;
		// Create Segments
		List<MySegment> segmente = new ArrayList<MySegment>();
		for(Timevalue timevalue : meta.timeline.subList(0, meta.timeline.size()-1)) {			
			// needed for Listref & Mediaref
			String id = timevalue.id;
			float interval = timevalue.interval;
			
			AtomicInteger longestAnnotationLength = new AtomicInteger(0);
			
			List<VerbalTrack> speakers = new ArrayList<VerbalTrack>();
			
			int sentence_number = -1; 
			for(int i = 0; i < meta.speakers.size(); i++){
				Speaker speaker = meta.speakers.get(i);
				String speakertext = pindex.getSpeakertextForTimevalue(speaker, timevalue);
				sentence_number = Math.max(sentence_number, pindex.getSentencenumberForTimevalue(speaker, timevalue));
				String speakerdescription = String.format("%s [v]", speaker.n);
				
				JCas speakerview = HiatTeiMetadata.getSpeakerView(textview, speaker);
				
				Stream<AnnotationTrack> annotations = createAnnotationTrack(speakerview, TEIspanGeneric.class, speaker, timevalue, longestAnnotationLength);
				annotations = Stream.concat(annotations,  createAnnotationTrack(speakerview, TEIspanEn.class, speaker, timevalue, longestAnnotationLength));
				annotations = Stream.concat(annotations, createAnnotationTrack(speakerview, TEIspanAkz.class, speaker, timevalue, longestAnnotationLength));
				annotations = Stream.concat(annotations, createAnnotationTrack(speakerview, TEIspanSup.class, speaker, timevalue, longestAnnotationLength));
				annotations = Stream.concat(annotations, createAnnotationTrack(speakerview, TEIspanK.class, speaker, timevalue, longestAnnotationLength));
				
				List<String> nvList = new ArrayList<>();
				List<String> nnList = new ArrayList<>();
				Stream<AnnotationTrack> incidents = JCasUtil.select(speakerview, Incident.class).stream()
						.filter(anno -> timevalue.id.equals(anno.getStartID())) // get all incidents that start here
						.filter(anno -> !StringUtils.isEmpty(anno.getDesc())) // ignore incidents that have no description (actually this shouldn't happen) 
						.filter(anno -> !anno.getIsTextual()) // only show non textual incidents as annotations
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
							return new AnnotationTrack(speaker, anno.getDesc(),  String.format("%s [%s]", speaker.n, annotationtyp), annotationtyp, annotationlength);
						});
				
				List<AnnotationTrack> all_annotations = Stream.concat(annotations, incidents).collect(Collectors.toList());

				
				if(!StringUtils.isEmpty(speakertext) || all_annotations.size() > 0)
					speakers.add(new VerbalTrack(speaker, speakertext, speakerdescription, i, all_annotations));
			}
			
			MySegment ms = new MySegment(id, interval, speakers, longestAnnotationLength.get(), sentence_number);
			segmente.add(ms);
		}
		
		// Create Big Segments
		int lastSegment = 0;
		int currentLength = 0;
		int maxLength = width;
		int neededSegments = 0;

		List<MyBigSegment> bigSegments = new ArrayList<>();
		for(MySegment mySegment : segmente) {
			
			if(neededSegments > 0) {
				neededSegments--;
				currentLength += mySegment.getLength();
			} else if(currentLength + mySegment.getLength() <= maxLength) {
				currentLength += mySegment.getLength();
			} else {
				int currentSegment = segmente.indexOf(mySegment);
				bigSegments.add(new MyBigSegment(new ArrayList<MySegment>(segmente.subList(lastSegment, currentSegment))));
				
				lastSegment = currentSegment;
				currentLength = mySegment.getLength();
			}
			
			if(mySegment.getLongestAnnotation() > neededSegments) {
				neededSegments = mySegment.getLongestAnnotation() - 1;
			}
		}
		if(lastSegment != segmente.size()) {
			bigSegments.add(new MyBigSegment(new ArrayList<MySegment>(segmente.subList(lastSegment, segmente.size()))));
		}
		
		return bigSegments;
	}
}
