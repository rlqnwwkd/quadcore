package dao.userLocation;

public class UserLocationDTO {
	private String date2;
	private double x;
	private double y;
	private long geofenceId;
	private String userId;
	
	
	public String getDate2() {
		return date2;
	}
	public void setDate2(String date2) {
		this.date2 = date2;
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
	public long getGeofenceId() {
		return geofenceId;
	}
	public void setGeofenceId(long geofenceId) {
		this.geofenceId = geofenceId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
