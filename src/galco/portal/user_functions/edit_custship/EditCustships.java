package galco.portal.user_functions.edit_custship;

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
import galco.portal.wds.dao.Custship;
import galco.portal.wds.dao.Spolicy;
import galco.portal.wds.dao.UserSession;
import galco.portal.wds.matcher.CustNumBasedExistingContactMatcher;
import galco.portal.wds.matcher.CustNumBasedMatcherResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class EditCustships {
	private static Logger log = Logger.getLogger(EditCustships.class);
	
	public static void editCustships(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession == null) {
			String messageToUser = null;
			Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser);
			return;
		}
		
		GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(userSession.getUsername(), null, true);
		
		Cust cust = Cust.getCustForGivenCustNo(dbConnector, userSession.getCust_num()).get(0);
		Contact contact = Contact.getContact(dbConnector, userSession.getCust_num(), userSession.getCont_no()).get(0);
		ArrayList<Custship> custshipAL;
		if (gluuUser.getAccessRole().compareTo("2") >= 0) {
			custshipAL = Custship.getCustshipsForGivenCustNo(dbConnector, userSession.getCust_num());
		} else {
			custshipAL = Custship.getCustshipsForGivenCustNoContNo(dbConnector, userSession.getCust_num(), userSession.getCont_no());
		}

		String dfltAddrShip = gluuUser.getDfltAddrShip();
		Custship dfltCustship = null;
		if ((StringUtils.isBlank(dfltAddrShip) == false) && (dfltAddrShip.compareTo("78z7Sw4T1@3%") != 0)) {
			ArrayList<Custship> dfltCustshipAL = Custship.getCustshipsForGivenCustNoAndShipToNum(dbConnector, userSession.getCust_num(), dfltAddrShip);
			if ((dfltCustshipAL != null) && (dfltCustshipAL.size() > 0)) {
				dfltCustship = dfltCustshipAL.get(0);
				
				for (Iterator<Custship> iterator = custshipAL.iterator(); iterator.hasNext();) {
					Custship custship = iterator.next();
					if (custship.getShipto_num().compareTo(dfltCustship.getShipto_num()) == 0) {
						iterator.remove();
						break;
					}
				}
			}
		}		
		
		if ((custshipAL != null) && (custshipAL.size() == 0)) {
			custshipAL = null;
		}

		String messageToUser = null;
		HttpSession session = request.getSession(false);
		if (session != null) {
			messageToUser = (String) session.getAttribute("messageToUser");
			session.invalidate();
		}
		
		request.setAttribute("cust", cust);
		request.setAttribute("contact", contact);
		request.setAttribute("dfltCustship", dfltCustship);
		request.setAttribute("custshipAL", custshipAL);
		
		String profileEditing = request.getParameter("profileEditing");
		if ((profileEditing != null) && (profileEditing.compareToIgnoreCase("y") == 0)) {
			request.setAttribute("profileEditing", "y");			
		} else {
			request.setAttribute("profileEditing", null);			
		}		
		
		Utils.forwardToJSP(request, response, "EditCustships.jsp", messageToUser);
	}

	public static void removeCustship(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession == null) {
			String messageToUser = null;
			Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser);
			return;
		}
		
		String messageToUser = null;
		
		GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(userSession.getUsername(), null, true);
		
		String shipto_num = request.getParameter("shipto_num");
		log.debug("Trying to remove ShipTo " + shipto_num);

		if (StringUtils.isBlank(shipto_num) == false) {
			ArrayList<Custship> custshipAL = Custship.getCustshipsForGivenCustNoAndShipToNum(dbConnector, userSession.getCust_num(), shipto_num);
			if ((custshipAL != null) && (custshipAL.size() > 0)) {
				Custship custship = custshipAL.get(0);

				if (gluuUser.getAccessRole().compareTo("2") >= 0) {
					Custship.inactivate(dbConnector, custship.getCust_num(), custship.getShipto_num());
					
					String dfltAddrShip = gluuUser.getDfltAddrShip();
					log.debug("dfltAddrShip:***" + dfltAddrShip + "***, shipto_num:***" + shipto_num + "***");
					if ((dfltAddrShip != null) && (dfltAddrShip.compareTo(shipto_num) == 0)) {
						log.debug("dfltAddrShip is cleared.");
						// gluuUser.modify(false);
						gluuUser.modifyCustomAttribute("dfltAddrShip", "78z7Sw4T1@3%");
						Spolicy.update_dfltAddrShip(dbConnector, gluuUser.getUserName(), "");
					}

					messageToUser = "Successfully removed the selected shiping address.";					
				} else {
					if (custship.getCont_no() == gluuUser.getContNo()) {
						Custship.inactivate(dbConnector, custship.getCust_num(), custship.getShipto_num());

						String dfltAddrShip = gluuUser.getDfltAddrShip();
						log.debug("dfltAddrShip:***" + dfltAddrShip + "***, shipto_num:***" + shipto_num + "***");
						if ((dfltAddrShip != null) && (dfltAddrShip.compareTo(shipto_num) == 0)) {
							log.debug("dfltAddrShip is cleared.");
							// gluuUser.modify(false);
							gluuUser.modifyCustomAttribute("dfltAddrShip", "78z7Sw4T1@3%");
							Spolicy.update_dfltAddrShip(dbConnector, gluuUser.getUserName(), "");							
						}

						messageToUser = "Successfully removed the selected shiping address.";
					} else {
						messageToUser = "You don't have access to remove this shiping address. Please contact customer service.";
					}
				}
			} else {
				messageToUser = "Shiping address is not found in our system. Please contact customer service.";				
			}
		} else {
			messageToUser = "You can't remove this shiping address. Please contact customer service.";			
		}
		
		HttpSession session = request.getSession(true);
		session.setAttribute("messageToUser", messageToUser);
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", "/portal/controller?formFunction=EditCustships");		
	}

	public static void selectCustship(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession == null) {
			String messageToUser = null;
			Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser);
			return;
		}
		
		String messageToUser = null;
		
		GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(userSession.getUsername(), null, true);
		
		String shipto_num = request.getParameter("shipto_num");
		boolean makeDefault = false;
		{
			String makeDefaultS = request.getParameter("makeDefault");
			if ((makeDefaultS == null) || (makeDefaultS.compareToIgnoreCase("n") == 0)) {
				makeDefault = false;
			} else {
				makeDefault = true;
			}
		}
		// if ((makeDefault == true) && (StringUtils.isBlank(shipto_num) == false)) {
		if (makeDefault == true) {
			if (StringUtils.isBlank(shipto_num) == false) {
				gluuUser.modifyCustomAttribute("dfltAddrShip", shipto_num);
				Spolicy.update_dfltAddrShip(dbConnector, gluuUser.getUserName(), shipto_num);			
			} else {
				gluuUser.modifyCustomAttribute("dfltAddrShip", "78z7Sw4T1@3%");
				Spolicy.update_dfltAddrShip(dbConnector, gluuUser.getUserName(), "");				
			}
		}
		
		log.debug("Trying to select ShipTo " + shipto_num);		
		
		if (StringUtils.isBlank(shipto_num) == true) {
			shipto_num = "cust";
		}
		
		response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		// response.setHeader("Location", "https://" + Parms.HOST_NAME + "/scripts/cgiip.exe/wa/wcat/checkout.htm?shipto=" + shipto_num);
		response.setHeader("Location", "https://" + Parms.HOST_NAME + "/scripts/cgiip.exe/wa/wcat/checkout.htm?checkoutstep=shiping&change_address=yes&shipto=" + shipto_num);
	}
	
    public static void changeDefaultCustship(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
        UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
        if (userSession == null) {
            String messageToUser = null;
            Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser);
            return;
        }
            
        GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(userSession.getUsername(), null, true);
        
        String shipto_num = request.getParameter("shipto_num");
        if (StringUtils.isBlank(shipto_num) == false) {
            gluuUser.modifyCustomAttribute("dfltAddrShip", shipto_num);
            Spolicy.update_dfltAddrShip(dbConnector, gluuUser.getUserName(), shipto_num);           
        } else {
            gluuUser.modifyCustomAttribute("dfltAddrShip", "78z7Sw4T1@3%");
            Spolicy.update_dfltAddrShip(dbConnector, gluuUser.getUserName(), "");               
        }
        
        HttpSession session = request.getSession(true);
        session.setAttribute("messageToUser", "Default shiping address has been changed successfully.");
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", "/portal/controller?formFunction=EditCustships&profileEditing=y");   
    }	
	
	public static void editCustship(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession == null) {
			String messageToUser = null;
			Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser);
			return;
		}
		
		String shipto_num = request.getParameter("shipto_num");
		log.debug("Trying to edit ShipTo " + shipto_num);

		GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(userSession.getUsername(), null, true);

		Custship custship = null;
		if (StringUtils.isBlank(shipto_num) == false) {
			ArrayList<Custship> custshipAL = Custship.getCustshipsForGivenCustNoAndShipToNum(dbConnector, userSession.getCust_num(), shipto_num);
			if ((custshipAL != null) && (custshipAL.size() > 0)) {
				custship = custshipAL.get(0);
			}
		}
		if (custship == null) {
			HttpSession session = request.getSession(true);
			session.setAttribute("messageToUser", "Shiping address is not found.");
	        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	        response.setHeader("Location", "/portal/controller?formFunction=EditCustships");
	        return;
		}
				
	    String name = request.getParameter("name");
		if (name == null) {
			request.setAttribute("shipto_num", shipto_num);
			
			request.setAttribute("name", custship.getName());
			request.setAttribute("address", custship.getAddress());
			request.setAttribute("address2", custship.getAddress2());
			request.setAttribute("city", custship.getCity());
			request.setAttribute("state", custship.getState());
			request.setAttribute("zip", custship.getZip());
			
			if ((custship.getCountry().compareToIgnoreCase("US") == 0 ) ||
			    (custship.getCountry().compareToIgnoreCase("USA") == 0)    ) {
				request.setAttribute("country", "United States");
			}
			
			request.setAttribute("phoneWork", custship.getPhone());
			
			request.setAttribute("addAddress", null);
			
			String profileEditing = request.getParameter("profileEditing");
			if ((profileEditing != null) && (profileEditing.compareToIgnoreCase("y") == 0)) {
				request.setAttribute("profileEditing", "y");			
			} else {
				request.setAttribute("profileEditing", null);			
			}			
			
			Utils.forwardToJSP(request, response, "EditCustship.jsp", null);
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
		    
		    // Custship.inactivate(dbConnector, custship.getCust_num(), custship.getShipto_num());

		    // custship.setShipto_num(shipto_num);
		    custship.setCont_no(userSession.getCont_no());
		    
		    custship.setName(name);
		    custship.setAddress(address);
		    custship.setAddress2(address2);
		    custship.setCity(city);
		    
			if ((state != null) && (state.length() > 2)) {
				log.debug("State has length > 2. Truncating... State b4: " + state + ", state after truncation: " + state.substring(0, 2));					
				state = state.substring(0, 2);					
			}		    
		    custship.setState(state);
		    
		    custship.setZip(zip);
		    custship.setCountry(country);
		    		    
		    // custship.setPhone(phoneWork);
		    Long parsedPhone = Utils.extractPhoneNumberFromString(phoneWork);
		    if (parsedPhone != null) {
			    custship.setPhone(parsedPhone.toString());
		    } else {
		    	custship.setPhone(phoneWork);
		    }
		    
            custship.setAudit_date(new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
            custship.setAudit_time(new SimpleDateFormat("HH:mm:ss").format(new Date()));
            custship.setAudit_userid("WEB");
            
    		if (gluuUser.getAccessRole().compareTo("1") >= 0) {
                custship.setChanged_online(false);
    		} else {
                custship.setChanged_online(true);    			
    		}
		    
    		String old_shipto_num = shipto_num;
		    
    		try {
        		custship.persist(dbConnector);        		
			} catch (PortalException e) {
				HttpSession session = request.getSession(true);
				session.setAttribute("messageToUser", "Failed to change address.");
		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=EditCustships");
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
				(old_shipto_num.compareTo(gluuUser.getDfltAddrShip()) == 0	)    ) {
				gluuUser.modifyCustomAttribute("dfltAddrShip", custship.getShipto_num());
				Spolicy.update_dfltAddrShip(dbConnector, gluuUser.getUserName(), custship.getShipto_num());			
			}
		       	
			HttpSession session = request.getSession(true);
			session.setAttribute("messageToUser", "Shiping address has been changed successfully.");
	        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	        
			String profileEditing = request.getParameter("profileEditing");
			if ((profileEditing != null) && (profileEditing.compareToIgnoreCase("y") == 0)) {
		        response.setHeader("Location", "/portal/controller?formFunction=EditCustships&profileEditing=y");				
			} else {
		        response.setHeader("Location", "/portal/controller?formFunction=EditCustships");				
			}
		}
	}
	
	
	public static void addCustship(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession == null) {
			String messageToUser = null;
			Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser);
			return;
		}
		
		log.debug("Trying to add ShipTo");

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
			
			Utils.forwardToJSP(request, response, "EditCustship.jsp", null);
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
		    
		    Custship custship = new Custship();
		    
		    custship.setCust_num(userSession.getCust_num());
		    
		    custship.setCont_no(userSession.getCont_no());
		    
		    custship.setName(name);
		    custship.setAddress(address);
		    custship.setAddress2(address2);
		    custship.setCity(city);

			if ((state != null) && (state.length() > 2)) {
				log.debug("State has length > 2. Truncating... State b4: " + state + ", state after truncation: " + state.substring(0, 2));					
				state = state.substring(0, 2);					
			}		    
		    custship.setState(state);

		    custship.setZip(zip);
		    custship.setCountry(country);
		    
		    // custship.setPhone(phoneWork);
		    Long parsedPhone = Utils.extractPhoneNumberFromString(phoneWork);
		    if (parsedPhone != null) {
			    custship.setPhone(parsedPhone.toString());
		    } else {
		    	custship.setPhone(phoneWork);
		    }		    
		    
            custship.setAudit_date(new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
            custship.setAudit_time(new SimpleDateFormat("HH:mm:ss").format(new Date()));
            custship.setAudit_userid("WEB");
            
    		if (gluuUser.getAccessRole().compareTo("1") >= 0) {
                custship.setChanged_online(false);
    		} else {
                custship.setChanged_online(true);    			
    		}
		    	    
    		try {
        		custship.persist(dbConnector);        		
			} catch (PortalException e) {
				HttpSession session = request.getSession(true);
				session.setAttribute("messageToUser", "Failed to change address.");
		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=EditCustships");
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
				gluuUser.modifyCustomAttribute("dfltAddrShip", custship.getShipto_num());
				Spolicy.update_dfltAddrShip(dbConnector, gluuUser.getUserName(), custship.getShipto_num());			
			}
		       	
			HttpSession session = request.getSession(true);
			session.setAttribute("messageToUser", "Shiping address has been added successfully.");
	        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	        
			String profileEditing = request.getParameter("profileEditing");
			if ((profileEditing != null) && (profileEditing.compareToIgnoreCase("y") == 0)) {
		        response.setHeader("Location", "/portal/controller?formFunction=EditCustships&profileEditing=y");				
			} else {
		        response.setHeader("Location", "/portal/controller?formFunction=EditCustships");				
			}	        
		}
	}
}
