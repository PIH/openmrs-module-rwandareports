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
package org.openmrs.module.rwandareports.reporting.library.shared.hiv;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Program;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.rwandareports.metadata.HivMetadata;
import org.openmrs.module.rwandareports.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.rwandareports.util.MetadataLookup;
import org.openmrs.module.rwandareports.util.ReportUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Library of HIV related cohort definitions
 */
@Component
public class HivCohortLibrary {
	
	@Autowired
	private CommonCohortLibrary commonCohorts;
	
	/**
	 * Auto generated method comment
	 * 
	 * @return
	 */
	public CohortDefinition inHIVOver15() {
		
		CohortDefinition over15Cohort = commonCohorts.agedAtLeast(15);
		
		List<Program> hivPrograms = new ArrayList<Program>();
		
		hivPrograms.add(MetadataLookup.getProgram(HivMetadata._Program.ADULT_HIV_PROGRAM));
		hivPrograms.add(MetadataLookup.getProgram(HivMetadata._Program.PEDI_HIV_PROGRAM));
		hivPrograms.add(MetadataLookup.getProgram(HivMetadata._Program.PMTCT_PREGNANCY_PROGRAM));
		hivPrograms.add(MetadataLookup.getProgram(HivMetadata._Program.PMTCT_COMBINED_MOTHER_PROGRAM));
		
		InProgramCohortDefinition inAnyHIVProgram = commonCohorts.inProgramParameterizableByDate(
		    "hivQD: In All HIV Programs", hivPrograms, "onOrBefore");
		
		CompositionCohortDefinition inHIVOver15 = new CompositionCohortDefinition();
		inHIVOver15.setName("hivQD: In all programs over 15");
		inHIVOver15.addParameter(new Parameter("endDate", "endDate", Date.class));
		inHIVOver15.addSearch("1", ReportUtils.map(over15Cohort, "effectiveDate=${endDate}"));
		inHIVOver15.addSearch("2", ReportUtils.map(inAnyHIVProgram, "onOrBefore=${endDate}"));
		inHIVOver15.setCompositionString("1 AND 2");
		
		return inHIVOver15;
	}
}
