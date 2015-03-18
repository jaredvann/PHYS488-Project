
public class VacuumLayer extends Layer {
    private Trajectory trajectory;
    
    public VacuumLayer(String n, double d, double t, Trajectory trajectory) {
        super(n, d, t);

        this.trajectory = trajectory;
    }

    public double[] handle(double[] particle) {
        particle[2] = trajectory.getAngles(particle[1], particle[2], (distance+thickness))[0];
        return particle;
    }
}
