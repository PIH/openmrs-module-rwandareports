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
package org.openmrs.module.rwandareports.reporting;

import org.junit.Ignore;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.rwandareports.util.MetadataLookup;

import java.util.Arrays;
import java.util.List;

/**
 * Tests the output of the Adult HIV Consultation Sheet Report
 */
@Ignore
public class SetupAdultHIVConsultationSheetTest extends SetupReportTest {
	
	@Override
	protected void setupTestData() {
		// TODO: Implement me
	}
	
	@Override
	public String getReportName() {
		return "HIV-Adult Consultation Sheet";
	}
	
	@Override
	public List<String> getReportDesignNames() {
		return Arrays.asList("AdultHIVConsultationSheet.xls_");
	}
	
	@Override
	public SetupReport getSetupReportClass() {
		return new SetupAdultHIVConsultationSheet();
	}
	
	@Override
	public EvaluationContext getEvaluationContext() {
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("location", getLocation("Kirehe Health Center"));
		context.addParameterValue("state",
		    MetadataLookup.getProgramWorkflowState("HIV PROGRAM", "TREATMENT GROUP", "GROUP 1"));
		return context;
	}
	
	@Override
	public void testResults(ReportData data) {
		printReportData(data);
		
		// TODO: Find some way to test the output of this
	}
}
