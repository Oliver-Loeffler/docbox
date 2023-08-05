package net.raumzeitfalle.docdrop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.quarkus.qute.Template;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import net.raumzeitfalle.docdrop.storage.GroupIndexGenerator;

@QuarkusTest
public class GroupIndexGeneratorTest extends TestArtifactStorage {
    
    private GroupIndexGenerator classUnderTest; 
    
    private Path targetDir;
    
    private final Configuration config = new Configuration();
    
    @Inject
    private Template groupIndexTest;
    
    @BeforeAll
    public static void prepare() throws Exception {
        removeAll();
        createArtifact(createGroup("group1"), "artifact1", "version1");
        createArtifact(createGroup("group2"), "artifactX");
        createArtifact(createGroup("group3"), "artifact1", "version1");
        createArtifact(createGroup("group4"), "artifact1", "version1");
        createArtifact(createGroup("group5"), "artifact1", "version1");
    }

    @Test
    void that_group_index_is_created_properly() {
        targetDir = Paths.get("TestData/01_artifacts_groups");
        config.artifactStorageRoot = Paths.get("TestData").toAbsolutePath().toString();
        config.applicationName = "Application Name";
        config.repositoryName = "Repository";
        config.scmUrl = "http://gitbucket/DocDrop";
        config.uploadUrl = "http://localhost:8080/upload.html";
        config.bootstrapCssUrl = "http://localhost/dist/css/styles.css";

        classUnderTest = new GroupIndexGenerator(targetDir, "Group IDs");
        classUnderTest.createIndex();
        String result = classUnderTest.render(groupIndexTest.instance(), config);

        String expected = """
                <HTML>
                <H1>Application Name</H1>
                <H2>Group IDs</H2>
                <SPAN>Root: TestData/01_artifacts_groups</SPAN>
                <SPAN>HasParent: false</SPAN>
                <SPAN>Upload URL: http://localhost:8080/upload.html</SPAN>
                <SPAN>CSS URL: http://localhost/dist/css/styles.css</SPAN>
                <SPAN>SCM URL: http://gitbucket/DocDrop</SPAN>
                <SPAN>Repository: Repository</SPAN>
                <UL>
                <LI><a href="group1">group1</a> 1 artifact(s)</LI>
                <LI><a href="group2">group2</a> 1 artifact(s)</LI>
                <LI><a href="group3">group3</a> 1 artifact(s)</LI>
                <LI><a href="group4">group4</a> 1 artifact(s)</LI>
                <LI><a href="group5">group5</a> 1 artifact(s)</LI>
                </UL>
                </HTML>
                """;

        assertEquals(expected, result);
    }
}
