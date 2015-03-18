
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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public double getThickness() { return thickness; }
    public void setThickness(double thickness) { this.thickness = thickness; }
}
