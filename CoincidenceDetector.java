import java.io.IOException;

class CoincidenceDetector {

	private Config config;

	public double radiusA, radiusB, thickness, midradius;
	private double momentum_split, mag_field;

	private double min_momentum, max_momentum;

	public CoincidenceDetector(double radiusA, double radiusB) throws IOException {
		this.radiusA = radiusA;
		this.radiusB = radiusB;

		this.config = new Config("main");

		mag_field = config.getDouble("magField");

		min_momentum = config.getDouble("coincidenceMinMomentum");
		max_momentum = config.getDouble("coincidenceMaxMomentum");

		// These calculations only have to be done once
		thickness = radiusB - radiusA;
		midradius = (radiusA + radiusB)/2;
	}

	public boolean estimateMomentum(double angleAtA, double angleAtB) {
		// Find delta angle
		double delta = Math.atan(radiusB*(angleAtA - angleAtB)/thickness);

		//Find radius of particle trajectory
		double r = 2*delta; //TODO - check this

		// Estimate momentum
		double momentum = 0.3*mag_field*r/(2*delta);

		// Return whether momentum is in accepted range
		return (momentum > min_momentum || momentum < max_momentum);
	}
}
