package org.openmrs.module.rwandareports.reporting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
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
import org.openmrs.module.rowperpatientreports.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.rwandareports.dataset.ConsecutiveCombinedDataSetDefinition;
import org.openmrs.module.rwandareports.dataset.WeekViewDataSetDefinition;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.RowPerPatientColumns;

import java.util.*;

public class SetupChemotherapyPediatricExpectedPatientList implements SetupReport {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	//properties retrieved from global variables
	private Program oncologyProgram;
	
	private ProgramWorkflow diagnosis;
	
	private Concept telephone;
	
	private Concept telephone2;
	
	private Concept confirmedDiagnosis;
	
	private Form OncologyScheduleAppointmentForm;
	
	private Concept ChemotherapyInpatientWardVisit;
	
	private Concept pediatricChemotherapy;
	
	private Concept pediatricNonChemotherapy;
	
	private List<Concept> pediatricChemoDates = new ArrayList<Concept>();
	
	private List<Form> visitForms = new ArrayList<Form>();
	
	//private Concept weight;
	//private Concept height;
	
	/**
	 * @return
	 */
	@Override
	public String getReportName() {
		return null;
	}
	
	public void setup() throws Exception {
		
		setupProperties();
		
		ReportDefinition rd = createReportDefinition();
		
		ReportDesign design = Helper.createRowPerPatientXlsOverviewReportDesign(rd,
		    "ChemotherapyPediatricExpectedPatientList.xls", "ChemotherapyPediatricExpectedPatientList.xls_", null);
		
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,row:7,dataset:dataset1");
		props.put("sortWeight", "5000");
		design.setProperties(props);
		
		Helper.saveReportDesign(design);
		
	}
	
	public void delete() {
		Helper.purgeReportDefinition("ONC-Oncology Expected Patient List - Pediatric Ward");
	}
	
	private ReportDefinition createReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("ONC-Oncology Expected Patient List - Pediatric Ward");
		reportDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		reportDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		reportDefinition.setBaseCohortDefinition(
		    Cohorts.createInProgramParameterizableByStartEndDate("Oncology", oncologyProgram),
		    ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		//reportDefinition.addParameter(new Parameter("endDate", "Week of (select Monday)", Date.class));
		
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
		
		//Add Filters
		dataSetDefinition.addFilter(Cohorts.getMondayToSundayPatientReturnVisit(visitForms, pediatricChemoDates),
		    ParameterizableUtil.createParameterMappings("end=${endDate},start=${endDate}"));
		
		SortCriteria sortCriteria = new SortCriteria();
		sortCriteria.addSortElement("nextRDV", SortDirection.ASC);
		dataSetDefinition.setSortCriteria(sortCriteria);
		
		//Add Columns
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("nextRDV", pediatricChemotherapy, "yyyy/MM/dd"),
		    new HashMap<String, Object>());
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecent("nextRDVDate", pediatricNonChemotherapy, "yyyy/MM/dd"),
		    new HashMap<String, Object>());
		
		addCommonColumns(dataSetDefinition);
		
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("endDate", "${endDate}");
		mappings.put("startDate", "${startDate}");
		
		reportDefinition.addDataSetDefinition("dataset1", dataSetDefinition, mappings);
		
		//	WeekViewDataSetDefinition weekDataSetDefinition = new WeekViewDataSetDefinition();
		//	weekDataSetDefinition.setName("weekDataSetDefinition");
		//	weekDataSetDefinition.setBaseDefinition(baseSetDefinition);
		//	weekDataSetDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
		//
		//	Map<String, Object> baseMappings = new HashMap<String, Object>();
		//	baseMappings.put("startDate", "${endDate}");
		//
		//	reportDefinition.addDataSetDefinition("dataset1d", weekDataSetDefinition, baseMappings);
		//
	}
	
	private void setupProperties() {
		GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
		oncologyProgram = gp.getProgram(GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		diagnosis = gp.getProgramWorkflow(GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
		    GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		telephone = gp.getConcept(GlobalPropertiesManagement.TELEPHONE_NUMBER_CONCEPT);
		
		telephone2 = gp.getConcept(GlobalPropertiesManagement.SECONDARY_TELEPHONE_NUMBER_CONCEPT);
		
		OncologyScheduleAppointmentForm = gp.getForm(GlobalPropertiesManagement.ONCOLOGY_SCHEDULE_APPOINTMENT_FORM);
		
		pediatricChemotherapy = Context.getConceptService().getConceptByUuid("5efb51db-4e71-497d-822c-91501ac167f6");
		pediatricNonChemotherapy = Context.getConceptService().getConceptByUuid("8c3b045a-aa94-4361-b2e9-8a80c26ccede");
		pediatricChemoDates.add(pediatricChemotherapy);
		pediatricChemoDates.add(pediatricNonChemotherapy);
		
		//		visitForms.add(OncologyScheduleAppointmentForm);
		
		confirmedDiagnosis = gp.getConcept(GlobalPropertiesManagement.CONFIRMED_DIAGNOSIS_CONCEPT);
		/*height=gp.getConcept(GlobalPropertiesManagement.HEIGHT_CONCEPT);
		weight=gp.getConcept(GlobalPropertiesManagement.WEIGHT_CONCEPT);*/
		
	}
	
	private void addCommonColumns(RowPerPatientDataSetDefinition dataSetDefinition) {
		
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecent("nextRDV", pediatricChemotherapy, "dd/MMM/yyyy", null),
		    new HashMap<String, Object>());
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecent("nextDate", pediatricNonChemotherapy, "dd/MMM/yyyy", null),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getArchivingId("archivingid"), new HashMap<String, Object>());
		
		//dataSetDefinition.addColumn(RowPerPatientColumns.getDrugRegimenInformationParameterized("regimen", false, false), ParameterizableUtil.createParameterMappings("asOfDate=${endDate},untilDate=${endDate+7d}"));
		//baseSetDefinition.addColumn(RowPerPatientColumns.getDrugRegimenInformationParameterized("regimen", false, false), ParameterizableUtil.createParameterMappings("asOfDate=${endDate},untilDate=${endDate+7d}"));
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getDrugRegimenInformationParameterized("regimen", false, false),
		    ParameterizableUtil.createParameterMappings("asOfDate=${endDate},untilDate=${endDate+1d}"));
		
		//dataSetDefinition.addColumn(RowPerPatientColumns.getRegimenDateInformationParameterized("regimenDate", "dd/MMM/yyyy"), ParameterizableUtil.createParameterMappings("asOfDate=${endDate},untilDate=${endDate+7d}"));
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getRegimenDateInformationParameterized("regimenDate", "dd/MMM/yyyy"),
		    ParameterizableUtil.createParameterMappings("asOfDate=${endDate},untilDate=${endDate+1d}"));
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getStateOfPatient("diagnosis", oncologyProgram, diagnosis, null),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("diagnosisNew", confirmedDiagnosis, null),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentHeight("RecentHeight", null),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentWeight("RecentWeight", null),
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
