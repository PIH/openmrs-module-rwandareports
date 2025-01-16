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
package org.openmrs.module.rwandareports.definition.evaluator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.openmrs.Drug;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.rwandareports.definition.DrugsActiveCohortDefinition;

/**
 * 
 */
@Handler(supports = { DrugsActiveCohortDefinition.class })
public class DrugsActiveCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	/**
	 * Default Constructor
	 */
	public DrugsActiveCohortDefinitionEvaluator() {
	}
	
	/**
	 * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		DrugsActiveCohortDefinition definition = (DrugsActiveCohortDefinition) cohortDefinition;
		
		List<Integer> drugIds = new ArrayList<Integer>();
		for (Drug drug : definition.getDrugs()) {
			drugIds.add(drug.getDrugId());
		}
		
		// Create SQL query
		SqlQueryBuilder sb = new SqlQueryBuilder();
		sb.append("select orders.patient_id ");
		sb.append("from drug, drug_order, orders ");
		sb.append("where orders.order_id = drug_order.order_id ");
		sb.append("and drug.drug_id = drug_order.drug_inventory_id ");
		if (drugIds != null && !drugIds.isEmpty()) {
			sb.append("and drug_order.drug_inventory_id in (:drugIds) ");
		}
		sb.append("and ifnull(orders.scheduled_date, orders.date_activated) is not null ");
		if (definition.getAsOfDate() != null) {
			sb.append("and ifnull(orders.scheduled_date, orders.date_activated) <= :asOfDate ");
			sb.append("and (");
			sb.append("  ifnull(orders.date_stopped, orders.auto_expire_date) is null or ");
			sb.append("  ifnull(orders.date_stopped, orders.auto_expire_date) > :asOfDate ");
			sb.append(") ");
		}
		sb.append("and drug.retired = false ");
		sb.append("and orders.voided = false ");
		sb.append("group by orders.patient_id ");
		
		sb.addParameter("drugIds", drugIds);
		sb.addParameter("asOfDate", definition.getAsOfDate());
		
		List<Integer> l = Context.getService(EvaluationService.class).evaluateToList(sb, Integer.class, context);
		if (context.getBaseCohort() != null) {
			l.retainAll(context.getBaseCohort().getMemberIds());
		}
		
		EvaluatedCohort ret = new EvaluatedCohort(cohortDefinition, context);
		ret.setMemberIds(new HashSet<Integer>(l));
		return ret;
	}
}
