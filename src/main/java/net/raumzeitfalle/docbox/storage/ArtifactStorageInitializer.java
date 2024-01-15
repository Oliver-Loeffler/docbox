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
package net.raumzeitfalle.docbox.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.quarkus.qute.Template;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import net.raumzeitfalle.docbox.Configuration;

@Startup
/**
 * Creates static HTML files needed for the web server to redirect from empty
 * non-functional locations to the upload or status form.
 */
public class ArtifactStorageInitializer {

    private final Logger LOG = Logger.getLogger(getClass().getName());
    
    @Inject
    Configuration configuration;
    
    @Inject
    Template rootIndex;

    /**
     * Creates the {@code index.html} file inside the storage root directory.
     * This file will automatically forward to the upload form.
     */
    @PostConstruct
    public void createDataRootIndexFile() throws IOException {
        String uploadUrl = configuration.getUploadUrl();
        var storageRoot = configuration.getStorageRoot().toAbsolutePath();
        if (Files.notExists(storageRoot)) {
        	LOG.log(Level.INFO, "Creating storage root directory: {0}", storageRoot);
        	Files.createDirectories(configuration.getStorageRoot());
        }
        Path fileName = storageRoot.resolve("index.html");
        LOG.log(Level.INFO, "Writing repository index: {0}", fileName.toAbsolutePath());
        String html = rootIndex.instance().data("forwardurl", uploadUrl).render();
        Files.write(fileName, html.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
    }
    
    @PostConstruct
    public void createDataIngestDirectory() throws IOException {
        var ingestDir = configuration.getIngestDirectory().toAbsolutePath();
        if (Files.notExists(ingestDir)) {
        	LOG.log(Level.INFO, "Creating ingest directory: {0}", ingestDir);
        	Files.createDirectories(ingestDir);
        } else {
        	LOG.log(Level.INFO, "Detected ingest directory: {0}", ingestDir);
        }
    }
    
}
