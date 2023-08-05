package net.raumzeitfalle.docdrop.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SimpleUnzip {
    
    Logger LOG = Logger.getLogger(SimpleUnzip.class.getName());

    private final Path source;
    
    private final Path target;

    public SimpleUnzip(Path file) {
        LOG.info("Preparing Unzip");
        this.source = file.toAbsolutePath();
        this.target = file.getParent().toAbsolutePath(); 
    }
    
    public SimpleUnzip(Path source, Path target) {
        LOG.info("Preparing Unzip");
        this.source = source;
        this.target = target; 
    }
    
    public int exec() throws IOException, InterruptedException {
        ProcessBuilder pb2 = new ProcessBuilder("unzip","-oqq",source.toString(),"-d", target.toString());
        LOG.log(Level.INFO, "External Call: [{0}]", pb2.command().stream().collect(Collectors.joining(" ")));
        Process p2 = pb2.start();
        return p2.waitFor();
    }
}
