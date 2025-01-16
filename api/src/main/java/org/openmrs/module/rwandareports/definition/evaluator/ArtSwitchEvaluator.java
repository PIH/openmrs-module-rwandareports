package org.openmrs.module.rwandareports.definition.evaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.util.OrderEntryUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.evaluator.RowPerPatientDataEvaluator;
import org.openmrs.module.rowperpatientreports.patientdata.result.AllDrugOrdersResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rowperpatientreports.patientdata.service.RowPerPatientDataService;
import org.openmrs.module.rwandareports.definition.ArtSwitch;

@Handler(supports = { ArtSwitch.class })
public class ArtSwitchEvaluator implements RowPerPatientDataEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) throws EvaluationException {
		
		AllDrugOrdersResult par = new AllDrugOrdersResult(patientData, context);
		ArtSwitch pd = (ArtSwitch) patientData;
		
		par.setDateFormat(pd.getDateFormat());
		
		Mapped<RowPerPatientData> definition = pd.getArtData();
		
		AllDrugOrdersResult patientDataResult = (AllDrugOrdersResult) Context.getService(RowPerPatientDataService.class)
		        .evaluate(definition, context);
		
		List<DrugOrder> existing = patientDataResult.getValue();
		
		List<DrugOrder> results = new ArrayList<DrugOrder>();
		
		if (existing != null && existing.size() > 0) {
			List<Drug> existingDrugs = new ArrayList<Drug>();
			
			for (DrugOrder o : existing) {
				existingDrugs.add(o.getDrug());
			}
			
			Date currentStart = null;
			for (DrugOrder ex : existing) {
				if (currentStart == null || ex.getEffectiveStartDate().after(currentStart)) {
					currentStart = ex.getEffectiveStartDate();
				}
			}
			
			List<DrugOrder> orders = OrderEntryUtil.getDrugOrdersByPatient(pd.getPatient());
			
			if (orders != null && orders.size() > 0) {
				if (pd.getDrugConceptSetConcept() != null) {
					List<Concept> drugConcepts = Context.getConceptService().getConceptsByConceptSet(
					    pd.getDrugConceptSetConcept());
					if (drugConcepts != null) {
						Date startingDate = null;
						
						List<DrugOrder> arts = new ArrayList<DrugOrder>();
						for (DrugOrder order : orders) {
							Concept drug = null;
							try {
								drug = order.getDrug().getConcept();
							}
							catch (Exception e) {
								log.error("Unable to retrieve a drug from the drug order: " + e.getMessage());
							}
							if (drug != null) {
								//make sure it is art and not a drug the patient is already on (to remove change in dosages)
								if (drugConcepts.contains(drug)) {
									arts.add(order);
									if (!existingDrugs.contains(order.getDrug())) {
										if (order.getEffectiveStartDate() != null) {
											if (order.getEffectiveStartDate().after(currentStart)
											        && (startingDate == null || startingDate.after(order
											                .getEffectiveStartDate()))
											        && (pd.getEndDate() == null || order.getEffectiveStartDate().before(
											            pd.getEndDate()))) {
												startingDate = order.getEffectiveStartDate();
											}
										}
									}
								}
							}
						}
						
						//now check to see if any of the current drugs have been stopped before the next starting date
						for (DrugOrder eo : existing) {
							if ((eo.getEffectiveStopDate() != null && eo.getEffectiveStopDate().after(currentStart))
							        && (startingDate == null || startingDate.after(eo.getEffectiveStopDate()))
							        && (pd.getEndDate() == null || eo.getEffectiveStopDate().before(pd.getEndDate()))) {
								startingDate = eo.getEffectiveStopDate();
							}
						}
						
						if (startingDate != null) {
							for (DrugOrder art : arts) {
								if (OrderEntryUtil.isCurrent(art, startingDate)) {
									results.add(art);
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
