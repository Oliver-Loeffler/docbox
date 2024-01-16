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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.quarkus.qute.Template;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import net.raumzeitfalle.docbox.storage.ArtifactIndexGenerator;

@QuarkusTest
public class ArtifactIndexGeneratorTest extends TestArtifactStorage {
    
    private ArtifactIndexGenerator classUnderTest; 
    
    private Path targetDir;
    
    private final Configuration config = new Configuration();
    
    @Inject
    private Template artifactIndexTest;
    
    @BeforeAll
    public static void prepare() throws Exception {
        removeAll();
        createSnapshot("group1", "artifact1", "version1", "snapshot1");
        createEmptySnapshot("group1", "artifact1", "version1", "snapshot2");
        createArtifact(createGroup("group1"), "artifact1", "version2");
        createArtifact(createGroup("group2"), "artifactX");
        createEmptySnapshot("group3", "artifact1", "version1", "snapshot2");
        createSnapshot("group4", "artifact1", "version1", "snapshot1");
        createSnapshot("group5", "artifact1", "version1", "snapshot1");
        createSnapshot("group5", "artifact1", "version1", "snapshot2");
    }
    
    @Test
    void that_index_is_generated_with_single_artifact_multiple_versions() {
        targetDir = Paths.get("TestData/01_artifacts_groups/group1");
        config.artifactStorageRoot = Paths.get("TestData").toAbsolutePath().toString();
        config.applicationName = "Application Name";
        config.repositoryName = "Repository";
        config.scmUrl = "http://gitbucket/DocDrop";
        config.uploadUrl = "http://localhost:8080/upload.html";
        config.bootstrapCssUrl = "http://localhost/dist/css/styles.css";

        classUnderTest = new ArtifactIndexGenerator(targetDir);
        classUnderTest.createIndex();
        String result = classUnderTest.render(artifactIndexTest.instance(), config);

        String expected = """
                <!doctype html>
                <HTML>
                <H1>Application Name</H1>
                <H2>01_artifacts_groups</H2>
                <H3>group1</H3>
                <SPAN>Root: TestData/01_artifacts_groups/group1</SPAN>
                <SPAN>HasParent: true</SPAN>
                <SPAN>Upload URL: http://localhost:8080/upload.html</SPAN>
                <SPAN>CSS URL: http://localhost/dist/css/styles.css</SPAN>
                <SPAN>SCM URL: http://gitbucket/DocDrop</SPAN>
                <SPAN>Repository: Repository</SPAN>
                <UL>
                <LI><a href="artifact1">artifact1</a> 2 versions(s)</LI>
                </UL>
                </HTML>
                """;

        assertEquals(expected.replace("\r\n", "\n"), result.replace("\r\n", "\n"));
    }

    @Test
    void that_index_is_generated_for_group_with_artifact_without_versions() {
        targetDir = Paths.get("TestData/01_artifacts_groups/group2");
        config.artifactStorageRoot = Paths.get("TestData").toAbsolutePath().toString();
        config.applicationName = "Application Name";
        config.repositoryName = "Repository";
        config.scmUrl = "http://gitbucket/DocDrop";
        config.uploadUrl = "http://localhost:8080/upload.html";
        config.bootstrapCssUrl = "http://localhost/dist/css/styles.css";

        classUnderTest = new ArtifactIndexGenerator(targetDir);
        classUnderTest.createIndex();
        String result = classUnderTest.render(artifactIndexTest.instance(), config);

        String expected = """
                <!doctype html>
                <HTML>
                <H1>Application Name</H1>
                <H2>01_artifacts_groups</H2>
                <H3>group2</H3>
                <SPAN>Root: TestData/01_artifacts_groups/group2</SPAN>
                <SPAN>HasParent: true</SPAN>
                <SPAN>Upload URL: http://localhost:8080/upload.html</SPAN>
                <SPAN>CSS URL: http://localhost/dist/css/styles.css</SPAN>
                <SPAN>SCM URL: http://gitbucket/DocDrop</SPAN>
                <SPAN>Repository: Repository</SPAN>
                <UL>
                <LI>artifactX</LI>
                </UL>
                </HTML>
                """;

        assertEquals(expected.replace("\r\n", "\n"), result.replace("\r\n", "\n"));
    }
}
