package galco.portal.exception;

import org.apache.log4j.Logger;

public class OtherException extends Exception {
	private static Logger log = Logger.getLogger(OtherException.class);

	public OtherException(String message) {
		super(message);
	}
}
