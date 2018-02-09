package eionet.rod.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eionet.rod.dao.AnalysisService;
import eionet.rod.model.AnalysisDTO;
import eionet.rod.util.BreadCrumbs;

@Controller
@RequestMapping("/analysis")
public class AnalysisController {
	
	@Autowired
    AnalysisService analysisService;

	@RequestMapping({"", "/"})
	public String analysisHome(Model model) {
		
		AnalysisDTO analysis = analysisService.getStatistics();
		model.addAttribute("analysis", analysis);
		BreadCrumbs.set(model, "Database Content Statistics");
		model.addAttribute("activeTab", "analysis");
		model.addAttribute("title","Database Content Statistics");
		return "analysis";		
	}

}