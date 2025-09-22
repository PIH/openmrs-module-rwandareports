/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.rwandareports.reporting;

import javassist.expr.NewArray;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.*;
import org.openmrs.module.reporting.common.DurationUnit;
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
import org.openmrs.module.rwandareports.util.Indicators;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;

import java.util.Map;

import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class SetupHealtCenterHMISIndicatorMonthlyReport implements SetupReport {

	protected final Log log = LogFactory.getLog(getClass());

	// properties

	private List<Form> OPDForms;

	private Form OPDForm;

	private  Concept caseStatus;

	private  Concept oldCase;

	private  Concept newCase;
    private  Concept primaryDiagnosis;
    private  Concept secondaryDiagnosis;
    private List<Concept> diagnosisConfirmed=new ArrayList<Concept>();
    private  Concept referralDisposition;
    private Concept referredToHospital;
    private Concept catchmentArea;
    private Concept catchmentAreaInZone;
    private Concept catchmentAreaOutZone;

    private Concept fever;
    private Concept pregnancyStatus;
    private Concept pregnantYes;
    private Concept lastDeliveryDate;
    private Concept digestiveMinor;




    private int noneInsuranceID;

	private String hundrepercentInsuredInsuranceIDs;

	private String indigentsInsuranceIDs;

	private  int ICDConceptClassId;


	private  List<String> onOrAfterOnOrBefore =new ArrayList<String>();

	Properties properties = new Properties();

    /**
     * @return 
     */
    @Override
    public String getReportName() {
        return null;
    }

    public void setup() throws Exception {
		
		setUpProperties();


		//Monthly report set-up



		properties.setProperty("hierarchyFields", "countyDistrict:District");



		EncounterCohortDefinition patientWithOPDForm=Cohorts.createEncounterBasedOnForms("patientWithOPDForm",onOrAfterOnOrBefore,OPDForms);

// II. Outpatient
        ReportDefinition monthlyRdII = createReportDefinition("HC HMIS II Outpatient Report", properties);
        monthlyRdII.setBaseCohortDefinition(patientWithOPDForm,
                ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
        monthlyRdII.addDataSetDefinition(createEncounterCohortMonthlyLocationDataSetII(),
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));

        SqlDataSetDefinition insAll = new SqlDataSetDefinition();
        insAll.setName("INS_D_ALL");
        insAll.addParameter(new Parameter("startDate","Start Date",Date.class));
        insAll.addParameter(new Parameter("endDate","End Date",Date.class));

        Helper.saveReportDefinition(monthlyRdII);
        ReportDesign monthlyDesignII = Helper.createRowPerPatientXlsOverviewReportDesign(monthlyRdII,
                "Health_Center_Monthly_HMIS_Report_II.xls", "Health_Center_Monthly_HMIS_Report_II", null);
        Properties monthlyPropsII = new Properties();
        monthlyPropsII.put("repeatingSections", "sheet:1,dataset:Encounter Monthly Data Set II");
        monthlyPropsII.put("sortWeight","5000");
        monthlyDesignII.setProperties(monthlyPropsII);
        Helper.saveReportDesign(monthlyDesignII);


    }
	
	public void delete() {
        Helper.purgeReportDefinition("HC HMIS II Outpatient Report");

    }


	public ReportDefinition createReportDefinition(String name, Properties properties){

		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		reportDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		reportDefinition.addParameter(new Parameter("location", "Location", AllLocation.class, properties));
		reportDefinition.setName(name);
		return reportDefinition;
	}

    // II. Outpatient consultations

    public LocationHierachyIndicatorDataSetDefinition createEncounterCohortMonthlyLocationDataSetII() {
        LocationHierachyIndicatorDataSetDefinition ldsd =
                new LocationHierachyIndicatorDataSetDefinition(createEncounterMonthlyBaseDataSetII());
        ldsd.addBaseDefinition(createCohortMonthlyBaseDataSetII());
        ldsd.setName("Encounter Monthly Data Set II");
        ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
        return ldsd;
    }

    // create Monthly cohort Data set
    private CohortIndicatorDataSetDefinition createCohortMonthlyBaseDataSetII() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("Monthly Cohort Data Set");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        createCohortMonthlyIndicatorsII(dsd);
        return dsd;
    }

    private EncounterIndicatorDataSetDefinition createEncounterMonthlyBaseDataSetII() {
        EncounterIndicatorDataSetDefinition eidsd = new EncounterIndicatorDataSetDefinition();
        eidsd.setName("eidsd");
        eidsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        eidsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        createEncounterMonthlyIndicatorsII(eidsd);
        return eidsd;
    }




// II. Outpatient Consultations/ Consultations Externes: EncounterIndicatorDataSetDefinition

	private void createEncounterMonthlyIndicatorsII(EncounterIndicatorDataSetDefinition dsd) {

		// A) Outpatient Morbidity summary table/ Tableau synthétique Consultations externes

         //Outpatient visits/Consultations Externes


		//New Case

		// Male Under 5 years

		SqlEncounterQuery outpatientVisitsNewCaseMaleUnder5Years=new SqlEncounterQuery();
		outpatientVisitsNewCaseMaleUnder5Years.setName("outpatientVisitsNewCaseMaleUnder5Years");
		outpatientVisitsNewCaseMaleUnder5Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='M' and DATEDIFF(:endDate , p.birthdate) < 1825 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+newCase.getConceptId()+" group by e.encounter_datetime");
		// adultHivVisits.setQuery("select e.encounter_id from encounter e, person p where e.encounter_type in ("+pediHIVEncounterType.getEncounterTypeId()+","+adultHIVEncounterType.getEncounterTypeId()+") and e.encounter_datetime >= :startDate and e.encounter_datetime <= :endDate and p.person_id = e.patient_id and DATEDIFF(:endDate , p.birthdate) >=5475 and e.voided=0 and p.voided=0");
		outpatientVisitsNewCaseMaleUnder5Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsNewCaseMaleUnder5Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsNewCaseMaleUnder5YearsIndicator = new EncounterIndicator();
		outpatientVisitsNewCaseMaleUnder5YearsIndicator.setName("outpatientVisitsNewCaseMaleUnder5YearsIndicator");
		outpatientVisitsNewCaseMaleUnder5YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsNewCaseMaleUnder5Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsNewCaseMaleUnder5YearsIndicator);


		// Female Under 5 years

		SqlEncounterQuery outpatientVisitsNewCaseFemaleUnder5Years=new SqlEncounterQuery();
		outpatientVisitsNewCaseFemaleUnder5Years.setName("outpatientVisitsNewCaseFemaleUnder5Years");
		outpatientVisitsNewCaseFemaleUnder5Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='F' and DATEDIFF(:endDate , p.birthdate) < 1825 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+newCase.getConceptId()+" group by e.encounter_datetime");
		outpatientVisitsNewCaseFemaleUnder5Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsNewCaseFemaleUnder5Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsNewCaseFemaleUnder5YearsIndicator = new EncounterIndicator();
		outpatientVisitsNewCaseFemaleUnder5YearsIndicator.setName("outpatientVisitsNewCaseFemaleUnder5YearsIndicator");
		outpatientVisitsNewCaseFemaleUnder5YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsNewCaseFemaleUnder5Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsNewCaseFemaleUnder5YearsIndicator);

        // Male >=5 y

		SqlEncounterQuery outpatientVisitsNewCaseMaleEqualAndAbove5Years=new SqlEncounterQuery();
		outpatientVisitsNewCaseMaleEqualAndAbove5Years.setName("outpatientVisitsNewCaseMaleEqualAndAbove5Years");
		outpatientVisitsNewCaseMaleEqualAndAbove5Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='M' and DATEDIFF(:endDate , p.birthdate) >= 1825 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+newCase.getConceptId()+" group by e.encounter_datetime");
		outpatientVisitsNewCaseMaleEqualAndAbove5Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsNewCaseMaleEqualAndAbove5Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsNewCaseMaleEqualAndAbove5YearsIndicator = new EncounterIndicator();
		outpatientVisitsNewCaseMaleEqualAndAbove5YearsIndicator.setName("outpatientVisitsNewCaseMaleEqualAndAbove5YearsIndicator");
		outpatientVisitsNewCaseMaleEqualAndAbove5YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsNewCaseMaleEqualAndAbove5Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsNewCaseMaleEqualAndAbove5YearsIndicator);


		// Female >=5 y

		SqlEncounterQuery outpatientVisitsNewCaseFemaleEqualAndAbove5Years=new SqlEncounterQuery();
		outpatientVisitsNewCaseFemaleEqualAndAbove5Years.setName("outpatientVisitsNewCaseFemaleEqualAndAbove5Years");
		outpatientVisitsNewCaseFemaleEqualAndAbove5Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='F' and DATEDIFF(:endDate , p.birthdate) >= 1825 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+newCase.getConceptId()+" group by e.encounter_datetime");
		outpatientVisitsNewCaseFemaleEqualAndAbove5Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsNewCaseFemaleEqualAndAbove5Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsNewCaseFemaleEqualAndAbove5YearsIndicator = new EncounterIndicator();
		outpatientVisitsNewCaseFemaleEqualAndAbove5YearsIndicator.setName("outpatientVisitsNewCaseFemaleEqualAndAbove5YearsIndicator");
		outpatientVisitsNewCaseFemaleEqualAndAbove5YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsNewCaseFemaleEqualAndAbove5Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsNewCaseFemaleEqualAndAbove5YearsIndicator);





		//===========================================
		//Old Case									//
		//===========================================

		// Male Under 5 years

		SqlEncounterQuery outpatientVisitsoldCaseMaleUnder5Years=new SqlEncounterQuery();
		outpatientVisitsoldCaseMaleUnder5Years.setName("outpatientVisitsoldCaseMaleUnder5Years");
		outpatientVisitsoldCaseMaleUnder5Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='M' and DATEDIFF(:endDate , p.birthdate) < 1825 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+oldCase.getConceptId()+" group by e.encounter_datetime");
		// adultHivVisits.setQuery("select e.encounter_id from encounter e, person p where e.encounter_type in ("+pediHIVEncounterType.getEncounterTypeId()+","+adultHIVEncounterType.getEncounterTypeId()+") and e.encounter_datetime >= :startDate and e.encounter_datetime <= :endDate and p.person_id = e.patient_id and DATEDIFF(:endDate , p.birthdate) >=5475 and e.voided=0 and p.voided=0");
		outpatientVisitsoldCaseMaleUnder5Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsoldCaseMaleUnder5Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsoldCaseMaleUnder5YearsIndicator = new EncounterIndicator();
		outpatientVisitsoldCaseMaleUnder5YearsIndicator.setName("outpatientVisitsoldCaseMaleUnder5YearsIndicator");
		outpatientVisitsoldCaseMaleUnder5YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsoldCaseMaleUnder5Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsoldCaseMaleUnder5YearsIndicator);


		// Female Under 5 years

		SqlEncounterQuery outpatientVisitsoldCaseFemaleUnder5Years=new SqlEncounterQuery();
		outpatientVisitsoldCaseFemaleUnder5Years.setName("outpatientVisitsoldCaseFemaleUnder5Years");
		outpatientVisitsoldCaseFemaleUnder5Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='F' and DATEDIFF(:endDate , p.birthdate) < 1825 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+oldCase.getConceptId()+" group by e.encounter_datetime");
		outpatientVisitsoldCaseFemaleUnder5Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsoldCaseFemaleUnder5Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsoldCaseFemaleUnder5YearsIndicator = new EncounterIndicator();
		outpatientVisitsoldCaseFemaleUnder5YearsIndicator.setName("outpatientVisitsoldCaseFemaleUnder5YearsIndicator");
		outpatientVisitsoldCaseFemaleUnder5YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsoldCaseFemaleUnder5Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsoldCaseFemaleUnder5YearsIndicator);
        
        
		// Male >=5 y

		SqlEncounterQuery outpatientVisitsoldCaseMaleEqualAndAbove5Years=new SqlEncounterQuery();
		outpatientVisitsoldCaseMaleEqualAndAbove5Years.setName("outpatientVisitsoldCaseMaleEqualAndAbove5Years");
		outpatientVisitsoldCaseMaleEqualAndAbove5Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='M' and DATEDIFF(:endDate , p.birthdate) >= 1825 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+oldCase.getConceptId()+" group by e.encounter_datetime");
		outpatientVisitsoldCaseMaleEqualAndAbove5Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsoldCaseMaleEqualAndAbove5Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsoldCaseMaleEqualAndAbove5YearsIndicator = new EncounterIndicator();
		outpatientVisitsoldCaseMaleEqualAndAbove5YearsIndicator.setName("outpatientVisitsoldCaseMaleEqualAndAbove5YearsIndicator");
		outpatientVisitsoldCaseMaleEqualAndAbove5YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsoldCaseMaleEqualAndAbove5Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsoldCaseMaleEqualAndAbove5YearsIndicator);


		// Female >=5 y

		SqlEncounterQuery outpatientVisitsoldCaseFemaleEqualAndAbove5Years=new SqlEncounterQuery();
		outpatientVisitsoldCaseFemaleEqualAndAbove5Years.setName("outpatientVisitsoldCaseFemaleEqualAndAbove5Years");
		outpatientVisitsoldCaseFemaleEqualAndAbove5Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='F' and DATEDIFF(:endDate , p.birthdate) >= 1825 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+oldCase.getConceptId()+" group by e.encounter_datetime");
		outpatientVisitsoldCaseFemaleEqualAndAbove5Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsoldCaseFemaleEqualAndAbove5Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsoldCaseFemaleEqualAndAbove5YearsIndicator = new EncounterIndicator();
		outpatientVisitsoldCaseFemaleEqualAndAbove5YearsIndicator.setName("outpatientVisitsoldCaseFemaleEqualAndAbove5YearsIndicator");
		outpatientVisitsoldCaseFemaleEqualAndAbove5YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsoldCaseFemaleEqualAndAbove5Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsoldCaseFemaleEqualAndAbove5YearsIndicator);


	}


	// II. Outpatient Consultations/ Consultations Externes : CohortIndicatorDataSetDefinition
    // Adds 5+ (M/F) columns for a diagnosis matched by concept_name LIKE pattern
    private void addDiag5PlusByNamePattern(
            CohortIndicatorDataSetDefinition dsd,
            String tokenBase, String labelBase, String namePattern,
            Concept caseStatus, Concept newCase,
            AgeCohortDefinition age5plus, GenderCohortDefinition males) {

        // Diagnosis + new case inside window via your helper
        SqlCohortDefinition diagNewCase =
                patientWithICDCodeObsByStartDateAndEndDate(namePattern, caseStatus, newCase);

        // Male ≥5
        CompositionCohortDefinition male5plus = new CompositionCohortDefinition();
        male5plus.setName(tokenBase + ".M.5plus");
        male5plus.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        male5plus.addParameter(new Parameter("startDate","startDate",Date.class));
        male5plus.addParameter(new Parameter("endDate","endDate",Date.class));
        male5plus.getSearches().put("1", new Mapped<CohortDefinition>(diagNewCase,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        male5plus.getSearches().put("2", new Mapped<CohortDefinition>(age5plus,
                ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        male5plus.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        male5plus.setCompositionString("1 and 2 and 3");

        CohortIndicator maleInd = Indicators.newCohortIndicator(
                tokenBase + ".male5plusIndicator", male5plus,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        dsd.addColumn(tokenBase + ".1", labelBase + " (M ≥5y)",
                new Mapped<CohortIndicator>(maleInd,
                        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
                "");

        // Female ≥5 (= not males)
        CompositionCohortDefinition female5plus = new CompositionCohortDefinition();
        female5plus.setName(tokenBase + ".F.5plus");
        female5plus.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        female5plus.addParameter(new Parameter("startDate","startDate",Date.class));
        female5plus.addParameter(new Parameter("endDate","endDate",Date.class));
        female5plus.getSearches().put("1", new Mapped<CohortDefinition>(diagNewCase,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        female5plus.getSearches().put("2", new Mapped<CohortDefinition>(age5plus,
                ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        female5plus.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        female5plus.setCompositionString("1 and 2 and (not 3)");

        CohortIndicator femaleInd = Indicators.newCohortIndicator(
                tokenBase + ".female5plusIndicator", female5plus,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        dsd.addColumn(tokenBase + ".2", labelBase + " (F ≥5y)",
                new Mapped<CohortIndicator>(femaleInd,
                        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
                "");
    }

    // Female-only variant (for Gynecological problems)
    private void addDiag5PlusFemaleOnlyByNamePattern(
            CohortIndicatorDataSetDefinition dsd,
            String tokenBase, String labelBase, String namePattern,
            Concept caseStatus, Concept newCase,
            AgeCohortDefinition age5plus, GenderCohortDefinition males) {

        SqlCohortDefinition diagNewCase =
                patientWithICDCodeObsByStartDateAndEndDate(namePattern, caseStatus, newCase);

        CompositionCohortDefinition female5plus = new CompositionCohortDefinition();
        female5plus.setName(tokenBase + ".F.5plus");
        female5plus.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        female5plus.addParameter(new Parameter("startDate","startDate",Date.class));
        female5plus.addParameter(new Parameter("endDate","endDate",Date.class));
        female5plus.getSearches().put("1", new Mapped<CohortDefinition>(diagNewCase,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        female5plus.getSearches().put("2", new Mapped<CohortDefinition>(age5plus,
                ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        female5plus.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        female5plus.setCompositionString("1 and 2 and (not 3)");

        CohortIndicator femaleInd = Indicators.newCohortIndicator(
                tokenBase + ".female5plusIndicator", female5plus,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        dsd.addColumn(tokenBase + ".2", labelBase + " (F ≥5y)",
                new Mapped<CohortIndicator>(femaleInd,
                        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
                "");
    }

    // Adds 6 columns (M/F x 3 age bands) for a diagnosis name pattern using your new-case helper
    private void addDiagByBands(
            CohortIndicatorDataSetDefinition dsd,
            String tokenBase, String labelBase, String namePattern,
            Concept caseStatus, Concept newCase,
            AgeCohortDefinition age0to19, AgeCohortDefinition age20to39, AgeCohortDefinition age40plus,
            GenderCohortDefinition males) {

        SqlCohortDefinition diagNewCase =
                patientWithICDCodeObsByStartDateAndEndDate(namePattern, caseStatus, newCase);

        // local lambda to reduce repetition
        java.util.function.BiFunction<AgeCohortDefinition, Boolean, CompositionCohortDefinition> make =
                (ageBand, male) -> {
                    CompositionCohortDefinition c = new CompositionCohortDefinition();
                    c.setName(tokenBase + "." + (male ? "M" : "F"));
                    c.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
                    c.addParameter(new Parameter("startDate","startDate",Date.class));
                    c.addParameter(new Parameter("endDate","endDate",Date.class));
                    c.getSearches().put("1", new Mapped<CohortDefinition>(diagNewCase,
                            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
                    c.getSearches().put("2", new Mapped<CohortDefinition>(ageBand,
                            ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
                    c.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
                    c.setCompositionString(male ? "1 and 2 and 3" : "1 and 2 and (not 3)");
                    return c;
                };

        // 0–19
        CohortIndicator m019 = Indicators.newCohortIndicator(tokenBase + ".M_0_19",
                make.apply(age0to19, true),
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        CohortIndicator f019 = Indicators.newCohortIndicator(tokenBase + ".F_0_19",
                make.apply(age0to19, false),
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        dsd.addColumn(tokenBase + ".1", labelBase + " (M 0–19)", new Mapped<>(m019,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
        dsd.addColumn(tokenBase + ".2", labelBase + " (F 0–19)", new Mapped<>(f019,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // 20–39
        CohortIndicator m2039 = Indicators.newCohortIndicator(tokenBase + ".M_20_39",
                make.apply(age20to39, true),
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        CohortIndicator f2039 = Indicators.newCohortIndicator(tokenBase + ".F_20_39",
                make.apply(age20to39, false),
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        dsd.addColumn(tokenBase + ".3", labelBase + " (M 20–39)", new Mapped<>(m2039,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
        dsd.addColumn(tokenBase + ".4", labelBase + " (F 20–39)", new Mapped<>(f2039,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // ≥40
        CohortIndicator m40p = Indicators.newCohortIndicator(tokenBase + ".M_40_plus",
                make.apply(age40plus, true),
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        CohortIndicator f40p = Indicators.newCohortIndicator(tokenBase + ".F_40_plus",
                make.apply(age40plus, false),
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        dsd.addColumn(tokenBase + ".5", labelBase + " (M ≥40)", new Mapped<>(m40p,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
        dsd.addColumn(tokenBase + ".6", labelBase + " (F ≥40)", new Mapped<>(f40p,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
    }

    /** Attach 4 single-row SqlDataSetDefinitions for Section D (Insurance) */
    private void attachInsuranceSectionD(ReportDefinition rd) {

    }






    // AND of multiple cohorts (all are date-bounded SQL/comp cohorts)
    private CompositionCohortDefinition composeAND(CohortDefinition... parts) {
        CompositionCohortDefinition c = new CompositionCohortDefinition();
        c.addParameter(new Parameter("startDate","startDate",Date.class));
        c.addParameter(new Parameter("endDate","endDate",Date.class));
        StringBuilder expr = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String key = String.valueOf(i+1);
            c.getSearches().put(key,
                    new Mapped<CohortDefinition>(parts[i],
                            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
            if (i > 0) expr.append(" and ");
            expr.append(key);
        }
        c.setCompositionString(expr.toString());
        return c;
    }

    // NOT of a cohort
    private CompositionCohortDefinition composeNOT(CohortDefinition part) {
        CompositionCohortDefinition c = new CompositionCohortDefinition();
        c.addParameter(new Parameter("startDate","startDate",Date.class));
        c.addParameter(new Parameter("endDate","endDate",Date.class));
        c.getSearches().put("1",
                new Mapped<CohortDefinition>(part,
                        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        c.setCompositionString("not 1");
        return c;
    }


    /**
     * Adds 4 columns:
     *   tokenBase.M.U5, tokenBase.F.U5, tokenBase.M.5P, tokenBase.F.5P
     * built from: base ∩ ageBand ∩ (male / not male)
     */
    private void addByAgeGender(
            CohortIndicatorDataSetDefinition dsd,
            String tokenBase, String labelBase,
            CohortDefinition base,
            AgeCohortDefinition u5, AgeCohortDefinition age5plus,
            GenderCohortDefinition males) {

        // M U5  (base ∧ U5 ∧ M)
        CompositionCohortDefinition mU5 = new CompositionCohortDefinition();
        mU5.setName(tokenBase + ".M.U5");
        mU5.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        mU5.addParameter(new Parameter("startDate","startDate",Date.class));
        mU5.addParameter(new Parameter("endDate","endDate",Date.class));
        mU5.getSearches().put("1", new Mapped<CohortDefinition>(base,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        mU5.getSearches().put("2", new Mapped<CohortDefinition>(u5,
                ParameterizableUtil.createParameterMappings("effectiveDate=${endDate}")));
        mU5.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        mU5.setCompositionString("1 and 2 and 3");
        CohortIndicator indMU5 = Indicators.newCohortIndicator(tokenBase + ".ind.M.U5", mU5,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn(tokenBase + ".M.U5", labelBase + " (M <5y)",
                new Mapped<CohortIndicator>(indMU5,
                        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // F U5  (base ∧ U5 ∧ ¬M)
        CompositionCohortDefinition fU5 = new CompositionCohortDefinition();
        fU5.setName(tokenBase + ".F.U5");
        fU5.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        fU5.addParameter(new Parameter("startDate","startDate",Date.class));
        fU5.addParameter(new Parameter("endDate","endDate",Date.class));
        fU5.getSearches().put("1", new Mapped<CohortDefinition>(base,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        fU5.getSearches().put("2", new Mapped<CohortDefinition>(u5,
                ParameterizableUtil.createParameterMappings("effectiveDate=${endDate}")));
        fU5.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        fU5.setCompositionString("1 and 2 and (not 3)");
        CohortIndicator indFU5 = Indicators.newCohortIndicator(tokenBase + ".ind.F.U5", fU5,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn(tokenBase + ".F.U5", labelBase + " (F <5y)",
                new Mapped<CohortIndicator>(indFU5,
                        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // M ≥5y  (base ∧ ≥5 ∧ M)
        CompositionCohortDefinition m5p = new CompositionCohortDefinition();
        m5p.setName(tokenBase + ".M.5P");
        m5p.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        m5p.addParameter(new Parameter("startDate","startDate",Date.class));
        m5p.addParameter(new Parameter("endDate","endDate",Date.class));
        m5p.getSearches().put("1", new Mapped<CohortDefinition>(base,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        m5p.getSearches().put("2", new Mapped<CohortDefinition>(age5plus,
                ParameterizableUtil.createParameterMappings("effectiveDate=${endDate}")));
        m5p.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        m5p.setCompositionString("1 and 2 and 3");
        CohortIndicator indM5p = Indicators.newCohortIndicator(tokenBase + ".ind.M.5P", m5p,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn(tokenBase + ".M.5P", labelBase + " (M ≥5y)",
                new Mapped<CohortIndicator>(indM5p,
                        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // F ≥5y  (base ∧ ≥5 ∧ ¬M)
        CompositionCohortDefinition f5p = new CompositionCohortDefinition();
        f5p.setName(tokenBase + ".F.5P");
        f5p.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        f5p.addParameter(new Parameter("startDate","startDate",Date.class));
        f5p.addParameter(new Parameter("endDate","endDate",Date.class));
        f5p.getSearches().put("1", new Mapped<CohortDefinition>(base,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        f5p.getSearches().put("2", new Mapped<CohortDefinition>(age5plus,
                ParameterizableUtil.createParameterMappings("effectiveDate=${endDate}")));
        f5p.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        f5p.setCompositionString("1 and 2 and (not 3)");
        CohortIndicator indF5p = Indicators.newCohortIndicator(tokenBase + ".ind.F.5P", f5p,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn(tokenBase + ".F.5P", labelBase + " (F ≥5y)",
                new Mapped<CohortIndicator>(indF5p,
                        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
    }


    /**
     * Adds 2 columns:
     *   tokenBase.U5, tokenBase.5P
     * built from: base ∩ ageBand (no gender split)
     */
    private void addByAgeBandsNoGender(
            CohortIndicatorDataSetDefinition dsd,
            String tokenBase, String labelBase,
            CohortDefinition base,
            AgeCohortDefinition u5, AgeCohortDefinition age5plus) {

        // U5
        CompositionCohortDefinition cU5 = new CompositionCohortDefinition();
        cU5.setName(tokenBase + ".U5");
        cU5.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        cU5.addParameter(new Parameter("startDate","startDate",Date.class));
        cU5.addParameter(new Parameter("endDate","endDate",Date.class));
        cU5.getSearches().put("1", new Mapped<CohortDefinition>(base,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        cU5.getSearches().put("2", new Mapped<CohortDefinition>(u5,
                ParameterizableUtil.createParameterMappings("effectiveDate=${endDate}")));
        cU5.setCompositionString("1 and 2");
        CohortIndicator indU5 = Indicators.newCohortIndicator(tokenBase + ".ind.U5", cU5,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn(tokenBase + ".U5", labelBase + " (<5y)",
                new Mapped<CohortIndicator>(indU5,
                        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // ≥5
        CompositionCohortDefinition c5p = new CompositionCohortDefinition();
        c5p.setName(tokenBase + ".5P");
        c5p.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        c5p.addParameter(new Parameter("startDate","startDate",Date.class));
        c5p.addParameter(new Parameter("endDate","endDate",Date.class));
        c5p.getSearches().put("1", new Mapped<CohortDefinition>(base,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        c5p.getSearches().put("2", new Mapped<CohortDefinition>(age5plus,
                ParameterizableUtil.createParameterMappings("effectiveDate=${endDate}")));
        c5p.setCompositionString("1 and 2");
        CohortIndicator ind5p = Indicators.newCohortIndicator(tokenBase + ".ind.5P", c5p,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn(tokenBase + ".5P", labelBase + " (≥5y)",
                new Mapped<CohortIndicator>(ind5p,
                        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
    }

    private void createCohortMonthlyIndicatorsII(CohortIndicatorDataSetDefinition dsd) {

        AgeCohortDefinition age5plus = patientWithAgeAbove(5);

        GenderCohortDefinition males = new GenderCohortDefinition();
        males.setName("male Patients");
        males.setMaleIncluded(true);

    // referred patients


    SqlCohortDefinition referredToHospitalDuringPeriod = new SqlCohortDefinition(
            "select distinct o.person_id " +
                    "from obs o " +
                    "where o.voided = 0 " +
                    "  and o.concept_id = " + referralDisposition.getConceptId() + " " +
                    "  and o.value_coded = " + referredToHospital.getConceptId() + " " +
                    "  and o.obs_datetime >= :startDate and o.obs_datetime <= :endDate"
    );
    referredToHospitalDuringPeriod.setName("referredToHospitalDuringPeriod");
    referredToHospitalDuringPeriod.addParameter(new Parameter("startDate", "startDate", Date.class));
    referredToHospitalDuringPeriod.addParameter(new Parameter("endDate", "endDate", Date.class));

    CohortIndicator referredToHospitalTotalIndicator = Indicators.newCohortIndicator(
            "referredToHospitalTotalIndicator",
            referredToHospitalDuringPeriod,
            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")
    );

// Token: #II.B.1#
    dsd.addColumn(
            "II.B.1",
            "Referred to Hospital (Total) / Référés à l'hôpital (Total)",
            new Mapped<CohortIndicator>(referredToHospitalTotalIndicator,
                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
            ""
    );

    // catchment area patients


// 0) New case during period (patients with caseStatus = newCase in window)
    SqlCohortDefinition newCaseDuringPeriod = new SqlCohortDefinition(
            "select distinct o.person_id " +
                    "from obs o " +
                    "where o.voided = 0 " +
                    "  and o.concept_id = " + caseStatus.getConceptId() + " " +
                    "  and o.value_coded = " + newCase.getConceptId() + " " +
                    "  and o.obs_datetime >= :startDate and o.obs_datetime <= :endDate"
    );
    newCaseDuringPeriod.setName("newCaseDuringPeriod");
    newCaseDuringPeriod.addParameter(new Parameter("startDate", "startDate", Date.class));
    newCaseDuringPeriod.addParameter(new Parameter("endDate", "endDate", Date.class));

// 1) From catchment area (In Zone) during period
    SqlCohortDefinition inZoneDuringPeriod = new SqlCohortDefinition(
            "select distinct o.person_id " +
                    "from obs o " +
                    "where o.voided = 0 " +
                    "  and o.concept_id = " + catchmentArea.getConceptId() + " " +     // residence status question
                    "  and o.value_coded = " + catchmentAreaInZone.getConceptId() + " " +    // In Zone
                    "  and o.obs_datetime >= :startDate and o.obs_datetime <= :endDate"
    );
    inZoneDuringPeriod.setName("inZoneDuringPeriod");
    inZoneDuringPeriod.addParameter(new Parameter("startDate", "startDate", Date.class));
    inZoneDuringPeriod.addParameter(new Parameter("endDate", "endDate", Date.class));

// Composition: In Zone ∩ New Case
    CompositionCohortDefinition inZoneNewCase = new CompositionCohortDefinition();
    inZoneNewCase.setName("inZoneNewCase");
    inZoneNewCase.addParameter(new Parameter("startDate", "startDate", Date.class));
    inZoneNewCase.addParameter(new Parameter("endDate", "endDate", Date.class));
    inZoneNewCase.getSearches().put("A",
            new Mapped<CohortDefinition>(inZoneDuringPeriod,
                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
    inZoneNewCase.getSearches().put("B",
            new Mapped<CohortDefinition>(newCaseDuringPeriod,
                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
    inZoneNewCase.setCompositionString("A and B");

    CohortIndicator inZoneNewCaseIndicator = Indicators.newCohortIndicator(
            "inZoneNewCaseIndicator",
            inZoneNewCase,
            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")
    );

// #II.C.1#
    dsd.addColumn(
            "II.C.1",
            "From catchment area (Total) / De la zone de desserte (Total)",
            new Mapped<CohortIndicator>(inZoneNewCaseIndicator,
                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
            ""
    );

// 2) Out of catchment area (Out of Zone) during period
    SqlCohortDefinition outZoneDuringPeriod = new SqlCohortDefinition(
            "select distinct o.person_id " +
                    "from obs o " +
                    "where o.voided = 0 " +
                    "  and o.concept_id = " + catchmentArea.getConceptId() + " " +     // residence status question
                    "  and o.value_coded = " + catchmentAreaOutZone.getConceptId() + " " +    // out of Zone
                    "  and o.obs_datetime >= :startDate and o.obs_datetime <= :endDate"
    );
    outZoneDuringPeriod.setName("outZoneDuringPeriod");
    outZoneDuringPeriod.addParameter(new Parameter("startDate", "startDate", Date.class));
    outZoneDuringPeriod.addParameter(new Parameter("endDate", "endDate", Date.class));

// Composition: Out of Zone ∩ New Case
    CompositionCohortDefinition outZoneNewCase = new CompositionCohortDefinition();
    outZoneNewCase.setName("outZoneNewCase");
    outZoneNewCase.addParameter(new Parameter("startDate", "startDate", Date.class));
    outZoneNewCase.addParameter(new Parameter("endDate", "endDate", Date.class));
    outZoneNewCase.getSearches().put("A",
            new Mapped<CohortDefinition>(outZoneDuringPeriod,
                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
    outZoneNewCase.getSearches().put("B",
            new Mapped<CohortDefinition>(newCaseDuringPeriod,
                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
    outZoneNewCase.setCompositionString("A and B");

    CohortIndicator outZoneNewCaseIndicator = Indicators.newCohortIndicator(
            "outZoneNewCaseIndicator",
            outZoneNewCase,
            ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")
    );

// #II.C.2#
    dsd.addColumn(
            "II.C.2",
            "Out of catchment area (Total) / Hors zone de desserte (Total)",
            new Mapped<CohortIndicator>(outZoneNewCaseIndicator,
                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
            ""
    );


    // diagnosis for patients

        addDiag5PlusByNamePattern(dsd, "II.E.1",  "Diarrhea diseases/Maladies diarrhéiques", "diarrh",       caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.2",  "Intestinal parasites/Parasitose intestinale", "parasite|helminth|ascar|amoeb|giardia|taeni|ankylostom", caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.3",  "Snake bite /Morsure de serpent", "snake bite|morsure.*serpent|envenomation", caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.4",  "Pneumonia /Pneumonie", "pneumon", caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.5",  "Bronchitis", "bronchit", caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.6",  "Acute Respiratory diseases other/Maladies aigues des voies respiratoires autres", "acute respiratory|infection respiratoire aigue|ira", caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.7",  "Acute Malnutrition/Malnutrition Aigue", "malnutrition|kwashiorkor|marasmu", caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.8",  "HIV/AIDS related opportunistic Infection/Infection opportuniste du VIH", "opportunistic|candid|pcp|toxoplasmo|cryptococc|herpes zoster|tb.*hiv", caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.9",  "Gastritis and duodenitis/ Gastrite et Duodénite", "gastrit|duodenit", caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.10", "Scabies (Crusted or classic scabies)", "scabie|gale", caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.11", "Tungiasis/ Jigger Disease (amavunja)", "tungiasis|jigger|amavunj", caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.12", "Skin Diseases/Maladies de la peau", "dermat|eczema|psoriasis|impetig|cellulit|furunc|pyoderma|urticair", caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.13", "Abscesses / Abcès", "absces|abces", caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.14", "Anemia/Anémie (confirmée)", "anemi", caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.15", "Viral hepatitis B, C Chronic/ Hépatite B,C Chronique", "hepatit(e|e).*b|hepatit(e|e).*c", caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.16", "Diseases of Urinary tract system/ Maladies du système urinaire", "uti|urinary tract|cystit|pyelone|uretr|prostat|nephrit", caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.17", "Bone and Joint Diseases, other than fractures /Maladies des os et des articulations, autres que les traumatismes", "arthrit|arthros|osteoarth|rheumat|sciatica|tendinit|bursit|spondyl", caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.18", "Bone and Joint Fractures/ Fracture osseuse et articulaire", "fractur|fracture", caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.19", "Physical traumas, other than fractures/ Traumatismes Physiques, Autres que les Fractures", "trauma(?!.*fractur)|contusion|plaie|lacerat|entorse|sprain|brulure|burn", caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.20", "Sepsis/Septicémie", "sepsis|septicem", caseStatus, newCase, age5plus, males);
        addDiag5PlusByNamePattern(dsd, "II.E.21", "Goitre/Goitre", "goitr", caseStatus, newCase, age5plus, males);

        // Female-only per the sheet
        addDiag5PlusFemaleOnlyByNamePattern(dsd, "II.E.22",
                "Gynecological problems/Problèmes gynécologiques",
                "gynec|gynaec|uterin|cervic|vagin|ovari|pelvic inflam|fibrom|kyst",
                caseStatus, newCase, age5plus, males);


        // Genders
        males.setName("male Patients");
        males.setMaleIncluded(true);

// Age bands
        AgeCohortDefinition age0to19  = patientWithAgeBetween(0, 19);
        AgeCohortDefinition age20to39 = patientWithAgeBetween(20, 39);

        AgeCohortDefinition age40plus = patientWithAgeAbove(40); // you already have this helper

        // Eye diseases
        addDiagByBands(dsd, "II.F.1",  "Refractive errors",            "refract",      caseStatus, newCase, age0to19, age20to39, age40plus, males);
        addDiagByBands(dsd, "II.F.2",  "Allergic conjunctivitis",      "allerg conjunct", caseStatus, newCase, age0to19, age20to39, age40plus, males);
        addDiagByBands(dsd, "II.F.3",  "Infectious conjunctivitis",    "infect conjunct", caseStatus, newCase, age0to19, age20to39, age40plus, males);
        addDiagByBands(dsd, "II.F.4",  "Eye problem other",            "eye",          caseStatus, newCase, age0to19, age20to39, age40plus, males);  // broad; refine if needed

// Ear diseases
        addDiagByBands(dsd, "II.F.5",  "Otitis externa (unspecified)", "otitis externa", caseStatus, newCase, age0to19, age20to39, age40plus, males);
        addDiagByBands(dsd, "II.F.6",  "External ear disorder (unspec)","external ear", caseStatus, newCase, age0to19, age20to39, age40plus, males);
        addDiagByBands(dsd, "II.F.7",  "Suppurative otitis media",     "otitis media", caseStatus, newCase, age0to19, age20to39, age40plus, males);
        addDiagByBands(dsd, "II.F.8",  "Hearing impairment/deafness",  "hearing|deaf", caseStatus, newCase, age0to19, age20to39, age40plus, males); // use REGEXP if you enabled it
        addDiagByBands(dsd, "II.F.9",  "Other ear diseases",           "ear disease",  caseStatus, newCase, age0to19, age20to39, age40plus, males);

// Oral & dental
        addDiagByBands(dsd, "II.F.10", "Dental caries",                "caries",       caseStatus, newCase, age0to19, age20to39, age40plus, males);
        addDiagByBands(dsd, "II.F.11", "Periodontal disease",          "periodont",    caseStatus, newCase, age0to19, age20to39, age40plus, males);
        addDiagByBands(dsd, "II.F.12", "Other oral/teeth diseases",    "oral|buccal",  caseStatus, newCase, age0to19, age20to39, age40plus, males); // REGEXP recommended




    }





	private void setUpProperties() {

		onOrAfterOnOrBefore.add("onOrAfter");
		
		onOrAfterOnOrBefore.add("onOrBefore");


		OPDForm=Context.getFormService().getFormByUuid("a4d59540-9c55-4cda-8e0d-0337a743540e");

		caseStatus=Context.getConceptService().getConceptByUuid("14183f94-59b2-4b62-bad7-2c788a21a422");

		newCase=Context.getConceptService().getConceptByUuid("f7b5bf49-cb07-4fca-8c15-93ba92249344");

		oldCase=Context.getConceptService().getConceptByUuid("ae5ba489-9be2-4960-8e44-8d09071ab8ca");
        primaryDiagnosis = Context.getConceptService().getConceptByUuid("2dce81f9-3874-4247-b441-6369ca0725c2");
        secondaryDiagnosis = Context.getConceptService().getConceptByUuid("afb8006f-e7c4-45bd-82bd-16f6e4b4b51d");
        diagnosisConfirmed.add(primaryDiagnosis);
        diagnosisConfirmed.add(secondaryDiagnosis);
        referralDisposition = Context.getConceptService().getConceptByUuid("f965dd1d-c4e1-481c-af19-3581c80fb81f");
        referredToHospital = Context.getConceptService().getConceptByUuid("3cdd5c02-26fe-102b-80cb-0017a47871b2");
        catchmentArea = Context.getConceptService().getConceptByUuid("f8c17ed2-38e4-4010-bd10-e27c99185eb4");
        catchmentAreaInZone = Context.getConceptService().getConceptByUuid("1aa519c4-ce19-4c2f-baa4-fb9808164f47");
        catchmentAreaOutZone = Context.getConceptService().getConceptByUuid("94efe7f2-9b74-4c38-b017-f77b9874d88b");


        noneInsuranceID =Integer.parseInt(Context.getAdministrationService().getGlobalProperty("reports.NoneInsuranceID"));

		hundrepercentInsuredInsuranceIDs=Context.getAdministrationService().getGlobalProperty("reports.hundrepercentInsuredInsuranceIDs");

		indigentsInsuranceIDs=Context.getAdministrationService().getGlobalProperty("reports.indigentsInsuranceIDs");

		ICDConceptClassId=Integer.parseInt(Context.getAdministrationService().getGlobalProperty("reports.ICDConceptClassId"));

	}

	private AgeCohortDefinition patientWithAgeBelow(int age) {
		AgeCohortDefinition patientsWithAgebilow = new AgeCohortDefinition();
		patientsWithAgebilow.setName("patientsWithAgebilow");
		patientsWithAgebilow.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientsWithAgebilow.setMaxAge(age - 1);
		patientsWithAgebilow.setMaxAgeUnit(DurationUnit.YEARS);
		return patientsWithAgebilow;
	}

	private AgeCohortDefinition patientWithAgeBelowAndIncuded(int age) {
		AgeCohortDefinition patientsWithAgebilow = new AgeCohortDefinition();
		patientsWithAgebilow.setName("patientsWithAgebilow");
		patientsWithAgebilow.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientsWithAgebilow.setMaxAge(age);
		patientsWithAgebilow.setMaxAgeUnit(DurationUnit.YEARS);
		return patientsWithAgebilow;
	}

    private AgeCohortDefinition patientWithAgeBetween(int minInclusive, int maxInclusive) {
        AgeCohortDefinition d = new AgeCohortDefinition();
        d.setName("age" + minInclusive + "to" + maxInclusive);
        d.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        d.setMinAge(minInclusive);
        d.setMinAgeUnit(DurationUnit.YEARS);
        d.setMaxAge(maxInclusive);
        d.setMaxAgeUnit(DurationUnit.YEARS);
        return d;
    }


    private AgeCohortDefinition patientWithAgeAbove(int age) {
		AgeCohortDefinition patientsWithAge = new AgeCohortDefinition();
		patientsWithAge.setName("patientsWithAge");
		patientsWithAge.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientsWithAge.setMinAge(age);
		patientsWithAge.setMinAgeUnit(DurationUnit.YEARS);
		return patientsWithAge;
	}


/*	private SqlCohortDefinition patientWithICDCodeObsByStartDateAndEndDate(String ICDCode){

		SqlCohortDefinition patientWithIDCObs=new SqlCohortDefinition("select o.person_id from obs o where o.value_coded in (select distinct concept_id from concept_name where name like '%"+ICDCode+"%') and o.value_coded in (select distinct concept_id from concept where class_id="+ICDConceptClassId+") and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");
		patientWithIDCObs.setName("patientWithIDCObs");
		patientWithIDCObs.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientWithIDCObs.addParameter(new Parameter("endDate", "endDate", Date.class));

		return patientWithIDCObs;

	}*/

    private SqlCohortDefinition patientWithICDCodeObsByStartDateAndEndDate(
            String diagnosiName, Concept caseStatusQuestion, Concept caseAnswer) {

        String raw = (diagnosiName == null ? "" : diagnosiName.trim());
        String escaped = raw.replace("'", "''"); // basic escape

        // Build name filter: REGEXP if pattern contains '|', else LIKE
        String nameFilter;
        if (escaped.contains("|")) {
            // MySQL 5.7/8.0 compatible case-insensitive REGEXP
            nameFilter = "lower(cn.name) regexp '" + escaped.toLowerCase() + "'";
        } else {
            if (escaped.isEmpty()) escaped = "_"; // avoid LIKE '%%'
            nameFilter = "lower(cn.name) like lower('%" + escaped + "%')";
        }

        String sql =
                "select distinct o.person_id \n" +
                        "from obs o \n" +
                        "inner join obs o2 on o.encounter_id = o2.encounter_id \n" +
                        "inner join concept_name cn on cn.concept_id = o.value_coded and cn.voided = 0 \n" +
                        "where o.voided = 0 \n" +
                        "  and o2.voided = 0 \n" +
                        "  and o.obs_datetime >= :startDate and o.obs_datetime <= :endDate \n" +
                        "  and o2.concept_id = " + caseStatusQuestion.getConceptId() + " \n" +
                        "  and o2.value_coded = " + caseAnswer.getConceptId() + " \n" +
                        "  and o.concept_id in (select concept_id from concept " +
                        "                        where uuid in ('2dce81f9-3874-4247-b441-6369ca0725c2','afb8006f-e7c4-45bd-82bd-16f6e4b4b51d')) \n" +
                        "  and ( (cn.locale_preferred = 1) or (cn.concept_name_type = 'FULLY_SPECIFIED') ) \n" +
                        "  and " + nameFilter + " \n";

        SqlCohortDefinition def = new SqlCohortDefinition(sql);
        def.setName("patientWithIDCObs");
        def.addParameter(new Parameter("startDate", "startDate", Date.class));
        def.addParameter(new Parameter("endDate", "endDate", Date.class));
        return def;
    }


    private SqlCohortDefinition patientWithICDCodesObsByStartDateAndEndDate(String ICDCodes,Concept caseStatusQuestion, Concept caseAnswer){

		String icdTencodes[] =ICDCodes.split(",");

		StringBuilder q=new StringBuilder();
		q.append("select o.person_id from obs o " +
				"inner join obs o2 on o.encounter_id=o2.encounter_id" +
				" where o.value_coded in (select distinct concept_id from concept_name where ");
		int i=0;
		for (String c:icdTencodes){
			if(i==0){
				q.append("name like '%"+c+"%'");
				i++;
			}else {
				q.append(" or name like '%"+c+"%'");
				i++;
			}
		}
		q.append(") and o.value_coded in (select distinct concept_id from concept where class_id="+ICDConceptClassId+") and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate and o2.concept_id="+caseStatusQuestion.getConceptId()+" and o2.value_coded="+caseAnswer.getConceptId()+"");

		SqlCohortDefinition patientWithIDCObs=new SqlCohortDefinition(q.toString());
		patientWithIDCObs.setName("patientWithIDCObs");
		patientWithIDCObs.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientWithIDCObs.addParameter(new Parameter("endDate", "endDate", Date.class));

		return patientWithIDCObs;

	}

	private SqlCohortDefinition diedPatientWithICDCodeObsByStartDateAndEndDate(String ICDCode){

		SqlCohortDefinition patientWithIDCObs=new SqlCohortDefinition("select o.person_id from obs o " +
				"inner join person p on o.person_id=p.person_id" +
				" where o.value_coded in (select distinct concept_id from concept_name where name like '%"+ICDCode+"%') and o.value_coded in (select distinct concept_id from concept where class_id="+ICDConceptClassId+") and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate and p.dead=1 and p.voided=0");
		patientWithIDCObs.setName("patientWithIDCObs");
		patientWithIDCObs.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientWithIDCObs.addParameter(new Parameter("endDate", "endDate", Date.class));

		return patientWithIDCObs;

	}

	private SqlCohortDefinition  diedPatientWithICDCodesObsByStartDateAndEndDate(String ICDCodes){

		String icdTencodes[] =ICDCodes.split(",");

		StringBuilder q=new StringBuilder();
		q.append("select o.person_id from obs o " +
				"inner join person p on o.person_id=p.person_id" +
				" where o.value_coded in (select distinct concept_id from concept_name where ");
		int i=0;
		for (String c:icdTencodes){
			if(i==0){
				q.append("name like '%"+c+"%'");
				i++;
			}else {
				q.append(" or name like '%"+c+"%'");
				i++;
			}
		}
		q.append(") and o.value_coded in (select distinct concept_id from concept where class_id="+ICDConceptClassId+") and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate  and p.dead=1 and p.voided=0");

		SqlCohortDefinition patientWithIDCObs=new SqlCohortDefinition(q.toString());
		patientWithIDCObs.setName("patientWithIDCObs");
		patientWithIDCObs.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientWithIDCObs.addParameter(new Parameter("endDate", "endDate", Date.class));

		return patientWithIDCObs;

	}

}
