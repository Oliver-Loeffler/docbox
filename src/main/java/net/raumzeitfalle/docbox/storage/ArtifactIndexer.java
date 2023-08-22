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
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.quarkus.qute.Template;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.raumzeitfalle.docbox.Configuration;

@Singleton
public class ArtifactIndexer {

    Logger LOG = Logger.getLogger(ArtifactIndexer.class.getName());

    @Inject
    ArtifactStatistics statistics;

    @Inject
    Template groupIndex;

    @Inject
    Template artifactIndex;

    @Inject
    Template versionIndex;

    @Inject
    Template htaccessIndex;

    @Inject
    Configuration configuration;

    @Inject
    Template snapshotIndex;

    @Blocking
    public void index(Artifact input) {
        try {
            createIndex(input);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void createGroupIndex() {
        long groupsIndexed = 0;
        java.nio.file.Path targetDir = configuration.getArtifactsDirectory();
        GroupIndexGenerator groupIndexer = new GroupIndexGenerator(targetDir, "Documentation Group IDs");
        groupsIndexed += groupIndexer.createIndexHtml(groupIndex, configuration);
        LOG.log(Level.INFO, "{0} groups indexed.", groupsIndexed);
        this.statistics.numberOfGroups = groupsIndexed;
    }

    public void createArtifactIndex() {
        long artifactsIndexed = 0;
        java.nio.file.Path targetDir = configuration.getArtifactsDirectory();
        List<String> children = new SubDirectory(targetDir).collectChildren();
        LOG.log(Level.INFO, "Re-creating index for {0} groups.", children.size());
        for (String child : children) {
            Path groupDir = targetDir.resolve(child);
            ArtifactIndexGenerator artifactIndexer = new ArtifactIndexGenerator(groupDir);
            artifactsIndexed += artifactIndexer.createIndexHtml(artifactIndex, configuration);
        }
        LOG.log(Level.INFO, "{0} artifacts indexed.", artifactsIndexed);
        this.statistics.numberOfArtifacts = artifactsIndexed;
    }

    public void createVersionIndex() {
        long versionsIndexed = 0;
        java.nio.file.Path targetDir = configuration.getArtifactsDirectory();
        List<String> groups = new SubDirectory(targetDir).collectChildren();
        LOG.log(Level.INFO, "Re-creating version index for artifacts in {0} groups.", groups.size());
        for (String group : groups) {
            Path groupDir = targetDir.resolve(group);
            List<String> artifacts = new SubDirectory(groupDir).collectChildren();

            LOG.log(Level.INFO, "Re-creating index for {0} artifacts in group {1} .",
                    new Object[] {artifacts.size(), group});

            for (String artifact : artifacts) {
                LOG.log(Level.INFO, "Re-creating index artifact {0}.", artifact);
                Path artifactDir = groupDir.resolve(artifact);
                VersionIndexGenerator versionIndexer = new VersionIndexGenerator(artifactDir, group);
                versionsIndexed += versionIndexer.createIndexHtml(versionIndex, configuration);
            }
        }
        LOG.log(Level.INFO, "{0} versions indexed.", versionsIndexed);
        this.statistics.numberOfVersions = versionsIndexed;
    }

    public void createSnapshotIndex() {
        long snapshotsIndexed = 0;
        java.nio.file.Path targetDir = configuration.getArtifactsDirectory();
        List<String> groups = new SubDirectory(targetDir).collectChildren();
        LOG.log(Level.INFO, "Re-creating snapshot index for artifacts in {0} groups.", groups.size());
        for (String group : groups) {
            Path groupDir = targetDir.resolve(group);
            List<String> artifacts = new SubDirectory(groupDir).collectChildren();
            LOG.log(Level.INFO, "Found {0} artifacts in group {1}.", new Object[] {artifacts.size(), group});
            for (String artifact : artifacts) {
                Path artifactDir = groupDir.resolve(artifact);
                List<String> versions = new SubDirectory(artifactDir).collectChildren();
                LOG.log(Level.INFO, "Found {0} versions for artifact {1}.", new Object[] {versions.size(), artifact});

                VersionIndexGenerator versionIndexer = new VersionIndexGenerator(artifactDir, group);
                versionIndexer.createIndexHtml(versionIndex, configuration);

                for (String version : versions) {
                    LOG.log(Level.INFO, "Re-creating snapshot index for version: {0}:{1}.",
                            new Object[] {artifact, version});
                    Path versionDir = artifactDir.resolve(version);
                    SnapshotIndexGenerator snapshotIndexer = new SnapshotIndexGenerator(versionDir, group, artifact);
                    snapshotsIndexed += snapshotIndexer.createIndexHtml(snapshotIndex, configuration);

                    snapshotIndexer.getLatestSnapshot().ifPresent(snapshot -> {
                        versionIndexer.writeLatestVersionHtaccess(htaccessIndex, snapshot);
                    });
                }
            }
        }
        LOG.log(Level.INFO, "{0} snapshot indexed", snapshotsIndexed);
        this.statistics.numberOfSnapshots = snapshotsIndexed;
    }

    public void createIndex(Artifact input) throws IOException {
        java.nio.file.Path targetDir = input.artifactsDirectory();
        var groupDir = targetDir.resolve(input.groupId());
        var artifactDir = groupDir.resolve(input.artifactName());
        var versionDir = artifactDir.resolve(input.version());

        GroupIndexGenerator groupIndexer = new GroupIndexGenerator(targetDir, "Documentation Group IDs");
        groupIndexer.createIndexHtml(groupIndex, configuration);

        ArtifactIndexGenerator artifactIndexer = new ArtifactIndexGenerator(groupDir);
        artifactIndexer.createIndexHtml(artifactIndex, configuration);

        VersionIndexGenerator versionIndexer = new VersionIndexGenerator(artifactDir, input.groupId());
        versionIndexer.createIndexHtml(versionIndex, configuration);

        SnapshotIndexGenerator snapshotIndexer = new SnapshotIndexGenerator(versionDir, input.groupId(),
                input.artifactName());
        snapshotIndexer.createIndexHtml(snapshotIndex, configuration);
        snapshotIndexer.getLatestSnapshot().ifPresent(snapshot -> {
            versionIndexer.writeLatestVersionHtaccess(htaccessIndex, snapshot);
        });
    }

}
