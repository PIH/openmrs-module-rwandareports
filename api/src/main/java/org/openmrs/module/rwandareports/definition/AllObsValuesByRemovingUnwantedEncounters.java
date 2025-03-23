package org.openmrs.module.rwandareports.definition;

import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.rowperpatientreports.patientdata.definition.BasePatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;

import java.util.List;

/**
 * Created by josua on 10/30/18.
 */
public class AllObsValuesByRemovingUnwantedEncounters extends BasePatientData implements RowPerPatientData {
	
	@ConfigurationProperty(required = false)
	private Concept ConceptOfWantedObs;
	
	@ConfigurationProperty(required = false)
	private Concept ConceptOfUnWantedObs;
	
	private List<Form> forms;
	
	private boolean includeNull = true;
	
	public List<Form> getForms() {
		return forms;
	}
	
	public void setForms(List<Form> forms) {
		this.forms = forms;
	}
	
	public Concept getConceptOfWantedObs() {
		return ConceptOfWantedObs;
	}
	
	public void setConceptOfWantedObs(Concept conceptOfWantedObs) {
		ConceptOfWantedObs = conceptOfWantedObs;
	}
	
	public Concept getConceptOfUnWantedObs() {
		return ConceptOfUnWantedObs;
	}
	
	public void setConceptOfUnWantedObs(Concept conceptOfUnWantedObs) {
		ConceptOfUnWantedObs = conceptOfUnWantedObs;
	}
	
	public boolean isIncludeNull() {
		return includeNull;
	}
	
	public void setIncludeNull(boolean includeNull) {
		this.includeNull = includeNull;
	}
}
