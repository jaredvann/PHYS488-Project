class FieldLayer extends Layer {
    public double field;

    // Maximum number of steps to iterate through for each particle
    private static final int STEPS = 100;

    public FieldLayer(double _start,
                      double _end,
                      double _field) {
        super(_start, _end);

        field = _field;
    }

    public boolean handle(Particle p) {
        double pX, pY, lX, lY, lTheta;

        // Step size is proportional to layer thickness and inversely
        // proportional to the number of steps. This makes sure particles have
        // a good chance of making it through the layer, whilst keeping good
        // accuracy.
        double stepSize = (end - start) / STEPS; // m

        // Find the angle the particle momentum is changed by through one step
        // Multiplication by 1000 is to convert into GeV/c
        double theta = (stepSize * (1000 * 0.3 * field)) / p.momentum;

        // Convert particle azimuthal angle into cartesian coordinates
        pX = start * Math.cos(p.azimuth);
        pY = start * Math.sin(p.azimuth);

        for (int i = 0; i < (STEPS * 5); i++) {
            // Calculate the angle of 'L'
            lTheta = p.direction - theta*p.charge/2;

            // Create 'L' vector in cartesian coordinates
            // 'L' is the straight line distance the particle moves in the step
            lX = stepSize * Math.cos(lTheta);
            lY = stepSize * Math.sin(lTheta);

            // Add L to the particle position
            pX = pX + lX;
            pY = pY + lY;

            // Bend the angle of the momentum vector depending on charge
            p.direction -= theta*p.charge;

            // End loop if particle leaves field layer
            if ((pX*pX + pY*pY) >= end*end) {
                // Convert particle position back into polar coordinates
                p.azimuth = Math.atan2(pY, pX);

                return true;
            }
        }

        return false;
    }
}
