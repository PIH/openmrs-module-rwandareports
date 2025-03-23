package org.openmrs.module.rwandareports.filter;

import java.util.ArrayList;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;

public class OncologyExitReasonsInEncounterFilter implements ResultFilter {
	
	private String obsValueConceptName;
	
	public Object filter(Object value) {
		int encounterId = Integer.parseInt((String) value);
		
		Concept exitReasonReferred = Context.getConceptService().getConceptByUuid(
		    Context.getAdministrationService().getGlobalProperty("reports.exitReasonReferred"));
		
		Concept exitReasonUnExpectedDeath = Context.getConceptService().getConceptByUuid(
		    Context.getAdministrationService().getGlobalProperty("reports.exitReasonUnExpectedDeath"));
		
		Concept exitReasonUnPriorTreatmentDeath = Context.getConceptService().getConceptByUuid(
		    Context.getAdministrationService().getGlobalProperty("reports.exitReasonUnPriorTreatmentDeath"));
		
		Set<Obs> obs = Context.getEncounterService().getEncounter(encounterId).getAllObs();
		
		for (Obs ob : obs) {
			if (ob.getConcept().getId() == exitReasonReferred.getId()) {
				obsValueConceptName = exitReasonReferred.getName().toString();
			}
			if (ob.getConcept().getId() == exitReasonUnExpectedDeath.getId()) {
				obsValueConceptName = exitReasonUnExpectedDeath.getName().toString();
			}
			if (ob.getConcept().getId() == exitReasonUnPriorTreatmentDeath.getId()) {
				obsValueConceptName = exitReasonUnPriorTreatmentDeath.getName().toString();
			}
		}
		
		return obsValueConceptName;
	}
	
	public Object filterWhenNull() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
