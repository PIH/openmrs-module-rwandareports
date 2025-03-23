package org.openmrs.module.rwandareports.reporting;

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
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.RowPerPatientColumns;

import java.util.*;

public class SetupOncologyPediatricClinicMissedVisit extends SingleSetupReport {
	
	//properties retrieved from global variables
	private Program oncologyProgram;
	
	private ProgramWorkflow diagnosis;
	
	private Concept scheduledVisit;
	
	private Concept biopsyResultVisit;
	
	private Concept specialVisit;
	
	private Concept telephone;
	
	private Concept telephone2;
	
	private Concept confirmedDiagnosis;
	
	private Form OncologyScheduleAppointmentForm;
	
	private Form outpatientClinicVisitsForm;
	
	private Form ipdDischargeForm;
	
	private Concept pediatricChemotherapy;
	
	private Concept pediatricNonChemotherapy;
	
	private List<Concept> visitDates = new ArrayList<Concept>();
	
	private List<Form> visitForms = new ArrayList<Form>();
	
	@Override
	public String getReportName() {
		return "ONC-Oncology Missed Visit Patient List - Pediatric Ward";
	}
	
	public void setup() throws Exception {
		log.info("Setting up report: " + getReportName());
		setupProperties();
		
		ReportDefinition rd = createReportDefinition();
		
		ReportDesign design = Helper.createRowPerPatientXlsOverviewReportDesign(rd, "OncologyPediatricMissedVisit.xls",
		    "PediatricMissedVisits.xls_", null);
		
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,row:7,dataset:dataset|sheet:2,row:7,dataset:dataset2");
		props.put("sortWeight", "5000");
		design.setProperties(props);
		
		Helper.saveReportDesign(design);
	}
	
	private ReportDefinition createReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName(getReportName());
		
		reportDefinition.addParameter(new Parameter("startDate", "From ", Date.class));
		reportDefinition.addParameter(new Parameter("endDate", "To", Date.class));
		
		reportDefinition.setBaseCohortDefinition(
		    Cohorts.createInProgramParameterizableByStartEndDate("Oncology", oncologyProgram),
		    ParameterizableUtil.createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}"));
		createDataSetDefinition(reportDefinition);
		
		Helper.saveReportDefinition(reportDefinition);
		
		return reportDefinition;
	}
	
	private void createDataSetDefinition(ReportDefinition reportDefinition) {
		// Create new dataset definition 
		RowPerPatientDataSetDefinition dataSetDefinition = new RowPerPatientDataSetDefinition();
		dataSetDefinition.setName("Pediatric Chemo Visit Missed List");
		RowPerPatientDataSetDefinition dataSetDefinition2 = new RowPerPatientDataSetDefinition();
		dataSetDefinition2.setName("Pediatric Non Chemo Visit Missed List");
		
		dataSetDefinition.addParameter(new Parameter("startDate", "From ", Date.class));
		dataSetDefinition.addParameter(new Parameter("endDate", "To", Date.class));
		
		dataSetDefinition2.addParameter(new Parameter("startDate", "From ", Date.class));
		dataSetDefinition2.addParameter(new Parameter("endDate", "To", Date.class));
		
		SortCriteria sortCriteria = new SortCriteria();
		sortCriteria.addSortElement("nextRDVDate", SortDirection.ASC);
		dataSetDefinition.setSortCriteria(sortCriteria);
		dataSetDefinition2.setSortCriteria(sortCriteria);
		
		//Add filters
		dataSetDefinition.addFilter(Cohorts.createPatientsMissedVisit(pediatricChemotherapy, visitForms),
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dataSetDefinition2.addFilter(Cohorts.createPatientsMissedVisit(pediatricNonChemotherapy, visitForms),
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		//Add Columns
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("nextRDV", pediatricChemotherapy, "dd/MMM/yyyy"),
		    new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getMostRecent("nextRDV", pediatricNonChemotherapy, "dd/MMM/yyyy"),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("nextRDVDate", pediatricChemotherapy, "yyyy/MM/dd"),
		    new HashMap<String, Object>());
		dataSetDefinition2.addColumn(
		    RowPerPatientColumns.getMostRecent("nextRDVDate", pediatricNonChemotherapy, "yyyy/MM/dd"),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getArchivingId("archivingId"), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getArchivingId("archivingId"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getStateOfPatient("diagnosis", oncologyProgram, diagnosis, null),
		    new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getStateOfPatient("diagnosis", oncologyProgram, diagnosis, null),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("diagnosisNew", confirmedDiagnosis, null),
		    new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getMostRecent("diagnosisNew", confirmedDiagnosis, null),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("telephone", telephone, null),
		    new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getMostRecent("telephone", telephone, null),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("telephone2", telephone2, null),
		    new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getMostRecent("telephone2", telephone2, null),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getPatientAddress("address", true, true, true, true),
		    new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getPatientAddress("address", true, true, true, true),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getAccompRelationship("accompagnateur"),
		    new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getAccompRelationship("accompagnateur"),
		    new HashMap<String, Object>());
		
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("endDate", "${endDate}");
		mappings.put("startDate", "${startDate}");
		
		reportDefinition.addDataSetDefinition("dataset", dataSetDefinition, mappings);
		reportDefinition.addDataSetDefinition("dataset2", dataSetDefinition2, mappings);
		
	}
	
	private void setupProperties() {
		
		oncologyProgram = gp.getProgram(GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		diagnosis = gp.getProgramWorkflow(GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
		    GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		scheduledVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_SCHEDULED_OUTPATIENT_VISIT);
		
		biopsyResultVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_PATHOLOGY_RESULT_VISIT);
		
		specialVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_SPECIAL_VISIT);
		
		telephone = gp.getConcept(GlobalPropertiesManagement.TELEPHONE_NUMBER_CONCEPT);
		
		telephone2 = gp.getConcept(GlobalPropertiesManagement.SECONDARY_TELEPHONE_NUMBER_CONCEPT);
		
		OncologyScheduleAppointmentForm = gp.getForm(GlobalPropertiesManagement.ONCOLOGY_SCHEDULE_APPOINTMENT_FORM);
		
		outpatientClinicVisitsForm = gp.getForm(GlobalPropertiesManagement.OUTPATIENT_CLINIC_VISITS_FORM);
		
		ipdDischargeForm = gp.getForm(GlobalPropertiesManagement.INPATIENT_DISCHARGE_FORM);
		pediatricChemotherapy = Context.getConceptService().getConceptByUuid("5efb51db-4e71-497d-822c-91501ac167f6");
		pediatricNonChemotherapy = Context.getConceptService().getConceptByUuid("8c3b045a-aa94-4361-b2e9-8a80c26ccede");
		
		visitForms.add(OncologyScheduleAppointmentForm);
		visitForms.add(outpatientClinicVisitsForm);
		visitForms.add(ipdDischargeForm);
		
		visitDates.add(pediatricChemotherapy);
		visitDates.add(pediatricNonChemotherapy);
		
		confirmedDiagnosis = gp.getConcept(GlobalPropertiesManagement.CONFIRMED_DIAGNOSIS_CONCEPT);
		
	}
}
