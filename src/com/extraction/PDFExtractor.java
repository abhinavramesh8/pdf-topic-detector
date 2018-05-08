package com.extraction;

import com.io.DirHandler;
import com.io.FileHandler;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created on 19/04/18.
 */
public class PDFExtractor {
    private static final String defualtDirPath = "/Users/rabhinav/Documents/pdf_text";

    public static String getDefaultDirPath() {
        return defualtDirPath;
    }

    private static List<PDDocument> getPages(PDDocument document) throws IOException {
        Splitter splitter = new Splitter();
        return splitter.split(document);
    }

    private static ArrayList<String> getText(File PDFFile) throws IOException {
        PDDocument document = PDDocument.load(PDFFile);
        PDFTextStripper textStripper = new PDFTextStripper();
        List<PDDocument> pages = getPages(document);
        Iterator<PDDocument> iterator = pages.listIterator();
        ArrayList<String> strings = new ArrayList<>();
        while(iterator.hasNext()) {
            PDDocument doc = iterator.next();
            strings.add(textStripper.getText(doc));
        }
        document.close();
        return strings;
    }

    private static ArrayList<String> getText(String path) throws IOException {
        File file = new File(path);
        return getText(file);
    }

    public static ArrayList<String> writeToDir(String PDFPath) throws IOException {
        return writeToDir(PDFPath, getDefaultDirPath());
    }

    public static ArrayList<String> writeToDir(String PDFPath, String dirPath) throws IOException {
        DirHandler dir = new DirHandler(dirPath);
        ArrayList<String> strings = getText(PDFPath);
        for(int i=0; i<strings.size(); i++) {
            FileHandler file = dir.createFile("page_" + (i+1));
            file.write(strings.get(i), false);
        }
        return strings;
    }
}
