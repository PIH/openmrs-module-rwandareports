package org.openmrs.module.rwandareports.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.evaluator.RowPerPatientDataEvaluator;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.StringResult;
import org.openmrs.module.rwandareports.definition.PatientDied;

import java.text.SimpleDateFormat;

@Handler(supports = { PatientDied.class })
public class PatientDiedEvaluator implements RowPerPatientDataEvaluator {
	
	protected Log log = LogFactory.getLog(PatientDiedEvaluator.class);
	
	public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) {
		StringResult result = new StringResult(patientData, context);
		PatientDied pd = (PatientDied) patientData;
		
		SimpleDateFormat sdf = null;
		if (pd.getDateFormat() != null) {
			sdf = new SimpleDateFormat(pd.getDateFormat());
		} else {
			sdf = new SimpleDateFormat("dd/MM/yyyy");
		}
		
		Person p = Context.getPersonService().getPerson(pd.getPatientId());
		if (p.getDead()) {
			
			if (pd.getValueType().equalsIgnoreCase("DeathDate")) {
				result.setValue("" + sdf.format(p.getDeathDate()));
			} else if (pd.getValueType().equalsIgnoreCase("CauseOfDeath")) {
				result.setValue("" + p.getCauseOfDeath());
			} else {
				result.setValue(" ");
			}
		}
		if (pd.getFilter() != null) {
			result.setValue((String) pd.getFilter().filter(result.getValue()));
		}
		
		return result;
		
	}
}
