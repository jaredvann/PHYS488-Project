import java.util.Random;

class ParticleFactory {
	private Random random;

	private double[] masses;

	private double momentum;
	private double smear;

	public ParticleFactory(double _momentum, double _smear, double[] _masses)  {
		random = new Random();

		momentum = _momentum;
		smear = _smear;

		masses = _masses;
	}

	public Particle newParticle() {
		double momentum = Helpers.gauss(this.momentum, this.momentum * smear);

		return new Particle(
			masses[random.nextInt(masses.length)],
			momentum,
			random.nextDouble()*(2*Math.PI),
			0
		);
	}
}
