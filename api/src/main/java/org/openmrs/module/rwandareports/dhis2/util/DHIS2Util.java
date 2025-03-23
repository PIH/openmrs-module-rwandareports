package org.openmrs.module.rwandareports.dhis2.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.Report;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Priority;
import org.openmrs.module.reporting.report.ReportRequest.Status;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.web.renderers.DefaultWebRenderer;
import org.openmrs.module.rwandareports.dhis2.model.DHISDataValue;
import org.openmrs.module.rwandareports.dhis2.model.DHISDataValueSet;
import org.openmrs.module.rwandareports.widget.AllLocation;

public class DHIS2Util {
	
	protected final static Log log = LogFactory.getLog(DHIS2Util.class);
	
	public static void runAndPushReportToDHIS(ReportDefinition reportDefinition, Date startDate, Date endDate,
	        Location location, String orgUnitUid) {
		Report ranReport = runIndicatorReport(reportDefinition, startDate, endDate, location);
		if (ranReport != null) {
			sendReportDataToDHIS(ranReport, orgUnitUid);
		}
	}
	
	public static Report runIndicatorReport(ReportDefinition reportDef, Date startDate, Date endDate, Location location) {
		
		AllLocation allLocatons = new AllLocation();
		allLocatons.setHierarchy(AllLocation.LOCATION);
		allLocatons.setValue(location.getName());
		
		ReportRequest request = new ReportRequest(new Mapped<ReportDefinition>(reportDef, null), null, new RenderingMode(
		        new DefaultWebRenderer(), "Web", null, 100), Priority.HIGHEST, null);
		
		request.getReportDefinition().addParameterMapping("startDate", startDate);
		request.getReportDefinition().addParameterMapping("endDate", endDate);
		request.getReportDefinition().addParameterMapping("location", allLocatons);
		request.setStatus(Status.PROCESSING);
		request = Context.getService(ReportService.class).saveReportRequest(request);
		
		return Context.getService(ReportService.class).runReport(request);
	}
	
	public static Object sendReportDataToDHIS(Report ranReport, String orgUnitUid) {
		
		DHISDataValueSet dataValueSet = new DHISDataValueSet();
		String datasetToSend = Context.getAdministrationService().getGlobalProperty("reports.HMISDataSetToSend");
		DataSet ds = ranReport.getReportData().getDataSets().get(datasetToSend);
		List<DataSetColumn> columns = ds.getMetaData().getColumns();
		DataSetRow row = ds.iterator().next();
		List<DHISDataValue> dataValues = new ArrayList<DHISDataValue>();
		String dataSetId = Context.getAdministrationService().getGlobalProperty("reports.HMISDataSetId");
		
		String indicatorsMapping = Context.getAdministrationService().getGlobalProperty(
		    "reports.HMISIndicatorToDataElementsMapping");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = null;
		try {
			actualObj = mapper.readTree(indicatorsMapping);
			
			for (int i = 0; i < columns.size(); i++) {
				DHISDataValue dv = new DHISDataValue();
				String column = columns.get(i).getName();
				
				if (StringUtils.isNotBlank(column)) {
					String value = row.getColumnValue(column).toString();
					
					if (StringUtils.isNotBlank(value)) {
						if (actualObj.get(column) != null) {
							dv.setDataElement(actualObj.get(column).get("dataElement").asText());
							dv.setValue(value);
							dv.setCategoryOptionCombo(actualObj.get(column).get("categoryOptionCombo").asText());
							dv.setComment(column);
							dataValues.add(dv);
						}
					}
				}
			}
		}
		catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		dataValueSet.setDataValues(dataValues);
		dataValueSet.setOrgUnit(orgUnitUid);
		dataValueSet.setCompleteData(getCompleteDate(new Date()));
		dataValueSet.setPeriod(getPeriod(LocalDate.now()));
		dataValueSet.setDataSet(dataSetId);
		
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			String json = ow.writeValueAsString(dataValueSet);
			log.error("Posting data..." + json);
			postDataToDhis2(json);
		}
		catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String getCompleteDate(Date completeDate) {
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(completeDate);
	}
	
	public static String getPeriod(LocalDate reportingPeriod) {
		if (reportingPeriod.getMonthValue() - 1 < 10) {
			return (reportingPeriod.getYear() + "0" + (reportingPeriod.getMonthValue() - 1));
		}
		return reportingPeriod.getYear() + "" + (reportingPeriod.getMonthValue() - 1);
	}
	
	/**
	 * Creates a JSON post request to a configured URL from "dhis2.postURL" global property.
	 * 
	 * @param jsonData The actual json to post
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws AuthenticationException
	 */
	public static CloseableHttpResponse postDataToDhis2(String jsonData) throws ClientProtocolException, IOException,
	        AuthenticationException {
		String DHIS2Username = Context.getAdministrationService().getGlobalProperty("reports.DHIS2AuthorizationUsername");
		String DHIS2Password = Context.getAdministrationService().getGlobalProperty("reports.DHIS2AuthorizationPassword");
		HttpPost httpPost = new HttpPost(Context.getAdministrationService().getGlobalProperty("reports.DHIS2PostURL"));
		Credentials DHIS2Credentials = new UsernamePasswordCredentials(DHIS2Username, DHIS2Password);
		Header authenticateScheme = new BasicScheme().authenticate(DHIS2Credentials, httpPost, new BasicHttpContext());
		httpPost.setEntity(new StringEntity(jsonData));
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		httpPost.setHeader("Authorization", authenticateScheme.getValue());
		
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response = client.execute(httpPost);
		client.close();
		return response;
	}
}
