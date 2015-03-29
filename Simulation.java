// Import statements
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import java.io.FileWriter;
import java.io.FileNotFoundException;

import histogram.Histogram;

import java.util.Arrays;

/**
 * Let's get our simulation on!
 */
public class Simulation {
    private static PrintWriter screen;
    private static Config config;

    private static ParticleFactory factory;
    private static CoincidenceDetector cd;

    private static Attenuator beryllium_attn, silicon_attn;

    private static List<DetectorLayer> detector_layers = new ArrayList<DetectorLayer>();
	private static List<Layer> layers = new ArrayList<Layer>();

    private static Particle[] particles;

    public static void main(String[] args) throws IOException {
        // Setup Config & PrintWriter instances
        screen = new PrintWriter(System.out, true);
        config = new Config("config.properties");

        // How many particles should we simulate?
        int count = config.getInt("numParticles");

        // Initialize the muon array and get a new MuonFactory instance
        particles = new Particle[count];

        // Only generate muons so only muon mass needed
        double[] masses = new double[] { 106 }; // MeV/c^2
        factory = new ParticleFactory(
            config.getDouble("minMomentum"),
            config.getDouble("maxMomentum"),
            masses);

        // Set up the Coincidence Detector
        cd = new CoincidenceDetector(
            config.getDouble("coincidenceDetectorRadiusA"),
            config.getDouble("coincidenceDetectorRadiusB"),
            0.05
        );

        // Add beryllium attenuator and beam pipe layer with 20 steps
        beryllium_attn = new Attenuator(4, 9.0121831, 1.85, 0.3/20);
        layers.add(new AttenuatorLayer(
            "Beryllium Beam Pipe",
            3.5,
            3.7,
            beryllium_attn
        ));

        // // Radii and thickness of silicon detector layers
        // double[] sdr = { 4.5, 8, 12, 18, 30, 40, 50, 70, 90, 91 };
        // double sdt = 0.05; // cm
        //
        // // Add silicon attenuator instance
        // silicon_attn = new Attenuator(14, 28.0855, 2.3290, sdt/20);
        //
        // // Add silicon detectors
        // for (int i = 0; i < sdr.length; i++) {
        //     layers.add( new AttenuatorLayer(
        //         "S_"+String.valueOf(sdr[i]),
        //         sdr[i],
        //         (sdr[i] + sdt),
        //         silicon_attn
        //     ));
        // }
        //
        // detector_layers.add(new DetectorLayer("Detector1", 90.05, 360, 1));
		// detector_layers.add(new DetectorLayer("Detector2", 91, 360, 1));

        // Temporary detector setup to test magnetic field layers
        for (int i = 0; i < 10; i++) {
            detector_layers.add(new DetectorLayer("Detector", i, 360, 1));
        }

        // Add additional vacuum layers and sort into correct order
        setupLayers();

        // Run a simulation for each of the muons
        // Particle particle = factory.newParticle();

        // Test Particle
        Particle particle = new Particle(105.9, 1000, 0, 0);

        double estMomentum;

        particleloop:
        for (int i = 0; i < count; i++) {
            // We'll remember the original muon details for safe-keeping
            // particle = factory.newParticle();
            particles[i] = new Particle(particle);

            // TODO - Could do with refactoring into own method
            // Send the muon through all the layers in the accelerator
            for (Layer layer : layers) {
                if (particle.getMomentum() > 0) {
                    if (layer.handle(particle) == false){
                        System.out.println("ERR");
                        break particleloop;
                    }
                } else {
                    System.out.println("\t[!] Muon " + (i+1) + " Stopped @ " + layer.getName());
                    break particleloop;
                }
            }

            // I broke this, sorry - jared
            // If we are using a Coincidence Detector then hand it over!
            // if (particle.getMomentum() > 0 && cd != null)
            //     trigger(particle, i);
        }

        writeToDisk("data.csv", new Particle[] { particle });
    }


    public static void trigger(Particle particle, int i) {
        // Get angles at the two coincidence detectors
        // --- For reference: see final page of project handout
        //                    these are the angles phi_9A and phi_9B
        double angleAtA = particle.getPositions().get(90.05);
        double angleAtB = particle.getPositions().get(91.00);

        // Return the momentum estimated by the coincidence detector
        double estMomentum = cd.estimateMomentum(angleAtA, angleAtB);

        screen.println("[*] Actual momentum: " + particles[i].getMomentum());
        screen.println("[*] Predicted momentum: " + estMomentum);
        screen.println("[*] QOP: " + (int) (estMomentum*100 / particles[i].getMomentum()) + "%");

        double cdMinMomentum = config.getDouble("coincidenceMinMomentum");
        double cdMaxMomentum = config.getDouble("coincidenceMaxMomentum");

        // screen.println(particles[i].getPositions());

        // Check to see if this particle has the required momentum
        if (estMomentum > cdMinMomentum && estMomentum < cdMaxMomentum) {
            screen.println("[x] Muon " + (i+1) + " has momentum within bounds!");
        } else {
            screen.println("[!] Muon " + (i+1) + " has momentum out of bounds!");
        }
    }


    private static void setupLayers() {
		// Add detector layers
		layers.addAll(detector_layers);

		// Initial layer sort
		orderLayers();

		// Add vacuum layers to fill in gaps between physical layers
		ArrayList<Layer> layers2 = new ArrayList<Layer>();
		double last = 0.0;

		for (Layer l : layers) {
			if (l.start > last) {
				layers2.add( new FieldLayer(
					"V-"+String.valueOf(last),
					last,
					l.start,
                    config.getDouble("magField")
				));
			}
			last = l.end;
		}

		// Add vacuum layers
		layers.addAll(layers2);

		// Final layer sort
		orderLayers();
	}

	private static void orderLayers() {
		// Sort layers in order of ascending radius
		layers.sort(new Comparator<Layer>() {
			@Override
			public int compare(Layer l1, Layer l2) {
				Double r1 = l1.start;
				return r1.compareTo(l2.start);
			}
		});
	}

    static public boolean writeToDisk(String filepath, Particle[] particles) throws IOException {
        FileWriter file;
        PrintWriter toFile = null;

        try {
            file = new FileWriter(filepath); // File stream
            toFile = new PrintWriter(file); // File writer

            for (DetectorLayer dl : detector_layers) {
				for (Double angle : dl.getHits()) {
					toFile.print(angle+",");
				}
				toFile.print("\n");
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
