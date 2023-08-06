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
import net.raumzeitfalle.docdrop.commands.Platform;

@Singleton
public class Configuration {
    @ConfigProperty(name = "docdrop.views.upload.url", defaultValue = "http://localhost:8080/upload.html")
    public String uploadUrl;

    @ConfigProperty(name = "docdrop.views.status.url", defaultValue = "http://localhost:8080/status.html")
    public String statusUrl;

    @ConfigProperty(name = "docdrop.views.artifacts.index.url", defaultValue = "http://localhost/artifacts")
    public String artifactsIndexUrl;

    @ConfigProperty(name = "docdrop.artifact.storage.location", defaultValue = "data")
    public String artifactStorageRoot;

    @ConfigProperty(name = "docdrop.css.bootstrap.dist.url")
    public String bootstrapCssUrl;

    @ConfigProperty(name = "docdrop.css.url")
    public String docdropCssUrl;

    @ConfigProperty(name = "docdrop.commands.7z.location", defaultValue = "C:\\Github\\loefflo\\docdrop\\Binaries\\Windows\\7z\\7za.exe")
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

    public Path getArtifactsDirectory() {
        return Path.of(artifactStorageRoot).resolve("artifacts");
    }

    public Path getIngestDirectory() {
        return Path.of(artifactStorageRoot).resolve("ingest");
    }

    public Platform platform = Platform.get();

}
