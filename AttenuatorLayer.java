
public class AttenuatorLayer extends Layer {
    private static final int steps = 20;
    private double stepSize;

    private Attenuator attn;

    public AttenuatorLayer(String n, double d, double t, Attenuator attn) {
        super(n, d, t);

        this.stepSize = t / steps;

        this.attn = attn;
    }

    public void handle(Particle particle) {
        double mass = particle.getMass();
        double momentum = particle.getMomentum();
        double direction = particle.getDirection();
        double position = particle.getPosition();

        double theta;
        double distance;

        for (int i = 0; i < steps; i++) {
            theta = attn.getMCSTheta0(particle.getMass(), particle.getMomentum());
            distance = stepSize / Math.cos(theta); // Hypotenuse

            momentum -= attn.getEnergyLoss(mass, momentum) * Math.abs(distance);
            direction += Helpers.gauss(0, theta);
            position += stepSize * Math.sin(theta);

            if (momentum <= 0) {
                momentum = 0;
                break;
            }
        }

        particle.setMomentum(momentum);
        particle.setDirection(direction);
        particle.setPosition(position);
    }
}
