package org.openmrs.module.rwandareports.web.controller;

import org.openmrs.module.rwandareports.util.ReportSetup;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RwandaRegisterAllReportsFormController {
	
	@RequestMapping("/module/rwandareports/register_allReports")
	public ModelAndView registerAllReports() throws Exception {
		ReportSetup.registerHIVReports();
		ReportSetup.registerNCDReports();
		ReportSetup.registerCentralReports();
		ReportSetup.registerSiteReports();
		ReportSetup.registerCHWReports();
		ReportSetup.registerOncologyReports();
		ReportSetup.registerPDCReports();
		ReportSetup.registerMHReports();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	
	@RequestMapping("/module/rwandareports/register_allHIVReports")
	public ModelAndView registerAllHIVReports() throws Exception {
		ReportSetup.registerHIVReports();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	
	@RequestMapping("/module/rwandareports/register_allNCDReports")
	public ModelAndView registerAllNCDReports() throws Exception {
		ReportSetup.registerNCDReports();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	
	@RequestMapping("/module/rwandareports/register_allCentralReports")
	public ModelAndView registerAllCentralReports() throws Exception {
		ReportSetup.registerCentralReports();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	
	@RequestMapping("/module/rwandareports/register_allSiteReports")
	public ModelAndView registerAllSiteReports() throws Exception {
		ReportSetup.registerSiteReports();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	
	@RequestMapping("/module/rwandareports/register_allOncologyReport")
	public ModelAndView registerAllOncologyReports() throws Exception {
		ReportSetup.registerOncologyReports();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	
	@RequestMapping("/module/rwandareports/register_allCHWReport")
	public ModelAndView registerAllCHWReports() throws Exception {
		ReportSetup.registerCHWReports();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	
	@RequestMapping("/module/rwandareports/register_allPDCReport")
	public ModelAndView registerAllregisterPDCReports() throws Exception {
		ReportSetup.registerPDCReports();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	
	@RequestMapping("/module/rwandareports/register_allMHReport")
	public ModelAndView registerAllregisterMHReports() throws Exception {
		ReportSetup.registerMHReports();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	
}
