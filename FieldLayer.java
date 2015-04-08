class FieldLayer extends Layer {
	private static int steps = 100;

	private double field;
	private double step_size;

	public FieldLayer(String _name,
					  double _start,
					  double _end,
					  double _field) {
		super(_name, _start, _end);

		field = _field;

		step_size = (end - start) * 3 / steps;
	}

	public boolean handle(Particle p) {
		double pX, pY;
		double lX, lY, lTheta;
		double trajectoryRadius;

		// Store particle properties as local variables
		double pAzimuth   = p.getAzimuth();
		double pDirection = p.getDirection();
		double pCharge    = p.getCharge();

		// Find the angle the particle momentum is changed by through one step
		// 1000 is to convert into GeV/c
		double theta = (step_size * 1000 * 0.3 * field) / p.getMomentum();

		// Convert particle azimuthal angle into cartesian coordinates
		pX = start * Math.cos(pAzimuth);
		pY = start * Math.sin(pAzimuth);

		for (int i = 0; i < steps; i++) {
			// Calculate the angle of 'L'
			lTheta = pDirection + theta*-pCharge/2;

			// Create 'L' vector in cartesian coordinates
			// 'L' is the straight line distance the particle moves in the step
			lX = step_size * Math.cos(lTheta);
			lY = step_size * Math.sin(lTheta);

			// Add L to the particle position
			pX = pX + lX;
			pY = pY + lY;

			// Bend the angle of the momentum vector depending on charge
			pDirection = pDirection + theta*-pCharge;

			// End loop if particle leaves field layer
			if ((pX*pX + pY*pY) >= end*end) {
				// Convert particle position back into polar coordinates
				pAzimuth = Math.atan2(pY, pX);

				// Update particle properties
				p.setAzimuth(end, pAzimuth);
				p.setDirection(pDirection);

				return true;
			}
		}

		return false;
	}
}
