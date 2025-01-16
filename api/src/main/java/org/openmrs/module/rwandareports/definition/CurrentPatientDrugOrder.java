package org.openmrs.module.rwandareports.definition;

import org.openmrs.Concept;
import org.openmrs.module.rowperpatientreports.patientdata.definition.BasePatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;

public class CurrentPatientDrugOrder extends BasePatientData implements RowPerPatientData {
	
	private String drugName;
	
	private ResultFilter resultFilter;
	
	public String getDrugName() {
		return drugName;
	}
	
	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}
	
	public ResultFilter getResultFilter() {
		return resultFilter;
	}
	
	public void setResultFilter(ResultFilter resultFilter) {
		this.resultFilter = resultFilter;
	}
	
}
