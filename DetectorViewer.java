import java.util.*;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

public class DetectorViewer extends Application {

	public int width = 800;
	public int height = 800;

	public double radius = 1000;
	private double scale_factor;

	private double PI2 = Math.PI/2;

	static public ArrayList<DetectorLayer2> layers = new ArrayList<DetectorLayer2>();

	static public double[][] points;
	static public double[] detector_radii;
	static public int[] detector_resolutions;


	public static void main(String[] args) throws IOException {

		double[] beryllium = {35};
		double[] silicon = {45, 80, 120, 180, 300, 400, 500, 700};
		double[] coincidence = {900, 910};

		for (double r : beryllium)
			layers.add(new DetectorLayer2(r, r+6, Color.GREY));

		for (double r : silicon)
			layers.add(new DetectorLayer2(r, r+2, Color.GREY));

		for (double r : coincidence)
			layers.add(new DetectorLayer2(r, r+2, Color.GREY));


		double[] dr = {0, 100, 200, 300, 400, 500, 600, 700, 800, 900};
		detector_radii = dr;

		int[] dr2 = {360,360,360,360,360,360,360,360,360,360};
		detector_resolutions = dr2;

		points = importCSVData("data.csv", 10, 1);

		launch(args);
	}


	public DetectorViewer() {
		scale_factor = 0.5*width/radius;
	}


	private static double[][] importCSVData(String file_path, int detectors, int particles) throws IOException {
		double[][] points = new double[detectors][particles];
		double[] d_values = new double[particles];
		String[] s_values;
		BufferedReader br = null;
		String line = "";

		try {
			br = new BufferedReader(new FileReader(file_path));

			for (int i = 0; i < detectors; i++) {
				if ((line = br.readLine()) != null) {
					s_values = line.split(",");
					d_values = new double[particles];

					for (int j = 0; j < s_values.length; j++)
						d_values[j] = Double.parseDouble(s_values[j]);

					points[i] = d_values;
				}
			}
		}
		catch (FileNotFoundException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
		finally {
			if (br != null) {
				try { br.close(); }
				catch (IOException e) { e.printStackTrace(); }
			}
		}

		return points;
	}


	@Override
	public void start(Stage stage) {
		Group root = new Group();
		Canvas canvas = new Canvas(width, height);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		drawDetector(gc);
		drawPoints(gc);

		stage.setTitle("DetectorViewer");
		root.getChildren().add(canvas);
		stage.setScene(new Scene(root));
		stage.show();
	}


	private void drawDetector(GraphicsContext gc) {
		double r;

		for (int i = layers.size()-1; i >= 0; i--) {
			r = layers.get(i).outer_radius * scale_factor;
			gc.setFill(layers.get(i).color);
			gc.fillOval(width/2-r, height/2-r, r*2, r*2);

			r = layers.get(i).inner_radius * scale_factor;
			gc.setFill(Color.WHITE);
			gc.fillOval(width/2-r, height/2-r, r*2, r*2);
		}
	}


	private void drawPoints(GraphicsContext gc) {
		double r; double r2 = 4; double angle; double sf;

		for (int i = 0; i < points.length; i++) {
			r = detector_radii[i] * scale_factor;

			for (int j = 0; j < points[i].length; j++) {
				angle = points[i][j];

				gc.setFill(Color.PURPLE);
				gc.fillOval(
					width/2 + r*Math.cos(angle) - r2/2,
					height/2 - r*Math.sin(angle) - r2/2,
					r2, r2
				);
			}
		}
	}

	private Color randomColor() {
		return new Color(Math.random(), Math.random(), Math.random(), 1.0);
	}
}



class DetectorLayer2 {

	public double inner_radius, outer_radius, radius;
	public Color color = Color.GREY;
	public ArrayList<CollisionPoint> c_points = new ArrayList<CollisionPoint>();

	public DetectorLayer2(double inner_radius, double outer_radius) {
		this.inner_radius = inner_radius;
		this.outer_radius = outer_radius;
		this.radius = (this.outer_radius+this.inner_radius)/2;
	}

	public DetectorLayer2(double inner_radius, double outer_radius, Color color) {
		this.inner_radius = inner_radius;
		this.outer_radius = outer_radius;
		this.radius = (this.outer_radius+this.inner_radius)/2;
		this.color = color;
	}

}
