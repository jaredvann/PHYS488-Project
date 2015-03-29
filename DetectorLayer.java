import java.util.List;
import java.util.ArrayList;

class DetectorLayer extends Layer {

	private List<Double> hits;

	private double z_detectors, r_detectors;
	private double z_resolution, r_resolution, angle;
	private int bin;

	public DetectorLayer(String _name,
						 double _radius
	) {
		super(_name, _radius, _radius);

		// TODO - resolution and detectors variables for future use
		r_detectors = z_detectors = 1;

		r_resolution = 2*Math.PI;
		// z_resolution = 1;

		hits = new ArrayList<Double>();
	}

	public DetectorLayer(String _name,
						 double _radius,
						 double _r_detectors,
						 double _z_detectors
	) {
		super(_name, _radius, _radius);

		r_detectors = _r_detectors;
		// z_detectors = _z_detectors;

		r_resolution = 2*Math.PI / r_detectors;
		// z_resolution = 1 / z_detectors;

		hits = new ArrayList<Double>();
	}

	public boolean handle(Particle p) {
		hits.add(p.getPosition());
		return true;
	}

	public List<Double> getHits() { return hits; }
}
