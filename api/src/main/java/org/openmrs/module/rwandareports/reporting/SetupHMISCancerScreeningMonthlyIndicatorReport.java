package org.openmrs.module.rwandareports.reporting;

import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.*;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.query.encounter.definition.SqlEncounterQuery;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rwandareports.dataset.EncounterIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.dataset.LocationHierachyIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.Indicators;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class SetupHMISCancerScreeningMonthlyIndicatorReport {
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	// properties
	
	private Form oncologyBreastScreeningExamination;
	
	private Form oncologyCervicalScreeningExamination;
	
	private Form mUzimaBreastScreening;
	
	private Form mUzimaCervicalScreening;
	
	private Form oncologyScreeningLabResultsForm;
	
	private Form muzimaOncologyScreeningLabResults;
	
	private List<Form> oncologyScreeningLabResultsForms = new ArrayList<Form>();
	
	private List<Form> screeningExaminationForms = new ArrayList<Form>();
	
	private List<Form> screeningCervicalForms = new ArrayList<Form>();
	
	private List<String> parameterNames = new ArrayList<String>();
	
	private Concept screeningType;
	
	private Concept HPV;
	
	private Concept testResult;
	
	private Concept HPVpositive;
	
	private Concept HPVNegative;
	
	private List<Concept> testResults = new ArrayList<Concept>();
	
	private List<Concept> positiveTestResults = new ArrayList<Concept>();
	
	private Form mUzimaCervicalCancerScreeningFollowup;
	
	private Form OncologyCervicalScreeningFollowUp;
	
	private List<Form> cervicalCancerScreeningFollowupAndExaminationForms = new ArrayList<Form>();;
	
	private Concept typeOfVIAPerformed;
	
	private Concept VIATriage;
	
	private List<Concept> VIATriageInList = new ArrayList<Concept>();
	
	private Concept VIAResults;
	
	private Concept VIAAndEligibleForThermalAblation;
	
	private Concept VIAAndEligibleForLEEP;
	
	private List<Concept> VIAAndEligibleResults = new ArrayList<Concept>();
	
	private Form mUzimaBreastCancerScreening;
	
	private List<Form> breastCancerScreeningForms = new ArrayList<Form>();
	
	private Concept reasonForBreastExam;
	
	private Concept breastSymptoms;
	
	private Concept screening;
	
	private Concept breastExamination;
	
	private Concept ABNORMAL;
	
	private Concept nextStep;
	
	private Concept referredTo;
	
	private Concept furtherManagement;
	
	private Concept medicalImaging;
	
	private Concept BIOPSY;
	
	private Concept OTHERNONCODED;
	
	private Concept breastUltrasound;
	
	private Concept YES;
	
	private Concept solidMass;
	
	private Concept intermediate;
	
	private Concept highSuspiciousForMalignancy;
	
	private Concept proceduresDone;
	
	private Concept PAPANICOLAOUSMEAR;
	
	private Concept NORMAL;
	
	private Form muzimaOncologyScreeningDiagnosis;
	
	private Form oncologyScreeningDiagnosis;
	
	private List<Form> diagnosisScreeningForms = new ArrayList<Form>();
	
	private Concept CONFIRMEDDIAGNOSIS;
	
	private Concept confirmedCancerDiagnosis;
	
	private Concept cervicalCancer;
	
	private Concept breastCancer;
	
	private Form oncologyBreastCancerScreeningTransferIn;
	
	private List<Form> oncologyBreastCancerScreeningTransferInList = new ArrayList<Form>();
	
	private Concept VIANegative;
	
	private List<Concept> VIANegativeInList = new ArrayList<Concept>();;
	
	private Concept VIAScreen;
	
	private List<Concept> VIAScreenInList = new ArrayList<Concept>();
	
	private Concept typeOfTreatmentPerformed;
	
	private Concept thermalAblation;
	
	private Concept cryotherapy;
	
	private List<Concept> thermalAblationAndCryotherapyList = new ArrayList<Concept>();
	
	private Concept reasonsForReferral;
	
	private Concept loopElectrosurgicalExcisionProcedure;
	
	private List<Concept> loopElectrosurgicalExcisionProcedureInList = new ArrayList<Concept>();
	
	private List<Concept> LEEPAndColposcopy = new ArrayList<Concept>();
	
	private Concept suspectedCancer;
	
	private List<Concept> suspectedCancerInList = new ArrayList<Concept>();
	
	private Concept biopsyperformed;
	
	private Concept yes;
	
	private List<Concept> yesInList = new ArrayList<Concept>();
	
	/* private Form oncologyCervicalCancerScreeningTransferIn;
	 private  List<Form> oncologyCervicalCancerScreeningTransferInList=new ArrayList<Form>();
	*/
	private Concept hivStatus;
	
	private Concept positive;
	
	private Concept reasonsForReferralIn;
	
	public void setup() throws Exception {
		
		setUpProperties();
		
		Properties properties = new Properties();
		properties.setProperty("hierarchyFields", "countyDistrict:District");
		
		// Quarterly Report Definition: Start
		
		ReportDefinition monthlyRd = new ReportDefinition();
		monthlyRd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		monthlyRd.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		monthlyRd.addParameter(new Parameter("location", "Location", AllLocation.class, properties));
		
		monthlyRd.setName("ONC - HMIS Cancer Screening Monthly Indicator Report");
		
		monthlyRd.addDataSetDefinition(createMonthlyLocationDataSet(),
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
		
		// Monthly Report Definition: End
		
		EncounterCohortDefinition ScreeningExaminationEncounter = Cohorts.createEncounterBasedOnForms(
		    "ScreeningExaminationEncounter", parameterNames, screeningExaminationForms);
		
		monthlyRd.setBaseCohortDefinition(ScreeningExaminationEncounter,
		    ParameterizableUtil.createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}"));
		
		Helper.saveReportDefinition(monthlyRd);
		
		ReportDesign mothlyDesign = Helper.createRowPerPatientXlsOverviewReportDesign(monthlyRd,
		    "ONC_HMIS_Cancer_Screening_Monthly.xls", "HMIS Cancer Screening Monthly Indicator Report (Excel)", null);
		Properties monthlyProps = new Properties();
		monthlyProps.put("repeatingSections", "sheet:1,dataset:Encounter HMIS Cancer Screening Data Set");
		monthlyProps.put("sortWeight", "5000");
		mothlyDesign.setProperties(monthlyProps);
		Helper.saveReportDesign(mothlyDesign);
		
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("HMIS Cancer Screening Monthly Indicator Report (Excel)".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		Helper.purgeReportDefinition("ONC - HMIS Cancer Screening Monthly Indicator Report");
		
	}
	
	//Create Monthly Encounter Data set
	
	public LocationHierachyIndicatorDataSetDefinition createMonthlyLocationDataSet() {
		
		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
		        createEncounterMonthlyBaseDataSet());
		ldsd.addBaseDefinition(createMonthlyBaseDataSet());
		ldsd.setName("Encounter HMIS Cancer Screening Data Set");
		ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
		
		return ldsd;
	}
	
	private EncounterIndicatorDataSetDefinition createEncounterMonthlyBaseDataSet() {
		
		EncounterIndicatorDataSetDefinition eidsd = new EncounterIndicatorDataSetDefinition();
		eidsd.setName("eidsd");
		eidsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		eidsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		createMonthlyIndicators(eidsd);
		return eidsd;
	}
	
	private void createMonthlyIndicators(EncounterIndicatorDataSetDefinition dsd) {
		
	}
	
	// create monthly cohort Data set
	
	private CohortIndicatorDataSetDefinition createMonthlyBaseDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Monthly Cohort Data Set");
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		createMonthlyIndicators(dsd);
		return dsd;
	}
	
	private void createMonthlyIndicators(CohortIndicatorDataSetDefinition dsd) {
		
		SqlCohortDefinition screenedForCervicalCancerWithHPV = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate("screenedForCervicalCancerWithHPV",
		            screeningCervicalForms, screeningType, HPV);
		
		GenderCohortDefinition female = Cohorts.createFemaleCohortDefinition("female");
		
		SqlCohortDefinition hivPositivePatient = Cohorts.getPatientsWithCodedObsEver("hivPositivePatient", hivStatus,
		    positive);
		
		// ==================== C1 ====================================================
		CompositionCohortDefinition femaleScreenedForCervicalCancerWithHPV = new CompositionCohortDefinition();
		femaleScreenedForCervicalCancerWithHPV.setName("femaleScreenedForCervicalCancerWithHPV");
		femaleScreenedForCervicalCancerWithHPV.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleScreenedForCervicalCancerWithHPV.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleScreenedForCervicalCancerWithHPV.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithHPV, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithHPV.getSearches().put("2", new Mapped<CohortDefinition>(female, null));
		femaleScreenedForCervicalCancerWithHPV.setCompositionString("1 and 2");
		
		CohortIndicator femaleScreenedForCervicalCancerWithHPVIndicator = Indicators.newCountIndicator(
		    "femaleScreenedForCervicalCancerWithHPVIndicator", femaleScreenedForCervicalCancerWithHPV,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C1",
		    "Number of women  screened for cervical cancer with HPV",
		    new Mapped(femaleScreenedForCervicalCancerWithHPVIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivPositifFemaleScreenedForCervicalCancerWithHPV = new CompositionCohortDefinition();
		hivPositifFemaleScreenedForCervicalCancerWithHPV.setName("femaleScreenedForCervicalCancerWithHPV");
		hivPositifFemaleScreenedForCervicalCancerWithHPV.addParameter(new Parameter("startDate", "startDate", Date.class));
		hivPositifFemaleScreenedForCervicalCancerWithHPV.addParameter(new Parameter("endDate", "endDate", Date.class));
		hivPositifFemaleScreenedForCervicalCancerWithHPV.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithHPV, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivPositifFemaleScreenedForCervicalCancerWithHPV.getSearches().put("2", new Mapped<CohortDefinition>(female, null));
		hivPositifFemaleScreenedForCervicalCancerWithHPV.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivPositifFemaleScreenedForCervicalCancerWithHPV.setCompositionString("1 and 2 and 3");
		
		CohortIndicator hivPositifFemaleScreenedForCervicalCancerWithHPVIndicator = Indicators.newCountIndicator(
		    "hivPositifFemaleScreenedForCervicalCancerWithHPVIndicator", hivPositifFemaleScreenedForCervicalCancerWithHPV,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C1P",
		    "HIV Positive Number of women  screened for cervical cancer with HPV",
		    new Mapped(hivPositifFemaleScreenedForCervicalCancerWithHPVIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivNegaveFemaleScreenedForCervicalCancerWithHPV = new CompositionCohortDefinition();
		hivNegaveFemaleScreenedForCervicalCancerWithHPV.setName("femaleScreenedForCervicalCancerWithHPV");
		hivNegaveFemaleScreenedForCervicalCancerWithHPV.addParameter(new Parameter("startDate", "startDate", Date.class));
		hivNegaveFemaleScreenedForCervicalCancerWithHPV.addParameter(new Parameter("endDate", "endDate", Date.class));
		hivNegaveFemaleScreenedForCervicalCancerWithHPV.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithHPV, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegaveFemaleScreenedForCervicalCancerWithHPV.getSearches().put("2", new Mapped<CohortDefinition>(female, null));
		hivNegaveFemaleScreenedForCervicalCancerWithHPV.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivNegaveFemaleScreenedForCervicalCancerWithHPV.setCompositionString("1 and 2 and (not 3)");
		
		CohortIndicator hivNegaveFemaleScreenedForCervicalCancerWithHPVIndicator = Indicators.newCountIndicator(
		    "hivNegaveFemaleScreenedForCervicalCancerWithHPVIndicator", hivNegaveFemaleScreenedForCervicalCancerWithHPV,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C1N",
		    "HIV Negative Number of women  screened for cervical cancer with HPV",
		    new Mapped(hivNegaveFemaleScreenedForCervicalCancerWithHPVIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//============================================== C2 =============================================
		SqlCohortDefinition screenedForCervicalCancerWithHPVResult = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate("screenedForCervicalCancerWithHPVResult",
		            oncologyScreeningLabResultsForms, testResult, testResults);
		
		CompositionCohortDefinition femaleScreenedForCervicalCancerWithHPVResult = new CompositionCohortDefinition();
		femaleScreenedForCervicalCancerWithHPVResult.setName("femaleScreenedForCervicalCancerWithHPVResult");
		femaleScreenedForCervicalCancerWithHPVResult.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleScreenedForCervicalCancerWithHPVResult.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleScreenedForCervicalCancerWithHPVResult.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithHPVResult, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithHPVResult.getSearches().put("2", new Mapped<CohortDefinition>(female, null));
		femaleScreenedForCervicalCancerWithHPVResult.setCompositionString("1 and 2");
		
		CohortIndicator femaleScreenedForCervicalCancerWithHPVResultIndicator = Indicators.newCountIndicator(
		    "femaleScreenedForCervicalCancerWithHPVResultIndicator", femaleScreenedForCervicalCancerWithHPVResult,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C2",
		    "Number of women  screened for cervical cancer with HPV results available this month",
		    new Mapped(femaleScreenedForCervicalCancerWithHPVResultIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivfemaleScreenedForCervicalCancerWithHPVResult = new CompositionCohortDefinition();
		hivfemaleScreenedForCervicalCancerWithHPVResult.setName("hivfemaleScreenedForCervicalCancerWithHPVResult");
		hivfemaleScreenedForCervicalCancerWithHPVResult.addParameter(new Parameter("startDate", "startDate", Date.class));
		hivfemaleScreenedForCervicalCancerWithHPVResult.addParameter(new Parameter("endDate", "endDate", Date.class));
		hivfemaleScreenedForCervicalCancerWithHPVResult.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithHPVResult, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivfemaleScreenedForCervicalCancerWithHPVResult.getSearches().put("2", new Mapped<CohortDefinition>(female, null));
		hivfemaleScreenedForCervicalCancerWithHPVResult.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivfemaleScreenedForCervicalCancerWithHPVResult.setCompositionString("1 and 2 and 3");
		
		CohortIndicator hivfemaleScreenedForCervicalCancerWithHPVResultIndicator = Indicators.newCountIndicator(
		    "hivfemaleScreenedForCervicalCancerWithHPVResultIndicator", hivfemaleScreenedForCervicalCancerWithHPVResult,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C2P",
		    " HIV Positive Number of women  screened for cervical cancer with HPV results available this month",
		    new Mapped(hivfemaleScreenedForCervicalCancerWithHPVResultIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivNegavefemaleScreenedForCervicalCancerWithHPVResult = new CompositionCohortDefinition();
		hivNegavefemaleScreenedForCervicalCancerWithHPVResult
		        .setName("hivNegavefemaleScreenedForCervicalCancerWithHPVResult");
		hivNegavefemaleScreenedForCervicalCancerWithHPVResult.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		hivNegavefemaleScreenedForCervicalCancerWithHPVResult.addParameter(new Parameter("endDate", "endDate", Date.class));
		hivNegavefemaleScreenedForCervicalCancerWithHPVResult.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithHPVResult, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegavefemaleScreenedForCervicalCancerWithHPVResult.getSearches().put("2",
		    new Mapped<CohortDefinition>(female, null));
		hivNegavefemaleScreenedForCervicalCancerWithHPVResult.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivNegavefemaleScreenedForCervicalCancerWithHPVResult.setCompositionString("1 and 2 and (not 3)");
		
		CohortIndicator hivNegavefemaleScreenedForCervicalCancerWithHPVResultIndicator = Indicators.newCountIndicator(
		    "hivNegavefemaleScreenedForCervicalCancerWithHPVResultIndicator",
		    hivNegavefemaleScreenedForCervicalCancerWithHPVResult,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C2N",
		    " hiv Negave Number of women  screened for cervical cancer with HPV results available this month",
		    new Mapped(hivNegavefemaleScreenedForCervicalCancerWithHPVResultIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//===================================== c3 ======================================
		SqlCohortDefinition screenedForCervicalCancerWithPositiveHPVResult = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate("screenedForCervicalCancerWithHPVResult",
		            cervicalCancerScreeningFollowupAndExaminationForms, testResult, positiveTestResults);
		CompositionCohortDefinition femaleScreenedForCervicalCancerWithPositiveHPVResult = new CompositionCohortDefinition();
		femaleScreenedForCervicalCancerWithPositiveHPVResult.setName("femaleScreenedForCervicalCancerWithPositiveHPVResult");
		femaleScreenedForCervicalCancerWithPositiveHPVResult
		        .addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleScreenedForCervicalCancerWithPositiveHPVResult.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleScreenedForCervicalCancerWithPositiveHPVResult.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithPositiveHPVResult, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithPositiveHPVResult.getSearches().put("2",
		    new Mapped<CohortDefinition>(female, null));
		femaleScreenedForCervicalCancerWithPositiveHPVResult.setCompositionString("1 and 2");
		
		CohortIndicator femaleScreenedForCervicalCancerWithPositiveHPVResultIndicator = Indicators.newCountIndicator(
		    "femaleScreenedForCervicalCancerWithPositiveHPVResultIndicator",
		    femaleScreenedForCervicalCancerWithPositiveHPVResult,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C3",
		    "Number of women  tested HPV positive",
		    new Mapped(femaleScreenedForCervicalCancerWithPositiveHPVResultIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivFemaleScreenedForCervicalCancerWithPositiveHPVResult = new CompositionCohortDefinition();
		hivFemaleScreenedForCervicalCancerWithPositiveHPVResult
		        .setName("hivFemaleScreenedForCervicalCancerWithPositiveHPVResult");
		hivFemaleScreenedForCervicalCancerWithPositiveHPVResult.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		hivFemaleScreenedForCervicalCancerWithPositiveHPVResult
		        .addParameter(new Parameter("endDate", "endDate", Date.class));
		hivFemaleScreenedForCervicalCancerWithPositiveHPVResult.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithPositiveHPVResult, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivFemaleScreenedForCervicalCancerWithPositiveHPVResult.getSearches().put("2",
		    new Mapped<CohortDefinition>(female, null));
		hivFemaleScreenedForCervicalCancerWithPositiveHPVResult.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivFemaleScreenedForCervicalCancerWithPositiveHPVResult.setCompositionString("1 and 2 and 3");
		
		CohortIndicator hivFemaleScreenedForCervicalCancerWithPositiveHPVResultIndicator = Indicators.newCountIndicator(
		    "hivFemaleScreenedForCervicalCancerWithPositiveHPVResultIndicator",
		    hivFemaleScreenedForCervicalCancerWithPositiveHPVResult,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C3P",
		    "HIV Positive Number of women  tested HPV positive",
		    new Mapped(hivFemaleScreenedForCervicalCancerWithPositiveHPVResultIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivNegaveFemaleScreenedForCervicalCancerWithPositiveHPVResult = new CompositionCohortDefinition();
		hivNegaveFemaleScreenedForCervicalCancerWithPositiveHPVResult
		        .setName("hivNegaveFemaleScreenedForCervicalCancerWithPositiveHPVResult");
		hivNegaveFemaleScreenedForCervicalCancerWithPositiveHPVResult.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		hivNegaveFemaleScreenedForCervicalCancerWithPositiveHPVResult.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		hivNegaveFemaleScreenedForCervicalCancerWithPositiveHPVResult.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithPositiveHPVResult, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegaveFemaleScreenedForCervicalCancerWithPositiveHPVResult.getSearches().put("2",
		    new Mapped<CohortDefinition>(female, null));
		hivNegaveFemaleScreenedForCervicalCancerWithPositiveHPVResult.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivNegaveFemaleScreenedForCervicalCancerWithPositiveHPVResult.setCompositionString("1 and 2 and (not 3)");
		
		CohortIndicator hivNegaveFemaleScreenedForCervicalCancerWithPositiveHPVResultIndicator = Indicators
		        .newCountIndicator("hivNegaveFemaleScreenedForCervicalCancerWithPositiveHPVResultIndicator",
		            hivNegaveFemaleScreenedForCervicalCancerWithPositiveHPVResult,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C3N",
		    "HIV Negative Number of women  tested HPV positive",
		    new Mapped(hivNegaveFemaleScreenedForCervicalCancerWithPositiveHPVResultIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//============================ c4 =========================================================
		
		SqlCohortDefinition screenedForCervicalCancerWithPositiveHPVResultWithVIATriage = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForCervicalCancerWithPositiveHPVResultWithVIATriage",
		            cervicalCancerScreeningFollowupAndExaminationForms, typeOfVIAPerformed, VIATriageInList);
		CompositionCohortDefinition femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage = new CompositionCohortDefinition();
		femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage
		        .setName("femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage");
		femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(femaleScreenedForCervicalCancerWithPositiveHPVResult, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithPositiveHPVResultWithVIATriage, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage.setCompositionString("1 and 2");
		
		CohortIndicator femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageIndicator = Indicators
		        .newCountIndicator("femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageIndicator",
		            femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C4",
		    "Number of women  tested HPV positive received  VIA Triage",
		    new Mapped(femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage = new CompositionCohortDefinition();
		hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage
		        .setName("hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage");
		hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithPositiveHPVResultWithVIATriage, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage.setCompositionString("1 and 2 and 3");
		
		CohortIndicator hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageIndicator = Indicators
		        .newCountIndicator("hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageIndicator",
		            hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C4P",
		    "HIV positive Number of women  tested HPV positive received  VIA Triage",
		    new Mapped(hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage = new CompositionCohortDefinition();
		hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage
		        .setName("hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage");
		hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithPositiveHPVResultWithVIATriage, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage
		        .setCompositionString("1 and 2 and (not 3)");
		
		CohortIndicator hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageIndicator = Indicators
		        .newCountIndicator("hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageIndicator",
		            hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn("C4N", "HIV Negative Number of women  tested HPV positive received  VIA Triage",
		    new Mapped(hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageIndicator,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//========================================= 5 =============================================================
		SqlCohortDefinition screenedForCervicalCancerWithVIATriageAndVIAAndElligibleResult = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult",
		            cervicalCancerScreeningFollowupAndExaminationForms, VIAResults, VIAAndEligibleResults);
		
		CompositionCohortDefinition femalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult = new CompositionCohortDefinition();
		femalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult
		        .setName("femalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult");
		femalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult
		        .addParameter(new Parameter("startDate", "startDate", Date.class));
		femalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult
		        .addParameter(new Parameter("endDate", "endDate", Date.class));
		femalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIATriageAndVIAAndElligibleResult, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult
		        .setCompositionString("1 and 2");
		
		CohortIndicator femalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResultIndicator = Indicators
		        .newCountIndicator(
		            "femalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResultIndicator",
		            femalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C5",
		    "Number of women  tested HPV positive and VIA Triage positive: With Result VIA+ Eligible for Thermal ablation OR VIA+ Eligible for LEEP",
		    new Mapped(femalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResultIndicator,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivPositiveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult = new CompositionCohortDefinition();
		hivPositiveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult
		        .setName("hivPositiveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult");
		hivPositiveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult
		        .addParameter(new Parameter("startDate", "startDate", Date.class));
		hivPositiveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult
		        .addParameter(new Parameter("endDate", "endDate", Date.class));
		hivPositiveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage,
		                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivPositiveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult.getSearches()
		        .put(
		            "2",
		            new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIATriageAndVIAAndElligibleResult,
		                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivPositiveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult.getSearches()
		        .put("3", new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivPositiveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult
		        .setCompositionString("1 and 2 and 3");
		
		CohortIndicator hivPositiveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResultIndicator = Indicators
		        .newCountIndicator(
		            "hivPositiveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResultIndicator",
		            hivPositiveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C5P",
		    "HIV Positive Number of women  tested HPV positive and VIA Triage positive: With Result VIA+ Eligible for Thermal ablation OR VIA+ Eligible for LEEP",
		    new Mapped(
		            hivPositiveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResultIndicator,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivNegaveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult = new CompositionCohortDefinition();
		hivNegaveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult
		        .setName("hivNegaveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult");
		hivNegaveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult
		        .addParameter(new Parameter("startDate", "startDate", Date.class));
		hivNegaveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult
		        .addParameter(new Parameter("endDate", "endDate", Date.class));
		hivNegaveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage,
		                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegaveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult.getSearches()
		        .put(
		            "2",
		            new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIATriageAndVIAAndElligibleResult,
		                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegaveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult.getSearches()
		        .put("3", new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivNegaveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult
		        .setCompositionString("1 and 2 and (not 3)");
		
		CohortIndicator hivNegaveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResultIndicator = Indicators
		        .newCountIndicator(
		            "hivNegaveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResultIndicator",
		            hivNegaveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C5N",
		    "HIV Negative Number of women  tested HPV positive and VIA Triage positive: With Result VIA+ Eligible for Thermal ablation OR VIA+ Eligible for LEEP",
		    new Mapped(
		            hivNegaveFemalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResultIndicator,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//================================================ c6 ===================================================================
		SqlCohortDefinition screenedForCervicalCancerWithVIANegativeResult = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult",
		            cervicalCancerScreeningFollowupAndExaminationForms, VIAResults, VIANegativeInList);
		
		CompositionCohortDefinition femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult = new CompositionCohortDefinition();
		femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult
		        .setName("femalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult");
		femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult.addParameter(new Parameter(
		        "endDate", "endDate", Date.class));
		femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIANegativeResult, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult
		        .setCompositionString("1 and 2");
		
		CohortIndicator femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResultIndicator = Indicators
		        .newCountIndicator(
		            "femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResultIndicator",
		            femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn("C6", "Number of women  tested HPV positive and VIA Triage negative: VIA-", new Mapped(
		        femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResultIndicator,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult = new CompositionCohortDefinition();
		hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult
		        .setName("femalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult");
		hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult.addParameter(new Parameter(
		        "endDate", "endDate", Date.class));
		hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIANegativeResult, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult
		        .setCompositionString("1 and 2 and 3");
		
		CohortIndicator hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResultIndicator = Indicators
		        .newCountIndicator(
		            "hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResultIndicator",
		            hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn("C6P", "HIV positive Number of women  tested HPV positive and VIA Triage negative: VIA-", new Mapped(
		        hivFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResultIndicator,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult = new CompositionCohortDefinition();
		hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult
		        .setName("femalesscreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIAAndElligibleResult");
		hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult
		        .addParameter(new Parameter("startDate", "startDate", Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult
		        .addParameter(new Parameter("endDate", "endDate", Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(femalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriage,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIANegativeResult, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult.getSearches().put(
		    "3", new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult
		        .setCompositionString("1 and 2 and (not 3)");
		
		CohortIndicator hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResultIndicator = Indicators
		        .newCountIndicator(
		            "hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResultIndicator",
		            hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResult,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn("C6N", "HIV Negative Number of women  tested HPV positive and VIA Triage negative: VIA-", new Mapped(
		        hivNegaveFemalescreenedForCervicalCancerWithPositiveHPVResultWithVIATriageAndVIANegativeResultIndicator,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//============================================== c7 ======================================================
		SqlCohortDefinition screenedForCervicalCancerWithVIAScreen = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForCervicalCancerWithPositiveHPVResultWithVIATriage",
		            cervicalCancerScreeningFollowupAndExaminationForms, typeOfVIAPerformed, VIAScreenInList);
		
		CompositionCohortDefinition femalescreenedForCervicalCancerWithVIAScreen = new CompositionCohortDefinition();
		femalescreenedForCervicalCancerWithVIAScreen.setName("femalescreenedForCervicalCancerWithVIAScreen");
		femalescreenedForCervicalCancerWithVIAScreen.addParameter(new Parameter("startDate", "startDate", Date.class));
		femalescreenedForCervicalCancerWithVIAScreen.addParameter(new Parameter("endDate", "endDate", Date.class));
		femalescreenedForCervicalCancerWithVIAScreen.getSearches().put("1", new Mapped<CohortDefinition>(female, null));
		femalescreenedForCervicalCancerWithVIAScreen.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIAScreen, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalescreenedForCervicalCancerWithVIAScreen.setCompositionString("1 and 2");
		
		CohortIndicator femalescreenedForCervicalCancerWithVIAScreenIndicator = Indicators.newCountIndicator(
		    "femalescreenedForCervicalCancerWithVIAScreenIndicator", femalescreenedForCervicalCancerWithVIAScreen,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C7",
		    "Number of women screened for cervical cancer with VIA only",
		    new Mapped(femalescreenedForCervicalCancerWithVIAScreenIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hiFemalescreenedForCervicalCancerWithVIAScreen = new CompositionCohortDefinition();
		hiFemalescreenedForCervicalCancerWithVIAScreen.setName("hiFemalescreenedForCervicalCancerWithVIAScreen");
		hiFemalescreenedForCervicalCancerWithVIAScreen.addParameter(new Parameter("startDate", "startDate", Date.class));
		hiFemalescreenedForCervicalCancerWithVIAScreen.addParameter(new Parameter("endDate", "endDate", Date.class));
		hiFemalescreenedForCervicalCancerWithVIAScreen.getSearches().put("1", new Mapped<CohortDefinition>(female, null));
		hiFemalescreenedForCervicalCancerWithVIAScreen.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIAScreen, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hiFemalescreenedForCervicalCancerWithVIAScreen.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hiFemalescreenedForCervicalCancerWithVIAScreen.setCompositionString("1 and 2 and 3");
		
		CohortIndicator hiFemalescreenedForCervicalCancerWithVIAScreenIndicator = Indicators.newCountIndicator(
		    "hiFemalescreenedForCervicalCancerWithVIAScreenIndicator", hiFemalescreenedForCervicalCancerWithVIAScreen,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C7P",
		    "HIV Positive Number of women screened for cervical cancer with VIA only",
		    new Mapped(hiFemalescreenedForCervicalCancerWithVIAScreenIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivNegaveFemalescreenedForCervicalCancerWithVIAScreen = new CompositionCohortDefinition();
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreen
		        .setName("hivNegaveFemalescreenedForCervicalCancerWithVIAScreen");
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreen.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreen.addParameter(new Parameter("endDate", "endDate", Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreen.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreen.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIAScreen, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreen.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreen.setCompositionString("1 and 2 and (not 3)");
		
		CohortIndicator hivNegaveFemalescreenedForCervicalCancerWithVIAScreenIndicator = Indicators.newCountIndicator(
		    "hivNegaveFemalescreenedForCervicalCancerWithVIAScreenIndicator",
		    hivNegaveFemalescreenedForCervicalCancerWithVIAScreen,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C7N",
		    "HIV Negative Number of women screened for cervical cancer with VIA only",
		    new Mapped(hivNegaveFemalescreenedForCervicalCancerWithVIAScreenIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//================================================ c8 ==================================================
		
		CompositionCohortDefinition femalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult = new CompositionCohortDefinition();
		femalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult
		        .setName("femalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult");
		femalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		femalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		femalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(femalescreenedForCervicalCancerWithVIAScreen, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIATriageAndVIAAndElligibleResult, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult.setCompositionString("1 and 2");
		
		CohortIndicator femalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResultIndicator = Indicators
		        .newCountIndicator("femalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResultIndicator",
		            femalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C8",
		    "Number of women screened for cervical cancer with VIA only: Type of VIA performed is VIA Screen and Result is VIA+ Eligible for Thermal ablation OR VIA+ Eligible for LEEP",
		    new Mapped(femalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResultIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult = new CompositionCohortDefinition();
		hivFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult
		        .setName("hivFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult");
		hivFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		hivFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		hivFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(femalescreenedForCervicalCancerWithVIAScreen, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIATriageAndVIAAndElligibleResult, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult.setCompositionString("1 and 2 and 3");
		
		CohortIndicator hivFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResultIndicator = Indicators
		        .newCountIndicator("hivFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResultIndicator",
		            hivFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C8P",
		    "HIV Positive Number of women screened for cervical cancer with VIA only: Type of VIA performed is VIA Screen and Result is VIA+ Eligible for Thermal ablation OR VIA+ Eligible for LEEP",
		    new Mapped(hivFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResultIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivNegaveFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult = new CompositionCohortDefinition();
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult
		        .setName("hivNegaveFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult");
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(femalescreenedForCervicalCancerWithVIAScreen, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIATriageAndVIAAndElligibleResult, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult
		        .setCompositionString("1 and 2 and (not 3)");
		
		CohortIndicator hivNegaveFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResultIndicator = Indicators
		        .newCountIndicator("hivNegaveFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResultIndicator",
		            hivNegaveFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResult,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C8N",
		    "HIV Negative Number of women screened for cervical cancer with VIA only: Type of VIA performed is VIA Screen and Result is VIA+ Eligible for Thermal ablation OR VIA+ Eligible for LEEP",
		    new Mapped(hivNegaveFemalescreenedForCervicalCancerWithVIAScreenAndVIAAndElligibleResultIndicator,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		// ==================================================== c9 ========================================================
		
		CompositionCohortDefinition femalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult = new CompositionCohortDefinition();
		femalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult
		        .setName("femalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult");
		femalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		femalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		femalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(femalescreenedForCervicalCancerWithVIAScreen, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIANegativeResult, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult.setCompositionString("1 and 2");
		
		CohortIndicator femalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResultIndicator = Indicators
		        .newCountIndicator("femalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResultIndicator",
		            femalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C9",
		    "Number of women screened for cervical cancer with VIA only: Type of VIA performed is VIA Screen and Result is VIA-",
		    new Mapped(femalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResultIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult = new CompositionCohortDefinition();
		hivFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult
		        .setName("hivFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult");
		hivFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		hivFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		hivFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(femalescreenedForCervicalCancerWithVIAScreen, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIANegativeResult, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult.setCompositionString("1 and 2 and 3");
		
		CohortIndicator hivFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResultIndicator = Indicators
		        .newCountIndicator("hivFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResultIndicator",
		            hivFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C9P",
		    "HIV Positive Number of women screened for cervical cancer with VIA only: Type of VIA performed is VIA Screen and Result is VIA-",
		    new Mapped(hivFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResultIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivNegaveFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult = new CompositionCohortDefinition();
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult
		        .setName("hivNegaveFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult");
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(femalescreenedForCervicalCancerWithVIAScreen, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIANegativeResult, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivNegaveFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult
		        .setCompositionString("1 and 2 and (not 3)");
		
		CohortIndicator hivNegaveFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResultIndicator = Indicators
		        .newCountIndicator("hivNegaveFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResultIndicator",
		            hivNegaveFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResult,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C9N",
		    "HIV Negative Number of women screened for cervical cancer with VIA only: Type of VIA performed is VIA Screen and Result is VIA-",
		    new Mapped(hivNegaveFemalescreenedForCervicalCancerWithVIAScreenWithVIANegativeResultIndicator,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		// ============================================================ c10 ==============================================================
		
		SqlCohortDefinition screenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForCervicalCancerWithThermalAblationAndLEEPTreatmentType",
		            cervicalCancerScreeningFollowupAndExaminationForms, typeOfTreatmentPerformed,
		            thermalAblationAndCryotherapyList);
		
		CompositionCohortDefinition femalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType = new CompositionCohortDefinition();
		femalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType
		        .setName("femalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType");
		femalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		femalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		femalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		femalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType.setCompositionString("1 and 2");
		
		CohortIndicator femalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentTypeIndicator = Indicators
		        .newCountIndicator("femalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentTypeIndicator",
		            femalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn("C10", "Number of  screened positive women treated with thermo ablation or Cryotherapy",
		    new Mapped(femalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentTypeIndicator,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType = new CompositionCohortDefinition();
		hivFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType
		        .setName("hivFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType");
		hivFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		hivFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType.addParameter(new Parameter(
		        "endDate", "endDate", Date.class));
		hivFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		hivFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType
		        .setCompositionString("1 and 2 and 3");
		
		CohortIndicator hivFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentTypeIndicator = Indicators
		        .newCountIndicator(
		            "hivFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentTypeIndicator",
		            hivFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn("C10P", "HIV positive Number of  screened positive women treated with thermo ablation or Cryotherapy",
		    new Mapped(hivFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentTypeIndicator,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivNegaveFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType = new CompositionCohortDefinition();
		hivNegaveFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType
		        .setName("hivNegaveFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType");
		hivNegaveFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType.addParameter(new Parameter(
		        "endDate", "endDate", Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		hivNegaveFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegaveFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivNegaveFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType
		        .setCompositionString("1 and 2 and (not 3)");
		
		CohortIndicator hivNegaveFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentTypeIndicator = Indicators
		        .newCountIndicator(
		            "hivNegaveFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentTypeIndicator",
		            hivNegaveFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentType,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn("C10N", "HIV Negative Number of  screened positive women treated with thermo ablation or Cryotherapy",
		    new Mapped(hivNegaveFemalescreenedForCervicalCancerWithThermalAblationAndCryotherapyTreatmentTypeIndicator,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//=========================================================== c11 =====================================================================
		SqlCohortDefinition screenedForCervicalCancerWithLEEPAsReasonsForReferral = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForCervicalCancerWithLEEPAsReasonsForReferral",
		            cervicalCancerScreeningFollowupAndExaminationForms, reasonsForReferral, LEEPAndColposcopy);
		
		CompositionCohortDefinition femalescreenedForCervicalCancerWithLEEPAsReasonsForReferral = new CompositionCohortDefinition();
		femalescreenedForCervicalCancerWithLEEPAsReasonsForReferral
		        .setName("femalescreenedForCervicalCancerWithLEEPAsReasonsForReferral");
		femalescreenedForCervicalCancerWithLEEPAsReasonsForReferral.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		femalescreenedForCervicalCancerWithLEEPAsReasonsForReferral.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		femalescreenedForCervicalCancerWithLEEPAsReasonsForReferral.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		femalescreenedForCervicalCancerWithLEEPAsReasonsForReferral.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithLEEPAsReasonsForReferral, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalescreenedForCervicalCancerWithLEEPAsReasonsForReferral.setCompositionString("1 and 2");
		
		CohortIndicator femalescreenedForCervicalCancerWithLEEPAsReasonsForReferralIndicator = Indicators.newCountIndicator(
		    "femalescreenedForCervicalCancerWithLEEPAsReasonsForReferralIndicator",
		    femalescreenedForCervicalCancerWithLEEPAsReasonsForReferral,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C11",
		    "Number of screened positive women referred  for LEEP & Colposcopy",
		    new Mapped(femalescreenedForCervicalCancerWithLEEPAsReasonsForReferralIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral = new CompositionCohortDefinition();
		hivFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral
		        .setName("hivFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral");
		hivFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		hivFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		hivFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		hivFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithLEEPAsReasonsForReferral, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral.setCompositionString("1 and 2 and 3");
		
		CohortIndicator hivFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferralIndicator = Indicators
		        .newCountIndicator("hivFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferralIndicator",
		            hivFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C11P",
		    "HIV positive Number of screened positive women referred  for LEEP & Colposcopy",
		    new Mapped(hivFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferralIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivNegaveFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral = new CompositionCohortDefinition();
		hivNegaveFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral
		        .setName("hivNegaveFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral");
		hivNegaveFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		hivNegaveFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithLEEPAsReasonsForReferral, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegaveFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivNegaveFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral.setCompositionString("1 and 2 and (not 3)");
		
		CohortIndicator hivNegaveFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferralIndicator = Indicators
		        .newCountIndicator("hivNegaveFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferralIndicator",
		            hivNegaveFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferral,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C11N",
		    "HIV Negative Number of screened positive women referred  for LEEP & Colposcopy",
		    new Mapped(hivNegaveFemalescreenedForCervicalCancerWithLEEPAsReasonsForReferralIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//============================================================= c12 =================================
		SqlCohortDefinition screenedForCervicalCancerWithVIASuspectedCancer = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForCervicalCancerWithVIASuspectedCancer", cervicalCancerScreeningFollowupAndExaminationForms,
		            VIAResults, suspectedCancerInList);
		CompositionCohortDefinition femalescreenedForCervicalCancerWithVIASuspectedCancer = new CompositionCohortDefinition();
		femalescreenedForCervicalCancerWithVIASuspectedCancer
		        .setName("femalescreenedForCervicalCancerWithVIASuspectedCancer");
		femalescreenedForCervicalCancerWithVIASuspectedCancer.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		femalescreenedForCervicalCancerWithVIASuspectedCancer.addParameter(new Parameter("endDate", "endDate", Date.class));
		femalescreenedForCervicalCancerWithVIASuspectedCancer.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		femalescreenedForCervicalCancerWithVIASuspectedCancer.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIASuspectedCancer, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalescreenedForCervicalCancerWithVIASuspectedCancer.setCompositionString("1 and 2");
		
		CohortIndicator femalescreenedForCervicalCancerWithVIASuspectedCancerIndicator = Indicators.newCountIndicator(
		    "femalescreenedForCervicalCancerWithVIASuspectedCancerIndicator",
		    femalescreenedForCervicalCancerWithVIASuspectedCancer,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C12",
		    "Number of screened women with suspected cervical cancer",
		    new Mapped(femalescreenedForCervicalCancerWithVIASuspectedCancerIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hiFemalescreenedForCervicalCancerWithVIASuspectedCancer = new CompositionCohortDefinition();
		hiFemalescreenedForCervicalCancerWithVIASuspectedCancer
		        .setName("hiFemalescreenedForCervicalCancerWithVIASuspectedCancer");
		hiFemalescreenedForCervicalCancerWithVIASuspectedCancer.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		hiFemalescreenedForCervicalCancerWithVIASuspectedCancer
		        .addParameter(new Parameter("endDate", "endDate", Date.class));
		hiFemalescreenedForCervicalCancerWithVIASuspectedCancer.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		hiFemalescreenedForCervicalCancerWithVIASuspectedCancer.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIASuspectedCancer, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hiFemalescreenedForCervicalCancerWithVIASuspectedCancer.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hiFemalescreenedForCervicalCancerWithVIASuspectedCancer.setCompositionString("1 and 2 and 3");
		
		CohortIndicator hiFemalescreenedForCervicalCancerWithVIASuspectedCancerIndicator = Indicators.newCountIndicator(
		    "hiFemalescreenedForCervicalCancerWithVIASuspectedCancerIndicator",
		    hiFemalescreenedForCervicalCancerWithVIASuspectedCancer,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C12P",
		    "HIV Positive Number of screened women with suspected cervical cancer",
		    new Mapped(hiFemalescreenedForCervicalCancerWithVIASuspectedCancerIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivNegaveFemalescreenedForCervicalCancerWithVIASuspectedCancer = new CompositionCohortDefinition();
		hivNegaveFemalescreenedForCervicalCancerWithVIASuspectedCancer
		        .setName("hivNegaveFemalescreenedForCervicalCancerWithVIASuspectedCancer");
		hivNegaveFemalescreenedForCervicalCancerWithVIASuspectedCancer.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithVIASuspectedCancer.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithVIASuspectedCancer.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		hivNegaveFemalescreenedForCervicalCancerWithVIASuspectedCancer.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIASuspectedCancer, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegaveFemalescreenedForCervicalCancerWithVIASuspectedCancer.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivNegaveFemalescreenedForCervicalCancerWithVIASuspectedCancer.setCompositionString("1 and 2 and (not 3)");
		
		CohortIndicator hivNegaveFemalescreenedForCervicalCancerWithVIASuspectedCancerIndicator = Indicators
		        .newCountIndicator("hiFemalescreenedForCervicalCancerWithVIASuspectedCancerIndicator",
		            hivNegaveFemalescreenedForCervicalCancerWithVIASuspectedCancer,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C12N",
		    "HIV Negative Number of screened women with suspected cervical cancer",
		    new Mapped(hivNegaveFemalescreenedForCervicalCancerWithVIASuspectedCancerIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//=================================================== c14 =========================================
		SqlCohortDefinition screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForCervicalCancerWithLEEPAsReasonsForReferral",
		            cervicalCancerScreeningFollowupAndExaminationForms, reasonsForReferral, suspectedCancerInList);
		
		CompositionCohortDefinition femalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral = new CompositionCohortDefinition();
		femalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral
		        .setName("femalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral");
		femalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		femalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		femalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		femalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral.setCompositionString("1 and 2");
		
		CohortIndicator femalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralIndicator = Indicators
		        .newCountIndicator("femalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralIndicator",
		            femalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C14",
		    "Number of  women  with suspected   cervical cancer referred to other level",
		    new Mapped(femalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral = new CompositionCohortDefinition();
		hivFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral
		        .setName("hivFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral");
		hivFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		hivFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		hivFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		hivFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral.setCompositionString("1 and 2 and 3");
		
		CohortIndicator hivFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralIndicator = Indicators
		        .newCountIndicator("hivFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralIndicator",
		            hivFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn("C14P", "HIV Positive Number of  women  with suspected   cervical cancer referred to other level",
		    new Mapped(hivFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralIndicator,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivNegaveFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral = new CompositionCohortDefinition();
		hivNegaveFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral
		        .setName("hivNegaveFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral");
		hivNegaveFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral.addParameter(new Parameter(
		        "endDate", "endDate", Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		hivNegaveFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegaveFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivNegaveFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral
		        .setCompositionString("1 and 2 and (not 3)");
		
		CohortIndicator hivNegaveFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralIndicator = Indicators
		        .newCountIndicator(
		            "hivNegaveFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralIndicator",
		            hivNegaveFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn("C14N", "HIV Negative Number of  women  with suspected   cervical cancer referred to other level",
		    new Mapped(hivNegaveFemalescreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralIndicator,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		// ========================================================= c18 ======================================================
		
		SqlCohortDefinition screenedForCervicalCancerWithLEEPAsTreatmentPerformed = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForCervicalCancerWithLEEPAsReasonsForReferral",
		            cervicalCancerScreeningFollowupAndExaminationForms, typeOfTreatmentPerformed,
		            loopElectrosurgicalExcisionProcedureInList);
		
		CompositionCohortDefinition femalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed = new CompositionCohortDefinition();
		femalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed
		        .setName("femalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed");
		femalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		femalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		femalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		femalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithLEEPAsTreatmentPerformed, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed.setCompositionString("1 and 2");
		
		CohortIndicator femalescreenedForCervicalCancerWithLEEPAsTreatmentPerformedIndicator = Indicators.newCountIndicator(
		    "femalescreenedForCervicalCancerWithLEEPAsTreatmentPerformedIndicator",
		    femalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C18",
		    "Number of  screened positive women treated with LEEP",
		    new Mapped(femalescreenedForCervicalCancerWithLEEPAsTreatmentPerformedIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed = new CompositionCohortDefinition();
		hivFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed
		        .setName("hivFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed");
		hivFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		hivFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		hivFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		hivFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithLEEPAsTreatmentPerformed, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed.setCompositionString("1 and 2 and 3 ");
		
		CohortIndicator hivFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformedIndicator = Indicators
		        .newCountIndicator("hivFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformedIndicator",
		            hivFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C18P",
		    "HIV Positive Number of  screened positive women treated with LEEP",
		    new Mapped(hivFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformedIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivNegaveFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed = new CompositionCohortDefinition();
		hivNegaveFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed
		        .setName("hivNegaveFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed");
		hivNegaveFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		hivNegaveFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithLEEPAsTreatmentPerformed, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegaveFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivNegaveFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed.setCompositionString("1 and 2 and (not 3) ");
		
		CohortIndicator hivNegaveFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformedIndicator = Indicators
		        .newCountIndicator("hivNegaveFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformedIndicator",
		            hivNegaveFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformed,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C18N",
		    "HIV Positive Negative of  screened positive women treated with LEEP",
		    new Mapped(hivNegaveFemalescreenedForCervicalCancerWithLEEPAsTreatmentPerformedIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//========================================================= c19 =================================================
		SqlCohortDefinition screenedForCervicalCancerWithBiobsyPerformed = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForCervicalCancerWithLEEPAsReasonsForReferral",
		            cervicalCancerScreeningFollowupAndExaminationForms, biopsyperformed, yesInList);
		
		CompositionCohortDefinition femalescreenedForCervicalCancerWithBiobsyPerformed = new CompositionCohortDefinition();
		femalescreenedForCervicalCancerWithBiobsyPerformed.setName("femalescreenedForCervicalCancerWithBiobsyPerformed");
		femalescreenedForCervicalCancerWithBiobsyPerformed.addParameter(new Parameter("startDate", "startDate", Date.class));
		femalescreenedForCervicalCancerWithBiobsyPerformed.addParameter(new Parameter("endDate", "endDate", Date.class));
		femalescreenedForCervicalCancerWithBiobsyPerformed.getSearches()
		        .put("1", new Mapped<CohortDefinition>(female, null));
		femalescreenedForCervicalCancerWithBiobsyPerformed.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithBiobsyPerformed, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalescreenedForCervicalCancerWithBiobsyPerformed.setCompositionString("1 and 2");
		
		CohortIndicator femalescreenedForCervicalCancerWithBiobsyPerformedIndicator = Indicators.newCountIndicator(
		    "femalescreenedForCervicalCancerWithBiobsyPerformedIndicator",
		    femalescreenedForCervicalCancerWithBiobsyPerformed,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C19",
		    "Number of women with cervical biopsy performed",
		    new Mapped(femalescreenedForCervicalCancerWithBiobsyPerformedIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivFemalescreenedForCervicalCancerWithBiobsyPerformed = new CompositionCohortDefinition();
		hivFemalescreenedForCervicalCancerWithBiobsyPerformed
		        .setName("hivFemalescreenedForCervicalCancerWithBiobsyPerformed");
		hivFemalescreenedForCervicalCancerWithBiobsyPerformed.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		hivFemalescreenedForCervicalCancerWithBiobsyPerformed.addParameter(new Parameter("endDate", "endDate", Date.class));
		hivFemalescreenedForCervicalCancerWithBiobsyPerformed.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		hivFemalescreenedForCervicalCancerWithBiobsyPerformed.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithBiobsyPerformed, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivFemalescreenedForCervicalCancerWithBiobsyPerformed.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivFemalescreenedForCervicalCancerWithBiobsyPerformed.setCompositionString("1 and 2 and 3");
		
		CohortIndicator hivFemalescreenedForCervicalCancerWithBiobsyPerformedIndicator = Indicators.newCountIndicator(
		    "hivFemalescreenedForCervicalCancerWithBiobsyPerformedIndicator",
		    hivFemalescreenedForCervicalCancerWithBiobsyPerformed,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C19P",
		    "HIV positive Number of women with cervical biopsy performed",
		    new Mapped(hivFemalescreenedForCervicalCancerWithBiobsyPerformedIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivNegaveFemalescreenedForCervicalCancerWithBiobsyPerformed = new CompositionCohortDefinition();
		hivNegaveFemalescreenedForCervicalCancerWithBiobsyPerformed
		        .setName("hivNegaveFemalescreenedForCervicalCancerWithBiobsyPerformed");
		hivNegaveFemalescreenedForCervicalCancerWithBiobsyPerformed.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithBiobsyPerformed.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		hivNegaveFemalescreenedForCervicalCancerWithBiobsyPerformed.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		hivNegaveFemalescreenedForCervicalCancerWithBiobsyPerformed.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithBiobsyPerformed, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegaveFemalescreenedForCervicalCancerWithBiobsyPerformed.getSearches().put("3",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivNegaveFemalescreenedForCervicalCancerWithBiobsyPerformed.setCompositionString("1 and 2 and (not 3)");
		
		CohortIndicator hivNegaveFemalescreenedForCervicalCancerWithBiobsyPerformedIndicator = Indicators.newCountIndicator(
		    "hivNegaveFemalescreenedForCervicalCancerWithBiobsyPerformedIndicator",
		    hivNegaveFemalescreenedForCervicalCancerWithBiobsyPerformed,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C19N",
		    "HIV Negateve Number of women with cervical biopsy performed",
		    new Mapped(hivNegaveFemalescreenedForCervicalCancerWithBiobsyPerformedIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//==================================================== c16 ======================================================
		
		SqlCohortDefinition screenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferral = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForCervicalCancerWithLEEPAsReasonsForReferral", screeningCervicalForms, reasonsForReferralIn,
		            LEEPAndColposcopy);
		
		CompositionCohortDefinition screenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital = new CompositionCohortDefinition();
		screenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital
		        .setName("femalescreenedForCervicalCancerWithLEEPAsReasonsForReferral");
		screenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		screenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital.addParameter(new Parameter(
		        "endDate", "endDate", Date.class));
		screenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		screenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithLEEPAsReasonsForReferral, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferral,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital
		        .setCompositionString("1 and 2 and 3");
		
		CohortIndicator screenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospitalIndicator = Indicators
		        .newCountIndicator(
		            "screenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospitalIndicator",
		            screenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn("C16",
		    "Number of women  referred from health centers  to Hospital  for LEEP & Colposcopy received by  the hospital",
		    new Mapped(screenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospitalIndicator,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital = new CompositionCohortDefinition();
		hivScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital
		        .setName("femalescreenedForCervicalCancerWithLEEPAsReasonsForReferral");
		hivScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		hivScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital.addParameter(new Parameter(
		        "endDate", "endDate", Date.class));
		hivScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		hivScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithLEEPAsReasonsForReferral, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferral,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital.getSearches().put("4",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital
		        .setCompositionString("1 and 2 and 3 and 4");
		
		CohortIndicator hivScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospitalIndicator = Indicators
		        .newCountIndicator(
		            "hivScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospitalIndicator",
		            hivScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C16P",
		    "HIV positive Number of women  referred from health centers  to Hospital  for LEEP & Colposcopy received by  the hospital",
		    new Mapped(hivScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospitalIndicator,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivNegativeScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital = new CompositionCohortDefinition();
		hivNegativeScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital
		        .setName("femalescreenedForCervicalCancerWithLEEPAsReasonsForReferral");
		hivNegativeScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital
		        .addParameter(new Parameter("startDate", "startDate", Date.class));
		hivNegativeScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital
		        .addParameter(new Parameter("endDate", "endDate", Date.class));
		hivNegativeScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		hivNegativeScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithLEEPAsReasonsForReferral, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegativeScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferral,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegativeScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital.getSearches().put("4",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivNegativeScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital
		        .setCompositionString("1 and 2 and 3 and (not 4)");
		
		CohortIndicator hivNegativeScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospitalIndicator = Indicators
		        .newCountIndicator(
		            "hivNegativeScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospitalIndicator",
		            hivNegativeScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospital,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C16N",
		    "HIV Negative Number of women  referred from health centers  to Hospital  for LEEP & Colposcopy received by  the hospital",
		    new Mapped(
		            hivNegativeScreenedForCervicalCancerWithLEEPAndColscopyAsReasonsForReferralRecevedAtHospitalIndicator,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//========================================================================== c17 ===============================================
		
		SqlCohortDefinition screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralInTransferIn = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForCervicalCancerWithLEEPAsReasonsForReferral", screeningCervicalForms, reasonsForReferralIn,
		            suspectedCancerInList);
		
		CompositionCohortDefinition screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital = new CompositionCohortDefinition();
		screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital
		        .setName("femalescreenedForCervicalCancerWithLEEPAsReasonsForReferral");
		screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.addParameter(new Parameter(
		        "endDate", "endDate", Date.class));
		screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralInTransferIn,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital
		        .setCompositionString("1 and 2 and 3");
		
		CohortIndicator screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospitalIndicator = Indicators
		        .newCountIndicator(
		            "screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospitalIndicator",
		            screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C17",
		    "Number of women  referred from health centers  to Hospital  for  cervical cancer suspicion  received by  the hospital",
		    new Mapped(screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospitalIndicator,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital = new CompositionCohortDefinition();
		hivScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital
		        .setName("femalescreenedForCervicalCancerWithLEEPAsReasonsForReferral");
		hivScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		hivScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.addParameter(new Parameter(
		        "endDate", "endDate", Date.class));
		hivScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		hivScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralInTransferIn,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.getSearches().put("4",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital
		        .setCompositionString("1 and 2 and 3 and 4");
		
		CohortIndicator hivScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospitalIndicator = Indicators
		        .newCountIndicator(
		            "hivScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospitalIndicator",
		            hivScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C17P",
		    "HIV positive Number of women  referred from health centers  to Hospital  for  cervical cancer suspicion  received by  the hospital",
		    new Mapped(hivScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospitalIndicator,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition hivNegativeScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital = new CompositionCohortDefinition();
		hivNegativeScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital
		        .setName("hivNegativeScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital");
		hivNegativeScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital
		        .addParameter(new Parameter("startDate", "startDate", Date.class));
		hivNegativeScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital
		        .addParameter(new Parameter("endDate", "endDate", Date.class));
		hivNegativeScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.getSearches().put("1",
		    new Mapped<CohortDefinition>(female, null));
		hivNegativeScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegativeScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralInTransferIn,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		hivNegativeScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.getSearches().put("4",
		    new Mapped<CohortDefinition>(hivPositivePatient, null));
		hivNegativeScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital
		        .setCompositionString("1 and 2 and 3 and (not 4)");
		
		CohortIndicator hivNegativeScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospitalIndicator = Indicators
		        .newCountIndicator(
		            "hivNegativeScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospitalIndicator",
		            hivNegativeScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "C17N",
		    "HIV Negative Number of women  referred from health centers  to Hospital  for  cervical cancer suspicion  received by  the hospital",
		    new Mapped(
		            hivNegativeScreenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospitalIndicator,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//=================================
		// Breast cancer early detection //
		//=================================
		
		// B1.Number of people reporting breast symptoms received  clinical breast exam
		
		AgeCohortDefinition below30Y = Cohorts.createUnderAgeCohort("below30Y", 30);
		AgeCohortDefinition between30And49Y = Cohorts.createXtoYAgeCohort("between30And49Y", 30, 49);
		AgeCohortDefinition above50y = Cohorts.createAboveAgeCohort("50YearAndAbove", 50, DurationUnit.YEARS);
		
		SqlCohortDefinition screenedForBreastCancerWithReasonForBreastExamAsBreastSymptoms = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForBreastCancerWithReasonForBreastExamAsBreastSymptoms", breastCancerScreeningForms,
		            reasonForBreastExam, breastSymptoms);
		
		CompositionCohortDefinition screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition = new CompositionCohortDefinition();
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition
		        .setName("screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition");
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForBreastExamAsBreastSymptoms, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition.setCompositionString("1");
		
		CohortIndicator screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsIndicator = Indicators
		        .newCountIndicator("screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsIndicator",
		            screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "B1",
		    "Number of people reporting breast symptoms received  clinical breast exam",
		    new Mapped(screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition30 = new CompositionCohortDefinition();
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition30
		        .setName("screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition");
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition30.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition30.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition30.addParameter(new Parameter(
		        "effectiveDate", "effectiveDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition30.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForBreastExamAsBreastSymptoms, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition30.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(below30Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition30.setCompositionString("1 and 2");
		
		CohortIndicator screenedForBreastCancerWithReasonForBreastExamAsBreastSymptoms30Indicator = Indicators
		        .newCountIndicator("screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsIndicator",
		            screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition30, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B130",
		    "Number of under 30 Years people reporting breast symptoms received  clinical breast exam",
		    new Mapped(screenedForBreastCancerWithReasonForBreastExamAsBreastSymptoms30Indicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition3049 = new CompositionCohortDefinition();
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition3049
		        .setName("screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition");
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition3049.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition3049.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition3049.addParameter(new Parameter(
		        "effectiveDate", "effectiveDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition3049.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForBreastExamAsBreastSymptoms, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition3049.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(between30And49Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition3049.setCompositionString("1 and 2");
		
		CohortIndicator screenedForBreastCancerWithReasonForBreastExamAsBreastSymptoms3049Indicator = Indicators
		        .newCountIndicator("screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsIndicator",
		            screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition3049, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B13049",
		    "Number of between 30 and 49 Years people reporting breast symptoms received  clinical breast exam",
		    new Mapped(screenedForBreastCancerWithReasonForBreastExamAsBreastSymptoms3049Indicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition50 = new CompositionCohortDefinition();
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition50
		        .setName("screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition50");
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition50.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition50.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition50.addParameter(new Parameter(
		        "effectiveDate", "effectiveDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition50.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForBreastExamAsBreastSymptoms, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition50.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(above50y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition50.setCompositionString("1 and 2");
		
		CohortIndicator screenedForBreastCancerWithReasonForBreastExamAsBreastSymptoms50Indicator = Indicators
		        .newCountIndicator("screenedForBreastCancerWithReasonForBreastExamAsBreastSymptoms50Indicator",
		            screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsComposition50, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B150",
		    "Number of people with 50 years and above reporting breast symptoms received  clinical breast exam",
		    new Mapped(screenedForBreastCancerWithReasonForBreastExamAsBreastSymptoms50Indicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		// B2. Number of people  not reporting breast symptoms received screening with clinical breast exam
		
		SqlCohortDefinition screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptoms = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptoms", breastCancerScreeningForms,
		            reasonForBreastExam, screening);
		
		CompositionCohortDefinition screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition = new CompositionCohortDefinition();
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition
		        .setName("screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition");
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptoms,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition.setCompositionString("1");
		
		CohortIndicator screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsIndicator = Indicators
		        .newCountIndicator("screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsIndicator",
		            screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "B2",
		    "Number of people  not reporting breast symptoms received screening with clinical breast exam",
		    new Mapped(screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition30 = new CompositionCohortDefinition();
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition30
		        .setName("screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition30");
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition30.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition30.addParameter(new Parameter(
		        "endDate", "endDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition30.addParameter(new Parameter(
		        "effectiveDate", "effectiveDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition30.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptoms,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition30.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(below30Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition30.setCompositionString("1 AND 2");
		
		CohortIndicator screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsIndicator30 = Indicators
		        .newCountIndicator("screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsIndicator30",
		            screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition30, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B230",
		    "Number of people under 30 Years not reporting breast symptoms received screening with clinical breast exam",
		    new Mapped(screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsIndicator30, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition3049 = new CompositionCohortDefinition();
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition3049
		        .setName("screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition3049");
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition3049.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition3049.addParameter(new Parameter(
		        "endDate", "endDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition3049.addParameter(new Parameter(
		        "effectiveDate", "effectiveDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition3049.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptoms,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition3049.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(between30And49Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition3049.setCompositionString("1 AND 2");
		
		CohortIndicator screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsIndicator3049 = Indicators
		        .newCountIndicator("screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsIndicator3049",
		            screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition3049, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B23049",
		    "Number of people between 30 and 49 Years not reporting breast symptoms received screening with clinical breast exam",
		    new Mapped(screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsIndicator3049, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition50 = new CompositionCohortDefinition();
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition50
		        .setName("screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition50");
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition50.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition50.addParameter(new Parameter(
		        "endDate", "endDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition50.addParameter(new Parameter(
		        "effectiveDate", "effectiveDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition50.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptoms,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition50.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(above50y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition50.setCompositionString("1 AND 2");
		
		CohortIndicator screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsIndicator50 = Indicators
		        .newCountIndicator("screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsIndicator50",
		            screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsComposition50, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B250",
		    "Number of people with 50 years and above not reporting breast symptoms received screening with clinical breast exam",
		    new Mapped(screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsIndicator50, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		// B3. Number of people reporting symptoms whose clinical breast exam shows abnormal breast findings
		
		SqlCohortDefinition screenedForBreastCancerWithAbnormalBreastFindings = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForBreastCancerWithAbnormalBreastFindings", breastCancerScreeningForms, breastExamination,
		            ABNORMAL);
		
		CompositionCohortDefinition screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition = new CompositionCohortDefinition();
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition
		        .setName("screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition");
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition
		        .addParameter(new Parameter("startDate", "startDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition
		        .addParameter(new Parameter("endDate", "endDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForBreastExamAsBreastSymptoms,
		                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition.getSearches()
		        .put(
		            "2",
		            new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition
		        .setCompositionString("1 and 2");
		
		CohortIndicator screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsIndicator = Indicators
		        .newCountIndicator(
		            "screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsIndicator",
		            screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn("B3", "Number of people reporting symptoms whose clinical breast exam shows abnormal breast findings",
		    new Mapped(screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsIndicator,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition30 = new CompositionCohortDefinition();
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition30
		        .setName("screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition30");
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition30
		        .addParameter(new Parameter("startDate", "startDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition30
		        .addParameter(new Parameter("endDate", "endDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition30
		        .addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition30.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForBreastExamAsBreastSymptoms,
		                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition30.getSearches()
		        .put(
		            "2",
		            new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition30.getSearches()
		        .put(
		            "3",
		            new Mapped<CohortDefinition>(below30Y, ParameterizableUtil
		                    .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition30
		        .setCompositionString("1 and 2 and 3");
		
		CohortIndicator screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsIndicator30 = Indicators
		        .newCountIndicator(
		            "screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsIndicator30",
		            screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition30,
		            ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn("B330",
		    "Number of people under 30 reporting symptoms whose clinical breast exam shows abnormal breast findings",
		    new Mapped(screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsIndicator30,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition3049 = new CompositionCohortDefinition();
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition3049
		        .setName("screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition3049");
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition3049
		        .addParameter(new Parameter("startDate", "startDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition3049
		        .addParameter(new Parameter("endDate", "endDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition3049
		        .addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition3049.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForBreastExamAsBreastSymptoms,
		                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition3049.getSearches()
		        .put(
		            "2",
		            new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition3049.getSearches()
		        .put(
		            "3",
		            new Mapped<CohortDefinition>(between30And49Y, ParameterizableUtil
		                    .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition3049
		        .setCompositionString("1 and 2 and 3");
		
		CohortIndicator screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsIndicator3049 = Indicators
		        .newCountIndicator(
		            "screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsIndicator3049",
		            screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition3049,
		            ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B33049",
		    "Number of people between 30 and 49 reporting symptoms whose clinical breast exam shows abnormal breast findings",
		    new Mapped(screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsIndicator3049,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition50 = new CompositionCohortDefinition();
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition50
		        .setName("screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition50");
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition50
		        .addParameter(new Parameter("startDate", "startDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition50
		        .addParameter(new Parameter("endDate", "endDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition50
		        .addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition50.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForBreastExamAsBreastSymptoms,
		                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition50.getSearches()
		        .put(
		            "2",
		            new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition50.getSearches()
		        .put(
		            "3",
		            new Mapped<CohortDefinition>(above50y, ParameterizableUtil
		                    .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition50
		        .setCompositionString("1 and 2 and 3");
		
		CohortIndicator screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsIndicator50 = Indicators
		        .newCountIndicator(
		            "screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsIndicator50",
		            screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsComposition50,
		            ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B350",
		    "Number of people with 50 and above reporting symptoms whose clinical breast exam shows abnormal breast findings",
		    new Mapped(screenedForBreastCancerWithReasonForBreastExamAsBreastSymptomsAndAbnormalBreastFindingsIndicator50,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		// B4. Number of people not reporting breast  symptoms whose clinical breast exam shows abnormal breast findings
		
		CompositionCohortDefinition screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition = new CompositionCohortDefinition();
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition
		        .setName("screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition");
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition
		        .addParameter(new Parameter("startDate", "startDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition
		        .addParameter(new Parameter("endDate", "endDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition
		        .getSearches().put(
		            "1",
		            new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptoms,
		                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition
		        .getSearches().put(
		            "2",
		            new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition
		        .setCompositionString("1 and 2");
		
		CohortIndicator screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsIndicator = Indicators
		        .newCountIndicator(
		            "screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsIndicator",
		            screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn("B4",
		    "Number of people not reporting breast  symptoms whose clinical breast exam shows abnormal breast findings",
		    new Mapped(
		            screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsIndicator,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition30 = new CompositionCohortDefinition();
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition30
		        .setName("screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition30");
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition30
		        .addParameter(new Parameter("startDate", "startDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition30
		        .addParameter(new Parameter("endDate", "endDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition30
		        .addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition30
		        .getSearches().put(
		            "1",
		            new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptoms,
		                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition30
		        .getSearches().put(
		            "2",
		            new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition30
		        .getSearches().put(
		            "3",
		            new Mapped<CohortDefinition>(below30Y, ParameterizableUtil
		                    .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition30
		        .setCompositionString("1 and 2 and 3");
		
		CohortIndicator screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsIndicator30 = Indicators
		        .newCountIndicator(
		            "screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsIndicator30",
		            screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition30,
		            ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B430",
		    "Number of people under 30 not reporting breast  symptoms whose clinical breast exam shows abnormal breast findings",
		    new Mapped(
		            screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsIndicator30,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition3049 = new CompositionCohortDefinition();
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition3049
		        .setName("screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition3049");
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition3049
		        .addParameter(new Parameter("startDate", "startDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition3049
		        .addParameter(new Parameter("endDate", "endDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition3049
		        .addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition3049
		        .getSearches().put(
		            "1",
		            new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptoms,
		                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition3049
		        .getSearches().put(
		            "2",
		            new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition3049
		        .getSearches().put(
		            "3",
		            new Mapped<CohortDefinition>(between30And49Y, ParameterizableUtil
		                    .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition3049
		        .setCompositionString("1 and 2 and 3");
		
		CohortIndicator screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsIndicator3049 = Indicators
		        .newCountIndicator(
		            "screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsIndicator3049",
		            screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition3049,
		            ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B43049",
		    "Number of people between 30 and 49 not reporting breast  symptoms whose clinical breast exam shows abnormal breast findings",
		    new Mapped(
		            screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsIndicator3049,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition50 = new CompositionCohortDefinition();
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition50
		        .setName("screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition50");
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition50
		        .addParameter(new Parameter("startDate", "startDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition50
		        .addParameter(new Parameter("endDate", "endDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition50
		        .addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition50
		        .getSearches().put(
		            "1",
		            new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptoms,
		                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition50
		        .getSearches().put(
		            "2",
		            new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition50
		        .getSearches().put(
		            "3",
		            new Mapped<CohortDefinition>(above50y, ParameterizableUtil
		                    .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition50
		        .setCompositionString("1 and 2 and 3");
		
		CohortIndicator screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsIndicator50 = Indicators
		        .newCountIndicator(
		            "screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsIndicator50",
		            screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsComposition50,
		            ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B450",
		    "Number of people with 50 years and above not reporting breast  symptoms whose clinical breast exam shows abnormal breast findings",
		    new Mapped(
		            screenedForBreastCancerWithReasonForBreastExamAsScreeningNoSymptomsAndAbnormalBreastFindingsIndicator50,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		// B5. Number of people treated for breast concerns without requiring referral
		
		SqlCohortDefinition screenedForBreastCancerWithReferral = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate("screenedForBreastCancerWithRefferal",
		            breastCancerScreeningForms, nextStep, referredTo);
		SqlCohortDefinition screenedForBreastCancerWithReasonForReferralAsFurtherManagement = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForBreastCancerWithReasonForReferralAsFurtherManagement", breastCancerScreeningForms,
		            reasonsForReferral, furtherManagement);
		SqlCohortDefinition screenedForBreastCancerWithReasonForReferralAsImaging = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForBreastCancerWithReasonForReferralAsImaging", breastCancerScreeningForms, reasonsForReferral,
		            medicalImaging);
		SqlCohortDefinition screenedForBreastCancerWithReasonForReferralAsBiopsy = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForBreastCancerWithReasonForReferralAsBiopsy", breastCancerScreeningForms, reasonsForReferral,
		            BIOPSY);
		SqlCohortDefinition screenedForBreastCancerWithOtherReason = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate("screenedForBreastCancerWithOtherReason",
		            breastCancerScreeningForms, reasonsForReferral, OTHERNONCODED);
		
		CompositionCohortDefinition numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition = new CompositionCohortDefinition();
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition
		        .setName("numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition");
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReferral, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsFurtherManagement,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsImaging, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition.getSearches().put(
		    "5",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsBiopsy, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition.getSearches().put(
		    "6",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithOtherReason, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition
		        .setCompositionString("1 and ((not 2) and (not 3) and (not 4) and (not 5) and (not 6)) ");
		
		CohortIndicator numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralIndicator = Indicators
		        .newCountIndicator("numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralIndicator",
		            numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "B5",
		    "Number of people treated for breast concerns without requiring referral",
		    new Mapped(numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition30 = new CompositionCohortDefinition();
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition30
		        .setName("numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition30");
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition30.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition30.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition30.addParameter(new Parameter(
		        "effectiveDate", "effectiveDate", Date.class));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition30.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition30.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReferral, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition30.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsFurtherManagement,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition30.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsImaging, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition30.getSearches().put(
		    "5",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsBiopsy, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition30.getSearches().put(
		    "6",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithOtherReason, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition30.getSearches().put(
		    "7",
		    new Mapped<CohortDefinition>(below30Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition30
		        .setCompositionString("1 and ((not 2) and (not 3) and (not 4) and (not 5) and (not 6)) and 7 ");
		
		CohortIndicator numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralIndicator30 = Indicators
		        .newCountIndicator("numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralIndicator30",
		            numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition30, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B530",
		    "Number of people under 30 treated for breast concerns without requiring referral",
		    new Mapped(numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralIndicator30, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition3049 = new CompositionCohortDefinition();
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition3049
		        .setName("numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition3049");
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition3049.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition3049.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition3049.addParameter(new Parameter(
		        "effectiveDate", "effectiveDate", Date.class));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition3049.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition3049.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReferral, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition3049.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsFurtherManagement,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition3049.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsImaging, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition3049.getSearches().put(
		    "5",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsBiopsy, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition3049.getSearches().put(
		    "6",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithOtherReason, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition3049.getSearches().put(
		    "7",
		    new Mapped<CohortDefinition>(between30And49Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition3049
		        .setCompositionString("1 and ((not 2) and (not 3) and (not 4) and (not 5) and (not 6)) and 7 ");
		
		CohortIndicator numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralIndicator3049 = Indicators
		        .newCountIndicator("numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralIndicator3049",
		            numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition3049, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B53049",
		    "Number of people between 30 and 49 treated for breast concerns without requiring referral",
		    new Mapped(numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralIndicator3049, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition50 = new CompositionCohortDefinition();
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition50
		        .setName("numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition50");
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition50.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition50.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition50.addParameter(new Parameter(
		        "effectiveDate", "effectiveDate", Date.class));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition50.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition50.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReferral, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition50.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsFurtherManagement,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition50.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsImaging, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition50.getSearches().put(
		    "5",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsBiopsy, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition50.getSearches().put(
		    "6",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithOtherReason, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition50.getSearches().put(
		    "7",
		    new Mapped<CohortDefinition>(above50y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition50
		        .setCompositionString("1 and ((not 2) and (not 3) and (not 4) and (not 5) and (not 6)) and 7 ");
		
		CohortIndicator numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralIndicator50 = Indicators
		        .newCountIndicator("numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralIndicator50",
		            numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralComposition50, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B550",
		    "Number of people between 30 and 49 treated for breast concerns without requiring referral",
		    new Mapped(numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralIndicator50, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		// B6. Number of people referred for further evaluation of abnormal clinical breast exam
		
		CompositionCohortDefinition numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition = new CompositionCohortDefinition();
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition
		        .setName("numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition");
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition.addParameter(new Parameter(
		        "endDate", "endDate", Date.class));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReferral, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsFurtherManagement,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsImaging, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition.getSearches().put(
		    "5",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsBiopsy, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition.getSearches().put(
		    "6",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithOtherReason, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition
		        .setCompositionString("1 and (2 or 3 or 4 or 5 or 6)");
		
		CohortIndicator numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastIndicator = Indicators
		        .newCountIndicator("numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastIndicator",
		            numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "B6",
		    "Number of people referred for further evaluation of abnormal clinical breast exam",
		    new Mapped(numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition30 = new CompositionCohortDefinition();
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition30
		        .setName("numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition30");
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition30.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition30.addParameter(new Parameter(
		        "endDate", "endDate", Date.class));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition30.addParameter(new Parameter(
		        "effectiveDate", "effectiveDate", Date.class));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition30.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition30.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReferral, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition30.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsFurtherManagement,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition30.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsImaging, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition30.getSearches().put(
		    "5",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsBiopsy, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition30.getSearches().put(
		    "6",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithOtherReason, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition30.getSearches().put(
		    "7",
		    new Mapped<CohortDefinition>(below30Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition30
		        .setCompositionString("1 and (2 or 3 or 4 or 5 or 6) and 7");
		
		CohortIndicator numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastIndicator30 = Indicators
		        .newCountIndicator("numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastIndicator30",
		            numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition30, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B630",
		    "Number of people under 30 referred for further evaluation of abnormal clinical breast exam",
		    new Mapped(numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastIndicator30, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition3049 = new CompositionCohortDefinition();
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition3049
		        .setName("numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition3049");
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition3049.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition3049.addParameter(new Parameter(
		        "endDate", "endDate", Date.class));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition3049.addParameter(new Parameter(
		        "effectiveDate", "effectiveDate", Date.class));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition3049.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition3049.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReferral, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition3049.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsFurtherManagement,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition3049.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsImaging, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition3049.getSearches().put(
		    "5",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsBiopsy, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition3049.getSearches().put(
		    "6",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithOtherReason, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition3049.getSearches().put(
		    "7",
		    new Mapped<CohortDefinition>(between30And49Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition3049
		        .setCompositionString("1 and (2 or 3 or 4 or 5 or 6) and 7");
		
		CohortIndicator numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastIndicator3049 = Indicators
		        .newCountIndicator("numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastIndicator3049",
		            numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition3049,
		            ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B63049",
		    "Number of people between 30 and 49 referred for further evaluation of abnormal clinical breast exam",
		    new Mapped(numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastIndicator3049, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition50 = new CompositionCohortDefinition();
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition50
		        .setName("numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition50");
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition50.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition50.addParameter(new Parameter(
		        "endDate", "endDate", Date.class));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition50.addParameter(new Parameter(
		        "effectiveDate", "effectiveDate", Date.class));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition50.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition50.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReferral, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition50.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsFurtherManagement,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition50.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsImaging, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition50.getSearches().put(
		    "5",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReasonForReferralAsBiopsy, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition50.getSearches().put(
		    "6",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithOtherReason, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition50.getSearches().put(
		    "7",
		    new Mapped<CohortDefinition>(above50y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition50
		        .setCompositionString("1 and (2 or 3 or 4 or 5 or 6) and 7");
		
		CohortIndicator numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastIndicator50 = Indicators
		        .newCountIndicator("numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastIndicator50",
		            numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastExamComposition50, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B650",
		    "Number of people with 50 years and above referred for further evaluation of abnormal clinical breast exam",
		    new Mapped(numberOfPeopleReferredForFurtherEvaluationOfAbnormalClinicalBreastIndicator50, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//================================================
		// Additional indicators for Hospitals (Breast) //
		//================================================
		
		// B7. Number of people referred from Health center  to the hospital for abnormal breast findings received by the hospital
		
		SqlCohortDefinition tranferInForBreastCancerWithReasonForReferralAsFurtherManagement = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForBreastCancerWithReasonForReferralAsFurtherManagement", breastCancerScreeningForms,
		            reasonsForReferralIn, furtherManagement);
		SqlCohortDefinition tranferInForBreastCancerWithReasonForReferralAsImaging = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForBreastCancerWithReasonForReferralAsImaging", breastCancerScreeningForms,
		            reasonsForReferralIn, medicalImaging);
		SqlCohortDefinition tranferInForBreastCancerWithReasonForReferralAsBiopsy = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForBreastCancerWithReasonForReferralAsBiopsy", breastCancerScreeningForms,
		            reasonsForReferralIn, BIOPSY);
		SqlCohortDefinition tranferInForBreastCancerWithOtherReason = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate("screenedForBreastCancerWithOtherReason",
		            breastCancerScreeningForms, reasonsForReferralIn, OTHERNONCODED);
		
		CompositionCohortDefinition transferedInForAbnormalBreastfindingsAndReachedTheHospital = new CompositionCohortDefinition();
		transferedInForAbnormalBreastfindingsAndReachedTheHospital
		        .setName("transferedInForAbnormalBreastfindingsAndReachedTheHospital");
		transferedInForAbnormalBreastfindingsAndReachedTheHospital.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReferral, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(tranferInForBreastCancerWithReasonForReferralAsFurtherManagement,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(tranferInForBreastCancerWithReasonForReferralAsImaging, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital.getSearches().put(
		    "5",
		    new Mapped<CohortDefinition>(tranferInForBreastCancerWithReasonForReferralAsBiopsy, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital.getSearches().put(
		    "6",
		    new Mapped<CohortDefinition>(tranferInForBreastCancerWithOtherReason, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		
		transferedInForAbnormalBreastfindingsAndReachedTheHospital.setCompositionString("1 and 2 and (3 or 4 or 5 or 6) ");
		
		CohortIndicator transferedInForAbnormalBreastfindingsAndReachedTheHospitalIndicator = Indicators.newCountIndicator(
		    "numberOfPeopleTreatedForBreastConcernsWithoutRequiringReferralIndicator",
		    transferedInForAbnormalBreastfindingsAndReachedTheHospital,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "B7",
		    "Number people referred from Health center  to the hospital for abnormal breast findings received by the hospital",
		    new Mapped(transferedInForAbnormalBreastfindingsAndReachedTheHospitalIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition transferedInForAbnormalBreastfindingsAndReachedTheHospital30 = new CompositionCohortDefinition();
		transferedInForAbnormalBreastfindingsAndReachedTheHospital30
		        .setName("transferedInForAbnormalBreastfindingsAndReachedTheHospital30");
		transferedInForAbnormalBreastfindingsAndReachedTheHospital30.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital30.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital30.addParameter(new Parameter("effectiveDate",
		        "effectiveDate", Date.class));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital30.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital30.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReferral, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital30.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(tranferInForBreastCancerWithReasonForReferralAsFurtherManagement,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital30.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(tranferInForBreastCancerWithReasonForReferralAsImaging, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital30.getSearches().put(
		    "5",
		    new Mapped<CohortDefinition>(tranferInForBreastCancerWithReasonForReferralAsBiopsy, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital30.getSearches().put(
		    "6",
		    new Mapped<CohortDefinition>(tranferInForBreastCancerWithOtherReason, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital30.getSearches().put(
		    "7",
		    new Mapped<CohortDefinition>(below30Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		transferedInForAbnormalBreastfindingsAndReachedTheHospital30
		        .setCompositionString("1 and 2 and (3 or 4 or 5 or 6) and 7 ");
		
		CohortIndicator transferedInForAbnormalBreastfindingsAndReachedTheHospitalIndicator30 = Indicators
		        .newCountIndicator("transferedInForAbnormalBreastfindingsAndReachedTheHospitalIndicator30",
		            transferedInForAbnormalBreastfindingsAndReachedTheHospital30, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B730",
		    "Number of people under 30 referred from Health center  to the hospital for abnormal breast findings received by the hospital",
		    new Mapped(transferedInForAbnormalBreastfindingsAndReachedTheHospitalIndicator30, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition transferedInForAbnormalBreastfindingsAndReachedTheHospital3049 = new CompositionCohortDefinition();
		transferedInForAbnormalBreastfindingsAndReachedTheHospital3049
		        .setName("transferedInForAbnormalBreastfindingsAndReachedTheHospital3049");
		transferedInForAbnormalBreastfindingsAndReachedTheHospital3049.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital3049.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital3049.addParameter(new Parameter("effectiveDate",
		        "effectiveDate", Date.class));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital3049.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital3049.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReferral, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital3049.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(tranferInForBreastCancerWithReasonForReferralAsFurtherManagement,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital3049.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(tranferInForBreastCancerWithReasonForReferralAsImaging, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital3049.getSearches().put(
		    "5",
		    new Mapped<CohortDefinition>(tranferInForBreastCancerWithReasonForReferralAsBiopsy, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital3049.getSearches().put(
		    "6",
		    new Mapped<CohortDefinition>(tranferInForBreastCancerWithOtherReason, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital3049.getSearches().put(
		    "7",
		    new Mapped<CohortDefinition>(between30And49Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		transferedInForAbnormalBreastfindingsAndReachedTheHospital3049
		        .setCompositionString("1 and 2 and (3 or 4 or 5 or 6) and 7 ");
		
		CohortIndicator transferedInForAbnormalBreastfindingsAndReachedTheHospitalIndicator3049 = Indicators
		        .newCountIndicator("transferedInForAbnormalBreastfindingsAndReachedTheHospitalIndicator3049",
		            transferedInForAbnormalBreastfindingsAndReachedTheHospital3049, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B73049",
		    "Number of people between 30 and 49 referred from Health center  to the hospital for abnormal breast findings received by the hospital",
		    new Mapped(transferedInForAbnormalBreastfindingsAndReachedTheHospitalIndicator3049, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition transferedInForAbnormalBreastfindingsAndReachedTheHospital50 = new CompositionCohortDefinition();
		transferedInForAbnormalBreastfindingsAndReachedTheHospital50
		        .setName("transferedInForAbnormalBreastfindingsAndReachedTheHospital50");
		transferedInForAbnormalBreastfindingsAndReachedTheHospital50.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital50.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital50.addParameter(new Parameter("effectiveDate",
		        "effectiveDate", Date.class));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital50.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithAbnormalBreastFindings, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital50.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForBreastCancerWithReferral, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital50.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(tranferInForBreastCancerWithReasonForReferralAsFurtherManagement,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital50.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(tranferInForBreastCancerWithReasonForReferralAsImaging, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital50.getSearches().put(
		    "5",
		    new Mapped<CohortDefinition>(tranferInForBreastCancerWithReasonForReferralAsBiopsy, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital50.getSearches().put(
		    "6",
		    new Mapped<CohortDefinition>(tranferInForBreastCancerWithOtherReason, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		transferedInForAbnormalBreastfindingsAndReachedTheHospital50.getSearches().put(
		    "7",
		    new Mapped<CohortDefinition>(above50y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		transferedInForAbnormalBreastfindingsAndReachedTheHospital50
		        .setCompositionString("1 and 2 and (3 or 4 or 5 or 6) and 7 ");
		
		CohortIndicator transferedInForAbnormalBreastfindingsAndReachedTheHospitalIndicator50 = Indicators
		        .newCountIndicator("transferedInForAbnormalBreastfindingsAndReachedTheHospitalIndicator50",
		            transferedInForAbnormalBreastfindingsAndReachedTheHospital50, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B750",
		    "Number of people with 50 years and above referred from Health center  to the hospital for abnormal breast findings received by the hospital",
		    new Mapped(transferedInForAbnormalBreastfindingsAndReachedTheHospitalIndicator50, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		// B8. Number of people receiving diagnostic with  breast ultrasound
		
		SqlCohortDefinition numberofPeopleReceivingDiagnosticWithBreastUltrasound = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "numberofPeopleReceivingDiagnosticWithBreastUltrasound", breastCancerScreeningForms, breastUltrasound,
		            YES);
		
		CompositionCohortDefinition numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition = new CompositionCohortDefinition();
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition
		        .setName("numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition");
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberofPeopleReceivingDiagnosticWithBreastUltrasound, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition.setCompositionString("1");
		
		CohortIndicator numberofPeopleReceivingDiagnosticWithBreastUltrasoundIndicator = Indicators.newCountIndicator(
		    "numberofPeopleReceivingDiagnosticWithBreastUltrasoundIndicator",
		    numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "B8",
		    "Number of people receiving diagnostic with  breast ultrasound",
		    new Mapped(numberofPeopleReceivingDiagnosticWithBreastUltrasoundIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition30 = new CompositionCohortDefinition();
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition30
		        .setName("numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition30");
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition30.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition30.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition30.addParameter(new Parameter("effectiveDate",
		        "effectiveDate", Date.class));
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition30.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberofPeopleReceivingDiagnosticWithBreastUltrasound, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition30.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(below30Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition30.setCompositionString("1 and 2");
		
		CohortIndicator numberofPeopleReceivingDiagnosticWithBreastUltrasoundIndicator30 = Indicators.newCountIndicator(
		    "numberofPeopleReceivingDiagnosticWithBreastUltrasoundIndicator30",
		    numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition30, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B830",
		    "Number of people under 30 receiving diagnostic with  breast ultrasound",
		    new Mapped(numberofPeopleReceivingDiagnosticWithBreastUltrasoundIndicator30, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition3049 = new CompositionCohortDefinition();
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition3049
		        .setName("numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition3049");
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition3049.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition3049.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition3049.addParameter(new Parameter("effectiveDate",
		        "effectiveDate", Date.class));
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition3049.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberofPeopleReceivingDiagnosticWithBreastUltrasound, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition3049.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(between30And49Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition3049.setCompositionString("1 and 2");
		
		CohortIndicator numberofPeopleReceivingDiagnosticWithBreastUltrasoundIndicator3049 = Indicators.newCountIndicator(
		    "numberofPeopleReceivingDiagnosticWithBreastUltrasoundIndicator3049",
		    numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition3049, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B83049",
		    "Number of people between 30 and 49 receiving diagnostic with  breast ultrasound",
		    new Mapped(numberofPeopleReceivingDiagnosticWithBreastUltrasoundIndicator3049, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition50 = new CompositionCohortDefinition();
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition50
		        .setName("numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition50");
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition50.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition50.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition50.addParameter(new Parameter("effectiveDate",
		        "effectiveDate", Date.class));
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition50.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberofPeopleReceivingDiagnosticWithBreastUltrasound, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition50.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(above50y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition50.setCompositionString("1 and 2");
		
		CohortIndicator numberofPeopleReceivingDiagnosticWithBreastUltrasoundIndicator50 = Indicators.newCountIndicator(
		    "numberofPeopleReceivingDiagnosticWithBreastUltrasoundIndicator50",
		    numberofPeopleReceivingDiagnosticWithBreastUltrasoundComposition50, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B850",
		    "Number of people with 50 years and above receiving diagnostic with  breast ultrasound",
		    new Mapped(numberofPeopleReceivingDiagnosticWithBreastUltrasoundIndicator50, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		// B9. Number of  screened people with suspected breast cancer at breast ultrasound
		
		SqlCohortDefinition numberOfScreenedPeopleWithIntermediateSolidMass = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "numberOfScreenedPeopleWithIntermediateSolidMass", breastCancerScreeningForms, solidMass, intermediate);
		SqlCohortDefinition numberOfScreenedPeopleWithHighSuspiciousForMalignancySolidMass = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "numberOfScreenedPeopleWithHighSuspiciousForMalignancySolidMass", breastCancerScreeningForms, solidMass,
		            highSuspiciousForMalignancy);
		
		CompositionCohortDefinition numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition = new CompositionCohortDefinition();
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition
		        .setName("numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition");
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition.addParameter(new Parameter("startDate",
		        "startDate", Date.class));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberofPeopleReceivingDiagnosticWithBreastUltrasound, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(numberOfScreenedPeopleWithIntermediateSolidMass, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(numberOfScreenedPeopleWithHighSuspiciousForMalignancySolidMass, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition.setCompositionString("1 and (2 or 3)");
		
		CohortIndicator numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundIndicator = Indicators
		        .newCountIndicator("numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundIndicator",
		            numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition,
		            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "B9",
		    "Number of  screened people with suspected breast cancer at breast ultrasound",
		    new Mapped(numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition30 = new CompositionCohortDefinition();
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition30
		        .setName("numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition30");
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition30.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition30.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition30.addParameter(new Parameter(
		        "effectiveDate", "effectiveDate", Date.class));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition30.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberofPeopleReceivingDiagnosticWithBreastUltrasound, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition30.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(numberOfScreenedPeopleWithIntermediateSolidMass, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition30.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(numberOfScreenedPeopleWithHighSuspiciousForMalignancySolidMass, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition30.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(below30Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition30
		        .setCompositionString("1 and (2 or 3) and 4");
		
		CohortIndicator numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundIndicator30 = Indicators
		        .newCountIndicator("numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundIndicator30",
		            numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition30, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B930",
		    "Number of  screened people under 30 with suspected breast cancer at breast ultrasound",
		    new Mapped(numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundIndicator30, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition3049 = new CompositionCohortDefinition();
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition3049
		        .setName("numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition3049");
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition3049.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition3049.addParameter(new Parameter(
		        "endDate", "endDate", Date.class));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition3049.addParameter(new Parameter(
		        "effectiveDate", "effectiveDate", Date.class));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition3049.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberofPeopleReceivingDiagnosticWithBreastUltrasound, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition3049.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(numberOfScreenedPeopleWithIntermediateSolidMass, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition3049.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(numberOfScreenedPeopleWithHighSuspiciousForMalignancySolidMass, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition3049.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(between30And49Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition3049
		        .setCompositionString("1 and (2 or 3) and 4");
		
		CohortIndicator numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundIndicator3049 = Indicators
		        .newCountIndicator("numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundIndicator3049",
		            numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition3049, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B93049",
		    "Number of  screened people between 30 and 49 with suspected breast cancer at breast ultrasound",
		    new Mapped(numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundIndicator3049, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition50 = new CompositionCohortDefinition();
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition50
		        .setName("numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition50");
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition50.addParameter(new Parameter(
		        "startDate", "startDate", Date.class));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition50.addParameter(new Parameter("endDate",
		        "endDate", Date.class));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition50.addParameter(new Parameter(
		        "effectiveDate", "effectiveDate", Date.class));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition50.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberofPeopleReceivingDiagnosticWithBreastUltrasound, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition50.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(numberOfScreenedPeopleWithIntermediateSolidMass, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition50.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(numberOfScreenedPeopleWithHighSuspiciousForMalignancySolidMass, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition50.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(above50y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition50
		        .setCompositionString("1 and (2 or 3) and 4");
		
		CohortIndicator numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundIndicator50 = Indicators
		        .newCountIndicator("numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundIndicator50",
		            numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundComposition50, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B950",
		    "Number of  screened people with 50 years and above with suspected breast cancer at breast ultrasound",
		    new Mapped(numberOfScreenedPeopleWithSuspectedBreastCancerAtBreastUltrasoundIndicator50, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		// B10. Number of  women with breast biopsy performed
		
		SqlCohortDefinition numberOfPatientWithBreastBiopsyPerformed = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate("numberOfWomenWithBreastBiopsyPerformed",
		            breastCancerScreeningForms, proceduresDone, BIOPSY);
		
		CompositionCohortDefinition numberOfWomenWithBreastBiopsyPerformedComposition = new CompositionCohortDefinition();
		numberOfWomenWithBreastBiopsyPerformedComposition.setName("numberOfWomenWithBreastBiopsyPerformedComposition");
		numberOfWomenWithBreastBiopsyPerformedComposition.addParameter(new Parameter("startDate", "startDate", Date.class));
		numberOfWomenWithBreastBiopsyPerformedComposition.addParameter(new Parameter("endDate", "endDate", Date.class));
		numberOfWomenWithBreastBiopsyPerformedComposition.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberOfPatientWithBreastBiopsyPerformed, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfWomenWithBreastBiopsyPerformedComposition.getSearches().put("2", new Mapped<CohortDefinition>(female, null));
		
		numberOfWomenWithBreastBiopsyPerformedComposition.setCompositionString("1 and 2");
		
		CohortIndicator numberOfWomenWithBreastBiopsyPerformedCompositionIndicator = Indicators.newCountIndicator(
		    "numberOfWomenWithBreastBiopsyPerformedCompositionIndicator", numberOfWomenWithBreastBiopsyPerformedComposition,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "B10",
		    "Number of  women with breast biopsy performed",
		    new Mapped(numberOfWomenWithBreastBiopsyPerformedCompositionIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberOfWomenWithBreastBiopsyPerformedComposition30 = new CompositionCohortDefinition();
		numberOfWomenWithBreastBiopsyPerformedComposition30.setName("numberOfWomenWithBreastBiopsyPerformedComposition30");
		numberOfWomenWithBreastBiopsyPerformedComposition30
		        .addParameter(new Parameter("startDate", "startDate", Date.class));
		numberOfWomenWithBreastBiopsyPerformedComposition30.addParameter(new Parameter("endDate", "endDate", Date.class));
		numberOfWomenWithBreastBiopsyPerformedComposition30.addParameter(new Parameter("effectiveDate", "effectiveDate",
		        Date.class));
		numberOfWomenWithBreastBiopsyPerformedComposition30.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberOfPatientWithBreastBiopsyPerformed, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfWomenWithBreastBiopsyPerformedComposition30.getSearches().put("2",
		    new Mapped<CohortDefinition>(female, null));
		numberOfWomenWithBreastBiopsyPerformedComposition30.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(below30Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberOfWomenWithBreastBiopsyPerformedComposition30.setCompositionString("1 and 2 and 3");
		
		CohortIndicator numberOfWomenWithBreastBiopsyPerformedCompositionIndicator30 = Indicators.newCountIndicator(
		    "numberOfWomenWithBreastBiopsyPerformedCompositionIndicator30",
		    numberOfWomenWithBreastBiopsyPerformedComposition30, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B1030",
		    "Number of  women under 30 with breast biopsy performed",
		    new Mapped(numberOfWomenWithBreastBiopsyPerformedCompositionIndicator30, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberOfWomenWithBreastBiopsyPerformedComposition3049 = new CompositionCohortDefinition();
		numberOfWomenWithBreastBiopsyPerformedComposition3049
		        .setName("numberOfWomenWithBreastBiopsyPerformedComposition3049");
		numberOfWomenWithBreastBiopsyPerformedComposition3049.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		numberOfWomenWithBreastBiopsyPerformedComposition3049.addParameter(new Parameter("endDate", "endDate", Date.class));
		numberOfWomenWithBreastBiopsyPerformedComposition3049.addParameter(new Parameter("effectiveDate", "effectiveDate",
		        Date.class));
		numberOfWomenWithBreastBiopsyPerformedComposition3049.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberOfPatientWithBreastBiopsyPerformed, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfWomenWithBreastBiopsyPerformedComposition3049.getSearches().put("2",
		    new Mapped<CohortDefinition>(female, null));
		numberOfWomenWithBreastBiopsyPerformedComposition3049.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(between30And49Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberOfWomenWithBreastBiopsyPerformedComposition3049.setCompositionString("1 and 2 and 3");
		
		CohortIndicator numberOfWomenWithBreastBiopsyPerformedCompositionIndicator3049 = Indicators.newCountIndicator(
		    "numberOfWomenWithBreastBiopsyPerformedCompositionIndicator3049",
		    numberOfWomenWithBreastBiopsyPerformedComposition3049, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B103049",
		    "Number of  women between 30 and 49 with breast biopsy performed",
		    new Mapped(numberOfWomenWithBreastBiopsyPerformedCompositionIndicator3049, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberOfWomenWithBreastBiopsyPerformedComposition50 = new CompositionCohortDefinition();
		numberOfWomenWithBreastBiopsyPerformedComposition50.setName("numberOfWomenWithBreastBiopsyPerformedComposition50");
		numberOfWomenWithBreastBiopsyPerformedComposition50
		        .addParameter(new Parameter("startDate", "startDate", Date.class));
		numberOfWomenWithBreastBiopsyPerformedComposition50.addParameter(new Parameter("endDate", "endDate", Date.class));
		numberOfWomenWithBreastBiopsyPerformedComposition50.addParameter(new Parameter("effectiveDate", "effectiveDate",
		        Date.class));
		numberOfWomenWithBreastBiopsyPerformedComposition50.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberOfPatientWithBreastBiopsyPerformed, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfWomenWithBreastBiopsyPerformedComposition50.getSearches().put("2",
		    new Mapped<CohortDefinition>(female, null));
		numberOfWomenWithBreastBiopsyPerformedComposition50.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(above50y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberOfWomenWithBreastBiopsyPerformedComposition50.setCompositionString("1 and 2 and 3");
		
		CohortIndicator numberOfWomenWithBreastBiopsyPerformedCompositionIndicator50 = Indicators.newCountIndicator(
		    "numberOfWomenWithBreastBiopsyPerformedCompositionIndicator50",
		    numberOfWomenWithBreastBiopsyPerformedComposition50, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B1050",
		    "Number of  women with 50 years and above with breast biopsy performed",
		    new Mapped(numberOfWomenWithBreastBiopsyPerformedCompositionIndicator50, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//B11. Number of women with suspected  breast cancer referred to other levels  (skipped)
		
		//B12. Number of women screened for cervical cancer with  Papsmear/cytology
		
		SqlCohortDefinition screenedForCervicalCancerWithPapSmear = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate("screenedForCervicalCancerWithPapSmear",
		            screeningExaminationForms, screeningType, PAPANICOLAOUSMEAR);
		
		CompositionCohortDefinition femaleScreenedForCervicalCancerWithPapSmear = new CompositionCohortDefinition();
		femaleScreenedForCervicalCancerWithPapSmear.setName("femaleScreenedForCervicalCancerWithPapSmear");
		femaleScreenedForCervicalCancerWithPapSmear.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleScreenedForCervicalCancerWithPapSmear.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleScreenedForCervicalCancerWithPapSmear.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithPapSmear, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithPapSmear.getSearches().put("2", new Mapped<CohortDefinition>(female, null));
		femaleScreenedForCervicalCancerWithPapSmear.setCompositionString("1 and 2");
		
		CohortIndicator femaleScreenedForCervicalCancerWithPapSmearIndicator = Indicators.newCountIndicator(
		    "femaleScreenedForCervicalCancerWithPapSmearIndicator", femaleScreenedForCervicalCancerWithPapSmear,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "B12",
		    "Number of women screened for cervical cancer with  Papsmear/cytology",
		    new Mapped(femaleScreenedForCervicalCancerWithPapSmearIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition femaleScreenedForCervicalCancerWithPapSmear30 = new CompositionCohortDefinition();
		femaleScreenedForCervicalCancerWithPapSmear30.setName("femaleScreenedForCervicalCancerWithPapSmear");
		femaleScreenedForCervicalCancerWithPapSmear30.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleScreenedForCervicalCancerWithPapSmear30.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleScreenedForCervicalCancerWithPapSmear30.addParameter(new Parameter("effectiveDate", "effectiveDate",
		        Date.class));
		femaleScreenedForCervicalCancerWithPapSmear30.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithPapSmear, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithPapSmear30.getSearches().put("2", new Mapped<CohortDefinition>(female, null));
		femaleScreenedForCervicalCancerWithPapSmear30.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(below30Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		femaleScreenedForCervicalCancerWithPapSmear30.setCompositionString("1 and 2 and 3");
		
		CohortIndicator femaleScreenedForCervicalCancerWithPapSmearIndicator30 = Indicators.newCountIndicator(
		    "femaleScreenedForCervicalCancerWithPapSmearIndicator30", femaleScreenedForCervicalCancerWithPapSmear30,
		    ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B1230",
		    "Number of women under 30 screened for cervical cancer with  Papsmear/cytology",
		    new Mapped(femaleScreenedForCervicalCancerWithPapSmearIndicator30, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition femaleScreenedForCervicalCancerWithPapSmear3049 = new CompositionCohortDefinition();
		femaleScreenedForCervicalCancerWithPapSmear3049.setName("femaleScreenedForCervicalCancerWithPapSmear3049");
		femaleScreenedForCervicalCancerWithPapSmear3049.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleScreenedForCervicalCancerWithPapSmear3049.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleScreenedForCervicalCancerWithPapSmear3049.addParameter(new Parameter("effectiveDate", "effectiveDate",
		        Date.class));
		femaleScreenedForCervicalCancerWithPapSmear3049.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithPapSmear, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithPapSmear3049.getSearches().put("2", new Mapped<CohortDefinition>(female, null));
		femaleScreenedForCervicalCancerWithPapSmear3049.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(between30And49Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		femaleScreenedForCervicalCancerWithPapSmear3049.setCompositionString("1 and 2 and 3");
		
		CohortIndicator femaleScreenedForCervicalCancerWithPapSmearIndicator3049 = Indicators.newCountIndicator(
		    "femaleScreenedForCervicalCancerWithPapSmearIndicator3049", femaleScreenedForCervicalCancerWithPapSmear3049,
		    ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B123049",
		    "Number of women between 30 and 49 screened for cervical cancer with  Papsmear/cytology",
		    new Mapped(femaleScreenedForCervicalCancerWithPapSmearIndicator3049, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition femaleScreenedForCervicalCancerWithPapSmear50 = new CompositionCohortDefinition();
		femaleScreenedForCervicalCancerWithPapSmear50.setName("femaleScreenedForCervicalCancerWithPapSmear50");
		femaleScreenedForCervicalCancerWithPapSmear50.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleScreenedForCervicalCancerWithPapSmear50.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleScreenedForCervicalCancerWithPapSmear50.addParameter(new Parameter("effectiveDate", "effectiveDate",
		        Date.class));
		femaleScreenedForCervicalCancerWithPapSmear50.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithPapSmear, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithPapSmear50.getSearches().put("2", new Mapped<CohortDefinition>(female, null));
		femaleScreenedForCervicalCancerWithPapSmear50.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(above50y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		femaleScreenedForCervicalCancerWithPapSmear50.setCompositionString("1 and 2 and 3");
		
		CohortIndicator femaleScreenedForCervicalCancerWithPapSmearIndicator50 = Indicators.newCountIndicator(
		    "femaleScreenedForCervicalCancerWithPapSmearIndicator50", femaleScreenedForCervicalCancerWithPapSmear50,
		    ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B1250",
		    "Number of women with 50 years and above screened for cervical cancer with  Papsmear/cytology",
		    new Mapped(femaleScreenedForCervicalCancerWithPapSmearIndicator50, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//B13. Number of women  screened  with Papsmear with abnomal papsmear result
		
		SqlCohortDefinition screenedForCervicalCancerWithAbnormalPapSmearResults = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForCervicalCancerWithAbnormalPapSmearResults", screeningExaminationForms, PAPANICOLAOUSMEAR,
		            ABNORMAL);
		
		CompositionCohortDefinition femaleScreenedForCervicalCancerWithAbnormalPapSmearResults = new CompositionCohortDefinition();
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults
		        .setName("femaleScreenedForCervicalCancerWithAbnormalPapSmearResults");
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithPapSmear, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithAbnormalPapSmearResults, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults.getSearches().put("3",
		    new Mapped<CohortDefinition>(female, null));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults.setCompositionString("1 and 2 and 3");
		
		CohortIndicator femaleScreenedForCervicalCancerWithAbnormalPapSmearResultsIndicator = Indicators.newCountIndicator(
		    "femaleScreenedForCervicalCancerWithAbnormalPapSmearResultsIndicator",
		    femaleScreenedForCervicalCancerWithAbnormalPapSmearResults,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "B13",
		    "Number of women  screened  with Papsmear with abnomal papsmear result",
		    new Mapped(femaleScreenedForCervicalCancerWithAbnormalPapSmearResultsIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition femaleScreenedForCervicalCancerWithAbnormalPapSmearResults30 = new CompositionCohortDefinition();
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults30
		        .setName("femaleScreenedForCervicalCancerWithAbnormalPapSmearResults30");
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults30.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults30.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults30.addParameter(new Parameter("effectiveDate",
		        "effectiveDate", Date.class));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults30.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithPapSmear, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults30.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithAbnormalPapSmearResults, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults30.getSearches().put("3",
		    new Mapped<CohortDefinition>(female, null));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults30.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(below30Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults30.setCompositionString("1 and 2 and 3 and 4");
		
		CohortIndicator femaleScreenedForCervicalCancerWithAbnormalPapSmearResultsIndicator30 = Indicators
		        .newCountIndicator("femaleScreenedForCervicalCancerWithAbnormalPapSmearResultsIndicator30",
		            femaleScreenedForCervicalCancerWithAbnormalPapSmearResults30, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B1330",
		    "Number of women  under 30 screened  with Papsmear with abnomal papsmear result",
		    new Mapped(femaleScreenedForCervicalCancerWithAbnormalPapSmearResultsIndicator30, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition femaleScreenedForCervicalCancerWithAbnormalPapSmearResults3049 = new CompositionCohortDefinition();
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults3049
		        .setName("femaleScreenedForCervicalCancerWithAbnormalPapSmearResults3049");
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults3049.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults3049.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults3049.addParameter(new Parameter("effectiveDate",
		        "effectiveDate", Date.class));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults3049.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithPapSmear, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults3049.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithAbnormalPapSmearResults, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults3049.getSearches().put("3",
		    new Mapped<CohortDefinition>(female, null));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults3049.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(between30And49Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults3049.setCompositionString("1 and 2 and 3 and 4");
		
		CohortIndicator femaleScreenedForCervicalCancerWithAbnormalPapSmearResultsIndicator3049 = Indicators
		        .newCountIndicator("femaleScreenedForCervicalCancerWithAbnormalPapSmearResultsIndicator3049",
		            femaleScreenedForCervicalCancerWithAbnormalPapSmearResults3049, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B133049",
		    "Number of women  between 30 and 49 screened  with Papsmear with abnomal papsmear result",
		    new Mapped(femaleScreenedForCervicalCancerWithAbnormalPapSmearResultsIndicator3049, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition femaleScreenedForCervicalCancerWithAbnormalPapSmearResults50 = new CompositionCohortDefinition();
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults50
		        .setName("femaleScreenedForCervicalCancerWithAbnormalPapSmearResults50");
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults50.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults50.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults50.addParameter(new Parameter("effectiveDate",
		        "effectiveDate", Date.class));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults50.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithPapSmear, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults50.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithAbnormalPapSmearResults, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults50.getSearches().put("3",
		    new Mapped<CohortDefinition>(female, null));
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults50.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(above50y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		femaleScreenedForCervicalCancerWithAbnormalPapSmearResults50.setCompositionString("1 and 2 and 3 and 4");
		
		CohortIndicator femaleScreenedForCervicalCancerWithAbnormalPapSmearResultsIndicator50 = Indicators
		        .newCountIndicator("femaleScreenedForCervicalCancerWithAbnormalPapSmearResultsIndicator50",
		            femaleScreenedForCervicalCancerWithAbnormalPapSmearResults50, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B1350",
		    "Number of women  with 50 years and above screened  with Papsmear with abnomal papsmear result",
		    new Mapped(femaleScreenedForCervicalCancerWithAbnormalPapSmearResultsIndicator50, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//B14. Number of women  screened  with Papsmear with normal papsmear result
		
		SqlCohortDefinition screenedForCervicalCancerWithNormalPapSmearResults = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		            "screenedForCervicalCancerWithNormalPapSmearResults", screeningExaminationForms, PAPANICOLAOUSMEAR,
		            NORMAL);
		
		CompositionCohortDefinition femaleScreenedForCervicalCancerWithNormalPapSmearResults = new CompositionCohortDefinition();
		femaleScreenedForCervicalCancerWithNormalPapSmearResults
		        .setName("femaleScreenedForCervicalCancerWithNormalPapSmearResults");
		femaleScreenedForCervicalCancerWithNormalPapSmearResults.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults
		        .addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithPapSmear, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithNormalPapSmearResults, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults.getSearches().put("3",
		    new Mapped<CohortDefinition>(female, null));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults.setCompositionString("1 and 2 and 3");
		
		CohortIndicator femaleScreenedForCervicalCancerWithNormalPapSmearResultsIndicator = Indicators.newCountIndicator(
		    "femaleScreenedForCervicalCancerWithNormalPapSmearResultsIndicator",
		    femaleScreenedForCervicalCancerWithNormalPapSmearResults,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "B14",
		    "Number of women  screened  with Papsmear with normal papsmear result",
		    new Mapped(femaleScreenedForCervicalCancerWithNormalPapSmearResultsIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition femaleScreenedForCervicalCancerWithNormalPapSmearResults30 = new CompositionCohortDefinition();
		femaleScreenedForCervicalCancerWithNormalPapSmearResults30
		        .setName("femaleScreenedForCervicalCancerWithNormalPapSmearResults30");
		femaleScreenedForCervicalCancerWithNormalPapSmearResults30.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults30.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults30.addParameter(new Parameter("effectiveDate",
		        "effectiveDate", Date.class));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults30.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithPapSmear, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults30.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithNormalPapSmearResults, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults30.getSearches().put("3",
		    new Mapped<CohortDefinition>(female, null));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults30.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(below30Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		femaleScreenedForCervicalCancerWithNormalPapSmearResults30.setCompositionString("1 and 2 and 3 and 4");
		
		CohortIndicator femaleScreenedForCervicalCancerWithNormalPapSmearResultsIndicator30 = Indicators.newCountIndicator(
		    "femaleScreenedForCervicalCancerWithNormalPapSmearResultsIndicator30",
		    femaleScreenedForCervicalCancerWithNormalPapSmearResults30, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B1430",
		    "Number of women under 30 screened  with Papsmear with normal papsmear result",
		    new Mapped(femaleScreenedForCervicalCancerWithNormalPapSmearResultsIndicator30, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition femaleScreenedForCervicalCancerWithNormalPapSmearResults3049 = new CompositionCohortDefinition();
		femaleScreenedForCervicalCancerWithNormalPapSmearResults3049
		        .setName("femaleScreenedForCervicalCancerWithNormalPapSmearResults3049");
		femaleScreenedForCervicalCancerWithNormalPapSmearResults3049.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults3049.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults3049.addParameter(new Parameter("effectiveDate",
		        "effectiveDate", Date.class));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults3049.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithPapSmear, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults3049.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithNormalPapSmearResults, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults3049.getSearches().put("3",
		    new Mapped<CohortDefinition>(female, null));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults3049.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(between30And49Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		femaleScreenedForCervicalCancerWithNormalPapSmearResults3049.setCompositionString("1 and 2 and 3 and 4");
		
		CohortIndicator femaleScreenedForCervicalCancerWithNormalPapSmearResultsIndicator3049 = Indicators
		        .newCountIndicator("femaleScreenedForCervicalCancerWithNormalPapSmearResultsIndicator3049",
		            femaleScreenedForCervicalCancerWithNormalPapSmearResults3049, ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B143049",
		    "Number of women between 30 and 49 screened  with Papsmear with normal papsmear result",
		    new Mapped(femaleScreenedForCervicalCancerWithNormalPapSmearResultsIndicator3049, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition femaleScreenedForCervicalCancerWithNormalPapSmearResults50 = new CompositionCohortDefinition();
		femaleScreenedForCervicalCancerWithNormalPapSmearResults50
		        .setName("femaleScreenedForCervicalCancerWithNormalPapSmearResults50");
		femaleScreenedForCervicalCancerWithNormalPapSmearResults50.addParameter(new Parameter("startDate", "startDate",
		        Date.class));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults50.addParameter(new Parameter("endDate", "endDate",
		        Date.class));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults50.addParameter(new Parameter("effectiveDate",
		        "effectiveDate", Date.class));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults50.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithPapSmear, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults50.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(screenedForCervicalCancerWithNormalPapSmearResults, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults50.getSearches().put("3",
		    new Mapped<CohortDefinition>(female, null));
		femaleScreenedForCervicalCancerWithNormalPapSmearResults50.getSearches().put(
		    "4",
		    new Mapped<CohortDefinition>(above50y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		femaleScreenedForCervicalCancerWithNormalPapSmearResults50.setCompositionString("1 and 2 and 3 and 4");
		
		CohortIndicator femaleScreenedForCervicalCancerWithNormalPapSmearResultsIndicator50 = Indicators.newCountIndicator(
		    "femaleScreenedForCervicalCancerWithNormalPapSmearResultsIndicator50",
		    femaleScreenedForCervicalCancerWithNormalPapSmearResults50, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B1450",
		    "Number of women with 50 years and above screened  with Papsmear with normal papsmear result",
		    new Mapped(femaleScreenedForCervicalCancerWithNormalPapSmearResultsIndicator50, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//B15. Number of  biopsy confirmed cervical cancer
		
		SqlCohortDefinition numberOfConfirmedCancer = Cohorts.getPatientsWithObservationInEncounterBetweenStartAndEndDate(
		    "numberOfConfirmedCancer", diagnosisScreeningForms, CONFIRMEDDIAGNOSIS, YES);
		SqlCohortDefinition numberOfCervicalCancerDiagnised = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate("numberOfCervicalCancerDiagnised",
		            diagnosisScreeningForms, confirmedCancerDiagnosis, cervicalCancer);
		
		CompositionCohortDefinition numberOfBiopsyConfirmedCervicalCancer = new CompositionCohortDefinition();
		numberOfBiopsyConfirmedCervicalCancer.setName("numberOfBiopsyConfirmedCervicalCancer");
		numberOfBiopsyConfirmedCervicalCancer.addParameter(new Parameter("startDate", "startDate", Date.class));
		numberOfBiopsyConfirmedCervicalCancer.addParameter(new Parameter("endDate", "endDate", Date.class));
		numberOfBiopsyConfirmedCervicalCancer.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberOfConfirmedCancer, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfBiopsyConfirmedCervicalCancer.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(numberOfCervicalCancerDiagnised, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfBiopsyConfirmedCervicalCancer.setCompositionString("1 and 2");
		
		CohortIndicator numberOfBiopsyConfirmedCervicalCancerIndicator = Indicators.newCountIndicator(
		    "numberOfBiopsyConfirmedCervicalCancerIndicator", numberOfBiopsyConfirmedCervicalCancer,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "B15",
		    "Number of  biopsy confirmed cervical cancer",
		    new Mapped(numberOfBiopsyConfirmedCervicalCancerIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberOfBiopsyConfirmedCervicalCancer30 = new CompositionCohortDefinition();
		numberOfBiopsyConfirmedCervicalCancer30.setName("numberOfBiopsyConfirmedCervicalCancer30");
		numberOfBiopsyConfirmedCervicalCancer30.addParameter(new Parameter("startDate", "startDate", Date.class));
		numberOfBiopsyConfirmedCervicalCancer30.addParameter(new Parameter("endDate", "endDate", Date.class));
		numberOfBiopsyConfirmedCervicalCancer30.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		numberOfBiopsyConfirmedCervicalCancer30.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberOfConfirmedCancer, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfBiopsyConfirmedCervicalCancer30.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(numberOfCervicalCancerDiagnised, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfBiopsyConfirmedCervicalCancer30.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(below30Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberOfBiopsyConfirmedCervicalCancer30.setCompositionString("1 and 2 and 3");
		
		CohortIndicator numberOfBiopsyConfirmedCervicalCancerIndicator30 = Indicators.newCountIndicator(
		    "numberOfBiopsyConfirmedCervicalCancerIndicator30", numberOfBiopsyConfirmedCervicalCancer30, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B1530",
		    "Number of people under 30 with biopsy confirmed cervical cancer",
		    new Mapped(numberOfBiopsyConfirmedCervicalCancerIndicator30, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberOfBiopsyConfirmedCervicalCancer3049 = new CompositionCohortDefinition();
		numberOfBiopsyConfirmedCervicalCancer3049.setName("numberOfBiopsyConfirmedCervicalCancer3049");
		numberOfBiopsyConfirmedCervicalCancer3049.addParameter(new Parameter("startDate", "startDate", Date.class));
		numberOfBiopsyConfirmedCervicalCancer3049.addParameter(new Parameter("endDate", "endDate", Date.class));
		numberOfBiopsyConfirmedCervicalCancer3049.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		numberOfBiopsyConfirmedCervicalCancer3049.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberOfConfirmedCancer, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfBiopsyConfirmedCervicalCancer3049.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(numberOfCervicalCancerDiagnised, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfBiopsyConfirmedCervicalCancer3049.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(between30And49Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberOfBiopsyConfirmedCervicalCancer3049.setCompositionString("1 and 2 and 3");
		
		CohortIndicator numberOfBiopsyConfirmedCervicalCancerIndicator3049 = Indicators.newCountIndicator(
		    "numberOfBiopsyConfirmedCervicalCancerIndicator3049", numberOfBiopsyConfirmedCervicalCancer3049,
		    ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B153049",
		    "Number of people between 30 and 49 with biopsy confirmed cervical cancer",
		    new Mapped(numberOfBiopsyConfirmedCervicalCancerIndicator3049, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberOfBiopsyConfirmedCervicalCancer50 = new CompositionCohortDefinition();
		numberOfBiopsyConfirmedCervicalCancer50.setName("numberOfBiopsyConfirmedCervicalCancer50");
		numberOfBiopsyConfirmedCervicalCancer50.addParameter(new Parameter("startDate", "startDate", Date.class));
		numberOfBiopsyConfirmedCervicalCancer50.addParameter(new Parameter("endDate", "endDate", Date.class));
		numberOfBiopsyConfirmedCervicalCancer50.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		numberOfBiopsyConfirmedCervicalCancer50.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberOfConfirmedCancer, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfBiopsyConfirmedCervicalCancer50.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(numberOfCervicalCancerDiagnised, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfBiopsyConfirmedCervicalCancer50.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(above50y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberOfBiopsyConfirmedCervicalCancer50.setCompositionString("1 and 2 and 3");
		
		CohortIndicator numberOfBiopsyConfirmedCervicalCancerIndicator50 = Indicators.newCountIndicator(
		    "numberOfBiopsyConfirmedCervicalCancerIndicator50", numberOfBiopsyConfirmedCervicalCancer3049,
		    ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B1550",
		    "Number of people with 50 years and above with biopsy confirmed cervical cancer",
		    new Mapped(numberOfBiopsyConfirmedCervicalCancerIndicator50, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//B16. Number of people with biopsy confimed Breast cancer
		
		SqlCohortDefinition numberOfBreastCancerDiagnised = Cohorts
		        .getPatientsWithObservationInEncounterBetweenStartAndEndDate("numberOfBreastCancerDiagnised",
		            diagnosisScreeningForms, confirmedCancerDiagnosis, breastCancer);
		
		CompositionCohortDefinition numberOfBiopsyConfirmedBreastCancer = new CompositionCohortDefinition();
		numberOfBiopsyConfirmedBreastCancer.setName("numberOfBiopsyConfirmedBreastCancer");
		numberOfBiopsyConfirmedBreastCancer.addParameter(new Parameter("startDate", "startDate", Date.class));
		numberOfBiopsyConfirmedBreastCancer.addParameter(new Parameter("endDate", "endDate", Date.class));
		numberOfBiopsyConfirmedBreastCancer.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberOfConfirmedCancer, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfBiopsyConfirmedBreastCancer.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(numberOfBreastCancerDiagnised, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfBiopsyConfirmedBreastCancer.setCompositionString("1 and 2");
		
		CohortIndicator numberOfBiopsyConfirmedBreastCancerIndicator = Indicators.newCountIndicator(
		    "numberOfBiopsyConfirmedBreastCancerIndicator", numberOfBiopsyConfirmedBreastCancer,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "B16",
		    "Number of people with biopsy confimed Breast cancer",
		    new Mapped(numberOfBiopsyConfirmedBreastCancerIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberOfBiopsyConfirmedBreastCancer30 = new CompositionCohortDefinition();
		numberOfBiopsyConfirmedBreastCancer30.setName("numberOfBiopsyConfirmedBreastCancer30");
		numberOfBiopsyConfirmedBreastCancer30.addParameter(new Parameter("startDate", "startDate", Date.class));
		numberOfBiopsyConfirmedBreastCancer30.addParameter(new Parameter("endDate", "endDate", Date.class));
		numberOfBiopsyConfirmedBreastCancer30.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		numberOfBiopsyConfirmedBreastCancer30.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberOfConfirmedCancer, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfBiopsyConfirmedBreastCancer30.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(numberOfBreastCancerDiagnised, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfBiopsyConfirmedBreastCancer30.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(below30Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberOfBiopsyConfirmedBreastCancer30.setCompositionString("1 and 2 and 3");
		
		CohortIndicator numberOfBiopsyConfirmedBreastCancerIndicator30 = Indicators.newCountIndicator(
		    "numberOfBiopsyConfirmedBreastCancerIndicator30", numberOfBiopsyConfirmedBreastCancer30, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B1630",
		    "Number of people under 30 with biopsy confimed Breast cancer",
		    new Mapped(numberOfBiopsyConfirmedBreastCancerIndicator30, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberOfBiopsyConfirmedBreastCancer3049 = new CompositionCohortDefinition();
		numberOfBiopsyConfirmedBreastCancer3049.setName("numberOfBiopsyConfirmedBreastCancer3049");
		numberOfBiopsyConfirmedBreastCancer3049.addParameter(new Parameter("startDate", "startDate", Date.class));
		numberOfBiopsyConfirmedBreastCancer3049.addParameter(new Parameter("endDate", "endDate", Date.class));
		numberOfBiopsyConfirmedBreastCancer3049.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		numberOfBiopsyConfirmedBreastCancer3049.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberOfConfirmedCancer, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfBiopsyConfirmedBreastCancer3049.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(numberOfBreastCancerDiagnised, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfBiopsyConfirmedBreastCancer3049.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(between30And49Y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberOfBiopsyConfirmedBreastCancer3049.setCompositionString("1 and 2 and 3");
		
		CohortIndicator numberOfBiopsyConfirmedBreastCancerIndicator3049 = Indicators.newCountIndicator(
		    "numberOfBiopsyConfirmedBreastCancerIndicator3049", numberOfBiopsyConfirmedBreastCancer3049, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B163049",
		    "Number of people between 30 and 49 with biopsy confimed Breast cancer",
		    new Mapped(numberOfBiopsyConfirmedBreastCancerIndicator3049, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		CompositionCohortDefinition numberOfBiopsyConfirmedBreastCancer50 = new CompositionCohortDefinition();
		numberOfBiopsyConfirmedBreastCancer50.setName("numberOfBiopsyConfirmedBreastCancer50");
		numberOfBiopsyConfirmedBreastCancer50.addParameter(new Parameter("startDate", "startDate", Date.class));
		numberOfBiopsyConfirmedBreastCancer50.addParameter(new Parameter("endDate", "endDate", Date.class));
		numberOfBiopsyConfirmedBreastCancer50.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		numberOfBiopsyConfirmedBreastCancer50.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(numberOfConfirmedCancer, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfBiopsyConfirmedBreastCancer50.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(numberOfBreastCancerDiagnised, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		numberOfBiopsyConfirmedBreastCancer50.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(above50y, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		numberOfBiopsyConfirmedBreastCancer50.setCompositionString("1 and 2 and 3");
		
		CohortIndicator numberOfBiopsyConfirmedBreastCancerIndicator50 = Indicators.newCountIndicator(
		    "numberOfBiopsyConfirmedBreastCancerIndicator50", numberOfBiopsyConfirmedBreastCancer50, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn(
		    "B1650",
		    "Number of people with 50 years and above with biopsy confirmed Breast cancer",
		    new Mapped(numberOfBiopsyConfirmedBreastCancerIndicator50, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
	}
	
	private void setUpProperties() {
		oncologyBreastScreeningExamination = gp.getForm(GlobalPropertiesManagement.ONCOLOGY_BREAST_SCREENING_EXAMINATION);
		oncologyCervicalScreeningExamination = gp
		        .getForm(GlobalPropertiesManagement.ONCOLOGY_CERVICAL_SCREENING_EXAMINATION);
		
		mUzimaBreastScreening = Context.getFormService().getForm("mUzima Breast cancer screening");
		mUzimaCervicalScreening = Context.getFormService().getForm("mUzima Cervical cancer screening");
		
		parameterNames.add("onOrBefore");
		parameterNames.add("onOrAfter");
		
		//mUzimaCervicalCancerScreeningFollowup=Context.getFormService().getFormByUuid("94470633-8a84-4430-9910-10dcd628a0a2");
		mUzimaCervicalCancerScreeningFollowup = Context.getFormService().getForm(
		    "mUzima Cervical cancer screening follow up");
		OncologyCervicalScreeningFollowUp = Context.getFormService().getFormByUuid("9de98350-bc86-4012-a559-fcce13fc10c5");
		
		screeningExaminationForms.add(oncologyBreastScreeningExamination);
		screeningExaminationForms.add(oncologyCervicalScreeningExamination);
		screeningExaminationForms.add(mUzimaBreastScreening);
		screeningExaminationForms.add(mUzimaCervicalScreening);
		screeningExaminationForms.add(mUzimaCervicalCancerScreeningFollowup);
		screeningExaminationForms.add(OncologyCervicalScreeningFollowUp);
		
		screeningCervicalForms.add(oncologyCervicalScreeningExamination);
		screeningCervicalForms.add(mUzimaBreastScreening);
		screeningCervicalForms.add(mUzimaCervicalScreening);
		screeningCervicalForms.add(mUzimaCervicalCancerScreeningFollowup);
		screeningCervicalForms.add(OncologyCervicalScreeningFollowUp);
		
		screeningType = Context.getConceptService().getConceptByUuid("7e4e6554-d6c5-4ca3-b371-49806a754992");
		HPV = Context.getConceptService().getConceptByUuid("f7c2d59d-2043-42ce-b04d-08564d54b0c7");
		
		oncologyScreeningLabResultsForm = Context.getFormService().getFormByUuid("d7e4f3e6-2462-427d-83df-97d8488a53aa");
		muzimaOncologyScreeningLabResults = Context.getFormService().getFormByUuid("3a0e1a09-c88a-4412-99c6-cdbd7add50fd");
		
		oncologyScreeningLabResultsForms.add(oncologyScreeningLabResultsForm);
		oncologyScreeningLabResultsForms.add(muzimaOncologyScreeningLabResults);
		/* oncologyScreeningLabResultsForms.add(mUzimaCervicalCancerScreeningFollowup);
		 oncologyScreeningLabResultsForms.add(OncologyCervicalScreeningFollowUp);
		 oncologyScreeningLabResultsForms.add(oncologyCervicalScreeningExamination);
		 oncologyScreeningLabResultsForms.add(mUzimaCervicalScreening);*/
		
		testResult = Context.getConceptService().getConceptByUuid("bfb3eb1e-db98-4846-9915-0168511c6298");
		HPVpositive = Context.getConceptService().getConceptByUuid("1b4a5f67-6106-4a4d-a389-2f430be543e4");
		HPVNegative = Context.getConceptService().getConceptByUuid("64c23192-54e4-4750-9155-2ed0b736a0db");
		testResults.add(HPVpositive);
		testResults.add(HPVNegative);
		positiveTestResults.add(HPVpositive);
		
		cervicalCancerScreeningFollowupAndExaminationForms.add(mUzimaCervicalCancerScreeningFollowup);
		cervicalCancerScreeningFollowupAndExaminationForms.add(OncologyCervicalScreeningFollowUp);
		cervicalCancerScreeningFollowupAndExaminationForms.add(oncologyCervicalScreeningExamination);
		cervicalCancerScreeningFollowupAndExaminationForms.add(mUzimaCervicalScreening);
		
		typeOfVIAPerformed = Context.getConceptService().getConceptByUuid("820b0e37-5d3e-46c6-9462-a8e7adaff954");
		VIATriage = Context.getConceptService().getConceptByUuid("69a0ca97-2fee-4c4a-9d84-4f2c25f70c93");
		VIATriageInList.add(VIATriage);
		
		VIAResults = Context.getConceptService().getConceptByUuid("a37a937a-a2a6-4c22-975f-986fb3599ea3");
		VIAAndEligibleForThermalAblation = Context.getConceptService().getConceptByUuid(
		    "3fe69559-cc82-48cb-926e-5d925aca088b");
		VIAAndEligibleForLEEP = Context.getConceptService().getConceptByUuid("402f3951-420e-4c09-9a9a-955bc0cff140");
		
		VIAAndEligibleResults.add(VIAAndEligibleForThermalAblation);
		VIAAndEligibleResults.add(VIAAndEligibleForLEEP);
		
		VIANegative = Context.getConceptService().getConceptByUuid("a7b08a37-0380-49dd-8f12-c2c2c76c8b13");
		VIANegativeInList.add(VIANegative);
		
		VIAScreen = Context.getConceptService().getConceptByUuid("690d8a13-a1ac-4fd7-97a4-f3964b97f049");
		VIAScreenInList.add(VIAScreen);
		
		typeOfTreatmentPerformed = Context.getConceptService().getConceptByUuid("f7719cd4-e591-4a39-8bfe-9dd93d3cab89");
		thermalAblation = Context.getConceptService().getConceptByUuid("ef4d5bd3-2c4e-4ac2-9b91-9d4ba12b44f8");
		cryotherapy = Context.getConceptService().getConceptByUuid("e48bc8db-8f6e-4556-9083-0a000a136e95");
		thermalAblationAndCryotherapyList.add(thermalAblation);
		thermalAblationAndCryotherapyList.add(cryotherapy);
		
		reasonsForReferral = Context.getConceptService().getConceptByUuid("1aa373f4-4db5-4b01-bce0-c10a636bb931");
		reasonsForReferralIn = Context.getConceptService().getConceptByUuid("cc227602-a240-43fb-926d-b6ad3f42edab");
		loopElectrosurgicalExcisionProcedure = Context.getConceptService().getConceptByUuid(
		    "55040927-bae5-410c-80da-c79f7574167f");
		loopElectrosurgicalExcisionProcedureInList.add(loopElectrosurgicalExcisionProcedure);
		
		LEEPAndColposcopy.add(loopElectrosurgicalExcisionProcedure); // Colposcopy not available in Reason fo referral
		
		suspectedCancer = Context.getConceptService().getConceptByUuid("55040927-bae5-410c-80da-c79f7574167f");
		suspectedCancerInList.add(suspectedCancer);
		
		biopsyperformed = Context.getConceptService().getConceptByUuid("5563e827-9a1e-40eb-b05b-81d55981ce6d");
		yes = Context.getConceptService().getConceptByUuid("3cd6f600-26fe-102b-80cb-0017a47871b2");
		yesInList.add(yes);
		/*

		        oncologyCervicalCancerScreeningTransferIn=Context.getFormService().getFormByUuid("f939311f-53ac-4587-9a9a-48d41ea1b38b");
		        oncologyCervicalCancerScreeningTransferInList.add(oncologyCervicalCancerScreeningTransferIn);
		*/
		
		mUzimaBreastCancerScreening = Context.getFormService().getForm("mUzima Breast cancer screening");
		
		breastCancerScreeningForms.add(mUzimaBreastCancerScreening);
		breastCancerScreeningForms.add(oncologyBreastScreeningExamination);
		
		reasonForBreastExam = Context.getConceptService().getConceptByUuid("0483b8fa-b6d2-4551-ab4c-b141399897d7");
		breastSymptoms = Context.getConceptService().getConceptByUuid("b761e2c9-3d07-4a03-b274-c38282cc723c");
		screening = Context.getConceptService().getConceptByUuid("d9e56001-1c4e-479b-95f1-878e18d7ece8");
		breastExamination = Context.getConceptService().getConceptByUuid("f9576ba2-e96f-4b48-a178-42e72e6382ca");
		ABNORMAL = Context.getConceptService().getConceptByUuid("3cd75230-26fe-102b-80cb-0017a47871b2");
		nextStep = Context.getConceptService().getConceptByUuid("69b9671b-d8b1-461b-bb7d-adb151775a57");
		referredTo = Context.getConceptService().getConceptByUuid("25782f2c-074f-4834-b7d2-4668cd645a57");
		furtherManagement = Context.getConceptService().getConceptByUuid("de3a4342-b07e-48f8-ab80-202aab697756");
		medicalImaging = Context.getConceptService().getConceptByUuid("7f779262-de04-425b-97f7-9e5cc834eb55");
		BIOPSY = Context.getConceptService().getConceptByUuid("db64df50-1db1-4f80-abe5-b0307d7d4f9e");
		OTHERNONCODED = Context.getConceptService().getConceptByUuid("3cee7fb4-26fe-102b-80cb-0017a47871b2");
		breastUltrasound = Context.getConceptService().getConceptByUuid("58eacedf-cb05-4a8f-a98d-cc5717348d74");
		YES = Context.getConceptService().getConceptByUuid("3cd6f600-26fe-102b-80cb-0017a47871b2");
		solidMass = Context.getConceptService().getConceptByUuid("9ca1695a-f070-462e-81ce-d9778ee749b6");
		intermediate = Context.getConceptService().getConceptByUuid("636fc0a4-b82e-4a18-a97a-2cd43af8b852");
		highSuspiciousForMalignancy = Context.getConceptService().getConceptByUuid("f133b708-d7cf-47b3-81aa-17e618d47b2d");
		proceduresDone = Context.getConceptService().getConceptByUuid("e44f17a3-28bd-4fe1-a4e1-1bb94779a1fd");
		PAPANICOLAOUSMEAR = Context.getConceptService().getConceptByUuid("3cd4de2e-26fe-102b-80cb-0017a47871b2");
		NORMAL = Context.getConceptService().getConceptByUuid("3cd750a0-26fe-102b-80cb-0017a47871b2");
		
		muzimaOncologyScreeningDiagnosis = Context.getFormService().getForm("muzima oncology screening Diagnosis");
		oncologyScreeningDiagnosis = Context.getFormService().getForm("Oncology Screening Diagnosis");
		
		diagnosisScreeningForms.add(muzimaOncologyScreeningDiagnosis);
		diagnosisScreeningForms.add(oncologyScreeningDiagnosis);
		CONFIRMEDDIAGNOSIS = Context.getConceptService().getConceptByUuid("3d762b82-f951-4d13-b147-6aaba63b25d1");
		confirmedCancerDiagnosis = Context.getConceptService().getConceptByUuid("3dc2eb50-6981-43d3-b907-e3dd8b5ed620");
		cervicalCancer = Context.getConceptService().getConceptByUuid("36052b70-ba49-466f-a4eb-bc99581be7a2");
		breastCancer = Context.getConceptService().getConceptByUuid("e1bd83f4-e9fa-4564-b8aa-74a9b199aca8");
		
		/*  oncologyBreastCancerScreeningTransferIn = Context.getFormService().getForm("Oncology Breast Cancer Screening transfer in");
		  oncologyBreastCancerScreeningTransferInList.add(oncologyBreastCancerScreeningTransferIn);
		*/
		hivStatus = Context.getConceptService().getConceptByUuid("aec6ad18-f4dd-4cfa-b68d-3d7bb6ea908e");
		
		positive = Context.getConceptService().getConceptByUuid("3cd3a7a2-26fe-102b-80cb-0017a47871b2");
	}
}
