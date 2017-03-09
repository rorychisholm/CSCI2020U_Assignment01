//William Rory Chisholm - 100560820 - March/7/2017
//Assignment 01 - Main Class
package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;

import java.io.*;
import java.util.*;

public class Main extends Application {
    private BorderPane layout;
    private TableView<TestFile> table;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Spam Detector - Assignment 01"); // Sets title of the window

        Map<String,Double> trainFreq = new TreeMap<>();// Combined frequency percent of spam and ham in map
        Map<String,Double> trainSpamFreq = new TreeMap<>(); // Frequency percent of spam
        Map<String,Double> trainHamFreq = new TreeMap<>(); // Frequency percent of ham

        // DirectoryChooserListing 2: Sample code for DirectoryChooser from listing 2
        DirectoryChooser directoryChooser = new DirectoryChooser(); // Initializes directoryChooser
        directoryChooser.setInitialDirectory(new File(".")); // Sets initial directory
        File mainDirectory; // Creates main directory that user will select

        try { // Catch null error when no file is selected.
            do {
                mainDirectory = directoryChooser.showDialog(primaryStage);// Shows window for selecting the main directory
            }while(!checkData(mainDirectory)); // Doesn't let users pass without selecting a file containing ham and spam files for test and train.

            File hamDir = findFile(findFile(mainDirectory, "train"), "ham"); // Finds ham directory in train
            File spamDir = findFile(findFile(mainDirectory, "train"), "spam"); // Finds spam directory in train
            File testHamDir = findFile(findFile(mainDirectory, "test"), "ham"); // Finds ham directory in test
            File testSpamDir = findFile(findFile(mainDirectory, "test"), "spam"); // Finds spam directory in test

            WordProcessor trainHamProcessor = new WordProcessor(hamDir); // Initializes word processor class for the training ham file
            trainHamFreq = trainHamProcessor.getWordFreq(hamDir); // Sets up the map for training ham

            WordProcessor trainSpamProcessor = new WordProcessor(spamDir); // Initializes word processor class for the training spam file
            trainSpamFreq = trainSpamProcessor.getWordFreq(spamDir); // Sets up the map for training ham

            WordProcessor testHamProcessor = new WordProcessor(testHamDir); // Initializes word processor class for the testing ham file
            WordProcessor testSpamProcessor = new WordProcessor(testSpamDir); // Initializes word processor class for the testing spam file

            TestFile[] testFileHam = new TestFile[testHamProcessor.fileCount(testHamDir)]; // Initializes TestFile class array for the testing ham files
            TestFile[] testFileSpam = new TestFile[testSpamProcessor.fileCount(testSpamDir)]; // Initializes TestFile class array for the testing spam files

            // Combining the hamFreq and spamFreq into trainFreq
            Set<String> spamKeys = trainSpamFreq.keySet(); // Obtains key set
            Set<String> hamKeys = trainHamFreq.keySet(); // Obtains key set

            Iterator<String> spamKeyIterator = spamKeys.iterator(); // Declares iterator
            Iterator<String> hamKeyIterator; // Declares iterator

            while(spamKeyIterator.hasNext()) { // While
                String spamKey = spamKeyIterator.next(); // Declares key for getting the next iterator
                hamKeyIterator = hamKeys.iterator(); // Declares iterator
                while (hamKeyIterator.hasNext()) {
                    String hamKey = hamKeyIterator.next(); // Declares key for getting the next iterator
                    if (spamKey.equals(hamKey)) {
                        double freq = ((trainSpamFreq.get(spamKey)) / (trainSpamFreq.get(spamKey) + trainHamFreq.get(hamKey)));
                        // Pr(S/W) = Pr(W/S) / (Pr(W/S) + Pr(W/H))
                        trainFreq.put(hamKey, freq); // puts Pr(S/W) in to new map
                    }
                }
            }


            naiveBayes(testHamDir, trainFreq, testFileHam, -1 , "Ham"); // Calculates the second half of Naives Bayes formula for ham
            naiveBayes(testSpamDir, trainFreq, testFileSpam, -1 , "Spam"); // Calculates the second half of Naives Bayes formula for spam

            // Places ham and spam TestFile in ObservableList data
            for(int i = 0; i < testFileHam.length; i++){
                data.add(new TestFile(testFileHam[i].getFilename(), testFileHam[i].getSpamProbability(), testFileHam[i].getActualClass()));
            }
            for(int i = 0; i < testFileSpam.length; i++){
                data.add(new TestFile(testFileSpam[i].getFilename(), testFileSpam[i].getSpamProbability(), testFileSpam[i].getActualClass()));
            }

            //Sets up table
            table = new TableView<>();
            table.setItems(data);
            table.setEditable(true);

            // Sets up the file name column
            TableColumn<TestFile,String> fileNameColumn = null;
            fileNameColumn = new TableColumn<>("FileName");
            fileNameColumn.setMinWidth(300);
            fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));

            // Sets up the probability column
            TableColumn<TestFile,String> probColumn = null;
            probColumn = new TableColumn<>("Spam Probability");
            probColumn.setMinWidth(130);
            probColumn.setCellValueFactory(new PropertyValueFactory<>("SpamProbRounded"));

            // Sets up the class type column
            TableColumn<TestFile,String> classColumn = null;
            classColumn = new TableColumn<>("Actual Class");
            classColumn.setMinWidth(100);
            classColumn.setCellValueFactory(new PropertyValueFactory<>("actualClass"));

            // Adds columns to table
            table.getColumns().add(fileNameColumn);
            table.getColumns().add(probColumn);
            table.getColumns().add(classColumn);

            // Makes GridPane
            GridPane editArea = new GridPane();
            editArea.setPadding(new Insets(10, 10, 10, 10));
            editArea.setVgap(10);
            editArea.setHgap(10);

            // Makes accuracy text box
            Label accuracyLabel = new Label("Accuracy: ");
            editArea.add(accuracyLabel, 0, 0);
            TextField accuracyField = new TextField();
            accuracyField.setEditable(false);
            accuracyField.setText(Double.toString(getPercentAccuracy(testFileHam,testFileHam)));
            editArea.add(accuracyField, 1, 0);

            // Makes precision text box
            Label precisionLabel = new Label("Accuracy: ");
            editArea.add(precisionLabel, 2, 0);
            TextField precisionField = new TextField();
            precisionField.setEditable(false);
            precisionField.setText(Double.toString(getPrecision(testFileHam,testFileHam)));
            editArea.add(precisionField, 4, 0);

            // Sets layout
            layout = new BorderPane();
            layout.setCenter(table);
            layout.setBottom(editArea);


            // Sets scene and stage
            Scene scene = new Scene(layout, 700, 600);
            primaryStage.setScene(scene);
            primaryStage.show();

        }catch(NullPointerException e){// Catches error from not file selected

        }
    }

    public boolean checkData(File file) throws IOException{ // Check if necessary files can be found in a sub folder
        boolean fileFindFlag[][] = {
            {false, false, false},
            {false, false, false}
        }; // If any flags are set of user will not be able to continue since it can't find the necessary files

        // Uses findFile to pull the file if it comes back null then file was not found and the flag will not be switched
        File temp;
        temp = findFile(file, "train");
        if (temp != null){
            fileFindFlag[0][0] = true;
            System.out.println("Success!! Train file found!!");
            if (findFile(temp, "ham") != null) {
                fileFindFlag[0][1] = true;
                System.out.println("Success!! Ham file found!!");
            }
            if (findFile(temp, "spam") != null) {
                fileFindFlag[0][2] = true;
                System.out.println("Success!! Spam file found!!");
            }
        }
        temp = findFile(file, "test");
        if (temp != null){
            fileFindFlag[1][0] = true;
            System.out.println("Success!! Test file found!!");
            if (findFile(temp, "ham") != null) {
                fileFindFlag[1][1] = true;
                System.out.println("Success!! Ham file found!!");
            }
            if (findFile(temp, "spam") != null) {
                fileFindFlag[1][2] = true;
                System.out.println("Success!! Spam file found!!");
            }
        }
        for (int i = 0; i < 2;i++){
            for (int j = 0; j < 2;j++){
                if (fileFindFlag[i][j] == false){
                    System.out.println("Not all files found...");
                    return false; // User cannot continue, not all files were found
                }
            }
        }
        System.out.println("Files found.");
        return true; // User can continue, all file were found
    }

    public File findFile(File file, String fileName) throws IOException{
        if (file.isDirectory()) {
            // Recursively sorts throw files to find a file matching the filename given.
            File temp;
            File[] filesInDir = file.listFiles();
            for (int i = 0; i < filesInDir.length; i++) {
                if (filesInDir[i].getName().equalsIgnoreCase(fileName)){
                    return filesInDir[i];// When found returns file
                }else{
                     temp = findFile(filesInDir[i], fileName);
                     if (temp != null){ // If file if found
                         return temp;
                     }
                }
            }
        }
        return null; // If no results returns null
    }

    public void naiveBayes(File file, Map<String,Double> temp,TestFile[] testFileTemp,int i, String classType) throws IOException {
        // Calculates the second part of Naives Bayes formula recursively
        double n = 1.0f;
        if (file.isDirectory()) {
            File[] filesInDir = file.listFiles();
            for (int j = 0; j < filesInDir.length; j++) {
                if (filesInDir[j].exists() && !filesInDir[j].isDirectory()) { // If true adds +1 to i and continues
                    i++;
                    naiveBayes(filesInDir[j], temp, testFileTemp, i, classType);
                }else { // Else just continues
                    naiveBayes(filesInDir[j], temp, testFileTemp, i, classType);
                }
            }
        }else if (file.exists()){
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String word = scanner.next();
                if (isWord(word) && temp.containsKey(word)) {
                    n += (Math.log((1.0f-temp.get(word))) - Math.log(temp.get(word))); // Calculation for n
                }
            }
            double PrSF = 1.0f/(1.0f+Math.pow(Math.E,n)); // Calculation for Pr(S/F)
            testFileTemp[i] = new TestFile(file.getName(),PrSF,classType);
        }

    }

    private boolean isWord(String str){ // Check if string is a word
        String pattern = "^[a-zA-Z]*$";
        if (str.matches(pattern)){
            return true;
        }
        return false;
    }

    public double getPercentAccuracy(TestFile[] ham, TestFile[] spam){ // Calculates Percent Accuracy
        double percent;
        int hamCount=0;
        int spamCount=0;
        for(int i = 0; i < ham.length; i++){
            percent = Double.parseDouble(ham[i].getSpamProbRounded());
            if (percent < 1){
                hamCount++;
            }
        }
        for(int i = 0; i < spam.length; i++){
            percent = Double.parseDouble(spam[i].getSpamProbRounded());
            if (percent > 99){
                spamCount++;
            }
        }
        double count = spamCount+hamCount;
        double total = spam.length + ham.length;
        return (count/ total);
    }

    public double getPrecision(TestFile[] ham, TestFile[] spam){ // Calculates Precision
        double percent;
        double hamAverage=0;
        double spamAverage=0;
        for(int i = 0; i < ham.length; i++){
            percent = Double.parseDouble(ham[i].getSpamProbRounded());
            hamAverage += (percent);
        }
        for(int i = 0; i < spam.length; i++){
            percent = Double.parseDouble(spam[i].getSpamProbRounded());
            spamAverage += (percent);
        }

        double total = (hamAverage/ham.length) + (spamAverage/spam.length);
        return total;
    }

    final ObservableList<TestFile> data = FXCollections.observableArrayList( // Data list for the table
    );

    public static void main(String[] args) {
        launch(args);
    }
}
