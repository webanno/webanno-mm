/*
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab and FG Language Technology
 * Technische Universität Darmstadt
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
package de.tudarmstadt.ukp.clarin.webanno.webapp;

import org.apache.wicket.Page;
import org.apache.wicket.injection.Injector;
import org.springframework.stereotype.Component;

import de.tudarmstadt.ukp.clarin.webanno.api.dao.ProjectInitializationServiceImpl;
import de.tudarmstadt.ukp.clarin.webanno.ui.core.WicketApplicationBase;
import de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.MediaResourceStreamResource;
import de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda.MediafileResourceReference;
import de.tudarmstadt.ukp.clarin.webanno.ui.menu.MainMenuPage;

/**
 * The Wicket application class. Sets up pages, authentication, theme, and other application-wide
 * configuration.
 */
@Component("wicketApplication")
public class WicketApplication
    extends WicketApplicationBase
{
    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<? extends Page> getHomePage()
    {
        return MainMenuPage.class;
    }
    
    @Override
    protected void init() {
    	super.init();
    	MediafileResourceReference mediaresources = new MediafileResourceReference();
        Injector.get().inject(mediaresources); // manually inject springbeans since autoinjection only works for subclasses of Component
//        InitializeProject p = new InitializeProject();
        
        mountResource(
        		String.format("/media/${%s}/${%s}", 
        				MediaResourceStreamResource.PAGE_PARAM_PROJECT_ID, 
        				MediaResourceStreamResource.PAGE_PARAM_FILE_ID), 
        		mediaresources);
        
    	
    }
}