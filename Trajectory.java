
/** Class to calculate the trajectory of a particle in a magnetic field
  * through empty space */
class Trajectory {
	double depth;
	Field field;

	public Trajectory(double depth, Field field) {
		this.depth = depth;
		this.field = field;
	}

	public boolean handle(Particle p) {
		return false;
	}
}
