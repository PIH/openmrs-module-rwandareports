/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.rwandareports.web.controller;

import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.rwandareports.patientsummary.PatientSummaryManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Responsible for loading the model and delegating to the view for all patient summaries
 */
@Controller
public class PatientSummaryController {
	
	@RequestMapping("/module/rwandareports/patientSummary.form")
	public String handle(Model model, @RequestParam("type") Class<? extends PatientSummaryManager> type,
	        @RequestParam("patientId") Integer patientId) throws Exception {
		
		PatientSummaryManager manager = (PatientSummaryManager) type.newInstance();
		ReportDefinition rd = manager.constructReportDefinition();
		
		model.addAttribute("patientId", patientId);
		model.addAttribute("patientSummaryManager", manager);
		model.addAttribute("reportDefinition", rd);
		
		EvaluationContext context = new EvaluationContext();
		Cohort c = new Cohort();
		c.addMember(patientId);
		context.setBaseCohort(c);
		
		ReportData data = Context.getService(ReportDefinitionService.class).evaluate(rd, context);
		SimpleDataSet dataSet = (SimpleDataSet) data.getDataSets().values().iterator().next();
		for (DataSetColumn column : dataSet.getMetaData().getColumns()) {
			model.addAttribute(column.getName(), dataSet.getColumnValue(patientId, column.getName()));
		}
		
		return "/module/rwandareports/patientSummaries/" + manager.getKey();
	}
	
}
