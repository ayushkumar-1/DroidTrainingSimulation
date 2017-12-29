package droid.stormtropper.beans;

import org.springframework.web.multipart.MultipartFile;

public class TrainingFiles {

	private String pathOfFile;
	private MultipartFile partFile;

	public String getPathOfFile() {
		return pathOfFile;
	}

	public void setPathOfFile(String pathOfFile) {
		this.pathOfFile = pathOfFile;
	}

	public MultipartFile getPartFile() {
		return partFile;
	}

	public void setPartFile(MultipartFile partFile) {
		this.partFile = partFile;
	}

}
