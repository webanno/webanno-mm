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

import static de.tudarmstadt.ukp.clarin.webanno.api.ProjectService.PROJECT;
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
import java.util.zip.ZipFile;

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
import de.tudarmstadt.ukp.clarin.webanno.api.MediafileService;
import de.tudarmstadt.ukp.clarin.webanno.api.ProjectLifecycleAware;
import de.tudarmstadt.ukp.clarin.webanno.model.Mediaresource;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocumentToMediafileMapping;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.tudarmstadt.ukp.clarin.webanno.support.logging.Logging;

@Component(MediafileService.SERVICE_NAME)
public class MediafileServiceImpl implements InitializingBean, MediafileService, ProjectLifecycleAware {
	
	public static final String MEDIA = "/media/";
	
    private static final Logger log = LoggerFactory.getLogger(MediafileService.class);

    @PersistenceContext
    private EntityManager entityManager;

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
	public void createMediafile(Mediaresource mfile) throws IOException {
		if (mfile.getId() == 0) {
            entityManager.persist(mfile);
        }
        else {
            entityManager.merge(mfile);
        }
	}

	@Override
	@Transactional
	public boolean existsMediafile(Project project, String fileName) {
        try {
            entityManager
                    .createQuery(
                            "FROM Mediaresource WHERE project = :project AND name =:name ",
                            Mediaresource.class).setParameter("project", project)
                    .setParameter("name", fileName).getSingleResult();
            return true;
        }
        catch (NoResultException ex) {
            return false;
        }
	}

	@Override
	@Transactional(noRollbackFor = NoResultException.class)
	public Mediaresource getMediafile(Project project, String fileName) {
        return entityManager
                .createQuery("FROM Mediaresource WHERE name = :name AND project =:project",
                        Mediaresource.class).setParameter("name", fileName)
                .setParameter("project", project).getSingleResult();
	}

	@Override
	@Transactional(noRollbackFor = NoResultException.class)
	public Mediaresource getMediafile(long projectId, long fileId) {
		return entityManager.createQuery("FROM Mediaresource WHERE id = :fid AND project.id =:pid", Mediaresource.class)
                .setParameter("fid", fileId)
                .setParameter("pid", projectId).getSingleResult();
	}
	
	@Override
	@Transactional(noRollbackFor = NoResultException.class)
	public SourceDocumentToMediafileMapping getMediafileMapping(long projectId, long fileId, long source_document_id) {
		return entityManager.createQuery("FROM SourceDocumentToMediafileMapping WHERE mediafile.id =:fid AND source_document.id =:did AND project.id =:pid", SourceDocumentToMediafileMapping.class)
                .setParameter("fid", fileId)
                .setParameter("did", source_document_id)
                .setParameter("pid", projectId)
                .getSingleResult();
	}
	
	@Override
	@Transactional
	public boolean existsMediafileMapping(long projectId, long fileId, long source_document_id) {
        try {
            getMediafileMapping(projectId, fileId, source_document_id);
            return true;
        }
        catch (NoResultException ex) {
            return false;
        }
	}
	
	@Override
	@Transactional(noRollbackFor = NoResultException.class)
	public List<SourceDocumentToMediafileMapping> listMediafileMappings(long project_id, long source_document_id) {
		return entityManager
	        .createQuery("FROM SourceDocumentToMediafileMapping where project.id =:pid AND source_document.id =:did ORDER BY source_document.name ASC", SourceDocumentToMediafileMapping.class)
	        .setParameter("pid", project_id)
	        .setParameter("did", source_document_id)
	        .getResultList();
	}
	
	@Override
	@Transactional(noRollbackFor = NoResultException.class)
	public List<SourceDocumentToMediafileMapping> listMediafileMappings(Project p) {
		return entityManager
	        .createQuery("FROM SourceDocumentToMediafileMapping where project.id =:pid", SourceDocumentToMediafileMapping.class)
	        .setParameter("pid", p.getId())
	        .getResultList();
	}

//	TODO: implement me
//	@Override
//	@Transactional(noRollbackFor = NoResultException.class)
//	public List<SourceDocumentToMediafileMapping> listMediafileMappings(long project_id, long media_file_id) {
//		return entityManager
//	        .createQuery("FROM SourceDocumentToMediafileMapping where project.id =:pid AND source_document.id =:did ORDER BY source_document.name ASC", SourceDocumentToMediafileMapping.class)
//	        .setParameter("pid", project_id)
//	        .setParameter("did", source_document_id)
//	        .getResultList();
//	}
	
	@Override
	@Transactional
	public void createMediafileMapping(SourceDocumentToMediafileMapping mapping) {
		if (mapping.getId() == 0) {
            entityManager.persist(mapping);
        }
        else {
            entityManager.merge(mapping);
        }
	}
	
	@Override
	@Transactional
	public void removeMediafileMapping(SourceDocumentToMediafileMapping mapping) {
        entityManager.remove(mapping);

        try (MDC.MDCCloseable closable = MDC.putCloseable(Logging.KEY_PROJECT_ID,
                String.valueOf(mapping.getProject().getId()))) {
            Project project = mapping.getProject();
            log.info("Removed SourceDocument-MediaFile Mapping [{}-{}]({}) from project [{}]({})", mapping.getSource_document().getName(), mapping.getMediafile().getName(),
            		mapping.getId(), project.getName(), project.getId());
        }
		
	}
	

	@Override
	public File getFile(Mediaresource mediafile) throws IOException {
        return new File(getMediafileFolder(mediafile), mediafile.getName());
	}
	
	@Override
	public InputStream getContentAsInputStream(Mediaresource mediafile) throws IOException {
        return new FileInputStream(getFile(mediafile));
	}
	
	@Override
	@Transactional(noRollbackFor = NoResultException.class)
	public List<Mediaresource> listMediafiles(Project aProject) {
		return entityManager
	        .createQuery("FROM Mediaresource where project =:project ORDER BY name ASC", Mediaresource.class)
	        .setParameter("project", aProject).getResultList();
	}

	@Override
	@Transactional
	public void removeMediafile(Mediaresource mediafile) throws IOException {
        entityManager.remove(mediafile);

        File f =  getFile(mediafile);
        if (f.exists()) 
            FileUtils.forceDelete(f);

        try (MDC.MDCCloseable closable = MDC.putCloseable(Logging.KEY_PROJECT_ID,
                String.valueOf(mediafile.getProject().getId()))) {
            Project project = mediafile.getProject();
            log.info("Removed media file [{}]({}) from project [{}]({})", mediafile.getName(),
            		mediafile.getId(), project.getName(), project.getId());
        }
		
	}

	@Override
	@Transactional
	public void uploadMediafile(File file, Mediaresource mfile) throws IOException {
        // Create the metadata record - this also assigns the ID to the document
        createMediafile(mfile);

        // Copy the original file into the repository
        File targetFile = getFile(mfile);
        FileUtils.forceMkdir(targetFile.getParentFile());
        FileUtils.copyFile(file, targetFile);
        
        try (MDC.MDCCloseable closable = MDC.putCloseable(Logging.KEY_PROJECT_ID,
                String.valueOf(mfile.getProject().getId()))) {
            Project project = mfile.getProject();
            log.info("Imported media file [{}]({}) to project [{}]({})", 
            		mfile.getName(), mfile.getId(), project.getName(), project.getId());
        }
		
	}

    @Override
    @Transactional
	public void uploadMediafile(InputStream in, Mediaresource mfile) throws IOException, UIMAException {
    	mfile.setTimestamp(new Date());
        // Create the metadata record - this also assigns the ID to the document
        createMediafile(mfile);
        
        File targetFile = getFile(mfile);
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
                String.valueOf(mfile.getProject().getId()))) {
            Project project = mfile.getProject();
            log.info("Imported media file [{}]({}) to project [{}]({})", 
                    mfile.getName(), mfile.getId(), project.getName(), project.getId());
        }
		
	}

	@Override
	public File getMediafileFolder(Mediaresource mfile) throws IOException {
		  File mdir = new File(dir, PROJECT + mfile.getProject().getId() + MEDIA
	                + mfile.getId());
	        FileUtils.forceMkdir(mdir);
	        return mdir;
	}
	
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Repository: " + dir);
    }

	@Override
	public void afterProjectCreate(Project aProject) throws Exception {
		// Nothing at the moment
		
	}

	@Override
	public void beforeProjectRemove(Project aProject) throws Exception {
    	for (SourceDocumentToMediafileMapping mapping : listMediafileMappings(aProject)) {
    		removeMediafileMapping(mapping);
    	}
		for (Mediaresource mfile : listMediafiles(aProject)) {
        	removeMediafile(mfile);
        }
	}

	@Override
	public void onProjectImport(ZipFile zip, de.tudarmstadt.ukp.clarin.webanno.export.model.Project aExportedProject,
			Project aProject) throws Exception {
		// Nothing at the moment
		
	}
    

}
