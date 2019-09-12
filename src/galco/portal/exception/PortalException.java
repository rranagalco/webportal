package galco.portal.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import galco.portal.db.DBConnector;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.UserSession;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ThrowableRenderer;

public class PortalException extends Throwable {
	private static Logger log = Logger.getLogger(PortalException.class);

	public static final int SEVERITY_LEVEL_1 = 1;
	public static final int SEVERITY_LEVEL_2 = 2;
	public static final int SEVERITY_LEVEL_3 = 3;
	
	public static int INCIDENT_NUMBER = 1;

	private Exception e;
	private int severety;

    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------

	public PortalException(Exception e, int severety) {
		this.e = e;
		this.severety = severety;
	}

    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------

	public void handleException(String formFunction, DBConnector dbConnector, HttpServletRequest request, HttpServletResponse response) {
		UserSession userSession = null;
		try {
			userSession = UserSession.getUserSession(request, dbConnector, response);
		} catch (PortalException e1) {
			// log.error("Error occurred while getting UserSession in handleException.", e);
			log.error("Error occurred while getting UserSession in handleException.");
		}
		
		if (dbConnector != null) {
			try {
				dbConnector.closeConnections();
			} catch (PortalException e1) {
				log.error("Exception occurred while trying to close DB connections. " + e1.getMessage());
			}				
		}		
		
		// long incidentNumber = 1000000 + ((long) (Math.random() * 1000000));
		int incidentNumber = INCIDENT_NUMBER;
		INCIDENT_NUMBER++;

		log.error("Incident Number : " + incidentNumber);
		log.error("Severity        : " + severety);
		if (userSession != null) {
			log.error("Cust_num        : " + userSession.getCust_num());			
			log.error("Cont_no         : " + userSession.getCont_no());			
			log.error("Username        : " + userSession.getUsername());			
			log.error("Email           : " + userSession.getEmail());			
			log.error("formFunction    : " + formFunction);			
		}
		log.error("Error occurred.", e);
		
		try {
			// request.setAttribute("incidentNumber", incidentNumber);
			// Utils.forwardToJSP(request, response, "SevereError.jsp", null);

			String eMailText;
			if (userSession != null) {
				eMailText = "Incident Number : " + incidentNumber + "\n" +
					    	"Severity        : " + severety + "\n" +
							"Cust_num        : " + userSession.getCust_num() + "\n" +			
							"Cont_no         : " + userSession.getCont_no() + "\n" +			
							"Username        : " + userSession.getUsername() + "\n" +			
							"Email           : " + userSession.getEmail() + "\n" +			
							"formFunction    : " + formFunction + "\n\n\n";			
			} else {
				eMailText = "Incident Number : " + incidentNumber + "\n" +
					    	"Severity        : " + severety + "\n" +
							"formFunction    : " + formFunction + "\n\n\n";			
			}

			
			// Utils.sendMail("sati@galco.com,ITSupport@galco.com", "WebPortal@galco.com", "Exception Occured, Incident Number: " + incidentNumber, eMailText + "\n\n\n" + ExceptionUtils.getStackTrace(e));
			Utils.sendMail("sati@galco.com,ynguyen@galco.com", "WebPortal@galco.com", "Exception Occured, Incident Number: " + incidentNumber, eMailText + "\n\n\n" + ExceptionUtils.getStackTrace(e));
			
			
			// Utils.sendMail("sati@galco.com,WebPortal@galco.com", "WebPortal@galco.com", "Exception Occured, Incident Number: " + incidentNumber, eMailText + "\n\n\n" + ExceptionUtils.getStackTrace(e));
			// Utils.sendMail("ynguyen@galco.com", "WebPortal@galco.com", "Exception Occured, Incident Number: " + incidentNumber, eMailText + "\n\n\n" + ExceptionUtils.getStackTrace(e));
			

			if (request.getSession(false) != null) {
		    	request.getSession(false).invalidate();
		    }
			request.getSession(true).setAttribute("incidentNumber", "" + incidentNumber);
			
	        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	        response.setHeader("Location", "/portal/controller?formFunction=SevereError");
		// } catch (PortalException e) {
		} catch (Exception e) {
			log.error("Error occurred while trying to redirect to SevereError.jsp.", e);
		}
	}

    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------

	public Exception getE() {
		return e;
	}

	public void setE(Exception e) {
		this.e = e;
	}

	public int getSeverety() {
		return severety;
	}

	public void setSeverety(int severety) {
		this.severety = severety;
	}
}
