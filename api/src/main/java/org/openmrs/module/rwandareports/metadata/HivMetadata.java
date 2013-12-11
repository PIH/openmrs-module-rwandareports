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
package org.openmrs.module.rwandareports.metadata;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.rwandareports.Dictionary;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.*;



/**
 * HIV metadata bundle
 */
@Component
@Requires({ CommonMetadata.class })
public class HivMetadata extends AbstractMetadataBundle {

	public static final class _Program {
		public final static String ADULT_HIV_PROGRAM = "cf7c0e30-2700-102b-80cb-0017a47871b2"; 
		public final static String PEDI_HIV_PROGRAM = "cf7c16e6-2700-102b-80cb-0017a47871b2";
		public final static String PMTCT_PREGNANCY_PROGRAM = "cf7c13da-2700-102b-80cb-0017a47871b2";
		public final static String PMTCT_COMBINED_MOTHER_PROGRAM = "2f7506b4-21e6-445d-ba73-2c782a6ecbee";
	}

	/**
     * @see org.openmrs.module.metadatadeploy.bundle.MetadataBundle#install()
     */
    @Override
    public void install() {
    	
    	install(program("Adult HIV program", "PIH Adult HIV program", Dictionary.ADULT_HIV_PROGRAM, _Program.ADULT_HIV_PROGRAM));
    	install(program("Pedi HIV program", "PIH Pediatric HIV program", Dictionary.PEDI_HIV_PROGRAM, _Program.PEDI_HIV_PROGRAM));
    	install(program("PMTCT Pregnancy program", "PIH PMTCT Pregnancy program", Dictionary.PMTCT_PREGNANCY_PROGRAM, _Program.PMTCT_PREGNANCY_PROGRAM));
    	install(program("PMTCT Combined Mother program", "PIH PMTCT Combined Mother program", Dictionary.PMTCT_COMBINED_MOTHER_PROGRAM, _Program.PMTCT_COMBINED_MOTHER_PROGRAM));
	    
    }

}
