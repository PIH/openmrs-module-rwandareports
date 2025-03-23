package org.openmrs.module.rwandareports.definition;

import java.util.List;
import java.util.Map;
import org.openmrs.Obs;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.rowperpatientreports.patientdata.definition.BasePatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.PersonData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;

public class EvaluateMotherDefinition extends BasePatientData implements RowPerPatientData {
	
	private Mapped<PersonData> personData;
	
	private Mapped<RowPerPatientData> definition;
	
	private List<Obs> value;
	
	/**
	 * @return the personData
	 */
	public Mapped<PersonData> getPersonData() {
		return personData;
	}
	
	/**
	 * @param personData the personData to set
	 */
	public void setPersonData(PersonData personData, Map<String, Object> mappings) {
		this.personData = new Mapped<PersonData>(personData, mappings);
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
	
	public List<Obs> getValue() {
		return value;
	}
	
	public void setValue(List<Obs> value) {
		this.value = value;
	}
	
}
