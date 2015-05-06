// Import statements

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class Simulation {
    private Random random;

    public ArrayList<DetectorLayer> detector_layers;
    public ArrayList<Layer> layers;

    public int num_particles;
    public double particle_mass;

    public double mag_field;

    public double momentum;
    public double momentum_smear;
    public double momentum_limit;

    public double trigger_radius_A;
    public double trigger_radius_B;
    public double trigger_thickness;
    public double trigger_resolution;

    public Simulation() throws IOException {
        random = new Random();

        // Load all needed configuration values
        Config config = new Config("config.properties");

        num_particles = config.getInt("num_particles");
        particle_mass = config.getDouble("particle_mass");
        mag_field     = config.getDouble("mag_field");

        momentum       = config.getDouble("momentum");
        momentum_smear = config.getDouble("momentum_smear");
        momentum_limit = config.getDouble("momentum_limit");

        trigger_radius_A   = config.getDouble("trigger_radius_A");
        trigger_radius_B   = config.getDouble("trigger_radius_B");
        trigger_thickness  = config.getDouble("trigger_thickness");
        trigger_resolution = config.getDouble("trigger_resolution");

        // Initialize layer arrays
        detector_layers = new ArrayList<DetectorLayer>();
        layers          = new ArrayList<Layer>();

        generateLayers();
    }

    // This method is called if the Simulation file is run directly
    // (eg. not from the Analyse class)
    public static void main(String[] args) throws IOException {
        PrintWriter screen = new PrintWriter(System.out, true);
        Simulation simulation = new Simulation();

        System.out.println("Simulation started.");

        double[][] properties = simulation.run_simulation();

        Helpers.write_to_disk("data.csv", properties);

        simulation.exportViewerData();

        System.out.println("Simulation completed.");

        // Debugging
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
            if (p[3] >= simulation.momentum_limit)
                count += 1;

            if (p[4] >= simulation.momentum_limit)
                estCount += 1;
        }

        screen.println("\n[*] Actual Count:    " + count);
        screen.println("[*] Estimated Count: " + estCount);
        screen.println("[*] Abs Efficiency: " + (estCount / simulation.num_particles));
        screen.println("[*] WRT Efficiency:      " + ((count == 0) ? 0 : (estCount*100/count)) + "%\n");
    }

    // This is where the magic happens!
    public double[][] run_simulation() {
        double[][] properties = new double[num_particles][];
        double estimation;

        for (int i = 0; i < num_particles; i++) {
            Particle particle = newParticle();
            properties[i] = new double[5];

            // particle.handle returns True if particle reaches edge of detector
            boolean continue_ = particle.handle(layers);

            // Use the --last two-- detectors as the track trigger.
            if (particle.momentum > 0 && continue_) {
                estimation = estimateMomentum(particle);
            } else {
                estimation = 0;
            }

            // Add the information we need to an array and store it for analysis
            properties[i] = new double[]{
                    i+1,
                    particle.mass,
                    particle.original_momentum,
                    particle.momentum,
                    estimation
            };
        }

        return properties;
    }

    // This method replaces the particle factory class as it can all fit in
    // this one method
    // Gives a linear spread of angles and a Gaussian spread of momentums
    public Particle newParticle() {
        return new Particle(
                // Mass
                particle_mass,
                // Momentum Smear
                Math.abs(Helpers.gauss(
                        momentum,
                        momentum * momentum_smear
                )),
                // Direction
                random.nextDouble()*(2*Math.PI),
                // Azimuth
                0
        );
    }

    public double estimateMomentum(Particle p) {
        // Get Coincidence Detector properties
        double range = trigger_radius_B - (trigger_radius_A + trigger_thickness);

        // Get angles at the two coincidence detectors
        // --- For reference: see final page of project handout
        //                    these are the angles phi_9A and phi_9B
        // --- This kind of simulates the resolution of the detectors?
        double angle_a = get_detector_angle(p.getTraceAt(trigger_radius_A + trigger_thickness));
        double angle_b = get_detector_angle(p.getTraceAt(trigger_radius_B));

        // Estimate particle momentum (*1000 to convert GeV -> MeV)
        double delta =
                Math.abs(Math.atan(trigger_radius_B*(angle_b - angle_a)/range));

        return 1000 * 0.3 * mag_field * trigger_radius_B / (2*delta);
    }

    public void updateFieldLayers(double field) {
        mag_field = field;

        for (Layer l : layers)
            if (l instanceof FieldLayer) {
                FieldLayer fl = (FieldLayer) l;
                fl.field = field;
            }
    }

    public void updateTriggerThickness(double thick) {
        Layer a = layers.get(layers.size()-3);
        Layer b = layers.get(layers.size()-1);

        trigger_thickness = thick;
        a.end = a.start + thick;
        b.end = b.start + thick;
    }

    public void updateTriggerRadiusA(double radius) {
        Layer fl1 = layers.get(layers.size() - 4);
        Layer l   = layers.get(layers.size()-3);
        Layer fl2 = layers.get(layers.size() - 2);

        trigger_radius_A = radius;

        fl1.end = l.start = radius;
        l.end = fl2.start = radius + trigger_thickness;
    }

    public void updateTriggerRadiusB(double radius) {
        Layer fl = layers.get(layers.size() - 2);
        Layer l  = layers.get(layers.size() - 1);

        trigger_radius_B = radius;

        l.start = fl.end = radius;
        l.end = radius + trigger_thickness;
    }

    public void updateTriggerRadius(double radius) {
        double diff = trigger_radius_B - trigger_radius_A;

        updateTriggerRadiusA(radius);
        updateTriggerRadiusB(radius + diff);
    }

    // Smears the angle using a gaussian distribution
    private double get_detector_angle(double angle) {
        // trigger_resolution == 'Bin Size'
        // return Math.round(angle/trigger_resolution) * (trigger_resolution);
        return Helpers.gauss(angle, trigger_resolution);
    }

    // Sets up all the physical detector layers and the empty gaps in between
    private void generateLayers() {
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
            detector_layers.add(
                    new DetectorLayer(
                            r,
                            (r + sd_thickness),
                            silicon_attn
                    )
            );
        }

        // Add coincidence detectors
        detector_layers.add(
                new DetectorLayer(
                        trigger_radius_A,
                        (trigger_radius_A + trigger_thickness),
                        silicon_attn
                )
        );

        detector_layers.add(
                new DetectorLayer(
                        trigger_radius_B,
                        (trigger_radius_B + trigger_thickness),
                        silicon_attn
                )
        );

        // Add additional vacuum layers and sort into correct order
        fillLayerGaps();
    }

    // Fills the gaps between physical layers with a FieldLayer, to represent
    // the particle travelling through a magnetic field (in a vacuum).
    private void fillLayerGaps() {
        // Add detector layers
        layers.addAll(detector_layers);
        layers.sort((l1, l2) -> (new Double(l1.start)).compareTo(l2.start));

        // Add vacuum layers to fill in gaps between physical layers
        ArrayList<Layer> layers2 = new ArrayList<Layer>();

        double last = 0.0;
        for (Layer layer : layers) {
            layers2.add(
                    new FieldLayer(
                            last,
                            layer.start,
                            mag_field
                    )
            );

            last = layer.end;
        }

        // Add vacuum (field) layers to the rest of the layers
        layers.addAll(layers2);
        // One last check that every layer is in order
        layers.sort((l1, l2) -> (new Double(l1.start)).compareTo(l2.start));
    }

    // Exports data in the format used by the DetectorViewer visualisation
    private void exportViewerData() throws IOException {
        double[][] layers = new double[detector_layers.size()][];
        for (int i = 0; i < layers.length; i++) {
            layers[i] = new double[detector_layers.get(i).hits.size()];

            for (int j = 0; j < layers[i].length; j++) {
                layers[i][j] = detector_layers.get(i).hits.get(j);
            }
        }

        Helpers.write_to_disk("layers.csv", layers);
    }
}
