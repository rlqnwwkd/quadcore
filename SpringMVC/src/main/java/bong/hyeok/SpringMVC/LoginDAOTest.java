package bong.hyeok.SpringMVC;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import javax.sql.DataSource;

import login.bean.LoginDAO;
import login.bean.LoginDTO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/test-root-context.xml")
public class LoginDAOTest {
	
	@Autowired
	private LoginDAO loginDAO;
	
	@Autowired 
	DataSource dataSource;
	
	@Test
	public void duplicateKey(){
		LoginDTO user1 = new LoginDTO();
		
		try{
			user1.setId("bong");
			user1.setPass("hyeok3");
			loginDAO.loginInsert(user1);
		}catch(DuplicateKeyException ex){
			System.out.println(ex.getRootCause());
			SQLException sqlEx = (SQLException)ex.getRootCause();
			SQLExceptionTranslator set = 
					new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
			System.out.println(set.translate(null, null, sqlEx));
		}
	}
	
}

