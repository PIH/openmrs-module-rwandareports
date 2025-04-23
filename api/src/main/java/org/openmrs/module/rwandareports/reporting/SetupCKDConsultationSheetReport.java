package org.openmrs.module.rwandareports.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.RelationshipType;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.rowperpatientreports.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.rowperpatientreports.patientdata.definition.MostRecentObservation;
import org.openmrs.module.rwandareports.filter.DateFormatFilter;
import org.openmrs.module.rwandareports.filter.DrugDosageCurrentFilter;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.RowPerPatientColumns;

public class SetupCKDConsultationSheetReport extends SingleSetupReport {

    //properties retrieved from global variables
    private Program CKDProgram;

    private Form rendevousForm;
    private Form CKDEnrollmentForm;

    private List<Form> DDBAndRendezvousForms=new ArrayList<Form>();

    private RelationshipType HBCP;

    List<EncounterType> HF_HTN_CKD_Encounter_TYPE;

    public void setup() throws Exception {
        log.info("Setting up report: " + getReportName());
        setupProperties();

        ReportDefinition rd = createReportDefinition();

        ReportDesign design = Helper.createRowPerPatientXlsOverviewReportDesign(rd, "CKDConsultationSheet.xls",
                "CKDConsultationSheet.xls_", null);

        Properties props = new Properties();
        props.put("repeatingSections", "sheet:1,row:11,dataset:dataSet");
        props.put("sortWeight","5000");
        design.setProperties(props);

        Helper.saveReportDesign(design);
    }

    @Override
    public String getReportName() {
        return "NCD-CKD Consultation Sheet";
    }

    private ReportDefinition createReportDefinition() {

        ReportDefinition reportDefinition = new ReportDefinition();
        reportDefinition.setName(getReportName());

        reportDefinition.addParameter(new Parameter("location", "Health Facility", Location.class));
        reportDefinition.setBaseCohortDefinition(Cohorts.createParameterizedLocationCohort("At Location"), ParameterizableUtil.createParameterMappings("location=${location}"));
        reportDefinition.addParameter(new Parameter("endDate", "Monday", Date.class));
        createDataSetDefinition(reportDefinition);

        Helper.saveReportDefinition(reportDefinition);

        return reportDefinition;
    }

    private void createDataSetDefinition(ReportDefinition reportDefinition) {
        // Create new dataset definition
        RowPerPatientDataSetDefinition dataSetDefinition = new RowPerPatientDataSetDefinition();
        dataSetDefinition.setName("CKD Consultation Data Set");

        SortCriteria sortCriteria = new SortCriteria();
        sortCriteria.addSortElement("nextRDV", SortCriteria.SortDirection.ASC);
        dataSetDefinition.setSortCriteria(sortCriteria);

        dataSetDefinition.addParameter(new Parameter("location", "Location", Location.class));
        dataSetDefinition.addParameter(new Parameter("endDate", "Monday", Date.class));

        //Add filters
        dataSetDefinition.addFilter(Cohorts.createInProgramParameterizableByDate("Patients in " + CKDProgram.getName(), CKDProgram), ParameterizableUtil.createParameterMappings("onDate=${endDate}"));

        dataSetDefinition.addFilter(Cohorts.getMondayToSundayPatientReturnVisit(DDBAndRendezvousForms), ParameterizableUtil.createParameterMappings("end=${endDate+6d},start=${endDate}"));

        DateFormatFilter dateFilter = new DateFormatFilter();
        dateFilter.setFinalDateFormat("dd-MMM-yyyy");

        //Add Columns
//        dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentReturnVisitDate("nextRDV", "yyyy/MM/dd", null), new HashMap<String, Object>());
//
//        dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentReturnVisitDate("nextVisit", null, dateFilter), new HashMap<String, Object>());

        dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentEncounterOfSpecificEncountertypeInperiod("nextVisit",gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE),
                null,null,HF_HTN_CKD_Encounter_TYPE,null,"yyyy/MM/dd"), new HashMap<String, Object>());

        dataSetDefinition.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());

        dataSetDefinition.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());

        dataSetDefinition.addColumn(RowPerPatientColumns.getIMBId("Id"), new HashMap<String, Object>());

        dataSetDefinition.addColumn(RowPerPatientColumns.getAge("age"), new HashMap<String, Object>());

        dataSetDefinition.addColumn(RowPerPatientColumns.getGender("Sex"), new HashMap<String, Object>());
        dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentCreatinine("creatinine","@ddMMMyy"),new HashMap<String, Object>());

        MostRecentObservation systolic = RowPerPatientColumns.getMostRecentSystolicPB("systolic", "dd-MMM-yy");
        dataSetDefinition.addColumn(systolic, new HashMap<String, Object>());

        MostRecentObservation diastolic = RowPerPatientColumns.getMostRecentDiastolicPB("diastolic", "dd-MMM-yy");
        dataSetDefinition.addColumn(diastolic, new HashMap<String, Object>());

        dataSetDefinition.addColumn(RowPerPatientColumns.getPatientCurrentlyActiveOnDrugOrder("Regimen", null),
                new HashMap<String, Object>());


        dataSetDefinition.addColumn(RowPerPatientColumns.getAccompRelationship("Accompagnateur"), new HashMap<String, Object>());

        dataSetDefinition.addColumn(RowPerPatientColumns.getPatientRelationship("HBCP",HBCP.getRelationshipTypeId(),"A",null), new HashMap<String, Object>());


        //AllObservationValues allSystolicBP = RowPerPatientColumns.getAllObservationValues("systolicLastTwo", systolicBP, null, new LastTwoObsFilter(),
        //    null);

        //AllObservationValues allDiastolicBP = RowPerPatientColumns.getAllObservationValues("diastolicLastTwo", diastolicBP, null, new LastTwoObsFilter(),
        //    null);

       /* CustomCalculationBasedOnMultiplePatientDataDefinitions alert = new CustomCalculationBasedOnMultiplePatientDataDefinitions();
        alert.setName("alert");
        alert.addPatientDataToBeEvaluated(systolic, new HashMap<String, Object>());
        alert.addPatientDataToBeEvaluated(diastolic, new HashMap<String, Object>());
        //alert.addPatientDataToBeEvaluated(allSystolicBP, new HashMap<String, Object>());
        //alert.addPatientDataToBeEvaluated(allDiastolicBP, new HashMap<String, Object>());
        alert.setCalculator(new CKDAlerts());
        dataSetDefinition.addColumn(alert, new HashMap<String, Object>());*/


        Map<String, Object> mappings = new HashMap<String, Object>();
        mappings.put("location", "${location}");
        mappings.put("endDate", "${endDate}");

        reportDefinition.addDataSetDefinition("dataSet", dataSetDefinition, mappings);
    }

    private void setupProperties() {
        CKDProgram = gp.getProgram(GlobalPropertiesManagement.CKD_PROGRAM);

        rendevousForm=gp.getForm(GlobalPropertiesManagement.CKD_RDV_FORM);

        CKDEnrollmentForm =gp.getForm(GlobalPropertiesManagement.CKD_ENROLLMENT_FORM);

        HF_HTN_CKD_Encounter_TYPE.add(gp.getEncounterType(GlobalPropertiesManagement.HF_HTN_CKD_ENCOUNTER_TYPE));

        DDBAndRendezvousForms.add(rendevousForm);
        DDBAndRendezvousForms.add(CKDEnrollmentForm);

        //DDBAndRendezvousForms=gp.getFormList(GlobalPropertiesManagement.CKD_DDB_FLOW_VISIT);

        HBCP=gp.getRelationshipType(GlobalPropertiesManagement.HBCP_RELATIONSHIP);

    }
}
