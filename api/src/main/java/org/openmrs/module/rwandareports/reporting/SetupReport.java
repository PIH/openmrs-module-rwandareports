package org.openmrs.module.rwandareports.reporting;

/**
 * Report Setup / teardown interface
 */
public interface SetupReport {
	
	String getReportName();
	
	void setup() throws Exception;
	
	void delete();
}
