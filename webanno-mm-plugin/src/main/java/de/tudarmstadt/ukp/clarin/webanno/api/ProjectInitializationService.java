package de.tudarmstadt.ukp.clarin.webanno.api;


import java.io.IOException;

import de.tudarmstadt.ukp.clarin.webanno.model.Project;

/**
 * 
 */
public interface ProjectInitializationService {
	
	static final String SERVICE_NAME = "exmaraldaProjectInitializationService";

	void init(Project aProject) throws IOException;
		
}
