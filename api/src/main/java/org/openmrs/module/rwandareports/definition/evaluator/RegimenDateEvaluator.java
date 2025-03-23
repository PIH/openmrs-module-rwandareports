package org.openmrs.module.rwandareports.definition.evaluator;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.OrderGroup;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.HibernateUtil;
import org.openmrs.module.orderextension.DrugRegimen;
import org.openmrs.module.orderextension.api.OrderExtensionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.evaluator.RowPerPatientDataEvaluator;
import org.openmrs.module.rowperpatientreports.patientdata.result.DateResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rwandareports.definition.RegimenDateInformation;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.util.OpenmrsUtil;

@Handler(supports = { RegimenDateInformation.class })
public class RegimenDateEvaluator implements RowPerPatientDataEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) {
		
		DateResult par = new DateResult(patientData, context);
		
		GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
		List<Concept> iv = gp.getConceptList(GlobalPropertiesManagement.IV_CONCEPT);
		
		RegimenDateInformation pd = (RegimenDateInformation) patientData;
		par.setFormat(pd.getDateFormat());
		
		DrugRegimen regimen = null;
		
		if (pd.getRegimen() != null) {
			Integer regimenId = Integer.parseInt(pd.getRegimen());
			regimen = Context.getService(OrderExtensionService.class).getDrugRegimen(regimenId);
		} else {
			List<DrugOrder> drugOrders = Context.getService(OrderExtensionService.class).getDrugOrders(pd.getPatient(),
			    pd.getIndication(), pd.getAsOfDate(), pd.getUntilDate());
			
			for (DrugOrder eo : drugOrders) {
				if (eo.getOrderGroup() != null) {
					OrderGroup orderGroup = HibernateUtil.getRealObjectFromProxy(eo.getOrderGroup());
					if (orderGroup instanceof DrugRegimen) {
						DrugRegimen reg = (DrugRegimen) orderGroup;
						
						if (regimen == null || reg.getFirstDrugOrderStartDate().after(regimen.getFirstDrugOrderStartDate())) {
							regimen = (DrugRegimen) orderGroup;
						}
					}
				}
			}
		}
		
		if (regimen != null) {
			
			Date startDate = null;
			for (DrugOrder order : regimen.getMembers()) {
				if (order.getRoute() != null && iv.contains(order.getRoute())) {
					
					if (pd.getAsOfDate() != null) {
						
						if (startDate == null && OpenmrsUtil.compare(order.getEffectiveStartDate(), pd.getAsOfDate()) >= 0) {
							startDate = order.getEffectiveStartDate();
						} else if (startDate != null && order.getEffectiveStartDate().before(startDate)
						        && OpenmrsUtil.compare(order.getEffectiveStartDate(), pd.getAsOfDate()) >= 0) {
							startDate = order.getEffectiveStartDate();
						}
					} else {
						if (startDate == null || order.getEffectiveStartDate().after(startDate)) {
							startDate = order.getEffectiveStartDate();
						}
					}
				}
			}
			par.setValue(startDate);
		}
		
		return par;
	}
}
