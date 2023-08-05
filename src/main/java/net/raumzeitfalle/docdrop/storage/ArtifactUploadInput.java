package net.raumzeitfalle.docdrop.storage;

import java.util.List;

import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.ws.rs.FormParam;

public class ArtifactUploadInput {
    
    @FormParam("group")
    public String group;

    @FormParam("artifact")
    public String artifact;

    @FormParam("version")
    public String version;

    @FormParam("file")
    public List<FileUpload> files;

    
}
