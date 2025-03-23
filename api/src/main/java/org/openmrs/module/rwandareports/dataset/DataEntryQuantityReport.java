package org.openmrs.module.rwandareports.dataset;

import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.ArrayList;
import java.util.List;

public class DataEntryQuantityReport extends BaseDataSetDefinition {
	
	@ConfigurationProperty
	private Program program;
	
	@ConfigurationProperty
	private List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
	
	public DataEntryQuantityReport() {
		
	}
	
	public Program getProgram() {
		return program;
	}
	
	public void setProgram(Program program) {
		this.program = program;
	}
	
	public List<EncounterType> getEncounterTypes() {
		return encounterTypes;
	}
	
	public void setEncounterTypes(List<EncounterType> encounterTypes) {
		this.encounterTypes = encounterTypes;
	}
	
	public void addEncounterType(EncounterType encounterType) {
		encounterTypes.add(encounterType);
	}
}
