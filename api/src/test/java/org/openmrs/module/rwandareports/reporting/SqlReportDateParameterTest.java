/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.rwandareports.reporting;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetUtil;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.rwandareports.StandaloneContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Tests elements of the SetupAdultLateVisitAndCD4Report class
 */
@Ignore
public class SqlReportDateParameterTest extends StandaloneContextSensitiveTest {

    @Autowired @Qualifier("reportingDataSetDefinitionService")
    DataSetDefinitionService dataSetDefinitionService;

    @Autowired
    DbSessionFactory sessionFactory;

    String query = "select encounter_id, date_created from encounter where date_created < :date limit 10";

    @Test
    public void testWithSqlDataSetDefinition() throws Exception {
        SqlDataSetDefinition sqlDsd = new SqlDataSetDefinition();
        sqlDsd.addParameter(new Parameter("date", "Date", Date.class));
        sqlDsd.setSqlQuery(query);
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("date", new Date());
        DataSet dataSet = dataSetDefinitionService.evaluate(sqlDsd, context);
        DataSetUtil.printDataSet(dataSet, System.out);
    }

    @Test
    public void testWithPreparedStatement() throws Exception {
        String psQuery = query.replace(":date", "?");
        sessionFactory.getCurrentSession().doWork(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(psQuery)) {
                statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                ResultSetMetaData metadata = statement.getMetaData();
                for (int i=1; i<=metadata.getColumnCount(); i++) {
                    String columnName = metadata.getColumnLabel(i);
                    System.out.println(columnName);
                }
            }
        });
    }
}
