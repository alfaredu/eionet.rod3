package eionet.rod.service;

import eionet.rod.model.*;

import java.util.List;

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
