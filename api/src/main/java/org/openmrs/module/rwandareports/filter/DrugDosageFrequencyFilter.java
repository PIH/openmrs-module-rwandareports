package org.openmrs.module.rwandareports.filter;

import org.openmrs.DrugOrder;
import org.openmrs.OrderFrequency;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;

public class DrugDosageFrequencyFilter implements ResultFilter {
	
	private String finalDateFormat = null;
	
	public Object filter(Object value) {
		DrugOrder drugOrder = (DrugOrder) value;
		
		StringBuilder result = new StringBuilder();
		
		if (drugOrder != null && drugOrder.getDrug() != null) {
			result.append(drugOrder.getDrug().getName());
			result.append(" ");
			result.append(drugOrder.getDose());
			result.append(drugOrder.getDoseUnits() == null ? "" : drugOrder.getDoseUnits().getDisplayString());
			result.append(" ");
			OrderFrequency frequency = drugOrder.getFrequency();
			result.append(frequency == null ? "" : frequency.getConcept().getDisplayString());
		}
		
		return result.toString();
	}
	
	public String getFinalDateFormat() {
		return finalDateFormat;
	}
	
	public void setFinalDateFormat(String finalDateFormat) {
		this.finalDateFormat = finalDateFormat;
	}
	
	public Object filterWhenNull() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
