package net.raumzeitfalle.docdrop.storage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.raumzeitfalle.docdrop.Configuration;
import net.raumzeitfalle.docdrop.utils.SimpleUntarGz;
import net.raumzeitfalle.docdrop.utils.SimpleUnzip;

@Singleton
public class ArtifactStorage {
    
    Logger LOG = Logger.getLogger(ArtifactStorage.class.getName());
    
    @Inject
    Configuration configuration;
        
    public ArtifactStorage() {
        
    }

    @Blocking
    public void store(Artifact input) {
        try {
            var storage = prepareStorage(input);
            distribute(input,storage);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public java.nio.file.Path prepareStorage(Artifact input) throws IOException {
        LOG.log(Level.INFO, "Preparing directories for artifact [{0}] version [{1}] in group [{2}] (snapshot: {3}).",
                new Object[] {input.artifactName(), input.version(), input.groupId(), input.snapshot()});

        java.nio.file.Path targetDir = configuration.getArtifactsDirectory();
        var groupDir = targetDir.resolve(input.groupId());
        var artifactDir = groupDir.resolve(input.artifactName());
        var versionDir = artifactDir.resolve(input.version());
        var timedDir = versionDir.resolve(input.snapshot());
        Files.createDirectories(timedDir);
        return timedDir;
    }
    
    @Blocking
    public Optional<Path> distribute(Artifact artifact, java.nio.file.Path storage) throws IOException {
        LOG.log(Level.INFO, "Received atrifact for ingestion.");
        if (artifact.sourceFileName().toLowerCase().endsWith("-javadoc.jar")) {
            LOG.log(Level.INFO, "Javadoc artifact detected.");
            return distributeArtifact(artifact, storage, this::decompressZip);
        } else if (artifact.sourceFileName().toLowerCase().endsWith(".tar.gz")) {
            LOG.log(Level.INFO, "Compressed Tarball artifact detected.");
            return distributeArtifact(artifact, storage, this::decompressTarGz);
        } else if (artifact.sourceFileName().toLowerCase().endsWith(".tar")) {
            LOG.log(Level.INFO, "Tarball artifact detected.");
            return distributeArtifact(artifact, storage, this::unpackTar);
        } else if (artifact.sourceFileName().toLowerCase().endsWith(".zip")) {
            LOG.log(Level.INFO, "ZIP artifact detected.");
            return distributeArtifact(artifact, storage, this::decompressZip);
        }
        return Optional.empty();
    }

    private Optional<Path> distributeArtifact(Artifact artifact, java.nio.file.Path storage, BiConsumer<Path, Path> decompressor) throws IOException {
        Path source = artifact.file();
        Path target = storage.resolve(artifact.sourceFileName());
        decompressor.accept(source, target);
        
        LOG.log(Level.INFO, "Copying artifact into snapshot directory: " + target);
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

        LOG.log(Level.INFO, "Deleting ingested file: " + source);
        Files.deleteIfExists(source);
        return Optional.of(target);
    }

    private void decompressZip(Path source, Path target) {
        try {
            LOG.log(Level.INFO, "Extracting: [{0}] into [{1}]", new Object[] {source.getFileName(), target.getParent()});
            new SimpleUnzip(source, target.getParent()).exec();
            LOG.log(Level.INFO, "Extracted at least {0} files,", target.getParent().toFile().listFiles().length);
        } catch (IOException ioe) {
            LOG.log(Level.SEVERE, "File Handling Error!", ioe);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            LOG.log(Level.SEVERE, "Unzip Thread Interrupted!", ie);
        }
    }
    
    private void decompressTarGz(Path source, Path target) {
        try {
            LOG.log(Level.INFO, "Extracting: [{0}] into [{1}]", new Object[] {source.getFileName(), target.getParent()});
            new SimpleUntarGz(source, target.getParent()).exec();
            LOG.log(Level.INFO, "Extracted at least {0} files,", target.getParent().toFile().listFiles().length);
        } catch (IOException ioe) {
            LOG.log(Level.SEVERE, "File Handling Error!", ioe);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            LOG.log(Level.SEVERE, "Unzip Thread Interrupted!", ie);
        }
    }
    
    private void unpackTar(Path source, Path target) {
        try {
            LOG.log(Level.INFO, "Extracting: [{0}] into [{1}]", new Object[] {source.getFileName(), target.getParent()});
            new SimpleUntarGz(source, target.getParent()).execUntar();
            LOG.log(Level.INFO, "Extracted at least {0} files,", target.getParent().toFile().listFiles().length);
        } catch (IOException ioe) {
            LOG.log(Level.SEVERE, "File Handling Error!", ioe);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            LOG.log(Level.SEVERE, "Unzip Thread Interrupted!", ie);
        }
    }

    public List<String> collectGroups() {
        try (Stream<java.nio.file.Path> items = Files.list(configuration.getArtifactsDirectory())) {
            return items.filter(IndexGenerator::isValidDirectory)
                        .map(java.nio.file.Path::getFileName)
                        .map(java.nio.file.Path::toString)
                        .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

}
