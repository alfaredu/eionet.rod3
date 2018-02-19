/**
 * 
 */
package eionet.rod.dao;

import java.util.List;

import org.springframework.context.ApplicationContextException;

import eionet.rod.model.ClientDTO;
import eionet.rod.model.Issue;
import eionet.rod.model.Obligations;
import eionet.rod.model.SiblingObligation;
import eionet.rod.model.Spatial;

/**
 * @author ycarrasco
 *
 */
public interface ObligationsDao {

	List<Obligations> findAll();
	
	Obligations findOblId(Integer obligationId) throws ApplicationContextException;
	
	List<SiblingObligation> findSiblingObligations(Integer obligationId);
	
	Integer insertObligation(Obligations obligation, List<ClientDTO> allObligationClients, List<Spatial> allObligationCountries,List<Spatial> allObligationVoluntaryCountries, List<Issue> allSelectedIssues);
	
	void updateObligations(Obligations obligations, List<ClientDTO> allObligationClients, List<Spatial> allObligationCountries,List<Spatial> allObligationVoluntaryCountries, List<Issue> allSelectedIssues);
	
	List<Spatial> findAllCountriesByObligation(Integer ObligationID, String voluntary);
	
	List<Issue> findAllIssuesbyObligation(Integer ObligationID);
	
	List<Obligations> findObligationList(String clientId, String issueId, String spatialId, String terminate, String deadlineCase, String anmode);
	
	void deleteObligations(String obligations);
	
	Obligations findObligationRelation(Integer obligationId);
		
}
