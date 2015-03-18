// Import statements
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import histogram.Histogram;

/**
 * Let's get our simulation on!
 */
public class Simulation {
    private static PrintWriter screen;
    private static Config config;

    private static ParticleFactory factory;
    private static Trajectory trajectory;
    private static CoincidenceDetector cd;

    private static ArrayList<Layer> layers;

    private static double cdMinMomentum, cdMaxMomentum;
    private static double[] masses;
    private static double[][] muons;

    public static double trigger(double[] muon) {
        // Get angles at the two coincidence detectors
        // ---For reference: see final page of project handout
        //                   these are the angles phi_9A and phi_9B
        double angleAtA = trajectory.getAngles(muon[1], muon[2], cd.radiusA)[0];
        double angleAtB = trajectory.getAngles(muon[1], muon[2], cd.radiusB)[0];

        // Return the momentum estimated by the coincidence detector
        return cd.estimateMomentum(angleAtA, angleAtB);
    }

    public static void main(String[] args) throws IOException {
        // Setup Config & PrintWriter instances
        screen = new PrintWriter(System.out, true);
        config = new Config("main");

        // Set up the Coincidence Detector
        cd = new CoincidenceDetector(
            config.getDouble("coincidenceDetectorRadiusA"),
            config.getDouble("coincidenceDetectorRadiusB")
        );

        // Add beam pipe layer
        layers.add(
            new AttenuatorLayer(
                "Beam Pipe",
                3.5,
                3,
                new Attenuator(4, 9.0121831, 1.85, 3/20)
            )
        );
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // This is hard coded BERYLLIUM properties and step sizes, need to change
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        // Radii and thickness of silicon detector layers
        double[] sdr = {4.5, 8, 12, 18, 30, 40, 50, 70, 90, 91}; // cm
        double sdt = 0.05; // cm

        // Add silicon detector layers
		for (int i = 0; i < sdr.length; i++) {
			layers.add(
                new AttenuatorLayer(
                    "S-"+String.valueOf(sdr[i]),
                    sdr[i],
                    sdt,
                    new Attenuator(14, 28.0855, 2.3290, sdt/20))
            );
        }

        // Add additional vacuum layers and sort into correct order
	    setup();

        // How many muons should we simulate?
        int count = config.getInt("numParticles");

        // Initialize the muon array and get a new MuonFactory instance
        muons = new double[count][3];

        // Only generate muons so only muon mass needed
        masses = new double[] { 106 }; // MeV/c^2
        factory = new ParticleFactory(
            config.getDouble("minMomentum"),
            config.getDouble("maxMomentum"),
            masses);

        trajectory = new Trajectory(config.getDouble("magField"));

        cdMinMomentum = config.getDouble("coincidenceMinMomentum");
		cdMaxMomentum = config.getDouble("coincidenceMaxMomentum");

        // Run a simulation for each of the muons
        double[] muon = new double[3];
        double momentum;
        for (int i = 0; i < count; i++) {
            // We'll remember the original muon details for safe-keeping
            muon = muons[i] = factory.newParticle();

            // Send the muon through all the layers in the accelerator
            for (Layer layer : layers)
                muon = layer.handle(muon);

            // If we are using a Coincidence Detector then hand it over!
            if (cd != null) {
                momentum = trigger(muon);

                screen.println("[*] Actual momentum: " + muon[1]);
                screen.println("[*] Predicted momentum: " + momentum);

                // Check to see if this particle has the required momentum
                if (momentum > cdMinMomentum && momentum < cdMaxMomentum)
                    screen.println("[x] Muon " + (i+1) + " has momentum within bounds!");
                else
                    screen.println("[!] Muon " + (i+1) + " has momentum out of bounds!");
            }
        }
    }

    private static void setup() {
		ArrayList<Layer> layers2 = new ArrayList<Layer>();
		double last_z = 0.0;

        // Add vacuum layers to fill in gaps between physical layers
		for (Layer l : layers) {
			if (l.getDistance() > last_z) {
				layers2.add(
                    new VacuumLayer(
                        "V-"+String.valueOf(last_z),
                        last_z,
                        l.getDistance(),
                        trajectory
                    )
                );

                last_z = l.getDistance() + l.getThickness();
			}
		}

        // Join two lists of layers together
		layers.addAll(layers2);

        // Sort layers so in order of ascending radius
		layers.sort(new Comparator<Layer>() {
			@Override
			public int compare(Layer l1, Layer l2) {
				return ((Double) l1.getDistance()).compareTo(l2.getDistance());
			}
		});
	}
}
