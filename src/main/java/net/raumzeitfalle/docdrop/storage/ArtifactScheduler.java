package net.raumzeitfalle.docdrop.storage;

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

    //@Scheduled(cron = "{cron.expr}") 
    @Scheduled(every="5s")
    void increment() {
        counter.incrementAndGet();
        if (workload.getReceivedArtifacts().size() > 0) {
//            workload.store();
            workload.multiStore();
        }
    }
    
    
}
