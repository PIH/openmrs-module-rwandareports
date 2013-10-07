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
package org.openmrs.module.rwandareports.dataset.evaluator;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.ExtendedDrugOrder;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.rwandareports.dataset.DrugOrderDataSetDefinition;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

/**
 * The logic that evaluates a {@link EncounterDataSetDefinition} and produces an {@link DataSet}
 */
@Handler(supports = DrugOrderDataSetDefinition.class)
public class DrugOrderDataSetEvaluator implements DataSetEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	/**
	 * Public constructor
	 */
	public DrugOrderDataSetEvaluator() {
	}
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	@SuppressWarnings("unchecked")
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		
		Concept bsa = gp.getConcept(GlobalPropertiesManagement.BSA_CONCEPT);
		Concept weight = gp.getConcept(GlobalPropertiesManagement.WEIGHT_CONCEPT);
		
		Cohort cohort = context.getBaseCohort();
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		
		DataSetColumn patientName = new DataSetColumn("patientName", "patientName", String.class);
		dataSet.getMetaData().addColumn(patientName);
		
		DataSetColumn patientSex = new DataSetColumn("patientSex", "patientSex", String.class);
		dataSet.getMetaData().addColumn(patientSex);
		
		DataSetColumn patientAge = new DataSetColumn("patientAge", "patientAge", String.class);
		dataSet.getMetaData().addColumn(patientAge);
		
		DataSetColumn drug1 = new DataSetColumn("drug1", "drug1", String.class);
		dataSet.getMetaData().addColumn(drug1);
		
		DataSetColumn drug2 = new DataSetColumn("drug2", "drug2", String.class);
		dataSet.getMetaData().addColumn(drug2);
		
		DataSetColumn drug3 = new DataSetColumn("drug3", "drug3", String.class);
		dataSet.getMetaData().addColumn(drug3);
		
		DataSetColumn drug4 = new DataSetColumn("drug4", "drug4", String.class);
		dataSet.getMetaData().addColumn(drug4);
		
		DataSetColumn drug5 = new DataSetColumn("drug5", "drug5", String.class);
		dataSet.getMetaData().addColumn(drug5);
		
		DataSetColumn drug6 = new DataSetColumn("drug6", "drug6", String.class);
		dataSet.getMetaData().addColumn(drug6);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		DrugOrderDataSetDefinition drugDSD = (DrugOrderDataSetDefinition) dataSetDefinition;
		
		if (cohort != null) {
			for (Integer pId : cohort.getMemberIds()) {
				Patient patient = Context.getPatientService().getPatient(pId);
				List<DrugOrder> allDrugOrders = Context.getOrderService().getDrugOrdersByPatient(patient);
				
				Collections.sort(allDrugOrders, new Comparator<DrugOrder>(){

					@Override
                    public int compare(DrugOrder o1, DrugOrder o2) {
	                    return o1.getDrug().getName().compareTo(o2.getDrug().getName());
                    }
					
				});
				
				StringBuilder patientN = new StringBuilder();
				patientN.append(patient.getGivenName());
				patientN.append(" ");
				if (patient.getMiddleName() != null && patient.getMiddleName().trim().length() > 0) {
					patientN.append(patient.getMiddleName());
					patientN.append(" ");
				}
				patientN.append(patient.getFamilyName());
				dataSet.addColumnValue(pId, patientName, patientN.toString());
				dataSet.addColumnValue(pId, patientSex, patient.getGender());
				dataSet.addColumnValue(pId, patientAge, patient.getAge(drugDSD.getAsOfDate()));
				
				DecimalFormat f = new DecimalFormat("0.#");
				
				int index = 1;
				
				String drugString6 = null;
				
				for (DrugOrder drO : allDrugOrders) {
					if (drO instanceof ExtendedDrugOrder) {
						ExtendedDrugOrder eDrO = (ExtendedDrugOrder) drO;
						if (drugDSD.getIndication().contains(eDrO.getIndication())) {
							if (drO.getDrug() != null
							        && drO.getDrug().getConcept() != null
							        && (drugDSD.getDrugExclusions() == null || !drugDSD.getDrugExclusions().contains(
							            drO.getDrug().getConcept()))) {
								
								if (eDrO.getStartDate() != null && (sdf.format(eDrO.getStartDate())).equals(sdf.format(drugDSD.getAsOfDate()))) {
									String dosage = "";
									if (eDrO.getDose() != null && eDrO.getUnits() != null) {
										
										if (eDrO.getUnits().contains("/m2")) {
											
											List<Obs> bsaValues = Context.getObsService().getObservationsByPersonAndConcept(
											    patient, bsa);
											
											if (bsaValues != null && bsaValues.size() > 0) {
												Obs recent = null;
												for (Obs o : bsaValues) {
													if (recent == null || recent.getObsDatetime().before(o.getObsDatetime())) {
														recent = o;
													}
												}
												
												double calcDose = eDrO.getDose() * recent.getValueNumeric();
												if (eDrO.getDrug() != null && eDrO.getDrug().getMaximumDailyDose() != null
												        && calcDose > eDrO.getDrug().getMaximumDailyDose()) {
													calcDose = eDrO.getDrug().getMaximumDailyDose();
												}
												dosage = f.format(calcDose)
												        + eDrO.getDrug().getUnits().substring(0, eDrO.getDrug().getUnits().indexOf("/"));
											}
										} else if (eDrO.getDrug().getUnits().contains("/kg")) {
											
											List<Obs> weightValues = Context.getObsService()
											        .getObservationsByPersonAndConcept(patient, weight);
											
											if (weightValues != null && weightValues.size() > 0) {
												Obs recent = null;
												for (Obs o : weightValues) {
													if (recent == null || recent.getObsDatetime().before(o.getObsDatetime())) {
														recent = o;
													}
												}
												
												double calcDose = eDrO.getDose() * recent.getValueNumeric();
												if (eDrO.getDrug() != null && eDrO.getDrug().getMaximumDailyDose() != null
												        && calcDose > eDrO.getDrug().getMaximumDailyDose()) {
													calcDose = eDrO.getDrug().getMaximumDailyDose();
												}
												dosage = f.format(calcDose)
												        + eDrO.getUnits().substring(0, eDrO.getUnits().indexOf("/"));
											}
										} else if (eDrO.getUnits().contains("AUC")) {
											dosage = eDrO.getUnits() + "=" + eDrO.getDose();
										} else {
											dosage = eDrO.getDose() + eDrO.getUnits();
										}
										
										String drugString = eDrO.getDrug().getName() + " " + dosage;
										
										if(eDrO.getDrug().getRoute() != null)
										{
											drugString = drugString + " " + eDrO.getDrug().getRoute().getDisplayString();
										}
										        
										if (index == 1) {
											dataSet.addColumnValue(pId, drug1, "R1 " + drugString);
										} else if (index == 2) {
											dataSet.addColumnValue(pId, drug2, "R2 " + drugString);
										} else if (index == 3) {
											dataSet.addColumnValue(pId, drug3, "R3 " + drugString);
										} else if (index == 4) {
											dataSet.addColumnValue(pId, drug4, drugString);
										} else if (index == 5) {
											dataSet.addColumnValue(pId, drug5, drugString);
										} else if (index == 6) {
											drugString6 = drugString;
										} else if (index > 6) {
											drugString6 = drugString6 + ", " + drugString;
										}
										index++;
									}
								}
							}
						}
					}
					if (index == 2) {
						dataSet.addColumnValue(pId, drug2, "");
						dataSet.addColumnValue(pId, drug3, "");
						dataSet.addColumnValue(pId, drug4, "");
						dataSet.addColumnValue(pId, drug5, "");
						dataSet.addColumnValue(pId, drug6, "");
					} else if (index == 3) {
						dataSet.addColumnValue(pId, drug3, "");
						dataSet.addColumnValue(pId, drug4, "");
						dataSet.addColumnValue(pId, drug5, "");
						dataSet.addColumnValue(pId, drug6, "");
					} else if (index == 4) {
						dataSet.addColumnValue(pId, drug4, "");
						dataSet.addColumnValue(pId, drug5, "");
						dataSet.addColumnValue(pId, drug6, "");
					} else if (index == 5) {
						dataSet.addColumnValue(pId, drug5, "");
						dataSet.addColumnValue(pId, drug6, "");
					} else if (index == 6) {
						dataSet.addColumnValue(pId, drug6, "");
					} else if (index > 6) {
						dataSet.addColumnValue(pId, drug6, drugString6);
					}
				}
			}
		}
		return dataSet;
	}
}
