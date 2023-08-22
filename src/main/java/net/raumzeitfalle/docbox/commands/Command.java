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
package net.raumzeitfalle.docbox.commands;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.raumzeitfalle.docbox.Configuration;

abstract sealed class Command implements Executable 
                              permits UnzipCommand,
                                      TarCommand,
                                      TarGzCommand,
                                      SevenZipCommand,
                                      SevenZipTarGzCommand {

    protected Logger LOG = Logger.getLogger(getClass().getName());

    private final List<String> commandLine;

    protected Path workingDirectory;

    public Command() {
        this.commandLine = new ArrayList<>();
        this.workingDirectory = null;
    }

    protected void addItem(String commandLineElement) {
        this.commandLine.add(commandLineElement.strip());
    }

    protected void setItem(int index, String commandLineElement) {
        this.commandLine.set(index, commandLineElement.strip());
    }

    protected void clearCommandline() {
        this.commandLine.clear();
    }

    protected void setWorkingDirectory(Path directory) {
        this.workingDirectory = directory.toAbsolutePath();
    }

    @Override
    public void configure(Configuration config) {

    }

    @Override
    public int exec() throws IOException, InterruptedException {
        var processBuilder = new ProcessBuilder(commandLine);
        if (workingDirectory != null) {
            processBuilder.directory(workingDirectory.toFile());
        }

        LOG.log(Level.INFO, "External Call: [{0}]", processBuilder.command().stream().collect(Collectors.joining(" ")));

        return processBuilder.inheritIO().start().waitFor();
    }

    @Override
    public String toString() {
        return "Command [commandLine=" + commandLine + ", workingDirectory=" + workingDirectory + "]";
    }

}
