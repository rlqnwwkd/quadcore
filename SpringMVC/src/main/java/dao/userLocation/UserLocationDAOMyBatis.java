package dao.userLocation;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

public class UserLocationDAOMyBatis implements UserLocationDAO{
	
	@Autowired
    private SqlSession sqlSession;
	
	@Override
	public void insertUserLocation(UserLocationDTO userLocationDTO) {
		sqlSession.insert("insertUserLocation", userLocationDTO);
	}


	@Override
	public List<UserLocationDTO> getRecentUsersLocation(UserLocationDTO userLocationDTO) {
		return sqlSession.selectList("getRecentUsersLocation", userLocationDTO);
	}
	
	
	
	
	
	
	
	
	
	public SqlSession getSqlSession() {
		return sqlSession;
	}

	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}	
}
