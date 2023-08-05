package net.raumzeitfalle.docdrop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.quarkus.qute.Template;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import net.raumzeitfalle.docdrop.storage.ArtifactIndexGenerator;

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

        assertEquals(expected, result);
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

        assertEquals(expected, result);
    }
}
