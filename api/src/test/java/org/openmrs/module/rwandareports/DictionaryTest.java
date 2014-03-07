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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests for {@link Dictionary}
 */
public class DictionaryTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	@Test
	public void integration() {
		new Dictionary();
	}

	/**
	 * @see Dictionary#getConcept(String)
	 */
	@Test
	public void getConcept_shouldFetchByMappingOrUuid() {
		// Check lookup by UUID
		Concept adultHivProgramConcept = Context.getConceptService().getConceptByUuid(Dictionary.ADULT_HIV_PROGRAM);
		Concept fetched = Dictionary.getConcept(Dictionary.ADULT_HIV_PROGRAM);
		Assert.assertThat(fetched, is(adultHivProgramConcept));
	}

	/**
	 * @see Dictionary#getConcept(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getConcept_shouldThrowExceptionForNonExistent() {
		Dictionary.getConcept("PIH:XXXXXXXXXXXXXXX");
	}

	/**
	 * @see Dictionary#getConcepts(String...)
	 */
	@Test
	public void getConcepts_shouldFetchByMappingOrUuid() {
		// Check lookup by UUID
		Concept adultHivProgramConcept = Context.getConceptService().getConceptByUuid(Dictionary.ADULT_HIV_PROGRAM);
		Concept pediHivProgramConcept = Context.getConceptService().getConceptByUuid(Dictionary.PEDI_HIV_PROGRAM);
		Assert.assertThat(Dictionary.getConcepts(Dictionary.ADULT_HIV_PROGRAM, Dictionary.PEDI_HIV_PROGRAM), contains(adultHivProgramConcept, pediHivProgramConcept));
	}

	/**
	 * @see Dictionary#getConcepts(String...)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getConcepts_shouldThrowExceptionForNonExistent() {
		Dictionary.getConcepts(Dictionary.ADULT_HIV_PROGRAM, "PIH:XXXXXXXXXXXXXXX");
	}
}