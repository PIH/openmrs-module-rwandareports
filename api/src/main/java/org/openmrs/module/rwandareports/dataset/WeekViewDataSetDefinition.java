package org.openmrs.module.rwandareports.dataset;

import java.util.Date;

import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.rowperpatientreports.dataset.definition.RowPerPatientDataSetDefinition;

/**
 *
 */
public class WeekViewDataSetDefinition extends BaseDataSetDefinition {
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty
	private RowPerPatientDataSetDefinition baseDefinition;
	
	@ConfigurationProperty
	private Date startDate;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public WeekViewDataSetDefinition() {
		
	}
	
	/**
	 * Base Constructor
	 */
	public WeekViewDataSetDefinition(RowPerPatientDataSetDefinition baseDefinition) {
		this();
		this.baseDefinition = baseDefinition;
	}
	
	//***** INSTANCE METHODS *****
	
	//***** PROPERTY ACCESS *****
	
	/**
	 * @return the baseDefinition
	 */
	public RowPerPatientDataSetDefinition getBaseDefinition() {
		return baseDefinition;
	}
	
	/**
	 * @param baseDefinition the baseDefinition to set
	 */
	public void setBaseDefinition(RowPerPatientDataSetDefinition baseDefinition) {
		this.baseDefinition = baseDefinition;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}
