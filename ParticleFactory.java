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

	public static void main(String[] args) {
		minP = 100;
		maxP = 200;

		double[] p = newParticle();

		System.out.println(p[0]);
		System.out.println(p[1]);
		System.out.println(p[2]);
	}

	//double[] = {mass, momentum, theta}
	public static double[] newParticle() {
		double mass = masses[random.nextInt(masses.length)];
		double momentum = random.nextDouble()*(maxP-minP) + minP;
		double theta = random.nextDouble()*Math.PI*2;

		double[] particle = { mass, momentum, theta };
		return particle;
	}
}
