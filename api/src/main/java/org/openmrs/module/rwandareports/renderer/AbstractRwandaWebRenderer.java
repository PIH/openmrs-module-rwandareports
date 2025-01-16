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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.web.renderers.AbstractWebReportRenderer;
import org.openmrs.module.reporting.web.renderers.DefaultWebRenderer;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Renderer for Data Quality report
 */
public abstract class AbstractRwandaWebRenderer extends AbstractWebReportRenderer {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * This should contain the display name for the report output that the user will choose in the
	 * UI
	 */
	public abstract String getLabel();
	
	/**
	 * This should be set to the name of the dataset that a report must contain in order for this
	 * renderer to be enabled for this report.
	 */
	public abstract String getDataSetNameToCheck();
	
	@Override
	public boolean canRender(ReportDefinition reportDefinition) {
		return !getRenderingModes(reportDefinition).isEmpty();
		
	}
	
	/**
	 * @see org.openmrs.module.reporting.report.renderer.ReportRenderer#getRenderingModes(org.openmrs.module.reporting.report.definition.ReportDefinition)
	 */
	@Override
	public Collection<RenderingMode> getRenderingModes(ReportDefinition definition) {
		List<RenderingMode> ret = new ArrayList<RenderingMode>();
		for (Map.Entry<String, Mapped<? extends DataSetDefinition>> e : definition.getDataSetDefinitions().entrySet()) {
			String name = e.getKey();
			DataSetDefinition def = e.getValue().getParameterizable();
			if (getDataSetNameToCheck() != null && getDataSetNameToCheck().equals(def.getName())) {
				ret.add(new RenderingMode(this, this.getLabel(), name, Integer.MAX_VALUE - 5));
			}
		}
		return ret;
	}
}
