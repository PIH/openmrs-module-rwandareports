/*
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

package org.openmrs.module.rwandareports.patientsummary;

import org.openmrs.Obs;
import org.openmrs.PersonName;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Base implementation of ReportManager that provides some common method implementations
 */
//@Component
public class DemoPatientSummary extends BasePatientSummaryManager {
	
	@Override
	public String getUuid() {
		return UUID.randomUUID().toString(); // TODO: Make this a static value in a constant
	}
	
	/**
	 * @return the unique key that can be used to reference this patient summary
	 */
	public String getKey() {
		return "demoPatientSummary";
	}
	
	@Override
	public List<Program> getRequiredPrograms() {
		Program hivProgram = Context.getProgramWorkflowService().getProgram(3);
		return Arrays.asList(hivProgram);
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition reportDefinition = new ReportDefinition();
		PatientDataSetDefinition dsd = new PatientDataSetDefinition();
		Map<String, Object> mappings = new HashMap<String, Object>();
		
		reportDefinition.addDataSetDefinition("patient", dsd, mappings);
		
		// TODO: Get all of the below definitions from a library, do not construct on the fly
		
		{
			PatientIdDataDefinition d = new PatientIdDataDefinition();
			dsd.addColumn("patientId", d, mappings);
		}
		{
			PreferredNameDataDefinition d = new PreferredNameDataDefinition();
			dsd.addColumn("givenName", d, mappings, new PropertyConverter(PersonName.class, "givenName"));
			dsd.addColumn("familyName", d, mappings, new PropertyConverter(PersonName.class, "familyName"));
		}
		{
			ObsForPersonDataDefinition d = new ObsForPersonDataDefinition();
			d.setWhich(TimeQualifier.LAST);
			d.setQuestion(Context.getConceptService().getConcept("WEIGHT (KG)"));
			dsd.addColumn("lastWeight", d, mappings, new PropertyConverter(Obs.class, "valueNumeric"));
			dsd.addColumn("lastWeightDate", d, mappings, new PropertyConverter(Obs.class, "obsDatetime"), new DateConverter(
			        "dd/MMM/yyyy"));
		}
		
		return reportDefinition;
	}
}
