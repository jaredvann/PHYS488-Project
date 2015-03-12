import java.util.ArrayList;

class CoincidenceDetector {

	private Config config = new Config("main");

	private double radius1, radius2;
	private double momentum_split, mag_field;

	public CoincidenceDetector(double radius1, double radius2) {
		this.radius1 = radius1;
		this.radius2 = radius2;

		momentum_split = config.getDouble("coincidence_detector_momentum_highlow_split");
		mag_field = config.getDouble("mag_field");
	}

	public boolean estimateMomentum(double[2] angles) {
		// Calculate delta angle from two position angles
		// Estimate whether momentum is high or low from delta angle

		// Delta angle
		double delta = Math.atan(radius2*(angles[1] - angles[0])/(radius2 - radius1));

		// Estimated momentum
		double momentum = 0.3*mag_field*radius1/(2*delta) //CHECK RADIUS VALUE

		// Return high low momentum
		if (momentum >= momentum_split)
			return true;
		else
			return false;
	}
}
