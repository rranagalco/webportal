package galco.portal.control;

import galco.fascor.utils.FasUtils;
//import galco.portal.batch.GetveroBatchProcessor;
import galco.portal.config.Parms;
import galco.portal.dashboard.DashboardRequestHandler;
import galco.portal.db.DBConnector;
import galco.portal.emailpref.EmailPref;
import galco.portal.exception.PortalException;
import galco.portal.ups.UPSAddress;
import galco.portal.user.signin.UserAuthenticationHandler;
import galco.portal.user.signup.UserSignUpHandler;
import galco.portal.user_functions.edit_custbill.EditCustbills;
import galco.portal.user_functions.edit_custship.EditCustships;
import galco.portal.user_functions.orderlist.OrderDetailsProvider;
import galco.portal.user_functions.orderlist.OrderListDataHandler;
import galco.portal.user_functions.orderlist.SubsequentOrderFetcher;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.Part;
import galco.portal.wds.dao.UserSession;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.naming.Context;
import javax.naming.InitialContext;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.servlet.CaptchaServletUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
//import org.apache.tomcat.jdbc.pool.DataSource;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.jsslutils.extra.apachehttpclient.SslContextedSecureProtocolSocketFactory;
import org.xdi.util.EasySSLProtocolSocketFactory;

import com.sun.net.ssl.HttpsURLConnection;

/**
 * Servlet implementation class Controler
 */
@WebServlet("/controller")
public class ControlServlet extends HttpServlet {
	private static Logger log = Logger.getLogger(ControlServlet.class);
	
	public static HashMap<Long, Connection> servletIDsHM = new HashMap<Long, Connection>(100); 

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------------------------------------------

    public long getServletID() {
    	long servletID = (long) (Math.random() * 1000000000);
    	while (servletIDsHM.get(servletID) != null) {
        	servletID = (long) (Math.random() * 1000000000);
    	}
    	
    	return servletID;    	
    }
    
	// -------------------------------------------------------------------------------------------------------------
    
    public void makeDBConnections(HttpServletRequest request) throws PortalException {
        try {
        	log.debug("DBConn - 1");
        	
            // Obtain our environment naming context
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");

        	log.debug("DBConn - 2");

            // Look up our data source
            DataSource dsWDS = (DataSource) envCtx.lookup("jdbc/progressdb_wds");
            DataSource dsSRO = (DataSource) envCtx.lookup("jdbc/progressdb_sro");
            DataSource dsWEB = (DataSource) envCtx.lookup("jdbc/progressdb_web");

        	log.debug("DBConn - 3");

            // Allocate and use a connection from the pool
            Connection connWDS = dsWDS.getConnection();
            Connection connSRO = dsSRO.getConnection();
            Connection connWEB = dsWEB.getConnection();
            
			// long servletID = getServletID();
            
			request.setAttribute("connWDS", connWDS);
			request.setAttribute("connSRO", connSRO);	            
			request.setAttribute("connWEB", connWEB);
			
        	log.debug("DBConn - 4");			
        } catch (Exception e) {
        	throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
        }
    	
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnector dbConnector = null;
		String formFunction = null;
		
		try {
			// Utils.sendMail("sati@galco.com,rajuati@yahoo.com,subbaraoati1@gmail.com", "WebPortal@galco.com", "Controller Testing.", "Just testing...");
			
			// log.debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			// log.debug("Server name: " + request.getServerName());
			int serverPort = request.getServerPort();
			// log.debug("Server Port: " + request.getServerPort());
			// log.debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");			
			
			String requestURL = request.getRequestURL().toString();

		    formFunction = request.getParameter("formFunction");
			log.debug("formFunction:" + formFunction);

			if ((serverPort != 8080) && (formFunction != null) && (formFunction.compareTo("DashboardSearch") == 0)) {
                log.debug("subb Someone is trying to access Dashboard from outside");			
                
				response.setHeader("Location", "https://" + Parms.HOST_NAME + "/portal/controller?formFunction=SignIn");
				response.setStatus(HttpServletResponse.SC_FOUND);	
				
	            return;				
			}
			
			if ((serverPort != 8080) || (formFunction == null) || (formFunction.compareTo("DashboardSearch") != 0)) {
		        if ((request.getScheme().equals("http")					 				) &&
		        	((formFunction == null 										) ||

		        	 ((formFunction.compareTo("AddGetvero") != 0	    ) &&	
				      (formFunction.compareTo("VisualRcvYN") != 0	    ) &&	        			
		        	  (formFunction.compareTo("IsSessionValid") != 0	)    	) 

		        																		)    ) {
	
	                String redirectURL = "https://" + Parms.HOST_NAME + request.getRequestURI();
	                if (StringUtils.isBlank(request.getQueryString()) == false) {
	                	redirectURL = redirectURL + "?" + request.getQueryString();
	                }
	                
	                // log.debug(formFunction.compareTo("AddGetvero"));
	                // log.debug(formFunction.compareTo("IsSessionValid"));
	                
	                // log.debug("subb 0822 16:49 requestURL  : " + requestURL);			
	                // log.debug("subb 0822 16:49 redirectURL : " + redirectURL);			
	                
					response.setHeader("Location", redirectURL);
					response.setStatus(HttpServletResponse.SC_FOUND);	
					
		            return;
		        }
			}
			
			// log.debug("Logger, Subbadu, in Control Servlet");
			
			// formFunction = request.getParameter("formFunction");
			// log.debug("formFunction:" + formFunction);
			
			// makeDBConnections(request);
			
	        // dbConnector = new DBConnector(true);				
	        try {
		        dbConnector = new DBConnector(true);
			} catch (PortalException e) {
		        if ((formFunction != null) && (formFunction.compareTo("IsSessionValid") == 0)) {
					JSONObject jsonObject = null;
					PrintWriter printWriter = response.getWriter();
					response.setContentType("application/json");

					jsonObject = new JSONObject();
					jsonObject.put("session_expired", "YES");
					printWriter.print(jsonObject);
					printWriter.flush();

					log.debug("PortalException occurred. FormFunction: IsSessionValid, returning Session Expired.");
					log.debug(e.getE());

					return;
		        } else {
		        	throw e;
		        }
			}
	        
			if ((formFunction == null) || (formFunction.compareTo("SignIn") == 0)) {
				UserAuthenticationHandler.authenticateUser(request, dbConnector, response);
			} else if (formFunction.compareTo("IsSessionValid") == 0) {
				UserAuthenticationHandler.isSessionValid(request, dbConnector, response);
				
			} else if (formFunction.compareTo("AddGetvero") == 0) {
				log.debug("GetVero is not there anymore. formFunction=AddGetvero received.");
				// EmailPref.addUserWDS(request, dbConnector, response);
								
			} else if (formFunction.compareTo("GetSessionUserDetails") == 0) {
				UserAuthenticationHandler.getSessionUserDetails(request, dbConnector, response);
				
			} else if (formFunction.compareTo("SignUp") == 0) {
				UserSignUpHandler.userSignUp(request, dbConnector, response);
			} else if (formFunction.compareTo("SignUpBill") == 0) {
				UserSignUpHandler.userSignUpBill(request, dbConnector, response);				
			} else if (formFunction.compareTo("SignUpShip") == 0) {
				UserSignUpHandler.userSignUpShip(request, dbConnector, response);				

				
			} else if (formFunction.compareTo("ShowEmailConfirmation") == 0) {
				Utils.forwardToJSP(request, response, "SignUpShowEmailConfirmation.jsp", null);
			} else if (formFunction.compareTo("EmailConfirmationFailure") == 0) {
				Utils.forwardToJSP(request, response, "EmailConfirmationFailure.jsp", null);				
			} else if (formFunction.compareTo("ConfirmEmail") == 0) {
				UserSignUpHandler.confirmEmail(request, dbConnector, response);

				
			} else if (formFunction.compareTo("EditCustbills") == 0) {
				EditCustbills.editCustbills(request, dbConnector, response);
			} else if (formFunction.compareTo("SelectCustbill") == 0) {
				EditCustbills.selectCustbill(request, dbConnector, response);
			} else if (formFunction.compareTo("EditCustbill") == 0) {
				EditCustbills.editCustbill(request, dbConnector, response);
			} else if (formFunction.compareTo("AddCustbill") == 0) {
				EditCustbills.addCustbill(request, dbConnector, response);				
			} else if (formFunction.compareTo("RemoveCustbill") == 0) {
				EditCustbills.removeCustbill(request, dbConnector, response);
			} else if (formFunction.compareTo("ChangeDefaultCustbill") == 0) {
				EditCustbills.changeDefaultCustbill(request, dbConnector, response);

            } else if (formFunction.compareTo("EditCustships") == 0) {
                EditCustships.editCustships(request, dbConnector, response);
            } else if (formFunction.compareTo("SelectCustship") == 0) {
                EditCustships.selectCustship(request, dbConnector, response);
            } else if (formFunction.compareTo("EditCustship") == 0) {
                EditCustships.editCustship(request, dbConnector, response);
            } else if (formFunction.compareTo("AddCustship") == 0) {
                EditCustships.addCustship(request, dbConnector, response);              
            } else if (formFunction.compareTo("RemoveCustship") == 0) {
                EditCustships.removeCustship(request, dbConnector, response);
			} else if (formFunction.compareTo("ChangeDefaultCustship") == 0) {
				EditCustships.changeDefaultCustship(request, dbConnector, response);                

            } else if (formFunction.compareTo("EmailPreferences") == 0) {
                EmailPref.emailPreferences(request, dbConnector, response);               
            } else if (formFunction.compareTo("EmailPreferencesProcess") == 0) {
                EmailPref.emailPreferencesProcess(request, dbConnector, response);
            } else if (formFunction.compareTo("EmailPreferencesUnsubscribe") == 0) {
                EmailPref.anonymousUnsubscribe(request, dbConnector, response);
            } else if (formFunction.compareTo("EmailPreferenceChangeSuccess") == 0) {
            	Utils.forwardToJSP(request, response, "EmailPreferenceChangeSuccess.jsp", null);
            } else if (formFunction.compareTo("EmailPreferenceChangeFailure") == 0) {
            	Utils.forwardToJSP(request, response, "EmailPreferenceChangeFailure.jsp", null);
            	
            	
			} else if (formFunction.compareTo("TestJSP") == 0) {
				String jspName = request.getParameter("jspName");
				Utils.forwardToJSP(request, response, jspName, null);				

			} else if (formFunction.compareTo("Error_MatchedInactiveCust") == 0) {
				Utils.forwardToJSP(request, response, "Error_MatchedInactiveCust.jsp", null);				
				
			} else if (formFunction.compareTo("ChangeEmail") == 0) {
				UserSignUpHandler.changeEmail(request, dbConnector, response);
			} else if (formFunction.compareTo("EmailChangeConfirmation") == 0) {
				Utils.forwardToJSP(request, response, "EmailChangeConfirmation.jsp", null);	
			} else if (formFunction.compareTo("ConfirmNewEmail") == 0) {
				UserSignUpHandler.confirmNewEmail(request, dbConnector, response);				
				
			} else if (formFunction.compareTo("UpdateAccountInfo") == 0) {
				UserSignUpHandler.userAccountInfoUpdate(request, dbConnector, response);
			} else if (formFunction.compareTo("ChangePassword") == 0) {
				/*
				if (true) {
					new PortalException(new Exception("Just testing, man!"), PortalException.SEVERITY_LEVEL_1).handleException(formFunction, dbConnector, request, response);
				}
				*/
				UserSignUpHandler.changePassword(request, dbConnector, response);
			} else if (formFunction.compareTo("ForgotPassword") == 0) {
				UserSignUpHandler.resetForgottenPassword_CreateRequest(request, dbConnector, response);
			} else if (formFunction.compareTo("ForgotPasswordUsernameSelection") == 0) {
				UserSignUpHandler.resetForgottenPassword_UserSelectionHandler(request, dbConnector, response);
			} else if (formFunction.compareTo("ForgotPasswordKeyEntryRequest") == 0) {
				UserSignUpHandler.resetForgottenPassword_KeyEntry(request, dbConnector, response);
			} else if (formFunction.compareTo("ForgotPasswordKeyEntry") == 0) {
				UserSignUpHandler.resetForgottenPassword_KeyEntry(request, dbConnector, response);
			} else if (formFunction.compareTo("ForgotPasswordChangePassword") == 0) {
				UserSignUpHandler.resetForgottenPassword_ChangePassword(request, dbConnector, response);
			} else if (formFunction.compareTo("LogOff") == 0) {
				UserSignUpHandler.logOff(request, dbConnector, response);
			} else if (formFunction.compareTo("OrderList") == 0) {
				OrderListDataHandler.displayOrderList(request, dbConnector, response);
			} else if (formFunction.compareTo("OrderListSubsequent") == 0) {
				SubsequentOrderFetcher.returnSubsequentOrders(request, dbConnector, response);
			} else if (formFunction.compareTo("OrderDetails") == 0) {
				OrderDetailsProvider.returnOrderDetails(request, dbConnector, response);
			} else if (formFunction.compareTo("DashboardSearch") == 0) {
			    String subFunction = request.getParameter("subFunction");
			    subFunction = (subFunction != null)?subFunction:"";
			    
			    if (subFunction.compareTo("Conf") == 0) {
			    	DashboardRequestHandler.dashboardConfirmEmail(request, dbConnector, response);			    	
			    } else if (subFunction.compareTo("OrdSrch") == 0) {
			    	DashboardRequestHandler.dashboardOrderSearch(request, dbConnector, response);
			    } else {
			    	DashboardRequestHandler.dashboardSearch(request, dbConnector, response);
			    }
			} else if (formFunction.compareTo("VisualRcvYN") == 0) {
			    // qqq
			    String part_num = request.getParameter("sku");
			    String sec_code = request.getParameter("sec_code");
			    
			    log.debug("sku: " + part_num);

		        PrintWriter printWriter = response.getWriter();
				response.setContentType("text/html");

				if ((sec_code != null) && (sec_code.compareTo("SomKaaRamSub") == 0)) {
					String imageFilePath = null;
					try {
						// String imageFilePath = galco.fascor.utils.Utils.getImageFilePath(dbConnector.getConnectionWDS(), part_num);
						imageFilePath = FasUtils.getSmallImagePath(request, dbConnector, part_num);
					    log.debug("imageFilePath: " + imageFilePath);

						if ((imageFilePath == null) || (imageFilePath.toLowerCase().indexOf("/picture-na_s.jpg") >= 0)) {
							printWriter.print("N");
						} else {
							printWriter.print("Y");						
						}
					} catch (PortalException e) {
						log.debug("", e.getE());
						printWriter.print("E");
					}					
			    } else {
					printWriter.print("E");
			    }

				printWriter.flush();
				printWriter.close();

			} else if (formFunction.compareTo("SevereError") == 0) {
				String incidentNumber = "4";
				HttpSession session = request.getSession(false);				
				if (session != null) {
					incidentNumber = (String) session.getAttribute("incidentNumber");
					session.invalidate();
			    }
			    request.setAttribute("incidentNumber", incidentNumber);

				Utils.forwardToJSP(request, response, "SevereError.jsp", null);								
				

			/*
			} else if (formFunction.compareTo("GetCaptcha") == 0) {
			    int _width = 200;
			    int _height = 50;
			    
			    log.debug("Subban GetCaptcha");
			
			    Captcha captcha = new Captcha.Builder(_width, _height)
			    	.addText()
			    	.addBackground(new GradiatedBackgroundProducer())
			    	.gimp()
			    	.addNoise()
			    	.addBorder()
			    	.build();
			    
			    //try {
				//   File outputfile = new File("/usr/share/tomcat/webapps/portal/zsubbtemp.png");
				//   ImageIO.write(captcha.getImage(), "png", outputfile);
				//} catch (Exception e) {
				//	 log.debug("", e);
				//}
			    
			    CaptchaServletUtil.writeImage(response, captcha.getImage());
			    request.getSession().setAttribute(nl.captcha.Captcha.NAME, captcha);
			*/
			
			}

			dbConnector.closeConnections();
		} catch (PortalException e) {
			e.handleException(formFunction, dbConnector, request, response);
		} catch (Exception e) {
			new PortalException(e, PortalException.SEVERITY_LEVEL_1).handleException(formFunction, dbConnector, request, response);
		}
	}

	// -------------------------------------------------------------------------------------------------------------

	/*
	
	static {
        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        	
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
	        ctx.init(new KeyManager[0], new TrustManager[] {tm}, new SecureRandom());			
	        SSLContext.setDefault(ctx);
	        
            SslContextedSecureProtocolSocketFactory secureProtocolSocketFactory =
                    new SslContextedSecureProtocolSocketFactory(ctx);
            Protocol.registerProtocol("https", new Protocol("https", (ProtocolSocketFactory) secureProtocolSocketFactory, 443));
            
            //Protocol easyhttps = new Protocol("https", (ProtocolSocketFactory) new EasySSLProtocolSocketFactory(), 443);
            //Protocol.registerProtocol("https", easyhttps);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}
	*/

	public static boolean tmInstalled = false;
	public static boolean getveroDaemonStarted = false;
	
	static {
		if (tmInstalled == false) {
			installTM();
			tmInstalled = true;
		}
		
		/*
		if (getveroDaemonStarted == false) {
			getveroDaemonStarted = true;
			
	        // log.debug("Starting GetveroBatchProcessor");
	        Thread t = new Thread(new GetveroBatchProcessor());
			t.setDaemon(true);
	        t.start();
	        // log.debug("Started GetveroBatchProcessor");
	        
	        java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
	        java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);
	        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
	        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
	        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "ERROR");
	        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "ERROR");
	        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "ERROR");	        
		}
		*/
	}
	
	public static void installTM() {
        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        	
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
	        ctx.init(new KeyManager[0], new TrustManager[] {tm}, new SecureRandom());			
	        SSLContext.setDefault(ctx);

	        /*
            SslContextedSecureProtocolSocketFactory secureProtocolSocketFactory =
                    new SslContextedSecureProtocolSocketFactory(ctx);
            Protocol.registerProtocol("https", new Protocol("https", (ProtocolSocketFactory) secureProtocolSocketFactory, 443));
            */
            
            Protocol easyhttps = new Protocol("https", (ProtocolSocketFactory) new EasySSLProtocolSocketFactory(), 443);
            Protocol.registerProtocol("https", easyhttps);
	        /*
            */
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}

	/*
	static {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { 
		    new X509TrustManager() {     
		        public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
		            return new X509Certificate[0];
		        } 
		        public void checkClientTrusted( 
		            java.security.cert.X509Certificate[] certs, String authType) {
		            } 
		        public void checkServerTrusted( 
		            java.security.cert.X509Certificate[] certs, String authType) {
		        }
		    } 
		}; 
	
		// Install the all-trusting trust manager
		try {
		    SSLContext sc = SSLContext.getInstance("SSL"); 
		    sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		    sc = SSLContext.getInstance("TLS"); 
		    sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());		
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	*/

	public static String GUPTA_GLUU;
	public static String GUPTA_PORTAL_TALLI_GUPTAPADAM;
	public static String GUPTA_PASS_DISPLAY_GUPTAPADAM;
	static {
		ArrayList<String> guptaPadaaluAL;

		if (Parms.FASCOR_INTEGRATION_IS_RUNNING == false) {
			// guptaPadaaluAL = galco.portal.utils.Utils.loadFileIntoArrayList("/usr/share/tomcat/webapps/ZGuptaPadaalu.txt");			
			guptaPadaaluAL = galco.portal.utils.Utils.loadFileIntoArrayList("/opt/tomcat/webapps/ZGuptaPadaalu.txt");
		} else {
			guptaPadaaluAL = galco.portal.utils.Utils.loadFileIntoArrayList("C:\\Users\\rrana\\AppServers\\Tomcat\\apache-tomcat-9022\\webapps/ZGuptaPadaalu.txt");
		}
		
		ControlServlet.GUPTA_GLUU = guptaPadaaluAL.get(0);
		ControlServlet.GUPTA_PORTAL_TALLI_GUPTAPADAM = guptaPadaaluAL.get(1);
		ControlServlet.GUPTA_PASS_DISPLAY_GUPTAPADAM = guptaPadaaluAL.get(2);
	}
	
    public ControlServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
		String formFunction = request.getParameter("formFunction");
    	if (formFunction.compareTo("UPSValidation") == 0) {
			try {
		        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		        String jsonRequestString = "";
		        if(br != null) {
		            String curline = br.readLine();
		        	while (curline != null) {
		        		jsonRequestString += curline;
		        		
			            curline = br.readLine();		        		
		        	}
		            // json = br.readLine();
		        }
		        // JSONObject requestJSONObject = new JSONObject(jsonRequestString);		 
		        // log.debug("json: " + jsonRequestString);
		        
		        
		        String name = (String) request.getParameter("name");
		        String address = (String) request.getParameter("address");
		        String address2 = (String) request.getParameter("address2");
		        String city = (String) request.getParameter("city");
		        String state = (String) request.getParameter("state");
		        String zip5 = (String) request.getParameter("zip5");
		        String zip4 = (String) request.getParameter("zip4");
		        String country = (String) request.getParameter("country");
		        
				// GOOD_ADDRESS
				// address = "1587 CANTERBURY RD NORTH"; city = "ST PETERSBURG"; state = "FL"; zip = "33710"; zip4 = null; country = "US";
				
				// AMBIGUOUS_ADDRESS
				// address = "9999 CANTERBURY RD NORTH"; city = "ST PETERSBURG"; state = "FL"; zip = "33710"; zip4 = null; country = "US";
				
				// GOOD_ADDRESS and EXACT MATCH
				// address = "26010 PINEHURST DR"; city = "MADISON HEIGHTS"; state = "MI"; zip = "48071"; zip4 = null; country = "US";
				
				// GOOD_ADDRESS but DIDN'T MATCH
				// address = "26010 PINEHURST DRIVE"; city = "MADISON HEIGHTS"; state = "MI"; zip = "48071"; zip4 = null; country = "US";
				
				// GOOD_ADDRESS but WRONG ZIP CODE
				// address = "26010 PINEHURST DRIVE"; city = "MADISON HEIGHTS"; state = "MI"; zip = "48336"; zip4 = null; country = "US";

				// COMPLETE JUNK ADDRESS
				// address = "26010 WHATEVER DRIVE"; city = "SOME CITY"; state = "JJ"; zip = "87888"; zip4 = null; country = "US";

				// AMBIGUOUS_ADDRESS
				// address = "617 E HWY 83"; city = "MC ALLEN"; state = "TX"; zip = "78501"; zip4 = null; country = "US";
				
				// AMBIGUOUS_ADDRESS, didn't find the box
				// address = "RR1 BOX 409-D"; city = "HAZLETON"; state = "PA"; zip = "18201"; zip4 = null; country = "US";
				
				// GOOD_ADDRESS, Address Matched
				// address = "PO BOX 89"; city = "ALBERT LEA"; state = "MN"; zip = "56007"; zip4 = null; country = "US";
				
				// GOOD_ADDRESS, P. O. Box ignored
				// address = "1461 NORTH MITCHELL, PO BOX 417"; city = "CADILLAC"; state = "MI"; zip = "49601"; zip4 = null; country = "US";
				
				// GOOD_ADDRESS, P. O. Box ignored
				// address = "1461 NORTH MITCHELL, PO BOX 417"; city = "CADILLAC"; state = "MI"; zip = "49601"; zip4 = null; country = "US";
				
		        log.debug("\n\n");
		        log.debug("name:" + name);
		        log.debug("address:" + address);
		        log.debug("address2:" + address2);
		        log.debug("city:" + city);
		        log.debug("state:" + state);
		        log.debug("zip5:" + zip5);
		        log.debug("zip4:" + zip4);
		        log.debug("country:" + country);
		        
				Object[] addressValidationResults = UPSAddress.validateAddressWithUPS(name, address, city, state, zip5, zip4, country); 
				
				int returnCode = (Integer) addressValidationResults[0];
				UPSAddress[] upsAddresses = (UPSAddress[]) addressValidationResults[1];
				
				if (returnCode == UPSAddress.GOOD_ADDRESS) {
					log.debug("returnCode          : GOOD_ADDRESS");
				} else if (returnCode == UPSAddress.AMBIGUOUS_ADDRESS) {
					log.debug("returnCode          : AMBIGUOUS_ADDRESS");
				} else if (returnCode == UPSAddress.CODE_IS_MISSING) {
					log.debug("returnCode          : CODE_IS_MISSING");
				} else if (returnCode == UPSAddress.NO_CANDIDATES) {
					log.debug("returnCode          : NO_CANDIDATES");
				} else if (returnCode == UPSAddress.UNKNOWN_CODE) {
					log.debug("returnCode          : UNKNOWN_ERROR");
				} else if (returnCode == UPSAddress.JSON_EXCEPTION) {
					log.debug("returnCode          : JSON_EXCEPTION");		
				} else if (returnCode == UPSAddress.EXCEPTIION_OCCURRED) {
					log.debug("returnCode          : EXCEPTIION_OCCURRED");
				}		
				
				if (upsAddresses != null) {
					for (int i = 0; i < upsAddresses.length; i++) {
						upsAddresses[i].print();
					}
					
					if (returnCode == UPSAddress.GOOD_ADDRESS) {
						boolean addressesmatched = upsAddresses[0].areTheAddressesSame(address, city, state, zip5, zip4, country);
						
						System.out.println("Addresses Matched : " + addressesmatched);			
					}
				}
				
	
		        PrintWriter printWriter = response.getWriter();
				response.setContentType("application/json");
				// printWriter.print("{\"XAVResponse\":{\"Response\":{\"ResponseStatus\":{\"Code\":\"1\", \"Description\":\"Success\"}, \"TransactionReference\":{\"CustomerContext\":\"subb-test-man\"}}, \"ValidAddressIndicator\":\"\", \"Candidate\":{\"AddressKeyFormat\":{\"AddressLine\":\"12880 CLOVERDALE ST\", \"PoliticalDivision2\":\"OAK PARK\", \"PoliticalDivision1\":\"MI\", \"PostcodePrimaryLow\":\"48237\", \"PostcodeExtendedLow\":\"3206\", \"Region\":\"OAK PARK MI 48237-3206\", \"CountryCode\":\"US\"}}}}");
				
				log.debug("addressValidationResults[2]");
				log.debug(addressValidationResults[2] + "\n\n\n");
				printWriter.print(addressValidationResults[2]);				
				printWriter.flush();
			} catch(Exception e) {
				new PortalException(e, PortalException.SEVERITY_LEVEL_2).handleException(formFunction, null, request, response);
			}
    	} else if (formFunction.compareTo("AddGetvero") == 0) {
			log.debug("AddGetvero - post - request received.");
			
			JSONObject jsonObject = null;
			PrintWriter printWriter = response.getWriter();
			response.setContentType("application/json");

			jsonObject = new JSONObject();
			try {
				jsonObject.put("AddGetvero", "OK");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			printWriter.print(jsonObject);
			printWriter.flush();
    	}
    }
}
