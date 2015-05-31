import java.util.List;
import java.util.TreeMap;

public class Particle {
    public double mass; // MeV
    public double momentum; // MeV
    public double original_momentum; // MeV
    public double direction; // Radians
    public double azimuth; // Radians
    public double charge; // e (Elementary charge)

    public TreeMap<Double, Double> trace;

    public Particle(double _mass,
                    double _momentum,
                    double _direction,
                    double _azimuth) {
        mass = _mass;
        momentum = _momentum;
        original_momentum = _momentum;
        direction = _direction;
        azimuth = _azimuth;

        charge = 1;

        this.trace = new TreeMap<Double, Double>();
        this.trace.put(0.0, 0.0);
    }
}
