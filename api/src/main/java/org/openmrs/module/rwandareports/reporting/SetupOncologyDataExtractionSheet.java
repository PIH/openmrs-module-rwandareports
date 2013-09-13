package org.openmrs.module.rwandareports.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.common.SortCriteria.SortDirection;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rowperpatientreports.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.rwandareports.filter.DateFormatFilter;
import org.openmrs.module.rwandareports.filter.DateOfVisitSixmonthsPostIntakeFilter;
import org.openmrs.module.rwandareports.filter.LocationEncounterFilter;
import org.openmrs.module.rwandareports.filter.ObservationValueCodedAfterSixmonthsFilter;
import org.openmrs.module.rwandareports.filter.ObservationValueCodedInEncounterFilter;
import org.openmrs.module.rwandareports.filter.ObservationValueNumericAfterSixmonthsFilter;
import org.openmrs.module.rwandareports.filter.OncologyExitReasonsInEncounterFilter;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.MetadataLookup;
import org.openmrs.module.rwandareports.util.RowPerPatientColumns;

public class SetupOncologyDataExtractionSheet {
	
	Helper h = new Helper();
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	//properties retrieved from global variables
	private Program oncologyProgram;

	private ArrayList<Program> oncologyPrograms = new ArrayList<Program>();
	
	private ProgramWorkflow diagnosis;
	
	private ProgramWorkflow diagnosisStatus;
	
	/*private Concept scheduledVisit;
	
	private Concept biopsyResultVisit;
	
	private Concept specialVisit;*/
	
	/*private Concept telephone;
	
	private Concept telephone2;
	*/
	private ArrayList<EncounterType> intakeEncountertypes=new ArrayList<EncounterType>();
	
	private EncounterType intakeInPatient;
	
	private EncounterType intakeOutPatient;
	
	private EncounterType nonClinical;
	
	private Concept typeOfReferrencingToClinicOrHospital;
	
	private Form demoForm;
	
	private ArrayList<Form> demoForms=new ArrayList<Form>();
	
	private ArrayList<Form> intakeForms=new ArrayList<Form>();
	
	private ArrayList<Form> dstForms=new ArrayList<Form>();
	
	
	
	
	private List<EncounterType> nonClinicalEncountertypes=new ArrayList<EncounterType>();
	
	private Concept hivStatus;
	
	private Concept smokingHistory;
	
	private Concept alcoholHistory;
	
	private Concept cancerFamilyStatus;
	
	private Concept familyMemberWithCancer;
	
	private Concept familyMemberWithCancerDiagnosis;
	
	private Concept performanceStatus;
	
	private Concept previousCancerTreatment;
	
	private Concept dateOfPathologyReport;
	
	
	private ProgramWorkflow surgeryStatus;
	
	private ProgramWorkflow chemotherapyStatus;
	
	private ProgramWorkflow radiationStatus;
	
	private Concept erStatus;
	
	private Concept her2ihc; 	
	
	private Concept her2fish;
	
	private ArrayList<EncounterType> allOncologyEncountertypes=new ArrayList<EncounterType>();
	
	private Concept cancerProgressionStatus;
	
	private ArrayList<Form> outpatientFlows=new ArrayList<Form>();
	
	private Concept exitReason;
	
	private ArrayList<Form> exitForms=new ArrayList<Form>();
	
	private Concept mutuelle;
	
	private Concept mutuelleLevel;
	
	private ProgramWorkflow treatmentIntent;
	
	private ArrayList<Form> performanceStatusForms=new ArrayList<Form>();
	
	private ArrayList<Form> notIntakeForms=new ArrayList<Form>();
	
	private ArrayList<Form> changeWorkFlowForms=new ArrayList<Form>();
	
	
	
	/*private List<String> onOrAfterOnOrBefore = new ArrayList<String>();*/
	
	public void setup() throws Exception {
		
		setupProperties();
		
		ReportDefinition rd = createReportDefinition();
		
		ReportDesign design = h.createRowPerPatientXlsOverviewReportDesign(rd, "OncologyDataExtractionSheet.xls",
		    "OncologyDataExtractionSheet.xls_", null);
		
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,row:8,dataset:dataset");
		props.put("sortWeight","5000");
		design.setProperties(props);
		
		h.saveReportDesign(design);
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("OncologyDataExtractionSheet.xls_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeReportDefinition("ONC-Oncology Data extraction");
	}
	
	private ReportDefinition createReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("ONC-Oncology Data extraction");
		//reportDefinition.addParameter(new Parameter("startDate", "StartDate", Date.class));		
		reportDefinition.addParameter(new Parameter("endDate", "EndDate", Date.class));
		
		//reportDefinition.addParameter(new Parameter("endDate", "Date", Date.class));
		
		reportDefinition.setBaseCohortDefinition(Cohorts.createInProgram("Oncology", oncologyPrograms), null);
		createDataSetDefinition(reportDefinition);
		
		h.saveReportDefinition(reportDefinition);
		
		return reportDefinition;
	}
	
	private void createDataSetDefinition(ReportDefinition reportDefinition) {
		// Create new dataset definition 
		RowPerPatientDataSetDefinition dataSetDefinition = new RowPerPatientDataSetDefinition();
		dataSetDefinition.setName("dataextraction");
		dataSetDefinition.addParameter(new Parameter("startDate", "StartDate", Date.class));
		
		dataSetDefinition.addParameter(new Parameter("endDate", "EndDate", Date.class));
		
		
		
		
		SortCriteria sortCriteria = new SortCriteria();
		//sortCriteria.addSortElement("nextRDV", SortDirection.ASC);
		sortCriteria.addSortElement("familyName", SortDirection.ASC);
		dataSetDefinition.setSortCriteria(sortCriteria);
		
		
		DateFormatFilter dateFormat=new DateFormatFilter();
		dateFormat.setFinalDateFormat("dd/MM/yyyy");
		
		
		
		//Add filters
		//dataSetDefinition.addFilter(Cohorts.createDateObsCohortDefinition(scheduledVisit, RangeComparator.GREATER_EQUAL, RangeComparator.LESS_EQUAL, TimeModifier.LAST), ParameterizableUtil.createParameterMappings("value2=${endDate},value1=${startDate}"));
		
		//Add Columns
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getPatientHash("researchId"), new HashMap<String, Object>());
        
		dataSetDefinition.addColumn(RowPerPatientColumns.getAge("age"), new HashMap<String, Object>());		
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getGender("sex"), new HashMap<String, Object>());	
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getDateOfBirth("dateofBirth", "dd/MM/yyyy", "dd/MM/yyyy"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getPatientAddress("province", false, true,false,false, false, false), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getPatientAddress("district", false, false,true,false, false, false), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getPatientAddress("sector", false, false,false, true, false, false), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("mutuelle",mutuelle,demoForms,null,null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("mutuelleLevel",mutuelleLevel,demoForms,null,null), new HashMap<String, Object>());
		
		
		
		
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getRecentEncounter("recentencounterdate", intakeForms,null,"dd/MM/yyyy", null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getRecentEncounter("recentencounterLocation", intakeForms,null,null, new LocationEncounterFilter()), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("obsInMostRecentEncounter",typeOfReferrencingToClinicOrHospital,demoForms,null,null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("hivStatus",hivStatus,intakeForms,null,null), new HashMap<String, Object>());
		
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("smokingStatus",smokingHistory,intakeForms,null,null), new HashMap<String, Object>());
		
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("alcoholStatus",alcoholHistory,intakeForms,null,null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("cancerFamilyStatus",cancerFamilyStatus,intakeForms,null,null), new HashMap<String, Object>());
		
		
		//to be reviewed
		//dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("familyMemberWithCancer",familyMemberWithCancer,intakeForms,null,null), new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getRecentEncounter("familyMemberWithCancer", intakeForms,null,null, new ObservationValueCodedInEncounterFilter(familyMemberWithCancer.getId())), new HashMap<String, Object>());
		
		
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("familyMemberWithCancerDiagnosis",familyMemberWithCancerDiagnosis,intakeForms,null,null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("performanceStatus",performanceStatus,intakeForms,null,null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("previousCancerTreatment",previousCancerTreatment,intakeForms,null,null), new HashMap<String, Object>());
		
        dataSetDefinition.addColumn(RowPerPatientColumns.getFirstStateOfPatient("firstDiagnosisStatus", oncologyProgram, diagnosisStatus, null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFirstStateOfPatient("firstDiagnosis", oncologyProgram, diagnosis, null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getStateOfPatientMatchingWithEncounter("encounterMatchdiagnosisStatus", oncologyProgram, diagnosisStatus, changeWorkFlowForms,null), new HashMap<String, Object>());
		
		
		
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getStateOfPatient("diagnosisStatus", oncologyProgram, diagnosisStatus, null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getStateOfPatient("diagnosis", oncologyProgram, diagnosis, null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("dateOfPathologyReport",dateOfPathologyReport,dstForms,null,null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getStateOfPatient("surgeryStatus", oncologyProgram, surgeryStatus, null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getStateOfPatient("chemotherapyStatus", oncologyProgram, chemotherapyStatus, null), new HashMap<String, Object>());
		
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getStateOfPatient("treatmentIntent", oncologyProgram, treatmentIntent, null), new HashMap<String, Object>());
		
		
		
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getStateOfPatient("radiationStatus", oncologyProgram, radiationStatus, null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("erStatus",erStatus,dstForms,null,new DateFormatFilter()), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("her2ihc",her2ihc,dstForms,null,new DateFormatFilter()), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("her2fish",her2fish,dstForms,null,new DateFormatFilter()), new HashMap<String, Object>());
				
		dataSetDefinition.addColumn(RowPerPatientColumns.getRecentEncounterType("lastvisit", allOncologyEncountertypes, null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("lastPerformanceStatus", performanceStatus, "dd/MM/yyyy"), new HashMap<String, Object>());
		
		//dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("cancerProgressionStatus", cancerProgressionStatus, "dd/MM/yyyy"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("cancerProgressionStatus",cancerProgressionStatus,outpatientFlows,null,new DateFormatFilter()), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getRecentEncounter("dateOfVisitSixMonthsPostIntake", intakeForms,null,null, new DateOfVisitSixmonthsPostIntakeFilter(intakeForms,notIntakeForms)), new HashMap<String, Object>());
		
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFirstRecordedObservation("performanceStatusAfterSixMonths", performanceStatus, new ObservationValueNumericAfterSixmonthsFilter(performanceStatusForms)), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFirstRecordedObservation("cancerProgressionStatusAfterSixMonths",cancerProgressionStatus, new ObservationValueCodedAfterSixmonthsFilter()), new HashMap<String, Object>());
		
		//dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("lastexitReason", exitReason, "dd/MM/yyyy"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getRecentEncounter("lastexitReason", exitForms,null,"dd/MM/yyyy", new OncologyExitReasonsInEncounterFilter()), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getDatesOfVisitsByStartDateAndEndDate("datesofAllOncologyVisits", allOncologyEncountertypes, null), ParameterizableUtil.createParameterMappings("startDate=${endDate-12m},endDate=${endDate}"));
		
		
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("endDate", "${endDate}");
		mappings.put("startDate", "${startDate}");
		
		reportDefinition.addDataSetDefinition("dataset", dataSetDefinition, mappings);
		
	}
	
	private void setupProperties() {
		
		oncologyProgram = gp.getProgram(GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		oncologyPrograms.add(oncologyProgram);
		
		intakeInPatient=gp.getEncounterType(GlobalPropertiesManagement.INPATIENT_ONCOLOGY_ENCOUNTER);
		
		intakeOutPatient=gp.getEncounterType(GlobalPropertiesManagement.OUTPATIENT_ONCOLOGY_ENCOUNTER);
		
		intakeEncountertypes.add(intakeInPatient);
		
		intakeEncountertypes.add(intakeOutPatient);
		
		nonClinical=gp.getEncounterType(GlobalPropertiesManagement.NON_CLINICAL_ONCOLOGY_ENCOUNTER);
		
		typeOfReferrencingToClinicOrHospital=gp.getConcept(GlobalPropertiesManagement.TYPE_OF_REFERRING_CLINIC_OR_HOSPITAL);
		
		nonClinicalEncountertypes.add(nonClinical);
		
		demoForm=gp.getForm(GlobalPropertiesManagement.ONCOLOGY_DEMO_FORM);
		
		demoForms.add(demoForm);		
		
		intakeForms.add(gp.getForm(GlobalPropertiesManagement.ONCOLOGY_INTAKE_INPATIENT_FORM));
		
		intakeForms.add(gp.getForm(GlobalPropertiesManagement.ONCOLOGY_INTAKE_OUTPATIENT_FORM));
		
		hivStatus=gp.getConcept(GlobalPropertiesManagement.HIV_STATUS);
		
		diagnosis = gp.getProgramWorkflow(GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW, GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		diagnosisStatus = gp.getProgramWorkflow(GlobalPropertiesManagement.ONCOLOGY_DIAGNOSIS_STATUS_PROGRAM_WORKFLOW, GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		
		//allOncologyEncountertypes.add(nonClinical);
		
		allOncologyEncountertypes.add(intakeInPatient);
		
		allOncologyEncountertypes.add(intakeOutPatient);
		/* scheduledVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_SCHEDULED_OUTPATIENT_VISIT);
		
		biopsyResultVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_PATHOLOGY_RESULT_VISIT);
		
		specialVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_SPECIAL_VISIT);
				
		*/
		
		
		smokingHistory=gp.getConcept(GlobalPropertiesManagement.SMOKING_HISTORY);			
		
		alcoholHistory=gp.getConcept(GlobalPropertiesManagement.ALCOHOOL_HISTORY);		
		
		cancerFamilyStatus = gp.getConcept(GlobalPropertiesManagement.FAMILY_MEMBER_WITH_CANCER_STATUS);			
			
		familyMemberWithCancer = gp.getConcept(GlobalPropertiesManagement.FAMILY_MEMBER_WITH_CANCER);		
			
		familyMemberWithCancerDiagnosis= gp.getConcept(GlobalPropertiesManagement.FAMILY_MEMBER_WITH_CANCER_DIAGNOSIS);			
			
		performanceStatus= gp.getConcept(GlobalPropertiesManagement.PERFORMANCE_STATUS);
		
			
		previousCancerTreatment= gp.getConcept(GlobalPropertiesManagement.PREVIOUS_CANCER_TREATMENT);
		
		dstForms.add(gp.getForm(GlobalPropertiesManagement.DST_FORM));
		
		dateOfPathologyReport=gp.getConcept(GlobalPropertiesManagement.DATE_OF_PATHOLOGY_REPORT);
			
		surgeryStatus = gp.getProgramWorkflow(GlobalPropertiesManagement.ONCOLOGY_SURGERY_STATUS_PROGRAM_WORKFLOW, GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
			
		chemotherapyStatus = gp.getProgramWorkflow(GlobalPropertiesManagement.ONCOLOGY_CHEMO_STATUS_PROGRAM_WORKFLOW, GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
				
		radiationStatus = gp.getProgramWorkflow(GlobalPropertiesManagement.ONCOLOGY_RADIATION_STATUS_PROGRAM_WORKFLOW, GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		treatmentIntent=gp.getProgramWorkflow(GlobalPropertiesManagement.ONCOLOGY_TREATMENT_INTENT_PROGRAM_WORKFLOW, GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		
		
		erStatus=gp.getConcept(GlobalPropertiesManagement.ER_STATUS);			
				
		her2ihc =gp.getConcept(GlobalPropertiesManagement.HER2_IHC);	
		
		her2fish = gp.getConcept(GlobalPropertiesManagement.HER2_FISH);
		
//to fix in the structure of rwandareports module
		
		cancerProgressionStatus=gp.getConcept(GlobalPropertiesManagement.CANCER_PROGRESSION_STATUS);
			
			//Context.getConceptService().getConceptByUuid("05841055-295e-4313-ad3b-64b13be4118e");
		
		Form outpatientFlowClinicVisit=gp.getForm(GlobalPropertiesManagement.OUTPATIENT_CLINIC_VISITS_FORM);
			
			//Context.getFormService().getForm("Oncology - Outpatient Flow: Outpatient Clinic visits");
		
		outpatientFlows.add(outpatientFlowClinicVisit);
		
		performanceStatusForms.add(outpatientFlowClinicVisit);
		performanceStatusForms.add(gp.getForm(GlobalPropertiesManagement.ONCOLOGY_INTAKE_INPATIENT_FORM));
		performanceStatusForms.add(gp.getForm(GlobalPropertiesManagement.ONCOLOGY_INTAKE_OUTPATIENT_FORM));
		
		notIntakeForms.add(outpatientFlowClinicVisit);
		notIntakeForms.add(gp.getForm(GlobalPropertiesManagement.DST_FORM));
		
		changeWorkFlowForms.add(gp.getForm(GlobalPropertiesManagement.DST_FORM));
		
		changeWorkFlowForms.add(gp.getForm(GlobalPropertiesManagement.ONCOLOGY_INTAKE_INPATIENT_FORM));
		
		changeWorkFlowForms.add(gp.getForm(GlobalPropertiesManagement.ONCOLOGY_INTAKE_OUTPATIENT_FORM));
		
		
		
		//exitReason=Context.getConceptService().getConceptByUuid("3cde5ef4-26fe-102b-80cb-0017a47871b2");
		
		exitForms.add(gp.getForm(GlobalPropertiesManagement.ONCOLOGY_EXIT_FORM));
		
		//exitForms.add(Context.getFormService().getForm("Oncology - Exit form"));
		
		mutuelle=Context.getConceptService().getConceptByUuid("8da67e73-776c-43f6-9758-79d1f6786db3");

		mutuelleLevel=Context.getConceptService().getConceptByUuid("a9191adf-c999-422d-94e0-14de5f076127");
		
		
	}
}
