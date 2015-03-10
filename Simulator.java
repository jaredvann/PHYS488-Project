
/**
 *
 */
public class Simulator {

    private muonFactory;

    private ArrayList<Handler> handlers;

    private Detector triggerA;
    private Detector triggerB;

    public Simulator(double energyLow, double energyHigh) {
        muonFactory = new MuonFactory(energyLow, energyHigh);

        handlers = new ArrayList<>();

        triggerA = new Detector();
        triggerB = new Detector();
    }

    public void add(Handler handler) { handler.add(handler); }

    public void simulate(Particle p) {
        for (Handler h : handlers)
            h.handle(p);

        triggerA.handle();
        triggerB.handle();
    }

    public static void main(String[] args) {
        Simulator me = new Simulator();
        me.add(new Trajectory());

        simulate(muonFactory.newParticle());
    }
}
