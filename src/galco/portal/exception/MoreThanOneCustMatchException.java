package galco.portal.exception;

import org.apache.log4j.Logger;

public class MoreThanOneCustMatchException extends Exception {
	private static Logger log = Logger.getLogger(MoreThanOneCustMatchException.class);

	public MoreThanOneCustMatchException(String message) {
		super(message);
	}
}
