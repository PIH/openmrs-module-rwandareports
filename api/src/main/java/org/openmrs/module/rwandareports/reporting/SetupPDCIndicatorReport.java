package org.openmrs.module.rwandareports.reporting;
 
import java.util.*;

//import org.omg.CORBA.PRIVATE_MEMBER;
import org.openmrs.*;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.SqlEncounterQuery;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rwandareports.dataset.EncounterIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.dataset.LocationHierachyIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.indicator.EncounterIndicator;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.Indicators;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.AllProvider;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;
 
public class SetupPDCIndicatorReport {
        
        GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
        
        //properties
        private Program PDCProgram;
        
        private List<String> onOrAfterOnOrBefore = new ArrayList<String>();

        
        private Concept reasoForReferral;
        
        private Concept returnVisitDate;
                 
        private Concept breathingDangerSignsPresent;
         
        private Concept convulsionsDangerSignsPresent;         
         
        private Concept LethargyOrUnresponsivenessDangerSignsPresent;
         
        private Concept umbilicalCordRednessDangerSigns;        
         
        private Concept stiffNeckOrBulgingFontanellesDangerSigns;
         
        private Concept ASQAgeUsed;

        private Concept heightForAgeZScore;

        private Concept temperature;
          
        private Concept respiratoryRate;
        
        private Concept yes;
        
        private Concept socialEconomicAssistance;
        
        private Concept socialEconomicAssistanceAlreadyReceived;
        
        private Concept socialEconomicAssistanceRecommanded;
        
        private Concept socialEconomicAssistanceNotRecommanded;
        
        private List<Concept> socialAssistanceTypes=new ArrayList<Concept>();
        
        private Form PDCIntakeForm;
        
        private Form PDCReferralForm;
        
        private Form PDCVisitForm;
        
        
        private List<Form> intakeVisitForms=new ArrayList<Form>();
        
        private List<Form> PDCVisitForms=new ArrayList<Form>();
        
        private Concept intervalGrogth;
        
        private Concept birthWeight;
        
        private Concept correctedAge;        
        
        private Concept  weightForAgeZScore;
        
        private Concept HCForAgeZScore;
        
        private Concept weightForHeightZScore;
        
        private Concept headCircumference;
        
        private Concept weight;
        
        private Concept height;
        
        private Concept smallMuscleMovement;
        
        private Concept largeMuscleMovement;
        
        private Concept communication;
        
        private Concept problemSolving;
        
        private Concept personalSocial;
        
        private Concept abnormal;
        
        private Concept ECDCounselingSession;
        
        private Concept reasonForNotDoingFollowUp;

        private Concept reasonForExitingFromCare;
        
        private Concept intervalgrowthCoded;
        
        private Concept inadequate;

    private List<Concept> reasonsForReferral=new ArrayList<Concept>();
    private Concept lowBirthWeight;
    private Concept prematureBirth;
    private Concept hypoxicIschemicEncephalopathy;
    private Concept hydrocephalus;
    private Concept trisomy21;
    private Concept cleftLipOrPalate;
    private Concept otherDevelopmentalDelay;
    private Concept otherNoneCoded;
    private Concept severeMalnutrition;
    private Concept centralNervousSystemInfection;

    private Concept PDCweightForAgeZScore;
    private Concept na;
    private Concept zScoreGreaterThatMinesThreeAndLessThanTwo;
    private Concept zSccoreLessThanThree;
    private Concept PDCHeightForAgeZScore;
    private Concept PDCweightForHeightZScore;

    private EncounterType PDCEncounterType;
    private Concept PDCProgramEndReasons;
    private Concept LostToFollowUPOutcome;
    private Concept DeathCauseSevereDehydrationOutcome;
    private Concept DeathCauseHypoglycemiaOutcome;
    private Concept DeathCauseRespiratoryDistressOutcome;
    private Concept DeathCauseSevereInfectionOutcome;
    private Concept DeathUnknownCauseOutcome;
    private Concept whenToStartTreatmentCoded;
    private Concept startToday;





    public void setup() throws Exception {

                setUpProperties();
                
                
                ReportDefinition rd = new ReportDefinition();
                rd.addParameter(new Parameter("startDate", "Start Date", Date.class));
                rd.addParameter(new Parameter("endDate", "End Date", Date.class));
                
                Properties properties = new Properties();
                properties.setProperty("hierarchyFields", "countyDistrict:District");
                rd.addParameter(new Parameter("location", "Location", AllLocation.class, properties));

                Parameter providerParameter = new Parameter("provider","Provider", AllProvider.class);
                providerParameter.setRequired(false);
                rd.addParameter(providerParameter);

                rd.setName("PDC-Indicator Report");
                
                rd.addDataSetDefinition(createQuarterlyLocationDataSet(),
                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));

                ProgramEnrollmentCohortDefinition everEnrolled=Cohorts.createProgramEnrollment("PDCProgram", PDCProgram);
                InProgramCohortDefinition inProgram = Cohorts.createInProgram("PDCProgram", PDCProgram);
                rd.setBaseCohortDefinition(everEnrolled,new HashMap<String, Object>());

                Helper.saveReportDefinition(rd);
                
                
                ReportDesign monthlyDesign = Helper.createRowPerPatientXlsOverviewReportDesign(rd,"PDCIndicators.xls", "PDCIndicators", null);
                Properties monthlyProps = new Properties();
                monthlyProps.put("repeatingSections", "sheet:1,dataset:PDC Data Set");
                monthlyProps.put("sortWeight","5000");
                monthlyDesign.setProperties(monthlyProps);
                Helper.saveReportDesign(monthlyDesign);              
                
                
        }
        
        public void delete() {
                ReportService rs = Context.getService(ReportService.class);
                for (ReportDesign rd : rs.getAllReportDesigns(false)) {
                        if ("PDCIndicators".equals(rd.getName())) {
                                rs.purgeReportDesign(rd);
                        }
                }
                Helper.purgeReportDefinition("PDC-Indicator Report");
                
        }
        
        
        public LocationHierachyIndicatorDataSetDefinition createQuarterlyLocationDataSet() {
                
                LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
                        createQuarterlyEncounterBaseDataSet());
                ldsd.addBaseDefinition(createQuarterlyBaseDataSet());
                ldsd.setName("PDC Data Set");
                ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
                ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
                ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
//                ldsd.addParameter(new Parameter("provider","Provider",Provider.class));
                
                return ldsd;
        }
        
        private EncounterIndicatorDataSetDefinition createQuarterlyEncounterBaseDataSet() {
                
                EncounterIndicatorDataSetDefinition eidsd = new EncounterIndicatorDataSetDefinition();
                
                eidsd.setName("eidsd");
                eidsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
                eidsd.addParameter(new Parameter("endDate", "End Date", Date.class));
//                eidsd.addParameter(new Parameter("provider","Provider",Provider.class));
                
                createQuarterlyIndicators(eidsd);
                return eidsd;
        }
        
        private void createQuarterlyIndicators(EncounterIndicatorDataSetDefinition dsd) {
                        	
// PDC6N       	
        	/*
        	  SqlEncounterQuery visitWithNutritionDangerSignSreeningAndASQdocumentedQuery=new SqlEncounterQuery();
        	  visitWithNutritionDangerSignSreeningAndASQdocumentedQuery.setName("visitWithNutritionDangerSignSreeningAndASQdocumentedQuery");
        	  visitWithNutritionDangerSignSreeningAndASQdocumentedQuery.setQuery("select e.encounter_id from encounter e"
				+" inner join obs o1 on e.encounter_id=o1.encounter_id and o1.concept_id= "+breathingDangerSignsPresent.getConceptId()+" and o1.value_coded="+yes.getConceptId()+" and o1.voided=0"
				+" inner join obs o2 on e.encounter_id=o2.encounter_id and o2.concept_id= "+convulsionsDangerSignsPresent.getConceptId()+" and o2.value_coded="+yes.getConceptId()+" and o2.voided=0"
				+" inner join obs o3 on e.encounter_id=o3.encounter_id and o3.concept_id= "+LethargyOrUnresponsivenessDangerSignsPresent.getConceptId()+" and o3.value_coded="+yes.getConceptId()+" and o3.voided=0"
				+" inner join obs o4 on e.encounter_id=o4.encounter_id and o4.concept_id= "+umbilicalCordRednessDangerSigns.getConceptId()+" and o4.value_coded="+yes.getConceptId()+" and o4.voided=0"
				+" inner join obs o5 on e.encounter_id=o5.encounter_id and o5.concept_id= "+stiffNeckOrBulgingFontanellesDangerSigns.getConceptId()+" and o5.value_coded="+yes.getConceptId()+" and o5.voided=0"
				+" inner join obs o6 on e.encounter_id=o6.encounter_id and o6.concept_id= "+ASQAgeUsed.getConceptId()+" and o6.value_coded is not null and o6.voided=0"
				
				+" inner join obs o7 on e.encounter_id=o7.encounter_id and o7.concept_id= "+heightForAgeZScore.getConceptId()+" and o7.value_numeric is not null and o7.voided=0"
				+" inner join obs o8 on e.encounter_id=o8.encounter_id and o8.concept_id= "+temperature.getConceptId()+" and o8.value_numeric is not null and o8.voided=0"
				+" inner join obs o9 on e.encounter_id=o9.encounter_id and o9.concept_id= "+respiratoryRate.getConceptId()+" and o9.value_numeric is not null and o9.voided=0"
				+" inner join person p on e.patient_id=p.person_id and DATEDIFF(e.encounter_datetime,p.birthdate)>180 and DATEDIFF(e.encounter_datetime,p.birthdate)<=720" 
				+" where  e.form_id="+PDCVisitForm.getFormId()+" and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate");
        	  visitWithNutritionDangerSignSreeningAndASQdocumentedQuery.addParameter(new Parameter("startDate", "startDate", Date.class));
        	  visitWithNutritionDangerSignSreeningAndASQdocumentedQuery.addParameter(new Parameter("endDate", "endDate", Date.class));*/

            SqlEncounterQuery visitWithNutritionDangerSignSreeningAndASQdocumentedQuery=new SqlEncounterQuery();
            visitWithNutritionDangerSignSreeningAndASQdocumentedQuery.setName("visitWithNutritionDangerSignSreeningAndASQdocumentedQuery");
            visitWithNutritionDangerSignSreeningAndASQdocumentedQuery.setQuery("select e.encounter_id from encounter e"
                    +" inner join obs o1 on e.encounter_id=o1.encounter_id and o1.concept_id= "+breathingDangerSignsPresent.getConceptId()+" and o1.value_coded="+yes.getConceptId()+" and o1.voided=0"
                    +" inner join obs o2 on e.encounter_id=o2.encounter_id and o2.concept_id= "+convulsionsDangerSignsPresent.getConceptId()+" and o2.value_coded="+yes.getConceptId()+" and o2.voided=0"
                    +" inner join obs o3 on e.encounter_id=o3.encounter_id and o3.concept_id= "+LethargyOrUnresponsivenessDangerSignsPresent.getConceptId()+" and o3.value_coded="+yes.getConceptId()+" and o3.voided=0"
                    +" inner join obs o4 on e.encounter_id=o4.encounter_id and o4.concept_id= "+umbilicalCordRednessDangerSigns.getConceptId()+" and o4.value_coded="+yes.getConceptId()+" and o4.voided=0"
                    +" inner join obs o5 on e.encounter_id=o5.encounter_id and o5.concept_id= "+stiffNeckOrBulgingFontanellesDangerSigns.getConceptId()+" and o5.value_coded="+yes.getConceptId()+" and o5.voided=0"
                    +" inner join obs o6 on e.encounter_id!=o6.encounter_id and o6.concept_id= "+ASQAgeUsed.getConceptId()+" and o6.value_coded is not null and o6.voided=0 and DATEDIFF(e.encounter_datetime,o6.obs_datetime)<=180 and o6.value_coded!="+na.getConceptId()+" "
                    +" inner join obs o7 on e.encounter_id=o7.encounter_id and o7.concept_id= "+PDCweightForAgeZScore.getConceptId()+" and o7.value_coded is not null and o7.voided=0"
                    +" inner join person p on e.patient_id=p.person_id and DATEDIFF(e.encounter_datetime,p.birthdate)>180 and DATEDIFF(e.encounter_datetime,p.birthdate)<=720"
                    +" where  e.form_id="+PDCVisitForm.getFormId()+" and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate");
            visitWithNutritionDangerSignSreeningAndASQdocumentedQuery.addParameter(new Parameter("startDate", "startDate", Date.class));
            visitWithNutritionDangerSignSreeningAndASQdocumentedQuery.addParameter(new Parameter("endDate", "endDate", Date.class));
              
        	  
        	  
        	  EncounterIndicator visitWithNutritionDangerSignSreeningAndASQdocumentedQueryIndicator = new EncounterIndicator();
        	  visitWithNutritionDangerSignSreeningAndASQdocumentedQueryIndicator.setName("visitWithNutritionDangerSignSreeningAndASQdocumentedQueryIndicator");
        	  visitWithNutritionDangerSignSreeningAndASQdocumentedQueryIndicator.setEncounterQuery(new Mapped<EncounterQuery>(visitWithNutritionDangerSignSreeningAndASQdocumentedQuery,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));
              
              dsd.addColumn(visitWithNutritionDangerSignSreeningAndASQdocumentedQueryIndicator); 
        	  
   // PDC6D     	  
        	  
        	  SqlEncounterQuery visitDoneWhenChildIsBetween6And24MonthsQuery=new SqlEncounterQuery();
        	  visitDoneWhenChildIsBetween6And24MonthsQuery.setName("visitDoneWhenChildIsBetween6And24MonthsQuery");
        	  visitDoneWhenChildIsBetween6And24MonthsQuery.setQuery("select e.encounter_id from encounter e"
				+" inner join person p on e.patient_id=p.person_id and DATEDIFF(e.encounter_datetime,p.birthdate)>180 and DATEDIFF(e.encounter_datetime,p.birthdate)<=720" 
				+" where  e.form_id="+PDCVisitForm.getFormId()+" and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate");
        	  visitDoneWhenChildIsBetween6And24MonthsQuery.addParameter(new Parameter("startDate", "startDate", Date.class));
        	  visitDoneWhenChildIsBetween6And24MonthsQuery.addParameter(new Parameter("endDate", "endDate", Date.class));
              
        	  
        	  EncounterIndicator visitDoneWhenChildIsBetween6And24MonthsQueryIndicator = new EncounterIndicator();
        	  visitDoneWhenChildIsBetween6And24MonthsQueryIndicator.setName("visitDoneWhenChildIsBetween6And24MonthsQueryIndicator");
        	  visitDoneWhenChildIsBetween6And24MonthsQueryIndicator.setEncounterQuery(new Mapped<EncounterQuery>(visitDoneWhenChildIsBetween6And24MonthsQuery,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));
              
              dsd.addColumn(visitDoneWhenChildIsBetween6And24MonthsQueryIndicator); 
              
              
// PDC9N       	
          	
        	  SqlEncounterQuery visitWithDangerSignsDocumentedQuery=new SqlEncounterQuery();
        	  visitWithDangerSignsDocumentedQuery.setName("visitWithDangerSignsDocumentedQuery");
        	  visitWithDangerSignsDocumentedQuery.setQuery("select e.encounter_id from encounter e"
				+" inner join obs o1 on e.encounter_id=o1.encounter_id and o1.concept_id= "+breathingDangerSignsPresent.getConceptId()+" and o1.voided=0"
				+" inner join obs o2 on e.encounter_id=o2.encounter_id and o2.concept_id= "+convulsionsDangerSignsPresent.getConceptId()+" and o2.voided=0"
				+" inner join obs o3 on e.encounter_id=o3.encounter_id and o3.concept_id= "+LethargyOrUnresponsivenessDangerSignsPresent.getConceptId()+" and o3.voided=0"
				+" inner join obs o4 on e.encounter_id=o4.encounter_id and o4.concept_id= "+umbilicalCordRednessDangerSigns.getConceptId()+" and o4.voided=0"
				+" inner join obs o5 on e.encounter_id=o5.encounter_id and o5.concept_id= "+stiffNeckOrBulgingFontanellesDangerSigns.getConceptId()+" and o5.voided=0"
				+" inner join obs o8 on e.encounter_id=o8.encounter_id and o8.concept_id= "+temperature.getConceptId()+" and o8.voided=0"
				+" inner join obs o9 on e.encounter_id=o9.encounter_id and o9.concept_id= "+respiratoryRate.getConceptId()+" and o9.voided=0"
				+" where  e.form_id="+PDCVisitForm.getFormId()+" and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate");
        	  visitWithDangerSignsDocumentedQuery.addParameter(new Parameter("startDate", "startDate", Date.class));
        	  visitWithDangerSignsDocumentedQuery.addParameter(new Parameter("endDate", "endDate", Date.class));
              
        	  
        	  
        	  EncounterIndicator visitWithDangerSignsDocumentedQueryIndicator = new EncounterIndicator();
        	  visitWithDangerSignsDocumentedQueryIndicator.setName("visitWithDangerSignsDocumentedQueryIndicator");
        	  visitWithDangerSignsDocumentedQueryIndicator.setEncounterQuery(new Mapped<EncounterQuery>(visitWithDangerSignsDocumentedQuery,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));
              
              dsd.addColumn(visitWithDangerSignsDocumentedQueryIndicator); 
   // PDC9D     	  
        	  
        	  SqlEncounterQuery visitsInQuarter=new SqlEncounterQuery();
        	  visitsInQuarter.setName("visitsInQuarter");
        	  visitsInQuarter.setQuery("select e.encounter_id from encounter e"
				+" where  e.form_id="+PDCVisitForm.getFormId()+" and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate");
        	  visitsInQuarter.addParameter(new Parameter("startDate", "startDate", Date.class));
        	  visitsInQuarter.addParameter(new Parameter("endDate", "endDate", Date.class));
              
        	  
        	  EncounterIndicator visitsInQuarterIndicator = new EncounterIndicator();
        	  visitsInQuarterIndicator.setName("visitsInQuarterIndicator");
        	  visitsInQuarterIndicator.setEncounterQuery(new Mapped<EncounterQuery>(visitsInQuarter,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));
              
              dsd.addColumn(visitsInQuarterIndicator); 
          }
        
     private CohortIndicatorDataSetDefinition createQuarterlyBaseDataSet() {
                CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
                dsd.setName("Quarterly Cohort Data Set");
                dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
                dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
//                dsd.addParameter(new Parameter("provider","Provider",Provider.class));
                
                createQuarterlyIndicators(dsd);
                return dsd;
        }
                
        private void createQuarterlyIndicators(CohortIndicatorDataSetDefinition dsd) {
                
                InProgramCohortDefinition inPDCProgramByDate = Cohorts.createInProgramParameterizableByDate(
                    "In PDC Programs by date", PDCProgram, "onOrBefore");
                
                InProgramCohortDefinition inPDCProgramByStartDateAndEndDate = Cohorts.createInProgramParameterizableByDate(
                    "In PDC Programs by startdate and enddate", PDCProgram, onOrAfterOnOrBefore);

                ProgramEnrollmentCohortDefinition completedPCDProgramByStartDateAndEnddate=Cohorts.createProgramEnrollment("completedPCDProgramByStartDateAndEnddate",PDCProgram);
                completedPCDProgramByStartDateAndEnddate.addParameter(new Parameter("completedOnOrBefore","completedOnOrBefore",Date.class));


            EncounterCohortDefinition  patientWithVisitInQuarter=Cohorts.createEncounterBasedOnForms("patientWithVisitInQuarter", onOrAfterOnOrBefore, PDCVisitForms);
                
                
//PDC1
                SqlCohortDefinition patientUnderOneYear=new SqlCohortDefinition();
                patientUnderOneYear.setName("patientUnderOneYear");
                patientUnderOneYear.setQuery("select p.person_id from person p,patient pt where DATEDIFF(:endDate,p.birthdate)<365 and p.voided=0 and p.person_id=pt.patient_id and pt.voided=0");
                patientUnderOneYear.addParameter(new Parameter("endDate","endDate",Date.class));

                
                SqlCohortDefinition patientGreatterThanOrEqualOneYearAndLessThanTwoyears=new SqlCohortDefinition();
                patientGreatterThanOrEqualOneYearAndLessThanTwoyears.setName("patientGreatterThanOrEqualOneYearAndLessThanTwoyears");
                patientGreatterThanOrEqualOneYearAndLessThanTwoyears.setQuery("select p.person_id from person p,patient pt where DATEDIFF(:endDate,p.birthdate)>=365 and DATEDIFF(:endDate,p.birthdate)<730 and p.voided=0 and p.person_id=pt.patient_id and pt.voided=0");
                patientGreatterThanOrEqualOneYearAndLessThanTwoyears.addParameter(new Parameter("endDate","endDate",Date.class));

                SqlCohortDefinition patientGreatterThanOrEqualTwoyears=new SqlCohortDefinition();
                patientGreatterThanOrEqualTwoyears.setName("patientGreatterThanOrEqualTwoyears");
                patientGreatterThanOrEqualTwoyears.setQuery("select p.person_id from person p,patient pt where DATEDIFF(:endDate,p.birthdate)>=730 and p.voided=0 and p.person_id=pt.patient_id and pt.voided=0");
                patientGreatterThanOrEqualTwoyears.addParameter(new Parameter("endDate","endDate",Date.class));
                
                //CodedObsCohortDefinition reasonForNotDoingFollowUpCohort=Cohorts.createCodedObsCohortDefinition("reasonForNotDoingFollowUpCohort",reasonForNotDoingFollowUp, null, SetComparator.IN, TimeModifier.LAST);
                
                SqlCohortDefinition reasonForNotDoingFollowUpCohort=new SqlCohortDefinition();
                reasonForNotDoingFollowUpCohort.setName("reasonForNotDoingFollowUpCohort");
                reasonForNotDoingFollowUpCohort.setQuery("select o.person_id from obs o where (o.concept_id="+reasonForNotDoingFollowUp.getConceptId()+" or o.concept_id="+reasonForExitingFromCare.getConceptId()+") and o.voided=0 order by o.obs_datetime desc");
                //reasonForNotDoingFollowUpCohort.addParameter(new Parameter("endDate","endDate",Date.class));

             
                CompositionCohortDefinition activePatient = new CompositionCohortDefinition();
                activePatient.setName("activePatient");
                activePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
                activePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
                activePatient.getSearches().put("1", new Mapped(inPDCProgramByDate, ParameterizableUtil.createParameterMappings("onOrBefore=${endDate}")));
                activePatient.getSearches().put("2", new Mapped(patientWithVisitInQuarter, ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-6m},onOrBefore=${endDate}")));
                activePatient.getSearches().put("3", new Mapped(patientWithVisitInQuarter, ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-9m},onOrBefore=${endDate}")));
                activePatient.getSearches().put("4", new Mapped(patientWithVisitInQuarter, ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-18m},onOrBefore=${endDate}")));
                activePatient.getSearches().put("5", new Mapped(patientUnderOneYear, ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
                activePatient.getSearches().put("6", new Mapped(patientGreatterThanOrEqualOneYearAndLessThanTwoyears, ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
                activePatient.getSearches().put("7", new Mapped(patientGreatterThanOrEqualTwoyears, ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
                activePatient.getSearches().put("8", new Mapped(reasonForNotDoingFollowUpCohort, null));
                activePatient.getSearches().put("9", new Mapped(completedPCDProgramByStartDateAndEnddate, ParameterizableUtil.createParameterMappings("completedOnOrBefore=${startDate-1d}")));
                //activePatient.setCompositionString("1 AND (NOT (2 AND 5)) AND (NOT (3 AND 6)) AND (NOT (4 AND 7)) AND (NOT 8) AND (NOT 9)");
                //activePatient.setCompositionString("1 AND (NOT (((NOT 2) AND 5) OR ((NOT 3) AND 6) OR ((NOT 4) AND 7) OR 8 OR 9))");
                activePatient.setCompositionString("1 AND (NOT (((NOT 2) AND 5) OR ((NOT 3) AND 6) OR ((NOT 4) AND 7) OR 8 OR 9))");

                //activePatient.setCompositionString("1");


            CohortIndicator activePatientIndicator = Indicators.newCountIndicator("Number of active patients Indicator", activePatient,
                        ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
                           
                    
                dsd.addColumn("PDC1", "Number of active patients",
                        new Mapped(activePatientIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

//            SqlCohortDefinition patientsEnrolledByProvider = new SqlCohortDefinition();
//            patientsEnrolledByProvider.setName("patientsEnrolledByProvider");
//            patientsEnrolledByProvider.setQuery("SELECT distinct patient_program.patient_id \n" +
//                    "from patient_program \n" +
//                    "LEFT JOIN users on users.user_id=patient_program.creator \n" +
//                    "LEFT JOIN provider on provider.person_id=users.person_id \n" +
//                    "LEFT JOIN (SELECT encounter.patient_id, encounter_provider.provider_id \n" +
//                    "    from encounter \n" +
//                    "    LEFT JOIN encounter_provider on encounter_provider.encounter_id=encounter.encounter_id \n" +
//                    "    LEFT JOIN provider on provider.provider_id=encounter_provider.provider_id \n" +
//                    "    where form_id= \n" + PDCIntakeForm.getFormId() +
//                    "    and encounter.voided=0 and \n" +
//                    "    encounter_provider.voided=0) \n" +
//                    " intake on intake.patient_id=patient_program.patient_id \n" +
//                    " where program_id= \n" + PDCProgram.getProgramId() +
//                    " and patient_program.voided=0 \n" +
//                    " and (intake.provider_id= :provider \n" +
//                    "     or provider.provider_id= :provider \n" +
//                    " ) \n" +
//                    " and encounter_datetime>= :startDate \n" +
//                    " and encounter_datetime<= :endDate"
//                    );
//            patientsEnrolledByProvider.addParameter(new Parameter("startDate","startDate",Date.class));
//            patientsEnrolledByProvider.addParameter(new Parameter("endDate","endDate",Date.class));
//            patientsEnrolledByProvider.addParameter(new Parameter("provider","provider",Provider.class));

// PDC2
               // SqlCohortDefinition patientWithRefferralReasonOnIntakeForm=Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate("patientWithReferralReason",PDCIntakeForm,reasoForReferral);
                SqlCohortDefinition patientWithRefferralReasons=new SqlCohortDefinition();
                patientWithRefferralReasons.setName("patientWithRefferralReasons");
                patientWithRefferralReasons.setQuery("select distinct o.person_id from encounter e, obs o where e.encounter_id=o.encounter_id and (e.form_id="+PDCIntakeForm.getFormId()+" or e.form_id="+PDCReferralForm.getFormId()+") and o.concept_id="+reasoForReferral.getConceptId()+" " +
                "and o.voided=0 and e.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");
                patientWithRefferralReasons.addParameter(new Parameter("startDate","startDate",Date.class));
                patientWithRefferralReasons.addParameter(new Parameter("endDate","endDate",Date.class));
                
                //SqlCohortDefinition patientWithRefferralReasonOnReferralForm=Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate("patientWithReferralReason",PDCReferralForm,reasoForReferral);
                /*SqlCohortDefinition patientWithRefferralReasonOnReferralForm=new SqlCohortDefinition();
                patientWithRefferralReasonOnReferralForm.setName("patientWithRefferralReasonOnReferralForm");
                patientWithRefferralReasonOnReferralForm.setQuery("select distinct o.person_id from encounter e, obs o where e.encounter_id=o.encounter_id and e.form_id="+PDCReferralForm.getFormId()+" and o.concept_id="+reasoForReferral.getConceptId()+" and o.voided=0 and e.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");
                patientWithRefferralReasonOnReferralForm.addParameter(new Parameter("startDate","startDate",Date.class));
                patientWithRefferralReasonOnReferralForm.addParameter(new Parameter("endDate","endDate",Date.class));
                */
                CompositionCohortDefinition newPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm = new CompositionCohortDefinition();
                newPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm.setName("newPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm");
                newPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm.addParameter(new Parameter("startDate", "startDate", Date.class));
                newPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm.addParameter(new Parameter("endDate", "endDate", Date.class));
                newPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm.getSearches().put("1", new Mapped(inPDCProgramByDate, ParameterizableUtil.createParameterMappings("onOrBefore=${startDate}")));
                newPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm.getSearches().put("2", new Mapped(inPDCProgramByStartDateAndEndDate, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
                newPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm.getSearches().put("3", new Mapped(patientWithRefferralReasons, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
                //newPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm.getSearches().put("4", new Mapped(patientWithRefferralReasonOnReferralForm, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
               // newPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm.setCompositionString("(2 AND (NOT 1)) AND (3 OR 4)");
                //newPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm.setCompositionString("(2 AND (NOT 1)) AND 3");
                newPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm.setCompositionString("2 AND (NOT 1)");


            CohortIndicator newPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormIndicator = Indicators.newCountIndicator("Number of newly enrolled patients total and per intake category indicator", newPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm,
                    ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
                       
                dsd.addColumn("PDC2", "Number of newly enrolled patients total and per intake category",
                    new Mapped(newPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

// PDC20  Low Birth Weight
// PDC21  Premature Birth
// PDC22  Hypoxic ischemic encephalopathy
// PDC23  Central Nervous System Infection
// PDC24  Hydrocephalus
// PDC25  Trisomy 21
// PDC26  Cleft Lip or Palate
// PDC27  Other Developmental Delay, Suspected Neuromuscular Disease, and Suspected Genetic Syndromes
// PDC28  Severe malnutrition requiring hospitalization
// PDC29 Others
            for(int i=0;i<reasonsForReferral.size();i++){

            SqlCohortDefinition patientWithRefferralReason=new SqlCohortDefinition();
                patientWithRefferralReason.setName("patientWithRefferralReason"+reasonsForReferral.get(i).getName().toString());
                patientWithRefferralReason.setQuery("select distinct o.person_id from encounter e, obs o where e.encounter_id=o.encounter_id and (e.form_id="+PDCIntakeForm.getFormId()+" or e.form_id="+PDCReferralForm.getFormId()+") and o.concept_id="+reasoForReferral.getConceptId()+" " +
                    "and o.voided=0 and e.voided=0 and o.value_coded="+reasonsForReferral.get(i).getConceptId()+" and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");
                patientWithRefferralReason.addParameter(new Parameter("startDate","startDate",Date.class));
                patientWithRefferralReason.addParameter(new Parameter("endDate","endDate",Date.class));

            //SqlCohortDefinition patientWithRefferralReasonOnReferralForm=Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate("patientWithReferralReason",PDCReferralForm,reasoForReferral);
                /*SqlCohortDefinition patientWithRefferralReasonOnReferralForm=new SqlCohortDefinition();
                patientWithRefferralReasonOnReferralForm.setName("patientWithRefferralReasonOnReferralForm");
                patientWithRefferralReasonOnReferralForm.setQuery("select distinct o.person_id from encounter e, obs o where e.encounter_id=o.encounter_id and e.form_id="+PDCReferralForm.getFormId()+" and o.concept_id="+reasoForReferral.getConceptId()+" and o.voided=0 and e.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");
                patientWithRefferralReasonOnReferralForm.addParameter(new Parameter("startDate","startDate",Date.class));
                patientWithRefferralReasonOnReferralForm.addParameter(new Parameter("endDate","endDate",Date.class));
                */
            CompositionCohortDefinition newPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategory = new CompositionCohortDefinition();
                newPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategory.setName("newPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategory"+reasonsForReferral.get(i).getName().toString());
                newPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategory.addParameter(new Parameter("startDate", "startDate", Date.class));
                newPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategory.addParameter(new Parameter("endDate", "endDate", Date.class));
                newPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategory.getSearches().put("1", new Mapped(inPDCProgramByDate, ParameterizableUtil.createParameterMappings("onOrBefore=${startDate}")));
                newPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategory.getSearches().put("2", new Mapped(inPDCProgramByStartDateAndEndDate, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
                newPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategory.getSearches().put("3", new Mapped(patientWithRefferralReason, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
                newPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategory.setCompositionString("(2 AND (NOT 1)) AND 3");


            CohortIndicator newPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategoryIndicator = Indicators.newCountIndicator("Number of newly enrolled patients total and per intake category indicator "+reasonsForReferral.get(i).getName().toString(), newPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategory,
                    ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

            dsd.addColumn("PDC2"+i, "Number of newly enrolled patients total and per intake category",
                    new Mapped(newPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategoryIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

            }

// PDC3
               // SqlCohortDefinition patientWithRefferralReasonOnIntakeFormAnyTime=Cohorts.getPatientsWithObservationInForm("patientWithReferralReasonAnyTime",PDCIntakeForm,reasoForReferral);
                
                SqlCohortDefinition patientWithRefferralReasonAnyTime=new SqlCohortDefinition();
                patientWithRefferralReasonAnyTime.setName("patientWithRefferralReasonAnyTime");
                patientWithRefferralReasonAnyTime.setQuery("select distinct o.person_id from encounter e, obs o where e.encounter_id=o.encounter_id and (e.form_id="+PDCIntakeForm.getFormId()+" or e.form_id="+PDCReferralForm.getFormId()+") and o.concept_id="+reasoForReferral.getConceptId()+" " +
                "and o.voided=0 and e.voided=0 ");
                
                
                //SqlCohortDefinition patientWithRefferralReasonOnReferralFormAnyTime=Cohorts.getPatientsWithObservationInForm("patientWithReferralReasonAnyTime",PDCReferralForm,reasoForReferral);
                
               /* SqlCohortDefinition patientWithRefferralReasonOnReferralFormAnyTime=new SqlCohortDefinition();
                patientWithRefferralReasonOnReferralFormAnyTime.setName("patientWithRefferralReasonOnIntakeFormAnyTime");
                patientWithRefferralReasonOnReferralFormAnyTime.setQuery("select distinct o.person_id from encounter e, obs o where e.encounter_id=o.encounter_id and e.form_id="+PDCReferralForm.getFormId()+" and o.concept_id="+reasoForReferral.getConceptId()+" " +
                "and o.voided=0 and e.voided=0 and (o.value_numeric is NOT NULL or o.value_coded is NOT NULL or o.value_datetime is NOT NULL or o.value_boolean is NOT NULL)");
                */
                
                CompositionCohortDefinition everPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm = new CompositionCohortDefinition();
                everPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm.setName("everPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm");
                everPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm.addParameter(new Parameter("endDate", "endDate", Date.class));
                everPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm.getSearches().put("1", new Mapped(inPDCProgramByDate, ParameterizableUtil.createParameterMappings("onOrBefore=${endDate}")));
                everPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm.getSearches().put("2", new Mapped(patientWithRefferralReasonAnyTime, null));
               // everPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm.getSearches().put("3", new Mapped(patientWithRefferralReasonOnReferralFormAnyTime, null));
                //everPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm.setCompositionString("1 AND 2");
                everPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm.setCompositionString("1");

            CohortIndicator everPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormIndicator = Indicators.newCountIndicator("Number of ever enrolled patients total and per intake category indicator", everPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm,
                    ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
                       
                
                dsd.addColumn("PDC3", "Number of ever enrolled patients total and per intake category",
                    new Mapped(everPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

            for(int i=0;i<reasonsForReferral.size();i++){
                // SqlCohortDefinition patientWithRefferralReasonOnIntakeFormAnyTime=Cohorts.getPatientsWithObservationInForm("patientWithReferralReasonAnyTime",PDCIntakeForm,reasoForReferral);

                SqlCohortDefinition patientWithRefferralReasonAnyTimeByCategory=new SqlCohortDefinition();
                patientWithRefferralReasonAnyTimeByCategory.setName("patientWithRefferralReasonAnyTimeByCategory"+reasonsForReferral.get(i).getName().toString());
                patientWithRefferralReasonAnyTimeByCategory.setQuery("select distinct o.person_id from encounter e, obs o where e.encounter_id=o.encounter_id and (e.form_id="+PDCIntakeForm.getFormId()+" or e.form_id="+PDCReferralForm.getFormId()+") and o.concept_id="+reasoForReferral.getConceptId()+" " +
                        "and o.voided=0 and o.value_coded="+reasonsForReferral.get(i).getConceptId()+" and e.voided=0 ");


                //SqlCohortDefinition patientWithRefferralReasonOnReferralFormAnyTime=Cohorts.getPatientsWithObservationInForm("patientWithReferralReasonAnyTime",PDCReferralForm,reasoForReferral);

               /* SqlCohortDefinition patientWithRefferralReasonOnReferralFormAnyTime=new SqlCohortDefinition();
                patientWithRefferralReasonOnReferralFormAnyTime.setName("patientWithRefferralReasonOnIntakeFormAnyTime");
                patientWithRefferralReasonOnReferralFormAnyTime.setQuery("select distinct o.person_id from encounter e, obs o where e.encounter_id=o.encounter_id and e.form_id="+PDCReferralForm.getFormId()+" and o.concept_id="+reasoForReferral.getConceptId()+" " +
                "and o.voided=0 and e.voided=0 and (o.value_numeric is NOT NULL or o.value_coded is NOT NULL or o.value_datetime is NOT NULL or o.value_boolean is NOT NULL)");
                */

                CompositionCohortDefinition everPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategory = new CompositionCohortDefinition();
                everPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategory.setName("everPatientEnrolledWithRefferralReasonOnIntakeOrReferralForm"+reasonsForReferral.get(i).getName().toString());
                everPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategory.addParameter(new Parameter("endDate", "endDate", Date.class));
                everPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategory.getSearches().put("1", new Mapped(inPDCProgramByDate, ParameterizableUtil.createParameterMappings("onOrBefore=${endDate}")));
                everPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategory.getSearches().put("2", new Mapped(patientWithRefferralReasonAnyTimeByCategory, null));
                // everPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategory.getSearches().put("3", new Mapped(patientWithRefferralReasonOnReferralFormAnyTime, null));
                everPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategory.setCompositionString("1 AND 2");

                CohortIndicator everPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormIndicatorByCategory = Indicators.newCountIndicator("Number of ever enrolled patients total and per intake category indicator"+reasonsForReferral.get(i).getName().toString(), everPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormByCategory,
                        ParameterizableUtil.createParameterMappings("endDate=${endDate}"));


                dsd.addColumn("PDC3"+i, "Number of ever enrolled patients total and per intake category"+reasonsForReferral.get(i).getName().toString(),
                        new Mapped(everPatientEnrolledWithRefferralReasonOnIntakeOrReferralFormIndicatorByCategory, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

            }


//PDC4
              // EncounterCohortDefinition patientWithIntakeVisit=Cohorts.createEncounterBasedOnForms("patientWithIntakeVisit", onOrAfterOnOrBefore, intakeVisitForms);
                //SqlCohortDefinition patientWithIntakeDateOnPDCReferralForm=Cohorts.getPatientsWithObservationValueDateTimeInFormBetweenStartAndEndDate("patientWithIntakeDateOnPDCReferralForm",PDCReferralForm,returnVisitDate);
               
                SqlCohortDefinition patientWithIntakeDateOnPDCReferralForm=new SqlCohortDefinition();
                patientWithIntakeDateOnPDCReferralForm.setName("patientWithIntakeDateOnPDCReferralForm");
                patientWithIntakeDateOnPDCReferralForm.setQuery("select distinct o.person_id from encounter e, obs o where e.encounter_id=o.encounter_id and e.form_id="+PDCReferralForm.getFormId()+" " +
                "and o.concept_id="+returnVisitDate.getConceptId()+" and o.voided=0 and e.voided=0 and o.value_datetime>= :startDate and o.value_datetime<= :endDate");
                patientWithIntakeDateOnPDCReferralForm.addParameter(new Parameter("startDate","startDate",Date.class));
                patientWithIntakeDateOnPDCReferralForm.addParameter(new Parameter("endDate","endDate",Date.class));
                
                
               CompositionCohortDefinition patientWithIntakeVisitAndEnrolledInQuarter = new CompositionCohortDefinition();
               patientWithIntakeVisitAndEnrolledInQuarter.setName("patientWithIntakeVisitAndEnrolledInQuarter");
               patientWithIntakeVisitAndEnrolledInQuarter.addParameter(new Parameter("startDate", "startDate", Date.class));
               patientWithIntakeVisitAndEnrolledInQuarter.addParameter(new Parameter("endDate", "endDate", Date.class));
               patientWithIntakeVisitAndEnrolledInQuarter.getSearches().put("1", new Mapped(inPDCProgramByDate, ParameterizableUtil.createParameterMappings("onOrBefore=${startDate}")));
               patientWithIntakeVisitAndEnrolledInQuarter.getSearches().put("2", new Mapped(inPDCProgramByStartDateAndEndDate, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
               patientWithIntakeVisitAndEnrolledInQuarter.getSearches().put("3", new Mapped(patientWithIntakeDateOnPDCReferralForm, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
               patientWithIntakeVisitAndEnrolledInQuarter.setCompositionString("(2 AND (NOT 1)) AND 3");
                          
               CohortIndicator patientWithIntakeVisitAndEnrolledInQuarterIndicatorNumerator = Indicators.newCountIndicator("Number of identified high risk neonates with an intake visit scheduled in the reference quarter that enrolled in the reference quarter indicator", patientWithIntakeVisitAndEnrolledInQuarter,
                   ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
                      
               
               dsd.addColumn("PDC4N", "Number of identified high risk neonates with an intake visit scheduled in the reference quarter that enrolled in the reference quarter",
                   new Mapped(patientWithIntakeVisitAndEnrolledInQuarterIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
       
               
               CohortIndicator patientWithIntakeVisitAndEnrolledInQuarterIndicatorDenominator = Indicators.newCountIndicator("Number of identified high risk neonates with an intake visit scheduled in the reference quarter that enrolled in the reference quarter indicator", patientWithIntakeDateOnPDCReferralForm,
                       ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
                          
                   
                   dsd.addColumn("PDC4D", "Number of identified high risk neonates with their intake visit scheduled in the reference quarter",
                       new Mapped(patientWithIntakeVisitAndEnrolledInQuarterIndicatorDenominator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
           
 //PDC5               
                   
                   
                   //EncounterCohortDefinition patientWithVisitForm=Cohorts.createEncounterBasedOnForms("patientWithIntakeVisit",PDCVisitForms);

                   SqlCohortDefinition patientWithVisitFormNotOnSameDayOfIntake=new SqlCohortDefinition();
                   patientWithVisitFormNotOnSameDayOfIntake.setName("patientWithVisitFormNotOnSameDayOfIntake");
                   patientWithVisitFormNotOnSameDayOfIntake.setQuery("select distinct vis.patient_id from (select patient_id,encounter_datetime from encounter where form_id="+PDCIntakeForm.getFormId()+" and voided=0) as intk,(select patient_id,encounter_datetime from encounter where form_id="+PDCVisitForm.getFormId()+" and voided=0) as vis where intk.encounter_datetime!=vis.encounter_datetime and intk.encounter_datetime < vis.encounter_datetime and intk.patient_id=vis.patient_id");


                   ProgramEnrollmentCohortDefinition patientsEverEnrolled=Cohorts.createProgramEnrollment("patientsEverEnrolled", PDCProgram);
                
                   CompositionCohortDefinition patientWithVisitFormEverEnrolled = new CompositionCohortDefinition();
                   patientWithVisitFormEverEnrolled.setName("patientWithVisitFormEverEnrolled");
                   patientWithVisitFormEverEnrolled.addParameter(new Parameter("startDate", "startDate", Date.class));
                   patientWithVisitFormEverEnrolled.addParameter(new Parameter("endDate", "endDate", Date.class));
                   patientWithVisitFormEverEnrolled.getSearches().put("1", new Mapped(patientWithVisitFormNotOnSameDayOfIntake, null));
                   patientWithVisitFormEverEnrolled.getSearches().put("2", new Mapped(patientsEverEnrolled, null));
                   patientWithVisitFormEverEnrolled.setCompositionString("1 AND 2");
                              
                   CohortIndicator patientWithVisitFormEverEnrolledIndicatorNumerator = Indicators.newCountIndicator("Number of ever enrolled children who have at least one follow up visit indicator", patientWithVisitFormEverEnrolled,
                       ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
                          
                   
                   dsd.addColumn("PDC5N", "Number of ever enrolled children who have at least one follow up visit",
                       new Mapped(patientWithVisitFormEverEnrolledIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
           
                   CohortIndicator patientsEverEnrolledIndicatorDenominator = Indicators.newCountIndicator("Number of ever enrolled children indicator", patientsEverEnrolled,
                           null);
                              
                       
                   dsd.addColumn("PDC5D", "Number of ever enrolled children",
                           new Mapped(patientsEverEnrolledIndicatorDenominator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
 
                   
  //PDC6 visits is located up
                   
                   
  /*//PDC7
                   SqlCohortDefinition patientsWithAnySocialSupportAlreadyReceived=Cohorts.getPatientsWithObservationValueDateTimeInFormBetweenStartAndEndDate("patientsWithAnySocialSupport",PDCVisitForm,socialEconomicAssistanceAlreadyReceived);
                   SqlCohortDefinition patientsWithAnySocialSupportRecommanded=Cohorts.getPatientsWithObservationValueDateTimeInFormBetweenStartAndEndDate("patientsWithAnySocialSupport",PDCVisitForm,socialEconomicAssistanceRecommanded);
                   SqlCohortDefinition patientsWithAnySocialSupportNotRecommended=Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate("patientsWithAnySocialSupport",PDCVisitForms,socialEconomicAssistance,socialAssistanceTypes);
                    */
                   SqlCohortDefinition patientsWithAnySocialSupportRecommended=new SqlCohortDefinition();
                   patientsWithAnySocialSupportRecommended.setName("patientsWithAnySocialSupportRecommended");
                   patientsWithAnySocialSupportRecommended.setQuery("select distinct o.person_id from encounter e, obs o where e.encounter_id=o.encounter_id and e.form_id="+PDCVisitForm.getFormId()+" and o.concept_id="+socialEconomicAssistanceRecommanded.getConceptId()+" and o.voided=0 and e.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");
                   patientsWithAnySocialSupportRecommended.addParameter(new Parameter("startDate","startDate",Date.class));
                   patientsWithAnySocialSupportRecommended.addParameter(new Parameter("endDate","endDate",Date.class));
                   
                   SqlCohortDefinition patientsWithAnySocialSupportAlreadyReceived=new SqlCohortDefinition();
                   patientsWithAnySocialSupportAlreadyReceived.setName("patientsWithAnySocialSupportAlreadyReceived");
                   patientsWithAnySocialSupportAlreadyReceived.setQuery("select distinct o.person_id from encounter e, obs o where e.encounter_id=o.encounter_id and e.form_id="+PDCVisitForm.getFormId()+" and o.concept_id="+socialEconomicAssistanceAlreadyReceived.getConceptId()+" and o.voided=0 and e.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");
                   patientsWithAnySocialSupportAlreadyReceived.addParameter(new Parameter("startDate","startDate",Date.class));
                   patientsWithAnySocialSupportAlreadyReceived.addParameter(new Parameter("endDate","endDate",Date.class));
                   
                   SqlCohortDefinition patientsWithAnySocialSupportNotRecommended=new SqlCohortDefinition();
                   patientsWithAnySocialSupportNotRecommended.setName("patientsWithAnySocialSupportNotRecommended");
                   patientsWithAnySocialSupportNotRecommended.setQuery("select distinct o.person_id from encounter e, obs o where e.encounter_id=o.encounter_id and e.form_id="+PDCVisitForm.getFormId()+" and o.concept_id="+socialEconomicAssistance.getConceptId()+" and o.value_coded="+socialEconomicAssistanceNotRecommanded.getConceptId()+" and o.voided=0 and e.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");
                   patientsWithAnySocialSupportNotRecommended.addParameter(new Parameter("startDate","startDate",Date.class));
                   patientsWithAnySocialSupportNotRecommended.addParameter(new Parameter("endDate","endDate",Date.class));
                   
                   CompositionCohortDefinition newPatientsWithAnySocialSupportInQuarter = new CompositionCohortDefinition();
                   newPatientsWithAnySocialSupportInQuarter.setName("newPatientsWithAnySocialSupportInQuarter");
                   newPatientsWithAnySocialSupportInQuarter.addParameter(new Parameter("startDate", "startDate", Date.class));
                   newPatientsWithAnySocialSupportInQuarter.addParameter(new Parameter("endDate", "endDate", Date.class));
                   newPatientsWithAnySocialSupportInQuarter.getSearches().put("1", new Mapped(inPDCProgramByDate, ParameterizableUtil.createParameterMappings("onOrBefore=${startDate}")));
                   newPatientsWithAnySocialSupportInQuarter.getSearches().put("2", new Mapped(inPDCProgramByStartDateAndEndDate, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
                   newPatientsWithAnySocialSupportInQuarter.getSearches().put("3", new Mapped(patientsWithAnySocialSupportNotRecommended, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
                   newPatientsWithAnySocialSupportInQuarter.getSearches().put("4", new Mapped(patientsWithAnySocialSupportAlreadyReceived, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
                   newPatientsWithAnySocialSupportInQuarter.getSearches().put("5", new Mapped(patientsWithAnySocialSupportRecommended, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
                   newPatientsWithAnySocialSupportInQuarter.setCompositionString("(2 AND (NOT 1)) AND (3 OR 4 OR 5)");
                   
                   
                  CohortIndicator newPatientsWithAnySocialSupportInQuarterIndicatorNumerator = Indicators.newCountIndicator("Number of new patients enrolled in the reference quarter who were screened (defined as “already receiving”, “recommended”, or “not recommended” answer on any visit) indicator", 
                		  newPatientsWithAnySocialSupportInQuarter,
                           ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
                          
                         
                       
                       dsd.addColumn("PDC7N", "Number of new patients enrolled in the reference quarter who were screened (defined as “already receiving”, “recommended”, or “not recommended” answer on any visit)",
                           new Mapped(newPatientsWithAnySocialSupportInQuarterIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
                
                  
                   CompositionCohortDefinition newPatientsInQuarter = new CompositionCohortDefinition();
                   newPatientsInQuarter.setName("newPatientsInQuarter");
                   newPatientsInQuarter.addParameter(new Parameter("startDate", "startDate", Date.class));
                   newPatientsInQuarter.addParameter(new Parameter("endDate", "endDate", Date.class));
                   newPatientsInQuarter.getSearches().put("1", new Mapped(inPDCProgramByDate, ParameterizableUtil.createParameterMappings("onOrBefore=${startDate}")));
                   newPatientsInQuarter.getSearches().put("2", new Mapped(inPDCProgramByStartDateAndEndDate, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
                   newPatientsInQuarter.setCompositionString("2 AND (NOT 1)");
                              
                   CohortIndicator newPatientsInQuarterIndicatorDenominator = Indicators.newCountIndicator("Number of new patients enrolled in the reference quarter indicator", newPatientsInQuarter,
                       ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
                          
                   
                   dsd.addColumn("PDC7D", "Number of new patients enrolled in the reference quarter",
                       new Mapped(newPatientsInQuarterIndicatorDenominator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
           
    //PDC8
                   
                   CompositionCohortDefinition currentlyEnrolledPatientsWithAnySocialSupportInQuarter = new CompositionCohortDefinition();
                   currentlyEnrolledPatientsWithAnySocialSupportInQuarter.setName("currentlyEnrolledPatientsWithAnySocialSupportInQuarter");
                   currentlyEnrolledPatientsWithAnySocialSupportInQuarter.addParameter(new Parameter("startDate", "startDate", Date.class));
                   currentlyEnrolledPatientsWithAnySocialSupportInQuarter.addParameter(new Parameter("endDate", "endDate", Date.class));
                   currentlyEnrolledPatientsWithAnySocialSupportInQuarter.getSearches().put("1", new Mapped(inPDCProgramByStartDateAndEndDate, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
                   currentlyEnrolledPatientsWithAnySocialSupportInQuarter.getSearches().put("2", new Mapped(patientsWithAnySocialSupportAlreadyReceived, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
                   currentlyEnrolledPatientsWithAnySocialSupportInQuarter.getSearches().put("3", new Mapped(patientsWithAnySocialSupportRecommended, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
                   currentlyEnrolledPatientsWithAnySocialSupportInQuarter.setCompositionString("1 AND (2 OR 3)");
                              
                   CohortIndicator currentlyEnrolledPatientsWithAnySocialSupportInQuarterIndicatorNumerator = Indicators.newCountIndicator("Number # of currently enrolled children receiving social support at last visit  (defined as “already receiving” or “recommended and starting today” answer) indicator", currentlyEnrolledPatientsWithAnySocialSupportInQuarter,
                       ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
                          
                   
                   dsd.addColumn("PDC8N", "Number of currently enrolled children receiving social support at last visit  (defined as “already receiving” or “recommended and starting today” answer))",
                       new Mapped(currentlyEnrolledPatientsWithAnySocialSupportInQuarterIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
           
                             
                   CohortIndicator currentlyEnrolledPatientsIndicatorDenominator = Indicators.newCountIndicator("Number of currently enrolled children  indicator", inPDCProgramByStartDateAndEndDate,
                       ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
                          
                   
                   dsd.addColumn("PDC8D", "Number of currently enrolled children ",
                       new Mapped(currentlyEnrolledPatientsIndicatorDenominator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
           
              
  //PDC10
                   
                   SqlCohortDefinition patientUnderSixMonthWithBirthWeightAndIntervalGrowthDocumentedInLastVisit=new SqlCohortDefinition();
                   patientUnderSixMonthWithBirthWeightAndIntervalGrowthDocumentedInLastVisit.setName("patientUnderSixMonthWithBirthWeightAndIntervalGrowthDocumented");
                   patientUnderSixMonthWithBirthWeightAndIntervalGrowthDocumentedInLastVisit.setQuery("select lastEnc.patient_id from (select * from (select * from encounter where form_id="+PDCVisitForm.getFormId()+" and voided=0 order by encounter_datetime desc) as orderedEnc group by orderedEnc.encounter_id) as lastEnc"
					+" inner join person p on lastEnc.patient_id=p.person_id and DATEDIFF(lastEnc.encounter_datetime,p.birthdate)<180 and p.voided=0"
					+" inner join obs o on lastEnc.patient_id=o.person_id and o.concept_id="+birthWeight.getConceptId()+" and o.voided=0"
					+" inner join obs o2 on lastEnc.patient_id=o2.person_id and o2.concept_id="+intervalGrogth.getConceptId()+" and o2.voided=0"
					+" where lastEnc.encounter_datetime >= :startDate and lastEnc.encounter_datetime <= :endDate");
                   patientUnderSixMonthWithBirthWeightAndIntervalGrowthDocumentedInLastVisit.addParameter(new Parameter("startDate","startDate",Date.class));
                   patientUnderSixMonthWithBirthWeightAndIntervalGrowthDocumentedInLastVisit.addParameter(new Parameter("endDate","endDate",Date.class));

                   CohortIndicator patientUnderSixMonthWithBirthWeightAndIntervalGrowthDocumentedInLastVisitIndicatorNumerator = Indicators.newCountIndicator("Number of children with a visit in the reference quarter with age <6 months at time of visit who have interval growth documented indicator", patientUnderSixMonthWithBirthWeightAndIntervalGrowthDocumentedInLastVisit,
                           ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
                              
                   dsd.addColumn("PDC10N", "Number of of children with a visit in the reference quarter with age <6 months at time of visit who have interval growth documented ",
                           new Mapped(patientUnderSixMonthWithBirthWeightAndIntervalGrowthDocumentedInLastVisitIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
                   
                   SqlCohortDefinition patientUnderSixMonthWithBirthWeightDocumentedInLastVisit=new SqlCohortDefinition();
                   patientUnderSixMonthWithBirthWeightDocumentedInLastVisit.setName("patientUnderSixMonthWithBirthWeightAndIntervalGrowthDocumented");
                   patientUnderSixMonthWithBirthWeightDocumentedInLastVisit.setQuery("select lastEnc.patient_id from (select * from (select * from encounter where form_id="+PDCVisitForm.getFormId()+" and voided=0 order by encounter_datetime desc) as orderedEnc group by orderedEnc.encounter_id) as lastEnc"
					+" inner join person p on lastEnc.patient_id=p.person_id and DATEDIFF(lastEnc.encounter_datetime,p.birthdate)<180 and p.voided=0"
					+" inner join obs o on lastEnc.patient_id=o.person_id and o.concept_id="+birthWeight.getConceptId()+" and o.voided=0"
					+" where lastEnc.encounter_datetime >= :startDate and lastEnc.encounter_datetime <= :endDate");
                   patientUnderSixMonthWithBirthWeightDocumentedInLastVisit.addParameter(new Parameter("startDate","startDate",Date.class));
                   patientUnderSixMonthWithBirthWeightDocumentedInLastVisit.addParameter(new Parameter("endDate","endDate",Date.class));
                   
                   CohortIndicator patientUnderSixMonthWithBirthWeightDocumentedInLastVisitIndicatorDenominator = Indicators.newCountIndicator("Number of children with a visit in the reference quarter with age <6 months at time of visit who have interval growth documented indicator", patientUnderSixMonthWithBirthWeightDocumentedInLastVisit,
                           ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
                              
                   dsd.addColumn("PDC10D", "Number of children with a visit in the reference quarter with age <6 months at time of visit who have interval growth documented",
                           new Mapped(patientUnderSixMonthWithBirthWeightDocumentedInLastVisitIndicatorDenominator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
                 
  
 //PDC11
                   
                   SqlCohortDefinition patientUnderSixMonthWithInadaquateIntervalGrowthDocumentedInLastVisit=new SqlCohortDefinition();
                   patientUnderSixMonthWithInadaquateIntervalGrowthDocumentedInLastVisit.setName("patientUnderSixMonthWithInadaquateIntervalGrowthDocumentedInLastVisit");
                   patientUnderSixMonthWithInadaquateIntervalGrowthDocumentedInLastVisit.setQuery("select e.patient_id from encounter e"
					+" inner join person p on e.patient_id=p.person_id and DATEDIFF(e.encounter_datetime,p.birthdate)<180 and p.voided=0"
					+" inner join obs o on e.patient_id=o.person_id and e.encounter_id=o.encounter_id and o.concept_id="+intervalgrowthCoded.getConceptId()+" and o.value_coded="+inadequate.getConceptId()+" and o.voided=0"
					+" where e.form_id="+PDCVisitForm.getFormId()+" and  e.encounter_datetime >= :startDate and e.encounter_datetime <= :endDate ");
                   patientUnderSixMonthWithInadaquateIntervalGrowthDocumentedInLastVisit.addParameter(new Parameter("startDate","startDate",Date.class));
                   patientUnderSixMonthWithInadaquateIntervalGrowthDocumentedInLastVisit.addParameter(new Parameter("endDate","endDate",Date.class));
                   
                   
                   CohortIndicator patientUnderSixMonthWithInadaquateIntervalGrowthDocumentedInLastVisitIndicatorNumerator = Indicators.newCountIndicator("Number of children with a visit in the reference quarter with age <6months at the time of visit who have inadequate interval growth indicator", patientUnderSixMonthWithInadaquateIntervalGrowthDocumentedInLastVisit,
                           ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
                              
                   dsd.addColumn("PDC11N", "Number of children with a visit in the reference quarter with age <6months at the time of visit who have inadequate interval growth ",
                           new Mapped(patientUnderSixMonthWithInadaquateIntervalGrowthDocumentedInLastVisitIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
 //PDC12

           /* SqlCohortDefinition patientWithPDCWeightForHeightzScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit=new SqlCohortDefinition();
            patientWithPDCWeightForHeightzScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit.setName("patientWithPDCWeightForHeightzScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit");
            patientWithPDCWeightForHeightzScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit.setQuery("select e.patient_id from encounter e"
                    +" inner join obs o on e.patient_id=o.person_id and e.encounter_id=o.encounter_id and o.concept_id="+PDCweightForHeightZScore.getConceptId()+" and (o.value_coded="+zScoreGreaterThatMinesThreeAndLessThanTwo.getConceptId()+" or o.value_coded="+zSccoreLessThanThree.getConceptId()+") and o.voided=0"
                    +" where e.form_id="+PDCVisitForm.getFormId()+" and  e.encounter_datetime >= :startDate and e.encounter_datetime <= :endDate ");
            patientWithPDCWeightForHeightzScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit.addParameter(new Parameter("startDate","startDate",Date.class));
            patientWithPDCWeightForHeightzScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit.addParameter(new Parameter("endDate","endDate",Date.class));
*/
            SqlCohortDefinition patientWithPDCWeightForHeightzScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit=new SqlCohortDefinition();
            patientWithPDCWeightForHeightzScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit.setName("patientWithPDCWeightForHeightzScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit");
            patientWithPDCWeightForHeightzScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit.setQuery("Select lastenc.patient_id " +
                    "from " +
                    "(select * from (select e.patient_id,e.encounter_id,e.encounter_datetime from encounter e where e.form_id="+PDCVisitForm.getFormId()+" and e.voided=0 order by e.encounter_datetime desc) ordered group by ordered.patient_id) lastenc, obs o " +
                    " where " +
                    "lastenc.encounter_id=o.encounter_id and o.concept_id="+PDCweightForHeightZScore.getConceptId()+" and (o.value_coded="+zScoreGreaterThatMinesThreeAndLessThanTwo.getConceptId()+" or o.value_coded="+zSccoreLessThanThree.getConceptId()+") and o.voided=0 and lastenc.encounter_datetime>= :startDate and lastenc.encounter_datetime <= :endDate");
            patientWithPDCWeightForHeightzScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit.addParameter(new Parameter("startDate","startDate",Date.class));
            patientWithPDCWeightForHeightzScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit.addParameter(new Parameter("endDate","endDate",Date.class));






            /*SqlCohortDefinition patientWithPDCWeightForHeightzScoreDocumentedInLastVisit=new SqlCohortDefinition();
            patientWithPDCWeightForHeightzScoreDocumentedInLastVisit.setName("patientWithPDCWeightForHeightzScoreDocumentedInLastVisit");
            patientWithPDCWeightForHeightzScoreDocumentedInLastVisit.setQuery("select e.patient_id from encounter e"
                    +" inner join obs o on e.patient_id=o.person_id and e.encounter_id=o.encounter_id and o.concept_id="+PDCweightForHeightZScore.getConceptId()+" and o.voided=0"
                    +" where e.form_id="+PDCVisitForm.getFormId()+" and  e.encounter_datetime >= :startDate and e.encounter_datetime <= :endDate ");
            patientWithPDCWeightForHeightzScoreDocumentedInLastVisit.addParameter(new Parameter("startDate","startDate",Date.class));
            patientWithPDCWeightForHeightzScoreDocumentedInLastVisit.addParameter(new Parameter("endDate","endDate",Date.class));*/

            SqlCohortDefinition patientWithPDCWeightForHeightzScoreDocumentedInLastVisit=new SqlCohortDefinition();
            patientWithPDCWeightForHeightzScoreDocumentedInLastVisit.setName("patientWithPDCWeightForHeightzScoreDocumentedInLastVisit");
            patientWithPDCWeightForHeightzScoreDocumentedInLastVisit.setQuery("Select lastenc.patient_id " +
                    "from " +
                    "(select * from (select e.patient_id,e.encounter_id,e.encounter_datetime from encounter e where e.form_id="+PDCVisitForm.getFormId()+" and e.voided=0 order by e.encounter_datetime desc) ordered group by ordered.patient_id) lastenc, obs o " +
                    "where " +
                    "lastenc.encounter_id=o.encounter_id and o.concept_id="+PDCweightForHeightZScore.getConceptId()+" and o.voided=0 and lastenc.encounter_datetime>= :startDate and lastenc.encounter_datetime <= :endDate ");
            patientWithPDCWeightForHeightzScoreDocumentedInLastVisit.addParameter(new Parameter("startDate","startDate",Date.class));
            patientWithPDCWeightForHeightzScoreDocumentedInLastVisit.addParameter(new Parameter("endDate","endDate",Date.class));


            CohortIndicator patientWithPDCWeightForHeightzScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisitIndicatorNumerator = Indicators.newCountIndicator("Number of children with a visit in the reference quarter who have wt/ht z-score <=-2 at last appointment indicator", patientWithPDCWeightForHeightzScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit,
                    ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

            dsd.addColumn("PDC12N", "Number of children with a visit in the reference quarter who have wt/ht z-score <=-2 at last appointment ",
                    new Mapped(patientWithPDCWeightForHeightzScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisitIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

            CohortIndicator patientWithPDCWeightForHeightzScoreDocumentedInLastVisitIndicatorDenominator = Indicators.newCountIndicator("Number of children with a visit in the reference quarter who had wt/ht z-score documented at last appointment indicator", patientWithPDCWeightForHeightzScoreDocumentedInLastVisit,
                    ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

            dsd.addColumn("PDC12D", "Number of children with a visit in the reference quarter who had wt/ht z-score documented at last appointment",
                    new Mapped(patientWithPDCWeightForHeightzScoreDocumentedInLastVisitIndicatorDenominator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");


//PDC13
/*

            SqlCohortDefinition patientWithPDCheightForAgeZScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit=new SqlCohortDefinition();
            patientWithPDCheightForAgeZScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit.setName("patientWithPDCheightForAgeZScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit");
            patientWithPDCheightForAgeZScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit.setQuery("select e.patient_id from encounter e"
                    +" inner join obs o on e.patient_id=o.person_id and e.encounter_id=o.encounter_id and o.concept_id="+PDCHeightForAgeZScore.getConceptId()+" and (o.value_coded="+zScoreGreaterThatMinesThreeAndLessThanTwo.getConceptId()+" or o.value_coded="+zSccoreLessThanThree.getConceptId()+") and o.voided=0"
                    +" where e.form_id="+PDCVisitForm.getFormId()+" and  e.encounter_datetime >= :startDate and e.encounter_datetime <= :endDate ");
            patientWithPDCheightForAgeZScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit.addParameter(new Parameter("startDate","startDate",Date.class));
            patientWithPDCheightForAgeZScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit.addParameter(new Parameter("endDate","endDate",Date.class));
*/



            SqlCohortDefinition patientWithPDCheightForAgeZScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit=new SqlCohortDefinition();
            patientWithPDCheightForAgeZScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit.setName("patientWithPDCheightForAgeZScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit");
            patientWithPDCheightForAgeZScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit.setQuery("Select lastenc.patient_id " +
                    "from " +
                    "(select * from (select e.patient_id,e.encounter_id,e.encounter_datetime from encounter e where e.form_id="+PDCVisitForm.getFormId()+" and e.voided=0 order by e.encounter_datetime desc) ordered group by ordered.patient_id) lastenc, obs o " +
                    " where " +
                    "lastenc.encounter_id=o.encounter_id and o.concept_id="+PDCHeightForAgeZScore.getConceptId()+" and (o.value_coded="+zScoreGreaterThatMinesThreeAndLessThanTwo.getConceptId()+" or o.value_coded="+zSccoreLessThanThree.getConceptId()+") and o.voided=0 and lastenc.encounter_datetime>= :startDate and lastenc.encounter_datetime <= :endDate");
            patientWithPDCheightForAgeZScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit.addParameter(new Parameter("startDate","startDate",Date.class));
            patientWithPDCheightForAgeZScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit.addParameter(new Parameter("endDate","endDate",Date.class));




            /*SqlCohortDefinition patientWithPDCheightForAgeZScoreDocumentedInLastVisit=new SqlCohortDefinition();
            patientWithPDCheightForAgeZScoreDocumentedInLastVisit.setName("patientWithPDCheightForAgeZScoreDocumentedInLastVisit");
            patientWithPDCheightForAgeZScoreDocumentedInLastVisit.setQuery("select e.patient_id from encounter e"
                    +" inner join obs o on e.patient_id=o.person_id and e.encounter_id=o.encounter_id and o.concept_id="+PDCHeightForAgeZScore.getConceptId()+" and o.voided=0"
                    +" where e.form_id="+PDCVisitForm.getFormId()+" and  e.encounter_datetime >= :startDate and e.encounter_datetime <= :endDate ");
            patientWithPDCheightForAgeZScoreDocumentedInLastVisit.addParameter(new Parameter("startDate","startDate",Date.class));
            patientWithPDCheightForAgeZScoreDocumentedInLastVisit.addParameter(new Parameter("endDate","endDate",Date.class));
*/

            SqlCohortDefinition patientWithPDCheightForAgeZScoreDocumentedInLastVisit=new SqlCohortDefinition();
            patientWithPDCheightForAgeZScoreDocumentedInLastVisit.setName("patientWithPDCheightForAgeZScoreDocumentedInLastVisit");
            patientWithPDCheightForAgeZScoreDocumentedInLastVisit.setQuery("Select lastenc.patient_id " +
                    "from " +
                    "(select * from (select e.patient_id,e.encounter_id,e.encounter_datetime from encounter e where e.form_id="+PDCVisitForm.getFormId()+" and e.voided=0 order by e.encounter_datetime desc) ordered group by ordered.patient_id) lastenc, obs o " +
                    "where " +
                    "lastenc.encounter_id=o.encounter_id and o.concept_id="+PDCHeightForAgeZScore.getConceptId()+" and o.voided=0 and lastenc.encounter_datetime>= :startDate and lastenc.encounter_datetime <= :endDate ");
            patientWithPDCheightForAgeZScoreDocumentedInLastVisit.addParameter(new Parameter("startDate","startDate",Date.class));
            patientWithPDCheightForAgeZScoreDocumentedInLastVisit.addParameter(new Parameter("endDate","endDate",Date.class));




            CohortIndicator patientWithPDCheightForAgeZScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisitIndicatorNumerator = Indicators.newCountIndicator("Number of children with a visit in the reference quarter who have ht/age z-score <=-2 at last appointment indicator", patientWithPDCheightForAgeZScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisit,
                    ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

            dsd.addColumn("PDC13N", "Number of children with a visit in the reference quarter who have ht/age z-score <=-2 at last appointment",
                    new Mapped(patientWithPDCheightForAgeZScoreGreaterThatMinesThreeAndLessThanTwoDocumentedInLastVisitIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

            CohortIndicator patientWithPDCheightForAgeZScoreDocumentedInLastVisitIndicatorDenominator = Indicators.newCountIndicator("Number of children with a visit in the reference quarter who had ht/age z-score documented at last appointment indicator", patientWithPDCheightForAgeZScoreDocumentedInLastVisit,
                    ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));

            dsd.addColumn("PDC13D", "Number of children with a visit in the reference quarter who had ht/age z-score documented at last appointment",
                    new Mapped(patientWithPDCheightForAgeZScoreDocumentedInLastVisitIndicatorDenominator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");


//PDC14
                   
                  /* NumericObsCohortDefinition patientWithCorrectedAge=Cohorts.createNumericObsCohortDefinition("patientWithCorrectedAge", correctedAge, 40.0, RangeComparator.LESS_THAN, TimeModifier.LAST);
                   
                   SqlCohortDefinition patientWithOneMonthToTwoYearAndWithHeightWeightHCAndZScoresInLastEncounter=new SqlCohortDefinition();
                   patientWithOneMonthToTwoYearAndWithHeightWeightHCAndZScoresInLastEncounter.setName("patientWithOneMonthToTwoYearAndWithHeightWeightHCAndZScoresInLastEncounter");
                   patientWithOneMonthToTwoYearAndWithHeightWeightHCAndZScoresInLastEncounter.setQuery("select lastEnc.patient_id from (select * from (select * from encounter where form_id="+PDCVisitForm.getFormId()+" and voided=0 order by encounter_datetime desc) as orderedEnc group by orderedEnc.encounter_id) as lastEnc"
                	+" inner join person p on lastEnc.patient_id=p.person_id and DATEDIFF(lastEnc.encounter_datetime,p.birthdate)<730 and DATEDIFF(lastEnc.encounter_datetime,p.birthdate)>30 and p.voided=0"
                	+" inner join obs o1 on lastEnc.patient_id=o1.person_id and o1.concept_id="+weight.getConceptId()+" and o1.voided=0"
                	+" inner join obs o2 on lastEnc.patient_id=o2.person_id and o2.concept_id="+height.getConceptId()+" and o2.voided=0"
                	+" inner join obs o3 on lastEnc.patient_id=o3.person_id and o3.concept_id="+headCircumference.getConceptId()+" and o3.voided=0"						
                	+" inner join obs o4 on lastEnc.patient_id=o4.person_id and o4.concept_id="+weightForAgeZScore.getConceptId()+" and o4.voided=0"
					+" inner join obs o5 on lastEnc.patient_id=o5.person_id and o5.concept_id="+heightForAgeZScore.getConceptId()+" and o5.voided=0"
					+" inner join obs o6 on lastEnc.patient_id=o6.person_id and o6.concept_id="+HCForAgeZScore.getConceptId()+" and o6.voided=0"
					+" inner join obs o7 on lastEnc.patient_id=o7.person_id and o7.concept_id="+weightForHeightZScore.getConceptId()+" and o7.voided=0"					
					+" where lastEnc.encounter_datetime >= :startDate and lastEnc.encounter_datetime <= :endDate");
                   patientWithOneMonthToTwoYearAndWithHeightWeightHCAndZScoresInLastEncounter.addParameter(new Parameter("startDate","startDate",Date.class));
                   patientWithOneMonthToTwoYearAndWithHeightWeightHCAndZScoresInLastEncounter.addParameter(new Parameter("endDate","endDate",Date.class));
*/

            NumericObsCohortDefinition patientWithCorrectedAge=Cohorts.createNumericObsCohortDefinition("patientWithCorrectedAge", correctedAge, 40.0, RangeComparator.LESS_THAN, TimeModifier.LAST);

            SqlCohortDefinition patientWithOneMonthToTwoYearAndWithHeightWeightHCAndZScoresInLastEncounter=new SqlCohortDefinition();
            patientWithOneMonthToTwoYearAndWithHeightWeightHCAndZScoresInLastEncounter.setName("patientWithOneMonthToTwoYearAndWithHeightWeightHCAndZScoresInLastEncounter");
            patientWithOneMonthToTwoYearAndWithHeightWeightHCAndZScoresInLastEncounter.setQuery("select lastEnc.patient_id from (select * from (select * from encounter where form_id="+PDCVisitForm.getFormId()+" and voided=0 order by encounter_datetime desc) as orderedEnc group by orderedEnc.encounter_id) as lastEnc"
                    +" inner join person p on lastEnc.patient_id=p.person_id and DATEDIFF(lastEnc.encounter_datetime,p.birthdate)<730 and p.voided=0"
                    +" inner join obs o1 on lastEnc.patient_id=o1.person_id and o1.concept_id="+weight.getConceptId()+" and o1.voided=0"
                    +" inner join obs o2 on lastEnc.patient_id=o2.person_id and o2.concept_id="+height.getConceptId()+" and o2.voided=0"
                    +" inner join obs o3 on lastEnc.patient_id=o3.person_id and o3.concept_id="+headCircumference.getConceptId()+" and o3.voided=0"
                    +" inner join obs o4 on lastEnc.patient_id=o4.person_id and o4.concept_id="+correctedAge.getConceptId()+" and o4.voided=0"
                    +" inner join obs o5 on lastEnc.patient_id=o5.person_id and o5.concept_id="+prematureBirth.getConceptId()+" and o5.voided=0"
                    +" inner join obs o6 on lastEnc.patient_id=o6.person_id and o6.concept_id="+lowBirthWeight.getConceptId()+" and o6.voided=0"
                    +" where lastEnc.encounter_datetime >= :startDate and lastEnc.encounter_datetime <= :endDate");
            patientWithOneMonthToTwoYearAndWithHeightWeightHCAndZScoresInLastEncounter.addParameter(new Parameter("startDate","startDate",Date.class));
            patientWithOneMonthToTwoYearAndWithHeightWeightHCAndZScoresInLastEncounter.addParameter(new Parameter("endDate","endDate",Date.class));



          /*  CompositionCohortDefinition patientWithweightForAgeZScoreInLastEncounterWithoutCorrectedAge = new CompositionCohortDefinition();
                   patientWithweightForAgeZScoreInLastEncounterWithoutCorrectedAge.setName("patientWithweightForAgeZScoreInLastEncounterWithoutCorrectedAge");
                   patientWithweightForAgeZScoreInLastEncounterWithoutCorrectedAge.addParameter(new Parameter("startDate", "startDate", Date.class));
                   patientWithweightForAgeZScoreInLastEncounterWithoutCorrectedAge.addParameter(new Parameter("endDate", "endDate", Date.class));
                   patientWithweightForAgeZScoreInLastEncounterWithoutCorrectedAge.getSearches().put("1", new Mapped(patientWithCorrectedAge,null));
                   patientWithweightForAgeZScoreInLastEncounterWithoutCorrectedAge.getSearches().put("2", new Mapped(patientWithOneMonthToTwoYearAndWithHeightWeightHCAndZScoresInLastEncounter, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
                   patientWithweightForAgeZScoreInLastEncounterWithoutCorrectedAge.setCompositionString("2 AND (NOT 1)");
          */
                   CohortIndicator patientWithweightForAgeZScoreInLastEncounterWithoutCorrectedAgeIndicatorNumerator = Indicators.newCountIndicator("Number of children with a visit in the reference quarter who are  < 2 years and >1 month and no documented corrected age <0 or 40 week at time of encounter who have wt/ht/HC  measured and z scores determined at last appointment", patientWithOneMonthToTwoYearAndWithHeightWeightHCAndZScoresInLastEncounter,
                           ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
                              
                   dsd.addColumn("PDC14N", "Number of children with a visit in the reference quarter who are  < 2 years and >1 month and no documented corrected age <0 or 40 week at time of encounter who have wt/ht/HC  measured and z scores determined at last appointment",
                           new Mapped(patientWithweightForAgeZScoreInLastEncounterWithoutCorrectedAgeIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
                   
                /*
                   SqlCohortDefinition patientWithOneMonthToTwoYearAtLastEncounter=new SqlCohortDefinition();
                   patientWithOneMonthToTwoYearAtLastEncounter.setName("patientWithOneMonthToTwoYearAtLastEncounter");
                   patientWithOneMonthToTwoYearAtLastEncounter.setQuery("select lastEnc.patient_id from (select * from (select * from encounter where form_id="+PDCVisitForm.getFormId()+" and voided=0 order by encounter_datetime desc) as orderedEnc group by orderedEnc.encounter_id) as lastEnc"
                	+" inner join person p on lastEnc.patient_id=p.person_id and DATEDIFF(lastEnc.encounter_datetime,p.birthdate)<730 and DATEDIFF(lastEnc.encounter_datetime,p.birthdate)>30 and p.voided=0"
                	+" where lastEnc.encounter_datetime >= :startDate and lastEnc.encounter_datetime <= :endDate");
                   patientWithOneMonthToTwoYearAtLastEncounter.addParameter(new Parameter("startDate","startDate",Date.class));
                   patientWithOneMonthToTwoYearAtLastEncounter.addParameter(new Parameter("endDate","endDate",Date.class));
*/
            SqlCohortDefinition patientWithOneMonthToTwoYearAtLastEncounter=new SqlCohortDefinition();
            patientWithOneMonthToTwoYearAtLastEncounter.setName("patientWithOneMonthToTwoYearAtLastEncounter");
            patientWithOneMonthToTwoYearAtLastEncounter.setQuery("select lastEnc.patient_id from (select * from (select * from encounter where form_id="+PDCVisitForm.getFormId()+" and voided=0 order by encounter_datetime desc) as orderedEnc group by orderedEnc.encounter_id) as lastEnc"
                    +" inner join person p on lastEnc.patient_id=p.person_id and DATEDIFF(lastEnc.encounter_datetime,p.birthdate)<730 and p.voided=0"
                    +" inner join obs o5 on lastEnc.patient_id=o5.person_id and o5.concept_id="+prematureBirth.getConceptId()+" and o5.voided=0"
                    +" inner join obs o6 on lastEnc.patient_id=o6.person_id and o6.concept_id="+lowBirthWeight.getConceptId()+" and o6.voided=0"
                    +" where lastEnc.encounter_datetime >= :startDate and lastEnc.encounter_datetime <= :endDate");
            patientWithOneMonthToTwoYearAtLastEncounter.addParameter(new Parameter("startDate","startDate",Date.class));
            patientWithOneMonthToTwoYearAtLastEncounter.addParameter(new Parameter("endDate","endDate",Date.class));


            /*CompositionCohortDefinition patientWithOneMonthToTwoYearAtLastEncounterWithoutCorrectedAge = new CompositionCohortDefinition();
                   patientWithOneMonthToTwoYearAtLastEncounterWithoutCorrectedAge.setName("patientWithweightForAgeZScoreInLastEncounterWithoutCorrectedAge");
                   patientWithOneMonthToTwoYearAtLastEncounterWithoutCorrectedAge.addParameter(new Parameter("startDate", "startDate", Date.class));
                   patientWithOneMonthToTwoYearAtLastEncounterWithoutCorrectedAge.addParameter(new Parameter("endDate", "endDate", Date.class));
                   patientWithOneMonthToTwoYearAtLastEncounterWithoutCorrectedAge.getSearches().put("1", new Mapped(patientWithCorrectedAge,null));
                   patientWithOneMonthToTwoYearAtLastEncounterWithoutCorrectedAge.getSearches().put("2", new Mapped(patientWithOneMonthToTwoYearAtLastEncounter, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
                   patientWithOneMonthToTwoYearAtLastEncounterWithoutCorrectedAge.setCompositionString("2 AND (NOT 1)");
                 */
                   CohortIndicator patientWithOneMonthToTwoYearAtLastEncounterWithoutCorrectedAgeIndicatorDenominator = Indicators.newCountIndicator("Number of children with a visit in the reference quarter who are  < 2 years and >1 month and no documented corrected age <0 or 40 week at time of encounter", patientWithOneMonthToTwoYearAtLastEncounter,
                           ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
                              
                   dsd.addColumn("PDC14D", "Number of children with a visit in the reference quarter who are  < 2 years and >1 month and no documented corrected age <0 or 40 week at time of encounter",
                           new Mapped(patientWithOneMonthToTwoYearAtLastEncounterWithoutCorrectedAgeIndicatorDenominator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
                
//PDC15
                   
                   CodedObsCohortDefinition abnormalCommunication=Cohorts.createCodedObsCohortDefinition("abnormalCommunication", onOrAfterOnOrBefore, communication, abnormal, SetComparator.IN, TimeModifier.LAST);
                   
                   CodedObsCohortDefinition abnormalLargeMuscleMovement=Cohorts.createCodedObsCohortDefinition("abnormalLargeMuscleMovement", onOrAfterOnOrBefore, largeMuscleMovement, abnormal, SetComparator.IN, TimeModifier.LAST);
                   
                   CodedObsCohortDefinition abnormalSmallMuscleMovement=Cohorts.createCodedObsCohortDefinition("abnormalSmallMuscleMovement", onOrAfterOnOrBefore, smallMuscleMovement, abnormal, SetComparator.IN, TimeModifier.LAST);

                   CodedObsCohortDefinition abnormalProblemSolving=Cohorts.createCodedObsCohortDefinition("abnormalProblemSolving", onOrAfterOnOrBefore, problemSolving, abnormal, SetComparator.IN, TimeModifier.LAST);

                   CodedObsCohortDefinition abnormalPersonalSocial=Cohorts.createCodedObsCohortDefinition("abnormalPersonalSocial", onOrAfterOnOrBefore, personalSocial, abnormal, SetComparator.IN, TimeModifier.LAST);

                   CodedObsCohortDefinition ASQAgeUsedCohort=Cohorts.createCodedObsCohortDefinition("ASQAgeUsedCohort",onOrAfterOnOrBefore, ASQAgeUsed, null, SetComparator.IN, TimeModifier.LAST);
        
                   
                   CompositionCohortDefinition patientWithASQAndGrayBlackInQuarter = new CompositionCohortDefinition();
                   patientWithASQAndGrayBlackInQuarter.setName("patientWithASQAndGrayBlackInQuarter");
                   patientWithASQAndGrayBlackInQuarter.addParameter(new Parameter("startDate", "startDate", Date.class));
                   patientWithASQAndGrayBlackInQuarter.addParameter(new Parameter("endDate", "endDate", Date.class));
                   patientWithASQAndGrayBlackInQuarter.getSearches().put("1", new Mapped(ASQAgeUsedCohort, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
                   patientWithASQAndGrayBlackInQuarter.getSearches().put("2", new Mapped(abnormalCommunication, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
                   patientWithASQAndGrayBlackInQuarter.getSearches().put("3", new Mapped(abnormalLargeMuscleMovement, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
                   patientWithASQAndGrayBlackInQuarter.getSearches().put("4", new Mapped(abnormalSmallMuscleMovement, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
                   patientWithASQAndGrayBlackInQuarter.getSearches().put("5", new Mapped(abnormalProblemSolving, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
                   patientWithASQAndGrayBlackInQuarter.getSearches().put("6", new Mapped(abnormalPersonalSocial, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
                   patientWithASQAndGrayBlackInQuarter.setCompositionString("1 AND (2 OR 3 OR 4 OR 5 OR 6)");
                              
                   CohortIndicator patientWithASQAndGrayBlackInQuarterIndicatorNumerator = Indicators.newCountIndicator("Number of children with a completed and documented ASQ in the reference quarter scoring with borderline or concern noted (gray/black) in at least one domain  indicator", patientWithASQAndGrayBlackInQuarter,
                       ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
                          
                   
                   dsd.addColumn("PDC15N", "Number of children with a completed and documented ASQ in the reference quarter scoring with borderline or concern noted (gray/black) in at least one domain",
                       new Mapped(patientWithASQAndGrayBlackInQuarterIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
           
                   CohortIndicator patientWithASQInQuarterIndicatorDenominator = Indicators.newCountIndicator("Number of children with an ASQ completed and documented in the reference quarter Indicator", ASQAgeUsedCohort,
                           ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
                       
                   dsd.addColumn("PDC15D", "Number of children with an ASQ completed and documented in the reference quarter",
                           new Mapped(patientWithASQInQuarterIndicatorDenominator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
                           
//PDC16
                   
                   /*SqlCohortDefinition patientBetween6And12MonthsWithASQDocumentedInLastEncounter=new SqlCohortDefinition();
                   patientBetween6And12MonthsWithASQDocumentedInLastEncounter.setName("patientBetween6And12MonthsWithASQDocumentedInLastEncounter");
                   patientBetween6And12MonthsWithASQDocumentedInLastEncounter.setQuery("select lastEnc.patient_id from (select * from (select * from encounter where form_id="+PDCVisitForm.getFormId()+" and voided=0 order by encounter_datetime desc) as orderedEnc group by orderedEnc.encounter_id) as lastEnc"
                	+" inner join person p on lastEnc.patient_id=p.person_id and DATEDIFF(lastEnc.encounter_datetime,p.birthdate)<=365 and DATEDIFF(lastEnc.encounter_datetime,p.birthdate)>180 and p.voided=0"
                	+" inner join obs o1 on lastEnc.patient_id=o1.person_id and o1.concept_id="+ASQAgeUsed.getConceptId()+" and o1.voided=0"
                	+" where lastEnc.encounter_datetime >= :startDate and lastEnc.encounter_datetime <= :endDate");
                   patientBetween6And12MonthsWithASQDocumentedInLastEncounter.addParameter(new Parameter("startDate","startDate",Date.class));
                   patientBetween6And12MonthsWithASQDocumentedInLastEncounter.addParameter(new Parameter("endDate","endDate",Date.class));
                   */

            SqlCohortDefinition patientBetween6And12MonthsWithASQDocumentedInLastEncounter=new SqlCohortDefinition();
            patientBetween6And12MonthsWithASQDocumentedInLastEncounter.setName("patientBetween6And12MonthsWithASQDocumentedInLastEncounter");
            patientBetween6And12MonthsWithASQDocumentedInLastEncounter.setQuery("select lastEnc.patient_id from (select * from (select * from encounter where form_id="+PDCVisitForm.getFormId()+" and voided=0 order by encounter_datetime desc) as orderedEnc group by orderedEnc.encounter_id) as lastEnc"
                    +" inner join person p on lastEnc.patient_id=p.person_id and DATEDIFF(lastEnc.encounter_datetime,p.birthdate)<=365 and DATEDIFF(lastEnc.encounter_datetime,p.birthdate)>180 and p.voided=0"
                    +" inner join obs o1 on lastEnc.patient_id=o1.person_id and o1.concept_id="+communication.getConceptId()+" and o1.voided=0"
                    +" inner join obs o2 on lastEnc.patient_id=o2.person_id and o2.concept_id="+largeMuscleMovement.getConceptId()+" and o2.voided=0"
                    +" inner join obs o3 on lastEnc.patient_id=o3.person_id and o3.concept_id="+smallMuscleMovement.getConceptId()+" and o3.voided=0"
                    +" inner join obs o4 on lastEnc.patient_id=o4.person_id and o4.concept_id="+problemSolving.getConceptId()+" and o4.voided=0"
                    +" inner join obs o5 on lastEnc.patient_id=o5.person_id and o5.concept_id="+personalSocial.getConceptId()+" and o5.voided=0"
                    +" where lastEnc.encounter_datetime >= :startDate and lastEnc.encounter_datetime <= :endDate");
            patientBetween6And12MonthsWithASQDocumentedInLastEncounter.addParameter(new Parameter("startDate","startDate",Date.class));
            patientBetween6And12MonthsWithASQDocumentedInLastEncounter.addParameter(new Parameter("endDate","endDate",Date.class));

            SqlCohortDefinition patientBetween6And12MonthsInLastEncounter=new SqlCohortDefinition();
                   patientBetween6And12MonthsInLastEncounter.setName("patientBetween6And12MonthsWithASQDocumentedInLastEncounter");
                   patientBetween6And12MonthsInLastEncounter.setQuery("select lastEnc.patient_id from (select * from (select * from encounter where form_id="+PDCVisitForm.getFormId()+" and voided=0 order by encounter_datetime desc) as orderedEnc group by orderedEnc.encounter_id) as lastEnc"
                	+" inner join person p on lastEnc.patient_id=p.person_id and DATEDIFF(lastEnc.encounter_datetime,p.birthdate)<=365 and DATEDIFF(lastEnc.encounter_datetime,p.birthdate)>180 and p.voided=0"
                	+" where lastEnc.encounter_datetime >= :startDate and lastEnc.encounter_datetime <= :endDate");
                   patientBetween6And12MonthsInLastEncounter.addParameter(new Parameter("startDate","startDate",Date.class));
                   patientBetween6And12MonthsInLastEncounter.addParameter(new Parameter("endDate","endDate",Date.class));
                   
                   
                   CohortIndicator patientBetween6And12MonthsWithASQDocumentedInLastEncounterIndicatorNumerator = Indicators.newCountIndicator("Number of children with a visit in the reference quarter who were between 6 and 12 months of age at encounter with at least 1 ASQ score documented indicator", patientBetween6And12MonthsWithASQDocumentedInLastEncounter,
                           ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
                              
                       
                   dsd.addColumn("PDC16N", "Number of children with a visit in the reference quarter who were between 6 and 12 months of age at encounter with at least 1 ASQ score documented",
                           new Mapped(patientBetween6And12MonthsWithASQDocumentedInLastEncounterIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
                   
                   
                   CohortIndicator patientBetween6And12MonthsInLastEncounterIndicatorNumerator = Indicators.newCountIndicator("Number of children with a visit in the reference quarter who were between 6 and 12 months of age at encounter indicator", patientBetween6And12MonthsInLastEncounter,
                           ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
                              
                       
                   dsd.addColumn("PDC16D", "Number of children with a visit in the reference quarter who were between 6 and 12 months of age at encounter",
                           new Mapped(patientBetween6And12MonthsInLastEncounterIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
                   
//PDC17
                   
                   //EncounterCohortDefinition  patientWithVisitInQuarter=Cohorts.createEncounterBasedOnForms("patientWithVisitInQuarter", onOrAfterOnOrBefore, PDCVisitForms);
                   
                   //CodedObsCohortDefinition patientsWithECDCounseling=Cohorts.createCodedObsCohortDefinition("patientsWithECDCounseling", ECDCounselingSession,yes, SetComparator.IN, TimeModifier.ANY);
                   SqlCohortDefinition patientsWithECDCounseling=new SqlCohortDefinition();
                   patientsWithECDCounseling.setName("patientsWithECDCounseling");
                   patientsWithECDCounseling.setQuery("select distinct o.person_id from obs o where o.concept_id="+ECDCounselingSession.getConceptId()+" and o.value_coded="+yes.getConceptId()+" " +
                   		"and o.voided=0 order by o.obs_datetime desc");
                   
                   CompositionCohortDefinition currentlyEnrolledPatientsWithAnyECDCounselingInQuarter = new CompositionCohortDefinition();
                   currentlyEnrolledPatientsWithAnyECDCounselingInQuarter.setName("currentlyEnrolledPatientsWithAnyECDCounselingInQuarter");
                   currentlyEnrolledPatientsWithAnyECDCounselingInQuarter.addParameter(new Parameter("startDate", "startDate", Date.class));
                   currentlyEnrolledPatientsWithAnyECDCounselingInQuarter.addParameter(new Parameter("endDate", "endDate", Date.class));
                   currentlyEnrolledPatientsWithAnyECDCounselingInQuarter.getSearches().put("1", new Mapped(inPDCProgramByStartDateAndEndDate, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
                   currentlyEnrolledPatientsWithAnyECDCounselingInQuarter.getSearches().put("2", new Mapped(patientWithVisitInQuarter, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
                   currentlyEnrolledPatientsWithAnyECDCounselingInQuarter.getSearches().put("3", new Mapped(patientsWithECDCounseling, null));
                   currentlyEnrolledPatientsWithAnyECDCounselingInQuarter.setCompositionString("1 AND 2 AND 3");
                              
                   CohortIndicator currentlyEnrolledPatientsWithAnyECDCounselingInQuarterIndicatorNumerator = Indicators.newCountIndicator("Number of children with a visit in the reference quarter whose caregivers have had at least one documented ECD counseling session Indicator", currentlyEnrolledPatientsWithAnyECDCounselingInQuarter,
                       ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
                          
                   
                   dsd.addColumn("PDC17N", "Number of currently enrolled children receiving social support at last visit  (defined as “already receiving” or “recommended and starting today” answer",
                       new Mapped(currentlyEnrolledPatientsWithAnyECDCounselingInQuarterIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");
           
                    
                   CohortIndicator patientsWithVisitInQuarterIndicatorNumerator = Indicators.newCountIndicator("Number of children who had a visit in the reference quarter Indicator", patientWithVisitInQuarter,
                           ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
                              
                       
                       dsd.addColumn("PDC17D", "Number of children who had a visit in the reference quarter",
                           new Mapped(patientsWithVisitInQuarterIndicatorNumerator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

//              Add patients seen in period by disesases with dieseases

            EncounterCohortDefinition patientSeen = Cohorts.createEncounterParameterizedByDate("Patients seen",onOrAfterOnOrBefore, PDCEncounterType);
            SqlCohortDefinition patientWithEncByProvider = Cohorts.getEncountersByProvider("patientWithEncByProvider");
            for(int i=0;i<reasonsForReferral.size();i++){
                // SqlCohortDefinition patientWithRefferralReasonOnIntakeFormAnyTime=Cohorts.getPatientsWithObservationInForm("patientWithReferralReasonAnyTime",PDCIntakeForm,reasoForReferral);

                SqlCohortDefinition patientWithRefferralReasonAnyTimeBeforeEndDate=new SqlCohortDefinition();
                patientWithRefferralReasonAnyTimeBeforeEndDate.setName("patientWithRefferralReasonAnyTimeBeforeEndDate"+reasonsForReferral.get(i).getName().toString());
                patientWithRefferralReasonAnyTimeBeforeEndDate.setQuery("select distinct o.person_id from encounter e, obs o where e.encounter_id=o.encounter_id and (e.form_id="+PDCIntakeForm.getFormId()+" or e.form_id="+PDCReferralForm.getFormId()+") and o.concept_id="+reasoForReferral.getConceptId()+" " +
                        "and o.voided=0 and o.value_coded="+reasonsForReferral.get(i).getConceptId()+" and e.voided=0 and o.obs_datetime<= :endDate");
                patientWithRefferralReasonAnyTimeBeforeEndDate.addParameter(new Parameter("endDate","endDate",Date.class));


                //SqlCohortDefinition patientWithRefferralReasonOnReferralFormAnyTime=Cohorts.getPatientsWithObservationInForm("patientWithReferralReasonAnyTime",PDCReferralForm,reasoForReferral);

               /* SqlCohortDefinition patientWithRefferralReasonOnReferralFormAnyTime=new SqlCohortDefinition();
                patientWithRefferralReasonOnReferralFormAnyTime.setName("patientWithRefferralReasonOnIntakeFormAnyTime");
                patientWithRefferralReasonOnReferralFormAnyTime.setQuery("select distinct o.person_id from encounter e, obs o where e.encounter_id=o.encounter_id and e.form_id="+PDCReferralForm.getFormId()+" and o.concept_id="+reasoForReferral.getConceptId()+" " +
                "and o.voided=0 and e.voided=0 and (o.value_numeric is NOT NULL or o.value_coded is NOT NULL or o.value_datetime is NOT NULL or o.value_boolean is NOT NULL)");
                */

                CompositionCohortDefinition patientSeenInPeriodWithRefferralReasonOnIntakeOrReferralFormByCategory = new CompositionCohortDefinition();
                patientSeenInPeriodWithRefferralReasonOnIntakeOrReferralFormByCategory.setName("patientSeenInPeriodWithRefferralReasonOnIntakeOrReferralFormByCategory"+reasonsForReferral.get(i).getName().toString());
                patientSeenInPeriodWithRefferralReasonOnIntakeOrReferralFormByCategory.addParameter(new Parameter("endDate", "endDate", Date.class));
                patientSeenInPeriodWithRefferralReasonOnIntakeOrReferralFormByCategory.addParameter(new Parameter("startDate", "startDate", Date.class));
                patientSeenInPeriodWithRefferralReasonOnIntakeOrReferralFormByCategory.addParameter(new Parameter("provider", "provider", Provider.class));
                patientSeenInPeriodWithRefferralReasonOnIntakeOrReferralFormByCategory.getSearches().put("1", new Mapped(patientSeen, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
                patientSeenInPeriodWithRefferralReasonOnIntakeOrReferralFormByCategory.getSearches().put("2", new Mapped(patientWithRefferralReasonAnyTimeBeforeEndDate, ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
                patientSeenInPeriodWithRefferralReasonOnIntakeOrReferralFormByCategory.getSearches().put("3", new Mapped(inPDCProgramByDate, ParameterizableUtil.createParameterMappings("onOrBefore=${endDate}")));
                patientSeenInPeriodWithRefferralReasonOnIntakeOrReferralFormByCategory.getSearches().put("4", new Mapped(patientWithEncByProvider, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},provider=${provider}")));

                patientSeenInPeriodWithRefferralReasonOnIntakeOrReferralFormByCategory.setCompositionString("1 AND 2 AND 3 AND 4");

                CohortIndicator patientSeenInPeriodWithRefferralReasonOnIntakeOrReferralFormByCategoryIndicator = Indicators.newCountIndicator("Number of patients seen in period per referral reason category indicator "+reasonsForReferral.get(i).getName().toString(), patientSeenInPeriodWithRefferralReasonOnIntakeOrReferralFormByCategory,
                        ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate},provider=${provider}"));


                dsd.addColumn("PDCFLASH1"+i, "Number of patients seen in period per referral reason category"+reasonsForReferral.get(i).getName().toString(),
                        new Mapped(patientSeenInPeriodWithRefferralReasonOnIntakeOrReferralFormByCategoryIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate},provider=${provider}")), "");

            }


            //Low Birth weight or Premature
            SqlCohortDefinition patientWithPrematureRefferralReasonAnyTimeBeforeEndDate=new SqlCohortDefinition();
            patientWithPrematureRefferralReasonAnyTimeBeforeEndDate.setName("patientWithPrematureRefferralReasonAnyTimeBeforeEndDate");
            patientWithPrematureRefferralReasonAnyTimeBeforeEndDate.setQuery("select distinct o.person_id from encounter e, obs o where e.encounter_id=o.encounter_id and (e.form_id="+PDCIntakeForm.getFormId()+" or e.form_id="+PDCReferralForm.getFormId()+") and o.concept_id="+reasoForReferral.getConceptId()+" " +
                    "and o.voided=0 and o.value_coded="+prematureBirth.getConceptId()+" and e.voided=0 and o.obs_datetime<= :endDate");
            patientWithPrematureRefferralReasonAnyTimeBeforeEndDate.addParameter(new Parameter("endDate","endDate",Date.class));

            SqlCohortDefinition patientWithLBWRefferralReasonAnyTimeBeforeEndDate=new SqlCohortDefinition();
            patientWithLBWRefferralReasonAnyTimeBeforeEndDate.setName("patientWithLBWRefferralReasonAnyTimeBeforeEndDate");
            patientWithLBWRefferralReasonAnyTimeBeforeEndDate.setQuery("select distinct o.person_id from encounter e, obs o where e.encounter_id=o.encounter_id and (e.form_id="+PDCIntakeForm.getFormId()+" or e.form_id="+PDCReferralForm.getFormId()+") and o.concept_id="+reasoForReferral.getConceptId()+" " +
                    "and o.voided=0 and o.value_coded="+lowBirthWeight.getConceptId()+" and e.voided=0 and o.obs_datetime<= :endDate");
            patientWithLBWRefferralReasonAnyTimeBeforeEndDate.addParameter(new Parameter("endDate","endDate",Date.class));

            //SqlCohortDefinition patientWithRefferralReasonOnReferralFormAnyTime=Cohorts.getPatientsWithObservationInForm("patientWithReferralReasonAnyTime",PDCReferralForm,reasoForReferral);

               /* SqlCohortDefinition patientWithRefferralReasonOnReferralFormAnyTime=new SqlCohortDefinition();
                patientWithRefferralReasonOnReferralFormAnyTime.setName("patientWithRefferralReasonOnIntakeFormAnyTime");
                patientWithRefferralReasonOnReferralFormAnyTime.setQuery("select distinct o.person_id from encounter e, obs o where e.encounter_id=o.encounter_id and e.form_id="+PDCReferralForm.getFormId()+" and o.concept_id="+reasoForReferral.getConceptId()+" " +
                "and o.voided=0 and e.voided=0 and (o.value_numeric is NOT NULL or o.value_coded is NOT NULL or o.value_datetime is NOT NULL or o.value_boolean is NOT NULL)");
                */

            CompositionCohortDefinition patientSeenInPeriodWithPrematureOrLBWRefferralReasonOnIntakeOrReferralFormByCategory = new CompositionCohortDefinition();
            patientSeenInPeriodWithPrematureOrLBWRefferralReasonOnIntakeOrReferralFormByCategory.setName("patientSeenInPeriodWithPrematureOrLBWRefferralReasonOnIntakeOrReferralFormByCategory".toString());
            patientSeenInPeriodWithPrematureOrLBWRefferralReasonOnIntakeOrReferralFormByCategory.addParameter(new Parameter("endDate", "endDate", Date.class));
            patientSeenInPeriodWithPrematureOrLBWRefferralReasonOnIntakeOrReferralFormByCategory.addParameter(new Parameter("startDate", "startDate", Date.class));
            patientSeenInPeriodWithPrematureOrLBWRefferralReasonOnIntakeOrReferralFormByCategory.getSearches().put("1", new Mapped(patientSeen, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
            patientSeenInPeriodWithPrematureOrLBWRefferralReasonOnIntakeOrReferralFormByCategory.getSearches().put("2", new Mapped(patientWithPrematureRefferralReasonAnyTimeBeforeEndDate, ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
            patientSeenInPeriodWithPrematureOrLBWRefferralReasonOnIntakeOrReferralFormByCategory.getSearches().put("3", new Mapped(patientWithLBWRefferralReasonAnyTimeBeforeEndDate, ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
            patientSeenInPeriodWithPrematureOrLBWRefferralReasonOnIntakeOrReferralFormByCategory.getSearches().put("4", new Mapped(inPDCProgramByDate, ParameterizableUtil.createParameterMappings("onOrBefore=${endDate}")));
            patientSeenInPeriodWithPrematureOrLBWRefferralReasonOnIntakeOrReferralFormByCategory.setCompositionString("1 AND (2 OR 3) AND 4");

            CohortIndicator patientSeenInPeriodWithPrematureOrLBWRefferralReasonOnIntakeOrReferralFormByCategoryIndicator = Indicators.newCountIndicator("Number of patients seen in period per referral reason category indicator "+prematureBirth.getName().toString()+" OR " +lowBirthWeight.getName().toString(), patientSeenInPeriodWithPrematureOrLBWRefferralReasonOnIntakeOrReferralFormByCategory,
                    ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));


            dsd.addColumn("PDCFLASHPrematureORLBW", "Number of patients seen in period per referral reason category"+prematureBirth.getName().toString()+" OR " +lowBirthWeight.getName().toString() ,
                    new Mapped(patientSeenInPeriodWithPrematureOrLBWRefferralReasonOnIntakeOrReferralFormByCategoryIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");



            // Total Patients seen in period
            CompositionCohortDefinition PatientsSeenInPeriod = new CompositionCohortDefinition();
            PatientsSeenInPeriod.setName("PatientsSeenInPeriod");
            PatientsSeenInPeriod.addParameter(new Parameter("startDate", "startDate", Date.class));
            PatientsSeenInPeriod.addParameter(new Parameter("endDate", "endDate", Date.class));
            PatientsSeenInPeriod.getSearches().put("1", new Mapped(patientSeen, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
            PatientsSeenInPeriod.getSearches().put("2", new Mapped(inPDCProgramByDate, ParameterizableUtil.createParameterMappings("onOrBefore=${endDate}")));

            PatientsSeenInPeriod.setCompositionString("1 AND 2");

            CohortIndicator PatientsSeenInPeriodIndicator = Indicators.newCountIndicator("PatientsSeenInPeriodIndicator", PatientsSeenInPeriod,
                    ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));


            dsd.addColumn("PDCFLASH20", "Total patients seen in the period",
                    new Mapped(PatientsSeenInPeriodIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

            //Missed visits

            SqlCohortDefinition missedVisitPatientsInperiod=new SqlCohortDefinition();
            missedVisitPatientsInperiod.setName("missedVisitPatientsInperiod");
            missedVisitPatientsInperiod.setQuery("SELECT  person_id from\n" +
                    "(\n" +
                    "    SELECT max(obs.value_datetime) last_scheduled_apointment_in_period, obs.person_id\n" +
                    "    from obs\n" +
                    "    LEFT JOIN encounter on encounter.encounter_id=obs.encounter_id\n" +
                    "    where obs.voided=0\n" +
                    "    and obs.concept_id= "+returnVisitDate.getConceptId()+"\n" +
                    "    and encounter.voided=0\n" +
                    "    and encounter.encounter_type= "+PDCEncounterType.getEncounterTypeId()+"\n" +
                    "    and value_datetime>= :startDate \n" +
                    "    and value_datetime<= :endDate\n" +
                    "    group by obs.person_id\n" +
                    " ) appointment_scheduled_in_period  \n" +
                    "LEFT JOIN \n" +
                    "(\n" +
                    "    SELECT encounter.patient_id, max(encounter_datetime) last_encounter_in_period\n" +
                    "    from encounter\n" +
                    "    where encounter.voided=0\n" +
                    "    and encounter.encounter_type= "+PDCEncounterType.getEncounterTypeId()+"\n" +
                    "    and encounter_datetime>= :startDate \n" +
                    "    and encounter_datetime<= :endDate\n" +
                    "    group by encounter.patient_id\n" +
                    " ) visit_in_period on visit_in_period.patient_id=appointment_scheduled_in_period.person_id #did the patient have a PDC encounter in the period\n" +
                    " where last_encounter_in_period is null \n" +
                    "or last_encounter_in_period<date_add(last_scheduled_apointment_in_period, INTERVAL 2 DAY)");
            missedVisitPatientsInperiod.addParameter(new Parameter("startDate","startDate",Date.class));
            missedVisitPatientsInperiod.addParameter(new Parameter("endDate","endDate",Date.class));

            CompositionCohortDefinition patientsWithMissedVisitInPeriod = new CompositionCohortDefinition();
            patientsWithMissedVisitInPeriod.setName("patientsWithMissedVisitInPeriod");
            patientsWithMissedVisitInPeriod.addParameter(new Parameter("startDate", "startDate", Date.class));
            patientsWithMissedVisitInPeriod.addParameter(new Parameter("endDate", "endDate", Date.class));
            patientsWithMissedVisitInPeriod.getSearches().put("1", new Mapped(missedVisitPatientsInperiod, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));

            patientsWithMissedVisitInPeriod.setCompositionString("1");

            CohortIndicator patientsWithMissedVisitInPeriodIndicator = Indicators.newCountIndicator("patientsWithMissedVisitInPeriodIndicator", patientsWithMissedVisitInPeriod,
                    ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));


            dsd.addColumn("PDCFLASH21", "Total patients with missed visit in period",
                    new Mapped(patientsWithMissedVisitInPeriodIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

            // # patients who completed program without either LTFU or died as completed reason

            ProgramEnrollmentCohortDefinition completedInPDCProgramInperiod=Cohorts.createProgramCompletedInPeriod("Completed PDCProgram in period",PDCProgram);
            SqlCohortDefinition programWithLTFOutcome = Cohorts.getPatientsWithOutcomeprogramEndReasons("Completed PDCProgram with LTFU outcome in period",PDCProgramEndReasons,LostToFollowUPOutcome);
            SqlCohortDefinition programWithDeathCauseSevereDehydrationOutcome = Cohorts.getPatientsWithOutcomeprogramEndReasons("Completed PDCProgram with DeathCauseSevereDehydrationOutcome outcome in period",PDCProgramEndReasons,DeathCauseSevereDehydrationOutcome);
            SqlCohortDefinition programWithDeathCauseHypoglycemiaOutcome = Cohorts.getPatientsWithOutcomeprogramEndReasons("Completed PDCProgram with DeathCauseHypoglycemiaOutcome outcome in period",PDCProgramEndReasons,DeathCauseHypoglycemiaOutcome);
            SqlCohortDefinition programWithDeathCauseRespiratoryDistressOutcome = Cohorts.getPatientsWithOutcomeprogramEndReasons("Completed PDCProgram with DeathCauseRespiratoryDistressOutcome outcome in period",PDCProgramEndReasons,DeathCauseRespiratoryDistressOutcome);
            SqlCohortDefinition programWithDeathCauseSevereInfectionOutcome = Cohorts.getPatientsWithOutcomeprogramEndReasons("Completed PDCProgram with DeathCauseSevereInfectionOutcome outcome in period",PDCProgramEndReasons,DeathCauseSevereInfectionOutcome);
            SqlCohortDefinition programWithDeathUnknownCauseOutcome = Cohorts.getPatientsWithOutcomeprogramEndReasons("Completed PDCProgram with DeathUnknownCauseOutcome outcome in period",PDCProgramEndReasons,DeathUnknownCauseOutcome);


            CompositionCohortDefinition patientsWithoutLTFUANDDiedAsPDCOutcomes = new CompositionCohortDefinition();
            patientsWithoutLTFUANDDiedAsPDCOutcomes.setName("patientsWithoutLTFUANDDiedAsPDCOutcomes");
            patientsWithoutLTFUANDDiedAsPDCOutcomes.addParameter(new Parameter("startDate", "startDate", Date.class));
            patientsWithoutLTFUANDDiedAsPDCOutcomes.addParameter(new Parameter("endDate", "endDate", Date.class));
            patientsWithoutLTFUANDDiedAsPDCOutcomes.addParameter(new Parameter("completedOnOrBefore", "completedOnOrBefore", Date.class));
            patientsWithoutLTFUANDDiedAsPDCOutcomes.addParameter(new Parameter("completedOnOrAfter", "completedOnOrAfter", Date.class));
            patientsWithoutLTFUANDDiedAsPDCOutcomes.getSearches().put("1", new Mapped(completedInPDCProgramInperiod, ParameterizableUtil.createParameterMappings("completedOnOrBefore=${completedOnOrBefore},completedOnOrAfter=${completedOnOrAfter}")));
            patientsWithoutLTFUANDDiedAsPDCOutcomes.getSearches().put("2", new Mapped(programWithLTFOutcome, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
            patientsWithoutLTFUANDDiedAsPDCOutcomes.getSearches().put("3", new Mapped(programWithDeathCauseSevereDehydrationOutcome, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
            patientsWithoutLTFUANDDiedAsPDCOutcomes.getSearches().put("4", new Mapped(programWithDeathCauseHypoglycemiaOutcome, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
            patientsWithoutLTFUANDDiedAsPDCOutcomes.getSearches().put("5", new Mapped(programWithDeathCauseRespiratoryDistressOutcome, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
            patientsWithoutLTFUANDDiedAsPDCOutcomes.getSearches().put("6", new Mapped(programWithDeathCauseSevereInfectionOutcome, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
            patientsWithoutLTFUANDDiedAsPDCOutcomes.getSearches().put("7", new Mapped(programWithDeathUnknownCauseOutcome, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));

            patientsWithoutLTFUANDDiedAsPDCOutcomes.setCompositionString("1 and NOT (2 OR 3 OR 4 OR 5 OR 6 OR 7) ");

            CohortIndicator patientsWithoutLTFUANDDiedAsPDCOutcomesIndicator = Indicators.newCountIndicator("patientsWithoutLTFUANDDiedAsPDCOutcomesIndicator", patientsWithoutLTFUANDDiedAsPDCOutcomes,
                    ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate},completedOnOrBefore=${endDate},completedOnOrAfter=${startDate}"));


            dsd.addColumn("PDCFLASH22", "patients who completed program without either LTFU or died as completed reason",
                    new Mapped(patientsWithoutLTFUANDDiedAsPDCOutcomesIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

            // Patients who died
            SqlCohortDefinition getPatientsDiedByStartDateAndEndDate = Cohorts.getPatientsDiedByStartDateAndEndDate("Patients who died in period");

            CompositionCohortDefinition patientsWhoDied = new CompositionCohortDefinition();
            patientsWhoDied.setName("patientsWhoDied");
            patientsWhoDied.addParameter(new Parameter("startDate", "startDate", Date.class));
            patientsWhoDied.addParameter(new Parameter("endDate", "endDate", Date.class));
            patientsWhoDied.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
            patientsWhoDied.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
            patientsWhoDied.getSearches().put("1", new Mapped(getPatientsDiedByStartDateAndEndDate, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
            patientsWhoDied.getSearches().put("2", new Mapped(programWithDeathCauseSevereDehydrationOutcome, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
            patientsWhoDied.getSearches().put("3", new Mapped(programWithDeathCauseHypoglycemiaOutcome, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
            patientsWhoDied.getSearches().put("4", new Mapped(programWithDeathCauseRespiratoryDistressOutcome, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
            patientsWhoDied.getSearches().put("5", new Mapped(programWithDeathCauseSevereInfectionOutcome, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
            patientsWhoDied.getSearches().put("6", new Mapped(programWithDeathUnknownCauseOutcome, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));

            patientsWhoDied.setCompositionString("1 OR 2 OR 3 OR 4 OR 5 OR 6 ");

            CohortIndicator patientsWhoDiedIndicator = Indicators.newCountIndicator("patientsWhoDiedIndicator", patientsWhoDied,
                    ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate},onOrBefore=${endDate},onOrAfter=${startDate}"));


            dsd.addColumn("PDCFLASH23", "Patients who died",
                    new Mapped(patientsWhoDiedIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");


            //In program by start date
            InProgramCohortDefinition inProgramByStartDate = Cohorts.createInProgramParameterizableByDate("inProgramByStartDate",PDCProgram);

            CompositionCohortDefinition patientsInProgramByStartDateComposition = new CompositionCohortDefinition();
            patientsInProgramByStartDateComposition.setName("patientsInProgramByStartDateComposition");
            patientsInProgramByStartDateComposition.addParameter(new Parameter("onDate", "onDate", Date.class));
            patientsInProgramByStartDateComposition.getSearches().put("1", new Mapped(inProgramByStartDate, ParameterizableUtil.createParameterMappings("onDate=${onDate}")));

            patientsInProgramByStartDateComposition.setCompositionString("1");

            CohortIndicator patientsInProgramByStartDateCompositionIndicator = Indicators.newCountIndicator("patientsInProgramByStartDateCompositionIndicator", patientsInProgramByStartDateComposition,
                    ParameterizableUtil.createParameterMappings("onDate=${startDate}"));


            dsd.addColumn("PDCFLASH24", "In program by start date",
                    new Mapped(patientsInProgramByStartDateCompositionIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate}")), "");

            //In program by end date
            InProgramCohortDefinition inProgramByEndDate = Cohorts.createInProgramParameterizableByDate("inProgramByEndDate",PDCProgram);

            CompositionCohortDefinition patientsInProgramByEndDateComposition = new CompositionCohortDefinition();
            patientsInProgramByEndDateComposition.setName("patientsInProgramByEndDateComposition");
            patientsInProgramByEndDateComposition.addParameter(new Parameter("onDate", "onDate", Date.class));
            patientsInProgramByEndDateComposition.getSearches().put("1", new Mapped(inProgramByEndDate, ParameterizableUtil.createParameterMappings("onDate=${onDate}")));

            patientsInProgramByEndDateComposition.setCompositionString("1");

            CohortIndicator patientsInProgramByEndDateIndicator = Indicators.newCountIndicator("patientsInProgramByEndDateIndicator", patientsInProgramByEndDateComposition,
                    ParameterizableUtil.createParameterMappings("onDate=${endDate}"));


            dsd.addColumn("PDCFLASH25", "In program by end date",
                    new Mapped(patientsInProgramByEndDateIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");


            // patients to start treatment in the period
            SqlCohortDefinition patientsToStartTreatmentToday = Cohorts.getPatientsWithCodedObservationsBetweenStartDateAndEndDate("patientsToStartTreatmentToday",whenToStartTreatmentCoded,startToday);



            CompositionCohortDefinition patientsToStartTreatmentInThePeriodComposition = new CompositionCohortDefinition();
            patientsToStartTreatmentInThePeriodComposition.setName("patientsToStartTreatmentInThePeriodComposition");
            patientsToStartTreatmentInThePeriodComposition.addParameter(new Parameter("startDate", "startDate", Date.class));
            patientsToStartTreatmentInThePeriodComposition.addParameter(new Parameter("endDate", "endDate", Date.class));
            patientsToStartTreatmentInThePeriodComposition.getSearches().put("1", new Mapped(patientsToStartTreatmentToday, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));

            patientsToStartTreatmentInThePeriodComposition.setCompositionString("1");

            CohortIndicator patientsToStartTreatmentInThePeriodIndicator = Indicators.newCountIndicator("patientsToStartTreatmentInThePeriodIndicator", patientsToStartTreatmentInThePeriodComposition,
                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));


            dsd.addColumn("PDCFLASH26", "patients to start treatment in the period",
                    new Mapped(patientsToStartTreatmentInThePeriodIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



        }
        
        private void setUpProperties() {
                
                onOrAfterOnOrBefore.add("onOrAfter");
                onOrAfterOnOrBefore.add("onOrBefore");
                
                PDCProgram = gp.getProgram(GlobalPropertiesManagement.PDC_PROGRAM);
                reasoForReferral=gp.getConcept(GlobalPropertiesManagement.REASON_FOR_REFERRAL);
                PDCIntakeForm=gp.getForm(GlobalPropertiesManagement.PDC_INTAKE_FORM);
                PDCReferralForm=gp.getForm(GlobalPropertiesManagement.PDC_REFERRAL_FORM);
                PDCVisitForm=gp.getForm(GlobalPropertiesManagement.PDC_VISIT_FORM);
                intakeVisitForms.add(PDCIntakeForm);
                PDCVisitForms.add(PDCVisitForm);
                returnVisitDate=gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE);
                breathingDangerSignsPresent=gp.getConcept(GlobalPropertiesManagement.BREATHING_DANGER_SIGNS_PRESENT);
                convulsionsDangerSignsPresent=gp.getConcept(GlobalPropertiesManagement.CONVULSIONS_DANGER_SIGNS_PRESENT);
                LethargyOrUnresponsivenessDangerSignsPresent=gp.getConcept(GlobalPropertiesManagement.LETHARGY_OR_UNRESPONSIVENESS_DANGER_SIGNS_PRESENT);
                umbilicalCordRednessDangerSigns=gp.getConcept(GlobalPropertiesManagement.UMBILICAL_CORD_REDNESS_DANGER_SIGNS);
                stiffNeckOrBulgingFontanellesDangerSigns=gp.getConcept(GlobalPropertiesManagement.STIFF_NECK_OR_BULGING_FONTANELLES_DANGER_SIGNS_PRESENT);
                ASQAgeUsed=gp.getConcept(GlobalPropertiesManagement.ASQ_SCORE);
                temperature=gp.getConcept(GlobalPropertiesManagement.TEMPERATURE);
                respiratoryRate=gp.getConcept(GlobalPropertiesManagement.RESPIRATORY_RATE);
                yes=gp.getConcept(GlobalPropertiesManagement.YES);
                socialEconomicAssistance=gp.getConcept(GlobalPropertiesManagement.SOCIAL_WORK_ASSESSMENT);
                socialEconomicAssistanceAlreadyReceived=gp.getConcept(GlobalPropertiesManagement.SOCIAL_ECONOMIC_ASSISTANCE_ALREADY_RECEIVED);
                socialEconomicAssistanceRecommanded=gp.getConcept(GlobalPropertiesManagement.SOCIAL__ECONOMIC_ASSISTANCE_RECOMMANDED);
                socialEconomicAssistanceNotRecommanded=gp.getConcept(GlobalPropertiesManagement.SOCIAL__ECONOMIC_ASSISTANCE_NOT_RECOMMANDED);
                socialAssistanceTypes.add(socialEconomicAssistanceNotRecommanded);
                intervalGrogth=gp.getConcept(GlobalPropertiesManagement.INTERVAL_GROWTH);
                birthWeight=gp.getConcept(GlobalPropertiesManagement.BIRTH_WEIGHT);
                correctedAge=gp.getConcept(GlobalPropertiesManagement.CORRECTED_AGE);
                weightForAgeZScore=gp.getConcept(GlobalPropertiesManagement.WTAGEZScore);
              weightForHeightZScore=gp.getConcept(GlobalPropertiesManagement.WTHEIGHTZScore);
            heightForAgeZScore=gp.getConcept(GlobalPropertiesManagement.HEIGHT_FOR_AGE_Z_SCORE);
            HCForAgeZScore=gp.getConcept(GlobalPropertiesManagement.HC_FOR_AGE_ZSCORE);
            headCircumference=gp.getConcept(GlobalPropertiesManagement.HEAD_CIRCUMFERENCE);
            weight=gp.getConcept(GlobalPropertiesManagement.WEIGHT_CONCEPT);
            height=gp.getConcept(GlobalPropertiesManagement.HEIGHT_CONCEPT);
            smallMuscleMovement=gp.getConcept(GlobalPropertiesManagement.SMALL_MUSCLE_MOVEMENTS);
            largeMuscleMovement=gp.getConcept(GlobalPropertiesManagement.LARGE_MUSCLE_MOVEMENTS);
            communication=gp.getConcept(GlobalPropertiesManagement.COMMUNICATION);
            problemSolving=gp.getConcept(GlobalPropertiesManagement.PROBLEM_SOLVING);
            personalSocial=gp.getConcept(GlobalPropertiesManagement.PERSONAL_SOCIAL);
            abnormal=gp.getConcept(GlobalPropertiesManagement.ABNORMAL);
            ECDCounselingSession=gp.getConcept(GlobalPropertiesManagement.ECD_EDUCATION);
            reasonForNotDoingFollowUp=gp.getConcept(GlobalPropertiesManagement.REASON_FOR_NOT_DOING_FOLLOWUP);
            reasonForExitingFromCare=gp.getConcept(GlobalPropertiesManagement.REASON_FOR_EXITING_CARE);
            intervalgrowthCoded=gp.getConcept(GlobalPropertiesManagement.INTERVAL_GROWTH_CODED);
            inadequate=gp.getConcept(GlobalPropertiesManagement.INTERVAL_GROWTH_INADEQUATE);
            lowBirthWeight= gp.getConcept(GlobalPropertiesManagement.LOW_BIRTH_WEIGHT);
            prematureBirth=gp.getConcept(GlobalPropertiesManagement.PRE_MATURE_BIRTH);
            hypoxicIschemicEncephalopathy=gp.getConcept(GlobalPropertiesManagement.HYPOXIC_ISCHEMIS_ENCEPHALOPATHY);
            hydrocephalus=gp.getConcept(GlobalPropertiesManagement.HYDROCEPHALUS);
            trisomy21=gp.getConcept(GlobalPropertiesManagement.TRISOMY21);
            cleftLipOrPalate=gp.getConcept(GlobalPropertiesManagement.CLEFTLIP_OR_PILATE);
            otherDevelopmentalDelay=gp.getConcept(GlobalPropertiesManagement.OTHER_DEVELOPMENT_DELAY);
            severeMalnutrition=gp.getConcept(GlobalPropertiesManagement.SEVERE_MALNUTRITION);
            centralNervousSystemInfection=gp.getConcept(GlobalPropertiesManagement.CENTRAL_NERVOUS_SYSTEM_INFECTION);
            otherNoneCoded=gp.getConcept(GlobalPropertiesManagement.OTHER_NON_CODED);

            reasonsForReferral.add(lowBirthWeight);
            reasonsForReferral.add(prematureBirth);
            reasonsForReferral.add(hypoxicIschemicEncephalopathy);
            reasonsForReferral.add(hydrocephalus);
            reasonsForReferral.add(trisomy21);
            reasonsForReferral.add(cleftLipOrPalate);
            reasonsForReferral.add(otherDevelopmentalDelay);
            reasonsForReferral.add(severeMalnutrition);
            reasonsForReferral.add(centralNervousSystemInfection);
            reasonsForReferral.add(otherNoneCoded);

            PDCweightForAgeZScore=gp.getConcept(GlobalPropertiesManagement.PDC_WEIGHT_FOR_AGE_ZSCORE);
            PDCHeightForAgeZScore=gp.getConcept(GlobalPropertiesManagement.PDC_HEIGHT_FOR_AGE_ZSCORE);
            PDCweightForHeightZScore=gp.getConcept(GlobalPropertiesManagement.PDC_WEIGHT_FOR_HEIGHT_ZSCORE);
            na=gp.getConcept(GlobalPropertiesManagement.NOT_APPLICABLE);
            zScoreGreaterThatMinesThreeAndLessThanTwo= gp.getConcept(GlobalPropertiesManagement.ZSCORE_GREATER_THAN_NEGATIVE_THREE_AND_LESS_THAN_NEGATIVE_TWO);
            zSccoreLessThanThree=gp.getConcept(GlobalPropertiesManagement.ZSCORE_LESS_THAN_NEGATIVE_THREE);
            PDCEncounterType = gp.getEncounterType(GlobalPropertiesManagement.PDC_VISIT);

            PDCProgramEndReasons = gp.getConcept(GlobalPropertiesManagement.PDC_PROGRAM_END_REASONS);
            LostToFollowUPOutcome = gp.getConcept(GlobalPropertiesManagement.LOST_TO_FOLLOWUP_OUTCOME);
            DeathCauseSevereDehydrationOutcome = gp.getConcept(GlobalPropertiesManagement.Death_Cause_Severe_Dehydration_Outcome);
            DeathCauseHypoglycemiaOutcome = gp.getConcept(GlobalPropertiesManagement.Death_Cause_Hypoglycemia_Outcome);
            DeathCauseRespiratoryDistressOutcome = gp.getConcept(GlobalPropertiesManagement.Death_Cause_Respiratory_Distress_Outcome);
            DeathCauseSevereInfectionOutcome = gp.getConcept(GlobalPropertiesManagement.Death_Cause_Severe_Infection_Outcome);
            DeathUnknownCauseOutcome = gp.getConcept(GlobalPropertiesManagement.Death_Unknown_Cause_Outcome);
            whenToStartTreatmentCoded = gp.getConcept(GlobalPropertiesManagement.WHENTOSTARTTREATMENTCODED);
            startToday = gp.getConcept(GlobalPropertiesManagement.STARTTODAY);



        }
}