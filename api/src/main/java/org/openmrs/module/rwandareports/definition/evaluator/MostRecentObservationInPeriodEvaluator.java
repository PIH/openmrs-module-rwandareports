package org.openmrs.module.rwandareports.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.evaluator.RowPerPatientDataEvaluator;
import org.openmrs.module.rowperpatientreports.patientdata.result.ObservationResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rwandareports.definition.MostRecentObservationInPeriod;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Handler(supports = { MostRecentObservationInPeriod.class })
public class MostRecentObservationInPeriodEvaluator implements RowPerPatientDataEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) {
		
		ObservationResult par = new ObservationResult(patientData, context);
		MostRecentObservationInPeriod pd = (MostRecentObservationInPeriod) patientData;
		
		Concept c = pd.getConcept();
		Date startDate = pd.getStartDate();
		Date endDate = pd.getEndDate();
		if (pd.getStartDate() == null)
			startDate = (Date) context.getParameterValue("startDate");
		if (pd.getEndDate() == null)
			endDate = (Date) context.getParameterValue("endDate");
		
		List<Person> personList = new ArrayList<Person>();
		personList.add(pd.getPatient());
		List<Concept> conceptsList = new ArrayList<Concept>();
		conceptsList.add(pd.getConcept());
		
		List<Obs> obs = Context.getObsService().getObservations(personList, null, conceptsList, pd.getAnswers(), null, null,
		    null, null, null, startDate, endDate, false);
		
		Obs ob = null;
		if (obs != null) {
			for (Obs o : obs) {
				if (ob == null || o.getObsDatetime().compareTo(ob.getObsDatetime()) > 0) {
					if (pd.isIncludeNull()) {
						ob = o;
					} else {
						String value = o.getValueAsString(Context.getLocale());
						if (value != null && value.trim().length() > 0) {
							ob = o;
						}
					}
				}
			}
		}
		
		if (ob != null) {
			
			if (pd.getFilter() != null) {
				par.setResultFilter(pd.getFilter());
			}
			
			par.setDateOfObservation(ob.getObsDatetime());
			par.setObs(ob);
		}
		return par;
	}
	
}
