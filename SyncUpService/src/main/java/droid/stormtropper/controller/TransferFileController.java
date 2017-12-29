package droid.stormtropper.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import droid.stormtropper.beans.DroidInfo;
import droid.stormtropper.beans.TrainingFiles;
import droid.stormtropper.exceptions.ExceptionHandler;
import droid.stormtropper.processor.FileDataSyncUp;
import droid.stormtropper.utils.ConfigFileLoader;
import droid.stormtropper.utils.Constants;
import droid.stormtropper.utils.UtilityPurpose;

@RestController
public class TransferFileController {

	private final static Logger LOGGER = Logger.getLogger(TransferFileController.class.getName());

	@Autowired
	FileDataSyncUp fileDataSyncUp;

	@Autowired
	UtilityPurpose utilityPurpose;

	String statusOfTraining = "training";
	String nameOfDroid = "Droid1";

	PropertiesConfiguration propertiesConf = ConfigFileLoader.loadAppConfig();

	/**
	 * This service call is made my the trainer once the training is completed Here
	 * the service call to other droids are made.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@RequestMapping(value = "/searchForSyncUp", method = RequestMethod.GET, headers = "Accept=application/json")
	public void scanDroidsForSyncUp(HttpServletRequest req, HttpServletResponse resp)
			throws FileNotFoundException, IOException {

		// converting log file into multipartfile
		String logPath = propertiesConf.getProperty("droid.training.log").toString();
		MultipartFile trainingLogFile = utilityPurpose.getMultipartFile(logPath);

		// getting all the updated/newlycreated/files as per log
		List<TrainingFiles> listOfUpdatedFile = new ArrayList<>();
		utilityPurpose.getListOfUpdatedFiles(listOfUpdatedFile, logPath, propertiesConf);

		DroidInfo myDroidInfo = new DroidInfo(nameOfDroid, statusOfTraining, listOfUpdatedFile, trainingLogFile);

		// Do the HTTP call to scan other droids for syncup
		// and redirect when get a response with ready for syncup
		String statusOfContactedDroid = utilityPurpose.initiateSyncUp(myDroidInfo, propertiesConf);
		if ("readyForSyncUp".equals(statusOfContactedDroid)) {
			//A droid is available for syncup, hence the status changed to syncUpInProgress
			propertiesConf.setProperty("droid.training.status", "syncUpInProgress");
			utilityPurpose.savePropertyConfig(propertiesConf);
			
			resp.sendRedirect("/syncUpEstablished");
		} else {
			// Logic for scans of other droids
		}

	}

	@RequestMapping(value = "/syncUpCheck", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody DroidInfo getSyncUpStatus(@RequestParam("droidStatus") String droidStatus)
			throws FileNotFoundException, IOException {

		// converting log file into multipartfile
		String logPath = propertiesConf.getProperty("droid.training.log").toString();
		MultipartFile trainingLogFile = utilityPurpose.getMultipartFile(logPath);

		// getting all the updated/newlycreated/files as per log
		List<TrainingFiles> listOfUpdatedFile = new ArrayList<>();
		utilityPurpose.getListOfUpdatedFiles(listOfUpdatedFile, logPath, propertiesConf);

		DroidInfo myDroidInfo = new DroidInfo(nameOfDroid, statusOfTraining, listOfUpdatedFile, trainingLogFile);

		// properties.setProperty("droid.trainning.status", "readyForSyncUp");
		String status = propertiesConf.getProperty("droid.trainning.status").toString();
		if (("readyForSyncUp".equals(status) || "syncUpCompleted".equals(status))
				&& "readyForSyncUp".equals(droidStatus)) {
			propertiesConf.setProperty("droid.training.status", "syncUpInProgress");
			utilityPurpose.savePropertyConfig(propertiesConf);
		}
		return myDroidInfo;
	}


	@RequestMapping(value = "/syncUpEstablished", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody DroidInfo syncUpStrategies(@RequestBody DroidInfo contactingDroidInfo)
			throws FileNotFoundException, IOException {

		// converting log file into multipartfile
		String logPath = propertiesConf.getProperty("droid.training.log").toString();
		MultipartFile trainingLogFile = utilityPurpose.getMultipartFile(logPath);

		List<TrainingFiles> listOfUpdatedFile = new ArrayList<>();
		utilityPurpose.getListOfUpdatedFiles(listOfUpdatedFile, logPath, propertiesConf);

		DroidInfo myDroidInfo = new DroidInfo(nameOfDroid, statusOfTraining, listOfUpdatedFile, trainingLogFile);

		// Logic to add data from file and do sync up
		fileDataSyncUp.syncUpStrategiesFiles(listOfUpdatedFile, propertiesConf);

		// add this to "syncUpCompleted" in the file
		propertiesConf.setProperty("droid.training.status", "syncUpCompleted");
		utilityPurpose.savePropertyConfig(propertiesConf);
		
		myDroidInfo.setTrainingStatus("syncUpCompleted");

		// when other droids want to syncUp with this droid
		// they need to check for either syncUp completed or ready for syncUp
		return myDroidInfo;
	}
}
