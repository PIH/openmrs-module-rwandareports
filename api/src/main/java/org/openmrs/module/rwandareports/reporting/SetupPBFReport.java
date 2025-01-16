package org.openmrs.module.rwandareports.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.rwandareports.dataset.LocationHierachyIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;

public class SetupPBFReport extends SingleSetupReport {
	
	protected final static Log log = LogFactory.getLog(SetupPBFReport.class);
	
	// properties
	private Program pmtctPregnancyProgram;
	
	private Program CCMotherProgram;
	
	private Program exposedInfant;
	
	private ProgramWorkflowState Artpregnant;
	
	private List<ProgramWorkflowState> ArtpregnantState = new ArrayList<ProgramWorkflowState>();
	
	private List<String> onOrAfterOnOrBefore = new ArrayList<String>();
	
	private List<String> startDateEndDate = new ArrayList<String>();
	
	private Program pediatrichivProgram;
	
	private Program adulthivProgram;
	
	private Concept cotrimoxazole;
	
	private Concept azt;
	
	private Concept nvp;
	
	private Form tranferToCC;
	
	private List<Form> transferToCCForms = new ArrayList<Form>();
	
	List<Program> hivPrograms = new ArrayList<Program>();
	
	private Concept hivPCR;
	
	private Concept sti;
	
	private Concept reasonForExitingCare;
	
	private Concept patientDied;
	
	private Concept patientTransferedOut;
	
	private Concept weight;
	
	private Concept tbScreeningtest;
	
	private Concept cd4;
	
	private Concept viralLoadConcept;
	
	private ProgramWorkflowState adultOnART;
	
	private ProgramWorkflowState pediOnART;
	
	private ProgramWorkflowState adultOnFollowing;
	
	private ProgramWorkflowState pediOnFollowing;
	
	private ProgramWorkflowState pmtctArt;
	
	private List<EncounterType> clinicalEnountersIncLab;
	
	@Override
	public String getReportName() {
		return "PBF Report";
	}
	
	public void setup() throws Exception {
		log.info("Setting up report: " + getReportName());
		setupProperties();
		
		ReportDefinition rd = createReportDefinition();
		ReportDesign design = Helper.createRowPerPatientXlsOverviewReportDesign(rd, "MohPBFReport.xls",
		    "XlsPBFreporttemplate", null);
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,dataset:PBF Report Location");
		props.put("sortWeight", "5000");
		design.setProperties(props);
		Helper.saveReportDesign(design);
	}
	
	private ReportDefinition createReportDefinition() {
		
		ReportDefinition rd = new ReportDefinition();
		rd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		rd.addParameter(new Parameter("endDate", "End Date", Date.class));
		Properties properties = new Properties();
		//properties.setProperty("hierarchyFields", "countyDistrict:District");
		rd.addParameter(new Parameter("location", "Location", AllLocation.class, properties));
		
		rd.setName(getReportName());
		
		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
		        createDataSetDefinition());
		ldsd.setName("PBF Report Location");
		ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
		
		rd.addDataSetDefinition(ldsd,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
		
		Helper.saveReportDefinition(rd);
		
		return rd;
		
	}
	
	private DataSetDefinition createDataSetDefinition() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("PBF Report Indicators");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		return dsd;
	}
	
	private void setupProperties() {
		pmtctPregnancyProgram = gp.getProgram(GlobalPropertiesManagement.PMTCT_PREGNANCY_PROGRAM);
		CCMotherProgram = gp.getProgram(GlobalPropertiesManagement.PMTCT_COMBINED_MOTHER_PROGRAM);
		pediatrichivProgram = gp.getProgram(GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
		adulthivProgram = gp.getProgram(GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
		exposedInfant = gp.getProgram(GlobalPropertiesManagement.PMTCT_COMBINED_CLINIC_PROGRAM);
		Artpregnant = gp.getProgramWorkflowState(GlobalPropertiesManagement.FOLLOWING_STATE,
		    GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.PMTCT_PREGNANCY_PROGRAM);
		ArtpregnantState.add(Artpregnant);
		hivPrograms.add(pediatrichivProgram);
		hivPrograms.add(adulthivProgram);
		onOrAfterOnOrBefore.add("onOrAfter");
		onOrAfterOnOrBefore.add("onOrBefore");
		startDateEndDate.add("startDate");
		startDateEndDate.add("endDate");
		cotrimoxazole = gp.getConcept(GlobalPropertiesManagement.COTRIMOXAZOLE_DRUG);
		transferToCCForms.add(tranferToCC);
		hivPCR = gp.getConcept(GlobalPropertiesManagement.DBS_CONCEPT);
		sti = gp.getConcept(GlobalPropertiesManagement.STI);
		reasonForExitingCare = gp.getConcept(GlobalPropertiesManagement.REASON_FOR_EXITING_CARE);
		patientDied = gp.getConcept(GlobalPropertiesManagement.PATIENT_DIED);
		patientTransferedOut = gp.getConcept(GlobalPropertiesManagement.TRASNFERED_OUT);
		adultOnART = gp.getProgramWorkflowState(GlobalPropertiesManagement.ON_ANTIRETROVIRALS_STATE,
		    GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
		pediOnART = gp.getProgramWorkflowState(GlobalPropertiesManagement.ON_ANTIRETROVIRALS_STATE,
		    GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
		adultOnFollowing = gp.getProgramWorkflowState(GlobalPropertiesManagement.FOLLOWING_STATE,
		    GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
		pediOnFollowing = gp.getProgramWorkflowState(GlobalPropertiesManagement.FOLLOWING_STATE,
		    GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
		clinicalEnountersIncLab = gp.getEncounterTypeList(GlobalPropertiesManagement.CLINICAL_ENCOUNTER_TYPES);
		weight = gp.getConcept(GlobalPropertiesManagement.WEIGHT_CONCEPT);
		cd4 = gp.getConcept(GlobalPropertiesManagement.CD4_TEST);
		tbScreeningtest = gp.getConcept(GlobalPropertiesManagement.TB_SCREENING_TEST);
		viralLoadConcept = gp.getConcept(GlobalPropertiesManagement.VIRAL_LOAD_TEST);
		pmtctArt = gp.getProgramWorkflowState(GlobalPropertiesManagement.ON_ANTIRETROVIRALS_STATE,
		    GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.PMTCT_PREGNANCY_PROGRAM);
		
	}
	
}
