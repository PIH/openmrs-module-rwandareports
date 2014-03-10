package org.openmrs.module.rwandareports.customcalculator;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.CustomCalculation;
import org.openmrs.module.rowperpatientreports.patientdata.result.DateValueResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.ObservationResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientAttributeResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;


public class EpilepsyAlerts implements CustomCalculation {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	public PatientDataResult calculateResult(List<PatientDataResult> results, EvaluationContext context) {
		
		PatientAttributeResult alert = new PatientAttributeResult(null, null);
		
		StringBuffer alerts = new StringBuffer();
		
		for (PatientDataResult result : results) {
			
			if (!patientHasEpilepsyDDBForm(result))
				alerts.append("no DDB \n");
			
			@SuppressWarnings("unused")
			boolean uncontrolledAlert = false;
			if (result.getName().equals("RecentSeizure")) {
				ObservationResult seizure = (ObservationResult)result;
				
				if(seizure.getValue() != null && seizure.getObs() != null && seizure.getObs().getValueNumeric() >= 2)
				{
					if(alerts.length() > 0)
					{
						alerts.append(",");
					}
					alerts.append(">=2 episodes/month at last visit");
				}
				
				uncontrolledAlert = true;
			}
			
			/*if(result.getName().equals("lastEncInMonth"))
			  {
				DateValueResult encinmonths = (DateValueResult)result;
				if(encinmonths.getValue() != null)
				{
				Date dateVl =encinmonths.getDateOfObservation();
				Date date = Calendar.getInstance().getTime();
				int diff = calculateMonthsDifference(date, dateVl);
				if(diff > 12){
				alerts.append("LTFU determine status.\n");
				     }
				  } 	
			  }*/
			
			//TODO: finish off alerts
		}
		
		alert.setValue(alerts.toString().trim());
		return alert;
	}
	
	private boolean patientHasEpilepsyDDBForm(PatientDataResult result){
		try {
			int formId = gp.getForm(GlobalPropertiesManagement.EPILEPSY_DDB).getFormId();
			Patient p = result.getPatientData().getPatient();
			List<Encounter> patientEncounters =Context.getEncounterService().getEncountersByPatient(p);
			
		    for (Encounter encounter : patientEncounters) {
				if (encounter != null && encounter.getForm() !=null) {
					if(encounter.getForm().getFormId() == formId){	
						return true;
					} 				
				}
			}
		} catch (NumberFormatException e) {
			 log.error("Could not parse value of "+ GlobalPropertiesManagement.EPILEPSY_DDB+ "to integer");
		}
			
		return false;
	}
	
	private int calculateMonthsDifference(Date observation, Date startingDate){
		int diff = 0;
	    Calendar obsDate = Calendar.getInstance();	
		obsDate.setTime(observation);
	
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(startingDate);
	
		//find out if there is any difference in years first
		diff = obsDate.get(Calendar.YEAR) - startDate.get(Calendar.YEAR);
		diff = diff * 12;
	
		int monthDiff = obsDate.get(Calendar.MONTH) - startDate.get(Calendar.MONTH);
		diff = diff + monthDiff;
	
		return diff;
	}
	
}
