package org.openmrs.module.rwandareports.customcalculator;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.CustomCalculation;
import org.openmrs.module.rowperpatientreports.patientdata.result.AgeResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.DateValueResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.ObservationResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientAttributeResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientPropertyResult;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

public class PDCAlerts implements CustomCalculation {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	public PatientDataResult calculateResult(List<PatientDataResult> results, EvaluationContext context) {
		
		PatientAttributeResult alert = new PatientAttributeResult(null, null);
		
		StringBuffer alerts = new StringBuffer();
		int babyAgeInmonths = 0;
		
		for (PatientDataResult resultGrowth : results)
			for (PatientDataResult resultGrowthCoded : results) {
				if (resultGrowth.getName().equals("intervalgrowth") && resultGrowthCoded.getName().equals("inadequate")) {
					ObservationResult intervGrowth = (ObservationResult) resultGrowth;
					ObservationResult inadequateGrowth = (ObservationResult) resultGrowthCoded;
					try {
						String intervalgrowth = intervGrowth.getValue();
						String inadequate = inadequateGrowth.getValue();
						double lowIntgrowth = Double.parseDouble(intervalgrowth);
						if (intervGrowth.getValue() != null && inadequateGrowth.getValue() != null
						        && inadequate.toString().contains("INADEQUATE")) {
							alerts.append("Inadequate interval growth: " + lowIntgrowth + "\n");
						}
					}
					catch (Exception e) {}
				}
			}
		for (PatientDataResult result : results) {
			
			if (result.getName().equals("wtagezcore")) {
				ObservationResult wtageZcore = (ObservationResult) result;
				if (wtageZcore.getValue() != null && wtageZcore.getObs() != null
				        && wtageZcore.getObs().getValueNumeric() < -3) {
					alerts.append("Very Low Wt/Age Z-score: " + wtageZcore.getObs().getValueNumeric() + " \n");
				}
			}
			if (result.getName().equals("wthtzscore")) {
				ObservationResult wtHeightzcore = (ObservationResult) result;
				
				if (wtHeightzcore.getValue() != null && wtHeightzcore.getObs() != null
				        && wtHeightzcore.getObs().getValueNumeric() < -3) {
					alerts.append("Very Low Wt/Ht Z-score: " + wtHeightzcore.getObs().getValueNumeric() + " \n");
				}
			}
			if (result.getName().equals("age")) {
				AgeResult ageinmonths = (AgeResult) result;
				if (ageinmonths.getValue() != null) {
					babyAgeInmonths = ageinmonths.getValue();
				}
			}
			if (result.getName().equals("temperature")) {
				ObservationResult temperature = (ObservationResult) result;
				if (temperature.getValue() != null) {
					String temperatureValue = temperature.getValue();
					try {
						double tempinNum = Double.parseDouble(temperatureValue);
						if ((babyAgeInmonths < 2 && (tempinNum < 35.5 || tempinNum > 37.5))
						        || ((babyAgeInmonths > 2 && babyAgeInmonths < 59) && tempinNum > 37.5)) {
							alerts.append("last temperature danger sign:" + tempinNum + "\n");
						}
					}
					catch (Exception e) {}
				}
			}
			if (result.getName().equals("respitatorysign")) {
				ObservationResult temperature = (ObservationResult) result;
				if (temperature.getValue() != null) {
					String respRateSign = temperature.getValue();
					try {
						double respNum = Double.parseDouble(respRateSign);
						if ((babyAgeInmonths < 2 && respNum > 60) || (babyAgeInmonths > 2 && respNum > 50)
						        || (babyAgeInmonths < 12 && respNum > 50) || (babyAgeInmonths > 12 && respNum > 40)) {
							alerts.append("last respiratory rate danger sign:" + respNum + "\n");
						}
					}
					catch (Exception e) {}
				}
			}
			if (result.getName().equals("swa") && result.getValue() == null) {
				alerts.append("No SW Assessment \n");
			}
			if (result.getName().equals("ecdeducation") && result.getValue() == null) {
				alerts.append("No ECD Education Session \n");
			}
			if (result.getName().equals("dischargedmet")) {
				ObservationResult condition = (ObservationResult) result;
				if (condition.getValue() != null) {
					String dischargeCond = condition.getValue();
					if (dischargeCond.toString().contains("MORE THAT FIVE YEARS OLD")) {
						alerts.append("No condition specific met. \n");
					}
				} else if (condition.getValue() == null) {
					alerts.append("No condition specific met. \n");
				}
			}
		}
		for (PatientDataResult result : results)
			for (PatientDataResult resultAge : results) {
				if (resultAge.getName().equals("age"))
					if (result.getName().equals("asqscore")) {
						AgeResult ageinmonths = (AgeResult) resultAge;
						ObservationResult asqScore = (ObservationResult) result;
						int age = ageinmonths.getValue();
						if (age > 6 && asqScore.getValue() == null) {
							alerts.append("No ASQ recorded \n");
						}
						if (asqScore.getValue() != null) {
							Date lastAsq = asqScore.getDateOfObservation();
							Date date = Calendar.getInstance().getTime();
							int diff = calculateMonthsDifference(date, lastAsq);
							if (((age > 6 || age < 30) && diff > 8) || (age >= 30 && diff > 14)) {
								alerts.append("Late ASQ (" + diff + "months) \n");
							}
						}
					}
			}
		for (PatientDataResult result : results)
			for (PatientDataResult resultVisit : results) {
				if (resultVisit.getName().equals("nextVisit") && result.getName().equals("lastintake")) {
					ObservationResult nextRDV = (ObservationResult) resultVisit;
					DateValueResult lastVisit = (DateValueResult) result;
					if (nextRDV.getValue() != null) {
						try {
							Date nextVisit = nextRDV.getObs().getValueDatetime();
							Date date = Calendar.getInstance().getTime();
							int diff = calculateDaysDifference(nextVisit, date);
							if (diff > 7 && lastVisit.getValue() == null) {
								alerts.append("Missed intake appointment \n");
							}
						}
						catch (Exception e) {}
					}
				}
			}
		//Weekly Report
		for (PatientDataResult resultAge : results)
			for (PatientDataResult resultEnc : results)
				for (PatientDataResult resultVisit : results) {
					if (resultAge.getName().equals("ageinYrs"))
						if (resultEnc.getName().equals("lastEnc"))
							if (resultVisit.getName().equals("nextVisit")) {
								PatientPropertyResult ageInt = (PatientPropertyResult) resultAge;
								DateValueResult lastVisit = (DateValueResult) resultEnc;
								ObservationResult nextRDV = (ObservationResult) resultVisit;
								
								Integer age = Integer.parseInt(ageInt.getValueAsString());
								Date visit = lastVisit.getDateOfObservation();
								Date date = Calendar.getInstance().getTime();
								int diff = calculateMonthsDifference(date, visit);
								if (age < 1 && diff >= 6) {
									alerts.append("Lost to follow-up " + diff + " (months) \n");
								}
								if ((age >= 1 && age < 2) && diff >= 9) {
									alerts.append("Lost to follow-up (>1 to <2 years old) " + diff + " (months) \n");
								}
								if (age > 2 && diff >= 18) {
									alerts.append("Lost to follow-up (>1 to <2 years old) " + diff + " (months) \n");
								}
								
								if (nextRDV.getValue() != null) {
									Date nextVisit = nextRDV.getObs().getValueDatetime();
									int diffRDV = calculateMonthsDifference(date, nextVisit);
									if (diffRDV > 3) {
										alerts.append("Patient Exit: " + diffRDV + " (surpassed) \n");
									}
								}
							}
				}
		
		alert.setValue(alerts.toString());
		return alert;
	}
	
	private int calculateMonthsDifference(Date observation, Date startingDate) {
		int diff = 0;
		
		Calendar obsDate = Calendar.getInstance();
		obsDate.setTime(observation);
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(startingDate);
		diff = obsDate.get(Calendar.YEAR) - startDate.get(Calendar.YEAR);
		diff = diff * 12;
		int monthDiff = obsDate.get(Calendar.MONTH) - startDate.get(Calendar.MONTH);
		diff = diff + monthDiff;
		
		return diff;
	}
	
	private int calculateDaysDifference(Date observation, Date startingDate) {
		long diff = 0;
		
		Calendar obsDate = Calendar.getInstance();
		obsDate.setTime(observation);
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(startingDate);
		
		diff = obsDate.getTimeInMillis() - startDate.getTimeInMillis();
		diff = diff * 12;
		int monthDiff = obsDate.get(Calendar.DAY_OF_YEAR) - startDate.get(Calendar.DAY_OF_YEAR);
		diff = diff + monthDiff;
		
		diff = startDate.getTimeInMillis() - obsDate.getTimeInMillis();
		diff = diff / (24 * 60 * 60 * 1000);
		int days = (int) diff;
		return days;
	}
	
}
