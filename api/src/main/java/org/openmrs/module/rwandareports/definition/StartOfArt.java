package org.openmrs.module.rwandareports.definition;

import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.rowperpatientreports.patientdata.definition.BasePatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

public class StartOfArt extends BasePatientData implements RowPerPatientData {
	
	@ConfigurationProperty
	private Date endDate = null;
	
	private Concept drugConceptSetConcept = null;
	
	private GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	public StartOfArt() {
		drugConceptSetConcept = gp.getConcept(GlobalPropertiesManagement.ART_DRUGS_SET);
	}
	
	/**
	 * @return the drugConceptSet
	 */
	public Concept getDrugConceptSetConcept() {
		return drugConceptSetConcept;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
