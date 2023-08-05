package net.raumzeitfalle.docdrop.storage;

import java.nio.file.Path;
import java.util.logging.Level;

import io.quarkus.qute.TemplateInstance;
import net.raumzeitfalle.docdrop.Configuration;

public final class ArtifactIndexGenerator extends IndexGenerator {

    public ArtifactIndexGenerator(Path directory) {
        super(directory, directory.getFileName().toString(), true);
    }

    public String render(TemplateInstance templateInstance, Configuration config) {
        LOG.log(Level.INFO, "Rendering artifact index");
        return templateInstance.data("item", this)
                               .data("parent", this.parent)
                               .data("dirs", this.dirs)
                               .data("config", config)
                               .data("css_url", config.bootstrapCssUrl)
                               .render();
    }

}
