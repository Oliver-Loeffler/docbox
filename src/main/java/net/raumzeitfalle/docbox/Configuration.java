/*-
 * #%L
 * docdrop
 * %%
 * Copyright (C) 2023 Oliver Loeffler, Raumzeitfalle.net
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package net.raumzeitfalle.docbox;

import java.nio.file.Path;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.inject.Singleton;

@Singleton
public class Configuration {

    @ConfigProperty(name = "docbox.host.url")
    public String hostUrl; 
    
    @ConfigProperty(name = "docbox.views.upload.url", defaultValue = "/upload.html")
    public String uploadUrl;
    
    @ConfigProperty(name = "docbox.endpoints.upload.url", defaultValue = "/upload")
    public String uploadEndpoint;

    @ConfigProperty(name = "docbox.views.status.url", defaultValue = "/status.html")
    public String statusUrl;

    @ConfigProperty(name = "docbox.views.artifacts.index.url", defaultValue = "/artifacts")
    public String artifactsIndexUrl;

    @ConfigProperty(name = "docbox.artifact.storage.location", defaultValue = "data")
    public String artifactStorageRoot;

    @ConfigProperty(name = "docbox.css.bootstrap.dist.url", defaultValue = "/dist/bootstrap-5.3.1/css/bootstrap.css")
    public String bootstrapCssUrl;

    @ConfigProperty(name = "docbox.css.url", defaultValue = "/dist/application.css")
    public String docdropCssUrl;
    
    @ConfigProperty(name = "docbox.commands.7z.location", defaultValue = "C:\\Program Files\\7-Zip\\7z.exe")
    public String commandSevenZipLocation;

    @ConfigProperty(name = "docbox.commands.tar.location", defaultValue = "/usr/bin/tar")
    public String commandTarLocation;

    @ConfigProperty(name = "docbox.commands.unzip.location", defaultValue = "/usr/bin/unzip")
    public String commandUnzipLocation;

    @ConfigProperty(name = "docbox.application.name", defaultValue = "DocDrop")
    public String applicationName;

    @ConfigProperty(name = "docbox.repository.name", defaultValue = "DocDrop")
    public String repositoryName;

    @ConfigProperty(name = "docbox.repository.index.file", defaultValue = "index.html")
    public String repositoryIndexFile;
    
    @ConfigProperty(name = "docbox.repository.actions.drop", defaultValue = "NO")
    public String repositoryActionsAllowDrop;

    @ConfigProperty(name = "docbox.scm.url", defaultValue = "http://gitbucket/docdrop")
    public String scmUrl;
    
    @ConfigProperty(name = "docbox.css.forkmegit.url", defaultValue = "https://cdnjs.cloudflare.com/ajax/libs/github-fork-ribbon-css/0.2.3/gh-fork-ribbon.min.css")
    public String githubForkCssUrl;
    
    @ConfigProperty(name = "apache.httpd.port")
    public int apacheHttpdPort;

    public Path getArtifactsDirectory() {
        return getStorageRoot().resolve("artifacts");
    }

    public Path getIngestDirectory() {
        return getStorageRoot().resolve("ingest");
    }
    
    public Path getStorageRoot() {
        return Path.of(artifactStorageRoot);
    }
    
    public String getUploadUrl() {
        return insertPortIfConfigured(hostUrl, uploadUrl);
    }
    
    public String getStatusUrl() {
    	return insertPortIfConfigured(hostUrl, statusUrl);
    }

    public String getArtifactsIndexUrl() {
        return insertPortIfConfigured(hostUrl, artifactsIndexUrl);
    }
    
    public String getCssBootstrapDistUrl() {
        return useAbsoluteUriWhenConfigured(bootstrapCssUrl);
    }
    
    public String getCssDocdropUrl() {
        return useAbsoluteUriWhenConfigured(docdropCssUrl);
    }
    
    public String getForkRibbonUrl() {
        return useAbsoluteUriWhenConfigured(githubForkCssUrl);
    }
    
    public String getUploadEndpointUrl() {
        return useAbsoluteUriWhenConfigured(uploadEndpoint);
    }

    /**
	 * When an end point is configured as an absolute URL starting with a protocol,
	 * than the fully qualified URL is not built from the configured host name.
	 * 
	 * @param the desired or configured end point or resource.
	 * @return {@link String} URL consisting of protocol, host, port and end point
	 *         or resource definition.
	 */
	private String useAbsoluteUriWhenConfigured(String endpoint) {
		if (endpoint.toLowerCase().startsWith("http")) {
            return endpoint;
        }
        return insertPortIfConfigured(hostUrl, endpoint);
	}

	/**
	 * The port is only added to the URL when configured different from port 80.
	 * 
	 * @param host     host name as root of URL, {@link String}
	 * @param endpoint end point or resource to be used, {@link String}
	 * @return {@link String} URL consisting of host name, port and end point. Port
	 *         only when different than port 80.
	 */
	private String insertPortIfConfigured(String host, String endpoint) {
		if (apacheHttpdPort != 80) {
        	return host+":"+apacheHttpdPort+endpoint;	
        }
        return host+endpoint;
	}
	
	/**
	 * Dropping (deleting) the full repository contents is only allowed when the
	 * corresponding environment variable is set.
	 * 
	 * @return boolean true when {@code DOCDROP_REPOSITORY_ACTIONS_DROP} is
	 *         configured to {@code YES}. When undefined or with another value,
	 *         deletion of repository is not permitted.
	 */
	public boolean allowRepositoryDrop() {
		return "YES".equalsIgnoreCase(repositoryActionsAllowDrop);
	}

}
