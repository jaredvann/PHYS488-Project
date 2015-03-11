
class Particle {
	double mass, momentum, angle, age;
	PPoint position;

	public Particle(
		double mass,
		double momentum,
		PPoint position,
		double angle,
		double age
	) {
		this.mass = mass;
		this.momentum = momentum;
		this.position = position;
		this.angle = angle;
		this.age = age;
	}
}
