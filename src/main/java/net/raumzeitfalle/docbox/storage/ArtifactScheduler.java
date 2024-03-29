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

import java.util.concurrent.atomic.AtomicInteger;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ArtifactScheduler {

    @Inject
    ArtifactQueue workload;

    @Inject
    ArtifactIndexer indexer;

    private AtomicInteger counter = new AtomicInteger();

    /*
     * TODO: Replace the fixed 5s schedule with a cron expression
     * 
     * @Scheduled(cron = "{cron.expr}")
     */
    @Scheduled(every = "5s")
    void increment() {
        counter.incrementAndGet();
        if (workload.getReceivedArtifacts().size() > 0) {
            workload.multiStore();
        }
    }

}
