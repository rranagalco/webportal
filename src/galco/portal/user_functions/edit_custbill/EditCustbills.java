package galco.portal.user_functions.edit_custbill;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import galco.portal.config.Parms;
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
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.Contact;
import galco.portal.wds.dao.Cust;
import galco.portal.wds.dao.Custbill;
import galco.portal.wds.dao.Spolicy;
import galco.portal.wds.dao.UserSession;
import galco.portal.wds.matcher.CustNumBasedExistingContactMatcher;
import galco.portal.wds.matcher.CustNumBasedMatcherResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class EditCustbills {
	private static Logger log = Logger.getLogger(EditCustbills.class);
	
	public static void editCustbills(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession == null) {
			String messageToUser = null;
			Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser);
			return;
		}
		
		GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(userSession.getUsername(), null, true);
		
		Cust cust = Cust.getCustForGivenCustNo(dbConnector, userSession.getCust_num()).get(0);
		Contact contact = Contact.getContact(dbConnector, userSession.getCust_num(), userSession.getCont_no()).get(0);
		ArrayList<Custbill> custbillAL;
		if (gluuUser.getAccessRole().compareTo("2") >= 0) {
			custbillAL = Custbill.getCustbillsForGivenCustNo(dbConnector, userSession.getCust_num());
		} else {
			custbillAL = Custbill.getCustbillsForGivenCustNoContNo(dbConnector, userSession.getCust_num(), userSession.getCont_no());
		}

		String dfltAddrBill = gluuUser.getDfltAddrBill();
		Custbill dfltCustbill = null;
		if ((StringUtils.isBlank(dfltAddrBill) == false) && (dfltAddrBill.compareTo("78z7Sw4T1@3%") != 0)) {
			ArrayList<Custbill> dfltCustbillAL = Custbill.getCustbillsForGivenCustNoAndBillToNum(dbConnector, userSession.getCust_num(), dfltAddrBill);
			if ((dfltCustbillAL != null) && (dfltCustbillAL.size() > 0)) {
				dfltCustbill = dfltCustbillAL.get(0);
				
				for (Iterator<Custbill> iterator = custbillAL.iterator(); iterator.hasNext();) {
					Custbill custbill = iterator.next();
					if (custbill.getBillto_num().compareTo(dfltCustbill.getBillto_num()) == 0) {
						iterator.remove();
						break;
					}
				}
			}
		}		
		
		if ((custbillAL != null) && (custbillAL.size() == 0)) {
			custbillAL = null;
		}

		String messageToUser = null;
		HttpSession session = request.getSession(false);
		if (session != null) {
			messageToUser = (String) session.getAttribute("messageToUser");
			session.invalidate();
		}
		
		request.setAttribute("cust", cust);
		request.setAttribute("contact", contact);
		request.setAttribute("dfltCustbill", dfltCustbill);
		request.setAttribute("custbillAL", custbillAL);
		
		String profileEditing = request.getParameter("profileEditing");
		if ((profileEditing != null) && (profileEditing.compareToIgnoreCase("y") == 0)) {
			request.setAttribute("profileEditing", "y");			
		} else {
			request.setAttribute("profileEditing", null);			
		}

		Utils.forwardToJSP(request, response, "EditCustbills.jsp", messageToUser);
	}

	public static void removeCustbill(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession == null) {
			String messageToUser = null;
			Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser);
			return;
		}
		
		String messageToUser = null;
		
		GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(userSession.getUsername(), null, true);
		
		String billto_num = request.getParameter("billto_num");
		log.debug("Trying to remove BillTo " + billto_num);

		if (StringUtils.isBlank(billto_num) == false) {
			ArrayList<Custbill> custbillAL = Custbill.getCustbillsForGivenCustNoAndBillToNum(dbConnector, userSession.getCust_num(), billto_num);
			if ((custbillAL != null) && (custbillAL.size() > 0)) {
				Custbill custbill = custbillAL.get(0);

				if (gluuUser.getAccessRole().compareTo("2") >= 0) {
					Custbill.inactivate(dbConnector, custbill.getCust_num(), custbill.getBillto_num());
					
					String dfltAddrBill = gluuUser.getDfltAddrBill();
					log.debug("dfltAddrBill:***" + dfltAddrBill + "***, billto_num:***" + billto_num + "***");
					if ((dfltAddrBill != null) && (dfltAddrBill.compareTo(billto_num) == 0)) {
						log.debug("dfltAddrBill is cleared.");
						// gluuUser.modify(false);
						gluuUser.modifyCustomAttribute("dfltAddrBill", "78z7Sw4T1@3%");
						Spolicy.update_dfltAddrBill(dbConnector, gluuUser.getUserName(), "");
					}

					messageToUser = "Successfully removed the selected billing address.";					
				} else {
					if (custbill.getCont_no() == gluuUser.getContNo()) {
						Custbill.inactivate(dbConnector, custbill.getCust_num(), custbill.getBillto_num());

						String dfltAddrBill = gluuUser.getDfltAddrBill();
						log.debug("dfltAddrBill:***" + dfltAddrBill + "***, billto_num:***" + billto_num + "***");
						if ((dfltAddrBill != null) && (dfltAddrBill.compareTo(billto_num) == 0)) {
							log.debug("dfltAddrBill is cleared.");
							// gluuUser.modify(false);
							gluuUser.modifyCustomAttribute("dfltAddrBill", "78z7Sw4T1@3%");
							Spolicy.update_dfltAddrBill(dbConnector, gluuUser.getUserName(), "");							
						}

						messageToUser = "Successfully removed the selected billing address.";
					} else {
						messageToUser = "You don't have access to remove this billing address. Please contact customer service.";
					}
				}
			} else {
				messageToUser = "Billing address is not found in our system. Please contact customer service.";				
			}
		} else {
			messageToUser = "You can't remove this billing address. Please contact customer service.";			
		}
		
		HttpSession session = request.getSession(true);
		session.setAttribute("messageToUser", messageToUser);
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", "/portal/controller?formFunction=EditCustbills");		
	}

	public static void selectCustbill(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession == null) {
			String messageToUser = null;
			Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser);
			return;
		}
		
		String messageToUser = null;
		
		GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(userSession.getUsername(), null, true);
		
		String billto_num = request.getParameter("billto_num");
		boolean makeDefault = false;
		{
			String makeDefaultS = request.getParameter("makeDefault");
			if ((makeDefaultS == null) || (makeDefaultS.compareToIgnoreCase("n") == 0)) {
				makeDefault = false;
			} else {
				makeDefault = true;
			}
		}
		// if ((makeDefault == true) && (StringUtils.isBlank(billto_num) == false)) {
		if (makeDefault == true) {
			if (StringUtils.isBlank(billto_num) == false) {
				gluuUser.modifyCustomAttribute("dfltAddrBill", billto_num);
				Spolicy.update_dfltAddrBill(dbConnector, gluuUser.getUserName(), billto_num);			
			} else {
				gluuUser.modifyCustomAttribute("dfltAddrBill", "78z7Sw4T1@3%");
				Spolicy.update_dfltAddrBill(dbConnector, gluuUser.getUserName(), "");				
			}
		}
		
		log.debug("Trying to select BillTo " + billto_num);		
		
		if (StringUtils.isBlank(billto_num) == true) {
			billto_num = "cust";
		}
 
		response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		// response.setHeader("Location", "https://" + Parms.HOST_NAME + "/scripts/cgiip.exe/wa/wcat/checkout.htm?billto=" + billto_num);
		response.setHeader("Location", "https://" + Parms.HOST_NAME + "/scripts/cgiip.exe/wa/wcat/checkout.htm?checkoutstep=billing&change_address=yes&billto=" + billto_num);
	}
	
	public static void changeDefaultCustbill(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession == null) {
			String messageToUser = null;
			Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser);
			return;
		}
			
		GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(userSession.getUsername(), null, true);
		
		String billto_num = request.getParameter("billto_num");
		if (StringUtils.isBlank(billto_num) == false) {
			gluuUser.modifyCustomAttribute("dfltAddrBill", billto_num);
			Spolicy.update_dfltAddrBill(dbConnector, gluuUser.getUserName(), billto_num);			
		} else {
			gluuUser.modifyCustomAttribute("dfltAddrBill", "78z7Sw4T1@3%");
			Spolicy.update_dfltAddrBill(dbConnector, gluuUser.getUserName(), "");				
		}
		
		HttpSession session = request.getSession(true);
		session.setAttribute("messageToUser", "Default billing address has been changed successfully.");
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", "/portal/controller?formFunction=EditCustbills&profileEditing=y");	
	}
	
	public static void editCustbill(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession == null) {
			String messageToUser = null;
			Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser);
			return;
		}
		
		String billto_num = request.getParameter("billto_num");
		log.debug("Trying to edit BillTo " + billto_num);

		GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(userSession.getUsername(), null, true);

		Custbill custbill = null;
		if (StringUtils.isBlank(billto_num) == false) {
			ArrayList<Custbill> custbillAL = Custbill.getCustbillsForGivenCustNoAndBillToNum(dbConnector, userSession.getCust_num(), billto_num);
			if ((custbillAL != null) && (custbillAL.size() > 0)) {
				custbill = custbillAL.get(0);
			}
		}
		if (custbill == null) {
			HttpSession session = request.getSession(true);
			session.setAttribute("messageToUser", "Billing address is not found.");
	        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	        response.setHeader("Location", "/portal/controller?formFunction=EditCustbills");	
	        return;
		}
				
	    String name = request.getParameter("name");
		if (name == null) {
			request.setAttribute("billto_num", billto_num);
			
			request.setAttribute("name", custbill.getName());
			request.setAttribute("address", custbill.getAddress());
			request.setAttribute("address2", custbill.getAddress2());
			request.setAttribute("city", custbill.getCity());
			request.setAttribute("state", custbill.getState());
			request.setAttribute("zip", custbill.getZip());
			
			if ((custbill.getCountry().compareToIgnoreCase("US") == 0 ) ||
			    (custbill.getCountry().compareToIgnoreCase("USA") == 0)    ) {
				request.setAttribute("country", "United States");
			}
			
			request.setAttribute("phoneWork", custbill.getPhone());
			
			request.setAttribute("addAddress", null);

			String profileEditing = request.getParameter("profileEditing");
			if ((profileEditing != null) && (profileEditing.compareToIgnoreCase("y") == 0)) {
				request.setAttribute("profileEditing", "y");			
			} else {
				request.setAttribute("profileEditing", null);			
			}
			
			Utils.forwardToJSP(request, response, "EditCustbill.jsp", null);
			return;
		} else {
			if (StringUtils.isBlank(name) == true) {
				Cust cust = Cust.getCustForGivenCustNo(dbConnector, userSession.getCust_num()).get(0);
				name = cust.getName();
			}			
			
		    String address = request.getParameter("address");
		    String address2 = request.getParameter("address2");
		    String city = request.getParameter("city");
		    String state = request.getParameter("state");
		    String zip = request.getParameter("zip");
		    String country = request.getParameter("country");
			if (country.compareToIgnoreCase("United States") == 0 ) {
				country = "USA";
			}
		    String phoneWork = request.getParameter("phoneWork");

		    log.debug("\n\n\n");
		    log.debug("name: " + name);
		    log.debug("address: " + address);
		    log.debug("address2: " + address2);
		    log.debug("city: " + city);
		    log.debug("state: " + state);
		    log.debug("zip: " + zip);
		    log.debug("country: " + country);
		    log.debug("phoneWork: " + phoneWork);
		    
		    // Custbill.inactivate(dbConnector, custbill.getCust_num(), custbill.getBillto_num());

		    // custbill.setBillto_num(billto_num);
		    custbill.setCont_no(userSession.getCont_no());
		    
		    custbill.setName(name);
		    custbill.setAddress(address);
		    custbill.setAddress2(address2);
		    custbill.setCity(city);
		    
			if ((state != null) && (state.length() > 2)) {
				log.debug("State has length > 2. Truncating... State b4: " + state + ", state after truncation: " + state.substring(0, 2));					
				state = state.substring(0, 2);					
			}		    
		    custbill.setState(state);
		    
		    custbill.setZip(zip);
		    custbill.setCountry(country);
		    
		    // custbill.setPhone(phoneWork);
		    Long parsedPhone = Utils.extractPhoneNumberFromString(phoneWork);
		    if (parsedPhone != null) {
		    	custbill.setPhone(parsedPhone.toString());
		    } else {
		    	custbill.setPhone(phoneWork);
		    }		    
		    
            custbill.setAudit_date(new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
            custbill.setAudit_time(new SimpleDateFormat("HH:mm:ss").format(new Date()));
            custbill.setAudit_userid("WEB");
            
    		if (gluuUser.getAccessRole().compareTo("1") >= 0) {
                custbill.setChanged_online(false);
    		} else {
                custbill.setChanged_online(true);    			
    		}
		    
    		String old_billto_num = billto_num;
		    
    		try {
        		custbill.persist(dbConnector);        		
			} catch (PortalException e) {
				HttpSession session = request.getSession(true);
				session.setAttribute("messageToUser", "Failed to change address.");
		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=EditCustbills");
		        return;
		    }
		    
			boolean makeDefault = false;
			{
				String makeDefaultS = request.getParameter("makeDefault");
			    log.debug("makeDefault: " + makeDefault);				
				if ((makeDefaultS == null) || (makeDefaultS.compareToIgnoreCase("n") == 0)) {
					makeDefault = false;
				} else {
					makeDefault = true;
				}
			}
			if ((makeDefault == true										) ||
				(old_billto_num.compareTo(gluuUser.getDfltAddrBill()) == 0	)    ) {
				gluuUser.modifyCustomAttribute("dfltAddrBill", custbill.getBillto_num());
				Spolicy.update_dfltAddrBill(dbConnector, gluuUser.getUserName(), custbill.getBillto_num());			
			}

			HttpSession session = request.getSession(true);
			session.setAttribute("messageToUser", "Billing address has been changed successfully.");
	        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	        
			String profileEditing = request.getParameter("profileEditing");
			if ((profileEditing != null) && (profileEditing.compareToIgnoreCase("y") == 0)) {
		        response.setHeader("Location", "/portal/controller?formFunction=EditCustbills&profileEditing=y");				
			} else {
		        response.setHeader("Location", "/portal/controller?formFunction=EditCustbills");				
			}
		}
	}
	
	
	public static void addCustbill(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession == null) {
			String messageToUser = null;
			Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser);
			return;
		}
		
		log.debug("Trying to add BillTo");

		GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(userSession.getUsername(), null, true);
			
	    String name = request.getParameter("name");
		if (name == null) {
			request.setAttribute("addAddress", "y");

			String profileEditing = request.getParameter("profileEditing");
			if ((profileEditing != null) && (profileEditing.compareToIgnoreCase("y") == 0)) {
				request.setAttribute("profileEditing", "y");			
			} else {
				request.setAttribute("profileEditing", null);			
			}
			
			Utils.forwardToJSP(request, response, "EditCustbill.jsp", null);
			return;
		} else {
			if (StringUtils.isBlank(name) == true) {
				Cust cust = Cust.getCustForGivenCustNo(dbConnector, userSession.getCust_num()).get(0);
				name = cust.getName();
			}			
			
		    String address = request.getParameter("address");
		    String address2 = request.getParameter("address2");
		    String city = request.getParameter("city");
		    String state = request.getParameter("state");
		    String zip = request.getParameter("zip");
		    String country = request.getParameter("country");
			if (country.compareToIgnoreCase("United States") == 0 ) {
				country = "USA";
			}
		    String phoneWork = request.getParameter("phoneWork");

		    log.debug("\n\n\n");
		    log.debug("name: " + name);
		    log.debug("address: " + address);
		    log.debug("address2: " + address2);
		    log.debug("city: " + city);
		    log.debug("state: " + state);
		    log.debug("zip: " + zip);
		    log.debug("country: " + country);
		    log.debug("phoneWork: " + phoneWork);
		    
		    Custbill custbill = new Custbill();
		    
		    custbill.setCust_num(userSession.getCust_num());
		    
		    custbill.setCont_no(userSession.getCont_no());
		    
		    custbill.setName(name);
		    custbill.setAddress(address);
		    custbill.setAddress2(address2);
		    custbill.setCity(city);

			if ((state != null) && (state.length() > 2)) {
				log.debug("State has length > 2. Truncating... State b4: " + state + ", state after truncation: " + state.substring(0, 2));					
				state = state.substring(0, 2);					
			}		    
		    custbill.setState(state);

		    custbill.setZip(zip);
		    custbill.setCountry(country);
		    
		    // custbill.setPhone(phoneWork);
		    Long parsedPhone = Utils.extractPhoneNumberFromString(phoneWork);
		    if (parsedPhone != null) {
		    	custbill.setPhone(parsedPhone.toString());
		    } else {
		    	custbill.setPhone(phoneWork);
		    }		    
		    
            custbill.setAudit_date(new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
            custbill.setAudit_time(new SimpleDateFormat("HH:mm:ss").format(new Date()));
            custbill.setAudit_userid("WEB");
            
    		if (gluuUser.getAccessRole().compareTo("1") >= 0) {
                custbill.setChanged_online(false);
    		} else {
                custbill.setChanged_online(true);    			
    		}
		    	    
    		try {
        		custbill.persist(dbConnector);        		
			} catch (PortalException e) {
				HttpSession session = request.getSession(true);
				session.setAttribute("messageToUser", "Failed to change address.");
		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=EditCustbills");
		        return;
		    }
		    
			boolean makeDefault = false;
			{
				String makeDefaultS = request.getParameter("makeDefault");
			    log.debug("makeDefault: " + makeDefault);				
				if ((makeDefaultS == null) || (makeDefaultS.compareToIgnoreCase("n") == 0)) {
					makeDefault = false;
				} else {
					makeDefault = true;
				}
			}
			if (makeDefault == true) {
				gluuUser.modifyCustomAttribute("dfltAddrBill", custbill.getBillto_num());
				Spolicy.update_dfltAddrBill(dbConnector, gluuUser.getUserName(), custbill.getBillto_num());			
			}
		       	
			HttpSession session = request.getSession(true);
			session.setAttribute("messageToUser", "Billing address has been added successfully.");
	        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	        
			String profileEditing = request.getParameter("profileEditing");
			if ((profileEditing != null) && (profileEditing.compareToIgnoreCase("y") == 0)) {
		        response.setHeader("Location", "/portal/controller?formFunction=EditCustbills&profileEditing=y");				
			} else {
		        response.setHeader("Location", "/portal/controller?formFunction=EditCustbills");				
			}
		}
	}
}
