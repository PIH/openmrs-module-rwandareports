package org.openmrs.module.rwandareports.definition.evaluator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderGroup;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.HibernateUtil;
import org.openmrs.module.orderextension.DrugRegimen;
import org.openmrs.module.orderextension.api.OrderExtensionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.evaluator.RowPerPatientDataEvaluator;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.StringResult;
import org.openmrs.module.rwandareports.definition.DrugRegimenInformation;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.util.OpenmrsUtil;

@Handler(supports = { DrugRegimenInformation.class })
public class DrugRegimenInformationEvaluator implements RowPerPatientDataEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) {
		
		StringResult par = new StringResult(patientData, context);
		DrugRegimenInformation pd = (DrugRegimenInformation) patientData;
		
		GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
		List<Concept> iv = gp.getConceptList(GlobalPropertiesManagement.IV_CONCEPT);
		
		DrugRegimen regimen = null;
		
		if (pd.getRegimen() != null) {
			String[] regimenInfo = pd.getRegimen().split(":");
			Integer regimenId = Integer.parseInt(regimenInfo[0]);
			regimen = Context.getService(OrderExtensionService.class).getDrugRegimen(regimenId);
			
			if (regimenInfo.length > 1) {
				Integer regOffset = Integer.parseInt(regimenInfo[1]);
				Calendar offset = Calendar.getInstance();
				offset.setTime(regimen.getFirstDrugOrderStartDate());
				offset.add(Calendar.DAY_OF_YEAR, regOffset);
				pd.setAsOfDate(offset.getTime());
				pd.setUntilDate(offset.getTime());
				pd.setShowStartDate(true);
			}
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
			StringBuilder result = new StringBuilder();
			if (regimen.isCyclical()) {
				Integer maxCycleNum = Context.getService(OrderExtensionService.class)
				        .getMaxNumberOfCyclesForRegimen(regimen);
				
				result.append("Cycle #: ");
				result.append(regimen.getCycleNumber());
				result.append(" of ");
				result.append(maxCycleNum.toString());
			}
			
			result.append(" Regimen: ");
			result.append(regimen.getName());
			
			StringBuilder drugs = new StringBuilder();
			
			List<DrugOrder> members = new ArrayList<DrugOrder>();
			members.addAll(regimen.getMembers());
			Collections.sort(members, new Comparator<DrugOrder>() {
				
				@Override
				public int compare(DrugOrder o1, DrugOrder o2) {
					return o1.getDrug().getName().compareTo(o2.getDrug().getName());
				}
				
			});
			
			Date startDate = null;
			for (DrugOrder order : members) {
				
				if (pd.isShowDrugDetails()
				        && (pd.getIndication() == null || pd.getIndication().equals(order.getOrderReason()))) {
					if ((OpenmrsUtil.compare(order.getEffectiveStartDate(), pd.getAsOfDate()) >= 0)
					        && (pd.getUntilDate() == null || OpenmrsUtil.compare(order.getEffectiveStartDate(),
					            pd.getUntilDate()) <= 0)) {
						drugs.append("\n");
						drugs.append(order.getDrug().getName());
						drugs.append(" ");
						drugs.append(order.getDose());
						drugs.append(order.getDoseUnits() == null ? "" : order.getDoseUnits().getDisplayString());
					}
				}
				
				if (order.getRoute() != null && iv.contains(order.getRoute())) {
					
					if (pd.getAsOfDate() != null) {
						if (((startDate == null || OpenmrsUtil.compare(order.getEffectiveStartDate(), pd.getAsOfDate()) >= 0) || (order
						        .getEffectiveStartDate().before(startDate) && OpenmrsUtil.compare(
						    order.getEffectiveStartDate(), pd.getAsOfDate()) >= 0))
						        && (pd.getUntilDate() == null || OpenmrsUtil.compare(order.getEffectiveStartDate(),
						            pd.getUntilDate()) <= 0)) {
							startDate = order.getEffectiveStartDate();
						}
					} else {
						if (startDate == null || order.getEffectiveStartDate().after(startDate)) {
							startDate = order.getEffectiveStartDate();
						}
					}
				}
			}
			
			if (startDate != null) {
				long cycleDay = startDate.getTime() - regimen.getFirstDrugOrderStartDate().getTime();
				if (cycleDay > 0) {
					cycleDay = cycleDay / 86400000;
				}
				result.append(" Day: ");
				result.append(String.valueOf(cycleDay + 1));
			}
			
			if (pd.isShowDrugDetails()) {
				result.append("");
				result.append(drugs);
			}
			
			par.setValue(result.toString());
		}
		return par;
	}
}
