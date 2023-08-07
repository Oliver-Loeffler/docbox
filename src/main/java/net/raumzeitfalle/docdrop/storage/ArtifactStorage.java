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
package net.raumzeitfalle.docdrop.storage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.raumzeitfalle.docdrop.Configuration;
import net.raumzeitfalle.docdrop.commands.UnpackCommand;

@Singleton
public class ArtifactStorage {

    private static final Logger LOG = Logger.getLogger(ArtifactStorage.class.getName());

    @Inject
    Configuration configuration;

    private static final String META_FORWARD_TEMPLATE = """
            <!doctype html>
            <head>
                <meta charset="UTF-8">
                <meta http-equiv="refresh" content="0; {folder}/">
                <title>Forward into {folder}/</title>
            </head>
            <body>
            <a href="{folder}/">Forward into: {folder}</a>
            </body>
            </html>
            """;

    public ArtifactStorage() {

    }

    @Blocking
    public Optional<Path> store(Artifact input) {
        try {
            var storage = prepareStorage(input);
            return distribute(input, storage);
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
        LOG.log(Level.INFO, "Received artifact for ingestion.");
        Path source = artifact.file();
        Path target = storage.resolve(artifact.sourceFileName());

        Optional<UnpackCommand> unpackCommand = UnpackCommand.fromArtifact(artifact);
        if (unpackCommand.isEmpty()) {
            LOG.log(Level.INFO, "Dropping unsupported artifact type.");
            deleteIngestedArtifact(source);
            return Optional.empty();
        }

        unpackCommand.get()
                     .configure(configuration)
                     .accept(source, storage);
        
        generateMetaForwardForEmptyDirectory(target.getParent());
        
        moveArtifact(source, target);
        deleteIngestedArtifact(source);
        return Optional.of(target);
    }

    private void generateMetaForwardForEmptyDirectory(Path target) {
        try (Stream<Path> directory = Files.list(target)) {
            List<Path> files = directory.toList();
            if (files.size() == 1 && Files.isDirectory(files.get(0))) {
                LOG.log(Level.INFO, "Createing meta forward via index.html in [{0}]", target);
                String subDir = files.get(0).getFileName().toString();
                String forward = META_FORWARD_TEMPLATE.replace("{folder}", subDir);
                Files.write(target.resolve("index.html"), forward.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
            }
        } catch (IOException ioError) {
            LOG.log(Level.WARNING, "Failed to create meta forward via index.html in: [%s]".formatted(target), ioError);
        }
    }

    private void moveArtifact(Path source, Path target) throws IOException {
        LOG.log(Level.INFO, "Copying artifact into snapshot directory: " + target.getParent());
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    private void deleteIngestedArtifact(Path source) throws IOException {
        LOG.log(Level.INFO, "Deleting ingested file: " + source);
        Files.deleteIfExists(source);
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

    public void dropArtifacts() {
        Path artifactsRoot = configuration.getArtifactsDirectory();
        try (Stream<java.nio.file.Path> items = Files.list(artifactsRoot)) {
            items.forEach(this::delete);
            LOG.log(Level.INFO, "Dropped repository contents.");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Deletion of repository item failed.", e);
        }
    }

    public void dropIngestedArtifacts() {
        LOG.log(Level.INFO, "Deleting ingested files...");
        Path artifactsRoot = configuration.getIngestDirectory();
        try (Stream<java.nio.file.Path> items = Files.list(artifactsRoot)) {
            items.forEach(this::delete);
            LOG.log(Level.INFO, "Dropped ingested files.");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Deletion of ingested files failed.", e);
        }
    }

    private void delete(Path file) {
        try {
            if (Files.isDirectory(file)) {
                LOG.log(Level.INFO, "Deleting item: {0}", file.toAbsolutePath());
                deleteDirectory(file);
            } else {
                deleteFile(file);
            }
        } catch (IOException error) {
            throw new UncheckedIOException(error);
        }
    }

    private void deleteFile(Path file) throws IOException {
        try {
            Files.deleteIfExists(file);
        } catch (java.nio.file.NoSuchFileException noSuchFile) {
            /* Thats actually no problem if this happens by accident. */
        }
    }

    private void deleteDirectory(Path directory) throws IOException {
        try (Stream<java.nio.file.Path> items = Files.list(directory)) {
            List<Path> entries = items.toList();
            for (Path entry : entries) {
                delete(entry);
            }
            Files.delete(directory);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Deletion of directory failed.", e);
        }
    }

}
