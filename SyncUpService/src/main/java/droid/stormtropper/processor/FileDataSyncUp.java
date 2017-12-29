package droid.stormtropper.processor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.web.multipart.MultipartFile;

import droid.stormtropper.beans.DroidInfo;
import droid.stormtropper.beans.TrainingFiles;
import droid.stormtropper.exceptions.ExceptionHandler;
import droid.stormtropper.utils.Constants;

public class FileDataSyncUp {

	/**
	 * Based on training log --> 
	 * if new scenario added then 
	 * logic gets established accordingly
	 * 
	 * @param listOfUpdatedFile
	 * @param propertiesConf 
	 * @throws IOException
	 */
	PropertiesConfiguration propertiesConf = null;
	
	public void syncUpStrategiesFiles(List<TrainingFiles> listOfUpdatedFile, PropertiesConfiguration propertiesConfig) throws IOException {
		propertiesConf = propertiesConfig;
		String rootDirectory = propertiesConf.getProperty("droid.path").toString();
		for(int i = 0; i < listOfUpdatedFile.size(); i++){
			StringBuilder pathOfFile = new StringBuilder();
			pathOfFile.append(rootDirectory).append(listOfUpdatedFile.get(i).getPathOfFile());
			File f = new File(pathOfFile.toString());
			//Check for path, if file doesn't exist create and add the data
			if(null != listOfUpdatedFile.get(i).getPartFile()) {
			
			dataSyncBasedOnFileStructure(f, listOfUpdatedFile.get(i));
			}else if(f.exists()){
				//delete the file or folder
				f.delete();
			}
		}

		
		
		
		
		
		
	}

	private void dataSyncBasedOnFileStructure(File f, TrainingFiles trainingFiles) throws IOException {
		
		if(!f.exists() && f.isDirectory()) {
			f.mkdir();
		}else if(!f.exists() && f.isFile()) {
			f.createNewFile(); //with new strategies
			//Now update as if already existing file getting updated
			updateExistingStrategy(trainingFiles.getPathOfFile(), trainingFiles.getPartFile());
		}else{
			//update the existing strategies
			updateExistingStrategy(trainingFiles.getPathOfFile(), trainingFiles.getPartFile());
		}
		
	}
	
	private void updateExistingStrategy(String path, MultipartFile updatedFile) {
		String pathOfUpdatedFile = propertiesConf.getProperty("droid.path").toString()+path;
		byte b = '\n';
		byte[] existingBytes;
		try {
			existingBytes = updatedFile.getBytes();

		FileOutputStream fos = new FileOutputStream(pathOfUpdatedFile, true);
        BufferedOutputStream stream = 
                new BufferedOutputStream(fos);
        stream.write(b);
        stream.write(existingBytes);
        stream.close();
		} catch (IOException e) {
			new ExceptionHandler(Constants.ERR_MSG, e);
		}
	}
}

