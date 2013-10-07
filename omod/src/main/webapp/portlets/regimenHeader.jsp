<%@ include file="/WEB-INF/template/include.jsp" %>
<jsp:useBean id="now" class="java.util.Date" />
<script type="text/javascript">

	jQuery(document).ready(function() {
		
		jQuery('#availableCycles').click(function(){ 
			jQuery('#treatmentPlanDialog').dialog('open');
		});

		jQuery('#treatmentPlanDialog').dialog({
			position: 'middle',
			autoOpen: false,
			modal: true,
			title: '<spring:message code="rwandareports.printTreatmentAdminPlan" javaScriptEscape="true"/>',
			width: '50%',
			zIndex: 100,
			buttons: { 'Print': function() { printTreatmentPlan(); },
					   '<spring:message code="general.cancel"/>': function() { jQuery(this).dialog("close"); }
			}
		});	
		
		jQuery('#availableCyclesReadOnly').click(function(){ 
			jQuery('#treatmentPlanReadOnlyDialog').dialog('open');
		});

		jQuery('#treatmentPlanReadOnlyDialog').dialog({
			position: 'middle',
			autoOpen: false,
			modal: true,
			title: '<spring:message code="rwandareports.printTreatmentAdminPlanReadOnly" javaScriptEscape="true"/>',
			width: '50%',
			zIndex: 100,
			buttons: { 'Print': function() { printTreatmentPlanReadOnly(); },
					   '<spring:message code="general.cancel"/>': function() { jQuery(this).dialog("close"); }
			}
		});	
		
		jQuery('#summary').click(function(){ 
			jQuery('#treatmentPlanSummaryDialog').dialog('open');
		});

		jQuery('#treatmentPlanSummaryDialog').dialog({
			position: 'middle',
			autoOpen: false,
			modal: true,
			title: '<spring:message code="rwandareports.printTreatmentAdminPlanSummary" javaScriptEscape="true"/>',
			width: '50%',
			zIndex: 100,
			buttons: { 'Print': function() { printTreatmentSummaryPlan(); },
					   '<spring:message code="general.cancel"/>': function() { jQuery(this).dialog("close"); }
			}
		});	
	});
	
	function printTreatmentPlan()
	{	
		jQuery('#treatmentPlan').submit();
		jQuery('#treatmentPlanDialog').dialog("close");
	}

	function printTreatmentPlanReadOnly()
	{	
		jQuery('#treatmentPlanReadOnly').submit();
		jQuery('#treatmentPlanDialogReadOnly').dialog("close");
	}
	
	function printTreatmentSummaryPlan()
	{
		jQuery('#printArea').jqprint();
		jQuery('#treatmentPlanSummaryDialog').dialog("close");
	}
	
</script>
	
<div>
	<c:if test="${!empty model.regimens}">
		<table>
			<tr>
				<td>
					<div  id="availableCycles">
						<input type="button" id="printPlan"  value="<spring:message code="rwandareports.printTreatmentAdminPlan" />"/>
					</div>
				</td> 
				<td>
					<div  id="availableCyclesReadOnly">
						<input type="button" id="printPlanReadOnly"  value="<spring:message code="rwandareports.printTreatmentAdminPlanReadOnly" />"/>
					</div>
				</td> 
				<td>
					<div  id="summary">
						<input type="button" id="printPlanSummary"  value="<spring:message code="rwandareports.printTreatmentAdminPlanSummary" />"/>
					</div>
				</td>
			</tr>
		</table>
	</c:if>	
</div>
					
<div id="treatmentPlanDialog">	
	<div class="box">
		<form id="treatmentPlan" name="treatmentPlan" method="post" action="${pageContext.request.contextPath}/module/rwandaReports/printReportAndRegister.form">
			<input type="hidden" name="patientId" value="${model.patient.patientId}">
			<input type="hidden" name="report" value="ONC-Chemotherapy Treatment Administration Plan">
			<input type="hidden" name="parameters" value="patientId,regimenId">
			<input type="hidden" name="returnPage" value="/module/pihrwanda/apps/oncology/patientDashboard.form?patientId=${model.patient.patientId}"/>	
			<select name="regimenId" id="regimenId">
				<c:forEach items="${model.regimens}" var="drugGroup">
					<c:choose>
						<c:when test="${fn:length(drugGroup.startDates) > 1 }">
							<c:forEach items="${drugGroup.startDates }" var="startDate">
								<option value="${drugGroup.drugRegimen.id}:${startDate.startDay}"><spring:message code="orderextension.regimen.currentCycleNumber" /> <c:out value="${drugGroup.drugRegimen.cycleNumber}"/> <spring:message code="general.of" /> <c:out value="${drugGroup.drugRegimen.orderSet.name}"/> <spring:message code="rwandareports.day"/>: <c:out value="${startDate.startDay + 1}"/> <spring:message code="general.dateStart"/>: <openmrs:formatDate date="${startDate.startDate}" type="medium" /></option></option>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<option value="${drugGroup.drugRegimen.id}"><spring:message code="orderextension.regimen.currentCycleNumber" /> <c:out value="${drugGroup.drugRegimen.cycleNumber}"/> <spring:message code="general.of" /> <c:out value="${drugGroup.drugRegimen.orderSet.name}"/> <spring:message code="general.dateStart"/>: <openmrs:formatDate date="${drugGroup.drugRegimen.firstDrugOrderStartDate}" type="medium" /></option>
						</c:otherwise>
					</c:choose>	
				</c:forEach>								
			</select>
		</form>
	</div>
</div>
					
<div id="treatmentPlanReadOnlyDialog">	
	<div class="box">
		<form id="treatmentPlanReadOnly" name="treatmentPlanReadOnly" method="post" action="${pageContext.request.contextPath}/module/rwandaReports/printReport.form">
			<input type="hidden" name="patientId" value="${model.patient.patientId}">
			<input type="hidden" name="report" value="ONC-Chemotherapy Treatment Administration Plan">
			<input type="hidden" name="parameters" value="patientId,regimenId">
			<input type="hidden" name="returnPage" value="/module/pihrwanda/apps/oncology/patientDashboard.form?patientId=${model.patient.patientId}"/>	
			<select name="regimenId" id="regimenId">
				<c:forEach items="${model.regimens}" var="drugGroup">
					<c:choose>
						<c:when test="${fn:length(drugGroup.startDates) > 1 }">
							<c:forEach items="${drugGroup.startDates }" var="startDate">
								<option value="${drugGroup.drugRegimen.id}:${startDate.startDay}"><spring:message code="orderextension.regimen.currentCycleNumber" /> <c:out value="${drugGroup.drugRegimen.cycleNumber}"/> <spring:message code="general.of" /> <c:out value="${drugGroup.drugRegimen.orderSet.name}"/> <spring:message code="rwandareports.day"/>: <c:out value="${startDate.startDay + 1}"/> <spring:message code="general.dateStart"/>: <openmrs:formatDate date="${startDate.startDate}" type="medium" /></option></option>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<option value="${drugGroup.drugRegimen.id}"><spring:message code="orderextension.regimen.currentCycleNumber" /> <c:out value="${drugGroup.drugRegimen.cycleNumber}"/> <spring:message code="general.of" /> <c:out value="${drugGroup.drugRegimen.orderSet.name}"/> <spring:message code="general.dateStart"/>: <openmrs:formatDate date="${drugGroup.drugRegimen.firstDrugOrderStartDate}" type="medium" /></option>
						</c:otherwise>
					</c:choose>	
				</c:forEach>								
			</select>
		</form>
	</div>
</div>


<div id="treatmentPlanSummaryDialog">	
	<div class="box">
		<table id="printArea">
			<tr>
				<th align="left"><c:out value="${model.patient.givenName}"/> <c:out value="${model.patient.familyName}"/> </th><th align="right"> <spring:message code="rwandareports.printedOn"/> <openmrs:formatDate date="${now}" type="medium" /></th>
			</tr>
			<tr>
				<th align="left"><spring:message code="rwandareports.printTreatmentSummary"/> </th>
			</tr>
		<c:forEach items="${model.allRegimens}" var="drugGroup">
			<c:choose>
				<c:when test="${fn:length(drugGroup.startDates) > 1 }">
					<c:forEach items="${drugGroup.startDates }" var="startDate">
						<tr>
							<td>
								<spring:message code="orderextension.regimen.currentCycleNumber" /> <c:out value="${drugGroup.drugRegimen.cycleNumber}"/> <spring:message code="general.of" /> <c:out value="${drugGroup.drugRegimen.orderSet.name}"/>  <spring:message code="rwandareports.day"/>: <c:out value="${startDate.startDay + 1}"/> <spring:message code="general.dateStart"/>: <openmrs:formatDate date="${startDate.startDate}" type="medium" />
							</td>
						</tr>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<tr>
						<td>
							<option value="${drugGroup.drugRegimen.id}"><spring:message code="orderextension.regimen.currentCycleNumber" /> <c:out value="${drugGroup.drugRegimen.cycleNumber}"/> <spring:message code="general.of" /> <c:out value="${drugGroup.drugRegimen.orderSet.name}"/> <spring:message code="general.dateStart"/>: <openmrs:formatDate date="${drugGroup.drugRegimen.firstDrugOrderStartDate}" type="medium" /></option>
						</td>
					</tr>
				</c:otherwise>
			</c:choose>	
		</c:forEach>
		</table>								
	</div>
</div>
