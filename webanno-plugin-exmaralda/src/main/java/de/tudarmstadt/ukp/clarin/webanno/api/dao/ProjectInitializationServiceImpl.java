package de.tudarmstadt.ukp.clarin.webanno.api.dao;


import static de.tudarmstadt.ukp.clarin.webanno.api.WebAnnoConst.SPAN_TYPE;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.uima.cas.CAS;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import de.tudarmstadt.ukp.clarin.webanno.api.AnnotationSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.api.ProjectInitializationService;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationFeature;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationLayer;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.uhh.lt.webanno.exmaralda.type.Incident;
import de.uhh.lt.webanno.exmaralda.type.PlayableAnchor;
import de.uhh.lt.webanno.exmaralda.type.PlayableSegmentAnchor;
import de.uhh.lt.webanno.exmaralda.type.TEIspanAkz;
import de.uhh.lt.webanno.exmaralda.type.TEIspanEn;
import de.uhh.lt.webanno.exmaralda.type.TEIspanGeneric;
import de.uhh.lt.webanno.exmaralda.type.TEIspanK;
import de.uhh.lt.webanno.exmaralda.type.TEIspanSup;

/**
 * 
 */
@Component(ProjectInitializationService.SERVICE_NAME)
public class ProjectInitializationServiceImpl implements InitializingBean, ProjectInitializationService {
    
    public static final String js = ""
            + "if(window.partitur && !window.partitur.closed){" + "\n"
            + "  if(window.partitur.location.hash == '#'+$PARAM.AnchorID){" + "\n"
            + "    window.partitur.onhashchange();" + "\n"
            + "  }else{" + "\n"
            + "    window.partitur.location.hash = $PARAM.AnchorID;" + "\n"
            + "  }" + "\n"
            + "}else{" + "\n"
            + "  window.partitur = window.open(`partitur/${$PARAM.PID}/${$PARAM.DOCID}#${$PARAM.AnchorID}`, 'partitur');" + "\n"
            + "}" + "\n"
            + "";
	
    @Resource(name = AnnotationSchemaService.SERVICE_NAME)
    private AnnotationSchemaService annotationService;
				
	@Override
	public void afterPropertiesSet() throws Exception { /* nothing to do */ }
	
	@Override
	public void init(Project aProject) throws IOException {
		createTranscriptLayer(aProject);
	}
	
	private void createTranscriptLayer(Project aProject)
            throws IOException
        {
    	/* BEGIN: add play buttons */
        AnnotationLayer playbuttonSegmentLayer = new AnnotationLayer(
        		PlayableSegmentAnchor.class.getName(),
                "Play Segment", 
                SPAN_TYPE, 
                aProject, 
                true);
        playbuttonSegmentLayer.setDescription("This Layer is used internally to create anchors for the partitur view.");
        playbuttonSegmentLayer.setAllowStacking(true);
        playbuttonSegmentLayer.setLockToTokenOffset(false);
        playbuttonSegmentLayer.setMultipleTokens(false);
        playbuttonSegmentLayer.setCrossSentence(false);
        playbuttonSegmentLayer.setReadonly(true);
        playbuttonSegmentLayer.setMultipleTokens(false);
//        playbuttonSegmentLayer.setZeroWidthOnly(true);
        playbuttonSegmentLayer.setOnClickJavascriptAction(js);
        annotationService.createLayer(playbuttonSegmentLayer);
        
        AnnotationFeature playbuttonSegmentFeature = new AnnotationFeature();
        playbuttonSegmentFeature.setLayer(playbuttonSegmentLayer);
        playbuttonSegmentFeature.setDescription("Anchor description.");
        playbuttonSegmentFeature.setName("Info");
        playbuttonSegmentFeature.setUiName("Info");
        playbuttonSegmentFeature.setType(CAS.TYPE_NAME_STRING);
        playbuttonSegmentFeature.setProject(aProject);
        playbuttonSegmentFeature.setVisible(true);
        annotationService.createFeature(playbuttonSegmentFeature);
        
        playbuttonSegmentFeature = new AnnotationFeature();
        playbuttonSegmentFeature.setDescription("Anchor ID.");
        playbuttonSegmentFeature.setName("AnchorID");
        playbuttonSegmentFeature.setUiName("AnchorID");
        playbuttonSegmentFeature.setType(CAS.TYPE_NAME_STRING);
        playbuttonSegmentFeature.setProject(aProject);
        playbuttonSegmentFeature.setLayer(playbuttonSegmentLayer);
        playbuttonSegmentFeature.setVisible(false);
        annotationService.createFeature(playbuttonSegmentFeature);
        
        AnnotationLayer playbuttonLayer = new AnnotationLayer(
        		PlayableAnchor.class.getName(),
                "Play", 
                SPAN_TYPE, 
                aProject, 
                true);
        playbuttonLayer.setDescription("This Layer is used internally to create anchors for the partitur view.");
        playbuttonLayer.setAllowStacking(true);
        playbuttonLayer.setLockToTokenOffset(false);
        playbuttonLayer.setMultipleTokens(false);
        playbuttonLayer.setCrossSentence(false);
        playbuttonLayer.setReadonly(true);
        playbuttonLayer.setMultipleTokens(false);
//        playbuttonLayer.setZeroWidthOnly(true);
        playbuttonLayer.setOnClickJavascriptAction(js);
        annotationService.createLayer(playbuttonLayer);

        AnnotationFeature playbuttonfeature = new AnnotationFeature();
        playbuttonfeature.setDescription("Anchor description.");
        playbuttonfeature.setName("Info");
        playbuttonfeature.setUiName("Info");
        playbuttonfeature.setType(CAS.TYPE_NAME_STRING);
        playbuttonfeature.setProject(aProject);
        playbuttonfeature.setLayer(playbuttonLayer);        
        playbuttonfeature.setVisible(true);
        annotationService.createFeature(playbuttonfeature);
        
        playbuttonfeature = new AnnotationFeature();
        playbuttonfeature.setDescription("Anchor ID.");
        playbuttonfeature.setName("AnchorID");
        playbuttonfeature.setUiName("AnchorID");
        playbuttonfeature.setType(CAS.TYPE_NAME_STRING);
        playbuttonfeature.setProject(aProject);
        playbuttonfeature.setLayer(playbuttonLayer);
        playbuttonfeature.setVisible(false);
        annotationService.createFeature(playbuttonfeature);
        /* END: add play buttons */
        
        /* BEGIN: add span annotations */
        addSpanAnnotationLayer(
                aProject, 
                TEIspanGeneric.class,
                "Generic TEI Span Annotation",
                "This Layer is used to create generic TEI span annotations which occur also in the partitur view.");
        addSpanAnnotationLayer(
                aProject, 
                TEIspanEn.class,
                "'en'",
                "This Layer is used to create 'en'-type TEI span annotations which occur also in the partitur view.");
        addSpanAnnotationLayer(
                aProject, 
                TEIspanAkz.class,
                "'akz'",
                "This Layer is used to create 'akz'-type TEI span annotations which occur also in the partitur view.");
        addSpanAnnotationLayer(
                aProject, 
                TEIspanK.class,
                "'k'",
                "This Layer is used to create 'k'-type TEI span annotations which occur also in the partitur view.");
        addSpanAnnotationLayer(
                aProject, 
                TEIspanSup.class,
                "'sup'",
                "This Layer is used to create 'sup'-type TEI span annotations which occur also in the partitur view.");
        /* END: add span annotations*/
        
        /* BEGIN: add incident annotations */
        AnnotationLayer incident_annotation_layer = new AnnotationLayer(
                Incident.class.getName(),
                "TEI Incident", 
                SPAN_TYPE, 
                aProject, 
                true);
        incident_annotation_layer.setDescription("This Layer is used to create TEI incident annotations which occur also in the partitur view.");
        incident_annotation_layer.setAllowStacking(true);
        incident_annotation_layer.setLockToTokenOffset(false);
        incident_annotation_layer.setMultipleTokens(false);
        incident_annotation_layer.setCrossSentence(true);
        incident_annotation_layer.setReadonly(true);
        incident_annotation_layer.setMultipleTokens(true);
        annotationService.createLayer(incident_annotation_layer);
        
        // content feature
        AnnotationFeature incident_annotation_feature = new AnnotationFeature();
        incident_annotation_feature.setDescription("Incident Description.");
        incident_annotation_feature.setName("Desc");
        incident_annotation_feature.setType(CAS.TYPE_NAME_STRING);
        incident_annotation_feature.setProject(aProject);
        incident_annotation_feature.setUiName("Description");
        incident_annotation_feature.setLayer(incident_annotation_layer);
        incident_annotation_feature.setVisible(false);
        annotationService.createFeature(incident_annotation_feature);
        
        // span type
        incident_annotation_feature = new AnnotationFeature();
        incident_annotation_feature.setDescription("Incident Type.");
        incident_annotation_feature.setName("IncidentType");
        incident_annotation_feature.setType(CAS.TYPE_NAME_STRING);
        incident_annotation_feature.setProject(aProject);
        incident_annotation_feature.setUiName("Type");
        incident_annotation_feature.setVisible(true);
        incident_annotation_feature.setLayer(incident_annotation_layer);
        annotationService.createFeature(incident_annotation_feature);
               
        /* END: add span annotations*/
            
    }
	
	private void addSpanAnnotationLayer(Project aProject, Class<?> spanClass, String uiName, String description) throws IOException {
	    
        AnnotationLayer span_annotation_layer = new AnnotationLayer(
                spanClass.getName(),
                uiName, 
                SPAN_TYPE, 
                aProject, 
                true);
        span_annotation_layer.setDescription(description);
        span_annotation_layer.setAllowStacking(true);
        span_annotation_layer.setLockToTokenOffset(false);
        span_annotation_layer.setMultipleTokens(false);
        span_annotation_layer.setCrossSentence(true);
        span_annotation_layer.setReadonly(true);
        span_annotation_layer.setMultipleTokens(true);
        annotationService.createLayer(span_annotation_layer);
        
        // content feature
        AnnotationFeature span_annotation_feature = new AnnotationFeature();
        span_annotation_feature.setDescription("Span Content.");
        span_annotation_feature.setName("Content");
        span_annotation_feature.setType(CAS.TYPE_NAME_STRING);
        span_annotation_feature.setProject(aProject);
        span_annotation_feature.setUiName("Content");
        span_annotation_feature.setLayer(span_annotation_layer);
        span_annotation_feature.setVisible(false);
        annotationService.createFeature(span_annotation_feature);
        
        // span type
        span_annotation_feature = new AnnotationFeature();
        span_annotation_feature.setDescription("Span Type.");
        span_annotation_feature.setName("SpanType");
        span_annotation_feature.setType(CAS.TYPE_NAME_STRING);
        span_annotation_feature.setProject(aProject);
        span_annotation_feature.setUiName("Type");
        span_annotation_feature.setLayer(span_annotation_layer);
        span_annotation_feature.setVisible(true);
        annotationService.createFeature(span_annotation_feature);
	    
	}
	
}
