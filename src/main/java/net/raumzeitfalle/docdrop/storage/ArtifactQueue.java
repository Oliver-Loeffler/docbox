package net.raumzeitfalle.docdrop.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ArtifactQueue {
    
    private static final Logger LOG = Logger.getLogger(ArtifactQueue.class.getName());

    private Deque<Artifact> storeQueue = new ConcurrentLinkedDeque<>();
    
    private Deque<Artifact> indexQueue = new ConcurrentLinkedDeque<>();

    @Inject
    ArtifactIndexer indexer;
    
    @Inject
    ArtifactStorageWorker worker;
    
    @Inject
    ArtifactStorage storage;
    
    public Deque<Artifact> getReceivedArtifacts() {
        return storeQueue;
    }
    
    public Deque<Artifact> getArtifacts2Index() {
        return storeQueue;
    }

    public void push(Artifact artifact) {
        this.storeQueue.push(artifact);
    }
    
    public void index(Artifact artifact) {
        this.indexQueue.push(artifact);
    }

    public void store() {
        LOG.log(Level.INFO, "Artifacts in store queue: {0}", storeQueue.size());
        Artifact artifact = storeQueue.pop();
        indexQueue.push(artifact);
        worker.runInThread(()->{
            storage.store(artifact);
            index();
        });
    }
    
    public void multiStore() {
        int processed = 0;
        while (storeQueue.size() > 0) {
            if (processed > 10) {
                return;
            }
            store();
            processed++;
        }
    }

    public void index() {
        LOG.log(Level.INFO, "Artifacts in index queue: {0}", indexQueue.size());
        Artifact artifact = indexQueue.pollFirst();
        if (null != artifact) {            
            worker.runBlocking(()->indexer.index(artifact));
        }
    }

    public void emptyIngestDirectory() {
        LOG.log(Level.INFO, "Cleaning ingest directory.");
        Path ingest = storage.configuration.getIngestDirectory();
        if (null != ingest) {
            worker.runInThread(()->this.deleteFilesIn(ingest));
        }
    }
    
    private void deleteFilesIn(Path dir) {
        try (Stream<Path> files = Files.list(dir)) {
            List<Path> candidates = files.filter(isFile())
                                         .toList();
            int count = 0;
            for (Path file : candidates) {
                try {
                    Files.deleteIfExists(file);
                    count++;
                } catch (IOException deleteError) {
                    LOG.log(Level.WARNING,
                            "Could not delete ingested file: %s".formatted(file.toAbsolutePath()),
                            deleteError);
                }
            }
            LOG.log(Level.INFO, "Deleted {0} of {1} ingested files.", new Object[] {count, candidates.size()});
        } catch (IOException ioe) {
            LOG.log(Level.SEVERE, "Error during INGEST directory cleanup.", ioe);
        }
    }
    
    private Predicate<Path> isFile() {
        return path -> !Files.isDirectory(path);
    }

    public void recreateGroupIndex() {
        LOG.log(Level.INFO, "Re-creating group index.");
        worker.runBlocking(()->indexer.createGroupIndex());
    }

    public void recreateArtifactIndex() {
        LOG.log(Level.INFO, "Re-creating artifact index.");
        worker.runBlocking(()->indexer.createArtifactIndex());
    }

    public void recreateVersionIndex() {
        LOG.log(Level.INFO, "Re-creating artifact/version index.");
        worker.runBlocking(()->indexer.createVersionIndex());
    }

}
