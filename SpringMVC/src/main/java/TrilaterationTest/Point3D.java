package TrilaterationTest;

public class Point3D {
	private double x;
	private double y;
	private double z;
	private double r;
	
	public Point3D(){ 	}
	public Point3D(double x, double y, double z)
	{ 	
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Point3D(double x, double y,double z, double r)
	{ 	
		this.x = x;
		this.y = y;
		this.z = z;
		this.r=r;
	}
	
	
	public double getR() {
		return r;
	}

	public void setR(double r) {
		this.r = r;
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
	public double getZ() {
		return z;
	}
	public void setZ(double z) {
		this.z = z;
	}
	
	
}
