

class TestFieldLayer {

    public static void main(String[] args) {
        FieldLayer fieldLayer = new FieldLayer(0.0, 1.0, 1.0);

        Particle p = new Particle(100.0, 1000.0, 0.0, 0.0); // mass, momentum, direction, azimuth

        boolean result = fieldLayer.handle(p);

        System.out.println(result);
        System.out.println(p.trace);
        System.out.println(p.azimuth);
    }
}
