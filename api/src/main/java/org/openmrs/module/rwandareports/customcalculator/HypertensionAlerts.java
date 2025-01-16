package org.openmrs.module.rwandareports.customcalculator;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.CustomCalculation;
import org.openmrs.module.rowperpatientreports.patientdata.result.AllObservationValuesResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.ObservationResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientAttributeResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.RwandaReportsUtil;

public class HypertensionAlerts implements CustomCalculation {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	public PatientDataResult calculateResult(List<PatientDataResult> results, EvaluationContext context) {
		
		PatientAttributeResult alert = new PatientAttributeResult(null, null);
		
		StringBuffer alerts = new StringBuffer();
		
		for (PatientDataResult result : results) {
			
			if (!patientHasHypertensionDDBForm(result) && !alerts.toString().contains("no Enrollment form"))
				alerts.append("no Enrollment form");
			
			//boolean uncontrolledAlert = false;
			if (result.getName().equals("systolic") && !alerts.toString().contains("Last BP was uncontrolled")) {
				ObservationResult systolic = (ObservationResult) result;
				
				if (systolic.getValue() != null && systolic.getObs() != null && systolic.getObs().getValueNumeric() > 140) {
					if (alerts.length() > 0) {
						alerts.append(", \n");
					}
					alerts.append("Last BP was uncontrolled");
					//uncontrolledAlert = true;
				}
				
				//uncontrolledAlert = true;
			}
			
			if (!alerts.toString().contains("Last BP was uncontrolled")) {
				if (result.getName().equals("diastolic")) {
					ObservationResult diastolic = (ObservationResult) result;
					
					if (diastolic.getValue() != null && diastolic.getObs() != null
					        && diastolic.getObs().getValueNumeric() > 90) {
						if (alerts.length() > 0) {
							alerts.append(", \n");
						}
						alerts.append("Last BP was uncontrolled");
					}
				}
			}
			
			//			if(result.getName().equals("systolicLastTwo"))
			//			{
			//				AllObservationValuesResult systolicLastTwo = (AllObservationValuesResult)result;
			//				
			//				if(systolicLastTwo.getValue() != null && systolicLastTwo.getValue().size() > 1)
			//				{
			//					int increase = increaseBetweenTwoObs(systolicLastTwo.getValue());
			//					if(increase >= 5)
			//					{
			//						if(alerts.length() > 0)
			//						{
			//							alerts.append(", ");
			//						}
			//						alerts.append("Last last 2 recorded systolic BP showed an increase of " + increase);
			//					}
			//				}
			//			}
			//			
			//			if(result.getName().equals("diastolicLastTwo"))
			//			{
			//				AllObservationValuesResult diastolicLastTwo = (AllObservationValuesResult)result;
			//				
			//				if(diastolicLastTwo.getValue() != null && diastolicLastTwo.getValue().size() > 1)
			//				{
			//					int increase = increaseBetweenTwoObs(diastolicLastTwo.getValue());
			//					if(increase >= 5)
			//					{
			//						if(alerts.length() > 0)
			//						{
			//							alerts.append(", ");
			//						}
			//						alerts.append("Last last 2 recorded diastolic BP showed an increase of " + increase);
			//					}
			//				}
			//			}
		}
		
		alert.setValue(alerts.toString().trim());
		return alert;
	}
	
	private boolean patientHasHypertensionDDBForm(PatientDataResult result) {
		
		List<Form> forms = gp.getFormList(GlobalPropertiesManagement.HYPERTENSION_DDBs);
		Patient p = result.getPatientData().getPatient();
		for (Form form : forms) {
			if (RwandaReportsUtil.patientHasForm(p, form))
				return true;
		}
		return false;
	}
	
	private int increaseBetweenTwoObs(List<Obs> obs) {
		Obs one = obs.get(0);
		Obs two = obs.get(1);
		
		if (one.getValueNumeric() != null && two.getValueNumeric() != null) {
			if (one.getObsDatetime().after(two.getObsDatetime())) {
				return one.getValueNumeric().intValue() - two.getValueNumeric().intValue();
			}
			
			return two.getValueNumeric().intValue() - one.getValueNumeric().intValue();
		}
		
		return 0;
	}
	
}
