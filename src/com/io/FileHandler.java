package com.io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 19/04/18.
 */
public class FileHandler {
    private File file;
    public FileHandler(String path) throws IOException {
        this.file = new File(path);
        file.createNewFile();
    }
    public FileHandler(File f) {
        this.file = f;
        // Assuming here that the file has already been created
    }
    public void write(String data, boolean append) throws IOException {
        FileWriter fileWriter = new FileWriter(this.file, append);
        fileWriter.write(data);
        fileWriter.close();
    }
    public String read() throws IOException {
        return new String(Files.readAllBytes(Paths.get(this.file.getCanonicalPath())));
    }

    public List<String> readLines() throws IOException {
        FileReader fileReader = new FileReader(getFile());
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<>();
        String line = null;
        while((line = bufferedReader.readLine()) != null) {
            if(!line.isEmpty()) {
                lines.add(line);
            }
        }
        bufferedReader.close();
        fileReader.close();
        return lines;
    }

    public File getFile() {
        return file;
    }

    public boolean delete() {
        return getFile().delete();
    }
}
