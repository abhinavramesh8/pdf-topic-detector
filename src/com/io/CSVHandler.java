package com.io;

import com.topic_detection.Topic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created on 28/04/18.
 */
public class CSVHandler extends FileHandler {
    private final String COMMA_DELIM = ",";
    private final String NEW_LINE_SEP = "\n";
    public CSVHandler(String path) throws IOException {
        super(path);
    }

    public CSVHandler(File f) {
        super(f);
    }

    private void writeTopic(Topic topic, FileWriter fileWriter) throws IOException {
        fileWriter.append(topic.toString());
        for(int docID: topic.getDocIDs()) {
            fileWriter.append(COMMA_DELIM);
            fileWriter.append(String.valueOf(docID+1));
        }
        fileWriter.append(NEW_LINE_SEP);
    }

    public void write(Map<Integer, Topic> topics) throws IOException {
        FileWriter fileWriter = new FileWriter(getFile());
        for(Topic topic : topics.values()) {
            writeTopic(topic, fileWriter);
        }
        fileWriter.flush();
        fileWriter.close();
    }

}
