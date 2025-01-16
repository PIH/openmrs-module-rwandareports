package org.openmrs.module.rwandareports.definition.evaluator;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.DrugOrder;
import org.openmrs.annotation.Handler;
import org.openmrs.module.orderextension.util.OrderEntryUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.evaluator.RowPerPatientDataEvaluator;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rwandareports.definition.CurrentPatientDrugOrder;
import org.openmrs.module.rwandareports.definition.result.CurrentDrugOrderResults;

@Handler(supports = { CurrentPatientDrugOrder.class })
public class CurrentPatientDrugOrderEvaluator implements RowPerPatientDataEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	StringBuilder result = new StringBuilder();
	
	public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) {
		
		CurrentDrugOrderResults par = new CurrentDrugOrderResults(patientData, context);
		CurrentPatientDrugOrder pd = (CurrentPatientDrugOrder) patientData;
		par.setDrugFilter(pd.getResultFilter());
		List<DrugOrder> orders = OrderEntryUtil.getDrugOrdersByPatient(pd.getPatient());
		for (Iterator<DrugOrder> i = orders.iterator(); i.hasNext();) {
			DrugOrder drugOrder = i.next();
			if (!OrderEntryUtil.isCurrent(drugOrder)) {
				i.remove();
			}
		}
		for (DrugOrder drugOrder : orders) {
			if ((drugOrder != null) && !drugOrder.isDiscontinuedRightNow()) {
				par.setValue(orders);
			}
		}
		return par;
	}
}
