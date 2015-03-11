
class TestDetector {

	public static void main(String[] args) {
		Detector detector = new Detector();

		Particle p1 = new Particle(0, 0, new PPoint(), 0, 0);
		System.out.println("Success adding particle p1 to detector: " + detector.handle(p1));

		Particle p2 = new Particle(0, 0, new PPoint(), 0, 0);
		System.out.println("Success adding particle p2 to detector: " + detector.handle(p2));

		System.out.println("Detector events list contents:");
		System.out.println(detector.getEvents());
	}
}
