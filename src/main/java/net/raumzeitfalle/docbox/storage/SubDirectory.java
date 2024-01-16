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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.vdurmont.semver4j.Semver;
import com.vdurmont.semver4j.Semver.SemverType;

public class SubDirectory implements Comparable<SubDirectory> {

    private static final Logger LOG = Logger.getLogger(SubDirectory.class.getName());

    public final String name;
    public long size = 0L;
    public long files = 0L;
    private Path root;
    public boolean effectivelyEmpty;

    SubDirectory(Path subDirectory) {
        this(subDirectory.getParent(), subDirectory.getFileName().toString());
    }

    SubDirectory(Path root, String name) {
        this.root = root;
        this.name = name.replace("\\", "/");
    }

    public long getSize() {
        return size;
    }

    public String getName() {
        return this.name;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public SubDirectory countChildren() {
        try (Stream<java.nio.file.Path> items = Files.list(root.resolve(name))) {
            this.size = items.filter(IndexGenerator::isValidDirectory).count();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "File Handling Error!", e);
        }
        return this;
    }

    public SubDirectory countFiles() {
        try (Stream<java.nio.file.Path> items = Files.list(root.resolve(name))) {
            this.files = items.filter(Files::isRegularFile).count();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "File Handling Error!", e);
        }
        return this;
    }

    public List<String> collectChildren() {
        try (Stream<java.nio.file.Path> items = Files.list(root.resolve(name))) {
            return items.filter(IndexGenerator::isValidDirectory)
                        .map(SubDirectory::new)
                        .map(SubDirectory::countChildren)
                        .filter(s -> s.size > 0)
                        .map(SubDirectory::getName)
                        .toList();
        } catch (IOException error) {
            LOG.log(Level.SEVERE, "Error trying to collect all non-empty sub-directories!", error);
        }
        return Collections.emptyList();
    }

    public SubDirectory checkIfEffectivelyEmpty() {
        try (Stream<java.nio.file.Path> items = Files.list(root.resolve(name))) {
            long notEmpty = items.filter(IndexGenerator::isValidDirectory)
                                 .map(SubDirectory::new)
                                 .map(SubDirectory::countFiles)
                                 .filter(s -> s.files > 0)
                                 .count();
            this.effectivelyEmpty = notEmpty == 0;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "File Handling Error!", e);
        }
        return this;
    }

    public static Comparator<SubDirectory> byName() {
        return (a, b) -> a.compareTo(b);
    }

    @Override
    public int compareTo(SubDirectory o) {
        return this.name.compareTo(o.name);
    }

    public static Comparator<SubDirectory> byVersion() {
        return (a, b) -> a.compareByVersion(b);
    }

    public int compareByVersion(SubDirectory other) {
        var thisVersion = byName(this);
        var otherVersion = byName(other);

        if (thisVersion != null && otherVersion != null) {
            return thisVersion.compareTo(otherVersion);
        }
        return this.compareTo(other);
    }

    private static Semver byName(SubDirectory subDir) {
        try {
            return new Semver(subDir.name);
        } catch (Exception versionNumberError) {
            /* lets try LOOSE */
        }

        try {
            return new Semver(subDir.name, SemverType.LOOSE);
        } catch (Exception versionNumberError) {
            /* lets try NPM */
        }

        try {
            return new Semver(subDir.name, SemverType.NPM);
        } catch (Exception versionNumberError) {
            return null;
        }
    }

}
