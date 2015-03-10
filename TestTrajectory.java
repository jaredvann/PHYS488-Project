
class TestTrajectory {

	public static void main(String[] args) {
		Field field = new BasicField();
		Trajectory trajectory = new Trajectory(field);

		Particle p1 = new Particle(0, 0, new Point(), new Vector(), 0);

		System.out.println(trajectory.handle(p1));

	}
}

class BasicField implements Field {

	public Vector field(Point position) {
		return new Vector(0.0, 0.0, 1.0);
	}
}
