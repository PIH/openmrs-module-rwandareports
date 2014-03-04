package org.openmrs.module.rwandareports.definition;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.rowperpatientreports.patientdata.definition.BasePatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;



public class AllMotherObservationValues extends BasePatientData implements RowPerPatientData {

	private Concept concept;
	
	private ResultFilter filter;
	
	private ResultFilter outputFilter;
	
	private int minResultsOutput = -1;
	
	private Mapped<RowPerPatientData> definition;
	
	@ConfigurationProperty
	private Date startDate = null;
	
	@ConfigurationProperty
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
    	if(concept != null)
    	{
    		setName(concept.getName().getName());
    		setDescription(concept.getDisplayString());
    	}
    }


	public ResultFilter getFilter() {
		return filter;
	}


	public void setFilter(ResultFilter filter) {
		this.filter = filter;
	}


	public ResultFilter getOutputFilter() {
		return outputFilter;
	}


	public void setOutputFilter(ResultFilter outputFilter) {
		this.outputFilter = outputFilter;
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
	
    public int getMinResultsOutput() {
    	return minResultsOutput;
    }

    public void setMinResultsOutput(int minResultsOutput) {
    	this.minResultsOutput = minResultsOutput;
    }

    /**
     * @return the definition
     */
    public Mapped<RowPerPatientData> getDefinition() {
    	return definition;
    }

	
    /**
     * @param definition the definition to set
     */
    public void setDefinition(RowPerPatientData definition, Map<String, Object> mappings) {
    	this.definition = new Mapped<RowPerPatientData>(definition, mappings);
    }


}
