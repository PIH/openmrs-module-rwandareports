package org.openmrs.module.rwandareports;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

import java.io.File;
import java.nio.file.Files;
import java.util.Properties;

@SkipBaseSetup
public abstract class StandaloneContextSensitiveTest extends BaseModuleContextSensitiveTest {

	private static final Properties props = new Properties();

	static {
		String serverId = System.getProperty("serverId");
		if (serverId == null) {
			throw new RuntimeException("serverId is required as a system property");
		}
		File homeDir = new File(System.getProperty("user.home"));
		File sdkDir = new File(homeDir, "openmrs");
		File serverDir = new File(sdkDir, serverId);
		File runtimePropertiesFile = new File(serverDir, "openmrs-runtime.properties");
		if (!runtimePropertiesFile.exists()) {
			throw new RuntimeException("No runtime properties file found at: " + runtimePropertiesFile.getAbsolutePath());
		}
		try {
			props.load(Files.newInputStream(runtimePropertiesFile.toPath()));
		}
		catch (Exception e) {
			throw new RuntimeException("Error loading properties from " + runtimePropertiesFile, e);
		}

		System.setProperty("databaseUrl", props.getProperty("connection.url"));
		System.setProperty("databaseUsername", props.getProperty("connection.username"));
		System.setProperty("databasePassword", props.getProperty("connection.password"));
		System.setProperty("databaseDriver", props.getProperty("connection.driver_class"));
		System.setProperty("databaseDialect", "org.hibernate.dialect.MySQLDialect");
		System.setProperty("useInMemoryDatabase", "false");
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
		return props;
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
