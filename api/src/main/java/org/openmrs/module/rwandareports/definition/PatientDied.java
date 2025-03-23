package org.openmrs.module.rwandareports.definition;

import org.openmrs.module.rowperpatientreports.patientdata.definition.BasePatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.DateOfPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;

public class PatientDied extends BasePatientData implements DateOfPatientData {
	
	private ResultFilter filter;
	
	private String valueType;
	
	private String dateFormat;
	
	public ResultFilter getFilter() {
		return filter;
	}
	
	public void setFilter(ResultFilter filter) {
		this.filter = filter;
	}
	
	public String getValueType() {
		return valueType;
	}
	
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
	
	@Override
	public String getDateFormat() {
		return dateFormat;
	}
	
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
}
