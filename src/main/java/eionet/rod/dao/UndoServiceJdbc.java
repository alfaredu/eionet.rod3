package eionet.rod.dao;


import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.metadata.TableMetaDataContext;
import org.springframework.stereotype.Service;

import eionet.rod.model.InstrumentFactsheetDTO;
import eionet.rod.model.Obligations;
import eionet.rod.model.UndoDTO;

@Service
public class UndoServiceJdbc implements UndoService {
	
	private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

	@Override
	public void insertIntoUndo(long ts, String table, String column, String state, String quotes, String isPrimary, String value, int rowCnt , String show) {
		String query = "INSERT INTO T_UNDO (UNDO_TIME, TAB, "
                + "COL, OPERATION, QUOTES, P_KEY, VALUE, "
                + "SUB_TRANS_NR, SHOW_OBJECT) VALUES (?,?,?,?,?,?,?,?,?)";
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(query,
				ts,
				table,
				column,
				state,
				quotes,
				isPrimary,
				value,
				rowCnt,
				show
				);
	}

	@Override
	public void deleteByPK(Integer undoTime, String table, String column, String operation, Integer subTransNr) {
		String query = "DELETE FROM T_UNDO WHERE UNDO_TIME = ? AND TAB = ? AND "
				+ "COL = ? AND OPERATION = ? AND SUB_TRANS_NR = ?";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(query, undoTime, table, column, operation, subTransNr);
	}

	@Override
	public void update(UndoDTO undoRec) {
		String update = "UPDATE T_UNDO SET QUOTES = ?, P_KEY = ?, "
                + "VALUE = ?, SHOW_OBJECT = ? "
                + "WHERE UNDO_TIME = ? AND TAB = ? AND "
                + "COL = ? AND OPERATION = ? AND SUB_TRANS_NR = ?";
		 JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	        jdbcTemplate.update(update,
	        		undoRec.getQuotes(),
	        		undoRec.getPrimaryKey(),
	        		undoRec.getValue(),
	        		undoRec.getShow(),
	        		undoRec.getUndoTime(),
	        		undoRec.getTable(),
	        		undoRec.getColumn(),
	        		undoRec.getOperation(),
	        		undoRec.getSubTransNr()
	        		);
	}

	@Override
	public void insertIntoUndo(Integer id, String state, String table, String id_field, long ts, String extraSQL, String show) {
		
		String sql_stmt = null;
		String value = null;
		String quotes = null;
		String isPrimary = null;
		int rowCnt = 0;
		boolean aux;
		//String queryColumns = "SELECT COLUMN_NAME FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_NAME`=? AND TABLE_SCHEMA='rod3testdb'";
		String insert ="INSERT INTO T_UNDO (UNDO_TIME, TAB, COL, OPERATION, QUOTES, P_KEY, VALUE, SUB_TRANS_NR, SHOW_OBJECT) VALUES (?,?,?,?,?,?,?,?,?)";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		TableMetaDataContext tableMetadataContext = new TableMetaDataContext();
		tableMetadataContext.setTableName(table);
		tableMetadataContext.processMetaData(dataSource, Collections.<String>emptyList(), new String[0]);

		List<String> columns = tableMetadataContext.getTableColumns(); 
		
		//List<String> columns = jdbcTemplate.queryForList(queryColumns, String.class, table);
		
		if (table.equals("T_SOURCE")) {
			sql_stmt = "SELECT T_SOURCE.PK_SOURCE_ID AS sourceId,"
	                + "TITLE AS sourceTitle, URL AS sourceUrl,"
	                + "ALIAS AS sourceAlias, CELEX_REF AS sourceCelexRef, SOURCE_CODE AS sourceCode,"
	                + "VALID_FROM AS sourceValidFrom, ABSTRACT AS sourceAbstract, COMMENT AS sourceComment, ISSUED_BY_URL AS sourceIssuedByUrl,"
	                + "EC_ENTRY_INTO_FORCE AS sourceEcEntryIntoForce, EC_ACCESSION AS sourceEcAccession, SECRETARIAT AS sourceSecretariat,"
	                + "SECRETARIAT_URL AS sourceSecretariatUrl, TERMINATE AS sourceTerminate "
	                + "FROM " + table + " WHERE " + id_field + " = ?" + " " + extraSQL;
			
			InstrumentFactsheetDTO instrument = jdbcTemplate.queryForObject(sql_stmt, new BeanPropertyRowMapper<InstrumentFactsheetDTO>(InstrumentFactsheetDTO.class), id);
					
			for (int j = 0; j < columns.size(); j++) {
				aux = true;
				String column = columns.get(j);
				
				switch (column) {
					case "PK_SOURCE_ID":
						value = instrument.getSourceId().toString();
						quotes = "n";
						isPrimary = "y";
						break;
					case "SOURCE_CODE": 
						value = instrument.getSourceCode();
						quotes = "y";
						isPrimary = "n";
						break;
					case "TITLE":
						value = instrument.getSourceTitle();
						quotes = "y";
						isPrimary = "n";
						break;
					case "CELEX_REF":
						value = instrument.getSourceCelexRef();
						quotes = "y";
						isPrimary = "n";
						break;
					case "URL":
						value = instrument.getSourceUrl();
						quotes = "y";
						isPrimary = "n";
						break;
					case "ALIAS":
						value = instrument.getSourceAlias();
						quotes = "y";
						isPrimary = "n";
						break;
					case "VALID_FROM":
						value = instrument.getSourceValidFrom();
						quotes = "y";
						isPrimary = "n";
						break;
					case "ABSTRACT":
						value = instrument.getSourceAbstract();
						quotes = "y";
						isPrimary = "n";
						break;
					case "COMMENT":
						value = instrument.getSourceComment();
						quotes = "y";
						isPrimary = "n";
						break;
					case "ISSUED_BY_URL":
						value = instrument.getSourceIssuedByUrl();
						quotes = "y";
						isPrimary = "n";
						break;
					case "EC_ENTRY_INTO_FORCE":
						value = instrument.getSourceEcEntryIntoForce();
						quotes = "y";
						isPrimary = "n";
						break;
					case "EC_ACCESSION":
						value = instrument.getSourceEcAccession();
						quotes = "y";
						isPrimary = "n";
						break;
					case "SECRETARIAT":
						value = instrument.getSourceSecretariat();
						quotes = "y";
						isPrimary = "n";
						break;
					case "SECRETARIAT_URL":
						value = instrument.getSourceSecretariatUrl();
						quotes = "y";
						isPrimary = "n";
						break;
					case "TERMINATE":
						value = instrument.getSourceTerminate();
						quotes = "y";
						isPrimary = "n";
						break;
					default:
						aux = false;
						break;
				}		
				
				if (aux) {
					//isPrimary = isPrimaryKey(table, column);
					jdbcTemplate.update(insert,
							ts,
							table,
							column,
							state,
							quotes,
							isPrimary,
							value != null ? value : "null",
						    rowCnt,
						    show
							);
				}
				
			}			
					
		} else if (table.equals("T_CLIENT_SOURCE_LNK")) {
			sql_stmt = "SELECT FK_SOURCE_ID AS sourceId, FK_CLIENT_ID AS clientId, STATUS AS clientSourceLnkStatus "
					+ "FROM " + table + " WHERE " + id_field + " = ?";
			
			InstrumentFactsheetDTO instrument = jdbcTemplate.queryForObject(sql_stmt, new BeanPropertyRowMapper<InstrumentFactsheetDTO>(InstrumentFactsheetDTO.class), id);
			for (int j = 0; j < columns.size(); j++) {
				String column = columns.get(j);
				
				switch (column) {
					case "FK_CLIENT_ID":
						value = instrument.getClientId().toString();
						quotes = "n";
						isPrimary = "y";
						break;
					case "FK_SOURCE_ID":
						value = id.toString();
						quotes = "n";
						isPrimary = "y";
						break;
					case "STATUS":
						value = instrument.getClientSourceLnkStatus();
						quotes = "y";
						isPrimary = "y";
						break;
				}
				
				//isPrimary = isPrimaryKey(table, column);
				
				jdbcTemplate.update(insert,
						ts,
						table,
						column,
						state,
						quotes,
						isPrimary,
						value != null ? value : "null",
					    rowCnt,
					    show
						);				
			}
		} else if (table.equals("T_SOURCE_LNK")) {
			sql_stmt = "SELECT PK_SOURCE_LNK_ID AS sourceLnkPKSourceId, FK_SOURCE_CHILD_ID AS sourceLnkFKSourceChildId, CHILD_TYPE AS sourceChildType, "
					+ "FK_SOURCE_PARENT_ID AS sourceLnkFKSourceParentId, PARENT_TYPE AS sourceParentType "
					+ "FROM " + table + " WHERE " + id_field + " = ?" + " " + extraSQL;
			List<InstrumentFactsheetDTO> instruments = jdbcTemplate.query(sql_stmt, new BeanPropertyRowMapper<InstrumentFactsheetDTO>(InstrumentFactsheetDTO.class), id);
			if (instruments != null) {
				for (int i = 0; i < instruments.size(); i++) {
					InstrumentFactsheetDTO instrument = instruments.get(i);
					for (int j = 0; j < columns.size(); j++) {
						String column = columns.get(j);
						
						switch (column) {
							case "PK_SOURCE_LNK_ID":
								value = instrument.getSourceLnkPKSourceId().toString();
								quotes = "n";
								isPrimary = "y";
								break;
							case "FK_SOURCE_CHILD_ID":
								value = instrument.getSourceLnkFKSourceChildId().toString();
								quotes = "n";
								isPrimary = "n";
								break;
							case "CHILD_TYPE":
								value = instrument.getSourceChildType();
								quotes = "y";
								isPrimary = "n";
								break;
							case "FK_SOURCE_PARENT_ID":
								value = instrument.getSourceLnkFKSourceParentId().toString();
								quotes = "n";
								isPrimary = "n";
								break;
							case "PARENT_TYPE":
								value = instrument.getSourceParentType();
								quotes = "y";
								isPrimary = "n";
								break;
						}
			
						//isPrimary = isPrimaryKey(table, column);
						
						jdbcTemplate.update(insert,
								ts,
								table,
								column,
								state,
								quotes,
								isPrimary,
								value != null ? value : "null",
							    rowCnt,
							    show
								);
					}
					rowCnt++;
				}
			}
		} else if (table.equals("T_OBLIGATION")) {
			sql_stmt = "SELECT OB.PK_RA_ID AS obligationId, OB.TITLE AS oblTitle, OB.DESCRIPTION AS description, "
					+ "OB.EEA_PRIMARY as eeaPrimary, OB.EEA_CORE as eeaCore, OB.FLAGGED as flagged, OB.COORDINATOR as coordinator, OB.COORDINATOR_URL as coordinatorUrl, "
					+ "OB.COORDINATOR_ROLE as coordinatorRole, OB.COORDINATOR_ROLE_SUF as coordinatorRoleSuf, OB.NATIONAL_CONTACT as nationalContact, OB.NATIONAL_CONTACT_URL as nationalContactUrl, OB.TERMINATE as terminate, "
					+ "OB.NEXT_REPORTING as nextReporting, "
					+ "OB.NEXT_DEADLINE as nextDeadline, "
					+ "OB.REPORT_FREQ_MONTHS as reportFreqMonths, OB.DATE_COMMENTS as dateComments, OB.FORMAT_NAME as formatName, OB.REPORT_FORMAT_URL as reportFormatUrl, "
					+ "OB.REPORTING_FORMAT as reportingFormat, OB.LOCATION_PTR as locationPtr, OB.LOCATION_INFO as locationInfo, OB.DATA_USED_FOR as dataUsedFor, OB.DATA_USED_FOR_URL as dataUsedForUrl, "
					+ "OB.VALID_SINCE as validSince, "
					+ "OB.AUTHORITY as authority, OB.COMMENT as comment, OB.FIRST_REPORTING as firstReporting, OB.VALID_TO as validTo, "
					+ "OB.REPORT_FREQ_DETAIL AS reportFreqDetail, OB.LAST_UPDATE AS lastUpdate, OB.REPORT_FREQ AS reportFreq, OB.LAST_HARVESTED AS lastHarvested, OB.FK_DELIVERY_COUNTRY_IDS AS deliveryCountryId, "
					+ "OB.NEXT_DEADLINE2 AS nextDeadline2, OB.CONTINOUS_REPORTING AS continousReporting, "
					+ "CRO.ROLE_ID AS coordRoleId, CRO.ROLE_NAME AS coordRoleName, CRO.ROLE_URL AS coordRoleUrl, "
					+ "RRO.ROLE_ID AS respRoleId, RRO.ROLE_NAME AS respRoleName, OB.RESPONSIBLE_ROLE as responsibleRole,OB.RESPONSIBLE_ROLE_SUF as responsibleRoleSuf, "
					+ "SO.PK_SOURCE_ID as sourceId, SO.TITLE AS sourceTitle, SO.ALIAS as sourceAlias, "
					+ "CL.PK_CLIENT_ID as clientId, CL.CLIENT_NAME as clientName "
					+ "FROM T_OBLIGATION OB "
					+ "LEFT JOIN T_SOURCE SO ON SO.PK_SOURCE_ID = OB.FK_SOURCE_ID "
					+ "LEFT JOIN T_ROLE CRO ON CRO.ROLE_ID=OB.COORDINATOR_ROLE "
					+ "LEFT JOIN T_ROLE RRO ON RRO.ROLE_ID=OB.RESPONSIBLE_ROLE "
					+ "LEFT JOIN T_CLIENT_OBLIGATION_LNK CLK ON CLK.STATUS='M' AND CLK.FK_RA_ID=OB.PK_RA_ID "
					+ "LEFT JOIN T_CLIENT CL ON CLK.FK_CLIENT_ID=CL.PK_CLIENT_ID "
					+ "WHERE PK_RA_ID = ?";
			Obligations obligation = jdbcTemplate.queryForObject(sql_stmt, new BeanPropertyRowMapper<Obligations> (Obligations.class), id);
			
			for (int j = 0; j < columns.size(); j++) {
				aux = true;
				String column = columns.get(j);
				switch (column) {
					case "PK_RA_ID":
						value = id.toString();
						quotes = "n";
						isPrimary = "y";
						break;
					case "TITLE":
						value = obligation.getOblTitle();
						quotes = "y";
						isPrimary = "n";
						break;
					case "DESCRIPTION":
						value = obligation.getDescription();
						quotes = "y";
						isPrimary = "n";
						break;
					case "FIRST_REPORTING":
						value = obligation.getFirstReporting();
						quotes = "y";
						isPrimary = "n";
						break;
					case "VALID_TO":
						value = obligation.getValidTo();
						quotes = "y";
						isPrimary = "n";
						break;
					case "REPORT_FREQ_MONTHS":
						value = obligation.getReportFreqMonths();
						quotes = "n";
						isPrimary = "n";
						break;
					case "NEXT_DEADLINE":
						value = obligation.getNextDeadline();
						quotes = "y";
						isPrimary = "n";
						break;
					case "NEXT_REPORTING":
						value = obligation.getNextReporting();
						quotes = "y";
						isPrimary = "n";
						break;
					case "DATE_COMMENTS":
						value = obligation.getDateComments();
						quotes = "y";
						isPrimary = "n";
						break;
					case "FORMAT_NAME":
						value = obligation.getFormatName();
						quotes = "y";
						isPrimary = "n";
					case "REPORT_FORMAT_URL":
						value = obligation.getReportFormatUrl();
						quotes = "y";
						isPrimary = "n";
						break;
					case "VALID_SINCE":
						value = obligation.getValidSince();
						quotes = "y";
						isPrimary = "n";
						break;
					case "REPORTING_FORMAT":
						value = obligation.getReportingFormat();
						quotes = "y";
						isPrimary = "n";
						break;
					case "LOCATION_INFO":
						value = obligation.getLocationInfo();
						quotes = "y";
						isPrimary = "n";
						break;
					case "LOCATION_PTR":
						value = obligation.getLocationPtr();
						quotes = "y";
						isPrimary = "n";
						break;
					case "DATA_USED_FOR":
						value = obligation.getDataUsedFor();
						quotes = "y";
						isPrimary = "n";
						break;
					case "DATA_USED_FOR_URL":
						value = obligation.getDataUsedForUrl();
						quotes = "y";
						isPrimary = "n";
						break;
					case "COORDINATOR_ROLE":
						value = obligation.getCoordinatorRole();
						quotes = "y";
						isPrimary = "n";
						break;
					case "COORDINATOR_ROLE_SUF":
						value = obligation.getCoordinatorRoleSuf();
						quotes = "y";
						isPrimary = "n";
						break;
					case "COORDINATOR":
						value = obligation.getCoordinator();
						quotes = "y";
						isPrimary = "n";
						break;
					case "COORDINATOR_URL":
						value = obligation.getCoordinatorUrl();
						quotes = "y";
						isPrimary = "n";
						break;
					case "RESPONSIBLE_ROLE":
						value = obligation.getResponsibleRole();
						quotes = "y";
						isPrimary = "n";
						break;
					case "RESPONSIBLE_ROLE_SUF":
						value = obligation.getResponsibleRoleSuf();
						quotes = "y";
						isPrimary = "n";
						break;
					case "NATIONAL_CONTACT":
						value = obligation.getNationalContact();
						quotes = "y";
						isPrimary = "n";
						break;
					case "NATIONAL_CONTACT_URL":
						value = obligation.getNationalContactUrl();
						quotes = "y";
						isPrimary = "n";
						break;
					case "EEA_PRIMARY":
						if (obligation.getEeaPrimary() != null) {
							value = obligation.getEeaPrimary().toString();
						} else {
							value = null;
						}		
						quotes = "n";
						isPrimary = "n";
						break;
					case "EEA_CORE":
						if (obligation.getEeaCore() != null) {
							value = obligation.getEeaCore().toString();
						} else {
							value = null;
						}		
						quotes = "n";
						isPrimary = "n";
						break;
					case "FLAGGED":
						if (obligation.getFlagged() != null) {
							value = obligation.getFlagged().toString();
						} else {
							value = null;
						}		
						quotes = "n";
						isPrimary = "n";
						break;
					case "COMMENT":
						value = obligation.getComment();
						quotes = "y";
						isPrimary = "n";
					case "AUTHORITY":
						value = obligation.getAuthority();
						quotes = "y";
						isPrimary = "n";
						break;
					case "FK_SOURCE_ID":
						value = obligation.getSourceId();
						quotes = "n";
						isPrimary = "n";
						break;
					case "REPORT_FREQ_DETAIL":
						value = obligation.getReportFreqDetail();
						quotes = "y";
						isPrimary = "n";
						break;
					case "LAST_UPDATE":
						value = obligation.getLastUpdate();
						quotes = "y";
						isPrimary = "n";
						break;
					case "TERMINATE":
						value = obligation.getTerminate();
						quotes = "y";
						isPrimary = "n";
						break;
					case "REPORT_FREQ":
						value = obligation.getTerminate();
						quotes = "y";
						isPrimary = "n";
						break;
					case "FK_DELIVERY_COUNTRY_IDS":
						value = obligation.getDeliveryCountryId();
						quotes = "y";
						isPrimary = "n";
						break;
					case "NEXT_DEADLINE2":
						value = obligation.getNextDeadline2();
						quotes = "y";
						isPrimary = "n";
						break;
					case "LAST_HARVESTED":
						value = obligation.getLastHarvested();
						quotes = "y";
						isPrimary = "n";
						break;
					case "CONTINOUS_REPORTING":
						value = obligation.getContinousReporting();
						quotes = "y";
						isPrimary = "n";
						break;
					default:
						aux = false;
						break;
				}
				//System.out.println(value);
				if (aux) {
					jdbcTemplate.update(insert,
							ts,
							table,
							column,
							state,
							quotes,
							isPrimary,
							value != null ? value : "null",
						    rowCnt,
						    show
							);
				}				
				
			}
			
		} else if (table.equals("T_RAISSUE_LNK")) {
			sql_stmt = "SELECT FK_ISSUE_ID AS issueId "
					+ "FROM T_RAISSUE_LNK "
					+ "WHERE FK_RA_ID=?";
			List<Obligations> obligations = jdbcTemplate.query(sql_stmt, new BeanPropertyRowMapper<Obligations>(Obligations.class), id);
			if (obligations != null) {
				for (int i = 0; i < obligations.size(); i++) {
					Obligations obligation = obligations.get(i);
					for (int j = 0; j < columns.size(); j++) {
						String column = columns.get(j);
						
						switch (column) {
							case "FK_ISSUE_ID":
								value = obligation.getIssueId().toString();
								quotes = "n";
								isPrimary = "y";
								break;
							case "FK_RA_ID":
								value = id.toString();
								quotes = "n";
								isPrimary = "y";
								break;
						}
						
						//isPrimary = isPrimaryKey(table, column);
						
						jdbcTemplate.update(insert,
								ts,
								table,
								column,
								state,
								quotes,
								isPrimary,
								value != null ? value : "null",
							    rowCnt,
							    show
								);
						
					}
					rowCnt++;
				}
			}
		} else if (table.equals("T_RASPATIAL_LNK")) {
			sql_stmt = "SELECT FK_SPATIAL_ID AS spatialId, VOLUNTARY AS voluntary "
					+ "FROM T_RASPATIAL_LNK "
					+ "WHERE FK_RA_ID=?";
			List<Obligations> obligations = jdbcTemplate.query(sql_stmt, new BeanPropertyRowMapper<Obligations>(Obligations.class), id);
			
			if (obligations != null) {
				for (int i = 0; i < obligations.size(); i++) {
					Obligations obligation = obligations.get(i);
					for (int j = 0; j < columns.size(); j++) {
						String column = columns.get(j);
						
						switch (column) {
							case "FK_SPATIAL_ID":
								value = obligation.getSpatialId().toString();
								quotes = "n";
								isPrimary = "y";
								break;
							case "FK_RA_ID":
								value = id.toString();
								quotes = "n";
								isPrimary = "y";
								break;
							case "VOLUNTARY":
								value = obligation.getVoluntary();
								quotes = "y";
								isPrimary = "y";
								break;
						}
						
						//isPrimary = isPrimaryKey(table, column);
						
						jdbcTemplate.update(insert,
								ts,
								table,
								column,
								state,
								quotes,
								isPrimary,
								value != null ? value : "null",
							    rowCnt,
							    show
								);
							
					}
					rowCnt++;
				}
			}
		} else if(table.equals("T_CLIENT_OBLIGATION_LNK")) {
			sql_stmt = "SELECT FK_CLIENT_ID AS clientLnkFKClientId, STATUS AS clientLnkStatus "
					+ "FROM T_CLIENT_OBLIGATION_LNK "
					+ "WHERE FK_RA_ID=?";
			List<Obligations> obligations = jdbcTemplate.query(sql_stmt, new BeanPropertyRowMapper<Obligations>(Obligations.class), id);
			
			if (obligations != null) {
				for (int i = 0; i < obligations.size(); i++) {
					Obligations obligation = obligations.get(i);
					for (int j = 0; j < columns.size(); j++) {
						String column = columns.get(j);
						switch (column) {
							case "FK_CLIENT_ID":
								value = obligation.getClientLnkFKClientId();
								quotes = "n";
								isPrimary = "y";
								break;
							case "FK_RA_ID":
								value = id.toString();
								quotes = "n";
								isPrimary = "y";
								break;
							case "STATUS":
								value = obligation.getClientLnkStatus();
								quotes = "y";
								isPrimary = "y";
								break;
						}
						
						//isPrimary = isPrimaryKey(table, column);
						
						jdbcTemplate.update(insert,
								ts,
								table,
								column,
								state,
								quotes,
								isPrimary,
								value != null ? value : "null",
							    rowCnt,
							    show
								);
						
					}
					rowCnt++;
				}
			}
		} else if(table.equals("T_OBLIGATION_RELATION")) {
			String queryCount = "SELECT Count(*) "
	                + "FROM T_OBLIGATION_RELATION "
	                + "WHERE FK_RA_ID = ? ";
			Integer countObligation = jdbcTemplate.queryForObject(queryCount, Integer.class, id);
			
			if (countObligation > 0) {
				sql_stmt = "SELECT RELATION AS oblRelationId, FK_RA_ID2 AS relObligationId "
						+ "FROM T_OBLIGATION_RELATION "
						+ "WHERE FK_RA_ID=?";
				Obligations obligation = jdbcTemplate.queryForObject(sql_stmt, new BeanPropertyRowMapper<Obligations>(Obligations.class), id);
				for (int j = 0; j < columns.size(); j++) {
					String column = columns.get(j);
					switch (column) {
						case "FK_RA_ID":
							value = id.toString();
							quotes = "n";
							isPrimary = "y";
						case "RELATION":
							value = obligation.getOblRelationId();
							quotes = "y";
							isPrimary = "n";
						case "FK_RA_ID2":
							value = obligation.getRelObligationId().toString();
							quotes = "n";
							isPrimary = "y";
					}
					
					jdbcTemplate.update(insert,
							ts,
							table,
							column,
							state,
							quotes,
							isPrimary,
							value != null ? value : "null",
						    rowCnt,
						    show
							);					
				
				}
			}
			
		}
		
		
	}
	
	/*private String isPrimaryKey(String table, String column)  {
		String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE "
				+ "TABLE_NAME = ? AND COLUMN_KEY IN('PRI')";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<String> prymaryKeys = jdbcTemplate.queryForList(query, String.class, table);
		
		if (prymaryKeys.contains(column)) {
			return "y";
		} else {
			return "n";
		}		
		
	}*/

	@Override
	public void insertTransactionInfo(Integer id, String state, String table, String id_field, long ts,
			String extraSQL) {

		String whereClause = id_field + " = " + id + " " + extraSQL;
		String insert = "INSERT INTO T_UNDO VALUES (?,?,?,?,'y','n',?,0,'n')";
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		jdbcTemplate.update(insert,
				ts,
				table,
				id_field,
				state,
				whereClause);
		
	}

	@Override
	public void addObligationIdsIntoUndo(Integer id, long ts, String table) {
		String query = "SELECT PK_RA_ID " +
				"FROM T_OBLIGATION " +
				"WHERE FK_SOURCE_ID=?";
		String insert = "INSERT INTO T_UNDO VALUES (?,?,'OBLIGATIONS','O','y','n',?,0,'y')";
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		List<Integer> keys = jdbcTemplate.queryForList(query, Integer.class, id);
		StringBuffer obligation_ids = new StringBuffer();
		 
		if (keys != null) {
			for (int i = 0; i < keys.size(); i++) {
				obligation_ids.append(keys.get(i));
				if (i+1 < keys.size()) {
					obligation_ids.append(",");
				}
			}
			jdbcTemplate.update(insert,
					ts,
					table,
					obligation_ids.toString());
		}
		
	}

	@Override
	public List<UndoDTO> getPreviousActionsReportSpecific(Integer id, String tab, String id_field, String operation) {
		String query = "SELECT UNDO_TIME AS undoTime FROM T_UNDO WHERE COL=? "
				+ "AND VALUE=? AND TAB=? AND OPERATION=? "
				+ "ORDER BY UNDO_TIME DESC";
		
		String queryUser = "SELECT VALUE FROM T_UNDO WHERE UNDO_TIME=? "
				+ "AND COL=? AND TAB=?";
		
		String queryCount = "SELECT Count(*) "
				+ "FROM T_UNDO WHERE COL=? AND VALUE=? AND TAB=? AND OPERATION=?";
		
		String user;
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		Integer countObligation = jdbcTemplate.queryForObject(queryCount, Integer.class, id_field, id, tab, operation);	
		
		if (countObligation > 0) {
			List<UndoDTO> versions = jdbcTemplate.query(query, new BeanPropertyRowMapper<UndoDTO> (UndoDTO.class), id_field, id, tab, operation);
			for (int i = 0; i < versions.size(); i++) {
				user = jdbcTemplate.queryForObject(queryUser, String.class, versions.get(i).getUndoTime(), "A_USER", tab);
				versions.get(i).setValue(user);
			}
			return versions;
		} else {
			return null;
		}
				
	}
	
}
