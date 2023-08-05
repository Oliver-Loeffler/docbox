package net.raumzeitfalle.docdrop;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class UploadFormControllerTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/upload.html")
          .then()
             .statusCode(200)
             .body(containsStringIgnoringCase("Submit"),
                   containsStringIgnoringCase("Add one more file..."),
                   containsStringIgnoringCase("curl -v -F group"));
    }

}
