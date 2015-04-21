import java.util.List;
import java.util.TreeMap;
import java.util.SortedMap;

public class Particle {
    public double mass; // MeV
    public double momentum; // MeV
    public double original_momentum;
    public double direction; // Radians
    public double azimuth; // Radians
    public double charge; // e (Charge on an electron)

    public SortedMap<Double, Double> trace;

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

    public Particle(Particle p) {
        this(
            p.mass,
            p.momentum,
            p.direction,
            p.azimuth
        );
    }

    // ---------- Handlers ----------

    public boolean handle(List<Layer> layers) {
        // Send the muon through all the layers in the accelerator
        for (Layer layer : layers) {
            if (momentum <= 0)
                return false;

            if (layer.handle(this) == false)
                return false;
        }

        return true;
    }

    // ---------- Helpers ----------

    public double getX(double radius) { return radius * Math.cos(azimuth); }
    public double getY(double radius) { return radius * Math.sin(azimuth); }

    // ---------- Setters ----------

    public void setMass(double _mass) { mass = _mass; }

    public void setMomentum(double _momentum) { momentum = _momentum; }

    public void setDirection(double _direction) { direction = _direction; }

    public void setAzimuth(double _azimuth) { azimuth = _azimuth; }
    public void setAzimuth(double _radius, double _azimuth) {
        azimuth = _azimuth;
        trace.put(_radius, _azimuth);
    }

    public void setCharge(double _charge) { charge = _charge; }

    public double getTraceAt(double r) { return trace.get(r); }
}
