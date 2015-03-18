// Import statements
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import histogram.Histogram;

/**
 * Let's get our simulation on!
 */
public class Simulation {
    private static PrintWriter screen = new PrintWriter(System.out, true);
    private static Config config;

    private static CoincidenceDetector cd;

    private static double[] masses;
    private static ParticleFactory factory;
    private static double[][] muons;

<<<<<<< HEAD
    private static Histogram momenta;
=======
    private static double momentum, cdMinMomentum, cdMaxMomentum;
>>>>>>> origin/master

    private static Trajectory trajectory;

    public static double trigger(double[] muon) {
        // Get angles at the two coincidence detectors
        // ---For reference: see final page of project handout
        //                   these are the angles phi_9A and phi_9B
        double angleAtA = trajectory.getAngles(muon[1], muon[2], cd.radiusA)[0];
        double angleAtB = trajectory.getAngles(muon[1], muon[2], cd.radiusB)[0];

        // Is this particle high or not?
        double mom = cd.estimateMomentum(angleAtA, angleAtB);

        return mom;
    }

    public static void main(String[] args) throws IOException {
        config = new Config("main");

        // Set up the Coincidence Detector (it needs the radii)
        cd = new CoincidenceDetector();

        // How many muons should we simulate?
        int count = 1;
        // int count = config.getInt("numberOfParticles");

        // Initialize the muon array and get a new MuonFactory instance
        muons = new double[count][2];

        masses = new double[] { 106 };
        factory = new ParticleFactory(
            config.getDouble("minMomentum"),
            config.getDouble("maxMomentum"),
            masses);

        momenta = new Histogram(config.getDouble("minMomentum"), config.getDouble("maxMomentum"));

        trajectory = new Trajectory(config.getDouble("magField"));

        cdMinMomentum = config.getDouble("coincidenceMinMomentum");
		cdMaxMomentum = config.getDouble("coincidenceMaxMomentum");

        // Run a simulation for each of the muons
        double[] muon = new double[2];
        for (int i = 0; i < count; i++) {
            // We'll remember the original muon details for safe-keeping
            muon = muons[i] = factory.newParticle();
            momenta.add(muon[1]);

            // Send the muon through all the layers in the accelerator
            // for (Layer layer : layers)
            //     muon = layer.handle(muon);

            // If we are using a Coincidence Detector then hand it over!

            screen.println("[*] Actual momentum: " + muon[1]);

            if (cd != null)

                momentum = trigger(muon);

                screen.println("[*] Predicted momentum: " + momentum);

                if (momentum > cdMinMomentum && momentum < cdMaxMomentum)
                    screen.println("[*] Muon " + (i+1) + " has momentum within bounds!");
                else
                    screen.println("[*] Muon " + (i+1) + " has momentum out of bounds!");
        }
    }
}
