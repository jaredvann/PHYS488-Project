
public abstract class Layer {
    private double distance;
    private double thickness;

    public Layer(double distance, double thickness) {
        this.distance = distance;
        this.thickness = thickness;
    }

    public abstract double[] handle(double[] particle);

    public double getDistance() { return distance; }
    public double setDistance(double distance) { this.distance = distance; }

    public double getThickness() { return thickness; }
    public double setThickness(double thickness) { this.thickness = thickness; }
}
