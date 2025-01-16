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
package org.openmrs.module.rwandareports.web.controller;

import java.util.List;

import org.openmrs.module.orderextension.DrugRegimen;
import org.springframework.stereotype.Controller;

/**
 * The main controller.
 */
@Controller
public class RegimenDTO {
	
	private List<StartDateDTO> startDates;
	
	private DrugRegimen drugRegimen;
	
	public List<StartDateDTO> getStartDates() {
		return startDates;
	}
	
	public void setStartDates(List<StartDateDTO> startDates) {
		this.startDates = startDates;
	}
	
	public DrugRegimen getDrugRegimen() {
		return drugRegimen;
	}
	
	public void setDrugRegimen(DrugRegimen drugRegimen) {
		this.drugRegimen = drugRegimen;
	}
}
