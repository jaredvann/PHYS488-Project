// Import statements
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

class Analyse {
    private static Config config;
    private static Simulation simulation;

    private static String step_var;
    private static int step_count;
    private static double step_size;

    private static int sample_size;

    public static void main(String[] args) throws IOException {
        config = new Config("config.properties");

        step_var = config.get("step_var");
        step_count = config.getInt("step_count");
        step_size  = config.getDouble("step_size");
        sample_size = config.getInt("sample_size");

        simulation = new Simulation();

        // Sets the output filename to the current time and date so data is
        // not overwritten
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String filename = "./data/" + sdf.format(new Date()) + ".csv";

        // Add the general parameters of the simulation to the top of the output
        String header =
                "num_particles," + simulation.num_particles + "\n" +
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

        double sum, value, estCount;

        System.out.println("Started simulations");

        // Loop through each step
        for (int i = 0; i < step_count; i++) {
            System.out.println("Running simulation " + (i+1) + "/" + step_count);

            estCount = 0;

            for (double[] row : simulation.run_simulation()) {
                if (row[4] >= simulation.momentum_limit) {
                    estCount += 1;
                }
            }

            output[i] = new double[]{
                getVar(),
                estCount*100 / simulation.num_particles,
                0
            };

            // Increment the value of the changing parameter for the next iteration
            updateVar(getVar() + step_size);
        }

        System.out.println("Simulations and analysis finished");

        return output;
    }

    private static double getVar() {
        switch(step_var) {
            case "momentum":
                return simulation.momentum;
            case "limit":
                return simulation.momentum_limit;
            case "field":
                return simulation.mag_field;
            case "radiusA":
            case "position":
                return simulation.trigger_radius_A;
            case "radiusB":
                return simulation.trigger_radius_B;
            case "resolution":
                return simulation.trigger_resolution;
            case "thickness":
                return simulation.trigger_thickness;
        }

        return 0;
    }

    private static void updateVar(double val) {
        switch(step_var) {
            case "momentum":
                simulation.momentum = val;
                break;
            case "limit":
                simulation.momentum_limit = val;
                break;
            case "field":
                simulation.updateFieldLayers(val);
                break;
            case "radiusA":
                simulation.updateTriggerRadiusA(val);
                break;
            case "radiusB":
                simulation.updateTriggerRadiusB(val);
                break;
            case "position":
                simulation.updateTriggerRadius(val);
                break;
            case "resolution":
                simulation.trigger_resolution = val;
                break;
            case "thickness":
                simulation.updateTriggerThickness(val);
                break;
        }
    }

    private static double stderr(double[] values, double mean) {
        double sum = 0;
        for (double val : values)
            sum += (val - mean) * (val - mean);

        return (Math.sqrt(sum) / values.length);
    }
}
