
/**
 *
 */
public class Simulator {

    static muonFactory;

    public static void simulate(Handler[] handlers, Particle p) {
        for (Handler h : handlers)
            h.handle(p);
    }

    public static void main(String[] args) {
        muonFactory = new MuonFactory();

        Handler[] handlers = { new Trajectory(), new Detector() };
        simulate(handlers, muonFactory.newParticle());

        Screen.out.println("Hello, world!");
    }
}
