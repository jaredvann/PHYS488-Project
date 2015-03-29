class FieldLayer extends Layer {

	private double field, stepSize;
	private int steps = 100;

	public FieldLayer(String _name,
					   double _start,
					   double _end,
					   double _field
	) {
		super(_name, _start, _end);

		field = _field;

		// Overestimate step size as path travelled by particle will be greater than layer thickness
		stepSize = (end-start) * 1.5 / steps;
	}

	public boolean handle(Particle p) {
		double pX, pY, pTheta, pRadius, pDirection, pCharge;
		double lX, lY, lTheta;
		double trajectoryRadius, theta;

		// Set particle radius to inside edge of field layer
		pRadius = start;

		// Store particle properties as local variables
		pTheta = p.getPosition();
		pDirection = p.getDirection();
		pCharge = p.getCharge();

		// Find the angle the particle momentum is changed by through one step
		// 1000 is to convert into GeV/c
		theta = stepSize * 1000 * 0.3 * field / p.getMomentum();

		// Convert particle position into cartesian coordinates
		pX = pRadius*Math.cos(pTheta);
		pY = pRadius*Math.sin(pTheta);

		for (int i = 0; i < steps; i++) {
			// Calculate the angle of 'L'
			lTheta = pDirection + theta*-pCharge/2;

			// Create 'L' vector in cartesian coordinates
			// 'L' is the straight line distance the particle moves in the step
			lX = stepSize*Math.cos(lTheta);
			lY = stepSize*Math.sin(lTheta);

			// Add L to the particle position
			pX = pX + lX;
			pY = pY + lY;

			// Bend the angle of the momentum vector depending on charge
			pDirection = pDirection + theta*-pCharge;

			// End loop if particle leaves field layer
			if ((pX*pX + pY*pY) >= end*end) {
				// Convert particle position back into polar coordinates
				pRadius = pX*pX + pY*pY;
				pTheta = Math.atan2(pY, pX);

				// Update particle properties
				p.setPosition(pTheta);
				p.setDirection(pDirection);
				
				return true;
			}
		}


		return false;
	}
}
