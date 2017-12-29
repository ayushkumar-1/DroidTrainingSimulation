package droid.stormtropper.train;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class ConfigFileLoader {

	private ConfigFileLoader() {
		// Empty Constructor
	}

	/**
	 * @return Properties
	 */
	public static PropertiesConfiguration loadAppConfig() {
		PropertiesConfiguration propertiesCnfg = null;
		try {
			propertiesCnfg = new PropertiesConfiguration(Constants.CONFIG_FILE_PATH);
		} catch (ConfigurationException exception) {
			throw new ExceptionHandler(Constants.ERR_MSG, exception);
		}
		return propertiesCnfg;
	}

}
