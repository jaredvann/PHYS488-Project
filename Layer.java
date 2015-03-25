
public abstract class Layer {
    protected String name;

    protected double start;
    protected double end;

    public Layer(String _name, double _start, double _end) {
        name = _name;
        start = _start;
        end = _end;
    }

    public abstract void handle(Particle p);

    // ---------- Helpers ----------

    public double getThickness() { return end - start; }

    // ---------- Getters & Setters ----------

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getStart() { return start; }
    public void setStart(double _start) { start = _start; }

    public double getEnd() { return end; }
    public void setEnd(double _end) { end = _end; }
}
