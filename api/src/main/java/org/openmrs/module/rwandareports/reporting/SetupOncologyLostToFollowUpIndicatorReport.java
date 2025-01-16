package org.openmrs.module.rwandareports.reporting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.*;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.SqlEncounterQuery;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.rwandareports.dataset.EncounterIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.dataset.LocationHierachyIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.indicator.EncounterIndicator;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.Indicators;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class SetupOncologyLostToFollowUpIndicatorReport extends SingleSetupReport {
	
	private Program oncologyProgram;
	
	private List<Program> oncologyPrograms = new ArrayList<Program>();
	
	private ProgramWorkflow diagnosis;
	
	private Concept pathologyResultVisit;
	
	private Concept scheduledVisit;
	
	private Concept biopsyResultVisit;
	
	private Concept specialVisit;
	
	private Concept pediatricChemotherapy;
	
	private Concept pediatricNonChemotherapy;
	
	private Concept rwandaCancerCenterChemotherapy;
	
	private Concept ChemotherapyInpatientWardVisit;
	
	private EncounterType nonClinicalOncology;
	
	private EncounterType outPatientOncology;
	
	private EncounterType inPatientOncology;
	
	private EncounterType externalOncology;
	
	private EncounterType vitals;
	
	private List<Concept> visitDates = new ArrayList<Concept>();
	
	private List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
	
	Properties properties = new Properties();
	
	@Override
	public String getReportName() {
		return "ONC-Oncology Lost to FollowUp Indicator Report";
	}
	
	public void setup() throws Exception {
		log.info("Setting up report: " + getReportName());
		setUpProperties();
		
		Properties properties = new Properties();
		properties.setProperty("hierarchyFields", "countyDistrict:District");
		
		ReportDefinition LostRd = new ReportDefinition();
		LostRd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		LostRd.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		LostRd.addParameter(new Parameter("location", "Location", AllLocation.class, properties));
		
		LostRd.setName(getReportName());
		
		LostRd.addDataSetDefinition(createMonthlyLocationDataSet(),
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
		//LostRd.setBaseCohortDefinition(Cohorts.createInProgramParameterizableByStartEndDate("Oncology", oncologyProgram), ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		ProgramEnrollmentCohortDefinition patientEnrolledInOncologyProgram = new ProgramEnrollmentCohortDefinition();
		patientEnrolledInOncologyProgram.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientEnrolledInOncologyProgram.setPrograms(oncologyPrograms);
		
		LostRd.setBaseCohortDefinition(patientEnrolledInOncologyProgram,
		    ParameterizableUtil.createParameterMappings("enrolledOnOrBefore=${endDate}"));
		
		Helper.saveReportDefinition(LostRd);
		
		ReportDesign quarterlyDesign = Helper.createRowPerPatientXlsOverviewReportDesign(LostRd,
		    "OncologyLostToFollowupReport.xls", "ONC-Oncology Lost to FollowUp Indicator Report (Excel)", null);
		Properties MonthlyProps = new Properties();
		MonthlyProps.put("repeatingSections", "sheet:1,dataset:OncologyLTFIndicator");
		MonthlyProps.put("sortWeight", "5000");
		quarterlyDesign.setProperties(MonthlyProps);
		Helper.saveReportDesign(quarterlyDesign);
		
	}
	
	//Create Quarterly Encounter Data set
	
	public LocationHierachyIndicatorDataSetDefinition createMonthlyLocationDataSet() {
		
		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
		        createMonthlyBaseDataSet());
		ldsd.addBaseDefinition(createMonthlyBaseDataSet());
		ldsd.setName("OncologyLTFIndicator");
		ldsd.addParameter(new Parameter("startDate", "From", Date.class));
		ldsd.addParameter(new Parameter("endDate", "To", Date.class));
		ldsd.addParameter(new Parameter("location", "Hospital", LocationHierarchy.class));
		
		return ldsd;
	}
	
	//
	
	// create quarterly cohort Data set
	private CohortIndicatorDataSetDefinition createMonthlyBaseDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("OncologyLTFIndicator");
		dsd.addParameter(new Parameter("startDate", "From", Date.class));
		dsd.addParameter(new Parameter("endDate", "To", Date.class));
		createQuarterlyIndicators(dsd);
		return dsd;
	}
	
	private void createQuarterlyIndicators(CohortIndicatorDataSetDefinition dsd) {
		
		//====================================================================//
		// Total Patients who have RDV(Appointments) within the review period//
		//==================================================================//
		
		SqlCohortDefinition patientWithOncologyVisitDatesDeno = getpatientWithOncologyVisitsRDV(visitDates);
		
		CompositionCohortDefinition patientWithOncologyVisitDatesAll = new CompositionCohortDefinition();
		patientWithOncologyVisitDatesAll.setName("patientWithOncologyVisitDatesAll");
		patientWithOncologyVisitDatesAll.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientWithOncologyVisitDatesAll.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientWithOncologyVisitDatesAll.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientWithOncologyVisitDatesDeno, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientWithOncologyVisitDatesAll.setCompositionString("1");
		
		CohortIndicator patientWithOncologyVisitDatesIndicatorNumerator = Indicators.newCountIndicator(
		    "Total Patients who have RDV(Appointments) within the review period", patientWithOncologyVisitDatesDeno,
		    ParameterizableUtil.createParameterMappings("endDate=${endDate-6m},startDate=${startDate-6m}"));
		
		dsd.addColumn(
		    "Deno",
		    "Total Patients who have RDV(Appointments) within the review period",
		    new Mapped(patientWithOncologyVisitDatesIndicatorNumerator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
		//================================================================================================================//
		// Total Patients who have RDV(Appointments) within the review period and have not shown up until six months after//
		//================================================================================================================//
		
		SqlCohortDefinition patientLostToFollowUpOncologyNume = getpatientLostToFollowupWithOncologyVisitsRDV(visitDates,
		    encounterTypes);
		
		SqlCohortDefinition patientWithVisitInSixMonth = getpatientWithVisit(visitDates, encounterTypes);
		
		CompositionCohortDefinition patientLostToFollowUpOncology = new CompositionCohortDefinition();
		patientLostToFollowUpOncology.setName("patientLostToFollowUpOncology");
		patientLostToFollowUpOncology.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientLostToFollowUpOncology.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientLostToFollowUpOncology.addParameter(new Parameter("from", "From", Date.class));
		patientLostToFollowUpOncology.addParameter(new Parameter("to", "To", Date.class));
		patientLostToFollowUpOncology.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientWithOncologyVisitDatesDeno, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate-6m},endDate=${endDate-6m}")));
		patientLostToFollowUpOncology.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientWithVisitInSixMonth, ParameterizableUtil
		            .createParameterMappings("from=${from},to=${to}")));
		patientLostToFollowUpOncology.setCompositionString("1 and not 2");
		
		CohortIndicator patientLostToFollowUpOncologyIndicatorDenomurator = Indicators.newCountIndicator(
		    "Total Patients who have RDV(Appointments) within the review period", patientLostToFollowUpOncology,
		    ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate},from=${endDate-9m},to=${endDate}"));
		
		dsd.addColumn(
		    "Nume",
		    "Total Patients who have RDV(Appointments) within the review period and have not shown up until six months after",
		    new Mapped(patientLostToFollowUpOncologyIndicatorDenomurator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
		
	}
	
	private void setUpProperties() {
		oncologyProgram = gp.getProgram(GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		oncologyPrograms.add(oncologyProgram);
		
		scheduledVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_SCHEDULED_OUTPATIENT_VISIT);
		biopsyResultVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_BIOPSY_RESULT_VISIT);
		pathologyResultVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_PATHOLOGY_RESULT_VISIT);
		specialVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_SPECIAL_VISIT);
		pediatricChemotherapy = Context.getConceptService().getConceptByUuid("5efb51db-4e71-497d-822c-91501ac167f6");
		pediatricNonChemotherapy = Context.getConceptService().getConceptByUuid("8c3b045a-aa94-4361-b2e9-8a80c26ccede");
		rwandaCancerCenterChemotherapy = Context.getConceptService()
		        .getConceptByUuid("8eba01f9-2ea0-49d0-b61b-8d6001e2ff7b");
		ChemotherapyInpatientWardVisit = gp.getConcept(GlobalPropertiesManagement.CHEMOTHERAPY_INPATIENT_WARD_VISIT_DATE);
		nonClinicalOncology = Context.getEncounterService().getEncounterTypeByUuid("b1c2d207-7221-4c23-b580-6c5de5385847");
		outPatientOncology = Context.getEncounterService().getEncounterTypeByUuid("ff319885-3f20-4ae5-8663-3ad5678cba41");
		inPatientOncology = Context.getEncounterService().getEncounterTypeByUuid("0b3925f9-0336-47a6-931e-5c356e9cc82f");
		externalOncology = Context.getEncounterService().getEncounterTypeByUuid("570556f2-7080-49f6-9ef3-301feb313896");
		vitals = Context.getEncounterService().getEncounterTypeByUuid("daf32375-d293-4e27-a68d-2a58494c96e1");
		
		visitDates.add(scheduledVisit);
		visitDates.add(biopsyResultVisit);
		visitDates.add(specialVisit);
		visitDates.add(pathologyResultVisit);
		visitDates.add(pediatricChemotherapy);
		visitDates.add(pediatricNonChemotherapy);
		visitDates.add(rwandaCancerCenterChemotherapy);
		visitDates.add(ChemotherapyInpatientWardVisit);
		
		encounterTypes.add(nonClinicalOncology);
		encounterTypes.add(outPatientOncology);
		encounterTypes.add(inPatientOncology);
		encounterTypes.add(externalOncology);
		encounterTypes.add(vitals);
		
	}
	
	private SqlCohortDefinition getpatientWithOncologyVisitsRDV(List<Concept> visits) {
		
		StringBuilder visitsDate = new StringBuilder();
		int y = 0;
		for (Concept c : visits) {
			if (y > 0) {
				visitsDate.append(",");
			}
			visitsDate.append(c.getConceptId());
			
			y++;
		}
		
		StringBuilder qStr = new StringBuilder();
		SqlCohortDefinition patientWithVisitsDate = new SqlCohortDefinition();
		qStr.append("select o.person_id from obs o where o.voided=0 and o.concept_id in (");
		qStr.append(visitsDate);
		qStr.append(") and o.value_datetime>=:startDate and o.value_datetime<=:endDate");
		System.out.println("Rebaaaaaaaaaaaaaa" + qStr);
		patientWithVisitsDate.setQuery(qStr.toString());
		patientWithVisitsDate.setName("patientWithVisitsDate");
		patientWithVisitsDate.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientWithVisitsDate.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		return patientWithVisitsDate;
		
	}
	
	private SqlCohortDefinition getpatientLostToFollowupWithOncologyVisitsRDV(List<Concept> visits,
	        List<EncounterType> encounterTypes) {
		
		StringBuilder visitsDate = new StringBuilder();
		int y = 0;
		for (Concept c : visits) {
			if (y > 0) {
				visitsDate.append(",");
			}
			visitsDate.append(c.getConceptId());
			
			y++;
		}
		StringBuilder encounters = new StringBuilder();
		int i = 0;
		for (EncounterType e : encounterTypes) {
			if (i > 0) {
				encounters.append(",");
			}
			encounters.append(e.getEncounterTypeId());
			
			i++;
		}
		
		StringBuilder qStr = new StringBuilder();
		SqlCohortDefinition patientLosts = new SqlCohortDefinition();
		qStr.append("select lastObs.person_id from (select obs_id, value_datetime, obs.person_id from obs inner join (select person_id, max(value_datetime) as MostRecentDate from obs where voided = 0 and concept_id in (");
		qStr.append(visitsDate);
		qStr.append(") group by person_id ) maxTable ON maxTable.person_id = obs.person_id and maxTable.MostRecentDate = obs.value_datetime where concept_id in (");
		qStr.append(visitsDate);
		qStr.append(") and voided = 0) as lastObs, (SELECT  encounter_id, encounter_datetime, encounter.patient_id FROM encounter INNER JOIN ( SELECT patient_id, max(encounter_datetime) as LastVisit from encounter where encounter_type IN (");
		qStr.append(encounters);
		qStr.append(") AND voided = 0 group by patient_id) Last_Encounter ON encounter.patient_id = Last_Encounter.patient_id AND Last_Encounter.LastVisit = encounter.encounter_datetime WHERE encounter_type IN (");
		qStr.append(encounters);
		qStr.append(") AND voided = 0) as last_Visit WHERE lastObs.value_datetime>=:startDate and lastObs.value_datetime<=:endDate and (not last_Visit.encounter_datetime > lastObs.value_datetime)");
		qStr.append("and last_Visit.patient_id=lastObs.person_id;");
		System.out.println("lostttttttttttttttttttttttt" + qStr);
		patientLosts.setQuery(qStr.toString());
		patientLosts.setName("patientWithVisitsDate");
		patientLosts.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientLosts.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		return patientLosts;
		
	}
	
	private SqlCohortDefinition getpatientWithVisit(List<Concept> visits, List<EncounterType> encounterTypes) {
		
		StringBuilder visitsDate = new StringBuilder();
		int y = 0;
		for (Concept c : visits) {
			if (y > 0) {
				visitsDate.append(",");
			}
			visitsDate.append(c.getConceptId());
			
			y++;
		}
		StringBuilder encounters = new StringBuilder();
		int i = 0;
		for (EncounterType e : encounterTypes) {
			if (i > 0) {
				encounters.append(",");
			}
			encounters.append(e.getEncounterTypeId());
			
			i++;
		}
		
		StringBuilder qStr = new StringBuilder();
		SqlCohortDefinition patientLosts = new SqlCohortDefinition();
		qStr.append("select e.patient_id from encounter e where e.encounter_type IN (" + encounters
		        + ") and e.encounter_datetime>=:from and e.encounter_datetime<=:to and e.voided=0");
		System.out.println("lostttttttttttttttttttttttt" + qStr);
		patientLosts.setQuery(qStr.toString());
		patientLosts.setName("patientWithVisitsDate");
		patientLosts.addParameter(new Parameter("from", "From", Date.class));
		patientLosts.addParameter(new Parameter("to", "To", Date.class));
		
		return patientLosts;
		
	}
}
