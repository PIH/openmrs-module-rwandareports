package org.openmrs.module.rwandareports.customcalculator;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.util.OrderEntryUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.CustomCalculation;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.StringResult;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;

public class OnWarfarin implements CustomCalculation {
	
	protected Log log = LogFactory.getLog(OnWarfarin.class);
	
	public OnWarfarin() {
		
	}
	
	public PatientDataResult calculateResult(List<PatientDataResult> results, EvaluationContext context) {
		
		StringResult sr = new StringResult(null, null);
		
		for (PatientDataResult result : results) {
			if (result.getName().equals("age")) {
				
				Patient p = result.getPatientData().getPatient();
				EncounterSearchCriteriaBuilder builder = new EncounterSearchCriteriaBuilder();
				builder.setPatient(p).setIncludeVoided(false);
				List<Encounter> patientEncounters = Context.getEncounterService().getEncounters(
				    builder.createEncounterSearchCriteria());
				List<DrugOrder> patientDrugOrders = OrderEntryUtil.getDrugOrdersByPatient(p);
				for (Iterator<DrugOrder> i = patientDrugOrders.iterator(); i.hasNext();) {
					DrugOrder drugOrder = i.next();
					if (!OrderEntryUtil.isCurrent(drugOrder)) {
						i.remove();
					}
				}
				
				if (patientEncounters.size() > 0 && patientDrugOrders.size() > 0) {
					//Encounter recentEncounter = patientEncounters.get(patientEncounters.size() - 1);  //the last encounter in the List should be the most recent one.
					for (DrugOrder drugOrder : patientDrugOrders) {
						if (isWarfarinOrder(drugOrder) && !drugOrder.isDiscontinuedRightNow()) {
							
							sr.setValue("Y");
							return sr;
						}
					}
					sr.setValue("N");
					return sr;
					
				} else {
					sr.setValue("N");
				}
				
			}
		}
		
		return sr;
	}
	
	//should return true if the order-concept's conceptId is equal to one of the warfarin-concept conceptId
	public boolean isWarfarinOrder(DrugOrder order) {
		String insulineConcepts = Context.getAdministrationService().getGlobalProperty(GlobalPropertiesManagement.WARFARIN);
		
		try {
			int i = Integer.parseInt(insulineConcepts);
			if (i == order.getDrug().getConcept().getConceptId())
				return true;
		}
		catch (NumberFormatException e) {
			log.error("Invalid Global property: " + GlobalPropertiesManagement.WARFARIN
			        + ". Value mapped here should be an Integer");
		}
		
		return false;
		
	}
}
