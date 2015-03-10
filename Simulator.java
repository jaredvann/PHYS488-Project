
/**
 *
 */
public class Simulator {

    private static final screen = new PrintWriter(System.out);

    private muonFactory;

    private ArrayList<Handler> handlers;

    public Simulator(double energyLow, double energyHigh) {
        muonFactory = new MuonFactory(energyLow, energyHigh);

        handlers = new ArrayList<Handler>();
    }

    public void add(Handler handler) { this.handlers.add(handler); }

    public boolean simulate(Particle p) {
        for (Handler h : handlers) {
            if (!h.handle(p)) {
                return false;
                break;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        Simulator me = new Simulator();
        me.add(new Trajectory());

        // Coincidence detector
        me.add(new Detector());
        me.add(new Trajectory());
        me.add(new Detector());

        Particle p = muonFactory.newParticle();

        if (simulate(p))
            screen.println("Simulation successful!");
        else
            screen.println("Simulation unsuccessful!");
    }
}
