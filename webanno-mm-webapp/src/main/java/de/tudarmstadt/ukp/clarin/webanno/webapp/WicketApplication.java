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

import de.tudarmstadt.ukp.clarin.webanno.ui.core.WicketApplicationBase;
import de.tudarmstadt.ukp.clarin.webanno.ui.menu.MainMenuPage;
import de.tudarmstadt.ukp.clarin.webanno.ui.mm.MediaResourceReference;

/**
 * The Wicket application class. Sets up pages, authentication, theme, and other application-wide
 * configuration.
 */
@org.springframework.stereotype.Component("wicketApplication")
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
    protected void initWebFrameworks()
    {
        super.initWebFrameworks();

        initWebAnnoResources();

        initWebAnnoExmResources();
    }

    protected void initWebAnnoResources()
    {
        getComponentInstantiationListeners().add(component -> {
            if (component instanceof Page) {
                component.add(new WebAnnoResourcesBehavior());
            }
        });
    }

    protected void initWebAnnoExmResources() {
        MediaResourceReference mediaresources = new MediaResourceReference();
        // manually inject springbeans since autoinjection only works for subclasses of Component
        Injector.get().inject(mediaresources); 
        // mount the media resource
        mountResource(
                String.format("/media/${%s}/${%s}",
                        MediaResourceReference.PAGE_PARAM_PROJECT_ID,
                        MediaResourceReference.PAGE_PARAM_FILE_ID),
                mediaresources);

    }

}
