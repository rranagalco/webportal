package galco.portal.exception;

import org.apache.log4j.Logger;

public class UserExistsInGluuException extends Exception {
	private static Logger log = Logger.getLogger(UserExistsInGluuException.class);

	public UserExistsInGluuException(String message) {
		super(message);
	}

}
