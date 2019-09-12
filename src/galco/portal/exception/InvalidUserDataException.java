package galco.portal.exception;

import org.apache.log4j.Logger;

public class InvalidUserDataException extends Exception {
	private static Logger log = Logger.getLogger(InvalidUserDataException.class);

	public InvalidUserDataException(String message) {
		super(message);
	}
}
