package org.openmrs.module.rwandareports.reporting;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.rwandareports.dataset.DataEntryQuantityReport;

public class SetupDataEntryQuantityReport extends SingleSetupReport {
	
	protected final static Log log = LogFactory.getLog(SetupDataEntryDelayReport.class);
	
	@Override
	public String getReportName() {
		return "Data Entry Quantity Report";
	}
	
	public void setup() throws Exception {
		log.info("Setting up report: " + getReportName());
		ReportDefinition rd = createReportDefinition();
		ReportDesign design = Helper.createRowPerPatientXlsOverviewReportDesign(rd, "DataEntryQuantityReport.xls",
		    "DataEntryQuantityReport", null);
		
		createDataSetDefinition(rd);
		
		Helper.saveReportDefinition(rd);
		
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,row:10,dataset:dataSet");
		
		props.put("sortWeight", "5000");
		design.setProperties(props);
		Helper.saveReportDesign(design);
		
	}
	
	private ReportDefinition createReportDefinition() {
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName(getReportName());
		reportDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		reportDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		Parameter prog = new Parameter("program", "Program", Program.class);
		prog.setRequired(false);
		reportDefinition.addParameter(prog);
		Parameter enc = new Parameter("encounterType", "Encounter Type", EncounterType.class);
		enc.setRequired(false);
		reportDefinition.addParameter(enc);
		
		return reportDefinition;
	}
	
	private void createDataSetDefinition(ReportDefinition rd) {
		
		DataEntryQuantityReport dataEntryDelay = new DataEntryQuantityReport();
		dataEntryDelay.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dataEntryDelay.addParameter(new Parameter("endDate", "End Date", Date.class));
		dataEntryDelay.addParameter(new Parameter("program", "Program", Program.class));
		dataEntryDelay.addParameter(new Parameter("encounterType", "Encounter Type", EncounterType.class));
		
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("endDate", "${endDate}");
		mappings.put("startDate", "${startDate}");
		mappings.put("program", "${program}");
		mappings.put("encounterType", "${encounterType}");
		
		rd.addDataSetDefinition("dataSet", dataEntryDelay, mappings);
	}
}
