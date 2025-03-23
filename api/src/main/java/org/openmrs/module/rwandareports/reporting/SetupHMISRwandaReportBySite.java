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
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.rwandareports.dataset.LocationHierachyIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.Indicators;
import org.openmrs.module.rwandareports.util.MetadataLookup;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;

public class SetupHMISRwandaReportBySite extends SingleSetupReport {
	
	protected final static Log log = LogFactory.getLog(SetupHMISRwandaReportBySite.class);
	
	//MetadataLookup mlookup=new MetadataLookup();
	// properties
	private Program adulthivProgram;
	
	private Program pediatrichivProgram;
	
	private Program pmtctcombinedMother;
	
	private Program pmtctPregnancyProgram;
	
	private Program pmtctCombinedInfantProgram;
	
	private ProgramWorkflowState adultOnFollowing;
	
	private ProgramWorkflowState pediOnFollowing;
	
	private ProgramWorkflowState adultOnART;
	
	private ProgramWorkflowState pediOnART;
	
	private ProgramWorkflowState pmtctOnART;
	
	private ProgramWorkflowState pediPreAndArtDiedState;
	
	private ProgramWorkflowState adultPreAndArtDiedState;
	
	private ProgramWorkflowState adulttransferedOutState;
	
	private ProgramWorkflowState peditransferedOutState;
	
	private Concept tbScreeningtest;
	
	private Concept positiveStatus;
	
	private Concept reasonForExitingCare;
	
	private Concept patientDied;
	
	private Concept patientTransferedOut;
	
	private Concept patientDefaulted;
	
	private Form transferinForm;
	
	private List<String> onOrAfterOnOrBefore = new ArrayList<String>();
	
	private List<String> onOrAfterDateOnOrBeforeDate = new ArrayList<String>();
	
	private List<EncounterType> clinicalEnountersIncLab;
	
	private List<EncounterType> pediAdnAdultEncounters;
	
	private EncounterType patientTransferEncounterType;
	
	private Concept kaletra;
	
	private Concept cotrimoxazole;
	
	private Concept dapsone;
	
	@Override
	public String getReportName() {
		return "HIV-HMIS Report";
	}
	
	public void setup() throws Exception {
		log.info("Setting up report: " + getReportName());
		setupProperties();
		
		ReportDefinition rd = createReportDefinition();
		ReportDesign design = Helper.createRowPerPatientXlsOverviewReportDesign(rd, "Mohrwandatracnetreporttemplate.xls",
		    "Xlshivhmisreporttemplate", null);
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,dataset:TracNet Report Location");
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
		ldsd.setName("TracNet Report Location");
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
		dsd.setName("TracNet Report Indicators");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		//Gender Cohort definitions
		GenderCohortDefinition femaleCohort = Cohorts.createFemaleCohortDefinition("femalesDefinition");
		GenderCohortDefinition maleCohort = Cohorts.createMaleCohortDefinition("malesDefinition");
		SqlCohortDefinition under15inDays = new SqlCohortDefinition();
		under15inDays.setName("under15inDays");
		under15inDays
		        .setQuery("SELECT distinct pe.person_id from person pe WHERE DATEDIFF(:dateborn, pe.birthdate) < 5479 and pe.dead=0 and pe.voided=0 ");
		under15inDays.addParameter(new Parameter("dateborn", "dateborn", Date.class));
		
		SqlCohortDefinition over15inDays = new SqlCohortDefinition();
		over15inDays.setName("over15inDays");
		over15inDays
		        .setQuery("SELECT distinct pe.person_id from person pe WHERE DATEDIFF(:dateborn, pe.birthdate) >= 5479 and pe.dead=0 and pe.voided=0 ");
		over15inDays.addParameter(new Parameter("dateborn", "dateborn", Date.class));
		
		AgeCohortDefinition at18monthsOfAge = Cohorts.createUnder18monthsCohort("TR:at18monthsOfAge");
		AgeCohortDefinition underFive = new AgeCohortDefinition(null, 4, null);
		
		//Program Cohorts
		//PMTCT Combined Mother
		List<Program> PmtctCombinrMotherProgram = new ArrayList<Program>();
		PmtctCombinrMotherProgram.add(pmtctcombinedMother);
		InProgramCohortDefinition inPmtctMotherprogram = Cohorts.createInProgramParameterizableByDate(
		    "TR:inPmtctMotherprogram", PmtctCombinrMotherProgram, "onDate");
		//HIV programs
		List<Program> hivPrograms = new ArrayList<Program>();
		hivPrograms.add(adulthivProgram);
		hivPrograms.add(pediatrichivProgram);
		InProgramCohortDefinition inPediAndAdultprogram = Cohorts.createInProgramParameterizableByDate("TR:inPediAndAdult",
		    hivPrograms, "onDate");
		SqlCohortDefinition onARTDrugs = Cohorts.getArtDrugs("TR:On Art Drugs ever");
		// PMTCT Pregnancy Program
		List<Program> PmtctPregnancyProgram = new ArrayList<Program>();
		PmtctPregnancyProgram.add(pmtctPregnancyProgram);
		InProgramCohortDefinition inPmtctPregnancyprogram = Cohorts.createInProgramParameterizableByDate(
		    "TR:inPmtctPregnancyprogram", PmtctPregnancyProgram, "onDate");
		
		ProgramEnrollmentCohortDefinition patientEnrolledInPediAndAdultProgram = new ProgramEnrollmentCohortDefinition();
		patientEnrolledInPediAndAdultProgram.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore",
		        Date.class));
		patientEnrolledInPediAndAdultProgram
		        .addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientEnrolledInPediAndAdultProgram.setPrograms(hivPrograms);
		
		// Total number of female pediatric patients (age <15 years) ever enrolled in HIV care  
		CompositionCohortDefinition onARTStateInPMTCTClinic = new CompositionCohortDefinition();
		onARTStateInPMTCTClinic.setName("TR:onARTStateInPMTCTClinic");
		onARTStateInPMTCTClinic.getSearches().put("1", new Mapped<CohortDefinition>(onARTDrugs, null));
		onARTStateInPMTCTClinic.getSearches()
		        .put(
		            "2",
		            new Mapped<CohortDefinition>(inPmtctMotherprogram, ParameterizableUtil
		                    .createParameterMappings("onDate=${now}")));
		onARTStateInPMTCTClinic.setCompositionString("1 AND 2");
		
		List<ProgramWorkflowState> OnFollowingstates = new ArrayList<ProgramWorkflowState>();
		OnFollowingstates.add(adultOnFollowing);
		OnFollowingstates.add(pediOnFollowing);
		InStateCohortDefinition onFollowingStateCohort = Cohorts.createInCurrentState("TR:onFollowingStateCohort",
		    OnFollowingstates, "onDate");
		
		CompositionCohortDefinition onFollowingStateHIVClinic = new CompositionCohortDefinition();
		onFollowingStateHIVClinic.setName("TR:onFollowingStateHIVClinic");
		onFollowingStateHIVClinic.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(onFollowingStateCohort, ParameterizableUtil
		            .createParameterMappings("onDate=${now}")));
		onFollowingStateHIVClinic.getSearches()
		        .put(
		            "2",
		            new Mapped<CohortDefinition>(inPediAndAdultprogram, ParameterizableUtil
		                    .createParameterMappings("onDate=${now}")));
		onFollowingStateHIVClinic.setCompositionString("1 AND 2");
		
		CodedObsCohortDefinition exitedCareWithDeadStatus = Cohorts.createCodedObsCohortDefinition(
		    "exitedCareWithDeadStatus", onOrAfterOnOrBefore, reasonForExitingCare, patientDied, SetComparator.IN,
		    TimeModifier.LAST);
		CodedObsCohortDefinition exitedCareWithtransferStatus = Cohorts.createCodedObsCohortDefinition(
		    "exitedCareWithtransferStatus", onOrAfterOnOrBefore, reasonForExitingCare, patientTransferedOut,
		    SetComparator.IN, TimeModifier.LAST);
		CodedObsCohortDefinition exitedCareWithDefaultedStatus = Cohorts.createCodedObsCohortDefinition(
		    "exitedCareWithDefaultedStatus", onOrAfterOnOrBefore, reasonForExitingCare, patientDefaulted, SetComparator.IN,
		    TimeModifier.LAST);
		
		SqlCohortDefinition patientsWithhivTransferVisit = new SqlCohortDefinition();
		patientsWithhivTransferVisit.setQuery("select distinct patient_id from encounter where encounter_type="
		        + patientTransferEncounterType.getId() + " and (form_id=" + transferinForm.getFormId()
		        + ") and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
		patientsWithhivTransferVisit.setName("patientsWithhivTransferVisit");
		patientsWithhivTransferVisit.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientsWithhivTransferVisit.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		CompositionCohortDefinition everEnrolledInHIVCare = new CompositionCohortDefinition();
		everEnrolledInHIVCare.setName("TR: everEnrolledInHIVCare");
		everEnrolledInHIVCare.getSearches().put("1", new Mapped<CohortDefinition>(onARTStateInPMTCTClinic, null));
		everEnrolledInHIVCare.getSearches().put("2", new Mapped<CohortDefinition>(onFollowingStateHIVClinic, null));
		everEnrolledInHIVCare.setCompositionString("1 OR 2");
		
		//Art States
		List<ProgramWorkflowState> onARTstates = new ArrayList<ProgramWorkflowState>();
		onARTstates.add(adultOnART);
		onARTstates.add(pediOnART);
		InStateCohortDefinition onARTstatesStateCohort = Cohorts.createInCurrentState("onARTstatesStateCohort", onARTstates,
		    "onDate");
		CompositionCohortDefinition onARTStateHIVClinic = new CompositionCohortDefinition();
		onARTStateHIVClinic.setName("onARTStateHIVClinic");
		onARTStateHIVClinic.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(onARTstatesStateCohort, ParameterizableUtil
		            .createParameterMappings("onDate=${now}")));
		onARTStateHIVClinic.getSearches()
		        .put(
		            "2",
		            new Mapped<CohortDefinition>(inPediAndAdultprogram, ParameterizableUtil
		                    .createParameterMappings("onDate=${now}")));
		onARTStateHIVClinic.setCompositionString("1 AND 2");
		EncounterCohortDefinition patientTransferEncounter = Cohorts.createEncounterParameterizedByDate(
		    "patientTransferEncounter", onOrAfterOnOrBefore, patientTransferEncounterType);
		
		//------------------------------
		//     PRE-ART START
		//------------------------------ 
		
		//1.1 (NEW) Total number of males patients  (<10 years old) currently in Pre-ART
		AgeCohortDefinition patientWithUnder10 = Cohorts.createUnderAgeCohort("patientWithUnder10", 10);
		
		CompositionCohortDefinition malecurrentlyinPreArt = new CompositionCohortDefinition();
		malecurrentlyinPreArt.setName("malecurrentlyinPreArt");
		malecurrentlyinPreArt.addParameter(new Parameter("onDate", "onDate", Date.class));
		malecurrentlyinPreArt.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(onFollowingStateCohort, ParameterizableUtil
		            .createParameterMappings("onDate=${now}")));
		malecurrentlyinPreArt.getSearches().put("2", new Mapped<CohortDefinition>(maleCohort, null));
		malecurrentlyinPreArt.setCompositionString("1 AND 2");
		
		CompositionCohortDefinition malePreArtunder10 = new CompositionCohortDefinition();
		malePreArtunder10.setName("malePreArtunder10");
		malePreArtunder10.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(malecurrentlyinPreArt, ParameterizableUtil
		                    .createParameterMappings("onDate=${now}")));
		malePreArtunder10.getSearches().put("2", new Mapped<CohortDefinition>(patientWithUnder10, null));
		malePreArtunder10.setCompositionString("1 AND 2");
		CohortIndicator malePreArtunder10Ind = Indicators
		        .newCohortIndicator("malePreArtunder10Ind", malePreArtunder10, null);
		
		//1.2 (NEW) Total number of females patients  (<10 years old) currently in Pre-ART
		CompositionCohortDefinition femalecurrentlyinPreArt = new CompositionCohortDefinition();
		femalecurrentlyinPreArt.setName("femalecurrentlyinPreArt");
		femalecurrentlyinPreArt.addParameter(new Parameter("onDate", "onDate", Date.class));
		femalecurrentlyinPreArt.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(onFollowingStateCohort, ParameterizableUtil
		            .createParameterMappings("onDate=${now}")));
		femalecurrentlyinPreArt.getSearches().put("2", new Mapped<CohortDefinition>(femaleCohort, null));
		femalecurrentlyinPreArt.setCompositionString("1 AND 2");
		
		CompositionCohortDefinition femalePreArtunder10 = new CompositionCohortDefinition();
		femalePreArtunder10.setName("femalePreArtunder10");
		femalePreArtunder10.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(femalecurrentlyinPreArt, ParameterizableUtil
		            .createParameterMappings("onDate=${now}")));
		femalePreArtunder10.getSearches().put("2", new Mapped<CohortDefinition>(patientWithUnder10, null));
		femalePreArtunder10.setCompositionString("1 AND 2");
		CohortIndicator femalePreArtunder10Ind = Indicators.newCohortIndicator("femalePreArtunder10Ind",
		    femalePreArtunder10, null);
		
		// 1.3 (NEW) Total number of males patients  ( aged between 10-14 years) currently in Pre-ART
		// AgeCohortDefinition patientWith10To14=Cohorts.create10to14AgeCohort("patientWith10To14");
		AgeCohortDefinition patientWith10To14 = Cohorts.createXtoYAgeCohort("patientWith10To14", 10, 14);
		
		CompositionCohortDefinition malePreArtbetween10and14 = new CompositionCohortDefinition();
		malePreArtbetween10and14.setName("malePreArtbetween10and14");
		malePreArtbetween10and14.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(malecurrentlyinPreArt, ParameterizableUtil
		                    .createParameterMappings("onDate=${now}")));
		malePreArtbetween10and14.getSearches().put("2", new Mapped<CohortDefinition>(patientWith10To14, null));
		malePreArtbetween10and14.setCompositionString("1 AND 2");
		CohortIndicator malePreArtbetween10and14Ind = Indicators.newCohortIndicator("malePreArtbetween10and14Ind",
		    malePreArtbetween10and14, null);
		
		//1.4 (NEW) Total number of females patients  ( aged between 10-14 years) currently in Pre-ART
		CompositionCohortDefinition femalePreArtbetween10and14 = new CompositionCohortDefinition();
		femalePreArtbetween10and14.setName("femalePreArtbetween10and14");
		femalePreArtbetween10and14.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(femalecurrentlyinPreArt, ParameterizableUtil
		            .createParameterMappings("onDate=${now}")));
		femalePreArtbetween10and14.getSearches().put("2", new Mapped<CohortDefinition>(patientWith10To14, null));
		femalePreArtbetween10and14.setCompositionString("1 AND 2");
		CohortIndicator femalePreArtbetween10and14Ind = Indicators.newCohortIndicator("malePreArtbetween10and14Ind",
		    femalePreArtbetween10and14, null);
		
		//1.5 Total number of male  patients (aged between 15-19years )currently in Pre ART
		//AgeCohortDefinition patientWith15To19=Cohorts.create15to19AgeCohort("patientWith15To19");
		AgeCohortDefinition patientWith15To19 = Cohorts.createXtoYAgeCohort("patientWith10To14", 15, 19);
		
		CompositionCohortDefinition malePreArtbetween15and19 = new CompositionCohortDefinition();
		malePreArtbetween15and19.setName("malePreArtbetween15and19");
		malePreArtbetween15and19.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(malecurrentlyinPreArt, ParameterizableUtil
		                    .createParameterMappings("onDate=${now}")));
		malePreArtbetween15and19.getSearches().put("2", new Mapped<CohortDefinition>(patientWith15To19, null));
		malePreArtbetween15and19.setCompositionString("1 AND 2");
		CohortIndicator malePreArtbetween15and19Ind = Indicators.newCohortIndicator("malePreArtbetween15and19Ind",
		    malePreArtbetween15and19, null);
		
		//1.6 Total number of female  patients (aged between 15-19years )currently in Pre ART
		CompositionCohortDefinition femalePreArtbetween15and19 = new CompositionCohortDefinition();
		femalePreArtbetween15and19.setName("femalePreArtbetween15and19");
		femalePreArtbetween15and19.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(femalecurrentlyinPreArt, ParameterizableUtil
		            .createParameterMappings("onDate=${now}")));
		femalePreArtbetween15and19.getSearches().put("2", new Mapped<CohortDefinition>(patientWith15To19, null));
		femalePreArtbetween15and19.setCompositionString("1 AND 2");
		CohortIndicator femalePreArtbetween15and19Ind = Indicators.newCohortIndicator("femalePreArtbetween15and19Ind",
		    femalePreArtbetween15and19, null);
		
		//1.7 Total number of male patients ( aged of 20 and above) currently in Pre ART
		// AgeCohortDefinition patienOver20=Cohorts.createOver20AgeCohort("patienOver20");
		AgeCohortDefinition patienOver20 = Cohorts.createOverXAgeCohort("patienOver20", 20);
		
		CompositionCohortDefinition malePreArtbetweenabove20 = new CompositionCohortDefinition();
		malePreArtbetweenabove20.setName("malePreArtbetweenabove20");
		malePreArtbetweenabove20.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(malecurrentlyinPreArt, ParameterizableUtil
		                    .createParameterMappings("onDate=${now}")));
		malePreArtbetweenabove20.getSearches().put("2", new Mapped<CohortDefinition>(patienOver20, null));
		malePreArtbetweenabove20.setCompositionString("1 AND 2");
		CohortIndicator malePreArtbetweenabove20Ind = Indicators.newCohortIndicator("malePreArtbetweenabove20Ind",
		    malePreArtbetweenabove20, null);
		
		//1.8 Total number of female patients ( aged of 20 and above) currently in Pre ART
		CompositionCohortDefinition femalePreArtbetweenabove20 = new CompositionCohortDefinition();
		femalePreArtbetweenabove20.setName("femalePreArtbetweenabove20");
		femalePreArtbetweenabove20.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(femalecurrentlyinPreArt, ParameterizableUtil
		            .createParameterMappings("onDate=${now}")));
		femalePreArtbetweenabove20.getSearches().put("2", new Mapped<CohortDefinition>(patienOver20, null));
		femalePreArtbetweenabove20.setCompositionString("1 AND 2");
		CohortIndicator femalePreArtbetweenabove20Ind = Indicators.newCohortIndicator("femalePreArtbetweenabove20Ind",
		    femalePreArtbetweenabove20, null);
		
		//SqlCohortDefinition under18monthsAtEnrol = Cohorts.createUnder18monthsAtEnrollmentCohort("under18monthsAtEnrol", pediatrichivProgram);
		//SqlCohortDefinition under5YrsAtEnrol = Cohorts.createUnder5AtEnrollmentCohort("under5YrsAtEnrol", pediatrichivProgram);
		SqlCohortDefinition under15YrsAtEnrol = Cohorts.createUnder15AtEnrollmentCohort("under15YrsAtEnrol",
		    pediatrichivProgram);
		SqlCohortDefinition over15YrsAtEnrol = Cohorts.create15orOverAtEnrollmentCohort("over15YrsAtEnrol", adulthivProgram);
		// SqlCohortDefinition enrolledInAdultProgram = Cohorts.createPatientInProgramDuringTime("Enrolled In adult HIV Program");
		
		// 2.1 Total number of male patients(<15 years old) newly enrolled in Pre ART  this month
		InProgramCohortDefinition inPediHIVOnDateProgram = Cohorts.createInProgramParameterizableByDate(
		    "inPediHIVOnDateProgram", pediatrichivProgram, "onDate");
		InProgramCohortDefinition inAdultHIVOnDateProgram = Cohorts.createInProgramParameterizableByDate(
		    "inAdultHIVOnDateProgram", adulthivProgram, "onDate");
		InStateCohortDefinition onPreartStatePeriod = Cohorts.createInCurrentState("onPreartStatePeriod", OnFollowingstates,
		    onOrAfterOnOrBefore);
		
		CompositionCohortDefinition pedipreArtduringPeriod = new CompositionCohortDefinition();
		pedipreArtduringPeriod.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		pedipreArtduringPeriod.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		pedipreArtduringPeriod.setName("pedipreArtduringPeriod");
		pedipreArtduringPeriod.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(inPediHIVOnDateProgram, ParameterizableUtil
		            .createParameterMappings("onDate=${now}")));
		pedipreArtduringPeriod.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(onPreartStatePeriod, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		pedipreArtduringPeriod.setCompositionString("1 AND 2");
		
		CompositionCohortDefinition malespedinewlyEnrolledInPreart = new CompositionCohortDefinition();
		malespedinewlyEnrolledInPreart.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		malespedinewlyEnrolledInPreart.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		malespedinewlyEnrolledInPreart.setName("malespedinewlyEnrolledInPreart");
		malespedinewlyEnrolledInPreart.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(pedipreArtduringPeriod, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		malespedinewlyEnrolledInPreart.getSearches().put("2", new Mapped<CohortDefinition>(under15YrsAtEnrol, null));
		malespedinewlyEnrolledInPreart.getSearches().put("3", new Mapped<CohortDefinition>(maleCohort, null));
		malespedinewlyEnrolledInPreart.setCompositionString("1 AND 2 AND 3 ");
		CohortIndicator malespedinewlyEnrolledInPreartInd = Indicators.newCohortIndicator(
		    "malespedinewlyEnrolledInPreartInd", malespedinewlyEnrolledInPreart,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		malespedinewlyEnrolledInPreartInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		malespedinewlyEnrolledInPreartInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		// 2.2 Total number of female patients(<15 years old) newly enrolled in Pre ART  this month
		CompositionCohortDefinition femalespedinewlyEnrolledInPreart = new CompositionCohortDefinition();
		femalespedinewlyEnrolledInPreart.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femalespedinewlyEnrolledInPreart.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femalespedinewlyEnrolledInPreart.setName("femalespedinewlyEnrolledInPreart");
		femalespedinewlyEnrolledInPreart.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(pedipreArtduringPeriod, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		femalespedinewlyEnrolledInPreart.getSearches().put("2", new Mapped<CohortDefinition>(under15YrsAtEnrol, null));
		femalespedinewlyEnrolledInPreart.getSearches().put("3", new Mapped<CohortDefinition>(femaleCohort, null));
		femalespedinewlyEnrolledInPreart.setCompositionString("1 AND 2 AND 3 ");
		CohortIndicator femalespedinewlyEnrolledInPreartInd = Indicators.newCohortIndicator(
		    "femalespedinewlyEnrolledInPreartInd", femalespedinewlyEnrolledInPreart,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		femalespedinewlyEnrolledInPreartInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femalespedinewlyEnrolledInPreartInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		//2.3 Total number of male patients(15 years old and above) newly enrolled in Pre ART  this month
		CompositionCohortDefinition nonActivePatients = new CompositionCohortDefinition();
		nonActivePatients.setName("TR:nonActivePatients");
		nonActivePatients.getSearches().put("1", new Mapped<CohortDefinition>(exitedCareWithDeadStatus, null));
		nonActivePatients.getSearches().put("2", new Mapped<CohortDefinition>(exitedCareWithtransferStatus, null));
		nonActivePatients.getSearches().put("3", new Mapped<CohortDefinition>(exitedCareWithDefaultedStatus, null));
		nonActivePatients.setCompositionString("1 OR 2 OR 3");
		
		CompositionCohortDefinition adultpreArtduringPeriod = new CompositionCohortDefinition();
		adultpreArtduringPeriod.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		adultpreArtduringPeriod.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		adultpreArtduringPeriod.setName("adultpreArtduringPeriod");
		adultpreArtduringPeriod.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(inAdultHIVOnDateProgram, ParameterizableUtil
		            .createParameterMappings("onDate=${now}")));
		adultpreArtduringPeriod.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(onPreartStatePeriod, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		adultpreArtduringPeriod.setCompositionString("1 AND 2");
		
		CompositionCohortDefinition malesAdultnewlyEnrolledInPreart = new CompositionCohortDefinition();
		malesAdultnewlyEnrolledInPreart.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		malesAdultnewlyEnrolledInPreart.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		malesAdultnewlyEnrolledInPreart.setName("malesAdultnewlyEnrolledInPreart");
		malesAdultnewlyEnrolledInPreart.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(adultpreArtduringPeriod, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		malesAdultnewlyEnrolledInPreart.getSearches().put("2", new Mapped<CohortDefinition>(over15YrsAtEnrol, null));
		malesAdultnewlyEnrolledInPreart.getSearches().put("3", new Mapped<CohortDefinition>(maleCohort, null));
		malesAdultnewlyEnrolledInPreart.getSearches().put("4", new Mapped<CohortDefinition>(nonActivePatients, null));
		malesAdultnewlyEnrolledInPreart.setCompositionString("1 AND 2 AND 3 AND (NOT 4) ");
		CohortIndicator malesAdultnewlyEnrolledInPreartInd = Indicators.newCohortIndicator(
		    "malesAdultnewlyEnrolledInPreartInd", malesAdultnewlyEnrolledInPreart,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		malesAdultnewlyEnrolledInPreartInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		malesAdultnewlyEnrolledInPreartInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		//2.4 Total number of female patients(15 years old and above) newly enrolled in Pre ART  this month
		
		CompositionCohortDefinition femalesAdultnewlyEnrolledInPreart = new CompositionCohortDefinition();
		femalesAdultnewlyEnrolledInPreart.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femalesAdultnewlyEnrolledInPreart.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femalesAdultnewlyEnrolledInPreart.setName("femalesAdultnewlyEnrolledInPreart");
		femalesAdultnewlyEnrolledInPreart.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(adultpreArtduringPeriod, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		femalesAdultnewlyEnrolledInPreart.getSearches().put("2", new Mapped<CohortDefinition>(over15YrsAtEnrol, null));
		femalesAdultnewlyEnrolledInPreart.getSearches().put("3", new Mapped<CohortDefinition>(femaleCohort, null));
		femalesAdultnewlyEnrolledInPreart.getSearches().put("4", new Mapped<CohortDefinition>(nonActivePatients, null));
		femalesAdultnewlyEnrolledInPreart.setCompositionString("1 AND 2 AND 3 AND (NOT 4) ");
		CohortIndicator femalesAdultnewlyEnrolledInPreartInd = Indicators.newCohortIndicator(
		    "femalesAdultnewlyEnrolledInPreartInd", femalesAdultnewlyEnrolledInPreart,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		femalesAdultnewlyEnrolledInPreartInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femalesAdultnewlyEnrolledInPreartInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		//3 Total number of  patients ( Pre and ART)  on prophylaxis (cotrimoxazole & Dapsone) this month
		//ART + PreART states
		List<ProgramWorkflowState> onArtAndPreArtStates = new ArrayList<ProgramWorkflowState>();
		onArtAndPreArtStates.add(adultOnART);
		onArtAndPreArtStates.add(pediOnART);
		onArtAndPreArtStates.add(adultOnFollowing);
		onArtAndPreArtStates.add(pediOnFollowing);
		InStateCohortDefinition onARTandPreARTstatesStateCohort = Cohorts.createInCurrentState("onARTstatesStateCohort",
		    onArtAndPreArtStates, "onDate");
		SqlCohortDefinition startedCotrimoXazoleInPeriod = Cohorts.getPatientsOnRegimenBasedOnStartDateEndDate(
		    "startedCotrimoXazoleDuringP", cotrimoxazole);
		SqlCohortDefinition startedDapsoneinPeriod = Cohorts.getPatientsOnRegimenBasedOnStartDateEndDate("startedDapsone",
		    dapsone);
		
		CompositionCohortDefinition preArtAndARTOnCotrimoComp = new CompositionCohortDefinition();
		preArtAndARTOnCotrimoComp.setName("preArtAndARTOnCotrimoComp");
		preArtAndARTOnCotrimoComp.addParameter(new Parameter("startDate", "startDate", Date.class));
		preArtAndARTOnCotrimoComp.addParameter(new Parameter("endDate", "endDate", Date.class));
		preArtAndARTOnCotrimoComp.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(onARTandPreARTstatesStateCohort, ParameterizableUtil
		            .createParameterMappings("onDate=${now}")));
		preArtAndARTOnCotrimoComp.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(startedCotrimoXazoleInPeriod, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		preArtAndARTOnCotrimoComp.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(startedDapsoneinPeriod, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		preArtAndARTOnCotrimoComp.setCompositionString("1 AND (2 OR 3)");
		
		CohortIndicator allPatientsNewOnprophyInd = Indicators.newCohortIndicator("allPatientsNewOnprophyInd",
		    preArtAndARTOnCotrimoComp,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		//4 Number of patients newly enrolled screened for TB this month 
		CodedObsCohortDefinition patientsScreenedForTB = Cohorts.createCodedObsCohortDefinition("patientsScreenedForTB",
		    onOrAfterOnOrBefore, tbScreeningtest, null, SetComparator.IN, TimeModifier.LAST);
		CodedObsCohortDefinition tbScreenngPosTest = Cohorts.createCodedObsCohortDefinition("tbScreenngPosTest",
		    onOrAfterOnOrBefore, tbScreeningtest, positiveStatus, SetComparator.IN, TimeModifier.LAST);
		
		CompositionCohortDefinition screenedForTbInHIVProgramsComp = new CompositionCohortDefinition();
		screenedForTbInHIVProgramsComp.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		screenedForTbInHIVProgramsComp.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		screenedForTbInHIVProgramsComp.setName("screenedForTbInHIVProgramsComp");
		screenedForTbInHIVProgramsComp.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(pedipreArtduringPeriod, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		screenedForTbInHIVProgramsComp.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(adultpreArtduringPeriod, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		screenedForTbInHIVProgramsComp.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(patientsScreenedForTB, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		screenedForTbInHIVProgramsComp.setCompositionString("(1 OR 2) AND 3");
		CohortIndicator screenedForTbInHIVProgramsIndi = Indicators.newCohortIndicator("screenedForTbInHIVProgramsIndi",
		    screenedForTbInHIVProgramsComp,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrAfter}"));
		screenedForTbInHIVProgramsIndi.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		screenedForTbInHIVProgramsIndi.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		//5 Number of new patients screened positive for TB this month 
		CompositionCohortDefinition screenedPosTb = new CompositionCohortDefinition();
		screenedPosTb.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		screenedPosTb.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		screenedPosTb.setName("screenedPosTb");
		screenedPosTb.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(pedipreArtduringPeriod, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		screenedPosTb.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(adultpreArtduringPeriod, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		screenedPosTb.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(tbScreenngPosTest, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		screenedPosTb.setCompositionString("(1 OR 2) AND 3");
		CohortIndicator screenedPosTbInd = Indicators.newCohortIndicator("screenedPosTbInd", screenedPosTb,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrAfter}"));
		screenedPosTbInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		screenedPosTbInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		CompositionCohortDefinition screenedPosForTbInHIVProgramsComp = new CompositionCohortDefinition();
		screenedPosForTbInHIVProgramsComp.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		screenedPosForTbInHIVProgramsComp.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		screenedPosForTbInHIVProgramsComp.setName("screenedPosForTbInHIVProgramsComp");
		screenedPosForTbInHIVProgramsComp.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(pedipreArtduringPeriod, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		screenedPosForTbInHIVProgramsComp.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(adultpreArtduringPeriod, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		screenedPosForTbInHIVProgramsComp.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(tbScreenngPosTest, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		screenedPosForTbInHIVProgramsComp.setCompositionString("(1 OR 2) AND 3");
		CohortIndicator screenedPosForTbInHIVProgramsInd = Indicators.newCohortIndicator("screenedForTbInHIVProgramsIndi",
		    screenedPosForTbInHIVProgramsComp,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrAfter}"));
		screenedPosForTbInHIVProgramsInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		screenedPosForTbInHIVProgramsInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		//6 Number of PRE-ARV patients lost to follow up (> 3months)
		EncounterCohortDefinition clinicalEncWithoutLab = Cohorts.createEncounterParameterizedByDate(
		    "clinicalEncWithoutLab", onOrAfterOnOrBefore, clinicalEnountersIncLab);
		CompositionCohortDefinition ltfDuringPeriod = new CompositionCohortDefinition();
		ltfDuringPeriod.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		ltfDuringPeriod.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		ltfDuringPeriod.setName("ltfDuringPeriod");
		ltfDuringPeriod.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(clinicalEncWithoutLab, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		ltfDuringPeriod.setCompositionString("NOT 1");
		
		CompositionCohortDefinition patientsinHIVcareLostTofolowUp = new CompositionCohortDefinition();
		patientsinHIVcareLostTofolowUp.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsinHIVcareLostTofolowUp.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsinHIVcareLostTofolowUp.setName("patientsinHIVcareLostTofolowUp");
		patientsinHIVcareLostTofolowUp.getSearches().put("1", new Mapped<CohortDefinition>(onFollowingStateHIVClinic, null));
		patientsinHIVcareLostTofolowUp.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(ltfDuringPeriod, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientsinHIVcareLostTofolowUp.getSearches().put("3", new Mapped<CohortDefinition>(nonActivePatients, null));
		patientsinHIVcareLostTofolowUp.setCompositionString("1 AND 2 AND (NOT 3)");
		CohortIndicator patientsinHIVcareLostTofolowUpInd = Indicators.newCohortIndicator(
		    "patientsinHIVcareLostTofolowUpInd", patientsinHIVcareLostTofolowUp,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		patientsinHIVcareLostTofolowUpInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsinHIVcareLostTofolowUpInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		//7 Total number  of  patients  in Pre  ART lost to follow up retraced  this month
		SqlCohortDefinition lostbacktoProgramThismonth = new SqlCohortDefinition();
		lostbacktoProgramThismonth.setName("TR:lostbacktoProgramThismonth");
		lostbacktoProgramThismonth
		        .setQuery("select DISTINCT patient_id FROM encounter WHERE patient_id "
		                + "IN (select DISTINCT e.patient_id FROM encounter e, patient p WHERE e.patient_id=p.patient_id "
		                + "AND DATEDIFF(:startDate, e.encounter_datetime) >= 90 AND e.encounter_type IN (1,2,3,4,24,25) "
		                + "AND p.voided=0 AND e.voided=0) AND encounter_datetime >= :startDate AND encounter_datetime <= :endDate AND voided=0  ");
		lostbacktoProgramThismonth.addParameter(new Parameter("startDate", "startDate", Date.class));
		lostbacktoProgramThismonth.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		CompositionCohortDefinition patientsinLostAndBackToPRogramThismonth = new CompositionCohortDefinition();
		patientsinLostAndBackToPRogramThismonth.setName("patientsinLostAndBackToPRogramThismonth");
		patientsinLostAndBackToPRogramThismonth.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientsinLostAndBackToPRogramThismonth.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientsinLostAndBackToPRogramThismonth.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(lostbacktoProgramThismonth, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientsinLostAndBackToPRogramThismonth.getSearches().put("2",
		    new Mapped<CohortDefinition>(onFollowingStateHIVClinic, null));
		patientsinLostAndBackToPRogramThismonth.getSearches()
		        .put("3", new Mapped<CohortDefinition>(nonActivePatients, null));
		patientsinLostAndBackToPRogramThismonth.setCompositionString("1 AND 2 AND (NOT 3)");
		CohortIndicator patientsinLostAndBackToPRogramThismonthInd = Indicators.newCohortIndicator(
		    "patientsinLostAndBackToPRogramThismonth", patientsinLostAndBackToPRogramThismonth,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		//8 Total number of patients  in Pre ART who  are reported died this month
		SqlCohortDefinition pediOnpreArtBeforeExitedFromCare = Cohorts
		        .createArtOrPreArtAndActiveonPatientDiedORTransferedStateDuringPeriod("TR:pediOnpreArtBeforeExitedFromCare",
		            pediatrichivProgram, pediOnFollowing, pediPreAndArtDiedState);
		SqlCohortDefinition adultOnpreArtBeforeExitedFromCare = Cohorts
		        .createArtOrPreArtAndActiveonPatientDiedORTransferedStateDuringPeriod(
		            "TR:adultOnpreArtBeforeExitedFromCare", adulthivProgram, adultOnFollowing, adultPreAndArtDiedState);
		
		CompositionCohortDefinition patientsDiedandNotOnART = new CompositionCohortDefinition();
		patientsDiedandNotOnART.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientsDiedandNotOnART.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientsDiedandNotOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsDiedandNotOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsDiedandNotOnART.setName("patientsDiedandNotOnART");
		patientsDiedandNotOnART.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(pediOnpreArtBeforeExitedFromCare, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientsDiedandNotOnART.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(adultOnpreArtBeforeExitedFromCare, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientsDiedandNotOnART.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(exitedCareWithDeadStatus, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientsDiedandNotOnART.setCompositionString("(1 OR 2) AND 3 ");
		CohortIndicator patientsDiedandNotOnARTInd = Indicators
		        .newCohortIndicator(
		            "patientsDiedandNotOnARTInd",
		            patientsDiedandNotOnART,
		            ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		patientsDiedandNotOnARTInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsDiedandNotOnARTInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		//9 Number of patients ( all ages and both sex) in Pre ART transfered out this month 
		SqlCohortDefinition pediOnpreArtBeforeTranferedFromCare = Cohorts
		        .createArtOrPreArtAndActiveonPatientDiedORTransferedStateDuringPeriod(
		            "TR:pediOnpreArtBeforeTranferedFromCare", pediatrichivProgram, pediOnFollowing, peditransferedOutState);
		SqlCohortDefinition adultOnpreArtBeforeTransferedFromCare = Cohorts
		        .createArtOrPreArtAndActiveonPatientDiedORTransferedStateDuringPeriod(
		            "TR:adultOnpreArtBeforeTransferedFromCare", adulthivProgram, adultOnFollowing, adulttransferedOutState);
		
		CompositionCohortDefinition patientsTransferedoutAndnotOnART = new CompositionCohortDefinition();
		patientsTransferedoutAndnotOnART.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientsTransferedoutAndnotOnART.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientsTransferedoutAndnotOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsTransferedoutAndnotOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsTransferedoutAndnotOnART.setName("patientsTransferedoutAndnotOnART");
		patientsTransferedoutAndnotOnART.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(pediOnpreArtBeforeTranferedFromCare, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientsTransferedoutAndnotOnART.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(adultOnpreArtBeforeTransferedFromCare, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientsTransferedoutAndnotOnART.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(exitedCareWithtransferStatus, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientsTransferedoutAndnotOnART.setCompositionString("(1 OR 2) AND 3");
		CohortIndicator patientsTransferedoutAndnotOnARTInd = Indicators
		        .newCohortIndicator(
		            "patientsTransferedoutAndnotOnARTInd",
		            patientsTransferedoutAndnotOnART,
		            ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		patientsTransferedoutAndnotOnARTInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsTransferedoutAndnotOnARTInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		//10 Number of patients ( all ages and both sex) in Pre ART transfered out this month
		CompositionCohortDefinition patientsTransferedIntAndnotOnART = new CompositionCohortDefinition();
		patientsTransferedIntAndnotOnART.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientsTransferedIntAndnotOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsTransferedIntAndnotOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsTransferedIntAndnotOnART.setName("patientsTransferedIntAndnotOnART");
		patientsTransferedIntAndnotOnART.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientEnrolledInPediAndAdultProgram, ParameterizableUtil
		            .createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter}")));
		patientsTransferedIntAndnotOnART.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientTransferEncounter, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsTransferedIntAndnotOnART.getSearches().put("3", new Mapped<CohortDefinition>(onARTStateHIVClinic, null));
		patientsTransferedIntAndnotOnART.setCompositionString("1 AND 2 AND (NOT 3)");
		
		CohortIndicator patientsTransferedIntAndnotOnARTInd = Indicators
		        .newCohortIndicator(
		            "patientsTransferedIntAndnotOnARTInd",
		            patientsTransferedIntAndnotOnART,
		            ParameterizableUtil
		                    .createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}"));
		patientsTransferedIntAndnotOnARTInd
		        .addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientsTransferedIntAndnotOnARTInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsTransferedIntAndnotOnARTInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		// -------------------------------------------
		//       ART CATEGORY
		//-------------------------------------------
		
		InStateCohortDefinition onArtatEndDatePeriod = Cohorts.createInCurrentState("TR: started on Art", onARTstates,
		    onOrAfterOnOrBefore);
		CompositionCohortDefinition onARTStateatTheEnd = new CompositionCohortDefinition();
		onARTStateatTheEnd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		onARTStateatTheEnd.setName("onARTStateatTheEnd");
		onARTStateatTheEnd.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(onArtatEndDatePeriod, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore}")));
		onARTStateatTheEnd.getSearches()
		        .put(
		            "2",
		            new Mapped<CohortDefinition>(inPediAndAdultprogram, ParameterizableUtil
		                    .createParameterMappings("onDate=${now}")));
		onARTStateatTheEnd.setCompositionString("1 AND 2");
		
		List<ProgramWorkflowState> onArtatState = new ArrayList<ProgramWorkflowState>();
		OnFollowingstates.add(adultOnART);
		OnFollowingstates.add(pediOnART);
		InStateCohortDefinition onArtatStateCohort = Cohorts.createInCurrentState("TR:onFollowingStateCohort", onArtatState,
		    "onDate");
		
		//1.1 Total number of males patients (< 1 years old) currently on ART
		AgeCohortDefinition patientWithUnder1 = Cohorts.createUnderAgeCohort("patientWithUnder1", 1);
		CompositionCohortDefinition malecurrentlyinArt = new CompositionCohortDefinition();
		malecurrentlyinArt.setName("malecurrentlyinArt");
		malecurrentlyinArt.addParameter(new Parameter("onDate", "onDate", Date.class));
		malecurrentlyinArt.getSearches().put("1",
		    new Mapped<CohortDefinition>(onArtatStateCohort, ParameterizableUtil.createParameterMappings("onDate=${now}")));
		malecurrentlyinArt.getSearches().put("2", new Mapped<CohortDefinition>(maleCohort, null));
		malecurrentlyinArt.setCompositionString("1 AND 2");
		
		CompositionCohortDefinition maleArtunder1 = new CompositionCohortDefinition();
		maleArtunder1.setName("maleArtunder1");
		maleArtunder1.getSearches().put("1",
		    new Mapped<CohortDefinition>(malecurrentlyinArt, ParameterizableUtil.createParameterMappings("onDate=${now}")));
		maleArtunder1.getSearches().put("2", new Mapped<CohortDefinition>(patientWithUnder10, null));
		maleArtunder1.setCompositionString("1 AND 2");
		CohortIndicator maleArtunder1Ind = Indicators.newCohortIndicator("maleArtunder1Ind", maleArtunder1, null);
		
		//1.2 Total number of females patients (< 1 years old) currently on ART
		CompositionCohortDefinition femalecurrentlyinArt = new CompositionCohortDefinition();
		femalecurrentlyinArt.setName("femalecurrentlyinArt");
		femalecurrentlyinArt.addParameter(new Parameter("onDate", "onDate", Date.class));
		femalecurrentlyinArt.getSearches().put("1",
		    new Mapped<CohortDefinition>(onArtatStateCohort, ParameterizableUtil.createParameterMappings("onDate=${now}")));
		femalecurrentlyinArt.getSearches().put("2", new Mapped<CohortDefinition>(femaleCohort, null));
		femalecurrentlyinArt.setCompositionString("1 AND 2");
		
		CompositionCohortDefinition femaleArtunder1 = new CompositionCohortDefinition();
		femaleArtunder1.setName("femaleArtunder1");
		femaleArtunder1.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(femalecurrentlyinArt, ParameterizableUtil
		                    .createParameterMappings("onDate=${now}")));
		femaleArtunder1.getSearches().put("2", new Mapped<CohortDefinition>(patientWithUnder10, null));
		femaleArtunder1.setCompositionString("1 AND 2");
		CohortIndicator femaleArtunder1Ind = Indicators.newCohortIndicator("femaleArtunder1Ind", femaleArtunder1, null);
		
		// 1.3 (NEW) Total number of males patients  ( aged between 1-4 years) currently in ART
		AgeCohortDefinition patientWith1To4 = Cohorts.createXtoYAgeCohort("patientWith1To4", 1, 4);
		
		CompositionCohortDefinition maleArtbetween1To4 = new CompositionCohortDefinition();
		maleArtbetween1To4.setName("maleArtbetween1To4");
		maleArtbetween1To4.getSearches().put("1",
		    new Mapped<CohortDefinition>(malecurrentlyinArt, ParameterizableUtil.createParameterMappings("onDate=${now}")));
		maleArtbetween1To4.getSearches().put("2", new Mapped<CohortDefinition>(patientWith1To4, null));
		maleArtbetween1To4.setCompositionString("1 AND 2");
		CohortIndicator malePreArtbetween1To4Ind = Indicators.newCohortIndicator("malePreArtbetween1To4Ind",
		    maleArtbetween1To4, null);
		
		//1.4 (NEW) Total number of females patients  ( aged between 1-4 years) currently in ART
		CompositionCohortDefinition femaleArtbetween1To4 = new CompositionCohortDefinition();
		femaleArtbetween1To4.setName("femaleArtbetween1To4");
		femaleArtbetween1To4.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(femalecurrentlyinArt, ParameterizableUtil
		                    .createParameterMappings("onDate=${now}")));
		femaleArtbetween1To4.getSearches().put("2", new Mapped<CohortDefinition>(patientWith1To4, null));
		femaleArtbetween1To4.setCompositionString("1 AND 2");
		CohortIndicator femalePreArtbetween1To4Ind = Indicators.newCohortIndicator("femalePreArtbetween1To4Ind",
		    femaleArtbetween1To4, null);
		
		//1.5 Total number of male  patients (aged between 5-9years )currently in ART
		AgeCohortDefinition patientWith5To9 = Cohorts.createXtoYAgeCohort("patientWith5To9", 5, 9);
		
		CompositionCohortDefinition maleArtbetween5To9 = new CompositionCohortDefinition();
		maleArtbetween5To9.setName("maleArtbetween5To9");
		maleArtbetween5To9.getSearches().put("1",
		    new Mapped<CohortDefinition>(malecurrentlyinArt, ParameterizableUtil.createParameterMappings("onDate=${now}")));
		maleArtbetween5To9.getSearches().put("2", new Mapped<CohortDefinition>(patientWith5To9, null));
		maleArtbetween5To9.setCompositionString("1 AND 2");
		CohortIndicator malePreArtbetween5To9Ind = Indicators.newCohortIndicator("malePreArtbetween5To9Ind",
		    maleArtbetween5To9, null);
		
		//1.6 Total number of female  patients (aged between 15-19years )currently in ART
		CompositionCohortDefinition femaleArtbetween5To9 = new CompositionCohortDefinition();
		femaleArtbetween5To9.setName("femaleArtbetween5To9");
		femaleArtbetween5To9.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(femalecurrentlyinArt, ParameterizableUtil
		                    .createParameterMappings("onDate=${now}")));
		femaleArtbetween5To9.getSearches().put("2", new Mapped<CohortDefinition>(patientWith5To9, null));
		femaleArtbetween5To9.setCompositionString("1 AND 2");
		CohortIndicator femalePreArtbetween5To9Ind = Indicators.newCohortIndicator("femalePreArtbetween5To9Ind",
		    femaleArtbetween5To9, null);
		
		// 1.7 (NEW) Total number of males patients  ( aged between 10-14 years) currently in ART
		CompositionCohortDefinition maleArtbetween10and14 = new CompositionCohortDefinition();
		maleArtbetween10and14.setName("maleArtbetween10and14");
		maleArtbetween10and14.getSearches().put("1",
		    new Mapped<CohortDefinition>(malecurrentlyinArt, ParameterizableUtil.createParameterMappings("onDate=${now}")));
		maleArtbetween10and14.getSearches().put("2", new Mapped<CohortDefinition>(patientWith10To14, null));
		maleArtbetween10and14.setCompositionString("1 AND 2");
		CohortIndicator maleArtbetween10and14Ind = Indicators.newCohortIndicator("maleArtbetween10and14Ind",
		    maleArtbetween10and14, null);
		
		//1.8 (NEW) Total number of females patients  ( aged between 10-14 years) currently in ART
		CompositionCohortDefinition femaleArtbetween10and14 = new CompositionCohortDefinition();
		femaleArtbetween10and14.setName("femaleArtbetween10and14");
		femaleArtbetween10and14.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(femalecurrentlyinArt, ParameterizableUtil
		                    .createParameterMappings("onDate=${now}")));
		femaleArtbetween10and14.getSearches().put("2", new Mapped<CohortDefinition>(patientWith10To14, null));
		femaleArtbetween10and14.setCompositionString("1 AND 2");
		CohortIndicator femaleArtbetween10and14Ind = Indicators.newCohortIndicator("femaleArtbetween10and14Ind",
		    femaleArtbetween10and14, null);
		
		//1.9 Total number of male  patients (aged between 15-19years )currently in ART
		CompositionCohortDefinition maleArtbetween15and19 = new CompositionCohortDefinition();
		maleArtbetween15and19.setName("maleArtbetween15and19");
		maleArtbetween15and19.getSearches().put("1",
		    new Mapped<CohortDefinition>(malecurrentlyinArt, ParameterizableUtil.createParameterMappings("onDate=${now}")));
		maleArtbetween15and19.getSearches().put("2", new Mapped<CohortDefinition>(patientWith15To19, null));
		maleArtbetween15and19.setCompositionString("1 AND 2");
		CohortIndicator maleArtbetween15and19Ind = Indicators.newCohortIndicator("maleArtbetween15and19Ind",
		    maleArtbetween15and19, null);
		
		//1.10 Total number of female  patients (aged between 15-19years )currently in ART
		CompositionCohortDefinition femaleArtbetween15and19 = new CompositionCohortDefinition();
		femaleArtbetween15and19.setName("femaleArtbetween15and19");
		femaleArtbetween15and19.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(femalecurrentlyinArt, ParameterizableUtil
		                    .createParameterMappings("onDate=${now}")));
		femaleArtbetween15and19.getSearches().put("2", new Mapped<CohortDefinition>(patientWith15To19, null));
		femaleArtbetween15and19.setCompositionString("1 AND 2");
		CohortIndicator femaleArtbetween15and19Ind = Indicators.newCohortIndicator("femaleArtbetween15and19Ind",
		    femaleArtbetween15and19, null);
		
		//1.11 Total number of male patients ( aged of 20 and above) currently in ART
		CompositionCohortDefinition maleArtbetweenabove20 = new CompositionCohortDefinition();
		maleArtbetweenabove20.setName("maleArtbetweenabove20");
		maleArtbetweenabove20.getSearches().put("1",
		    new Mapped<CohortDefinition>(malecurrentlyinArt, ParameterizableUtil.createParameterMappings("onDate=${now}")));
		maleArtbetweenabove20.getSearches().put("2", new Mapped<CohortDefinition>(patienOver20, null));
		maleArtbetweenabove20.setCompositionString("1 AND 2");
		CohortIndicator maleArtbetweenabove20Ind = Indicators.newCohortIndicator("maleArtbetweenabove20Ind",
		    maleArtbetweenabove20, null);
		
		//1.12 Total number of female patients ( aged of 20 and above) currently in  ART
		CompositionCohortDefinition femaleArtbetweenabove20 = new CompositionCohortDefinition();
		femaleArtbetweenabove20.setName("femaleArtbetweenabove20");
		femaleArtbetweenabove20.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(femalecurrentlyinArt, ParameterizableUtil
		                    .createParameterMappings("onDate=${now}")));
		femaleArtbetweenabove20.getSearches().put("2", new Mapped<CohortDefinition>(patienOver20, null));
		femaleArtbetweenabove20.setCompositionString("1 AND 2");
		CohortIndicator femaleArtbetweenabove20Ind = Indicators.newCohortIndicator("femaleArtbetweenabove20Ind",
		    femaleArtbetweenabove20, null);
		
		//2.1 Total number of pediatric patients who are on First Line Regimen
		CompositionCohortDefinition patientOnArtOrPMTCTPrograms = new CompositionCohortDefinition();
		patientOnArtOrPMTCTPrograms.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientOnArtOrPMTCTPrograms.setName("patientOnArtOrPMTCTPrograms");
		patientOnArtOrPMTCTPrograms.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(onARTStateatTheEnd, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore}")));
		patientOnArtOrPMTCTPrograms.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(inPmtctPregnancyprogram, ParameterizableUtil
		            .createParameterMappings("onDate=${now}")));
		patientOnArtOrPMTCTPrograms.getSearches()
		        .put(
		            "3",
		            new Mapped<CohortDefinition>(inPmtctMotherprogram, ParameterizableUtil
		                    .createParameterMappings("onDate=${now}")));
		patientOnArtOrPMTCTPrograms.setCompositionString("1 OR 2 OR 3");
		
		SqlCohortDefinition onCurrentKaletraDrugOrder = Cohorts.getPatientsOnCurrentRegimenBasedOnEndDate(
		    "onCurrentKaletraDrugOrder", kaletra);
		CompositionCohortDefinition notOnCurrentKaletraDrugOrder = new CompositionCohortDefinition();
		notOnCurrentKaletraDrugOrder.setName("notOnCurrentKaletraDrugOrder");
		notOnCurrentKaletraDrugOrder.addParameter(new Parameter("endDate", "endDate", Date.class));
		notOnCurrentKaletraDrugOrder.addParameter(new Parameter("dateborn", "dateborn", Date.class));
		notOnCurrentKaletraDrugOrder.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		notOnCurrentKaletraDrugOrder.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientOnArtOrPMTCTPrograms, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore}")));
		notOnCurrentKaletraDrugOrder.getSearches()
		        .put(
		            "2",
		            new Mapped<CohortDefinition>(under15inDays, ParameterizableUtil
		                    .createParameterMappings("dateborn=${dateborn}")));
		notOnCurrentKaletraDrugOrder.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(onCurrentKaletraDrugOrder, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")));
		notOnCurrentKaletraDrugOrder.setCompositionString("1 AND 2 AND (NOT 3)");
		CohortIndicator notOnCurrentKaletraDrugOrderInd = Indicators.newCohortIndicator("notOnCurrentKaletraDrugOrderInd",
		    notOnCurrentKaletraDrugOrder,
		    ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},endDate=${endDate},dateborn=${dateborn}"));
		notOnCurrentKaletraDrugOrderInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		notOnCurrentKaletraDrugOrderInd.addParameter(new Parameter("dateborn", "dateborn", Date.class));
		
		//2.2 Total number of pediatric patients who are on Second Line Regimen 
		CompositionCohortDefinition onCurrentKaletraDrugOrderComp = new CompositionCohortDefinition();
		onCurrentKaletraDrugOrderComp.setName("activeOnCurrentKaletraDrugOrderComp");
		onCurrentKaletraDrugOrderComp.addParameter(new Parameter("endDate", "endDate", Date.class));
		onCurrentKaletraDrugOrderComp.addParameter(new Parameter("dateborn", "dateborn", Date.class));
		onCurrentKaletraDrugOrderComp.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		onCurrentKaletraDrugOrderComp.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientOnArtOrPMTCTPrograms, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore}")));
		onCurrentKaletraDrugOrderComp.getSearches()
		        .put(
		            "2",
		            new Mapped<CohortDefinition>(under15inDays, ParameterizableUtil
		                    .createParameterMappings("dateborn=${dateborn}")));
		onCurrentKaletraDrugOrderComp.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(onCurrentKaletraDrugOrder, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")));
		onCurrentKaletraDrugOrderComp.setCompositionString("1 AND 2 AND 3");
		CohortIndicator activeOnCurrentKaletraDrugOrderInd = Indicators.newCohortIndicator(
		    "activeOnCurrentKaletraDrugOrderInd", onCurrentKaletraDrugOrderComp,
		    ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},dateborn=${dateborn},endDate=${endDate}"));
		activeOnCurrentKaletraDrugOrderInd.addParameter(new Parameter("dateborn", "dateborn", Date.class));
		activeOnCurrentKaletraDrugOrderInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		//2.3 Total number of pediatric patients who are on Third Line Regimen 
		// 2.4 Total number of patients aged between 10-19 on ART 1st line regimen 
		AgeCohortDefinition patientWith10To19 = Cohorts.createXtoYAgeCohort("patientWith10To19", 10, 19);
		
		CompositionCohortDefinition patients10and19on1stLine = new CompositionCohortDefinition();
		patients10and19on1stLine.setName("patients10and19on1stLine");
		patients10and19on1stLine.addParameter(new Parameter("endDate", "endDate", Date.class));
		patients10and19on1stLine.getSearches().put("1", new Mapped<CohortDefinition>(onARTStateHIVClinic, null));
		patients10and19on1stLine.getSearches().put("2", new Mapped<CohortDefinition>(patientWith10To19, null));
		patients10and19on1stLine.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(onCurrentKaletraDrugOrder, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")));
		patients10and19on1stLine.setCompositionString("1 AND 2 AND (NOT 3)");
		CohortIndicator patients10and19on1stLineInd = Indicators.newCohortIndicator("patients10and19on1stLineInd",
		    patients10and19on1stLine, ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
		
		//2.5 Total number of patients aged between 10-19 on ART Second Line Regimen
		CompositionCohortDefinition patients10and19on2ndLine = new CompositionCohortDefinition();
		patients10and19on2ndLine.setName("patients10and19on2ndLine");
		patients10and19on2ndLine.addParameter(new Parameter("endDate", "endDate", Date.class));
		patients10and19on2ndLine.getSearches().put("1", new Mapped<CohortDefinition>(onARTStateHIVClinic, null));
		patients10and19on2ndLine.getSearches().put("2", new Mapped<CohortDefinition>(patientWith10To19, null));
		patients10and19on2ndLine.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(onCurrentKaletraDrugOrder, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")));
		patients10and19on2ndLine.setCompositionString("1 AND 2 AND 3");
		CohortIndicator patients10and19on2ndLineInd = Indicators.newCohortIndicator("patients10and19on2ndLineInd",
		    patients10and19on2ndLine, ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
		
		//2.6 Total number of patient aged 10-19 on ART Third Line Regimen 
		//2.7 Total number of adult patients who are on First Line Regimen
		CompositionCohortDefinition adultsnotOnCurrentKaletraDrugOrder = new CompositionCohortDefinition();
		adultsnotOnCurrentKaletraDrugOrder.setName("adultsnotOnCurrentKaletraDrugOrder");
		adultsnotOnCurrentKaletraDrugOrder.addParameter(new Parameter("endDate", "endDate", Date.class));
		adultsnotOnCurrentKaletraDrugOrder.addParameter(new Parameter("dateborn", "dateborn", Date.class));
		adultsnotOnCurrentKaletraDrugOrder.getSearches().put("1", new Mapped<CohortDefinition>(onARTStateHIVClinic, null));
		adultsnotOnCurrentKaletraDrugOrder.getSearches().put("2",
		    new Mapped<CohortDefinition>(over15inDays, ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
		adultsnotOnCurrentKaletraDrugOrder.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(onCurrentKaletraDrugOrder, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")));
		adultsnotOnCurrentKaletraDrugOrder.setCompositionString("1 AND 2 AND (NOT 3)");
		CohortIndicator notoadultsOnCurrentKaletraDrugOrderInd = Indicators.newCohortIndicator(
		    "notoadultsOnCurrentKaletraDrugOrderInd", adultsnotOnCurrentKaletraDrugOrder,
		    ParameterizableUtil.createParameterMappings("dateborn=${dateborn},endDate=${endDate}"));
		notoadultsOnCurrentKaletraDrugOrderInd.addParameter(new Parameter("dateborn", "dateborn", Date.class));
		
		//2.8 Total number of adult patients who are on Second Line Regimen
		CompositionCohortDefinition adultonCurrentKaletraDrugOrderCompo = new CompositionCohortDefinition();
		adultonCurrentKaletraDrugOrderCompo.setName("adultonCurrentKaletraDrugOrderCompo");
		adultonCurrentKaletraDrugOrderCompo.addParameter(new Parameter("endDate", "endDate", Date.class));
		adultonCurrentKaletraDrugOrderCompo.addParameter(new Parameter("dateborn", "dateborn", Date.class));
		adultonCurrentKaletraDrugOrderCompo.getSearches().put("1", new Mapped<CohortDefinition>(onARTStateHIVClinic, null));
		adultonCurrentKaletraDrugOrderCompo.getSearches().put("2",
		    new Mapped<CohortDefinition>(over15inDays, ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
		adultonCurrentKaletraDrugOrderCompo.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(onCurrentKaletraDrugOrder, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")));
		adultonCurrentKaletraDrugOrderCompo.setCompositionString("1 AND 2 AND 3");
		CohortIndicator adultonCurrentKaletraDrugOrderCompoInd = Indicators.newCohortIndicator(
		    "adultonCurrentKaletraDrugOrderCompoInd", adultonCurrentKaletraDrugOrderCompo,
		    ParameterizableUtil.createParameterMappings("dateborn=${dateborn},endDate=${endDate}"));
		adultonCurrentKaletraDrugOrderCompoInd.addParameter(new Parameter("dateborn", "dateborn", Date.class));
		
		//2.9 Total number of adult patients who are on Third Line Regimen
		//3.1 Number of  new pediatric patients (<15 years old) who initiated ART this month
		SqlCohortDefinition under15yearsAtstartOfArt = Cohorts.createUnder15yrsAtStartOfArtbyStartEndDate(
		    "TR:under15yearsAtstartOfArt", pediatrichivProgram, pediOnART);
		CohortIndicator femalesPedsNotOnArtStateNotOnFolowingInd = Indicators.newCohortIndicator(
		    "femalesPedsNotOnArtStateNotOnFolowingInd", under15yearsAtstartOfArt,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		//3.2 Number of new adult patients (15 years and above) who initiated ART this month
		SqlCohortDefinition over15yearsAtstartOfArtAd = Cohorts.createOver15yrsAtStartOfArtbyStartEndDate(
		    "TR:over15yearsAtstartOfArtAd", adulthivProgram, adultOnART);
		SqlCohortDefinition over15yearsAtstartOfArtPMTCT = Cohorts.createOver15yrsAtStartOfArtbyStartEndDate(
		    "TR:over15yearsAtstartOfArtPMTCT", pmtctPregnancyProgram, pmtctOnART);
		SqlCohortDefinition patientstakingARTfortheFirstTimes = Cohorts
		        .getPatientsWithFirstDrugOrdersOnlyDurindStartEndDate("patientstakingARTfortheFirstTimes");
		CompositionCohortDefinition patientneverTakenorStoppedOnARTBeforeComp = new CompositionCohortDefinition();
		patientneverTakenorStoppedOnARTBeforeComp.setName("TR:patientneverTakenorStoppedOnARTBeforeComp");
		patientneverTakenorStoppedOnARTBeforeComp.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientneverTakenorStoppedOnARTBeforeComp.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientneverTakenorStoppedOnARTBeforeComp.getSearches().put("1",
		    new Mapped(inPmtctMotherprogram, ParameterizableUtil.createParameterMappings("onDate=${now}")));
		patientneverTakenorStoppedOnARTBeforeComp.getSearches().put(
		    "2",
		    new Mapped(patientstakingARTfortheFirstTimes, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrAfter}")));
		patientneverTakenorStoppedOnARTBeforeComp.setCompositionString("1 AND 2 ");
		
		CompositionCohortDefinition adultsEnrolledInPMTCNewToArtorMotherProg = new CompositionCohortDefinition();
		adultsEnrolledInPMTCNewToArtorMotherProg.setName("TR:adultsEnrolledInPMTCTHIVforTheFirstTime");
		adultsEnrolledInPMTCNewToArtorMotherProg.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		adultsEnrolledInPMTCNewToArtorMotherProg.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		adultsEnrolledInPMTCNewToArtorMotherProg.addParameter(new Parameter("startDate", "startDate", Date.class));
		adultsEnrolledInPMTCNewToArtorMotherProg.addParameter(new Parameter("endDate", "endDate", Date.class));
		adultsEnrolledInPMTCNewToArtorMotherProg.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(over15yearsAtstartOfArtAd, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		adultsEnrolledInPMTCNewToArtorMotherProg.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(over15yearsAtstartOfArtPMTCT, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		adultsEnrolledInPMTCNewToArtorMotherProg.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(patientneverTakenorStoppedOnARTBeforeComp, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrAfter}")));
		adultsEnrolledInPMTCNewToArtorMotherProg.setCompositionString("(1 OR 2 OR 3");
		CohortIndicator adultsEnrolledInPMTCNewToArtorMotherProgInd = Indicators
		        .newCohortIndicator(
		            "adultsEnrolledInPMTCNewToArtorMotherProgInd",
		            adultsEnrolledInPMTCNewToArtorMotherProg,
		            ParameterizableUtil
		                    .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrAfter},startDate=${startDate},endDate=${endDate}"));
		adultsEnrolledInPMTCNewToArtorMotherProgInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		adultsEnrolledInPMTCNewToArtorMotherProgInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		// 4 Number of patients on ART who died this month
		SqlCohortDefinition pediOnArtBeforeExitedFromCare = Cohorts
		        .createArtOrPreArtAndActiveonPatientDiedORTransferedStateDuringPeriod("TR:pediOnArtBeforeExitedFromCare",
		            pediatrichivProgram, pediOnART, pediPreAndArtDiedState);
		SqlCohortDefinition adultOnArtBeforeExitedFromCare = Cohorts
		        .createArtOrPreArtAndActiveonPatientDiedORTransferedStateDuringPeriod("TR:adultOnArtBeforeExitedFromCare",
		            adulthivProgram, adultOnART, adultPreAndArtDiedState);
		
		CompositionCohortDefinition patientsDiedNotOnART = new CompositionCohortDefinition();
		patientsDiedNotOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsDiedNotOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsDiedNotOnART.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientsDiedNotOnART.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientsDiedNotOnART.setName("patientsDiedNotOnART");
		patientsDiedNotOnART.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(pediOnArtBeforeExitedFromCare, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientsDiedNotOnART.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(adultOnArtBeforeExitedFromCare, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientsDiedNotOnART.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(exitedCareWithDeadStatus, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientsDiedNotOnART.setCompositionString("(1 OR 2) AND 3  ");
		
		CohortIndicator patientsDiedNotOnARTInd = Indicators
		        .newCohortIndicator(
		            "patientsDiedNotOnARTInd",
		            patientsDiedNotOnART,
		            ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		patientsDiedNotOnARTInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsDiedNotOnARTInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		
		//5.1 Number of pediatric male patients (<15years old)  currently on ART who have been initiated  to TB treatment this month 
		SqlCohortDefinition onTBDrugs = Cohorts.geOnTBDrugsByStartEndDate("onTBDrugs");
		CompositionCohortDefinition malesPediCurentlyinARTNewtoTB = new CompositionCohortDefinition();
		malesPediCurentlyinARTNewtoTB.addParameter(new Parameter("startDate", "startDate", Date.class));
		malesPediCurentlyinARTNewtoTB.addParameter(new Parameter("endDate", "endDate", Date.class));
		malesPediCurentlyinARTNewtoTB.setName("malesPediCurentlyinARTNewtoTB");
		malesPediCurentlyinARTNewtoTB.getSearches().put("1",
		    new Mapped<CohortDefinition>(malecurrentlyinArt, ParameterizableUtil.createParameterMappings("onDate=${now}")));
		malesPediCurentlyinARTNewtoTB.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(onTBDrugs, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		malesPediCurentlyinARTNewtoTB.getSearches().put("3", new Mapped<CohortDefinition>(under15YrsAtEnrol, null));
		malesPediCurentlyinARTNewtoTB.setCompositionString("1 AND 2 AND 3");
		CohortIndicator malesPediCurentlyinARTNewtoTBInd = Indicators.newCohortIndicator("malesPediCurentlyinARTNewtoTBInd",
		    malesPediCurentlyinARTNewtoTB,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		//5.2 Number of pediatric female patients (<15years old)  currently on ART who have been initiated  to TB treatment this month 
		CompositionCohortDefinition femalesPediCurentlyinARTNewtoTB = new CompositionCohortDefinition();
		femalesPediCurentlyinARTNewtoTB.addParameter(new Parameter("startDate", "startDate", Date.class));
		femalesPediCurentlyinARTNewtoTB.addParameter(new Parameter("endDate", "endDate", Date.class));
		femalesPediCurentlyinARTNewtoTB.setName("femalesPediCurentlyinARTNewtoTB");
		femalesPediCurentlyinARTNewtoTB.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(femalecurrentlyinArt, ParameterizableUtil
		                    .createParameterMappings("onDate=${now}")));
		femalesPediCurentlyinARTNewtoTB.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(onTBDrugs, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalesPediCurentlyinARTNewtoTB.getSearches().put("3", new Mapped<CohortDefinition>(under15YrsAtEnrol, null));
		femalesPediCurentlyinARTNewtoTB.setCompositionString("1 AND 2 AND 3");
		CohortIndicator femalesPediCurentlyinARTNewtoTBInd = Indicators.newCohortIndicator(
		    "femalesPediCurentlyinARTNewtoTBInd", femalesPediCurentlyinARTNewtoTB,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		//5.3 Number of adult male patients (aged of 15 and above)  currently on ART who have been initiated  to TB treatment this month 
		CompositionCohortDefinition malesAdultsCurentlyinARTNewtoTB = new CompositionCohortDefinition();
		malesAdultsCurentlyinARTNewtoTB.addParameter(new Parameter("startDate", "startDate", Date.class));
		malesAdultsCurentlyinARTNewtoTB.addParameter(new Parameter("endDate", "endDate", Date.class));
		malesAdultsCurentlyinARTNewtoTB.setName("malesAdultsCurentlyinARTNewtoTB");
		malesAdultsCurentlyinARTNewtoTB.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(femalecurrentlyinArt, ParameterizableUtil
		                    .createParameterMappings("onDate=${now}")));
		malesAdultsCurentlyinARTNewtoTB.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(onTBDrugs, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		malesAdultsCurentlyinARTNewtoTB.getSearches().put("3", new Mapped<CohortDefinition>(over15YrsAtEnrol, null));
		malesAdultsCurentlyinARTNewtoTB.setCompositionString("1 AND 2 AND 3");
		CohortIndicator malesAdultsCurentlyinARTNewtoTBInd = Indicators.newCohortIndicator(
		    "malesAdultsCurentlyinARTNewtoTBInd", malesAdultsCurentlyinARTNewtoTB,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		//5.4 Number of adult female patients (aged of 15 and above)  currently on ART who have been initiated  to TB treatment this month 
		CompositionCohortDefinition femalesAdultsCurentlyinARTNewtoTB = new CompositionCohortDefinition();
		femalesAdultsCurentlyinARTNewtoTB.addParameter(new Parameter("startDate", "startDate", Date.class));
		femalesAdultsCurentlyinARTNewtoTB.addParameter(new Parameter("endDate", "endDate", Date.class));
		femalesAdultsCurentlyinARTNewtoTB.setName("femalesAdultsCurentlyinARTNewtoTB");
		femalesAdultsCurentlyinARTNewtoTB.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(femalecurrentlyinArt, ParameterizableUtil
		                    .createParameterMappings("onDate=${now}")));
		femalesAdultsCurentlyinARTNewtoTB.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(onTBDrugs, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalesAdultsCurentlyinARTNewtoTB.getSearches().put("3", new Mapped<CohortDefinition>(over15YrsAtEnrol, null));
		femalesAdultsCurentlyinARTNewtoTB.setCompositionString("1 AND 2 AND 3");
		CohortIndicator femalesAdultsCurentlyinARTNewtoTBInd = Indicators.newCohortIndicator(
		    "femalesAdultsCurentlyinARTNewtoTBInd", femalesAdultsCurentlyinARTNewtoTB,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		// 6 Number of patients on ART lost to follow up (>3 months) this month
		EncounterCohortDefinition patientWithHIVEncountersDates = Cohorts.createEncounterParameterizedByDate(
		    "patientWithHIVEncounters", onOrAfterDateOnOrBeforeDate, pediAdnAdultEncounters);
		CompositionCohortDefinition artLostAdActive = new CompositionCohortDefinition();
		artLostAdActive.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		artLostAdActive.addParameter(new Parameter("onOrAfterDate", "onOrAfterDate", Date.class));
		artLostAdActive.addParameter(new Parameter("onOrBeforeDate", "onOrBeforeDate", Date.class));
		artLostAdActive.setName("artLostAdActive");
		artLostAdActive.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(onARTStateatTheEnd, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore}")));
		artLostAdActive.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientWithHIVEncountersDates, ParameterizableUtil
		            .createParameterMappings("onOrAfterDate=${onOrAfterDate-3m},onOrBeforeDate=${onOrBeforeDate}")));
		artLostAdActive.getSearches().put("3", new Mapped<CohortDefinition>(nonActivePatients, null));
		artLostAdActive.setCompositionString("1 AND 2 AND (NOT 3) ");
		
		CohortIndicator pedsOnArtLostAndwithHIVFormsInd = Indicators
		        .newCohortIndicator(
		            "pedsOnArtLostAndwithHIVFormsInd",
		            artLostAdActive,
		            ParameterizableUtil
		                    .createParameterMappings("onOrAfterDate=${onOrAfterDate-3m},onOrBeforeDate=${onOrBeforeDate},onOrBefore=${onOrBefore}"));
		pedsOnArtLostAndwithHIVFormsInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		pedsOnArtLostAndwithHIVFormsInd.addParameter(new Parameter("onOrAfterDate", "onOrAfterDate", Date.class));
		pedsOnArtLostAndwithHIVFormsInd.addParameter(new Parameter("onOrBeforeDate", "onOrBeforeDate", Date.class));
		
		//7 Number of patients on ART lost to follow up (> 3months) retraced this program
		SqlCohortDefinition lostbacktoProgramThismonthinART = Cohorts
		        .createPatientBackToProgramThisYear("TR:lostbacktoProgramThismonthinART");
		
		CompositionCohortDefinition patientsinLostAndBackToProgramThismonthART = new CompositionCohortDefinition();
		patientsinLostAndBackToProgramThismonthART.setName("patientsinLostAndBackToProgramThismonthART");
		patientsinLostAndBackToProgramThismonthART.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientsinLostAndBackToProgramThismonthART.addParameter(new Parameter("endDate", "endDate", Date.class));
		//patientsinLostAndBackToProgramThismonthART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsinLostAndBackToProgramThismonthART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsinLostAndBackToProgramThismonthART.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(lostbacktoProgramThismonthinART, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientsinLostAndBackToProgramThismonthART.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(onARTStateatTheEnd, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore}")));
		patientsinLostAndBackToProgramThismonthART.setCompositionString("1 AND 2");
		CohortIndicator patientsinLostAndBackToProgramThismonthARTInd = Indicators.newCohortIndicator(
		    "patientsinLostAndBackToProgramThismonthARTInd", patientsinLostAndBackToProgramThismonthART, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},startDate=${startDate},endDate=${endDate}"));
		patientsinLostAndBackToProgramThismonthARTInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsinLostAndBackToProgramThismonthARTInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		//8 Number of  patients (all ages and both sex) on ART transfered out this  month 
		SqlCohortDefinition pediOnArtBeforeTranferedFromCare = Cohorts
		        .createArtOrPreArtAndActiveonPatientDiedORTransferedStateDuringPeriod("pediOnArtBeforeTranferedFromCare",
		            pediatrichivProgram, pediOnART, peditransferedOutState);
		SqlCohortDefinition adultOnArtBeforeTransferedFromCare = Cohorts
		        .createArtOrPreArtAndActiveonPatientDiedORTransferedStateDuringPeriod("adultOnArtBeforeTransferedFromCare",
		            adulthivProgram, adultOnART, adulttransferedOutState);
		
		CompositionCohortDefinition patientArtTrasferedOut = new CompositionCohortDefinition();
		patientArtTrasferedOut.setName("patientArtTrasferedOut");
		patientArtTrasferedOut.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientArtTrasferedOut.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientArtTrasferedOut.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientArtTrasferedOut.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientArtTrasferedOut.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(pediOnArtBeforeTranferedFromCare, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientArtTrasferedOut.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(adultOnArtBeforeTransferedFromCare, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientArtTrasferedOut.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(exitedCareWithtransferStatus, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientArtTrasferedOut.setCompositionString("(1 OR 2) AND 3");
		
		CohortIndicator pedionARTTransferedOutDuringPInd = Indicators
		        .newCohortIndicator(
		            "pedionARTTransferedOutDuringPInd",
		            patientArtTrasferedOut,
		            ParameterizableUtil
		                    .createParameterMappings("startDate=${startDate},endDate=${endDate},onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		pedionARTTransferedOutDuringPInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		pedionARTTransferedOutDuringPInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		//9 Number of  patients (all ages and both sex) on ART transfered in this  month 
		CompositionCohortDefinition pedionWithTransferEncounter = new CompositionCohortDefinition();
		pedionWithTransferEncounter.setName("pedionWithTransferEncounter");
		pedionWithTransferEncounter.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		pedionWithTransferEncounter.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		pedionWithTransferEncounter.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(onARTStateatTheEnd, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore}")));
		pedionWithTransferEncounter.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientTransferEncounter, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pedionWithTransferEncounter.setCompositionString("1 AND 2");
		CohortIndicator pedionWithTransferInFormInd = Indicators.newCohortIndicator("pedionWithTransferInFormInd",
		    pedionWithTransferEncounter,
		    ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}"));
		pedionWithTransferInFormInd.setName("pedionWithTransferEncounter");
		pedionWithTransferInFormInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		pedionWithTransferInFormInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		//-------------------------------------------
		//       EXPOSED INFANT CATEGORY
		//-------------------------------------------
		// Number of HIV exposed infants who are 6 weeks of age this month
		InProgramCohortDefinition inPMTCTInfantOnEndDate = Cohorts.createInProgramParameterizableByDate(
		    "In PMTCT Program on End Date", pmtctCombinedInfantProgram);
		AgeCohortDefinition under6Weeks = new AgeCohortDefinition();
		under6Weeks.setName("under6Weeks");
		under6Weeks.setMaxAge(5);
		under6Weeks.setMaxAgeUnit(DurationUnit.WEEKS);
		under6Weeks.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		
		AgeCohortDefinition over6Weeks = new AgeCohortDefinition();
		over6Weeks.setName("over6Weeks");
		over6Weeks.setMinAge(6);
		over6Weeks.setMinAgeUnit(DurationUnit.WEEKS);
		over6Weeks.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		
		CompositionCohortDefinition patientsInPMTCTInfantTurned6WeeksByStartAndEndDate = new CompositionCohortDefinition();
		patientsInPMTCTInfantTurned6WeeksByStartAndEndDate.setName("over6Weeks");
		patientsInPMTCTInfantTurned6WeeksByStartAndEndDate.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientsInPMTCTInfantTurned6WeeksByStartAndEndDate.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientsInPMTCTInfantTurned6WeeksByStartAndEndDate.getSearches().put("1",
		    new Mapped(inPMTCTInfantOnEndDate, ParameterizableUtil.createParameterMappings("onDate=${endDate}")));
		patientsInPMTCTInfantTurned6WeeksByStartAndEndDate.getSearches().put("2",
		    new Mapped(under6Weeks, ParameterizableUtil.createParameterMappings("effectiveDate=${startDate}")));
		patientsInPMTCTInfantTurned6WeeksByStartAndEndDate.getSearches().put("3",
		    new Mapped(over6Weeks, ParameterizableUtil.createParameterMappings("effectiveDate=${endDate}")));
		patientsInPMTCTInfantTurned6WeeksByStartAndEndDate.setCompositionString("1 AND 2 AND 3");
		
		CohortIndicator sixWeeksInfantsInd = Indicators.newCountIndicator("sixWeeksInfantsInd",
		    patientsInPMTCTInfantTurned6WeeksByStartAndEndDate,
		    ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
		
		// Number of HIV exposed Infants starting Cotrimoxazole Prophylaxis at 6 weeks  
		
		CompositionCohortDefinition patientsonCotrimoAt6Weeks = new CompositionCohortDefinition();
		patientsonCotrimoAt6Weeks.setName("patientsonCotrimoAt6Weeks");
		patientsonCotrimoAt6Weeks.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientsonCotrimoAt6Weeks.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientsonCotrimoAt6Weeks.getSearches().put("1",
		    new Mapped(inPMTCTInfantOnEndDate, ParameterizableUtil.createParameterMappings("onDate=${endDate}")));
		patientsonCotrimoAt6Weeks.getSearches().put("2",
		    new Mapped(over6Weeks, ParameterizableUtil.createParameterMappings("effectiveDate=${endDate}")));
		patientsonCotrimoAt6Weeks.getSearches().put("3",
		    new Mapped(startedCotrimoXazoleInPeriod, ParameterizableUtil.createParameterMappings("startDate=${startDate}")));
		patientsonCotrimoAt6Weeks.setCompositionString("1 AND 2 AND 3");
		
		CohortIndicator patientsonCotrimoAt6WeeksInd = Indicators.newCountIndicator("patientsonCotrimoAt6WeeksInd",
		    patientsInPMTCTInfantTurned6WeeksByStartAndEndDate,
		    ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
		
		//Add global filters to the report
		//PRE-ART DATA ELEMENT
		dsd.addColumn("1.1", "rwandareports.tracnetreport.indicator.preart.currentMalespreArt", new Mapped(
		        malePreArtunder10Ind, null), "");
		dsd.addColumn("1.2", "rwandareports.tracnetreport.indicator.preart.currentFemalespreArt", new Mapped(
		        femalePreArtunder10Ind, null), "");
		dsd.addColumn("1.3", "rwandareports.tracnetreport.indicator.preart.currentMales10to14preArt", new Mapped(
		        malePreArtbetween10and14Ind, null), "");
		dsd.addColumn("1.4", "rwandareports.tracnetreport.indicator.preart.currentFemales10to14preArt", new Mapped(
		        femalePreArtbetween10and14Ind, null), "");
		dsd.addColumn("1.5", "rwandareports.tracnetreport.indicator.preart.currentMales15to19preArt", new Mapped(
		        malePreArtbetween15and19Ind, null), "");
		dsd.addColumn("1.6", "rwandareports.tracnetreport.indicator.preart.currentFemales15to19preArt", new Mapped(
		        femalePreArtbetween15and19Ind, null), "");
		dsd.addColumn("1.7", "rwandareports.tracnetreport.indicator.preart.currentMalesAbove20Art", new Mapped(
		        malePreArtbetweenabove20Ind, null), "");
		dsd.addColumn("1.8", "rwandareports.tracnetreport.indicator.preart.currentFemalesAbove20Art", new Mapped(
		        femalePreArtbetweenabove20Ind, null), "");
		dsd.addColumn(
		    "2.1",
		    "rwandareports.tracnetreport.indicator.preart.newMaleUnderFifteenInHivCare",
		    new Mapped(malespedinewlyEnrolledInPreartInd, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")), "");
		dsd.addColumn(
		    "2.2",
		    "rwandareports.tracnetreport.indicator.preart.newFemaleUnderFifteenInHivCare",
		    new Mapped(femalespedinewlyEnrolledInPreartInd, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")), "");
		dsd.addColumn(
		    "2.3",
		    "rwandareports.tracnetreport.indicator.preart.newMaleMoreThanFifteenInHivCare",
		    new Mapped(malesAdultnewlyEnrolledInPreartInd, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")), "");
		dsd.addColumn(
		    "2.4",
		    "rwandareports.tracnetreport.indicator.preart.newFemaleMoreThanFifteenInHivCare",
		    new Mapped(femalesAdultnewlyEnrolledInPreartInd, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")), "");
		dsd.addColumn(
		    "3",
		    "rwandareports.tracnetreport.indicator.preart.patientsOnCotrimoProphylaxis",
		    new Mapped(allPatientsNewOnprophyInd, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		dsd.addColumn(
		    "4",
		    "rwandareports.tracnetreport.indicator.preart.patientsActiveTbAtEnrolThisMonth",
		    new Mapped(screenedForTbInHIVProgramsIndi, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")), "");
		dsd.addColumn(
		    "5",
		    "rwandareports.tracnetreport.indicator.preart.patientsTbPositiveAtEnrolThisMonth",
		    new Mapped(screenedPosTbInd, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")), "");
		dsd.addColumn(
		    "6",
		    "rwandareports.tracnetreport.indicator.preart.patientsInPreARVTLostToFollowUpThisMonth",
		    new Mapped(patientsinHIVcareLostTofolowUpInd, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${endDate}")), "");
		dsd.addColumn(
		    "7",
		    "rwandareports.tracnetreport.indicator.preart.patientsInPreARVTLostToFollowUpNotLostThisMonth",
		    new Mapped(patientsinLostAndBackToPRogramThismonthInd, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		dsd.addColumn(
		    "8",
		    "rwandareports.tracnetreport.indicator.preart.patientsInPreARVDiedThisMonth",
		    new Mapped(patientsDiedandNotOnARTInd, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")), "");
		dsd.addColumn(
		    "9",
		    "rwandareports.tracnetreport.indicator.preart.patientsInPreARVTransferredOutThisMonth",
		    new Mapped(patientsTransferedoutAndnotOnARTInd, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")), "");
		dsd.addColumn(
		    "10",
		    "rwandareports.tracnetreport.indicator.preart.patientsInPreARVTransferredInThisMonth",
		    new Mapped(patientsTransferedIntAndnotOnARTInd, ParameterizableUtil
		            .createParameterMappings("enrolledOnOrAfter=${startDate},onOrAfter=${startDate},onOrBefore=${endDate}")),
		    "");
		
		// ART DATA ELEMENTS
		dsd.addColumn("1b.1", "rwandareports.tracnetreport.indicator.art.malesUnder1yrCurrentOnArv", new Mapped(
		        maleArtunder1Ind, null), "");
		dsd.addColumn("1b.2", "rwandareports.tracnetreport.indicator.art.femalesUnder1yrCurrentOnArv", new Mapped(
		        femaleArtunder1Ind, null), "");
		dsd.addColumn("1b.3", "rwandareports.tracnetreport.indicator.art.malePeds1to4CurrentOnArv", new Mapped(
		        malePreArtbetween1To4Ind, null), "");
		dsd.addColumn("1b.4", "rwandareports.tracnetreport.indicator.art.femalePeds1to4CurrentOnArv", new Mapped(
		        femalePreArtbetween1To4Ind, null), "");
		dsd.addColumn("1b.5", "rwandareports.tracnetreport.indicator.art.malePeds5to9CurrentOnArv", new Mapped(
		        malePreArtbetween5To9Ind, null), "");
		dsd.addColumn("1b.6", "rwandareports.tracnetreport.indicator.art.femalePeds5to9CurrentOnArv", new Mapped(
		        femalePreArtbetween5To9Ind, null), "");
		dsd.addColumn("1b.7", "rwandareports.tracnetreport.indicator.art.malePeds10to14CurrentOnArv", new Mapped(
		        maleArtbetween10and14Ind, null), "");
		dsd.addColumn("1b.8", "rwandareports.tracnetreport.indicator.art.femalePeds10to14CurrentOnArv", new Mapped(
		        femaleArtbetween10and14Ind, null), "");
		dsd.addColumn("1b.9", "rwandareports.tracnetreport.indicator.art.male15to19CurrentOnArv", new Mapped(
		        maleArtbetween15and19Ind, null), "");
		dsd.addColumn("1b.10", "rwandareports.tracnetreport.indicator.art.female15to19CurrentOnArv", new Mapped(
		        femaleArtbetween15and19Ind, null), "");
		dsd.addColumn("1b.11", "rwandareports.tracnetreport.indicator.art.male20orAboveCurrentOnArv", new Mapped(
		        maleArtbetweenabove20Ind, null), "");
		dsd.addColumn("1b.12", "rwandareports.tracnetreport.indicator.art.female20orAboveCurrentOnArv", new Mapped(
		        femaleArtbetweenabove20Ind, null), "");
		dsd.addColumn(
		    "2b.1",
		    "rwandareports.tracnetreport.indicator.art.patientsonFirstLine",
		    new Mapped(notOnCurrentKaletraDrugOrderInd, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},dateborn=${endDate},onOrBefore=${endDate}")), "");
		dsd.addColumn(
		    "2b.2",
		    "rwandareports.tracnetreport.indicator.art.patientsonSecondLine",
		    new Mapped(activeOnCurrentKaletraDrugOrderInd, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},dateborn=${endDate},onOrBefore=${endDate}")), "");
		//dsd.addColumn("2.3","rwandareports.tracnetreport.indicator.art.patientsonThirdLine",new Mapped(pediOnArtStateinWhostage4Ind,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")),"");
		dsd.addColumn("2b.4", "rwandareports.tracnetreport.indicator.art.adults10to19onFirstLine", new Mapped(
		        patients10and19on1stLineInd, ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn("2b.5", "rwandareports.tracnetreport.indicator.art.adults10to19onSecondLine", new Mapped(
		        patients10and19on2ndLineInd, ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		// dsd.addColumn("2.6","rwandareports.tracnetreport.indicator.art.adults10to19onThirdLine",new Mapped(pediOnArtStateinWhostage1Ind,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")),""); 
		dsd.addColumn(
		    "2b.7",
		    "rwandareports.tracnetreport.indicator.art.adults15orMoreFirstLine",
		    new Mapped(notoadultsOnCurrentKaletraDrugOrderInd, ParameterizableUtil
		            .createParameterMappings("dateborn=${endDate},endDate=${endDate}")), "");
		dsd.addColumn(
		    "2b.8",
		    "rwandareports.tracnetreport.indicator.art.adults15orMoreSecondLine",
		    new Mapped(adultonCurrentKaletraDrugOrderCompoInd, ParameterizableUtil
		            .createParameterMappings("dateborn=${endDate},endDate=${endDate}")), "");
		//dsd.addColumn("2.9","rwandareports.tracnetreport.indicator.art.adults15orMoreThirdLine",new Mapped(maleAdultsadultsOnArtStateInd,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");  
		dsd.addColumn(
		    "3b.1",
		    "rwandareports.tracnetreport.indicator.art.pedsNewonART",
		    new Mapped(femalesPedsNotOnArtStateNotOnFolowingInd, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		dsd.addColumn(
		    "3b.2",
		    "rwandareports.tracnetreport.indicator.art.adultsNewonART",
		    new Mapped(
		            adultsEnrolledInPMTCNewToArtorMotherProgInd,
		            ParameterizableUtil
		                    .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}")),
		    "");
		dsd.addColumn(
		    "4b",
		    "rwandareports.tracnetreport.indicator.art.arvPedsDiedThisMonth",
		    new Mapped(
		            patientsDiedNotOnARTInd,
		            ParameterizableUtil
		                    .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}")),
		    "");
		dsd.addColumn(
		    "5b.1",
		    "rwandareports.tracnetreport.indicator.art.malesPediCurrenltyonArtnewOnTB",
		    new Mapped(malesPediCurentlyinARTNewtoTBInd, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		dsd.addColumn(
		    "5b.2",
		    "rwandareports.tracnetreport.indicator.art.femalesPediCurrenltyonArtnewOnTB",
		    new Mapped(femalesPediCurentlyinARTNewtoTBInd, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		dsd.addColumn(
		    "5b.3",
		    "rwandareports.tracnetreport.indicator.art.malesAdultsCurrenltyonArtnewOnTB",
		    new Mapped(malesAdultsCurentlyinARTNewtoTBInd, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		dsd.addColumn(
		    "5b.4",
		    "rwandareports.tracnetreport.indicator.art.femalesAdultsCurrenltyonArtnewOnTB",
		    new Mapped(femalesAdultsCurentlyinARTNewtoTBInd, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		dsd.addColumn(
		    "6b",
		    "rwandareports.tracnetreport.indicator.art.arvLostFollowupMoreThreeMonths",
		    new Mapped(
		            pedsOnArtLostAndwithHIVFormsInd,
		            ParameterizableUtil
		                    .createParameterMappings("onOrAfterDate=${startDate-3m},onOrBeforeDate=${endDate},onOrBefore=${endDate}")),
		    "");
		dsd.addColumn(
		    "7b",
		    "rwandareports.tracnetreport.indicator.art.arvLostFollowupRetracedThisMonths",
		    new Mapped(patientsinLostAndBackToProgramThismonthARTInd, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}")), "");
		dsd.addColumn(
		    "8b",
		    "rwandareports.tracnetreport.indicator.art.arvAdultTransferredOutThisMonth",
		    new Mapped(
		            pedionARTTransferedOutDuringPInd,
		            ParameterizableUtil
		                    .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}")),
		    "");
		dsd.addColumn(
		    "9b",
		    "rwandareports.tracnetreport.indicator.art.arvPedsTransferredInThisMonth",
		    new Mapped(pedionWithTransferInFormInd, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")), "");
		
		// EXPOSED INFANT
		dsd.addColumn(
		    "1c",
		    "rwandareports.tracnetreport.indicator.infantHivPosMothersAged6WeeksThisMonth",
		    new Mapped(sixWeeksInfantsInd, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		dsd.addColumn(
		    "2c",
		    "rwandareports.tracnetreport.indicator.infantHivPosMothersAged6WeeksThisMonthonCotrimo",
		    new Mapped(patientsonCotrimoAt6WeeksInd, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		return dsd;
	}
	
	private void setupProperties() {
		adulthivProgram = gp.getProgram(GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
		pediatrichivProgram = gp.getProgram(GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
		pmtctcombinedMother = gp.getProgram(GlobalPropertiesManagement.PMTCT_COMBINED_MOTHER_PROGRAM);
		pmtctPregnancyProgram = gp.getProgram(GlobalPropertiesManagement.PMTCT_PREGNANCY_PROGRAM);
		pmtctCombinedInfantProgram = gp.getProgram(GlobalPropertiesManagement.PMTCT_COMBINED_CLINIC_PROGRAM);
		adultOnFollowing = gp.getProgramWorkflowState(GlobalPropertiesManagement.FOLLOWING_STATE,
		    GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
		pediOnFollowing = gp.getProgramWorkflowState(GlobalPropertiesManagement.FOLLOWING_STATE,
		    GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
		adultOnART = gp.getProgramWorkflowState(GlobalPropertiesManagement.ON_ANTIRETROVIRALS_STATE,
		    GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
		pediOnART = gp.getProgramWorkflowState(GlobalPropertiesManagement.ON_ANTIRETROVIRALS_STATE,
		    GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
		pmtctOnART = gp.getProgramWorkflowState(GlobalPropertiesManagement.ON_ANTIRETROVIRALS_STATE,
		    GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.PMTCT_PREGNANCY_PROGRAM);
		adulttransferedOutState = gp.getProgramWorkflowState(GlobalPropertiesManagement.TRANSFERRED_OUT_STATE,
		    GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
		peditransferedOutState = gp.getProgramWorkflowState(GlobalPropertiesManagement.TRANSFERRED_OUT_STATE,
		    GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
		pediPreAndArtDiedState = gp.getProgramWorkflowState(GlobalPropertiesManagement.PATIENT_DIED_STATE,
		    GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
		adultPreAndArtDiedState = gp.getProgramWorkflowState(GlobalPropertiesManagement.PATIENT_DIED_STATE,
		    GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
		onOrAfterOnOrBefore.add("onOrAfter");
		onOrAfterOnOrBefore.add("onOrBefore");
		onOrAfterDateOnOrBeforeDate.add("onOrAfterDate");
		onOrAfterDateOnOrBeforeDate.add("onOrBeforeDate");
		tbScreeningtest = gp.getConcept(GlobalPropertiesManagement.TB_SCREENING_TEST);
		positiveStatus = gp.getConcept(GlobalPropertiesManagement.POSITIVE_HIV_TEST_ANSWER);
		reasonForExitingCare = gp.getConcept(GlobalPropertiesManagement.REASON_FOR_EXITING_CARE);
		patientDied = gp.getConcept(GlobalPropertiesManagement.PATIENT_DIED);
		patientTransferedOut = gp.getConcept(GlobalPropertiesManagement.TRASNFERED_OUT);
		patientDefaulted = MetadataLookup.getConcept("PIH:1743");
		clinicalEnountersIncLab = gp.getEncounterTypeList(GlobalPropertiesManagement.CLINICAL_ENCOUNTER_TYPES);
		pediAdnAdultEncounters = gp.getEncounterTypeList(GlobalPropertiesManagement.PEDIANDADULTHIV_ENCOUNTER_TYPES);
		transferinForm = gp.getForm(GlobalPropertiesManagement.HIV_TRANSFER_FORM);
		patientTransferEncounterType = gp.getEncounterType(GlobalPropertiesManagement.PATIENT_TRANSFER_ENCOUNTER);
		kaletra = gp.getConcept(GlobalPropertiesManagement.KALETRA_DRUG);
		cotrimoxazole = gp.getConcept(GlobalPropertiesManagement.COTRIMOXAZOLE_DRUG);
		dapsone = gp.getConcept(GlobalPropertiesManagement.DAPSONE_DRUG);
		
	}
	
}
