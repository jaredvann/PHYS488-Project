// Import statements
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

/**
 * Let's get our simulation on!
 */
public class Simulation {
    private static final PrintWriter screen = new PrintWriter(System.out);
    private static final Config config = new Config("main");

    private static CoincidenceDetector cd;
    private static List<Layer> layers;

    private static double[] masses;
    private static ParticleFactory factory;

    private static double[][] muons;

    public static boolean trigger(double[] muon) {

        // Get angles at the two coincidence detectors
        // ---For reference: see final page of project handout
        //                   these are the angles phi_9A and phi_9B
        double angleAtA = Trajectory.run(muon, cd.radiusA);
        double angleAtB = Trajectory.run(muon, cd.radiusB);

        // Is this particle high or not?
        boolean amIHigh = cd.estimateMomentum(angleAtA, angleAtB);

        return amIHigh;
    }

    public static void main(String[] args) {
        layers = new ArrayList<Layer>();

        // Set up the Coincidence Detector (it needs the radii)
        cd = new CoincidenceDetector(
            config.getDouble("coincidenceDetectorRadiusA"),
            config.getDouble("coincidenceDetectorRadiusA"));

        // How many muons should we simulate?
        int count = 1;
        // int count = config.getInt("numberOfParticles");

        // Initialize the muon array and get a new MuonFactory instance
        muons = new double[count][2];

        masses = new double[] { 106 };
        factory = new MuonFactory(
            config.getDouble("minMomentum"),
            config.getDouble("maxMomentum"),
            masses);

        // Run a simulation for each of the muons
        double[] muon = new double[2];
        for (int i = 0; i < count; i++) {
            // We'll remember the original muon details for safe-keeping
            muon = muons[i] = factory.newMuon();

            // Send the muon through all the layers in the accelerator
            // for (Layer layer : layers)
            //     muon = layer.handle(muon);

            // If we are using a Coincidence Detector then hand it over!
            if (cd != null)
                if (trigger(muon))
                    screen.println("[*] Muon " + (i+1) + " has high momentum!");
                else
                    screen.println("[*] Muon " + (i+1) + " has low momentum!");
        }
    }
}
