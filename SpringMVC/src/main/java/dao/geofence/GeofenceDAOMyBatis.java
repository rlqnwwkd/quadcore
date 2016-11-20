package dao.geofence;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import dao.userLocation.UserLocationDTO;


public class GeofenceDAOMyBatis implements GeofenceDAO{
	@Autowired
    private SqlSession sqlSession;
    
	
	@Override
	public void insertGeofence(GeofenceDTO geofenceDTO) {
		sqlSession.insert("insertGeofence", geofenceDTO);
	}
	
	@Override
	public void deleteGeofence(long id) {
		sqlSession.delete("deleteGeofence",id);
	}
	
	// getters, setters
	public SqlSession getSqlSession() {
		return sqlSession;
	}

	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	@Override
	public GeofenceDTO selectGeofence(long id) {
		return this.sqlSession.selectOne("selectGeofence", id);
	}

	
}
