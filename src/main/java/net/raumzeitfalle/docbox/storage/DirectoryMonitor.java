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
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryMonitor implements Runnable {

    private static final Logger LOG = Logger.getLogger(DirectoryMonitor.class.getName());

    private volatile boolean running = false;

    private WatchService watchService;

    private volatile Path path;

    private Thread thread;

    private ThreadFactory threadFactory;

    private static final Path WORKING_DIR = Path.of("C:\\Temp\\WORK");
    
    private static final Path ARCHIVE_DIR = Path.of("C:\\Temp\\PROCESSED");
    
    @Override
    public void run() {
        if (watchService == null) {
            try {
                watchService = FileSystems.getDefault().newWatchService();
                path = WORKING_DIR;
                path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Failed to setup directory monitor!", e);
                return;
            }
        }

        tryWatch();

    }

    private void tryWatch() {
        try {
            watch();
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "Watch thread stopped...");
            Thread.currentThread().interrupt();
        }
    }

    private void watch() throws InterruptedException {
        while (running) {
            LOG.log(Level.INFO, "Waiting for events...");
            WatchKey watchKey = watchService.take();
            List<WatchEvent<?>> events = watchKey.pollEvents();
            for (var event : events) {
                if (event.context() instanceof Path file) {
                    Path fileToProcess = path.resolve(file);
                    if (Files.isDirectory(fileToProcess)) {
                        processFilesIn(fileToProcess);
                    } else {
                        processFile(fileToProcess);
                    }
                }
            }
            running = watchKey.reset();
        }
    }

    private void processFilesIn(Path fileToProcess) {
        try (Stream<Path> files = Files.list(fileToProcess)) {
            List<Path> items = files.collect(Collectors.toList());
            for (var item : items) {
                processFile(item);
            }
        } catch (IOException error) {
            LOG.log(Level.SEVERE, "Failed to process files in directory: %s".formatted(fileToProcess), error);
        }
    }

    private void processFile(Path fileToProcess) {
        if (Files.exists(fileToProcess)) {
            LOG.log(Level.INFO, "Reading file...");
            LOG.log(Level.INFO, readContent(fileToProcess));
        } else {
            /* ignore */
            LOG.log(Level.INFO, "Yup ... [{0}] already processed", fileToProcess);
        }
    }

    private void removeFile(Path resolve) {
        try {
            var archiveDir = ARCHIVE_DIR;
            LOG.log(Level.INFO, "Monitor Archive: " + archiveDir);
            Files.createDirectories(archiveDir);
            var source = resolve;
            LOG.log(Level.INFO, "Monitor Source: " + source);
            var timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
            var copy = archiveDir.resolve(timestamp + "_" + source.getFileName().toString());
            LOG.log(Level.INFO, "Monitor Copy: " + copy);
            Files.move(source, copy, StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(source);
            LOG.log(Level.INFO, "Monitor: " + "Removed file: " + resolve);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readContent(Path file) {
        if (Files.exists(file)) {
            try {
                if (file.getFileName().toString().toLowerCase().endsWith(".pef")) {
                    var contents = file.toAbsolutePath().toString();
                    removeFile(file);
                    return contents;
                } else {
                    var contents = Files.readString(file);
                    removeFile(file);
                    return contents;
                }
            } catch (AccessDeniedException e) {
                LOG.log(Level.INFO, "Monitor: File is beeing accessed, skipping... {0}", file);
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Failed to read file! %s".formatted(file), e);
            }
        }
        return "";
    }

    public synchronized void setThreadFactory(final ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public synchronized void start() throws Exception {
        if (running) {
            throw new IllegalStateException("Monitor is already running");
        }
//	        for (final FileAlterationObserver observer : observers) {
//	            observer.initialize();
//	        }
        running = true;
        if (threadFactory != null) {
            thread = threadFactory.newThread(this);
        } else {
            thread = new Thread(this);
        }
        thread.start();
    }

    public synchronized void stop(final long stopInterval) throws Exception {
        if (!running) {
            throw new IllegalStateException("Monitor is not running");
        }
        running = false;
        try {
            thread.interrupt();
            thread.join(stopInterval);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
//        for (final FileAlterationObserver observer : observers) {
//            observer.destroy();
//        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public Path getPath() {
        return this.path;
    }

}
