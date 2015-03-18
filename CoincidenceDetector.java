import java.io.IOException;

class CoincidenceDetector {
	private Config config;

	public double radiusA, radiusB;
	private double thickness, midradius;
	private double momentum_split, mag_field;

	public CoincidenceDetector() throws IOException {
		this.config = new Config("main");

		mag_field = config.getDouble("magField");

		radiusA = config.getDouble("coincidenceDetectorRadiusA");
		radiusB = config.getDouble("coincidenceDetectorRadiusB");

		// These calculations only have to be done once
		thickness = radiusB - radiusA;
		midradius = radiusA + thickness/2;
	}

	public double estimateMomentum(double angleAtA, double angleAtB) {
		// Find delta angle
		double delta = Math.abs(Math.atan(radiusB*(angleAtB - angleAtA)/thickness));

		// Estimate momentum
		double momentum = 0.3*mag_field*midradius/(2*delta);

		// Return momentum
		return momentum;
	}
}
