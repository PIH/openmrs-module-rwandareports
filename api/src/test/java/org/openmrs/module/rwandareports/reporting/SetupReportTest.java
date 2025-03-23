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

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rwandareports.BaseRwandaReportsTest;

import java.util.List;

/**
 * Abstract class for testing Rwanda Reports
 */
public abstract class SetupReportTest extends BaseRwandaReportsTest {
	
	/**
	 * @return the name of the report that this is testing
	 */
	public abstract String getReportName();
	
	/**
	 * @return the names of any saved Report Designs that are created for this report
	 */
	public abstract List<String> getReportDesignNames();
	
	/**
	 * @return an instance of SetupReport that is responsible for creating and deleting the report
	 */
	public abstract SetupReport getSetupReportClass();
	
	/**
	 * @return the names and values of the parameters to run the report with
	 */
	public abstract EvaluationContext getEvaluationContext();
	
	/**
	 * This method should be overridden to apply any specific tests to the evaluated report data for
	 * the given input parameters
	 */
	public abstract void testResults(ReportData data);
	
	@Before
	public void setup() throws Exception {
		authenticate();
		getSetupReportClass().delete();
	}
	
	/**
	 * Tests that the Report and associated ReportDesigns are successfully saved
	 */
	@Test
	public void shouldSetupReport() throws Exception {
		List<ReportDefinition> l = getReportDefinitionService().getDefinitions(getReportName(), true);
		Assert.assertEquals(0, l.size());
		
		ReportDefinition reportDefinition = setupReport();
		Assert.assertEquals(getReportName(), reportDefinition.getName());
		
		ReportService rs = Context.getService(ReportService.class);
		List<ReportDesign> designs = rs.getReportDesigns(reportDefinition, null, false);
		
		Assert.assertEquals(getReportDesignNames().size(), designs.size());
		int numMatches = 0;
		for (ReportDesign rd : designs) {
			if (getReportDesignNames().contains(rd.getName())) {
				numMatches++;
			}
		}
		Assert.assertEquals(getReportDesignNames().size(), numMatches);
	}
	
	/**
	 * Tests that the Report and associated ReportDesigns are successfully run
	 */
	@Test
	public void shouldRunReport() throws Exception {
		ReportDefinition reportDefinition = setupReport();
		ReportData data = Context.getService(ReportDefinitionService.class).evaluate(reportDefinition,
		    getEvaluationContext());
		testResults(data);
	}
	
	/**
	 * Tests that the Report and associated ReportDesigns are successfully deleted
	 */
	@Test
	public void shouldDeleteReport() throws Exception {
		ReportService rs = Context.getService(ReportService.class);
		setupReport();
		
		int startingDesignCount = rs.getAllReportDesigns(true).size();
		
		getSetupReportClass().delete();
		
		List<ReportDefinition> l = getReportDefinitionService().getDefinitions(getReportName(), true);
		Assert.assertEquals(0, l.size());
		
		int endingDesignCount = rs.getAllReportDesigns(true).size();
		Assert.assertEquals(getReportDesignNames().size(), startingDesignCount - endingDesignCount);
	}
	
	//******* HELPER METHODS *****
	
	protected ReportDefinition setupReport() throws Exception {
		getSetupReportClass().setup();
		List<ReportDefinition> l = getReportDefinitionService().getDefinitions(getReportName(), true);
		Assert.assertEquals(1, l.size());
		return l.get(0);
	}
}
