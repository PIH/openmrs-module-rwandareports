package org.openmrs.module.rwandareports.definition;

import java.util.Date;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.rowperpatientreports.patientdata.definition.BasePatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

public class ArtSwitchDate extends BasePatientData implements RowPerPatientData {
	
	@ConfigurationProperty
	private Date endDate = null;
	
	private Concept drugConceptSetConcept = null;
	
	private Mapped<RowPerPatientData> artData;
	
	private GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	public ArtSwitchDate() {
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
	
	public Mapped<RowPerPatientData> getArtData() {
		return artData;
	}
	
	public void setArtData(Mapped<RowPerPatientData> artData) {
		this.artData = artData;
	}
	
	/**
	 * @param dateOfPatientData the dateOfPatientData to set
	 */
	public void setArtData(RowPerPatientData artData, Map<String, Object> mappings) {
		this.artData = new Mapped<RowPerPatientData>(artData, mappings);
	}
	
}
