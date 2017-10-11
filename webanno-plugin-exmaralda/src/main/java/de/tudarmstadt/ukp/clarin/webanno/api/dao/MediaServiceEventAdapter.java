package de.tudarmstadt.ukp.clarin.webanno.api.dao;

import java.io.IOException;

import javax.annotation.Resource;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.tudarmstadt.ukp.clarin.webanno.api.MediaService;
import de.tudarmstadt.ukp.clarin.webanno.api.event.BeforeDocumentRemovedEvent;
import de.tudarmstadt.ukp.clarin.webanno.api.event.BeforeProjectRemovedEvent;
import de.tudarmstadt.ukp.clarin.webanno.model.DocumentToMediaMapping;
import de.tudarmstadt.ukp.clarin.webanno.model.Mediaresource;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;

@Component
public class MediaServiceEventAdapter
{
    
    private @Resource MediaService service;

    @EventListener
    public void beforeProjectRemove(BeforeProjectRemovedEvent aEvent)
            throws IOException
    {
        Project project = aEvent.getProject();
        for (DocumentToMediaMapping mapping : service.listDocumentMediaMappings(project)) {
            service.removeDocumentMediaMapping(mapping);
        }
        for (Mediaresource media : service.listMedia(project)) {
            service.removeMedia(media);
        }
    }

    @EventListener
    public void beforeDocumentRemove(BeforeDocumentRemovedEvent aEvent) throws Exception {
        SourceDocument document = aEvent.getDocument();
        for (DocumentToMediaMapping mapping : service.listDocumentMediaMappings(document.getProject().getId(), document)) {
            service.removeDocumentMediaMapping(mapping);
        }
    }

}
