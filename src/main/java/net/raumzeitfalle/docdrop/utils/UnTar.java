package net.raumzeitfalle.docdrop.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import jakarta.inject.Singleton;

/*
 * 7za.exe x -aoa C:\Temp\Ingest\archive.tar -oC:\Temp\Storage\asasa\B\1\20230801232239
 */
@Singleton
public class UnTar implements BiConsumer<java.nio.file.Path, java.nio.file.Path>{
    
    public UnTar() {

    }
    
    @Override
    public void accept(java.nio.file.Path tarFile, java.nio.file.Path storage) {
        List<String> cl = new ArrayList<>();
        cl.add("C:\\Github\\loefflo\\docdrop\\Binaries\\Windows\\7z\\7za.exe");
        cl.add("x");
        cl.add("-aoa");
        cl.add(tarFile.toAbsolutePath().toString());
        cl.add("-o"+storage.toAbsolutePath().toString());
        System.out.println(" >>> 7ZIP untar: " + cl.stream().collect(Collectors.joining(" ")));
        
        ProcessBuilder pb = new ProcessBuilder(cl).directory(tarFile.getParent().toFile());

        try {
            Process process = pb.start();
            int code = process.waitFor();
            if (code != 0) {
                System.out.println("Failed to untar: " + tarFile);
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
