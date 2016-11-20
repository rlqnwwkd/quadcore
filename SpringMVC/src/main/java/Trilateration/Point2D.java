package Trilateration;

public class Point2D {
	private double x;
	private double y;
	private double distance;
	
	public Point2D(){ 	}
	public Point2D(double x, double y)
	{ 	
		this.x = x;
		this.y = y;
	}
	public Point2D(double x, double y, double distance)
	{ 	
		this.x = x;
		this.y = y;
		this.distance=distance;
	}
	
	
	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
}
