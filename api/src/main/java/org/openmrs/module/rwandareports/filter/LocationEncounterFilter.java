package org.openmrs.module.rwandareports.filter;

import org.openmrs.api.context.Context;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;

public class LocationEncounterFilter implements ResultFilter {
	
	private String encounterLocation;
	
	public Object filter(Object value) {
		int encounterId = Integer.parseInt((String) value);
		encounterLocation = Context.getEncounterService().getEncounter(encounterId).getLocation().getName();
		return encounterLocation;
	}
	
	public Object filterWhenNull() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
