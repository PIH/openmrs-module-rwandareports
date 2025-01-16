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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Obs;
import org.openmrs.OrderFrequency;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.DrugOrderComparator;
import org.openmrs.module.orderextension.DrugRegimen;
import org.openmrs.module.orderextension.api.OrderExtensionService;
import org.openmrs.module.orderextension.util.OrderEntryUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.rwandareports.dataset.ExtendedDrugOrderDataSetDefinition;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.util.OpenmrsUtil;

/**
 * The logic that evaluates a {@link EncounterDataSetDefinition} and produces an {@link DataSet}
 */
@Handler(supports = ExtendedDrugOrderDataSetDefinition.class)
public class ExtendedDrugOrderDataSetEvaluator implements DataSetEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	/**
	 * Public constructor
	 */
	public ExtendedDrugOrderDataSetEvaluator() {
	}
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	@SuppressWarnings("unchecked")
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		
		ExtendedDrugOrderDataSetDefinition dsd = (ExtendedDrugOrderDataSetDefinition) dataSetDefinition;
		context = ObjectUtil.nvl(context, new EvaluationContext());
		
		SimpleDataSet dataSet = new SimpleDataSet(dsd, context);
		
		List<DrugOrder> orders = new ArrayList<DrugOrder>();
		
		if (dsd.getDrugRegimen() != null) {
			
			String[] regimenInfo = dsd.getDrugRegimen().split(":");
			Integer regimenId = Integer.parseInt(regimenInfo[0]);
			
			DrugRegimen regimen = Context.getService(OrderExtensionService.class).getDrugRegimen(regimenId);
			
			Date activeDate = null;
			if (regimenInfo.length > 1) {
				Integer regOffset = Integer.parseInt(regimenInfo[1]);
				Calendar offset = Calendar.getInstance();
				offset.setTime(regimen.getFirstDrugOrderStartDate());
				offset.add(Calendar.DAY_OF_YEAR, regOffset);
				activeDate = offset.getTime();
			}
			
			if (dsd.getIndication() != null) {
				for (DrugOrder order : regimen.getMembers()) {
					if (!order.isVoided() && order.getOrderReason() != null
					        && order.getOrderReason().equals(dsd.getIndication())) {
						if (activeDate != null) {
							if (OrderEntryUtil.isCurrent(order, activeDate)
							        || OpenmrsUtil.compare(order.getEffectiveStartDate(), activeDate) == 0
							        || (order.getAutoExpireDate() != null && OpenmrsUtil.compare(order.getAutoExpireDate(),
							            activeDate) == 0)
							        || (order.getEffectiveStopDate() != null && OpenmrsUtil.compare(
							            order.getEffectiveStopDate(), activeDate) == 0)) {
								orders.add(order);
							}
						} else {
							orders.add(order);
						}
					}
				}
			} else {
				for (DrugOrder order : regimen.getMembers()) {
					if (!order.isVoided()) {
						orders.add(order);
					}
				}
			}
			
			DataSetColumn start = new DataSetColumn("startDate", "startDate", Date.class);
			dataSet.getMetaData().addColumn(start);
			
			DataSetColumn drug = new DataSetColumn("drug", "drug", String.class);
			dataSet.getMetaData().addColumn(drug);
			
			DataSetColumn doseReduction = new DataSetColumn("doseReduction", "doseReduction", String.class);
			dataSet.getMetaData().addColumn(doseReduction);
			
			DataSetColumn dose = new DataSetColumn("dose", "dose", String.class);
			dataSet.getMetaData().addColumn(dose);
			
			DataSetColumn actualDose = new DataSetColumn("actualDose", "actualDose", String.class);
			dataSet.getMetaData().addColumn(actualDose);
			
			DataSetColumn route = new DataSetColumn("route", "route", String.class);
			dataSet.getMetaData().addColumn(route);
			
			DataSetColumn infInst = new DataSetColumn("infusionInstructions", "infusionInstructions", String.class);
			dataSet.getMetaData().addColumn(infInst);
			
			DataSetColumn freq = new DataSetColumn("frequency", "frequency", String.class);
			dataSet.getMetaData().addColumn(freq);
			
			DataSetColumn instructions = new DataSetColumn("instructions", "instructions", String.class);
			dataSet.getMetaData().addColumn(instructions);
			
			DataSetColumn indication = new DataSetColumn("indication", "indication", String.class);
			dataSet.getMetaData().addColumn(indication);
			
			DataSetColumn discontuedReason = new DataSetColumn("discontuedReason", "discontuedReason", String.class);
			dataSet.getMetaData().addColumn(discontuedReason);
			
			Collections.sort(orders, new DrugOrderComparator());
			
			for (DrugOrder edo : orders) {
				
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
				dataSet.addColumnValue(edo.getId(), start, dateFormat.format(edo.getEffectiveStartDate()));
				
				dataSet.addColumnValue(edo.getId(), drug, getDrugDisplay(edo));
				dataSet.addColumnValue(edo.getId(), doseReduction, getDoseReductionDisplay(edo));
				dataSet.addColumnValue(edo.getId(), dose, getDoseDisplay(edo));
				dataSet.addColumnValue(edo.getId(), actualDose, getActualDoseDisplay(edo));
				
				String routeDisplay = "";
				if (edo.getRoute() != null) {
					routeDisplay = edo.getRoute().getShortestName(Context.getLocale(), false).getName();
				}
				dataSet.addColumnValue(edo.getId(), route, routeDisplay);
				
				String infInstDisplay = "";
				if (edo.getDosingInstructions() != null) {
					infInstDisplay = edo.getDosingInstructions();
				}
				dataSet.addColumnValue(edo.getId(), infInst, infInstDisplay);
				
				String freqDisplay = "";
				if (edo.getFrequency() != null) {
					OrderFrequency frequency = edo.getFrequency();
					if (frequency != null) {
						freqDisplay = frequency.getConcept().getDisplayString();
					}
					
					int length = 0;
					if (edo.getEffectiveStopDate() != null) {
						length = calculateDaysDifference(edo.getEffectiveStopDate(), edo.getEffectiveStartDate());
					}
					
					if (length > 1) {
						freqDisplay = edo.getFrequency() + " for " + Integer.toString(length) + " days";
					}
				}
				dataSet.addColumnValue(edo.getId(), freq, freqDisplay);
				
				String instructionsDisplay = "";
				if (edo.getInstructions() != null) {
					instructionsDisplay = edo.getInstructions();
				}
				dataSet.addColumnValue(edo.getId(), instructions, instructionsDisplay);
				
				String discontiedReasonDisplay = OrderEntryUtil.getDiscontinueReason(edo);
				dataSet.addColumnValue(edo.getId(), discontuedReason, discontiedReasonDisplay);
			}
			if (orders.size() == 0) {
				dataSet.addColumnValue(-1, start, "");
				dataSet.addColumnValue(-1, drug, "");
				dataSet.addColumnValue(-1, doseReduction, "");
				dataSet.addColumnValue(-1, dose, "");
				dataSet.addColumnValue(-1, actualDose, "");
				dataSet.addColumnValue(-1, route, "");
				dataSet.addColumnValue(-1, infInst, "");
				dataSet.addColumnValue(-1, freq, "");
				dataSet.addColumnValue(-1, instructions, "");
				dataSet.addColumnValue(-1, discontuedReason, "");
			}
		}
		return dataSet;
	}
	
	private int calculateDaysDifference(Date observation, Date startingDate) {
		long milis1 = observation.getTime();
		long milis2 = startingDate.getTime();
		
		long diff = milis1 - milis2;
		
		long diffDays = diff / (24 * 60 * 60 * 1000);
		
		return (int) diffDays + 1;
	}
	
	private String getDoseDisplay(DrugOrder edo) {
		String doseDisplay = "";
		String doseUnits = (edo.getDoseUnits() == null ? null : edo.getDoseUnits().getDisplayString());
		DecimalFormat f = new DecimalFormat("0.###");
		if (edo.getDose() != null && doseUnits != null) {
			if (doseUnits.equalsIgnoreCase("mg/m2")) {
				doseDisplay = f.format(edo.getDose());
			} else if (doseUnits.toLowerCase().contains("/m2") || doseUnits.toLowerCase().contains("/kg")) {
				doseDisplay = f.format(edo.getDose()) + doseUnits;
			} else if (doseUnits.toUpperCase().contains("AUC")) {
				doseDisplay = doseUnits + "=" + f.format(edo.getDose());
			}
		}
		return doseDisplay;
	}
	
	private String getActualDoseDisplay(DrugOrder edo) {
		String actualDoseDisplay = "";
		Concept bsa = gp.getConcept(GlobalPropertiesManagement.BSA_CONCEPT);
		Concept weight = gp.getConcept(GlobalPropertiesManagement.WEIGHT_CONCEPT);
		DecimalFormat f2 = new DecimalFormat("0.#");
		String doseUnits = (edo.getDoseUnits() == null ? null : edo.getDoseUnits().getDisplayString());
		if (edo.getDose() != null && doseUnits != null) {
			if (doseUnits.contains("/m2")) {
				
				Patient patient = edo.getPatient();
				List<Obs> bsaValues = Context.getObsService().getObservationsByPersonAndConcept(patient, bsa);
				
				if (bsaValues != null && bsaValues.size() > 0) {
					Obs recent = null;
					for (Obs o : bsaValues) {
						if (recent == null || recent.getObsDatetime().before(o.getObsDatetime())) {
							recent = o;
						}
					}
					
					double calcDose = edo.getDose() * recent.getValueNumeric();
					if (edo.getDrug() != null && edo.getDrug().getMaximumDailyDose() != null
					        && calcDose > edo.getDrug().getMaximumDailyDose()) {
						calcDose = edo.getDrug().getMaximumDailyDose();
					}
					actualDoseDisplay = f2.format(calcDose);
				}
			} else if (doseUnits.contains("/kg")) {
				
				Patient patient = edo.getPatient();
				List<Obs> weightValues = Context.getObsService().getObservationsByPersonAndConcept(patient, weight);
				
				if (weightValues != null && weightValues.size() > 0) {
					Obs recent = null;
					for (Obs o : weightValues) {
						if (recent == null || recent.getObsDatetime().before(o.getObsDatetime())) {
							recent = o;
						}
					}
					
					double calcDose = edo.getDose() * recent.getValueNumeric();
					if (edo.getDrug() != null && edo.getDrug().getMaximumDailyDose() != null
					        && calcDose > edo.getDrug().getMaximumDailyDose()) {
						calcDose = edo.getDrug().getMaximumDailyDose();
					}
					actualDoseDisplay = f2.format(calcDose);
				}
				
			} else if (doseUnits.contains("AUC")) {
				actualDoseDisplay = "";
			} else {
				actualDoseDisplay = f2.format(edo.getDose()) + " (" + doseUnits + ")";
			}
		}
		return actualDoseDisplay;
	}
	
	private String getDoseReductionDisplay(DrugOrder edo) {
		String ret = "";
		DecimalFormat fPerc = new DecimalFormat("0");
		Double drugStrength = null;
		try {
			drugStrength = Double.valueOf(edo.getDrug().getStrength());
		}
		catch (Exception e) {}
		if (drugStrength != null) {
			Double reduction = (edo.getDose() / drugStrength) * 100;
			if (reduction < 100) {
				ret = fPerc.format(reduction) + "%";
			}
		}
		return ret;
	}
	
	private String getDrugDisplay(DrugOrder edo) {
		String drugDisplay = "";
		if (edo.getDrug() != null) {
			drugDisplay = edo.getDrug().getName();
		} else if (edo.getConcept() != null) {
			drugDisplay = edo.getConcept().getDisplayString();
		}
		return drugDisplay;
	}
}
