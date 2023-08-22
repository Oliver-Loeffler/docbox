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
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Stream;

import net.raumzeitfalle.docbox.Configuration;

public final class SevenZipTarGzCommand extends Command {

    private Path targetPath;

    private Path tarGzFile;

    private String tarGzName;

    private String program;

    public SevenZipTarGzCommand(Path file) {
        this(file.toAbsolutePath(), file.getParent().toAbsolutePath());
    }

    public SevenZipTarGzCommand(Path source, Path target) {
        LOG.info("Preparing 7Zip Gzipped-Tarball decompression");
        this.tarGzFile = source;
        this.tarGzName = source.getFileName().toString();
        this.program = "7za.exe";
        this.targetPath = target.toAbsolutePath();
        LOG.log(Level.INFO, "workdir: {0}", this.workingDirectory);
    }

    @Override
    public void configure(Configuration config) {
        if (config != null) {
            this.program = config.commandSevenZipLocation;
        }
    }

    public int exec() throws IOException, InterruptedException {
        Path source = prepareSource();
        int removeGzip = unpackGzipContainer(source);
        if (removeGzip != 0) {
            LOG.log(Level.WARNING, "Error during attempt to unpack GZip container.");
            tryRemovingTempDirectory(source.getParent());
            return removeGzip;
        }
        Path tarFile = findTarArchive(source);
        int unTar = unpackTarArchive(tarFile);
        tryRemovingTempDirectory(source.getParent());
        return unTar;
    }

    private int unpackTarArchive(Path tarFile) throws IOException, InterruptedException {
        LOG.info("Unpacking Tarball");
        this.clearCommandline();
        this.addItem(program);
        this.addItem("x");
        this.addItem("-aoa");
        this.addItem(tarFile.toAbsolutePath().toString());
        this.addItem("-o\"" + targetPath + "\"");
        this.setWorkingDirectory(tarFile.getParent());
        int unTar = super.exec();
        return unTar;
    }

    /**
     * Interestingly the 7Zip unpack may produce .tar files which are not named
     * according to their .tar.gz container. Given the assumption that the .tar.gz
     * container unfolds into only one .tar file, this function finds the first .tar
     * file and returns this one.
     * 
     * @param source The .tar.gz file which was unfolded in its temporary directory.
     * @return {@link Path} of the unpacked .tar archive
     * @throws IOException In any case of IO errors.
     */
    private Path findTarArchive(Path source) throws IOException {
        return findFirstTarFile(source.getParent()).orElseThrow(missingTarFile(source));
    }

    private Supplier<IOException> missingTarFile(Path source) {
        String message = "No .tar file found after attempt to unpack: [%s]".formatted(source.toAbsolutePath());
        return () -> new IOException(message);
    }

    /**
     * 7Zip is not able to unpack .tar.gz in one run. Hence herewith the Gzip
     * container is extracted.
     * 
     * @param source The .tar.gz file to be unpacked.
     * @return 7zip return code
     * @throws IOException          At any file system related error.
     * @throws InterruptedException In case the process is interrupted.
     */
    private int unpackGzipContainer(Path source) throws IOException, InterruptedException {
        LOG.info("Unpacking GZip container.");
        this.addItem(program);
        this.addItem("x");
        this.addItem("-aoa");
        this.addItem(source.toAbsolutePath().toString());
        this.setWorkingDirectory(source.getParent());
        int removeGzip = super.exec();
        return removeGzip;
    }

    /**
     * TarGz archives without an appropriate file name extension (.tar.gz) are not
     * extracted properly by 7zip. Hence a temporary file with the correct extension
     * is created. The working copy is stored in a temporary folder in the source
     * directory.
     * 
     * @return {@link Path} The .tar.gz file to be unpacked which is also named
     *         accordingly.
     * @throws IOException In case of any file system error.
     */
    private Path prepareSource() throws IOException {
        Path source = tarGzFile;
        if (!tarGzName.toLowerCase().endsWith(".tar.gz")) {
            Path temporary = source.getParent().resolve(UUID.randomUUID().toString());
            Files.createDirectories(temporary);
            Path copy = temporary.resolve(tarGzName + ".tar.gz");
            Files.copy(tarGzFile, copy);
            source = copy;
        }
        return source;
    }

    private void tryRemovingTempDirectory(Path parent) throws IOException {
        try (Stream<Path> files = Files.list(parent)) {
            files.forEach(this::delete);
        }
        this.delete(parent);
    }

    private Optional<Path> findFirstTarFile(Path parent) throws IOException {
        try (Stream<Path> files = Files.list(parent)) {
            return files.filter(this::isTar).findAny();
        }
    }

    private boolean isTar(Path file) {
        return file.getFileName().toString().toLowerCase().endsWith(".tar");

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
        return artifactName.toLowerCase().strip().endsWith(".tar.gz");
    }

    private void delete(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
