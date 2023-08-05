package net.raumzeitfalle.docdrop.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import jakarta.inject.Singleton;

/**
 * unzip -o tar-1.13-1-bin.zip -d C:\Temp\Storage\my-group\my-artifact\2.0.1-SNAPSHOT\20230801224818
 */
@Singleton
public class Unzip implements BiConsumer<java.nio.file.Path,java.nio.file.Path> {
        
    public Unzip() {

    }
    
    @Override
    public void accept(Path file, Path storage) {
        if (OS.isLinux()) {
            this.unzip(file, storage);
        } else {
            this.sevenUnzip(file, storage);
        }
    }

    public void unzip(Path file, Path storage) {
        List<String> cl = new ArrayList<>();
        cl.add("/usr/bin/unzip");
        cl.add("-oq");
        cl.add(file.toAbsolutePath().toString());
        cl.add("-d");
        cl.add(storage.toAbsolutePath().toString());
        System.out.println(" >>> UNZIP: " + cl.stream().collect(Collectors.joining(" ")));
        
        
        
        ProcessBuilder pb = new ProcessBuilder(cl);

        try {
            Process process = pb.start();
            process.wait(10000);
            int code = process.waitFor();
            if (code != 0) {
                System.out.println("Error: " + code);
                System.out.println("Failed to unzip: " + file);
            }
        } catch (IOException e) {
            System.out.println("Unzip error: " + e);
            e.printStackTrace(System.out);
        } catch (InterruptedException e) {
            System.out.println("Unzip error: " + e);
            e.printStackTrace(System.out);
            Thread.currentThread().interrupt();
        }
    }
    
    public void sevenUnzip(Path zipFile, Path storage) {
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
