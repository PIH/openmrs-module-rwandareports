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
package org.openmrs.module.rwandareports;

import ch.vorburger.mariadb4j.DB;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Abstract class for testing Rwanda Reports
 */
public abstract class BaseRwandaReportsTest extends BaseModuleContextSensitiveTest {

	protected TestDataManager tdm;

	public BaseRwandaReportsTest() {
		super();
		tdm = new TestDataManager();
	}

	protected abstract void setupTestData();

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	@Before
	public void setupRwandaReports() throws Exception {
		authenticate();
		setupTestData();
	}

	@After
	public void teardownRwandaReports() throws Exception {
		// TODO: If needed, here is where we need to iterate over the data created in "tdm" and delete from DB
		// TODO: Though we should check to see if rolling back transactions will do this for us
	}

	@Override
	public Properties getRuntimeProperties() {
		Properties p = super.getRuntimeProperties();
		p.setProperty("junit.username", "admin");
		p.setProperty("junit.password", "Test1234");
		p.setProperty("connection.username", "root");
		p.setProperty("connection.password", "");
		p.setProperty("connection.url", "jdbc:mysql://localhost:3307/test?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8");
		p.setProperty("hibernate.show_sql", "false");
		return p;
	}

	public ReportDefinitionService getReportDefinitionService() {
		return Context.getService(ReportDefinitionService.class);
	}

	public static Location getLocation(String name) {
		return Context.getLocationService().getLocation(name);
	}

	public static void printReportData(ReportData data) {
		for (String dsName : data.getDataSets().keySet()) {
			System.out.println(dsName);
			System.out.println("------------------------");
			printDataSet(data.getDataSets().get(dsName));
			System.out.println("");
		}
	}

	public static void printDataSet(DataSet d) {

		Map<DataSetColumn, Integer> columnLengthMap = new LinkedHashMap<DataSetColumn, Integer>();
		for (DataSetColumn c : d.getMetaData().getColumns()) {
			columnLengthMap.put(c, c.toString().length());
		}
		for (DataSetRow r : d) {
			for (DataSetColumn c : r.getColumnValues().keySet()) {
				String val = ObjectUtil.nvlStr(r.getColumnValue(c), "");
				val = val.replace(System.getProperty("line.separator"), " ");
				if (columnLengthMap.get(c) < val.length()) {
					columnLengthMap.put(c, val.length());
				}
			}
		}

		StringBuilder output = new StringBuilder();
		for (Map.Entry<DataSetColumn, Integer> c : columnLengthMap.entrySet()) {
			StringBuilder n = new StringBuilder(c.getKey().toString());
			while (n.length() < c.getValue()) {
				n.append(" ");
			}
			output.append(n.toString() + "\t");
		}
		output.append("\n");
		for (Map.Entry<DataSetColumn, Integer> c : columnLengthMap.entrySet()) {
			StringBuilder n = new StringBuilder();
			while (n.length() < c.getValue()) {
				n.append("-");
			}
			output.append(n.toString() + "\t");
		}
		output.append("\n");
		for (Iterator<DataSetRow> i = d.iterator(); i.hasNext();) {
			DataSetRow r = i.next();
			for (Map.Entry<DataSetColumn, Integer> c : columnLengthMap.entrySet()) {
				String val = ObjectUtil.nvlStr(r.getColumnValue(c.getKey()), "");
				val = val.replace(System.getProperty("line.separator"), " ");
				StringBuilder n = new StringBuilder(val);
				while (n.length() < c.getValue()) {
					n.append(" ");
				}
				output.append(n + "\t");
			}
			output.append("\n");
		}
		System.out.println(output.toString());
	}
}