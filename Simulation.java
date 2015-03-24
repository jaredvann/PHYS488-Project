// Import statements
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import java.io.FileWriter;
import java.io.FileNotFoundException;

import histogram.Histogram;

/**
 * Let's get our simulation on!
 */
public class Simulation {
    private static PrintWriter screen;
    private static Config config;

    private static ParticleFactory factory;
    private static CoincidenceDetector cd;

    private static Attenuator beryllium_attn, silicon_attn;
    private static ArrayList<Layer> layers;

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

        layers = new ArrayList<Layer>();

        // Set up the Coincidence Detector
        cd = new CoincidenceDetector(
            config.getDouble("coincidenceDetectorRadiusA"),
            config.getDouble("coincidenceDetectorRadiusB"),
            0.05
        );

        // Add beryllium attenuator and beam pipe layer with 20 steps
        beryllium_attn = new Attenuator(4, 9.0121831, 1.85, 0.3/20);
        layers.add( new AttenuatorLayer(
            "Beryllium Beam Pipe",
            3.5,
            0.3,
            beryllium_attn
        ));

        // Radii and thickness of silicon detector layers
        double[] sdr = { 4.5, 8, 12, 18, 30, 40, 50, 70, 90, 91 };
        double sdt = 0.05; // cm

        // Add silicon attenuator instance
        silicon_attn = new Attenuator(14, 28.0855, 2.3290, sdt/20);

        // Add silicon detectors
        for (int i = 0; i < sdr.length; i++) {
            layers.add( new AttenuatorLayer(
                "S_"+String.valueOf(sdr[i]),
                sdr[i],
                sdt,
                silicon_attn
            ));
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

            // Send the muon through all the layers in the accelerator
            for (Layer layer : layers) {
                if (particle.getMomentum() > 0) {
                    layer.handle(particle);
                } else {
                    System.out.println("\t[!] Muon " + (i+1) + " Stopped @ " + layer.getName());
                    break particleloop;
                }
            }

            particles[i] = particle;

            // If we are using a Coincidence Detector then hand it over!
            if (particle.getMomentum() > 0 && cd != null)
                trigger(particle, i);
        }

        writeToDisk("data.csv", particles);
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


    private static void setupDetectorLayers() {
        // Adds vacuum layers inbetween attenuation layers and sorts into
        // order of ascending radius.
        ArrayList<Layer> layers2 = new ArrayList<Layer>();
        double last_z = 0.0;

        // Add vacuum layers to fill in gaps between physical layers
        for (Layer l : layers) {
            if (l.getDistance() > last_z) {
                layers2.add( new VacuumLayer(
                    "V-"+String.valueOf(last_z),
                    last_z,
                    l.getDistance()-last_z,
                    config.getDouble("magField")
                ));
            }

            last_z = l.getDistance() + l.getThickness();
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


    static public boolean writeToDisk(String filepath, Particle[] particles) throws IOException {
        FileWriter file;
        PrintWriter toFile = null;

        try {
            file = new FileWriter(filepath); // File stream
            toFile = new PrintWriter(file); // File writer

            for (Particle p : particles) {
                for (Double value : p.getPositions().values()) {
                    toFile.print(value);
                    toFile.print(',');
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
