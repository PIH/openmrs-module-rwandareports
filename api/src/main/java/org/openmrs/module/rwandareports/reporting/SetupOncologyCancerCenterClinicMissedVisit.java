package org.openmrs.module.rwandareports.reporting;

import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.InverseCohortDefinition;
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

public class SetupOncologyCancerCenterClinicMissedVisit extends SingleSetupReport {
	
	//properties retrieved from global variables
	private Program oncologyProgram;
	
	private ProgramWorkflow diagnosis;
	
	private Concept telephone;
	
	private Concept telephone2;
	
	private Concept rwandaCancerCenterChemotherapy;
	
	private Concept confirmedDiagnosis;
	
	private Form OncologyScheduleAppointmentForm;
	
	private Form outpatientClinicVisitsDataOfficerEntryForm;
	
	private Form outpatientClinicVisitsForm;
	
	private Form BSAForm;
	
	private Form ipdDischargeForm;
	
	private List<Concept> visitDates = new ArrayList<Concept>();
	
	private List<Form> visitForms = new ArrayList<Form>();
	
	private List<Form> preventedForms = new ArrayList<Form>();
	
	private List<String> onOrAfterOnOrBefore = new ArrayList<String>();
	
	@Override
	public String getReportName() {
		return "ONC-Oncology Missed Visit Patient List - Rwanda Cancer Center Visit";
	}
	
	public void setup() throws Exception {
		log.info("Setting up report: " + getReportName());
		setupProperties();
		
		ReportDefinition rd = createReportDefinition();
		
		ReportDesign design = Helper.createRowPerPatientXlsOverviewReportDesign(rd,
		    "OncologyRwandaCancerCenterClinicMissedVisit.xls", "OncologyRwandaCancerCenterClinicMissedVisit.xls_", null);
		
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,row:7,dataset:dataset");
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
		dataSetDefinition.setName("Rwanda Cancer Center Missed List");
		
		dataSetDefinition.addParameter(new Parameter("startDate", "From ", Date.class));
		dataSetDefinition.addParameter(new Parameter("endDate", "To", Date.class));
		
		SortCriteria sortCriteria = new SortCriteria();
		sortCriteria.addSortElement("nextRDVDate", SortDirection.ASC);
		dataSetDefinition.setSortCriteria(sortCriteria);
		
		//Add filters
		dataSetDefinition.addFilter(Cohorts.createPatientsMissedVisit(rwandaCancerCenterChemotherapy, visitForms),
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		// who do not have a recorded visit (outpatient, BSA) since the scheduled visit date
		
		dataSetDefinition.addFilter(
		    new InverseCohortDefinition(Cohorts.createEncounterBasedOnForms("patientsWithFlowFormAndBSA",
		        onOrAfterOnOrBefore, preventedForms)), ParameterizableUtil
		            .createParameterMappings("onOrAfter=${endDate-6d},onOrBefore=${endDate}"));
		
		//Add Columns
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecent("nextRDV", rwandaCancerCenterChemotherapy, "dd/MMM/yyyy"),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecent("nextRDVDate", rwandaCancerCenterChemotherapy, "yyyy/MM/dd"),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getArchivingId("archivingId"), new HashMap<String, Object>());
		
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
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecent("nextRDVDate", rwandaCancerCenterChemotherapy, "yyyy/MM/dd"),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getAccompRelationship("accompagnateur"),
		    new HashMap<String, Object>());
		
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("endDate", "${endDate}");
		mappings.put("startDate", "${startDate}");
		
		reportDefinition.addDataSetDefinition("dataset", dataSetDefinition, mappings);
	}
	
	private void setupProperties() {
		
		oncologyProgram = gp.getProgram(GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		diagnosis = gp.getProgramWorkflow(GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
		    GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		telephone = gp.getConcept(GlobalPropertiesManagement.TELEPHONE_NUMBER_CONCEPT);
		
		telephone2 = gp.getConcept(GlobalPropertiesManagement.SECONDARY_TELEPHONE_NUMBER_CONCEPT);
		
		OncologyScheduleAppointmentForm = gp.getForm(GlobalPropertiesManagement.ONCOLOGY_SCHEDULE_APPOINTMENT_FORM);
		
		outpatientClinicVisitsForm = gp.getForm(GlobalPropertiesManagement.OUTPATIENT_CLINIC_VISITS_FORM);
		
		outpatientClinicVisitsDataOfficerEntryForm = gp
		        .getForm(GlobalPropertiesManagement.OUTPATIENT_CLINIC_VISITS_DATA_OFFICER_ENTRY_FORM);
		ipdDischargeForm = gp.getForm(GlobalPropertiesManagement.INPATIENT_DISCHARGE_FORM);
		
		BSAForm = gp.getForm(GlobalPropertiesManagement.BSA_VISITS_FORM);
		
		visitForms.add(outpatientClinicVisitsForm);
		visitForms.add(BSAForm);
		visitForms.add(outpatientClinicVisitsDataOfficerEntryForm);
		visitForms.add(ipdDischargeForm);
		
		rwandaCancerCenterChemotherapy = Context.getConceptService()
		        .getConceptByUuid("8eba01f9-2ea0-49d0-b61b-8d6001e2ff7b");
		
		visitForms.add(OncologyScheduleAppointmentForm);
		
		visitDates.add(rwandaCancerCenterChemotherapy);
		
		onOrAfterOnOrBefore.add("onOrAfter");
		onOrAfterOnOrBefore.add("onOrBefore");
		confirmedDiagnosis = gp.getConcept(GlobalPropertiesManagement.CONFIRMED_DIAGNOSIS_CONCEPT);
	}
}
