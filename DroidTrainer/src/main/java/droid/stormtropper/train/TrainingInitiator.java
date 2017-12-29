package droid.stormtropper.train;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 * Daemon Service to monitor training of droid
 * @author Jedi AyushKumar
 *
 */
public class TrainingInitiator {

	final static Logger LOG = Logger.getLogger(TrainingInitiator.class);

	static PropertiesConfiguration propertiesConf = ConfigFileLoader.loadAppConfig();

	public static void main(String[] args) {

		LOG.debug("Droid Path: " + propertiesConf.getProperty("droid.path"));
		// Training start
		// The daemon service keeps running until the stop file is encountered
		String filePath = propertiesConf.getProperty("droid.trainingStopFile.location").toString();
		File stopFile = new File(filePath);
		boolean stopFileExists = false;
		try {
			do {

				if (stopFile.exists()) {
					stopFileExists = true;
					LOG.debug("Stop Training Command Initiated. Stopping Training");
				}
				// call method to read the data
				LOG.debug("Hello Tracking File: " + System.currentTimeMillis());
				// start Training monitor
				String currentDroidStatus = propertiesConf.getProperty("droid.training.status").toString();
				String lastTrainingCompleteTime = propertiesConf.getProperty("droid.training.completeTime").toString();
				//For first time droid training
				Long durationSinceLastTraining = 0L;
				if(null != lastTrainingCompleteTime || lastTrainingCompleteTime.isEmpty()) {
				durationSinceLastTraining = System.currentTimeMillis() - Long.parseLong(lastTrainingCompleteTime);
				}
				// After 10 mins, restart the training in case of droidstatus =
				// completed/notstarted or first time trainee
				if (("syncUpCompleted".equals(currentDroidStatus) || "trainingNotStarted".equals(currentDroidStatus))
						&& (durationSinceLastTraining > 600000 || durationSinceLastTraining == 0L)) {
					DroidTrainingMonitor.monitorTraining();
				}
				// Training Completed
				// Scan for sync up with other droids (in readyForSyncUp status), only when status = ready for sync up
				if ("readyForSyncUp".equals(currentDroidStatus)) {
					DataForceSyncUp.initiateSyncUp(propertiesConf);
				}

			} while (!stopFileExists);
		} catch (Exception e1) {
			throw new ExceptionHandler(Constants.ERR_MSG, e1);
		} finally {
			if (stopFile.exists()) {
				LOG.debug("Removing stopTraining file. Manually Run the jar to restart the daemon service");
				stopFile.delete();
			}
		}
	}
}
