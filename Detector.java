import java.util.ArrayList;

class Detector implements Handler {

	private ArrayList<DetectorEvent> events = new ArrayList<DetectorEvent>();

	/** Adds the position and time of a particle hitting a detector as a
	  * new DetectorEvent instance to the events list */
	public boolean handle(Particle p) {
		events.add(new DetectorEvent(p.position, p.age));
		return true;
	}

	/** Returns a list of all events that are stored as DetectorEvents */
	public ArrayList<DetectorEvent> getEvents() {
		return events;
	}
}
