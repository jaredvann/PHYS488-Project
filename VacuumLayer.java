
public class VacuumLayer extends Layer {
    private Trajectory trajectory;

    public VacuumLayer(String n, double d, double t, Trajectory trajectory) {
        super(n, d, t);
        this.trajectory = trajectory;
    }

    public void handle(Particle particle) {
        particle.setPosition(
            trajectory.getAngles(
                particle.getMomentum(),
                particle.getDirection(),
                thickness
            )[0]
        );
    }
}
