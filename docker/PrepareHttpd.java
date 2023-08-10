import java.io.IOException;
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
		
		String configured = conf.replace("DOCDROP_HOSTURL", serverUrl)
				                .replace("DOCDROP_PORT", docdropPort);
		
		Path optionalConf = Path.of("/etc/httpd/conf.d/docdrop.conf");
		try {
			Files.writeString(optionalConf, configured, StandardOpenOption.CREATE);
			LOG.log(Level.SEVERE, "Httpd configuration written to: {0}", optionalConf);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Failed to write httpd configuration in: %s".formatted(optionalConf),e);
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
