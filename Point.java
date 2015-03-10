
/** Class that describes a point in space
  * 0-3 parameters can be passed depending on dimentionality
  * Unfilled dimensions default to 0.0 */
class Point {
	double x = 0;
	double y = 0;
	double z = 0;

	public Point() {}

	public Point(double x) {
		this.x = x;
	}

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
