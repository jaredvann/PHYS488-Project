
public abstract class Layer {
    private String name;
    private double distance;
    private double thickness;

    public Layer(String name, double distance, double thickness) {
        this.name = name;
        this.distance = distance;
        this.thickness = thickness;
    }

    public abstract double[] handle(double[] particle);

    public double getName() { return name; }
    public double setName(String name) { this.name = name; }

    public double getDistance() { return distance; }
    public double setDistance(double distance) { this.distance = distance; }

    public double getThickness() { return thickness; }
    public double setThickness(double thickness) { this.thickness = thickness; }
}
