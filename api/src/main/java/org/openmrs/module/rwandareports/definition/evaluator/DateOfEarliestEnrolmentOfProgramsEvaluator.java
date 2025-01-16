package org.openmrs.module.rwandareports.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.evaluator.RowPerPatientDataEvaluator;
import org.openmrs.module.rowperpatientreports.patientdata.result.DateResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rwandareports.definition.DateOfEarliestEnrolmentOfPrograms;

import java.util.ArrayList;
import java.util.List;

@Handler(supports = { DateOfEarliestEnrolmentOfPrograms.class })
public class DateOfEarliestEnrolmentOfProgramsEvaluator implements RowPerPatientDataEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) {
		
		DateResult result = new DateResult(patientData, context);
		DateOfEarliestEnrolmentOfPrograms pd = (DateOfEarliestEnrolmentOfPrograms) patientData;
		
		result.setFormat(pd.getDateFormat());
		List<PatientProgram> pProgram = new ArrayList<PatientProgram>();
		
		PatientProgram earliestEnrollment = null;
		pProgram = Context.getProgramWorkflowService().getPatientPrograms(pd.getPatient(), null, pd.getStartDate(),
		    pd.getEndDate(), null, null, false);
		
		if (pd.getPrograms() != null) {
			for (PatientProgram pProg : pProgram) {
				if (pd.getPrograms().contains(pProg.getProgram())) {
					if (earliestEnrollment == null || pProg.getDateEnrolled().before(earliestEnrollment.getDateEnrolled())) {
						earliestEnrollment = pProg;
					}
				}
			}
		} else {
			for (PatientProgram pProg : pProgram) {
				if (earliestEnrollment == null || pProg.getDateEnrolled().before(earliestEnrollment.getDateEnrolled())) {
					earliestEnrollment = pProg;
				}
			}
		}
		if (earliestEnrollment != null) {
			result.setValue(earliestEnrollment.getDateEnrolled());
		}
		
		return result;
	}
}
