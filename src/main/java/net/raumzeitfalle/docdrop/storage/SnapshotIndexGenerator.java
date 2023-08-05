package net.raumzeitfalle.docdrop.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.quarkus.qute.TemplateInstance;
import net.raumzeitfalle.docdrop.Configuration;

public final class SnapshotIndexGenerator extends IndexGenerator {
    
    public final String groupId;

    public final String artifact;

    public SnapshotIndexGenerator(Path directory, String groupId, String artifact) {
        super(directory, directory.getFileName().toString(), true);
        this.groupId = groupId;
        this.artifact = artifact;
    }

    public void createIndex() {
        try (Stream<java.nio.file.Path> items = Files.list(directory)) {
            LOG.log(Level.INFO, "Analysing version directory [{0}]", directory);
            List<SubDirectory> children = items.filter(IndexGenerator::isValidDirectory)
                                               .map(SubDirectory::new)
                                               .map(SubDirectory::countFiles)
                                               .sorted(SubDirectory.byName().reversed())
                                               .collect(Collectors.toList());
            this.dirs = children;
            this.size = this.dirs.size();
            LOG.log(Level.INFO, "Found [{0}] snapshots for version [{1}]", new Object[] {this.size, this.name});
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Generation of SNAPSHOT INDEX failed!", e);
        }
    }

    public String render(TemplateInstance templateInstance, Configuration config) {
        LOG.log(Level.INFO, "Rendering snapshot index");
        return templateInstance.data("item", this)
                               .data("parent", this.parent)
                               .data("dirs", this.dirs)
                               .data("config", config)
                               .data("css_url", config.bootstrapCssUrl)
                               .render();
    }

}
