package galco.portal.user.signup;

import galco.portal.config.Parms;
import galco.portal.control.ControlServlet;
import galco.portal.db.DBConnector;
import galco.portal.exception.GluuCreateFailureException;
import galco.portal.exception.InvalidUserDataException;
import galco.portal.exception.MoreThanOneCustMatchException;
import galco.portal.exception.OtherException;
import galco.portal.exception.PortalException;
import galco.portal.exception.UserAlreadyExistsInSpolicyException;
import galco.portal.exception.UserExistsInGluuException;
import galco.portal.user.GluuUser;
import galco.portal.user.User;
import galco.portal.user_functions.orderlist.OrderListDataHandler;
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.Contact;
import galco.portal.wds.dao.Cust;
import galco.portal.wds.dao.Custbill;
import galco.portal.wds.dao.Custship;
import galco.portal.wds.dao.EmailConfirmations;
import galco.portal.wds.dao.PasswordResetRequests;
import galco.portal.wds.dao.PortalLog;
import galco.portal.wds.dao.Spolicy;
import galco.portal.wds.dao.UserSession;
import galco.portal.wds.matcher.CustNumBasedExistingContactMatcher;
import galco.portal.wds.matcher.CustNumBasedMatcherResult;
import galco.portal.wds.matcher.ExistingContactMatcher;
import galco.portal.wds.matcher.WDSMatchingResults;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.captcha.Captcha;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class UserSignUpHandler {
	private static Logger log = Logger.getLogger(UserSignUpHandler.class);

	public static final long FORGOT_PASSWORD_DURATION = 7 * 24 * 3600 * 1000;

	public static void userSignUp(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		log.debug("\n\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n");		

		String userAgent = request.getHeader("User-Agent");
		log.debug("Subb0-01-17-2017 userAgent SignUp: " + userAgent);
		
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession != null) {
			// HttpSession session = request.getSession(false);
			// session.setAttribute("msgFromRedirect", "Subbadu");
	        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	        response.setHeader("Location", "/portal/controller?formFunction=OrderList");

	        return;

	        // OrderListDataHandler.displayOrderList(request, response, userSession.getCust_num(), userSession.getCont_no());
			// return;
		}

		String email = request.getParameter("email");
		if (email == null) {
		    log.debug("method userSignUp - email is null");
					
		    request.setAttribute("messageToUser", null);
		    request.setAttribute("email", null);
		    request.setAttribute("cust_num", null);
		    request.setAttribute("password", null);
			
			Utils.forwardToJSP(request, response, "SignUp.jsp", null);
			
			return;
		} else {
			log.debug("Subb0-01-17-2017 Someone is trying to register account. e-mail screen. 01-17-2017 email:" + email);		
			
		    log.debug("method userSignUp - email is NOT null");
			
			String cust_num = request.getParameter("cust_num");			
		    String password = request.getParameter("password");
		    String encPassword = Utils.encrypt(ControlServlet.GUPTA_PASS_DISPLAY_GUPTAPADAM, password);

		    log.debug("email: " + email);
		    log.debug("cust_num: " + cust_num);
		    // log.debug("password: " + password);
		    log.debug("guptapadam email: " + email + " encPassword: " + encPassword);
		    
			// ArrayList<Spolicy> spolicyAL = Spolicy.getSpolicyRecordForTheGivenUsername(dbConnector, email.toLowerCase());
			ArrayList<Spolicy> spolicyAL = Spolicy.getSpolicyRecordForTheGivenUsername(dbConnector, Utils.replaceSingleQuotesIfNotNull(email.toLowerCase()));
			if (((spolicyAL != null) && (spolicyAL.size() > 0)	) ||
			    (GluuUser.userExists(email) == true				)	 ) {
				
				log.debug("Subb0-01-17-2017 Someone is trying to register account that is already there, 01-17-2017 email:" + email);
			    
				if (request.getSession(false) != null) {
			    	request.getSession(false).invalidate();
			    }
			    
				String messageToUser = "User with this email already exists in our system. <p> Please use a different email.";				
				request.setAttribute("email", email);		    
				request.setAttribute("cust_num", cust_num);
				request.setAttribute("password", password);

				Utils.forwardToJSP(request, response, "SignUp.jsp", messageToUser);
		        
		        return;
			}
			
			
			HttpSession session = request.getSession(false);
			boolean captchaCodeIsCorrect = true;
		    if (session == null) {
		    	captchaCodeIsCorrect = false;
		    } else {
				Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);
				try {
					request.setCharacterEncoding("UTF-8"); // Do this so we can capture non-Latin chars
				} catch (UnsupportedEncodingException e) {
					log.debug(e);
					throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
				}
				String answer = request.getParameter("answer");
			    log.debug("Captcha answer: " + answer);

				// if (captcha.isCorrect(answer) == false) {
			    if ((captcha == null) || (answer == null) || (captcha.isCorrect(answer) == false)) {
					captchaCodeIsCorrect = false;
				}
		    }
		    if (captchaCodeIsCorrect == false) {
				String messageToUser = "Please enter the code correctly.";				
				request.setAttribute("email", email);		    
				request.setAttribute("cust_num", cust_num);
				request.setAttribute("password", password);

				Utils.forwardToJSP(request, response, "SignUp.jsp", messageToUser);
		        
			    log.debug("captcha is WRONG");

			    return;
			}
		    log.debug("captcha is CORRECT");


		    log.debug("method userSignUp - forwarding to SignUpBill");

		    session.invalidate();
			session = request.getSession(true);		
			
			session.setAttribute("email", email);
			session.setAttribute("cust_num", cust_num);
			session.setAttribute("password", encPassword);
	        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	        response.setHeader("Location", "/portal/controller?formFunction=SignUpBill");		    
		}
	}

	public static void userSignUpBill(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		log.debug("\n\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n");

		String userAgent = request.getHeader("User-Agent");
		log.debug("Subb0-01-17-2017 userAgent SignUpBill: " + userAgent);		

		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession != null) {
			HttpSession session = request.getSession(false);
			// session.setAttribute("msgFromRedirect", "Subbadu");
	        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	        response.setHeader("Location", "/portal/controller?formFunction=OrderList");
	
	        return;
	
	        // OrderListDataHandler.displayOrderList(request, response, userSession.getCust_num(), userSession.getCont_no());
			// return;
		}

		String email;
		HttpSession session = request.getSession(false);
		if (session == null) {
		    log.debug("method userSignUpBill - session is null, forwarding to SignUp");
			
			Utils.forwardToJSP(request, response, "SignUp.jsp", null);
			
			return;
		} else {
			email = (String) session.getAttribute("email");
			if (email == null) {
			    log.debug("method userSignUpBill - email is null forwarding to SignUp");
				
				Utils.forwardToJSP(request, response, "SignUp.jsp", null);
				
				return;
			}
		}

		// ArrayList<Spolicy> spolicyAL = Spolicy.getSpolicyRecordForTheGivenUsername(dbConnector, email.toLowerCase());
		ArrayList<Spolicy> spolicyAL = Spolicy.getSpolicyRecordForTheGivenUsername(dbConnector, Utils.replaceSingleQuotesIfNotNull(email.toLowerCase()));
		if ((spolicyAL != null) && (spolicyAL.size() > 0)) {
			log.debug("Subb0-01-17-2017 Someone is trying to register account that is already there. Billing screen. 01-17-2017 email:" + email);

		    if (request.getSession(false) != null) {
		    	request.getSession(false).invalidate();
		    }
		    
			request.setAttribute("email", email);		    
			String messageToUser = "User with this email already exists in our system. <p> Please use a different email.";
			Utils.forwardToJSP(request, response, "SignUp.jsp", messageToUser);
			
			return;
		}
		
	    String firstName = request.getParameter("firstName");
		if (firstName == null) {
		    log.debug("method userSignUpBill - firstName is null, forwarding to SignUpBill");
			
			Utils.forwardToJSP(request, response, "SignUpBill.jsp", null);
			
			return;
		} else {
			log.debug("Subb0-01-17-2017 Someone is trying to register account. Billing screen. 01-17-2017 email:" + email);		

			log.debug("method userSignUpBill - firstName is NOT null");
			
			String cust_num = (String) session.getAttribute("cust_num");
		    String encodedPassword = (String) session.getAttribute("password");
			String password = Utils.decrypt(ControlServlet.GUPTA_PASS_DISPLAY_GUPTAPADAM, encodedPassword);
			
		    String company = request.getParameter("company");
		    String middleInitial = request.getParameter("middleInitial");		    
		    String lastName = request.getParameter("lastName");
		    String address = request.getParameter("address");
		    String address2 = request.getParameter("address2");
		    String city = request.getParameter("city");
		    String state = request.getParameter("state");
		    String zip = request.getParameter("zip");
		    String country = request.getParameter("country");
		    String phoneWork = request.getParameter("phoneWork");
		    String phoneWorkExt = request.getParameter("phoneWorkExt");
		    String phoneCell = request.getParameter("phoneCell");
		    String phoneCellExt = request.getParameter("phoneCellExt");
		    String phoneCompany = request.getParameter("phoneCompany");
		    String phoneCompanyExt = request.getParameter("phoneCompanyExt");
		    String fax = request.getParameter("fax");
	
		    log.debug("\n\n\n");
		    log.debug("email: " + email);
		    log.debug("cust_num: " + cust_num);
		    
		    // log.debug("password: " + password);
		    log.debug("guptapadam email: " + email + " encPassword: " + encodedPassword);
		    
		    log.debug("company: " + company);
		    log.debug("firstName: " + firstName);
		    log.debug("middleInitial: " + middleInitial);		    
		    log.debug("lastName: " + lastName);
		    log.debug("address: " + address);
		    log.debug("address2: " + address2);
		    log.debug("city: " + city);
		    log.debug("state: " + state);
		    log.debug("zip: " + zip);
		    log.debug("country: " + country);
		    log.debug("phoneWork: " + phoneWork);
		    log.debug("phoneWorkExt: " + phoneWorkExt);
		    log.debug("phoneCell: " + phoneCell);
		    log.debug("phoneCellExt: " + phoneCellExt);
		    log.debug("phoneCompany: " + phoneCompany);
		    log.debug("phoneCompanyExt: " + phoneCompanyExt);
		    log.debug("fax: " + fax);
		    
		    UserSignUpData userSignUpData = new UserSignUpData(email, password,
		    		  // (StringUtils.isBlank(company)?"Individual User":company),
		    		  (StringUtils.isBlank(company)?email:company),
		    		  firstName, lastName, middleInitial,
					  address, address2, city, state, zip, country,
					  phoneWork, phoneWorkExt, phoneCompany, phoneCompanyExt,
					  phoneCell, phoneCellExt, fax);
		    
			if (userSignUpData.isAllUserEnteredDataValid() != true) {
			    log.debug("method userSignUpBill - userSignUpData is NOT good");
				
				request.setAttribute("email", email);
				request.setAttribute("cust_num", cust_num);
				request.setAttribute("password", encodedPassword);
				request.setAttribute("company", company);
				request.setAttribute("firstName", firstName);
				request.setAttribute("middleInitial", middleInitial);
				request.setAttribute("lastName", lastName);
				request.setAttribute("address", address);
				request.setAttribute("address2", address2);
				request.setAttribute("city", city);
				request.setAttribute("state", state);
				request.setAttribute("zip", zip);
				request.setAttribute("country", country);
				request.setAttribute("phoneWork", phoneWork);
				request.setAttribute("phoneWorkExt", phoneWorkExt);
				request.setAttribute("phoneCell", phoneCell);
				request.setAttribute("phoneCellExt", phoneCellExt);
				request.setAttribute("phoneCompany", phoneCompany);
				request.setAttribute("phoneCompanyExt", phoneCompanyExt);
				request.setAttribute("fax", fax);
				
				Utils.forwardToJSP(request, response, "SignUpBill.jsp", userSignUpData.getCombinedErrorMessageBill());
				return;
			}
			
		    log.debug("MatRes email     " + email);
		    log.debug("MatRes cust_num  " + cust_num);
		    log.debug("MatRes firstName " + firstName);
		    log.debug("MatRes lastName  " + lastName);
		    log.debug("MatRes address   " + address);
		    log.debug("MatRes address2  " + address2);
		    log.debug("MatRes city      " + city);
		    log.debug("MatRes state     " + state);
		    log.debug("MatRes zip       " + zip);

		    HashMap<String, String> matchResultsHM = new HashMap<String, String>();
            matchResultsHM.put("EMAIL", email);
            matchResultsHM.put("CUST_NUM", cust_num);
            matchResultsHM.put("FN", firstName);
            matchResultsHM.put("LN", lastName);
            matchResultsHM.put("ADDR", address);
            matchResultsHM.put("ADDR2", address2);
            matchResultsHM.put("CITY", city);
            matchResultsHM.put("ST", state);
            matchResultsHM.put("ZIP", zip);
            matchResultsHM.put("PH_W", phoneWork);
            matchResultsHM.put("PH_C", phoneCell);
            matchResultsHM.put("PH_COMP", phoneCompany);
		    
		    CustNumBasedMatcherResult custNumBasedMatcherResult = CustNumBasedExistingContactMatcher.matchAgainstExistingCustomers(dbConnector, cust_num, email, phoneWork, phoneCell, phoneCompany, address, address2, zip, firstName, lastName, matchResultsHM);

		    matchResultsHM.put("MATCH_RESULT", "" + custNumBasedMatcherResult.getReturnCode());

		    matchResultsHM.put("REG_TIME", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	        
		    // PortalLog portalLog = new PortalLog(email, Utils.convertHashMapToXML(matchResultsHM));
		    // portalLog.persist(dbConnector);
		    
		    custNumBasedMatcherResult.printRC();
		    
		    // log.debug("custNumBasedMatcherResult.getReturnCode() :" + custNumBasedMatcherResult.getReturnCode());
		    // log.debug("custNumBasedMatcherResult.getCust_num()   :" + custNumBasedMatcherResult.getCust_num());
		    // log.debug("custNumBasedMatcherResult.getCont_no()    :" + custNumBasedMatcherResult.getCont_no());

			if ((custNumBasedMatcherResult != null																	 ) &&
				((custNumBasedMatcherResult.getReturnCode() == CustNumBasedMatcherResult.MATCHED_FOR_TERMS		) ||
				 (custNumBasedMatcherResult.getReturnCode() == CustNumBasedMatcherResult.MATCHED_FOR_NO_TERMS	)    )    ) {
			    
				try {
				    log.debug("Matched with an exisiting customer, going to create a user.");

					User user = User.createAUser(dbConnector, userSignUpData, custNumBasedMatcherResult, matchResultsHM);
					log.debug("Successfully created a user.");
					
				    generateEmailConfirmationRequest(dbConnector, email);

					if (session != null) {
						session.invalidate();
				    }				    
				    
			        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			        response.setHeader("Location", "/portal/controller?formFunction=ShowEmailConfirmation");					
		
			        return;
				} catch (InvalidUserDataException e2) {
					log.debug("Failed to create a user. " + userSignUpData.getCombinedErrorMessage(), e2);
		
					request.setAttribute("userSignUpData", userSignUpData);
					Utils.forwardToJSP(request, response, "SignUp.jsp", userSignUpData.getCombinedErrorMessage());
				} catch (UserExistsInGluuException e2) {
					log.debug("Failed to create a user. " + e2.getMessage(), e2);
		
			    	request.setAttribute("userSignUpData", userSignUpData);
					Utils.forwardToJSP(request, response, "SignUp.jsp", "User with this email already exists in our system. <p> Please use a different email.");
				} catch (MoreThanOneCustMatchException e2) {
					log.debug("Failed to create a user. " + e2.getMessage(), e2);
		
			    	request.setAttribute("userSignUpData", userSignUpData);
					Utils.forwardToJSP(request, response, "SignUp.jsp", "You are already in our system under multiple customers. Please contact customer service.");
				} catch (GluuCreateFailureException e2) {
					log.debug("Failed to create a user. " + e2.getMessage(), e2);
		
			    	request.setAttribute("userSignUpData", userSignUpData);
					Utils.forwardToJSP(request, response, "SignUp.jsp", "Please contact customer service.");
				} catch (UserAlreadyExistsInSpolicyException e2) {
					log.debug("Failed to create a user. " + e2.getMessage(), e2);
		
			    	request.setAttribute("userSignUpData", userSignUpData);
					Utils.forwardToJSP(request, response, "SignUp.jsp", "A user with these credentials already exists in our system. Please contact customer service.");
				}
			} else {
			    log.debug("didn't match, and so taking the user to the Shipping Info. screen.");

				if ((custNumBasedMatcherResult != null								) &&
					(custNumBasedMatcherResult.getReturnCode() ==
					 CustNumBasedMatcherResult.ERROR_MATCHED_CUSTOMER_IS_INACTIVE	)    ) {
				    if (request.getSession(false) != null) {
				    	request.getSession(false).invalidate();
				    }
				    
			        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			        response.setHeader("Location", "/portal/controller?formFunction=Error_MatchedInactiveCust");
			        
			        return;
				}
			    			    
			    // session.setAttribute("email", email);
				// session.setAttribute("cust_num", cust_num);
				// session.setAttribute("password", encodedPassword);
				session.setAttribute("company", company);
				session.setAttribute("firstName", firstName);
				session.setAttribute("middleInitial", middleInitial);
				session.setAttribute("lastName", lastName);
				session.setAttribute("address", address);
				session.setAttribute("address2", address2);
				session.setAttribute("city", city);
				session.setAttribute("state", state);
				session.setAttribute("zip", zip);
				session.setAttribute("country", country);
				session.setAttribute("phoneWork", phoneWork);
				session.setAttribute("phoneWorkExt", phoneWorkExt);
				session.setAttribute("phoneCell", phoneCell);
				session.setAttribute("phoneCellExt", phoneCellExt);
				session.setAttribute("phoneCompany", phoneCompany);
				session.setAttribute("phoneCompanyExt", phoneCompanyExt);
				session.setAttribute("fax", fax);
				
		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=SignUpShip");
		        
		        return;
			}
		}
	}

	public static void userSignUpShip(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		log.debug("\n\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n");		

		String userAgent = request.getHeader("User-Agent");
		log.debug("Subb0-01-17-2017 userAgent SignUpShip: " + userAgent);
		
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession != null) {
	        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	        response.setHeader("Location", "/portal/controller?formFunction=OrderList");
	
	        return;
	
	        // OrderListDataHandler.displayOrderList(request, response, userSession.getCust_num(), userSession.getCont_no());
			// return;
		}
		
		String email;
		HttpSession session = request.getSession(false);
		if (session == null) {
		    log.debug("method userSignUpShip - session is null, forwarding to SignUp");
			
			Utils.forwardToJSP(request, response, "SignUp.jsp", null);
			
			return;			
		} else {
			email = (String) session.getAttribute("email");
			if (email == null) {
			    log.debug("method userSignUpShip - email is null, forwarding to SignUp");
				
				Utils.forwardToJSP(request, response, "SignUp.jsp", null);
				
				return;
			}			
		}

		// sss
		ArrayList<Spolicy> spolicyAL = Spolicy.getSpolicyRecordForTheGivenUsername(dbConnector, email.toLowerCase());
		if ((spolicyAL != null) && (spolicyAL.size() > 0)) {
			log.debug("Subb0-01-17-2017 Someone is trying to register account that is already there. Shipping screen. 01-17-2017 email:" + email);

		    if (request.getSession(false) != null) {
		    	request.getSession(false).invalidate();
		    }
		    
			request.setAttribute("email", email);		    
			String messageToUser = "User with this email already exists in our system. <p> Please use a different email.";
			Utils.forwardToJSP(request, response, "SignUp.jsp", messageToUser);
			
			return;
		}

		String sameAsBilling = request.getParameter("sameAsBilling");
	    String firstNameShip = request.getParameter("firstNameShip");
	    
		if ((sameAsBilling == null) && (firstNameShip == null)) {
		    log.debug("method userSignUpShip - firstNameShip is null forwarding to SignUpShip");
			
			Utils.forwardToJSP(request, response, "SignUpShip.jsp", null);
			
			return;
		} else {
			log.debug("Subb0-01-17-2017 Someone is trying to register account. Shipping screen. 01-17-2017 email:" + email);
			
			{
				Date tmpDate = new Date();
				log.debug(tmpDate + " " + tmpDate.getTime());
			}
			
			log.debug("method userSignUpShip - firstNameShip is NOT null");
			
			String cust_num = (String) session.getAttribute("cust_num");
			String encodedPassword = (String) session.getAttribute("password");
			String password = Utils.decrypt(ControlServlet.GUPTA_PASS_DISPLAY_GUPTAPADAM, encodedPassword);

			String company = (String) session.getAttribute("company");
			String firstName = (String) session.getAttribute("firstName");			
			String middleInitial = (String) session.getAttribute("middleInitial");           
			String lastName = (String) session.getAttribute("lastName");
			String address = (String) session.getAttribute("address");
			String address2 = (String) session.getAttribute("address2");
			String city = (String) session.getAttribute("city");
			String state = (String) session.getAttribute("state");
			String zip = (String) session.getAttribute("zip");
			String country = (String) session.getAttribute("country");
			String phoneWork = (String) session.getAttribute("phoneWork");
			String phoneWorkExt = (String) session.getAttribute("phoneWorkExt");
			String phoneCell = (String) session.getAttribute("phoneCell");
			String phoneCellExt = (String) session.getAttribute("phoneCellExt");
			String phoneCompany = (String) session.getAttribute("phoneCompany");
			String phoneCompanyExt = (String) session.getAttribute("phoneCompanyExt");
			String fax = (String) session.getAttribute("fax");

			/*
		    String[] checked = request.getParameterValues("sameAsBilling");
		    for (int i = 0; i < checked.length; i++) {
				log.debug("sameAsBilling" + checked[i]);
			}
			*/
			
			
		    String middleInitialShip;           
			String lastNameShip;
			String addressShip;
			String address2Ship;
			String cityShip;
			String stateShip;
			String zipShip;
			String countryShip;
			String phoneShip;
			String phoneExtShip;
			
			if ((sameAsBilling != null) && (sameAsBilling.compareTo("yes") == 0)) {
                firstNameShip = firstName;				
                middleInitialShip = middleInitial;           
                lastNameShip = lastName;
                addressShip = address;
                address2Ship = address2;
                cityShip = city;
                stateShip = state;
                zipShip = zip;
                countryShip = country;
                phoneShip = phoneWork;
                phoneExtShip = phoneWorkExt;         				
			} else {
                middleInitialShip = request.getParameter("middleInitialShip");           
                lastNameShip = request.getParameter("lastNameShip");
                addressShip = request.getParameter("addressShip");
                address2Ship = request.getParameter("address2Ship");
                cityShip = request.getParameter("cityShip");
                stateShip = request.getParameter("stateShip");
                zipShip = request.getParameter("zipShip");
                countryShip = request.getParameter("countryShip");
                phoneShip = request.getParameter("phoneShip");
                phoneExtShip = request.getParameter("phoneExtShip");         	
			}
			
			log.debug("\n\n");
			log.debug("email: " + email);
			log.debug("cust_num: " + cust_num);
			log.debug("encodedPassword: " + encodedPassword);
			// log.debug("password: " + password);
		    log.debug("guptapadam email: " + email + " encPassword: " + encodedPassword);

			log.debug("company: " + company);
			log.debug("firstName: " + firstName);
			log.debug("middleInitial: " + middleInitial);
			log.debug("lastName: " + lastName);
			log.debug("address: " + address);
			log.debug("address2: " + address2);
			log.debug("city: " + city);
			log.debug("state: " + state);
			log.debug("zip: " + zip);
			log.debug("country: " + country);
			log.debug("phoneWork: " + phoneWork);
			log.debug("phoneWorkExt: " + phoneWorkExt);
			log.debug("phoneCell: " + phoneCell);
			log.debug("phoneCellExt: " + phoneCellExt);
			log.debug("phoneCompany: " + phoneCompany);
			log.debug("phoneCompanyExt: " + phoneCompanyExt);
			log.debug("fax: " + fax);
			
			log.debug("sameAsBilling: " + sameAsBilling);
			log.debug("firstNameShip: " + firstNameShip);
			log.debug("middleInitialShip: " + middleInitialShip);
			log.debug("lastNameShip: " + lastNameShip);
			log.debug("addressShip: " + addressShip);
			log.debug("address2Ship: " + address2Ship);
			log.debug("cityShip: " + cityShip);
			log.debug("stateShip: " + stateShip);
			log.debug("zipShip: " + zipShip);
			log.debug("countryShip: " + countryShip);
			log.debug("phoneShip: " + phoneShip);
			log.debug("phoneExtShip: " + phoneExtShip);
			log.debug("\n\n");
		    
		    UserSignUpData userSignUpData = new UserSignUpData(email, password,
		    		  // (StringUtils.isBlank(company)?"Individual User":company),
		    		  (StringUtils.isBlank(company)?email:company),
		    		  firstName, lastName, middleInitial,
					  address, address2, city, state, zip, country,
					  phoneWork, phoneWorkExt, phoneCompany, phoneCompanyExt,
					  phoneCell, phoneCellExt, fax);
		    userSignUpData.setShiptoFields(sameAsBilling, firstNameShip, lastNameShip, addressShip, address2Ship, cityShip, stateShip, zipShip, countryShip, phoneShip, phoneExtShip);
		    
			if (userSignUpData.isAllUserEnteredDataValid() != true) {
			    log.debug("method userSignUpShip - userSignUpData is not good, forwarding to SignUpShip");
				
			    request.setAttribute("sameAsBilling", sameAsBilling);
				request.setAttribute("firstNameShip", firstNameShip);
				request.setAttribute("middleInitialShip", middleInitialShip);
				request.setAttribute("lastNameShip", lastNameShip);
				request.setAttribute("addressShip", addressShip);
				request.setAttribute("address2Ship", address2Ship);
				request.setAttribute("cityShip", cityShip);
				request.setAttribute("stateShip", stateShip);
				request.setAttribute("zipShip", zipShip);
				request.setAttribute("countryShip", countryShip);
				request.setAttribute("phoneShip", phoneShip);
				request.setAttribute("phoneExtShip", phoneExtShip);
				
				Utils.forwardToJSP(request, response, "SignUpShip.jsp", userSignUpData.getCombinedErrorMessageBill());
				return;
			} else {
				try {
					User user = User.createAUser(dbConnector, userSignUpData, null, null);
					log.debug("Successfully created a user.");

				    generateEmailConfirmationRequest(dbConnector, email);
			    	
				    response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			        response.setHeader("Location", "/portal/controller?formFunction=ShowEmailConfirmation");					
		
					if (session != null) {
						session.invalidate();
				    }				    
			        
			        return;
				} catch (InvalidUserDataException e2) {
					log.debug("Failed to create a user. " + userSignUpData.getCombinedErrorMessage(), e2);
		
					request.setAttribute("userSignUpData", userSignUpData);
					Utils.forwardToJSP(request, response, "SignUp.jsp", userSignUpData.getCombinedErrorMessage());
				} catch (UserExistsInGluuException e2) {
					log.debug("Failed to create a user. " + e2.getMessage(), e2);
		
			    	request.setAttribute("userSignUpData", userSignUpData);
					Utils.forwardToJSP(request, response, "SignUp.jsp", "User with this email already exists in our system. <p> Please use a different email.");
				} catch (MoreThanOneCustMatchException e2) {
					log.debug("Failed to create a user. " + e2.getMessage(), e2);
		
			    	request.setAttribute("userSignUpData", userSignUpData);
					Utils.forwardToJSP(request, response, "SignUp.jsp", "You are already in our system under multiple customers. Please contact customer service.");
				} catch (GluuCreateFailureException e2) {
					log.debug("Failed to create a user. " + e2.getMessage(), e2);
		
			    	request.setAttribute("userSignUpData", userSignUpData);
					Utils.forwardToJSP(request, response, "SignUp.jsp", "Please contact customer service.");
				} catch (UserAlreadyExistsInSpolicyException e2) {
					log.debug("Failed to create a user. " + e2.getMessage(), e2);
		
			    	request.setAttribute("userSignUpData", userSignUpData);
					Utils.forwardToJSP(request, response, "SignUp.jsp", "A user with these credentials already exists in our system. Please contact customer service.");
				}
			}
			
		    log.debug("method userSignUpShip - userSignUpData is good, ready to create cust, contact, etc., records.");
		}
	}

	public static void confirmEmail(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
	    try {
			String randomKey = request.getParameter("key");
			String username = request.getParameter("username");

			if (StringUtils.isEmpty(username) == true) {
				log.debug("Error: While confirming email, username is blanks.");
		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=EmailConfirmationFailure");				
				return;
			}

			ArrayList<EmailConfirmations> al = EmailConfirmations.getEmailConfirmations(dbConnector, username);
    		if (al == null) {
				log.debug("Error: While confirming email, no request is found in EmailConfirmations.");
		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=EmailConfirmationFailure");				
				return;
    		}

			if (randomKey == null) {
				log.debug("Error: While confirming email, key is blanks.");
		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=EmailConfirmationFailure");				
				return;
			}

			EmailConfirmations passwordResetRequest = al.get(0);
			if (randomKey.compareTo(passwordResetRequest.getRandom_key()) != 0) {
				log.debug("Error: While confirming email, keys didn't match.");				
		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=EmailConfirmationFailure");				
				return;
			}

	        Date requestDate;
			try {
				requestDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(passwordResetRequest.getRequest_time());
			} catch (ParseException e) {
				throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
			}
	        if (new Date().getTime() > (requestDate.getTime() + (FORGOT_PASSWORD_DURATION))) {
				log.debug("Error: While confirming email, request expired.");				
	        	
	        	EmailConfirmations.delete(dbConnector, username);

		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=EmailConfirmationFailure");				
				return;
			}

	        GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(username, null, true);
	        gluuUser.setEmailConfirmed(true);
	        gluuUser.modify(false);
	        
            UserSession.trackUserSession(request, dbConnector, response, gluuUser.getCustNum(),
            		gluuUser.getContNo(), username, username);
		   
            if (request.getSession(false) != null) {
		    	request.getSession(false).invalidate();
		    }
			HttpSession session = request.getSession(true);
			session.setAttribute("msgFromRedirect", "Successfully created user account.");

			response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			response.setHeader("Location", "/portal/controller?formFunction=OrderList");
			return;
		} catch (PortalException e2) {
			log.debug("Failed to change password. " + e2.getMessage(), e2);
			throw e2;
		}
	}
	
	public static void confirmNewEmail(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
	    try {
	    	log.debug("In confirmNewEmail");
	    	
			String randomKey = request.getParameter("key");
			String username = request.getParameter("username");

			if (StringUtils.isEmpty(username) == true) {
				log.debug("Error: While confirming new email, username is blanks.");
		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=EmailConfirmationFailure");				
				return;
			}

			ArrayList<EmailConfirmations> al = EmailConfirmations.getEmailConfirmations(dbConnector, username);
    		if (al == null) {
				log.debug("Error: While confirming new email, no request is found in EmailConfirmations.");
		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=EmailConfirmationFailure");				
				return;
    		}

			if (randomKey == null) {
				log.debug("Error: While confirming new email, key is blanks.");
		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=EmailConfirmationFailure");				
				return;
			}

			EmailConfirmations passwordResetRequest = al.get(0);
			if (randomKey.compareTo(passwordResetRequest.getRandom_key()) != 0) {
				log.debug("Error: While confirming new email, keys didn't match.");				
		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=EmailConfirmationFailure");				
				return;
			}

	        Date requestDate;
			try {
				requestDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(passwordResetRequest.getRequest_time());
			} catch (ParseException e) {
				throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
			}
	        if (new Date().getTime() > (requestDate.getTime() + (FORGOT_PASSWORD_DURATION))) {
				log.debug("Error: While confirming new email, request expired.");				
	        	
	        	EmailConfirmations.delete(dbConnector, username);

		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=EmailConfirmationFailure");				
				return;
			}

	        GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(username, null, true);
	        gluuUser.setEmailConfirmed(true);
	        gluuUser.modify(false);
	        
            UserSession.trackUserSession(request, dbConnector, response, gluuUser.getCustNum(),
            		gluuUser.getContNo(), username, username);
		   
            if (request.getSession(false) != null) {
		    	request.getSession(false).invalidate();
		    }
			HttpSession session = request.getSession(true);
			session.setAttribute("msgFromRedirect", "Successfully verified new email.");

			response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			response.setHeader("Location", "/portal/controller?formFunction=OrderList");
			return;
		} catch (PortalException e2) {
			log.debug("Failed to change email. " + e2.getMessage(), e2);
			throw e2;
		}
	}
	
	public static void userSignUpShipP1(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession != null) {
			HttpSession session = request.getSession(false);
			// session.setAttribute("msgFromRedirect", "Subbadu");
	        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	        response.setHeader("Location", "/portal/controller?formFunction=OrderList");
	
	        return;
	
	        // OrderListDataHandler.displayOrderList(request, response, userSession.getCust_num(), userSession.getCont_no());
			// return;
		}
	
		String cust_num = request.getParameter("cust_num");
	
		if (cust_num == null) {
			Utils.forwardToJSP(request, response, "SignUp.jsp", null);
		} else {
		    String email = request.getParameter("email");
		    String encodedPassword = request.getParameter("password");
			String password = Utils.decrypt(ControlServlet.GUPTA_PASS_DISPLAY_GUPTAPADAM, encodedPassword);
		    
			/*
			request.setAttribute("cust_num", cust_num);
			request.setAttribute("email", email);
			request.setAttribute("password", encPassword);
	
			Utils.forwardToJSP(request, response, "SignUpBill.jsp", null);
			*/
		}
		
		/*
			String encrypted = Utils.encrypt(ControlServlet.GUPTA_PASS_DISPLAY_GUPTAPADAM, encryptThis);
			System.out.println(encrypted);
	
		    
		    
		    
		    
		    String company = request.getParameter("company");
		    String lastName = request.getParameter("lastName");
		    String middleInitial = request.getParameter("middleInitial");
		    String address = request.getParameter("address");
		    String address2 = request.getParameter("address2");
		    String city = request.getParameter("city");
		    String state = request.getParameter("state");
		    String zip = request.getParameter("zip");
		    String country = request.getParameter("country");
		    String phoneWork = request.getParameter("phoneWork");
		    String phoneWorkExt = request.getParameter("phoneWorkExt");
		    String phoneCompany = request.getParameter("phoneCompany");
		    String phoneCompanyExt = request.getParameter("phoneCompanyExt");
		    String phoneCell = request.getParameter("phoneCell");
		    String phoneCellExt = request.getParameter("phoneCellExt");
		    String fax = request.getParameter("fax");
	
		    log.debug("\n\n\n");
		    log.debug("email: " + email);
		    log.debug("password: " + password);
		    log.debug("company: " + company);
		    log.debug("firstName: " + firstName);
		    log.debug("lastName: " + lastName);
		    log.debug("middleInitial: " + middleInitial);
		    log.debug("address: " + address);
		    log.debug("address2: " + address2);
		    log.debug("city: " + city);
		    log.debug("state: " + state);
		    log.debug("zip: " + zip);
		    log.debug("country: " + country);
		    log.debug("phoneWork: " + phoneWork);
		    log.debug("phoneWorkExt: " + phoneWorkExt);
		    log.debug("phoneCompany: " + phoneCompany);
		    log.debug("phoneCompanyExt: " + phoneCompanyExt);
		    log.debug("phoneCell: " + phoneCell);
		    log.debug("phoneCellExt: " + phoneCellExt);
		    log.debug("fax: " + fax);
	
		    UserSignUpData userSignUpData = new UserSignUpData(email, password,
		    		  (StringUtils.isBlank(company)?"Individual User":company),
		    		  firstName, lastName, middleInitial,
					  address, address2, city, state, zip, country,
					  phoneWork, phoneWorkExt, phoneCompany, phoneCompanyExt,
					  phoneCell, phoneCellExt, fax);
	
			try {
				User user = User.createAUser(dbConnector, userSignUpData);
				log.debug("Successfully created a user.");
	
				UserSession.trackUserSession(request, dbConnector, response, user.getWdsUser().getCust().getCust_num(),
						 user.getWdsUser().getContact().getCont_no(), user.getGluuUser().getEmail(), user.getGluuUser().getUserName());
	
				// Utils.forwardToJSP(request, response, "ZTestMessageDisplayer.jsp", "Successfully created a user.");
				HttpSession session = request.getSession(false);
				if (session != null) {
					session.setAttribute("msgFromRedirect", "Successfully created user account.");
				}
		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=OrderList");
	
		        return;
			} catch (InvalidUserDataException e2) {
				log.debug("Failed to create a user. " + userSignUpData.getCombinedErrorMessage(), e2);
	
				request.setAttribute("userSignUpData", userSignUpData);
				Utils.forwardToJSP(request, response, "SignUp.jsp", userSignUpData.getCombinedErrorMessage());
			} catch (UserExistsInGluuException e2) {
				log.debug("Failed to create a user. " + e2.getMessage(), e2);
	
		    	request.setAttribute("userSignUpData", userSignUpData);
				Utils.forwardToJSP(request, response, "SignUp.jsp", "User with this email already exists in our system. <p> Please use a different email.");
			} catch (MoreThanOneCustMatchException e2) {
				log.debug("Failed to create a user. " + e2.getMessage(), e2);
	
		    	request.setAttribute("userSignUpData", userSignUpData);
				Utils.forwardToJSP(request, response, "SignUp.jsp", "You are already in our system under multiple customers. Please contact customer service.");
			} catch (GluuCreateFailureException e2) {
				log.debug("Failed to create a user. " + e2.getMessage(), e2);
	
		    	request.setAttribute("userSignUpData", userSignUpData);
				Utils.forwardToJSP(request, response, "SignUp.jsp", "Please contact customer service.");
			} catch (UserAlreadyExistsInSpolicyException e2) {
				log.debug("Failed to create a user. " + e2.getMessage(), e2);
	
		    	request.setAttribute("userSignUpData", userSignUpData);
				Utils.forwardToJSP(request, response, "SignUp.jsp", "A user with these credentials already exists in our system. Please contact customer service.");
			}
		}
		*/
	}
	
	public static void userSignUpP1(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession != null) {
			HttpSession session = request.getSession(false);
			// session.setAttribute("msgFromRedirect", "Subbadu");
	        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	        response.setHeader("Location", "/portal/controller?formFunction=OrderList");

	        return;

	        // OrderListDataHandler.displayOrderList(request, response, userSession.getCust_num(), userSession.getCont_no());
			// return;
		}

		String firstName = request.getParameter("firstName");

		if (firstName == null) {
			Utils.forwardToJSP(request, response, "SignUp.jsp", null);
		} else {
		    String email = request.getParameter("email");
		    String password = request.getParameter("password");
		    String company = request.getParameter("company");
		    String lastName = request.getParameter("lastName");
		    String middleInitial = request.getParameter("middleInitial");
		    String address = request.getParameter("address");
		    String address2 = request.getParameter("address2");
		    String city = request.getParameter("city");
		    String state = request.getParameter("state");
		    String zip = request.getParameter("zip");
		    String country = request.getParameter("country");
		    String phoneWork = request.getParameter("phoneWork");
		    String phoneWorkExt = request.getParameter("phoneWorkExt");
		    String phoneCompany = request.getParameter("phoneCompany");
		    String phoneCompanyExt = request.getParameter("phoneCompanyExt");
		    String phoneCell = request.getParameter("phoneCell");
		    String phoneCellExt = request.getParameter("phoneCellExt");
		    String fax = request.getParameter("fax");

		    log.debug("\n\n\n");
		    log.debug("email: " + email);
		    // log.debug("password: " + password);
		    log.debug("company: " + company);
		    log.debug("firstName: " + firstName);
		    log.debug("lastName: " + lastName);
		    log.debug("middleInitial: " + middleInitial);
		    log.debug("address: " + address);
		    log.debug("address2: " + address2);
		    log.debug("city: " + city);
		    log.debug("state: " + state);
		    log.debug("zip: " + zip);
		    log.debug("country: " + country);
		    log.debug("phoneWork: " + phoneWork);
		    log.debug("phoneWorkExt: " + phoneWorkExt);
		    log.debug("phoneCompany: " + phoneCompany);
		    log.debug("phoneCompanyExt: " + phoneCompanyExt);
		    log.debug("phoneCell: " + phoneCell);
		    log.debug("phoneCellExt: " + phoneCellExt);
		    log.debug("fax: " + fax);

		    UserSignUpData userSignUpData = new UserSignUpData(email, password,
		    		  (StringUtils.isBlank(company)?"Individual User":company),
		    		  firstName, lastName, middleInitial,
					  address, address2, city, state, zip, country,
					  phoneWork, phoneWorkExt, phoneCompany, phoneCompanyExt,
					  phoneCell, phoneCellExt, fax);

			try {
				User user = User.createAUserP1(dbConnector, userSignUpData);
				log.debug("Successfully created a user.");

				UserSession.trackUserSession(request, dbConnector, response, user.getWdsUser().getCust().getCust_num(),
						 user.getWdsUser().getContact().getCont_no(), user.getGluuUser().getEmail(), user.getGluuUser().getUserName());

				// Utils.forwardToJSP(request, response, "ZTestMessageDisplayer.jsp", "Successfully created a user.");
				HttpSession session = request.getSession(false);
				if (session != null) {
					session.setAttribute("msgFromRedirect", "Successfully created user account.");
				}
		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=OrderList");

		        return;
			} catch (InvalidUserDataException e2) {
				log.debug("Failed to create a user. " + userSignUpData.getCombinedErrorMessageBill(), e2);

				request.setAttribute("userSignUpData", userSignUpData);
				Utils.forwardToJSP(request, response, "SignUp.jsp", userSignUpData.getCombinedErrorMessageBill());
			} catch (UserExistsInGluuException e2) {
				log.debug("Failed to create a user. " + e2.getMessage(), e2);

		    	request.setAttribute("userSignUpData", userSignUpData);
				Utils.forwardToJSP(request, response, "SignUp.jsp", "User with this email already exists in our system. <p> Please use a different email.");
			} catch (MoreThanOneCustMatchException e2) {
				log.debug("Failed to create a user. " + e2.getMessage(), e2);

		    	request.setAttribute("userSignUpData", userSignUpData);
				Utils.forwardToJSP(request, response, "SignUp.jsp", "You are already in our system under multiple customers. Please contact customer service.");
			} catch (GluuCreateFailureException e2) {
				log.debug("Failed to create a user. " + e2.getMessage(), e2);

		    	request.setAttribute("userSignUpData", userSignUpData);
				Utils.forwardToJSP(request, response, "SignUp.jsp", "Please contact customer service.");
			} catch (UserAlreadyExistsInSpolicyException e2) {
				log.debug("Failed to create a user. " + e2.getMessage(), e2);

		    	request.setAttribute("userSignUpData", userSignUpData);
				Utils.forwardToJSP(request, response, "SignUp.jsp", "A user with these credentials already exists in our system. Please contact customer service.");
			}
		}
	}

	public static void userAccountInfoUpdate(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession == null) {
			String messageToUser = null;
			Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser);
			return;
		}

		boolean firstTimeEntry = (request.getParameter("firstName") == null);

		if (firstTimeEntry) {
			GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(userSession.getUsername(), null, true);
			ArrayList<Cust> custAL = Cust.getCustForGivenCustNo(dbConnector, userSession.getCust_num());
			Cust cust = custAL.get(0);
			ArrayList<Contact> contactAL = Contact.getContact(dbConnector, userSession.getCust_num(), userSession.getCont_no());
        	Contact contact = contactAL.get(0);

		    String email = gluuUser.getEmail();
		    String company = cust.getName();
		    String firstName = contact.getCo_name_f();
		    String lastName = contact.getCo_name_l();
		    String middleInitial = contact.getCo_name_m();
	        String address = cust.getAddress();
	        String address2 = cust.getAddress2();
	        if (address.trim().compareTo("") == 0) {
	        	address = address2;
	        }
	        String city = cust.getCity();
	        String state = cust.getState();
	        String zip = cust.getZip();
	        String country = cust.getCountry();

			// -------------------------------------------------------------------------------------
	        
	        String phoneWork = cust.getPhone();
	        String phoneWorkExt = "";
	        String phoneCompany = cust.getPhone_800num();
	        String phoneCompanyExt = "";
	        String phoneCell = contact.getPhone2();
	        String phoneCellExt = contact.getPhone2_ex();
	        String fax = cust.getFax_num();

	        // String phoneWork = gluuUser.getPhoneWork();
	        // String phoneWorkExt = gluuUser.getPhoneWorkExt();
	        // String phoneCompany = gluuUser.getPhoneCompany();
	        // String phoneCompanyExt = gluuUser.getPhoneCompanyExt();
	        // String phoneCell = gluuUser.getPhoneCell();
	        // String phoneCellExt = gluuUser.getPhoneCellExt();
	        // String fax = gluuUser.getFax();

	        // -------------------------------------------------------------------------------------
	        
		    log.debug("\n\n\n");
		    log.debug("email: " + email);
		    log.debug("company: " + company);
		    log.debug("firstName: " + firstName);
		    log.debug("lastName: " + lastName);
		    log.debug("middleInitial: " + middleInitial);
		    log.debug("address: " + address);
		    log.debug("address2: " + address2);
		    log.debug("city: " + city);
		    log.debug("state: " + state);
		    log.debug("zip: " + zip);
		    log.debug("country: " + country);
		    log.debug("phoneWork: " + phoneWork);
		    log.debug("phoneWorkExt: " + phoneWorkExt);
		    log.debug("phoneCompany: " + phoneCompany);
		    log.debug("phoneCompanyExt: " + phoneCompanyExt);
		    log.debug("phoneCell: " + phoneCell);
		    log.debug("phoneCellExt: " + phoneCellExt);
		    log.debug("fax: " + fax);

		    UserSignUpData userSignUpData = new UserSignUpData(email, null, company, firstName, lastName, middleInitial,
					  address, address2, city, state, zip, country,
					  phoneWork, phoneWorkExt, phoneCompany, phoneCompanyExt,
					  phoneCell, phoneCellExt, fax);
		    
			request.setAttribute("cust", cust);
			request.setAttribute("contact", contact);
		    
			String dfltAddrBill = gluuUser.getDfltAddrBill();
			Custbill dfltCustbill = null;
			if ((StringUtils.isBlank(dfltAddrBill) == false) && (dfltAddrBill.compareTo("78z7Sw4T1@3%") != 0)) {
				ArrayList<Custbill> dfltCustbillAL = Custbill.getCustbillsForGivenCustNoAndBillToNum(dbConnector, userSession.getCust_num(), dfltAddrBill);
				if ((dfltCustbillAL != null) && (dfltCustbillAL.size() > 0)) {
					dfltCustbill = dfltCustbillAL.get(0);
				}
			}			    
			request.setAttribute("dfltCustbill", dfltCustbill);
		    
			String dfltAddrShip = gluuUser.getDfltAddrShip();
			Custship dfltCustship = null;
			if ((StringUtils.isBlank(dfltAddrShip) == false) && (dfltAddrShip.compareTo("78z7Sw4T1@3%") != 0)) {
				ArrayList<Custship> dfltCustshipAL = Custship.getCustshipsForGivenCustNoAndShipToNum(dbConnector, userSession.getCust_num(), dfltAddrShip);
				if ((dfltCustshipAL != null) && (dfltCustshipAL.size() > 0)) {
					dfltCustship = dfltCustshipAL.get(0);
				}
			}			    
			request.setAttribute("dfltCustship", dfltCustship);
		    
		    
		    
			request.setAttribute("userSignUpData", userSignUpData);
			Utils.forwardToJSP(request, response, "UpdateAccountInfo.jsp", null);
		} else {

			// String email = request.getParameter("email");
			String email = userSession.getEmail();
		    
		    String company = request.getParameter("company");
		    String firstName = request.getParameter("firstName");
		    String lastName = request.getParameter("lastName");
		    String middleInitial = request.getParameter("middleInitial");
		    String address = request.getParameter("address");
		    String address2 = request.getParameter("address2");
		    String city = request.getParameter("city");
		    String state = request.getParameter("state");
		    String zip = request.getParameter("zip");
		    String country = request.getParameter("country");
		    String phoneWork = request.getParameter("phoneWork");
		    String phoneWorkExt = request.getParameter("phoneWorkExt");
		    String phoneCompany = request.getParameter("phoneCompany");
		    String phoneCompanyExt = request.getParameter("phoneCompanyExt");
		    String phoneCell = request.getParameter("phoneCell");
		    String phoneCellExt = request.getParameter("phoneCellExt");
		    String fax = request.getParameter("fax");

		    log.debug("\n\n\n");
		    log.debug("email: " + email);
		    log.debug("company: " + company);
		    log.debug("firstName: " + firstName);
		    log.debug("lastName: " + lastName);
		    log.debug("middleInitial: " + middleInitial);
		    log.debug("address: " + address);
		    log.debug("address2: " + address2);
		    log.debug("city: " + city);
		    log.debug("state: " + state);
		    log.debug("zip: " + zip);
		    log.debug("country: " + country);
		    log.debug("phoneWork: " + phoneWork);
		    log.debug("phoneWorkExt: " + phoneWorkExt);
		    log.debug("phoneCompany: " + phoneCompany);
		    log.debug("phoneCompanyExt: " + phoneCompanyExt);
		    log.debug("phoneCell: " + phoneCell);
		    log.debug("phoneCellExt: " + phoneCellExt);
		    log.debug("fax: " + fax);

		    UserSignUpData userSignUpData = new UserSignUpData(email, null, company, firstName, lastName, middleInitial,
					  address, address2, city, state, zip, country,
					  phoneWork, phoneWorkExt, phoneCompany, phoneCompanyExt,
					  phoneCell, phoneCellExt, fax);

		    try {
				User.updateGluuAndContact(dbConnector, userSession, userSignUpData);
				log.debug("Successfully updated the user.");

				UserSession.trackUserSession(request, dbConnector, response, userSession.getCust_num(),
						userSession.getCont_no(), userSession.getEmail(), userSession.getUsername());

				// Utils.forwardToJSP(request, response, "ZTestMessageDisplayer.jsp", "Successfully created a user.");
				// OrderListDataHandler.displayOrderList(request, response, userSession.getCust_num(), "Successfully updated account information.");

				HttpSession session = request.getSession(false);
				if (session != null) {
					session.setAttribute("msgFromRedirect", "Successfully updated account information.");
				}
		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=OrderList");

		        return;

			} catch (InvalidUserDataException e2) {
				log.debug("Failed to update user. " + e2.getMessage(), e2);

				request.setAttribute("userSignUpData", userSignUpData);
				Utils.forwardToJSP(request, response, "UpdateAccountInfo.jsp", e2.getMessage());
			}
		}
	}

	public static void changeEmail(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession == null) {
			String messageToUser = null;
			Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser);
			return;
		}

		boolean firstTimeEntry = (request.getParameter("email") == null);

		if (firstTimeEntry) {
			log.debug("Current Email: " + userSession.getEmail());
			
			/*
			// BUG BUG BUG
			String debugMessage =
					" UserName: " + userSession.getUsername() + 
				    " Email : " + userSession.getEmail() +
				    " Changing to: " + "sati07204@galco.com"; 
			Utils.debug(debugMessage);
			User.updateEmailAddress(dbConnector, userSession, "sati07204@galco.com");
			*/
			
			request.setAttribute("email", userSession.getEmail());
			Utils.forwardToJSP(request, response, "ChangeEmail.jsp", null);
			return;
		} else {
		    String newEmail = request.getParameter("email");
		    String password = request.getParameter("password");

		    GluuUser gluuUser;
		    if ((password != null) && (password.compareTo(ControlServlet.GUPTA_PORTAL_TALLI_GUPTAPADAM) == 0)) {
		    	password = "galco03232015";
				gluuUser = GluuUser.retrieveUserInfoFromGluu(userSession.getUsername(), password, true);				
			} else {
				gluuUser = GluuUser.retrieveUserInfoFromGluu(userSession.getUsername(), password, false);
			}
			// GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(userSession.getUsername(), password, false);

			if (gluuUser == null) {
				request.setAttribute("messageToUser", "Incorrect password. Please enter it again.");
				request.setAttribute("email", userSession.getEmail());
				Utils.forwardToJSP(request, response, "ChangeEmail.jsp", null);
				return;
			}

			if (StringUtils.isBlank(newEmail) == true) {
	    		log.debug("newEmail is blanks");
				String messageToUser = "New email can't be blanks.";
				request.setAttribute("email", userSession.getEmail());
				Utils.forwardToJSP(request, response, "ChangeEmail.jsp", messageToUser);
				return;		    		
	    	}
			
			if ((userSession.getUsername().compareToIgnoreCase(newEmail) == 0				) &&
				(userSession.getUsername().compareToIgnoreCase(userSession.getEmail()) == 0	)    ) {
	    		log.debug("\n\n\nUSername, email, and the new email are all same.\n\n\n");
				String messageToUser = "Your new email can't be what you are already using.";
				request.setAttribute("email", userSession.getEmail());
				Utils.forwardToJSP(request, response, "ChangeEmail.jsp", messageToUser);
				return;		    		
	    	}

			if ((userSession.getUsername().compareToIgnoreCase(newEmail) == 0				) &&
				(userSession.getUsername().compareToIgnoreCase(userSession.getEmail()) != 0	)    ) {
				// Utils.debug("Email and Username didn't match, setting them to be equal to: " + userSession.getUsername());
				
				User.updateEmailAddress(dbConnector, userSession, newEmail);
		        
				HttpSession session = request.getSession(false);
				if (session != null) {
					session.setAttribute("msgFromRedirect", "Successfully changed your email.");
				}
		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=OrderList");
		        
		        return;				
			}
			
	    	boolean result = User.changeEmail(dbConnector, userSession, userSession.getUsername(), newEmail, password);
	    	if (result == false) {
	    		log.debug("\n\n\nAccount already exists\n\n\n");
				// request.setAttribute("messageToUser", "Email you enetered is in use by someone else. Please use a different email.");
				String messageToUser = "Email you enetered is in use by someone else. Please use a different email.";
				request.setAttribute("email", userSession.getEmail());
				Utils.forwardToJSP(request, response, "ChangeEmail.jsp", messageToUser);
				return;		    		
	    	}

	    	// log.debug("subb 07-13-17 start");
	    	
	    	generateEmailConfirmationRequestForNewEmail(dbConnector, newEmail);
			Utils.sendHtmlMail(userSession.getEmail(), "WebPortal@galco.com", "You have changed your email.", informOldEmail1 + newEmail + informOldEmail2);

			// log.debug("subb 07-13-17 end");
	    	
			UserSession.expireUserSession(request, response, dbConnector);

	        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	        response.setHeader("Location", "/portal/controller?formFunction=EmailChangeConfirmation");

	        return;
		}
	}
	
	public static void changePassword(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession == null) {
			String messageToUser = null;
			Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser);
			return;
		}

		boolean firstTimeEntry = (request.getParameter("oldPassword") == null);

		if (firstTimeEntry) {
			Utils.forwardToJSP(request, response, "ChangePassword.jsp", null);
		} else {
		    String oldPassword = request.getParameter("oldPassword");
		    String password = request.getParameter("password");

		    try {
				GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(userSession.getUsername(), oldPassword, false);
				if (gluuUser != null) {
					GluuUser.modifyPassword(userSession.getUsername(), password);

				    String encPassword = Utils.encrypt(ControlServlet.GUPTA_PASS_DISPLAY_GUPTAPADAM, password);				
				    log.debug("guptapadam email: " + userSession.getUsername() + " encPassword: " + encPassword);
					
					UserSession.trackUserSession(request, dbConnector, response, userSession.getCust_num(),
							userSession.getCont_no(), userSession.getEmail(), userSession.getUsername());

					// OrderListDataHandler.displayOrderList(request, response, userSession.getCust_num(), "Successfully changed password.");

					HttpSession session = request.getSession(false);
					if (session != null) {
						session.setAttribute("msgFromRedirect", "Successfully changed password.");
					}
			        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			        response.setHeader("Location", "/portal/controller?formFunction=OrderList");

			        return;
				} else {
					request.setAttribute("oldPassword", null);
					request.setAttribute("password", null);
					request.setAttribute("confirmPassword", null);
					Utils.forwardToJSP(request, response, "ChangePassword.jsp", "Incorrect old password, please try again.");
				}
			} catch (PortalException e2) {
				log.debug("Failed to change password. " + e2.getMessage(), e2);

				throw e2;
			}
		}
	}

	// =========================================================================================================
	// qqq

	
	
	// =========================================================================================================
	
	public static void resetForgottenPassword_CreateRequest(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		boolean firstTimeEntry = (request.getParameter("username") == null);

		if (firstTimeEntry) {
			Utils.forwardToJSP(request, response, "ForgotPassword.jsp", null);
		} else {
		    String username = request.getParameter("username");
		    String email = request.getParameter("email");
		    log.debug("username: "  + username);
		    log.debug("email   : "  + email);

		    if (StringUtils.isEmpty(username) && StringUtils.isEmpty(email)) {
				Utils.forwardToJSP(request, response, "ForgotPassword.jsp", "You need to enter either User ID or Email.");
				return;
		    }

		    try {
		    	if (StringUtils.isEmpty(username) == false) {
		    		GluuUser gluuUser = null;
		    		try {
			    		gluuUser = GluuUser.retrieveUserInfoFromGluu(username, null, true);
					} catch (Exception | PortalException e) {
						if (e instanceof PortalException) {
							log.debug("GLUU threw Exception.", ((PortalException) e).getE());
						}
						
						request.setAttribute("username", username);
						request.setAttribute("email", email);
						Utils.forwardToJSP(request, response, "ForgotPassword.jsp", "User ID is not found in our system, please try again.");
						return;
					}
		    		
		    		if (gluuUser == null) {
						request.setAttribute("username", username);
						request.setAttribute("email", email);
						Utils.forwardToJSP(request, response, "ForgotPassword.jsp", "User ID is not found in our system, please try again.");
						return;
				    }
		    		
		    		// qqq
		    		
					ArrayList<Cust> custAl = Cust.getCustForGivenCustNo(dbConnector, gluuUser.getCustNum());
					if ((custAl == null) || (custAl.size() == 0) || (custAl.get(0).getIs_active() == false)) {		    		
						request.setAttribute("username", username);
						request.setAttribute("email", email);
						Utils.forwardToJSP(request, response, "ForgotPassword.jsp", "There is a problem with your account, please contact customer service.");
						return;
					}
		    		
		    		ArrayList<PasswordResetRequests> al = PasswordResetRequests.getPasswordResetRequests(dbConnector, username);
		    		if (al != null) {
		    			PasswordResetRequests.delete(dbConnector, username);
		    		}

		    		String randomKey = RandomStringUtils.random(35, true, true);
		    		String request_time = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
		    		PasswordResetRequests passwordResetRequests = new PasswordResetRequests(request_time, randomKey, username);
		    		passwordResetRequests.persist(dbConnector);

		        	String body = "Key: " + randomKey + "\nTo reset your password, go to \nhttp://" + Parms.HOST_NAME + "/portal/controller?formFunction=ForgotPasswordKeyEntryRequest&username=" + username + " \nand enter the above key.";
		    		Utils.sendMail(gluuUser.getEmail(), "websales@galco.com", "Reset forgotten password request", body);
		    		// Utils.sendMail("sati@galco.com", "websales@galco.com", "Your galco.com password reset request", body);

					request.setAttribute("email", gluuUser.getEmail());
		    		Utils.forwardToJSP(request, response, "ForgotPasswordConfirmation.jsp", null);
		    	} else {
										// String[] usernames = GluuUser.getUsernamesWithAGivenEmail(dbConnector, email);
										// DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER
										// String[] usernames = {"sati@galco.com", "sati@galco.com", "sati@galco.com", "esmith@kerrpump.com"};
					// String[] usernames = GluuUser.getUserIDsWithAGivenEmail_LDAP(dbConnector, email);
					String[] usernames = null;
					try {
						usernames = GluuUser.getUserIDsWithAGivenEmail_UseContactAndSpolicy(dbConnector.getConnectionWDS(), email);
					} catch (SQLException e1) {
						log.debug("Exception while looking for accounts given email:" +email , e1);

						request.setAttribute("email", email);
						Utils.forwardToJSP(request, response, "ForgotPassword.jsp", "We didn't find any accounts with this email, please try again.");
						return;
					}
					if (usernames == null) {
						request.setAttribute("email", email);
						Utils.forwardToJSP(request, response, "ForgotPassword.jsp", "We didn't find any accounts with this email, please try again.");
						return;
					}

					if (usernames.length > 1) {
						request.setAttribute("usernames", usernames);
						Utils.forwardToJSP(request, response, "ForgotPasswordUsernameSelection.jsp", null);
						return;
					} else {
						username = usernames[0];

			    		// qqq
			    		GluuUser gluuUser = null;
			    		try {
				    		gluuUser = GluuUser.retrieveUserInfoFromGluu(username, null, true);
						} catch (Exception e) {
							throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
						} catch (PortalException e) {
							throw e;
						}
						ArrayList<Cust> custAl = Cust.getCustForGivenCustNo(dbConnector, gluuUser.getCustNum());
						if ((custAl == null) || (custAl.size() == 0) || (custAl.get(0).getIs_active() == false)) {		    		
							request.setAttribute("username", "");
							request.setAttribute("email", email);
							Utils.forwardToJSP(request, response, "ForgotPassword.jsp", "There is a problem with your account, please contact customer service.");
							return;
						}
			    		
						ArrayList<PasswordResetRequests> al = PasswordResetRequests.getPasswordResetRequests(dbConnector, username);
			    		if (al != null) {
			    			PasswordResetRequests.delete(dbConnector, username);
			    		}

			    		String randomKey = RandomStringUtils.random(35, true, true);
			    		String request_time = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
			    		PasswordResetRequests passwordResetRequests = new PasswordResetRequests(request_time, randomKey, username);
			    		passwordResetRequests.persist(dbConnector);

			        	String body = "Key: " + randomKey + "\nTo reset your password, go to \nhttp://" + Parms.HOST_NAME + "/portal/controller?formFunction=ForgotPasswordKeyEntryRequest&username=" + username + " \nand enter the above key.";
			    		// Utils.sendMail("sati@galco.com", "websales@galco.com", "Your galco.com password reset request", body);
			    		Utils.sendMail(email, "websales@galco.com", "Reset forgotten password request", body);

						request.setAttribute("email", email);
			    		Utils.forwardToJSP(request, response, "ForgotPasswordConfirmation.jsp", null);
					}
		    	}
			} catch (PortalException e2) {
				log.debug("Failed to change password. " + e2.getMessage(), e2);

				throw e2;
			}
		}
	}

	public static void resetForgottenPassword_UserSelectionHandler(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		String username = request.getParameter("username");

	    try {
	    	if (username == null) {
	    		throw new PortalException(new Exception("Username was not found on user section handler."), PortalException.SEVERITY_LEVEL_3);
	    	}

    		GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(username, null, true);
    		if (gluuUser == null) {
	    		throw new PortalException(new Exception("Username, from user section handler, is not found in our system."), PortalException.SEVERITY_LEVEL_3);
		    }

    		// qqq
    		
			ArrayList<Cust> custAl = Cust.getCustForGivenCustNo(dbConnector, gluuUser.getCustNum());
			if ((custAl == null) || (custAl.size() == 0) || (custAl.get(0).getIs_active() == false)) {		    		
				request.setAttribute("username", username);
				request.setAttribute("email", "");
				Utils.forwardToJSP(request, response, "ForgotPassword.jsp", "There is a problem with your account, please contact customer service.");
				return;
			}
    		
    		ArrayList<PasswordResetRequests> al = PasswordResetRequests.getPasswordResetRequests(dbConnector, username);
    		if (al != null) {
    			PasswordResetRequests.delete(dbConnector, username);
    		}

    		String randomKey = RandomStringUtils.random(35, true, true);
    		String request_time = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
    		PasswordResetRequests passwordResetRequests = new PasswordResetRequests(request_time, randomKey, username);
    		passwordResetRequests.persist(dbConnector);

        	String body = "Key: " + randomKey + "\nTo reset your password, go to \nhttp://" + Parms.HOST_NAME + "/portal/controller?formFunction=ForgotPasswordKeyEntryRequest&username=" + username + " \nand enter the above key.";
    		Utils.sendMail(gluuUser.getEmail(), "websales@galco.com", "Your galco.com password reset request", body);
    		// Utils.sendMail("sati@galco.com", "websales@galco.com", "Your galco.com password reset request", body);

			request.setAttribute("email", gluuUser.getEmail());
			Utils.forwardToJSP(request, response, "ForgotPasswordConfirmation.jsp", null);
		} catch (PortalException e2) {
			log.debug("Failed to change password. " + e2.getMessage(), e2);
			throw e2;
		}
	}

	public static void resetForgottenPassword_KeyEntry(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
	    try {
			String randomKey = request.getParameter("key");
			String username = request.getParameter("username");

			if (StringUtils.isEmpty(username) == true) {
				request.setAttribute("username", username);
				Utils.forwardToJSP(request, response, "ForgotPasswordNoRequests.jsp", null);
				return;
			}

			ArrayList<PasswordResetRequests> al = PasswordResetRequests.getPasswordResetRequests(dbConnector, username);
    		if (al == null) {
				request.setAttribute("username", username);
				Utils.forwardToJSP(request, response, "ForgotPasswordNoRequests.jsp", null);
				return;
    		}

			if (randomKey == null) {
				request.setAttribute("username", username);
				Utils.forwardToJSP(request, response, "ForgotPasswordKeyEntry.jsp", null);
				return;
			}

			PasswordResetRequests passwordResetRequest = al.get(0);
			if (randomKey.compareTo(passwordResetRequest.getRandom_key()) != 0) {
				request.setAttribute("key", randomKey);
				request.setAttribute("username", username);
				Utils.forwardToJSP(request, response, "ForgotPasswordKeyEntry.jsp", "Key you entered didn't match our records, please try again.");
				return;
			}

	        Date requestDate;
			try {
				requestDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(passwordResetRequest.getRequest_time());
			} catch (ParseException e) {
				throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
			}
	        if (new Date().getTime() > (requestDate.getTime() + (FORGOT_PASSWORD_DURATION))) {
    			PasswordResetRequests.delete(dbConnector, username);

				request.setAttribute("key", randomKey);
				request.setAttribute("username", username);
				Utils.forwardToJSP(request, response, "ForgotPasswordKeyEntry.jsp", "Your request has expired, please submit another request.");
				return;
			}

			request.setAttribute("key", randomKey);
			request.setAttribute("username", username);
			Utils.forwardToJSP(request, response, "ForgotPasswordChangePassword.jsp", null);
			return;
		} catch (PortalException e2) {
			log.debug("Failed to change password. " + e2.getMessage(), e2);
			throw e2;
		}
	}

	public static void resetForgottenPassword_ChangePassword(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
	    try {
			String randomKey = request.getParameter("key");
			String username = request.getParameter("username");

			boolean errorOccurred = false;
			if (StringUtils.isEmpty(username) == true) {
				log.debug("Username is blanks");
				errorOccurred = true;
			}
			if (StringUtils.isEmpty(username) == true) {
				log.debug("Randomkey is blanks for the user: " + username);
				errorOccurred = true;
			}

			if (errorOccurred == true) {
				request.setAttribute("messageToUser", null);
				Utils.forwardToJSP(request, response, "ErrorPage.jsp", null);
				return;
			}


			ArrayList<PasswordResetRequests> al = PasswordResetRequests.getPasswordResetRequests(dbConnector, username);
    		if (al == null) {
				request.setAttribute("username", username);
				Utils.forwardToJSP(request, response, "ForgotPasswordNoRequests.jsp", null);
				return;
    		}

			PasswordResetRequests passwordResetRequest = al.get(0);
			if (randomKey.compareTo(passwordResetRequest.getRandom_key()) != 0) {
				request.setAttribute("messageToUser", "Keys didn't match.");
				Utils.forwardToJSP(request, response, "ErrorPage.jsp", null);
				return;
			}

			String password = request.getParameter("password");

			GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(username, null, true);

			GluuUser.modifyPassword(username, password);

			UserSession.trackUserSession(request, dbConnector, response, gluuUser.getCustNum(),
										 gluuUser.getContNo(), gluuUser.getEmail(), gluuUser.getUserName());

			// OrderListDataHandler.displayOrderList(request, response, gluuUser.getCustNum(), "Successfully changed password.");

			HttpSession session = request.getSession(false);
			if (session != null) {
				session.setAttribute("msgFromRedirect", "Successfully changed password.");
			}
	        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	        response.setHeader("Location", "/portal/controller?formFunction=OrderList");

	        return;
		} catch (PortalException e2) {
			log.debug("Failed to change password. " + e2.getMessage(), e2);
			throw e2;
		}
	}

	public static void logOff(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
	    try {
			UserSession.expireUserSession(request, response, dbConnector);

			// Utils.forwardToJSP(request, response, "http:////www.galco.com", null);

			response.setHeader("Location", "http://www.galco.com");
			response.setStatus(HttpServletResponse.SC_FOUND);
		} catch (PortalException e2) {
			log.debug("Failed to logoff the user. " + e2.getMessage(), e2);
			throw e2;
		}
	}
	
	// ------------------------------------------------------------------------------------------------

	public static final String emailConfirmationHtmlP1 = 
			"   <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> " +
			"      <tr> " +
			"         <td align=\"center\"> " +
			"            <table width=\"600\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: 'Open Sans', Verdana, sans-serif; color: #0092D0; max-width: 600px; width: 600px; text-align: center; margin: 0 auto;\"> " +
			"               <tr style=\"background-color: #0092D0;\"> " +
			"                  <td style=\"font-size: 34px; color: #FFF; font-weight: 300; padding: 10px;\" align=\"center\"> " +
			"                     <a href=\"tel:800-575-5562\" style=\"text-decoration: none !important; text-decoration: none; color: #FFF !important; color: #FFF;\">800-575-5562</a> " +
			"                  </td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 10px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td> " +
			"                     <a href=\"http://www.galco.com/default.htm?source=confirmEmail\"> " +
			"                        <img src=\"http://www.galco.com/images/email/assets/logo.gif?source=confirmEmail\" alt=\"Galco Industrial Electronics\" width=\"600\" height=\"105\"> " +
			"                     </a> " +
			"                  </td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 25px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td> " +
			"                     <table style=\"width: 100%; margin-bottom: 30px; font-family: 'Open Sans', Verdana, sans-serif; color: #0092D0;\"> " +
			"                        <tr> " +
			"                           <td align=\"center\"> " +
			"                              <h1 style=\"font-weight: 300;margin-top: 0; margin-bottom: 0; font-size: 44px;\">Thank You</h1> " +
			"                              <h2 style=\"font-weight: 300;margin-top: 0; margin-bottom: 5px; font-size: 20px;\">For creating your Galco Account!</h2> " +
			"                           </td> " +
			"                        </tr> " +
			"                        <tr> " +
			"                           <td align=\"center\" style=\"color: #000;\">Please click the button below to confirm your new account.</td> " +
			"                        </tr> " +
			"                     </table> " +
			"                  </td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 25px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td align=\"center\"> " +
			"                     <table> " +
			"                        <tr> " +
			"                           <td></td> " +
			"                           <td> " +
			"                              <a href=\"";
			
public static final String emailConfirmationHtmlP2 = 
			"\"> " +
			"                                 <img src=\"http://www.galco.com/images/email/assets/confirm-account-button.gif?source=confirmEmail\" alt=\"Confirm Account\"> " +
			"                              </a> " +
			"                           </td> " +
			"                           <td></td> " +
			"                        </tr> " +
			"                     </table> " +
			"                  </td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 50px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"margin: 20px; border-top: 1px solid #ccc;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 50px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"font-size: 30px; font-weight: 600;\" align=\"center\"> " +
			"                     <a href=\"tel:800-575-5562\" style=\"text-decoration: none !important; text-decoration: none;color:#0092D0 !important;color:#0092D0;\">800-575-5562</a> " +
			"                  </td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 5px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td align=\"center\" style=\"font-size: 26px; font-weight: 600;\"> " +
			"                     <a href=\"http://www.galco.com/default.htm?source=confirmEmail\" style=\"text-decoration: none !important; text-decoration: none;color:#0092D0 !important;color:#0092D0;\">galco.com</a> " +
			"                  </td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 15px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td align=\"center\"> " +
			"                     <table> " +
			"                        <tr> " +
			"                           <td> " +
			"                              <a href=\"https://www.facebook.com/pages/Galco-Industrial-Electronics/135852279819123\" target=\"_blank\"> " +
			"                                 <img src=\"http://www.galco.com/images/email/assets/fb-icon.gif?source=confirmEmail\" width=\"30\" height=\"30\" alt=\"Facebook\"> " +
			"                              </a> " +
			"                           </td> " +
			"                           <td> " +
			"                              <a href=\"http://twitter.com/GalcoIndustrial\" target=\"_blank\"> " +
			"                                 <img src=\"http://www.galco.com/images/email/assets/twitter-icon.gif?source=confirmEmail\" width=\"30\" height=\"30\" alt=\"Twitter\"> " +
			"                              </a> " +
			"                           </td> " +
			"                           <td> " +
			"                              <a href=\"http://www.youtube.com/user/GalcoTV\" target=\"_blank\"> " +
			"                                 <img src=\"http://www.galco.com/images/email/assets/youtube-icon.gif?source=confirmEmail\" width=\"30\" height=\"30\" alt=\"Youtube\"> " +
			"                              </a> " +
			"                           </td> " +
			"                           <td> " +
			"                              <a href=\"https://www.linkedin.com/company/galco-industrial-electronics\" target=\"_blank\"> " +
			"                                 <img src=\"http://www.galco.com/images/email/assets/linked-in-icon.gif?source=confirmEmail\" width=\"30\" height=\"30\" alt=\"LinkedIn\"> " +
			"                              </a> " +
			"                           </td> " +
			"                           <td> " +
			"                              <a href=\"https://plus.google.com/103956110749171045161/about\" target=\"_blank\"> " +
			"                                 <img src=\"http://www.galco.com/images/email/assets/google-plus-icon.gif?source=confirmEmail\" width=\"30\" height=\"30\" alt=\"Google+\"> " +
			"                              </a> " +
			"                           </td> " +
			"                        </tr> " +
			"                     </table> " +
			"                  </td> " +
			"               </tr> " +
			"            </table> " +
			"         </td> " +
			"      </tr> " +
			"   </table> ";			


	public static void generateEmailConfirmationRequest(DBConnector dbConnector, String email) throws PortalException {
		try {
			String randomKey = RandomStringUtils.random(35, true, true);
			String request_time = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
			EmailConfirmations emailConfirmations = new EmailConfirmations(request_time, randomKey, email);
			emailConfirmations.persist(dbConnector);

	    	String urlForConfirmingEmail = "http://" + Parms.HOST_NAME + "/portal/controller?formFunction=ConfirmEmail&username=" + email + "&key=" + randomKey;

			log.debug("Subb0-01-17-2017 Registration is successfully completed. 01-17-2017 email: " + email + ". urlForConfirmingEmail: " + urlForConfirmingEmail);
	    	
			Utils.sendHtmlMail(email, "WebPortal@galco.com", "Please confirm your e-mail", emailConfirmationHtmlP1 + urlForConfirmingEmail + emailConfirmationHtmlP2);
			// Utils.sendHtmlMail("sati@galco.com", "WebPortal@galco.com", "Please confirm your new Galco account", emailConfirmationHtmlP1 + urlForConfirmingEmail + emailConfirmationHtmlP2);
		} catch (PortalException e2) {
			log.debug("Failed to change password. " + e2.getMessage(), e2);
	
			throw e2;
		}
	}
	
	public static final String newEmailConfirmationHtmlP1 = 
			"   <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> " +
			"      <tr> " +
			"         <td align=\"center\"> " +
			"            <table width=\"600\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: 'Open Sans', Verdana, sans-serif; color: #0092D0; max-width: 600px; width: 600px; text-align: center; margin: 0 auto;\"> " +
			"               <tr style=\"background-color: #0092D0;\"> " +
			"                  <td style=\"font-size: 34px; color: #FFF; font-weight: 300; padding: 10px;\" align=\"center\"> " +
			"                     <a href=\"tel:800-575-5562\" style=\"text-decoration: none !important; text-decoration: none; color: #FFF !important; color: #FFF;\">800-575-5562</a> " +
			"                  </td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 10px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td> " +
			"                     <a href=\"http://www.galco.com/default.htm?source=confirmEmail\"> " +
			"                        <img src=\"http://www.galco.com/images/email/assets/logo.gif?source=confirmEmail\" alt=\"Galco Industrial Electronics\" width=\"600\" height=\"105\"> " +
			"                     </a> " +
			"                  </td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 25px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td> " +
			"                     <table style=\"width: 100%; margin-bottom: 30px; font-family: 'Open Sans', Verdana, sans-serif; color: #0092D0;\"> " +
			"                        <tr> " +
			"                           <td align=\"center\"> " +
			"                              <h1 style=\"font-weight: 300;margin-top: 0; margin-bottom: 0; font-size: 44px;\">Your email has been changed.</h1> " +
			"                              <h2 style=\"font-weight: 300;margin-top: 0; margin-bottom: 5px; font-size: 20px;\">Your new email is: ";
			
	public static final String newEmailConfirmationHtmlP2 = 
			".</h2> " +
			"                              <h2 style=\"font-weight: 300;margin-top: 0; margin-bottom: 5px; font-size: 20px;\">You need to use this email as your login id from now on.</h2> " +
			"                           </td> " +
			"                        </tr> " +
			"                        <tr> " +
			"                           <td align=\"center\" style=\"color: #000;\">Please click the button below to confirm your new email.</td> " +
			"                        </tr> " +
			"                     </table> " +
			"                  </td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 25px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td align=\"center\"> " +
			"                     <table> " +
			"                        <tr> " +
			"                           <td></td> " +
			"                           <td> " +
			"                              <a href=\"";
			
	public static final String newEmailConfirmationHtmlP3 = 
			"\"> " +
			"                                 <img src=\"http://www.galco.com/images/email/assets/confirm-account-button.gif?source=confirmEmail\" alt=\"Confirm new email.\"> " +
			"                              </a> " +
			"                           </td> " +
			"                           <td></td> " +
			"                        </tr> " +
			"                     </table> " +
			"                  </td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 50px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"margin: 20px; border-top: 1px solid #ccc;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 50px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"font-size: 30px; font-weight: 600;\" align=\"center\"> " +
			"                     <a href=\"tel:800-575-5562\" style=\"text-decoration: none !important; text-decoration: none;color:#0092D0 !important;color:#0092D0;\">800-575-5562</a> " +
			"                  </td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 5px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td align=\"center\" style=\"font-size: 26px; font-weight: 600;\"> " +
			"                     <a href=\"http://www.galco.com/default.htm?source=confirmEmail\" style=\"text-decoration: none !important; text-decoration: none;color:#0092D0 !important;color:#0092D0;\">galco.com</a> " +
			"                  </td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 15px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td align=\"center\"> " +
			"                     <table> " +
			"                        <tr> " +
			"                           <td> " +
			"                              <a href=\"https://www.facebook.com/pages/Galco-Industrial-Electronics/135852279819123\" target=\"_blank\"> " +
			"                                 <img src=\"http://www.galco.com/images/email/assets/fb-icon.gif?source=confirmEmail\" width=\"30\" height=\"30\" alt=\"Facebook\"> " +
			"                              </a> " +
			"                           </td> " +
			"                           <td> " +
			"                              <a href=\"http://twitter.com/GalcoIndustrial\" target=\"_blank\"> " +
			"                                 <img src=\"http://www.galco.com/images/email/assets/twitter-icon.gif?source=confirmEmail\" width=\"30\" height=\"30\" alt=\"Twitter\"> " +
			"                              </a> " +
			"                           </td> " +
			"                           <td> " +
			"                              <a href=\"http://www.youtube.com/user/GalcoTV\" target=\"_blank\"> " +
			"                                 <img src=\"http://www.galco.com/images/email/assets/youtube-icon.gif?source=confirmEmail\" width=\"30\" height=\"30\" alt=\"Youtube\"> " +
			"                              </a> " +
			"                           </td> " +
			"                           <td> " +
			"                              <a href=\"https://www.linkedin.com/company/galco-industrial-electronics\" target=\"_blank\"> " +
			"                                 <img src=\"http://www.galco.com/images/email/assets/linked-in-icon.gif?source=confirmEmail\" width=\"30\" height=\"30\" alt=\"LinkedIn\"> " +
			"                              </a> " +
			"                           </td> " +
			"                           <td> " +
			"                              <a href=\"https://plus.google.com/103956110749171045161/about\" target=\"_blank\"> " +
			"                                 <img src=\"http://www.galco.com/images/email/assets/google-plus-icon.gif?source=confirmEmail\" width=\"30\" height=\"30\" alt=\"Google+\"> " +
			"                              </a> " +
			"                           </td> " +
			"                        </tr> " +
			"                     </table> " +
			"                  </td> " +
			"               </tr> " +
			"            </table> " +
			"         </td> " +
			"      </tr> " +
			"   </table> ";			

	public static final String informOldEmail1 = 
			"   <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> " +
			"      <tr> " +
			"         <td align=\"center\"> " +
			"            <table width=\"600\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: 'Open Sans', Verdana, sans-serif; color: #0092D0; max-width: 600px; width: 600px; text-align: center; margin: 0 auto;\"> " +
			"               <tr style=\"background-color: #0092D0;\"> " +
			"                  <td style=\"font-size: 34px; color: #FFF; font-weight: 300; padding: 10px;\" align=\"center\"> " +
			"                     <a href=\"tel:800-575-5562\" style=\"text-decoration: none !important; text-decoration: none; color: #FFF !important; color: #FFF;\">800-575-5562</a> " +
			"                  </td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 10px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td> " +
			"                     <a href=\"http://www.galco.com/default.htm?source=confirmEmail\"> " +
			"                        <img src=\"http://www.galco.com/images/email/assets/logo.gif?source=confirmEmail\" alt=\"Galco Industrial Electronics\" width=\"600\" height=\"105\"> " +
			"                     </a> " +
			"                  </td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 25px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td> " +
			"                     <table style=\"width: 100%; margin-bottom: 30px; font-family: 'Open Sans', Verdana, sans-serif; color: #0092D0;\"> " +
			"                        <tr> " +
			"                           <td align=\"center\"> " +
			"                              <h1 style=\"font-weight: 300;margin-top: 0; margin-bottom: 0; font-size: 44px;\">You just changed your email.</h1> " +
			"                              <h2 style=\"font-weight: 300;margin-top: 0; margin-bottom: 5px; font-size: 20px;\">Your new email is: ";
			
	public static final String informOldEmail2 = 
			".</h2> " +
			"                              <h2 style=\"font-weight: 300;margin-top: 0; margin-bottom: 5px; font-size: 20px;\">You need to use this email as your login id from now on.</h2> " +
			"                           </td> " +
			"                        </tr> " +
			"                     </table> " +
			"                  </td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 25px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 50px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"margin: 20px; border-top: 1px solid #ccc;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 50px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"font-size: 30px; font-weight: 600;\" align=\"center\"> " +
			"                     <a href=\"tel:800-575-5562\" style=\"text-decoration: none !important; text-decoration: none;color:#0092D0 !important;color:#0092D0;\">800-575-5562</a> " +
			"                  </td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 5px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td align=\"center\" style=\"font-size: 26px; font-weight: 600;\"> " +
			"                     <a href=\"http://www.galco.com/default.htm?source=confirmEmail\" style=\"text-decoration: none !important; text-decoration: none;color:#0092D0 !important;color:#0092D0;\">galco.com</a> " +
			"                  </td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td style=\"padding-top: 15px;\"></td> " +
			"               </tr> " +
			"               <tr> " +
			"                  <td align=\"center\"> " +
			"                     <table> " +
			"                        <tr> " +
			"                           <td> " +
			"                              <a href=\"https://www.facebook.com/pages/Galco-Industrial-Electronics/135852279819123\" target=\"_blank\"> " +
			"                                 <img src=\"http://www.galco.com/images/email/assets/fb-icon.gif?source=confirmEmail\" width=\"30\" height=\"30\" alt=\"Facebook\"> " +
			"                              </a> " +
			"                           </td> " +
			"                           <td> " +
			"                              <a href=\"http://twitter.com/GalcoIndustrial\" target=\"_blank\"> " +
			"                                 <img src=\"http://www.galco.com/images/email/assets/twitter-icon.gif?source=confirmEmail\" width=\"30\" height=\"30\" alt=\"Twitter\"> " +
			"                              </a> " +
			"                           </td> " +
			"                           <td> " +
			"                              <a href=\"http://www.youtube.com/user/GalcoTV\" target=\"_blank\"> " +
			"                                 <img src=\"http://www.galco.com/images/email/assets/youtube-icon.gif?source=confirmEmail\" width=\"30\" height=\"30\" alt=\"Youtube\"> " +
			"                              </a> " +
			"                           </td> " +
			"                           <td> " +
			"                              <a href=\"https://www.linkedin.com/company/galco-industrial-electronics\" target=\"_blank\"> " +
			"                                 <img src=\"http://www.galco.com/images/email/assets/linked-in-icon.gif?source=confirmEmail\" width=\"30\" height=\"30\" alt=\"LinkedIn\"> " +
			"                              </a> " +
			"                           </td> " +
			"                           <td> " +
			"                              <a href=\"https://plus.google.com/103956110749171045161/about\" target=\"_blank\"> " +
			"                                 <img src=\"http://www.galco.com/images/email/assets/google-plus-icon.gif?source=confirmEmail\" width=\"30\" height=\"30\" alt=\"Google+\"> " +
			"                              </a> " +
			"                           </td> " +
			"                        </tr> " +
			"                     </table> " +
			"                  </td> " +
			"               </tr> " +
			"            </table> " +
			"         </td> " +
			"      </tr> " +
			"   </table> ";			

	public static void generateEmailConfirmationRequestForNewEmail(DBConnector dbConnector, String email) throws PortalException {
		try {
    		String deleteQueryString = "delete from pub.EmailConfirmations where username = '" + email + "'";
    		JDBCUtils.executeUpdateSRO(dbConnector, deleteQueryString);
			
			String randomKey = RandomStringUtils.random(35, true, true);
			String request_time = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
			EmailConfirmations emailConfirmations = new EmailConfirmations(request_time, randomKey, email);
			emailConfirmations.persist(dbConnector);

	    	String urlForConfirmingEmail = "https://" + Parms.HOST_NAME + "/portal/controller?formFunction=ConfirmNewEmail&username=" + email + "&key=" + randomKey;

			log.debug("Changed email successfully, new email: " + email + ". urlForConfirmingEmail: " + urlForConfirmingEmail);
	    	
			Utils.sendHtmlMail(email, "WebPortal@galco.com", "Please confirm your new e-mail", newEmailConfirmationHtmlP1 + email + newEmailConfirmationHtmlP2 + urlForConfirmingEmail + newEmailConfirmationHtmlP3);					
		} catch (PortalException e2) {
			log.debug("Failed to change email. " + e2.getMessage(), e2);
	
			throw e2;
		}
	}
	
}
