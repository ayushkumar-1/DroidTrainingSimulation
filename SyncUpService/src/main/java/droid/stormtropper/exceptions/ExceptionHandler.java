package droid.stormtropper.exceptions;

public class ExceptionHandler extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	
	public ExceptionHandler(Exception exception){
		super(exception.getMessage());
	}
	
	public ExceptionHandler(String message, Exception exception){
		super( message + exception.getMessage());
	}
	
	public ExceptionHandler(String errMsg){
		super(errMsg);
	}
}
