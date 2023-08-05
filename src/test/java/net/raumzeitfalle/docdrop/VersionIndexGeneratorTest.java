package net.raumzeitfalle.docdrop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.quarkus.qute.Template;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import net.raumzeitfalle.docdrop.storage.VersionIndexGenerator;

@QuarkusTest
public class VersionIndexGeneratorTest extends TestArtifactStorage {
    
    private VersionIndexGenerator classUnderTest; 
    
    private Path targetDir;
    
    private final Configuration config = new Configuration();
    
    @Inject
    private Template versionIndexTest;
    
    @BeforeAll
    public static void prepare() throws Exception {
        removeAll();
        createSnapshot("group1", "artifact1", "version1", "snapshot1");
        createEmptySnapshot("group1", "artifact1", "version1", "snapshot2");
        createArtifact(createGroup("group1"), "artifact1", "version2");
        createEmptySnapshot("group3", "artifact1", "version1", "snapshot1");
        createSnapshot("group4", "artifact1", "version1", "snapshot1");
    }

    @Test
    void that_index_is_generated_with_for_same_version_and_multiple_snapshots() {
        targetDir = Paths.get("TestData/01_artifacts_groups/group1/artifact1");
        config.artifactStorageRoot = Paths.get("TestData").toAbsolutePath().toString();
        config.applicationName = "Application Name";
        config.repositoryName = "Repository";
        config.scmUrl = "http://gitbucket/DocDrop";
        config.uploadUrl = "http://localhost:8080/upload.html";
        config.bootstrapCssUrl = "http://localhost/dist/css/styles.css";

        classUnderTest = new VersionIndexGenerator(targetDir, "group1");
        classUnderTest.createIndex();
        String result = classUnderTest.render(versionIndexTest.instance(), config);

        String expected = """
                <HTML>
                <H1>Application Name</H1>
                <H2>group1</H2>
                <H3>artifact1</H3>
                <SPAN>Root: TestData/01_artifacts_groups/group1/artifact1</SPAN>
                <SPAN>HasParent: true</SPAN>
                <SPAN>Upload URL: http://localhost:8080/upload.html</SPAN>
                <SPAN>CSS URL: http://localhost/dist/css/styles.css</SPAN>
                <SPAN>SCM URL: http://gitbucket/DocDrop</SPAN>
                <SPAN>Repository: Repository</SPAN>
                <UL>
                <LI><a href="version1">version1</a> 2 snapshots</LI>
                <LI>version2</LI>
                </UL>
                </HTML>
                """;

        assertEquals(expected, result);
    }

    @Test
    void that_index_is_generated_for_version_without_snapshot() throws Exception {
        targetDir = Paths.get("TestData/01_artifacts_groups/group3/artifact1");
        config.artifactStorageRoot = Paths.get("TestData").toAbsolutePath().toString();
        config.applicationName = "Application Name";
        config.repositoryName = "Repository";
        config.scmUrl = "http://gitbucket/DocDrop";
        config.uploadUrl = "http://localhost:8080/upload.html";
        config.bootstrapCssUrl = "http://localhost/dist/css/styles.css";

        classUnderTest = new VersionIndexGenerator(targetDir, "group3");
        classUnderTest.createIndex();
        String result = classUnderTest.render(versionIndexTest.instance(), config);

        String expected = """
                <HTML>
                <H1>Application Name</H1>
                <H2>group3</H2>
                <H3>artifact1</H3>
                <SPAN>Root: TestData/01_artifacts_groups/group3/artifact1</SPAN>
                <SPAN>HasParent: true</SPAN>
                <SPAN>Upload URL: http://localhost:8080/upload.html</SPAN>
                <SPAN>CSS URL: http://localhost/dist/css/styles.css</SPAN>
                <SPAN>SCM URL: http://gitbucket/DocDrop</SPAN>
                <SPAN>Repository: Repository</SPAN>
                <UL>
                <LI>version1</LI>
                </UL>
                </HTML>
                """;

        assertEquals(expected, result);
    }
    
    @Test
    void that_index_is_generated_for_version_with_single_snapshot() throws Exception {
        targetDir = Paths.get("TestData/01_artifacts_groups/group4/artifact1");
        config.artifactStorageRoot = Paths.get("TestData").toAbsolutePath().toString();
        config.applicationName = "Application Name";
        config.repositoryName = "Repository";
        config.scmUrl = "http://gitbucket/DocDrop";
        config.uploadUrl = "http://localhost:8080/upload.html";
        config.bootstrapCssUrl = "http://localhost/dist/css/styles.css";

        classUnderTest = new VersionIndexGenerator(targetDir, "group4");
        classUnderTest.createIndex();
        String result = classUnderTest.render(versionIndexTest.instance(), config);

        String expected = """
                <HTML>
                <H1>Application Name</H1>
                <H2>group4</H2>
                <H3>artifact1</H3>
                <SPAN>Root: TestData/01_artifacts_groups/group4/artifact1</SPAN>
                <SPAN>HasParent: true</SPAN>
                <SPAN>Upload URL: http://localhost:8080/upload.html</SPAN>
                <SPAN>CSS URL: http://localhost/dist/css/styles.css</SPAN>
                <SPAN>SCM URL: http://gitbucket/DocDrop</SPAN>
                <SPAN>Repository: Repository</SPAN>
                <UL>
                <LI><a href="version1">version1</a></LI>
                </UL>
                </HTML>
                """;

        assertEquals(expected, result);
    }
}
