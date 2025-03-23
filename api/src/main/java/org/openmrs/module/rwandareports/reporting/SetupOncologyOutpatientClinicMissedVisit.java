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
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.RowPerPatientColumns;

public class SetupOncologyOutpatientClinicMissedVisit extends SingleSetupReport {
	
	//properties retrieved from global variables
	private Program oncologyProgram;
	
	private ProgramWorkflow diagnosis;
	
	private Concept pathologyResultVisit;
	
	private Concept scheduledVisit;
	
	private Concept biopsyResultVisit;
	
	private Concept specialVisit;
	
	private Concept telephone;
	
	private Concept telephone2;
	
	private Concept confirmedDiagnosis;
	
	private Form OncologyScheduleAppointmentForm;
	
	private Form outpatientClinicVisitsForm;
	
	private Form outpatientClinicVisitsDataOfficerEntryForm;
	
	private List<Concept> visitDates = new ArrayList<Concept>();
	
	private List<Form> visitForms = new ArrayList<Form>();
	
	@Override
	public String getReportName() {
		return "ONC-Oncology Missed Visit Patient List - Outpatient Ward";
	}
	
	public void setup() throws Exception {
		log.info("Setting up report: " + getReportName());
		setupProperties();
		
		ReportDefinition rd = createReportDefinition();
		
		ReportDesign design = Helper.createRowPerPatientXlsOverviewReportDesign(rd,
		    "OncologyOutpatientClinicMissedVisit.xls", "OncologyOutpatientClinicMissedVisit.xls_", null);
		
		Properties props = new Properties();
		props.put(
		    "repeatingSections",
		    "sheet:1,row:7,dataset:datasetAll|sheet:2,row:7,dataset:dataset|sheet:3,row:7,dataset:dataset2|sheet:4,row:7,dataset:dataset3|sheet:5,row:7,dataset:dataset4");
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
		dataSetDefinition.setName("Outpatient Missed List");
		RowPerPatientDataSetDefinition dataSetDefinition2 = new RowPerPatientDataSetDefinition();
		dataSetDefinition2.setName("Outpatient Biopsy Result List");
		RowPerPatientDataSetDefinition dataSetDefinition3 = new RowPerPatientDataSetDefinition();
		dataSetDefinition3.setName("Outpatient Special List");
		RowPerPatientDataSetDefinition dataSetDefinition4 = new RowPerPatientDataSetDefinition();
		dataSetDefinition3.setName("Outpatient Pathology Results Lists");
		RowPerPatientDataSetDefinition dataSetDefinition5 = new RowPerPatientDataSetDefinition();
		dataSetDefinition3.setName("Outpatient All Lists");
		
		dataSetDefinition.addParameter(new Parameter("startDate", "From ", Date.class));
		dataSetDefinition.addParameter(new Parameter("endDate", "To", Date.class));
		dataSetDefinition2.addParameter(new Parameter("startDate", "From ", Date.class));
		dataSetDefinition2.addParameter(new Parameter("endDate", "To", Date.class));
		dataSetDefinition3.addParameter(new Parameter("startDate", "From ", Date.class));
		dataSetDefinition3.addParameter(new Parameter("endDate", "To", Date.class));
		dataSetDefinition4.addParameter(new Parameter("startDate", "From ", Date.class));
		dataSetDefinition4.addParameter(new Parameter("endDate", "To", Date.class));
		dataSetDefinition5.addParameter(new Parameter("startDate", "From ", Date.class));
		dataSetDefinition5.addParameter(new Parameter("endDate", "To", Date.class));
		
		SortCriteria sortCriteria = new SortCriteria();
		sortCriteria.addSortElement("nextRDVDate", SortDirection.ASC);
		dataSetDefinition.setSortCriteria(sortCriteria);
		dataSetDefinition2.setSortCriteria(sortCriteria);
		dataSetDefinition3.setSortCriteria(sortCriteria);
		dataSetDefinition4.setSortCriteria(sortCriteria);
		dataSetDefinition5.setSortCriteria(sortCriteria);
		
		//Add filters
		dataSetDefinition.addFilter(Cohorts.createPatientsMissedVisit(scheduledVisit, visitForms),
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dataSetDefinition2.addFilter(Cohorts.createPatientsMissedVisit(biopsyResultVisit, visitForms),
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dataSetDefinition3.addFilter(Cohorts.createPatientsMissedVisit(specialVisit, visitForms),
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dataSetDefinition4.addFilter(Cohorts.createPatientsMissedVisit(pathologyResultVisit, visitForms),
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dataSetDefinition5.addFilter(Cohorts.createPatientsMissedVisit(visitDates, visitForms),
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		//Add Columns
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("nextRDV", scheduledVisit, "dd/MMM/yyyy"),
		    new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getMostRecent("nextRDV", biopsyResultVisit, "dd/MMM/yyyy"),
		    new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getMostRecent("nextRDV", specialVisit, "dd/MMM/yyyy"),
		    new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getMostRecent("nextRDV", pathologyResultVisit, "dd/MMM/yyyy"),
		    new HashMap<String, Object>());
		
		dataSetDefinition5.addColumn(
		    RowPerPatientColumns.getMostRecent("nextRDVscheduledVisit", scheduledVisit, "dd/MMM/yyyy"),
		    new HashMap<String, Object>());
		dataSetDefinition5.addColumn(
		    RowPerPatientColumns.getMostRecent("nextRDVbiopsyResultVisit", biopsyResultVisit, "dd/MMM/yyyy"),
		    new HashMap<String, Object>());
		dataSetDefinition5.addColumn(RowPerPatientColumns.getMostRecent("nextRDVspecialVisit", specialVisit, "dd/MMM/yyyy"),
		    new HashMap<String, Object>());
		dataSetDefinition5.addColumn(
		    RowPerPatientColumns.getMostRecent("nextRDVpathologyVisit", pathologyResultVisit, "dd/MMM/yyyy"),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("nextRDVDate", scheduledVisit, "yyyy/MM/dd"),
		    new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getMostRecent("nextRDVDate", biopsyResultVisit, "yyyy/MM/dd"),
		    new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getMostRecent("nextRDVDate", specialVisit, "yyyy/MM/dd"),
		    new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getMostRecent("nextRDVDate", pathologyResultVisit, "yyyy/MM/dd"),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		dataSetDefinition5.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		dataSetDefinition5.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		dataSetDefinition5.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		dataSetDefinition5.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getArchivingId("archivingId"), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getArchivingId("archivingId"), new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getArchivingId("archivingId"), new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getArchivingId("archivingId"), new HashMap<String, Object>());
		dataSetDefinition5.addColumn(RowPerPatientColumns.getArchivingId("archivingId"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getStateOfPatient("diagnosis", oncologyProgram, diagnosis, null),
		    new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getStateOfPatient("diagnosis", oncologyProgram, diagnosis, null),
		    new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getStateOfPatient("diagnosis", oncologyProgram, diagnosis, null),
		    new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getStateOfPatient("diagnosis", oncologyProgram, diagnosis, null),
		    new HashMap<String, Object>());
		dataSetDefinition5.addColumn(RowPerPatientColumns.getStateOfPatient("diagnosis", oncologyProgram, diagnosis, null),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("diagnosisNew", confirmedDiagnosis, null),
		    new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getMostRecent("diagnosisNew", confirmedDiagnosis, null),
		    new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getMostRecent("diagnosisNew", confirmedDiagnosis, null),
		    new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getMostRecent("diagnosisNew", confirmedDiagnosis, null),
		    new HashMap<String, Object>());
		dataSetDefinition5.addColumn(RowPerPatientColumns.getMostRecent("diagnosisNew", confirmedDiagnosis, null),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("telephone", telephone, null),
		    new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getMostRecent("telephone", telephone, null),
		    new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getMostRecent("telephone", telephone, null),
		    new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getMostRecent("telephone", telephone, null),
		    new HashMap<String, Object>());
		dataSetDefinition5.addColumn(RowPerPatientColumns.getMostRecent("telephone", telephone, null),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("telephone2", telephone2, null),
		    new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getMostRecent("telephone2", telephone2, null),
		    new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getMostRecent("telephone2", telephone2, null),
		    new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getMostRecent("telephone2", telephone2, null),
		    new HashMap<String, Object>());
		dataSetDefinition5.addColumn(RowPerPatientColumns.getMostRecent("telephone2", telephone2, null),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getPatientAddress("address", true, true, true, true),
		    new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getPatientAddress("address", true, true, true, true),
		    new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getPatientAddress("address", true, true, true, true),
		    new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getPatientAddress("address", true, true, true, true),
		    new HashMap<String, Object>());
		dataSetDefinition5.addColumn(RowPerPatientColumns.getPatientAddress("address", true, true, true, true),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getAccompRelationship("accompagnateur"),
		    new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getAccompRelationship("accompagnateur"),
		    new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getAccompRelationship("accompagnateur"),
		    new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getAccompRelationship("accompagnateur"),
		    new HashMap<String, Object>());
		dataSetDefinition5.addColumn(RowPerPatientColumns.getAccompRelationship("accompagnateur"),
		    new HashMap<String, Object>());
		
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("endDate", "${endDate}");
		mappings.put("startDate", "${startDate}");
		
		reportDefinition.addDataSetDefinition("dataset", dataSetDefinition, mappings);
		reportDefinition.addDataSetDefinition("dataset2", dataSetDefinition2, mappings);
		reportDefinition.addDataSetDefinition("dataset3", dataSetDefinition3, mappings);
		reportDefinition.addDataSetDefinition("dataset4", dataSetDefinition4, mappings);
		reportDefinition.addDataSetDefinition("datasetAll", dataSetDefinition5, mappings);
		
	}
	
	private void setupProperties() {
		
		oncologyProgram = gp.getProgram(GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		diagnosis = gp.getProgramWorkflow(GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
		    GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		scheduledVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_SCHEDULED_OUTPATIENT_VISIT);
		
		biopsyResultVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_BIOPSY_RESULT_VISIT);
		pathologyResultVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_PATHOLOGY_RESULT_VISIT);
		
		specialVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_SPECIAL_VISIT);
		
		telephone = gp.getConcept(GlobalPropertiesManagement.TELEPHONE_NUMBER_CONCEPT);
		
		telephone2 = gp.getConcept(GlobalPropertiesManagement.SECONDARY_TELEPHONE_NUMBER_CONCEPT);
		
		OncologyScheduleAppointmentForm = gp.getForm(GlobalPropertiesManagement.ONCOLOGY_SCHEDULE_APPOINTMENT_FORM);
		
		outpatientClinicVisitsForm = gp.getForm(GlobalPropertiesManagement.OUTPATIENT_CLINIC_VISITS_FORM);
		
		outpatientClinicVisitsDataOfficerEntryForm = gp
		        .getForm(GlobalPropertiesManagement.OUTPATIENT_CLINIC_VISITS_DATA_OFFICER_ENTRY_FORM);
		
		visitForms.add(OncologyScheduleAppointmentForm);
		visitForms.add(outpatientClinicVisitsForm);
		visitForms.add(outpatientClinicVisitsDataOfficerEntryForm);
		
		visitDates.add(scheduledVisit);
		visitDates.add(biopsyResultVisit);
		visitDates.add(specialVisit);
		visitDates.add(pathologyResultVisit);
		
		confirmedDiagnosis = gp.getConcept(GlobalPropertiesManagement.CONFIRMED_DIAGNOSIS_CONCEPT);
		
	}
}
