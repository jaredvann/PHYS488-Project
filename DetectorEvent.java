
/** Data class that holds the position and time of an event of a particle
  * hitting a detector */
class DetectorEvent {
	Point position;
	double time;

	public DetectorEvent(Point position, double time) {
		this.position = position;
		this.time = time;
	}
}
