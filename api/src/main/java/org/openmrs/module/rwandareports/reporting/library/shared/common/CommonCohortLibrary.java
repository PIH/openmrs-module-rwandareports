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
package org.openmrs.module.rwandareports.reporting.library.shared.common;

import java.util.Date;
import java.util.List;

import org.openmrs.Program;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

/**
 * Library of common cohort definitions
 */
@Component
public class CommonCohortLibrary {
	
	/**
	 * Patients who are female
	 * 
	 * @return the cohort definition
	 */
	public CohortDefinition females() {
		GenderCohortDefinition cd = new GenderCohortDefinition();
		cd.setName("females");
		cd.setFemaleIncluded(true);
		return cd;
	}
	
	/**
	 * Patients who are male
	 * 
	 * @return the cohort definition
	 */
	public CohortDefinition males() {
		GenderCohortDefinition cd = new GenderCohortDefinition();
		cd.setName("males");
		cd.setMaleIncluded(true);
		return cd;
	}
	
	/**
	 * Patients who at most maxAge years old on ${effectiveDate}
	 * 
	 * @return the cohort definition
	 */
	public CohortDefinition agedAtMost(int maxAge) {
		AgeCohortDefinition cd = new AgeCohortDefinition();
		cd.setName("aged at most");
		cd.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		cd.setMaxAge(maxAge);
		return cd;
	}
	
	/**
	 * Patients who are at least minAge years old on ${effectiveDate}
	 * 
	 * @return the cohort definition
	 */
	public CohortDefinition agedAtLeast(int minAge) {
		AgeCohortDefinition cd = new AgeCohortDefinition();
		cd.setName("aged at least");
		cd.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		cd.setMinAge(minAge);
		return cd;
	}
	
	public InProgramCohortDefinition inProgram(String name, List<Program> programs) {
		InProgramCohortDefinition inProgram = new InProgramCohortDefinition();
		inProgram.setName(name);
		
		inProgram.setPrograms(programs);
		
		return inProgram;
	}
	
	public InProgramCohortDefinition inProgramParameterizableByDate(String name, List<Program> programs, String parameterName) {
		InProgramCohortDefinition inProgram = inProgram(name, programs);
		inProgram.addParameter(new Parameter(parameterName, parameterName, Date.class));
		return inProgram;
	}
}
