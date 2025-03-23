package org.openmrs.module.rwandareports.definition.evaluator;

import java.util.ArrayList;
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
import org.openmrs.module.rowperpatientreports.patientdata.result.DateResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rowperpatientreports.patientdata.service.RowPerPatientDataService;
import org.openmrs.module.rwandareports.definition.ArtSwitchDate;

@Handler(supports = { ArtSwitchDate.class })
public class ArtSwitchDateEvaluator implements RowPerPatientDataEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) throws EvaluationException {
		
		DateResult par = new DateResult(patientData, context);
		ArtSwitchDate pd = (ArtSwitchDate) patientData;
		
		par.setFormat(pd.getDateFormat());
		
		Mapped<RowPerPatientData> definition = pd.getArtData();
		
		AllDrugOrdersResult patientDataResult = (AllDrugOrdersResult) Context.getService(RowPerPatientDataService.class)
		        .evaluate(definition, context);
		
		List<DrugOrder> existing = patientDataResult.getValue();
		
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
							//check to see if any orders are still valid
							boolean stillActive = false;
							for (DrugOrder check : arts) {
								if (OrderEntryUtil.isCurrent(check, startingDate)) {
									stillActive = true;
									break;
								}
							}
							
							if (stillActive) {
								par.setValue(startingDate);
							}
						}
					}
				}
			}
		}
		
		return par;
	}
}
