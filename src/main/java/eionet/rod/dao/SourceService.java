package eionet.rod.dao;

import java.util.List;

import eionet.rod.model.InstrumentClassificationDTO;

import eionet.rod.model.InstrumentFactsheetDTO;
import eionet.rod.model.InstrumentObligationDTO;
import eionet.rod.model.InstrumentsListDTO;
import eionet.rod.model.HierarchyInstrumentDTO;


/**
 * Service to store metadata for T_SOURCE using JDBC.
 */
public interface SourceService {
	
	InstrumentFactsheetDTO getById(Integer sourceId);
	
	List<InstrumentObligationDTO> getObligationsById(Integer sourceId);
	
	void update(InstrumentFactsheetDTO instrumentFactsheetRec);
	
	Integer insert(InstrumentFactsheetDTO instrumentFactsheetRec);
	
	List<InstrumentFactsheetDTO> getAllInstruments();
	
	List<InstrumentClassificationDTO> getAllClassifications();
	
	void insertClassifications(InstrumentFactsheetDTO instrumentFactsheetRec);
	
	void deleteClassifications(Integer sourceId);
	
	String getHierarchy(Integer id, boolean hasParent, String mode);
	
	InstrumentsListDTO getHierarchyInstrument(Integer id);
	
	List<HierarchyInstrumentDTO> getHierarchyInstruments(Integer id);
	
	void delete(Integer sourceId);
		
}
