package org.openmrs.module.rwandareports.customcalculator;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.CustomCalculation;
import org.openmrs.module.rowperpatientreports.patientdata.result.AllObservationValuesResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.ObservationResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientAttributeResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

public class AsthmaClassificationAlerts implements CustomCalculation {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	public PatientDataResult calculateResult(List<PatientDataResult> results, EvaluationContext context) {
		
		PatientAttributeResult alert = new PatientAttributeResult(null, null);
		
		StringBuffer alerts = new StringBuffer();
		
		for (PatientDataResult result : results) {
			
			if (!patientHasDiabetesDDBForm(result))
				alerts.append("no Enrollment form \n");
			
			if (result.getName().equals("asthmaClassification")) {
				AllObservationValuesResult asthmaClassification = (AllObservationValuesResult) result;
				
				if (asthmaClassification.getValue() != null) {
					String stepDown = checkStepDown(asthmaClassification.getValue());
					alerts.append(stepDown);
					
				}
			}
			
		}
		
		alert.setValue(alerts.toString().trim());
		return alert;
	}
	
	private String checkStepDown(List<Obs> obs) {
		
		Map<String, Integer> classification = new HashMap<String, Integer>();
		
		String[] classOder = Context.getAdministrationService().getGlobalProperty(gp.ASTHMA_CLASSIFICATION_ORDER).split(",");
		
		for (String c : classOder) {
			String[] currentClass = c.split(":");
			classification.put(currentClass[0], Integer.parseInt(currentClass[1]));
		}
		
		Obs lastOb = null;
		Obs beforeLastOb = null;
		
		if (obs.size() > 0) {
			lastOb = obs.get(obs.size() - 1);
		}
		
		if (obs.size() > 1) {
			beforeLastOb = obs.get(obs.size() - 2);
		}
		
		if (lastOb != null && beforeLastOb != null) {
			String firstVal = lastOb.getValueCoded().getName().toString();
			String beforeLastVal = beforeLastOb.getValueCoded().getName().toString();
			
			if (firstVal != null && beforeLastVal != null
			        && classification.get(firstVal) > classification.get(beforeLastVal)) {
				
				/*String stepDown = beforeLastVal +","+ firstVal;*/
				
				return "Worsening classification at last visit";
				
			}
		}
		
		return "";
	}
	
	private boolean patientHasDiabetesDDBForm(PatientDataResult result) {
		try {
			//int formId = gp.getForm(gp.ASTHMA_DDB).getFormId();
			List<Form> forms = gp.getFormList(GlobalPropertiesManagement.ASTHMA_DDBs);
			Patient p = result.getPatientData().getPatient();
			List<Encounter> patientEncounters = Context.getEncounterService().getEncountersByPatient(p);
			
			for (Encounter encounter : patientEncounters) {
				if (encounter != null && encounter.getForm() != null) {
					for (Form f : forms) {
						if (encounter.getForm().getFormId() == f.getFormId()) {
							return true;
						}
					}
				}
			}
		}
		catch (NumberFormatException e) {
			log.error("Could not parse value of " + gp.ASTHMA_DDBs + "to integer");
		}
		
		return false;
	}
	
}
