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
import java.io.Writer;
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
import org.openmrs.module.reporting.report.renderer.SimpleHtmlReportRenderer;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;

/**
 * Renderer for Data Quality report
 */
@Handler
public class CalendarWebRenderer extends SimpleHtmlReportRenderer implements WebReportRenderer {
	
	/**
	 * @see org.openmrs.report.ReportRenderer#getLabel()
	 */
	@Override
	public String getLabel() {
		return "Calendar Web Renderer";
	}
	
	/**
	 * @see org.openmrs.module.reporting.web.renderers.WebReportRenderer#getLinkUrl(org.openmrs.module.reporting.report.definition.ReportDefinition)
	 */
	@Override
	public String getLinkUrl(ReportDefinition reportDefinition) {
		return "module/rwandareports/renderCalendarWebRenderer.form";
	}
	
	@Override
	public boolean canRender(ReportDefinition reportDefinition) {
		
		if (reportDefinition.getName().contains("Appointment")) {
			return true;
		}
		return false;
	}
	
	public Collection<RenderingMode> getRenderingModes(ReportDefinition definition) {
		List<RenderingMode> ret = new ArrayList<RenderingMode>();
		if (definition.getName().contains("Appointment")) {
			ret.add(new RenderingMode(this, this.getLabel(), this.getLabel(), Integer.MAX_VALUE - 5));
		}
		return ret;
	}
	
	public List<String> getDisplayColumns() {
		return null;
	}
	
	public void render(ReportData results, String argument, OutputStream out) throws IOException, RenderingException {
	}
	
	public void render(ReportData reportData, OutputStream out) throws IOException, RenderingException {
	}
	
	public void render(ReportData reportData, Writer writer) throws IOException, RenderingException {
	}
	
	public void render(ReportData reportData, String argument, Writer writer) throws IOException, RenderingException {
	}
	
	public void setDisplayColumns(List<String> displayColumns) {
	}
	
}
