package org.openmrs.module.rwandareports.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.RelationshipType;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.rowperpatientreports.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.rowperpatientreports.patientdata.definition.CustomCalculationBasedOnMultiplePatientDataDefinitions;
import org.openmrs.module.rowperpatientreports.patientdata.definition.MostRecentObservation;
import org.openmrs.module.rowperpatientreports.patientdata.definition.MultiplePatientDataDefinitions;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ObservationInMostRecentEncounterOfType;
import org.openmrs.module.rowperpatientreports.patientdata.definition.PatientAddress;
import org.openmrs.module.rowperpatientreports.patientdata.definition.PatientProperty;
import org.openmrs.module.rwandareports.customcalculator.DaysLate;
import org.openmrs.module.rwandareports.filter.AccompagnateurDisplayFilter;
import org.openmrs.module.rwandareports.filter.DateFormatFilter;
import org.openmrs.module.rwandareports.filter.DrugDosageCurrentFilter;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.RowPerPatientColumns;

/**
 * Created by josua on 10/13/17.
 */
public class SetupCKDMissedvisitReport extends SingleSetupReport {
	
	protected final static Log log = LogFactory.getLog(SetupCKDMissedvisitReport.class);
	
	//Properties retrieved from global variables
	private Program CKDProgram;
	
	private EncounterType CKDflowsheet;
	
	private EncounterType HFHTNCKDENCOUNTER;
	
	private List<EncounterType> CKDencounterTypes = new ArrayList<EncounterType>();
	
	private Form CKDRDVForm;
	
	private Form CKDEnrollmentForm;
	
	private Form followUpForm;
	
	//    private Concept creatinine;
	
	RelationshipType HBCP;
	
	private List<Form> CKDForms = new ArrayList<Form>();
	
	public void setup() throws Exception {
		log.info("Setting up report: " + getReportName());
		setupProperties();
		
		ReportDefinition rd = createReportDefinition();
		ReportDesign design = Helper.createRowPerPatientXlsOverviewReportDesign(rd, "CKDLateVisit.xls", "XLSCKDLateVisit",
		    null);
		
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,row:10,dataset:dataSet");
		props.put("sortWeight", "5000");
		design.setProperties(props);
		Helper.saveReportDesign(design);
	}
	
	@Override
	public String getReportName() {
		return "NCD-CKD Late Visit";
	}
	
	private ReportDefinition createReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName(getReportName());
		reportDefinition.addParameter(new Parameter("location", "Location", Location.class));
		reportDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		reportDefinition.setBaseCohortDefinition(Cohorts.createParameterizedLocationCohort("At Location"),
		    ParameterizableUtil.createParameterMappings("location=${location}"));
		
		createDataSetDefinition(reportDefinition);
		Helper.saveReportDefinition(reportDefinition);
		
		return reportDefinition;
	}
	
	private void createDataSetDefinition(ReportDefinition reportDefinition) {
		
		DateFormatFilter dateFilter = new DateFormatFilter();
		dateFilter.setFinalDateFormat("yyyy/MM/dd");
		
		RowPerPatientDataSetDefinition dataSetDefinition1 = new RowPerPatientDataSetDefinition();
		dataSetDefinition1.setName("LateVisit");
		
		dataSetDefinition1.addFilter(
		    Cohorts.createInProgramParameterizableByDate("Patients in " + CKDProgram.getName(), CKDProgram),
		    ParameterizableUtil.createParameterMappings("onDate=${endDate}"));
		
		dataSetDefinition1.addFilter(
		    Cohorts.createPatientsLateForVisitINDifferentEncounterTypes(CKDForms, CKDencounterTypes),
		    ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
		
		//==================================================================
		//                 Columns of report settings
		//==================================================================
		
		MultiplePatientDataDefinitions imbType = RowPerPatientColumns.getAnyId("PC Id");
		dataSetDefinition1.addColumn(imbType, new HashMap<String, Object>());
		
		PatientProperty givenName = RowPerPatientColumns.getFirstNameColumn("familyName");
		dataSetDefinition1.addColumn(givenName, new HashMap<String, Object>());
		
		PatientProperty familyName = RowPerPatientColumns.getFamilyNameColumn("givenName");
		dataSetDefinition1.addColumn(familyName, new HashMap<String, Object>());
		
		//        MostRecentObservation lastphonenumber = RowPerPatientColumns.getMostRecentPatientPhoneNumber("telephone", null);
		//        dataSetDefinition1.addColumn(lastphonenumber, new HashMap<String, Object>());
		
		PatientProperty gender = RowPerPatientColumns.getGender("Sex");
		dataSetDefinition1.addColumn(gender, new HashMap<String, Object>());
		
		PatientProperty age = RowPerPatientColumns.getAge("Age");
		dataSetDefinition1.addColumn(age, new HashMap<String, Object>());
		
		dataSetDefinition1.addColumn(RowPerPatientColumns.getNextVisitMostRecentEncounterOfTheTypes("nextVisit",
		    CKDencounterTypes, new ObservationInMostRecentEncounterOfType(), null), new HashMap<String, Object>());
		
		CustomCalculationBasedOnMultiplePatientDataDefinitions numberofdaysLate = new CustomCalculationBasedOnMultiplePatientDataDefinitions();
		numberofdaysLate.addPatientDataToBeEvaluated(RowPerPatientColumns.getNextVisitMostRecentEncounterOfTheTypes(
		    "nextVisit", CKDencounterTypes, new ObservationInMostRecentEncounterOfType(), dateFilter),
		    new HashMap<String, Object>());
		numberofdaysLate.setName("numberofdaysLate");
		numberofdaysLate.setCalculator(new DaysLate());
		numberofdaysLate.addParameter(new Parameter("endDate", "endDate", Date.class));
		dataSetDefinition1.addColumn(numberofdaysLate, ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
		
		MostRecentObservation creatinine = RowPerPatientColumns.getMostRecentCreatinine("creatinine", "@ddMMMyy");
		dataSetDefinition1.addColumn(creatinine, new HashMap<String, Object>());
		
		//        MostRecentObservation diastolic = RowPerPatientColumns.getMostRecentDiastolicPB("diastolic", "@ddMMMyy");
		//        dataSetDefinition1.addColumn(diastolic, new HashMap<String, Object>());
		
		dataSetDefinition1.addColumn(RowPerPatientColumns.getPatientCurrentlyActiveOnDrugOrder("Regimen", null),
		    new HashMap<String, Object>());
		
		PatientAddress address1 = RowPerPatientColumns.getPatientAddress("Address", true, true, true, true);
		dataSetDefinition1.addColumn(address1, new HashMap<String, Object>());
		
		dataSetDefinition1.addColumn(
		    RowPerPatientColumns.getAccompRelationship("AccompName", new AccompagnateurDisplayFilter()),
		    new HashMap<String, Object>());
		
		dataSetDefinition1.addColumn(
		    RowPerPatientColumns.getPatientRelationship("HBCP", HBCP.getRelationshipTypeId(), "A", null),
		    new HashMap<String, Object>());
		
		dataSetDefinition1.addParameter(new Parameter("location", "Location", Location.class));
		dataSetDefinition1.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("location", "${location}");
		mappings.put("endDate", "${endDate}");
		
		reportDefinition.addDataSetDefinition("dataSet", dataSetDefinition1, mappings);
		
	}
	
	private void setupProperties() {
		
		CKDProgram = gp.getProgram(GlobalPropertiesManagement.CKD_PROGRAM);
		
		CKDflowsheet = gp.getEncounterType(GlobalPropertiesManagement.CKD_ENCOUNTER_TYPE);
		HFHTNCKDENCOUNTER = gp.getEncounterType(GlobalPropertiesManagement.HF_HTN_CKD_ENCOUNTER_TYPE);
		
		CKDencounterTypes.add(CKDflowsheet);
		CKDencounterTypes.add(HFHTNCKDENCOUNTER);
		
		CKDRDVForm = gp.getForm(GlobalPropertiesManagement.CKD_RDV_FORM);
		
		CKDEnrollmentForm = gp.getForm(GlobalPropertiesManagement.CKD_ENROLLMENT_FORM);
		
		CKDForms.add(CKDEnrollmentForm);
		CKDForms.add(CKDRDVForm);
		
		// CKDForms =gp.getFormList(GlobalPropertiesManagement.CKD_DDB_FLOW_VISIT);
		
		HBCP = gp.getRelationshipType(GlobalPropertiesManagement.HBCP_RELATIONSHIP);
	}
}
