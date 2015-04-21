// Import statements
import java.util.Date;
import java.util.ArrayList;
import java.io.IOException;
import java.text.SimpleDateFormat;

class Analyse {
    private static Config config;
    private static Simulation simulation;

    private static double min;
    private static int sample_size;
    private static int step_count;
    private static double step_size;

    public static void main(String[] args) throws IOException {
        config = new Config("config.properties");

        sample_size = config.getInt("sample_size");
        step_count = config.getInt("step_count");
        step_size  = config.getDouble("step_size");

        simulation = new Simulation();

        // Sets the output filename to the current time and date so data is
        // not overwritten
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String filename = "./data/" + sdf.format(new Date()) + ".csv";

        // Add the general parameters of the simulation to the top of the output
        String header = "num_particles," + simulation.num_particles + "\n" +
        "particle_mass," + simulation.particle_mass + "\n" +
        "mag_field," + simulation.mag_field + "\n" +
        "momentum," + simulation.momentum + "\n" +
        "momentum_smear," + simulation.momentum_smear + "\n" +
        "momentum_limit," + simulation.momentum_limit + "\n" +
        "trigger_radius_A," + simulation.trigger_radius_A + "\n" +
        "trigger_radius_B," + simulation.trigger_radius_B + "\n" +
        "trigger_thickness," + simulation.trigger_thickness + "\n" +
        "trigger_resolution," + simulation.trigger_resolution + "\n";

        // Actually run all the simulations
        double[][] simulation_output = run_simulations();

        Helpers.write_to_disk(filename, simulation_output, header);
    }

    // CHANGE THE VARIABLES MARKED BELOW TO THE VARIABLE YOU WANT TO CHANGE
    public static double[][] run_simulations() throws IOException {
        // Create array for output data
        double[][] output = new double[step_count][];

        double sum, value;

        System.out.println("Started simulations");

        // Loop through each step
        for (int i = 0; i < step_count; i++) {
            // Initialise sub-array to hold sample results
            output[i] = new double[(2+sample_size)];

            // Set the first value to the variating config value
            //                        vvvvv -- CHANGE THIS VARIABLE
            output[i][0] = simulation.momentum_limit;

            sum = 0;

            // Run samples
            for (int j = 0; j < sample_size; j++) {
                System.out.println("Running simulation " + (i*sample_size + j+1) + "/" + step_count*sample_size);

                value = analyse(simulation.run_simulation());
                output[i][(1+j)] = value;
                sum += value;
            }

            // Add average value of all samples to end of line
            output[i][sample_size+1] = sum / sample_size;

            // Increment the value of the changing parameter for the next iteration
            //         vvvvv -- CHANGE THIS VARIABLE
            simulation.momentum_limit += step_size;
        }

        System.out.println("Simulations and analysis finished");

        return output;
    }

    public static double analyse() throws IOException {
        ArrayList<double[]> csv = Helpers.read_CSV("./data.csv");
        return analyse(csv.toArray(new double[csv.size()][]));
    }

    public static double analyse(double[][] data) throws IOException {
        double count = 0;
        double estCount = 0;
        double mom = simulation.momentum_limit;

        for (double[] row : data) {
            if (row[2] >= mom) {
                count += 1;
            }

            if (row[4] >= mom) {
                estCount += 1;
            }
        }

        return estCount*100 / count;
    }
}
