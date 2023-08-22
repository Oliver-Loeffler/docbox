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
package net.raumzeitfalle.docbox.commands;

import java.util.logging.Level;
import java.util.logging.Logger;

public enum Platform {
    WINDOWS,
    LINUX,
    OTHER;
    
    public static Platform from(String osName) {
        if (osName != null) {
        	if (osName.toLowerCase().contains("nux")) {
        		return LINUX;
        	}
            if (osName.toLowerCase().contains("win")) {
                return WINDOWS;
            }
            return OTHER;
        }
        throw new IllegalArgumentException("Failed to detect operating system! " + osName);
    }
    
    private static Platform thisSystem = null;
    
    public static Platform get() {
        if (thisSystem == null) {
            thisSystem = Platform.from(System.getProperty("os.name").toLowerCase());
            Logger.getLogger(Platform.class.getName()).log(Level.INFO, "Detected OS: {0}", thisSystem);
        }
        return thisSystem;
    }
}
