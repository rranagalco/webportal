package galco.portal.exception;

import org.apache.log4j.Logger;

public class FascorLockedRecordException extends Exception {
	private static Logger log = Logger.getLogger(GluuCreateFailureException.class);

	public FascorLockedRecordException(String message) {
		super(message);
	}
}
