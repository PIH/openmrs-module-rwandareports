package org.openmrs.module.rwandareports.customcalculator;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.CustomCalculation;
import org.openmrs.module.rowperpatientreports.patientdata.result.DateResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.StringResult;

public class DateDiffBetweenTwoDateResults implements CustomCalculation {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	protected String name1;
	
	protected String name2;
	
	public DateDiffBetweenTwoDateResults(String name1, String name2) {
		super();
		this.name1 = name1;
		this.name2 = name2;
	}
	
	public PatientDataResult calculateResult(List<PatientDataResult> results, EvaluationContext context) {
		
		StringResult result = new StringResult(null, null);
		
		Date date1 = null;
		Date date2 = null;
		
		for (PatientDataResult res : results) {
			if (res.getName().equals(name1)) {
				DateResult dateResult = (DateResult) res;
				date1 = dateResult.getValue();
			}
			
			if (res.getName().equals(name2)) {
				DateResult dateResult = (DateResult) res;
				date2 = dateResult.getValue();
			}
		}
		
		if (date1 != null && date2 != null) {
			long diff = date2.getTime() - date1.getTime();
			diff = diff / (24 * 60 * 60 * 1000);
			result.setValue(String.valueOf(diff));
		}
		
		return result;
	}
}
