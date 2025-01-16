package org.openmrs.module.rwandareports.definition.result;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;
import org.openmrs.module.rowperpatientreports.patientdata.result.BasePatientDataResult;

public class AllMotherObservationValuesResult extends BasePatientDataResult {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	private String dateFormat = "yyyy-MM-dd";
	
	private List<Obs> value;
	
	private ResultFilter filter = null;
	
	private int minResultsOutput = -1;
	
	public AllMotherObservationValuesResult(RowPerPatientData patientData, EvaluationContext ec) {
		super(patientData, ec);
		dateFormat = patientData.getDateFormat();
	}
	
	public Class<?> getColumnClass() {
		return String.class;
	}
	
	public List<Obs> getValue() {
		return value;
	}
	
	public boolean isMultiple() {
		return true;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(List<Obs> value) {
		this.value = value;
	}
	
	public String getValueAsString() {
		StringBuilder result = new StringBuilder(" ");
		
		for (Obs o : getValue()) {
			try {
				if (filter != null) {
					result.append(filter.filter(o));
				} else {
					result.append(o.getValueAsString(Context.getLocale()));
					result.append(" ");
					result.append(new SimpleDateFormat(dateFormat).format(o.getObsDatetime()));
					result.append(" ");
				}
			}
			catch (Exception e) {
				log.info("Error retrieving obs info", e);
			}
		}
		return result.toString().trim();
	}
	
	public ResultFilter getFilter() {
		return filter;
	}
	
	public void setFilter(ResultFilter filter) {
		this.filter = filter;
	}
	
	public String getDateFormat() {
		return dateFormat;
	}
	
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	public int getMinResultsOutput() {
		return minResultsOutput;
	}
	
	public void setMinResultsOutput(int minResultsOutput) {
		this.minResultsOutput = minResultsOutput;
	}
}
