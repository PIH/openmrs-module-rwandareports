package org.openmrs.module.rwandareports.customcalculator;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.CustomCalculation;
import org.openmrs.module.rowperpatientreports.patientdata.result.DateResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.DrugOrdersResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.StringResult;

public class DiffBetweenStatusAndRegimenManipulation implements CustomCalculation {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public PatientDataResult calculateResult(List<PatientDataResult> results, EvaluationContext context) {
		
		StringResult result = new StringResult(null, null);
		
		Date artStart = null;
		Date regimenStart = null;
		
		for (PatientDataResult res : results) {
			if (res.getName().equals("artWorkflowStart")) {
				DateResult stateStart = (DateResult) res;
				artStart = stateStart.getValue();
			}
			
			if (res.getName().equals("StartART")) {
				DrugOrdersResult regimen = (DrugOrdersResult) res;
				if (regimen.getValue() != null) {
					regimenStart = regimen.getValue().getEffectiveStartDate();
				}
			}
		}
		
		if (artStart != null && regimenStart != null) {
			long diff = regimenStart.getTime() - artStart.getTime();
			diff = diff / (24 * 60 * 60 * 1000);
			result.setValue(String.valueOf(diff));
		}
		
		return result;
	}
}
