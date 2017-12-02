package eionet.rod.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor.*;

import javax.sql.DataSource;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.junit.Before;
import org.junit.Ignore;
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
 * Test the clients controller.
 */
public class ITClientsController {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private DataSource dataSource;

    private IDatabaseTester databaseTester;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
            .addFilters(this.springSecurityFilterChain)
            .build();
        databaseTester = new DataSourceDatabaseTester(dataSource);
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(getClass().getClassLoader().getResourceAsStream("seed-clients.xml"));
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
    }

    /**
     * Simple test to list clients.
     */
    @Test
    public void listClients() throws Exception {
        this.mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("breadcrumbs"))
                .andExpect(model().attributeExists("allClients"))
                .andExpect(view().name("clients"));
    }

    /**
     * Since it is protected, it will redirect to login.
     */
    @Test
    public void clientEdit() throws Exception {
        this.mockMvc.perform(get("/clients/edit")
                .param("clientId", "1"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Login and edit.
     */
    @Test
    public void clientEditWithAuth() throws Exception {
        this.mockMvc.perform(get("/clients/edit")
                .param("clientId", "1")
                //.with(csrf()).with(user("editor").roles("EDITOR")))
                .with(user("editor").roles("EDITOR")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("client"));
    }

    /**
     * Login and post an update.
     */
    @Test
    public void clientPostWithAuth() throws Exception {
        this.mockMvc.perform(post("/clients/edit")
                .param("clientId", "1")
                .param("name", "New Name")
                .with(csrf()).with(user("editor").roles("EDITOR")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("view?message=Client+1+updated"))
                .andExpect(model().attributeExists("message"));
    }


    /**
     * Login and post an update, but without CSRF.
     * CSRF is Cross Site Request Forgery prevention
     */
    @Test
    public void clientPostWithoutCSRF() throws Exception {
        this.mockMvc.perform(post("/clients/edit")
                .param("clientId", "1")
                .param("name", "New Name")
                .with(user("editor").roles("EDITOR")))
                .andExpect(status().is4xxClientError());
    }

}
