class Attenuator {
    public int atomicNumber;
    public double massNumber, density;

    public double mcsPreCalc, eLossPreCalc, two_me_over_iM;

    public Attenuator(int _atomicNumber,
                      double _massNumber,
                      double _density,
                      double stepSize) {
        atomicNumber = _atomicNumber;
        massNumber = _massNumber;
        density = _density;

        init(stepSize);
    }

    private void init(double stepSize) {
        // Precalculate most of mcsTheta equation to save repetitive calculations.
        // Approximates step size as constant.
        // May introduce slight errors at far edge of material.
        double xa = x0() / density;
        mcsPreCalc =
                13.6 * Helpers.CHARGE_ELECTRON *
                        Math.sqrt(stepSize / xa) * (1 + 0.038 * Math.log(stepSize / xa));

        // Precalculate some of energyLoss eqaution to save repetitive calculations.
        // Does not introduce approximations.
        eLossPreCalc = 0.307 * density * atomicNumber/massNumber;
        // 2 * Electron Mass / Inertia Moment
        two_me_over_iM = 2 * Helpers.MASS_ELECTRON / (0.0000135 * atomicNumber);
    }

    private double x0() {
        double numer = 716.4 * massNumber; // Numerator
        double denom =
                atomicNumber * (atomicNumber+1) *
                        Math.log(287 / Math.sqrt(atomicNumber)); // Denominator

        return (numer / denom); // Fraction
    }

    public double getEnergyLoss(double mass, double momentum) {
        double b2 = Math.pow(Helpers.beta(mass, momentum), 2); // Beta(p)^2
        double g2 = 1/(1 - b2); // Gamma(p)^2

        return eLossPreCalc * (1/b2) * (Math.log(two_me_over_iM * b2 * g2) - b2); //MeV
    }

    public double getTheta(double mass, double momentum) {
        return mcsPreCalc / (momentum * Helpers.beta(mass, momentum)); //radians
    }
}