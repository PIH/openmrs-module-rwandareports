package org.openmrs.module.rwandareports.customcalculator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.CustomCalculation;
import org.openmrs.module.rowperpatientreports.patientdata.result.DateResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;

public class ValueDateManipulation implements CustomCalculation {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public ValueDateManipulation(String name, Integer dateChange, Integer dateUnit, String dateFormat) {
		this.name = name;
		this.dateChange = dateChange;
		this.dateUnit = dateUnit;
		this.dateFormat = dateFormat;
	}
	
	private Integer dateChange;
	
	private Integer dateUnit;
	
	private String name;
	
	private String dateFormat;
	
	public PatientDataResult calculateResult(List<PatientDataResult> results, EvaluationContext context) {
		
		DateResult dateResult = new DateResult(null, null);
		
		for (PatientDataResult result : results) {
			if (result.getName().equals(name)) {
				if (result != null && result.getValue() != null && result.getValueAsString() != null) {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
					Date convertedDate;
					try {
						
						convertedDate = simpleDateFormat.parse(result.getValueAsString());
						Calendar adjustedDate = Calendar.getInstance();
						adjustedDate.setTime(convertedDate);
						
						adjustedDate.add(dateUnit, dateChange);
						
						dateResult.setValue(adjustedDate.getTime());
						
						dateResult.setFormat(dateFormat);
					}
					catch (ParseException e) {
						
						log.error("Error generated", e);
					}
				}
			}
		}
		
		return dateResult;
	}
}
