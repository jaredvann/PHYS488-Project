package histogram;

// Import statements

import java.text.DecimalFormat;
import java.util.List;

public class AdaptiveStorage extends HistogramStorage {
    private Tree<Bin> tree;

    public AdaptiveStorage(double min, double max) {
        tree = new Tree<Bin>(new Bin(0, min, max));
    }

    public AdaptiveStorage() { tree = new Tree<Bin>(new Bin()); }

    public void reset() {
        Bin old = tree.getValue();

        tree.reset(); old.reset();

        tree.setValue(old);
    }

    public void add(double value) {
        List<Tree<Bin>> fringe = tree.fringe();
        boolean counted = false;

        for (Tree<Bin> leaf : fringe) {
            if (counted) return;

            Bin bin = leaf.getValue();

            if (bin.contains(value)) {
                counted = true;

                total++;
                bin.increment();

                if (bin.count >= getSplitLimit() && bin.min != bin.max) {
                    List<Bin> newBins;

                    if (value > bin.getCentre())
                        newBins = bin.split();
                    else
                        newBins = bin.split();

                    leaf.setLeft(new Tree<Bin>(newBins.get(0)));
                    leaf.setRight(new Tree<Bin>(newBins.get(1)));

                    if (bin.count % 2 != 0) {
                        if (leaf.getLeft().getValue().contains(value))
                            leaf.getLeft().getValue().increment();
                        else
                            leaf.getRight().getValue().increment();
                    }
                }
            }
        }

        Bin toExtend;
        if (!counted) {
            if (fringe.get(0).getValue().min > value)
                toExtend = fringe.get(0).getValue();
            else
                toExtend = fringe.get((fringe.size()-1)).getValue();

            toExtend.extend(value);
            toExtend.increment();
        }
    }

    public int getCount(double value) {
        Bin bin = getBin(value);
        return (null != bin) ? bin.count : 0;
    }

    public int getAccumCount(double value) {
        int sum = 0;
        for (Tree<Bin> leaf : tree) {
            sum += leaf.getValue().count;
            if (leaf.getValue().contains(value)) break;
        }

        return sum;
    }

    public double getDensity(double value) {
        Bin bin = getBin(value);

        return (null != bin) ? bin.getDensity(getTotal()) : 0;
    }

    public double getValueAtPercentile(int perc) {
        int pSum = (int) getTotal(tree) * perc / 100;
        int cSum = 0;
        Bin bin = null;

        for (Tree<Bin> leaf : tree) {
            cSum += leaf.getValue().count;
            if (cSum >= pSum) {
                bin = leaf.getValue();
                break;
            }
        }

        return (null != bin) ? bin.getCentre() : 100;
    }

    private int getTotal(Tree<Bin> node) {
        int sum = 0;
        for (Tree<Bin> leaf : node.fringe())
            sum += leaf.getValue().count;

        return sum;
    }

    private Bin getBin(double value) {
        for (Tree<Bin> leaf : tree)
            if (leaf.getValue().contains(value)) return leaf.getValue();

        return null;
    }

    private int getSplitLimit() {
        return (total == 1) ? 1 : total / 10;
    }

    // Export methods
    public String toCsv() {
        String csv = "bin_centre,bin_width,bin_count,bin_unit_density\n";

        for (Tree<Bin> f : tree) {
            Bin bin = f.getValue();
            csv += bin.getCentre() + "," +
                   bin.getWidth() + "," +
                   bin.count + "," +
                   bin.getDensity(total) + "\n";
        }

        return csv;
    }

    public int[] toArray() {
        List<Tree<Bin>> fringe = tree.fringe();
        int[] arr = new int[fringe.size()];

        for (int i = 0; i < arr.length; i++)
            arr[i] = (int) fringe.get(i).getValue().getDensity(total);

        return arr;
    }

    public String toPrettyString() {
        StringBuilder sb = new StringBuilder();

		String sep = System.getProperty("line.separator");
		DecimalFormat df = new DecimalFormat("#0.00");
		DecimalFormat dfMore = new DecimalFormat("#0.0000");

        sb.append(sep);

		// Add all the leaf nodes
        for (Tree<Bin> node : tree.fringe()) {
            Bin bin = node.getValue();

			sb.append(
				"Bin @ " + dfMore.format(bin.getCentre()) + ": " + bin.getCount() +
				"\t[\u03c3 = " + df.format(error(bin.getCount())) + "]" +
				"\t(" + dfMore.format(bin.max - bin.min) + ")" + sep
			); // U+03C3 is a sigma character
		}

        return sb.toString();
    }
}
