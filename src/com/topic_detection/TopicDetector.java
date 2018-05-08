package com.topic_detection;


import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.ArrayIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 23/04/18.
 */
public class TopicDetector {

    private static final String TOPIC_REGEX = "topic(\\d+)";

    private static ParallelTopicModel createLDA(InstanceList instanceList, int numTopics, int numIters) throws IOException {
        ParallelTopicModel model = new ParallelTopicModel(numTopics);
        model.addInstances(instanceList);
        model.setNumIterations(numIters);
        model.estimate();
        return model;
    }

    private static InstanceList createInstanceList(List<String> texts) {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequenceLowercase());
        pipes.add(new TokenSequenceRemoveStopwords());
        pipes.add(new TokenSequence2FeatureSequence());
        InstanceList instanceList = new InstanceList(new SerialPipes(pipes));
        instanceList.addThruPipe(new ArrayIterator(texts));
        return instanceList;
    }

    private static int extractTopicID(String topic) {
        Pattern p = Pattern.compile(TOPIC_REGEX);
        Matcher m = p.matcher(topic);
        m.find();
        return Integer.parseInt(m.group(1));
    }

    private static Map<Integer, Topic> getTopicMap(int docsNum, ParallelTopicModel model) {
        Map<Integer, Topic> topicMap = new HashMap<>();
        for(int i=0; i<docsNum; i++) {
            LabelSequence topicLabels = model.getData().get(i).topicSequence;
            Iterator iterator = topicLabels.iterator();
            while(iterator.hasNext()) {
                int topicID = extractTopicID(iterator.next().toString());
                Topic topic = topicMap.get(topicID);
                if(topic == null) {
                    topic = new Topic(topicID);
                    topicMap.put(topicID, topic);
                }
                topic.addDocID(i);
            }
        }
        return topicMap;
    }

    private static void addKeywordsToTopics(int numKeywords, Map<Integer, Topic> topics, InstanceList instances, ParallelTopicModel model) {
        Alphabet dataAlphabet = instances.getDataAlphabet();
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
        for(Topic topic: topics.values()) {
            Iterator<IDSorter> iterator = topicSortedWords.get(topic.getID()).iterator();
            int numWords = 0;
            while(iterator.hasNext() && numWords<numKeywords) {
                IDSorter idCountPair = iterator.next();
                String keyword = (String) dataAlphabet.lookupObject(idCountPair.getID());
                topic.addKeyword(keyword);
                numWords++;
            }
        }
    }

    public static Map<Integer, Topic> getTopics(List<String> texts, int numTopics, int numIters, int numKeywords) throws IOException {
        InstanceList instances = createInstanceList(texts);
        ParallelTopicModel model = createLDA(instances, numTopics, numIters);
        Map<Integer, Topic> topicMap = getTopicMap(texts.size(), model);
        addKeywordsToTopics(numKeywords, topicMap, instances, model);
        return topicMap;
    }
}
