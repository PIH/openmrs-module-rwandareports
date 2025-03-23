package org.openmrs.module.rwandareports.filter;

import java.text.SimpleDateFormat;

import org.openmrs.PatientState;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;

public class OutcomeDateStateFilter implements ResultFilter {
	
	public Object filter(Object value) {
		
		PatientState state = (PatientState) value;
		
		if (state != null) {
			String stateName = state.getState().getConcept().getName().getName();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String stateDate = sdf.format(state.getStartDate());
			
			if (stateName != null) {
				if (stateName.equals("ON ANTIRETROVIRALS") || stateName.equals("FOLLOWING")) {
					stateDate = "";
				}
				return stateDate;
			}
		}
		return "";
	}
	
	public Object filterWhenNull() {
		return "";
	}
}
