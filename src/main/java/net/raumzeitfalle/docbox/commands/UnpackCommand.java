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
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.raumzeitfalle.docbox.Configuration;
import net.raumzeitfalle.docbox.storage.Artifact;

public enum UnpackCommand implements BiConsumer<Path, Path> {

    TAR(TarCommand::new, TarCommand::testFileName),
    TAR_GZ(TarGzCommand::new, TarGzCommand::testFileName),
    ZIP(UnzipCommand::new, UnzipCommand::testFileName),
    SEVEN_ZIP(SevenZipCommand::new, SevenZipCommand::testFileName),
    SEVEN_ZIP_TARGZ(SevenZipTarGzCommand::new, SevenZipTarGzCommand::testFileName);

    private static final Logger LOG = Logger.getLogger(UnpackCommand.class.getName());
    private final BiFunction<Path, Path, Executable> decompressor;
    private final Predicate<String> artifactTest;

    UnpackCommand(BiFunction<Path, Path, Executable> decompressor, Predicate<String> artifactTest) {
        this.decompressor = decompressor;
        this.artifactTest = artifactTest;
    }

    private Configuration config = null;
    
    public UnpackCommand configure(Configuration configuration) {
        this.config = configuration;
        return this;
    }
    
    @Override
    public void accept(Path sourceFile, Path targetDirectory) {
        LOG.log(Level.INFO, "Extracting: [{0}] into [{1}]",
                new Object[] {sourceFile.toAbsolutePath(), targetDirectory.toAbsolutePath()});

        
        Executable executable = this.decompressor.apply(sourceFile, targetDirectory);
        CommandRunner runner = new CommandRunner(executable, config);
        runner.run();

        LOG.log(Level.INFO, "Extracted at least {0} files,",
                targetDirectory.toFile().listFiles().length);
    }
    
    public static Optional<UnpackCommand> fromArtifact(Artifact artifact) {
        String fileName = artifact.sourceFileName();
        for (UnpackCommand cmd : values()) {
            if (cmd.artifactTest.test(fileName)) {
                LOG.log(Level.INFO, "Selecting {0} unpack command for {1}", new Object[] {cmd, fileName});
                return Optional.of(cmd);
            }
        }
        LOG.log(Level.WARNING, "No command defined to unpack artifact: {0}", fileName);
        return Optional.empty();
    }

}
