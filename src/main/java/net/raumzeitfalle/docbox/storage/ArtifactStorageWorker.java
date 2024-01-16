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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;

@Singleton
@Startup
public class ArtifactStorageWorker {

    private static final Logger LOG = Logger.getLogger(ArtifactStorageWorker.class.getName());

    private final WorkerExecutor executor;

    private final Executor cachedThreadPool;

    ArtifactStorageWorker(Vertx vertx) {
        executor = vertx.createSharedWorkerExecutor("docbox-storage-worker");
        cachedThreadPool = Executors.newCachedThreadPool();
    }

    void tearDown(@Observes ShutdownEvent ev) {
        executor.close();
    }

    public void runInThread(Runnable action) {
        LOG.info("Received background task request.");
        cachedThreadPool.execute(action);
    }

    public void runBlocking(Runnable action) {
        LOG.info("Received request to run blocking action");
        executor.executeBlocking(promise -> {
            LOG.info(">>> Blocking process started <<<");
            action.run();
            promise.complete();
        }).onComplete(promise -> {
            if (promise.succeeded()) {
                LOG.info(">>> Blocking process successfully completed. <<<");
            } else {
                LOG.log(Level.SEVERE, "Error occurred durong blocking process execution.", promise.cause());
            }
        });
    }
}
