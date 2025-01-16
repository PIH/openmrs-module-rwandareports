package org.openmrs.module.rwandareports.reporting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
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

public class SetupChemotherapyCancerCenterExpectedPatientList implements SetupReport {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	//properties retrieved from global variables
	private Program oncologyProgram;
	
	private ProgramWorkflow diagnosis;
	
	private Concept telephone;
	
	private Concept telephone2;
	
	private Concept confirmedDiagnosis;
	
	private Form OncologyScheduleAppointmentForm;
	
	private Concept rwandaCancerCenterChemotherapy;
	
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
		    "ChemotherapyCancerCenterExpectedPatientList.xls", "ChemotherapyPatientList.xls_", null);
		
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,row:7,dataset:dataset1");
		props.put("sortWeight", "5000");
		design.setProperties(props);
		
		//		Properties propsInfusion = new Properties();
		//		propsInfusion.put("repeatingSections", "sheet:1,row:7,dataset:dataset2");
		//		propsInfusion.put("sortWeight","5000");
		
		Helper.saveReportDesign(design);
		
	}
	
	public void delete() {
		Helper.purgeReportDefinition("ONC-Oncology Expected Patient List - Rwanda Cancer Center Ward");
	}
	
	private ReportDefinition createReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("ONC-Oncology Expected Patient List - Rwanda Cancer Center Ward");
		reportDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		reportDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		reportDefinition.setBaseCohortDefinition(
		    Cohorts.createInProgramParameterizableByStartEndDate("Oncology", oncologyProgram),
		    ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
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
		
		SortCriteria sortCriteria = new SortCriteria();
		sortCriteria.addSortElement("nextRDVDate", SortDirection.ASC);
		dataSetDefinition.setSortCriteria(sortCriteria);
		
		// Add Filters
		dataSetDefinition.addFilter(Cohorts.getMondayToSundayPatientReturnVisit(visitForms, rwandaCancerCenterChemotherapy),
		    ParameterizableUtil.createParameterMappings("start=${startDate},end=${endDate}"));
		
		//Add Columns
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecent("nextRDVDate", rwandaCancerCenterChemotherapy, "yyyy/MM/dd"),
		    new HashMap<String, Object>());
		
		//	addCommonColumns(dataSetDefinition, baseSetDefinition);
		addCommonColumns(dataSetDefinition);
		
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("startDate", "${startDate}");
		mappings.put("endDate", "${endDate}");
		
		reportDefinition.addDataSetDefinition("dataset1", dataSetDefinition, mappings);
		
	}
	
	private void setupProperties() {
		GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
		oncologyProgram = gp.getProgram(GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		diagnosis = gp.getProgramWorkflow(GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
		    GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		telephone = gp.getConcept(GlobalPropertiesManagement.TELEPHONE_NUMBER_CONCEPT);
		
		telephone2 = gp.getConcept(GlobalPropertiesManagement.SECONDARY_TELEPHONE_NUMBER_CONCEPT);
		
		OncologyScheduleAppointmentForm = gp.getForm(GlobalPropertiesManagement.ONCOLOGY_SCHEDULE_APPOINTMENT_FORM);
		
		rwandaCancerCenterChemotherapy = Context.getConceptService()
		        .getConceptByUuid("8eba01f9-2ea0-49d0-b61b-8d6001e2ff7b");
		
		//		visitForms.add(OncologyScheduleAppointmentForm);
		
		confirmedDiagnosis = gp.getConcept(GlobalPropertiesManagement.CONFIRMED_DIAGNOSIS_CONCEPT);
		/*height=gp.getConcept(GlobalPropertiesManagement.HEIGHT_CONCEPT);
		weight=gp.getConcept(GlobalPropertiesManagement.WEIGHT_CONCEPT);*/
		
	}
	
	private void addCommonColumns(RowPerPatientDataSetDefinition dataSetDefinition) {
		
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecent("nextRDV", rwandaCancerCenterChemotherapy, "dd/MMM/yyyy", null),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		//	baseSetDefinition.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		//	baseSetDefinition.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		//	baseSetDefinition.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		//	baseSetDefinition.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getArchivingId("archivingid"), new HashMap<String, Object>());
		//	baseSetDefinition.addColumn(RowPerPatientColumns.getArchivingId("archivingid"), new HashMap<String, Object>());
		
		//dataSetDefinition.addColumn(RowPerPatientColumns.getDrugRegimenInformationParameterized("regimen", false, false), ParameterizableUtil.createParameterMappings("asOfDate=${endDate},untilDate=${endDate+7d}"));
		//baseSetDefinition.addColumn(RowPerPatientColumns.getDrugRegimenInformationParameterized("regimen", false, false), ParameterizableUtil.createParameterMappings("asOfDate=${endDate},untilDate=${endDate+7d}"));
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getDrugRegimenInformationParameterized("regimen", false, false),
		    ParameterizableUtil.createParameterMappings("asOfDate=${endDate},untilDate=${endDate+1d}"));
		//    baseSetDefinition.addColumn(RowPerPatientColumns.getDrugRegimenInformationParameterized("regimen", false, false), ParameterizableUtil.createParameterMappings("asOfDate=${endDate},untilDate=${endDate+1d}"));
		
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
