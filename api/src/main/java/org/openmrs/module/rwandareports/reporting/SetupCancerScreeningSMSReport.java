package org.openmrs.module.rwandareports.reporting;

import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rowperpatientreports.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.rowperpatientreports.patientdata.definition.CustomCalculationBasedOnMultiplePatientDataDefinitions;
import org.openmrs.module.rowperpatientreports.patientdata.definition.MostRecentEncounterOfType;
import org.openmrs.module.rowperpatientreports.patientdata.definition.MostRecentObservation;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ObservationInMostRecentEncounter;
import org.openmrs.module.rwandareports.customcalculator.CancerScreenSMSAlert;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.RowPerPatientColumns;

import java.util.*;

public class SetupCancerScreeningSMSReport {
	
	//properties retrieved from global variables
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	private Concept hivStatus;
	
	private Concept HPV;
	
	private Concept testResult;
	
	private Concept HPVpositive;
	
	private Concept HPVNegative;
	
	private Concept treatmentPerformed;
	
	private Form oncologyBreastScreeningExamination;
	
	private Form oncologyCervicalScreeningExamination;
	
	private List<EncounterType> breastScreeningEncounterTypes = new ArrayList<EncounterType>();
	
	private List<EncounterType> cervicalScreeningEncounterTypes = new ArrayList<EncounterType>();
	
	private List<EncounterType> screeningEncounterTypes = new ArrayList<EncounterType>();
	
	private Concept hasPatientBeenReferred_cervical;
	
	private Concept nextStep;
	
	private Concept reasonsForReferral;
	
	private Concept referredTo;
	
	private Concept VIAResults;
	
	public void setup() throws Exception {
		setupProperties();
		
		ReportDefinition smsReportDefinition = createSMSReportDefinition();
		
		ReportDesign smsReporDesign = Helper.createRowPerPatientXlsOverviewReportDesign(smsReportDefinition,
		    "cancerscreeningSMSReport.xls", "cancerscreeningSMSReport.xls_", null);
		
		Properties consultProps = new Properties();
		consultProps.put("repeatingSections", "sheet:1,row:10,dataset:dataset");
		consultProps.put("sortWeight", "5000");
		
		smsReporDesign.setProperties(consultProps);
		
		Helper.saveReportDesign(smsReporDesign);
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("cancerscreeningSMSReport.xls_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		Helper.purgeReportDefinition("ONC-Cancer Screening SMS Report");
		
	}
	
	private ReportDefinition createSMSReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("ONC-Cancer Screening SMS Report");
		Parameter location = new Parameter("location", "Health Facility", Location.class);
		location.setRequired(false);
		reportDefinition.addParameter(location);
		//reportDefinition.addParameter(new Parameter("location", "Health Facility", Location.class));
		reportDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
		reportDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		SqlCohortDefinition locationDefinition = new SqlCohortDefinition();
		locationDefinition
		        .setQuery("select p.patient_id from patient p, person_attribute pa, person_attribute_type pat where p.patient_id = pa.person_id and pat.format ='org.openmrs.Location' and pa.voided = 0 and pat.person_attribute_type_id = pa.person_attribute_type_id and (:location is null or pa.value = :location)");
		locationDefinition.setName("locationDefinition");
		locationDefinition.addParameter(location);
		
		reportDefinition.setBaseCohortDefinition(locationDefinition,
		    ParameterizableUtil.createParameterMappings("location=${location}"));
		
		createsmsDataSetDefinition(reportDefinition);
		Helper.saveReportDefinition(reportDefinition);
		
		return reportDefinition;
	}
	
	private void createsmsDataSetDefinition(ReportDefinition reportDefinition) {
		// Create new dataset definition
		RowPerPatientDataSetDefinition dataSetDef = new RowPerPatientDataSetDefinition();
		dataSetDef.setName("SMS Data set");
		Parameter location = new Parameter("location", "Health Facility", Location.class);
		location.setRequired(false);
		dataSetDef.addParameter(location);
		//dataSetDef.addParameter(new Parameter("location", "location", Date.class));
		dataSetDef.addParameter(new Parameter("startDate", "startDate", Date.class));
		dataSetDef.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		//Add Columns
		
		dataSetDef.addColumn(RowPerPatientColumns.getArchivingId("Id"), new HashMap<String, Object>());
		
		dataSetDef.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		
		dataSetDef.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		
		dataSetDef.addColumn(RowPerPatientColumns.getAge("age"), new HashMap<String, Object>());
		dataSetDef.addColumn(RowPerPatientColumns.getPhoneNumber("phoneNumber"), new HashMap<String, Object>());
		
		//Calculation definitions
		
		MostRecentObservation mostRecentHPVResultTest = RowPerPatientColumns
		        .getMostRecent("hpvResultTest", testResult, null);
		mostRecentHPVResultTest.addParameter(new Parameter("startDate", "startDate", Date.class));
		mostRecentHPVResultTest.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		MostRecentObservation mostRecenthivStatus = RowPerPatientColumns.getMostRecent("hivResultTest", hivStatus, null);
		
		MostRecentObservation mostRecentTreatmentPerformed = RowPerPatientColumns.getMostRecent(
		    "mostRecentTreatmentPerformed", treatmentPerformed, null);
		
		MostRecentObservation mostRecentVIAResults = RowPerPatientColumns.getMostRecent("mostRecentVIAResults", VIAResults,
		    null);
		
		ObservationInMostRecentEncounter cervicalNextScheduledDate = RowPerPatientColumns
		        .getObservationInMostRecentEncounter("cervicalNextScheduledDate",
		            gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE), null, cervicalScreeningEncounterTypes, null);
		ObservationInMostRecentEncounter breastNextScheduledDate = RowPerPatientColumns.getObservationInMostRecentEncounter(
		    "breastNextScheduledDate", gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE), null,
		    breastScreeningEncounterTypes, null);
		
		ObservationInMostRecentEncounter cervicalNextScheduledDateInPeriod = RowPerPatientColumns
		        .getObservationInMostRecentEncounter("cervicalNextScheduledDateInPeriod",
		            gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE), null, cervicalScreeningEncounterTypes, null);
		cervicalNextScheduledDateInPeriod.addParameter(new Parameter("startDate", "startDate", Date.class));
		cervicalNextScheduledDateInPeriod.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		ObservationInMostRecentEncounter breastNextScheduledDateInPeriod = RowPerPatientColumns
		        .getObservationInMostRecentEncounter("breastNextScheduledDateInPeriod",
		            gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE), null, breastScreeningEncounterTypes, null);
		breastNextScheduledDateInPeriod.addParameter(new Parameter("startDate", "startDate", Date.class));
		breastNextScheduledDateInPeriod.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		ObservationInMostRecentEncounter referred_Cervical = RowPerPatientColumns.getObservationInMostRecentEncounter(
		    "referred_Cervical", hasPatientBeenReferred_cervical, null, cervicalScreeningEncounterTypes, null);
		ObservationInMostRecentEncounter referred_Breast = RowPerPatientColumns.getObservationInMostRecentEncounter(
		    "referred_Breast", nextStep, null, breastScreeningEncounterTypes, null);
		
		ObservationInMostRecentEncounter referredReasonBreast = RowPerPatientColumns.getObservationInMostRecentEncounter(
		    "referredBreast", reasonsForReferral, null, breastScreeningEncounterTypes, null);
		ObservationInMostRecentEncounter referredReasonCervical = RowPerPatientColumns.getObservationInMostRecentEncounter(
		    "referredCervical", reasonsForReferral, null, cervicalScreeningEncounterTypes, null);
		ObservationInMostRecentEncounter cervicalReferredTo = RowPerPatientColumns.getObservationInMostRecentEncounter(
		    "cervicalReferredTo", referredTo, null, cervicalScreeningEncounterTypes, null);
		ObservationInMostRecentEncounter beastReferredTo = RowPerPatientColumns.getObservationInMostRecentEncounter(
		    "breastReferredTo", referredTo, null, breastScreeningEncounterTypes, null);
		
		ObservationInMostRecentEncounter cervicalReferredToInPeriod = RowPerPatientColumns
		        .getObservationInMostRecentEncounter("cervicalReferredToInPeriod", referredTo, null,
		            cervicalScreeningEncounterTypes, null);
		cervicalReferredToInPeriod.addParameter(new Parameter("startDate", "startDate", Date.class));
		cervicalReferredToInPeriod.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		ObservationInMostRecentEncounter beastReferredToInPeriod = RowPerPatientColumns.getObservationInMostRecentEncounter(
		    "breastReferredToInPeriod", referredTo, null, breastScreeningEncounterTypes, null);
		beastReferredToInPeriod.addParameter(new Parameter("startDate", "startDate", Date.class));
		beastReferredToInPeriod.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		MostRecentEncounterOfType mostRecentCervicalEncounterOfType = RowPerPatientColumns.getMostRecentEncounter(
		    "mostRecentCervicalEncounterOfType", cervicalScreeningEncounterTypes);
		MostRecentEncounterOfType mostRecentBreastEncounterOfType = RowPerPatientColumns.getMostRecentEncounter(
		    "mostRecentBreastEncounterOfType", breastScreeningEncounterTypes);
		
		CustomCalculationBasedOnMultiplePatientDataDefinitions sms = new CustomCalculationBasedOnMultiplePatientDataDefinitions();
		sms.setName("sms");
		sms.addPatientDataToBeEvaluated(mostRecentHPVResultTest,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		sms.addPatientDataToBeEvaluated(mostRecenthivStatus, new HashMap<String, Object>());
		sms.addPatientDataToBeEvaluated(cervicalNextScheduledDate, new HashMap<String, Object>());
		sms.addPatientDataToBeEvaluated(cervicalNextScheduledDateInPeriod,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		sms.addPatientDataToBeEvaluated(cervicalReferredTo, new HashMap<String, Object>());
		sms.addPatientDataToBeEvaluated(breastNextScheduledDate, new HashMap<String, Object>());
		sms.addPatientDataToBeEvaluated(breastNextScheduledDateInPeriod,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		sms.addPatientDataToBeEvaluated(mostRecentCervicalEncounterOfType, new HashMap<String, Object>());
		sms.addPatientDataToBeEvaluated(mostRecentBreastEncounterOfType, new HashMap<String, Object>());
		sms.addPatientDataToBeEvaluated(beastReferredTo, new HashMap<String, Object>());
		
		sms.addPatientDataToBeEvaluated(cervicalReferredToInPeriod,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		sms.addPatientDataToBeEvaluated(beastReferredToInPeriod,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		sms.addPatientDataToBeEvaluated(mostRecentTreatmentPerformed, new HashMap<String, Object>());
		sms.addPatientDataToBeEvaluated(mostRecentVIAResults, new HashMap<String, Object>());
		
		sms.setCalculator(new CancerScreenSMSAlert());
		sms.addParameter(new Parameter("startDate", "startDate", Date.class));
		sms.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		dataSetDef.addColumn(sms, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("location", "${location}");
		mappings.put("endDate", "${endDate}");
		mappings.put("startDate", "${startDate}");
		
		reportDefinition.addDataSetDefinition("dataset", dataSetDef, mappings);
		
	}
	
	private void setupProperties() {
		hivStatus = Context.getConceptService().getConceptByUuid("aec6ad18-f4dd-4cfa-b68d-3d7bb6ea908e");
		treatmentPerformed = Context.getConceptService().getConceptByUuid("ae47a54a-9111-475c-9cc2-76951d78b0c8");
		
		HPV = Context.getConceptService().getConceptByUuid("f7c2d59d-2043-42ce-b04d-08564d54b0c7");
		testResult = Context.getConceptService().getConceptByUuid("bfb3eb1e-db98-4846-9915-0168511c6298");
		HPVpositive = Context.getConceptService().getConceptByUuid("1b4a5f67-6106-4a4d-a389-2f430be543e4");
		HPVNegative = Context.getConceptService().getConceptByUuid("64c23192-54e4-4750-9155-2ed0b736a0db");
		
		oncologyBreastScreeningExamination = gp.getForm(GlobalPropertiesManagement.ONCOLOGY_BREAST_SCREENING_EXAMINATION);
		oncologyCervicalScreeningExamination = gp
		        .getForm(GlobalPropertiesManagement.ONCOLOGY_CERVICAL_SCREENING_EXAMINATION);
		
		breastScreeningEncounterTypes.add(oncologyBreastScreeningExamination.getEncounterType());
		cervicalScreeningEncounterTypes.add(oncologyCervicalScreeningExamination.getEncounterType());
		screeningEncounterTypes.add(oncologyBreastScreeningExamination.getEncounterType());
		screeningEncounterTypes.add(oncologyCervicalScreeningExamination.getEncounterType());
		
		hasPatientBeenReferred_cervical = Context.getConceptService().getConceptByUuid(
		    "805f40f2-4720-4474-b761-5880c9d3870e");
		nextStep = Context.getConceptService().getConceptByUuid("69b9671b-d8b1-461b-bb7d-adb151775a57");
		reasonsForReferral = Context.getConceptService().getConceptByUuid("1aa373f4-4db5-4b01-bce0-c10a636bb931");
		referredTo = Context.getConceptService().getConceptByUuid("3a84ab37-f75c-48ad-8bcf-322c927f36bb");
		VIAResults = Context.getConceptService().getConceptByUuid("a37a937a-a2a6-4c22-975f-986fb3599ea3");
		
	}
}
