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
        String artifactMetaName = artifact.getFileName().toString() + ".meta.txt";
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
