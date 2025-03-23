package org.openmrs.module.rwandareports.dhis2.scheduler;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.rwandareports.dhis2.util.DHIS2Util;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * A scheduled task for sending HMIS Cancer Screening Monthly Indicator Report to DHIS2 . Scheduled
 * Tasks are regularly timed tasks that can run every few seconds, every day, every week, etc. See
 * Admin-->Manager Scheduled Tasks for the administration of them.
 * 
 * @author Bailly RURANGIRWA
 */
public class HMISReportAutoRunTask extends AbstractTask {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public void execute() {
		ReportDefinition hmisReportDefinition = Context.getService(ReportDefinitionService.class).getDefinitionByUuid(
		    Context.getAdministrationService().getGlobalProperty("reports.HMISReportDefinitionUUID"));
		DateTime now = new DateTime().minusMonths(1);
		Date startDate = DateUtil.getStartOfMonth(now.toDate());
		Date endDate = DateUtil.getEndOfMonth(now.toDate());
		
		String locationsMapping = Context.getAdministrationService().getGlobalProperty("reports.LocationsToOrgUnitsMapping");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj;
		try {
			actualObj = mapper.readTree(locationsMapping);
			Iterator<Entry<String, JsonNode>> locationsIterator = actualObj.getFields();
			while (locationsIterator.hasNext()) {
				Map.Entry<String, JsonNode> entry = locationsIterator.next();
				Location location = Context.getLocationService().getLocation(entry.getKey());
				if (location != null && hmisReportDefinition != null) {
					log.error("Running report for " + location.getName());
					DHIS2Util.runAndPushReportToDHIS(hmisReportDefinition, startDate, endDate, location, entry.getValue()
					        .asText());
				}
			}
		}
		catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
