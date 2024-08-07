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
		String docboxPort = getServerPort();
		String conf = """
                      ProxyPass /upload DOCBOX_HOSTURL:DOCBOX_PORT/upload
                      ProxyPassReverse /upload DOCBOX_HOSTURL:DOCBOX_PORT/upload
                      
                      ProxyPass /upload.html DOCBOX_HOSTURL:DOCBOX_PORT/upload.html
                      ProxyPassReverse /upload.html http://localhost:DOCBOX_PORT/upload.html      
                      
                      ProxyPass /status DOCBOX_HOSTURL:DOCBOX_PORT/status.html
                      ProxyPassReverse /status DOCBOX_HOSTURL:DOCBOX_PORT/status.html
                      
                      ProxyPass /status.html DOCBOX_HOSTURL:DOCBOX_PORT/status.html
                      ProxyPassReverse /status.html DOCBOX_HOSTURL:DOCBOX_PORT/status.html
                      """;
		
		Path httpdConf = Path.of("/etc/httpd/conf/httpd.conf");
		Path backupedHttpdConf = createBackup(httpdConf);
				
		String config = readHttpdConf(backupedHttpdConf);
		String proxy = conf.replace("DOCBOX_HOSTURL", serverUrl)
				           .replace("DOCBOX_PORT", docboxPort);
		
		if (args.length > 0 && "docker".equalsIgnoreCase(args[0])) {
			proxy = conf.replace("DOCBOX_HOSTURL", "http://localhost")
			           	.replace("DOCBOX_PORT", "8080");
		}
		

		config = config+System.lineSeparator()+proxy;
		try {
			Files.writeString(httpdConf, config, StandardOpenOption.CREATE);
			LOG.log(Level.SEVERE, "Httpd configuration written to: {0}", httpdConf);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Failed to write httpd configuration in: %s".formatted(httpdConf),e);
		}
		
		createHttpdLogDirIfNeeded();
	}

	private static void createHttpdLogDirIfNeeded() {
		Path logDir = Path.of("/var/log/httpd");
		if (Files.notExists(logDir)) {
			try {
				Files.createDirectories(logDir);
			} catch (IOException e) {
				LOG.log(Level.SEVERE, "Failed to create httpd log directory: %s".formatted(logDir),e);
			}
		}
	}

	private static Path createBackup(Path httpdConf) {
		Path backupFile = httpdConf.getParent().resolve(httpdConf.getFileName().toString()+"_backup");
		if (Files.exists(backupFile)) {
			return backupFile;
		}
		try {
			LOG.log(Level.INFO, "Creating backup of %s to %s".formatted(httpdConf, backupFile));
			Files.copy(httpdConf, backupFile);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Failed to create backup of %s into %s".formatted(httpdConf, backupFile),e);
			throw new UncheckedIOException(e);
		}
		return backupFile;
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
		String serverUrl = System.getenv("DOCBOX_HOSTURL");
		if (null == serverUrl) {
			LOG.log(Level.INFO, "Failed to read environment variable DOCBOX_HOSTURL.");
			serverUrl = "http://localhost";
		} else {
			LOG.log(Level.INFO, "Detected environment variable DOCBOX_HOSTURL={0}", serverUrl);
		}
		if (serverUrl.endsWith("/")) {
			serverUrl = serverUrl.substring(0, serverUrl.length()-2);
		}
		LOG.log(Level.INFO, "Detected Server URL: {0}", serverUrl);
		return serverUrl;
	}
}
