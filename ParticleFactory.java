import java.io.*;
import java.util.Random;

class ParticleFactory {

	static Random random = new Random();

	static double[] masses;
	static double minP, maxP, nextP;

	public ParticleFactory(double minP, double maxP, double[] masses)  {
		this.minP = minP;
		this.maxP = maxP;
		this.masses = masses;
	}

	//double[] = {mass, momentum, theta}
	public static double[] newParticle() {
		double[] particle = {
			masses[random.nextInt(masses.length)],	// Mass
			random.nextDouble()*(maxP-minP) + minP,	// Momentum
			random.nextDouble()*Math.PI*2			// Theta
		};
		return particle;
	}
}
