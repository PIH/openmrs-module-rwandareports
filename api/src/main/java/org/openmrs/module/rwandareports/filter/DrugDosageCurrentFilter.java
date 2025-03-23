package org.openmrs.module.rwandareports.filter;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.OrderFrequency;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;

public class DrugDosageCurrentFilter implements ResultFilter {
	
	private String finalDateFormat = null;
	
	private List<EncounterType> heartFailureEncounter;
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	public DrugDosageCurrentFilter(List<EncounterType> heartFailureEncounter) {
		heartFailureEncounter.add(gp.getEncounterType(GlobalPropertiesManagement.HEART_FAILURE_ENCOUNTER));
		heartFailureEncounter.add(gp.getEncounterType(GlobalPropertiesManagement.HF_HTN_CKD_ENCOUNTER_TYPE));
		this.heartFailureEncounter = heartFailureEncounter;
		
	}
	
	public Object filter(Object value) {
		DrugOrder drugOrder = (DrugOrder) value;
		
		StringBuilder result = new StringBuilder();
		
		if (drugOrder.getEffectiveStopDate() != null) {
			if (returnVisitDates().compareTo(drugOrder.getEffectiveStopDate()) < 0) {
				if (drugOrder != null && drugOrder.getDrug() != null) {
					result.append(drugOrder.getDrug().getName());
					result.append(" ");
					result.append(drugOrder.getDose());
					result.append(drugOrder.getDoseUnits() == null ? "" : drugOrder.getDoseUnits().getDisplayString());
					result.append(" ");
					OrderFrequency frequency = drugOrder.getFrequency();
					result.append(frequency == null ? "" : frequency.getConcept().getDisplayString());
				}
			} else if (drugOrder.getEffectiveStopDate() == null) {
				if (drugOrder != null && drugOrder.getDrug() != null) {
					result.append(drugOrder.getDrug().getName());
					result.append(" ");
					result.append(drugOrder.getDose());
					result.append(drugOrder.getDoseUnits() == null ? "" : drugOrder.getDoseUnits().getDisplayString());
					result.append(" ");
					OrderFrequency frequency = drugOrder.getFrequency();
					result.append(frequency == null ? "" : frequency.getConcept().getDisplayString());
				}
			}
		}
		
		return result.toString();
	}
	
	public String getFinalDateFormat() {
		return finalDateFormat;
	}
	
	public void setFinalDateFormat(String finalDateFormat) {
		this.finalDateFormat = finalDateFormat;
	}
	
	public Object filterWhenNull() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Date returnVisitDates() {
		DrugOrder drugOrder = new DrugOrder();
		Date retunv = null;
		Patient patient = drugOrder.getPatient();
		EncounterSearchCriteriaBuilder builder = new EncounterSearchCriteriaBuilder();
		builder.setPatient(patient).setEncounterTypes(heartFailureEncounter).setIncludeVoided(false);
		List<Encounter> patientEncounters = Context.getEncounterService().getEncounters(
		    builder.createEncounterSearchCriteria());
		
		if (patientEncounters.size() > 0 && !drugOrder.isDiscontinuedRightNow()) {
			Encounter recentEncounter = patientEncounters.get(patientEncounters.size() - 1); //the last encounter in the List should be the most recent one.
			
			for (Obs obs : recentEncounter.getObs()) {
				if (obs.getConcept().getConceptId() == 5096) {
					retunv = obs.getValueDatetime();
					
				}
			}
		}
		
		return retunv;
		
	}
	
}
