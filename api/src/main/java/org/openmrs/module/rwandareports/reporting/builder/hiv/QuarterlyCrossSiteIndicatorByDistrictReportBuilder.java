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
package org.openmrs.module.rwandareports.reporting.builder.hiv;

import java.util.Date;
import java.util.Properties;

import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.rwandareports.dataset.LocationHierachyIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.reporting.Helper;
import org.openmrs.module.rwandareports.reporting.builder.common.ReportBuilder;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;

/**
 *
 */
public class QuarterlyCrossSiteIndicatorByDistrictReportBuilder implements ReportBuilder {
	
	/**
	 * @see org.openmrs.module.rwandareports.reporting.builder.common.ReportBuilder#build()
	 */
	@Override
	public ReportDefinition build() {
		// PIH Quarterly Cross Site Indicator Report
		ReportDefinition rd = new ReportDefinition();
		rd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		rd.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		Properties properties = new Properties();
		properties.setProperty("hierarchyFields", "countyDistrict:District");
		rd.addParameter(new Parameter("location", "Location", AllLocation.class, properties));
		
		rd.setName("PIH-Boston Indicators-Quarterly");
		
		rd.addDataSetDefinition(createDataSet(),
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
		
		Helper.saveReportDefinition(rd);
		return null;
	}
	
	/**
	 * @see org.openmrs.module.rwandareports.reporting.builder.common.ReportBuilder#delete()
	 */
	@Override
	public void delete() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Creates the LocationHierachyIndicatorDataSetDefinition dataset which has a
	 * CohortIndicatorDataSetDefinition on it.
	 * 
	 * @return the dataset
	 */
	private DataSetDefinition createDataSet() {
		
		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(createBaseDataSet());
		ldsd.setName("PIH_Quarterly_Individual_District_Indicator Data Set");
		ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
		
		return ldsd;
	}
	
	/**
	 * Creates a CohortIndicatorDataSetDefinition
	 * 
	 * @return the dataset
	 */
	private CohortIndicatorDataSetDefinition createBaseDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		
		return dsd;
	}
	
}
