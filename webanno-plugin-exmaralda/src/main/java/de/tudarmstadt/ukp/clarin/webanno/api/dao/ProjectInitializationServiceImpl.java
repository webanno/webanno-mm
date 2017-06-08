package de.tudarmstadt.ukp.clarin.webanno.api.dao;


import static de.tudarmstadt.ukp.clarin.webanno.api.WebAnnoConst.SPAN_TYPE;

import java.io.IOException;
import java.util.zip.ZipFile;

import javax.annotation.Resource;

import org.apache.uima.cas.CAS;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import de.tudarmstadt.ukp.clarin.webanno.api.AnnotationSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.api.ProjectInitializationService;
import de.tudarmstadt.ukp.clarin.webanno.api.ProjectLifecycleAware;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationFeature;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationLayer;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.uhh.lt.webanno.exmaralda.type.PlayableAnchor;
import de.uhh.lt.webanno.exmaralda.type.PlayableSegmentAnchor;
import de.uhh.lt.webanno.exmaralda.type.TEIspan;

/**
 * 
 */
@Component(ProjectInitializationService.SERVICE_NAME)
public class ProjectInitializationServiceImpl implements InitializingBean, ProjectLifecycleAware, ProjectInitializationService {
	
    @Resource(name = AnnotationSchemaService.SERVICE_NAME)
    private AnnotationSchemaService annotationService;
			
	@Override
	public void afterProjectCreate(Project aProject) throws Exception { init(aProject);	}

	@Override
	public void beforeProjectRemove(Project aProject) throws Exception { /* nothing to do */ }

	@Override
	public void onProjectImport(ZipFile zip, de.tudarmstadt.ukp.clarin.webanno.export.model.Project aExportedProject, Project aProject) throws Exception{ /* TODO: check if necessary: init(aProject); */ }
	
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
        playbuttonSegmentLayer.setReadonly(false);
        playbuttonSegmentLayer.setMultipleTokens(false);
//        playbuttonSegmentLayer.setZeroWidthOnly(true);
        playbuttonSegmentLayer.setOnClickJavascriptAction("window.open('partitur/${PID}/${DOCID}#${AnchorID}')");
        annotationService.createLayer(playbuttonSegmentLayer);
        
        AnnotationFeature playbuttonSegmentLayerInfoFeature = new AnnotationFeature();
        playbuttonSegmentLayerInfoFeature.setDescription("Anchor description.");
        playbuttonSegmentLayerInfoFeature.setName("Info");
        playbuttonSegmentLayerInfoFeature.setType(CAS.TYPE_NAME_STRING);
        playbuttonSegmentLayerInfoFeature.setProject(aProject);
        playbuttonSegmentLayerInfoFeature.setUiName("Info");
        playbuttonSegmentLayerInfoFeature.setLayer(playbuttonSegmentLayer);
        annotationService.createFeature(playbuttonSegmentLayerInfoFeature);
        
        AnnotationLayer playbuttonLayer = new AnnotationLayer(
        		PlayableAnchor.class.getName(),
                "Play", 
                SPAN_TYPE, 
                aProject, 
                true);
        playbuttonSegmentLayer.setDescription("This Layer is used internally to create anchors for the partitur view.");
        playbuttonLayer.setAllowStacking(true);
        playbuttonLayer.setLockToTokenOffset(false);
        playbuttonLayer.setMultipleTokens(false);
        playbuttonLayer.setCrossSentence(false);
        playbuttonLayer.setReadonly(true);
        playbuttonLayer.setMultipleTokens(false);
//        playbuttonLayer.setZeroWidthOnly(true);
        playbuttonLayer.setOnClickJavascriptAction("window.open('http://bing.com')");
        annotationService.createLayer(playbuttonLayer);

        AnnotationFeature playbuttonLayerInfoFeature = new AnnotationFeature();
        playbuttonLayerInfoFeature.setDescription("Anchor description.");
        playbuttonLayerInfoFeature.setName("Info");
        playbuttonLayerInfoFeature.setType(CAS.TYPE_NAME_STRING);
        playbuttonLayerInfoFeature.setProject(aProject);
        playbuttonLayerInfoFeature.setUiName("Info");
        playbuttonLayerInfoFeature.setLayer(playbuttonLayer);        
        annotationService.createFeature(playbuttonLayerInfoFeature);
        /* END: add play buttons */
        
        /* BEGIN: add span annotations */
        AnnotationLayer span_annotation_layer = new AnnotationLayer(
        		TEIspan.class.getName(),
                "TEI Span Annotation", 
                SPAN_TYPE, 
                aProject, 
                true);
        span_annotation_layer.setDescription("This Layer is used to create TEI span annotations which occur also in the partitur view.");
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
        annotationService.createFeature(span_annotation_feature);
               
        /* END: add span annotations*/
            
    }





	
	
	
}
