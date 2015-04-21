public class AttenuatorLayer extends Layer {
    // Maximum amount of steps to reach end of layer before giving up.
    private static final int STEPS = 40;

    private double stepSize;
    private Attenuator attn;

    public AttenuatorLayer(String _name,
                           double _start,
                           double _end,
                           Attenuator _attn) {
        super(_name, _start, _end);

        // Step size proportional to thickness of attenuator to give a good
        // change of particles getting through
        stepSize = getThickness() / STEPS;
        attn = _attn;
    }

    public boolean handle(Particle particle) {
        // Create local variables of particle properties
        double mass = particle.getMass();
        double momentum = particle.getMomentum();
        double direction = particle.getDirection();
        double azimuth = particle.getAzimuth();

        // Energy loss and theta dependent on momentum which varies as particle
        // is attenuated, however changes in momentum are so small, as an
        // approximation, all steps can use the same value to save calculation
        // in each step
        double energyLoss = attn.getEnergyLoss(mass, momentum);
        double theta = attn.getTheta(mass, momentum);

        double thetaSmeared;

        double distance;

        // For each step update the particles momentum and direction according
        // to values obtained from the Attenuator class
        for (int i = 0; i < STEPS; i++) {
            thetaSmeared = Helpers.gauss(0, theta);
            distance = stepSize / Math.cos(thetaSmeared); // Hypotenuse

            momentum -= energyLoss * Math.abs(distance);
            direction += thetaSmeared;
            azimuth += stepSize * Math.tan(thetaSmeared);

            if (momentum <= 0)
                return false;
        }

        // Update particle instance with new properties
        particle.setMomentum(momentum);
        particle.setDirection(direction);
        particle.setAzimuth(end, azimuth);

        // Particle has successfully made it through the attenuator layer
        return true;
    }
}
