// Import statements
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Let's get our simulation on!
 */
public class Simulation {
    private static Config config;
    private static PrintWriter screen;

    private static ParticleFactory factory;
    private static Particle[] particles;

    private static List<DetectorLayer> detector_layers;
	private static List<Layer> layers;

    public static void main(String[] args) throws IOException {
        // Setup Config & PrintWriter instances
        config = new Config("config.properties");
        screen = new PrintWriter(System.out, true);

        // Only generate muons so only muon mass needed
        double[] masses = new double[] { 106 }; // MeV/c^2
        factory = new ParticleFactory(
            config.getDouble("momentum"),
            config.getDouble("momentum_smear"),
            masses
        );

        // How many particles should we simulate?
        int count = config.getInt("num_particles");
        particles = new Particle[count];

        // Initialize layer arrays
        detector_layers = new ArrayList<DetectorLayer>();
        layers          = new ArrayList<Layer>();

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
        detector_layers.add(new DetectorLayer("CoincidenceDetector_1", 90, 90.05, silicon_attn));
		detector_layers.add(new DetectorLayer("CoincidenceDetector_2", 91, 91.05, silicon_attn));

        // Add additional vacuum layers and sort into correct order
        setup_layers();

        // Run a simulation for each of the muons
        Particle particle = factory.newParticle();

        particleloop:
        for (int i = 0; i < count; i++) {
            // We'll remember the original muon details for safe-keeping
            // particle = factory.newParticle();
            particles[i] = new Particle(particle);

            // Send particle through the layers
            boolean err = !particle.handle(layers);
            if (err) {
                screen.println("[!] Particle " + (i+1) + " Stopped!");
                break;
            }

            // Use the --last two-- detectors as the Coinicdence Detector.
            if (particle.getMomentum() > 0) {
                double est = estimate_momentum(particle, config.getDouble("cd_resolution"));

                screen.println("[*] Actual momentum: " + particle.getMomentum());
                screen.println("[*] Predicted momentum: " + est);
                screen.println("[*] QOP: " + (int) (est * 100 / particle.getMomentum()) + "%");
            }
        }

        write_to_disk("data.csv");
    }

    // ---------- Handlers ----------

    public static double estimate_momentum(Particle particle, double res) {
        // Get Coincidence Deteector properties
        double mag_field = config.getDouble("mag_field");
        double radius_a  = config.getDouble("cd_radius_a");
        double radius_b  = config.getDouble("cd_radius_b");
        double thickness = config.getDouble("cd_thickness");
        double range     = radius_b - (radius_a + thickness);

        // Get angles at the two coincidence detectors
        // --- For reference: see final page of project handout
        //                    these are the angles phi_9A and phi_9B
        // The results slightly using Helpers.gauss
        // --- This approximately simulates the resolution of the detectors
        double angle_a =
            Helpers.gauss(particle.getTrace().get(radius_a + thickness), res);
        double angle_b =
            Helpers.gauss(particle.getTrace().get(radius_b), res);

        // Estimate particle momentum (*1000 to convert GeV -> MeV)
        double delta = Math.abs(Math.atan(radius_b*(angle_b - angle_a)/range));
		double momentum_est = 1000 * 0.3 * mag_field * radius_a / (2*delta);

        return momentum_est;
    }

    private static void setup_layers() {
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
                    config.getDouble("mag_field")
    			)
            );

			last = l.getEnd();
		}

		// Add vacuum (field) layers
		layers.addAll(layers2);
		order_layers();
	}

	private static void order_layers() {
		// Sort layers in order of ascending radius
		layers.sort(new Comparator<Layer>() {
			@Override
			public int compare(Layer l1, Layer l2) {
				return (new Double(l1.getStart())).compareTo(l2.getStart());
			}
		});
	}

    private static boolean write_to_disk(String filepath) throws IOException {
        FileWriter file;
        PrintWriter toFile = null;

        try {
            file = new FileWriter(filepath); // File stream
            toFile = new PrintWriter(file); // File writer

            for (DetectorLayer dl : detector_layers) {
				for (double angle : dl.getHits())
					toFile.print(angle);
			}
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            if (toFile != null)
                toFile.close();

            return true;
        }
    }
}
