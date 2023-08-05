package net.raumzeitfalle.docdrop;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.inject.Inject;
import net.raumzeitfalle.docdrop.storage.Artifact;
import net.raumzeitfalle.docdrop.storage.ArtifactQueue;
import net.raumzeitfalle.docdrop.storage.ArtifactUploadInput;

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
