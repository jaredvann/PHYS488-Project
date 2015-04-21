public class Layer {
    public String name;

    public double start;
    public double end;

    public Layer(double _start, double _end) {
        start = _start;
        end = _end;
    }

    // This method must be created by all implementations of the layer class
    public boolean handle(Particle p) { return false; }

    // Helper method
    public double getThickness() { return end - start; }
}
