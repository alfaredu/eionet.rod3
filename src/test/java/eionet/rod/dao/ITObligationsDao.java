package eionet.rod.dao;

import eionet.rod.model.*;
import eionet.rod.service.ObligationService;
import eionet.rod.util.RODUtil;
import eionet.rod.util.exception.ResourceNotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//import org.junit.Rule;
//import org.junit.rules.ExpectedException;
//import java.io.IOException;


/**
 * Test the spatial dao.
 */


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})
@Sql("/seed-obligation-source.sql")
public class ITObligationsDao {

//  @Rule
//  public ExpectedException exception = ExpectedException.none();

    @Autowired
    private ObligationService obligationsService;

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void testfindAll() throws ResourceNotFoundException {
        List<Obligations> obligations = obligationsService.findAll();
        assertEquals("1", obligations.get(0).getObligationId().toString());
        assertEquals("Fuel Quality Directive Article 7a", obligations.get(0).getOblTitle());
        assertEquals("2", obligations.get(1).getObligationId().toString());
        assertEquals("Test 2 - Fuel Quality Directive Article 7a", obligations.get(1).getOblTitle());

    }

    @Test
    public void testfindObligationList() throws ResourceNotFoundException {
        List<Obligations> obligations = obligationsService.findObligationList("0", "0", "0", "N", "0", null, null, null, false);
        //System.out.print(obligations.size());
        assertEquals("1", obligations.get(0).getObligationId().toString());
        assertEquals("eionet-nrc-waterquality", obligations.get(0).getRespRoleId());
        assertEquals("Test client", obligations.get(0).getClientName());

        obligations = obligationsService.findObligationList("0", "0", "0", "Y", "0", null, null, null, false);
        assertEquals("1", obligations.get(0).getObligationId().toString());
        assertEquals("eionet-nrc-waterquality", obligations.get(0).getRespRoleId());
        assertEquals("Test client", obligations.get(0).getClientName());
    }

    @Test
    public void testfindOblId() {
        Obligations obligation = obligationsService.findOblId(1);
        assertEquals("1", obligation.getObligationId().toString());
        assertEquals("Fuel Quality Directive Article 7a", obligation.getOblTitle());

        exception.expect(ResourceNotFoundException.class);
        obligation = obligationsService.findOblId(12);
        // todo implement
    }

    @Test
    public void testfindSiblingObligations() throws ResourceNotFoundException {
        List<SiblingObligation> siblingObligations = obligationsService.findSiblingObligations(1);
        //System.out.print("size:" + siblingObligations.size());
        //System.out.print("obl:" + siblingObligations.get(0).getSiblingOblId());
        //System.out.print("fk_source:" + siblingObligations.get(0).getFkSourceId());
        assertEquals("2", siblingObligations.get(0).getSiblingOblId());
        assertEquals("1", siblingObligations.get(0).getFkSourceId());
        assertEquals("Test 2 - Fuel Quality Directive Article 7a", siblingObligations.get(0).getSiblingTitle());
        assertEquals("Y", siblingObligations.get(0).getTerminate());
        assertEquals("Article 15", siblingObligations.get(0).getAuthority());
        List<SiblingObligation> siblingObligationsNull = obligationsService.findSiblingObligations(12);
        assertEquals(0, siblingObligationsNull.size());
    }

    @Test
    public void findAllCountriesByObligation() {
        List<Spatial> spatialNoVol = obligationsService.findAllCountriesByObligation(1, "N");
        //System.out.print(spatialNoVol.size());

        assertEquals("1", spatialNoVol.get(0).getSpatialId().toString());
        assertEquals("Austria", spatialNoVol.get(0).getName());
        assertEquals(0, obligationsService.findAllCountriesByObligation(1, "C").size());


    }

    @Test
    public void testfindAllIssuesbyObligation() {
        List<Issue> issues = obligationsService.findAllIssuesbyObligation(1);
        assertEquals("1", issues.get(0).getIssueId().toString());
        assertEquals("Climate Change", issues.get(0).getIssueName());

        assertEquals(0, obligationsService.findAllIssuesbyObligation(12).size());
    }

    @Test
    public void testinsertupdateObligation() throws ResourceNotFoundException {
        //data obligations
        Obligations obligation = new Obligations();
        obligation.setOblTitle("Test insert OBLIGATION");
        obligation.setSourceId("1");
        obligation.setDescription("Test DEscription Obligation");
        obligation.setTerminate("N");
        obligation.setCoordinatorRoleSuf("1");
        obligation.setResponsibleRoleSuf("0");
        obligation.setValidTo(RODUtil.readDate("21/12/2017"));
        obligation.setClientId("1");
        obligation.setReportFreqMonths("5");

        //data ClientDTO
        List<ClientDTO> clients = new ArrayList<>();
        ClientDTO client = new ClientDTO();
        client.setClientId(1);
        clients.add(client);

        List<Spatial> spatials = new ArrayList<>();
        Spatial spatial = new Spatial();
        spatial.setSpatialId(1);
        spatials.add(spatial);
        spatial = new Spatial();
        spatial.setSpatialId(2);
        spatials.add(spatial);

        List<Spatial> spatialsVoluntary = new ArrayList<>();
        Spatial spatialVoluntary = new Spatial();
        spatialVoluntary.setSpatialId(3);
        spatialsVoluntary.add(spatialVoluntary);


        List<Issue> issues = new ArrayList<>();
        Issue issue = new Issue();
        issue.setIssueId(1);
        issues.add(issue);


        Integer intObligationId = obligationsService.insertObligation(obligation, clients, spatials, spatialsVoluntary, issues);

        Obligations obliagationResult = obligationsService.findOblId(intObligationId);
        assertEquals(obligation.getOblTitle(), obliagationResult.getOblTitle());
        assertEquals(obligation.getClientId(), obliagationResult.getClientId());

        obligation.setObligationId(intObligationId);
        obligation.setDescription("Update Test Dscription Obligation");
        obligation.setReportFreqMonths("10");

        spatials = new ArrayList<>();
        spatial = new Spatial();
        spatial.setSpatialId(1);
        spatials.add(spatial);

        spatialsVoluntary = new ArrayList<>();
        spatialVoluntary = new Spatial();
        spatialVoluntary.setSpatialId(2);
        spatialsVoluntary.add(spatialVoluntary);

        issues = new ArrayList<>();
        issue = new Issue();
        issue.setIssueId(2);
        issues.add(issue);

        obligationsService.updateObligations(obligation, null, spatials, spatialsVoluntary, issues);

        obligationsService.deleteObligations(intObligationId.toString());

        exception.expect(ResourceNotFoundException.class);
        obligation = obligationsService.findOblId(intObligationId);
        // todo implement
    }

    @Test
    public void testfindObligationRelation() throws ResourceNotFoundException {
        Obligations oblRelation = obligationsService.findObligationRelation(1);
        assertEquals("replaces", oblRelation.getOblRelationId());
        assertEquals("2", oblRelation.getRelObligationId().toString());

    }

    @Test
    public void testfindAllClientsByObligation() {
        List<ClientDTO> clients = obligationsService.findAllClientsByObligation(1);
        assertEquals(2, clients.size());
        assertTrue(clients.get(0).getName().contains("Test client"));
        assertEquals(0, obligationsService.findAllClientsByObligation(175).size());
    }

}
