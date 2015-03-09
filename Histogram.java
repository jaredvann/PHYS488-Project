// Import statements
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Random;
import java.text.DecimalFormat;

 
/**
 * A Histogram object encapsulates the information needed to plot an arbitrary
 * distribution of numbers.
 *
 * @author      Thomas Spooner
 * @version     0.0.1
 */
public class Histogram {
 
    public String title; // Title of histogram
 
    protected int[] data; // Bin counts
    protected int size; // Number of bins
 
    protected double binLow; // Lower bound for bins
    protected double binHigh; // Upper bound for bins
    protected double binWidth; // Width of a single bin
 
    protected int underflows = 0; // Number of hits below lower bound
    protected int overflows = 0; // Number of hits above upper bound
 
 
    /**
     * Histogram constructor method; performs basic variable assignment.
     *
     * @param   t Title of histogram
     * @param   s Size of histogram (number of bins)
     * @param   low Lower bin bound
     * @param   high Upper bin bound
     * @return  Histogram
     */
    public Histogram(String t,
                     int s,
                     double low,
                     double high) {
        title = t;
 
        size = s;
        data = new int[s];
 
        binLow = low;
        binHigh = high;
        binWidth = binWidth(s, low, high);
    }
 
 
    /**
     * Adds a number to the histogram by sorting into the appropriate bin.
     *
     * @param x Number to add
     */
    public void add(double x) {
        if (x > binHigh || x < binLow) {
            if (x > binHigh) overflows++; // Number is too large
            if (x < binLow) underflows++; // Number is too small
        } else {
            data[(int) ((x - binLow) / binWidth)]++;
        }
    }
 
    /**
     * Adds an array of number to the histogram using the {@link #add} method.
     *
     * @param xs Array of numbers
     */
    public void add(double[] xs) {
        for (int i = 0; i < xs.length; i++)
            this.add(xs[i]);
    }
 
    public int[] getData() { return data; }
    public int getData(int bin) { return data[bin]; }
 
    public double getError(int bin) {
        return error(data[bin]);
    }
 
    public double[] getErrors() {
        double[] errors = new double[size];
 
        for (int i = 0; i < size; i++)
            errors[i] = getError(i);
 
        return errors;
    }
 
    public double getPercentageError(int bin) {
        return percentageError(data[bin]);
    }
 
    public double[] getPercentageErrors() {
        double[] errors = new double[size];
 
        for (int i = 0; i < size; i++)
            errors[i] = percentageError(data[i]);
 
        return errors;
    }
 
    public int getSize() { return size; }
    public void setSize(int newSize) {
        size = newSize;
        binWidth = binWidth(newSize, binLow, binHigh);
    }
 
    public int getUnderflows() { return underflows; }
    public int getOverflows() { return overflows; }
 
    /**
     * Write histogram data to disk using a comma delimited format.
     * Asks the user for a filename and does the entire operation.
     * It's an all-in-one method for this specific class.
     */
    public boolean writeToDisk(String filepath) throws IOException {
        FileWriter file;
        PrintWriter toFile = null;
 
        try {
            file = new FileWriter(filepath); // File stream
            toFile = new PrintWriter(file); // File writer
 
            toFile.println("title," + title);
 
            toFile.println("bin_size," + binWidth);
            toFile.println("bin_low," + binLow);
            toFile.println("bin_high," + binHigh);
            toFile.println("num_underflows," + underflows);
            toFile.println("num_overflows," + overflows);
            toFile.println(
                "\nbin,bin_centre,bin_count,bin_error,bin_error_percentage");
 
            double binCentre;
            for (int i = 0; i < size; i++) {
                binCentre = binLow + binWidth/2 + i*binWidth;
 
                toFile.println(
                    i + "," +
                    binCentre + "," +
                    data[i] + "," +
                    error(data[i]) + "," +
                    percentageError(data[i]));
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            if (toFile != null)
                toFile.close();
 
            return true;
        }
    }
 
    /**
     * Dumps the histogram data to the console. The bin counts and respective
     * errors are given. Some other useful information is also given.
     */
    @Override public String toString() {
        String csv = "";
        
        for(int i = 0; i < data.length; i++)
            csv = csv + data[i] + ",";
            
        return csv;
    }
    
    public String prettyPrint() {
        String sep = System.getProperty("line.separator");
        DecimalFormat df = new DecimalFormat("#0.00");
        DecimalFormat dfMore = new DecimalFormat("#0.0000");
 
        StringBuilder sb = new StringBuilder(sep + "Title: " + title + sep);
 
        sb.append(sep);
        
        // Append data array
        double centre;
        for (int i = 0; i < size; i++) {
            centre = binLow + i*binWidth + binWidth/2;
            sb.append(
                "Bin " + (i+1) + ": " + data[i] +
                "\t[\u03c3 = " + df.format(error(data[i])) +
                " || " + df.format(percentageError(data[i])) + "%]" +
                "\t(" + dfMore.format(centre) + ")" + sep
            ); // U+03C3 is a sigma character
        }
 
        // Append some other useful info
        sb.append(sep + "Bin width: " + binWidth);
        sb.append(sep + "Upper bound: " + binHigh);
        sb.append(sep + "Lower bound: " + binLow);
 
        sb.append(sep);
 
        sb.append(sep + "Underflows: " + underflows);
        sb.append(sep + "Overflows: " + overflows);
 
        return sb.toString();
    }
 
    /**
     * Calcualte the width of each bin given the number of bins in the
     * histogram, the upper bin bound and the lower bin bound.
     *
     * @param   Number of bins
     * @param   Lower bin bound
     * @param   Upper bin bound
     * @return  The bin width
     */
    private static double binWidth(int s,
                                   double low,
                                   double high) {
        return (high - low) / ((double) s);
    }
 
    /**
     * Calculate the statistical error on a number from a random distribution.
     *
     * @see java.lang.Math
     *
     * @param  num Number from which the error is calculated
     * @return     The statistical error on num, defaults to 0
     */
    protected static double error(int num)  {
        return (num == 0) ? 0 : Math.sqrt(num);
    }
 
    /**
     * Calculate the percentage error on a number from a random distribution.
     *
     * @see error
     *
     * @param  num Number from which the percentage error is calculated
     * @return     The percentage error on num, defaults to 0%
     */
    protected static double percentageError(int num) {
        return (num == 0) ? 0 : error(num)*100/num;
    }
}