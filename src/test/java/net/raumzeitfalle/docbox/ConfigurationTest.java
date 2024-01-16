/*-
 * #%L
 * docbox
 * %%
 * Copyright (C) 2023 - 2024 Oliver Loeffler, Raumzeitfalle.net
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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ConfigurationTest {

    private final Configuration config = new Configuration();
    
    @Test
    void that_port_80_is_not_shown_in_upload_url() {
        config.uploadUrl = "/upload.html";
        config.hostUrl = "http://myhost";
        config.apacheHttpdPort = 80;
        
        assertEquals("http://myhost/upload.html", config.getUploadUrl());
    }

    @Test
    void that_port_different_from_80_is_shown_in_upload_url() {
        config.uploadUrl = "/up.html";
        config.hostUrl = "http://otherhost";
        config.apacheHttpdPort = 8080;
        
        assertEquals("http://otherhost:8080/up.html", config.getUploadUrl());
    }
}
