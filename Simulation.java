import java.util.ArrayList;
import java.util.Random;


public class Simulation {

    Conf conf;

    private Random random;

    private double estMomPreCalc1, estMomPreCalc2;

    public ArrayList<Layer> layers;


    public Simulation(Conf _conf, ArrayList<Layer> _layers) {
        conf = _conf;
        layers = _layers;

        random = new Random();

        estMomPreCalc1 = 150 * conf.mag_field * conf.trigger_radius_B;
        estMomPreCalc2 = conf.trigger_radius_B - conf.trigger_radius_A - conf.trigger_thickness;

        return;
    }


    public double[][] run() {
        double[][] properties = new double[conf.num_particles][];
        double estimation;

        boolean stopped;

        for (int i = 0; i < conf.num_particles; i++) {
            Particle particle = new_particle();
            properties[i] = new double[5];

            stopped = false;

            for (Layer layer : layers) {
                if (particle.momentum <= 0 || layer.handle(particle) == false) {
                    stopped = true;
                    break;
                } else {
                    particle.trace.put(layer.end, particle.azimuth);
                }
            }

            estimation = stopped ? 0 : estimate_momentum(particle);

            // Add the information we need to an array and store it for analysis
            properties[i] = new double[]{
                    i+1,
                    particle.mass,
                    particle.original_momentum,
                    particle.momentum,
                    estimation
            };
        }

        return properties;
    }


    private Particle new_particle() {
        return new Particle(
                // Mass
                conf.particle_mass,
                // Momentum Smear
                Math.abs(Helpers.gauss(conf.momentum, conf.momentum * conf.momentum_smear)),
                // Direction
                random.nextDouble()*(2*Math.PI),
                // Azimuth
                0
        );
    }


    public double estimate_momentum(Particle p) {

        double angle_a = Helpers.gauss(p.trace.get(conf.trigger_radius_A + conf.trigger_thickness), conf.trigger_resolution);
        double angle_b = Helpers.gauss(p.trace.get(conf.trigger_radius_B), conf.trigger_resolution);

        return estMomPreCalc1 / Math.abs(Math.atan(conf.trigger_radius_B*(angle_b - angle_a)/estMomPreCalc2));
    }



}
