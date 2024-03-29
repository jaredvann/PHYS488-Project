// All lengths in mm

// Import statements
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;


public class DetectorViewer extends Application {
    // Window size, pixels
    private int width = 800;
    private int height = 800;

    // Radius of detector
    private double radius = 1000;
    private double scale_factor;

    private static ArrayList<Layer> layers = new ArrayList<Layer>();
    private static ArrayList<DetectorLayer> detector_layers = new ArrayList<DetectorLayer>();

    public static void main(String[] args) throws IOException {
        double[] beryllium = {35};
        double[] silicon = {45, 80, 120, 180, 300, 400, 500, 700};
        double[] coincidence = {900, 910};

        // Create all layers
        for (double r : beryllium)
            layers.add(new Layer(r, r+6));

        for (double r : silicon)
            layers.add(new Layer(r, r+2));

        for (double r : coincidence)
            layers.add(new Layer(r, r+1));

        for (double r : silicon)
            detector_layers.add(new DetectorLayer(r, 0, null));

        for (double r : coincidence)
            detector_layers.add(new DetectorLayer(r, 0, null));

        // Load hits data
        ArrayList<double[]> data = Helpers.read_CSV("layers.csv");

        // Add data to relevant detectors
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(i).length; j++) {
                detector_layers.get(i).addHit(data.get(i)[j]);
            }
        }

        launch(args);
    }

    public DetectorViewer() {
        scale_factor = 0.5*width/radius;
    }

    @Override
    public void start(Stage stage) {
        // Sort layers into correct order, will not draw correctly otherwise
        layers.sort((l1, l2) -> (new Double(l2.start)).compareTo(l1.start));

        // Setup JavaFX canvas
        Group root = new Group();
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Everything is drawn here
        drawDetector(gc);
        drawPoints(gc);

        // Setup window and show
        stage.setTitle("DetectorViewer");
        root.getChildren().add(canvas);
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void drawDetector(GraphicsContext gc) {
        double r;

        for (Layer layer : layers) {
            r = layer.end * scale_factor;
            gc.setFill(Color.GREY);
            gc.fillOval(width/2-r, height/2-r, r*2, r*2);

            r = layer.start * scale_factor;
            gc.setFill(Color.WHITE);
            gc.fillOval(width/2-r, height/2-r, r*2, r*2);
        }
    }

    private void drawPoints(GraphicsContext gc) {
        double r; double r2 = 4; double angle;

        for (DetectorLayer detector: detector_layers) {
            r = detector.start * scale_factor;
            for (int i = 0; i < detector.hits.size(); i++) {
                angle = detector.hits.get(i);
                gc.setFill(Color.BLACK);
                gc.fillOval(
                    width/2 + r*Math.cos(angle) - r2/2,
                    height/2 - r*Math.sin(angle) - r2/2,
                    r2, r2
                );
            }
        }
    }
}
