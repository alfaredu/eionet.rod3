package eionet.rod.controller;

import eionet.rod.service.*;
import eionet.rod.IAuthenticationFacade;
import eionet.rod.UNSEventSender;
import eionet.rod.model.*;
import eionet.rod.util.BreadCrumbs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * Spatial managing controller.
 */
@Controller
@RequestMapping("/subscribe")
public class SubscribeController {


    @Autowired
    IssueService issueService;

    @Autowired
    SpatialService spatialService;

    @Autowired
    ClientService clientService;

    @Autowired
    ObligationService obligationService;

    @Autowired
    SourceService sourceService;

    @Autowired
    IAuthenticationFacade authenticationFacade;

    @RequestMapping({"", "/", "/view"})
    public String viewObligations(Model model, @RequestParam(required = false) String message) {
        BreadCrumbs.set(model, "Subscribe to notifications - ROD");

        Subscribe subscribe = new Subscribe();

        model.addAttribute("title", "Subscribe to notifications - ROD");

        List<Obligations> obligations = obligationService.findAll();
        model.addAttribute("obligations", obligations);

        //Environmental issues
        List<Issue> issues = issueService.findAllIssuesList();
        model.addAttribute("issues", issues);

        //Countries/territories
        List<Spatial> countries = spatialService.findAll();
        model.addAttribute("allcountries", countries);

        //Countries/territories
        List<ClientDTO> clients = clientService.getAllClients();
        model.addAttribute("allClients", clients);

        List<InstrumentFactsheetDTO> instruments = sourceService.getAllInstruments();
        model.addAttribute("instruments", instruments);

        model.addAttribute("activeTab", "subscribe");

        model.addAttribute("subscribe", subscribe);

        if (message != null) model.addAttribute("message", message);
        return "subscribe";
    }


    /**
     * @param subscribe
     * @param model
     * @return view obligations
     */
    @RequestMapping(method = RequestMethod.POST)
    public String subscriptions(Subscribe subscribe, Model model) {

        model.addAttribute("activeTab", "subscribe");

        model.addAttribute("subscribe", subscribe);

        model.addAttribute("title", "Subscribe to notifications - ROD");

        List<Obligations> obligations = obligationService.findAll();
        model.addAttribute("obligations", obligations);

        //Environmental issues
        List<Issue> issues = issueService.findAllIssuesList();
        model.addAttribute("issues", issues);

        //Countries/territories
        List<Spatial> countries = spatialService.findAll();
        model.addAttribute("allcountries", countries);

        //Countries/territories
        List<ClientDTO> clients = clientService.getAllClients();
        model.addAttribute("allClients", clients);

        List<InstrumentFactsheetDTO> instruments = sourceService.getAllInstruments();
        model.addAttribute("instruments", instruments);

        Authentication authentication = authenticationFacade.getAuthentication();
        try {
            UNSEventSender.subscribe(authentication.getName(), subscribe);
            model.addAttribute("message", "Subscription successful");

        } catch (Exception e) {
            model.addAttribute("message", "Subscription unsuccessful! " + e.getMessage());
        }

        return "subscribe";
    }

}
