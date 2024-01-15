import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;


public class PrepareDist {
	
	private static final Logger LOG = Logger.getLogger(PrepareDist.class.getName());
	
	private static final Path DIST_ROOT = Path.of("/docbox/dist");
	
	private static final Path TARGET_DIR = Path.of("/var/www/html/dist");
	
	public static void main(String[] args) {
		new PrepareDist().run();
	}

	public void run() {
		copyDistContents();
	}

	private void copyDistContents() {
		File[] candidates = DIST_ROOT.toFile().listFiles();
		for (File src : candidates) {
			if (src.isDirectory()) {
				copyDir(src);
			} else {
				copyFile(src);
			}
		}
	}

	private void copyDir(File src) {
		try {
			copyDirectory(src.toPath(), TARGET_DIR);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Failed to copy directory!", e);
		}
	}

	private void copyFile(File src) {
		try {
			copyIfNotExists(src.toPath(), DIST_ROOT, TARGET_DIR);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Failed to copy file!", e);
		}
	}
	
	public void copyDirectory(Path sourceDir, Path targetDir) throws IOException {
		LOG.log(Level.INFO, "Attempting to copy " + sourceDir + " into " + targetDir);
		try (Stream<Path> files = Files.walk(sourceDir)) {
			files.forEach(source -> {
				try {
					copyIfNotExists(source, DIST_ROOT, TARGET_DIR);
				} catch (IOException e) {
					throw new java.io.UncheckedIOException(e);
				}
			});
		}
	}

	private void copyIfNotExists(Path source, Path sourceDir, Path targetDir) throws IOException {
		int sourceDirLength = sourceDir.toAbsolutePath().getNameCount();
		int sourceLength = source.toAbsolutePath().getNameCount();
		
		Path sourceFile = source.toAbsolutePath();
		Path targetFile = targetDir.resolve(source.toAbsolutePath().subpath(sourceDirLength, sourceLength)).toAbsolutePath();
		
		LOG.log(Level.INFO, "Attempting to copy file " + sourceFile + " to " + targetFile);
		
		if (Files.isDirectory(sourceFile) && Files.notExists(targetFile)) {
			Files.createDirectories(targetFile);
		} else if (!Files.isDirectory(sourceFile) && Files.notExists(targetFile)) {
			Files.copy(sourceFile, targetFile);
		}
	}

}
