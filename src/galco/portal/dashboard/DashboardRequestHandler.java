package galco.portal.dashboard;

import galco.portal.db.DBConnector;
import galco.portal.exception.GluuCreateFailureException;
import galco.portal.exception.InvalidUserDataException;
import galco.portal.exception.MoreThanOneCustMatchException;
import galco.portal.exception.PortalException;
import galco.portal.exception.UserAlreadyExistsInSpolicyException;
import galco.portal.exception.UserExistsInGluuException;
import galco.portal.user.GluuUser;
import galco.portal.user.User;
import galco.portal.user.signup.UserSignUpData;
import galco.portal.user.signup.UserSignUpHandler;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.Dashboard;
import galco.portal.wds.dao.PortalLog;
import galco.portal.wds.dao.Spolicy;
import galco.portal.wds.dao.UserSession;
import galco.portal.wds.matcher.CustNumBasedExistingContactMatcher;
import galco.portal.wds.matcher.CustNumBasedMatcherResult;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.captcha.Captcha;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class DashboardRequestHandler {
	private static Logger log = Logger.getLogger(UserSignUpHandler.class);

	public static final long FORGOT_PASSWORD_DURATION = 7 * 24 * 3600 * 1000;

	public static void dashboardSearch(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
        String co_name_l = request.getParameter("co_name_l");		
		if (co_name_l == null) {
		    request.setAttribute("messageToUser", null);
		    
		    // 

            String cust_num = request.getParameter("cust_num");
            String name = request.getParameter("name");
            String co_name_f = request.getParameter("co_name_f");
            // String co_name_l = request.getParameter("co_name_l");
            String e_mail_address = request.getParameter("e_mail_address");
            String order_num = request.getParameter("order_num");
            
            // request.setAttribute("cust_num", null);
            // request.setAttribute("name", null);
            // request.setAttribute("co_name_f", null);
            // request.setAttribute("co_name_l", null);
            // request.setAttribute("e_mail_address", null);

            request.setAttribute("cust_num", cust_num);
            request.setAttribute("name", name);
            request.setAttribute("co_name_f", co_name_f);
            request.setAttribute("co_name_l", null);
            request.setAttribute("e_mail_address", e_mail_address);
            request.setAttribute("order_num", order_num);
			
			Utils.forwardToJSP(request, response, "DashboardSearch.jsp", null);
			
			return;
		} else {
            String cust_num = request.getParameter("cust_num");
            String name = request.getParameter("name");
            String co_name_f = request.getParameter("co_name_f");
            // String co_name_l = request.getParameter("co_name_l");
            String e_mail_address = request.getParameter("e_mail_address");
            String order_num = request.getParameter("order_num");

			ArrayList<Dashboard> dashboardAL;
			if (StringUtils.isBlank(order_num) == false) {
				dashboardAL = Dashboard.getDashboardData(dbConnector, cust_num, name, co_name_f, co_name_l, e_mail_address, order_num);	
			} else {
				dashboardAL = Dashboard.getDashboardData(dbConnector, cust_num, name, co_name_f, co_name_l, e_mail_address);
			}

            request.setAttribute("dashboardAL", dashboardAL);

            request.setAttribute("cust_num", cust_num);
            request.setAttribute("name", name);
            request.setAttribute("co_name_f", co_name_f);
            request.setAttribute("e_mail_address", e_mail_address);
            request.setAttribute("order_num", order_num);

            if ((dashboardAL == null) || (dashboardAL.size() == 0)) {
    			Utils.forwardToJSP(request, response, "DashboardResults.jsp", "No matches.");            	
            } else if (dashboardAL.size() < 20) {
    			Utils.forwardToJSP(request, response, "DashboardResults.jsp", "Showing all matches.");
            } else {
    			Utils.forwardToJSP(request, response, "DashboardResults.jsp", "Showing first 20 matches.");
            }
		}
	}

	public static void dashboardConfirmEmail(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
        String e_mail_address = request.getParameter("e_mail_address");		
		if (StringUtils.isBlank(e_mail_address) == true) {
		    request.setAttribute("messageToUser", null);
		    
            request.setAttribute("e_mail_address", "");
			
			Utils.forwardToJSP(request, response, "DashboardConfirmEmails.jsp", null);
			
			return;
		} else {
			GluuUser gluuUser = null;
			try {
				gluuUser = GluuUser.retrieveUserInfoFromGluu(e_mail_address, "", true);
			} catch (PortalException e) {
				log.debug("Exception occurred.");
				log.debug(e.getE());
				gluuUser = null;
			}
			if (gluuUser == null) {
	            request.setAttribute("e_mail_address", "");
				request.setAttribute("result", "error");	            
    			Utils.forwardToJSP(request, response, "DashboardConfirmEmails.jsp", "E-mail " + e_mail_address + " is not a registered Portal user, please try again.");
    			return;				
			}
    			
			if (gluuUser.getEmailConfirmed() == true) {
				request.setAttribute("e_mail_address", "");				
				request.setAttribute("result", "error");				
				Utils.forwardToJSP(request, response, "DashboardConfirmEmails.jsp", "E-mail " + e_mail_address + " is already confirmed.");					
    			return;
			} else {
				try {
					gluuUser.modifyCustomAttribute("emailConfirmed", "Y");
				} catch (PortalException e) {
					log.debug("Exception occurred.");
					log.debug(e.getE());
		            request.setAttribute("e_mail_address", "");
					request.setAttribute("result", "error");	            
	    			Utils.forwardToJSP(request, response, "DashboardConfirmEmails.jsp", "Error encountered confirming the e-mail '" + e_mail_address + "', please contact Portal support.");            	
	    			return;
				}

				request.setAttribute("e_mail_address", "");				
				request.setAttribute("result", "success");				
				Utils.forwardToJSP(request, response, "DashboardConfirmEmails.jsp", "Successfully confirmed the e-mail '" + e_mail_address + "'.");            	
    			return;
			}
		}
	}

	public static void dashboardOrderSearch(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		Utils.forwardToJSP(request, response, "DashboardOrderSearch.jsp", null);
	}	
}
