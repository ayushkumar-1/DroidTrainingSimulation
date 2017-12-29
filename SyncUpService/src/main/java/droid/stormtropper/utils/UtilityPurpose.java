package droid.stormtropper.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import droid.stormtropper.beans.DroidInfo;
import droid.stormtropper.beans.TrainingFiles;
import droid.stormtropper.exceptions.ExceptionHandler;

public class UtilityPurpose {
	
	public String initiateSyncUp(DroidInfo myDroidInfo, PropertiesConfiguration propertiesConf) {
		
		try {
			String url = propertiesConf.getProperty("droid.scan.droid2").toString()
					+"?droidStatus="+myDroidInfo.getTrainingStatus();

			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setAllowUserInteraction(false);
            connection.setRequestProperty("Content-Type", "application/json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(
            		connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();
            
            return response.toString();


		} catch (IOException e) {
			throw new ExceptionHandler(Constants.ERR_MSG, e);
		}
		
	}
	
	public void getListOfUpdatedFiles(List<TrainingFiles> listOfUpdatedFile, String logPath, PropertiesConfiguration propertiesConf) {
		
		//read through the log file
		BufferedReader br = null;
		FileReader fr = null;
		try{
		fr = new FileReader(logPath);
		br = new BufferedReader(fr);
		String sCurrentLine;
		
		while((sCurrentLine = br.readLine())!= null){
			//make multipart files
			TrainingFiles trFiles = new TrainingFiles();
			trFiles = createTrainingFileObjects(sCurrentLine, trFiles, propertiesConf);
			listOfUpdatedFile.add(trFiles);
		}
		}
		catch(Exception e){
			
		}
		
	}
	
private TrainingFiles createTrainingFileObjects(String sCurrentLine, TrainingFiles trFiles, PropertiesConfiguration propertiesConf) throws FileNotFoundException, IOException {
		
		String locationOfDroidCore = propertiesConf.getProperty("droid.path").toString();
		String[] loggedData = sCurrentLine.split(":");
		
			//Scenarios: deletedScenario, deletedStrategy, updated, newScenario, newStrategy
			if("deletedScenario".equals(loggedData[1]) || "deletedStrategy".equals(loggedData[1])){
				trFiles.setPathOfFile(loggedData[0]);
				trFiles.setPartFile(null);
			}else {
				trFiles.setPathOfFile(loggedData[0]);
				MultipartFile fileData = getMultipartFile(locationOfDroidCore+loggedData[0]);
				trFiles.setPartFile(fileData);
			}		
		return trFiles;
		
	}

public MultipartFile getMultipartFile(String path) throws FileNotFoundException, IOException {
	File file = new File(path);
	return new MockMultipartFile(file.getName(),
			new FileInputStream(file));
	
}

public void savePropertyConfig(PropertiesConfiguration propertiesConf) {
	
	try {
		propertiesConf.save();
	} catch (ConfigurationException e) {
		new ExceptionHandler(Constants.ERR_MSG, e);
	}
	
}

}
