package Trilateration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TrilaterationTest {

	@Test
	public void getUserPosition(){
		Point2D position1 = new Point2D(0,0, Math.sqrt(50));
		Point2D position2 = new Point2D(10,0, Math.sqrt(50));
		Point2D position3 = new Point2D(0,10, Math.sqrt(50));
		
		Point2D userPosition = Trilateration.getTrilateration(position1, position2, position3);
		assertThat(userPosition.getX(), is(5.0));
		assertThat(userPosition.getY(), is(5.0));
	}
}
