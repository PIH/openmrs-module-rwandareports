package org.openmrs.module.rwandareports.reporting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

public class SetupPathologyRequestReport implements SetupReport {

    protected final Log log = LogFactory.getLog(getClass());
    GlobalPropertiesManagement gp = new GlobalPropertiesManagement();


    private EncounterType pathologyEncounterType;
    private PersonAttributeType healthCenterPersonAttributeType;
    private PersonAttributeType phoneNumberPersonAttributeType;
    private Form pathologyRequestForm;
    private Concept sampleStatusConcept;
    private Concept referralStatusConcept;
    private Concept sampleDropOffConcept;
    private Concept pathologyRequestEncounterUUID;
    private Concept telephoneNumberConcept;


    public void setup() throws Exception {

        setupProperties();

        ReportDefinition rd =createReportDefinition();
        ReportDesign designCSV = Helper.createCsvReportDesign(rd,"Pathology Request Report.csv_");
        Helper.saveReportDesign(designCSV);
    }

    public void delete() {
        Helper.purgeReportDefinition("Pathology Request Report");
    }

    private ReportDefinition createReportDefinition() {
        ReportDefinition reportDefinition = new ReportDefinition();
        reportDefinition.setName("Pathology Request Report");
        reportDefinition.setUuid("996cf192-ff54-11eb-a63a-080027ce9ca0");
        Parameter location = new Parameter("location", "Location", Location.class);
        location.setRequired(false);
        reportDefinition.addParameter(location);

        createDataSetDefinition(reportDefinition);

        Helper.saveReportDefinition(reportDefinition);

        return reportDefinition;

    }


    private void createDataSetDefinition(ReportDefinition reportDefinition) {


        SqlDataSetDefinition sqldsd=new SqlDataSetDefinition();

        sqldsd.setSqlQuery("select \n" +
                "\tp.person_id as personId,\n" +
                "\tp.uuid as patientUuid,\n" +
                "\tpn.family_name,\n" +
                "    pn.family_name2,\n" +
                "    pn.middle_name,\n" +
                "    pn.given_name,  \n" +
                "    phone.value_text as patientPhoneNumber,\n" +
                "    healthcenter.name as patientHealthCenter,\n" +
                "    enc.encounter_id as encounterId,\n" +
                "    enc.uuid as encounterUuid,\n" +
                "    enc.encounter_datetime as encounterDatetime, \n" +
                "    SAMPLESTATUS.name as sampleStatusObs, \n" +
                "    SAMPLESTATUS.uuid as sampleStatusObsUuid,\n" +
                "    REFERRALSTATUS.name as referralStatusObs, \n" +
                "    REFERRALSTATUS.uuid as referralStatusObsUuid, \n" +
                "    SAMPLEDROPOFF.name as sampleDropoffObs, \n" +
                "    SAMPLEDROPOFF.uuid as sampleDropoffObsUuid, \n" +
                "    requestUUIDObs.encounter_id as resultsEncounterId " +

                "from encounter enc \n" +
                "\tleft join person p on enc.patient_id=p.person_id\n" +
                " left join patient patient on patient.patient_id = p.person_id " +
                "    left join (select person_id,given_name,middle_name,family_name,family_name2 from person_name where preferred=1 and voided=0 group by person_id) pn on enc.patient_id=pn.person_id\n" +
                "    left join (select person_id,name,location_id,retired from person_attribute pat left join location l on pat.value=l.location_id where person_attribute_type_id= "+ healthCenterPersonAttributeType.getPersonAttributeTypeId() +" and pat.voided=0 group by person_id) \n" +
                "\t\t\t\thealthcenter on enc.patient_id=healthcenter.person_id\n" +
                " left join (\n" +
                "\t\t\t\tselect o.encounter_id,o.value_text,o.person_id\n" +
                "\t\t\t\t\tfrom obs o \n" +
                "\t\t\t\t\twhere o.concept_id= " + telephoneNumberConcept.getConceptId() + " and o.voided=0 order by obs_id desc) phone on enc.patient_id=phone.person_id" +
                "    left join (\n" +
                "\t\t\t\tselect o.encounter_id,cn.name,o.uuid \n" +
                "\t\t\t\t\tfrom obs o \n" +
                "\t\t\t\t\tleft join concept_name cn on o.value_coded=cn.concept_id  \n" +
                "\t\t\t\t\twhere o.concept_id= " + sampleStatusConcept.getConceptId() + " and cn.concept_name_type=\"FULLY_SPECIFIED\" and o.voided=0 and cn.voided=0 and locale=\"en\"\n" +
                "                    ) SAMPLESTATUS on enc.encounter_id=SAMPLESTATUS.encounter_id\n" +
                "\tleft join (\n" +
                "\t\t\t\tselect o.encounter_id,cn.name,o.uuid \n" +
                "\t\t\t\t\tfrom obs o \n" +
                "\t\t\t\t\tleft join concept_name cn on o.value_coded=cn.concept_id  \n" +
                "\t\t\t\t\twhere o.concept_id= " + referralStatusConcept.getConceptId() + " and cn.concept_name_type=\"FULLY_SPECIFIED\" and o.voided=0 and cn.voided=0 and locale=\"en\"\n" +
                "                    ) REFERRALSTATUS on enc.encounter_id=REFERRALSTATUS.encounter_id\n" +
                "\tleft join (\n" +
                "\t\t\t\tselect o.encounter_id,cn.name,o.uuid \n" +
                "\t\t\t\t\tfrom obs o \n" +
                "\t\t\t\t\tleft join concept_name cn on o.value_coded=cn.concept_id  \n" +
                "\t\t\t\t\twhere o.concept_id= " + sampleDropOffConcept.getConceptId() + " and cn.concept_name_type=\"FULLY_SPECIFIED\" and o.voided=0 and cn.voided=0 and locale=\"en\"\n" +
                "                    ) SAMPLEDROPOFF on enc.encounter_id=SAMPLEDROPOFF.encounter_id\n" +
                " \tleft join (\n" +
                "\t\t\t\tselect encounter_id,value_text from obs o where o.concept_id= "+pathologyRequestEncounterUUID.getConceptId() + " and o.voided=0\n" +
                "                    ) requestUUIDObs on enc.uuid=requestUUIDObs.value_text " +
                "where \n" +
                "\t enc.voided=0 " +
                "\tand p.voided=0 " +
                "    and enc.form_id= " + pathologyRequestForm.getFormId() +
                " and p.dead=0" +
                " and patient.voided=0 " +
                " and IF( :location IS NULL, true, healthcenter.location_id= :location) " +
                " group by enc.encounter_id " +
                " order by enc.encounter_id  ");

        sqldsd.addParameter(new Parameter("location", "Location", Location.class));


//        Map<String, Object> mappings = new HashMap<String, Object>();
//        mappings.put("location", "${location}");





        reportDefinition.addDataSetDefinition("dsd", Mapped.mapStraightThrough(sqldsd));


    }

    private void setupProperties() {

        pathologyRequestForm = gp.getForm(GlobalPropertiesManagement.PATHOLOGYREQUESTFORM);
        sampleStatusConcept =gp.getConcept(GlobalPropertiesManagement.SAMPLESTATUSCONCEPT);
        referralStatusConcept = gp.getConcept(GlobalPropertiesManagement.REFERRALSTATUSCONCEPT);
        sampleDropOffConcept = gp.getConcept(GlobalPropertiesManagement.SAMPLEDROPOFFCONCEPT);
        pathologyEncounterType  = gp.getEncounterType(GlobalPropertiesManagement.PATHOLOGYENCOUNTERTYPE);
        phoneNumberPersonAttributeType =gp.getPersonAttributeType(GlobalPropertiesManagement.PERSONATTRIBUTEPHONENUMBER);
        healthCenterPersonAttributeType = gp.getPersonAttributeType(GlobalPropertiesManagement.FACILITY_PERSON_ATTRIBUTE_TYPE_ID);
        pathologyRequestEncounterUUID =  gp.getConcept(GlobalPropertiesManagement.PATHOLOGYREQUESTENCOUNTERUUID);
        telephoneNumberConcept = gp.getConcept(GlobalPropertiesManagement.TELEPHONE_NUMBER_CONCEPT);
    }

}
