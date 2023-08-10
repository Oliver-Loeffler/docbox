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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PrepareHttpd {
	private static final Logger LOG = Logger.getLogger(PrepareHttpd.class.getName());
	public static void main(String[] args) {
		String serverUrl = getServerUrl();
		String docdropPort = getServerPort();
		String conf = """
				ProxyPass /upload DOCDROP_HOSTURL:DOCDROP_PORT/upload
                ProxyPassReverse /upload DOCDROP_HOSTURL:DOCDROP_PORT/upload

                ProxyPass /upload.html DOCDROP_HOSTURL:DOCDROP_PORT/upload.html
                ProxyPassReverse /upload.html http://localhost:DOCDROP_PORT/upload.html

                ProxyPass /status DOCDROP_HOSTURL:DOCDROP_PORT/status.html
                ProxyPassReverse /status DOCDROP_HOSTURL:DOCDROP_PORT/status.html

                ProxyPass /status.html DOCDROP_HOSTURL:DOCDROP_PORT/status.html
                ProxyPassReverse /status.html DOCDROP_HOSTURL:DOCDROP_PORT/status.html

				""";
		
		Path httpdConf = Path.of("/etc/httpd/conf/httpd.conf");
		String config = readHttpdConf(httpdConf);
		String proxy = conf.replace("DOCDROP_HOSTURL", serverUrl)
				           .replace("DOCDROP_PORT", docdropPort);

		config = config+System.lineSeparator()+proxy;
		try {
			Files.writeString(httpdConf, config, StandardOpenOption.CREATE);
			LOG.log(Level.SEVERE, "Httpd configuration written to: {0}", httpdConf);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Failed to write httpd configuration in: %s".formatted(httpdConf),e);
		}
	}

	private static String readHttpdConf(Path httpdConf) {
		try {
			return Files.readString(httpdConf);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Failed to read httpd configuration in: %s".formatted(httpdConf),e);
			throw new UncheckedIOException(e);
		}
	}

	private static String getServerPort() {
		return "8080";
	}

	private static String getServerUrl() {
		String serverUrl = System.getenv("DOCDROP_HOSTURL");
		if (null == serverUrl) {
			LOG.log(Level.INFO, "Failed to read environment variable DOCDROP_HOSTURL.");
			serverUrl = "http://localhost";
		} else {
			LOG.log(Level.INFO, "Detected environment variable DOCDROP_HOSTURL={0}", serverUrl);
		}
		if (serverUrl.endsWith("/")) {
			serverUrl = serverUrl.substring(0, serverUrl.length()-2);
		}
		LOG.log(Level.INFO, "Detected Server URL: {0}", serverUrl);
		return serverUrl;
	}
}
