package galco.portal.exception;

import org.apache.log4j.Logger;

public class GluuCreateFailureException extends Exception {
	private static Logger log = Logger.getLogger(GluuCreateFailureException.class);

	public GluuCreateFailureException(String message) {
		super(message);
	}
}
