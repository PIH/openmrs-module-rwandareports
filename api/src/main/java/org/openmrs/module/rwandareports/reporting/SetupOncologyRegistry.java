package org.openmrs.module.rwandareports.reporting;

import org.openmrs.*;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.rowperpatientreports.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.RowPerPatientColumns;

import java.util.*;

public class SetupOncologyRegistry extends SingleSetupReport {
	
	//properties retrieved from global variables
	private Program oncologyProgram;
	
	private ProgramWorkflow diagnosis;
	
	private ProgramWorkflow oncologySurgeryStatusProgramWorkflow;
	
	private ProgramWorkflow oncologyRadiationStatusProgramWorkflow;
	
	private Concept confirmedDiagnosis;
	
	private Concept otherOperativeFindings;
	
	private Concept diseaseStage;
	
	private Concept tumorOfTNM;
	
	private Concept nodeOfTNM;
	
	private Concept metastesesofTNM;
	
	private Concept chemotherapy;
	
	private Concept patientPresentsChemo;
	
	private Concept radiationorChemotherapyStatus;
	
	private Concept RadiationStatus;
	
	private Concept treatmentIntent;
	
	private Concept oncologyprogramendreason;
	
	private Concept TypeOfReferrencingToClinicOrHospital;
	
	private Concept locationreferraltype;
	
	private Concept Patientreferredtowhere;
	
	private Concept causeOfDeath;
	
	private Concept LOCATIONOFDEATH;
	
	private Concept yes;
	
	private EncounterType outpatientOncologyEncounterType;
	
	private EncounterType inpatientOncologyEncounterType;
	
	private List<EncounterType> oncologyEncounterTypes = new ArrayList<EncounterType>();
	
	private List<Concept> chemoAnswerList = new ArrayList<Concept>();
	
	private Concept namesAndFirstNamesOfContact;
	
	private Concept telephoneNumberOfContact;
	
	private Date startDate;
	
	private Date endDate;
	
	@Override
	public String getReportName() {
		return "ONC-Registry";
	}
	
	public void setup() throws Exception {
		log.info("Setting up report: " + getReportName());
		setupProperties();
		
		ReportDefinition rd = createReportDefinition();
		
		ReportDesign design = Helper.createRowPerPatientXlsOverviewReportDesign(rd, "OncologyRegistry.xls",
		    "OncologyRegistry.xls_", null);
		ReportDesign designCSV = Helper.createCsvReportDesign(rd, "OncologyRegistry.csv_");
		
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,row:7,dataset:dataset");
		props.put("sortWeight", "5000");
		design.setProperties(props);
		designCSV.setProperties(props);
		
		Helper.saveReportDesign(design);
		Helper.saveReportDesign(designCSV);
		
	}
	
	private ReportDefinition createReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName(getReportName());
		
		reportDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
		reportDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
		reportDefinition.setBaseCohortDefinition(Cohorts.createProgramEnrollmentEverByEndDate("Oncology", oncologyProgram),
		    ParameterizableUtil.createParameterMappings("enrolledOnOrBefore=${endDate}"));
		
		createDataSetDefinition(reportDefinition);
		
		Helper.saveReportDefinition(reportDefinition);
		
		return reportDefinition;
	}
	
	private void createDataSetDefinition(ReportDefinition reportDefinition) {
		// Create new dataset definition 
		RowPerPatientDataSetDefinition dataSetDefinition = new RowPerPatientDataSetDefinition();
		dataSetDefinition.setName("ONC-Registry");
		
		dataSetDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
		dataSetDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
		
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("endDate", "${endDate}");
		mappings.put("startDate", "${startDate}");
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFirstNameColumn("Given Name"), new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getFamilyNameColumn("Family Name"), new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getRwandaNationalID("Rwanda National ID"),
		    new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getArchivingId("Archiving Id"), new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentInPeriod("Next Of Kin Name",
		    namesAndFirstNamesOfContact, null, null, "dd-MMM-yyyy"), mappings);
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentInPeriod("Next Of Kin Tel Number",
		    telephoneNumberOfContact, null, null, "dd-MMM-yyyy"), mappings);
		dataSetDefinition.addColumn(RowPerPatientColumns.getDateOfBirth("Date of Birth", "dd-MMM-yyyy", "dd-MMM-yyyy"),
		    new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getGender("Sex"), new HashMap<String, Object>());
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getPatientAddress("Place of residence", true, true, true, true, true),
		    new HashMap<String, Object>());
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getPatientAddress("Nationality", true, false, false, false, false, false),
		    new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getPatientProgramInfo("Incidence date", oncologyProgram,
		    "EnrollmentDate", null, "dd-MMM-yyyy"), new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getAllObservationValuesBeforeEndDate(
		    "Basis of Diagnosis and Laterality (From operative Findings)", otherOperativeFindings, 0, "dd-MMM-yyyy", null,
		    null), new HashMap<String, Object>());
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getStateOfPatient("Primary Site", oncologyProgram, diagnosis, null),
		    new HashMap<String, Object>());
		dataSetDefinition
		        .addColumn(
		            RowPerPatientColumns.getMostRecentInPeriod("Histology", confirmedDiagnosis, null, null, "dd-MMM-yyyy"),
		            mappings);
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecentInPeriod("Stage", diseaseStage, null, null, "dd-MMM-yyyy"), mappings);
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentInPeriod("T", tumorOfTNM, null, null, "dd-MMM-yyyy"),
		    mappings);
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentInPeriod("N", nodeOfTNM, null, null, "dd-MMM-yyyy"),
		    mappings);
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecentInPeriod("M", metastesesofTNM, null, null, "dd-MMM-yyyy"), mappings);
		//
		//	//find away to retain the earliest.
		dataSetDefinition.addColumn(RowPerPatientColumns.getDateOfEarliestHIVProgramEnrolmentExcludePMTCTInfant(
		    "HIV status & Date of HIV enrollment", "dd-MMM-yyyy"), new HashMap<String, Object>());
		//
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecentInPeriod("Treatment Intent", treatmentIntent, null, null, "dd-MMM-yyyy"),
		    mappings);
		//
		//	//on the excell consider planned and completed only
		dataSetDefinition.addColumn(RowPerPatientColumns.getStateOfPatient("Surgerical statu(Surgery start date not known)",
		    oncologyProgram, oncologySurgeryStatusProgramWorkflow, null), new HashMap<String, Object>());
		//	//have to test if it works
		dataSetDefinition.addColumn(RowPerPatientColumns.getDateofFirstOrdersetsRestrictedByConceptEndDate(
		    "Date of first Chemotherapy order", chemotherapy, "dd-MMM-yyyy"), ParameterizableUtil
		        .createParameterMappings("endDate=${endDate}"));
		dataSetDefinition.addColumn(RowPerPatientColumns.getDateofLastOrdersetsRestrictedByConceptInThePeriod(
		    "Date of last Chemotherapy order", chemotherapy, "dd-MMM-yyyy"), mappings);
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentInperiodHavingCodedAnswers(
		    "dateOfLastGivenChemotherapy", patientPresentsChemo, chemoAnswerList, null, null, "dd-MMM-yyyy"), mappings);
		//	//have to test if it works
		dataSetDefinition.addColumn(RowPerPatientColumns.getNamesOfAllOrdersetsOfConceptInPeriod(
		    "list of Chemotherapy order sets prescribed", chemotherapy), ParameterizableUtil
		        .createParameterMappings("afterDate=${startDate},beforeDate=${endDate}"));
		//	//Radiotherapy (on Concepts check for planned only) on workflow check if it is completed treatment or planned
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentInPeriod("radiation or ChemotherapyStatus",
		    radiationorChemotherapyStatus, null, null, "dd-MMM-yyyy"), mappings);
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecentInPeriod("Radiation Status", RadiationStatus, null, null, "dd-MMM-yyyy"),
		    mappings);
		dataSetDefinition.addColumn(RowPerPatientColumns.getStateOfPatient("Oncology Radiation Status State",
		    oncologyProgram, oncologyRadiationStatusProgramWorkflow, null), new HashMap<String, Object>());
		//
		//	//Palliation only (include the treatment intent of palliation related answers ) and palliaton related of endreasons
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentInPeriod("Oncology Programend reason",
		    oncologyprogramendreason, null, null, "dd-MMM-yyyy"), mappings);
		//
		//
		//	//on Date of Admission use the IncidenceDate
		//
		dataSetDefinition.addColumn(RowPerPatientColumns.getPatientProgramInfo("Date Of Discharge", oncologyProgram,
		    "ExitDate", null, "dd-MMM-yyyy"), new HashMap<String, Object>());
		//
		//	//referred from (include both)
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentInPeriod("Referral Type",
		    TypeOfReferrencingToClinicOrHospital, null, null, "dd-MMM-yyyy"), mappings);
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentInPeriod("Referred from location",
		    locationreferraltype, null, null, "dd-MMM-yyyy"), mappings);
		
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecentInPeriod("referred to", Patientreferredtowhere, null, null, "dd-MMM-yyyy"),
		    mappings);
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentEncounterOfTypebyEnddate("dateOfLastContact",
		    oncologyEncounterTypes, "dd-MMM-yyyy"), mappings);
		
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getPatientProgramInfo("OncologyOutcome", oncologyProgram, "OutCome", null, "dd-MMM-yyyy"),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecentInPeriod("CAUSE OF DEATH", causeOfDeath, null, null, "dd-MMM-yyyy"), mappings);
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecentInPeriod("LOCATION OF DEATH", LOCATIONOFDEATH, null, null, "dd-MMM-yyyy"),
		    mappings);
		
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getPatientDeathInfo("Date Of Death", "DeathDate", null, "dd-MMM-yyyy"),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getIMBId("IMB ID"), new HashMap<String, Object>());
		
		reportDefinition.addDataSetDefinition("dataset", dataSetDefinition, mappings);
		
	}
	
	private void setupProperties() {
		
		oncologyProgram = gp.getProgram(GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		namesAndFirstNamesOfContact = gp.getConcept(GlobalPropertiesManagement.NAMESANDFIRSTNAMESOFCONTACT);
		telephoneNumberOfContact = gp.getConcept(GlobalPropertiesManagement.TELEPHONE_NUMBER_OF_CONTACT);
		diseaseStage = gp.getConcept(GlobalPropertiesManagement.OVERALL_ONCOLOGY_STAGE);
		otherOperativeFindings = gp.getConcept(GlobalPropertiesManagement.OTHEROPERATIVEFINDINGS);
		tumorOfTNM = gp.getConcept(GlobalPropertiesManagement.TUMOROFTNM);
		nodeOfTNM = gp.getConcept(GlobalPropertiesManagement.NODEOFTNM);
		metastesesofTNM = gp.getConcept(GlobalPropertiesManagement.METASTESESOFTNM);
		chemotherapy = gp.getConcept(GlobalPropertiesManagement.CHEMOTHERAPY);
		patientPresentsChemo = gp.getConcept(GlobalPropertiesManagement.PATIENT_PRESENTS_FOR_CHEMO);
		radiationorChemotherapyStatus = gp.getConcept(GlobalPropertiesManagement.RADIATIONORCHEMOTHERAPYSTATUS);
		RadiationStatus = gp.getConcept(GlobalPropertiesManagement.RADIATIONSTATUS);
		treatmentIntent = gp.getConcept(GlobalPropertiesManagement.TREATMENTINTENT);
		oncologyprogramendreason = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_PROGRAM_END_REASON);
		TypeOfReferrencingToClinicOrHospital = gp
		        .getConcept(GlobalPropertiesManagement.TYPE_OF_REFERRING_CLINIC_OR_HOSPITAL);
		locationreferraltype = gp.getConcept(GlobalPropertiesManagement.LOCATION_REFFERAL_TYPE);
		Patientreferredtowhere = gp.getConcept(GlobalPropertiesManagement.PATIENTREFERREDTOWHERE);
		causeOfDeath = gp.getConcept(GlobalPropertiesManagement.CAUSEOFDEATH);
		LOCATIONOFDEATH = gp.getConcept(GlobalPropertiesManagement.LOCATIONOFDEATH);
		confirmedDiagnosis = gp.getConcept(GlobalPropertiesManagement.CONFIRMED_DIAGNOSIS_CONCEPT);
		yes = gp.getConcept(GlobalPropertiesManagement.YES);
		
		chemoAnswerList.add(yes);
		
		outpatientOncologyEncounterType = gp.getEncounterType(GlobalPropertiesManagement.INPATIENT_ONCOLOGY_ENCOUNTER);
		inpatientOncologyEncounterType = gp.getEncounterType(GlobalPropertiesManagement.OUTPATIENT_ONCOLOGY_ENCOUNTER);
		oncologyEncounterTypes.add(outpatientOncologyEncounterType);
		oncologyEncounterTypes.add(inpatientOncologyEncounterType);
		diagnosis = gp.getProgramWorkflow(GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
		    GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		oncologySurgeryStatusProgramWorkflow = gp
		        .getProgramWorkflow(GlobalPropertiesManagement.ONCOLOGY_SURGERY_STATUS_PROGRAM_WORKFLOW,
		            GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		oncologyRadiationStatusProgramWorkflow = gp.getProgramWorkflow(
		    GlobalPropertiesManagement.ONCOLOGY_RADIATION_STATUS_PROGRAM_WORKFLOW,
		    GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
	}
	
}
