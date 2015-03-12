
class Particle {
	double mass, momentum, angle, age;
	Point position;
	Vector direction;

	public Particle(
		double mass,
		double momentum,
		Point position,
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
