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

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.module.rwandareports.util.MetadataLookup;

/**
 * Dictionary for concepts used by rwandareports
 */
public class Dictionary {
	
	/**
	 * Gets a concept by an identifier (mapping or UUID)
	 * 
	 * @param identifier the identifier
	 * @return the concept
	 * @throws IllegalArgumentException if the concept could not be found
	 */
	public static Concept getConcept(String identifier) {
		return MetadataLookup.getConcept(identifier);
	}
	
	/**
	 * Convenience method to fetch a list of concepts
	 * 
	 * @param identifiers the concept identifiers
	 * @return the concepts
	 * @throws IllegalArgumentException if a concept could not be found
	 */
	public static List<Concept> getConcepts(String... identifiers) {
		List<Concept> concepts = new ArrayList<Concept>();
		for (String identifier : identifiers) {
			concepts.add(getConcept(identifier));
		}
		return concepts;
	}
	
	// Concept identifiers (A-Z)
	public static final String ADULT_HIV_PROGRAM = "372a7cd8-7dca-4032-a094-6afcce5c8559";
	
	public static final String PEDI_HIV_PROGRAM = "3ce67698-26fe-102b-80cb-0017a47871b2";
	
	public static final String PMTCT_PREGNANCY_PROGRAM = "3cdc8138-26fe-102b-80cb-0017a47871b2";
	
	public static final String PMTCT_COMBINED_MOTHER_PROGRAM = "b00f1d61-a37c-451c-ac7f-c5fc0f231091";
}
