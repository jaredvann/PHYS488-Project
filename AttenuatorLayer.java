
public class AttenuatorLayer extends Layer {
    private static final int steps = 20;
    private Attenuator attn;

    public AttenuatorLayer(String n, double d, double t, Attenuator attn) {
        super(n, d, t);

        this.attn = attn;
    }

    public double[] handle(double[] particle) {
        for (int i = 0; i < steps; i++) {
            particle[1] -= attn.getEnergyLoss(particle[1], particle[0]);

            if (particle[1] <= 0) {
                particle[1] = 0;
                break;
            }
        }

        return particle;
    }
}
