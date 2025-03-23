package org.openmrs.module.rwandareports.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.common.SortCriteria.SortDirection;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.rowperpatientreports.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.rwandareports.dataset.ConsecutiveCombinedDataSetDefinition;
import org.openmrs.module.rwandareports.dataset.WeekViewDataSetDefinition;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.RowPerPatientColumns;

public class SetupOncologyOutpatientExpectedPatientList extends SingleSetupReport {
	
	//properties retrieved from global variables
	private Program oncologyProgram;
	
	private ProgramWorkflow diagnosis;
	
	private Concept scheduledVisit;
	
	private Concept biopsyResultVisit;
	
	private Concept pathologyResultVisit;
	
	private Concept specialVisit;
	
	private Concept telephone;
	
	private Concept telephone2;
	
	private Concept confirmedDiagnosis;
	
	private Form OncologyScheduleAppointmentForm;
	
	private Form outpatientClinicVisitsForm;
	
	private List<Concept> visitDates = new ArrayList<Concept>();
	
	private List<Form> visitForms = new ArrayList<Form>();
	
	@Override
	public String getReportName() {
		return "ONC-Oncology Expected Patient List - Outpatient Ward";
	}
	
	public void setup() throws Exception {
		log.info("Setting up report: " + getReportName());
		setupProperties();
		
		ReportDefinition rd = createReportDefinition();
		
		ReportDesign design = Helper.createRowPerPatientXlsOverviewReportDesign(rd,
		    "OncologyOutpatientExpectedPatientList.xls", "OncologyOutpatientExpectedPatientList.xls_", null);
		
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,row:7,dataset:dataset");
		props.put("sortWeight", "5000");
		design.setProperties(props);
		
		Helper.saveReportDesign(design);
		
	}
	
	private ReportDefinition createReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName(getReportName());
		
		reportDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		reportDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		reportDefinition.setBaseCohortDefinition(
		    Cohorts.createInProgramParameterizableByStartEndDate("Oncology", oncologyProgram),
		    ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		//		reportDefinition.addParameter(new Parameter("endDate", "Week of (select Monday)", Date.class));
		createDataSetDefinition(reportDefinition);
		
		Helper.saveReportDefinition(reportDefinition);
		
		return reportDefinition;
	}
	
	private void createDataSetDefinition(ReportDefinition reportDefinition) {
		// Create new dataset definition 
		RowPerPatientDataSetDefinition dataSetDefinition = new RowPerPatientDataSetDefinition();
		dataSetDefinition.setName("Chemotherapy Patient List");
		
		dataSetDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dataSetDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		dataSetDefinition.addFilter(Cohorts.getMondayToSundayPatientReturnVisit(visitForms, visitDates),
		    ParameterizableUtil.createParameterMappings("end=${endDate},start=${startDate}"));
		
		SortCriteria sortCriteria = new SortCriteria();
		sortCriteria.addSortElement("nextRDVDate", SortDirection.ASC);
		dataSetDefinition.setSortCriteria(sortCriteria);
		
		//Add Columns
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecent("nextRDVDateScheduledVisit", scheduledVisit, "yyyy/MM/dd"),
		    new HashMap<String, Object>());
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecent("nextRDVDatePathologyResultVisit", pathologyResultVisit, "yyyy/MM/dd"),
		    new HashMap<String, Object>());
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecent("nextRDVDateSpecialVisit", specialVisit, "yyyy/MM/dd"),
		    new HashMap<String, Object>());
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecent("nextRDVDateBiopsyResultVisit", biopsyResultVisit, "yyyy/MM/dd"),
		    new HashMap<String, Object>());
		
		addCommonColumns(dataSetDefinition);
		
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("startDate", "${startDate}");
		mappings.put("endDate", "${endDate}");
		
		reportDefinition.addDataSetDefinition("dataset", dataSetDefinition, mappings);
		//
		//	WeekViewDataSetDefinition weekDataSetDefinition = new WeekViewDataSetDefinition();
		//	weekDataSetDefinition.setName("weekDataSetDefinition");
		//	weekDataSetDefinition.setBaseDefinition(baseSetDefinition);
		//	weekDataSetDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
		//
		//	Map<String, Object> baseMappings = new HashMap<String, Object>();
		//	baseMappings.put("startDate", "${endDate}");
		//
		//	reportDefinition.addDataSetDefinition("dataset2", weekDataSetDefinition, baseMappings);
		//
	}
	
	private void setupProperties() {
		
		oncologyProgram = gp.getProgram(GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		diagnosis = gp.getProgramWorkflow(GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
		    GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		telephone = gp.getConcept(GlobalPropertiesManagement.TELEPHONE_NUMBER_CONCEPT);
		
		telephone2 = gp.getConcept(GlobalPropertiesManagement.SECONDARY_TELEPHONE_NUMBER_CONCEPT);
		
		OncologyScheduleAppointmentForm = gp.getForm(GlobalPropertiesManagement.ONCOLOGY_SCHEDULE_APPOINTMENT_FORM);
		
		outpatientClinicVisitsForm = gp.getForm(GlobalPropertiesManagement.OUTPATIENT_CLINIC_VISITS_FORM);
		
		scheduledVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_SCHEDULED_OUTPATIENT_VISIT);
		
		pathologyResultVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_PATHOLOGY_RESULT_VISIT);
		
		specialVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_SPECIAL_VISIT);
		
		biopsyResultVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_BIOPSY_RESULT_VISIT);
		
		//		visitForms.add(OncologyScheduleAppointmentForm);
		//		visitForms.add(outpatientClinicVisitsForm);
		
		visitDates.add(scheduledVisit);
		visitDates.add(biopsyResultVisit);
		visitDates.add(specialVisit);
		visitDates.add(pathologyResultVisit);
		
		confirmedDiagnosis = gp.getConcept(GlobalPropertiesManagement.CONFIRMED_DIAGNOSIS_CONCEPT);
		
	}
	
	private void addCommonColumns(RowPerPatientDataSetDefinition dataSetDefinition) {
		
		//dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("nextRDV",ChemotherapyInpatientWardVisit,"dd/MMM/yyyy",null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getArchivingId("archivingId"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getDrugRegimenInformationParameterized("regimen", false, false),
		    ParameterizableUtil.createParameterMappings("asOfDate=${endDate},untilDate=${endDate+7d}"));
		
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getRegimenDateInformationParameterized("regimenDate", "dd/MMM/yyyy"),
		    ParameterizableUtil.createParameterMappings("asOfDate=${endDate},untilDate=${endDate+7d}"));
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getStateOfPatient("diagnosis", oncologyProgram, diagnosis, null),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("diagnosisNew", confirmedDiagnosis, null),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("telephone", telephone, null),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("telephone2", telephone2, null),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getPatientAddress("address", true, true, true, true),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getAccompRelationship("accompagnateur"),
		    new HashMap<String, Object>());
		
	}
	
}
