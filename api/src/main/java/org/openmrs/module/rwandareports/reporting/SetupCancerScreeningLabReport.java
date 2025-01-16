package org.openmrs.module.rwandareports.reporting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rowperpatientreports.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.rwandareports.filter.LocationEncounterFilter;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.RowPerPatientColumns;

import java.util.*;

public class SetupCancerScreeningLabReport {
	
	protected final static Log log = LogFactory.getLog(SetupCancerScreeningLabReport.class);
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	private Form muzimaLabResultsform;
	
	private Form openmrsLabResultsform;
	
	private Concept sampleMethod;
	
	private Concept providerCollectedSample;
	
	private Concept selfCollectedSample;
	
	private List<Concept> sampleMethodList = new ArrayList<Concept>();
	
	private Concept HIVstatus;
	
	private Concept positive;
	
	private Concept negative;
	
	private Concept unknown;
	
	private List<Concept> HIVstatusList = new ArrayList<Concept>();
	
	private Concept testResults;
	
	private Concept HPVPositive;
	
	private Concept HPVNegative;
	
	private Concept HPVFailedResults;
	
	private List<Concept> testResultsList = new ArrayList<Concept>();
	
	private List<Form> resultFormsList = new ArrayList<Form>();
	
	private Concept specimenCode;
	
	private Concept screeningThrough;
	
	private Concept otherLabTestResult;
	
	private List<EncounterType> LabEncounterTypes = new ArrayList<EncounterType>();
	
	public void setup() throws Exception {
		setupPrograms();
		
		ReportDefinition createLabDefinition = createLabReportDefinition();
		
		ReportDesign createLabReportDesign = Helper.createRowPerPatientXlsOverviewReportDesign(createLabDefinition,
		    "OncologyCancerScreeningLabreport.xls", "OncologyCancerScreeningLabreport.xls_", null);
		
		Properties labProps = new Properties();
		labProps.put("repeatingSections", "sheet:1,row:10,dataset:dataset");
		labProps.put("sortWeight", "5000");
		
		createLabReportDesign.setProperties(labProps);
		
		Helper.saveReportDesign(createLabReportDesign);
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("OncologyCancerScreeningLabreport.xls_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		Helper.purgeReportDefinition("ONC-Cancer Screening Lab report sheet");
		
	}
	
	private ReportDefinition createLabReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("ONC-Cancer Screening Lab report sheet");
		// reportDefinition.addParameter(new Parameter("location", "Health Center", Location.class));
		reportDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
		reportDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		Parameter location = new Parameter("location", "Health Facility", Location.class);
		location.setRequired(false);
		
		reportDefinition.addParameter(location);
		
		SqlCohortDefinition locationDefinition = new SqlCohortDefinition();
		locationDefinition
		        .setQuery("select p.patient_id from patient p, person_attribute pa, person_attribute_type pat where p.patient_id = pa.person_id and pat.format ='org.openmrs.Location' and pa.voided = 0 and pat.person_attribute_type_id = pa.person_attribute_type_id and (:location is null or pa.value = :location)");
		locationDefinition.setName("locationDefinition");
		locationDefinition.addParameter(location);
		
		SqlCohortDefinition HPVLabResults = new SqlCohortDefinition(
		        "select distinct distinct patient_id as person_id from encounter where form_id in ("
		                + muzimaLabResultsform.getFormId() + "," + openmrsLabResultsform.getFormId()
		                + ") and  encounter_datetime<= :endDate and encounter_datetime>= :startDate and voided=0");
		HPVLabResults.addParameter(new Parameter("endDate", "endDate", Date.class));
		HPVLabResults.addParameter(new Parameter("startDate", "startDate", Date.class));
		
		SqlCohortDefinition HPVLabRequestsWithoutLabResults = new SqlCohortDefinition(
		        "select distinct person_id from obs where concept_id="
		                + sampleMethod.getConceptId()
		                + " and person_id not in (select distinct patient_id as person_id from encounter where form_id in ("
		                + muzimaLabResultsform.getFormId()
		                + ","
		                + openmrsLabResultsform.getFormId()
		                + ") and  encounter_datetime>= :endDate and voided=0) and obs_datetime<= :endDate and obs_datetime>= :startDate and voided=0");
		HPVLabRequestsWithoutLabResults.addParameter(new Parameter("endDate", "endDate", Date.class));
		HPVLabRequestsWithoutLabResults.addParameter(new Parameter("startDate", "startDate", Date.class));
		
		CompositionCohortDefinition labReportSheetBaseCohort = new CompositionCohortDefinition();
		labReportSheetBaseCohort.setName("labReportSheetBaseCohort");
		labReportSheetBaseCohort.addParameter(new Parameter("location", "Health Center", Location.class));
		labReportSheetBaseCohort.addParameter(new Parameter("startDate", "startDate", Date.class));
		labReportSheetBaseCohort.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		labReportSheetBaseCohort.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(locationDefinition, ParameterizableUtil
		            .createParameterMappings("location=${location}")));
		
		labReportSheetBaseCohort.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(HPVLabResults, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")));
		
		labReportSheetBaseCohort.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(HPVLabRequestsWithoutLabResults, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")));
		
		labReportSheetBaseCohort.setCompositionString("1 AND (2 OR 3)");
		
		reportDefinition.setBaseCohortDefinition(labReportSheetBaseCohort,
		    ParameterizableUtil.createParameterMappings("location=${location},endDate=${endDate},startDate=${startDate}"));
		
		createLabReportDataSetDefinition(reportDefinition);
		Helper.saveReportDefinition(reportDefinition);
		
		return reportDefinition;
	}
	
	private void createLabReportDataSetDefinition(ReportDefinition reportDefinition) {
		RowPerPatientDataSetDefinition dataSetDefinition = new RowPerPatientDataSetDefinition();
		dataSetDefinition.setName("Lab report Data set");
		dataSetDefinition.addParameter(new Parameter("location", "Location", Location.class));
		dataSetDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
		dataSetDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		//        SortCriteria sortCriteria = new SortCriteria();
		//        sortCriteria.addSortElement("nextRDV", SortCriteria.SortDirection.ASC);
		//        dataSetDefinition.setSortCriteria(sortCriteria);
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getRwandaNationalID("NID"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getArchivingId("Id"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentInperiodHavingCodedAnswers("HPVSample", sampleMethod,
		    sampleMethodList, null, null, null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentReturnVisitDate("nextRDV", "yyyy/MM/dd", null),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getPhoneNumber("phoneNumber"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getPatientAddress("Address", false, true, true, true, false, false),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentInperiodHavingCodedAnswers("HIVStatus", HIVstatus,
		    HIVstatusList, null, null, "dd/MM/yyyy"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecentInPeriod("testType", screeningThrough, null, null, "dd/MM/yyyy"),
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentInperiodHavingCodedAnswers("testResult", testResults,
		    testResultsList, null, null, "dd/MM/yyyy"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(
		    RowPerPatientColumns.getMostRecentInPeriod("otherLabTestResult", otherLabTestResult, null, null, "dd/MM/yyyy"),
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getRecentEncounter("recentencounterLocation", resultFormsList,
		    LabEncounterTypes, "dd/MM/yyyy", new LocationEncounterFilter()), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getAllObservationValuesAfterStartDateAndBeforeEndDate(
		    "specimenCode", specimenCode, "dd/MM/yyyy", null, null), ParameterizableUtil
		        .createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		
		//        dataSetDefinition.addColumn(RowPerPatientColumns);
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("location", "${location}");
		mappings.put("endDate", "${endDate}");
		mappings.put("startDate", "${startDate}");
		
		reportDefinition.addDataSetDefinition("dataset", dataSetDefinition, mappings);
		
	}
	
	private void setupPrograms() {
		
		muzimaLabResultsform = Context.getFormService().getForm("muzima oncology screening Lab Results");
		openmrsLabResultsform = Context.getFormService().getForm("Oncology Screening Lab Results");
		
		LabEncounterTypes.add(openmrsLabResultsform.getEncounterType());
		
		resultFormsList.add(muzimaLabResultsform);
		resultFormsList.add(openmrsLabResultsform);
		
		sampleMethod = Context.getConceptService().getConceptByUuid("f6f60fcb-2e6f-4e72-940d-ff96e22452aa");
		providerCollectedSample = Context.getConceptService().getConceptByUuid("37ad4306-50b9-4d4c-ab78-a1a77ddd7223");
		selfCollectedSample = Context.getConceptService().getConceptByUuid("8b710fc4-efa6-4ee1-b9a8-e2608149d7ff");
		sampleMethodList.add(providerCollectedSample);
		sampleMethodList.add(selfCollectedSample);
		
		HIVstatus = Context.getConceptService().getConceptByUuid("aec6ad18-f4dd-4cfa-b68d-3d7bb6ea908e");
		
		positive = Context.getConceptService().getConceptByUuid("3cd3a7a2-26fe-102b-80cb-0017a47871b2");
		negative = Context.getConceptService().getConceptByUuid("3cd28732-26fe-102b-80cb-0017a47871b2");
		unknown = Context.getConceptService().getConceptByUuid("3cd6fac4-26fe-102b-80cb-0017a47871b2");
		HIVstatusList.add(positive);
		HIVstatusList.add(negative);
		HIVstatusList.add(unknown);
		
		testResults = Context.getConceptService().getConceptByUuid("bfb3eb1e-db98-4846-9915-0168511c6298");
		HPVPositive = Context.getConceptService().getConceptByUuid("1b4a5f67-6106-4a4d-a389-2f430be543e4");
		HPVNegative = Context.getConceptService().getConceptByUuid("64c23192-54e4-4750-9155-2ed0b736a0db");
		HPVFailedResults = Context.getConceptService().getConceptByUuid("3b989534-ca6b-4bef-b99c-cd8397b1cdbe");
		specimenCode = Context.getConceptService().getConceptByUuid("16cd65e3-45af-4291-88fd-fe4d91847e4f");
		screeningThrough = Context.getConceptService().getConceptByUuid("7e4e6554-d6c5-4ca3-b371-49806a754992");
		otherLabTestResult = Context.getConceptService().getConceptByUuid("3ce1ca8a-26fe-102b-80cb-0017a47871b2");
		
		testResultsList.add(HPVPositive);
		testResultsList.add(HPVNegative);
		testResultsList.add(HPVFailedResults);
		
	}
}
