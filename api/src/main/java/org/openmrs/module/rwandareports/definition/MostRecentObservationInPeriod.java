package org.openmrs.module.rwandareports.definition;

import org.openmrs.Concept;
import org.openmrs.module.rowperpatientreports.patientdata.definition.BasePatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MostRecentObservationInPeriod extends BasePatientData implements RowPerPatientData {
	
	private Concept concept;
	
	private List<Concept> answers = new ArrayList<Concept>();
	
	private ResultFilter filter = null;
	
	private boolean includeNull = true;
	
	private Date startDate = null;
	
	private Date endDate = null;
	
	/**
	 * @return the concept
	 */
	public Concept getConcept() {
		return concept;
	}
	
	/**
	 * @param concept the concept to set
	 */
	public void setConcept(Concept concept) {
		this.concept = concept;
		
	}
	
	public List<Concept> getAnswers() {
		return answers;
	}
	
	public void setAnswers(List<Concept> answers) {
		this.answers = answers;
	}
	
	public void addAnswer(Concept answer) {
		answers.add(answer);
	}
	
	public ResultFilter getFilter() {
		return filter;
	}
	
	public void setFilter(ResultFilter filter) {
		this.filter = filter;
	}
	
	public boolean isIncludeNull() {
		return includeNull;
	}
	
	public void setIncludeNull(boolean includeNull) {
		this.includeNull = includeNull;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
