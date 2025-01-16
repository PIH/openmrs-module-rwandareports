package org.openmrs.module.rwandareports.definition.evaluator;

/**
 * Created by josua on 10/30/18.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.evaluator.RowPerPatientDataEvaluator;
import org.openmrs.module.rowperpatientreports.patientdata.result.AllObservationValuesResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rwandareports.definition.AllObsValuesByRemovingUnwantedEncounters;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Handler(supports = { AllObsValuesByRemovingUnwantedEncounters.class })
public class AllObsValuesByRemovingUnwantedEncountersEvaluator implements RowPerPatientDataEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) {
		AllObservationValuesResult par = new AllObservationValuesResult(patientData, context);
		AllObsValuesByRemovingUnwantedEncounters pd = (AllObsValuesByRemovingUnwantedEncounters) patientData;
		Date endDate = (Date) context.getParameterValue("endDate");
		
		Concept ConceptOfWantedObs = pd.getConceptOfWantedObs();
		Concept ConceptOfUnWantedObs = pd.getConceptOfUnWantedObs();
		List<Concept> listOfConceptOfWantedObs = new ArrayList<Concept>();
		listOfConceptOfWantedObs.add(ConceptOfWantedObs);
		
		List<Encounter> encs = Context.getEncounterService().getEncounters(pd.getPatient(), null, null, endDate,
		    pd.getForms(), null, null, null, null, false);
		List<Encounter> bufferEncounter = new ArrayList<Encounter>();
		if (encs.size() > 0) {
			//removing unwanted encounters
			for (Encounter enc : encs) {
				for (Obs o : enc.getObs()) {
					if (o.getConcept().getConceptId().equals(ConceptOfUnWantedObs.getId())) {
						bufferEncounter.add(enc);
					}
				}
			}
			if (bufferEncounter.size() > 0) {
				encs.removeAll(bufferEncounter);
			}
		}
		
		if (encs.size() > 0) {
			//selecting the wanted obs
			List<Obs> obs = Context.getObsService().getObservations(null, encs, listOfConceptOfWantedObs, null, null, null,
			    null, null, null, null, endDate, false);
			List<Obs> ansObs = new ArrayList<Obs>();
			if (obs.size() > 0) {
				for (Obs o : obs) {
					if (o.getValueCoded() != null) {
						ansObs.add(o);
					}
				}
				par.setValue(ansObs);
			} else {
				List<Obs> emptyList = new ArrayList<Obs>();
				
				par.setValue(emptyList);
			}
			
		} else {
			List<Obs> emptyList = new ArrayList<Obs>();
			par.setValue(emptyList);
		}
		return par;
	}
	
}
