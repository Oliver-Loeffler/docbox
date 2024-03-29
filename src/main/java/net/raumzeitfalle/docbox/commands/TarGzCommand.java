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
package net.raumzeitfalle.docbox.commands;

import java.nio.file.Path;

import net.raumzeitfalle.docbox.Configuration;

public final class TarGzCommand extends Command {

    public TarGzCommand(Path file) {
        this(file.toAbsolutePath(),
             file.getParent().toAbsolutePath()); 
    }

    public TarGzCommand(Path source, Path target) {
        LOG.info("Preparing tar/gz deflate");
        this.addItem("/usr/bin/tar");
        this.addItem("-xvzf");
        this.addItem(source.toString());
        this.addItem("-C");
        this.addItem(target.toString());
        this.setWorkingDirectory(source.getParent());
    }

    @Override
    public void configure(Configuration config) {
        if (config != null) {
            String tarProgram = config.commandTarLocation;
            setItem(0, tarProgram);
        }
    }
    
    public static boolean testFileName(String artifactName) {
        return testFileName(artifactName, Platform.get());
    }

    public static boolean testFileName(String artifactName, Platform platform) {
        if (null == artifactName) {
            return false;
        }
        if (!Platform.LINUX.equals(platform)) {
            return false;
        }
        return artifactName.toLowerCase()
                           .strip()
                           .endsWith(".tar.gz");
    }

}
