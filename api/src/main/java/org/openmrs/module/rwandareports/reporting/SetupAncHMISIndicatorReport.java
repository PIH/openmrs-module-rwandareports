package org.openmrs.module.rwandareports.reporting;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.rwandareports.dataset.LocationHierachyIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.indicator.EncounterIndicator;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.Indicators;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class SetupAncHMISIndicatorReport extends SingleSetupReport {


    private List<String> onOrAfterOnOrBefore = new ArrayList<String>();


    private Concept reasoForReferral;

    private List<Concept> pregnanciesRisks=new ArrayList<Concept>();
    private List<Concept> ultrasoundValues=new ArrayList<Concept>();
    private List<Concept> ancTT25=new ArrayList<Concept>();


    private Form HcAncEnrollmentForm;
    private Form HcAncContactForm;
    private List<Form> HcAncForms = new ArrayList<Form>();

    private Concept gestationalAge;
    private Concept gestationalAgeLMP;

    private Concept scarReferred;
    private Concept ancTTFull;
    private Concept ironFolicAcidFullReceived;
    private Concept netBeds;
    private Concept muacScreening;
    private Concept anemiaTest;
    private Concept ancContact;
    private Concept answerYes;
    private Concept hypertensionDisorders;
    private Concept prematureDelivery;
    private Concept birthDisabilities;
    private Concept previousCS;
    private Concept stillbirth;
    private Concept RecurrentAbortion3Times;
    private Concept deliveryNewbornUnder25Kg;
    private Concept macrosomia;
    private Concept multiplePregnancy;
    private Concept vaginalBleedingDuringPregnancy;
    private Concept AgHbs;
    private Concept AFI;
    private Concept GWU;
    private Concept EFW;
    private Concept FHR;
    private Concept NF;
    private Concept RPR;
    private Concept preventTreatmentAnc;
    private Concept TT1_MNCH;
    private Concept TT2_MNCH;
    private Concept TT3_MNCH;
    private Concept TT4_MNCH;
    private Concept TT5_MNCH;


    private EncounterType ancVisit;

   



    private void setUpProperties() {
        onOrAfterOnOrBefore.add("onOrAfter");
        onOrAfterOnOrBefore.add("onOrBefore");

        gestationalAge= Context.getConceptService().getConceptByUuid("d616d705-6c11-4b7f-96ec-a8c7994a07bc");
        gestationalAgeLMP= Context.getConceptService().getConceptByUuid("974d7f4b-4648-4bb5-8d32-397d27038a51");
        ancContact= Context.getConceptService().getConceptByUuid("bbe69d90-984d-40b4-9ab5-7fe758a58aaf");
        answerYes= Context.getConceptService().getConceptByUuid("3cd6f600-26fe-102b-80cb-0017a47871b2");


        hypertensionDisorders= Context.getConceptService().getConceptByUuid("042d3875-0e29-4d16-bc5b-8c8a40060c92");
        prematureDelivery= Context.getConceptService().getConceptByUuid("49bed4cd-cad1-4a05-bbca-69e00fc92d2a");
        birthDisabilities= Context.getConceptService().getConceptByUuid("c83a2371-ce82-4d15-bf0e-9bd8a528e509");
        previousCS= Context.getConceptService().getConceptByUuid("778553cf-55f8-4173-91de-08616142f17f");
        stillbirth= Context.getConceptService().getConceptByUuid("1e154024-518d-449c-8f40-6d3965ef120d");
        RecurrentAbortion3Times= Context.getConceptService().getConceptByUuid("f3429526-d600-4247-b5e1-557dc7c178dc");
        deliveryNewbornUnder25Kg= Context.getConceptService().getConceptByUuid("f8dbde0c-9915-4942-9d26-047790ce9863");
        macrosomia= Context.getConceptService().getConceptByUuid("891ca104-be06-4997-87cb-a40ac5a8422d");
        multiplePregnancy= Context.getConceptService().getConceptByUuid("69f193b7-9f8e-4b7d-be2c-3f82994eb44d");
        vaginalBleedingDuringPregnancy= Context.getConceptService().getConceptByUuid("e8f52434-9728-4502-b58e-2eb51256d50a");

        AgHbs = Context.getConceptService().getConceptByUuid("0831b054-2b80-4365-8c35-cfa681651b6b");
        AFI = Context.getConceptService().getConceptByUuid("ee891321-3066-41c4-8fb4-22a8c6d46b5d");
        GWU = Context.getConceptService().getConceptByUuid("b209fbaa-e240-472f-bb57-0efd2586c0ad");
        EFW = Context.getConceptService().getConceptByUuid("76f7ba1f-b3d5-4bfa-a75f-d9ab2b7e59f8");
        FHR = Context.getConceptService().getConceptByUuid("57c934dd-a086-4e73-a9b8-3b9c1abef1b1");
        NF = Context.getConceptService().getConceptByUuid("f0b20606-a261-4da4-b138-0785399cc850");
        RPR = Context.getConceptService().getConceptByUuid("3cdb36f2-26fe-102b-80cb-0017a47871b2");
        anemiaTest = Context.getConceptService().getConceptByUuid("3ccc7158-26fe-102b-80cb-0017a47871b2");
        muacScreening = Context.getConceptService().getConceptByUuid("4326b04b-3158-417a-bb8d-ad022295b0f4");
        preventTreatmentAnc = Context.getConceptService().getConceptByUuid("830919a3-dfda-4356-a7a0-80ea2e3cf35b");
        ironFolicAcidFullReceived = Context.getConceptService().getConceptByUuid("0bd8cc98-0aec-4fa4-89b2-68a0748a1c0e");
        netBeds = Context.getConceptService().getConceptByUuid("1594849e-f8db-43ec-9294-645ac0ec6e7d");
        ancTTFull = Context.getConceptService().getConceptByUuid("10cb2549-1b78-4483-9d30-01807b8e61e8");
        TT1_MNCH = Context.getConceptService().getConceptByUuid("1ee10434-7442-4c6a-9de7-fa06e3f61145");
        TT2_MNCH = Context.getConceptService().getConceptByUuid("5c95c913-2d15-4640-8ded-0a6a6aaaff01");
        TT3_MNCH = Context.getConceptService().getConceptByUuid("2344ec1b-1727-4350-849d-8c878dd4d3d7");
        TT4_MNCH = Context.getConceptService().getConceptByUuid("23e213d6-e7f9-4881-b0e3-950f8ae6de7c");
        TT5_MNCH = Context.getConceptService().getConceptByUuid("10cb2549-1b78-4483-9d30-01807b8e61e8");

        ancTT25.add(TT2_MNCH);
        ancTT25.add(TT3_MNCH);
        ancTT25.add(TT4_MNCH);
        ancTT25.add(TT5_MNCH);

        ultrasoundValues.add(AFI);
        ultrasoundValues.add(GWU);
        ultrasoundValues.add(EFW);
        ultrasoundValues.add(FHR);
        ultrasoundValues.add(NF);
                
        pregnanciesRisks.add(hypertensionDisorders);
        pregnanciesRisks.add(prematureDelivery);
        pregnanciesRisks.add(birthDisabilities);
        pregnanciesRisks.add(previousCS);
        pregnanciesRisks.add(stillbirth);
        pregnanciesRisks.add(RecurrentAbortion3Times);
        pregnanciesRisks.add(deliveryNewbornUnder25Kg);
        pregnanciesRisks.add(macrosomia);
        pregnanciesRisks.add(multiplePregnancy);
        pregnanciesRisks.add(vaginalBleedingDuringPregnancy);


        HcAncEnrollmentForm = Context.getFormService().getFormByUuid("867408ee-3b46-46ee-951f-aa521c47ec0f");
        HcAncContactForm = Context.getFormService().getFormByUuid("2a9c2299-2e93-4698-a4dd-75ac3a23f508");

        HcAncForms.add(HcAncContactForm);
        HcAncForms.add(HcAncEnrollmentForm);

        ancVisit = Context.getEncounterService().getEncounterTypeByUuid("a703372d-28b7-4831-9817-ee385c8c47d8");
    }



    @Override
    public String getReportName() {
        return "HC ANC HMIS Indicator Report";
    }

    public void setup() throws Exception {
        log.info("Setting up report: " + getReportName());
        setUpProperties();

        //Monthly report set-up


        Properties properties = new Properties();
        properties.setProperty("hierarchyFields", "countyDistrict:District");


        // ANC Report Definition: Start

        ReportDefinition MonthlyRd = new ReportDefinition();
        MonthlyRd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        MonthlyRd.addParameter(new Parameter("endDate", "End Date", Date.class));

        MonthlyRd.addParameter(new Parameter("location", "Location", AllLocation.class, properties));

        MonthlyRd.setName(getReportName());

        MonthlyRd.addDataSetDefinition(createMonthlyLocationDataSet(),
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));

        // ANC Report Definition: End

     

        Helper.saveReportDefinition(MonthlyRd);


        ReportDesign quarterlyDesign = Helper.createRowPerPatientXlsOverviewReportDesign(MonthlyRd,
                "ANC_HC_Monthly_Indicator_Report.xls", "HC ANC HMIS Indicator Report (Excel) ", null);
        Properties MonthlyProps = new Properties();
        MonthlyProps.put("repeatingSections", "sheet:1,dataset:ANCReport");
        MonthlyProps.put("sortWeight","5000");
        quarterlyDesign.setProperties(MonthlyProps);
        Helper.saveReportDesign(quarterlyDesign);

    }

    //Create Quarterly Encounter Data set

    public LocationHierachyIndicatorDataSetDefinition createMonthlyLocationDataSet() {

        LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
                createMonthlyBaseDataSet());
        ldsd.addBaseDefinition(createMonthlyBaseDataSet());
        ldsd.setName("ANCReport");
        ldsd.addParameter(new Parameter("startDate", "From", Date.class));
        ldsd.addParameter(new Parameter("endDate", "To", Date.class));
        ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));

        return ldsd;
    }


    // create quarterly cohort Data set
    private CohortIndicatorDataSetDefinition createMonthlyBaseDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("ANCReport");
        dsd.addParameter(new Parameter("startDate", "From", Date.class));
        dsd.addParameter(new Parameter("endDate", "To", Date.class));
        createQuarterlyIndicators(dsd);
        return dsd;
    }

    private void createQuarterlyIndicators(CohortIndicatorDataSetDefinition dsd) {

        //===================================================//
        // 1. ANC New Registrations/ CPN Nouvelles inscrites //
        //===================================================//

        SqlCohortDefinition ancNewRegisteredClients=new SqlCohortDefinition();
        ancNewRegisteredClients.setName("ancNewRegisteredClients");
        ancNewRegisteredClients.setQuery("SELECT DISTINCT person_id FROM obs WHERE concept_id = "+ ancContact.getConceptId() +" AND value_numeric = 1 " +
                "AND obs_datetime between :startDate and  DATE_ADD(:endDate, INTERVAL 1 DAY) " +
                "AND encounter_id in (SELECT encounter_id from encounter e WHERE form_id = "+HcAncEnrollmentForm.getFormId()+") ");
        ancNewRegisteredClients.addParameter(new Parameter("startDate","startDate",Date.class));
        ancNewRegisteredClients.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ancNewRegisteredClientsIndicatorNumerator = Indicators.newCountIndicator("ANC New Registrations/ CPN Nouvelles inscrites Indicator", ancNewRegisteredClients,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC1", "ANC New Registrations/ CPN Nouvelles inscrites",
                new Mapped(ancNewRegisteredClientsIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");



        //=====================================================================================================//
        // 2. ANC new registrations under 14 years/ CPN Grossesses chez les femmes de moins de 14 ans//
        //=====================================================================================================//

        SqlCohortDefinition ANCNewRegistrationsUnder14Years=new SqlCohortDefinition();
        ANCNewRegistrationsUnder14Years.setName("ANCNewRegistrationsUnder14Years");
        ANCNewRegistrationsUnder14Years.setQuery("SELECT DISTINCT o.person_id FROM obs o JOIN person p ON o.person_id = p.person_id " +
                "WHERE concept_id = "+ ancContact.getConceptId() +" AND value_numeric = 1 " +
                "AND obs_datetime between :startDate and  DATE_ADD(:endDate, INTERVAL 1 DAY) " +
                "AND encounter_id in (SELECT encounter_id from encounter e WHERE form_id = "+HcAncEnrollmentForm.getFormId()+") " +
                "AND TIMESTAMPDIFF(YEAR, p.birthdate, o.obs_datetime) < 14" );
        ANCNewRegistrationsUnder14Years.addParameter(new Parameter("startDate","startDate",Date.class));
        ANCNewRegistrationsUnder14Years.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANCNewRegistrationsUnder14YearsIndicatorNumerator = Indicators.newCountIndicator("ANC new registrations under 14 years/ CPN Grossesses chez les femmes de moins de 14 ans", ANCNewRegistrationsUnder14Years,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC2", "ANC new registrations under 14 years/ CPN Grossesses chez les femmes de moins de 14 ans",
                new Mapped(ANCNewRegistrationsUnder14YearsIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 3. ANC new registrations aged 14-17 years/ CPN Grossesses chez les femmes de 15-17 ans//                 //
        //==========================================================================================================//

        SqlCohortDefinition ANCNewRegistrationsAged1417Years=new SqlCohortDefinition();
        ANCNewRegistrationsAged1417Years.setName("ANCNewRegistrationsAged1417Years");
        ANCNewRegistrationsAged1417Years.setQuery("SELECT DISTINCT o.person_id FROM obs o JOIN person p ON o.person_id = p.person_id " +
                "WHERE concept_id = "+ ancContact.getConceptId() +" AND value_numeric = 1 " +
                "AND obs_datetime between :startDate and  DATE_ADD(:endDate, INTERVAL 1 DAY) " +
                "AND encounter_id in (SELECT encounter_id from encounter e WHERE form_id = "+HcAncEnrollmentForm.getFormId()+") " +
                "AND TIMESTAMPDIFF(YEAR, p.birthdate, o.obs_datetime) BETWEEN 14 AND 17");
        ANCNewRegistrationsAged1417Years.addParameter(new Parameter("startDate","startDate",Date.class));
        ANCNewRegistrationsAged1417Years.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANCNewRegistrationsAged1417YearsIndicatorNumerator = Indicators.newCountIndicator("ANC new registrations aged 14-17 years/ CPN Grossesses chez les femmes de 15-17 ans Indicator", ANCNewRegistrationsAged1417Years,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC3", "ANC new registrations aged 14-17 years/ CPN Grossesses chez les femmes de 15-17 ans",
                new Mapped(ANCNewRegistrationsAged1417YearsIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 4. ANC new registrations aged 18-19 years/ CPN Grossesses chez les femmes agees 18-19 ans//              //
        //==========================================================================================================//

        SqlCohortDefinition ANCNewRegistrationsAged1819Years=new SqlCohortDefinition();
        ANCNewRegistrationsAged1819Years.setName("ANCNewRegistrationsAged1819Years");
        ANCNewRegistrationsAged1819Years.setQuery("SELECT DISTINCT o.person_id FROM obs o JOIN person p ON o.person_id = p.person_id " +
                "WHERE concept_id = "+ ancContact.getConceptId() +" AND value_numeric = 1 " +
                "AND obs_datetime between :startDate and  DATE_ADD(:endDate, INTERVAL 1 DAY) " +
                "AND encounter_id in (SELECT encounter_id from encounter e WHERE form_id = "+HcAncEnrollmentForm.getFormId()+") " +
                "AND TIMESTAMPDIFF(YEAR, p.birthdate, o.obs_datetime) BETWEEN 18 AND 19");
        ANCNewRegistrationsAged1819Years.addParameter(new Parameter("startDate","startDate",Date.class));
        ANCNewRegistrationsAged1819Years.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANCNewRegistrationsAged1819YearsIndicatorNumerator = Indicators.newCountIndicator("ANC new registrations aged 18-19 years/ CPN Grossesses chez les femmes agees 18-19 ans", ANCNewRegistrationsAged1819Years,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC4", "ANC new registrations aged 18-19 years/ CPN Grossesses chez les femmes agees 18-19 ans",
                new Mapped(ANCNewRegistrationsAged1819YearsIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 5. ANC First standard contacts (Within 12 Weeks)/ CPN Première contact standard (endeans 12 semaines)    //
        //==========================================================================================================//

        SqlCohortDefinition ANCFirstStandardContacts12Weeks=new SqlCohortDefinition();
        ANCFirstStandardContacts12Weeks.setName("ANCFirstStandardContacts12Weeks");
        String query = "SELECT DISTINCT person_id FROM obs " +
                "WHERE concept_id = " + gestationalAge.getConceptId() + " " +
                "AND value_numeric <= 12 " +
                "AND encounter_id IN ( " +
                "SELECT encounter_id FROM obs " +
                "WHERE concept_id = " + ancContact.getConceptId() + " " +
                "AND value_numeric = 1 " +
                "AND encounter_id IN ( " +
                "SELECT encounter_id FROM encounter " +
                "WHERE form_id IN ( " +
                "SELECT form_id FROM form WHERE uuid = '2a9c2299-2e93-4698-a4dd-75ac3a23f508' " +
                ") " +
                ") " +
                "AND obs_datetime BETWEEN :startDate AND DATE_ADD(:endDate, INTERVAL 1 DAY) " +
                ")";

        ANCFirstStandardContacts12Weeks.setQuery(query);

        ANCFirstStandardContacts12Weeks.addParameter(new Parameter("startDate","startDate",Date.class));
        ANCFirstStandardContacts12Weeks.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANCFirstStandardContacts12WeeksIndicatorNumerator = Indicators.newCountIndicator("ANC First standard contacts (Within 12 Weeks)/ CPN Première contact standard (endeans 12 semaines) ", ANCFirstStandardContacts12Weeks,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC5", "ANC First standard contacts (Within 12 Weeks)/ CPN Première contact standard (endeans 12 semaines) ",
                new Mapped(ANCFirstStandardContacts12WeeksIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 6. ANC 4 contacts standards / CPN femmes ayant fait 4 contacts standard                                   //
        //==========================================================================================================//
        SqlCohortDefinition ANC4ContactsStandards =new SqlCohortDefinition();
        ANC4ContactsStandards.setName("ANCRiskPregnancyDetected");
        ANC4ContactsStandards.setQuery(
                "SELECT DISTINCT o.person_id \n" +
                        "FROM obs o \n" +
                        "JOIN encounter e ON o.encounter_id = e.encounter_id \n" +
                        "WHERE o.encounter_id IN ( \n" +
                        "    SELECT encounter_id \n" +
                        "    FROM encounter \n" +
                        "    WHERE form_id IN ( \n" +
                        "        SELECT form_id \n" +
                        "        FROM form \n" +
                        "        WHERE uuid = '2a9c2299-2e93-4698-a4dd-75ac3a23f508' \n" +
                        "    ) \n" +
                        ") \n" +
                        "AND o.concept_id = " + gestationalAgeLMP.getConceptId() + " \n" +
                        "AND o.value_numeric = 30 \n" +
                        "AND EXISTS ( \n" +
                        "    SELECT 1 \n" +
                        "    FROM obs o2 \n" +
                        "    WHERE o2.encounter_id = o.encounter_id \n" +
                        "    AND o2.concept_id = " + ancContact.getConceptId() + " \n" +
                        "    AND o2.value_numeric = 4 \n" +
                        ") \n" +
                        "AND o.obs_datetime BETWEEN :startDate AND DATE_ADD(:endDate, INTERVAL 1 DAY)"
        );

        ANC4ContactsStandards.addParameter(new Parameter("startDate","startDate",Date.class));
        ANC4ContactsStandards.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANC4ContactsStandardsIndicatorNumerator = Indicators.newCountIndicator("ANC 4 contacts standars / CPN femmes ayant fait 4 contacts standard", ANC4ContactsStandards,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC6", "ANC 4 contacts standars / CPN femmes ayant fait 4 contacts standard",
                new Mapped(ANC4ContactsStandardsIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 7. ANC 8 contacts / CPN femmes ayant fait 8 contacts                                                     //
        //==========================================================================================================//
        SqlCohortDefinition ANC8Contacts=new SqlCohortDefinition();
        ANC8Contacts.setName("ANC8Contacts");
        ANC8Contacts.setQuery("select distinct person_id from obs where concept_id = "+ ancContact.getConceptId() +" and value_numeric = 8 and obs_datetime between :startDate and  DATE_ADD(:endDate, INTERVAL 1 DAY) ");
        ANC8Contacts.addParameter(new Parameter("startDate","startDate",Date.class));
        ANC8Contacts.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANC8ContactsIndicatorNumerator = Indicators.newCountIndicator("ANC 8 contacts / CPN femmes ayant fait 8 contacts", ANC8Contacts,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC7", "ANC 8 contacts / CPN femmes ayant fait 8 contacts",
                new Mapped(ANC8ContactsIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 8. ANC risk pregnancy detected / CPN grossesses à haut risques dépistées                                 //
        //==========================================================================================================//

        SqlCohortDefinition ANCRiskPregnancyDetected =new SqlCohortDefinition();
        ANCRiskPregnancyDetected.setName("ANCRiskPregnancyDetected");
        ANCRiskPregnancyDetected.setQuery(
                "SELECT o.person_id \n" +
                        "FROM obs o \n" +
                        "JOIN concept c ON o.concept_id = c.concept_id \n" +
                        "WHERE c.uuid IN ( \n" +
                        "  '042d3875-0e29-4d16-bc5b-8c8a40060c92',\n" +
                        "  '49bed4cd-cad1-4a05-bbca-69e00fc92d2a',\n" +
                        "  'c83a2371-ce82-4d15-bf0e-9bd8a528e509',\n" +
                        "  '778553cf-55f8-4173-91de-08616142f17f',\n" +
                        "  '1e154024-518d-449c-8f40-6d3965ef120d',\n" +
                        "  'f3429526-d600-4247-b5e1-557dc7c178dc',\n" +
                        "  'f8dbde0c-9915-4942-9d26-047790ce9863',\n" +
                        "  '891ca104-be06-4997-87cb-a40ac5a8422d',\n" +
                        "  '69f193b7-9f8e-4b7d-be2c-3f82994eb44d',\n" +
                        "  'e8f52434-9728-4502-b58e-2eb51256d50a' \n" +
                        ") \n" +
                        "AND o.value_coded = " + answerYes.getConceptId() + " \n" +
                        "AND o.obs_datetime BETWEEN :startDate AND DATE_ADD(:endDate, INTERVAL 1 DAY) \n" +
                        "AND o.encounter_id IN ( \n" +
                        "    SELECT encounter_id \n" +
                        "    FROM encounter \n" +
                        "    WHERE form_id IN ( \n" +
                        "        SELECT form_id \n" +
                        "        FROM form \n" +
                        "        WHERE uuid = '2a9c2299-2e93-4698-a4dd-75ac3a23f508' \n" +
                        "    ) \n" +
                        ")"
        );
        ANCRiskPregnancyDetected.addParameter(new Parameter("startDate","startDate",Date.class));
        ANCRiskPregnancyDetected.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANCRiskPregnancyDetectedIndicatorNumerator = Indicators.newCountIndicator("ANC risk pregnancy detected / CPN grossesses à haut risques dépistées", ANCRiskPregnancyDetected,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC8", " ANC risk pregnancy detected / CPN grossesses à haut risques dépistées",
                new Mapped(ANCRiskPregnancyDetectedIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 9. Pregnant woman with previous scar referred to Hospital at last ANC (in last trimester)/               //
        // Femmes enceintes avec uterus cicatriciel transférées lors de la dernière CPN (dernier trimestre)         //
        //==========================================================================================================//


        //==========================================================================================================//
        // 10. ANC TT 1 given / CPN VAT1                                                                            //
        //==========================================================================================================//

        SqlCohortDefinition ANCTT1Given =new SqlCohortDefinition();
        ANCTT1Given.setName("ANCTT1Given");
        ANCTT1Given.setQuery(
                "SELECT DISTINCT person_id \n" +
                        "FROM obs \n" +
                        "WHERE concept_id IN (" + TT1_MNCH.getConceptId() + ") \n" +
                        "AND obs_datetime BETWEEN :startDate AND DATE_ADD(:endDate, INTERVAL 1 DAY) \n" +
                        "AND encounter_id IN ( \n" +
                        "    SELECT encounter_id \n" +
                        "    FROM encounter \n" +
                        "    WHERE form_id IN ( \n" +
                        "        SELECT form_id \n" +
                        "        FROM form \n" +
                        "        WHERE uuid = '2a9c2299-2e93-4698-a4dd-75ac3a23f508' \n" +
                        "    ) \n" +
                        ")"
        );
        ANCTT1Given.addParameter(new Parameter("startDate","startDate",Date.class));
        ANCTT1Given.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANCTT1GivenIndicatorNumerator = Indicators.newCountIndicator("ANC TT 1 given / CPN VAT1", ANCTT1Given,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC10", " ANC TT 1 given / CPN VAT1",
                new Mapped(ANCTT1GivenIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 11. ANC TT 2 to 5 given / CPN VAT 2 à 5                                                                      //
        //==========================================================================================================//

        SqlCohortDefinition ANCTT2To5Given =new SqlCohortDefinition();
        ANCTT2To5Given.setName("ANCTT2To5Given");
        ANCTT2To5Given.setQuery(
                "SELECT DISTINCT o.person_id \n" +
                        "FROM obs o \n" +
                        "JOIN concept c ON o.concept_id = c.concept_id \n" +
                        "WHERE o.obs_datetime BETWEEN :startDate AND DATE_ADD(:endDate, INTERVAL 1 DAY) \n" +
                        "AND o.encounter_id IN ( \n" +
                        "    SELECT encounter_id \n" +
                        "    FROM encounter \n" +
                        "    WHERE form_id IN ( \n" +
                        "        SELECT form_id \n" +
                        "        FROM form \n" +
                        "        WHERE uuid = '2a9c2299-2e93-4698-a4dd-75ac3a23f508' \n" +
                        "    ) \n" +
                        ") \n" +
                        "AND c.uuid IN ( \n" +
                        "    '2344ec1b-1727-4350-849d-8c878dd4d3d7', \n" +
                        "    '5c95c913-2d15-4640-8ded-0a6a6aaaff01', \n" +
                        "    '10cb2549-1b78-4483-9d30-01807b8e61e8', \n" +
                        "    '23e213d6-e7f9-4881-b0e3-950f8ae6de7c' \n" +
                        ")"
        );
        ANCTT2To5Given.addParameter(new Parameter("startDate","startDate",Date.class));
        ANCTT2To5Given.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANCTT2To5GivenIndicatorNumerator = Indicators.newCountIndicator("ANC TT 2 to 5 given / CPN VAT 2 à 5", ANCTT2To5Given,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC11", "ANC TT 2 to 5 given / CPN VAT 2 à 5",
                new Mapped(ANCTT2To5GivenIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 12. ANC TT new registrations fully vaccinated / CPN VAT Nouvelles inscrites complètement vaccinées       //
        //==========================================================================================================//

        SqlCohortDefinition ANCTTnewregistrationsFullyVaccinated  =new SqlCohortDefinition();
        ANCTTnewregistrationsFullyVaccinated.setName("ANCTTnewregistrationsFullyVaccinated");
        ANCTTnewregistrationsFullyVaccinated.setQuery(
                "SELECT DISTINCT person_id \n" +
                        "FROM obs \n" +
                        "WHERE obs_datetime BETWEEN :startDate AND DATE_ADD(:endDate, INTERVAL 1 DAY) \n" +
                        "AND encounter_id IN ( \n" +
                        "    SELECT encounter_id \n" +
                        "    FROM encounter \n" +
                        "    WHERE form_id IN ( \n" +
                        "        SELECT form_id \n" +
                        "        FROM form \n" +
                        "        WHERE uuid IN ( \n" +
                        "            '2a9c2299-2e93-4698-a4dd-75ac3a23f508', \n" +
                        "            '867408ee-3b46-46ee-951f-aa521c47ec0f' \n" +
                        "        ) \n" +
                        "    ) \n" +
                        ") \n" +
                        "AND concept_id = " + ancTTFull.getConceptId()
        );
        ANCTTnewregistrationsFullyVaccinated.addParameter(new Parameter("startDate","startDate",Date.class));
        ANCTTnewregistrationsFullyVaccinated.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANCTTnewregistrationsFullyVaccinatedIndicatorNumerator = Indicators.newCountIndicator("ANC TT new registrations fully vaccinated / CPN VAT Nouvelles inscrites complètement vaccinées", ANCTTnewregistrationsFullyVaccinated,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC12", "ANC TT new registrations fully vaccinated / CPN VAT Nouvelles inscrites complètement vaccinées",
                new Mapped(ANCTTnewregistrationsFullyVaccinatedIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 13. ANC new registrations who received full course of Iron and Folic Acid supplements (90 tablets)/      //
        // CPN nouvelles inscrites qui ont reçu 90 Comprimés de Fer et Acide Folique                                //
        //==========================================================================================================//

        SqlCohortDefinition ANCReceivedFullCourseIronFolicAcid  =new SqlCohortDefinition();
        ANCReceivedFullCourseIronFolicAcid.setName("ANCReceivedFullCourseIronFolicAcid");
        ANCReceivedFullCourseIronFolicAcid.setQuery(
                "SELECT DISTINCT person_id \n" +
                        "FROM obs \n" +
                        "WHERE concept_id = " + preventTreatmentAnc.getConceptId() + " \n" +
                        "AND value_coded = " + ironFolicAcidFullReceived.getConceptId() + " \n" +
                        "AND encounter_id IN ( \n" +
                        "    SELECT encounter_id \n" +
                        "    FROM obs \n" +
                        "    WHERE concept_id = " + ancContact.getConceptId() + " \n" +
                        "    AND value_numeric = 1 \n" +
                        "    AND encounter_id IN ( \n" +
                        "        SELECT encounter_id \n" +
                        "        FROM encounter \n" +
                        "        WHERE form_id IN ( \n" +
                        "            SELECT form_id \n" +
                        "            FROM form \n" +
                        "            WHERE uuid IN ( \n" +
                        "                '2a9c2299-2e93-4698-a4dd-75ac3a23f508', \n" +
                        "                '867408ee-3b46-46ee-951f-aa521c47ec0f' \n" +
                        "            ) \n" +
                        "        ) \n" +
                        "    ) \n" +
                        ") \n" +
                        "AND obs_datetime BETWEEN :startDate AND DATE_ADD(:endDate, INTERVAL 1 DAY)"
        );

        ANCReceivedFullCourseIronFolicAcid.addParameter(new Parameter("startDate","startDate",Date.class));
        ANCReceivedFullCourseIronFolicAcid.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANCReceivedFullCourseIronFolicAcidIndicatorNumerator = Indicators.newCountIndicator("ANC new registrations who received full course of Iron and Folic Acid supplements (90 tablets)/ CPN nouvelles inscrites qui ont reçu 90 Comprimés de Fer et Acide Folique", ANCReceivedFullCourseIronFolicAcid,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC13", "ANC new registrations who received full course of Iron and Folic Acid supplements (90 tablets)/ CPN nouvelles inscrites qui ont reçu 90 Comprimés de Fer et Acide Folique",
                new Mapped(ANCReceivedFullCourseIronFolicAcidIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 14. ANC Insecticide Treated Bed nets distributed / CPN Moustiquaires Imprégnées d'Insecticide distribuées//
        //==========================================================================================================//

        SqlCohortDefinition ANCInsecticideTreatedBedNetsDistributed   =new SqlCohortDefinition();
        ANCInsecticideTreatedBedNetsDistributed.setName("ANCInsecticideTreatedBedNetsDistributed");
        ANCInsecticideTreatedBedNetsDistributed.setQuery(
                "SELECT DISTINCT person_id \n" +
                        "FROM obs \n" +
                        "WHERE concept_id = " + preventTreatmentAnc.getConceptId() + " \n" +
                        "AND value_coded = " + netBeds.getConceptId() + " \n" +
                        "AND encounter_id IN ( \n" +
                        "    SELECT encounter_id \n" +
                        "    FROM obs \n" +
                        "    WHERE concept_id = " + ancContact.getConceptId() + " \n" +
                        "    AND value_numeric = 1 \n" +
                        "    AND encounter_id IN ( \n" +
                        "        SELECT encounter_id \n" +
                        "        FROM encounter \n" +
                        "    WHERE form_id IN ( \n" +
                        "        SELECT form_id \n" +
                        "        FROM form \n" +
                        "        WHERE uuid IN ( \n" +
                        "            '2a9c2299-2e93-4698-a4dd-75ac3a23f508', \n" +
                        "            '867408ee-3b46-46ee-951f-aa521c47ec0f' \n" +
                        "        ) \n" +
                        "    ) \n" +
                        ") \n" +
                        ") \n" +
                        "AND obs_datetime BETWEEN :startDate AND DATE_ADD(:endDate, INTERVAL 1 DAY)"
        );
        ANCInsecticideTreatedBedNetsDistributed.addParameter(new Parameter("startDate","startDate",Date.class));
        ANCInsecticideTreatedBedNetsDistributed.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANCInsecticideTreatedBedNetsDistributedIndicatorNumerator = Indicators.newCountIndicator("ANC Insecticide Treated Bed nets distributed / CPN Moustiquaires Imprégnées d'Insecticide distribuées", ANCInsecticideTreatedBedNetsDistributed,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC14", "ANC Insecticide Treated Bed nets distributed / CPN Moustiquaires Imprégnées d'Insecticide distribuées",
                new Mapped(ANCInsecticideTreatedBedNetsDistributedIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");


        //==========================================================================================================//
        // 15. ANC new registrations screened for malnutrition (MUAC) / CPN nouvelles inscrites dépistées pour la malnutrition (MUAC)//
        //==========================================================================================================//

        SqlCohortDefinition ANCScreenedForMUAC  =new SqlCohortDefinition();
        ANCScreenedForMUAC.setName("ANCScreenedForMUAC");
        ANCScreenedForMUAC.setQuery(
                "SELECT DISTINCT o1.person_id \n" +
                        "FROM obs o1 \n" +
                        "JOIN obs o2 ON o1.person_id = o2.person_id \n" +
                        "WHERE o1.concept_id = " + muacScreening.getConceptId() + " \n" +
                        "AND o1.obs_datetime BETWEEN :startDate AND DATE_ADD(:endDate, INTERVAL 1 DAY) \n" +
                        "AND o1.encounter_id IN ( \n" +
                        "    SELECT encounter_id \n" +
                        "    FROM encounter \n" +
                        "    WHERE form_id IN ( \n" +
                        "        SELECT form_id \n" +
                        "        FROM form \n" +
                        "        WHERE uuid IN ( \n" +
                        "            '2a9c2299-2e93-4698-a4dd-75ac3a23f508', \n" +
                        "            '867408ee-3b46-46ee-951f-aa521c47ec0f' \n" +
                        "        ) \n" +
                        "    ) \n" +
                        ") \n" +
                        "AND o2.concept_id = " + ancContact.getConceptId() + " \n" +
                        "AND o2.value_numeric = 1"
        );
        ANCScreenedForMUAC.addParameter(new Parameter("startDate","startDate",Date.class));
        ANCScreenedForMUAC.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANCScreenedForMUACIndicatorNumerator = Indicators.newCountIndicator("ANC new registrations screened for malnutrition (MUAC) / CPN nouvelles inscrites dépistées pour la malnutrition (MUAC) ", ANCScreenedForMUAC,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC15", "ANC new registrations screened for malnutrition (MUAC) / CPN nouvelles inscrites dépistées pour la malnutrition (MUAC) ",
                new Mapped(ANCScreenedForMUACIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 16. ANC new registrations screened who were malnourished (MUAC < 21 cm) / CPN nouvelles inscrites chez lesquelles la malnutrition est détectée (MUAC < 21 cm)//
        //==========================================================================================================//

        SqlCohortDefinition ANCScreenedForMUACUnder21  =new SqlCohortDefinition();
        ANCScreenedForMUACUnder21.setName("ANCScreenedForMUAC");
        ANCScreenedForMUACUnder21.setQuery(
                "SELECT DISTINCT o1.person_id \n" +
                        "FROM obs o1 \n" +
                        "JOIN obs o2 ON o1.person_id = o2.person_id \n" +
                        "WHERE o1.concept_id = " + muacScreening.getConceptId() + " \n" +
                        "AND o1.value_numeric < 21 \n" +
                        "AND o1.obs_datetime BETWEEN :startDate AND DATE_ADD(:endDate, INTERVAL 1 DAY) \n" +
                        "AND o1.encounter_id IN ( \n" +
                        "    SELECT encounter_id \n" +
                        "    FROM encounter \n" +
                        "    WHERE form_id IN ( \n" +
                        "        SELECT form_id \n" +
                        "        FROM form \n" +
                        "        WHERE uuid IN ( \n" +
                        "            '2a9c2299-2e93-4698-a4dd-75ac3a23f508', \n" +
                        "            '867408ee-3b46-46ee-951f-aa521c47ec0f' \n" +
                        "        ) \n" +
                        "    ) \n" +
                        ") \n" +
                        "AND o2.concept_id = " + ancContact.getConceptId() + " \n" +
                        "AND o2.value_numeric = 1"
        );
        ANCScreenedForMUACUnder21.addParameter(new Parameter("startDate","startDate",Date.class));
        ANCScreenedForMUACUnder21.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANCScreenedForMUACUnder21IndicatorNumerator = Indicators.newCountIndicator("ANC new registrations screened who were malnourished (MUAC < 21 cm) / CPN nouvelles inscrites chez lesquelles la malnutrition est détectée (MUAC < 21 cm) ", ANCScreenedForMUACUnder21,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC16", "ANC new registrations screened who were malnourished (MUAC < 21 cm) / CPN nouvelles inscrites chez lesquelles la malnutrition est détectée (MUAC < 21 cm) ",
                new Mapped(ANCScreenedForMUACUnder21IndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 17. ANC new registrations tested for anemia / CPN nouvelles inscrites testées pour l’anémie//
        //==========================================================================================================//

        SqlCohortDefinition ANCTestedForAnemia  =new SqlCohortDefinition();
        ANCTestedForAnemia.setName("ANCTestedForAnemia");
        ANCTestedForAnemia.setQuery(
                "SELECT DISTINCT e.patient_id \n" +
                        "FROM encounter e \n" +
                        "JOIN obs o ON e.encounter_id = o.encounter_id \n" +
                        "JOIN orders ord ON e.patient_id = ord.patient_id \n" +
                        "WHERE e.encounter_type = " + ancVisit.getEncounterTypeId() + " \n" +
                        "AND EXISTS ( \n" +
                        "    SELECT 1 \n" +
                        "    FROM obs o2 \n" +
                        "    WHERE o2.encounter_id = o.encounter_id \n" +
                        "    AND o2.concept_id = " + ancContact.getConceptId() + " \n" +
                        "    AND o2.value_numeric = 1 \n" +
                        ") \n" +
                        "AND ord.concept_id IN (" + anemiaTest.getConceptId() + ") \n" +
                        "AND DATE(e.encounter_datetime) = DATE(ord.date_activated) \n" +
                        "AND e.encounter_datetime BETWEEN :startDate AND DATE_ADD(:endDate, INTERVAL 1 DAY)"
        );
        ANCTestedForAnemia.addParameter(new Parameter("startDate","startDate",Date.class));
        ANCTestedForAnemia.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANCTestedForAnemiaIndicatorNumerator = Indicators.newCountIndicator("ANC new registrations tested for anemia / CPN nouvelles inscrites testées pour l’anémie", ANCTestedForAnemia,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC17", "ANC new registrations tested for anemia / CPN nouvelles inscrites testées pour l’anémie",
                new Mapped(ANCTestedForAnemiaIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        //   ANC new registrations with anemia (Moderate and Severe Hgb 7gr- 9.9gr/dl and <7gm/dl)//
        //==========================================================================================================//

        SqlCohortDefinition ANCAnemiaUnder11  =new SqlCohortDefinition();
        ANCAnemiaUnder11.setName("ANCAnemiaUnder11");
        ANCAnemiaUnder11.setQuery(
                "SELECT DISTINCT o.patient_id \n" +
                        "FROM orders o \n" +
                        "JOIN encounter e ON o.patient_id = e.patient_id \n" +
                        "JOIN obs ob ON o.patient_id = ob.person_id \n" +
                        "WHERE o.concept_id = " + anemiaTest.getConceptId() + " \n" +
                        "AND e.encounter_type = " + ancVisit.getEncounterTypeId() + " \n" +
                        "AND DATE(o.date_activated) = DATE(e.encounter_datetime) \n" +
                        "AND ob.concept_id = " + anemiaTest.getConceptId() + " \n" +
                        "AND ob.value_numeric < 9.9 \n" +
                        "AND o.date_activated BETWEEN :startDate AND DATE_ADD(:endDate, INTERVAL 1 DAY)"
        );
        ANCAnemiaUnder11.addParameter(new Parameter("startDate","startDate",Date.class));
        ANCAnemiaUnder11.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANCAnemiaUnder11IndicatorNumerator = Indicators.newCountIndicator("ANC new registrations with anemia Hb < 11 g/dL", ANCAnemiaUnder11,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC18", "ANC new registrations with anemia Hb < 11 g/dL",
                new Mapped(ANCAnemiaUnder11IndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 19. ANC new registrations syphilis tested/ CPN nouvelles inscrites testés pour la syphilis               //
        //==========================================================================================================//

        SqlCohortDefinition ANCSyphilisTested  =new SqlCohortDefinition();
        ANCSyphilisTested.setName("ANCSyphilisTested");
        ANCSyphilisTested.setQuery(
                "SELECT DISTINCT enc.patient_id \n" +
                        "FROM orders ord \n" +
                        "LEFT JOIN encounter enc ON enc.patient_id = ord.patient_id \n" +
                        "WHERE ord.concept_id = " + RPR.getConceptId() + " \n" +
                        "AND ord.voided = 0 \n" +
                        "AND DATE(ord.date_created) = DATE(enc.date_created) \n" +
                        "AND enc.form_id IN ( \n" +
                        "    SELECT form_id \n" +
                        "    FROM form \n" +
                        "    WHERE uuid IN ( \n" +
                        "        '2a9c2299-2e93-4698-a4dd-75ac3a23f508', \n" +
                        "        '867408ee-3b46-46ee-951f-aa521c47ec0f' \n" +
                        "    ) \n" +
                        ") \n" +
                        "AND enc.voided = 0 \n" +
                        "AND DATE(enc.date_created) >= DATE(:startDate) \n" +
                        "AND DATE(enc.date_created) <= DATE(:endDate)"
        );

        ANCSyphilisTested.addParameter(new Parameter("startDate","startDate",Date.class));
        ANCSyphilisTested.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANCSyphilisTestedIndicatorNumerator = Indicators.newCountIndicator("ANC new registrations syphilis tested/ CPN nouvelles inscrites testés pour la syphilis", ANCSyphilisTested,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC19", "ANC new registrations syphilis tested/ CPN nouvelles inscrites testés pour la syphilis",
                new Mapped(ANCSyphilisTestedIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 20. ANC new registrations syphilis tested positive/ CPN nouvelles inscrites testées positives pour la syphilis//
        //==========================================================================================================//

        SqlCohortDefinition ANCSyphilisTestedPositive  =new SqlCohortDefinition();
        ANCSyphilisTestedPositive.setName("ANCSyphilisTestedPositive");
        ANCSyphilisTestedPositive.setQuery(
                "SELECT DISTINCT person_id \n" +
                        "FROM obs o \n" +
                        "WHERE o.concept_id = " + RPR.getConceptId() + " \n" +
                        "AND o.obs_datetime BETWEEN :startDate AND DATE_ADD(:endDate, INTERVAL 1 DAY) \n" +
                        "AND o.value_coded = 703 \n" +
                        "AND o.person_id IN ( \n" +
                        "    SELECT enc.patient_id \n" +
                        "    FROM orders ord \n" +
                        "    LEFT JOIN encounter enc ON enc.patient_id = ord.patient_id \n" +
                        "    WHERE ord.concept_id = " + RPR.getConceptId() + " \n" +
                        "    AND ord.voided = 0 \n" +
                        "    AND DATE(ord.date_created) = DATE(enc.date_created) \n" +
                        "AND enc.form_id IN ( \n" +
                        "        SELECT form_id \n" +
                        "        FROM form \n" +
                        "        WHERE uuid IN ( \n" +
                        "            '2a9c2299-2e93-4698-a4dd-75ac3a23f508', \n" +
                        "            '867408ee-3b46-46ee-951f-aa521c47ec0f' \n" +
                        "        ) \n" +
                        "    ) \n" +
                        "    AND enc.voided = 0 \n" +
                        ")"
        );
        ANCSyphilisTestedPositive.addParameter(new Parameter("startDate","startDate",Date.class));
        ANCSyphilisTestedPositive.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANCSyphilisTestedPositiveIndicatorNumerator = Indicators.newCountIndicator("ANC new registrations syphilis tested positive/ CPN nouvelles inscrites testées positives pour la syphilis", ANCSyphilisTestedPositive,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC20", "ANC new registrations syphilis tested positive/ CPN nouvelles inscrites testées positives pour la syphilis",
                new Mapped(ANCSyphilisTestedPositiveIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 21. ANC new registrations received ultrasound scan / CPN nouvelles inscrites qui ont recu ultrasound scan//
        //==========================================================================================================//

        SqlCohortDefinition ANCReceivedUltrasoundScan  =new SqlCohortDefinition();
        ANCReceivedUltrasoundScan.setName("ANCReceivedUltrasoundScan");
        ANCReceivedUltrasoundScan.setQuery(
                "SELECT DISTINCT o1.person_id \n" +
                        "FROM obs o1 \n" +
                        "JOIN concept c ON o1.concept_id = c.concept_id \n" +
                        "JOIN obs o2 ON o1.person_id = o2.person_id \n" +
                        "WHERE c.uuid IN ( \n" +
                        "    'f0b20606-a261-4da4-b138-0785399cc850', \n" +
                        "    'ee891321-3066-41c4-8fb4-22a8c6d46b5d', \n" +
                        "    'b209fbaa-e240-472f-bb57-0efd2586c0ad', \n" +
                        "    '76f7ba1f-b3d5-4bfa-a75f-d9ab2b7e59f8', \n" +
                        "    '57c934dd-a086-4e73-a9b8-3b9c1abef1b1' \n" +
                        ") \n" +
                        "AND o1.obs_datetime BETWEEN :startDate AND DATE_ADD(:endDate, INTERVAL 1 DAY) \n" +
                        "AND o1.encounter_id IN ( \n" +
                        "    SELECT encounter_id \n" +
                        "    FROM encounter \n" +
                        "    WHERE form_id IN ( \n" +
                        "        SELECT form_id \n" +
                        "        FROM form \n" +
                        "        WHERE uuid IN ( \n" +
                        "            '2a9c2299-2e93-4698-a4dd-75ac3a23f508', \n" +
                        "            '867408ee-3b46-46ee-951f-aa521c47ec0f' \n" +
                        "        ) \n" +
                        "    ) \n" +
                        ") \n" +
                        "AND o2.concept_id = " + ancContact.getConceptId() + " \n" +
                        "AND o2.value_numeric = 1"
        );
        ANCReceivedUltrasoundScan.addParameter(new Parameter("startDate","startDate",Date.class));
        ANCReceivedUltrasoundScan.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANCReceivedUltrasoundScanIndicatorNumerator = Indicators.newCountIndicator("ANC new registrations received ultrasound scan / CPN nouvelles inscrites qui ont recu ultrasound scan", ANCReceivedUltrasoundScan,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC21", "ANC new registrations received ultrasound scan / CPN nouvelles inscrites qui ont recu ultrasound scan",
                new Mapped(ANCReceivedUltrasoundScanIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 22. Number of pregnant women screened for HBV                                                            //
        //==========================================================================================================//

        SqlCohortDefinition ANCPregnantWomenScreenedForHBV       =new SqlCohortDefinition();
        ANCPregnantWomenScreenedForHBV.setName("ANCPregnantWomenScreenedForHBV");
        ANCPregnantWomenScreenedForHBV.setQuery(
                "SELECT DISTINCT enc.patient_id \n" +
                        "FROM orders ord \n" +
                        "LEFT JOIN encounter enc ON enc.patient_id = ord.patient_id \n" +
                        "WHERE ord.concept_id = " + AgHbs.getConceptId() + " \n" +
                        "AND ord.voided = 0 \n" +
                        "AND DATE(ord.date_created) = DATE(enc.date_created) \n" +
                        "AND enc.form_id IN ( \n" +
                        "    SELECT form_id \n" +
                        "    FROM form \n" +
                        "    WHERE uuid IN ( \n" +
                        "        '2a9c2299-2e93-4698-a4dd-75ac3a23f508', \n" +
                        "        '867408ee-3b46-46ee-951f-aa521c47ec0f' \n" +
                        "    ) \n" +
                        ") \n" +
                        "AND enc.voided = 0 \n" +
                        "AND DATE(enc.date_created) >= DATE(:startDate) \n" +
                        "AND DATE(enc.date_created) <= DATE(:endDate)"
        );

        ANCPregnantWomenScreenedForHBV.addParameter(new Parameter("startDate","startDate",Date.class));
        ANCPregnantWomenScreenedForHBV.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANCPregnantWomenScreenedForHBVIndicatorNumerator = Indicators.newCountIndicator("Number of pregnant women screened for HBV   ", ANCPregnantWomenScreenedForHBV,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC23", "Number of pregnant women screened for HBV   ",
                new Mapped(ANCPregnantWomenScreenedForHBVIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 23. Number of pregnant women with HBV infection defined by HBsAg-positive serological status                                                        //
        //==========================================================================================================//

        SqlCohortDefinition ANCPregnantWomenScreenedForHBVPositive =new SqlCohortDefinition();
        ANCPregnantWomenScreenedForHBVPositive.setName("ANCPregnantWomenScreenedForHBVPositive");
        ANCPregnantWomenScreenedForHBVPositive.setQuery(
                "SELECT DISTINCT person_id \n" +
                        "FROM obs o \n" +
                        "WHERE o.concept_id = " + AgHbs.getConceptId() + " \n" +
                        "AND o.obs_datetime BETWEEN :startDate AND DATE_ADD(:endDate, INTERVAL 1 DAY) \n" +
                        "AND o.value_coded = 703 \n" +
                        "AND o.person_id IN ( \n" +
                        "    SELECT enc.patient_id \n" +
                        "    FROM orders ord \n" +
                        "    LEFT JOIN encounter enc ON enc.patient_id = ord.patient_id \n" +
                        "    WHERE ord.concept_id = " + AgHbs.getConceptId() + " \n" +
                        "    AND ord.voided = 0 \n" +
                        "    AND DATE(ord.date_created) = DATE(enc.date_created) \n" +
                        "AND enc.form_id IN ( \n" +
                        "        SELECT form_id \n" +
                        "        FROM form \n" +
                        "        WHERE uuid IN ( \n" +
                        "            '2a9c2299-2e93-4698-a4dd-75ac3a23f508', \n" +
                        "            '867408ee-3b46-46ee-951f-aa521c47ec0f' \n" +
                        "        ) \n" +
                        "    ) \n" +
                        "    AND enc.voided = 0 \n" +
                        ")"
        );
        ANCPregnantWomenScreenedForHBVPositive.addParameter(new Parameter("startDate","startDate",Date.class));
        ANCPregnantWomenScreenedForHBVPositive.addParameter(new Parameter("endDate","endDate",Date.class));

        CohortIndicator ANCPregnantWomenScreenedForHBVPositiveIndicatorNumerator = Indicators.newCountIndicator("Number of pregnant women with HBV infection defined by HBsAg-positive serological status", ANCPregnantWomenScreenedForHBVPositive,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

        dsd.addColumn("ANC24", "Number of pregnant women with HBV infection defined by HBsAg-positive serological status",
                new Mapped(ANCPregnantWomenScreenedForHBVPositiveIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 30. Women accompanied by their partners during antenatal consultation ANC fourth visit                   //
        //==========================================================================================================//
//
//        SqlCohortDefinition ANCWomenWithPartnersFourthVisit =new SqlCohortDefinition();
//        ANCWomenWithPartnersFourthVisit.setName("ANCWomenWithPartnersFourthVisit");
//        ANCWomenWithPartnersFourthVisit.setQuery("SELECT DISTINCT o.person_id, e.encounter_datetime FROM obs o JOIN encounter e ON o.encounter_id = e.encounter_id\n" +
//                "WHERE o.concept_id = 6547 AND o.value_coded = "+ answerYes.getConceptId() +" AND e.encounter_type  = "+ancVisit.getEncounterTypeId()+" AND e.encounter_datetime BETWEEN :startDate and  DATE_ADD(:endDate, INTERVAL 1 DAY)\n" +
//                "AND EXISTS (SELECT 1 FROM obs o2 WHERE o2.encounter_id = e.encounter_id AND o2.concept_id = "+ ancContact.getConceptId() +" AND o2.value_numeric = 4");
//        ANCWomenWithPartnersFourthVisit.addParameter(new Parameter("startDate","startDate",Date.class));
//        ANCWomenWithPartnersFourthVisit.addParameter(new Parameter("endDate","endDate",Date.class));
//
//        CohortIndicator ANCWomenWithPartnersFourthVisitIndicatorNumerator = Indicators.newCountIndicator("Women accompanied by their partners during antenatal consultation ANC fourth visit", ANCWomenWithPartnersFourthVisit,
//                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
//
//        dsd.addColumn("ANC30", "Women accompanied by their partners during antenatal consultation ANC fourth visit",
//                new Mapped(ANCWomenWithPartnersFourthVisitIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

        //==========================================================================================================//
        // 31. Women accompanied by their partners during antenatal consultation ANC first visit                                                   //
        //==========================================================================================================//
//
//        SqlCohortDefinition ANCWomenWithPartnersFirstVisit =new SqlCohortDefinition();
//        ANCWomenWithPartnersFirstVisit.setName("ANCWomenWithPartnersFirstVisit");
//        ANCWomenWithPartnersFirstVisit.setQuery("SELECT DISTINCT o.person_id, e.encounter_datetime FROM obs o JOIN encounter e ON o.encounter_id = e.encounter_id\n" +
//                "WHERE o.concept_id = 6547 AND o.value_coded = "+ answerYes.getConceptId() +" AND e.encounter_type  = "+ancVisit.getEncounterTypeId()+" AND e.encounter_datetime BETWEEN :startDate and  DATE_ADD(:endDate, INTERVAL 1 DAY)\n" +
//                "AND EXISTS (SELECT 1 FROM obs o2 WHERE o2.encounter_id = e.encounter_id AND o2.concept_id = "+ ancContact.getConceptId() +" AND o2.value_numeric = 1");
//        ANCWomenWithPartnersFirstVisit.addParameter(new Parameter("startDate","startDate",Date.class));
//        ANCWomenWithPartnersFirstVisit.addParameter(new Parameter("endDate","endDate",Date.class));
//
//        CohortIndicator ANCWomenWithPartnersFirstVisitIndicatorNumerator = Indicators.newCountIndicator("Women accompanied by their partners during antenatal consultation ANC first visit", ANCWomenWithPartnersFirstVisit,
//                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
//
//        dsd.addColumn("ANC31", "Women accompanied by their partners during antenatal consultation ANC first visit",
//                new Mapped(ANCWomenWithPartnersFirstVisitIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

    }

}
