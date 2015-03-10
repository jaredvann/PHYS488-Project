
class Trajectory implements Handler {
	Field field;

	public Trajectory(Field field) {
		this.field = field;
	}

	public boolean handle(Particle p) {
		return false;
	}
}
