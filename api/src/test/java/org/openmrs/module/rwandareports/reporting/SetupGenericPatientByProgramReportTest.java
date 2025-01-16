/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.rwandareports.reporting;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Program;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationProfiler;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.rwandareports.StandaloneContextSensitiveTest;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Iterator;

/**
 * Tests elements of the SetupAdultLateVisitAndCD4Report class
 */
@Ignore
public class SetupGenericPatientByProgramReportTest extends StandaloneContextSensitiveTest {
	
	@Autowired
	@Qualifier(value = "reportingReportDefinitionService")
	ReportDefinitionService reportDefinitionService;
	
	@Test
	public void test() throws Exception {
		
		//LogManager.getLogger(EvaluationProfiler.class).setLevel(Level.TRACE);
		
		StopWatch sw = new StopWatch();
		sw.start();
		GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
		
		Program hivProgram = gp.getProgram(GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
		
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("startDate", DateUtil.getDateTime(2016, 1, 1));
		context.addParameterValue("endDate", DateUtil.getDateTime(2016, 1, 7));
		context.addParameterValue("programs", hivProgram);
		
		System.out.println("Setting up report");
		SetupGenericPatientByProgramReport report = new SetupGenericPatientByProgramReport();
		report.setup();
		
		System.out.println("Running report");
		ReportDefinition rd = reportDefinitionService.getDefinitions("Generic Patient Report", true).get(0);
		ReportData data = reportDefinitionService.evaluate(rd, context);
		
		for (String dsName : data.getDataSets().keySet()) {
			System.out.println("Got Data Set: " + dsName);
			DataSet ds = data.getDataSets().get(dsName);
			int numRows = 0;
			for (Iterator<DataSetRow> i = ds.iterator(); i.hasNext();) {
				DataSetRow row = i.next();
				numRows++;
			}
			System.out.println("Iterated over " + numRows + " rows");
		}
		
		sw.stop();
		System.out.println("Lasted: " + sw.toString());
	}
	
}
