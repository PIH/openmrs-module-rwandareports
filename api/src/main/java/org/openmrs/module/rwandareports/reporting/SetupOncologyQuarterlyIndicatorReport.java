package org.openmrs.module.rwandareports.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InverseCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.SqlIndicator;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.SqlEncounterQuery;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rwandareports.dataset.EncounterIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.dataset.LocationHierachyIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.indicator.EncounterIndicator;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.Indicators;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;

public class SetupOncologyQuarterlyIndicatorReport {

	public Helper h = new Helper();

	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();

	// properties
	private Program oncologyProgram;
	private List<Program> oncologyPrograms = new ArrayList<Program>();
	private ProgramWorkflowState suspecteddiagnosisstate;
	private ProgramWorkflowState notCancerdiagnosisstate;
	private ProgramWorkflowState acutelymphoblasticcancer;
	private ProgramWorkflowState breastCancer;
	private ProgramWorkflowState burkittLymphoma;
	private ProgramWorkflowState cancerunkowntype;
	private ProgramWorkflowState cervicalcancer;
	private ProgramWorkflowState chronicmyelogenousleukemia;
	private ProgramWorkflowState colorectalcancer;
	private ProgramWorkflowState headandneckcancer;
	private ProgramWorkflowState hodgkinslymphoma;
	private ProgramWorkflowState karposisarcoma;
	private ProgramWorkflowState largebcelllymphoma;
	private ProgramWorkflowState lungcancerdiagnosis;
	private ProgramWorkflowState metastaticcancer;
	private ProgramWorkflowState multiplemyeloma;
	private ProgramWorkflowState neuphroblastoma;
	private ProgramWorkflowState otherliquidcancer;
	private ProgramWorkflowState othernonhodkinslymphoma;
	private ProgramWorkflowState othersolidcancer;
	private ProgramWorkflowState prostatecancer;
	private ProgramWorkflowState stomachcancer;
	private Form demographicform;
	private Form changeinDemographicForm;
	private Form inpatientOncForm;
	private Form outpatientOncForm;
	private Form outpatientclinicvisitform;
	private Form exitform;
	private List<Form> demographicsAndClinicalforms = new ArrayList<Form>();
	private List<Form> demographicsForms = new ArrayList<Form>();
	private List<Form> onlydemographicsForm = new ArrayList<Form>();
	private List<Form> clinicalIntakeForms = new ArrayList<Form>();
	private List<Form> intakeoutpatientclinicvisitflowform = new ArrayList<Form>();
	private List<Form> exitforms = new ArrayList<Form>();
	private List<String> onOrAfterOnOrBefore = new ArrayList<String>();
	private List<String> startDateEndDate = new ArrayList<String>();
	private Concept socioassistance;
	private Concept foodassistance;
	private Concept schoolassistance;
	private Concept transportassistance;
	private Concept clinicianhomevisit;
	private Concept homeassistance;
	private Concept referralType;
	private Concept healthclinic;
	private Concept districhospital;
	private Concept referralhospital;
	private Concept othernonCoded;
	private Concept notReferred;
	private Concept hivStatus;
	private Concept positiveStatus;
	private Concept locreferralType;
	private Concept outsideofRwanda;
	private Concept insideintakedistrict;
	private Concept outsideintakedistrict;
	private Concept visitType;
    private Concept unscheduledVisitType;
	private Concept oncologyprogramendreason;
	private Concept palliationonlycare;
	//private Concept cancerrelateddeath;
	//private Concept noncancerrelateddeath;
	//private Concept deathreasonunknown;
	private Concept reasonForExitingCare;
    private Concept patientDied;
    private EncounterType outpatientOncEncounterType;
    private EncounterType inpatientOncologyEncounter;
    
	public void setup() throws Exception {

		setUpProperties();

		ReportDefinition rd = new ReportDefinition();
		rd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		rd.addParameter(new Parameter("endDate", "End Date", Date.class));

		Properties properties = new Properties();
		properties.setProperty("hierarchyFields", "countyDistrict:District");
		rd.addParameter(new Parameter("location", "Location",AllLocation.class, properties));

		rd.setName("ONC-Indicator Report-Quarterly");

		rd.addDataSetDefinition(createQuarterlyLocationDataSet(),
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
		
		h.saveReportDefinition(rd);

		ReportDesign design = h.createRowPerPatientXlsOverviewReportDesign(rd,
				"OncologyQuarterlyIndicatorReport.xls",
				"OncologyQuarterlyIndicatorReport", null);
		Properties props = new Properties();
		props.put("repeatingSections",
				"sheet:1,dataset:Encounter Quarterly Data Set");
		props.put("sortWeight", "5000");
		design.setProperties(props);
		h.saveReportDesign(design);

	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("OncologyQuarterlyIndicatorReport".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeReportDefinition("ONC-Indicator Report-Quarterly");

	}

	public LocationHierachyIndicatorDataSetDefinition createQuarterlyLocationDataSet() {

		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
				createQuarterlyEncounterBaseDataSet());
		ldsd.addBaseDefinition(createQuarterlyBaseDataSet());
		ldsd.setName("Encounter Quarterly Data Set");
		ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ldsd.addParameter(new Parameter("location", "District",LocationHierarchy.class));

		return ldsd;
	}

	private EncounterIndicatorDataSetDefinition createQuarterlyEncounterBaseDataSet() {

		EncounterIndicatorDataSetDefinition eidsd = new EncounterIndicatorDataSetDefinition();

		eidsd.setName("eidsd");
		eidsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		eidsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		createQuarterlyIndicators(eidsd);
		return eidsd;
	}

	private void createQuarterlyIndicators(EncounterIndicatorDataSetDefinition dsd) {
		
		//====================================================//
		// A6 Total # of clinic visits in the last quarter   */
		//===================================================/
		 //clinic visits
		SqlEncounterQuery pediOncClinicVisits=new SqlEncounterQuery();
		pediOncClinicVisits.setName("pediOncClinicVisits");
		pediOncClinicVisits.setQuery("SELECT e.encounter_id FROM encounter e, person p WHERE e.encounter_type in ("+outpatientOncEncounterType.getEncounterTypeId()+","+inpatientOncologyEncounter.getEncounterTypeId()+") " +
				"AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND p.person_id = e.patient_id " +
				"AND DATEDIFF(:endDate , p.birthdate) <= 5475 " +
				"AND form_id in ("+outpatientclinicvisitform.getFormId()+","+inpatientOncForm.getFormId()+","+outpatientOncForm.getFormId()+") " +
				"AND e.voided=0 AND p.voided=0 ");
		pediOncClinicVisits.addParameter(new Parameter("startDate", "startDate", Date.class));
		pediOncClinicVisits.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		EncounterIndicator pedOncClinicVisitInd= new EncounterIndicator();
		pedOncClinicVisitInd.setName("pedOncClinicVisitInd");
		pedOncClinicVisitInd.setEncounterQuery(new Mapped<EncounterQuery>(pediOncClinicVisits,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));
        dsd.addColumn(pedOncClinicVisitInd);  
      
        SqlEncounterQuery adultOncVisits=new SqlEncounterQuery();
        adultOncVisits.setName("adultOncVisits");
        adultOncVisits.setQuery("SELECT e.encounter_id FROM encounter e, person p WHERE e.encounter_type in ("+outpatientOncEncounterType.getEncounterTypeId()+","+inpatientOncologyEncounter.getEncounterTypeId()+") AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND p.person_id = e.patient_id AND DATEDIFF(:endDate , p.birthdate) > 5475 AND form_id in ("+outpatientclinicvisitform.getFormId()+","+inpatientOncForm.getFormId()+","+outpatientOncForm.getFormId()+")  AND e.voided=0 AND p.voided=0");
        adultOncVisits.addParameter(new Parameter("startDate", "startDate", Date.class));
        adultOncVisits.addParameter(new Parameter("endDate", "endDate", Date.class));
        
        EncounterIndicator adultOncVisitsIndicator = new EncounterIndicator();
        adultOncVisitsIndicator.setName("adultOncVisitsIndicator");
        adultOncVisitsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(adultOncVisits,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));
        dsd.addColumn(adultOncVisitsIndicator); 
        
        
        //unscheduled clinic visits
        SqlEncounterQuery pediOncVisitsUnsched=new SqlEncounterQuery();
        pediOncVisitsUnsched.setName("pediOncVisitsUnsched");
        pediOncVisitsUnsched.setQuery("SELECT e.encounter_id FROM encounter e, obs o, person p WHERE e.encounter_id=o.encounter_id AND o.concept_id="+visitType.getConceptId()+" AND o.value_coded="+unscheduledVisitType.getConceptId()+" AND e.encounter_type in ("+outpatientOncEncounterType.getEncounterTypeId()+","+inpatientOncologyEncounter.getEncounterTypeId()+") AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND p.person_id = e.patient_id AND DATEDIFF(:endDate , p.birthdate) <= 5475 AND e.voided=0 AND p.voided=0");
        pediOncVisitsUnsched.addParameter(new Parameter("startDate", "startDate", Date.class));
        pediOncVisitsUnsched.addParameter(new Parameter("endDate", "endDate", Date.class));
        
        EncounterIndicator pediOncVisitsUnschInd = new EncounterIndicator();
        pediOncVisitsUnschInd.setName("pediOncVisitsUnschInd");
        pediOncVisitsUnschInd.setEncounterQuery(new Mapped<EncounterQuery>(pediOncVisitsUnsched,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));
        dsd.addColumn(pediOncVisitsUnschInd); 
        
        SqlEncounterQuery adultOncVisitsUnsched=new SqlEncounterQuery();
        adultOncVisitsUnsched.setName("adultOncVisitsUnsched");
        adultOncVisitsUnsched.setQuery("SELECT e.encounter_id FROM encounter e, obs o, person p WHERE e.encounter_id=o.encounter_id AND o.concept_id="+visitType.getConceptId()+" AND o.value_coded="+unscheduledVisitType.getConceptId()+" AND e.encounter_type in ("+outpatientOncEncounterType.getEncounterTypeId()+","+inpatientOncologyEncounter.getEncounterTypeId()+") AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND p.person_id = e.patient_id AND DATEDIFF(:endDate , p.birthdate) > 5475 AND e.voided=0 AND p.voided=0 AND o.voided=0");
        adultOncVisitsUnsched.addParameter(new Parameter("startDate", "startDate", Date.class));
        adultOncVisitsUnsched.addParameter(new Parameter("endDate", "endDate", Date.class));
        
        EncounterIndicator adultOncVisitsUnschedInd = new EncounterIndicator();
        adultOncVisitsUnschedInd.setName("adultOncVisitsUnschedInd");
        adultOncVisitsUnschedInd.setEncounterQuery(new Mapped<EncounterQuery>(adultOncVisitsUnsched,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));
        dsd.addColumn(adultOncVisitsUnschedInd);
        
        	
	}

	// create monthly cohort Data set

	private CohortIndicatorDataSetDefinition createQuarterlyBaseDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Quarterly Cohort Data Set");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		createQuarterlyIndicators(dsd);
		return dsd;
	}

	private void createQuarterlyIndicators(CohortIndicatorDataSetDefinition dsd) {

		// =============================================================== //
		/* Demographics of NEW cases in the last quarter, pedi and adults */
		// ================================================================ //

		AgeCohortDefinition over15Cohort = Cohorts
				.createOver15AgeCohort("ageQD: Over 15");
		AgeCohortDefinition under15Cohort = Cohorts
				.createUnder15AgeCohort("ageQD: Under 15");
		GenderCohortDefinition femaleCohort = Cohorts
				.createFemaleCohortDefinition("femalesDefinition");
		GenderCohortDefinition maleCohort = Cohorts
				.createMaleCohortDefinition("malesDefinition");

		InProgramCohortDefinition inOncologyProgram = Cohorts
				.createInProgramParameterizableByStartEndDate("inOncologyProgram",
						oncologyProgram);

		EncounterCohortDefinition patientWithDemoandIntakeForms = Cohorts.createEncounterBasedOnForms("patientWithDemoandIntakeForms",
				onOrAfterOnOrBefore, demographicsAndClinicalforms);
		
		EncounterCohortDefinition patientWithDemoandFormsOnly = Cohorts.createEncounterBasedOnForms("patientWithDemoForms",
				onOrAfterOnOrBefore, demographicsForms);
		
		EncounterCohortDefinition patientWithDemoOnly=Cohorts.createEncounterBasedOnForms("patientWithDemoOnly",
				onOrAfterOnOrBefore, onlydemographicsForm   );

		CompositionCohortDefinition newCasesunder15withintakeDemoform = new CompositionCohortDefinition();
		newCasesunder15withintakeDemoform.setName("newCasesunder15withintakeDemoform");
		newCasesunder15withintakeDemoform.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		newCasesunder15withintakeDemoform.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		newCasesunder15withintakeDemoform.getSearches().put("1",new Mapped<CohortDefinition>(patientWithDemoandIntakeForms,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newCasesunder15withintakeDemoform.getSearches().put("2",new Mapped<CohortDefinition>(inOncologyProgram,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newCasesunder15withintakeDemoform.getSearches().put("3",new Mapped<CohortDefinition>(under15Cohort, null));
		newCasesunder15withintakeDemoform.setCompositionString("1 AND 2 AND 3");
		
		CompositionCohortDefinition newCasesunder15withDemoformsonly = new CompositionCohortDefinition();
		newCasesunder15withDemoformsonly.setName("newCasesunder15withDemoformsonly");
		newCasesunder15withDemoformsonly.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		newCasesunder15withDemoformsonly.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		newCasesunder15withDemoformsonly.getSearches().put("1",new Mapped<CohortDefinition>(patientWithDemoandFormsOnly,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newCasesunder15withDemoformsonly.getSearches().put("2",new Mapped<CohortDefinition>(inOncologyProgram,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newCasesunder15withDemoformsonly.getSearches().put("3",new Mapped<CohortDefinition>(under15Cohort, null));
		newCasesunder15withDemoformsonly.setCompositionString("1 AND 2 AND 3");
		
		CompositionCohortDefinition newCasesunder15withOneDemoformonly = new CompositionCohortDefinition();
		newCasesunder15withOneDemoformonly.setName("newCasesunder15withOneDemoformonly");
		newCasesunder15withOneDemoformonly.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		newCasesunder15withOneDemoformonly.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		newCasesunder15withOneDemoformonly.getSearches().put("1",new Mapped<CohortDefinition>(patientWithDemoOnly,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newCasesunder15withOneDemoformonly.getSearches().put("2",new Mapped<CohortDefinition>(inOncologyProgram,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newCasesunder15withOneDemoformonly.getSearches().put("3",new Mapped<CohortDefinition>(under15Cohort, null));
		newCasesunder15withOneDemoformonly.setCompositionString("1 AND 2 AND 3");
		
		CompositionCohortDefinition newCasesOver15withOneDemoformonly = new CompositionCohortDefinition();
		newCasesOver15withOneDemoformonly.setName("newCasesOver15withOneDemoformonly");
		newCasesOver15withOneDemoformonly.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		newCasesOver15withOneDemoformonly.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		newCasesOver15withOneDemoformonly.getSearches().put("1",new Mapped<CohortDefinition>(patientWithDemoOnly,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newCasesOver15withOneDemoformonly.getSearches().put("2",new Mapped<CohortDefinition>(inOncologyProgram,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newCasesOver15withOneDemoformonly.getSearches().put("3",new Mapped<CohortDefinition>(over15Cohort, null));
		newCasesOver15withOneDemoformonly.setCompositionString("1 AND 2 AND 3");

		CompositionCohortDefinition newCasesOver15withintakeDemoform = new CompositionCohortDefinition();
		newCasesOver15withintakeDemoform.setName("newCasesOver15witnintadeDemoform");
		newCasesOver15withintakeDemoform.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		newCasesOver15withintakeDemoform.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		newCasesOver15withintakeDemoform.getSearches().put("1",new Mapped<CohortDefinition>(patientWithDemoandIntakeForms,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newCasesOver15withintakeDemoform.getSearches().put("2",new Mapped<CohortDefinition>(inOncologyProgram,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newCasesOver15withintakeDemoform.getSearches().put("3",new Mapped<CohortDefinition>(over15Cohort, null));
		newCasesOver15withintakeDemoform.setCompositionString("1 AND 2 AND 3");
		
		CompositionCohortDefinition newCasesOver15withDemoformsonly = new CompositionCohortDefinition();
		newCasesOver15withDemoformsonly.setName("newCasesOver15withDemoformsonly");
		newCasesOver15withDemoformsonly.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		newCasesOver15withDemoformsonly.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		newCasesOver15withDemoformsonly.getSearches().put("1",new Mapped<CohortDefinition>(patientWithDemoandFormsOnly,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newCasesOver15withDemoformsonly.getSearches().put("2",new Mapped<CohortDefinition>(inOncologyProgram,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newCasesOver15withDemoformsonly.getSearches().put("3",new Mapped<CohortDefinition>(over15Cohort, null));
		newCasesOver15withDemoformsonly.setCompositionString("1 AND 2 AND 3");

		CohortIndicator under15DemoIntakeFormsIndicator = Indicators
				.newCountIndicator(
						"under15DemoIntakeFormsIndicator",
						newCasesunder15withintakeDemoform,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		CohortIndicator over15DemoIntakeFormsIndicator = Indicators
				.newCountIndicator(
						"patientWithDemoandIntakeFormsIndicator",
						newCasesOver15withintakeDemoform,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		// ==========================================================================
		// //
		/*
		 * D1 Demographics of females NEW cases in the last quarter, pedi and
		 * adults
		 */
		// ==========================================================================
		// //

		CompositionCohortDefinition femalesnewCasesunder15 = new CompositionCohortDefinition();
		femalesnewCasesunder15.setName("femalesnewCasesunder15");
		femalesnewCasesunder15.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		femalesnewCasesunder15.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		femalesnewCasesunder15
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newCasesunder15withintakeDemoform,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		femalesnewCasesunder15.getSearches().put("2",
				new Mapped<CohortDefinition>(femaleCohort, null));
		femalesnewCasesunder15.setCompositionString("1 AND 2");

		CompositionCohortDefinition femalesnewCasesOver15 = new CompositionCohortDefinition();
		femalesnewCasesOver15.setName("femalesnewCasesOver15");
		femalesnewCasesOver15.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		femalesnewCasesOver15.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		femalesnewCasesOver15
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newCasesOver15withintakeDemoform,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		femalesnewCasesOver15.getSearches().put("2",
				new Mapped<CohortDefinition>(femaleCohort, null));
		femalesnewCasesOver15.setCompositionString("1 AND 2");

		CohortIndicator newCasesFemalesUnder15Indicator = Indicators
				.newCountIndicator(
						"newCasesFemalesUnder15Indicator",
						femalesnewCasesunder15,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		CohortIndicator newCasesFemalesOver15Indicator = Indicators
				.newCountIndicator(
						"newCasesFemalesOver15Indicator",
						femalesnewCasesOver15,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		 // =========================================================================
		//  D2 Demographics of males NEW cases in the last quarter, pedi and adults
	    // =========================================================================
		
        CompositionCohortDefinition malesnewCasesunder15 = new CompositionCohortDefinition();
		malesnewCasesunder15.setName("malesnewCasesunder15");
		malesnewCasesunder15.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		malesnewCasesunder15.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		malesnewCasesunder15.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		malesnewCasesunder15.getSearches().put("2",new Mapped<CohortDefinition>(maleCohort, null));
		malesnewCasesunder15.setCompositionString("1 AND 2");

		CompositionCohortDefinition malesnewCasesOver15 = new CompositionCohortDefinition();
		malesnewCasesOver15.setName("malesnewCasesOver15");
		malesnewCasesOver15.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		malesnewCasesOver15.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		malesnewCasesOver15
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newCasesOver15withintakeDemoform,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		malesnewCasesOver15.getSearches().put("2",
				new Mapped<CohortDefinition>(maleCohort, null));
		malesnewCasesOver15.setCompositionString("1 AND 2");

		CohortIndicator newCasesmalesUnder15Indicator = Indicators
				.newCountIndicator(
						"newCasesmalesUnder15Indicator",
						malesnewCasesunder15,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		CohortIndicator newCasesmalesOver15Indicator = Indicators
				.newCountIndicator(
						"newCasesmalesOver15Indicator",
						malesnewCasesOver15,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		// =======================================================================
		// D5 % of new cases recommended for socioeconomic assistance at intake
		// ======================================================================
		
        SqlCohortDefinition foodAssistancerec = Cohorts.getPatientWithProgramAndConcept("foodAssistancerec",oncologyProgram, socioassistance, foodassistance);
		SqlCohortDefinition transportAssistancerec = Cohorts.getPatientWithProgramAndConcept("transportAssistancerec",oncologyProgram, socioassistance, transportassistance);
		SqlCohortDefinition clinicianhomeVisit=Cohorts.getPatientWithProgramAndConcept("clinicianhomeVisit", oncologyProgram, socioassistance, clinicianhomevisit);
		SqlCohortDefinition schoolAssistancerec = Cohorts.getPatientWithProgramAndConcept("schoolAssistancerec",oncologyProgram, socioassistance, schoolassistance);
		SqlCohortDefinition homeAssistancerec = Cohorts.getPatientWithProgramAndConcept("homeAssistancerec",oncologyProgram, socioassistance, homeassistance);
		SqlCohortDefinition otherAssistancerec = Cohorts.getPatientWithProgramAndConcept("otherAssistancerec",oncologyProgram, socioassistance, othernonCoded);
		
		CompositionCohortDefinition patientsWithsocioeconomicassistancerecmended = new CompositionCohortDefinition();
		patientsWithsocioeconomicassistancerecmended.setName("patientsWithsocioeconomicassistancerecmended");
		patientsWithsocioeconomicassistancerecmended.getSearches().put("1",new Mapped<CohortDefinition>(foodAssistancerec, null));
		patientsWithsocioeconomicassistancerecmended.getSearches().put("2",new Mapped<CohortDefinition>(transportAssistancerec, null));
		patientsWithsocioeconomicassistancerecmended.getSearches().put("3",new Mapped<CohortDefinition>(clinicianhomeVisit, null));
		patientsWithsocioeconomicassistancerecmended.getSearches().put("4",new Mapped<CohortDefinition>(schoolAssistancerec, null));
		patientsWithsocioeconomicassistancerecmended.getSearches().put("5",new Mapped<CohortDefinition>(homeAssistancerec, null));
		patientsWithsocioeconomicassistancerecmended.getSearches().put("6",new Mapped<CohortDefinition>(otherAssistancerec, null));
		patientsWithsocioeconomicassistancerecmended.setCompositionString("1 OR 2 OR 3 OR 4 OR 5 OR 6");
		//pedi with socio economic recommended
		CompositionCohortDefinition pediWithSocioassistanceRecommended= new CompositionCohortDefinition();
		pediWithSocioassistanceRecommended.setName("pediWithSocioassistanceRecommended15");
		pediWithSocioassistanceRecommended.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		pediWithSocioassistanceRecommended.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		pediWithSocioassistanceRecommended.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withDemoformsonly,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pediWithSocioassistanceRecommended.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithsocioeconomicassistancerecmended, null));
		pediWithSocioassistanceRecommended.setCompositionString("1 AND 2");
		
		//adults with socio economic recommended
		CompositionCohortDefinition adultsWithSocioassistanceRec = new CompositionCohortDefinition();
		adultsWithSocioassistanceRec.setName("adultsWithSocioassistanceRec");
		adultsWithSocioassistanceRec.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		adultsWithSocioassistanceRec.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		adultsWithSocioassistanceRec.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withDemoformsonly,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsWithSocioassistanceRec.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithsocioeconomicassistancerecmended, null));
		adultsWithSocioassistanceRec.setCompositionString("1 AND 2");
		
		//pedi with missing socio economic recommended
		CompositionCohortDefinition pedWithMissingSocioassistanceRec = new CompositionCohortDefinition();
		pedWithMissingSocioassistanceRec.setName("pedWithMissingSocioassistanceRec");
		pedWithMissingSocioassistanceRec.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		pedWithMissingSocioassistanceRec.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		pedWithMissingSocioassistanceRec.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pedWithMissingSocioassistanceRec.getSearches().put("2",new Mapped<CohortDefinition>(pediWithSocioassistanceRecommended, 
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pedWithMissingSocioassistanceRec.setCompositionString("1 AND (NOT 2)");
		
		//adults with missing socio economic recommended
		CompositionCohortDefinition adultsWithMissingSocioassistanceRec= new CompositionCohortDefinition();
		adultsWithMissingSocioassistanceRec.setName("adultsWithMissingSocioassistanceRecover");
		adultsWithMissingSocioassistanceRec.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		adultsWithMissingSocioassistanceRec.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		adultsWithMissingSocioassistanceRec.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withintakeDemoform,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsWithMissingSocioassistanceRec.getSearches().put("2",new Mapped<CohortDefinition>(adultsWithSocioassistanceRec, 
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsWithMissingSocioassistanceRec.setCompositionString("1 AND (NOT 2)");

		CohortIndicator pediWithsocioeconomicrecomendationIndicator = Indicators.newCountIndicator("pediWithsocioeconomicrecomendationIndicator",
				pediWithSocioassistanceRecommended,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		CohortIndicator adultsWithSocioassistanceRecIndicator = Indicators.newCountIndicator("adultsWithSocioassistanceRecIndicator",
				adultsWithSocioassistanceRec,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		CohortIndicator pediWithMissingsocioeconomicrecIndicator = Indicators.newCountIndicator("pediWithMissingsocioeconomicrecIndicator",
				pedWithMissingSocioassistanceRec,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		CohortIndicator adultsWithMissingSocioassistanceRecIndicator = Indicators.newCountIndicator("adultsWithMissingSocioassistanceRecIndicator",
				adultsWithMissingSocioassistanceRec,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		// ================================================================== //
		// D6 % breakdown of new cases' referral facility type at intake */
		// ================================================================ //
		// Health center referral facility type
		CodedObsCohortDefinition healthCenter = Cohorts.createCodedObsCohortDefinition("healthCenter", onOrAfterOnOrBefore,referralType, healthclinic, SetComparator.IN,TimeModifier.LAST);
		CodedObsCohortDefinition fordistrictHospital = Cohorts.createCodedObsCohortDefinition("fordistrictHospital",onOrAfterOnOrBefore, referralType, districhospital,SetComparator.IN, TimeModifier.LAST);
		CodedObsCohortDefinition forReferralHospital = Cohorts.createCodedObsCohortDefinition("referredforReferralHospital",onOrAfterOnOrBefore, referralType, referralhospital,SetComparator.IN, TimeModifier.LAST);
		CodedObsCohortDefinition withOtheReferral = Cohorts.createCodedObsCohortDefinition("withOtheReferral",onOrAfterOnOrBefore, referralType, othernonCoded,SetComparator.IN, TimeModifier.LAST);
		CodedObsCohortDefinition patsnotReferred = Cohorts.createCodedObsCohortDefinition("patsnotReferred",onOrAfterOnOrBefore, referralType, notReferred,SetComparator.IN, TimeModifier.LAST);
        
		CompositionCohortDefinition pedPatientsreferredForHC = new CompositionCohortDefinition();
		pedPatientsreferredForHC.setName("pedPatientsreferredForHC");
		pedPatientsreferredForHC.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		pedPatientsreferredForHC.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		pedPatientsreferredForHC.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withOneDemoformonly,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pedPatientsreferredForHC.getSearches().put("2",new Mapped<CohortDefinition>(healthCenter, 
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pedPatientsreferredForHC.setCompositionString("1 AND 2");
        
		CompositionCohortDefinition adultsreferredforHC = new CompositionCohortDefinition();
		adultsreferredforHC.setName("adultsreferredforHC");
		adultsreferredforHC.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		adultsreferredforHC.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		adultsreferredforHC.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withOneDemoformonly,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsreferredforHC.getSearches().put("2",new Mapped<CohortDefinition>(healthCenter, 
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsreferredforHC.setCompositionString("1 AND 2");

        // district hospital
		CompositionCohortDefinition pedsreferredForDistrictHospital = new CompositionCohortDefinition();
		pedsreferredForDistrictHospital.setName("pedsreferredForDistrictHospital");
		pedsreferredForDistrictHospital.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		pedsreferredForDistrictHospital.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		pedsreferredForDistrictHospital.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withOneDemoformonly,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pedsreferredForDistrictHospital.getSearches().put("2",new Mapped<CohortDefinition>(fordistrictHospital, 
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pedsreferredForDistrictHospital.setCompositionString("1 AND 2");
        
		CompositionCohortDefinition adultsreferredToDistriHospital=new CompositionCohortDefinition();
		adultsreferredToDistriHospital.setName("adultsreferredToDistriHospital");
		adultsreferredToDistriHospital.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		adultsreferredToDistriHospital.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		adultsreferredToDistriHospital.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withOneDemoformonly,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsreferredToDistriHospital.getSearches().put("2",new Mapped<CohortDefinition>(fordistrictHospital,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsreferredToDistriHospital.setCompositionString("1 AND 2");
		
		//referral hospital
		CompositionCohortDefinition pedsreferredforReferralHospital = new CompositionCohortDefinition();
		pedsreferredforReferralHospital.setName("pedsreferredforReferralHospital");
		pedsreferredforReferralHospital.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		pedsreferredforReferralHospital.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		pedsreferredforReferralHospital.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withOneDemoformonly,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pedsreferredforReferralHospital.getSearches().put("2",new Mapped<CohortDefinition>(forReferralHospital,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pedsreferredforReferralHospital.setCompositionString("1 AND 2");
        
		CompositionCohortDefinition adultsreferredforReferralHospital = new CompositionCohortDefinition();
		adultsreferredforReferralHospital.setName("adultsreferredforReferralHospital");
		adultsreferredforReferralHospital.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		adultsreferredforReferralHospital.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		adultsreferredforReferralHospital.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withOneDemoformonly,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsreferredforReferralHospital.getSearches().put("2",new Mapped<CohortDefinition>(forReferralHospital, 
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsreferredforReferralHospital.setCompositionString("1 AND 2");
		
		// others
		CompositionCohortDefinition pedsWithOtherReferral = new CompositionCohortDefinition();
		pedsWithOtherReferral.setName("pedsWithOtherReferral");
		pedsWithOtherReferral.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		pedsWithOtherReferral.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		pedsWithOtherReferral.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withOneDemoformonly,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pedsWithOtherReferral.getSearches().put("2",new Mapped<CohortDefinition>(withOtheReferral, null));
		pedsWithOtherReferral.setCompositionString("1 AND 2");
        
		CompositionCohortDefinition adultsWithOtherReferral = new CompositionCohortDefinition();
		adultsWithOtherReferral.setName("adultsWithOtherReferral");
		adultsWithOtherReferral.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		adultsWithOtherReferral.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		adultsWithOtherReferral.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withOneDemoformonly,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsWithOtherReferral.getSearches().put("2",new Mapped<CohortDefinition>(withOtheReferral, null));
		adultsWithOtherReferral.setCompositionString("1 AND 2");
		
		//not referred
		CompositionCohortDefinition pedwithNoreferralType = new CompositionCohortDefinition();
		pedwithNoreferralType.setName("pedwithNoreferralType");
		pedwithNoreferralType.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		pedwithNoreferralType.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		pedwithNoreferralType.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withOneDemoformonly,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pedwithNoreferralType.getSearches().put("2",new Mapped<CohortDefinition>(patsnotReferred, 
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pedwithNoreferralType.setCompositionString("1 AND 2");

		CompositionCohortDefinition adultswithNoreferralType = new CompositionCohortDefinition();
		adultswithNoreferralType.setName("adultswithNoreferralType");
		adultswithNoreferralType.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		adultswithNoreferralType.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		adultswithNoreferralType.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withOneDemoformonly,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultswithNoreferralType.getSearches().put("2",new Mapped<CohortDefinition>(patsnotReferred, 
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultswithNoreferralType.setCompositionString("1 AND 2");
		
		//missing references
		CompositionCohortDefinition missingReferences = new CompositionCohortDefinition();
		missingReferences.setName("missingReferences");
		missingReferences.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		missingReferences.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		missingReferences.getSearches().put("1",new Mapped<CohortDefinition>(healthCenter, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		missingReferences.getSearches().put("2",new Mapped<CohortDefinition>(fordistrictHospital, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		missingReferences.getSearches().put("3",new Mapped<CohortDefinition>(forReferralHospital, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		missingReferences.getSearches().put("4",new Mapped<CohortDefinition>(withOtheReferral, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		missingReferences.getSearches().put("5",new Mapped<CohortDefinition>(patsnotReferred, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		missingReferences.setCompositionString("1 OR 2 OR 3 OR 4 OR 5");
		
		CompositionCohortDefinition pediWithMissingReferences = new CompositionCohortDefinition();
		pediWithMissingReferences.setName("pediWithMissingReferences");
		pediWithMissingReferences.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		pediWithMissingReferences.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		pediWithMissingReferences.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pediWithMissingReferences.getSearches().put("2",new Mapped<CohortDefinition>(missingReferences, 
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pediWithMissingReferences.setCompositionString("1 AND (NOT 2)");
		
		CompositionCohortDefinition adultsWithMissingReferences = new CompositionCohortDefinition();
		adultsWithMissingReferences.setName("adultsWithMissingReferences");
		adultsWithMissingReferences.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		adultsWithMissingReferences.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		adultsWithMissingReferences.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsWithMissingReferences.getSearches().put("2",new Mapped<CohortDefinition>(missingReferences, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsWithMissingReferences.setCompositionString("1 AND (NOT 2)");
		
		CohortIndicator pedPatientsreferredForHCIndicator= Indicators.newCountIndicator("pedPatientsreferredForHCIndicator",
				pedPatientsreferredForHC,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator adultsPatientsreferredForHCIndicator = Indicators.newCountIndicator("adultsPatientsreferredForHCIndicator",
				adultsreferredforHC,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator pedsreferredForDistrictHospitalIndicator= Indicators.newCountIndicator("pedsreferredForDistrictHospitalIndicator",
				pedsreferredForDistrictHospital,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator adultsreferreToDistrictHospitalIndicator = Indicators.newCountIndicator("adultsreferreToDistrictHospitalIndicator",
				adultsreferredToDistriHospital,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator pedsreferredforReferralHospitalIndicator= Indicators.newCountIndicator("pedsreferredforReferralHospitalIndicator",
				pedsreferredforReferralHospital,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator adultsreferredforReferralHospitalIndicator = Indicators.newCountIndicator("adultsreferredforReferralHospitalIndicator",
				adultsreferredforReferralHospital,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator pedsWithOtherReferralIndicator= Indicators.newCountIndicator("pedsWithOtherReferralIndicator",
				pedsWithOtherReferral,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator adultsWithOtherReferralIndicator = Indicators.newCountIndicator("adultsWithOtherReferralIndicator",
				adultsWithOtherReferral,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator pedwithNoreferralTypeIndicator= Indicators.newCountIndicator("pedwithNoreferralTypeIndicator",
				pedwithNoreferralType,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator adultswithreferralTypeIndicator = Indicators.newCountIndicator("adultswithreferralTypeIndicator",
				adultswithNoreferralType,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		CohortIndicator pediWithMissingReferencesIndi= Indicators.newCountIndicator("pediWithMissingReferencesIndi",
				pediWithMissingReferences,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator adultsWithMissingReferencesIndi = Indicators.newCountIndicator("adultsWithMissingReferencesIndi",
				adultsWithMissingReferences,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
				
		// ==============================================================================
		// D7 % of new referral cases referred from outside district of care at intake */
		// ==============================================================================//
		CodedObsCohortDefinition insideIntakeDistrictinRwanda = Cohorts.createCodedObsCohortDefinition("insideIntakeDistrictinRwanda",onOrAfterOnOrBefore, locreferralType,insideintakedistrict, SetComparator.IN,TimeModifier.LAST);
		CodedObsCohortDefinition outsideIntakeDistrict = Cohorts.createCodedObsCohortDefinition("patWithLocreferredOutsideRwanda", onOrAfterOnOrBefore,locreferralType, outsideintakedistrict,SetComparator.IN, TimeModifier.LAST);
		CodedObsCohortDefinition outsideRwanda = Cohorts.createCodedObsCohortDefinition("patWithLocreferredOutsideRwanda", onOrAfterOnOrBefore,locreferralType, outsideofRwanda, SetComparator.IN,TimeModifier.LAST);
		
		// Numerators:  total new cases
		CompositionCohortDefinition patientsReferredInRwandaAndInsideOusideIntake = new CompositionCohortDefinition();
		patientsReferredInRwandaAndInsideOusideIntake.setName("patientsReferredInRwandaAndInsideOusideIntake");
		patientsReferredInRwandaAndInsideOusideIntake.getSearches().put("1",new Mapped<CohortDefinition>(insideIntakeDistrictinRwanda, null));
		patientsReferredInRwandaAndInsideOusideIntake.getSearches().put("2",new Mapped<CohortDefinition>(outsideIntakeDistrict, null));
		patientsReferredInRwandaAndInsideOusideIntake.getSearches().put("3",new Mapped<CohortDefinition>(outsideRwanda,null));
		patientsReferredInRwandaAndInsideOusideIntake.setCompositionString("1 OR 2 OR 3");
		
		CompositionCohortDefinition totalpedinewCasesThatareReferred = new CompositionCohortDefinition();
		totalpedinewCasesThatareReferred.setName("totalpedinewCasesThatareReferred");
		totalpedinewCasesThatareReferred.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		totalpedinewCasesThatareReferred.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		totalpedinewCasesThatareReferred.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
                        ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		totalpedinewCasesThatareReferred.getSearches().put("2",new Mapped<CohortDefinition>(patientsReferredInRwandaAndInsideOusideIntake, null));
		totalpedinewCasesThatareReferred.setCompositionString("1 AND 2");
        
        CompositionCohortDefinition totaladultsnewCasesThatareReferred = new CompositionCohortDefinition();
        totaladultsnewCasesThatareReferred.setName("totalnewCasesThatareReferred");
        totaladultsnewCasesThatareReferred.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
        totaladultsnewCasesThatareReferred.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
        totaladultsnewCasesThatareReferred.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withintakeDemoform,
                        ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
        totaladultsnewCasesThatareReferred.getSearches().put("2",new Mapped<CohortDefinition>(patientsReferredInRwandaAndInsideOusideIntake, null));
        totaladultsnewCasesThatareReferred.setCompositionString("1 AND 2");
        
		//refered inside Rwanda District
		CompositionCohortDefinition pedireferredinsideRwandanDistrict=new CompositionCohortDefinition();
		pedireferredinsideRwandanDistrict.setName("pedireferredinsideRwandanDistrict");
		pedireferredinsideRwandanDistrict.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		pedireferredinsideRwandanDistrict.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		pedireferredinsideRwandanDistrict.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withOneDemoformonly,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pedireferredinsideRwandanDistrict.getSearches().put("2",new Mapped<CohortDefinition>(insideIntakeDistrictinRwanda, null));
		pedireferredinsideRwandanDistrict.setCompositionString("1 AND 2");
		
		CompositionCohortDefinition adultreferredinsideRwandanDistrict=new CompositionCohortDefinition();
		adultreferredinsideRwandanDistrict.setName("adultreferredinsideRwandanDistrict");
		adultreferredinsideRwandanDistrict.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		adultreferredinsideRwandanDistrict.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		adultreferredinsideRwandanDistrict.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withOneDemoformonly,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultreferredinsideRwandanDistrict.getSearches().put("2",new Mapped<CohortDefinition>(insideIntakeDistrictinRwanda, null));
		adultreferredinsideRwandanDistrict.setCompositionString("1 AND 2");
		
		//outside intake district within Rwanda.
		CompositionCohortDefinition pedireferredoutsideIntakedistrictInRwanda=new CompositionCohortDefinition();
		pedireferredoutsideIntakedistrictInRwanda.setName("pedireferredoutsideIntakedistrictInRwanda");
		pedireferredoutsideIntakedistrictInRwanda.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		pedireferredoutsideIntakedistrictInRwanda.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		pedireferredoutsideIntakedistrictInRwanda.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withOneDemoformonly,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pedireferredoutsideIntakedistrictInRwanda.getSearches().put("2",new Mapped<CohortDefinition>(outsideIntakeDistrict, null));
		pedireferredoutsideIntakedistrictInRwanda.setCompositionString("1 AND 2");
		
		CompositionCohortDefinition adultsreferredoutsideIntakedistrictInRwanda=new CompositionCohortDefinition();
		adultsreferredoutsideIntakedistrictInRwanda.setName("adultsreferredoutsideIntakedistrictInRwanda");
		adultsreferredoutsideIntakedistrictInRwanda.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		adultsreferredoutsideIntakedistrictInRwanda.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		adultsreferredoutsideIntakedistrictInRwanda.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withOneDemoformonly,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsreferredoutsideIntakedistrictInRwanda.getSearches().put("2",new Mapped<CohortDefinition>(outsideIntakeDistrict, null));
		adultsreferredoutsideIntakedistrictInRwanda.setCompositionString("1 AND 2");
		
		//outside Rwanda.
		CompositionCohortDefinition pedireferredOutsideOfRwanda=new CompositionCohortDefinition();
		pedireferredOutsideOfRwanda.setName("pedireferredOutsideOfRwanda");
		pedireferredOutsideOfRwanda.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		pedireferredOutsideOfRwanda.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		pedireferredOutsideOfRwanda.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withOneDemoformonly,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pedireferredOutsideOfRwanda.getSearches().put("2",new Mapped<CohortDefinition>(outsideRwanda, null));
		pedireferredOutsideOfRwanda.setCompositionString("1 AND 2");
		
		CompositionCohortDefinition adultsreferredOutsideOfRwanda=new CompositionCohortDefinition();
		adultsreferredOutsideOfRwanda.setName("adultsreferredOutsideOfRwanda");
		adultsreferredOutsideOfRwanda.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		adultsreferredOutsideOfRwanda.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		adultsreferredOutsideOfRwanda.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withOneDemoformonly,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsreferredOutsideOfRwanda.getSearches().put("2",new Mapped<CohortDefinition>(outsideRwanda, null));
		adultsreferredOutsideOfRwanda.setCompositionString("1 AND 2");
		
		// Denominator
		CohortIndicator totalpedinewCasesThatareReferredIndicators=Indicators.newCountIndicator("totalnewCasesThatareReferredIndicator",
				totalpedinewCasesThatareReferred,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		CohortIndicator totaladultsnewCasesThatareReferredIndicators=Indicators.newCountIndicator("totalnewCasesThatareReferredIndicator",
				totaladultsnewCasesThatareReferred,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		// Numerator
		CohortIndicator pedireferredinsideRwandanDistrictIndi = Indicators.newCountIndicator("referredinsideRwandanDistrictIndi",
				pedireferredinsideRwandanDistrict,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
        
		CohortIndicator adultsrefferedinsideRwandaDistrictInd=Indicators.newCohortIndicator("adultreferredinsideRwandanDistrict", 
				adultreferredinsideRwandanDistrict, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
				
		CohortIndicator pedireferredoutsideIntakedistrictInRwandaIndi = Indicators.newCountIndicator("pedireferredoutsideIntakedistrictInRwandaIndi",
				pedireferredoutsideIntakedistrictInRwanda,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		CohortIndicator adultsreferredoutsideIntakedistrictInRwandaIndi = Indicators.newCountIndicator("adultsreferredoutsideIntakedistrictInRwandaIndi",
				adultsreferredoutsideIntakedistrictInRwanda,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator pedireferredOutsideOfRwandaIndicator = Indicators.newCountIndicator("pedireferredOutsideOfRwandaIndicator",
			pedireferredOutsideOfRwanda,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		CohortIndicator adultsreferredOutsideOfRwandaIndicator = Indicators.newCountIndicator("adultsreferredOutsideOfRwandaIndicator",
				adultsreferredOutsideOfRwanda,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		// ======================================================= //
		// D8 % breakdown of new cases' home district at intake */
		// ======================================================= //

		// peds; GIS map; Inside home district:
		// 1) Kayonza,
		SqlCohortDefinition patientsLocatedInKayonzaDistrict = Cohorts.getPatientWithStructuredDistrict("patientsLocatedInKireheDistrict", "Kayonza");
		SqlCohortDefinition patientsLocatedInBureraDistrict = Cohorts.getPatientWithStructuredDistrict("patientsLocatedInKireheDistrict", "Burera");
		SqlCohortDefinition patientsLocatedInKireheDistrict = Cohorts.getPatientWithStructuredDistrict("patientsLocatedInKireheDistrict", "Kirehe");
		SqlCohortDefinition patientsLocatedInNyarugengeDistrict = Cohorts.getPatientWithStructuredDistrict("patientsLocatedInNyarugengeDistrict", "Nyarugenge");
		SqlCohortDefinition patientsLocatedInKicukiroDistrict = Cohorts.getPatientWithStructuredDistrict("patientsLocatedInKicukiroDistrict", "Kicukiro");
		SqlCohortDefinition patientsLocatedInGasaboDistrict = Cohorts.getPatientWithStructuredDistrict("patientsLocatedInGasaboDistrict", "Gasabo");
		SqlCohortDefinition patientwithUnstructuredDistrict = Cohorts.getPatientWithunStructuredDistrict("patientwithUnstructuredDistrict");

		CompositionCohortDefinition pediLocatedInKayonzaDistrictComposition = new CompositionCohortDefinition();
		pediLocatedInKayonzaDistrictComposition.setName("pediLocatedInKayonzaDistrictComposition");
		pediLocatedInKayonzaDistrictComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		pediLocatedInKayonzaDistrictComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		pediLocatedInKayonzaDistrictComposition.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pediLocatedInKayonzaDistrictComposition.getSearches().put("2",new Mapped<CohortDefinition>(patientsLocatedInKayonzaDistrict,null));
		pediLocatedInKayonzaDistrictComposition.setCompositionString("1 AND 2");

		CompositionCohortDefinition adultsLocatedInKayonzaDistrictComposition = new CompositionCohortDefinition();
		adultsLocatedInKayonzaDistrictComposition.setName("adultsLocatedInKayonzaDistrictComposition");
		adultsLocatedInKayonzaDistrictComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		adultsLocatedInKayonzaDistrictComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		adultsLocatedInKayonzaDistrictComposition.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withintakeDemoform,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsLocatedInKayonzaDistrictComposition.getSearches().put("2",new Mapped<CohortDefinition>(patientsLocatedInKayonzaDistrict,null));
		adultsLocatedInKayonzaDistrictComposition.setCompositionString("1 AND 2");

		// 2) Burera,
		CompositionCohortDefinition pediLocatedInBureraDistrictComposition = new CompositionCohortDefinition();
		pediLocatedInBureraDistrictComposition.setName("pediLocatedInBureraDistrictComposition");
		pediLocatedInBureraDistrictComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		pediLocatedInBureraDistrictComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		pediLocatedInBureraDistrictComposition.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pediLocatedInBureraDistrictComposition.getSearches().put("2",new Mapped<CohortDefinition>(patientsLocatedInBureraDistrict,null));
		pediLocatedInBureraDistrictComposition.setCompositionString("1 AND 2");
		
		CompositionCohortDefinition adultsLocatedInBureraDistrictComposition = new CompositionCohortDefinition();
		adultsLocatedInBureraDistrictComposition.setName("adultsLocatedInBureraDistrictComposition");
		adultsLocatedInBureraDistrictComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		adultsLocatedInBureraDistrictComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		adultsLocatedInBureraDistrictComposition.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsLocatedInBureraDistrictComposition.getSearches().put("2",new Mapped<CohortDefinition>(patientsLocatedInBureraDistrict,null));
		adultsLocatedInBureraDistrictComposition.setCompositionString("1 AND 2");

		// 3) Kirehe,
		CompositionCohortDefinition pediLocatedInKireheDistrictComposition = new CompositionCohortDefinition();
		pediLocatedInKireheDistrictComposition.setName("pediLocatedInKireheDistrictComposition");
		pediLocatedInKireheDistrictComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		pediLocatedInKireheDistrictComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		pediLocatedInKireheDistrictComposition.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pediLocatedInKireheDistrictComposition.getSearches().put("2",new Mapped<CohortDefinition>(patientsLocatedInKireheDistrict,null));
		pediLocatedInKireheDistrictComposition.setCompositionString("1 AND 2");
		
		CompositionCohortDefinition adultsLocatedInKireheDistrictComposition = new CompositionCohortDefinition();
		adultsLocatedInKireheDistrictComposition.setName("adultsLocatedInKireheDistrictComposition");
		adultsLocatedInKireheDistrictComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		adultsLocatedInKireheDistrictComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		adultsLocatedInKireheDistrictComposition.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withintakeDemoform,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsLocatedInKireheDistrictComposition.getSearches().put("2",new Mapped<CohortDefinition>(patientsLocatedInKireheDistrict,null));
		adultsLocatedInKireheDistrictComposition.setCompositionString("1 AND 2");

		// 4) Kigali,
		
		CompositionCohortDefinition KigaliDistricts = new CompositionCohortDefinition();
		KigaliDistricts.setName("KigaliDistricts");
		KigaliDistricts.getSearches().put("1",new Mapped<CohortDefinition>(patientsLocatedInNyarugengeDistrict, null));
		KigaliDistricts.getSearches().put("2",new Mapped<CohortDefinition>(patientsLocatedInKicukiroDistrict,null));
		KigaliDistricts.getSearches().put("3",new Mapped<CohortDefinition>(patientsLocatedInGasaboDistrict,null));
		KigaliDistricts.setCompositionString("1 OR 2 OR 3");
		
		CompositionCohortDefinition pediLocatedInKigaliDistrictComposition = new CompositionCohortDefinition();
		pediLocatedInKigaliDistrictComposition.setName("pediLocatedInKigaliDistrictComposition");
		pediLocatedInKigaliDistrictComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		pediLocatedInKigaliDistrictComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		pediLocatedInKigaliDistrictComposition.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pediLocatedInKigaliDistrictComposition.getSearches().put("2",new Mapped<CohortDefinition>(KigaliDistricts, null));
		pediLocatedInKigaliDistrictComposition.setCompositionString("1 AND 2");
		
		CompositionCohortDefinition adultsLocatedInKigaliDistrictComposition = new CompositionCohortDefinition();
		adultsLocatedInKigaliDistrictComposition.setName("adultsLocatedInKigaliDistrictComposition");
		adultsLocatedInKigaliDistrictComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		adultsLocatedInKigaliDistrictComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		adultsLocatedInKigaliDistrictComposition.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsLocatedInKigaliDistrictComposition.getSearches().put("2",new Mapped<CohortDefinition>(KigaliDistricts, null));
		adultsLocatedInKigaliDistrictComposition.setCompositionString("1 AND 2");

		// 5) Other Rwanda district,
		CompositionCohortDefinition patientsWithOtherRwandanDistrict = new CompositionCohortDefinition();
		patientsWithOtherRwandanDistrict.setName("patientsWithOtherRwandanDistrict");
		patientsWithOtherRwandanDistrict.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsWithOtherRwandanDistrict.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsWithOtherRwandanDistrict.getSearches().put("1",new Mapped<CohortDefinition>(patientsLocatedInKayonzaDistrict,null));
		patientsWithOtherRwandanDistrict.getSearches().put("2",new Mapped<CohortDefinition>(patientsLocatedInBureraDistrict,null));
		patientsWithOtherRwandanDistrict.getSearches().put("3",new Mapped<CohortDefinition>(patientsLocatedInKireheDistrict,null));
		patientsWithOtherRwandanDistrict.getSearches().put("4",new Mapped<CohortDefinition>(patientsLocatedInNyarugengeDistrict, null));
		patientsWithOtherRwandanDistrict.getSearches().put("5",new Mapped<CohortDefinition>(patientsLocatedInKicukiroDistrict,null));
		patientsWithOtherRwandanDistrict.getSearches().put("6",new Mapped<CohortDefinition>(patientsLocatedInGasaboDistrict,null));
		patientsWithOtherRwandanDistrict.setCompositionString("1 OR 2 OR 3 OR 4 OR 5 OR 6");

		SqlCohortDefinition notNullCountryAddress = new SqlCohortDefinition("select DISTINCT(p.patient_id) FROM patient p,person_address pa WHERE p.patient_id=pa.person_id AND pa.preferred=1 AND p.voided=0 AND pa.voided=0 AND country is not NULL");
		SqlCohortDefinition notNullCountryDistrictAddress = new SqlCohortDefinition("select DISTINCT(p.patient_id) FROM patient p,person_address pa WHERE p.patient_id=pa.person_id AND pa.preferred=1 AND p.voided=0 AND pa.voided=0 AND county_district is not NULL");
		   
        //Other Rwandan districts
		CompositionCohortDefinition pediLocatedInSpecifiedDistrict = new CompositionCohortDefinition();
		pediLocatedInSpecifiedDistrict.setName("pediLocatedInSpecifiedDistrict");
		pediLocatedInSpecifiedDistrict.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		pediLocatedInSpecifiedDistrict.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		pediLocatedInSpecifiedDistrict.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pediLocatedInSpecifiedDistrict.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithOtherRwandanDistrict,null));
		pediLocatedInSpecifiedDistrict.setCompositionString("1 AND 2");
		
		CompositionCohortDefinition adultsiLocatedInSpecifiedDistrict = new CompositionCohortDefinition();
		adultsiLocatedInSpecifiedDistrict.setName("adultsiLocatedInSpecifiedDistrict");
		adultsiLocatedInSpecifiedDistrict.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		adultsiLocatedInSpecifiedDistrict.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		adultsiLocatedInSpecifiedDistrict.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsiLocatedInSpecifiedDistrict.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithOtherRwandanDistrict,null));
		adultsiLocatedInSpecifiedDistrict.setCompositionString("1 AND 2");
		
		CompositionCohortDefinition pediLocatedInOtherRwandanDistricts = new CompositionCohortDefinition();
		pediLocatedInOtherRwandanDistricts.setName("pediLocatedInOtherRwandanDistricts");
		pediLocatedInOtherRwandanDistricts.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		pediLocatedInOtherRwandanDistricts.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		pediLocatedInOtherRwandanDistricts.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pediLocatedInOtherRwandanDistricts.getSearches().put("2",new Mapped<CohortDefinition>(notNullCountryDistrictAddress,null));
		pediLocatedInOtherRwandanDistricts.getSearches().put("3",new Mapped<CohortDefinition>(pediLocatedInSpecifiedDistrict,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pediLocatedInOtherRwandanDistricts.setCompositionString("1 AND 2 AND (NOT 3)");
		
		CompositionCohortDefinition adultsLocatedInOtherRwandanDistricts = new CompositionCohortDefinition();
		adultsLocatedInOtherRwandanDistricts.setName("adultsLocatedInOtherRwandanDistricts");
		adultsLocatedInOtherRwandanDistricts.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		adultsLocatedInOtherRwandanDistricts.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		adultsLocatedInOtherRwandanDistricts.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsLocatedInOtherRwandanDistricts.getSearches().put("2",new Mapped<CohortDefinition>(notNullCountryDistrictAddress,null));
		adultsLocatedInOtherRwandanDistricts.getSearches().put("3",new Mapped<CohortDefinition>(adultsiLocatedInSpecifiedDistrict,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsLocatedInOtherRwandanDistricts.setCompositionString("1 AND 2 AND (NOT 3)");

		//Patient with missing address
		CompositionCohortDefinition pediwithMissingAddress = new CompositionCohortDefinition();
		pediwithMissingAddress.setName("pediwithMissingAddress");
		pediwithMissingAddress.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		pediwithMissingAddress.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		pediwithMissingAddress.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pediwithMissingAddress.getSearches().put("2",new Mapped<CohortDefinition>(patientwithUnstructuredDistrict,null));
		pediwithMissingAddress.getSearches().put("3",new Mapped<CohortDefinition>(patientsWithOtherRwandanDistrict,null));
		pediwithMissingAddress.setCompositionString("1 AND 2 AND (NOT 3)");
		
		CompositionCohortDefinition adultswithMissingAddress = new CompositionCohortDefinition();
		adultswithMissingAddress.setName("adultswithMissingAddress");
		adultswithMissingAddress.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		adultswithMissingAddress.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		adultswithMissingAddress.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultswithMissingAddress.getSearches().put("2",new Mapped<CohortDefinition>(patientwithUnstructuredDistrict,null));
		adultswithMissingAddress.getSearches().put("3",new Mapped<CohortDefinition>(patientsWithOtherRwandanDistrict,null));
		adultswithMissingAddress.setCompositionString("1 AND 2 AND (NOT 3)");

		// 6) Outside Rwanda
		SqlCohortDefinition inRwanda = new SqlCohortDefinition("select DISTINCT(p.patient_id) FROM patient p,person_address pa WHERE p.patient_id=pa.person_id AND pa.preferred=1 AND p.voided=0 AND country='Rwanda' AND country is not NULL");
		InverseCohortDefinition patientsnotInRwanda = new InverseCohortDefinition(inRwanda);

		CompositionCohortDefinition pedifromOutsideOfRwanda = new CompositionCohortDefinition();
		pedifromOutsideOfRwanda.setName("pedifromOutsideOfRwanda");
		pedifromOutsideOfRwanda.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		pedifromOutsideOfRwanda.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		pedifromOutsideOfRwanda.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pedifromOutsideOfRwanda.getSearches().put("2",new Mapped<CohortDefinition>(notNullCountryAddress, null));
		pedifromOutsideOfRwanda.getSearches().put("3",new Mapped<CohortDefinition>(patientsnotInRwanda, null));
		pedifromOutsideOfRwanda.setCompositionString("1 AND 2 AND 3");
		
		CompositionCohortDefinition adultsfromOutsideOfRwanda = new CompositionCohortDefinition();
		adultsfromOutsideOfRwanda.setName("adultsfromOutsideOfRwanda");
		adultsfromOutsideOfRwanda.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		adultsfromOutsideOfRwanda.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		adultsfromOutsideOfRwanda.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withintakeDemoform,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsfromOutsideOfRwanda.getSearches().put("2",new Mapped<CohortDefinition>(notNullCountryAddress, null));
		adultsfromOutsideOfRwanda.getSearches().put("3",new Mapped<CohortDefinition>(patientsnotInRwanda, null));
		adultsfromOutsideOfRwanda.setCompositionString("1 AND 2 AND 3");

		CohortIndicator pediLocatedInKayonzaDistrictIndi = Indicators.newCountIndicator("pediLocatedInKayonzaDistrictIndi",pediLocatedInKayonzaDistrictComposition,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		CohortIndicator adultsLocatedInKayonzaDistrictIndi = Indicators.newCountIndicator("adultsLocatedInKayonzaDistrictIndi",adultsLocatedInKayonzaDistrictComposition,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator pediLocatedInBureraDistrictIndi = Indicators.newCountIndicator("pediLocatedInBureraDistrictIndi",pediLocatedInBureraDistrictComposition,
			    ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		CohortIndicator adultsLocatedInBureraDistrictIndi = Indicators.newCountIndicator("adultsLocatedInBureraDistrictIndi",adultsLocatedInBureraDistrictComposition,
			    ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator pediLocatedInKireheDistrictIndicator = Indicators.newCountIndicator("pediLocatedInKireheDistrictIndicator",pediLocatedInKireheDistrictComposition,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		CohortIndicator adultsLocatedInKireheDistrictIndicator = Indicators.newCountIndicator("adultsLocatedInKireheDistrictIndicator",adultsLocatedInKireheDistrictComposition,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator pediLocatedInKigaliDistrictIndicator = Indicators.newCountIndicator("patientsLocatedInKigaliDistrictIndicator",pediLocatedInKigaliDistrictComposition,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		CohortIndicator adultsLocatedInKigaliDistrictIndicator = Indicators.newCountIndicator("patientsLocatedInKigaliDistrictIndicator",adultsLocatedInKigaliDistrictComposition,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator pediLocatedInOtherRwandanDistrictsIndicator = Indicators.newCountIndicator("pediLocatedInOtherRwandanDistrictsIndicator",pediLocatedInOtherRwandanDistricts,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		CohortIndicator adultsLocatedInOtherRwandanDistrictsIndicator = Indicators.newCountIndicator("adultsLocatedInOtherRwandanDistrictsIndicator",adultsLocatedInOtherRwandanDistricts,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		CohortIndicator pediLocatedWithMissingIndicator = Indicators.newCountIndicator("pediLocatedWithMissingIndicator",pediwithMissingAddress,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		CohortIndicator adultsLocatedWithMissingIndicator = Indicators.newCountIndicator("adultsLocatedWithMissingIndicator",adultswithMissingAddress,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator pedifromOutsideOfRwandaIndicator = Indicators.newCountIndicator("patientsfromOutsideOfRwandaIndicator",pedifromOutsideOfRwanda,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		CohortIndicator adultsfromOutsideOfRwandaIndicator = Indicators.newCountIndicator("patientsfromOutsideOfRwandaIndicator",adultsfromOutsideOfRwanda,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		// ===========================================================================================
		// //
		// D9 Of the new cases with HIV status documented at intake, % with a
		// positive HIV status */
		// ==========================================================================================
		// //

		CodedObsCohortDefinition patientswithHivStatus = Cohorts
				.createCodedObsCohortDefinition("patientswithHivStatus",
						hivStatus, null, SetComparator.IN, TimeModifier.LAST);
		CodedObsCohortDefinition patientWithPosHivStatus = Cohorts
				.createCodedObsCohortDefinition("patientWithPosHivStatus",
						hivStatus, positiveStatus, SetComparator.IN,
						TimeModifier.LAST);

		// Denominator
		CompositionCohortDefinition newCasespediWithHiVstatus = new CompositionCohortDefinition();
		newCasespediWithHiVstatus.setName("newCasespediWithHiVstatus");
		newCasespediWithHiVstatus.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newCasespediWithHiVstatus.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newCasespediWithHiVstatus
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newCasesunder15withintakeDemoform,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newCasespediWithHiVstatus.getSearches().put("2",
				new Mapped<CohortDefinition>(patientswithHivStatus, null));
		newCasespediWithHiVstatus.setCompositionString("1 AND 2");

		CompositionCohortDefinition newCasesadultsWithHiVstatus = new CompositionCohortDefinition();
		newCasesadultsWithHiVstatus.setName("newCasesadultsWithHiVstatus");
		newCasesadultsWithHiVstatus.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newCasesadultsWithHiVstatus.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newCasesadultsWithHiVstatus
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newCasesOver15withintakeDemoform,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newCasesadultsWithHiVstatus.getSearches().put("2",
				new Mapped<CohortDefinition>(patientswithHivStatus, null));
		newCasesadultsWithHiVstatus.setCompositionString("1 AND 2");

		// Numerator
		CompositionCohortDefinition newCasespediWithHiVPositivestatus = new CompositionCohortDefinition();
		newCasespediWithHiVPositivestatus
				.setName("newCasespediWithHiVPositivestatus");
		newCasespediWithHiVPositivestatus.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newCasespediWithHiVPositivestatus.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newCasespediWithHiVPositivestatus
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newCasesunder15withintakeDemoform,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newCasespediWithHiVPositivestatus.getSearches().put("2",
				new Mapped<CohortDefinition>(patientWithPosHivStatus, null));
		newCasespediWithHiVPositivestatus.setCompositionString("1 AND 2");

		CompositionCohortDefinition newCasesadultsWithHiVPositivestatus = new CompositionCohortDefinition();
		newCasesadultsWithHiVPositivestatus
				.setName("newCasesadultsWithHiVPositivestatus");
		newCasesadultsWithHiVPositivestatus.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newCasesadultsWithHiVPositivestatus.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newCasesadultsWithHiVPositivestatus
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newCasesOver15withintakeDemoform,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newCasesadultsWithHiVPositivestatus.getSearches().put("2",
				new Mapped<CohortDefinition>(patientWithPosHivStatus, null));
		newCasesadultsWithHiVPositivestatus.setCompositionString("1 AND 2");

		CohortIndicator newCasespediWithHiVstatusIndicator = Indicators
				.newCountIndicator(
						"newCasespediWithHiVstatusIndicator",
						newCasespediWithHiVstatus,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newCasesadultsWithHiVstatusIndicator = Indicators
				.newCountIndicator(
						"newCasesadultsWithHiVstatusIndicator",
						newCasesadultsWithHiVstatus,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newCasespediWithHiVPositivestatusIndicator = Indicators
				.newCountIndicator(
						"newCasespediWithHiVPositivestatusIndicator",
						newCasespediWithHiVPositivestatus,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newCasesadultsWithHiVPositivestatusIndicator = Indicators
				.newCountIndicator(
						"newCasesadultsWithHiVPositivestatusIndicator",
						newCasesadultsWithHiVPositivestatus,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		// ===========================================================================================
		// //
		// A1 Total # of patients ever enrolled in program at the end of the
		// review quarter */
		// ==========================================================================================
		// //

		EncounterCohortDefinition patientWithClinicalIntakeForm = Cohorts.createEncounterBasedOnForms("patientWithClinicalIntakeForms",onOrAfterOnOrBefore, clinicalIntakeForms);
		InProgramCohortDefinition everEnrolleInOncologyProgram = Cohorts.createInProgramParameterizableByStartEndDate("everEnrolleInOncologyProgram", oncologyProgram);
		SqlCohortDefinition under15YrsAtEnrol = Cohorts.createUnder15AtEnrollmentCohort("under15YrsAtEnrol",oncologyProgram);

		CompositionCohortDefinition pedsWitClinicalIntakeintheReviewQuarter = new CompositionCohortDefinition();
		pedsWitClinicalIntakeintheReviewQuarter.setName("pedsWitClinicalIntakeintheReviewQuarter");
		pedsWitClinicalIntakeintheReviewQuarter.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		//pedsWitClinicalIntakeintheReviewQuarter.getSearches().put("1",new Mapped<CohortDefinition>(patientWithClinicalIntakeForm,
			//ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
		pedsWitClinicalIntakeintheReviewQuarter.getSearches().put("2",new Mapped<CohortDefinition>(under15YrsAtEnrol, null));
		pedsWitClinicalIntakeintheReviewQuarter.getSearches().put("3",new Mapped<CohortDefinition>(everEnrolleInOncologyProgram, null));
		pedsWitClinicalIntakeintheReviewQuarter.setCompositionString("2 AND 3");

		CompositionCohortDefinition adultsWitClinicalIntakeintheReviewQuarter = new CompositionCohortDefinition();
		adultsWitClinicalIntakeintheReviewQuarter.setName("adultsWitClinicalIntakeintheReviewQuarter");
		adultsWitClinicalIntakeintheReviewQuarter.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		//adultsWitClinicalIntakeintheReviewQuarter.getSearches().put("1",new Mapped<CohortDefinition>(patientWithClinicalIntakeForm,
			//ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
		adultsWitClinicalIntakeintheReviewQuarter.getSearches().put("2",new Mapped<CohortDefinition>(over15Cohort, null));
		adultsWitClinicalIntakeintheReviewQuarter.getSearches().put("3",new Mapped<CohortDefinition>(everEnrolleInOncologyProgram, null));
		adultsWitClinicalIntakeintheReviewQuarter.setCompositionString("2 AND 3");

		CohortIndicator pedsWitClinicalIntakeintheReviewQuarterIndicator = Indicators.newCountIndicator("pedsWitClinicalIntakeintheReviewQuarterIndicator",
				pedsWitClinicalIntakeintheReviewQuarter,null);

		CohortIndicator adultsWitClinicalIntakeintheReviewQuarterIndicator = Indicators.newCountIndicator("adultsWitClinicalIntakeintheReviewQuarterIndicator",
				adultsWitClinicalIntakeintheReviewQuarter,null);

		// ==================================================================================//
		// A2 Total # of new patients not suspected of cancer at intake in the
		// last quarter */
		// ===================================================================================//
		List<ProgramWorkflowState> notsuspectedOnCancer = new ArrayList<ProgramWorkflowState>();
		notsuspectedOnCancer.add(notCancerdiagnosisstate);
		InStateCohortDefinition notSuspectedstateOnOrBefore = Cohorts
				.createInCurrentState("ONC:inSuspectedstateOnOrBefore",
						notsuspectedOnCancer, onOrAfterOnOrBefore);

		// Ped
		CompositionCohortDefinition pediAtIntakeInTheProgram = new CompositionCohortDefinition();
		pediAtIntakeInTheProgram.setName("pediAtIntakeInTheProgram");
		pediAtIntakeInTheProgram.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		pediAtIntakeInTheProgram.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		pediAtIntakeInTheProgram.getSearches().put(
				"1",
				new Mapped<CohortDefinition>(inOncologyProgram,
						ParameterizableUtil
								.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pediAtIntakeInTheProgram
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithClinicalIntakeForm,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pediAtIntakeInTheProgram.getSearches().put("3",
				new Mapped<CohortDefinition>(under15YrsAtEnrol, null));
		pediAtIntakeInTheProgram.setCompositionString("1 AND 2 AND 3");

		CompositionCohortDefinition adultsAtIntakeInTheProgram = new CompositionCohortDefinition();
		adultsAtIntakeInTheProgram.setName("adultsAtIntakeInTheProgram");
		adultsAtIntakeInTheProgram.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		adultsAtIntakeInTheProgram.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		adultsAtIntakeInTheProgram.getSearches().put("1",new Mapped<CohortDefinition>(inOncologyProgram,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsAtIntakeInTheProgram.getSearches().put("2",new Mapped<CohortDefinition>(patientWithClinicalIntakeForm,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsAtIntakeInTheProgram.getSearches().put("3",new Mapped<CohortDefinition>(over15Cohort, null));
		adultsAtIntakeInTheProgram.setCompositionString("1 AND 2 AND 3");

		CompositionCohortDefinition newPediNotSuspectedCancerAtIntake = new CompositionCohortDefinition();
		newPediNotSuspectedCancerAtIntake.setName("newPediNotSuspectedCancerAtIntake");
		newPediNotSuspectedCancerAtIntake.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		newPediNotSuspectedCancerAtIntake.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		newPediNotSuspectedCancerAtIntake.getSearches().put("1",new Mapped<CohortDefinition>(pediAtIntakeInTheProgram,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediNotSuspectedCancerAtIntake.getSearches().put("2",new Mapped<CohortDefinition>(notSuspectedstateOnOrBefore,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediNotSuspectedCancerAtIntake.setCompositionString("1 AND 2 ");

		// Adults
		CompositionCohortDefinition newAdultsNotSuspectedCancerAtIntake = new CompositionCohortDefinition();
		newAdultsNotSuspectedCancerAtIntake.setName("newAdultsNotSuspectedCancerAtIntake");
		newAdultsNotSuspectedCancerAtIntake.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		newAdultsNotSuspectedCancerAtIntake.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		newAdultsNotSuspectedCancerAtIntake.getSearches().put("1", new Mapped<CohortDefinition>(adultsAtIntakeInTheProgram,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsNotSuspectedCancerAtIntake.getSearches().put("2",new Mapped<CohortDefinition>(notSuspectedstateOnOrBefore,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsNotSuspectedCancerAtIntake.setCompositionString("1 AND 2");

		CohortIndicator newPediNotSuspectedCancerAtIntakeIndicator = Indicators
				.newCountIndicator(
						"newPediNotSuspectedCancerAtIntakeIndicator",
						newPediNotSuspectedCancerAtIntake,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsNotSuspectedCancerAtIntakeIndicator = Indicators
				.newCountIndicator(
						"newPediNotSuspectedCancerAtIntakeIndicator",
						newAdultsNotSuspectedCancerAtIntake,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		// =========================================================================//
		// A3 Total # of new suspected cancer cases at intake in the last
		// quarter */
		// =========================================================================//
		List<ProgramWorkflowState> suspectedOnCancer = new ArrayList<ProgramWorkflowState>();
		suspectedOnCancer.add(suspecteddiagnosisstate);
		InStateCohortDefinition suspectedstateOnOrBefore = Cohorts
				.createInCurrentState("ONC:inSuspectedstateOnOrBefore",
						suspectedOnCancer, onOrAfterOnOrBefore);

		// adults and Pedi with all suspected cancer types (suspected state +all
		// diagnosis states):

		List<ProgramWorkflowState> suspectedCancerDiagnosisTypes = new ArrayList<ProgramWorkflowState>();
		suspectedCancerDiagnosisTypes.add(acutelymphoblasticcancer);
		suspectedCancerDiagnosisTypes.add(breastCancer);
		suspectedCancerDiagnosisTypes.add(burkittLymphoma);
		suspectedCancerDiagnosisTypes.add(cancerunkowntype);
		suspectedCancerDiagnosisTypes.add(cervicalcancer);
		suspectedCancerDiagnosisTypes.add(chronicmyelogenousleukemia);
		suspectedCancerDiagnosisTypes.add(colorectalcancer);
		suspectedCancerDiagnosisTypes.add(headandneckcancer);
		suspectedCancerDiagnosisTypes.add(hodgkinslymphoma);
		suspectedCancerDiagnosisTypes.add(karposisarcoma);
		suspectedCancerDiagnosisTypes.add(largebcelllymphoma);
		suspectedCancerDiagnosisTypes.add(lungcancerdiagnosis);
		suspectedCancerDiagnosisTypes.add(metastaticcancer);
		suspectedCancerDiagnosisTypes.add(multiplemyeloma);
		suspectedCancerDiagnosisTypes.add(neuphroblastoma);
		suspectedCancerDiagnosisTypes.add(otherliquidcancer);
		suspectedCancerDiagnosisTypes.add(othernonhodkinslymphoma);
		suspectedCancerDiagnosisTypes.add(othersolidcancer);
		suspectedCancerDiagnosisTypes.add(prostatecancer);
		suspectedCancerDiagnosisTypes.add(stomachcancer);
		InStateCohortDefinition patientWithAllSuspectedCancerDiagnosis = Cohorts
				.createInCurrentState(
						"ONC:patientWithAllSuspectedCancerDiagnosis",
						suspectedCancerDiagnosisTypes, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediSuspectedCanceratIntakeCases = new CompositionCohortDefinition();
		newPediSuspectedCanceratIntakeCases
				.setName("newPediSuspectedCanceratIntakeCases");
		newPediSuspectedCanceratIntakeCases.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newPediSuspectedCanceratIntakeCases.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediSuspectedCanceratIntakeCases
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								pediAtIntakeInTheProgram,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediSuspectedCanceratIntakeCases
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								suspectedstateOnOrBefore,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediSuspectedCanceratIntakeCases.setCompositionString("1 AND 2");

		CompositionCohortDefinition newPediSuspectedCancerAtIntake = new CompositionCohortDefinition();
		newPediSuspectedCancerAtIntake
				.setName("newPediSuspectedCancerAtIntake");
		newPediSuspectedCancerAtIntake.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediSuspectedCancerAtIntake.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediSuspectedCancerAtIntake
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediSuspectedCancerAtIntake
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithAllSuspectedCancerDiagnosis,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediSuspectedCancerAtIntake.setCompositionString("1 AND 2");

		CompositionCohortDefinition newAdultSuspectedCancerAtIntakeCases = new CompositionCohortDefinition();
		newAdultSuspectedCancerAtIntakeCases
				.setName("newAdultSuspectedCancerAtIntakeCases");
		newAdultSuspectedCancerAtIntakeCases.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultSuspectedCancerAtIntakeCases.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultSuspectedCancerAtIntakeCases
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								adultsAtIntakeInTheProgram,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultSuspectedCancerAtIntakeCases
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								suspectedstateOnOrBefore,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultSuspectedCancerAtIntakeCases.setCompositionString("1 AND 2");

		CompositionCohortDefinition newAdultSuspectedCancerAtIntake = new CompositionCohortDefinition();
		newAdultSuspectedCancerAtIntake
				.setName("newPediSuspectedCancerAtIntake");
		newAdultSuspectedCancerAtIntake.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newAdultSuspectedCancerAtIntake.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultSuspectedCancerAtIntake
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultSuspectedCancerAtIntake
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithAllSuspectedCancerDiagnosis,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultSuspectedCancerAtIntake.setCompositionString("1 AND 2");

		CohortIndicator newPediSuspectedCancerAtIntakeIndicator = Indicators
				.newCountIndicator(
						"newPediSuspectedCancerAtIntakeIndicator",
						newPediSuspectedCancerAtIntake,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		CohortIndicator newAdultSuspectedCancerAtIntakeIndicator = Indicators
				.newCountIndicator(
						"newAdultSuspectedCancerAtIntakeIndicator",
						newAdultSuspectedCancerAtIntake,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		// Acute lymphoblastic leukemia
		List<ProgramWorkflowState> acutelymphoblasticleukemiacancer = new ArrayList<ProgramWorkflowState>();
		acutelymphoblasticleukemiacancer.add(acutelymphoblasticcancer);
		InStateCohortDefinition patientWithAcutelypholeukemiaCancer = Cohorts
				.createInCurrentState("acutelymphoblasticleukemiacancer",
						acutelymphoblasticleukemiacancer, onOrAfterOnOrBefore);
		// Ped
		CompositionCohortDefinition newPediWithAcutelymphoblasticleukemia = new CompositionCohortDefinition();
		newPediWithAcutelymphoblasticleukemia
				.setName("newPediWithAcutelymphoblasticleukemia");
		newPediWithAcutelymphoblasticleukemia.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newPediWithAcutelymphoblasticleukemia.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithAcutelymphoblasticleukemia
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithAcutelymphoblasticleukemia
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithAcutelypholeukemiaCancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithAcutelymphoblasticleukemia.setCompositionString("1 AND 2 ");

		// Adults
		CompositionCohortDefinition newAdultsWithAcutelymphoblasticleukemia = new CompositionCohortDefinition();
		newAdultsWithAcutelymphoblasticleukemia
				.setName("newAdultsWithAcutelymphoblasticleukemia");
		newAdultsWithAcutelymphoblasticleukemia.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithAcutelymphoblasticleukemia.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithAcutelymphoblasticleukemia
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithAcutelymphoblasticleukemia
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithAcutelypholeukemiaCancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithAcutelymphoblasticleukemia
				.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithAcutelymphoblasticleukemiaIndicator = Indicators
				.newCountIndicator(
						"newPediWithAcutelymphoblasticleukemiaIndicator",
						newPediWithAcutelymphoblasticleukemia,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithAcutelymphoblasticleukemiaIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithAcutelymphoblasticleukemiaIndicator",
						newAdultsWithAcutelymphoblasticleukemia,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		// Breast Cancer
		List<ProgramWorkflowState> breastcancertype = new ArrayList<ProgramWorkflowState>();
		breastcancertype.add(breastCancer);
		InStateCohortDefinition patientWithBreastcancerCancer = Cohorts
				.createInCurrentState("breastcancertype", breastcancertype,
						onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithbreastcancertype = new CompositionCohortDefinition();
		newPediWithbreastcancertype.setName("newPediWithbreastcancertype");
		newPediWithbreastcancertype.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithbreastcancertype.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediWithbreastcancertype
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithbreastcancertype
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithBreastcancerCancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithbreastcancertype.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithbreastcancertype = new CompositionCohortDefinition();
		newAdultsWithbreastcancertype.setName("newAdultsWithbreastcancertype");
		newAdultsWithbreastcancertype.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newAdultsWithbreastcancertype.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newAdultsWithbreastcancertype
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithbreastcancertype
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithBreastcancerCancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithbreastcancertype.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithbreastcancertypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithbreastcancertypeIndicator",
						newPediWithbreastcancertype,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithbreastcancertypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithbreastcancertypeIndicator",
						newAdultsWithbreastcancertype,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		// Burkitt's lymphoma
		List<ProgramWorkflowState> burkitlymphomacancer = new ArrayList<ProgramWorkflowState>();
		burkitlymphomacancer.add(burkittLymphoma);
		InStateCohortDefinition patientWithBurkitlymphoma = Cohorts
				.createInCurrentState("burkitlymphomacancer",
						burkitlymphomacancer, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithburkitlymphomacancer = new CompositionCohortDefinition();
		newPediWithburkitlymphomacancer
				.setName("newPediWithburkitlymphomacancer");
		newPediWithburkitlymphomacancer.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithburkitlymphomacancer.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithburkitlymphomacancer
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithburkitlymphomacancer
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithBurkitlymphoma,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithburkitlymphomacancer.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithburkitlymphomacancer = new CompositionCohortDefinition();
		newAdultsWithburkitlymphomacancer
				.setName("newAdultsWithburkitlymphomacancer");
		newAdultsWithburkitlymphomacancer.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithburkitlymphomacancer.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithburkitlymphomacancer
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithburkitlymphomacancer
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithBurkitlymphoma,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithburkitlymphomacancer.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithburkitlymphomacancerIndicator = Indicators
				.newCountIndicator(
						"newPediWithburkitlymphomacancerIndicator",
						newPediWithburkitlymphomacancer,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithburkitlymphomacancerIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithburkitlymphomacancerIndicator",
						newAdultsWithburkitlymphomacancer,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		// Cancer unknown type
		List<ProgramWorkflowState> cancerunknowntypeC = new ArrayList<ProgramWorkflowState>();
		cancerunknowntypeC.add(cancerunkowntype);
		InStateCohortDefinition patientCanceunknowntype = Cohorts
				.createInCurrentState("cancerunknowntypeC", cancerunknowntypeC,
						onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithcancerunknowntypeC = new CompositionCohortDefinition();
		newPediWithcancerunknowntypeC.setName("newPediWithcancerunknowntypeC");
		newPediWithcancerunknowntypeC.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithcancerunknowntypeC.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediWithcancerunknowntypeC
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithcancerunknowntypeC
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientCanceunknowntype,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithcancerunknowntypeC.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithcancerunknowntypeC = new CompositionCohortDefinition();
		newAdultsWithcancerunknowntypeC
				.setName("newAdultsWithcancerunknowntypeC");
		newAdultsWithcancerunknowntypeC.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newAdultsWithcancerunknowntypeC.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithcancerunknowntypeC
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithcancerunknowntypeC
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientCanceunknowntype,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithcancerunknowntypeC.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithcancerunknowntypeCIndicator = Indicators
				.newCountIndicator(
						"newPediWithcancerunknowntypeCIndicator",
						newPediWithcancerunknowntypeC,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithcancerunknowntypeCIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithcancerunknowntypeCIndicator",
						newAdultsWithcancerunknowntypeC,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		// Cervical cancer
		List<ProgramWorkflowState> cervicalCancerType = new ArrayList<ProgramWorkflowState>();
		cervicalCancerType.add(cervicalcancer);
		InStateCohortDefinition patientcervicalCancerType = Cohorts
				.createInCurrentState("patientcervicalCancerType",
						cervicalCancerType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithcervicalCancerType = new CompositionCohortDefinition();
		newPediWithcervicalCancerType.setName("newPediWithcervicalCancerType");
		newPediWithcervicalCancerType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithcervicalCancerType.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediWithcervicalCancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithcervicalCancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientcervicalCancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithcervicalCancerType.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithcervicalCancerType = new CompositionCohortDefinition();
		newAdultsWithcervicalCancerType
				.setName("newAdultsWithcervicalCancerType");
		newAdultsWithcervicalCancerType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newAdultsWithcervicalCancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithcervicalCancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithcervicalCancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientcervicalCancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithcervicalCancerType.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithcervicalCancerTypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithcervicalCancerTypeIndicator",
						newPediWithcervicalCancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithcervicalCancerTypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithcervicalCancerTypeIndicator",
						newAdultsWithcervicalCancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		// Chronic Myelogenous leukemia
		List<ProgramWorkflowState> chronicmyelogousleukemiac = new ArrayList<ProgramWorkflowState>();
		chronicmyelogousleukemiac.add(chronicmyelogenousleukemia);
		InStateCohortDefinition patientWithchronicmyelogousleukemiacType = Cohorts
				.createInCurrentState(
						"patientWithchronicmyelogousleukemiacType",
						chronicmyelogousleukemiac, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithchronicmyelogousleukemiac = new CompositionCohortDefinition();
		newPediWithchronicmyelogousleukemiac
				.setName("newPediWithchronicmyelogousleukemiac");
		newPediWithchronicmyelogousleukemiac.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newPediWithchronicmyelogousleukemiac.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithchronicmyelogousleukemiac
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithchronicmyelogousleukemiac
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithchronicmyelogousleukemiacType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithchronicmyelogousleukemiac.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithchronicmyelogousleukemiac = new CompositionCohortDefinition();
		newAdultsWithchronicmyelogousleukemiac
				.setName("newAdultsWithchronicmyelogousleukemiac");
		newAdultsWithchronicmyelogousleukemiac.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithchronicmyelogousleukemiac.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithchronicmyelogousleukemiac
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithchronicmyelogousleukemiac
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithchronicmyelogousleukemiacType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithchronicmyelogousleukemiac.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithchronicmyelogousleukemiacIndicator = Indicators
				.newCountIndicator(
						"newPediWithchronicmyelogousleukemiacIndicator",
						newPediWithchronicmyelogousleukemiac,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithchronicmyelogousleukemiacIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithchronicmyelogousleukemiacIndicator",
						newAdultsWithchronicmyelogousleukemiac,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		// Colo-rectal cancer
		List<ProgramWorkflowState> colorectalcancerType = new ArrayList<ProgramWorkflowState>();
		colorectalcancerType.add(colorectalcancer);
		InStateCohortDefinition patientWithcolorectalcancerType = Cohorts
				.createInCurrentState("patientWithcolorectalcancerType",
						colorectalcancerType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithcolorectalcancerType = new CompositionCohortDefinition();
		newPediWithcolorectalcancerType
				.setName("newPediWithcolorectalcancerType");
		newPediWithcolorectalcancerType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithcolorectalcancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithcolorectalcancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithcolorectalcancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithcolorectalcancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithcolorectalcancerType.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithcolorectalcancerType = new CompositionCohortDefinition();
		newAdultsWithcolorectalcancerType
				.setName("newAdultsWithcolorectalcancerType");
		newAdultsWithcolorectalcancerType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithcolorectalcancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithcolorectalcancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithcolorectalcancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithcolorectalcancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithcolorectalcancerType.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithcolorectalcancerTypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithcolorectalcancerTypeIndicator",
						newPediWithcolorectalcancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithcolorectalcancerTypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithcolorectalcancerTypeIndicator",
						newAdultsWithcolorectalcancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		// Head and neck cancer
		List<ProgramWorkflowState> headandneckcancertype = new ArrayList<ProgramWorkflowState>();
		headandneckcancertype.add(headandneckcancer);
		InStateCohortDefinition patientwithheadandneckcancertype = Cohorts
				.createInCurrentState("patientwithheadandneckcancertype",
						headandneckcancertype, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithheadandneckcancertype = new CompositionCohortDefinition();
		newPediWithheadandneckcancertype
				.setName("newPediWithheadandneckcancertype");
		newPediWithheadandneckcancertype.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newPediWithheadandneckcancertype.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithheadandneckcancertype
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithheadandneckcancertype
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientwithheadandneckcancertype,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithheadandneckcancertype.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithheadandneckcancertype = new CompositionCohortDefinition();
		newAdultsWithheadandneckcancertype
				.setName("newAdultsWithheadandneckcancertype");
		newAdultsWithheadandneckcancertype.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithheadandneckcancertype.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithheadandneckcancertype
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithheadandneckcancertype
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientwithheadandneckcancertype,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithheadandneckcancertype.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithheadandneckcancertypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithheadandneckcancertypeIndicator",
						newPediWithheadandneckcancertype,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithheadandneckcancertypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithheadandneckcancertypeIndicator",
						newAdultsWithheadandneckcancertype,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		// Hodgkin's lymphoma
		List<ProgramWorkflowState> hodkinlymphomacancer = new ArrayList<ProgramWorkflowState>();
		hodkinlymphomacancer.add(hodgkinslymphoma);
		InStateCohortDefinition patientWithhodkinlymphomacancer = Cohorts
				.createInCurrentState("patientWithhodkinlymphomacancer",
						hodkinlymphomacancer, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithhodkinlymphomacancer = new CompositionCohortDefinition();
		newPediWithhodkinlymphomacancer
				.setName("newPediWithhodkinlymphomacancer");
		newPediWithhodkinlymphomacancer.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithhodkinlymphomacancer.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithhodkinlymphomacancer
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithhodkinlymphomacancer
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithhodkinlymphomacancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithhodkinlymphomacancer.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithhodkinlymphomacancer = new CompositionCohortDefinition();
		newAdultsWithhodkinlymphomacancer
				.setName("newAdultsWithhodkinlymphomacancer");
		newAdultsWithhodkinlymphomacancer.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithhodkinlymphomacancer.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithhodkinlymphomacancer
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithhodkinlymphomacancer
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithhodkinlymphomacancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithhodkinlymphomacancer.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithhodkinlymphomacancerIndicator = Indicators
				.newCountIndicator(
						"newPediWithhodkinlymphomacancerIndicator",
						newPediWithhodkinlymphomacancer,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithhodkinlymphomacancerIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithhodkinlymphomacancerIndicator",
						newAdultsWithhodkinlymphomacancer,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		// Karposi's sarcoma
		List<ProgramWorkflowState> karposysarcomaType = new ArrayList<ProgramWorkflowState>();
		karposysarcomaType.add(karposisarcoma);
		InStateCohortDefinition patientWithkarposusarcomaType = Cohorts
				.createInCurrentState("patientWithkarposusarcomaType",
						karposysarcomaType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithkarposysarcomaType = new CompositionCohortDefinition();
		newPediWithkarposysarcomaType.setName("newPediWithkarposysarcomaType");
		newPediWithkarposysarcomaType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithkarposysarcomaType.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediWithkarposysarcomaType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithkarposysarcomaType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithkarposusarcomaType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithkarposysarcomaType.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithkarposysarcomaType = new CompositionCohortDefinition();
		newAdultsWithkarposysarcomaType
				.setName("newAdultsWithkarposysarcomaType");
		newAdultsWithkarposysarcomaType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newAdultsWithkarposysarcomaType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithkarposysarcomaType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithkarposysarcomaType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithkarposusarcomaType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithkarposysarcomaType.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithkarposysarcomaTypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithkarposysarcomaTypeIndicator",
						newPediWithkarposysarcomaType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithkarposysarcomaTypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithkarposysarcomaTypeIndicator",
						newAdultsWithkarposysarcomaType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		// Large B-cell lymphoma
		List<ProgramWorkflowState> largebcellLymphomaCancerType = new ArrayList<ProgramWorkflowState>();
		largebcellLymphomaCancerType.add(largebcelllymphoma);
		InStateCohortDefinition patientWithlargebcellLymphomaCancerType = Cohorts
				.createInCurrentState(
						"patientWithlargebcellLymphomaCancerType",
						largebcellLymphomaCancerType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithlargebcellLymphomaCancerType = new CompositionCohortDefinition();
		newPediWithlargebcellLymphomaCancerType
				.setName("newPediWithlargebcellLymphomaCancerType");
		newPediWithlargebcellLymphomaCancerType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newPediWithlargebcellLymphomaCancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithlargebcellLymphomaCancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithlargebcellLymphomaCancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithlargebcellLymphomaCancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithlargebcellLymphomaCancerType
				.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithlargebcellLymphomaCancerType = new CompositionCohortDefinition();
		newAdultsWithlargebcellLymphomaCancerType
				.setName("newAdultsWithlargebcellLymphomaCancerType");
		newAdultsWithlargebcellLymphomaCancerType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithlargebcellLymphomaCancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithlargebcellLymphomaCancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithlargebcellLymphomaCancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithlargebcellLymphomaCancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithlargebcellLymphomaCancerType
				.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithlargebcellLymphomaCancerTypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithlargebcellLymphomaCancerTypeIndicator",
						newPediWithlargebcellLymphomaCancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithlargebcellLymphomaCancerTypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithlargebcellLymphomaCancerTypeIndicator",
						newAdultsWithlargebcellLymphomaCancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		// Lung cancer diagnosis
		List<ProgramWorkflowState> lungdiagnosistype = new ArrayList<ProgramWorkflowState>();
		lungdiagnosistype.add(lungcancerdiagnosis);
		InStateCohortDefinition patientWithLungdiagnosiscancerType = Cohorts
				.createInCurrentState("patientWithLungdiagnosiscancerType",
						lungdiagnosistype, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithlungdiagnosistype = new CompositionCohortDefinition();
		newPediWithlungdiagnosistype.setName("newPediWithlungdiagnosistype");
		newPediWithlungdiagnosistype.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithlungdiagnosistype.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediWithlungdiagnosistype
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithlungdiagnosistype
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithLungdiagnosiscancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithlungdiagnosistype.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithlungdiagnosistype = new CompositionCohortDefinition();
		newAdultsWithlungdiagnosistype
				.setName("newAdultsWithlungdiagnosistype");
		newAdultsWithlungdiagnosistype.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newAdultsWithlungdiagnosistype.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newAdultsWithlungdiagnosistype
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithlungdiagnosistype
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithLungdiagnosiscancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithlungdiagnosistype.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithlungdiagnosistypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithlungdiagnosistypeIndicator",
						newPediWithlungdiagnosistype,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithlungdiagnosistypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithlungdiagnosistypeIndicator",
						newAdultsWithlungdiagnosistype,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		// Metastatic cancer
		List<ProgramWorkflowState> metastaticcancerType = new ArrayList<ProgramWorkflowState>();
		metastaticcancerType.add(metastaticcancer);
		InStateCohortDefinition patientWithmetastaticcancerType = Cohorts
				.createInCurrentState("patientWithmetastaticcancerType",
						metastaticcancerType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithWithmetastaticcancerType = new CompositionCohortDefinition();
		newPediWithWithmetastaticcancerType
				.setName("newPediWithWithmetastaticcancerType");
		newPediWithWithmetastaticcancerType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newPediWithWithmetastaticcancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithWithmetastaticcancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithWithmetastaticcancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithmetastaticcancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithWithmetastaticcancerType.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithWithmetastaticcancerType = new CompositionCohortDefinition();
		newAdultsWithWithmetastaticcancerType
				.setName("newAdultsWithWithmetastaticcancerType");
		newAdultsWithWithmetastaticcancerType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithWithmetastaticcancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithWithmetastaticcancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithWithmetastaticcancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithmetastaticcancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithWithmetastaticcancerType.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithWithmetastaticcancerTypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithWithmetastaticcancerTypeIndicator",
						newPediWithWithmetastaticcancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithWithmetastaticcancerTypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithWithmetastaticcancerTypeIndicator",
						newAdultsWithWithmetastaticcancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		// Multiple myeloma
		List<ProgramWorkflowState> multiplemyelomaType = new ArrayList<ProgramWorkflowState>();
		multiplemyelomaType.add(multiplemyeloma);
		InStateCohortDefinition patientWithmultiplemyelomaType = Cohorts
				.createInCurrentState("patientWithmultiplemyelomaType",
						multiplemyelomaType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithmultiplemyelomaType = new CompositionCohortDefinition();
		newPediWithmultiplemyelomaType
				.setName("newPediWithmultiplemyelomaType");
		newPediWithmultiplemyelomaType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithmultiplemyelomaType.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediWithmultiplemyelomaType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithmultiplemyelomaType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithmultiplemyelomaType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithmultiplemyelomaType.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithmultiplemyelomaType = new CompositionCohortDefinition();
		newAdultsWithmultiplemyelomaType
				.setName("newAdultsWithmultiplemyelomaType");
		newAdultsWithmultiplemyelomaType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithmultiplemyelomaType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithmultiplemyelomaType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithmultiplemyelomaType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithmultiplemyelomaType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithmultiplemyelomaType.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithmultiplemyelomaTypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithmultiplemyelomaTypeIndicator",
						newPediWithmultiplemyelomaType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithmultiplemyelomaTypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithmultiplemyelomaTypeIndicator",
						newAdultsWithmultiplemyelomaType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		// Nephroblastoma
		List<ProgramWorkflowState> neurobblastomaType = new ArrayList<ProgramWorkflowState>();
		neurobblastomaType.add(neuphroblastoma);
		InStateCohortDefinition patientsWithneurobblastomaType = Cohorts
				.createInCurrentState("patientsWithneurobblastomaType",
						neurobblastomaType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithneurobblastomaType = new CompositionCohortDefinition();
		newPediWithneurobblastomaType.setName("newPediWithneurobblastomaType");
		newPediWithneurobblastomaType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithneurobblastomaType.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediWithneurobblastomaType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithneurobblastomaType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientsWithneurobblastomaType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithneurobblastomaType.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithneurobblastomaType = new CompositionCohortDefinition();
		newAdultsWithneurobblastomaType
				.setName("newAdultsWithneurobblastomaType");
		newAdultsWithneurobblastomaType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newAdultsWithneurobblastomaType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithneurobblastomaType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithneurobblastomaType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientsWithneurobblastomaType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithneurobblastomaType.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithneurobblastomaTypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithneurobblastomaTypeIndicator",
						newPediWithneurobblastomaType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithneurobblastomaTypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithneurobblastomaTypeIndicator",
						newAdultsWithneurobblastomaType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		// Other liquid cancer
		List<ProgramWorkflowState> otherLiquidcancer = new ArrayList<ProgramWorkflowState>();
		otherLiquidcancer.add(otherliquidcancer);
		InStateCohortDefinition patientWithotherLiquidcancer = Cohorts
				.createInCurrentState("patientWithotherLiquidcancer",
						otherLiquidcancer, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithotherLiquidcancer = new CompositionCohortDefinition();
		newPediWithotherLiquidcancer.setName("newPediWithotherLiquidcancer");
		newPediWithotherLiquidcancer.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithotherLiquidcancer.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediWithotherLiquidcancer
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithotherLiquidcancer
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithotherLiquidcancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithotherLiquidcancer.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithotherLiquidcancer = new CompositionCohortDefinition();
		newAdultsWithotherLiquidcancer
				.setName("newAdultsWithotherLiquidcancer");
		newAdultsWithotherLiquidcancer.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newAdultsWithotherLiquidcancer.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newAdultsWithotherLiquidcancer
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithotherLiquidcancer
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithotherLiquidcancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithotherLiquidcancer.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithotherLiquidcancerIndicator = Indicators
				.newCountIndicator(
						"newPediWithotherLiquidcancerIndicator",
						newPediWithotherLiquidcancer,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithotherLiquidcancerIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithotherLiquidcancerIndicator",
						newAdultsWithotherLiquidcancer,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		// Other non Hodgkin's lymphoma
		List<ProgramWorkflowState> othernonHodkindLymphomaType = new ArrayList<ProgramWorkflowState>();
		othernonHodkindLymphomaType.add(othernonhodkinslymphoma);
		InStateCohortDefinition patientWithothernonHodkindLymphomaType = Cohorts
				.createInCurrentState("patientWithothernonHodkindLymphomaType",
						othernonHodkindLymphomaType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithothernonHodkindLymphomaType = new CompositionCohortDefinition();
		newPediWithothernonHodkindLymphomaType
				.setName("newPediWithothernonHodkindLymphomaType");
		newPediWithothernonHodkindLymphomaType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newPediWithothernonHodkindLymphomaType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithothernonHodkindLymphomaType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithothernonHodkindLymphomaType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithothernonHodkindLymphomaType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithothernonHodkindLymphomaType.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithothernonHodkindLymphomaType = new CompositionCohortDefinition();
		newAdultsWithothernonHodkindLymphomaType
				.setName("newAdultsWithothernonHodkindLymphomaType");
		newAdultsWithothernonHodkindLymphomaType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithothernonHodkindLymphomaType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithothernonHodkindLymphomaType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithothernonHodkindLymphomaType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithothernonHodkindLymphomaType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithothernonHodkindLymphomaType
				.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithothernonHodkindLymphomaTypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithothernonHodkindLymphomaTypeIndicator",
						newPediWithothernonHodkindLymphomaType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithothernonHodkindLymphomaTypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithothernonHodkindLymphomaTypeIndicator",
						newAdultsWithothernonHodkindLymphomaType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		// Other solid cancer
		List<ProgramWorkflowState> othersolidcancerType = new ArrayList<ProgramWorkflowState>();
		othersolidcancerType.add(othersolidcancer);
		InStateCohortDefinition patientWithotherSolidcancerType = Cohorts
				.createInCurrentState("patientWithotherSolidcancerType",
						othersolidcancerType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithotherLiquidcancerType = new CompositionCohortDefinition();
		newPediWithotherLiquidcancerType
				.setName("newPediWithotherLiquidcancerType");
		newPediWithotherLiquidcancerType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newPediWithotherLiquidcancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithotherLiquidcancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithotherLiquidcancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithotherSolidcancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithotherLiquidcancerType.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithotherLiquidcancerType = new CompositionCohortDefinition();
		newAdultsWithotherLiquidcancerType
				.setName("newAdultsWithotherLiquidcancerType");
		newAdultsWithotherLiquidcancerType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithotherLiquidcancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithotherLiquidcancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithotherLiquidcancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithotherSolidcancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithotherLiquidcancerType.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithotherLiquidcancerTypeeIndicator = Indicators
				.newCountIndicator(
						"newPediWithotherLiquidcancerTypeeIndicator",
						newPediWithotherLiquidcancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithotherLiquidcancerTypeeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithotherLiquidcancerTypeeIndicator",
						newAdultsWithotherLiquidcancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		// Prostate cancer
		List<ProgramWorkflowState> prostatecancerType = new ArrayList<ProgramWorkflowState>();
		prostatecancerType.add(prostatecancer);
		InStateCohortDefinition patientsWithprostatecancerType = Cohorts
				.createInCurrentState("patientsWithprostatecancerType",
						prostatecancerType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithprostatecancerType = new CompositionCohortDefinition();
		newPediWithprostatecancerType.setName("newPediWithprostatecancerType");
		newPediWithprostatecancerType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithprostatecancerType.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediWithprostatecancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithprostatecancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientsWithprostatecancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithprostatecancerType.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithprostatecancerType = new CompositionCohortDefinition();
		newAdultsWithprostatecancerType
				.setName("newAdultsWithprostatecancerType");
		newAdultsWithprostatecancerType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newAdultsWithprostatecancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithprostatecancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithprostatecancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientsWithprostatecancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithprostatecancerType.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithprostatecancerTypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithprostatecancerTypeIndicator",
						newPediWithprostatecancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithprostatecancerTypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithprostatecancerTypeIndicator",
						newAdultsWithprostatecancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		// stomach cancer
		List<ProgramWorkflowState> stomachcancerType = new ArrayList<ProgramWorkflowState>();
		stomachcancerType.add(stomachcancer);
		InStateCohortDefinition patientsWithstomachcancer = Cohorts
				.createInCurrentState("patientsWithstomachcancer",
						stomachcancerType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithpatientsWithstomachcancer = new CompositionCohortDefinition();
		newPediWithpatientsWithstomachcancer
				.setName("newPediWithpatientsWithstomachcancer");
		newPediWithpatientsWithstomachcancer.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newPediWithpatientsWithstomachcancer.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithpatientsWithstomachcancer
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithpatientsWithstomachcancer
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientsWithstomachcancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithpatientsWithstomachcancer.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithpatientsWithstomachcancer = new CompositionCohortDefinition();
		newAdultsWithpatientsWithstomachcancer
				.setName("newAdultsWithpatientsWithstomachcancere");
		newAdultsWithpatientsWithstomachcancer.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithpatientsWithstomachcancer.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithpatientsWithstomachcancer
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithpatientsWithstomachcancer
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientsWithstomachcancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithpatientsWithstomachcancer.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithpatientsWithstomachcancerIndicator = Indicators
				.newCountIndicator(
						"newPediWithpatientsWithstomachcancerIndicator",
						newPediWithpatientsWithstomachcancer,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		CohortIndicator newAdultsWithpatientsWithstomachcancerIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithpatientsWithstomachcancerIndicator",
						newAdultsWithpatientsWithstomachcancer,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		// =======================================================================================//
		// A14 Total # of patients exited for palliation-only care within the
		// reporting period */
		// =====================================================================================//
		SqlCohortDefinition patientWithOncologyOutcomepalliationOnly = Cohorts.getPatientsWithOutcomeprogramEndReasons("patientWithOncologyOutcomepalliationOnly",oncologyprogramendreason, palliationonlycare);
		EncounterCohortDefinition patientWithExitform = Cohorts.createEncounterBasedOnForms("patientWithDemoandIntakeForms",startDateEndDate, exitforms);

		CompositionCohortDefinition pediExitedforPalliationOnlyCare = new CompositionCohortDefinition();
		pediExitedforPalliationOnlyCare.setName("pediExitedforPalliationOnlyCare");
		pediExitedforPalliationOnlyCare.addParameter(new Parameter("startDate","startDate", Date.class));
		pediExitedforPalliationOnlyCare.addParameter(new Parameter("endDate","endDate", Date.class));
		pediExitedforPalliationOnlyCare.getSearches().put("1",new Mapped<CohortDefinition>(inOncologyProgram, null));
		pediExitedforPalliationOnlyCare.getSearches().put("2",new Mapped<CohortDefinition>(patientWithOncologyOutcomepalliationOnly,
			ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		pediExitedforPalliationOnlyCare.getSearches().put("3",new Mapped<CohortDefinition>(under15Cohort, null));
		/*pediExitedforPalliationOnlyCare.getSearches().put("4",new Mapped<CohortDefinition>(patientWithExitform,
			ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		*/pediExitedforPalliationOnlyCare.setCompositionString("1 AND 2 AND 3");

		CompositionCohortDefinition adultsExitedforPalliationOnlyCare = new CompositionCohortDefinition();
		adultsExitedforPalliationOnlyCare.setName("adultsExitedforPalliationOnlyCare");
		adultsExitedforPalliationOnlyCare.addParameter(new Parameter("startDate", "startDate", Date.class));
		adultsExitedforPalliationOnlyCare.addParameter(new Parameter("endDate","endDate", Date.class));
		adultsExitedforPalliationOnlyCare.getSearches().put("1",new Mapped<CohortDefinition>(inOncologyProgram, null));
		adultsExitedforPalliationOnlyCare.getSearches().put("2",new Mapped<CohortDefinition>(patientWithOncologyOutcomepalliationOnly,
			ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		adultsExitedforPalliationOnlyCare.getSearches().put("3",new Mapped<CohortDefinition>(over15Cohort, null));
		/*adultsExitedforPalliationOnlyCare.getSearches().put("4",new Mapped<CohortDefinition>(patientWithExitform,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		*/adultsExitedforPalliationOnlyCare.setCompositionString("1 AND 2 AND 3");

		CohortIndicator pediExitedforPalliationOnlyCareIndicator = Indicators.newCountIndicator("pediExitedforPalliationOnlyCareIndicator",
				pediExitedforPalliationOnlyCare,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

		CohortIndicator adultsExitedforPalliationOnlyCareIndicator = Indicators.newCountIndicator("adultsExitedforPalliationOnlyCareIndicator",
						adultsExitedforPalliationOnlyCare,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

		// ======================================================//
		// A15 Total # of deaths within the reporting period * /
		// =====================================================//

		CodedObsCohortDefinition exitedpatientdied = Cohorts.createCodedObsCohortDefinition("exitedpatientdied", onOrAfterOnOrBefore,reasonForExitingCare, patientDied, SetComparator.IN,TimeModifier.LAST);
		CompositionCohortDefinition pediexitedfromcarewithDeathreasons = new CompositionCohortDefinition();
		pediexitedfromcarewithDeathreasons.setName("pediexitedfromcarewithDeathreasons");
		pediexitedfromcarewithDeathreasons.addParameter(new Parameter("startDate", "startDate", Date.class));
		pediexitedfromcarewithDeathreasons.addParameter(new Parameter("endDate", "endDate", Date.class));
		pediexitedfromcarewithDeathreasons.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		pediexitedfromcarewithDeathreasons.addParameter(new Parameter("OnOrBefore", "OnOrBefore", Date.class));
		pediexitedfromcarewithDeathreasons.getSearches().put("1",new Mapped<CohortDefinition>(inOncologyProgram, null));
		pediexitedfromcarewithDeathreasons.getSearches().put("2",new Mapped<CohortDefinition>(patientWithExitform,
			ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		pediexitedfromcarewithDeathreasons.getSearches().put("3",new Mapped<CohortDefinition>(under15Cohort, null));
		pediexitedfromcarewithDeathreasons.getSearches().put("4",new Mapped<CohortDefinition>(exitedpatientdied,
				ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},OnOrBefore=${OnOrBefore}")));
		pediexitedfromcarewithDeathreasons.setCompositionString("1 AND 2 AND 3 AND 4");

		CompositionCohortDefinition adultsexitedfromcarewithDeathreasons = new CompositionCohortDefinition();
		adultsexitedfromcarewithDeathreasons.setName("adultsexitedfromcarewithDeathreasons");
		adultsexitedfromcarewithDeathreasons.addParameter(new Parameter("startDate", "startDate", Date.class));
		adultsexitedfromcarewithDeathreasons.addParameter(new Parameter("endDate", "endDate", Date.class));
		adultsexitedfromcarewithDeathreasons.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		adultsexitedfromcarewithDeathreasons.addParameter(new Parameter("OnOrBefore", "OnOrBefore", Date.class));
		adultsexitedfromcarewithDeathreasons.getSearches().put("1",new Mapped<CohortDefinition>(inOncologyProgram, null));
		adultsexitedfromcarewithDeathreasons.getSearches().put("2",new Mapped<CohortDefinition>(patientWithExitform,
			ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		adultsexitedfromcarewithDeathreasons.getSearches().put("3",new Mapped<CohortDefinition>(over15Cohort, null));
		adultsexitedfromcarewithDeathreasons.getSearches().put("4",new Mapped<CohortDefinition>(exitedpatientdied, 
				ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},OnOrBefore=${OnOrBefore}")));
		adultsexitedfromcarewithDeathreasons.setCompositionString("1 AND 2 AND 3 AND 4");

		CohortIndicator pediexitedfromcarewithDeathreasonsIndicator = Indicators
				.newCountIndicator(
						"pediexitedfromcarewithDeathreasonsIndicator",
						pediexitedfromcarewithDeathreasons,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate},onOrAfter=${startDate},OnOrBefore=${endDate}"));

		CohortIndicator adultsexitedfromcarewithDeathreasonsIndicator = Indicators
				.newCountIndicator(
						"adultsexitedfromcarewithDeathreasonsIndicator",
						adultsexitedfromcarewithDeathreasons,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate},onOrAfter=${startDate},OnOrBefore=${endDate}"));

		// =================================================
		// Adding columns to data set definition //
		// =================================================

		dsd.addColumn(
				"D1Q1A",
				"% of peds demographics of new cases in the last quarter",
				new Mapped(
						under15DemoIntakeFormsIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D1Q1C",
				"% of adults demographics of new cases in the last quarter",
				new Mapped(
						over15DemoIntakeFormsIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D1Q2A",
				"% of females peds demographics of new cases in the last quarter",
				new Mapped(
						newCasesFemalesUnder15Indicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D1Q2C",
				"% of females adults demographics of new cases in the last quarter",
				new Mapped(
						newCasesFemalesOver15Indicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D2Q1A",
				"% of males peds demographics of new cases in the last quarter",
				new Mapped(
						newCasesmalesUnder15Indicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D2Q1C",
				"% of males adults demographics of new cases in the last quarter",
				new Mapped(
						newCasesmalesOver15Indicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn("D5Q1A","% of new cases peds recommended for socioeconomic assistance at intake",new Mapped(pediWithsocioeconomicrecomendationIndicator,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D5Q1C","% of new cases adults recommended for socioeconomic assistance at intake",new Mapped(adultsWithSocioassistanceRecIndicator,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		
		dsd.addColumn("D5Q2A","% of ped new cases with missing socioeconomic assistance recommendation ",new Mapped(pediWithMissingsocioeconomicrecIndicator,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		
		dsd.addColumn("D5Q2C","% of adults new cases with missing socioeconomic assistance recommendation",new Mapped(adultsWithMissingSocioassistanceRecIndicator,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		
		dsd.addColumn("D6Q1A","% pedi breakdown of new cases with Health Center at intake",new Mapped(pedPatientsreferredForHCIndicator,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q1C","% adults breakdown of new cases with Health Center as referral facility type  at intake",new Mapped (adultsPatientsreferredForHCIndicator,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
        dsd.addColumn("D6Q2A","% pedi breakdown of new cases with District Hospital at intake",new Mapped(pedsreferredForDistrictHospitalIndicator,
						ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q2C","% adults breakdown of new cases with District Hospital at intake",new Mapped(adultsreferreToDistrictHospitalIndicator,
						ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q3A","% pedi breakdown of new referral cases at intake",new Mapped(pedsreferredforReferralHospitalIndicator,
								ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q3C","% adults breakdown of new referral cases at intake",new Mapped(adultsreferredforReferralHospitalIndicator,
								ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q4A","% pedi breakdown of new cases with other referral types at intake",new Mapped(pedsWithOtherReferralIndicator,
										ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q4C","% adults breakdown of new cases with other referral types at intake",new Mapped(adultsWithOtherReferralIndicator,
										ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q5A","% pedi breakdown of new cases not referred at intake",new Mapped(pedwithNoreferralTypeIndicator,
												ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q5C","% adults breakdown of new cases not referred at intake",new Mapped(adultswithreferralTypeIndicator,
												ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q6A","% pedi breakdown of new cases with missing references at intake",new Mapped(pediWithMissingReferencesIndi,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q6C","% adults breakdown of new cases with missig references at intake",new Mapped(adultsWithMissingReferencesIndi,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		
		dsd.addColumn("D7Q1A","% of pedi new referral cases inside Intake district",new Mapped(pedireferredinsideRwandanDistrictIndi,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D7Q1C","% of adults new referral cases inside Intake district",new Mapped(adultsrefferedinsideRwandaDistrictInd,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D7Q2A","% of pedi new referral cases outside Intake district",new Mapped(pedireferredoutsideIntakedistrictInRwandaIndi,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D7Q2C","% of adults new referral cases outside Intake district",new Mapped(adultsreferredoutsideIntakedistrictInRwandaIndi,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D7Q3A","% of pedi new referral cases referred from outside of Rwanda ",new Mapped(pedireferredOutsideOfRwandaIndicator,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D7Q3C","% of adults new referral cases referred from outside of Rwanda ",new Mapped(adultsreferredOutsideOfRwandaIndicator,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		
		dsd.addColumn("D7QA","% of total pedi new referral cases referred",new Mapped(totalpedinewCasesThatareReferredIndicators,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D7QC","% of total adults new referral cases referred",new Mapped(totaladultsnewCasesThatareReferredIndicators,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		
		dsd.addColumn("D8Q1A","% breakdown of Kayonza new pedi cases home district at intake",new Mapped(pediLocatedInKayonzaDistrictIndi,
						ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D8Q1C","% breakdown of Kayonza new adults cases home district at intake",new Mapped(adultsLocatedInKayonzaDistrictIndi,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		
		dsd.addColumn("D8Q2A","% breakdown of Burera new cases home district at intake",new Mapped(pediLocatedInBureraDistrictIndi,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D8Q2C","% breakdown of Burera new cases home district at intake",new Mapped(adultsLocatedInBureraDistrictIndi,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		
		dsd.addColumn("D8Q3A","% breakdown of Kirehe new pedi cases home district at intake",new Mapped(pediLocatedInKireheDistrictIndicator,
						ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D8Q3C","% breakdown of Kirehe new adults cases home district at intake",new Mapped(adultsLocatedInKireheDistrictIndicator,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D8Q4A","% breakdown of Kigali new pedi cases home district at intake",new Mapped(pediLocatedInKigaliDistrictIndicator,
						ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D8Q4C","% breakdown of Kigali new adults cases home district at intake",new Mapped(adultsLocatedInKigaliDistrictIndicator,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		
		dsd.addColumn("D8Q5A","% breakdown of other rwandan districts new pedi cases home district at intake",new Mapped(pediLocatedInOtherRwandanDistrictsIndicator,
						ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		
		dsd.addColumn("D8Q5C","% breakdown of other rwandan districts new adults cases home district at intake",new Mapped(adultsLocatedInOtherRwandanDistrictsIndicator,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");

		
		dsd.addColumn("D8Q6A","% breakdown of new pedi cases with missing address at intake",new Mapped(pediLocatedWithMissingIndicator,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D8Q6C","% breakdown of new adults cases with missing address at intake",new Mapped(adultsLocatedWithMissingIndicator,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		
		dsd.addColumn("D8Q7A","% breakdown of new pedi cases outside of Rwandan home district at intake",new Mapped(pedifromOutsideOfRwandaIndicator,
						ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D8Q7C","% breakdown of new adults cases outside of Rwandan home district at intake",new Mapped(adultsfromOutsideOfRwandaIndicator,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");

		
		
		dsd.addColumn(
				"D9Q1A",
				"Of the new cases with HIV status documented at intake, pediatric ",
				new Mapped(
						newCasespediWithHiVstatusIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D9Q1C",
				"Of the new cases with HIV status documented at intake, adults ",
				new Mapped(
						newCasesadultsWithHiVstatusIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D9Q2A",
				"% Of the new cases with HIV status documented at intake, % of pedi with a positive HIV status",
				new Mapped(
						newCasespediWithHiVPositivestatusIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D9Q2C",
				"% Of the new cases with HIV status documented at intake, % of adults with a positive HIV status",
				new Mapped(
						newCasesadultsWithHiVPositivestatusIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn("A1Q1A","Total # of pediatric patients ever enrolled in program at the end of the review quarter",
				new Mapped(pedsWitClinicalIntakeintheReviewQuarterIndicator,null),"");
		dsd.addColumn("A1Q1C","Total # of adults patients ever enrolled in program at the end of the review quarter",
				new Mapped(adultsWitClinicalIntakeintheReviewQuarterIndicator,null),"");
		
		dsd.addColumn(
				"A2Q1A",
				"Total # of pediatric patients ever enrolled in program at the end of the review quarter",
				new Mapped(
						newPediNotSuspectedCancerAtIntakeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A2Q1C",
				"Total # of adults patients ever enrolled in program at the end of the review quarter",
				new Mapped(
						newAdultsNotSuspectedCancerAtIntakeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3QA",
				"Total # of new suspected  pedi cancer cases at intake in the last quarter",
				new Mapped(
						newPediSuspectedCancerAtIntakeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3QC",
				"Total # of new suspected adults cancer cases at intake in the last quarter",
				new Mapped(
						newAdultSuspectedCancerAtIntakeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q1A",
				"Total # of new pedi suspected of Acute lymphoblastic leukemia cancer at intake in the last quarter",
				new Mapped(
						newPediWithAcutelymphoblasticleukemiaIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q1C",
				"Total # of new adults suspected of Acute lymphoblastic leukemia cancer at intake in the last quarter",
				new Mapped(
						newAdultsWithAcutelymphoblasticleukemiaIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q2A",
				"Total # of new pedi suspected  of Breast Cancer at intake in the last quarter",
				new Mapped(
						newPediWithbreastcancertypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q2C",
				"Total # of new adults suspected  of Breast Cancer at intake in the last quarter",
				new Mapped(
						newAdultsWithbreastcancertypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q3A",
				"Total # of new pedi suspected  Burkitt's lymphoma at intake in the last quarter",
				new Mapped(
						newPediWithburkitlymphomacancerIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q3C",
				"Total # of new adults suspected  Burkitt's lymphoma at intake in the last quarter",
				new Mapped(
						newAdultsWithburkitlymphomacancerIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q4A",
				"Total # of new suspected pedi cancer unknown type at intake in the last quarter",
				new Mapped(
						newPediWithcancerunknowntypeCIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q4C",
				"Total # of new suspected adults of cancer unknown type at intake in the last quarter",
				new Mapped(
						newAdultsWithcancerunknowntypeCIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q5A",
				"Total # of new pedi suspected Cervical cancer at intake in the last quarter",
				new Mapped(
						newPediWithcervicalCancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q5C",
				"Total # of new adults suspected Cervical cancer at intake in the last quarter",
				new Mapped(
						newAdultsWithcervicalCancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q6A",
				"Total # of new adults suspected chronic Myelogenous leukemia at intake in the last quarter",
				new Mapped(
						newPediWithchronicmyelogousleukemiacIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q6C",
				"Total # of new adults suspected chronic Myelogenous leukemia at intake in the last quarter",
				new Mapped(
						newAdultsWithchronicmyelogousleukemiacIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q7A",
				"Total # of new suspected  pedi with Colo-rectal cancer at intake in the last quarter",
				new Mapped(
						newPediWithcolorectalcancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q7C",
				"Total # of new suspected adults with Colo-rectal cancer at intake in the last quarter",
				new Mapped(
						newAdultsWithcolorectalcancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q8A",
				"Total # of new suspected  pedi with Head and neck cancer at intake in the last quarter",
				new Mapped(
						newPediWithheadandneckcancertypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q8C",
				"Total # of new suspected adults with Head and neck cancer at intake in the last quarter",
				new Mapped(
						newAdultsWithheadandneckcancertypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q9A",
				"Total # of new pedi with Hodgkin's lymphoma suspected at intake in the last quarter",
				new Mapped(
						newPediWithhodkinlymphomacancerIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q9C",
				"Total # of new adults with Hodgkin's lymphoma suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithhodkinlymphomacancerIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q10A",
				"Total # of new pedi with Karposi's sarcoma suspected at intake in the last quarter",
				new Mapped(
						newPediWithkarposysarcomaTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q10C",
				"Total # of new adults with Karposi's sarcoma suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithkarposysarcomaTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");

		dsd.addColumn(
				"A3Q11A",
				"Total # of new pedi with Large B-cell lymphoma suspected at intake in the last quarter",
				new Mapped(
						newPediWithlargebcellLymphomaCancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q11C",
				"Total # of new adults with Large B-cell lymphoma suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithlargebcellLymphomaCancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q12A",
				"Total # of new pedi with Lung cancer diagnosis suspected at intake in the last quarter",
				new Mapped(
						newPediWithlungdiagnosistypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q12C",
				"Total # of new adults with Lung cancer diagnosis suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithlungdiagnosistypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q13A",
				"Total # of new pedi with Metastatic cancer suspected at intake in the last quarter",
				new Mapped(
						newPediWithWithmetastaticcancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q13C",
				"Total # of new adults with Metastatic cancer suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithWithmetastaticcancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q14A",
				"Total # of new pedi with Multiple myeloma suspected at intake in the last quarter",
				new Mapped(
						newPediWithmultiplemyelomaTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q14C",
				"Total # of new adults with Multiple myeloma suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithmultiplemyelomaTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q15A",
				"Total # of new pedi with Nephroblastoma suspected at intake in the last quarter",
				new Mapped(
						newPediWithneurobblastomaTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q15C",
				"Total # of new adults with Nephroblastoma suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithneurobblastomaTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q16A",
				"Total # of new pedi with Other liquid cancer suspected at intake in the last quarter",
				new Mapped(
						newPediWithotherLiquidcancerIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q16C",
				"Total # of new adults with Other liquid cancer suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithotherLiquidcancerIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q17A",
				"Total # of new pedi with Other non Hodgkin's lymphoma suspected at intake in the last quarter",
				new Mapped(
						newPediWithothernonHodkindLymphomaTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q17C",
				"Total # of new adults with Other non Hodgkin's lymphoma suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithothernonHodkindLymphomaTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q18A",
				"Total # of new adults with Other liquid cancer suspected at intake in the last quarter",
				new Mapped(
						newPediWithotherLiquidcancerTypeeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q18C",
				"Total # of new adults with Other liquid cancer suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithotherLiquidcancerTypeeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q19A",
				"Total # of new pedi with prostate cancer suspected in last quarter",
				new Mapped(
						newPediWithprostatecancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q19C",
				"Total # of new adults with prostate cancer suspected in the last quarter",
				new Mapped(
						newAdultsWithprostatecancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q20A",
				"Total # of new pedi with Stomach cancer suspected in the last quarter",
				new Mapped(
						newPediWithpatientsWithstomachcancerIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q20C",
				"Total # of new adults with Stomach cancer suspected in the last quarter",
				new Mapped(
						newAdultsWithpatientsWithstomachcancerIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A14Q1A",
				"Total # of pedi patients exited for palliation-only care within the reporting period",
				new Mapped(
						pediExitedforPalliationOnlyCareIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A14Q1C",
				"Total # of adults patients exited for palliation-only care within the reporting period",
				new Mapped(
						adultsExitedforPalliationOnlyCareIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A15Q1A",
				"Total # of pedi deaths within the reporting period",
				new Mapped(
						pediexitedfromcarewithDeathreasonsIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A15Q1C",
				"Total # of adults deaths within the reporting period",
				new Mapped(
						adultsexitedfromcarewithDeathreasonsIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");

	}

	private void setUpProperties() {
		oncologyProgram = gp
				.getProgram(GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		oncologyPrograms.add(oncologyProgram);
		notCancerdiagnosisstate = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.NOT_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		suspecteddiagnosisstate = gp
				.getProgramWorkflowState(
						GlobalPropertiesManagement.SUSPECTED_STATE,
						GlobalPropertiesManagement.ONCOLOGY_DIAGNOSIS_STATUS_PROGRAM_WORKFLOW,
						GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		acutelymphoblasticcancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.ACUTE_LYMPHOBLASTIC_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		breastCancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.BREAST_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		burkittLymphoma = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.BURKITTLYMPHOMA_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		cancerunkowntype = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.CANCERUNKNOWNTYPE_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		cervicalcancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.CERVICAL_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		chronicmyelogenousleukemia = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.CHRONICMYELOGENOUSLEUKEMIA_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		colorectalcancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.COLORECTAL_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		headandneckcancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.HEADANDNECK_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		hodgkinslymphoma = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.HODKINLYPHOMA_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		karposisarcoma = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.KARPOSISARCOMA_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		largebcelllymphoma = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.LARGEBCELLLYMPHOMA_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		lungcancerdiagnosis = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.LUNGCANCERDIAGNOSIS_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		metastaticcancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.METASTATIC_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		multiplemyeloma = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.MULTIPLEMYELOMA_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		neuphroblastoma = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.NEUPHROBLASTOMA_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		otherliquidcancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.OTHERLIQUID_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		othernonhodkinslymphoma = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.OTHERNONHODKINLYMPHOMA_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		othersolidcancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.OTHERSOLID_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		prostatecancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.PROSTATE_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		stomachcancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.STOMACH_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);

		demographicform = gp.getForm(GlobalPropertiesManagement.ONCOLOGY_DEMO_FORM);
		changeinDemographicForm=gp.getForm(GlobalPropertiesManagement.ONCOLOGY_CHANGE_IN_DEMO);
		
		inpatientOncForm = gp.getForm(GlobalPropertiesManagement.ONCOLOGY_INTAKE_INPATIENT_FORM);
		outpatientOncForm = gp.getForm(GlobalPropertiesManagement.ONCOLOGY_INTAKE_OUTPATIENT_FORM);
		
		outpatientclinicvisitform = gp.getForm(GlobalPropertiesManagement.OUTPATIENT_CLINIC_VISITS_FORM);
		exitform = gp.getForm(GlobalPropertiesManagement.ONCOLOGY_EXIT_FORM);
		
		demographicsAndClinicalforms.add(demographicform);
		demographicsAndClinicalforms.add(changeinDemographicForm);
		demographicsAndClinicalforms.add(inpatientOncForm);
		demographicsAndClinicalforms.add(outpatientOncForm);
		
		demographicsForms.add(demographicform);
		demographicsForms.add(changeinDemographicForm);
		
		onlydemographicsForm.add(demographicform);
		
		intakeoutpatientclinicvisitflowform.add(outpatientclinicvisitform);
		clinicalIntakeForms.add(inpatientOncForm);
		clinicalIntakeForms.add(outpatientOncForm);
		exitforms.add(exitform);
		onOrAfterOnOrBefore.add("onOrAfter");
		onOrAfterOnOrBefore.add("onOrBefore");
		startDateEndDate.add("startDate");
		startDateEndDate.add("endDate");
		socioassistance = gp
				.getConcept(GlobalPropertiesManagement.SOCIO_ECONOMIC_ASSISTANCE_RECOMENDED);
		foodassistance = gp
				.getConcept(GlobalPropertiesManagement.NUTRITIONAL_AID);
		schoolassistance = gp
				.getConcept(GlobalPropertiesManagement.SCHOOL_ASSISTANCE);
		transportassistance = gp
				.getConcept(GlobalPropertiesManagement.TRANSPORT_ASSISTANCE);
		clinicianhomevisit=gp.getConcept(GlobalPropertiesManagement.CLINICIAN_HOME_VISIT);
		homeassistance = gp
				.getConcept(GlobalPropertiesManagement.HOME_ASSISTANCE);
		referralType = gp
				.getConcept(GlobalPropertiesManagement.TYPE_OF_REFERRING_CLINIC_OR_HOSPITAL);
		healthclinic = gp.getConcept(GlobalPropertiesManagement.HEALTH_CLINIC);
		districhospital = gp
				.getConcept(GlobalPropertiesManagement.DISTRICT_HOSPITAL);
		referralhospital = gp
				.getConcept(GlobalPropertiesManagement.REFERRAL_HOSPITAL);
		visitType=gp.getConcept(GlobalPropertiesManagement.VISIT_TYPE);
		unscheduledVisitType=gp.getConcept(GlobalPropertiesManagement.UNSCHEDULED_VISIT_TYPE);
		othernonCoded = gp
				.getConcept(GlobalPropertiesManagement.OTHER_NON_CODED);
		notReferred = gp.getConcept(GlobalPropertiesManagement.NOT_REFERRED);
		hivStatus = gp.getConcept(GlobalPropertiesManagement.HIV_STATUS);
		positiveStatus = gp
				.getConcept(GlobalPropertiesManagement.POSITIVE_HIV_TEST_ANSWER);
		locreferralType = gp
				.getConcept(GlobalPropertiesManagement.LOCATION_REFFERAL_TYPE);
		outsideofRwanda = gp
				.getConcept(GlobalPropertiesManagement.REFERRED_OUTSIDE_OF_RWANDA);
		insideintakedistrict = gp
				.getConcept(GlobalPropertiesManagement.REFERRED_AT_INTAKE_DISTRICT);
		outsideintakedistrict = gp
				.getConcept(GlobalPropertiesManagement.REFERRED_OUTSIDE_INTAKE_DISTRICT);
		oncologyprogramendreason = gp
				.getConcept(GlobalPropertiesManagement.ONCOLOGY_PROGRAM_END_REASON);
		palliationonlycare = gp
				.getConcept(GlobalPropertiesManagement.REFERRED_FOR_PALLIATIONONLY_CARE);
		/*cancerrelateddeath = gp
				.getConcept(GlobalPropertiesManagement.CANCER_RELATED_DEATH);
		noncancerrelateddeath = gp
				.getConcept(GlobalPropertiesManagement.NON_CANCER_RELATED_DEATH);
		deathreasonunknown = gp
				.getConcept(GlobalPropertiesManagement.DEATH_UNKNOWN_REASON);
		*/
		reasonForExitingCare= gp.getConcept(GlobalPropertiesManagement.REASON_FOR_EXITING_CARE);
        patientDied = gp.getConcept(GlobalPropertiesManagement.PATIENT_DIED);
        outpatientOncEncounterType=gp.getEncounterType(GlobalPropertiesManagement.OUTPATIENT_ONCOLOGY_ENCOUNTER);
        inpatientOncologyEncounter=gp.getEncounterType(GlobalPropertiesManagement.INPATIENT_ONCOLOGY_ENCOUNTER);
		

	}
}