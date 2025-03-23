package org.openmrs.module.rwandareports.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.rowperpatientreports.patientdata.definition.EvaluateDefinitionForOtherPersonData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.definition.PersonData;
import org.openmrs.module.rowperpatientreports.patientdata.evaluator.RowPerPatientDataEvaluator;
import org.openmrs.module.rowperpatientreports.patientdata.result.EvaluateForOtherPatientDataResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PersonResult;
import org.openmrs.module.rowperpatientreports.patientdata.service.RowPerPatientDataService;
import org.openmrs.module.rwandareports.definition.EvaluateMotherDefinition;
import org.openmrs.module.rwandareports.definition.result.EvaluateMotherDefinitionResult;

@Handler(supports = { EvaluateMotherDefinition.class })
public class EvaluateMotherDefinitionEvaluator implements RowPerPatientDataEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) throws EvaluationException {
		
		EvaluateMotherDefinition pd = (EvaluateMotherDefinition) patientData;
		EvaluateMotherDefinitionResult result = new EvaluateMotherDefinitionResult(pd, context);
		
		//find all the people we need to evaluate this for
		if (pd.getPersonData() != null) {
			PersonData personData = pd.getPersonData().getParameterizable();
			personData.setPatientId(pd.getPatientId());
			personData.setPatient(pd.getPatient());
			PersonResult personResults = (PersonResult) Context.getService(RowPerPatientDataService.class).evaluate(
			    pd.getPersonData(), context);
			
			if (pd.getDefinition() != null) {
				Mapped<RowPerPatientData> mapped = pd.getDefinition();
				
				int count = 1;
				for (Person p : personResults.getValue()) {
					RowPerPatientData definition = mapped.getParameterizable();
					definition.setPatientId(p.getId());
					try {
						definition.setPatient(Context.getPatientService().getPatient(p.getId()));
						mapped.setParameterizable(definition);
						PatientDataResult value = Context.getService(RowPerPatientDataService.class).evaluate(mapped,
						    context);
						
						if (count > 1) {
							value.setName(pd.getName() + count);
							value.setDefinition(pd.getDescription() + count);
						} else {
							value.setName(pd.getName());
							value.setDefinition(pd.getDescription());
						}
						if (value.getValue() != null) {
							result.addResult(value);
						}
					}
					catch (Exception e) {
						log.info("Unable to retrieve and evaluate for patient id " + p.getId(), e);
					}
					
					count++;
				}
			}
		}
		
		return result;
	}
}
