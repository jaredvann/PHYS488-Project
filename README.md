# PHYS488-Project
Team Tom, Jared, Emma &amp; Jon!

### Super LHC track-trigger project
Feasibility/design study for a track trigger to be implemented Study in the ATLAS experiment at the LHC.

The LHC and its proposed successor the super-LHC produce billions of collisions per second. Experiments, such as the ATLAS experiment, can record at best a few hundred such collisions per second. It is therefore required to accept or reject collisions on-the-fly based on a very fast determination of the properties of these collisions.

An important selection criterion can be the presence of a trajectory in the tracking detectors of the experiment, originating from a high momentum lepton. You will study whether a very simple 2 layer coincidence detector could be used for this purpose. As part of the project you will generate muons, which you will track through a series of thin detectors located in a magnetic field, and generate hits in two of these detectors. Using these hits you then assess how well these data allow you to separate between very abundant low momentum particle and rare high momentum particles. At a later stage you can study how well you would expect this performance to hold up in the presence of thousands of other particles from the same collision.

-------------

### Code delegation
* Simulation - Tom & Jared
* MuonFactory - Jon
* Trajectory - Emma
* CoincidenceDetector - Jared & Tom

-------------

### Class structure

```java

/** The glue of the project - links the other classes together and makes 
comparisons of the data at the end. */
class Simulation {
	public double[] muons;

	public main(String[] args);
	
	public run();
}

/** Creates the Muons in their starting conditions at the origin, returns 
a random starting angle theta and momentum value. */
class MuonFactory {
	public MuonFactory();
	
	//double[] = {theta, momentum}
	public double[2] new();
}

/** Calculates a Muons trajectory through the detector taking into account 
the magnetic field. Takes a Muons momentum and angle of travel and the 
radius (distance) through the detector to run for. Returns the angle that 
the Muon hits the detector with given radius. */
class Trajectory {
	public Trajectory();
	
	//double = angle of detector hit
	public double run(double[2] theta_and_mom, double radius);
}

/** Estimates whether the Muon is high or low momentum. Is given the two 
angles that the Muon intersects the two coincidence detectors and returns 
an estimate of whether the Muon is has high or low momentum. */
class CoincidenceDetector {
	private double radius_1;
	private double radius_2;

	public CoincidenceDetector(double radius1, double radius2);

	public boolean estimateMomentum(double angleAtA, double angleAtB);
}

/** Tom's amazing histogram class. */
class Histogram {};

/** Useful things and stuff */
class Helpers {};

```
