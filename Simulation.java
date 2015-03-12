// Import statements
import java.io.PrintWriter;

/**
 * Let's get our simulation on!
 */
public class Simulation {
    private static final PrintWriter screen = new PrintWriter(System.out);
    private static final Config config = new Config("main");

    private static CoincidenceDetector cd;

    private static MuonFactory factory;
    private static double[][] muons;

    public static boolean run(double[] muon) {

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
        // Set up the Coincidence Detector (it needs the radii)
        cd = new CoincidenceDetector(
            config.getDouble("coincidenceDetectorRadiusA"),
            config.getDouble("coincidenceDetectorRadiusA"));

        // How many muons should we simulate?
        int count = 1;

        // Initialize the muon array and get a new MuonFactory instance
        muons = new double[count][2];
        factory = new MuonFactory(
            config.getDouble("minMomentum"),
            config.getDouble("maxMomentum"));

        // Run a simulation for each of the muons
        for (int i = 0; i < count; i++) {
            // We'll remember the original muon details for safe-keeping
            muons[i] = factory.newMuon();

            // Go Go Go Go Go!
            if (run(muons[i]))
                screen.println("[*] Muon " + (i+1) + " has high momentum!");
            else
                screen.println("[*] Muon " + (i+1) + " has low momentum!");
        }
    }
}
