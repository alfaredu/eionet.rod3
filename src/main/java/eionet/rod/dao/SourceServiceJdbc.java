package eionet.rod.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eionet.rod.model.ClientDTO;
import eionet.rod.model.HierarchyInstrumentDTO;
import eionet.rod.model.InstrumentClassificationDTO;
import eionet.rod.model.InstrumentDTO;
import eionet.rod.model.InstrumentFactsheetDTO;
import eionet.rod.model.InstrumentObligationDTO;
import eionet.rod.model.InstrumentsListDTO;

import eionet.rod.util.exception.ResourceNotFoundException;

/**
 * Service to store metadata for T_SPURCE using JDBC.
 */
@Service(value = "SourceService")
@Transactional
public class SourceServiceJdbc implements SourceService {
	
	private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

	@Override
	public InstrumentFactsheetDTO getById(Integer sourceId)  throws ResourceNotFoundException {
		String query = "SELECT T_SOURCE.PK_SOURCE_ID AS sourceId,"
                + "TITLE AS sourceTitle, URL AS sourceUrl,"
                + "ALIAS AS sourceAlias, CELEX_REF AS sourceCelexRef, SOURCE_CODE AS sourceCode,"
                + "VALID_FROM AS sourceValidFrom, ABSTRACT AS sourceAbstract, COMMENT AS sourceComment, ISSUED_BY_URL AS sourceIssuedByUrl,"
                + "EC_ENTRY_INTO_FORCE AS sourceEcEntryIntoForce, EC_ACCESSION AS sourceEcAccession, SECRETARIAT AS sourceSecretariat,"
                + "SECRETARIAT_URL AS sourceSecretariatUrl, TERMINATE AS sourceTerminate "
                + "FROM T_SOURCE "
                + "WHERE T_SOURCE.PK_SOURCE_ID = ?";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		InstrumentFactsheetDTO instrumentFactsheetRec = null;
		
		try {
		
			instrumentFactsheetRec = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<InstrumentFactsheetDTO>(InstrumentFactsheetDTO.class), sourceId);
			
			List<InstrumentObligationDTO> obligations = getObligationsById(sourceId);
	
			instrumentFactsheetRec.setObligations(obligations);
			
			ClientDTO clientRec = getClient(sourceId);
			if (clientRec != null) {
				instrumentFactsheetRec.setClientId(clientRec.getClientId());
				instrumentFactsheetRec.setClientName(clientRec.getName());
				instrumentFactsheetRec.setClientUrl(clientRec.getUrl());
			}
			instrumentFactsheetRec.setParent(getParent(sourceId));
			
			instrumentFactsheetRec.setRelatedInstruments(getRelatedInstruments(sourceId));
			
			instrumentFactsheetRec.setClassifications(getInstrumentClassifications(sourceId));
					
			/*try {
				instrumentFactsheetRec.setSourceValidFrom(dateParser(instrumentFactsheetRec.getSourceValidFrom()));
				instrumentFactsheetRec.setSourceEcEntryIntoForce(dateParser(instrumentFactsheetRec.getSourceEcEntryIntoForce()));
				instrumentFactsheetRec.setSourceEcAccession(dateParser(instrumentFactsheetRec.getSourceEcAccession()));
				System.out.println(instrumentFactsheetRec.getSourceValidFrom());
			} catch (ParseException e) {
				e.printStackTrace();
			}*/
		
		} catch (DataAccessException e) {
			throw new ResourceNotFoundException("DataAccessException error: " + e);
		}
		
		return instrumentFactsheetRec;
	}

	@Override
	public List<InstrumentObligationDTO> getObligationsById(Integer sourceId) {
		String query = "SELECT PK_RA_ID AS obligationId, O.TITLE AS title, AUTHORITY AS authority, O.TERMINATE AS terminate "
				+ "FROM T_OBLIGATION AS O "
				+ "LEFT JOIN T_SOURCE AS S ON S.PK_SOURCE_ID = O.FK_SOURCE_ID "
				+ "WHERE S.PK_SOURCE_ID = ?";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		return jdbcTemplate.query(query, new BeanPropertyRowMapper<InstrumentObligationDTO>(InstrumentObligationDTO.class), sourceId);
		
		
	}

	@Override
	public void update(InstrumentFactsheetDTO instrumentFactsheetRec)  throws ResourceNotFoundException {
		instrumentFactsheetRec = validateDates(instrumentFactsheetRec);
		
		String update = "UPDATE T_SOURCE SET TITLE=?, ALIAS=?, "
                + "SOURCE_CODE=?, URL=?, CELEX_REF=?, ISSUED_BY_URL=?, "
                + "VALID_FROM=?, ABSTRACT=?, COMMENT=?, EC_ENTRY_INTO_FORCE=?, EC_ACCESSION=?, "
                + "SECRETARIAT=?, SECRETARIAT_URL=?, TERMINATE=?, FK_CLIENT_ID=? "
                + "WHERE PK_SOURCE_ID=?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
               
        
		jdbcTemplate.update(update,
				instrumentFactsheetRec.getSourceTitle(),
				instrumentFactsheetRec.getSourceAlias(),
				instrumentFactsheetRec.getSourceCode(),
				instrumentFactsheetRec.getSourceUrl(),
				instrumentFactsheetRec.getSourceCelexRef(),
				instrumentFactsheetRec.getSourceIssuedByUrl(),
				instrumentFactsheetRec.getSourceValidFrom(),
				instrumentFactsheetRec.getSourceAbstract(),
				instrumentFactsheetRec.getSourceComment(),
				instrumentFactsheetRec.getSourceEcEntryIntoForce(),
				instrumentFactsheetRec.getSourceEcAccession(),
				instrumentFactsheetRec.getSourceSecretariat(),
				instrumentFactsheetRec.getSourceSecretariatUrl(),
				instrumentFactsheetRec.getSourceTerminate(),
				instrumentFactsheetRec.getClientId(),
				instrumentFactsheetRec.getSourceId());	
		
		update = "UPDATE T_CLIENT_SOURCE_LNK "
				+ "SET FK_CLIENT_ID=? "
				+ "WHERE FK_SOURCE_ID=? AND STATUS = 'M'";
		
		jdbcTemplate.update(update,
				instrumentFactsheetRec.getClientId(),
				instrumentFactsheetRec.getSourceId());
		
		String delete = "DELETE FROM T_SOURCE_LNK WHERE FK_SOURCE_CHILD_ID = ? "
				+ "AND CHILD_TYPE='S' AND PARENT_TYPE='S'";
		jdbcTemplate.update(delete, instrumentFactsheetRec.getSourceId());;
		
		if (instrumentFactsheetRec.getSourceLnkFKSourceParentId() != -1) {
			
			String insert = "INSERT INTO T_SOURCE_LNK (FK_SOURCE_CHILD_ID, CHILD_TYPE, "
					+ "FK_SOURCE_PARENT_ID, PARENT_TYPE) "
					+ "VALUES (?,?,?,?)";
			
			jdbcTemplate.update(insert,
					instrumentFactsheetRec.getSourceId(),
					"S",
					instrumentFactsheetRec.getSourceLnkFKSourceParentId(),
					"S");
		}
		
		deleteClassifications(instrumentFactsheetRec.getSourceId());
		insertClassifications(instrumentFactsheetRec);
		
	}

	private ClientDTO getClient(Integer sourceId) {
		String query = "SELECT C.PK_CLIENT_ID AS clientId, C.CLIENT_NAME AS name, C.CLIENT_URL AS url "
				+ "FROM T_CLIENT AS C "
				+ "INNER JOIN T_CLIENT_SOURCE_LNK AS CSL "
				+ "ON C.PK_CLIENT_ID=CSL.FK_CLIENT_ID "
				+ "INNER JOIN T_SOURCE AS S "
				+ "ON S.PK_SOURCE_ID=CSL.FK_SOURCE_ID "
				+ "WHERE S.PK_SOURCE_ID=? AND CSL.STATUS='M'";
		
		String queryCount = "SELECT count(*) AS clientId "
				+ "FROM T_CLIENT AS C "
				+ "INNER JOIN T_CLIENT_SOURCE_LNK AS CSL "
				+ "ON C.PK_CLIENT_ID=CSL.FK_CLIENT_ID "
				+ "INNER JOIN T_SOURCE AS S "
				+ "ON S.PK_SOURCE_ID=CSL.FK_SOURCE_ID "
				+ "WHERE S.PK_SOURCE_ID=? AND CSL.STATUS='M'";
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		Integer countClients= jdbcTemplate.queryForObject(queryCount, Integer.class, sourceId);
		if (countClients  != 0 ) {
			ClientDTO clientRec = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<ClientDTO>(ClientDTO.class), sourceId);
			return clientRec;
		}else {
			return null;
		}
		
	}

	@Override
	public Integer insert(InstrumentFactsheetDTO instrumentFactsheetRec) {
		instrumentFactsheetRec = validateDates(instrumentFactsheetRec);
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
		jdbcInsert.withTableName("T_SOURCE").usingGeneratedKeyColumns(
                "PK_SOURCE_ID");
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("TITLE", instrumentFactsheetRec.getSourceTitle());
		parameters.put("ALIAS", instrumentFactsheetRec.getSourceAlias());
		parameters.put("SOURCE_CODE", instrumentFactsheetRec.getSourceCode());
		parameters.put("TERMINATE", instrumentFactsheetRec.getSourceTerminate());
		parameters.put("URL", instrumentFactsheetRec.getSourceUrl());
		parameters.put("CELEX_REF", instrumentFactsheetRec.getSourceCelexRef());
		parameters.put("ISSUED_BY_URL", instrumentFactsheetRec.getSourceIssuedByUrl());
		parameters.put("VALID_FROM", instrumentFactsheetRec.getSourceValidFrom());
		parameters.put("ABSTRACT", instrumentFactsheetRec.getSourceAbstract());
		parameters.put("COMMENT", instrumentFactsheetRec.getSourceComment());
		parameters.put("EC_ENTRY_INTO_FORCE", instrumentFactsheetRec.getSourceEcEntryIntoForce());
		parameters.put("EC_ACCESSION", instrumentFactsheetRec.getSourceEcAccession());
		parameters.put("SECRETARIAT", instrumentFactsheetRec.getSourceSecretariat());
		parameters.put("SECRETARIAT_URL", instrumentFactsheetRec.getSourceSecretariatUrl());
		parameters.put("FK_CLIENT_ID", instrumentFactsheetRec.getClientId());
		parameters.put("FK_TYPE_ID", 0);
		parameters.put("LEGAL_NAME", "");
		// java.sql.Date
        Calendar calendar = Calendar.getInstance();
        java.sql.Date ourJavaDateObject = new java.sql.Date(calendar.getTime().getTime());
        
        parameters.put("LAST_UPDATE",ourJavaDateObject);
        System.out.println("new sourceID: ANTES");
		Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(
                parameters));
		Integer sourceId = ((Number) key).intValue();
		System.out.println("new sourceID:" + sourceId);
		/*String query = "INSERT INTO T_SOURCE (TITLE, ALIAS, SOURCE_CODE, TERMINATE, URL, "
				+ "CELEX_REF, ISSUED_BY_URL, VALID_FROM, ABSTRACT, COMMENT, "
				+ "EC_ENTRY_INTO_FORCE, EC_ACCESSION, SECRETARIAT, SECRETARIAT_URL, "
				+ "FK_CLIENT_ID) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";		
		
		jdbcTemplate.update(query,
				instrumentFactsheetRec.getSourceTitle(),
				instrumentFactsheetRec.getSourceAlias(),
				instrumentFactsheetRec.getSourceCode(),
				instrumentFactsheetRec.getSourceTerminate(),
				instrumentFactsheetRec.getSourceUrl(),
				instrumentFactsheetRec.getSourceCelexRef(),
				instrumentFactsheetRec.getSourceIssuedByUrl(),
				instrumentFactsheetRec.getSourceValidFrom(),
				instrumentFactsheetRec.getSourceAbstract(),
				instrumentFactsheetRec.getSourceComment(),
				instrumentFactsheetRec.getSourceEcEntryIntoForce(),
				instrumentFactsheetRec.getSourceEcAccession(),
				instrumentFactsheetRec.getSourceSecretariat(),
				instrumentFactsheetRec.getSourceSecretariatUrl(),
				instrumentFactsheetRec.getClientId()
				);	*/
		
		insertClient(sourceId, instrumentFactsheetRec.getClientId());
		System.out.println("new sourceID: 1");
		insertParent(sourceId, instrumentFactsheetRec.getSourceLnkFKSourceParentId());
		System.out.println("new sourceID: 2");
		instrumentFactsheetRec.setSourceId(sourceId);
		System.out.println("new sourceID: 3");
		insertClassifications(instrumentFactsheetRec);
		System.out.println("new sourceID: 4");
		return sourceId;
	}
	
	
	private void insertClient(Integer sourceId, Integer clientId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "INSERT INTO T_CLIENT_SOURCE_LNK (FK_CLIENT_ID, FK_SOURCE_ID, STATUS) "
				+ "VALUES (?,?,?)";
		
		jdbcTemplate.update(query,
				clientId,
				sourceId,
				"M"
				);
	}	
	

	private void insertParent(Integer sourceId, Integer sourceLnkFKSourceParentId) {
		if (sourceLnkFKSourceParentId != -1) {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
						
			String query = "INSERT INTO T_SOURCE_LNK (FK_SOURCE_CHILD_ID, CHILD_TYPE, FK_SOURCE_PARENT_ID, PARENT_TYPE) "
					+ "VALUES (?,?,?,?)";
			
			jdbcTemplate.update(query,
					sourceId,
					"S",
					sourceLnkFKSourceParentId,
					"S"
					);
		}
		
	}

	@Override
	public List<InstrumentFactsheetDTO> getAllInstruments() {
		String query = "SELECT T_SOURCE.PK_SOURCE_ID AS sourceId, ALIAS AS sourceAlias "                
                + "FROM T_SOURCE ORDER BY sourceAlias";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate.query(query, new BeanPropertyRowMapper<InstrumentFactsheetDTO>(InstrumentFactsheetDTO.class));
	}
	

	private InstrumentDTO getParent(Integer sourceId) {
		InstrumentDTO instrumentDTORec = null;
		List<InstrumentDTO> instruments = null;
		String query = "SELECT PK_SOURCE_ID AS sourceId, ALIAS AS sourceAlias "
				+ "FROM T_SOURCE WHERE PK_SOURCE_ID=("
				+ "SELECT FK_SOURCE_PARENT_ID FROM T_SOURCE_LNK WHERE FK_SOURCE_CHILD_ID=? "
				+ "AND CHILD_TYPE='S' AND PARENT_TYPE='S')";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		instruments = jdbcTemplate.query(query, new BeanPropertyRowMapper<InstrumentDTO>(InstrumentDTO.class), sourceId);
		if (instruments != null && instruments.size() > 0) {
			instrumentDTORec = instruments.get(0);
		}
		//instrumentDTORec = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<InstrumentDTO>(InstrumentDTO.class), sourceId);
		
		return instrumentDTORec;
	}
	
	
	private List<InstrumentDTO> getRelatedInstruments(Integer sourceId) {
		List<InstrumentDTO> relatedInstruments = new ArrayList<InstrumentDTO>();
		InstrumentDTO relatedInstrument = null;
		List<InstrumentFactsheetDTO> instruments = null;
		
		String query = "SELECT FK_SOURCE_CHILD_ID AS sourceLnkFKSourceChildId "
				+ "FROM T_SOURCE_LNK WHERE FK_SOURCE_PARENT_ID=? "
				+ "AND CHILD_TYPE='S' AND PARENT_TYPE='S'";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		instruments = jdbcTemplate.query(query, new BeanPropertyRowMapper<InstrumentFactsheetDTO>(InstrumentFactsheetDTO.class), sourceId);
		if (instruments != null && instruments.size() > 0) {
			
			for (InstrumentFactsheetDTO instrument : instruments) {
				query = "SELECT PK_SOURCE_ID AS sourceId, ALIAS AS sourceAlias "
						+ "FROM T_SOURCE WHERE PK_SOURCE_ID=?";
				relatedInstrument = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<InstrumentDTO>(InstrumentDTO.class), instrument.getSourceLnkFKSourceChildId());
				relatedInstruments.add(relatedInstrument);
			}
			
		}
		return relatedInstruments;
		
	}
	
	private List<InstrumentClassificationDTO> getInstrumentClassifications(Integer sourceId) {
		List<InstrumentClassificationDTO>  classifications = null;
		String query = "SELECT SC.PK_CLASS_ID AS classId, SC.CLASSIFICATOR AS classificator, SC.CLASS_NAME AS className "
				+ "FROM T_SOURCE_CLASS SC INNER JOIN T_SOURCE_LNK AS SL "
				+ "ON SC.PK_CLASS_ID = SL.FK_SOURCE_PARENT_ID "
				+ "INNER JOIN T_SOURCE AS S "
				+ "ON SL.FK_SOURCE_CHILD_ID = S.PK_SOURCE_ID "
				+ "WHERE S.PK_SOURCE_ID=? AND SL.CHILD_TYPE='S' AND SL.PARENT_TYPE='C'";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		classifications = jdbcTemplate.query(query, new BeanPropertyRowMapper<InstrumentClassificationDTO>(InstrumentClassificationDTO.class), sourceId);
		return classifications;		
	}
	
	@Override
	public List<InstrumentClassificationDTO> getAllClassifications() {
		List<InstrumentClassificationDTO>  classifications = null;
		String query = "SELECT PK_CLASS_ID AS classId, CLASSIFICATOR AS classificator, CLASS_NAME AS className "
				+ "FROM T_SOURCE_CLASS WHERE CLASS_NAME != '' ORDER BY CLASSIFICATOR";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		classifications = jdbcTemplate.query(query, new BeanPropertyRowMapper<InstrumentClassificationDTO>(InstrumentClassificationDTO.class));
		return classifications;
	}
	
	@Override
	public void insertClassifications(InstrumentFactsheetDTO instrumentFactsheetRec) {
		String query = "INSERT INTO T_SOURCE_LNK (FK_SOURCE_CHILD_ID, FK_SOURCE_PARENT_ID, CHILD_TYPE, PARENT_TYPE) "
				+ "VALUES (?,?,?,?)";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		System.out.print("Size clasifications" + instrumentFactsheetRec.getSelectedClassifications().size());
		
		System.out.print("Query" + query);
		
		for (String classificationId : instrumentFactsheetRec.getSelectedClassifications()) {
			jdbcTemplate.update(query,
					instrumentFactsheetRec.getSourceId(),
					Integer.parseInt(classificationId),
					"S",
					"C"
					);
		}
	}
	

	public void deleteClassifications(Integer sourceId) {
		String delete = "DELETE FROM T_SOURCE_LNK WHERE FK_SOURCE_CHILD_ID = ? " 
				+ "AND CHILD_TYPE='S' AND PARENT_TYPE='C'";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(delete,
				sourceId
				);
	}
		
	@Override
	public String getHierarchy(Integer id, boolean hasParent, String mode) {
		String newLine = "\n";
		StringBuilder ret = new StringBuilder();
		String query = "SELECT SC.PK_CLASS_ID AS classId, SC.CLASSIFICATOR AS classificator, SC.CLASS_NAME AS className, SL.FK_SOURCE_PARENT_ID AS parentId "
				+ "FROM T_SOURCE_CLASS SC_PARENT, T_SOURCE_CLASS SC, T_SOURCE_LNK SL "
				+ "WHERE SC_PARENT.PK_CLASS_ID=? AND SC_PARENT.PK_CLASS_ID=SL.FK_SOURCE_PARENT_ID "
				+ "AND SL.FK_SOURCE_CHILD_ID=SC.PK_CLASS_ID AND SL.CHILD_TYPE='C' AND SL.PARENT_TYPE='C' "
				+ "ORDER BY SC.CLASSIFICATOR";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<InstrumentsListDTO> intrumentsListDTOs = jdbcTemplate.query(query, new BeanPropertyRowMapper<InstrumentsListDTO>(InstrumentsListDTO.class), id);
		String style = "category";
        if (!hasParent) {
            style = "topcategory";
        }
        
        if (intrumentsListDTOs != null && intrumentsListDTOs.size() > 0) {
        	 ret.append("<ul class='").append(style).append("'>").append(newLine);
        	 for (InstrumentsListDTO intrumentsListDTO : intrumentsListDTOs) {
        		 ret.append("<li>").append(newLine);
        		 if (hasParent) {
        			 if (intrumentsListDTO.getClassificator() != null && !intrumentsListDTO.getClassificator().isEmpty()) {
        				 ret.append(intrumentsListDTO.getClassificator()).append("&#160;").append(newLine);
        			 }
        		 }
    			 ret.append("<a href='instruments?id=").append(intrumentsListDTO.getClassId());
    			 if (mode != null && mode.equals("X")) {
    				 ret.append("&amp;mode=X");
    			 }
    			 ret.append("'>").append(intrumentsListDTO.getClassName()).append("</a>").append(newLine);
    			 ret.append(getHierarchy(intrumentsListDTO.getClassId(), true, mode));
    			 ret.append("</li>").append(newLine);
        	 }
        	 ret.append("</ul>").append(newLine);
        }
		
        return ret.toString();		
	}

	@Override
	public InstrumentsListDTO getHierarchyInstrument(Integer id) {
		String query = "SELECT SC.PK_CLASS_ID AS classId, SC.CLASSIFICATOR AS classificator, SC.CLASS_NAME AS className, "
				+ "SL.FK_SOURCE_PARENT_ID AS parentId FROM T_SOURCE_CLASS SC, T_SOURCE_LNK SL "
				+ "WHERE SC.PK_CLASS_ID=? AND SC.PK_CLASS_ID=SL.FK_SOURCE_CHILD_ID AND SL.CHILD_TYPE='C' AND SL.PARENT_TYPE='C' "
				+ "ORDER BY SC.CLASSIFICATOR";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<InstrumentsListDTO> hierarchyInstrument = jdbcTemplate.query(query, new BeanPropertyRowMapper<InstrumentsListDTO>(InstrumentsListDTO.class), id);
		if (hierarchyInstrument != null && hierarchyInstrument.size() > 0) {
			return hierarchyInstrument.get(0);
		} else {
			return null;
		}
	}
	
	@Override
	public List<HierarchyInstrumentDTO> getHierarchyInstruments(Integer id) {
		String query = "SELECT S1.PK_SOURCE_ID AS sourceId, S1.ALIAS AS sourceAlias, S1.url AS sourceUrl, "
				+ "S2.PK_SOURCE_ID AS sourceParentId, S2.ALIAS AS sourceParentAlias "
				+ "FROM T_SOURCE_LNK AS SL1 "
				+ "INNER JOIN T_SOURCE as S1 ON SL1.FK_SOURCE_CHILD_ID = S1.PK_SOURCE_ID "
				+ "LEFT JOIN T_SOURCE_LNK AS SL2 ON S1.PK_SOURCE_ID = SL2.FK_SOURCE_CHILD_ID AND SL2.PARENT_TYPE = 'S' "
				+ "LEFT JOIN T_SOURCE AS S2 ON SL2.FK_SOURCE_PARENT_ID = S2.PK_SOURCE_ID "
				+ "WHERE SL1.PARENT_TYPE = 'C' AND SL1.FK_SOURCE_PARENT_ID=? "
				+ "ORDER BY S1.ALIAS";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<HierarchyInstrumentDTO> hierarchyInstruments = jdbcTemplate.query(query, new BeanPropertyRowMapper<HierarchyInstrumentDTO>(HierarchyInstrumentDTO.class), id);
		return hierarchyInstruments;		
	}
	
	public InstrumentFactsheetDTO validateDates(InstrumentFactsheetDTO instrumentFactsheetRec) {
		if (instrumentFactsheetRec.getSourceValidFrom().equals("")) {
			instrumentFactsheetRec.setSourceValidFrom(null);
		} 
		
		if (instrumentFactsheetRec.getSourceEcEntryIntoForce().equals("")) {
			instrumentFactsheetRec.setSourceEcEntryIntoForce(null);
		} 
		
		if (instrumentFactsheetRec.getSourceEcAccession().equals("")) {
			instrumentFactsheetRec.setSourceEcAccession(null);
		} 
		return instrumentFactsheetRec;
		
	}
	
	public String dateParser(String dateString) throws ParseException {
		if (dateString != null && !dateString.equals("")) {
			SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date date = parser.parse(dateString);
			return formatter.format(date);
		} else {
			return "";
		}
		
	}

	@Override
	public void delete(Integer sourceId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		String delete = "DELETE FROM T_SOURCE WHERE PK_SOURCE_ID=?";
		jdbcTemplate.update(delete, sourceId);
		
		delete = "DELETE FROM T_CLIENT_SOURCE_LNK WHERE FK_SOURCE_ID=?";
		jdbcTemplate.update(delete, sourceId);
		
		delete = "DELETE FROM T_SOURCE_LNK WHERE FK_SOURCE_CHILD_ID=?";
		jdbcTemplate.update(delete, sourceId);
	}

}