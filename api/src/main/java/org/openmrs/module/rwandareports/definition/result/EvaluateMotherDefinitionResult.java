package org.openmrs.module.rwandareports.definition.result;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.result.BasePatientDataResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;


public class EvaluateMotherDefinitionResult extends BasePatientDataResult {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	private List<PatientDataResult> value;
	
	private List<Obs> valueob;
	
    private Date dateOfObservation;
	
	//private Obs obs;
	
	private String dateFormat = "yyyy/MM/dd";
	
	
	public EvaluateMotherDefinitionResult(RowPerPatientData patientData, EvaluationContext ec) {
	    super(patientData, ec);
    }

	public Class<?> getColumnClass() {
		return String.class;
	}
	
	public List<PatientDataResult> getValue() {
		if(value == null)
		{
			value = new ArrayList<PatientDataResult>();
		}
		return value;
	}
	
	/**
     * @param value the value to set
     */
    public void setValue(List<PatientDataResult> value) {
    	this.value = value;
    }
	public boolean isMultiple() {
		return true;
	}    
	
    public String getValueAsString() {
	    StringBuilder result = new StringBuilder(" ");
	    
	    for(PatientDataResult p: getValue())
	    {
    		result.append(p.getValueAsString());
    		result.append(" ");
	    }
	    return result.toString().trim();
    }
    
     
        public String getValueAsObs() {
		
	    if(getValueAsString() != null && dateOfObservation != null)
	    {
	    	return new SimpleDateFormat(dateFormat).format(dateOfObservation);
	    }
	    else if(getValueAsString() != null)
	    {
	    	return getValueAsString()+ "";
	    }
    	
	    return null;
    	
      }
     
        public String getDateFormat() {
    		return dateFormat;
    	}

    	public void setDateFormat(String dateFormat) {
    		if(dateFormat != null)
    		{
    			this.dateFormat = dateFormat;
    		}
    	}

     
    
    public void addResult(PatientDataResult value)
    {
    	getValue().add(value);
    }
}
