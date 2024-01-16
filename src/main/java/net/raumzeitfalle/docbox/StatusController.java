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
package net.raumzeitfalle.docbox;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import net.raumzeitfalle.docbox.storage.ArtifactQueue;
import net.raumzeitfalle.docbox.storage.ArtifactStatistics;
import net.raumzeitfalle.docbox.storage.ArtifactStorage;

@ApplicationScoped
@Produces(MediaType.TEXT_HTML)
@Path("status.html")
public class StatusController {
    
    Logger LOG = Logger.getLogger(ArtifactStorage.class.getName());
    
    @Inject
    Configuration configuration;
    
    @Inject
    ArtifactQueue artifactsQueue;
    
    @Inject
    ArtifactStorage artifactStorage;
    
    @Inject
    ArtifactStatistics statistics;
    
    @Location("status.html")
    Template template;
    
    @GET
    public TemplateInstance get() {
        int jobs = artifactsQueue.getReceivedArtifacts().size();
        int index = artifactsQueue.getArtifacts2Index().size();
        String[] fileNames = configuration.getIngestDirectory().toFile().list();
        int ingestedFiles = fileNames != null ? fileNames.length : 0;
        return template.instance()
                       .data("config", configuration)
                       .data("jobs", jobs)
                       .data("index", index)
                       .data("ingest", ingestedFiles)
                       .data("statistics", statistics);
    }
    
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public TemplateInstance runAction(ActionFormInput input) {
        LOG.log(Level.INFO, "Received action request: {0}", input);
        switch (input.action) {
            case "emptyIngestDir": 
                artifactsQueue.dropIngestedArtifacts();
                break;
            case "recreateGroupIndex":
                artifactsQueue.recreateGroupIndex();
                break;
            case "recreateArtifactIndex":
                artifactsQueue.recreateArtifactIndex();
                break;
            case "recreateVersionIndex":
                artifactsQueue.recreateVersionIndex();
                break;
            case "recreateSnapshotIndex":
                artifactsQueue.recreateSnapshotIndex();
                break;
            case "emptyArtifactsDir":
                artifactsQueue.dropArtifacts();
                break;
        }
        return get();
    }

    public static class ActionFormInput {
        @FormParam(value = "action")
        public String action;
        @Override
        public String toString() {
            return "ActionFormInput [action=" + action + "]";
        }
    }
}
