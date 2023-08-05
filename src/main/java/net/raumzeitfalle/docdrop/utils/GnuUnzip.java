package net.raumzeitfalle.docdrop.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import jakarta.inject.Singleton;

@Singleton
public class GnuUnzip implements UnaryOperator<java.nio.file.Path>{

    public GnuUnzip() {

    }
    
    @Override
    public Path apply(java.nio.file.Path gzFile) {
        List<String> cl = new ArrayList<>();
        cl.add("C:\\Github\\loefflo\\docdrop\\Binaries\\Windows\\gzip\\bin\\gzip.exe");
        cl.add("-f");
        cl.add("-d");
        cl.add(gzFile.toAbsolutePath().toString());
        System.out.println(" >>> GZIP -d: " + cl.stream().collect(Collectors.joining(" ")));
        ProcessBuilder pb = new ProcessBuilder(cl);
        try {
            Process process = pb.start();
            int code = process.waitFor();
            if (code != 0) {
                System.out.println("Failed to unzip: " + gzFile);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        
        String decompressed = gzFile.getFileName().toString().replace(".tar.gz", ".tar");
        return gzFile.getParent().resolve(decompressed);
    }

}
