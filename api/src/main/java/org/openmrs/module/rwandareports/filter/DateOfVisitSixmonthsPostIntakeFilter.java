package org.openmrs.module.rwandareports.filter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;

public class DateOfVisitSixmonthsPostIntakeFilter implements ResultFilter {
	
	private ArrayList<Form> intakeForms = new ArrayList<Form>();
	
	private ArrayList<Form> visitForms = new ArrayList<Form>();
	
	public DateOfVisitSixmonthsPostIntakeFilter(ArrayList<Form> intakeForms, ArrayList<Form> visitForms) {
		super();
		this.intakeForms = intakeForms;
		this.visitForms = visitForms;
	}
	
	public Object filter(Object value) {
		int encounterId = Integer.parseInt((String) value);
		int patientId = Context.getEncounterService().getEncounter(encounterId).getPatient().getPatientId();
		
		List<Encounter> intakevisits = Context.getEncounterService().getEncounters(
		    Context.getPatientService().getPatient(patientId), null, null, null, intakeForms, null, null, null, null, true);
		
		List<Encounter> notIntakevisits = Context.getEncounterService().getEncounters(
		    Context.getPatientService().getPatient(patientId), null, null, null, visitForms, null, null, null, null, true);
		
		Encounter temp = null;
		
		for (int i = 0; i < intakevisits.size(); i++) {
			for (int j = 0; j < intakevisits.size(); j++) {
				if (j + 1 < intakevisits.size()
				        && intakevisits.get(j).getEncounterDatetime()
				                .compareTo(intakevisits.get(j + 1).getEncounterDatetime()) > 0) {
					temp = intakevisits.get(j + 1);
					intakevisits.set(j + 1, intakevisits.get(j));
					intakevisits.set(j, temp);
				}
			}
		}
		for (int i = 0; i < notIntakevisits.size(); i++) {
			for (int j = 0; j < notIntakevisits.size(); j++) {
				if (j + 1 < notIntakevisits.size()
				        && notIntakevisits.get(j).getEncounterDatetime()
				                .compareTo(notIntakevisits.get(j + 1).getEncounterDatetime()) > 0) {
					temp = notIntakevisits.get(j + 1);
					notIntakevisits.set(j + 1, notIntakevisits.get(j));
					notIntakevisits.set(j, temp);
				}
			}
		}
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		Encounter encNeeded = null;
		
		if (!intakevisits.isEmpty()) {
			cal1.setTime(intakevisits.get(intakevisits.size() - 1).getEncounterDatetime());
		}
		for (Encounter encounter : notIntakevisits) {
			cal2.setTime(encounter.getEncounterDatetime());
			
			if (cal2.getTimeInMillis() - cal1.getTimeInMillis() >= 15552000000L && cal1.getTimeInMillis() != 0) { //15552000000L milliseconds is equivalent to 6 months
				encNeeded = encounter;
				break;
			}
			
		}
		
		if (encNeeded != null) {
			String[] encDates = encNeeded.getEncounterDatetime().toString().split(" ");
			return encDates[0];
		}
		
		return null;
	}
	
	public Object filterWhenNull() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
