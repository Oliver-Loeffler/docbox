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
package net.raumzeitfalle.docdrop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vdurmont.semver4j.Semver;
import com.vdurmont.semver4j.Semver.SemverType;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class VersionCompareTest {

    @Test
    void test1() {
//        Semver oldest = new Semver("v20230101.12", SemverType.LOOSE);
        Semver oldie = new Semver("20230101.12", SemverType.LOOSE);
        Semver oldest = new Semver("v20230101.12", SemverType.NPM);
        Semver latest = new Semver("20230101.13", SemverType.LOOSE);

        assertTrue(latest.compareTo(oldest) > 0);
        assertEquals(oldie, oldest);
    }

    @Disabled("In works")
    @Test
    void test2() {

        Semver oldie = new Semver("20230101.12", SemverType.LOOSE);
        Semver oldest = new Semver("20230101.12", SemverType.LOOSE);
        Semver latest = new Semver("20230101.13");

        assertTrue(latest.compareTo(oldest) > 0);
        assertEquals(oldie, oldest);
    }

}
