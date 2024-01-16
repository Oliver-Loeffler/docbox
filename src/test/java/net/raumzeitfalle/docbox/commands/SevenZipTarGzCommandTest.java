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
package net.raumzeitfalle.docbox.commands;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.raumzeitfalle.docbox.storage.Artifact;

class SevenZipTarGzCommandTest {

    /*
     * groupId=A,
     * artifactName=A, 
     * version=A, 
     * dateTime=2023-08-06T16:04:03.566058, 
     * sourceFileName=myarchive.tar.gz, 
     * file=c:\Temp\ingest\resteasy-reactive15570287891417413586upload, 
     * artifactsDirectory=c:\Temp\artifacts 
     * 
     */

    @Disabled
    @Test
    void test() throws IOException, InterruptedException {
        Artifact artifact = new Artifact("groupA", 
                                         "artifactA", 
                                         "1.0.0", 
                                         LocalDateTime.now(), 
                                         "myarchive.tar.gz", 
                                         Path.of("c:\\Temp\\ingest\\resteasy-reactive1236310526222521455upload"),
                                         Path.of("c:\\Temp\\artifacts"));
        
        Path storage = Path.of("C:\\Temp\\Storage\\Artifacts");
        Path source = artifact.file();
        Path target = storage.resolve(artifact.sourceFileName());
        Optional<UnpackCommand> unpackCommand = UnpackCommand.fromArtifact(artifact);
        UnpackCommand command = unpackCommand.get();
        command.accept(source, target);
    }

}
