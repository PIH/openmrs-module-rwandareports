package org.openmrs.module.rwandareports.definition;

import org.openmrs.Program;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.rowperpatientreports.patientdata.definition.BasePatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.DateOfPatientData;

import java.util.Date;
import java.util.List;

public class DateOfEarliestEnrolmentOfPrograms extends BasePatientData implements DateOfPatientData {
	
	private List<Program> Programs = null;
	
	private String dateFormat = "yyyy-MM-dd";
	
	@ConfigurationProperty
	private Date startDate = null;
	
	@ConfigurationProperty
	private Date endDate = null;
	
	public List<Program> getPrograms() {
		return Programs;
	}
	
	public void setPrograms(List<Program> programs) {
		Programs = programs;
	}
	
	@Override
	public String getDateFormat() {
		return dateFormat;
	}
	
	@Override
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
