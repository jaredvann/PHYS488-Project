import java.io.IOException;

class CoincidenceDetector {
	private Config config;

	public double radiusA, radiusB;
	public double thickness, range;

	private double midRadius;
	private double mag_field;

	public CoincidenceDetector(double _rA, double _rB, double _thickness) throws IOException {
		config = new Config("config.properties");
		mag_field = config.getDouble("magField");

		radiusA = _rA;
		radiusB = _rB;
		thickness = _thickness;

		// These calculations only have to be done once
		range = radiusB - (radiusA + thickness);
		midRadius = radiusA + thickness + range/2;
	}

	public double estimateMomentum(double angleAtA, double angleAtB) {
		// Find delta angle
		double delta = Math.abs(Math.atan(radiusB*(angleAtB - angleAtA)/range));

		// Estimate and return momentum (*100 to convert GeV -> MeV)
		return 1000 * 0.3*mag_field*midRadius/(2*delta);
	}
}
