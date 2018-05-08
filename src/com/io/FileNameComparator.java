package com.io;

import java.io.File;
import java.util.Comparator;

/**
 * Created on 23/04/18.
 */
public class FileNameComparator implements Comparator<File> {
    public int compare(File a, File b) {
        return a.getName().compareTo(b.getName());
    }
}
