/*-
 * #%L
 * docbox
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import net.raumzeitfalle.docbox.Configuration;

public final class VersionIndexGenerator extends IndexGenerator {
    
    public final String groupId;

    public final String artifact;
    
    private SubDirectory latestVersion;

    public VersionIndexGenerator(Path directory, String groupId) {
        super(directory, directory.getFileName().toString(), true);
        this.groupId = groupId;
        this.artifact = name;
    }

    public int createIndex() {
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
            this.latestVersion = children.get(0);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Generation of VERSION INDEX failed!", e);
        }
        return this.size;
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

    public Optional<SubDirectory> getLatestVersion() {
        return Optional.of(this.latestVersion);
    }

    public void writeLatestVersionHtaccess(Template template, SubDirectory snapshot) {
        TemplateInstance instance = template.instance();
        if (this.latestVersion != null) {
            String redirect = latestVersion.name + "/" + snapshot.name;
            String htaccessText = instance.data("redirect", redirect).render();
            LOG.log(Level.INFO, "Generating .htaccess {0} for latest resource: \n{1}", new Object[] {this.directory, htaccessText});
            writeHtaccess(htaccessText);
        }
    }

}
