// Import statements
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Let's get our simulation on!
 */
public class Simulation {
    private Random random;
    private Config config;

    public List<DetectorLayer> detector_layers;
    public List<Layer> layers;

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
        mag_field = config.getDouble("mag_field");

        momentum = config.getDouble("momentum");
        momentum_smear = config.getDouble("momentum_smear");
        momentum_limit = config.getDouble("momentum_limit");

        trigger_radius_A = config.getDouble("trigger_radius_A");
        trigger_radius_B = config.getDouble("trigger_radius_B");
        trigger_thickness = config.getDouble("trigger_thickness");
        trigger_resolution = config.getDouble("trigger_resolution");

        // Initialize layer arrays
        detector_layers = new ArrayList<DetectorLayer>();
        layers          = new ArrayList<Layer>();
        generateLayers();
    }

    // This method is called if the Simulation file is run directly
    // (eg. not from the Analyse class)
    public static void main(String[] args) throws IOException {
        Simulation simulation = new Simulation();

        System.out.println("Simulation started.");

        double[][] properties = simulation.run_simulation();

        Helpers.write_to_disk("data.csv", properties);

        simulation.exportViewerData();

        System.out.println("Simulation completed.");
    }

    // This method replaces the particle factory class as it can all fit in
    // this one method
    // Gives a linear spread of angles and a Gaussian spread of momentums
    public Particle makeParticle() {
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

    // This is where the magic happens!
    public double[][] run_simulation() {
        Particle[] particles = new Particle[num_particles];
        double[][] properties = new double[num_particles][];
        double estimation;

        for (int i = 0; i < num_particles; i++) {
            // We'll remember the original muon details for safe-keeping
            Particle particle = makeParticle();
            particles[i] = new Particle(particle);
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
                particles[i].momentum,
                particle.momentum,
                estimation
            };
        }

        return properties;
    }

    public double estimateMomentum(Particle p) {
        // Get Coincidence Deteector properties
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
        double momentum_est =
            1000 * 0.3 * mag_field * trigger_radius_B / (2*delta);

        return momentum_est;
    }

    // Allocates angles into bins of discrete steps which fairly accurately
    // detectors in a real detector system
    public double get_detector_angle(double angle) {
        // trigger_resolution == 'Bin Size'
        return Math.round(angle/trigger_resolution) * trigger_resolution;
    }

    // Sets up all the physical detector layers and the empty gaps inbetween
    private void generateLayers() {
        // Silicon detector (sd) radii and thickness of layers
        double[] sd_radius = { 4.5, 8, 12, 18, 30, 40, 50, 70 };
        double sd_thickness = 0.05; // cm

        // Initialise beryllium/silicon attenuators
        Attenuator beryllium_attn = new Attenuator(4, 9.0121831, 1.85, 0.3/20);
        Attenuator silicon_attn = new Attenuator(14, 28.0855, 2.3290, sd_thickness/20);

        // Add beryllium beam pipe
        layers.add(
            new AttenuatorLayer(
                3.5, // Start radius
                3.7, // End radius
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
        order_layers();

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
        // Make sure everything is in order
        order_layers();
    }

    // Method to sort layers in order of ascending radius
    private void order_layers() {
        layers.sort(new Comparator<Layer>() {
            @Override
            public int compare(Layer l1, Layer l2) {
                return (new Double(l1.start)).compareTo(l2.start);
            }
        });
    }

    // Exports data in the format used by the DetectorViewer visualisation
    private void exportViewerData() throws IOException {
        double[][] layers = new double[detector_layers.size()][];
        for (int i = 0; i < layers.length; i++) {
            layers[i] = new double[detector_layers.get(i).getHits().size()];

            for (int j = 0; j < layers[i].length; j++) {
                layers[i][j] = detector_layers.get(i).getHits().get(j);
            }
        }

        Helpers.write_to_disk("layers.csv", layers);
    }
}
