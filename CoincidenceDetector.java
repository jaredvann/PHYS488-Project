import java.io.IOException;

class CoincidenceDetector {

	private Config config;

	public double radiusA, radiusB, thickness, midradius;
	private double momentum_split, mag_field;

	private double min_momentum, max_momentum;

	public CoincidenceDetector() throws IOException {
		this.config = new Config("main");

		mag_field = config.getDouble("magField");

		min_momentum = config.getDouble("coincidenceMinMomentum");
		max_momentum = config.getDouble("coincidenceMaxMomentum");

		radiusA = config.getDouble("coincidenceDetectorRadiusA");
		radiusB = config.getDouble("coincidenceDetectorRadiusB");

		// These calculations only have to be done once
		thickness = radiusB - radiusA;
		midradius = radiusA + thickness/2;
	}

	public boolean estimateMomentum(double angleAtA, double angleAtB) {
		System.out.println("A: " + angleAtA);
		System.out.println("B: " + angleAtB);

		// Find delta angle
		double delta = Math.abs(Math.atan(radiusB*(angleAtB - angleAtA)/thickness));

		// Estimate momentum
		double momentum = 0.3*mag_field*midradius/(2*delta);

		System.out.println("Delta: " + delta);

		System.out.println("Predicted momentum: " + momentum);

		// Return whether momentum is in accepted range
		return (momentum > min_momentum && momentum < max_momentum);
	}
}
