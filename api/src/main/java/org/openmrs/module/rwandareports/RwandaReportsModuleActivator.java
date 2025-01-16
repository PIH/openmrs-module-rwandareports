/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.rwandareports;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.mambacore.api.FlattenDatabaseService;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class RwandaReportsModuleActivator extends BaseModuleActivator {

    private static Log log = LogFactory.getLog(RwandaReportsModuleActivator.class);

    @Override
    public void started() {
        log.info("Started Rwanda Report Module Config");
        log.info("MambaETL Deploying scripts...");
        Context.getService(FlattenDatabaseService.class).setupEtl();
    }

    @Override
    public void stopped() {
        log.info("Stopped Rwanda Report Module");
        Context.getService(FlattenDatabaseService.class).shutdownEtlThread();
    }
}
