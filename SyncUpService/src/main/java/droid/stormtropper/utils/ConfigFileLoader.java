package droid.stormtropper.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
			propertiesCnfg = new 
					PropertiesConfiguration(Constants.CONFIG_FILE_PATH);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
        return propertiesCnfg;
    }

}

