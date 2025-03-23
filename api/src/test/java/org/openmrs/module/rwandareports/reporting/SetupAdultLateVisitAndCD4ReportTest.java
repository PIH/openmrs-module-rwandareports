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

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.EncounterType;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.rwandareports.StandaloneContextSensitiveTest;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;
import java.util.List;

/**
 * Tests elements of the SetupAdultLateVisitAndCD4Report class
 */
//@Ignore
public class SetupAdultLateVisitAndCD4ReportTest extends StandaloneContextSensitiveTest {
	
	@Autowired
	@Qualifier(value = "reportingCohortDefinitionService")
	CohortDefinitionService cohortDefinitionService;
	
	@Test
	public void testPatientWithViralLoadAndCD4TestedCohort() throws Exception {
		GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
		
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("onOrAfter", DateUtil.adjustDate(new Date(), -12, DurationUnit.MONTHS));
		
		Integer labEncounterTypeId = gp.getEncounterType(GlobalPropertiesManagement.LAB_ENCOUNTER_TYPE).getEncounterTypeId();
		Integer cd4ConceptId = gp.getConcept(GlobalPropertiesManagement.CD4_TEST).getConceptId();
		Integer viralLoadConceptId = gp.getConcept(GlobalPropertiesManagement.VIRAL_LOAD_TEST).getConceptId();
		List<EncounterType> clinicalEncoutersExcLab = gp
		        .getEncounterTypeList(GlobalPropertiesManagement.CLINICAL_ENCOUNTER_TYPES_EXC_LAB_TEST);
		
		SqlCohortDefinition patientsWithViralLoadAndCD4Tested = new SqlCohortDefinition(
		        "SELECT distinct e.patient_id FROM encounter e , obs o where o.encounter_id=e.encounter_id and e.encounter_type="
		                + labEncounterTypeId
		                + " and o.concept_id in ("
		                + viralLoadConceptId
		                + ","
		                + cd4ConceptId
		                + ") and e.encounter_datetime>= :onOrAfter and e.voided=0 and o.voided=0 and value_numeric is not null;");
		patientsWithViralLoadAndCD4Tested.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		
		Cohort r1 = cohortDefinitionService.evaluate(patientsWithViralLoadAndCD4Tested, context);
		System.out.println("patientsWithViralLoadAndCD4Tested: " + r1.size());
		
		EncounterCohortDefinition patientsWithClinicalEncountersWithoutLabTest = Cohorts.createEncounterParameterizedByDate(
		    "patientsWithClinicalEncounters", "onOrAfter", clinicalEncoutersExcLab);
		Cohort r2 = cohortDefinitionService.evaluate(patientsWithClinicalEncountersWithoutLabTest, context);
		System.out.println("patientsWithClinicalEncountersWithoutLabTest: " + r2.size());
		
		CompositionCohortDefinition patientsWithClinicalEncounters = new CompositionCohortDefinition();
		patientsWithClinicalEncounters.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsWithClinicalEncounters.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsWithViralLoadAndCD4Tested, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter}")));
		patientsWithClinicalEncounters.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientsWithClinicalEncountersWithoutLabTest, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter}")));
		patientsWithClinicalEncounters.setCompositionString("1 OR 2");
		
		Cohort r3 = cohortDefinitionService.evaluate(patientsWithClinicalEncounters, context);
		System.out.println("union of these: " + r3.size());
	}
	
}
