import java.util.ArrayList;

/**
 * DetectorLayer extends AttenuatorLayer to add hit location tracking functionality.
 */
class DetectorLayer extends AttenuatorLayer {
    public ArrayList<Double> hits;

    /**
     * DetectorLayer constructor method.
     *
     * @param _start the start radius of the layer.
     * @param _end   the end radius of the layer.
     * @param _attn  an Attenuator instance representing the material of the detector.
     */
    public DetectorLayer(double _start,
                         double _end,
                         Attenuator _attn) {
        super(_start, _end, _attn);

        hits = new ArrayList<Double>();
    }

    /**
     * Extension of the AttenatorLayer handle method to add the hit tracking logic.
     *
     * @param p a particle instance.
     * @return  whether the particle successfully traversed the layer.
     */
    @Override
    public boolean handle(Particle p) {
        addHit(p.azimuth);

        return super.handle(p);
    }

    /**
     * Add the azimuthal hit angle to an internal memory of hit locations.
     *
     * @param h the azimuthal hit angle.
     */
    public void addHit(double h) { hits.add(h); }
}