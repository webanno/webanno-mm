/*
 * Copyright 2017
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
package de.tudarmstadt.ukp.clarin.webanno.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.uima.UIMAException;
import org.springframework.security.access.prepost.PreAuthorize;

import de.tudarmstadt.ukp.clarin.webanno.model.DocumentToMediaMapping;
import de.tudarmstadt.ukp.clarin.webanno.model.Mediaresource;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;


public interface MediaService
{
	
	static final String SERVICE_NAME = "mediaService";
	
    /**
     * The Directory where the files are stored
     *
     * @return the directory.
     */
    File getDir();

    // --------------------------------------------------------------------------------------------
    // Methods related to Mediaresources
    // --------------------------------------------------------------------------------------------

	/**
	 * 
	 * @param media
	 * @throws IOException
	 */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER','ROLE_REMOTE')")
    void createMediaresource(Mediaresource media)
        throws IOException;

    /**
     * Check if a media file with this same name exist in the project. The caller method then
     * can decide to override or throw an exception/message to the client
     *
     * @param project
     *            the project.
     * @param fileName
     *            the file name.
     * @return if the file exists.
     */
    boolean existsMedia(Project project, String fileName);

    /**
     * Get meta data information about {@link Mediaresource} from the database.
     *
     * @param project
     *            the {@link Project} where the {@link Mediaresource} belongs
     * @param name
     *            the name of the {@link Mediaresource}
     * @return the media file.
     */
    Mediaresource getMedia(Project project, String mname);

    /**
     * Get meta data information about {@link Mediaresource} from the database.
     * 
     * @param projectId
     *            the id for the {@link Project}
     * @param mid
     *            the id for the {@link Mediaresource}
     * @return the media file
     */
    Mediaresource getMedia(long projectId, long mid);

    /**
     * Return the File
     *
     * @param media
     *            The {@link Mediaresource} to be examined
     * @return the Directory path of the media file
     * @throws IOException 
     */
    File getFile(Mediaresource media) throws IOException;
    
    /**
     * Return the content of the file stream 
     *
     * @param media
     *            The {@link Mediaresource} to be examined
     * @return the content of the file stream 
     * @throws IOException 
     */
    InputStream getContentAsInputStream(Mediaresource media) throws IOException;

    /**
     * List all media in a project.
     *
     * @param aProject
     *            The Project we are looking for files
     * @return list of media files
     */
    List<Mediaresource> listMedia(Project aProject);

    /**
     * ROLE_ADMINs or project admins can remove media files from a project.
     *
     * @param document
     *            the source document to be deleted
     * @throws IOException
     *             If the source document searched for deletion is not available
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER', 'ROLE_REMOTE')")
    void removeMedia(Mediaresource media)
        throws IOException;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER','ROLE_REMOTE')")
    void uploadMedia(File file, Mediaresource media)
        throws IOException, UIMAException;

    /**
     * Upload a media, obtained as Inputstream, such as from remote API Zip folder to a
     * repository directory. This way we don't need to create the file to a temporary folder
     *
     * @param file
     *            the file.
     * @param mfile
     *            the media file.
     * @throws IOException
     *             if an I/O error occurs.
     * @throws UIMAException
     *             if a conversion error occurs.
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER','ROLE_REMOTE')")
    void uploadMedia(InputStream file, Mediaresource media)
        throws IOException, UIMAException;

    /**
     * Get the directory of this {@link Mediaresource} usually to read the content of the file
     *
     * @param mfile
     *            the media file
     * @return the media file folder.
     * @throws IOException
     *             if an I/O error occurs.
     */
    File getMediaFolder(Mediaresource media)
        throws IOException;

    /**
     * TODO: fill me
     * @param projectId
     * @param fileId
     * @param source_document_id
     * @return
     */
    DocumentToMediaMapping getDocumentMediaMapping(long projectId, long fileId, long source_document_id);
    
    /**
     * TODO: fill me
     * @param projectId
     * @param fileId
     * @param source_document_id
     * @return
     */
    boolean existsDocumentMediaMapping(long projectId, long fileId, long source_document_id);
    
    /**
     * TODO: fill me
     * @param project_id
     * @param doc
     * @return
     */
    List<DocumentToMediaMapping> listDocumentMediaMappings(long project_id, SourceDocument doc);
    
    /**
     * TODO: fill me
     * @param project_id
     * @param media
     * @return
     */
    List<DocumentToMediaMapping> listDocumentMediaMappings(long project_id, Mediaresource media);

    /**
     * TODO: fill me
     * @param p
     * @return
     */
    List<DocumentToMediaMapping> listDocumentMediaMappings(Project p);
    
    /**
     * TODO: fill me
     * @param mapping
     */
    void createDocumentMediaMapping(DocumentToMediaMapping mapping);
    
    /**
     * TODO: fill me 
     * @param mapping
     */
    void removeDocumentMediaMapping(DocumentToMediaMapping mapping);
    
}
