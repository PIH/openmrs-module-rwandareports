package org.openmrs.module.rwandareports;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

import java.io.FileInputStream;
import java.util.Properties;

@SkipBaseSetup
public abstract class StandaloneContextSensitiveTest extends BaseModuleContextSensitiveTest {

	static Properties props = null;

	static {
		String propFile = System.getProperty("runtime-properties-file");
		if (propFile != null) {
			props = new Properties();
			try {
				props.load(new FileInputStream(propFile));
			} catch (Exception e) {
				System.out.println("Error loading properties from " + propFile + ": " + e.getMessage());
			}
		}

		if (props != null && !props.isEmpty()) {
			System.setProperty("databaseUrl", props.getProperty("connection.url"));
			System.setProperty("databaseUsername", props.getProperty("connection.username"));
			System.setProperty("databasePassword", props.getProperty("connection.password"));
			System.setProperty("databaseDriver", props.getProperty("connection.driver_class"));
			System.setProperty("databaseDialect", "org.hibernate.dialect.MySQLDialect");
			System.setProperty("useInMemoryDatabase", "false");
		}
	}

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	/**
	 * @return MS Note: use port 3306 as standard, 5538 for sandbox 5.5 mysql environment
	 */
	@Override
	public Properties getRuntimeProperties() {
		Properties p = super.getRuntimeProperties();
		p.putAll(props);
		return p;
	}

    @Before
	public void setupForTest() throws Exception {
        if (!Context.isSessionOpen()) {
            Context.openSession();
        }
        Context.clearSession();
        authenticate();
    }

	@Override
	public void deleteAllData() {
	}
}
