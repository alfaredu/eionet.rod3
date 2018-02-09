package eionet.rod.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.sql.DataSource;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})

/**
 * Test the intruments controller.
 */
public class ITInstrumentsController {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private DataSource datasource;

    private IDatabaseTester databaseTester;
    
    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
            .addFilters(this.springSecurityFilterChain)
            .build();
        databaseTester = new DataSourceDatabaseTester(datasource);
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(getClass().getClassLoader().getResourceAsStream("seed-obligation-source.xml"));
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
    }

      
    /**
     * Simple test to view list of the instruments.
     */
    @Test
    public void viewInstruments() throws Exception {
    	 this.mockMvc.perform(get("/instruments"))
	         .andExpect(status().isOk())
	         .andExpect(model().attributeExists("breadcrumbs"))
	         .andExpect(model().attributeExists("title"))
	         .andExpect(model().attributeExists("activeTab"))
	         .andExpect(view().name("instruments"));
    }
    
    @Test
    public void sourceFactsheet() throws Exception {
    	 this.mockMvc.perform(get("/instruments/1")
    	 		.param("sourceId","1"))
    			.andExpect(status().isOk());
    }
    
    @Test 
    public void editInstrumentForm() throws Exception
	{
    	this.mockMvc.perform(get("/instruments/edit?sourceId=1"))
    			.andExpect(status().isOk())
    			.andExpect(view().name("instrumentEditForm"));
    			
    }
    
    @Test
    public void addInstrumentForm() throws Exception
    {
    	this.mockMvc.perform(get("/instruments/add")
    	    	.with(user("editor").roles("EDITOR")))
    			.andExpect(status().isOk()) 
    			.andExpect(model().attributeExists("title"))
    			.andExpect(model().attributeExists("activeTab"))
    			.andExpect(view().name("instrumentEditForm"));
    }
    
    @Test
    public void addInstrument() throws Exception
    {
    	this.mockMvc.perform(post("/instruments/add")
    			.param("sourceId","4")
    			.param("sourceTitle", "Test Yoly")
    			.param("sourceAlias", "Test Yoly")
    			.param("sourceCode", "2222")
    			.param("sourceTerminate", "N")
    			.param("SourceUrl", "https://www.google.es")
    			.param("sourceLnkFKSourceParentId", "1")
    			.param("SourceCelexRef", "31977D0795")
    			.param("clientId", "2")
    			.param("sourceValidFrom","")
    			.param("sourceEcEntryIntoForce","")
    			.param("SourceEcAccession","")
    			.param("selectedClassifications", "1")
    			.with(user("editor").roles("EDITOR"))
        		.with(csrf()))
    			.andExpect(status().is3xxRedirection());
    	
    }
    
    @Test 
    public void deleteInstrumentsWithCsrf() throws Exception
	{
    	this.mockMvc.perform(post("/instruments/delete")
    			.param("sourceId", "4")
    			.with(user("editor").roles("EDITOR"))
        		.with(csrf()))
        		.andExpect(status().is3xxRedirection());

    }
    
    @Test 
    public void deleteInstrumentsWithoutCsrf() throws Exception
	{
    	this.mockMvc.perform(post("/instruments/delete")
    			.param("sourceId", "4")
    			.with(user("editor").roles("EDITOR")))
        		.andExpect(status().is4xxClientError());

    }
       
    
    @Test 
    public void editInstrument() throws Exception
	{
    	this.mockMvc.perform(post("/instruments/edit")
    			.param("sourceId","1")
    			.param("sourceTitle", "Basel Convention on the control of transboundary movements of hazardous wastes and their disposal")
    			.param("sourceAlias", "Basel Conventions")
    			.param("sourceCode", "")
    			.param("sourceUrl", "")
    			.param("sourceCelexRef", "")
    			.param("clientId", "2")
    			.param("sourceIssuedByUrl", "")
    			.param("sourceLnkFKSourceParentId","1")
    			.param("_selectedClassifications", "1")
    			.param("selectedClassifications", "1")
    			.param("selectedClassifications", "2")
    			.param("sourceValidFrom","")
				.param("SourceEcAccession","")
				.param("sourceEcEntryIntoForce","")
    			.param("edit","Save changes")
    			.with(user("editor").roles("EDITOR"))
    			.with(csrf()))
    			.andExpect(status().is3xxRedirection());
    }
    
            
}