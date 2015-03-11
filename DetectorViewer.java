import java.util.*;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DetectorViewer extends Application {

	public int width = 800;
	public int height = 800;

	public double radius = 1000;
	private double scale_factor;

	private double PI2 = Math.PI/2;

	static public ArrayList<Layer> layers = new ArrayList<Layer>();
	static public ArrayList<PTrack> particle_tracks = new ArrayList<PTrack>();

	static public double[][] event_angles;

	public DetectorViewer() {
		scale_factor = 0.5*width/radius;
	}

	public static void main(String[] args) {
		double[] beryllium = {35};
		double[] silicon = {45, 80, 120, 180, 300, 400, 500, 700};
		double[] coincidence = {900, 910};

		for (double r : beryllium)
			layers.add(new Layer(r, r+6, Color.RED));

		for (double r : silicon)
			layers.add(new Layer(r, r+2, Color.GREEN));

		for (double r : coincidence)
			layers.add(new Layer(r, r+2, Color.BLUE));

		double[] points = {0, 0, 0.05, 0.1, 0.2, 0.3, 0.38, 0.44, 0.55, 0.68, 0.685};

		PTrack track = new PTrack();
		for (int i = 0; i < layers.size(); i++)
			track.addPoint(new PPoint(layers.get(i).radius, points[i]));

		particle_tracks.add(track);

		double[][] event_angles = {
			{},{0},{0.05},{0.1},{0.2},{0.3},{0.38},{0.44},{0.55},{0.68},{0.685}
		};

		for (int i = 0; i < layers.size(); i++)
			for (int j = 0; j < event_angles[i].length; j++)
				layers.get(i).c_points.add(new CollisionPoint(event_angles[i][j]));

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

		gc.setStroke(Color.PURPLE);
		gc.setLineWidth(1);

		for (PTrack track : particle_tracks) {
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
	public Color color = Color.GREY;

	public void addPoint(PPoint p) {
		points.add(p);
	}
}


class CollisionPoint {

	public double angle;

	public CollisionPoint (double a) {
		this.angle = a;
	}
}


class Layer {

	public double inner_radius, outer_radius, radius;
	public Color color = Color.GREY;
	public ArrayList<CollisionPoint> c_points = new ArrayList<CollisionPoint>();

	public Layer(double inner_radius, double outer_radius) {
		this.inner_radius = inner_radius;
		this.outer_radius = outer_radius;
		this.radius = (this.outer_radius+this.inner_radius)/2;
	}

	public Layer(double inner_radius, double outer_radius, Color color) {
		this.inner_radius = inner_radius;
		this.outer_radius = outer_radius;
		this.radius = (this.outer_radius+this.inner_radius)/2;
		this.color = color;
	}

}
