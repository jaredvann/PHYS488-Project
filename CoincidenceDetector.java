import java.io.IOException;

class CoincidenceDetector {
	private Config config;

	public double radiusA, radiusB;
	public double thickness, range;

	private double midRadius;
	private double momentum_split, mag_field;

	public CoincidenceDetector(double radiusA, double radiusB, double thickness) throws IOException {
		this.config = new Config("config.properties");

		mag_field = config.getDouble("magField");

		this.radiusA = radiusA;
		this.radiusB = radiusB;
		this.thickness = thickness;

		// These calculations only have to be done once
		range = radiusB - (radiusA + thickness);
		midRadius = radiusA + thickness + range/2;
	}

	public double estimateMomentum(double angleAtA, double angleAtB) {
		// Find delta angle
		double delta = Math.abs(Math.atan(radiusB*(angleAtB - angleAtA)/range));

		// Estimate and return momentum
		return 0.3*mag_field*midRadius/(2*delta);
	}
}
