package org.openmrs.module.rwandareports;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.InverseCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.XlsReportRenderer;
import org.openmrs.module.rowperpatientreports.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.rowperpatientreports.patientdata.definition.PatientAttribute;
import org.openmrs.module.rowperpatientreports.patientdata.definition.PatientIdentifier;
import org.openmrs.module.rwandareports.dataset.LocationHierachyIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

@Ignore
public class MultiLocationRowPerPatientTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		authenticate();
	}
	
	/**
	 * @see BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
	
	@Test
	public void runReport() throws Exception {
		
		Map<String, String> properties = getRequiredGlobalProperties();
		Date now = new Date();
		
		// Create Report Definition
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("Missing CD4 All Sites Report");
		
		// Parameters
		reportDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		reportDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		Properties prop = new Properties();
		prop.setProperty("hierarchyFields", "countyDistrict:District");
		reportDefinition.addParameter(new Parameter("location", "Location", AllLocation.class, prop));
		
		// Base Cohort
		
		// Data Set
		
		RowPerPatientDataSetDefinition noResultDataSet = new RowPerPatientDataSetDefinition();
		noResultDataSet.setName("No Result Data Set");
		
		noResultDataSet.addParameter(new Parameter("location", "Location", Location.class));
		
		noResultDataSet.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		noResultDataSet.addParameter(new Parameter("startDate", "Start Date", Date.class));
		
		// Row Filters
		SqlCohortDefinition patientDied = new SqlCohortDefinition(
		        "SELECT DISTINCT person_id FROM obs o WHERE o.concept_id='" + properties.get("PATIENT_DIED_CONCEPT") + "'");
		InverseCohortDefinition patientAlive = new InverseCohortDefinition(patientDied);
		noResultDataSet.addFilter(patientAlive, new HashMap<String, Object>());
		
		//		SqlCohortDefinition noResult = new SqlCohortDefinition();
		//		noResult
		//		        .setQuery("select person_id from obs where comments in ('Re-order', 'Closed', 'Failed') and obs_datetime < :endDate " +
		//						"and concept_id =" +
		//						properties.get("CD4_CONCEPT") + 
		//						" and obs_id in (select o.obs_id from (select person_id as pi, max(obs_datetime) od from obs where concept_id=" +
		//						 properties.get("CD4_CONCEPT") + 
		//						" and voided = 0 group by person_id)RecentObs inner join obs o on o.obs_datetime = RecentObs.od and o.person_id = RecentObs.pi)");	
		
		//		noResult.addParameter(new Parameter("endDate", "endDate", Date.class));
		//		noResultDataSet.addFilter(noResult, ParameterizableUtil.createParameterMappings("endDate=${endDate-1w}"));
		
		// Column Data Definitions
		PatientIdentifierType imbType = Context.getPatientService().getPatientIdentifierTypeByName("IMB ID");
		PatientIdentifier imbId = new PatientIdentifier(imbType);
		imbId.setName("IMB ID");
		imbId.setDescription("IMB ID");
		noResultDataSet.addColumn(imbId, new HashMap<String, Object>());
		
		PatientAttribute healthCenter = new PatientAttribute();
		healthCenter.setAttribute("Health Center");
		noResultDataSet.addColumn(healthCenter, new HashMap<String, Object>());
		
		Map<String, Object> mappings1 = new HashMap<String, Object>();
		mappings1.put("location", "${location}");
		mappings1.put("endDate", "${endDate}");
		mappings1.put("startDate", "${startDate}");
		
		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(noResultDataSet);
		ldsd.setName("NotCompleted Data Set");
		ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
		
		reportDefinition.addDataSetDefinition("NoResult", ldsd, mappings1);
		
		EvaluationContext context = new EvaluationContext();
		AllLocation kirehe = new AllLocation();
		kirehe.setAllSites(false);
		kirehe.setHierarchy("countyDistrict");
		kirehe.setValue("Kirehe");
		context.addParameterValue("location", kirehe);
		
		Calendar startDate = Calendar.getInstance();
		startDate.add(Calendar.MONTH, -2);
		context.addParameterValue("startDate", startDate.getTime());
		context.addParameterValue("endDate", new Date());
		
		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		ReportData data = rs.evaluate(reportDefinition, context);
		
		String outFile = System.getProperty("java.io.tmpdir") + File.separator + "test.xls";
		FileOutputStream fos = new FileOutputStream(outFile);
		
		XlsReportRenderer renderer = new XlsReportRenderer();
		renderer.render(data, "xxx:xls", fos);
		fos.close();
	}
	
	public Map<String, String> getRequiredGlobalProperties() {
		Map<String, String> properties = new HashMap<String, String>();
		
		String hivProgram = Context.getAdministrationService().getGlobalProperty("reports.hivprogramname");
		properties.put("HIV_PROGRAM", hivProgram);
		
		String pediHivProgram = Context.getAdministrationService().getGlobalProperty("reports.pedihivprogramname");
		properties.put("PEDI_HIV_PROGRAM", pediHivProgram);
		
		String workflowStatus = Context.getAdministrationService().getGlobalProperty("reports.hivworkflowstatus");
		properties.put("HIV_WORKFLOW_STATUS", workflowStatus);
		
		String groupStatus = Context.getAdministrationService().getGlobalProperty("reports.hivtreatmentstatus");
		properties.put("HIV_TREATMENT_GROUP_STATUS", groupStatus);
		
		String patientDiedConcept = Context.getAdministrationService().getGlobalProperty("reports.patientDiedConcept");
		properties.put("PATIENT_DIED_CONCEPT", patientDiedConcept);
		
		String cd4Concept = Context.getAdministrationService().getGlobalProperty("reports.cd4Concept");
		properties.put("CD4_CONCEPT", cd4Concept);
		
		String cd4LabConcept = Context.getAdministrationService().getGlobalProperty("reports.cd4LabConcept");
		properties.put("CD4_LAB_CONCEPT", cd4LabConcept);
		
		String labOrderType = Context.getAdministrationService().getGlobalProperty("reports.labOrderType");
		properties.put("LAB_ORDER_TYPE", labOrderType);
		
		return properties;
	}
}
