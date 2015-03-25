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

	static public ArrayList<DetectorLayer> layers = new ArrayList<DetectorLayer>();
	static public ArrayList<PTrack> particle_tracks = new ArrayList<PTrack>();

	static public double[][] event_angles;

	public DetectorViewer() {
		scale_factor = 0.5*width/radius;
	}

	private static double[][] importCSVData(String file_path, int lines) throws IOException {
		double[][] particles = new double[lines][23];
		double[] d_values = new double[23];
		String[] s_values;
		BufferedReader br = null;
		String line = "";

		try {
			br = new BufferedReader(new FileReader(file_path));

			for (int i = 0; i < lines; i++) {
				if ((line = br.readLine()) != null) {
					s_values = line.split(",");
					d_values = new double[23];

					for (int j = 0; j < s_values.length; j++)
						d_values[j] = Double.parseDouble(s_values[j]);

					particles[i] = d_values;
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

		return particles;
	}


	public static void main(String[] args) throws IOException {
		double[] beryllium = {35};
		double[] silicon = {45, 80, 120, 180, 300, 400, 500, 700};
		double[] coincidence = {900, 910};

		for (double r : beryllium)
			layers.add(new DetectorLayer(r, r+6, Color.GREY));

		for (double r : silicon)
			layers.add(new DetectorLayer(r, r+2, Color.GREY));

		for (double r : coincidence)
			layers.add(new DetectorLayer(r, r+2, Color.GREY));

		double[][] points = importCSVData("data.csv", 10);

		for (int i = 0; i < points.length; i++) {
			PTrack track = new PTrack();
			for (int j = 0; j < layers.size(); j++)
				track.addPoint(new PPoint(layers.get(j).radius, points[i][j*2+1]));

			particle_tracks.add(track);
		}

		launch(args);
	}

	@Override
	public void start(Stage stage) {
		Group root = new Group();
		Canvas canvas = new Canvas(width, height);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		drawDetector(gc);
		drawTracks(gc);
		drawCollisionPoints(gc);

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

	private void drawCollisionPoints(GraphicsContext gc) {
		double r; double r2 = 4;

		for (int i = 0; i < layers.size(); i++) {
			r = layers.get(i).radius * scale_factor;

			for (CollisionPoint cp : layers.get(i).c_points) {
				gc.setFill(Color.PURPLE);
				gc.fillOval(
					width/2 + r*Math.cos(cp.angle - PI2) - r2/2,
					height/2 + r*Math.sin(cp.angle - PI2) - r2/2,
					r2, r2
				);
			}
		}
	}

	private void drawTracks(GraphicsContext gc){
		double r, a, x1, x2, y1, y2;

		gc.setLineWidth(1);

		for (PTrack track : particle_tracks) {
			gc.setStroke(track.color);

			x2 = width/2;
			y2 = height/2;

			for(int i = 0; i < track.points.size(); i++) {
				r = track.points.get(i).radius * scale_factor;
				a = track.points.get(i).angle - PI2;

				x1 = x2;
				y1 = y2;

				x2 = width/2 + r*Math.cos(a);
				y2 = height/2 + r*Math.sin(a);

				gc.strokeLine(x1, y1, x2, y2);
			}
		}
	}

	private Color randomColor() {
		return new Color(Math.random(), Math.random(), Math.random(), 1.0);
	}
}


class PTrack {

	public ArrayList<PPoint> points = new ArrayList<PPoint>();
	public Color color = new Color(Math.random(), Math.random(), Math.random(), 1.0);;

	public void addPoint(PPoint p) {
		points.add(p);
	}
}

class PPoint {
	double radius = 0;
	double angle = 0;

	public PPoint(double r, double a) {
		this.radius = r;
		this.angle = Math.PI/2 - a;
	}

}


class CollisionPoint {

	public double angle;

	public CollisionPoint (double a) {
		this.angle = Math.PI/2 - a;
	}
}


class DetectorLayer {

	public double inner_radius, outer_radius, radius;
	public Color color = Color.GREY;
	public ArrayList<CollisionPoint> c_points = new ArrayList<CollisionPoint>();

	public DetectorLayer(double inner_radius, double outer_radius) {
		this.inner_radius = inner_radius;
		this.outer_radius = outer_radius;
		this.radius = (this.outer_radius+this.inner_radius)/2;
	}

	public DetectorLayer(double inner_radius, double outer_radius, Color color) {
		this.inner_radius = inner_radius;
		this.outer_radius = outer_radius;
		this.radius = (this.outer_radius+this.inner_radius)/2;
		this.color = color;
	}

}
