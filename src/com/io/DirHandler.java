package com.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created on 19/04/18.
 */
public class DirHandler {
    private File dir;
    public DirHandler(String path) {
        dir = new File(path);
        dir.mkdirs();
    }
    public FileHandler createFile(String name) throws IOException {
        File file = new File(dir, name);
        file.createNewFile();
        return new FileHandler(file);
    }
    public ArrayList<FileHandler> getFiles() {
        File[] files = this.dir.listFiles();
        Arrays.sort(files, new FileNameComparator());
        ArrayList<FileHandler> fileHandlers = new ArrayList<>();
        for(File f: files) {
            fileHandlers.add(new FileHandler(f));
        }
        return fileHandlers;
    }
    public ArrayList<String> getTextFromFiles() throws IOException {
        List<FileHandler> files = getFiles();
        ArrayList<String> texts = new ArrayList<>();
        for(FileHandler fileHandler: files) {
            texts.add(fileHandler.read());
        }
        return texts;
    }

}
