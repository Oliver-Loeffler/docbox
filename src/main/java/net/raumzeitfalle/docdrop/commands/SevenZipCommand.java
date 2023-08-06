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
package net.raumzeitfalle.docdrop.commands;

import java.nio.file.Path;

import net.raumzeitfalle.docdrop.Configuration;

public final class SevenZipCommand extends Command {
    public SevenZipCommand(Path file) {
        this(file.toAbsolutePath(), file.getParent().toAbsolutePath());
    }

    public SevenZipCommand(Path source, Path target) {
        LOG.info("Preparing 7Zip ZIP file decompression");
        this.addItem("7za.exe");
        this.addItem("x");
        this.addItem("-aoa");
        this.addItem(source.toAbsolutePath().toString());
        this.addItem("-o" + target.toAbsolutePath().toString());
        this.setWorkingDirectory(source.getParent());
    }

    @Override
    public void configure(Configuration config) {
        if (config != null) {
            String sevenZipProgram = config.commandSevenZipLocation;
            setItem(0, sevenZipProgram);
        }
    }

    public static boolean testFileName(String artifactName) {
        return testFileName(artifactName, Platform.get());
    }

    public static boolean testFileName(String artifactName, Platform platform) {
        if (null == artifactName) {
            return false;
        }
        if (!Platform.WINDOWS.equals(platform)) {
            return false;
        }
        return artifactName.toLowerCase().strip().endsWith(".7z") 
            || artifactName.toLowerCase().strip().endsWith(".tar")
            || artifactName.toLowerCase().strip().endsWith(".zip")
            || artifactName.toLowerCase().strip().endsWith("-javadoc.jar");
    }
}
