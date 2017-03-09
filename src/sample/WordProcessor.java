//William Rory Chisholm - 100560820 - March/7/2017
//Assignment 01 - WordProcessor Class
package sample;

import java.io.*;
import java.util.*;

public class WordProcessor {
    private Map<String,Integer> wordFileCounts; // How many time the word appears in other files.
    private Map<String,File> fileTrack; // Keep track of last file used.

    public WordProcessor() { // Constructor
        wordFileCounts = new TreeMap<>();
        fileTrack = new TreeMap<>();
    }

    public WordProcessor(File file)throws IOException { // Constructor
        wordFileCounts = new TreeMap<>();
        fileTrack = new TreeMap<>();

        if (file.exists()) {
            this.processFile(file);
        }
    }

    // Processes file recursively goes through each file word by word and runs wordSearch
    public void processFile(File file) throws IOException {
        if (file.isDirectory()) {
            // process all of the files recursively
            File[] filesInDir = file.listFiles();
            for (int i = 0; i < filesInDir.length; i++) {
                processFile(filesInDir[i]);
            }
        } else if (file.exists()) {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String word = scanner.next();
                if (isWord(word)) {
                    wordSearch(file, word);
                }
            }
        }
    }

    private void wordSearch(File file, String word){ // Searches through other files for the same word and counts them
        if (wordFileCounts.containsKey(word) && (fileTrack.get(word) != file)){
            int oldCount = wordFileCounts.get(word);
            wordFileCounts.put(word, oldCount + 1);
            fileTrack.put(word, file);
        } else if (fileTrack.get(word) != file){
            wordFileCounts.put(word, 1);
            fileTrack.put(word, file);
        }
    }

    private boolean isWord(String str){ // Check if string is a word
        String pattern = "^[a-zA-Z]*$";
        if (str.matches(pattern)){
            return true;
        }
        return false;
    }

    public void printWordCounts(int minCount){ // Prints the count on each word
        Set<String> keys = wordFileCounts.keySet();
        Iterator<String> keyIterator = keys.iterator();
        while(keyIterator.hasNext()) {
            String key = keyIterator.next();
            int count = wordFileCounts.get(key);
            if (count >= minCount) {
                System.out.println(key + ": " + count);
            }
        }
    }

    public int fileCount(File file) throws IOException{ //Lists number of files not including directories.
        if (file.isDirectory()) { // Uses recursion
            File[] filesInDir = file.listFiles();
            int count = 0;
            for (int i = 0; i < filesInDir.length; i++) {
                count += fileCount(filesInDir[i]);
            }
            return count;
        } else if (file.exists()) {
            return 1;
        }else{
            return 0;
        }
    }

    public Map<String,Integer> getWordFileCounts(){ //Returns the file count
        return wordFileCounts;
    }

    // Gets the word frequency use the first half of Naive Bayes equation and rturns a tree with the new information
    public Map<String,Double> getWordFreq(File file)throws IOException{
        Map<String,Double> temp = new TreeMap<>();
        if (this.wordFileCounts.isEmpty()){
            this.processFile(file);
        }
        double numFiles = this.fileCount(file);
        Set<String> keys = this.wordFileCounts.keySet();
        Iterator<String> keyIterator = keys.iterator();
        while(keyIterator.hasNext()) {
            String key = keyIterator.next();
            double count = this.wordFileCounts.get(key);
            if (count >= 1) {
                double freq = (count/numFiles);
                temp.put(key, freq);
            }
        }
        return temp;
    }
}