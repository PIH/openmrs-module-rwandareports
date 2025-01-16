package org.openmrs.module.rwandareports.definition.evaluator;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.evaluator.RowPerPatientDataEvaluator;
import org.openmrs.module.rowperpatientreports.patientdata.result.DateResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rwandareports.definition.DateOfMostRecentEncounterOfTypeInPeriod;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;

@Handler(supports = { DateOfMostRecentEncounterOfTypeInPeriod.class })
public class DateOfMostRecentEncounterOfTypeInPeriodEvaluator implements RowPerPatientDataEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) {
		
		DateResult par = new DateResult(patientData, context);
		DateOfMostRecentEncounterOfTypeInPeriod pd = (DateOfMostRecentEncounterOfTypeInPeriod) patientData;
		par.setFormat(pd.getDateFormat());
		Date startDate = pd.getStartDate();
		Date endDate = pd.getEndDate();
		if (pd.getStartDate() == null && pd.getEndDate() == null) {
			startDate = (Date) context.getParameterValue("startDate");
			endDate = (Date) context.getParameterValue("endDate");
		}
		EncounterSearchCriteriaBuilder builder = new EncounterSearchCriteriaBuilder();
		builder.setPatient(pd.getPatient()).setFromDate(startDate).setToDate(endDate);
		
		List<Encounter> encounters = Context.getEncounterService().getEncounters(builder.createEncounterSearchCriteria());
		
		Encounter enc = null;
		if (encounters != null) {
			//find the most recent value with a considered encounterType.
			for (Encounter e : encounters) {
				boolean add = false;
				for (EncounterType et : pd.getEncounterTypes()) {
					if (e.getEncounterType().getEncounterTypeId() == et.getEncounterTypeId()) {
						add = true;
					}
				}
				
				if (add) {
					if (enc == null || e.getEncounterDatetime().compareTo(enc.getEncounterDatetime()) > 0) {
						enc = e;
					}
				}
			}
		}
		if (enc != null) {
			par.setValue(enc.getEncounterDatetime());
		}
		return par;
	}
}
