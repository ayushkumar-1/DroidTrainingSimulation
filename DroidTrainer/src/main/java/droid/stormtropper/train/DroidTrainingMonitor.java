package droid.stormtropper.train;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 * Training Simulation as a Daemon Service
 * @author Jedi AyushKumar
 *
 */
public class DroidTrainingMonitor {
	
	final static Logger LOG = Logger.getLogger(DroidTrainingMonitor.class);
	static PropertiesConfiguration propertiesConf = ConfigFileLoader.loadAppConfig();
	/**
	 * Monitor file changes after training
	 * assuming 10 mins of training
	 * @return
	 */
	public static void monitorTraining() {

		Map<String, Map<String, Long>> scenariosStrategiesMap = new HashMap<>();
		beforeTrainingStats(scenariosStrategiesMap);
		
		LOG.debug("TRAINING STARTS");
		trainingInProgress();
		LOG.debug("TRAINING OVER");
		
		StringBuilder logTrainingDetails = afterTrainingStats(scenariosStrategiesMap);
		
		LOG.debug("LOG training data");
		
		logTrainingData(logTrainingDetails);
		// Update properties file training status from InProgress to
		// ReadyForSyncUp
		changeTrainingStatus(propertiesConf, "readyForSyncUp");
		
		try {
			//the training completion time
			Long completionTimestamp = System.currentTimeMillis();
			propertiesConf.setProperty("droid.training.completeTime", completionTimestamp);
			propertiesConf.save();
		} catch (ConfigurationException e) {
			throw new ExceptionHandler(Constants.ERR_MSG, e);
		}
		
	}

	private static void changeTrainingStatus(PropertiesConfiguration propertiesConf, String status) {
	try {	
		propertiesConf.setProperty("droid.training.status", status);
		propertiesConf.save();
	} catch (ConfigurationException e) {
		throw new ExceptionHandler(Constants.ERR_MSG, e);
	}
		
	}

	private static void logTrainingData(StringBuilder logTrainingDetails) {
		FileWriter fw = null;
		BufferedWriter writer = null;
		try {
			fw = new FileWriter(new File("E:\\logTraining.txt"));
			writer = new BufferedWriter(fw);
			writer.write(logTrainingDetails.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				throw new ExceptionHandler(Constants.ERR_MSG, e);
			}
		}

		
	}

	private static StringBuilder afterTrainingStats(Map<String, Map<String, Long>> scenariosStrategiesMap) {
		StringBuilder logTrainingDetails = new StringBuilder();
		List<String> scenariosList = new ArrayList<>();
		File scenarios = new File(propertiesConf.getProperty("droid.path").toString());
		for (File scenario : scenarios.listFiles()) {
			scenariosList.add(scenario.getName());
			// For new scenario -->
			if (scenariosStrategiesMap.containsKey(scenario.getName())) {
				File strategies = new File(propertiesConf.getProperty("droid.path")
						+ "\\" + scenario.getName());
				for (File strategy : strategies.listFiles()) {
					if (!scenariosStrategiesMap.get(scenario.getName())
							.containsKey(strategy.getName())) {
						logTrainingDetails.append(scenario.getName())
								.append("\\").append(strategy.getName())
								.append(":newStrategy").append("\n");
					} else if (scenariosStrategiesMap.get(scenario.getName())
							.get(strategy.getName()) != strategy.lastModified()) {
						logTrainingDetails.append(scenario.getName())
								.append("\\").append(strategy.getName())
								.append(":updated").append("\n");
					}
				}
			} else {
				logTrainingDetails.append(
						scenario.getName() + ":" + "newScenario").append("\n");// signify
																				// copy
																				// complete
																				// tree
				logTrainingDetails = getNewDataFilesPath(scenario.getName());
			}

		}
		LOG.debug("ScenarioList: " + scenariosList);
		// Deleted scenario
		for (String scenario : scenariosStrategiesMap.keySet()) {
			if (!scenariosList.contains(scenario)) {
				logTrainingDetails.append(scenario + ":" + "deletedScenario")
						.append("\n");// signify
				// delete
				// complete
				// tree
			}
		}
		
		return logTrainingDetails;
	}

	private static void beforeTrainingStats(Map<String, Map<String, Long>> scenariosStrategiesMap) {
		
		// Scanning the folders of Scenarios in Droid1
				File scenarios = new File(propertiesConf.getProperty("droid.path").toString());
				// List<String> listScenarios = new ArrayList<>();
				// Scenario, Map<strategy,modifiedtime>
				for (File scenario : scenarios.listFiles()) {
					String scenarioName = scenario.getName();
					StringBuilder scenarioDir = new StringBuilder();
					scenarioDir.append(propertiesConf.getProperty("droid.path"))
							.append("\\").append(scenarioName);

					File strategies = new File(scenarioDir.toString());
					Map<String, Long> startegiesMap = new HashMap<>();
					for (File strategy : strategies.listFiles()) {
						startegiesMap.put(strategy.getName(), strategy.lastModified());
					}
					scenariosStrategiesMap.put(scenarioName, startegiesMap);
				}		
	}

	private static void trainingInProgress() {
		try {
			// ReadyForSyncUp
			changeTrainingStatus(propertiesConf, "trainingInProgress");
			Long durationOfTraining = Long.parseLong(propertiesConf.getProperty("droid.training.duration").toString());
			Thread.sleep(durationOfTraining);
			changeTrainingStatus(propertiesConf, "trainingCompleted");
		} catch (InterruptedException exception) {
			throw new ExceptionHandler(Constants.ERR_MSG, exception);
		}
		
	}
	
	private static StringBuilder getNewDataFilesPath(String scenario) {
		StringBuilder path = new StringBuilder();
		path.append(scenario + ":" + "newScenario").append("\n");
		String scenarioPath = propertiesConf.getProperty("droid.path") + "\\"
				+ scenario;

		File folder = new File(scenarioPath);
		for (File file : folder.listFiles()) {
			path.append(scenario).append("\\").append(file.getName())
					.append(":").append("newStrategy").append("\n");
		}

		return path;
	}

}
