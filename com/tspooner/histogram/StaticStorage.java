package com.tspooner.histogram;

import java.text.DecimalFormat;

public class StaticStorage extends HistogramStorage {
    private int size; // Number of bins
    private int[] data; // Bin counts

    private double binLow; // Lower bound for bins
    private double binHigh; // Upper bound for bins
    private double binWidth; // Width of a single bin

    private int overflows; // Number of hits above upper bound
    private int underflows; // Number of hits below lower bound

    public StaticStorage(int size, double min, double max) {
        this.size = size;

        reset();

        this.binLow = min;
        this.binHigh = max;
        this.binWidth = (max - min) / size;

        this.overflows = 0;
        this.underflows = 0;
    }

    public void reset() { this.data = new int[size]; }

    public void add(double value) {
        if (value > binHigh || value < binLow) {
			if (value > binHigh) overflows++; // Number is too large
			if (value < binLow) underflows++; // Number is too small
		} else {
			data[getOffset(value)]++;
		}
    }

    public int getCount(double value) { return data[getOffset(value)]; }
    
    public int getCount(int index) {
        if (index < data.length)
            return data[index];
        else
            return 0;
    }

    public int getAccumCount(double value) {
        int sum = 0;
        int offset = getOffset(value);

        for (int i = 0; i < data.length; i++) {
            sum += data[i];
            if (i == offset) break;
        }

        return sum;
    }

    public double getDensity(double value) {
        return data[getOffset(value)] / (getTotal() * binWidth);
    }

    public double getValueAtPercentile(int perc) {
        int pSum = getTotal() * perc / 100;
        int cSum = 0;
        int offset = size;

        for (int i = 0; i < data.length; i++) {
            cSum += data[i];
            if (cSum >= pSum) {
                offset = i;
                break;
            }
        }

        return (binWidth*(offset + 0.5));
    }

    private int getOffset(double value) {
        return (int) ((value - binLow) / binWidth);
    }

    public String toCsv() {
        String csv = "bin_centre,bin_width,bin_count,bin_density,bin_unit_density\n";

		for(int i = 0; i < data.length; i++)
			csv += (binWidth*(i+0.5)) + "," +
                   binWidth + "," +
                   data[i] + "," +
                   getDensity(data[i]) + "\n";

		return csv;
	}

    public int[] toArray() { return this.data; }

    public String toPrettyString() {
        StringBuilder sb = new StringBuilder();

		String sep = System.getProperty("line.separator");
		DecimalFormat df = new DecimalFormat("#0.00");
		DecimalFormat dfMore = new DecimalFormat("#0.0000");

        sb.append(sep);

		// Append data array
		double centre;
		for (int i = 0; i < size; i++) {
			centre = binLow + i*binWidth + binWidth/2;
			sb.append(
				"Bin " + (i+1) + ": " + data[i] +
				"\t[\u03c3 = " + df.format(error(data[i])) + "]" +
				"\t(" + dfMore.format(centre) + ")" + sep
			); // U+03C3 is a sigma character
		}

		// Append some other useful info
		sb.append(sep).append("Bin width:\t").append(binWidth);
		sb.append(sep).append("Upper bound:\t").append(binHigh);
		sb.append(sep).append("Lower bound:\t").append(binLow);

		sb.append(sep);

		sb.append(sep).append("Underflows:\t").append(underflows);
		sb.append(sep).append("Overflows:\t").append(overflows);

		return sb.toString();
	}
}
