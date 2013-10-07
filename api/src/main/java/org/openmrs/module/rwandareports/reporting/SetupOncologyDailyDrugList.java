package org.openmrs.module.rwandareports.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openmrs.Concept;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rwandareports.dataset.DrugOrderDataSetDefinition;
import org.openmrs.module.rwandareports.dataset.DrugOrderTotalDataSetDefinition;
import org.openmrs.module.rwandareports.definition.UpcomingChemotherapyCohortDefinition;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

public class SetupOncologyDailyDrugList {
	
	Helper h = new Helper();
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	//properties retrieved from global variables
	private Concept chemotherapy;
	
	private Concept premedication;
	
	private Concept normalSaline;
	
	List<Concept> indications = new ArrayList<Concept>();
	
	List<Concept> drugExclusions = new ArrayList<Concept>();
	
	private List<String> onOrAfterOnOrBeforeParamterNames = new ArrayList<String>();
	
	
	public void setup() throws Exception {
		
		setupProperties();
		
		ReportDefinition rd = createReportDefinition();
		
		ReportDesign design = h.createRowPerPatientXlsOverviewReportDesign(rd, "ChemotherapyDailyDrugList.xls",
		    "ChemotherapyDailyDrugList.xls_", null);
		
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,row:6-14,dataset:dataset|sheet:1,row:17,dataset:dataset2");
		props.put("sortWeight","5000");
		design.setProperties(props);
		
		h.saveReportDesign(design);
	
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("ChemotherapyDailyDrugList.xls_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeReportDefinition("ONC-Chemotherapy Daily Drug List");
	}
	
	private ReportDefinition createReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("ONC-Chemotherapy Daily Drug List");
		
		onOrAfterOnOrBeforeParamterNames.add("onOrAfter");
		onOrAfterOnOrBeforeParamterNames.add("onOrBefore");
				
		CodedObsCohortDefinition baseCohort = Cohorts.createCodedObsCohortDefinition("registeredChemo",
        		onOrAfterOnOrBeforeParamterNames, gp.getConcept(GlobalPropertiesManagement.PATIENT_PRESENTS_FOR_CHEMO),gp.getConcept(GlobalPropertiesManagement.YES), SetComparator.IN, TimeModifier.LAST);
		
		reportDefinition.setBaseCohortDefinition(baseCohort,ParameterizableUtil.createParameterMappings("onOrAfter=${endDate},onOrBefore=${endDate+1d}"));
		reportDefinition.addParameter(new Parameter("endDate", "Date", Date.class));
		createDataSetDefinition(reportDefinition);
		
		h.saveReportDefinition(reportDefinition);
		
		return reportDefinition;
	}
	
	private void createDataSetDefinition(ReportDefinition reportDefinition) {
		// Create new dataset definition 
		DrugOrderDataSetDefinition dataSetDefinition = new DrugOrderDataSetDefinition();
		dataSetDefinition.setName("Chemotherapy Daily Drug List");
		dataSetDefinition.addParameter(new Parameter("asOfDate", "asOfDate", Date.class));
		dataSetDefinition.setIndication(indications);
		dataSetDefinition.setDrugExclusions(drugExclusions);
		
		DrugOrderTotalDataSetDefinition dataSetDefinition2 = new DrugOrderTotalDataSetDefinition();
		dataSetDefinition2.setName("Chemotherapy Daily Drug List");
		dataSetDefinition2.addParameter(new Parameter("asOfDate", "asOfDate", Date.class));
		dataSetDefinition2.setIndication(indications);
		dataSetDefinition2.setDrugExclusions(drugExclusions);
		
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("asOfDate", "${endDate}");
		
		reportDefinition.addDataSetDefinition("dataset", dataSetDefinition, mappings);
		reportDefinition.addDataSetDefinition("dataset2", dataSetDefinition2, mappings);
	}
	
	private void setupProperties() {
		
		chemotherapy = gp.getConcept(GlobalPropertiesManagement.CHEMOTHERAPY);
		
		premedication = gp.getConcept(GlobalPropertiesManagement.PREMEDICATION);
		
		indications.add(chemotherapy);
		indications.add(premedication);
		
		normalSaline = gp.getConcept(GlobalPropertiesManagement.NORMAL_SALINE);
		
		drugExclusions.add(normalSaline);
	
	}
}
