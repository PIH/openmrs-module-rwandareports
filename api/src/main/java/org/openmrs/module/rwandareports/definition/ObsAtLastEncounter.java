package org.openmrs.module.rwandareports.definition;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.rowperpatientreports.patientdata.definition.BasePatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;

/**
 * Created by josua on 10/16/18.
 */
public class ObsAtLastEncounter extends BasePatientData implements RowPerPatientData {
	
	@ConfigurationProperty(required = false)
	private Concept concept;
	
	@ConfigurationProperty(required = false)
	private EncounterType encounterType;
	
	//    private ResultFilter filter = null;
	
	private boolean includeNull = true;
	
	/**
	 * @return the concept
	 */
	public Concept getConcept() {
		return concept;
	}
	
	public void setConcept(Concept concept) {
		this.concept = concept;
	}
	
	public boolean isIncludeNull() {
		return includeNull;
	}
	
	public void setIncludeNull(boolean includeNull) {
		this.includeNull = includeNull;
	}
	
	//    public ResultFilter getFilter() {
	//        return filter;
	//    }
	//
	//    public void setFilter(ResultFilter filter) {
	//        this.filter = filter;
	//    }
	
	public EncounterType getEncounterType() {
		return encounterType;
	}
	
	public void setEncounterType(EncounterType encounterType) {
		this.encounterType = encounterType;
	}
}
