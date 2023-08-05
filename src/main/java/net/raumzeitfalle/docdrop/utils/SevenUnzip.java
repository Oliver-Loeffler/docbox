package net.raumzeitfalle.docdrop.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import jakarta.inject.Singleton;

@Singleton
public class SevenUnzip implements BiConsumer<Path, Path>{

    public SevenUnzip() {
        
    }
    
    @Override
    public void accept(Path zipFile, Path storage) {
        List<String> cl = new ArrayList<>();
        cl.add("C:\\Github\\loefflo\\docdrop\\Binaries\\Windows\\7z\\7za.exe");
        cl.add("x");
        cl.add("-aoa");
        cl.add(zipFile.toAbsolutePath().toString());
        cl.add("-o"+storage.toAbsolutePath().toString());
        System.out.println(" >>> 7ZIP untar: " + cl.stream().collect(Collectors.joining(" ")));
        
        ProcessBuilder pb = new ProcessBuilder(cl).directory(zipFile.getParent().toFile());

        try {
            Process process = pb.start();
            int code = process.waitFor();
            if (code != 0) {
                System.out.println("Failed to untar: " + zipFile);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

}
