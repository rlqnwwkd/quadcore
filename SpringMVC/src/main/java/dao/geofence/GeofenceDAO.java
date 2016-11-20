package dao.geofence;

import dao.userLocation.UserLocationDTO;

public interface GeofenceDAO {
	public void insertGeofence(GeofenceDTO geofenceDTO);
	public void deleteGeofence(long id);
	public GeofenceDTO selectGeofence(long id);
}

