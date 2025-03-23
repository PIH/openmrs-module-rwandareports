package org.openmrs.module.rwandareports.customcalculator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.CustomCalculation;
import org.openmrs.module.rowperpatientreports.patientdata.result.*;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CancerScreenSMSAlert implements CustomCalculation {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	String hivStatus = "";
	
	ObservationResult hpvObsResultTest = null;
	
	ObservationResult cervicalReferredToObsResult = null;
	
	ObservationResult breastReferredToObsResult = null;
	
	ObservationResult cervicalReferredToInPeriodObsResult = null;
	
	ObservationResult breastReferredToInPeriodObsResult = null;
	
	ObservationResult cervicalNextScheduledDate = null;
	
	ObservationResult breastNextScheduledDate = null;
	
	ObservationResult cervicalNextScheduledDateInPeriod = null;
	
	ObservationResult breastNextScheduledDateInPeriod = null;
	
	EncounterResult mostRecentCervicalEncounterOfTypeResult = null;
	
	EncounterResult mostRecentBreastEncounterOfTypeResult = null;
	
	ObservationResult mostRecentTreatmentPerformedResult = null;
	
	ObservationResult mostRecentVIAResults = null;
	
	public PatientDataResult calculateResult(List<PatientDataResult> results, EvaluationContext context) {
		
		PatientAttributeResult alert = new PatientAttributeResult(null, null);
		
		Date startDate = (Date) context.getParameterValue("startDate");
		Date endDate = (Date) context.getParameterValue("endDate");
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		StringBuffer alerts = new StringBuffer();
		
		for (PatientDataResult result : results) {
			if (result.getName().equals("hivResultTest")) {
				ObservationResult hivResultTest = (ObservationResult) result;
				if (hivResultTest != null) {
					hivStatus = hivResultTest.getValue();
				}
			}
			if (result.getName().equals("hpvResultTest")) {
				ObservationResult hpvResultTestResult = (ObservationResult) result;
				if (hpvResultTestResult != null) {
					hpvObsResultTest = hpvResultTestResult;
				}
			}
			
			if (result.getName().equals("cervicalReferredTo")) {
				ObservationResult obs = (ObservationResult) result;
				if (obs != null) {
					cervicalReferredToObsResult = obs;
				}
			}
			if (result.getName().equals("breastReferredTo")) {
				ObservationResult obs = (ObservationResult) result;
				if (obs != null) {
					breastReferredToObsResult = obs;
				}
			}
			if (result.getName().equals("cervicalReferredToInPeriod")) {
				ObservationResult obs = (ObservationResult) result;
				if (obs != null) {
					cervicalReferredToInPeriodObsResult = obs;
				}
			}
			if (result.getName().equals("breastReferredToInPeriod")) {
				ObservationResult obs = (ObservationResult) result;
				if (obs != null) {
					breastReferredToInPeriodObsResult = obs;
				}
			}
			if (result.getName().equals("cervicalNextScheduledDateInPeriod")) {
				ObservationResult obs = (ObservationResult) result;
				if (obs != null) {
					cervicalNextScheduledDateInPeriod = obs;
				}
			}
			if (result.getName().equals("cervicalNextScheduledDate")) {
				ObservationResult obs = (ObservationResult) result;
				if (obs != null) {
					cervicalNextScheduledDate = obs;
				}
			}
			if (result.getName().equals("breastNextScheduledDateInPeriod")) {
				ObservationResult obs = (ObservationResult) result;
				if (obs != null) {
					breastNextScheduledDateInPeriod = obs;
				}
			}
			if (result.getName().equals("breastNextScheduledDate")) {
				ObservationResult obs = (ObservationResult) result;
				if (obs != null) {
					breastNextScheduledDate = obs;
				}
			}
			if (result.getName().equals("mostRecentTreatmentPerformed")) {
				ObservationResult obs = (ObservationResult) result;
				if (obs != null) {
					mostRecentTreatmentPerformedResult = obs;
				}
			}
			if (result.getName().equals("mostRecentVIAResults")) {
				ObservationResult obs = (ObservationResult) result;
				if (obs != null) {
					mostRecentVIAResults = obs;
				}
			}
			
			/*if(result.getName().equals("mostRecentCervicalEncounterOfType"))
			{
				EncounterResult encounter = (EncounterResult) result;
				if(encounter != null) {
					mostRecentCervicalEncounterOfTypeResult =encounter;
				}
			}*/
			/*if(result.getName().equals("mostRecentBreastEncounterOfType"))
			{
				EncounterResult encounter = (EncounterResult) result;
				if(encounter != null) {
					mostRecentBreastEncounterOfTypeResult =encounter;
				}
			}*/
		}
		
		for (PatientDataResult result : results) {
			String patientFullName = result.getPatientData().getPatient().getFamilyName() + " "
			        + result.getPatientData().getPatient().getGivenName();
			PersonAttribute healthFacilityAttribute = result.getPatientData().getPatient().getAttribute("Health Facility");
			String healthFacility = "your health facility/hospital";
			if (healthFacilityAttribute != null) {
				healthFacility = Context.getLocationService()
				        .getLocation(Integer.parseInt(healthFacilityAttribute.getValue())).getName();
			}
			if (result.getName().equals("hpvResultTest")) {
				ObservationResult hpvResultTest = (ObservationResult) result;
				
				if (hpvResultTest != null && hpvResultTest.getValue() != null) {
					Date obsDate = hpvResultTest.getObs().getObsDatetime();
					
					if (hpvResultTest.getValue().equals("HPV negative") && hivStatus != null
					        && !hivStatus.equals("POSITIVE") && obsDate.after(startDate) && obsDate.before(endDate))
						alerts.append("Message from:"
						        + healthFacility
						        + ". "
						        + patientFullName
						        + ", would like to inform  you have been tested negative for HPV, you are requested to come back for retesting after 5 years."
						        + "\n");
					
					if (hpvResultTest.getValue().equals("HPV negative") && hivStatus != null && hivStatus.equals("POSITIVE")
					        && obsDate.after(startDate) && obsDate.before(endDate))
						alerts.append("Message from:"
						        + healthFacility
						        + ". "
						        + patientFullName
						        + ", You have been tested negative for HPV, you are requested to come back for retesting after 3 years."
						        + "\n");
					
					if (hpvResultTest.getValue().equals("HPV positive Type") && obsDate.after(startDate)
					        && obsDate.before(endDate))
						alerts.append("" + patientFullName
						        + ", Your  HPV test result is available, you are requested to come  to " + healthFacility
						        + " for medical follow up." + "\n");
					
				}
				
			}
			if (result.getName().equals("cervicalNextScheduledDate")) {
				ObservationResult cervicalNextScheduledDateObs = (ObservationResult) result;
				if (cervicalNextScheduledDateObs != null && cervicalReferredToObsResult != null) {
					Date valueDate = null;
					if (cervicalNextScheduledDateObs.getObs() != null)
						valueDate = cervicalNextScheduledDateObs.getObs().getValueDate();
					if (valueDate != null
					        && valueDate.after(endDate)
					        && cervicalReferredToObsResult.getValue() != null
					        && !cervicalReferredToObsResult.getValue().equals("")
					        && valueDate.after(endDate)
					        && cervicalNextScheduledDateObs.getObs().getEncounter().getEncounterId() == cervicalReferredToObsResult
					                .getObs().getEncounter().getEncounterId())
						alerts.append("" + patientFullName
						        + ", you have an appointment for cervical cancer screening follow up on "
						        + cervicalNextScheduledDateObs.getValue() + " . Please go to "
						        + cervicalReferredToObsResult.getValue() + " to continue this care." + "\n");
				}
				if (cervicalNextScheduledDateObs != null) {
					Date valueDate = null;
					if (cervicalNextScheduledDateObs.getObs() != null)
						valueDate = cervicalNextScheduledDateObs.getObs().getValueDate();
					if (valueDate != null && valueDate.after(endDate) && cervicalReferredToObsResult.getValue() == null)
						alerts.append("" + patientFullName
						        + ", you have an appointment for cervical cancer screening follow up on "
						        + cervicalNextScheduledDateObs.getValue() + " . Please go to "
						        + cervicalNextScheduledDateObs.getObs().getLocation().getName() + " to continue this care."
						        + "\n");
				}
			}
			
			if (result.getName().equals("breastNextScheduledDate")) {
				ObservationResult breastlNextScheduledDateObs = (ObservationResult) result;
				if (breastlNextScheduledDateObs != null && breastReferredToObsResult != null) {
					Date valueDate = null;
					if (breastlNextScheduledDateObs.getObs() != null)
						valueDate = breastlNextScheduledDateObs.getObs().getValueDate();
					if (valueDate != null
					        && valueDate.after(endDate)
					        && breastReferredToObsResult.getValue() != null
					        && !breastReferredToObsResult.getValue().equals("")
					        && valueDate.after(endDate)
					        && breastlNextScheduledDateObs.getObs().getEncounter().getEncounterId() == breastReferredToObsResult
					                .getObs().getEncounter().getEncounterId())
						alerts.append("" + patientFullName
						        + ", you have an appointment for breast cancer screening follow up on "
						        + breastlNextScheduledDateObs.getValue() + " . Please go to "
						        + breastReferredToObsResult.getValue() + " to continue this care." + "\n");
					
				}
				if (breastlNextScheduledDateObs != null) {
					Date valueDate = null;
					if (breastlNextScheduledDateObs.getObs() != null) {
						valueDate = breastlNextScheduledDateObs.getObs().getValueDate();
					}
					if (valueDate != null && valueDate.after(endDate) && breastReferredToObsResult.getValue() == null) {
						alerts.append("" + patientFullName
						        + ", you have an appointment for breast cancer screening follow up on "
						        + breastlNextScheduledDateObs.getValue() + " . Please go to "
						        + breastlNextScheduledDateObs.getObs().getLocation().getName() + " to continue this care."
						        + "\n");
					}
				}
			}
			
			if (result.getName().equals("mostRecentCervicalEncounterOfType")) {
				
				EncounterResult encounter = (EncounterResult) result;
				if (encounter != null) {
					mostRecentCervicalEncounterOfTypeResult = encounter;
					
					if (cervicalNextScheduledDate.getValue() != null
					        && cervicalReferredToInPeriodObsResult.getValue() != null) {
						if (mostRecentCervicalEncounterOfTypeResult.getValue().getEncounterDatetime().before(endDate)
						        && cervicalNextScheduledDate.getObs().getValueDate().before(endDate)) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(cervicalNextScheduledDate.getObs().getValueDate());
							calendar.add(Calendar.WEEK_OF_YEAR, 1);
							String VisitDate = null;
							VisitDate = format.format(calendar.getTime());
							
							alerts.append("" + patientFullName
							        + ", you had an appointment for cervical cancer screening follow up. Please go to "
							        + cervicalReferredToInPeriodObsResult.getValue() + " on " + VisitDate
							        + " to continue this care." + "\n");
							
						}
					}
					if (cervicalNextScheduledDate.getValue() != null
					        && cervicalReferredToInPeriodObsResult.getValue() == null && hpvObsResultTest.getValue() != null) {
						if (mostRecentCervicalEncounterOfTypeResult.getValue().getEncounterDatetime().before(endDate)
						        && cervicalNextScheduledDate.getObs().getValueDate().before(endDate)
						        && !(hpvObsResultTest.getValue().equals("HPV negative") && hpvObsResultTest.getObs()
						                .getObsDatetime().after(cervicalNextScheduledDate.getObs().getValueDate()))) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(cervicalNextScheduledDate.getObs().getValueDate());
							calendar.add(Calendar.WEEK_OF_YEAR, 1);
							String VisitDate = null;
							VisitDate = format.format(calendar.getTime());
							
							alerts.append("" + patientFullName
							        + ", you had an appointment for cervical cancer screening follow up. Please go to "
							        + cervicalNextScheduledDate.getObs().getLocation().getName() + " on " + VisitDate
							        + " to continue this care." + "\n");
							System.out.println("" + patientFullName
							        + ", you had an appointment for cervical cancer screening follow up. Please go to "
							        + cervicalNextScheduledDate.getObs().getLocation().getName() + " on " + VisitDate
							        + " to continue this care.");
						}
					}
					
					if (encounter.getValue() != null) {
						/*LocalDate visitDate = convertToLocalDateViaInstant(encounter.getValue().getEncounterDatetime());
						LocalDate localStartDate = convertToLocalDateViaInstant(startDate);
						LocalDate localEndDate = convertToLocalDateViaInstant(endDate);
						*/
						Long diffStartDate = null;
						Long diffEndDate = null;
						
						Calendar calStartDate = Calendar.getInstance();
						calStartDate.setTime(startDate);
						
						Calendar calEndDate = Calendar.getInstance();
						calEndDate.setTime(endDate);
						
						Calendar calEncounterDate = Calendar.getInstance();
						calEncounterDate.setTime(encounter.getValue().getEncounterDatetime());
						
						diffStartDate = (calStartDate.getTimeInMillis() - calEncounterDate.getTimeInMillis())
						        / (1000 * 60 * 60 * 24);
						diffEndDate = (calEndDate.getTimeInMillis() - calEncounterDate.getTimeInMillis())
						        / (1000 * 60 * 60 * 24);
						System.out.println("Patient is: " + result.getPatientData().getPatient().getPatientId() + " Name:"
						        + patientFullName + " Condition1: " + diffStartDate + " Condition2: " + diffEndDate);
						
						//if(mostRecentTreatmentPerformedResult.getValue()!=null && !hivStatus.equals("") && hpvObsResultTest.getValue()!=null && )
						if (diffStartDate < 365
						        && diffEndDate >= 365
						        && ((mostRecentTreatmentPerformedResult.getValue() != null && mostRecentTreatmentPerformedResult
						                .getValue().equals("YES"))
						                || (hivStatus != null && hivStatus.equals("") && hivStatus.equals("NEGATIVE"))
						                || (hpvObsResultTest.getValue() != null && hpvObsResultTest.getValue().equals(
						                    "HPV positive Type")) || !(mostRecentVIAResults.getValue() != null && (mostRecentVIAResults
						                .getValue().equals("VIA+ Eligible for Thermal ablation") || mostRecentVIAResults
						                .getValue().equals("VIA+ Eligible for LEEP"))))) {
							alerts.append(""
							        + patientFullName
							        + ", "
							        + mostRecentCervicalEncounterOfTypeResult.getValue().getLocation().getName()
							        + " would like to inform you that it has been 1 year since your last cervical cancer screening visit so you should come to your health center for a routine follow up screening."
							        + "\n");
							
						}
						
						if (diffStartDate < 1095
						        && diffEndDate >= 1095
						        && (hivStatus != null && hivStatus.equals("") && hivStatus.equals("POSITIVE"))
						        && !(hpvObsResultTest.getValue() != null && hpvObsResultTest.getValue().equals(
						            "HPV positive Type"))) {
							System.out.println("Befor check Patient is: "
							        + result.getPatientData().getPatient().getPatientId() + " Name:" + patientFullName);
							alerts.append(""
							        + patientFullName
							        + ", "
							        + mostRecentCervicalEncounterOfTypeResult.getValue().getLocation().getName()
							        + " would like to inform you that it has been 3 years since your last cervical cancer screening visit so you should come to your health center for a routine follow up screening."
							        + "\n");
							
						}
						if (diffStartDate < 1825
						        && diffEndDate >= 1825
						        && !(hivStatus != null && hivStatus.equals("") && hivStatus.equals("POSITIVE"))
						        && !(hpvObsResultTest.getValue() != null && hpvObsResultTest.getValue().equals(
						            "HPV positive Type"))) {
							System.out.println("Befor check Patient is: "
							        + result.getPatientData().getPatient().getPatientId() + " Name:" + patientFullName);
							alerts.append(""
							        + patientFullName
							        + ", "
							        + mostRecentCervicalEncounterOfTypeResult.getValue().getLocation().getName()
							        + " would like to inform you that it has been 5 years since your last cervical cancer screening visit so you should come to your health center for a routine follow up screening."
							        + "\n");
							
						}
						if (diffStartDate < 1095
						        && diffEndDate >= 1095
						        && hpvObsResultTest.getValue() == null
						        && mostRecentVIAResults.getValue() != null
						        && (mostRecentVIAResults.getValue() != null && (mostRecentVIAResults.getValue().equals(
						            "VIA+ Eligible for Thermal ablation") || mostRecentVIAResults.getValue().equals(
						            "VIA+ Eligible for LEEP")))) {
							System.out.println("Befor check Patient is: "
							        + result.getPatientData().getPatient().getPatientId() + " Name:" + patientFullName);
							alerts.append(""
							        + patientFullName
							        + ", "
							        + mostRecentCervicalEncounterOfTypeResult.getValue().getLocation().getName()
							        + " would like to inform you that it has been 3 years since your last cervical cancer screening visit so you should come to your health center for a routine follow up screening."
							        + "\n");
							
						}
						
					}
					
				}
			}
			if (result.getName().equals("mostRecentBreastEncounterOfType")) {
				EncounterResult encounter = (EncounterResult) result;
				if (encounter != null) {
					mostRecentBreastEncounterOfTypeResult = encounter;
					
					if (breastNextScheduledDate.getValue() != null
					        && breastNextScheduledDate.getObs().getValueDate() != null
					        && breastReferredToInPeriodObsResult.getValue() != null
					        && mostRecentBreastEncounterOfTypeResult.getValue() != null) {
						if (mostRecentBreastEncounterOfTypeResult.getValue().getEncounterDatetime().before(endDate)
						        && breastNextScheduledDate.getObs().getValueDate().before(endDate)) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(breastNextScheduledDate.getObs().getValueDate());
							calendar.add(Calendar.WEEK_OF_YEAR, 1);
							String VisitDate = null;
							VisitDate = format.format(calendar.getTime());
							alerts.append("" + patientFullName
							        + ", you had an appointment for  Breast cancer screening follow up. Please go to "
							        + breastReferredToInPeriodObsResult.getValue() + " on " + VisitDate
							        + " to continue this care." + "\n");
						}
					}
					if (breastNextScheduledDate.getValue() != null
					        && breastNextScheduledDate.getObs().getValueDate() != null
					        && breastReferredToInPeriodObsResult.getValue() == null
					        && mostRecentBreastEncounterOfTypeResult.getValue() != null) {
						/*System.out.println("Encounter ID:"+mostRecentBreastEncounterOfTypeResult.getValue().getEncounterId()+" Encounter Date: "+mostRecentBreastEncounterOfTypeResult.getValue().getEncounterDatetime());
						System.out.println("OBS ID:"+breastNextScheduledDate.getObs().getObsId()+" Value Date: "+breastNextScheduledDate.getObs().getValueDate());
						*/
						if (mostRecentBreastEncounterOfTypeResult.getValue().getEncounterDatetime().before(endDate)
						        && breastNextScheduledDate.getObs().getValueDate().before(endDate)) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(breastNextScheduledDate.getObs().getValueDate());
							calendar.add(Calendar.WEEK_OF_YEAR, 1);
							String VisitDate = null;
							VisitDate = format.format(calendar.getTime());
							alerts.append("" + patientFullName
							        + ", you had an appointment for  Breast cancer screening follow up. Please go to "
							        + breastNextScheduledDate.getObs().getLocation().getName() + " on " + VisitDate
							        + " to continue this care." + "\n");
						}
					}
				}
			}
			
			/*	if(result.getName().equals("mostrecentScreeningEncounter"))
				{
					EncounterResult mostrecentScreeningEncounterResult = (EncounterResult) result;
					log.info("In mostrecentScreeningEncounterrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");

					ObservationResult cervicalNextScheduledDate =null;
					ObservationResult breastNextScheduledDate =null;
					if( cervicalNextScheduledDate != null || breastNextScheduledDate!=null)
					{
						log.info("Next scheduled data availableeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
						Date valueDate=null;
						if (mostrecentScreeningEncounterResult.getValue()!=null)
							valueDate=mostrecentScreeningEncounterResult.getValue().getEncounterDatetime();
						if (valueDate!=null && valueDate.before(endDate) && breastNextScheduledDate.getValue()!=null && valueDate.after(breastNextScheduledDate.getObs().getValueDate())==false && breastReferredToObsResult.getValue()!=null) {
							log.info("In Breastttttttttttttttttttttttttttttt");

							Calendar calendar = Calendar.getInstance();
							calendar.setTime(breastNextScheduledDate.getObs().getValueDate());
							calendar.add(Calendar.WEEK_OF_YEAR, 1);
							String VisitDate = null;
							VisitDate = format.format(calendar.getTime());

							alerts.append("" + patientFullName + ", you had an appointment for cervical cancer screening follow up. Please go to " + breastReferredToObsResult.getValue() + "on " + VisitDate + " to continue this care." + "\n");
						}
						else if (valueDate!=null && valueDate.before(endDate) && cervicalNextScheduledDate.getValue()!=null && valueDate.after(cervicalNextScheduledDate.getObs().getValueDate())==false && cervicalReferredToObsResult.getValue()!=null) {
							log.info("In Cervicallllllllllllllllllllllllllllllllllllll");

							Calendar calendar = Calendar.getInstance();
							calendar.setTime(cervicalNextScheduledDate.getObs().getValueDate());
							calendar.add(Calendar.WEEK_OF_YEAR, 1);
							String VisitDate = null;
							VisitDate = format.format(calendar.getTime());
							alerts.append("" + patientFullName + ", you had an appointment for cervical cancer screening follow up. Please go to " + cervicalReferredToObsResult.getValue() + "on " + VisitDate + " to continue this care." + "\n");
						}


					}
				}*/
			
		}
		
		alert.setValue(alerts.toString().trim());
		return alert;
	}
	
	public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
		return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
}
