package net.raumzeitfalle.docdrop;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import net.raumzeitfalle.docdrop.storage.ArtifactUploadInput;

@ApplicationScoped
@Path("/upload")
public class UploadRequestController extends UploadController {

    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @POST
    public void hello(ArtifactUploadInput input) {
        process(input, "Received artifact via POST request over \"/upload\":");
    }
}
