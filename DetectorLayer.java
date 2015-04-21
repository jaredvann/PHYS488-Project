import java.util.ArrayList;

class DetectorLayer extends AttenuatorLayer {
    public ArrayList<Double> hits;

    public DetectorLayer(double _start,
                         double _end,
                         Attenuator _attn) {
        super(_start, _end, _attn);

        hits = new ArrayList<Double>();
    }

    public DetectorLayer(double _radius) {
        this(_radius, 0, null);
    }

    @Override
    public boolean handle(Particle p) {
        addHit(p.azimuth);

        return super.handle(p);
    }

    public void addHit(double h) { hits.add(h); }
}
