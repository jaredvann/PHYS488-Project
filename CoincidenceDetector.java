
class CoincidenceDetector {

	private Config config = new Config("main");

	private double radius1, radius2;
	private double momentum_split, mag_field;

	public CoincidenceDetector(double radius1, double radius2) {
		this.radius1 = radius1;
		this.radius2 = radius2;

		mag_field = config.getDouble("magField");

		min_momentum = config.getDouble("coincidenceMinMomentum");
		max_momentum = config.getDouble("coincidenceMaxMomentum");
	}

	public boolean estimateMomentum(double angleAtA, double angleAtB) {
		// Calculate delta angle from two position angles
		// Estimate whether momentum is high or low from delta angle

		// Delta angle
		double delta = Math.atan(radius2*(angleAtA - angleAtB)/(radius2 - radius1));

		// Estimated momentum
		double momentum = 0.3*mag_field*((radius1+radius2)/2)/(2*delta);

		// Return high low momentum
		return ((momentum > min_momentum) || (momentum < max_momentum));
	}
}
