package org.openmrs.module.rwandareports.definition;

import org.openmrs.EncounterType;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.rowperpatientreports.patientdata.definition.BasePatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateOfMostRecentEncounterOfTypeInPeriod extends BasePatientData implements RowPerPatientData {
	
	private List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
	
	private String dateFormat = "yyyy-MM-dd";
	
	private Date startDate = null;
	
	private Date endDate = null;
	
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
	
	@Override
	public String getDateFormat() {
		return dateFormat;
	}
	
	@Override
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	public List<EncounterType> getEncounterTypes() {
		return encounterTypes;
	}
	
	public void setEncounterTypes(List<EncounterType> encounterTypes) {
		this.encounterTypes = encounterTypes;
		
	}
	
	public void addEncounterType(EncounterType encounterType) {
		encounterTypes.add(encounterType);
	}
}
