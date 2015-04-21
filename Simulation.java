// Import statements
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Comparator;
import java.text.DecimalFormat;

/**
 * Let's get our simulation on!
 */
public class Simulation {
    private Random random;
    private Config cfg;

    public List<DetectorLayer> detector_layers;
    public List<Layer> layers;

    public Simulation(Config _cfg) throws IOException {
        random = new Random();

        // Setup configuration
        cfg = _cfg;

        // Initialize layer arrays
        detector_layers = new ArrayList<DetectorLayer>();
        layers          = new ArrayList<Layer>();
        generateLayers();
    }

    public static void main(String[] args) throws IOException {
        PrintWriter screen = new PrintWriter(System.out, true);

        Config cfg = new Config("config.properties");
        Simulation sim = new Simulation(cfg);

        screen.println("Simulation started.");

        double[][] properties = sim.simulate();

        screen.println("Simulation completed.");

        Helpers.write_to_disk("data.csv", properties);
        sim.exportViewerData();
    }

    // ---------- Handlers ----------

    public Particle makeParticle() {
        return new Particle(
            // Mass
            cfg.masses[random.nextInt(cfg.masses.length)],
            // Momentum Smear
            Math.abs(Helpers.gauss(
                cfg.momentum,
                cfg.momentum * cfg.momentumSmear
            )),
            // Direction
            random.nextDouble()*(2*Math.PI),
            // Azimuth
            0
        );
    }

    public double[][] simulate() {
        Particle[] particles = new Particle[cfg.numParticles];
        double[][] properties = new double[cfg.numParticles][];

        for (int i = 0; i < cfg.numParticles; i++) {
            // We'll remember the original muon details for safe-keeping
            Particle particle = makeParticle();
            particles[i] = new Particle(particle);
            properties[i] = new double[5];

            // Send particle through the layers and report any errors
            boolean stopped = !particle.handle(layers);

            // Use the --last two-- detectors as the track trigger.
            double est;
            if (particle.getMomentum() > 0 && stopped == false)
                est = estimateMomentum(particle);
            else
                est = 0;

            properties[i][0] = i+1;
            properties[i][1] = particle.getMass();
            properties[i][2] = particles[i].getMomentum();
            properties[i][3] = particle.getMomentum();
            properties[i][4] = est;
        }

        return properties;
    }

    public double estimateMomentum(Particle p) {
        // Get Coincidence Deteector properties
        double range =
            cfg.triggerRadiusB - (cfg.triggerRadiusA + cfg.triggerThickness);

        // Get angles at the two coincidence detectors
        // --- For reference: see final page of project handout
        //                    these are the angles phi_9A and phi_9B
        // The results slightly smeared using Helpers.gauss
        // --- This kind of simulates the resolution of the detectors?

        double angle_a = get_detector_angle(p.getTraceAt(cfg.triggerRadiusA + cfg.triggerThickness));
        double angle_b = get_detector_angle(p.getTraceAt(cfg.triggerRadiusB));

        // Estimate particle momentum (*1000 to convert GeV -> MeV)
        double delta =
            Math.abs(Math.atan(cfg.triggerRadiusB*(angle_b - angle_a)/range));
        double momentum_est =
            1000 * 0.3 * cfg.magField * cfg.triggerRadiusB / (2*delta);

        return momentum_est;
    }

    public double get_detector_angle(double angle) {
        double binsize = cfg.triggerResolution;
        return Math.round(angle/binsize) * binsize;
    }

    // ---------- Layer Management ----------

    private void generateLayers() {
        // Radii and thickness of layers
        double[] sdr = { 4.5, 8, 12, 18, 30, 40, 50, 70 };
        double sdt = 0.05; // cm

        // Initialise beryllium/silicon attenuators
        Attenuator beryllium_attn = new Attenuator(4, 9.0121831, 1.85, 0.3/20);
        Attenuator silicon_attn = new Attenuator(14, 28.0855, 2.3290, sdt/20);

        // Add beryllium beam pipe
        layers.add(
            new AttenuatorLayer(
                "Beryllium Beam Pipe",
                3.5,
                3.7,
                beryllium_attn
            )
        );

        // Add silicon detectors
        for (double r : sdr) {
            detector_layers.add(
                new DetectorLayer(
                    "S_"+String.valueOf(r),
                    r,
                    (r + sdt),
                    silicon_attn
                )
            );
        }

        // Add coincidence detectors
        detector_layers.add(
            new DetectorLayer(
                "CoincidenceDetector_1",
                cfg.triggerRadiusA,
                (cfg.triggerRadiusA + cfg.triggerThickness),
                silicon_attn
            )
        );

        detector_layers.add(
            new DetectorLayer(
                "CoincidenceDetector_2",
                cfg.triggerRadiusB,
                (cfg.triggerRadiusB + cfg.triggerThickness),
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
        for (Layer l : layers) {
            layers2.add(
                new FieldLayer(
                    "V-"+String.valueOf(last),
                    last,
                    l.getStart(),
                    cfg.magField
                )
            );

            last = l.getEnd();
        }

        // Add vacuum (field) layers
        layers.addAll(layers2);
        order_layers();
    }

    // Sort layers in order of ascending radius
    private void order_layers() {
        layers.sort(new Comparator<Layer>() {
            @Override
            public int compare(Layer l1, Layer l2) {
                return (new Double(l1.getStart())).compareTo(l2.getStart());
            }
        });
    }

    // ---------- Exporting Data ----------

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
