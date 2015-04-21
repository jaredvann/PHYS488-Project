// Import statements
import java.util.ArrayList;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

class Analyse {
    private static Config config;

    private static double min;
    private static int sampleSize;
    private static int stepCount;
    private static double stepSize;

    public static void main(String[] args) throws IOException {
        config = new Config("config.properties");

        sampleSize = config.getInt("sample_size");
        stepCount = config.getInt("step_count");
        stepSize  = config.getDouble("step_size");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

        Helpers.write_to_disk(
            "./data/" + sdf.format(new Date()) + ".csv",
            run(),
            "num_particles," + config.numParticles + "\n" +
            "mass," + config.masses[0] + "\n" +
            "magField," + config.magField + "\n" +
            "momentum," + config.momentum + "\n" +
            "momentumSmear," + config.momentumSmear + "\n" +
            "momentumLimit," + config.momentumLimit + "\n" +
            "triggerRadiusA," + config.triggerRadiusA + "\n" +
            "triggerRadiusB," + config.triggerRadiusB + "\n" +
            "triggerThickness," + config.triggerThickness + "\n" +
            "triggerResolution," + config.triggerResolution + "\n"
        );
    }

    public static double[][] run() throws IOException {
        double[][] out = new double[stepCount][];

        Simulation sim;

        System.out.println("[*] 0%");

        int iter = 1;
        for (int i = 0; i < stepCount; i++) {
            sim = new Simulation(config);

            out[i] = new double[(1+sampleSize)];
            out[i][0] = config.momentumLimit;

            for (int j = 0; j < sampleSize; j++) {
                out[i][(1+j)] = analyse(sim.simulate());
                System.out.println("[*] " + ((iter)*100 / (sampleSize*stepCount)) + "%");
                iter += 1;
            }

            config.momentumLimit += stepSize;
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
