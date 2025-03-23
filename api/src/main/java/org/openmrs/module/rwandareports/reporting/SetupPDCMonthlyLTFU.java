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
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.common.SortCriteria.SortDirection;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.rowperpatientreports.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.rowperpatientreports.patientdata.definition.*;
import org.openmrs.module.rwandareports.customcalculator.PDCAlerts;
import org.openmrs.module.rwandareports.filter.LastEncounterFilter;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.RowPerPatientColumns;

public class SetupPDCMonthlyLTFU extends SingleSetupReport {
	
	//properties retrieved from global variables
	private Program PDCProgram;
	
	List<EncounterType> pdcEncounters;
	
	private ArrayList<Form> intakeForm = new ArrayList<Form>();
	
	private EncounterType pdcEncType;
	
	private List<Form> referralAndVisitForms = new ArrayList<Form>();
	
	private Form referralForm;
	
	private Form visitForm;
	
	private Concept returnVisitDate;
	
	@Override
	public String getReportName() {
		return "PDC Monthly LTFU";
	}
	
	public void setup() throws Exception {
		log.info("Setting up report: " + getReportName());
		setupProperties();
		
		ReportDefinition rd = createReportDefinition();
		
		ReportDesign design = Helper.createRowPerPatientXlsOverviewReportDesign(rd, "PDC_LTFU_Sheet.xls",
		    "PDCLostToFollowUpSheet.xls_", null);
		
		Properties props = new Properties();
		//props.put("repeatingSections", "sheet:1,row:9,dataset:undreOneYearDataSetDefinition|sheet:2,row:9,dataset:undreTwoYearsDataSetDefinition|sheet:3,row:9,dataset:aboveTwoYearsDataSetDefinition");
		
		props.put(
		    "repeatingSections",
		    "sheet:1,row:10,dataset:undreOneYearDataSetDefinition|sheet:1,row:15,dataset:undreTwoYearsDataSetDefinition|sheet:1,row:20,dataset:aboveTwoYearsDataSetDefinition");
		
		props.put("sortWeight", "5000");
		design.setProperties(props);
		
		Helper.saveReportDesign(design);
	}
	
	private ReportDefinition createReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName(getReportName());
		
		reportDefinition.addParameter(new Parameter("location", "Health Center", Location.class));
		reportDefinition.setBaseCohortDefinition(Cohorts.createParameterizedLocationCohort("At Location"),
		    ParameterizableUtil.createParameterMappings("location=${location}"));
		reportDefinition.addParameter(new Parameter("endDate", "Monday", Date.class));
		createDataSetDefinition(reportDefinition);
		
		Helper.saveReportDefinition(reportDefinition);
		return reportDefinition;
	}
	
	private void createDataSetDefinition(ReportDefinition reportDefinition) {
		
		AgeCohortDefinition OneYearAndBelow = Cohorts.createUnderAgeCohort("OneYearAndBelow", 1, DurationUnit.YEARS);
		OneYearAndBelow.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		
		AgeCohortDefinition OneYearAndAbove = Cohorts.createAboveAgeCohort("OneYearAndAbove", 1, DurationUnit.YEARS);
		OneYearAndAbove.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		
		AgeCohortDefinition twoYearsAndBelow = Cohorts.createUnderAgeCohort("twoYearsAndBelow", 2, DurationUnit.YEARS);
		twoYearsAndBelow.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		
		AgeCohortDefinition twoYearsAndAbove = Cohorts.createAboveAgeCohort("twoYearsAndAbove", 2, DurationUnit.YEARS);
		twoYearsAndAbove.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		
		CompositionCohortDefinition undreOneYear = new CompositionCohortDefinition();
		undreOneYear.setName("undreOneYear");
		undreOneYear.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		undreOneYear.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(OneYearAndBelow, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		undreOneYear.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(OneYearAndAbove, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		undreOneYear.setCompositionString("1 and (not 2)");
		
		CompositionCohortDefinition undreTwoYearsAndAboveOneYear = new CompositionCohortDefinition();
		undreTwoYearsAndAboveOneYear.setName("undreTwoYearsAndAboveOneYear");
		undreTwoYearsAndAboveOneYear.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		undreTwoYearsAndAboveOneYear.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(twoYearsAndBelow, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		undreTwoYearsAndAboveOneYear.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(undreOneYear, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		undreTwoYearsAndAboveOneYear.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(twoYearsAndAbove, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		undreTwoYearsAndAboveOneYear.setCompositionString("1 and (not 2) and  (not 3)");
		
		// Create Under One Year DataSet definition 
		RowPerPatientDataSetDefinition undreOneYearDataSetDefinition = addNameAndParameters("undreOneYearDataSetDefinition");
		undreOneYearDataSetDefinition.addFilter(Cohorts.createPatientsLateForPDCVisit(pdcEncType, 180),
		    ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
		//undreOneYearDataSetDefinition.addFilter(Cohorts.createUnderAgeCohort("undreOneYear",364,DurationUnit.DAYS), ParameterizableUtil.createParameterMappings("effectiveDate=${endDate}"));
		undreOneYearDataSetDefinition.addFilter(undreOneYear,
		    ParameterizableUtil.createParameterMappings("effectiveDate=${endDate}"));
		
		addColumns(undreOneYearDataSetDefinition);
		
		// Create Under two Years DataSet definition 
		RowPerPatientDataSetDefinition undreTwoYearsDataSetDefinition = addNameAndParameters("undreTwoYearsDataSetDefinition");
		undreTwoYearsDataSetDefinition.addFilter(Cohorts.createPatientsLateForPDCVisit(pdcEncType, 270),
		    ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
		//undreTwoYearsDataSetDefinition.addFilter(Cohorts.createUnderAgeCohort("undreTwoYears",1,DurationUnit.YEARS), ParameterizableUtil.createParameterMappings("effectiveDate=${endDate}"));
		undreTwoYearsDataSetDefinition.addFilter(undreTwoYearsAndAboveOneYear,
		    ParameterizableUtil.createParameterMappings("effectiveDate=${endDate}"));
		
		addColumns(undreTwoYearsDataSetDefinition);
		
		// Create Above Two Years DataSet definition 
		RowPerPatientDataSetDefinition aboveTwoYearsDataSetDefinition = addNameAndParameters("aboveTwoYearsDataSetDefinition");
		aboveTwoYearsDataSetDefinition.addFilter(Cohorts.createPatientsLateForPDCVisit(pdcEncType, 540),
		    ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
		//aboveTwoYearsDataSetDefinition.addFilter(Cohorts.createAboveAgeCohort("aboveTwoYears",2,DurationUnit.YEARS), ParameterizableUtil.createParameterMappings("effectiveDate=${endDate}"));
		aboveTwoYearsDataSetDefinition.addFilter(twoYearsAndAbove,
		    ParameterizableUtil.createParameterMappings("effectiveDate=${endDate}"));
		
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("location", "${location}");
		mappings.put("endDate", "${endDate}");
		
		reportDefinition.addDataSetDefinition("undreOneYearDataSetDefinition", undreOneYearDataSetDefinition, mappings);
		reportDefinition.addDataSetDefinition("undreTwoYearsDataSetDefinition", undreTwoYearsDataSetDefinition, mappings);
		reportDefinition.addDataSetDefinition("aboveTwoYearsDataSetDefinition", aboveTwoYearsDataSetDefinition, mappings);
	}
	
	/**
	 * @param datasetName
	 * @return
	 */
	public RowPerPatientDataSetDefinition addNameAndParameters(String datasetName) {
		RowPerPatientDataSetDefinition dataSetDefinition = new RowPerPatientDataSetDefinition();
		dataSetDefinition.setName(datasetName);
		
		SortCriteria sortCriteria = new SortCriteria();
		sortCriteria.addSortElement("LastVisits Date", SortDirection.DESC);
		dataSetDefinition.setSortCriteria(sortCriteria);
		
		dataSetDefinition.addParameter(new Parameter("location", "Location", Location.class));
		dataSetDefinition.addParameter(new Parameter("endDate", "Monday", Date.class));
		
		//Add filters
		dataSetDefinition.addFilter(
		    Cohorts.createInProgramParameterizableByDate("Patients in " + PDCProgram.getName(), PDCProgram),
		    ParameterizableUtil.createParameterMappings("onDate=${endDate}"));
		return dataSetDefinition;
	}
	
	/**
	 * @param dataSetDefinition
	 * @return
	 */
	public RowPerPatientDataSetDefinition addColumns(RowPerPatientDataSetDefinition dataSetDefinition) {
		//Add Columns
		dataSetDefinition.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getIMBId("Id"), new HashMap<String, Object>());
		
		PatientAgeInMonths ageinMonths = RowPerPatientColumns.getAgeInMonths("age");
		dataSetDefinition.addColumn(ageinMonths, new HashMap<String, Object>());
		
		PatientProperty ageinYrs = RowPerPatientColumns.getAge("ageinYrs");
		dataSetDefinition.addColumn(ageinYrs, new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getGender("Sex"), new HashMap<String, Object>());
		
		MostRecentObservation intervalgrowth = RowPerPatientColumns
		        .getMostRecentIntervalGrowth("intervalgrowth", "@ddMMMyy");
		dataSetDefinition.addColumn(intervalgrowth, new HashMap<String, Object>());
		
		MostRecentObservation intervalgrowthcoded = RowPerPatientColumns.getMostRecentCodedIntGrowth("inadequate",
		    "@ddMMMyy");
		dataSetDefinition.addColumn(intervalgrowthcoded, new HashMap<String, Object>());
		
		MostRecentObservation wtAgezscore = RowPerPatientColumns.getMostRecentWtAgezscore("wtagezcore", "@ddMMMyy");
		dataSetDefinition.addColumn(wtAgezscore, new HashMap<String, Object>());
		
		MostRecentObservation wtHeightzcore = RowPerPatientColumns.getMostRecentWtHeightzscore("wthtzscore", "@ddMMMyy");
		dataSetDefinition.addColumn(wtHeightzcore, new HashMap<String, Object>());
		
		MostRecentObservation temperaturesign = RowPerPatientColumns.getMostRecentTemperature("temperature", "@ddMMMMyy");
		dataSetDefinition.addColumn(temperaturesign, new HashMap<String, Object>());
		
		MostRecentObservation respitatorysign = RowPerPatientColumns.getMostRecentRespiratoryRate("respitatorysign",
		    "@ddMMMMyy");
		dataSetDefinition.addColumn(respitatorysign, new HashMap<String, Object>());
		
		MostRecentObservation asqScore = RowPerPatientColumns.getMostRecentASQ("asqscore", "@ddMMMMyy");
		dataSetDefinition.addColumn(asqScore, new HashMap<String, Object>());
		
		MostRecentObservation swAssesment = RowPerPatientColumns.getMostRecentSWA("swa", "@ddMMMMyy");
		dataSetDefinition.addColumn(swAssesment, new HashMap<String, Object>());
		
		MostRecentObservation ecdeducation = RowPerPatientColumns.getMostRecentECDEDUC("ecdeducation", "@ddMMMMyy");
		dataSetDefinition.addColumn(ecdeducation, new HashMap<String, Object>());
		
		MostRecentObservation discharged = RowPerPatientColumns.getMostRecentCondition("dischargedmet", "@ddMMMMyy");
		dataSetDefinition.addColumn(discharged, new HashMap<String, Object>());
		
		ObservationInMostRecentEncounterOfType nextVisit = RowPerPatientColumns.getReturnVisitInMostRecentEncounterOfType(
		    "nextVisit", pdcEncType);
		dataSetDefinition.addColumn(nextVisit, new HashMap<String, Object>());
		
		RecentEncounter lastpdcIntake = RowPerPatientColumns.getRecentEncounter("lastintake", intakeForm, pdcEncounters,
		    "dd-MMM-yyyy", null);
		dataSetDefinition.addColumn(lastpdcIntake, new HashMap<String, Object>());
		
		RecentEncounterType lastEncounterType = RowPerPatientColumns.getRecentEncounterType("LastVisit", pdcEncounters,
		    "dd-MMM-yyyy", new LastEncounterFilter());
		dataSetDefinition.addColumn(lastEncounterType, new HashMap<String, Object>());
		
		RecentEncounterType lastEncounterTypes = RowPerPatientColumns.getRecentEncounterType("LastVisits", pdcEncounters,
		    null, new LastEncounterFilter());
		dataSetDefinition.addColumn(lastEncounterTypes, new HashMap<String, Object>());
		
		/*DateDiff lateVisitInMonth = RowPerPatientColumns.getDifferenceSinceLastEncounter(
				"MonthsSinceLastVisit", pdcEncounters, DateDiff.DateDiffType.MONTHS);
		*/
		
		DateDiff monthsSinceLastVisit = RowPerPatientColumns.getDifferenceSinceLastEncounter("MonthsSinceLastVisit",
		    pdcEncounters, DateDiff.DateDiffType.MONTHS);
		monthsSinceLastVisit.addParameter(new Parameter("endDate", "endDate", Date.class));
		dataSetDefinition.addColumn(monthsSinceLastVisit, ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
		
		CustomCalculationBasedOnMultiplePatientDataDefinitions alert = new CustomCalculationBasedOnMultiplePatientDataDefinitions();
		alert.setName("alert");
		alert.addPatientDataToBeEvaluated(intervalgrowth, new HashMap<String, Object>());
		alert.addPatientDataToBeEvaluated(intervalgrowthcoded, new HashMap<String, Object>());
		alert.addPatientDataToBeEvaluated(wtAgezscore, new HashMap<String, Object>());
		alert.addPatientDataToBeEvaluated(wtHeightzcore, new HashMap<String, Object>());
		alert.addPatientDataToBeEvaluated(temperaturesign, new HashMap<String, Object>());
		alert.addPatientDataToBeEvaluated(respitatorysign, new HashMap<String, Object>());
		alert.addPatientDataToBeEvaluated(asqScore, new HashMap<String, Object>());
		alert.addPatientDataToBeEvaluated(ageinMonths, new HashMap<String, Object>());
		alert.addPatientDataToBeEvaluated(swAssesment, new HashMap<String, Object>());
		alert.addPatientDataToBeEvaluated(ecdeducation, new HashMap<String, Object>());
		alert.addPatientDataToBeEvaluated(discharged, new HashMap<String, Object>());
		alert.addPatientDataToBeEvaluated(nextVisit, new HashMap<String, Object>());
		alert.addPatientDataToBeEvaluated(lastpdcIntake, new HashMap<String, Object>());
		alert.setCalculator(new PDCAlerts());
		dataSetDefinition.addColumn(alert, new HashMap<String, Object>());
		
		return dataSetDefinition;
	}
	
	private void setupProperties() {
		PDCProgram = gp.getProgram(GlobalPropertiesManagement.PDC_PROGRAM);
		pdcEncounters = gp.getEncounterTypeList(GlobalPropertiesManagement.PDC_VISIT);
		pdcEncType = gp.getEncounterType(GlobalPropertiesManagement.PDC_VISIT);
		
		intakeForm.add(gp.getForm(GlobalPropertiesManagement.PDC_INTAKE_FORM));
		referralForm = gp.getForm(GlobalPropertiesManagement.PDC_REFERRAL_FORM);
		visitForm = gp.getForm(GlobalPropertiesManagement.PDC_VISIT_FORM);
		
		referralAndVisitForms.add(referralForm);
		referralAndVisitForms.add(visitForm);
		
		returnVisitDate = gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE);
		
	}
	
}
