
abstract class Layer {
    protected String name;
    protected double distance;
    protected double thickness;

    public Layer(String name, double distance, double thickness) {
        this.name = name;
        this.distance = distance;
        this.thickness = thickness;
    }

    public abstract void handle(Particle p);

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getThickness() { return thickness; }
    public void setThickness(double thickness) { this.thickness = thickness; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }
}
