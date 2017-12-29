package droid.stormtropper.train;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class DataForceSyncUp {

	public static void initiateSyncUp(PropertiesConfiguration propertiesConf) {

		try {
			propertiesConf.setProperty("droid.training.status", "readyForSyncUp");
			propertiesConf.save();

			String url = propertiesConf.getProperty("droid.scan.droid1").toString();

			HttpURLConnection connect = (HttpURLConnection) new URL(url).openConnection();
			connect.setRequestMethod("GET");
			connect.setConnectTimeout(1000);
			// After timeout, the trainer goes back to main - doesn't wait for response.

		} catch (IOException | ConfigurationException e) {
			throw new ExceptionHandler(Constants.ERR_MSG, e);
		}
	}
}
