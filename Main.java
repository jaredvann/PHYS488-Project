import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Main {

    static Conf conf;

    public static void main(String[] args) throws IOException {
        PrintWriter screen = new PrintWriter(System.out, true);

        conf = load_conf("config.properties");

        ArrayList<Layer> layers = fill_layer_gaps(generate_layers());

        Simulation sim = new Simulation(conf, layers);


        System.out.println("Simulation started.");

        double[][] properties = sim.run();
        Helpers.write_to_disk("data.csv", properties);

        System.out.println("Simulation completed.");


        String left_align_format = "| %-5d | %-4.0f | %-16.2f | %-14.2f | %-10.2f | %-5.1f%% |%n";

        screen.format("+-------+------+------------------+----------------+------------+--------+%n");
        screen.format("| ID    | Mass | Initial Momentum | Final Momentum | Estimation | QOP    |%n");
        screen.format("+-------+------+------------------+----------------+------------+--------+%n");

        for (int i = 0; i < properties.length; i++) {
            screen.format(
                    left_align_format,
                    (i+1),
                    properties[i][1],
                    properties[i][2],
                    properties[i][3],
                    properties[i][4],
                    (properties[i][4] * 100 / properties[i][3])
            );
        }

        screen.format("+-------+------+------------------+----------------+------------+--------+%n");

        int count = 0;
        int estCount = 0;
        for (double[] p : properties) {
            if (p[3] >= conf.momentum_limit)
                count += 1;

            if (p[4] >= conf.momentum_limit)
                estCount += 1;
        }

        screen.println("\n[*] Actual Count:    " + count);
        screen.println("[*] Estimated Count: " + estCount);
        screen.println("[*] Abs Efficiency: " + (estCount*100 / conf.num_particles));
        screen.println("[*] WRT Efficiency:      " + ((count == 0) ? 0 : (estCount*100/count)) + "%\n");
    }


    private static ArrayList<Layer> generate_layers() {
        ArrayList<Layer> layers = new ArrayList<Layer>();

        // Silicon detector (sd) radii and thickness of layers
        double[] sd_radius = { 0.045, 0.08, 0.12, 0.18, 0.3, 0.4, 0.5, 0.7 }; // m
        double sd_thickness = 0.0005; // m

        int stepCount = 30;

        // Initialise beryllium/silicon attenuators
        Attenuator beryllium_attn = new Attenuator(4, 9.0121831, 1.85, (0.003 / stepCount));
        Attenuator silicon_attn = new Attenuator(14, 28.0855, 2.3290, (sd_thickness / stepCount));

        // Add beryllium beam pipe
        layers.add(
                new AttenuatorLayer(
                        0.035, // Start radius (m)
                        0.037, // End radius (m)
                        beryllium_attn // Attenuator
                )
        );

        // Add silicon detectors
        for (double r : sd_radius) {
            layers.add(
                    new DetectorLayer(
                            r,
                            (r + sd_thickness),
                            silicon_attn
                    )
            );
        }

        // Add coincidence detectors
        layers.add(
                new DetectorLayer(
                        conf.trigger_radius_A,
                        (conf.trigger_radius_A + conf.trigger_thickness),
                        silicon_attn
                )
        );

        layers.add(
                new DetectorLayer(
                        conf.trigger_radius_B,
                        (conf.trigger_radius_B + conf.trigger_thickness),
                        silicon_attn
                )
        );

        return layers;
    }


    private static ArrayList<Layer> fill_layer_gaps(ArrayList<Layer> layers) {
        layers.sort((l1, l2) -> (new Double(l1.start)).compareTo(l2.start));

        // Add vacuum layers to fill in gaps between physical layers
        ArrayList<Layer> layers2 = new ArrayList<Layer>();

        double last = 0.0;
        for (Layer layer : layers) {
            layers2.add(
                    new FieldLayer(
                            last,
                            layer.start,
                            conf.mag_field
                    )
            );

            last = layer.end;
        }

        // Add vacuum (field) layers to the rest of the layers
        layers.addAll(layers2);
        // One last check that every layer is in order
        layers.sort((l1, l2) -> (new Double(l1.start)).compareTo(l2.start));

        return layers;
    }


    private static Conf load_conf(String path) throws IOException {
        Conf conf = new Conf();
        Config config = new Config(path);

        conf.num_particles = config.getInt("num_particles");
        conf.particle_mass = config.getDouble("particle_mass");
        conf.mag_field     = config.getDouble("mag_field");

        conf.momentum       = config.getDouble("momentum");
        conf.momentum_smear = config.getDouble("momentum_smear");
        conf.momentum_limit = config.getDouble("momentum_limit");

        conf.trigger_radius_A   = config.getDouble("trigger_radius_A");
        conf.trigger_radius_B   = config.getDouble("trigger_radius_B");
        conf.trigger_thickness  = config.getDouble("trigger_thickness");
        conf.trigger_resolution = config.getDouble("trigger_resolution");

        return conf;
    }
}
