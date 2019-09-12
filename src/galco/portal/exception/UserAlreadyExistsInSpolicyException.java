package galco.portal.exception;

import org.apache.log4j.Logger;

public class UserAlreadyExistsInSpolicyException extends Exception {
	private static Logger log = Logger.getLogger(UserExistsInGluuException.class);

	public UserAlreadyExistsInSpolicyException(String message) {
		super(message);
	}

}
