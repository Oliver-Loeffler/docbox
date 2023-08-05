package net.raumzeitfalle.docdrop.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.quarkus.qute.TemplateInstance;
import net.raumzeitfalle.docdrop.Configuration;

public final class VersionIndexGenerator extends IndexGenerator {
    
    public final String groupId;

    public final String artifact;

    public VersionIndexGenerator(Path directory, String groupId) {
        super(directory, directory.getFileName().toString(), true);
        this.groupId = groupId;
        this.artifact = name;
    }

    public void createIndex() {
        LOG.log(Level.INFO, "Analysing artifact directory [{0}]", directory);
        try (Stream<java.nio.file.Path> items = Files.list(directory)) {
            List<SubDirectory> children = items.filter(IndexGenerator::isValidDirectory)
                                               .map(SubDirectory::new)
                                               .map(SubDirectory::countChildren)
                                               .map(SubDirectory::checkIfEffectivelyEmpty)
                                               .sorted(SubDirectory.byVersion().reversed())
                                               .collect(Collectors.toList());
            this.dirs = children;
            this.size = this.dirs.size();
            LOG.log(Level.INFO, "Found [{0}] versions for artifact [{1}]", new Object[] {this.size, this.name});
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Generation of VERSION INDEX failed!", e);
        }
    }

    public String render(TemplateInstance templateInstance, Configuration config) {
        LOG.log(Level.INFO, "Rendering version index");
        return templateInstance.data("item", this)
                               .data("parent", this.parent)
                               .data("dirs", this.dirs)
                               .data("config", config)
                               .data("css_url", config.bootstrapCssUrl)
                               .render();
    }

}
