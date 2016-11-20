package dao.userLocation;

import java.util.List;

public interface UserLocationDAO {

	public void insertUserLocation(UserLocationDTO userLocationDTO);

	public List<UserLocationDTO> getRecentUsersLocation(long geofenceId);

}
