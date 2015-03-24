
public class Particle {
    private double mass; // MeV
    private double momentum; // MeV
    private double direction; // Radians
    private double position; // Radians

    public Particle(double _mass,
                    double _momentum,
                    double _direction,
                    double _position) {
        mass = _mass;
        momentum = _momentum;
        direction = _direction;
        position = _position;
    }

    public Particle(Particle p) {
        this(
            p.getMass(),
            p.getMomentum(),
            p.getDirection(),
            p.getPosition()
        );
    }

    public double getMass() { return mass; }
    public void setMass(double _mass) { mass = _mass; }

    public double getMomentum() { return momentum; }
    public void setMomentum(double _momentum) { momentum = _momentum; }

    public double getDirection() { return direction; }
    public void setDirection(double _direction) { direction = _direction; }

    public double getPosition() { return position; }
    public void setPosition(double _position) { position = _position; }
}
