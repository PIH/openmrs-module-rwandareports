package org.openmrs.module.rwandareports.definition;

import org.openmrs.Concept;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.rowperpatientreports.patientdata.definition.BasePatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.DateOfPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;

import java.util.Date;

public class DateOfFirstOrLastDrugOrderRestrictedByConcept extends BasePatientData implements RowPerPatientData, DateOfPatientData {
	
	@ConfigurationProperty(required = true)
	private Concept drugConcept = null;
	
	@ConfigurationProperty
	private Date startDate = null;
	
	@ConfigurationProperty
	private Date endDate = null;
	
	@ConfigurationProperty(required = true)
	private String firsOrLast;
	
	/**
	 * @return the drugConceptSet
	 */
	public Concept getDrugConcept() {
		return drugConcept;
	}
	
	/**
	 * @param drugConcept the drugConceptSet to set
	 */
	public void setDrugConcept(Concept drugConceptSetConcept) {
		this.drugConcept = drugConceptSetConcept;
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
	
	public String getFirsOrLast() {
		return firsOrLast;
	}
	
	public void setFirsOrLast(String firsOrLast) {
		this.firsOrLast = firsOrLast;
	}
}
