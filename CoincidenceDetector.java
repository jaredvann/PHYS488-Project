
class CoincidenceDetector {

	private Config config = new Config("main");

	private double radius1, radius2, thickness, midradius;
	private double momentum_split, mag_field;

	public CoincidenceDetector(double radius1, double radius2) {
		this.radius1 = radius1;
		this.radius2 = radius2;

		mag_field = config.getDouble("magField");

		min_momentum = config.getDouble("coincidenceMinMomentum");
		max_momentum = config.getDouble("coincidenceMaxMomentum");

		// These calculations only have to be done once
		thickness = radius2 - radius1;
		midradius = (radius1 + radius2)/2;
	}

	public boolean estimateMomentum(double angleAtA, double angleAtB) {
		// Find delta angle
		double delta = Math.atan(radius2*(angleAtA - angleAtB)/thickness);

		//Find radius of particle trajectory
		double r = 2*delta; //TODO - check this

		// Estimate momentum
		double momentum = 0.3*mag_field*r/(2*delta);

		// Return whether momentum is in accepted range
		return (momentum > min_momentum || momentum < max_momentum);
	}
}
