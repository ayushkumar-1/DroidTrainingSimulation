package droid.stormtropper.beans;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class DroidInfo {

	private String droidName;
	/**
	 * training, readyForSyncUp, syncInProgress, syncUpCompleted
	 */
	private String trainingStatus;
	private List<TrainingFiles> file;
	private MultipartFile trainingLog;

	public DroidInfo(String name, String status, List<TrainingFiles> file, MultipartFile logFile) {
		super();
		this.droidName = name;
		this.trainingStatus = status;
		this.file = file;
		this.trainingLog = logFile;
	}

	public String getDroidName() {
		return droidName;
	}

	public void setDroidName(String droidName) {
		this.droidName = droidName;
	}

	public String getTrainingStatus() {
		return trainingStatus;
	}

	public void setTrainingStatus(String trainingStatus) {
		this.trainingStatus = trainingStatus;
	}

	public List<TrainingFiles> getFile() {
		return file;
	}

	public void setFile(List<TrainingFiles> file) {
		this.file = file;
	}

	public MultipartFile getTrainingLog() {
		return trainingLog;
	}

	public void setTrainingLog(MultipartFile trainingLog) {
		this.trainingLog = trainingLog;
	}

}
