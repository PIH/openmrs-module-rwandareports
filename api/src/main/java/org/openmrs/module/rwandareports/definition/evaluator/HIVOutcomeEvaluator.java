package org.openmrs.module.rwandareports.definition.evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.evaluator.RowPerPatientDataEvaluator;
import org.openmrs.module.rowperpatientreports.patientdata.result.DateValueResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rwandareports.definition.HIVOutcome;

@Handler(supports = { HIVOutcome.class })
public class HIVOutcomeEvaluator implements RowPerPatientDataEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) {
		
		DateValueResult par = new DateValueResult(patientData, context);
		HIVOutcome pd = (HIVOutcome) patientData;
		
		par.setDateFormat(pd.getDateFormat());
		
		if (pd.getPatient().isDead()) {
			par.setValue("PATIENT DIED");
			par.setDateOfObservation(pd.getPatient().getDeathDate());
		}
		
		//Some patients don't have death dates, so want to pick up values from exit from care reason
		if (pd.getPatient().getDeathDate() == null) {
			List<Obs> exitFromCare = Context.getObsService().getObservationsByPersonAndConcept(pd.getPatient(),
			    pd.getExitFromCare());
			if (exitFromCare != null && exitFromCare.size() > 0) {
				par.setValue(exitFromCare.get(exitFromCare.size() - 1).getValueAsString(Context.getLocale()));
				par.setDateOfObservation(exitFromCare.get(exitFromCare.size() - 1).getObsDatetime());
			} else {
				List<PatientProgram> allPrograms = Context.getProgramWorkflowService().getPatientPrograms(pd.getPatient(),
				    null, null, pd.getEndDate(), null, null, false);
				
				if (allPrograms != null) {
					PatientProgram last = null;
					for (PatientProgram p : allPrograms) {
						if (p.getDateEnrolled() != null && pd.getAllHivPrograms().contains(p.getProgram())) {
							if (last == null || p.getDateEnrolled().after(last.getDateEnrolled())) {
								last = p;
							}
						}
					}
					
					if (last != null) {
						if (!((pd.getEndDate() != null && last.getActive(pd.getEndDate())) || (pd.getEndDate() == null && last
						        .getActive()))) {
							Set<PatientState> states = last.getCurrentStates();
							for (PatientState state : states) {
								String outcome = state.getState().getConcept().getName().getName();
								
								if (!outcome.contains("GROUP") && !outcome.equals("ON ANTIRETROVIRALS")
								        && !outcome.contains("FOLLOWING")) {
									par.setValue(outcome);
									par.setDateOfObservation(last.getDateCompleted());
									break;
								}
							}
							
							if (par.getValue() == null) {
								par.setValue("NO LONGER ENROLLED IN HIV PROGRAM");
								par.setDateOfObservation(last.getDateCompleted());
							}
						}
					}
				}
			}
		}
		
		return par;
	}
}
