//William Rory Chisholm - 100560820 - March/7/2017
//Assignment 01 - TestFile Class
package sample;

import java.text.DecimalFormat;

public class TestFile { // TestFile Class pulled from the assignment
    private String filename;
    private double spamProbability;
    private String actualClass;

    // Constructor
    public TestFile(String filename, double spamProbability, String actualClass) {
        this.filename = filename;
        this.spamProbability = spamProbability;
        this.actualClass = actualClass;
    }

    // Gets & Sets
    public String getFilename() { return this.filename; }
    public double getSpamProbability() { return this.spamProbability; }
    public String getSpamProbRounded() {
        DecimalFormat df = new DecimalFormat("0.00000");
        return df.format(this.spamProbability);
    }

    public String getActualClass() { return this.actualClass; }
    public void setFilename(String value) { this.filename = value; }
    public void setSpamProbability(double val ) { this.spamProbability = val ; }
    public void setActualClass(String value) { this.actualClass = value; }
}
