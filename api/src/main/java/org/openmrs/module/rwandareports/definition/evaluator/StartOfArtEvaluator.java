package org.openmrs.module.rwandareports.definition.evaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.util.OrderEntryUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.evaluator.RowPerPatientDataEvaluator;
import org.openmrs.module.rowperpatientreports.patientdata.result.AllDrugOrdersResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rwandareports.definition.StartOfArt;
import org.openmrs.util.OpenmrsUtil;

@Handler(supports = { StartOfArt.class })
public class StartOfArtEvaluator implements RowPerPatientDataEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) {
		
		AllDrugOrdersResult par = new AllDrugOrdersResult(patientData, context);
		StartOfArt pd = (StartOfArt) patientData;
		
		par.setDateFormat(pd.getDateFormat());
		
		List<DrugOrder> orders = OrderEntryUtil.getDrugOrdersByPatient(pd.getPatient());
		
		List<DrugOrder> results = new ArrayList<DrugOrder>();
		
		if (orders != null) {
			if (pd.getDrugConceptSetConcept() != null) {
				List<Concept> drugConcepts = Context.getConceptService().getConceptsByConceptSet(
				    pd.getDrugConceptSetConcept());
				if (drugConcepts != null) {
					for (DrugOrder order : orders) {
						Concept drug = null;
						try {
							drug = order.getDrug().getConcept();
						}
						catch (Exception e) {
							log.error("Unable to retrieve a drug from the drug order: " + e.getMessage());
						}
						if (drug != null) {
							if (drugConcepts.contains(drug)) {
								if (order.getEffectiveStartDate() != null
								        && (pd.getEndDate() == null || OpenmrsUtil.compare(pd.getEndDate(),
								            order.getEffectiveStartDate()) >= 0)) {
									if (results.size() == 0
									        || order.getEffectiveStartDate().before(results.get(0).getEffectiveStartDate())) {
										results = new ArrayList<DrugOrder>();
										results.add(order);
									} else if (results.size() > 0
									        && order.getEffectiveStartDate().equals(results.get(0).getEffectiveStartDate())) {
										results.add(order);
									}
								}
							}
						}
					}
				}
			}
		}
		
		Collections.sort(results, new Comparator<DrugOrder>() {
			
			@Override
			public int compare(DrugOrder d1, DrugOrder d2) {
				
				return d1.getDrug().getName().toLowerCase().compareTo(d2.getDrug().getName().toLowerCase());
			}
		});
		
		par.setValue(results);
		
		return par;
	}
}
