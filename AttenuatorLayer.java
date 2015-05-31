public class AttenuatorLayer extends Layer {
    // Maximum amount of steps to reach end of layer before giving up.
    private static final int STEPS = 40;

    private double stepSize;
    private Attenuator attn;

    public AttenuatorLayer(double _start,
                           double _end,
                           Attenuator _attn) {
        super(_start, _end);

        // Step size proportional to thickness of attenuator to give a good
        // change of particles getting through
        stepSize = (end - start) / STEPS;
        attn = _attn;
    }

    public boolean handle(Particle p) {
        // Energy loss and theta dependent on momentum which varies as particle
        // is attenuated, however changes in momentum are so small, as an
        // approximation, all steps can use the same value to save calculation
        // in each step
        double energyLoss = attn.getEnergyLoss(p.mass, p.momentum);
        double theta = attn.getTheta(p.mass, p.momentum);

        double thetaSmeared;

        double distance;

        // For each step update the particles momentum and direction according
        // to values obtained from the Attenuator class
        for (int i = 0; i < STEPS; i++) {
            thetaSmeared = Helpers.gauss(0, theta);
            distance = stepSize / Math.cos(thetaSmeared); // Hypotenuse

            p.momentum -= energyLoss * Math.abs(distance);
            p.direction += thetaSmeared;
            p.azimuth += stepSize * Math.tan(thetaSmeared);

            if (p.momentum <= 0) { return false; }
        }

        // Particle has successfully made it through the attenuator layer
        return true;
    }
}
