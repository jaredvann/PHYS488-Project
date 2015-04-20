// Import statements
import java.util.ArrayList;
import java.io.IOException;

class Analyse {
    private static final int SAMPLE_SIZE = 3;

    private static Config config;

    private static double min;
    private static int stepCount;
    private static double stepSize;

    public static void main(String[] args) throws IOException {
        config = new Config("config.properties");

        stepCount = 5;
        stepSize  = 0.5;

        Helpers.write_to_disk("./analysis.csv", run());
    }

    public static double[][] run() throws IOException {
        double[][] out = new double[stepCount][];

        Simulation sim;
        double eff = 0;
        for (int i = 0; i < stepCount; i++) {
            sim = new Simulation(config);

            for (int j = 0; j < SAMPLE_SIZE; j++)
                eff += analyse(sim.simulate());

            out[i] = new double[3];
            out[i][0] = i+1;
            out[i][1] = config.magField;
            out[i][2] = eff / SAMPLE_SIZE;

            eff = 0;
            config.magField += stepSize;

            System.out.println("[*] " + ((i+1)*100 / stepCount) + "%");
        }

        return out;
    }

    public static double analyse() throws IOException {
        ArrayList<double[]> csv = Helpers.read_CSV("./data.csv");
        return analyse(csv.toArray(new double[csv.size()][]));
    }

    public static double analyse(double[][] data) throws IOException {
        double count = 0;
        double estCount = 0;
        double mom = config.momentumLimit;

        for (double[] row : data) {
            if (row[2] >= mom)
                count += 1;

            if (row[4] >= mom)
                estCount += 1;
        }

        return estCount*100 / count;
    }
}
