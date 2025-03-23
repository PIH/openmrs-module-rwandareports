package org.openmrs.module.rwandareports.filter;

import org.openmrs.PatientState;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;

public class OutcomeStateFilter implements ResultFilter {
	
	public Object filter(Object value) {
		
		PatientState state = (PatientState) value;
		
		if (state != null) {
			String stateName = state.getState().getConcept().getName().getName();
			
			if (stateName != null) {
				if (stateName.equals("ON ANTIRETROVIRALS") || stateName.equals("FOLLOWING")) {
					stateName = "";
				}
				return stateName;
			}
		}
		return "";
	}
	
	public Object filterWhenNull() {
		
		return "";
	}
}
