// class BFieldLayer extends Layer {
//
// 	private double field;
//
// 	public BFieldLayer(String _name,
// 					   double _start,
// 					   double _end,
// 					   double _field
// 	) {
// 		super(_name, _start, _end);
//
// 		field = _field;
// 	}
//
// 	public boolean handle(Particle p) {
// 		double mom_mag = p.mom.magnitude();
//
// 		// 1000 is to convert into GeV/c
// 		double traj_radius = mom_mag / (1000 * 0.3 * field);
//
// 		// Find position of trajectory circle centre from normal of momentum
// 		Vec traj_centre = new Vec(
// 			(p.pos.x - traj_radius * p.mom.y / mom_mag),
// 			(p.pos.y + traj_radius * p.mom.x / mom_mag)
// 		);
//
// 		// Find angle to rotate coordinate system by
// 		double rotation_angle = Math.atan2(traj_centre.y, traj_centre.x);
//
// 		// Rotate trajectory centre point to align with x-axis
// 		Vec rot_traj_centre = traj_centre.rotate(-rotation_angle);
//
// 		// Using equations from http://mathworld.wolfram.com/Circle-CircleIntersection.html
// 		// Helper variables match up with webpage
// 		double r1 = end;
// 		double r2 = traj_radius;
// 		double d = rot_traj_centre.x;
//
// 		// Calculate x and y intersects
// 		double x_intersect = (d*d + r1*r1 - r2*r2)/(2*d);
// 		double y_intersect = Math.sqrt((r2-r1-d)*(r1-r2-d)*(r1+r2-d)*(r1+r2+d))/(2*d);
//
// 		// Check if particle trajectory intersects layer edge
// 		// Might be a bit hacky
// 		if (Double.isNaN(x_intersect) || Double.isNaN(y_intersect)) {
// 			return false;
// 		}
//
// 		// Create vectors of the circle intersection points
// 		Vec rot_cross_point_1 = new Vec(x_intersect, y_intersect);
// 		Vec rot_cross_point_2 = new Vec(x_intersect, -y_intersect);
//
// 		// Rotate back into normal reference frame
// 		Vec cross_point_1 = rot_cross_point_1.rotate(-rotation_angle);
// 		Vec cross_point_2 = rot_cross_point_2.rotate(-rotation_angle);
//
// 		// Flip y-axis points (can't remember why lol)
// 		cross_point_1.y = -cross_point_1.y;
// 		cross_point_2.y = -cross_point_2.y;
//
// 		// Choose the correct circle-circle intersection
// 		// (may not be accurate)
// 		if ( Helpers.isPos(p.mom.x) == Helpers.isPos(cross_point_1.x) &&
// 			 Helpers.isPos(p.mom.y) == Helpers.isPos(cross_point_1.y)
// 		) {
// 			setNewParams(p, cross_point_1.x, cross_point_1.y);
// 		} else {
// 			setNewParams(p, cross_point_1.x, cross_point_1.y);
// 		}
//
// 		return true;
// 	}
//
// 	private Particle setNewParams(Particle p, double x, double y) {
// 		// Update position
// 		p.pos.x = x;
// 		p.pos.y = y;
//
// 		//Find angle between original momentum and new position
// 		double mom_angle = p.mom.angle();
// 		double pos_angle = p.pos.angle();
// 		double delta = pos_angle - mom_angle;
//
// 		//Rotate momentum to exit angle
// 		p.mom = p.mom.rotate(delta);
//
// 		return p;
// 	}
// }
