import java.util.ArrayList;

class Detector implements Handler {

	private ArrayList<DetectorEvent> events = new ArrayList<DetectorEvent>();

	public boolean handle(Particle p) {
		events.add(new DetectorEvent(p.position, p.age));
		return true;
	}

	public ArrayList<DetectorEvent> getEvents() {
		return events;
	}
}
