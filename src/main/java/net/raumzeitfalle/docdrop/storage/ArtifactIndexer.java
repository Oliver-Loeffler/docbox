package net.raumzeitfalle.docdrop.storage;

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
import net.raumzeitfalle.docdrop.Configuration;

@Singleton
public class ArtifactIndexer {
    
    Logger LOG = Logger.getLogger(ArtifactIndexer.class.getName());
    
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
        java.nio.file.Path targetDir = configuration.getArtifactsDirectory();
        GroupIndexGenerator groupIndexer = new GroupIndexGenerator(targetDir, "Documentation Group IDs");
        groupIndexer.createIndexHtml(groupIndex, configuration);
    }
    
    public void createArtifactIndex() {
        java.nio.file.Path targetDir = configuration.getArtifactsDirectory();
        List<String> children = new SubDirectory(targetDir).collectChildren();
        LOG.log(Level.INFO, "Re-creating index for {0} groups.", children.size());
        for (String child : children) {
            Path groupDir = targetDir.resolve(child);
            ArtifactIndexGenerator artifactIndexer = new ArtifactIndexGenerator(groupDir);
            artifactIndexer.createIndexHtml(artifactIndex, configuration);
        }
    }

    public void createVersionIndex() {
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
                versionIndexer.createIndexHtml(versionIndex, configuration);   
            }
        }
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
        
        SnapshotIndexGenerator snapshotIndexer = new SnapshotIndexGenerator(versionDir, input.groupId(), input.artifactName());
        snapshotIndexer.createIndexHtml(snapshotIndex, configuration);
    }



}
