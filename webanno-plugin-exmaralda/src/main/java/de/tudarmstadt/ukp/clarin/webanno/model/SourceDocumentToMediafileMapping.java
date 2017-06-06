/*
 * Copyright 2017
 * Ubiquitous Knowledge Processing (UKP) Lab and AB Language Technology
 * Technische Universität Darmstadt, Universität Hamburg
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
package de.tudarmstadt.ukp.clarin.webanno.model;

import java.io.Serializable;
import java.util.Comparator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;

/**
 * A persistence object for meta-data of media documents. The file itself is
 * stored in the file system.
 *
 *
 */
@Entity
@Table(name = "source_document_to_mediafile", uniqueConstraints = { @UniqueConstraint(columnNames = { "project", "source_document", "mediafile" }) })
public class SourceDocumentToMediafileMapping implements Serializable {

	private static final long serialVersionUID = -1010330727538071627L;
	
	@Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name = "project")
    Project project;
    
    @ManyToOne
    @JoinColumn(name = "source_document")
    SourceDocument source_document;
    
	@ManyToOne
    @JoinColumn(name = "mediafile")
    Mediaresource mediafile;
    
    public long getId()
    {
        return id;
    }

    public void setId(long aId)
    {
        id = aId;
    }

    public Project getProject()
    {
        return project;
    }

    public void setProject(Project aProject)
    {
        project = aProject;
    }
    
    public SourceDocument getSource_document() {
		return source_document;
	}

	public void setSource_document(SourceDocument source_document) {
		this.source_document = source_document;
	}

	public Mediaresource getMediafile() {
		return mediafile;
	}

	public void setMediafile(Mediaresource mediafile) {
		this.mediafile = mediafile;
	}

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SourceDocumentToMediafileMapping other = (SourceDocumentToMediafileMapping) obj;
        return id != other.id;
    }


	public static final Comparator<SourceDocumentToMediafileMapping> NAME_COMPARATOR = new Comparator<SourceDocumentToMediafileMapping>() {
        @Override
        public int compare(SourceDocumentToMediafileMapping aO1, SourceDocumentToMediafileMapping aO2)
        {
            return Long.compare(aO1.id, aO2.id);
        }
    };
}
