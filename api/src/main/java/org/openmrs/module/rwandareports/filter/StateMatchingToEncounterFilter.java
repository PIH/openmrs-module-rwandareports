package org.openmrs.module.rwandareports.filter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.api.context.Context;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;

public class StateMatchingToEncounterFilter implements ResultFilter {
	
	private ArrayList<Form> intakeForms = new ArrayList<Form>();
	
	private ArrayList<Form> visitForms = new ArrayList<Form>();
	
	public StateMatchingToEncounterFilter(ArrayList<Form> intakeForms, ArrayList<Form> visitForms) {
		super();
		this.intakeForms = intakeForms;
		this.visitForms = visitForms;
	}
	
	public Object filter(Object value) {
		int encounterId = Integer.parseInt((String) value);
		int patientId = Context.getEncounterService().getEncounter(encounterId).getPatient().getPatientId();
		
		List<Encounter> intakevisits = Context.getEncounterService().getEncounters(
		    Context.getPatientService().getPatient(patientId), null, null, null, intakeForms, null, null, null, null, true);
		
		List<PatientProgram> patProg = Context.getProgramWorkflowService().getPatientPrograms(
		    Context.getPatientService().getPatient(patientId), Context.getProgramWorkflowService().getProgram(2), null,
		    null, null, null, false);
		
		for (PatientProgram patientProgram : patProg) {
			Set<PatientState> states = patientProgram.getStates();
			for (PatientState patientState : states) {
				for (Encounter encounter : intakevisits) {
					if (patientState.getStartDate().compareTo(encounter.getEncounterDatetime()) == 0) {
						
					}
				}
			}
		}
		
		return null;
	}
	
	public Object filterWhenNull() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
