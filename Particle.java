import java.util.TreeMap;
import java.util.SortedMap;

public class Particle {
    private double mass; // MeV
    private double momentum; // MeV
    private double direction; // Radians
    private double position; // Radians
    private double charge; // Elementary units?

    private SortedMap<Double, Double> positions;

    public Particle(double _mass,
                    double _momentum,
                    double _direction,
                    double _position) {
        mass = _mass;
        momentum = _momentum;
        direction = _direction;
        position = _position;

        charge = 1;

        this.positions = new TreeMap<Double, Double>();
        this.positions.put(0.0, 0.0);
    }

    public Particle(Particle p) {
        this(
            p.getMass(),
            p.getMomentum(),
            p.getDirection(),
            p.getPosition()
        );
    }

    // ---------- Helpers ----------

    public double getX(double radius) { return radius * Math.cos(position); }
    public double getY(double radius) { return radius * Math.sin(position); }

    // ---------- Getters & Setters ----------

    public double getMass() { return mass; }
    public void setMass(double _mass) { mass = _mass; }

    public double getMomentum() { return momentum; }
    public void setMomentum(double _momentum) { momentum = _momentum; }

    public double getDirection() { return direction; }
    public void setDirection(double _direction) { direction = _direction; }

    public double getPosition() { return position; }
    public void setPosition(double _position) { position = _position; }

    public double getCharge() { return charge; }
    public void setCharge(double _charge) { charge = _charge; }

    public SortedMap<Double, Double> getPositions() { return positions; }
    public void setPosition(double _radius, double _position) {
        position = _position;
        this.positions.put(_radius, _position);
    }
}
