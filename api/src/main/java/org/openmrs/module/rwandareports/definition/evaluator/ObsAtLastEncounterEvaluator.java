package org.openmrs.module.rwandareports.definition.evaluator;

/**
 * Created by josua on 10/16/18.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.evaluator.RowPerPatientDataEvaluator;
import org.openmrs.module.rowperpatientreports.patientdata.result.AllObservationValuesResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.ObservationResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rwandareports.definition.ObsAtLastEncounter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Handler(supports = { ObsAtLastEncounter.class })
public class ObsAtLastEncounterEvaluator implements RowPerPatientDataEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) {
		AllObservationValuesResult par = new AllObservationValuesResult(patientData, context);
		ObsAtLastEncounter pd = (ObsAtLastEncounter) patientData;
		Date endDate = (Date) context.getParameterValue("endDate");
		Concept c = pd.getConcept();
		EncounterType encounterType = pd.getEncounterType();
		List<EncounterType> theEncType = new ArrayList<EncounterType>();
		theEncType.add(encounterType);
		
		List<Encounter> encs = Context.getEncounterService().getEncounters(pd.getPatient(), null, null, endDate, null,
		    theEncType, null, null, null, false);
		
		Encounter mostRecentEncounter = null;
		if (encs.size() > 0) {
			//find the most recent encounter
			for (Encounter enc : encs) {
				if (mostRecentEncounter == null
				        || enc.getEncounterDatetime().compareTo(mostRecentEncounter.getEncounterDatetime()) > 0) {
					mostRecentEncounter = enc;
				}
			}
		}
		List<Concept> theConcept = new ArrayList<Concept>();
		theConcept.add(c);
		if (mostRecentEncounter != null) {
			List<Obs> obs = Context.getObsService().getObservations(null, null, theConcept, null, null, null, null, null,
			    null, mostRecentEncounter.getEncounterDatetime(), mostRecentEncounter.getEncounterDatetime(), false);
			List<Obs> ansObs = new ArrayList<Obs>();
			if (obs.size() > 0) {
				for (Obs o : obs) {
					ansObs.add(o);
				}
			}
			par.setValue(ansObs);
			
		} else {
			List<Obs> emptyList = new ArrayList<Obs>();
			par.setValue(emptyList);
		}
		
		return par;
	}
}
