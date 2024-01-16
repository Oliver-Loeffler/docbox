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

import io.quarkus.qute.TemplateInstance;
import net.raumzeitfalle.docbox.Configuration;

public final class SnapshotIndexGenerator extends IndexGenerator {
    
    public final String groupId;

    public final String artifact;
    
    private SubDirectory latestSnapshot;

    public SnapshotIndexGenerator(Path directory, String groupId, String artifact) {
        super(directory, directory.getFileName().toString(), true);
        this.groupId = groupId;
        this.artifact = artifact;
    }

    public int createIndex() {
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
            this.latestSnapshot = children.get(0);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Generation of SNAPSHOT INDEX failed!", e);
        }
        return this.size;
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
    
    public Optional<SubDirectory> getLatestSnapshot() {
        return Optional.of(this.latestSnapshot);
    }

}
