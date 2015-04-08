import javafx.scene.paint.Color;

public class Layer {
    protected String name;

    protected double start;
    protected double end;

    protected Color color = new Color(0.5, 0.5, 0.5, 1.0);

    public Layer(String _name, double _start, double _end) {
        name = _name;
        start = _start;
        end = _end;
    }

    // ---------- Handlers ----------

    public boolean handle(Particle p) { return false; }

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
