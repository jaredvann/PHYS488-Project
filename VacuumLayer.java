
public class VacuumLayer extends Layer {
    private double field;

    public VacuumLayer(String n, double d, double t, double f) {
        super(n, d, t);
        this.field = f;
    }

    public void handle(Particle particle) {
        double[] angles = getAngles(
            particle.getMomentum(),
            particle.getDirection(),
            particle.getPosition(),
            distance,
            (distance + thickness)
        );

        // System.out.println(angles[0]);
        // System.out.println("\n");

        particle.setPosition(
            distance+thickness,
            angles[0]
        );
    }

    public double getXPrime(double momentum, double dRadius) {
        return Math.pow(dRadius, 2) / (2*getRadius(momentum));
    }

    public double getX(double xPrime, double yPrime, double theta) {
        return xPrime*Math.cos(theta) + yPrime*Math.sin(theta);
    }

    public double[] getYPrime(double xPrime, double dRadius) {
        double yPrime = Math.sqrt(Math.pow(dRadius,2)-Math.pow(xPrime,2));
        return new double[]{ yPrime, -yPrime };
    }

    public double getY(double xPrime, double yPrime, double theta) {
        return yPrime*Math.cos(theta) - xPrime*Math.sin(theta);
    }

    public double[] getAngles(double momentum, double dir, double pos, double r1, double r2) {
        double rad = getRadius(momentum);
        double dSquared = Math.pow(rad, 2) + Math.pow(r2, 2) - 2*r2*rad*Math.cos(dir - pos);

        // System.out.println(dSquared);

        double x = (dSquared + Math.pow(r2, 2) - Math.pow(getRadius(momentum), 2)) / (2*Math.sqrt(dSquared));
        double y = Math.sqrt(Math.pow(r2, 2) - Math.pow(x, 2));
        double[] solutions = new double[2];
        solutions[0] = getAngle(y, x);

        // double theta = Math.PI/2 - dir;
        // double xPrime = getXPrime(momentum, dRadius);
        // double yPrime = getYPrime(xPrime, dRadius)[0];

        // solutions[0] = getAngle(getY(xPrime, yPrime, theta), getX(xPrime, yPrime, theta));
        // solutions[1] = getAngle(getY(xPrime, (-1*yPrime), theta), getX(xPrime, (-1*yPrime), theta));

        return solutions;
    }

    private double getAngle(double y, double x) {
        return Math.atan2(y, x);
    }

    private double getRadius(double momentum) { //getRadius=d
        return momentum / (0.3*field);
    }
}
