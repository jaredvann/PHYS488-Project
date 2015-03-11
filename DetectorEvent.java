
/** Data class that holds the position and time of an event of a particle
  * hitting a detector */
class DetectorEvent {
	PPoint position;
	double time;

	public DetectorEvent(PPoint position, double time) {
		this.position = position;
		this.time = time;
	}
}
