package eionet.rod.dao;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eionet.rod.model.Help;
import eionet.rod.util.exception.ResourceNotFoundException;


@Repository
@Transactional
public class HelpDaoImpl implements HelpDao{
	
private JdbcTemplate jdbcTemplate;
	
	@Resource
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public Help findId(String helpId) throws ResourceNotFoundException {
		String query = "SELECT PK_HELP_ID as helpId ,HELP_TITLE as title, HELP_TEXT as text "
				+ "FROM T_HELP "
				+ "WHERE PK_HELP_ID = ?";
		
		String queryCount = "SELECT count(*) as helpId "
				+ "FROM T_HELP "
				+ "WHERE PK_HELP_ID = ?";
		
		
		try {
			Integer countHelp = jdbcTemplate.queryForObject(queryCount, Integer.class, helpId);
		
			if (countHelp == 0) {
				Help help = new Help();
				help.setTitle("ERROR");
				help.setText("The heldp ID you requested: " + helpId + " was not found in the database");
				
				return help;
				//throw new ResourceNotFoundException("The help ID you requested: " + helpId + " was not found in the database");
			
			}else {
		
				Help help =  jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<Help>(Help.class), helpId);
		        return help;

			}
		
		} catch (DataAccessException e) {
			throw new ResourceNotFoundException("The heldp ID you requested: " + helpId + " was not found in the database or is empty");
		}
	        
	}
}