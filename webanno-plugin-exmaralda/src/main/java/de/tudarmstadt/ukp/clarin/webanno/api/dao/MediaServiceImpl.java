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
package de.tudarmstadt.ukp.clarin.webanno.api.dao;

import static de.tudarmstadt.ukp.clarin.webanno.api.ProjectService.PROJECT_FOLDER;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copyLarge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.tudarmstadt.ukp.clarin.webanno.api.AnnotationSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.api.CasStorageService;
import de.tudarmstadt.ukp.clarin.webanno.api.ImportExportService;
import de.tudarmstadt.ukp.clarin.webanno.api.MediaService;
import de.tudarmstadt.ukp.clarin.webanno.model.DocumentToMediaMapping;
import de.tudarmstadt.ukp.clarin.webanno.model.Mediaresource;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.tudarmstadt.ukp.clarin.webanno.support.logging.Logging;

@Component(MediaService.SERVICE_NAME)
public class MediaServiceImpl implements MediaService, InitializingBean {
	
	public static final String MEDIA_FOLDER = "media";
	
    private static final Logger log = LoggerFactory.getLogger(MediaService.class);

    @PersistenceContext
    private EntityManager entityManager;
    
//    @Resource
//    private ApplicationEventPublisher applicationEventPublisher;

    @Resource(name = AnnotationSchemaService.SERVICE_NAME)
    private AnnotationSchemaService annotationService;
    
    @Resource(name = "userRepository")
    private UserDao userRepository;

    @Resource(name = CasStorageService.SERVICE_NAME)
    private CasStorageService casStorageService;

    @Resource(name = ImportExportService.SERVICE_NAME)
    private ImportExportService importExportService;

    @Value(value = "${repository.path}")
    private File dir;
    
    @Override
    public File getDir() {
        return dir;
    }

	@Override
	@Transactional
	public void createMediaresource(Mediaresource media) throws IOException {
		if (media.getId() == 0) {
            entityManager.persist(media);
        }
        else {
            entityManager.merge(media);
        }
	}

	@Override
	@Transactional
	public boolean existsMedia(Project project, String mediaName) {
        try {
            entityManager
                    .createQuery(
                            "FROM Mediaresource WHERE project = :project AND name =:name ",
                            Mediaresource.class).setParameter("project", project)
                    .setParameter("name", mediaName).getSingleResult();
            return true;
        }
        catch (NoResultException ex) {
            return false;
        }
	}

	@Override
	@Transactional(noRollbackFor = NoResultException.class)
	public Mediaresource getMedia(Project project, String mediaName) {
        return entityManager
                .createQuery("FROM Mediaresource WHERE name = :name AND project =:project",
                        Mediaresource.class).setParameter("name", mediaName)
                .setParameter("project", project).getSingleResult();
	}

	@Override
	@Transactional(noRollbackFor = NoResultException.class)
	public Mediaresource getMedia(long projectId, long mid) {
		return entityManager.createQuery("FROM Mediaresource WHERE id = :mid AND project.id =:pid", Mediaresource.class)
                .setParameter("mid", mid)
                .setParameter("pid", projectId).getSingleResult();
	}
	
	@Override
	@Transactional(noRollbackFor = NoResultException.class)
	public DocumentToMediaMapping getDocumentMediaMapping(long projectId, long mid, long source_document_id) {
		return entityManager.createQuery("FROM DocumentToMediaMapping WHERE media.id =:mid AND source_document.id =:did AND project.id =:pid", DocumentToMediaMapping.class)
                .setParameter("mid", mid)
                .setParameter("did", source_document_id)
                .setParameter("pid", projectId)
                .getSingleResult();
	}
	
	@Override
	@Transactional
	public boolean existsDocumentMediaMapping(long projectId, long fileId, long source_document_id) {
        try {
        	getDocumentMediaMapping(projectId, fileId, source_document_id);
            return true;
        }
        catch (NoResultException ex) {
            return false;
        }
	}
	
	@Override
	@Transactional(noRollbackFor = NoResultException.class)
	public List<DocumentToMediaMapping> listDocumentMediaMappings(long project_id, SourceDocument doc) {
		return entityManager
	        .createQuery("FROM DocumentToMediaMapping where project.id =:pid AND source_document.id =:did ORDER BY source_document.name ASC", DocumentToMediaMapping.class)
	        .setParameter("pid", project_id)
	        .setParameter("did", doc.getId())
	        .getResultList();
	}
	
	@Override
	@Transactional(noRollbackFor = NoResultException.class)
	public List<DocumentToMediaMapping> listDocumentMediaMappings(long project_id, Mediaresource media) {
		return entityManager
	        .createQuery("FROM DocumentToMediaMapping where project.id =:pid AND media.id =:mid ORDER BY source_document.name ASC", DocumentToMediaMapping.class)
	        .setParameter("pid", project_id)
	        .setParameter("mid", media.getId())
	        .getResultList();
	}
	
	@Override
	@Transactional(noRollbackFor = NoResultException.class)
	public List<DocumentToMediaMapping> listDocumentMediaMappings(Project p) {
		return entityManager
	        .createQuery("FROM DocumentToMediaMapping where project.id =:pid", DocumentToMediaMapping.class)
	        .setParameter("pid", p.getId())
	        .getResultList();
	}
	
	@Override
	@Transactional
	public void createDocumentMediaMapping(DocumentToMediaMapping mapping) {
		if (mapping.getId() == 0) {
            entityManager.persist(mapping);
        }
        else {
            entityManager.merge(mapping);
        }
	}
	
	@Override
	@Transactional
	public void removeDocumentMediaMapping(DocumentToMediaMapping mapping) {
        entityManager.remove(mapping);

        try (MDC.MDCCloseable closable = MDC.putCloseable(Logging.KEY_PROJECT_ID,
                String.valueOf(mapping.getProject().getId()))) {
            Project project = mapping.getProject();
            log.info("Removed Document-Media Mapping [{}-{}]({}) from project [{}]({})", mapping.getSource_document().getName(), mapping.getMedia().getName(),
            		mapping.getId(), project.getName(), project.getId());
        }
		
	}
	

	@Override
	public File getFile(Mediaresource media) throws IOException {
        return new File(getMediaFolder(media), media.getName());
	}
	
	@Override
	public InputStream getContentAsInputStream(Mediaresource media) throws IOException {
        return new FileInputStream(getFile(media));
	}
	
	@Override
	@Transactional(noRollbackFor = NoResultException.class)
	public List<Mediaresource> listMedia(Project aProject) {
		return entityManager
	        .createQuery("FROM Mediaresource where project =:project ORDER BY name ASC", Mediaresource.class)
	        .setParameter("project", aProject).getResultList();
	}

	@Override
	@Transactional
	public void removeMedia(Mediaresource media) throws IOException {
		
    	for (DocumentToMediaMapping mapping : listDocumentMediaMappings(media.getProject().getId(), media))
    		removeDocumentMediaMapping(mapping);
		
        entityManager.remove(media);

        File f =  getFile(media);
        if (f.exists()) 
            FileUtils.forceDelete(f);

        try (MDC.MDCCloseable closable = MDC.putCloseable(Logging.KEY_PROJECT_ID,
                String.valueOf(media.getProject().getId()))) {
            Project project = media.getProject();
            log.info("Removed media resource [{}]({}) from project [{}]({})", media.getName(),
            		media.getId(), project.getName(), project.getId());
        }
		
	}

	@Override
	@Transactional
	public void uploadMedia(File file, Mediaresource media) throws IOException {
        // Create the metadata record - this also assigns the ID to the document
        createMediaresource(media);

        // Copy the original file into the repository
        File targetFile = getFile(media);
        FileUtils.forceMkdir(targetFile.getParentFile());
        FileUtils.copyFile(file, targetFile);
        
        try (MDC.MDCCloseable closable = MDC.putCloseable(Logging.KEY_PROJECT_ID,
                String.valueOf(media.getProject().getId()))) {
            Project project = media.getProject();
            log.info("Imported media file [{}]({}) to project [{}]({})", 
            		media.getName(), media.getId(), project.getName(), project.getId());
        }
		
	}

    @Override
    @Transactional
	public void uploadMedia(InputStream in, Mediaresource media) throws IOException, UIMAException {
    	media.setTimestamp(new Date());
        // Create the metadata record - this also assigns the ID to the document
        createMediaresource(media);
        
        File targetFile = getFile(media);
        FileUtils.forceMkdir(targetFile.getParentFile());

        OutputStream out = null;
        try {
            out = new FileOutputStream(targetFile);
            copyLarge(in, out);
        }
        finally {
            closeQuietly(out);
            closeQuietly(in);
        }
        
        try (MDC.MDCCloseable closable = MDC.putCloseable(Logging.KEY_PROJECT_ID,
                String.valueOf(media.getProject().getId()))) {
            Project project = media.getProject();
            log.info("Imported media file [{}]({}) to project [{}]({})", 
            		media.getName(), media.getId(), project.getName(), project.getId());
        }
		
	}

	@Override
	public File getMediaFolder(Mediaresource media) throws IOException {
		  File mdir = new File(new File(new File(new File(dir, PROJECT_FOLDER), String.valueOf(media.getProject().getId())), MEDIA_FOLDER), String.valueOf(media.getId()));
	        FileUtils.forceMkdir(mdir);
	        return mdir;
	}
	
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Repository: " + dir);
    }

	
//	@Transactional
//	@EventListener
//	public void beforeProjectRemove(BeforeProjectRemovedEvent aEvent) throws Exception {
//	    Project project = aEvent.getProject();
//    	for (DocumentToMediaMapping mapping : listDocumentMediaMappings(project)) {
//    		removeDocumentMediaMapping(mapping);
//    	}
//		for (Mediaresource media : listMedia(project)) {
//        	removeMedia(media);
//        }
//	}

//	@Transactional
//	@EventListener
//	public void beforeDocumentRemove(BeforeDocumentRemovedEvent aEvent) throws Exception {
//	    SourceDocument document = aEvent.getDocument();
//    	for (DocumentToMediaMapping mapping : listDocumentMediaMappings(document.getProject().getId(), document)) {
//    		removeDocumentMediaMapping(mapping);
//    	}
//	}
	
	
    

}
