package net.raumzeitfalle.docdrop.storage;

import java.nio.file.Path;
import java.util.logging.Level;

import io.quarkus.qute.TemplateInstance;
import net.raumzeitfalle.docdrop.Configuration;

public final class GroupIndexGenerator extends IndexGenerator {

    public GroupIndexGenerator(Path directory, String name) {
        super(directory, name, false);
    }

    public String render(TemplateInstance templateInstance, Configuration config) {
        LOG.log(Level.INFO, "Rendering group index");
        return templateInstance.data("item", this)
                               .data("dirs", this.dirs)
                               .data("config", config)
                               .data("css_url", config.bootstrapCssUrl)
                               .render();
    }

}
