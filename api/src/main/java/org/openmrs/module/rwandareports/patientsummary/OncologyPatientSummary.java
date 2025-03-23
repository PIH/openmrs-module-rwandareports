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

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.PersonName;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ScriptingLanguage;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ScriptedCompositionPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Base implementation of ReportManager that provides some common method implementations
 */
@Component
public class OncologyPatientSummary extends BasePatientSummaryManager {
	
	@Override
	public String getUuid() {
		return UUID.randomUUID().toString(); // TODO: Make this a static value in a constant
	}
	
	/**
	 * @return the unique key that can be used to reference this patient summary
	 */
	public String getKey() {
		return "oncologyPatientSummary";
	}
	
	@Override
	public List<Program> getRequiredPrograms() {
		Program oncologyProgram = Context.getProgramWorkflowService().getProgram(18);
		return Arrays.asList(oncologyProgram);
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
		
		{
			ObsForPersonDataDefinition d = new ObsForPersonDataDefinition();
			d.setWhich(TimeQualifier.LAST);
			d.setQuestion(Context.getConceptService().getConcept("HEIGHT (CM)"));
			dsd.addColumn("lastHeight", d, mappings, new PropertyConverter(Obs.class, "valueNumeric"));
			dsd.addColumn("lastHeightDate", d, mappings, new PropertyConverter(Obs.class, "obsDatetime"), new DateConverter(
			        "dd/MMM/yyyy"));
		}
		
		{
			PatientIdentifierDataDefinition d = new PatientIdentifierDataDefinition();
			d.addType(Context.getPatientService().getPatientIdentifierTypeByName("IMB ID"));
			d.addType(Context.getPatientService().getPatientIdentifierTypeByName("IMB Primary Care Registration ID"));
			dsd.addColumn("imbId", d, mappings);
		}
		{
			EncountersForPatientDataDefinition d = new EncountersForPatientDataDefinition();
			d.setWhich(TimeQualifier.LAST);
			dsd.addColumn("lastEncounterDate", d, mappings, new PropertyConverter(Encounter.class, "encounterDatetime"),
			    new DateConverter("dd/MMM/yyyy"));
			dsd.addColumn("lastEncounterType", d, mappings, new PropertyConverter(Encounter.class, "encounterType.name"));
		}
		
		{
			
			List<Program> programs = Context.getProgramWorkflowService().getAllPrograms();
			ScriptedCompositionPatientDataDefinition activeprograms = new ScriptedCompositionPatientDataDefinition();
			activeprograms.setScriptType(new ScriptingLanguage("groovy"));
			for (Program program : programs) {
				
				ProgramEnrollmentsForPatientDataDefinition d = new ProgramEnrollmentsForPatientDataDefinition();
				d.setProgram(program);
				d.addParameter(new Parameter("activeOnDate", "activeOnDate", Date.class));
				activeprograms.addContainedDataDefinition("pp" + program.getProgramId(), d,
				    ParameterizableUtil.createParameterMappings("activeOnDate=${now}"));
				
			}
			StringBuilder script = new StringBuilder();
			int i = 0;
			script.append("prognames=\"\";");
			for (Program program : programs) {
				if (i == 0) {
					script.append("if(pp" + program.getProgramId() + "!=null)");
					script.append("prognames=");
					script.append("pp" + program.getProgramId());
					script.append(".program.name; ");
					i++;
				} else {
					
					script.append(" if(pp" + program.getProgramId() + "!=null)");
					script.append("prognames=prognames");
					script.append("+\" \"+");
					script.append("pp" + program.getProgramId());
					script.append(".program.name; ");
					i++;
				}
			}
			script.append("return prognames;");
			activeprograms.setScriptCode(script.toString());
			dsd.addColumn("activePrograms", activeprograms, mappings);
		}
		
		return reportDefinition;
	}
}
