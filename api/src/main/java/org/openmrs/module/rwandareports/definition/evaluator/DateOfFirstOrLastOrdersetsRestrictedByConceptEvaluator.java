package org.openmrs.module.rwandareports.definition.evaluator;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.api.OrderExtensionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.evaluator.RowPerPatientDataEvaluator;
import org.openmrs.module.rowperpatientreports.patientdata.result.DateResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rwandareports.definition.DateOfFirstOrLastDrugOrderRestrictedByConcept;
import org.openmrs.util.OpenmrsUtil;

@Handler(supports = { DateOfFirstOrLastDrugOrderRestrictedByConcept.class })
public class DateOfFirstOrLastOrdersetsRestrictedByConceptEvaluator implements RowPerPatientDataEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) {
		
		DateResult par = new DateResult(patientData, context);
		DateOfFirstOrLastDrugOrderRestrictedByConcept pd = (DateOfFirstOrLastDrugOrderRestrictedByConcept) patientData;
		Date startDate = pd.getStartDate();
		Date endDate = pd.getEndDate();
		if (pd.getStartDate() == null)
			startDate = (Date) context.getParameterValue("startDate");
		if (pd.getEndDate() == null)
			endDate = (Date) context.getParameterValue("endDate");
		par.setFormat(pd.getDateFormat());
		
		Concept drugConcept = pd.getDrugConcept();
		List<DrugOrder> orders = Context.getService(OrderExtensionService.class).getDrugOrders(pd.getPatient(), drugConcept,
		    null, null);
		if (orders != null) {
			if (pd.getDrugConcept() != null) {
				DrugOrder drugOrder = null;
				for (DrugOrder order : orders) {
					if (order.getEffectiveStartDate() != null) {
						
						if (pd.getFirsOrLast() != null && pd.getFirsOrLast().compareToIgnoreCase("first") == 0) {
							if ((endDate == null || OpenmrsUtil.compare(order.getEffectiveStartDate(), endDate) <= 0)
							        && (drugOrder == null || order.getEffectiveStartDate().before(
							            drugOrder.getEffectiveStartDate()))) {
								drugOrder = order;
							}
						} else if (pd.getFirsOrLast() != null && pd.getFirsOrLast().compareToIgnoreCase("last") == 0) {
							if ((endDate == null || OpenmrsUtil.compare(order.getEffectiveStartDate(), endDate) <= 0)
							        && (drugOrder == null || order.getEffectiveStartDate().after(
							            drugOrder.getEffectiveStartDate()))) {
								drugOrder = order;
							}
						} else if (pd.getFirsOrLast() != null
						        && pd.getFirsOrLast().compareToIgnoreCase("firstInPeriod") == 0) {
							if (order.getEffectiveStartDate() != null
							        && (startDate == null || OpenmrsUtil.compare(order.getEffectiveStartDate(), startDate) >= 0)
							        && (endDate == null || OpenmrsUtil.compare(order.getEffectiveStartDate(), endDate) <= 0)) {
								if (drugOrder == null
								        || order.getEffectiveStartDate().before(drugOrder.getEffectiveStartDate())) {
									drugOrder = order;
								}
							}
						} else if (pd.getFirsOrLast() != null && pd.getFirsOrLast().compareToIgnoreCase("lastInPeriod") == 0) {
							if (order.getEffectiveStartDate() != null
							        && (startDate == null || OpenmrsUtil.compare(order.getEffectiveStartDate(), startDate) >= 0)
							        && (endDate == null || OpenmrsUtil.compare(order.getEffectiveStartDate(), endDate) <= 0)) {
								if (drugOrder == null
								        || order.getEffectiveStartDate().after(drugOrder.getEffectiveStartDate())) {
									drugOrder = order;
								}
							}
						}
						
					}
				}
				if (drugOrder != null) {
					par.setValue(drugOrder.getEffectiveStartDate());
				}
			}
		}
		
		return par;
	}
}
