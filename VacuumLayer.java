
public class VacuumLayer extends Layer {
    private double field;

    public VacuumLayer(String n,
                       double s,
                       double e,
                       double _field) {
        super(n, s, e);

        field = _field;
    }

    public boolean handle(Particle particle) {
        double nextAngle = getNextAngle(particle);

        System.out.println(particle.getPosition() + " -> " + nextAngle);

        particle.setPosition(end, nextAngle);

        return true;
    }

    public double getNextAngle(Particle particle) {
        double pRadius = getRadius(particle.getMomentum());
        double pDirection = particle.getDirection();

        double pMotionCenterX = particle.getX(0) + pRadius*Math.sin(pDirection);
        double pMotionCenterY = particle.getY(0) - pRadius*Math.cos(pDirection);

        System.out.println(pMotionCenterY);

        double d2 = Math.pow(pMotionCenterX, 2) + Math.pow(pMotionCenterY, 2);
        double x = (d2 - Math.pow(pRadius, 2) + Math.pow(end, 2)) / (2*Math.sqrt(d2));
        double y = Math.sqrt(Math.pow(end, 2) + Math.pow(x, 2));

        return Math.atan2(y, x);
    }

    private double getRadius(double momentum) {
        return momentum / (0.3 * field);
    }
}
