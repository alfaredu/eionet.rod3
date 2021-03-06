package eionet.rod.dao;


import eionet.rod.model.Documentation;
import eionet.rod.model.Help;
import eionet.rod.util.exception.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;


@Repository
@Transactional
public class HelpDaoImpl implements HelpDao {

    private static final Log logger = LogFactory.getLog(HelpDaoImpl.class);

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

        try {
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Help.class), helpId);
        } catch (EmptyResultDataAccessException e) {
            Help help = new Help();
            help.setTitle("ERROR");
            help.setText("The help ID you requested: " + helpId + " was not found in the database");
            return help;
        } catch (DataAccessException e) {
            logger.debug(e, e);
            throw new ResourceNotFoundException("The heldp ID you requested: " + helpId + " was not found in the database or is empty", e);
        }

    }

    private static final String q_get_doc =
            "SELECT AREA_ID, SCREEN_ID, DESCRIPTION, HTML " +
                    "FROM HLP_AREA " +
                    "WHERE AREA_ID=?";

    /* (non-Javadoc)
     * @see eionet.rod.services.modules.db.dao.IGenericDao#getDoc(String area_id)
     */
    public Documentation getDoc(String areaId) throws ResourceNotFoundException {

        try {
            return jdbcTemplate.queryForObject(q_get_doc, new BeanPropertyRowMapper<>(Documentation.class), areaId);
        } catch (DataAccessException sqle) {
            logger.debug(sqle, sqle);
            return null;
        }

    }

}
