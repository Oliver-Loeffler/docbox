package net.raumzeitfalle.docdrop;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import net.raumzeitfalle.docdrop.storage.ArtifactStorage;
import net.raumzeitfalle.docdrop.storage.ArtifactUploadInput;

@ApplicationScoped
@Produces(MediaType.TEXT_HTML)
@Path("upload.html")
public class UploadFormController extends UploadController {
   
    @Location("upload.html")
    Template template;
    
    @Inject
    ArtifactStorage storage;
    
    @GET
    public TemplateInstance get() {
        return template.instance()
                       .data("config", configuration)
                       .data("groups", storage.collectGroups());
    }
    
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public TemplateInstance upload(ArtifactUploadInput input) {
        process(input, "Received artifact via Web Form POST request over \"/upload.html\":");
        return template.instance()
                       .data("config", configuration)
                       .data("groups", storage.collectGroups());
    }

}
