package Trilateration;

public class Trilateration {
	
	// position = 기준점, 3개의 기준점을 필요로한다
	public static Point2D getTrilateration(Point2D position1,
			Point2D position2, Point2D position3)
	{
		// 기준점 1의 x 좌표
		double x1 = position1.getX();
		double y1 = position1.getY();
		double x2 = position2.getX();
		double y2 = position2.getY();
		double x3 = position3.getX();
		double y3 = position3.getY();
		
		// 기준점 1로부터의 직선거리(반지름)
		double r1 = position1.getDistance();
		double r2 = position2.getDistance();
		double r3 = position3.getDistance();
		
		double S = (Math.pow(x3, 2.) - Math.pow(x2, 2.) + Math.pow(y3, 2.) - Math.pow(y2, 2.) + Math.pow(r2, 2.) - Math.pow(r3, 2.)) / 2.0;
		double T = (Math.pow(x1, 2.) - Math.pow(x2, 2.) + Math.pow(y1, 2.) - Math.pow(y2, 2.) + Math.pow(r2, 2.) - Math.pow(r1, 2.)) / 2.0;
		double y = ((T * (x2 - x3)) - (S * (x2 - x1))) / (((y1 - y2) * (x2 - x3)) - ((y3 - y2) * (x2 - x1)));
		double x = ((y * (y1 - y2)) - T) / (x2 - x1);

		Point2D userLocation = new Point2D(x, y,0);
		return userLocation;
	}
}
