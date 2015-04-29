package com.tspooner.histogram;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A {@code Histogram} object that allows a client estimate the probability distribution
 * of a continuous variable. Adding values to the {@code Histogram} instance
 * will record the frequency of hits within certain ranges of values.
 *
 * <p>This class acts as a bridge for the different data storage implementations.
 * It contains methods for adding and resetting the histogram frequency data,
 * and some useful methods for basic statistical analysis.</p>
 *
 * <p>The majority of the methods in this class do nothing but relay the
 * commands to the appropriate HistogramStorage instance. They are there to
 * shield the user from the implementation of the data in the storage class.</p>
 *
 * @author Thomas Spooner
 * @version 0.0
 */
public class Histogram {
    /** {@code HistogramStorage} instance contains the data and strategy */
    private HistogramStorage store;

    /**
     * Constructs a newly allocated {@code Histogram} object by instantiating a
     * {@code HistogramStorage} instance for data manipulation. If {@code size} is
     * a non-zero integer, then a StaticStorage is used, otherwise the
     * {@code AdaptiveStorage} class is used.
     *
     * @param   min the lower limit on bin bounds.
     * @param   max the upper limit on bin bounds.
     * @param   size    the number of bins to use.
     */
    public Histogram(double min, double max, int size) {
        if (size > 0)
            this.store = new StaticStorage(size, min, max);
        else
            this.store = new AdaptiveStorage(min, max);
    }

    /**
     * Constructs a newly allocated {@code Histogram} object by instantiating an
     * {@code AdaptiveStorage} instance for data manipulation, between {@code min}
     * and {@code max}.
     *
     * @param   min the lower limit on bin bounds.
     * @param   max the upper limit on bin bounds.
     */
    public Histogram(double min, double max) { this(min, max, 0); }

    /**
     * Constructs a newly allocated {@code Histogram} object by instantiating a
     * {@code StaticStorage} instance for data manipulation. The
     * {@code Double.MAX_VALUE} constant is used for the bin bounds.
     *
     * @param   size    the number of bins to use.
     */
    public Histogram(int size) { this(-Double.MAX_VALUE, Double.MAX_VALUE, size); }

    /**
     * Constructs a newly allocated {@code Histogram} object by instantiating an
     * {@code AdaptiveStorage} instance for data manipulation. The
     * {@code Double.MAX_VALUE} constant is used for the bin bounds.
     */
    public Histogram() { this(0); }

    /**
     * Clears the histogram's data storage by resetting the count of each bin
     * to zero.
     */
    public void reset() { store.reset(); }

    /**
     * Finds the bin which contains {@code value} and increments the bin's count
     * by one.
     *
     * @param   value   the value to be added.
     */
    public void add(double value) { store.add(value); }

    /**
     * Returs the total number of counts from all the bins.
     *
     * @return The total number of counts.
     */
    public int getTotal() { return store.getTotal(); }

    /**
     * Returns the number of counts in the bin which contains {@code value}.
     *
     * @param   value  the number which is contained by the target bin's bounds.
     * @return  The selected bin's count.
     */
    public int getCount(double value) { return store.getCount(value); }

    /**
     * Returns the number of counts in the bin at index {@code i}.
     *
     * @param   i  the bin's index.
     * @return  The selected bin's count.
     */
    public int getCount(int i) { return store.getCount(i); }

    /**
     * Returns the cumulative count up to the bin which contains {@code value}.
     *
     * @param   value   the number which is contained by the target bin's bounds.
     * @return  The cumulative count total.
     */
    public int getAccumCount(double value) { return store.getAccumCount(value); }

    /**
     * Returns the percentile given by the bin which contains {@code value}.
     *
     * @param   value   the number which is contained by the target bin's bounds.
     * @return  The percentile of the selected bin.
     */
    public int getPercentile(double value) { return store.getPercentile(value); }

    /**
     * Returns the bin centre of the bin which corresponds to the percentile
     * {@code perc}.
     *
     * @param   perc    the requested percentile.
     * @return  The value given by the requested percentile.
     */
    public double getValueAtPercentile(int perc) { return store.getValueAtPercentile(perc); }

    /**
     * Returns the unit density of the bin which contains {@code value}.
     *
     * @param   value   the number which is contained by the target bin's bounds.
     * @return  The density of the selected bin.
     */
    public double getDensity(double value) { return store.getDensity(value); }

    /**
     * Write the histogram data to the disk in CSV format.
     *
     * @throws  IOException exceptions caused while trying to save the file.
     * @param   filepath    the filepath at which to save the data to.
     * @return  True if the file was written successfully.
     */
    public boolean writeToDisk(String filepath) throws IOException {
		FileWriter file;
		PrintWriter toFile = null;

		try {
			file = new FileWriter(filepath); // File stream
			toFile = new PrintWriter(file); // File writer
            toFile.print(store.toCsv());
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
     * Returns the histogram data as a string in csv format.
     *
     * @return  The CSV formatted string.
     */
    public String toCsv() { return store.toCsv(); }

    /**
     * Returns the histogram bin counts as an array of integers.
     *
     * @return The histogram counts.
     */
    public int[] toArray() { return store.toArray(); }

    /**
     * Returns a nicely formatted string of the histogram data, ready to be
     * printed to the console.
     *
     * @return  The nicely formatted histogram data.
     */
    public String toPrettyString() { return store.toPrettyString(); }
}
