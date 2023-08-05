package net.raumzeitfalle.docdrop;

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
import net.raumzeitfalle.docdrop.storage.ArtifactQueue;
import net.raumzeitfalle.docdrop.storage.ArtifactStorage;

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
    
    @Location("status.html")
    Template template;
    
    @GET
    public TemplateInstance get() {
        int jobs = artifactsQueue.getReceivedArtifacts().size();
        int index = artifactsQueue.getArtifacts2Index().size();
        int ingestedFiles = configuration.getIngestDirectory().toFile().list().length;
        return template.instance()
                       .data("config", configuration)
                       .data("jobs", jobs)
                       .data("index", index)
                       .data("ingest", ingestedFiles);
    }
    
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public TemplateInstance runAction(ActionFormInput input) {
        LOG.log(Level.INFO, "Received action request: {0}", input);
        switch (input.action) {
            case "emptyIngestDir": 
                artifactsQueue.emptyIngestDirectory();
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
