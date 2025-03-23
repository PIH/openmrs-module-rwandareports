package org.openmrs.module.rwandareports.definition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.rowperpatientreports.patientdata.definition.BasePatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

public class HIVOutcome extends BasePatientData implements RowPerPatientData {
	
	@ConfigurationProperty
	private Date endDate = null;
	
	private List<Program> allHivPrograms = new ArrayList<Program>();
	
	private Concept exitFromCare = null;
	
	private GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	public HIVOutcome() {
		Program pmtct = gp.getProgram(GlobalPropertiesManagement.PMTCT_PREGNANCY_PROGRAM);
		Program adultHiv = gp.getProgram(GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
		Program pediHiv = gp.getProgram(GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
		Program pmtctCC = gp.getProgram(GlobalPropertiesManagement.PMTCT_COMBINED_MOTHER_PROGRAM);
		
		allHivPrograms.add(pmtct);
		allHivPrograms.add(adultHiv);
		allHivPrograms.add(pediHiv);
		allHivPrograms.add(pmtctCC);
		
		exitFromCare = gp.getConcept(GlobalPropertiesManagement.REASON_FOR_EXITING_CARE);
	}
	
	public List<Program> getAllHivPrograms() {
		return allHivPrograms;
	}
	
	public Concept getExitFromCare() {
		return exitFromCare;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
