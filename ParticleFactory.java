import java.io.*;
import java.util.Random;

class ParticleFactory {
	private static Random random;

	private static double[] masses;

	private static double minP;
	private static double maxP;

	public ParticleFactory(double _minP, double _maxP, double[] _masses)  {
		random = new Random();

		minP = _minP;
		maxP = _maxP;
		masses = _masses;
	}

	public static Particle newParticle() {
		Particle particle = new Particle(
			masses[random.nextInt(masses.length)],
			random.nextDouble()*(maxP-minP) + minP,
			Math.PI/2,
			0
		);

		return particle;
	}
}
