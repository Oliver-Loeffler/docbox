package net.raumzeitfalle.docdrop.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record MetaFile(Path artifact, Path targetDir, ArtifactUploadInput artifactInput, LocalDateTime timestamp) {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    private static final String META_TEMPLATE = """
            file=%s
            timestamp=%s
            group=%s
            artifact=%s
            version=%s
            """;

    public Path write() throws IOException {
        String timestamp = TIMESTAMP_FORMAT.format(timestamp());
        ArtifactUploadInput input = artifactInput();
        String artifactName = artifact.getFileName().toString();
        String artifactMetaName = artifact.getFileName().toString()+".meta.txt";
        Path metaFile = targetDir().resolve(artifactMetaName);
        String meta = META_TEMPLATE.formatted(artifactName,
                                              timestamp,
                                              input.group,
                                              input.artifact,
                                              input.version);
        Files.writeString(metaFile, meta, StandardOpenOption.CREATE);
        return metaFile;
    }
}
