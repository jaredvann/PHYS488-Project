
/** Class that describes a direction in space
  * 0-3 parameters can be passed depending on dimentionality
  * Unfilled dimensions default to 0.0 */
class Vector {
	double x = 0;
	double y = 0;
	double z = 0;

	public Vector() {}

	public Vector(double x) {
		this.x = x;
	}

	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
