package org.openmrs.module.rwandareports.reporting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

/**
 * Report Setup / teardown interface
 */
public abstract class SingleSetupReport implements SetupReport {
	
	Log log = LogFactory.getLog(getClass());
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	public abstract String getReportName();
	
	public void delete() {
		Helper.purgeReportDefinition(getReportName());
	}
	
	protected ReportService getReportService() {
		return Context.getService(ReportService.class);
	}
	
	protected ReportDefinitionService getReportDefinitionService() {
		return Context.getService(ReportDefinitionService.class);
	}
	
	@Override
	public String toString() {
		return getReportName();
	}
}
