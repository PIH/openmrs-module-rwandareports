/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.rwandareports.renderer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
//import org.openmrs.module.reporting.web.renderers.IndicatorReportWebRenderer;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;

/**
 * Renderer for Data Quality report
 */
@Handler
public class DataQualityWebRenderedForNCDandOncology extends AbstractRwandaWebRenderer {
	
	/**
	 * @see org.openmrs.report.ReportRenderer#getLabel()
	 */
	@Override
	public String getLabel() {
		return "DQ-Data Quality NCD/ONCOLOGY Report By Site";
	}
	
	/**
	 * @see org.openmrs.module.reporting.web.renderers.WebReportRenderer#getLinkUrl(org.openmrs.module.reporting.report.definition.ReportDefinition)
	 */
	@Override
	public String getLinkUrl(ReportDefinition arg0) {
		return "module/rwandareports/renderDataQualityDataSet.form";
	}
	
	public String getDataSetNameToCheck() {
		return "DQ-Data Quality NCD/ONCOLOGY Report By Site Data Set";
	}
	
}
