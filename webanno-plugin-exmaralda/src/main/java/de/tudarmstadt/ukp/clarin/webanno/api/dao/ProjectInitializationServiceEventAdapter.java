package de.tudarmstadt.ukp.clarin.webanno.api.dao;

import javax.annotation.Resource;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.tudarmstadt.ukp.clarin.webanno.api.ProjectInitializationService;
import de.tudarmstadt.ukp.clarin.webanno.api.event.AfterProjectCreatedEvent;

@Component
public class ProjectInitializationServiceEventAdapter
{
    
    private @Resource ProjectInitializationService service;
    
    @EventListener
    public void afterProjectCreate(AfterProjectCreatedEvent aEvent) throws Exception { 
        service.init(aEvent.getProject());  
    }

}
