/*-
 * #%L
 * docbox
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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.inject.Inject;
import net.raumzeitfalle.docbox.storage.Artifact;
import net.raumzeitfalle.docbox.storage.ArtifactQueue;
import net.raumzeitfalle.docbox.storage.ArtifactUploadInput;

public abstract class UploadController {

    Logger LOG = Logger.getLogger(getClass().getName());
    
    @Inject
    Configuration configuration;
    
    @Inject
    ArtifactQueue artifactsQueue;
    
    public void process(ArtifactUploadInput input, String logmessage) {
        LocalDateTime timestamp = LocalDateTime.now();
        LOG.log(Level.INFO, "{0} [{1}][{2}][{3}][{4}]",
                new Object[] {logmessage, input.group, input.artifact, input.version, timestamp});
        for (FileUpload upload : input.files) {
            Artifact artifact = generateArtifact(input, timestamp, upload);
            artifactsQueue.push(artifact);
        }
    }
    
    public final Artifact generateArtifact(ArtifactUploadInput input, LocalDateTime timestamp, FileUpload upload) {
        try {
            var source = upload.filePath();
            var copy = Artifact.prepareSnapshot(configuration, upload);
            LOG.log(Level.INFO, "Ingesting file [{0}]: [{1}] -> [{2}]", new Object[] {upload.fileName(), source, copy});
            Files.copy(source, copy, StandardCopyOption.REPLACE_EXISTING);
            return Artifact.prepare(configuration, input, timestamp, upload, copy);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
