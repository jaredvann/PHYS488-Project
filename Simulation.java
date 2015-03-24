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
    private static CoincidenceDetector cd;

    private static Attenuator beryllium;
    private static Attenuator silicon;
    private static ArrayList<Layer> layers;

    private static double[] masses;
    private static Particle[] particles;

    public static double trigger(Particle particle) {
        // Get angles at the two coincidence detectors
        // --- For reference: see final page of project handout
        //                    these are the angles phi_9A and phi_9B
        (new AttenuatorLayer("trigger_A", cd.radiusA, cd.thickness, silicon))
            .handle(particle);
        double angleAtA = particle.getPosition();

        (new VacuumLayer("trigger_sep", (cd.radiusA+cd.thickness), cd.range, config.getDouble("magField")))
            .handle(particle);

        (new AttenuatorLayer("trigger_B", cd.radiusB, cd.thickness, silicon))
            .handle(particle);
        double angleAtB = particle.getPosition();

        // Return the momentum estimated by the coincidence detector
        return cd.estimateMomentum(angleAtA, angleAtB);
    }

    public static void main(String[] args) throws IOException {
        // Setup Config & PrintWriter instances
        screen = new PrintWriter(System.out, true);
        config = new Config("main");

        // How many particles should we simulate?
        int count = config.getInt("numParticles");

        // Initialize the muon array and get a new MuonFactory instance
        particles = new Particle[count];

        // Only generate muons so only muon mass needed
        masses = new double[] { 106 }; // MeV/c^2
        factory = new ParticleFactory(
            config.getDouble("minMomentum"),
            config.getDouble("maxMomentum"),
            masses);

        layers = new ArrayList<Layer>();

        // Set up the Coincidence Detector
        cd = new CoincidenceDetector(
            config.getDouble("coincidenceDetectorRadiusA"),
            config.getDouble("coincidenceDetectorRadiusB"),
            0.05
        );

        // Add beryllium beam pipe layer with 20 steps
        beryllium = new Attenuator(4, 9.0121831, 1.85, 0.3/20);
        layers.add(
            new AttenuatorLayer(
                "Beryllium Beam Pipe",
                3.5,
                0.3,
                beryllium
            )
        );

        // Radii and thickness of silicon detector layers
        double[] sdr = { 4.5, 8, 12, 18, 30, 40, 50, 70 }; // cm
        double sdt = 0.05; // cm

        // Add silicon detector layers with 20 steps each
        silicon = new Attenuator(14, 28.0855, 2.3290, sdt/20);
		for (int i = 0; i < sdr.length; i++) {
			layers.add(
                new AttenuatorLayer(
                    "S-"+String.valueOf(sdr[i]),
                    sdr[i],
                    sdt,
                    silicon
                )
            );
        }

        // Add additional vacuum layers and sort into correct order
	    setupDetectorLayers();

        // Run a simulation for each of the muons
        Particle particle;
        double estMomentum;

        particleloop:
        for (int i = 0; i < count; i++) {
            // We'll remember the original muon details for safe-keeping
            particle = factory.newParticle();
            particles[i] = new Particle(particle);

            // Send the muon through all the layers in the accelerator
            for (Layer layer : layers) {
                if (particle.getMomentum() > 0) {
                    layer.handle(particle);
                } else {
                    System.out.println("\t[!] Muon " + (i+1) + " Stopped @ " + layer.getName());
                    break particleloop;
                }
            }

            // If we are using a Coincidence Detector then hand it over!
            if (particle.getMomentum() > 0 && cd != null) {
                estMomentum = trigger(particle);

                screen.println("[*] Actual momentum: " + particles[i].getMomentum());
                screen.println("[*] Predicted momentum: " + estMomentum);
                screen.println("[*] QOP: " + (int) (estMomentum*100 / particles[i].getMomentum()) + "%");

                double cdMinMomentum = config.getDouble("coincidenceMinMomentum");
        		double cdMaxMomentum = config.getDouble("coincidenceMaxMomentum");

                // Check to see if this particle has the required momentum
                if (estMomentum > cdMinMomentum && estMomentum < cdMaxMomentum) {
                    screen.println("[x] Muon " + (i+1) + " has momentum within bounds!");
                } else {
                    screen.println("[!] Muon " + (i+1) + " has momentum out of bounds!");
                }
            }
        }
    }

    private static void setupDetectorLayers() {
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
                        config.getDouble("magField")
                    )
                );
			}

            last_z = l.getDistance() + l.getThickness();
		}

        // Join two lists of layers together
		layers.addAll(layers2);

        // Add layer between final detector and the first trigger layer
        layers.add(new VacuumLayer(
            "V-"+String.valueOf(last_z),
            last_z,
            cd.radiusA,
            config.getDouble("magField")
        ));

        // Sort layers so in order of ascending radius
		layers.sort(new Comparator<Layer>() {
			@Override
			public int compare(Layer l1, Layer l2) {
				return ((Double) l1.getDistance()).compareTo(l2.getDistance());
			}
		});
	}
}
