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

import de.tudarmstadt.ukp.clarin.webanno.model.Mediaresource;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocumentToMediafileMapping;


public interface MediafileService
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
     * Creates a {@link SourceDocument} in a database. The source document is created by ROLE_ADMIN
     * or Project admins. Source documents are created per project and it should have a unique name
     * in the {@link Project} it belongs. renaming a a source document is not possible, rather the
     * administrator should delete and re create it.
     *
     * @param document
     *            {@link SourceDocument} to be created
     * @throws IOException
     *             if an I/O error occurs.
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER','ROLE_REMOTE')")
    void createMediafile(Mediaresource mfile)
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
    boolean existsMediafile(Project project, String fileName);

    /**
     * Get meta data information about {@link Mediaresource} from the database.
     *
     * @param project
     *            the {@link Project} where the {@link Mediaresource} belongs
     * @param fileName
     *            the name of the {@link Mediaresource}
     * @return the media file.
     */
    Mediaresource getMediafile(Project project, String fileName);

    /**
     * Get meta data information about {@link Mediaresource} from the database.
     * 
     * @param projectId
     *            the id for the {@link Project}
     * @param fileId
     *            the id for the {@link Mediaresource}
     * @return the media file
     */
    Mediaresource getMediafile(long projectId, long fileId);

    /**
     * Return the File
     *
     * @param mediafile
     *            The {@link Mediaresource} to be examined
     * @return the Directory path of the media file
     * @throws IOException 
     */
    File getFile(Mediaresource mediafile) throws IOException;
    
    /**
     * Return the content of the file stream 
     *
     * @param mediafile
     *            The {@link Mediaresource} to be examined
     * @return the content of the file stream 
     * @throws IOException 
     */
    InputStream getContentAsInputStream(Mediaresource mediafile) throws IOException;

    /**
     * List all mediafiles in a project.
     *
     * @param aProject
     *            The Project we are looking for files
     * @return list of media files
     */
    List<Mediaresource> listMediafiles(Project aProject);

    /**
     * ROLE_ADMINs or project admins can remove media files from a project.
     *
     * @param document
     *            the source document to be deleted
     * @throws IOException
     *             If the source document searched for deletion is not available
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER', 'ROLE_REMOTE')")
    void removeMediafile(Mediaresource mediafile)
        throws IOException;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER','ROLE_REMOTE')")
    void uploadMediafile(File file, Mediaresource mfile)
        throws IOException, UIMAException;

    /**
     * Upload a Mediafile, obtained as Inputstream, such as from remote API Zip folder to a
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
    void uploadMediafile(InputStream file, Mediaresource mfile)
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
    File getMediafileFolder(Mediaresource mfile)
        throws IOException;

    /**
     * TODO: fill me
     * @param projectId
     * @param fileId
     * @param source_document_id
     * @return
     */
    SourceDocumentToMediafileMapping getMediafileMapping(long projectId, long fileId, long source_document_id);
    
    /**
     * TODO: fill me
     * @param projectId
     * @param fileId
     * @param source_document_id
     * @return
     */
    boolean existsMediafileMapping(long projectId, long fileId, long source_document_id);
    
    /**
     * TODO: fill me
     * @param project_id
     * @param source_document_id
     * @return
     */
    List<SourceDocumentToMediafileMapping> listMediafileMappings(long project_id, long source_document_id);

    /**
     * TODO: fill me
     * @param p
     * @return
     */
    List<SourceDocumentToMediafileMapping> listMediafileMappings(Project p);
    
    /**
     * TODO: fill me
     * @param mapping
     */
    void createMediafileMapping(SourceDocumentToMediafileMapping mapping);
    
    /**
     * TODO: fill me 
     * @param mapping
     */
    void removeMediafileMapping(SourceDocumentToMediafileMapping mapping);
    
}
