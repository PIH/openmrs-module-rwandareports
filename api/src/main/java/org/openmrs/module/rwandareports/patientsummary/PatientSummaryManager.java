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

import org.openmrs.Program;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.RenderingMode;

import java.util.List;
import java.util.Map;

/**
 * This interface enables defining a particular PatientSummary
 */
public interface PatientSummaryManager {
	
	/**
	 * @return the uuid of the Report
	 */
	String getUuid();
	
	/**
	 * @return the key of the Report
	 */
	String getKey();
	
	/**
	 * @return the name of the Report
	 */
	String getName();
	
	/**
	 * @return the description of the Report
	 */
	String getDescription();
	
	/**
	 * @return the programs this patient summary is for
	 */
	List<Program> getRequiredPrograms();
	
	/**
	 * @return the parameters of the Report
	 */
	List<Parameter> getParameters();
	
	/**
	 * @return the privilege required to view or evaluate this report
	 */
	String getRequiredPrivilege();
	
	/**
	 * @return a ReportDefinition that may be persisted or run
	 */
	ReportDefinition constructReportDefinition();
}
