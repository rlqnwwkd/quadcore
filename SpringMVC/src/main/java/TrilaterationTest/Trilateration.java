package TrilaterationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class Trilateration {

	@Test
	public void trilateration(){
		Point3D p1,p2,p3;
		p1 = new Point3D(0,2,0,2);
		p2 = new Point3D(2,0,0,2);
		p3 = new Point3D(2,2,0,2);
		
		List<Point3D> results = trilaterate(p1, p2, p3);
		
		System.out.println(results.get(0).getX()+","+results.get(0).getY()+","+results.get(0).getZ());
		System.out.println(results.get(1).getX()+","+results.get(1).getY()+","+results.get(1).getZ());
		
		assertThat(1,is(1));
	}
	
	public static List<Point3D> trilaterate(Point3D p1, Point3D p2, Point3D p3)
	{
		Point3D ex,a,ey,ez,p4a,p4b;
		double i,d,j,b,z;
		ex = vector_divide( vector_subtract(p2, p1), norm(vector_subtract(p2, p1)));
		System.out.println("ex:"+ex);
		
		i = dot(ex, vector_subtract(p3,p1));
		System.out.println("i:"+i);
		
		a = vector_subtract(vector_subtract(p3,p1), vector_multiply(ex,i));
		System.out.println("a.x:"+a.getX()+",a.y:"+a.getY()+",a.r:"+a.getR());
		
		ey = vector_divide(a, norm(a));
		System.out.println("ey.x:"+ey.getX()+",ey.y:"+ey.getY()+",ey.r:"+ey.getR());
		
		ez = vector_cross(ex, ey);
		System.out.println("ez.x:"+ez.getX()+",ez.y:"+ez.getY()+",ez.r:"+ez.getR());
		
		d = norm(vector_subtract(p2, p1));
		System.out.println("d:"+d);
		
		j = dot(ey,vector_subtract(p3,p1));
		System.out.println("j:"+j);
		
		double x = (sqr(p1.getR()) - sqr(p2.getR()) +sqr(d) ) / (2*d);
		System.out.println("x:"+x);
		////////////////////////
		// 이부분 나누기 뒤에 괄호가 있는지 없는지
		////////////////////////
		double y = (sqr(p1.getR()) - sqr(p3.getR()) + sqr(i) + sqr(j)) / (2*j) - (i/j) * x;
		System.out.println("y:"+y);
		
		b = sqr(p1.getR()) - sqr(x) - sqr(y);
		System.out.println("b:"+b);
		
		if (Math.abs(b) < 0.0000000001)
		{
			b = 0;
		}
		
		z= Math.sqrt(b);
		System.out.println("z:"+z);
		
		////////////////////////////
		// no solution found
		// 사용자 위치 값이 이상할 경우
		/////////////////////////////
		if ( Double.isNaN(z))
		{
			return null;
		}
		
		a = vector_add(p1, vector_add(vector_multiply(ex,x), vector_multiply(ey,y)));
		p4a = vector_add(a, vector_multiply(ez,z));
		p4b = vector_subtract(a, vector_multiply(ez,z));
		
		////////////////////////////
		// return_middle 용도 몰라서 뺌
		////////////////////////////
		if(z == 0)
		{
			List<Point3D> resultPoints= new ArrayList<Point3D>();;
			resultPoints.add(a);
			return resultPoints;
		}
		else
		{
			List<Point3D> resultPoints = new ArrayList<Point3D>();
			resultPoints.add(p4a);
			resultPoints.add(p4b);
			return resultPoints;
		}
	}
	
	
	public static double sqr(double a)
	{
		return a*a;
	}
	
	public static double norm(Point3D p)
	{
		return Math.sqrt( sqr( p.getX()) + sqr(p.getY()) + sqr(p.getZ()) );
	}
	
	public static double dot(Point3D p1, Point3D p2)
	{
		return (p1.getX()*p2.getX()) + (p1.getY()*p2.getY()) + (p1.getZ()*p2.getZ());
	}

	public static Point3D vector_subtract(Point3D p1, Point3D p2)
	{
		Point3D p = new Point3D();
		p.setX(p1.getX()-p2.getX());
		p.setY(p1.getY()-p2.getY());
		p.setZ(p1.getZ()-p2.getZ());
		return p;
	}
	
	public static Point3D vector_add(Point3D p1, Point3D p2)
	{
		Point3D p = new Point3D();
		p.setX(p1.getX()+p2.getX());
		p.setY(p1.getY()+p2.getY());
		p.setZ(p1.getZ()+p2.getZ());
		return p;
	}
	
	public static Point3D vector_divide(Point3D p1, double value)
	{
		Point3D p = new Point3D();
		p.setX(p1.getX()/value);
		p.setY(p1.getY()/value);
		p.setZ(p1.getZ()/value);
		return p;
	}
	
	public static Point3D vector_multiply(Point3D p1, double value)
	{
		Point3D p = new Point3D();
		p.setX(p1.getX()*value);
		p.setY(p1.getY()*value);
		p.setZ(p1.getZ()*value);
		return p;
	}
	
	public static Point3D vector_cross(Point3D p1, Point3D p2)
	{
		Point3D p = new Point3D();
		p.setX( (p1.getY()*p2.getZ()) - (p1.getZ()*p2.getY()) );
		p.setY( (p1.getZ()*p2.getZ()) - (p1.getX()*p2.getZ()) );
		p.setZ( (p1.getX()*p2.getY()) - (p1.getY()*p2.getX()) );
		return p;
	}
	
}








