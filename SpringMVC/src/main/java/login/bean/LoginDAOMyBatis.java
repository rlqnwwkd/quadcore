package login.bean;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

public class LoginDAOMyBatis implements LoginDAO{
    @Autowired
    private SqlSession sqlSession;
    
    @Override
    public void loginInsert(LoginDTO loginDTO) {
        sqlSession.insert("loginSQL.loginInsert", loginDTO);
    }
    
    public SqlSession getSqlSession() {
        return sqlSession;
    }
    
    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }
}

