import java.util.Random;

class Helpers {
	private static Random value = new Random();

	public static final double MASS_MUON = 106; // MeV
	public static final double MASS_ELECTRON = 0.511; // MeV

	public static final int CHARGE_ELECTRON = 1; // 1.60E-19 C

	public static final double SPEED_OF_LIGHT = 2.99792458E8;

	public static double beta(double mass, double momentum) {
		return momentum / energy(mass, momentum);
	}

	public static double gamma(double mass, double momentum) {
		double b = beta(mass, momentum);
		return 1 / Math.sqrt(1 - b*b);
	}

	public static double energy(double mass, double momentum) {
		return Math.sqrt(mass*mass + momentum*momentum);
	}

	public static double momentum(double mass, double energy) {
		return Math.sqrt(energy*energy - mass*mass);
	}

	public static double gauss(double xmean, double sigma){
		// Return a random number with a gaussian distribution
		double sum = 0;

		for (int n=0; n<=11; n++)
			sum += value.nextDouble(); // use the class Random to make a number

		return xmean + sigma*(sum-6);
	}
}
