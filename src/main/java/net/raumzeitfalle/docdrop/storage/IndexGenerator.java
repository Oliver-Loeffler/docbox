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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import net.raumzeitfalle.docdrop.Configuration;

public abstract sealed class IndexGenerator permits GroupIndexGenerator,
                                                    ArtifactIndexGenerator,
                                                    VersionIndexGenerator,
                                                    SnapshotIndexGenerator {

    protected final Logger LOG = Logger.getLogger(getClass().getName());
    
    public final Path directory;

    public String dirString;

    public final String name;
    
    public final String parent;

    public List<SubDirectory> dirs;

    public int size;

    public boolean hasParent;

    public IndexGenerator(Path directory, String name, boolean hasParent) {
        this.directory = directory;
        this.dirString = directory.toString().replace("\\", "/");
        this.name = name;
        this.dirs = new ArrayList<>();
        this.hasParent = hasParent;
        this.parent = getParent(directory);
    }
    
    private String getParent(Path source) {
        Path parentPath = source.getParent();
        if (null != parentPath && hasParent) {
            return parentPath.getFileName()
                             .toString()
                             .replace("\\", "/");
        }
        return null;
    }

    public Path getDirectory() {
        return directory;
    }

    public String getName() {
        return name;
    }

    public int createIndex() {
        LOG.log(Level.FINE, "Analysing directory [{0}]", directory);
        try (Stream<java.nio.file.Path> items = Files.list(directory)) {
            List<SubDirectory> children = items.filter(IndexGenerator::isValidDirectory)
                                               .map(SubDirectory::new)
                                               .sorted(SubDirectory.byName())
                                               .map(SubDirectory::countChildren)
                                               .collect(Collectors.toList());
            this.dirs = children;
            this.size = this.dirs.size();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Index generation failed!", e);
        }
        return this.size;
    }
    
    public abstract String render(TemplateInstance templateInstance, Configuration config);
    
    public int createIndexHtml(Template template, Configuration config) {
        int indexed = createIndex();
        String html = render(template.instance(), config);
        writeIndex(html, config);
        return indexed;
    }

    public void writeIndex(String html, Configuration config) {
        var index = this.directory.resolve(config.repositoryIndexFile);
        LOG.log(Level.INFO, "Writing index file: [{0}]", index);
        try {
            Files.deleteIfExists(index);
            Files.write(index, html.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to write index file!", e);
        }
    }
    
    public void writeHtaccess(String htaccessText) {
        var index = this.directory.resolve(".htaccess");
        LOG.log(Level.INFO, "Writing .htaccess file: [{0}]", index);
        try {
            Files.deleteIfExists(index);
            Files.write(index, htaccessText.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to write .htaccess file!", e);
        }
    }
    
    public static boolean isValidDirectory(java.nio.file.Path directory) {
        if (!Files.isDirectory(directory)) {
            return false;
        }
        
        if (".".equals(directory.getFileName().toString())) {
            return false;
        }
        
        if ("..".equals(directory.getFileName().toString())) {
            return false;
        }
        
        return true;
    }
}
