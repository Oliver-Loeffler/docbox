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
package net.raumzeitfalle.docbox.storage;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        worker.runInThread(() -> {
            storage.store(artifact).ifPresentOrElse(file -> index(),
                    () -> LOG.log(Level.INFO, "Index job is not started for unsupported artifacts."));
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
            worker.runBlocking(() -> indexer.index(artifact));
        }
    }

    public void dropIngestedArtifacts() {
        LOG.log(Level.WARNING, "All artifacts will be dropped!");
        worker.runInThread(() -> storage.dropIngestedArtifacts());
    }

    public void recreateGroupIndex() {
        LOG.log(Level.INFO, "Re-creating group index.");
        worker.runInThread(() -> indexer.createGroupIndex());
    }

    public void recreateArtifactIndex() {
        LOG.log(Level.INFO, "Re-creating artifact index.");
        worker.runInThread(() -> indexer.createArtifactIndex());
    }

    public void recreateVersionIndex() {
        LOG.log(Level.INFO, "Re-creating artifact/version index.");
        worker.runInThread(() -> indexer.createVersionIndex());
    }

    public void recreateSnapshotIndex() {
        LOG.log(Level.INFO, "Re-creating artifact/version/snapshot index.");
        worker.runInThread(() -> indexer.createSnapshotIndex());
    }

    public void dropArtifacts() {
        LOG.log(Level.WARNING, "All artifacts will be dropped!");
        worker.runInThread(() -> storage.dropArtifacts());
    }

}
