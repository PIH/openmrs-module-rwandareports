package org.openmrs.module.rwandareports.filter;

import java.util.ArrayList;
import java.util.Set;

import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;

public class ObservationValueCodedInEncounterFilter implements ResultFilter {
	
	private String obsValueCoded;
	
	private int obsId;
	
	public ObservationValueCodedInEncounterFilter(int obsId) {
		super();
		this.obsId = obsId;
	}
	
	public Object filter(Object value) {
		int encounterId = Integer.parseInt((String) value);
		StringBuilder answers = new StringBuilder();
		Set<Obs> obs = Context.getEncounterService().getEncounter(encounterId).getAllObs();
		int i = 0;
		for (Obs ob : obs) {
			if (ob.getConcept().getId() == obsId) {
				if (i != 0) {
					answers.append(", ");
				}
				answers.append(ob.getValueCoded().getName());
				i++;
			}
		}
		obsValueCoded = answers.toString();
		return obsValueCoded;
	}
	
	public Object filterWhenNull() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
