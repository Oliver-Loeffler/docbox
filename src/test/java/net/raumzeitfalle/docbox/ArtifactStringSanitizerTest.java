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

import org.junit.jupiter.api.Test;

import net.raumzeitfalle.docbox.storage.Artifact;

public class ArtifactStringSanitizerTest {

    @Test
    void test() {
        var badString = " - {} * '' ! ___ \" \n^^//``\t"
                      + "....He    l lo+_|- ? :W\t&o?[]()rld .2024 _---  ";

        assertEquals("-_.Hello+_-World.2024_-",
                Artifact.sanitize(badString));
    }

}
