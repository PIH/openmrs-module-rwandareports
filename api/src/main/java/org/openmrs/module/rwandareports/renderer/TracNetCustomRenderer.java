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

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

/**
 * Custom renderer for TracNet Report
 */
@Handler
public class TracNetCustomRenderer extends AbstractRwandaWebRenderer {
	
	@Override
	public String getLabel() {
		return "TracNet Web Report";
	}
	
	@Override
	public String getLinkUrl(ReportDefinition arg0) {
		return "module/rwandareports/renderTracNetDataSet.form";
	}
	
	@Override
	public String getDataSetNameToCheck() {
		return "TracNet Report Data Set";
	}
}
