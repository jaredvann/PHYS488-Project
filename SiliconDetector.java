
public class SiliconDetector extends Layer {
    private static final int stepSize = 20;

    private static Attenuator attn;

    private Histogram angles;
    private Histogram momenta;

    public SiliconDetector(String n, double d, double t) {
        super(n, d, t);

        attn = new Attenuator(14, 28.0855, 2.3290, t/stepSize);

        angles = new Histogram();
        momenta = new Histogram();
    }

    public double[] handle(double[] particle) {
        for (int i = 0; i < stepSize; i++) {
            particle[1] -= attn.getEnergyLoss(particle[0], particle[1]);

            if (particle[1] <= 0) {
                particle[1] = 0;
                break;
            }
        }

        return particle;
    }
}
