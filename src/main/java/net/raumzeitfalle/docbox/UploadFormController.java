/*-
 * #%L
 * docdrop
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

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import net.raumzeitfalle.docbox.storage.ArtifactStorage;
import net.raumzeitfalle.docbox.storage.ArtifactUploadInput;

@ApplicationScoped
@Produces(MediaType.TEXT_HTML)
@Path("upload.html")
public class UploadFormController extends UploadController {
   
    @Location("upload.html")
    Template template;
    
    @Inject
    ArtifactStorage storage;
    
    @GET
    public TemplateInstance get() {
        return template.instance()
                       .data("config", configuration)
                       .data("groups", storage.collectGroups());
    }
    
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public TemplateInstance upload(ArtifactUploadInput input) {
        process(input, "Received artifact via Web Form POST request over \"/upload.html\":");
        return template.instance()
                       .data("config", configuration)
                       .data("groups", storage.collectGroups());
    }

}
