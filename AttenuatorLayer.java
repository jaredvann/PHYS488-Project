public class AttenuatorLayer extends Layer {
    private static final int STEPS = 40;

    private double stepSize;

    private Attenuator attn;

    public AttenuatorLayer(String _name,
                           double _start,
                           double _end,
                           Attenuator _attn) {
        super(_name, _start, _end);

        stepSize = getThickness() / STEPS;
        attn = _attn;
    }

    public boolean handle(Particle particle) {
        double mass = particle.getMass();
        double momentum = particle.getMomentum();
        double direction = particle.getDirection();
        double azimuth = particle.getAzimuth();

        double energyLoss = attn.getEnergyLoss(mass, momentum);

        double theta = attn.getTheta(mass, momentum);
        double thetaSmeared;

        double distance;

        for (int i = 0; i < STEPS; i++) {
            thetaSmeared = Helpers.gauss(0, theta);
            distance = stepSize / Math.cos(thetaSmeared); // Hypotenuse

            momentum -= energyLoss * Math.abs(distance);
            direction += thetaSmeared;
            azimuth += stepSize * Math.tan(thetaSmeared);

            if (momentum <= 0)
                return false;
        }

        particle.setMomentum(momentum);
        particle.setDirection(direction);
        particle.setAzimuth(end, azimuth);

        return true;
    }
}
