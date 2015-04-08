import java.util.List;
import java.util.TreeMap;
import java.util.SortedMap;

public class Particle {
    private double mass; // MeV
    private double momentum; // MeV
    private double direction; // Radians
    private double azimuth; // Radians
    private double charge; // e (Charge on an electron)

    private SortedMap<Double, Double> trace;

    public Particle(double _mass,
                    double _momentum,
                    double _direction,
                    double _azimuth) {
        mass = _mass;
        momentum = _momentum;
        direction = _direction;
        azimuth = _azimuth;

        charge = 1;

        this.trace = new TreeMap<Double, Double>();
        this.trace.put(0.0, 0.0);
    }

    public Particle(Particle p) {
        this(
            p.getMass(),
            p.getMomentum(),
            p.getDirection(),
            p.getAzimuth()
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

    // ---------- Getters & Setters ----------

    public double getMass() { return mass; }
    public void setMass(double _mass) { mass = _mass; }

    public double getMomentum() { return momentum; }
    public void setMomentum(double _momentum) { momentum = _momentum; }

    public double getDirection() { return direction; }
    public void setDirection(double _direction) { direction = _direction; }

    public double getAzimuth() { return azimuth; }
    public void setAzimuth(double _azimuth) { azimuth = _azimuth; }
    public void setAzimuth(double _radius, double _azimuth) {
        azimuth = _azimuth;
        trace.put(_radius, _azimuth);
    }

    public double getCharge() { return charge; }
    public void setCharge(double _charge) { charge = _charge; }

    public SortedMap<Double, Double> getTrace() { return trace; }
}
