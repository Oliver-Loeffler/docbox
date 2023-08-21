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
package net.raumzeitfalle.docdrop;

import java.nio.file.Path;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.inject.Singleton;

@Singleton
public class Configuration {

    @ConfigProperty(name = "docdrop.host.url")
    public String hostUrl; 
    
    @ConfigProperty(name = "docdrop.views.upload.url", defaultValue = "/upload.html")
    public String uploadUrl;
    
    @ConfigProperty(name = "docdrop.endpoints.upload.url", defaultValue = "/upload")
    public String uploadEndpoint;

    @ConfigProperty(name = "docdrop.views.status.url", defaultValue = "/status.html")
    public String statusUrl;

    @ConfigProperty(name = "docdrop.views.artifacts.index.url", defaultValue = "/artifacts")
    public String artifactsIndexUrl;

    @ConfigProperty(name = "docdrop.artifact.storage.location", defaultValue = "data")
    public String artifactStorageRoot;

    @ConfigProperty(name = "docdrop.css.bootstrap.dist.url", defaultValue = "/dist/bootstrap-5.3.1/css/bootstrap.css")
    public String bootstrapCssUrl;

    @ConfigProperty(name = "docdrop.css.url", defaultValue = "/dist/docdrop.css")
    public String docdropCssUrl;
    
    @ConfigProperty(name = "docdrop.commands.7z.location", defaultValue = "C:\\Program Files\\7-Zip\\7z.exe")
    public String commandSevenZipLocation;

    @ConfigProperty(name = "docdrop.commands.tar.location", defaultValue = "/usr/bin/tar")
    public String commandTarLocation;

    @ConfigProperty(name = "docdrop.commands.unzip.location", defaultValue = "/usr/bin/unzip")
    public String commandUnzipLocation;

    @ConfigProperty(name = "docdrop.application.name", defaultValue = "DocDrop")
    public String applicationName;

    @ConfigProperty(name = "docdrop.repository.name", defaultValue = "DocDrop")
    public String repositoryName;

    @ConfigProperty(name = "docdrop.repository.index.file", defaultValue = "index.html")
    public String repositoryIndexFile;

    @ConfigProperty(name = "docdrop.scm.url", defaultValue = "http://gitbucket/docdrop")
    public String scmUrl;
    
    @ConfigProperty(name = "docdrop.css.forkmegit.url", defaultValue = "https://cdnjs.cloudflare.com/ajax/libs/github-fork-ribbon-css/0.2.3/gh-fork-ribbon.min.css")
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
        if (apacheHttpdPort != 80) {
        	return hostUrl+":"+apacheHttpdPort+uploadUrl;	
        }
        return hostUrl+uploadUrl;
    }
    
    public String getStatusUrl() {
        if (apacheHttpdPort != 80) {
        	return hostUrl+":"+apacheHttpdPort+statusUrl;	
        }
    	return hostUrl+statusUrl;
    }

    public String getArtifactsIndexUrl() {
        if (apacheHttpdPort != 80) {
        	return hostUrl+":"+apacheHttpdPort+artifactsIndexUrl;	
        }
        return hostUrl+artifactsIndexUrl;
    }
    
    public String getCssBootstrapDistUrl() {
        if (bootstrapCssUrl.toLowerCase().startsWith("http")) {
            return bootstrapCssUrl;
        }
        if (apacheHttpdPort != 80) {
        	return hostUrl+":"+apacheHttpdPort+bootstrapCssUrl;	
        }
        return hostUrl+bootstrapCssUrl;
    }
    
    public String getCssDocdropUrl() {
        if (docdropCssUrl.toLowerCase().startsWith("http")) {
            return docdropCssUrl;
        }
        if (apacheHttpdPort != 80) {
        	return hostUrl+":"+apacheHttpdPort+docdropCssUrl;	
        }
        return hostUrl+docdropCssUrl;
    }
    
    public String getForkRibbonUrl() {
        if (githubForkCssUrl.toLowerCase().startsWith("http")) {
            return githubForkCssUrl;
        }
        if (apacheHttpdPort != 80) {
        	return hostUrl+":"+apacheHttpdPort+githubForkCssUrl;	
        }
        return hostUrl+githubForkCssUrl;
    }
    
    public String getUploadEndpointUrl() {
        if (uploadEndpoint.toLowerCase().startsWith("http")) {
            return uploadEndpoint;
        }
        if (apacheHttpdPort != 80) {
        	return hostUrl+":"+apacheHttpdPort+uploadEndpoint;	
        }
        return hostUrl+uploadEndpoint;
    }

}
