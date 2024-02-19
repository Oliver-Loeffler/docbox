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

import java.util.logging.Level;
import java.util.logging.Logger;

import io.quarkus.runtime.ShutdownEvent;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.raumzeitfalle.docbox.Configuration;

@Singleton
public class DirectoryMonitorService {

    private static final Logger LOG = Logger.getLogger(DirectoryMonitorService.class.getName());

    @Inject
    Configuration configuration;

    private DirectoryMonitor monitor;

    public DirectoryMonitorService() {
        this.monitor = new DirectoryMonitor();
    }

    @PostConstruct
    public void startMonitor() {
        LOG.log(Level.INFO, "Starting monitor....");
        try {
            this.monitor.start();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Monitor startup failed!", e);
        }
    }

    public void stopMonitor() {
        LOG.log(Level.INFO, "Stopping monitor....");
        try {
            this.monitor.stop(12);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Monitor shutdown failed!", e);
        }
    }

    public void shutdown(@Observes ShutdownEvent event) {
        try {
            this.monitor.stop(1);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Monitor shutdown failed!", e);
        }
    }

    public void checkMonitor() {
        if (this.monitor != null) {
            boolean isRunning = this.monitor.isRunning();
            if (isRunning) {
                LOG.log(Level.INFO, "The monitor is [running] in: [{0}]", this.monitor.getPath());
            } else {
                LOG.log(Level.INFO, "The monitor is [NOT] running.");
            }
        }
    }
}
