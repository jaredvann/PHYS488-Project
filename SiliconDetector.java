
public class SiliconDetector extends Layer {
    private Histogram momenta;

    public SiliconDetector(double d, double t) {
        super(d, t);
        momenta = new Histogram();
    }

    public double[] handle(double[] particle) {
        return particle;
    }
}
