package com.topic_detection;

import java.util.*;

/**
 * Created on 23/04/18.
 */
public class Topic {
    private int id;
    private List<String> keywords;
    private SortedSet<Integer> docIDs;

    public Topic(int id, List<String> keywords, SortedSet<Integer> docIDs) {
        this.id = id;
        this.keywords = keywords;
        this.docIDs = docIDs;
    }

    public Topic(int id) {
        this(id, new ArrayList<>(), new TreeSet<>());
    }

    public int getID() {
        return this.id;
    }

    public void addDocID(int docID) {
        this.docIDs.add(docID);
    }

    public void addKeyword(String keyword) {
        this.keywords.add(keyword);
    }

    public SortedSet<Integer> getDocIDs() {
        return docIDs;
    }

    public String toString() {
        return String.join("_", this.keywords);
    }
}
