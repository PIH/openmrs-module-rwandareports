package org.openmrs.module.rwandareports.definition;

import org.openmrs.Concept;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.rowperpatientreports.patientdata.definition.BasePatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.DateOfPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;

import java.util.Date;

public class AllTheOrdersetsWithIndicationOfConcept extends BasePatientData implements RowPerPatientData, DateOfPatientData {
	
	@ConfigurationProperty(required = false)
	private Date beforeDate = null;
	
	@ConfigurationProperty(required = false)
	private Date afterDate = null;
	
	@ConfigurationProperty(required = true)
	private Concept indicationConcept;
	
	public Date getBeforeDate() {
		return beforeDate;
	}
	
	public void setBeforeDate(Date beforeDate) {
		this.beforeDate = beforeDate;
	}
	
	public Date getAfterDate() {
		return afterDate;
	}
	
	public void setAfterDate(Date afterDate) {
		this.afterDate = afterDate;
	}
	
	public Concept getIndicationConcept() {
		return indicationConcept;
	}
	
	public void setIndicationConcept(Concept indicationConcept) {
		this.indicationConcept = indicationConcept;
	}
}
