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

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.stream.Stream;

abstract class TestArtifactStorage {
    
    public static final Path ROOT = Path.of("TestData/01_artifacts_groups/");

    public static Path createArtifact(Path group, String artifactName, String version) throws IOException {
        Path artifactDir = group.resolve(artifactName);
        Path versionDir = artifactDir.resolve(version);
        if (Files.notExists(versionDir))
            Files.createDirectories(versionDir);
        return versionDir;
    }

    public static Path createEmptySnapshot(String group, String artifactName, String version, String snapshot) throws IOException {
        Path versionDir = createArtifact(createGroup(group), artifactName, version);
        Path snapshotDir = versionDir.resolve(snapshot);
        if (Files.notExists(snapshotDir))
            Files.createDirectories(snapshotDir);
        return snapshotDir;
    }
    
    public static Path createSnapshot(String group, String artifactName, String version, String snapshot) throws IOException {
        Path snapshotFile = createEmptySnapshot(group, artifactName, version, snapshot).resolve("Dummy.txt");
        if (Files.notExists(snapshotFile)) {
            Files.writeString(snapshotFile, "SnapshotContent", StandardOpenOption.CREATE);            
        }
        return snapshotFile;
    }

    public static Path createGroup(String groupId) throws IOException {
        Path groupDirectory = ROOT.resolve(groupId);
        if (Files.notExists(groupDirectory))
            Files.createDirectories(groupDirectory);
        return groupDirectory;
    }

    public static void createArtifact(Path group, String artifactName) throws IOException {
        Path artifactDir = group.resolve(artifactName);
        if (Files.notExists(artifactDir))
            Files.createDirectories(artifactDir);
    }
    
    public static void removeAll() {
        try {
            delete(ROOT);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void delete(Path rootPath) throws IOException {
        if (Files.exists(rootPath)) {
            try (Stream<Path> walk = Files.walk(rootPath)) {
                walk.sorted(Comparator.reverseOrder())
                    .filter(Files::exists)
                    .map(Path::toFile)
                    .peek(System.out::println)
                    .forEach(File::delete);
            }
        }
    }
}
