package dao.product;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;


public class ProductDAOMyBatis implements ProductDAO{
	@Autowired
    private SqlSession sqlSession;

	
	@Override
	public ProductDTO selectProduct(int id) 
	{
		return sqlSession.selectOne("selectProduct", id);
	}
	
	
	
	public SqlSession getSqlSession() {
		return sqlSession;
	}

	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}


}
