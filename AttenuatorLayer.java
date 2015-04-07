
public class AttenuatorLayer extends Layer {
    private static final int steps = 20;
    private double stepSize;

    private Attenuator attn;

    public AttenuatorLayer(String _name,
                           double _start,
                           double _end,
                           Attenuator _attn) {
        super(_name, _start, _end);

        stepSize = getThickness() / steps;
        attn = _attn;
    }

    public boolean handle(Particle particle) {
        double mass = particle.getMass();
        double momentum = particle.getMomentum();
        double direction = particle.getDirection();
        double position = particle.getPosition();

        double theta;
        double distance;

        for (int i = 0; i < steps; i++) {
            theta = Helpers.gauss(0, attn.getMCSTheta0(mass, momentum));
            distance = stepSize / Math.cos(theta); // Hypotenuse

            momentum -= attn.getEnergyLoss(mass, momentum) * Math.abs(distance);
            direction += theta;
            position += stepSize * Math.tan(theta);

            if (momentum <= 0) {
                momentum = 0;
                return false;
            }
        }

        particle.setMomentum(momentum);
        particle.setDirection(direction);
        particle.setPosition(end, position);

        return true;
    }
}
