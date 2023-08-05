package net.raumzeitfalle.docdrop;

import java.nio.file.Path;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.inject.Singleton;

@Singleton
public class Configuration {
    @ConfigProperty(name = "docdrop.views.upload.url", defaultValue = "http://localhost:8080/upload.html")
    public String uploadUrl;

    @ConfigProperty(name = "docdrop.views.status.url", defaultValue = "http://localhost:8080/status.html")
    public String statusUrl;
    
    @ConfigProperty(name = "docdrop.views.artifacts.index.url", defaultValue = "http://localhost/artifacts")
    public String artifactsIndexUrl;
    
    @ConfigProperty(name="docdrop.artifact.storage.location", defaultValue = "data")
    public String artifactStorageRoot;
    
    @ConfigProperty(name="docdrop.css.bootstrap.dist.url")
    public String bootstrapCssUrl;
    
    @ConfigProperty(name="docdrop.css.url")
    public String docdropCssUrl;
    
    @ConfigProperty(name="docdrop.application.name", defaultValue = "DocDrop")
    public String applicationName;
    
    @ConfigProperty(name="docdrop.repository.name", defaultValue = "DocDrop")
    public String repositoryName;
    
    @ConfigProperty(name="docdrop.repository.index.file", defaultValue = "index.html")
    public String repositoryIndexFile;

    @ConfigProperty(name="docdrop.scm.url", defaultValue = "http://gitbucket/docdrop")
    public String scmUrl;
    
    public Path getArtifactsDirectory() {
        return Path.of(artifactStorageRoot).resolve("artifacts");
    }

    public Path getIngestDirectory() {
        return Path.of(artifactStorageRoot).resolve("ingest");
    }
}
