
class Attenuator {

	public int atomicNumber;
	public double massNumber, density;
	public double mcsPreCalc, eLossPreCalc, two_me_over_iM;

	public Attenuator (int atomicNumber, double massNumber, double density, double stepSize) {
		this.atomicNumber = atomicNumber;
		this.massNumber = massNumber;
		this.density = density;

		// Precalculate most of mcsTheta equation to save repetitive calculations.
		// Approximates step size as constant.
		// May introduce slight errors at far edge of material.
		double sxa = stepSize * density/x0();
		double q = Helpers.CHARGE_ELECTRON;
		mcsPreCalc = 13.6 * q * Math.sqrt(sxa) * (1 + 0.038 * Math.log(sxa));

		// Precalculate some of energyLoss eqaution to save repetitive calculations.
		// Does not introduce approximations.
		eLossPreCalc = 0.307 * density * atomicNumber/massNumber;
		// 2 * Electron Mass / Inertia Moment
		two_me_over_iM = 2 * Helpers.MASS_ELECTRON / (0.0000135 * atomicNumber);
	}

	public double getEnergyLoss(double momentum, double mass) {
		double m_e = Helpers.MASS_ELECTRON;

		double b2 = Math.pow(Helpers.beta(mass, momentum), 2); // Beta(p)^2
		double g2 = 1/(1 - b2); // Gamma(p)^2

		return eLossPreCalc * 1/b2 * ( Math.log(two_me_over_iM * b2 * g2) - b2 ); //MeV
	}

	public double x0() {
		int atm = atomicNumber;

		double numer = 716.4 * massNumber; // Numerator
		double denom = atm * (atm+1) * Math.log(287 / Math.sqrt(atm)); // Denominator

		return (numer / denom); // Fraction
	}

	public double getMCSTheta0(double momentum, double mass) {
		return mcsPreCalc/(momentum*Helpers.beta(mass, momentum)); //radians
	}
}