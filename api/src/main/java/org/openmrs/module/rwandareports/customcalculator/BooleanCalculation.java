package org.openmrs.module.rwandareports.customcalculator;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.CustomCalculation;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.StringResult;

public class BooleanCalculation implements CustomCalculation {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public PatientDataResult calculateResult(List<PatientDataResult> results, EvaluationContext context) {
		
		StringResult res = new StringResult(null, null);
		
		for (PatientDataResult result : results) {
			
			if (result.getValueAsString() != null && result.getValueAsString().trim().length() > 0) {
				res.setValue("1");
			} else {
				res.setValue("0");
			}
			
		}
		
		return res;
	}
}
