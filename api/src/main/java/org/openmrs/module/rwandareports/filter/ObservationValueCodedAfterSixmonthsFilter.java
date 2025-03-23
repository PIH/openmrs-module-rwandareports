package org.openmrs.module.rwandareports.filter;

import java.util.Calendar;
import java.util.List;

import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ResultFilter;

public class ObservationValueCodedAfterSixmonthsFilter implements ResultFilter {
	
	private String obsValueCoded = "";
	
	public Object filter(Object value) {
		Obs obs = (Obs) value;
		if (obs != null && obs.getValueCoded() != null) {
			obsValueCoded = obs.getValueCoded().getName().toString();
			return obsValueCoded;
		}
		
		return null;
		
		/*int obsId = Integer.parseInt((String)value);
		
		Obs ob=Context.getObsService().getObs(obsId);
		
		List<Obs> obs = Context.getObsService().getObservationsByPersonAndConcept(ob.getPerson(), ob.getConcept());
		
		Obs obNeeded=null;
		
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		Obs tempOb=null;
		for (int j = 0; j < obs.size(); j++) {	
		for (int i = 0; i < obs.size(); i++) {
			if(i+1 < obs.size() && obs.get(i).getObsDatetime().compareTo(obs.get(i+1).getObsDatetime())>0){
				tempOb=obs.get(i);
				obs.set(i, obs.get(i+1));
				obs.set(i+1, tempOb);			
			}
		}
		}
		
		cal1.setTime(obs.get(0).getObsDatetime());
		
		if(obs != null && obs.size()>=2)
		{
			for(Obs o:obs)
			{
				cal2.setTime(o.getObsDatetime());
				
				if(cal2.getTimeInMillis()-cal1.getTimeInMillis() >= 15552000000L ) {   //15552000000L milliseconds is equivalent to 6 months
				obNeeded=o;
				break;
				}				
			}
		}
		if(obNeeded!=null && obNeeded.getValueCoded()!=null){
		obsValueCoded=obNeeded.getValueCoded().getName().toString();
		return obsValueCoded;
		}
		
			return null;
		*/}
	
	public Object filterWhenNull() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
