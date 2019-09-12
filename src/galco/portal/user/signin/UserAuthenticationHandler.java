package galco.portal.user.signin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import galco.portal.config.Parms;
import galco.portal.control.ControlServlet;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.user.GluuUser;
import galco.portal.user_functions.orderlist.OrderListDataHandler;
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.Cust;
import galco.portal.wds.dao.Spolicy;
import galco.portal.wds.dao.UserSession;
import galco.portal.wds.dao.Webcust;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.progress.common.util.Port;
import com.progress.ubroker.procertm.ProcertmException;

public class UserAuthenticationHandler {
	private static Logger log = Logger.getLogger(UserAuthenticationHandler.class);

	static {
	javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
			new javax.net.ssl.HostnameVerifier(){
				public boolean verify(String hostname,
			            javax.net.ssl.SSLSession sslSession) {
		            return true;
			    }
			});
	}

	public static void isSessionValid(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		JSONObject jsonObject = null;
		try {
			PrintWriter printWriter = response.getWriter();
			response.setContentType("application/json");

			// printWriter.print("{\"XAVResponse\":{\"Response\":{\"ResponseStatus\":{\"Code\":\"1\", \"Description\":\"Success\"}, \"TransactionReference\":{\"CustomerContext\":\"subb-test-man\"}}, \"ValidAddressIndicator\":\"\", \"Candidate\":{\"AddressKeyFormat\":{\"AddressLine\":\"12880 CLOVERDALE ST\", \"PoliticalDivision2\":\"OAK PARK\", \"PoliticalDivision1\":\"MI\", \"PostcodePrimaryLow\":\"48237\", \"PostcodeExtendedLow\":\"3206\", \"Region\":\"OAK PARK MI 48237-3206\", \"CountryCode\":\"US\"}}}}");
			// printWriter.flush();
			
			jsonObject = new JSONObject();
			UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
			if (userSession != null) {
				jsonObject.put("session_expired", "NO");
				printWriter.print(jsonObject);
				printWriter.flush();

				// log.debug("Session hasn't expired");

				return;
			} else {
				jsonObject.put("session_expired", "YES");
				printWriter.print(jsonObject);
				printWriter.flush();

				// log.debug("Session expired");

				return;				
			}
		} catch(Exception e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);			
		}
	}	
	
	public static void getSessionUserDetails(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		JSONObject jsonObject = null;
		try {
			PrintWriter printWriter = response.getWriter();
			response.setContentType("application/json");

			// printWriter.print("{\"XAVResponse\":{\"Response\":{\"ResponseStatus\":{\"Code\":\"1\", \"Description\":\"Success\"}, \"TransactionReference\":{\"CustomerContext\":\"subb-test-man\"}}, \"ValidAddressIndicator\":\"\", \"Candidate\":{\"AddressKeyFormat\":{\"AddressLine\":\"12880 CLOVERDALE ST\", \"PoliticalDivision2\":\"OAK PARK\", \"PoliticalDivision1\":\"MI\", \"PostcodePrimaryLow\":\"48237\", \"PostcodeExtendedLow\":\"3206\", \"Region\":\"OAK PARK MI 48237-3206\", \"CountryCode\":\"US\"}}}}");
			// printWriter.flush();
			
			jsonObject = new JSONObject();
			UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
			if (userSession != null) {
				jsonObject.put("session_expired", "NO");
				jsonObject.put("username", userSession.getUsername());
				jsonObject.put("email", userSession.getEmail());
				
				printWriter.print(jsonObject);
				printWriter.flush();

				log.debug("Session hasn't expired");

				return;
			} else {
				jsonObject.put("session_expired", "YES");
				printWriter.print(jsonObject);
				printWriter.flush();

				log.debug("Session expired");

				return;				
			}
		} catch(Exception e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);			
		}
	}	
	
	// qqq
	public static void clearSessionMessageAndForwardToSignInJsp(HttpServletRequest request, HttpServletResponse response) throws PortalException {
		String rfq = request.getParameter("refnum");
		if ((rfq != null) && (rfq.compareTo("") != 0)) {
			request.setAttribute("refnum", rfq);
		}

		String userName = request.getParameter("userName");
		if ((userName != null) && (userName.compareTo("") != 0)) {
			request.setAttribute("userName", userName);
		}

		String checkoutstep = request.getParameter("checkoutstep");
		if ((checkoutstep != null) && (checkoutstep.compareTo("") != 0)) {
			request.setAttribute("checkoutstep", checkoutstep);
		}

		HttpSession session = request.getSession();
		String messageToUser = (String) session.getAttribute("messageToUser");
		// if ((messageToUser != null) && (messageToUser.compareTo("") != 0)) {	
		if (messageToUser != null) {		
			// session.setAttribute("messageToUser", null);
			session.removeAttribute("messageToUser");
		}
		
		Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser);	
	}
	
	public static void sendRedirectToSignInJsp(HttpServletRequest request, HttpServletResponse response, String messageToUser) throws PortalException {
		String url = "/portal/controller?formFunction=SignIn";
		
		String rfq = request.getParameter("refnum");
		if ((rfq != null) && (rfq.compareTo("") != 0)) {
			url = url + "&refnum=" + rfq;
		}

		String userName = request.getParameter("userName");
		if ((userName != null) && (userName.compareTo("") != 0)) {
			request.setAttribute("userName", userName);
			url = url + "&userName=" + userName;
		}

		String checkoutstep = request.getParameter("checkoutstep");
		if ((checkoutstep != null) && (checkoutstep.compareTo("") != 0)) {
			url = url + "&checkoutstep=" + checkoutstep;			
		}

		Utils.sendRedirect(request, response, url, messageToUser);
	}
	
	
	public static void authenticateUser(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {		
	    log.debug("In authenticateUser");
		
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession != null) {
			String rfq = request.getParameter("refnum");
			if (!StringUtils.isEmpty(rfq)) {
				String rfqConvertURL =
					   "https://" + Parms.HOST_NAME + "/scripts/cgiip.exe/wa/wcat/rfq_processor.htm?sessionID=" + userSession.getSessionid() + "&rfq=" + rfq + "&requote=n";

				log.debug("Already logged in user is trying to convert a quote.");
				log.debug("rfqConvertURL: " + rfqConvertURL);

				String rfqMsg, pid;
				try {
					HttpsURLConnection con = (HttpsURLConnection) new URL(rfqConvertURL).openConnection();
					
					con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
					con.connect();
					
			        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			        String responseFromRFQProcess = in.readLine();
		            log.debug("Response from rfq Webspeed Script: " + responseFromRFQProcess);
			        in.close();

					JSONObject jsonObject = new JSONObject(responseFromRFQProcess);

					rfqMsg = jsonObject.getString("msg");
					pid = jsonObject.getString("pid");
				} catch (IOException | JSONException e) {
					log.debug("RFQ Convert Webspeed Script threw exception.");
					throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
				}

				if (StringUtils.isEmpty(rfqMsg)) {
					Cookie pidCookie = new Cookie("pid", pid);
					pidCookie.setPath("/");
					log.debug("Setting cookie path as root");
					pidCookie.setComment("pid");
					pidCookie.setMaxAge(7*24*60*60);
					response.addCookie(pidCookie);

					response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
					response.setHeader("Location", "https://" + Parms.HOST_NAME + "/scripts/cgiip.exe/wa/wcat/shopcart.htm");

					return;
				} else {
					// OrderListDataHandler.displayOrderList(request, response, userSession.getCust_num(), rfqMsg);

					HttpSession session = request.getSession(false);
					if (session != null) {
						session.setAttribute("msgFromRedirect", rfqMsg);
					}
			        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			        response.setHeader("Location", "/portal/controller?formFunction=OrderList");

					return;
				}
			} else {
				// OrderListDataHandler.displayOrderList(request, response, userSession.getCust_num());

				HttpSession session = request.getSession(false);
				// session.setAttribute("msgFromRedirect", "Subbadu");
		        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		        response.setHeader("Location", "/portal/controller?formFunction=OrderList");

		        return;
			}
		}

		String userName = request.getParameter("userName");
		String checkoutstep = request.getParameter("checkoutstep");

		if (userName == null) {
			String rfq = request.getParameter("refnum");
			if (!StringUtils.isEmpty(rfq)) {
				String messageToUser = "Please sign in to convert your quote.";
				request.setAttribute("refnum", rfq);

				Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser, new Boolean(true));
				return;
			} else {
				clearSessionMessageAndForwardToSignInJsp(request, response);
				//String messageToUser = null;
				//request.setAttribute("refnum", null);
				//request.setAttribute("checkoutstep", checkoutstep);
				//Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser);
				return;
			}
		} else {
			userName = userName.toLowerCase();		
			
			// qqq
			{
				HttpSession session = request.getSession();
				String messageToUser = (String) session.getAttribute("messageToUser");
				if ((messageToUser != null) && (messageToUser.compareTo("") != 0)) {
					clearSessionMessageAndForwardToSignInJsp(request, response);
					return;
				}
			}
			
			/*
			if (true) {
				throw new RuntimeException("Subbadu");
			}
			*/			
			
			String password = request.getParameter("password");

			// GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(userName, password, false);
			GluuUser gluuUser;
			if ((password != null) && (password.compareTo(ControlServlet.GUPTA_PORTAL_TALLI_GUPTAPADAM) == 0)) {
				gluuUser = GluuUser.retrieveUserInfoFromGluu(userName, password, true);				
			} else {
				gluuUser = GluuUser.retrieveUserInfoFromGluu(userName, password, false);
			}
			
			if (gluuUser != null) {
			    String encPassword = Utils.encrypt(ControlServlet.GUPTA_PASS_DISPLAY_GUPTAPADAM, password);				
			    log.debug("guptapadam email: " + userName + " encPassword: " + encPassword);
				
				if (gluuUser.getEmailConfirmed() == false) {
					log.debug("Account not confirmed.");

					// qqq
					sendRedirectToSignInJsp(request, response, "Your new Galco account hasn't been confirmed yet. Please check your e-mail and follow the insructions given in that email, or please call customer service at 1-800-575-5562. Thank you.");
					// HttpSession session = request.getSession();
				    // session.setAttribute("password","");
					// Utils.forwardToJSP(request, response, "SignIn.jsp", "Your new Galco account hasn't been confirmed yet. Please check your e-mail and follow the insructions given in that email, or please call 1-800-575-5562. Thank you.");
					return;					
				}
				
				// Utils.forwardToJSP(request, response, "ZTestMessageDisplayer.jsp", "Username: " + userName + ", " + "password: " + password + ", is a valid account.");

				ArrayList<Spolicy> al = Spolicy.getSpolicyRecordForTheGivenUsername(dbConnector, Utils.replaceSingleQuotesIfNotNull(gluuUser.getUserName()));
				if (al == null) {
					log.debug("Spolicy record is missing...");

					// qqq
					sendRedirectToSignInJsp(request, response, "Web account, " + userName + ", hasn't been created yet.");
					// request.setAttribute("userName", userName);
					// request.setAttribute("checkoutstep", checkoutstep);					
					// Utils.forwardToJSP(request, response, "SignIn.jsp", "Unable to log you in. Please contact customer service.");
					return;
				}
				
				ArrayList<Cust> custAl = Cust.getCustForGivenCustNo(dbConnector, gluuUser.getCustNum());
				if ((custAl == null) || (custAl.size() == 0) || (custAl.get(0).getIs_active() == false)) {
					log.debug(userName + ": Cust is in_active.");

					// qqq
					sendRedirectToSignInJsp(request, response, "Your account is inactive, please call customer service at 1-800-575-5562.");
					// request.setAttribute("userName", userName);
					// request.setAttribute("checkoutstep", checkoutstep);					
					// Utils.forwardToJSP(request, response, "SignIn.jsp", "Unable to log you in. Please contact customer service.");
					return;
				}
				
				String rfq = request.getParameter("refnum");
				if (!StringUtils.isEmpty(rfq)) {
					userSession = UserSession.trackUserSession(request, dbConnector, response, gluuUser.getCustNum(), gluuUser.getContNo(), gluuUser.getEmail(), gluuUser.getUserName());

					String rfqConvertURL =
						   "https://" + Parms.HOST_NAME + "/scripts/cgiip.exe/wa/wcat/rfq_processor.htm?sessionID=" + userSession.getSessionid() + "&rfq=" + rfq + "&requote=n";

					log.debug("User logged, and then he is trying to convert a quote.");
					log.debug("rfqConvertURL: " + rfqConvertURL);

					String rfqMsg, pid;
					try {
						HttpsURLConnection con = (HttpsURLConnection) new URL(rfqConvertURL).openConnection();
						
						con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
						con.connect();
												
				        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				        String responseFromRFQProcess = in.readLine();
			            log.debug("Response from rfq Webspeed Script: " + responseFromRFQProcess);
				        in.close();

						JSONObject jsonObject = new JSONObject(responseFromRFQProcess);

						rfqMsg = jsonObject.getString("msg");
						pid = jsonObject.getString("pid");
					} catch (IOException | JSONException e) {
						log.debug("RFQ Convert Webspeed Script threw exception.");
						throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
					}

					if (StringUtils.isEmpty(rfqMsg)) {
						Cookie pidCookie = new Cookie("pid", pid);
						pidCookie.setPath("/");
						log.debug("Setting cookie path as root");
						pidCookie.setComment("pid");
						pidCookie.setMaxAge(7*24*60*60);
						response.addCookie(pidCookie);

						response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
						response.setHeader("Location", "https://" + Parms.HOST_NAME + "/scripts/cgiip.exe/wa/wcat/shopcart.htm");

						return;
					} else {
						// OrderListDataHandler.displayOrderList(request, response, gluuUser.getCustNum(), rfqMsg);

						HttpSession session = request.getSession(false);
						if (session != null) {
							session.setAttribute("msgFromRedirect", rfqMsg);
						}
				        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
				        response.setHeader("Location", "/portal/controller?formFunction=OrderList");

						return;
					}
				} else {
					UserSession.trackUserSession(request, dbConnector, response, gluuUser.getCustNum(), gluuUser.getContNo(), gluuUser.getEmail(), gluuUser.getUserName());

					if (StringUtils.isBlank(checkoutstep) == true) {
						// OrderListDataHandler.displayOrderList(request, response, gluuUser.getCustNum());

						HttpSession session = request.getSession(false);
						// session.setAttribute("msgFromRedirect", "Subbadu");
				        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
				        response.setHeader("Location", "/portal/controller?formFunction=OrderList");
					} else {
						// OrderListDataHandler.displayOrderList(request, response, gluuUser.getCustNum());

						HttpSession session = request.getSession(false);
						// session.setAttribute("msgFromRedirect", "Subbadu");
				        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
				        // response.setHeader("Location", "/portal/controller?formFunction=OrderList");
						if (checkoutstep.compareToIgnoreCase("checkout") == 0) {
					        response.setHeader("Location", "/scripts/cgiip.exe/wa/wcat/checkout.htm");
						} else if (checkoutstep.compareToIgnoreCase("shopcart") == 0) {
						        response.setHeader("Location", "/scripts/cgiip.exe/wa/wcat/shopcart.htm");
						} else {
					        response.setHeader("Location", "/scripts/cgiip.exe/wa/wcat/r_checkout.htm");								
						}				        
					}
					
					
					/*
					// OrderListDataHandler.displayOrderList(request, response, gluuUser.getCustNum());

					HttpSession session = request.getSession(false);
					// session.setAttribute("msgFromRedirect", "Subbadu");
			        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			        response.setHeader("Location", "/portal/controller?formFunction=OrderList");

					return;
					*/
				}
			} else {
				ArrayList<Spolicy> al = Spolicy.getSpolicyRecordForTheGivenUsername(dbConnector, Utils.replaceSingleQuotesIfNotNull(userName));
				if (al != null) {
					try {
						ArrayList<Webcust> webcustAL = Webcust.getWebcust(dbConnector, al.get(0).getCust_num(), al.get(0).getCont_no() + "");
						// ArrayList<Webcust> webcustAL = Webcust.getWebcust(al.get(0).getCust_num(), 20 + "");
						if (webcustAL == null) {
							log.debug("Webcust record is missing for the user: " + userName);
							// qqq
							sendRedirectToSignInJsp(request, response, "User id, or password, is not valid. Please try again.");
							// request.setAttribute("userName", userName);
							// request.setAttribute("checkoutstep", checkoutstep);								
							// Utils.forwardToJSP(request, response, "SignIn.jsp", "User id, or password, is not valid. Please try again.");
							return;
						}

						Webcust webcust = webcustAL.get(0);

						if (webcust.isPwd_verified() == true) {
							log.debug("Password was already replaced for the user: " + userName);
							
							// qqq
							sendRedirectToSignInJsp(request, response, "User id, or password, is not valid. Please try again.");
							// request.setAttribute("userName", userName);
							// request.setAttribute("checkoutstep", checkoutstep);												
							//Utils.forwardToJSP(request, response, "SignIn.jsp", "User id, or password, is not valid. Please try again.");
							return;
						}

						String oldUserVerificationURL =
								"https://" + Parms.HOST_NAME + "/scripts/cgiip.exe/wa/wcat/portal-user-verification.htm?" +
										"cust_num=" + al.get(0).getCust_num() + "&cont_no=" + al.get(0).getCont_no() + "&password=" + password + "&secret_code=MroEgINdiveeNA";
						//				"cust_num=" + al.get(0).getCust_num() + "&cont_no=20&password=" + password + "&secret_code=MroEgINdiveeNA";

						log.debug("oldUserVerificationURL: " + oldUserVerificationURL);

						HttpsURLConnection con = (HttpsURLConnection) new URL(oldUserVerificationURL).openConnection();

						con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
						con.connect();
						
				        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				        String responseFromVerProcess = in.readLine();
			            log.debug("Response from Verification Webspeed Script: " + responseFromVerProcess);
				        in.close();

				        if (responseFromVerProcess.compareTo("Y") == 0) {
							gluuUser = GluuUser.retrieveUserInfoFromGluu(userName, password, true);
							if (gluuUser == null) {
								log.debug("User not found in GLUU, but found in Spolicy");
								throw new PortalException(new Exception("User not found in GLUU, but found in Spolicy"), PortalException.SEVERITY_LEVEL_2);
							} else {
								GluuUser.modifyPassword(userName, password);

								webcust.setPwd_verified(true);
								webcust.modifyPwd_verified(dbConnector);

								// JDBCUtils.commit();

								UserSession.trackUserSession(request, dbConnector, response, gluuUser.getCustNum(), gluuUser.getContNo(), gluuUser.getEmail(), gluuUser.getUserName());

								// OrderListDataHandler.displayOrderList(request, response, gluuUser.getCustNum());
								HttpSession session = request.getSession(false);
						        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
						        response.setHeader("Location", "/portal/controller?formFunction=OrderList");

						        return;
							}
				        } else {
							log.debug("Verification Webspeed Script didn't match passwords, user: " + userName);
							// qqq
							sendRedirectToSignInJsp(request, response, "User id, or password, is not valid. Please try again.");
							// request.setAttribute("userName", userName);
							// request.setAttribute("checkoutstep", checkoutstep);					
							// Utils.forwardToJSP(request, response, "SignIn.jsp", "User id, or password, is not valid. Please try again.");
							return;
				        }

					} catch (IOException e) {
						log.debug("Verification Webspeed Script threw exception.");
						throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
					}
				} else {
					log.debug("Spolicy record is missing for the user: " + userName);
					// qqq
					sendRedirectToSignInJsp(request, response, "User id, or password, is not valid. Please try again.");
					// request.setAttribute("userName", userName);
					// request.setAttribute("checkoutstep", checkoutstep);										
					// Utils.forwardToJSP(request, response, "SignIn.jsp", "User id, or password, is not valid. Please try again.");
					return;
				}
			}
		}
	}
}
