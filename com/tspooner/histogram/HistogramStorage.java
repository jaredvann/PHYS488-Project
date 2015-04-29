package com.tspooner.histogram;

import java.util.ArrayList;
import java.util.List;

abstract class HistogramStorage {
    protected int total = 0;

    // Base methods
    public abstract void reset();

    public abstract void add(double value);

    public int getTotal() { return total; }

    public abstract int getCount(double value);
    public abstract int getCount(int index);
    
    public abstract int getAccumCount(double value);
    public int getPercentile(double value) {
        return getAccumCount(value) / getTotal();
    }

    public abstract double getDensity(double value);
    public abstract double getValueAtPercentile(int perc);

    // Export methods
    public abstract String toCsv();
    public abstract int[] toArray();
    public abstract String toPrettyString();

    // Helper methods
    protected static double error(int count) {
        return Math.sqrt(count);
    }

    public class Bin {
        public int count;
        public double min;
        public double max;

        public Bin(int count, double min, double max) {
            this.count = count;
            this.min = min;
            this.max = max;
        }

        public Bin() { this(0, -Double.MAX_VALUE, Double.MAX_VALUE); }

        public void reset() { count = 0; }
        public void increment() { this.count++; }
        public void decrement() { this.count--; }

        public boolean contains(double value) {
            return (value >= min && value <= max);
        }

        public int getCount() { return this.count; }
        public double getCentre() { return (max + min) / 2; }

        public double getWidth() { return max - min; }
        public double getDensity(int total) { return count / (total * getWidth()); }

        public void extend(double extendTo) {
            if (extendTo > max) max = extendTo;
            else min = extendTo;
        }

        public List<Bin> split() {
            List<Bin> split = new ArrayList<Bin>();

            double splitAt = (this.min + this.max) / 2;
            int splitCount = (count / 2);

            split.add(new Bin(splitCount, min, splitAt));
            split.add(new Bin(splitCount, splitAt, max));

            return split;
        }
    }
}
