package net.raumzeitfalle.docdrop.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SimpleUntarGz {
    Logger LOG = Logger.getLogger(SimpleUnzip.class.getName());

    private final Path source;
    
    private final Path target;

    public SimpleUntarGz(Path file) {
        LOG.info("Preparing tar/gz deflate");
        this.source = file.toAbsolutePath();
        this.target = file.getParent().toAbsolutePath(); 
    }
    
    public SimpleUntarGz(Path source, Path target) {
        LOG.info("Preparing tar/gz deflate");
        this.source = source;
        this.target = target; 
    }
    
    public int exec() throws IOException, InterruptedException {
        ProcessBuilder pb2 = new ProcessBuilder("/usr/bin/tar","-xvzf",source.toString(),"-C", target.toString());
        LOG.log(Level.INFO, "External Call: [{0}]", pb2.command().stream().collect(Collectors.joining(" ")));
        Process p2 = pb2.start();
        return p2.waitFor();
    }
    
    public int execUntar() throws IOException, InterruptedException {
        ProcessBuilder pb2 = new ProcessBuilder("/usr/bin/tar","-xvf",source.toString(),"-C", target.toString());
        LOG.log(Level.INFO, "External Call: [{0}]", pb2.command().stream().collect(Collectors.joining(" ")));
        Process p2 = pb2.start();
        return p2.waitFor();
    }
}
