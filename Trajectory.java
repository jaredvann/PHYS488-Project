class Trajectory {
    double field;

    public Trajectory(double field){
        this.field = field;
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

    public double[] getAngles(double momentum, double theta0, double dRadius) {
        double[] solutions = new double[2];

        double theta = Math.PI/2 - theta0;
        double xPrime = getXPrime(momentum, dRadius);
        double yPrime = getYPrime(xPrime, dRadius)[0];

        solutions[0] = getAngle(getY(xPrime, yPrime, theta), getX(xPrime, yPrime, theta));
        solutions[1] = getAngle(getY(xPrime, (-1*yPrime), theta), getX(xPrime, (-1*yPrime), theta));

        return solutions;
    }

    private double getAngle(double y, double x) {
        return Math.atan2(y, x);
    }

    private double getRadius(double momentum) { //getRadius=d
        return momentum / (0.3*field);
    }
}
