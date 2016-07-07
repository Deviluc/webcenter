package core.exceptions;

public class NoElementFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2449492672406253807L;

	public NoElementFoundException() {
	}

	public NoElementFoundException(String message) {
		super(message);
	}

	public NoElementFoundException(Throwable cause) {
		super(cause);
	}

	public NoElementFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoElementFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
