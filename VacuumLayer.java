
public class VacuumLayer extends Layer {
    private double field;

    public VacuumLayer(String n,
                       double s,
                       double e,
                       double _field) {
        super(n, s, e);

        field = _field;
    }

    public void handle(Particle particle) {
        double[] angles = getAngles(particle);

        particle.setPosition(end, angles[0]);
        
    }

    public double[] getAngles(Particle particle) {
        double[] solutions = new double[2];

        double pRadius = getRadius(particle.getMomentum());
        double pDirection = getRadius(particle.getDirection());

        double pMotionCenterX = particle.getX(start) + getXPrime(pRadius, pDirection);
        double pMotionCenterY = particle.getY(start) - getYPrime(pRadius, pDirection);

        double d = Math.sqrt(Math.pow(pMotionCenterX, 2) + Math.pow(pMotionCenterY, 2));
        double x = (Math.pow(d, 2) - Math.pow(pRadius, 2) + Math.pow(end, 2)) / (2 * d);
        double y = Math.sqrt(Math.pow(end, 2) - Math.pow(x, 2));

        solutions[0] = Math.atan2(y, x);
        solutions[1] = Math.atan2(y, x);

        return solutions;
    }

    public double getXPrime(double pRadius, double pDirection) {
        return pRadius * Math.sin(pDirection);
    }

    public double getYPrime(double pRadius, double pDirection) {
        return pRadius * Math.cos(pDirection);
    }

    private double getRadius(double momentum) {
        return momentum / (0.3 * field);
    }
}
