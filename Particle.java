
class Particle {
	double mass, momentum, angle, age;
	Point position;
	Vector direction;
	
	public Particle(
		double mass,
		double momentum,
		Point position,
		Vector direction,
		double age
	) {
		this.mass = mass;
		this.momentum = momentum;
		this.position = position;
		this.direction = direction;
		this.age = age;
	}
}
