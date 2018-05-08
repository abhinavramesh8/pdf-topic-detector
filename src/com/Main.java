package com;

import com.extraction.PDFExtractor;
import com.io.CSVHandler;
import com.io.DirHandler;
import com.io.FileHandler;
import com.topic_detection.Topic;
import com.topic_detection.TopicDetector;
import com.ui.FileDialog;
import com.writing.TopicWriter;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created on 08/05/18.
 */
public class Main {

    private static int numTopics = 10;
    private static int numKeywords = 3;
    private static int numIters = 1000;
    private static String defaultCSVName = "topic_pages.csv";

    private static void index(String upl_path, String save_path) throws IOException {
        List<String> texts = PDFExtractor.writeToDir(upl_path);
        DirHandler dir = new DirHandler(PDFExtractor.getDefaultDirPath());
        CSVHandler csv = new CSVHandler(dir.createFile(defaultCSVName).getFile());
        Map<Integer, Topic> topics =  TopicDetector.getTopics(texts, numTopics, numIters, numKeywords);
        csv.write(topics);
        TopicWriter topicWriter = new TopicWriter(new File(upl_path));
        topicWriter.write(csv, save_path);
    }

    public static void run() throws IOException {
        DirHandler dir = new DirHandler(PDFExtractor.getDefaultDirPath());
        FileHandler file = dir.createFile(FileDialog.getDefaultFile());
        List<String> upl_save = file.readLines();
        System.out.println(upl_save);
        Iterator<String> iterator = upl_save.iterator();
        while (iterator.hasNext()) {
            String uploaded = iterator.next();
            String save = null;
            if (iterator.hasNext()) {
                save = iterator.next();
            }
            if (save != null) {
                index(uploaded, save);
            }

        }
    }

    public static void main(String[] args) {
        try {
            FileDialog.display();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
