import java.util.List;
import java.util.ArrayList;

class DetectorLayer extends AttenuatorLayer {
    private List<Double> hits;

    public DetectorLayer(String _name,
                         double _start,
                         double _end,
                         Attenuator _attn) {
        super(_name, _start, _end, _attn);

        hits = new ArrayList<Double>();
    }

    public DetectorLayer(String _name,
                         double _radius) {
        this(_name, _radius, 0, null);
    }

    @Override
    public boolean handle(Particle p) {
        addHit(p.getAzimuth());

        return super.handle(p);
    }

    public void addHit(double h) { hits.add(h); }
    public List<Double> getHits() { return hits; }
}
