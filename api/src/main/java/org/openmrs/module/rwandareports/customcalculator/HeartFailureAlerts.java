package org.openmrs.module.rwandareports.customcalculator;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.CustomCalculation;
import org.openmrs.module.rowperpatientreports.patientdata.result.AllObservationValuesResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.DateValueResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.ObservationResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientAttributeResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

public class HeartFailureAlerts implements CustomCalculation {
	protected Log log = LogFactory.getLog(this.getClass());
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	@SuppressWarnings("unused")
	public PatientDataResult calculateResult(List<PatientDataResult> results,
			EvaluationContext context) {

		PatientAttributeResult alert = new PatientAttributeResult(null, null);

		StringBuffer alerts = new StringBuffer();
		double height = 0;
		
		for (PatientDataResult result : results) {
			boolean uncontrolledAlert = false;
			
			if(!uncontrolledAlert)
			 {
			if (result.getName().equals("systolic")) {
				ObservationResult systolic = (ObservationResult)result;
				
				if(systolic.getValue() != null && systolic.getObs() != null && systolic.getObs().getValueNumeric() > 140)
				uncontrolledAlert = true;
			}
			
			if (result.getName().equals("diastolic")) {
					ObservationResult diastolic = (ObservationResult)result;
					
					if(diastolic.getValue() != null && diastolic.getObs() != null && diastolic.getObs().getValueNumeric() > 90)
					{
						if(alerts.length() > 0)
						{
						//	alerts.append(", ");
						}
						alerts.append("BP is above goal \n");
					}
					
				}
			}
			
			if(!uncontrolledAlert)
			 {
			if (result.getName().equals("systolic")) {
				ObservationResult systolic = (ObservationResult)result;
				
				if(systolic.getValue() != null && systolic.getObs() != null && systolic.getObs().getValueNumeric() < 90)
				uncontrolledAlert = true;
			}
			
			if (result.getName().equals("diastolic")) {
					ObservationResult diastolic = (ObservationResult)result;
					
					if(diastolic.getValue() != null && diastolic.getObs() != null && diastolic.getObs().getValueNumeric() < 60)
					{
						if(alerts.length() > 0)
						{
						//	alerts.append(", ");
						}
						alerts.append("BP is below goal \n");
					}
				}
			}
		
			 if(result.getName().equals("creatinine"))
			{
				ObservationResult creatinine = (ObservationResult)result;
				if(creatinine.getDateOfObservation() != null)
				{
					Date dateCreatinine= creatinine.getDateOfObservation();
					Date date = new Date();
					
					int diff = calculateMonthsDifference(date, dateCreatinine);
					
					if(diff > 6)
					{
						if(alerts.length() > 0)
						{
							//alerts.append(", ");
						}
						alerts.append("No Serum checked in over 6 months\n");
					}
				}
			}
			 
			if (result.getName().equals("lastINRobs")) {
				ObservationResult lastINRobs = (ObservationResult)result;
				
				if(lastINRobs.getValue() != null && lastINRobs.getObs() != null && lastINRobs.getObs().getValueNumeric() > 3)
				{
					if(alerts.length() > 0)
					alerts.append("Last INR > 3\n");
				}
			}
			
			if(result.getName().equals("weightLastTwo"))
			 {
			 	AllObservationValuesResult lastTwoWeight = (AllObservationValuesResult)result;
				
				if(lastTwoWeight.getValue() != null && lastTwoWeight.getValue().size() > 1)
				{
					int increase = increaseBetweenTwoObs(lastTwoWeight.getValue());
					if(increase > 3)
				     {
					 if(alerts.length() > 0)
						alerts.append("Significant change between the last two weight\n");
					}
				}
			}
			
			if(result.getName().equals("RecentHeight"))
			{
				ObservationResult heightOb = (ObservationResult)result;
				
				if(heightOb.getValue() == null || heightOb.getValue().trim().length() == 0)
				{
					alerts.append("No height ever\n");
				}
				else
				{
					height = Double.parseDouble(heightOb.getValue());
				}
			//just put this in here to make sure it is executed only once
			if (patientHasHeartFailureDDBForm(result) == false) {
				alerts.append("no DDB\n");
			}
		  }
			if(result.getName().equals("lastEncInMonth")){
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
			  }
			
		}
		
		alert.setValue(alerts.toString());
		return alert;
	}
	
	private boolean patientHasHeartFailureDDBForm(PatientDataResult result){
		try {
			int formId = gp.getForm(GlobalPropertiesManagement.HEARTFAILURE_DDB).getFormId();
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
			 log.error("Could not parse value of "+ GlobalPropertiesManagement.HEARTFAILURE_DDB+ "to integer");
		}
			
		return false;
	}
	
	private int increaseBetweenTwoObs(List<Obs> obs)
	{
		Obs one = obs.get(0);
		Obs two = obs.get(1);
		
		if(one.getValueNumeric() != null && two.getValueNumeric() != null)
		{
			if(one.getObsDatetime().after(two.getObsDatetime()))
			{
				return one.getValueNumeric().intValue() - two.getValueNumeric().intValue();
			}
			
			return two.getValueNumeric().intValue() - one.getValueNumeric().intValue();
		}
		
		return 0;
	}
	
	private int calculateMonthsDifference(Date observation, Date startingDate)
	{
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
