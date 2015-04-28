package histogram;

// Import statements

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Histogram {
    private HistogramStorage store;

    public Histogram(double min, double max, int size) {
        if (size > 0)
            this.store = new StaticStorage(size, min, max);
        else
            this.store = new AdaptiveStorage(min, max);
    }

    public Histogram(double min, double max) { this(min, max, 0); }
    public Histogram(int size) { this(-Double.MAX_VALUE, Double.MAX_VALUE, size); }
    public Histogram() { this(0); }

    public void reset() { store.reset(); }

    public void add(double value) { store.add(value); }

    public int getTotal() { return store.getTotal(); }

    public int getCount(double value) { return store.getCount(value); }
    public int getAccumCount(double value) { return store.getAccumCount(value); }
    public int getPercentile(double value) { return store.getPercentile(value); }

    public double getDensity(double value) { return store.getDensity(value); }
    public double getValueAtPercentile(int perc) { return store.getValueAtPercentile(perc); }

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

    public String toCsv() { return store.toCsv(); }
    public int[] toArray() { return store.toArray(); }
    public String toPrettyString() { return store.toPrettyString(); }
}
