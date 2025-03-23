/*
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

package org.openmrs.module.rwandareports.patientsummary;

import org.openmrs.Program;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation of ReportManager that provides some common method implementations
 */
public abstract class BasePatientSummaryManager implements PatientSummaryManager {
	
	@Override
	public String getName() {
		return translate("name");
	}
	
	@Override
	public String getDescription() {
		return translate("description");
	}
	
	@Override
	public List<Parameter> getParameters() {
		return new ArrayList<Parameter>();
	}
	
	@Override
	public List<Program> getRequiredPrograms() {
		return null;
	}
	
	@Override
	public String getRequiredPrivilege() {
		return null;
	}
	
	protected String translate(String code) {
		String messageCode = "rwandareports." + getKey() + "." + code;
		String translation = MessageUtil.translate(messageCode);
		if (messageCode.equals(translation)) {
			return messageCode;
		}
		return translation;
	}
	
	public <T extends Parameterizable> Mapped<T> map(T parameterizable, String mappings) {
		if (parameterizable == null) {
			throw new NullPointerException("Programming error: missing parameterizable");
		}
		if (mappings == null) {
			mappings = ""; // probably not necessary, just to be safe
		}
		return new Mapped<T>(parameterizable, ParameterizableUtil.createParameterMappings(mappings));
	}
	
}
