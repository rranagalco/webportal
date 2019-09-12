package galco.portal.emailpref;

import galco.portal.control.ControlServlet;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.ups.UPSAddress;
import galco.portal.user.GluuUser;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.Contact;
import galco.portal.wds.dao.Email_profile;
import galco.portal.wds.dao.Email_profile_err;
import galco.portal.wds.dao.UserSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jsslutils.extra.apachehttpclient.SslContextedSecureProtocolSocketFactory;
import org.xdi.util.EasySSLProtocolSocketFactory;

import com.sun.net.ssl.HttpsURLConnection;

public class EmailPref {
	private static Logger log = Logger.getLogger(EmailPref.class);

	// Test Test Test
	// public static final String AUTH_TOKEN = "MzAwNTU4NmU1YWI1MmY3OWMxOGQ3ZjRiYmQ2OWFhNWZmZjUzNDMxZTo3MTBhMDJkMzk3ZTQ0NzZiODRlZmVhOTdjMzQxMWJkNjhkNGQzYzE2";
	
	// Production Production Production
	public static final String AUTH_TOKEN = "NDc2YmMxNGI0OTY3MzUyYjllYzQ2NDBmYjgxNzkzMTIwZjUwNTgwMTowYmM1OTJiMGQwMDk0Y2M0NGUyZGQ2MDZmZTcxZmM4YjljODkzMzBl";
	
    public static final List<String> LIST_OF_ALL_TAGS = Arrays.asList("product_promotion,repair_services,eng_services,product_reviews,product_recommendations,surveys,new_videos".split("\\s*,\\s*"));
    public static final List<String> LIST_OF_ALL_TAGS_MUT_EXC = Arrays.asList("receive_daily,receive_weekly,receive_monthly".split("\\s*,\\s*"));
	static {
		Collections.sort(LIST_OF_ALL_TAGS);
		Collections.sort(LIST_OF_ALL_TAGS_MUT_EXC);
	}

	public static boolean addCutomer_GetveroIsGone(String email, String first_name, String last_name) throws PortalException {
		try {
	        HttpClient httpClient = new HttpClient();
	        PostMethod post;

            List<BasicNameValuePair> urlParameters = new ArrayList<BasicNameValuePair>();
            
            urlParameters.add(new BasicNameValuePair("auth_token", AUTH_TOKEN));
            urlParameters.add(new BasicNameValuePair("id", email));
            urlParameters.add(new BasicNameValuePair("email", email));
            
            urlParameters.add	(new BasicNameValuePair	(
            						"data", 
            						"{" + 
            						"\"first_name\": \"" +  first_name + "\"" + "," + 
            						"\"last_name\": \"" +  last_name + "\""   + "," +
            						"\"subscription\": \"medium\"" + 
            						"}"
            											)
            					);
            
            post = new PostMethod("https://api.getvero.com/api/v2/users/track" + "?" +
            					  URLEncodedUtils.format(urlParameters, "utf-8"));
            // log.debug("https://api.getvero.com/api/v2/users/track" + "?" + URLEncodedUtils.format(urlParameters, "utf-8"));
            // log.debug(post.getQueryString());
            
            int responseCode = httpClient.executeMethod(post);
            // log.debug(post.getResponseBodyAsString());
            
            if (responseCode != 200) {
            	return false;
            } else {
            	return true;
            }
		} catch (Exception e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}
	}

	public static boolean addTags_GetveroIsGone(String email, String commaSeparatedTags) throws PortalException {
		
		try {
	        HttpClient httpClient = new HttpClient();
	        PutMethod put;

            List<BasicNameValuePair> urlParameters = new ArrayList<BasicNameValuePair>();
            
            urlParameters.add(new BasicNameValuePair("auth_token", AUTH_TOKEN));
            urlParameters.add(new BasicNameValuePair("id", email));
            
            String tags = "[";
    		List<String> items = Arrays.asList(commaSeparatedTags.split("\\s*,\\s*"));
    		for (Iterator<String> iterator = items.iterator(); iterator.hasNext();) {
    			String tag = iterator.next();
    			
    			if (tags.length() == 1) {
    				tags += "\"" + tag + "\"";
    			} else {
    				tags += ",\"" + tag + "\"";    				
    			}
    		}
			tags += "]";
			log.debug(tags);
			
            urlParameters.add(new BasicNameValuePair("add", tags));
            
            put = new PutMethod("https://api.getvero.com/api/v2/users/tags/edit" + "?" +
            					URLEncodedUtils.format(urlParameters, "utf-8"));
            
            int responseCode = httpClient.executeMethod(put);
            log.debug(put.getResponseBodyAsString());
            
            if (responseCode != 200) {
            	return false;
            } else {
            	return true;
            }
		} catch (Exception e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}
	}
	
	public static boolean removeTags_GetveroIsGone(String email, String commaSeparatedTags) throws PortalException {
		
		try {
	        HttpClient httpClient = new HttpClient();
	        PutMethod put;

            List<BasicNameValuePair> urlParameters = new ArrayList<BasicNameValuePair>();
            
            urlParameters.add(new BasicNameValuePair("auth_token", AUTH_TOKEN));
            urlParameters.add(new BasicNameValuePair("id", email));
            
            String tags = "[";
    		List<String> items = Arrays.asList(commaSeparatedTags.split("\\s*,\\s*"));
    		for (Iterator<String> iterator = items.iterator(); iterator.hasNext();) {
    			String tag = iterator.next();
    			
    			if (tags.length() == 1) {
    				tags += "\"" + tag + "\"";
    			} else {
    				tags += ",\"" + tag + "\"";    				
    			}
    		}
			tags += "]";
			log.debug(tags);
			
            urlParameters.add(new BasicNameValuePair("remove", tags));
            
            put = new PutMethod("https://api.getvero.com/api/v2/users/tags/edit" + "?" +
            					URLEncodedUtils.format(urlParameters, "utf-8"));
            
            int responseCode = httpClient.executeMethod(put);
            log.debug(put.getResponseBodyAsString());
            
            if (responseCode != 200) {
            	return false;
            } else {
            	return true;
            }
		} catch (Exception e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}
	}

	public static boolean addAndRemoveTags_GetveroIsGone(String email, String addCommaSeparatedTags, String removeCommaSeparatedTags) throws PortalException {
		
		try {
	        HttpClient httpClient = new HttpClient();
	        PutMethod put;

            List<BasicNameValuePair> urlParameters = new ArrayList<BasicNameValuePair>();
            
            urlParameters.add(new BasicNameValuePair("auth_token", AUTH_TOKEN));
            urlParameters.add(new BasicNameValuePair("id", email));
            
            
            String tags = "[";
    		List<String> items = Arrays.asList(addCommaSeparatedTags.split("\\s*,\\s*"));
    		for (Iterator<String> iterator = items.iterator(); iterator.hasNext();) {
    			String tag = iterator.next();
    			
    			if (tags.length() == 1) {
    				tags += "\"" + tag + "\"";
    			} else {
    				tags += ",\"" + tag + "\"";    				
    			}
    		}
			tags += "]";
			// log.debug(tags);
            urlParameters.add(new BasicNameValuePair("add", tags));

            
            tags = "[";
    		items = Arrays.asList(removeCommaSeparatedTags.split("\\s*,\\s*"));
    		for (Iterator<String> iterator = items.iterator(); iterator.hasNext();) {
    			String tag = iterator.next();
    			
    			if (tags.length() == 1) {
    				tags += "\"" + tag + "\"";
    			} else {
    				tags += ",\"" + tag + "\"";    				
    			}
    		}
			tags += "]";
			// log.debug(tags);
            urlParameters.add(new BasicNameValuePair("remove", tags));

            
            put = new PutMethod("https://api.getvero.com/api/v2/users/tags/edit" + "?" +
            					URLEncodedUtils.format(urlParameters, "utf-8"));
            
            int responseCode = httpClient.executeMethod(put);
            // log.debug(put.getResponseBodyAsString());
            
            if (responseCode != 200) {
            	return false;
            } else {
            	return true;
            }
		} catch (Exception e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}
	}

	public static void unsubscribeAndInactivate_NoErr(DBConnector dbConnector, String email) throws PortalException {
		ArrayList<Email_profile> email_profileAL = Email_profile.getEmail_profile(dbConnector, email);
	    if ((email_profileAL != null) && (email_profileAL.size() > 0)) {
	    	Email_profile email_profile = email_profileAL.get(0);
	    	
	    	/*
            if (unsubscribe(email) == false) {
            	log.debug("Getvero trouble. unsubscribe. " + email);
            }
            */
            
	    	email_profile.setActive(false);
	    	email_profile.setAudit_date(null);
	    	email_profile.setAudit_time(null);
	    	email_profile.setAudit_userid(null);        	    	
			email_profile.persist(dbConnector);
	    }
	}

    			
	public static boolean unsubscribe_GetveroIsGone(String email) throws PortalException {
		try {
	        HttpClient httpClient = new HttpClient();
	        PostMethod post;
           

            List<BasicNameValuePair> urlParameters = new ArrayList<BasicNameValuePair>();
            
            urlParameters.add(new BasicNameValuePair("auth_token", AUTH_TOKEN));
            urlParameters.add(new BasicNameValuePair("id", email));
			
            post = new PostMethod("https://api.getvero.com/api/v2/users/unsubscribe" + "?" +
					  URLEncodedUtils.format(urlParameters, "utf-8"));
            
            int responseCode = httpClient.executeMethod(post);
            log.debug(post.getResponseBodyAsString());
            
            if (responseCode != 200) {
            	return false;
            } else {
            	return true;
            }
		} catch (Exception e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}
	}
	
	public static boolean resubscribe_GetveroIsGone(String email) throws PortalException {
		try {
	        HttpClient httpClient = new HttpClient();
	        PostMethod post;

            List<BasicNameValuePair> urlParameters = new ArrayList<BasicNameValuePair>();
            
            urlParameters.add(new BasicNameValuePair("auth_token", AUTH_TOKEN));
            urlParameters.add(new BasicNameValuePair("id", email));
			
            post = new PostMethod("https://api.getvero.com/api/v2/users/resubscribe" + "?" +
					  URLEncodedUtils.format(urlParameters, "utf-8"));
            
            int responseCode = httpClient.executeMethod(post);
            log.debug(post.getResponseBodyAsString());
            
            if (responseCode != 200) {
            	return false;
            } else {
            	return true;
            }
		} catch (Exception e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}
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
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}

	public static void installTM_URLConn() {
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
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static void createNewProfile(DBConnector dbConnector, String username, String firstName, String lastName) throws PortalException {
	    ArrayList<Email_profile> email_profileAL = Email_profile.getEmail_profile(dbConnector, username);
	    if ((email_profileAL != null) && (email_profileAL.size() > 0)) {
	    	return;
	    }
		
		
    	String addCommaSeparatedTags = "";
        for (Iterator<String> iterator = LIST_OF_ALL_TAGS.iterator(); iterator.hasNext();) {
            String tag = iterator.next();
            addCommaSeparatedTags += ((addCommaSeparatedTags.compareTo("") == 0)?"":",") + tag;
        }
        for (Iterator<String> iterator = LIST_OF_ALL_TAGS_MUT_EXC.iterator(); iterator.hasNext();) {
            String tag = iterator.next();
            addCommaSeparatedTags += ((addCommaSeparatedTags.compareTo("") == 0)?"":",") + tag;
        }
        log.debug("Creating new profile:" + username);
        log.debug("Adding all tags:" + addCommaSeparatedTags);
        
        /*
		if (addCutomer(username, firstName, lastName) == false) {
        	log.debug("Getvero trouble. addCutomer");

        	Email_profile_err email_profile_err = new Email_profile_err(username, firstName + "," + lastName + "," + addCommaSeparatedTags, "", "new", "n");
			email_profile_err.persist(dbConnector);
		}
		if (addAndRemoveTags(username, addCommaSeparatedTags, "") == false) {
        	log.debug("Getvero trouble. addAndRemoveTags");

        	Email_profile_err email_profile_err = new Email_profile_err(username, addCommaSeparatedTags, "", "update", "n");
			email_profile_err.persist(dbConnector);
		}
		*/

		Email_profile email_profile = new Email_profile(username, addCommaSeparatedTags, true);
    	email_profile.setAudit_date(null);
    	email_profile.setAudit_time(null);
    	email_profile.setAudit_userid(null);    	    				
		email_profile.persist(dbConnector);
	}
	
	
	public static void createNewProfileNoWeeklyAndMonthly_WriteCSV(DBConnector dbConnector, Path pathCSV, String username, String firstName, String lastName) throws PortalException {
	    ArrayList<Email_profile> email_profileAL = Email_profile.getEmail_profile(dbConnector, username);
	    if ((email_profileAL != null) && (email_profileAL.size() > 0)) {
	    	return;
	    }
		
		String addCommaSeparatedTags = "";
        for (Iterator<String> iterator = LIST_OF_ALL_TAGS.iterator(); iterator.hasNext();) {
            String tag = iterator.next();
            addCommaSeparatedTags += ((addCommaSeparatedTags.compareTo("") == 0)?"":",") + tag;
        }
        
        addCommaSeparatedTags = addCommaSeparatedTags + "," + "receive_daily";


        String csvLine = username + "," + firstName + "," +  lastName + "," + "\"\"\"" + addCommaSeparatedTags  + "\"\"\"";  
		try {
			Files.write(pathCSV, Arrays.asList(csvLine), StandardCharsets.UTF_8,
			    	    Files.exists(pathCSV) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
		} catch (IOException e) {
			System.out.println	("SUB IOException  " + username);
		}

		Email_profile email_profile = new Email_profile(username, addCommaSeparatedTags, true);
    	email_profile.setAudit_date(null);
    	email_profile.setAudit_time(null);
    	email_profile.setAudit_userid(null);    	    				
		email_profile.persist(dbConnector);
	}
	
	public static void createNewProfileNoWeeklyAndMonthly_UNS_WriteCSV(DBConnector dbConnector, Path pathCSV, String username, String firstName, String lastName) throws PortalException {
	    ArrayList<Email_profile> email_profileAL = Email_profile.getEmail_profile(dbConnector, username);
	    if ((email_profileAL != null) && (email_profileAL.size() > 0)) {
	    	return;
	    }
		
		String addCommaSeparatedTags = "";
        for (Iterator<String> iterator = LIST_OF_ALL_TAGS.iterator(); iterator.hasNext();) {
            String tag = iterator.next();
            addCommaSeparatedTags += ((addCommaSeparatedTags.compareTo("") == 0)?"":",") + tag;
        }
        
        addCommaSeparatedTags = addCommaSeparatedTags + "," + "receive_daily";


        String csvLine = username + "," + firstName + "," +  lastName + "," + "\"\"\"" + addCommaSeparatedTags + ",unsubscribed" + "\"\"\"";
        try {
        	Files.write(pathCSV, Arrays.asList(csvLine), StandardCharsets.UTF_8,
        				Files.exists(pathCSV) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
		} catch (IOException e) {
			System.out.println	("SUB IOException  " + username);
		}

		Email_profile email_profile = new Email_profile(username, addCommaSeparatedTags, false);
    	email_profile.setAudit_date(null);
    	email_profile.setAudit_time(null);
    	email_profile.setAudit_userid(null);    	    				
		email_profile.persist(dbConnector);
	}
	
	public static void createNewProfileNoWeeklyAndMonthly(DBConnector dbConnector, String username, String firstName, String lastName) throws PortalException {
	    ArrayList<Email_profile> email_profileAL = Email_profile.getEmail_profile(dbConnector, username);
	    if ((email_profileAL != null) && (email_profileAL.size() > 0)) {
	    	return;
	    }
		
		
    	String addCommaSeparatedTags = "";
        for (Iterator<String> iterator = LIST_OF_ALL_TAGS.iterator(); iterator.hasNext();) {
            String tag = iterator.next();
            addCommaSeparatedTags += ((addCommaSeparatedTags.compareTo("") == 0)?"":",") + tag;
        }
        
        /*
        for (Iterator<String> iterator = LIST_OF_ALL_TAGS_MUT_EXC.iterator(); iterator.hasNext();) {
            String tag = iterator.next();
            addCommaSeparatedTags += ((addCommaSeparatedTags.compareTo("") == 0)?"":",") + tag;
        }
        */
        addCommaSeparatedTags = addCommaSeparatedTags + "," + "receive_daily";

        // log.debug("Creating new profile:" + username);
        // log.debug("Adding all tags:" + addCommaSeparatedTags);
        
        /*
		if (addCutomer(username, firstName, lastName) == false) {
        	log.debug("Getvero trouble. addCutomer");
        	// Email_profile_err email_profile_err = new Email_profile_err(username, firstName + "," + lastName + "," + addCommaSeparatedTags, "", "new", "n");
			// email_profile_err.persist(dbConnector);
        	return;
		}
		if (addAndRemoveTags(username, addCommaSeparatedTags, "") == false) {
        	log.debug("Getvero trouble. addAndRemoveTags");
        	// Email_profile_err email_profile_err = new Email_profile_err(username, addCommaSeparatedTags, "", "update", "n");
			// email_profile_err.persist(dbConnector);
        	return;
		}
		*/

		Email_profile email_profile = new Email_profile(username, addCommaSeparatedTags, true);
    	email_profile.setAudit_date(null);
    	email_profile.setAudit_time(null);
    	email_profile.setAudit_userid(null);    	    				
		email_profile.persist(dbConnector);
	}
	
	public static void addUserWDS(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) {
	    String email = request.getParameter("email");
	    String fName = request.getParameter("fName");
	    String lName = request.getParameter("lName");
	    String code = request.getParameter("code");
	    
		log.debug("AddGetvero request received. email:" + email + ", fName:" + fName + ", lName:" + lName);

		try {
		    if ((code == null) || (code.compareTo("BanginAPalli") != 0)) {
				log.debug("AddGetvero code mismatch.");
				
				JSONObject jsonObject = null;
				PrintWriter printWriter;
				printWriter = response.getWriter();
				response.setContentType("application/json");

				jsonObject = new JSONObject();
				jsonObject.put("AddGetvero", "BadCode");
				printWriter.print(jsonObject);
				printWriter.flush();
				
				return;
		    }
		} catch (Exception e) {
			log.debug("Exception occurred while processing AddGetvero request from WDS.");	
			e.printStackTrace();
			return;
		}
	    
		try {
			createNewProfile(dbConnector, email, fName, lName);
			
			JSONObject jsonObject = null;
			PrintWriter printWriter;
			printWriter = response.getWriter();
			response.setContentType("application/json");

			jsonObject = new JSONObject();
			jsonObject.put("AddGetvero", "OK");
			printWriter.print(jsonObject);
			printWriter.flush();
			
			log.debug("AddGetvero was processed successfully.");			
		} catch (Exception e) {
			log.debug("Exception occurred while processing AddGetvero request from WDS.");	
			e.printStackTrace();
		} catch (PortalException e) {
			log.debug("Exception occurred while processing AddGetvero request from WDS.");	
			e.getE().printStackTrace();
		}
	}

	public static void emailPreferences(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession == null) {
			String messageToUser = null;
			Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser);
			return;
		}
		
		String username = userSession.getUsername();
	    ArrayList<Email_profile> email_profileAL = Email_profile.getEmail_profile(dbConnector, username);
	    
	    boolean product_promotion = true, repair_services = true, eng_services = true,
	    	    product_reviews = true, product_recommendations = true,
	    	    surveys = true, new_videos = true, receive_daily = true,
	    	    receive_weekly = true, receive_monthly = true;
	    
	    boolean active = true; 
	    
	    if ((email_profileAL != null) && (email_profileAL.size() > 0)) {
	    	Email_profile email_profile = email_profileAL.get(0);
	    	
    		List<String> userTags = Arrays.asList(email_profile.getTags().split("\\s*,\\s*"));

    		active = email_profile.getActive();
    		
    		product_promotion = (userTags.indexOf("product_promotion") >= 0)?true:false;
    		repair_services = (userTags.indexOf("repair_services") >= 0)?true:false;
    		eng_services = (userTags.indexOf("eng_services") >= 0)?true:false;
    		product_reviews = (userTags.indexOf("product_reviews") >= 0)?true:false;
    		product_recommendations = (userTags.indexOf("product_recommendations") >= 0)?true:false;
    		surveys = (userTags.indexOf("surveys") >= 0)?true:false;
    		new_videos = (userTags.indexOf("new_videos") >= 0)?true:false;
    		receive_daily = (userTags.indexOf("receive_daily") >= 0)?true:false;
    		receive_weekly = (userTags.indexOf("receive_weekly") >= 0)?true:false;
    		receive_monthly = (userTags.indexOf("receive_monthly") >= 0)?true:false;
	    } else {
			ArrayList<Contact> contactAL = Contact.getContact(dbConnector, userSession.getCust_num(), userSession.getCont_no());
			if ((contactAL != null) && (contactAL.size() > 0)) {
		    	createNewProfile(dbConnector, username, contactAL.get(0).getCo_name_f(), contactAL.get(0).getCo_name_l());
			} else {
				throw new PortalException(new Exception("Contact record is missing"), PortalException.SEVERITY_LEVEL_1);
			}
	    }
	    
	    request.setAttribute("active", active);
	    request.setAttribute("product_promotion", product_promotion);
	    request.setAttribute("repair_services", repair_services);
	    request.setAttribute("eng_services", eng_services);
	    request.setAttribute("product_reviews", product_reviews);
	    request.setAttribute("product_recommendations", product_recommendations);
	    request.setAttribute("surveys", surveys);
	    request.setAttribute("new_videos", new_videos);
	    request.setAttribute("receive_daily", receive_daily);
	    request.setAttribute("receive_weekly", receive_weekly);
	    request.setAttribute("receive_monthly", receive_monthly);
		
		Utils.forwardToJSP(request, response, "EmailPreferences.jsp", null);
	}

	public static void emailPreferencesProcess(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = UserSession.getUserSession(request, dbConnector, response);
		if (userSession == null) {
			String messageToUser = null;
			Utils.forwardToJSP(request, response, "SignIn.jsp", messageToUser);
			return;
		}
		
		
		String username = userSession.getUsername();
	    ArrayList<Email_profile> email_profileAL = Email_profile.getEmail_profile(dbConnector, username);

	    
	    String msgFromRedirect = "Updated e-mail preferences successfully.";
	    

        String unsubscribe = request.getParameter("unsubscribe");
        if ((unsubscribe != null) && (unsubscribe.compareToIgnoreCase("y") == 0)) {
    	    if ((email_profileAL != null) && (email_profileAL.size() > 0)) {
    	    	Email_profile email_profile = email_profileAL.get(0);
    	    	
        		boolean active = email_profile.getActive();
        		if (active == false) {
        			/*
                    if (resubscribe(username) == false) {
                    	log.debug("Getvero trouble. resubscribe");

                    	Email_profile_err email_profile_err = new Email_profile_err(username, email_profile.getTags(), "", "resub", "n");
            			email_profile_err.persist(dbConnector);
                    }
                    */
        	    	email_profile.setActive(true);
        	    	email_profile.setAudit_date(null);
        	    	email_profile.setAudit_time(null);
        	    	email_profile.setAudit_userid(null);        	    	
        			email_profile.persist(dbConnector);                    	
        		} else {
        			/*
                    if (unsubscribe(username) == false) {
                    	log.debug("Getvero trouble. unsubscribe");

                    	Email_profile_err email_profile_err = new Email_profile_err(username, email_profile.getTags(), "", "unsub", "n");
            			email_profile_err.persist(dbConnector);
                    }
                    */
        	    	email_profile.setActive(false);
        	    	email_profile.setAudit_date(null);
        	    	email_profile.setAudit_time(null);
        	    	email_profile.setAudit_userid(null);        	    	
        			email_profile.persist(dbConnector);                    	
        		}

        		/*

    	    	String removeCommaSeparatedTags = "";
                for (Iterator<String> iterator = LIST_OF_ALL_TAGS.iterator(); iterator.hasNext();) {
                    String tag = iterator.next();
                    removeCommaSeparatedTags += ((removeCommaSeparatedTags.compareTo("") == 0)?"":",") + tag;
                }
                for (Iterator<String> iterator = LIST_OF_ALL_TAGS_MUT_EXC.iterator(); iterator.hasNext();) {
                    String tag = iterator.next();
                    removeCommaSeparatedTags += ((removeCommaSeparatedTags.compareTo("") == 0)?"":",") + tag;
                }
                log.debug("Removing all tags:" + removeCommaSeparatedTags);
                
                if (removeTags(username, removeCommaSeparatedTags) == true) {
        	    	email_profile.setTags("");
        	    	email_profile.setAudit_date(null);
        	    	email_profile.setAudit_time(null);
        	    	email_profile.setAudit_userid(null);        	    	
        			email_profile.persist(dbConnector);
                } else {
                	msgFromRedirect = "Unable to update e-mail preferences, please contact customer service.";
                }
                
                */
    	    }
        } else {
	        String addCommaSeparatedTags = "", removeCommaSeparatedTags = "";
	        
	        {
	            String product_promotion = request.getParameter("product_promotion");
	            if ((product_promotion != null) && (product_promotion.compareToIgnoreCase("y") == 0)) {
	                addCommaSeparatedTags += ((addCommaSeparatedTags.compareTo("") == 0)?"":",") + "product_promotion";
	            } else {
	                removeCommaSeparatedTags += ((removeCommaSeparatedTags.compareTo("") == 0)?"":",") + "product_promotion";
	            }            
	        }
	
	        {
	            String repair_services = request.getParameter("repair_services");
	            if ((repair_services != null) && (repair_services.compareToIgnoreCase("y") == 0)) {
	                addCommaSeparatedTags += ((addCommaSeparatedTags.compareTo("") == 0)?"":",") + "repair_services";
	            } else {
	                removeCommaSeparatedTags += ((removeCommaSeparatedTags.compareTo("") == 0)?"":",") + "repair_services";
	            }            
	        }
	
	        {
	            String eng_services = request.getParameter("eng_services");
	            if ((eng_services != null) && (eng_services.compareToIgnoreCase("y") == 0)) {
	                addCommaSeparatedTags += ((addCommaSeparatedTags.compareTo("") == 0)?"":",") + "eng_services";
	            } else {
	                removeCommaSeparatedTags += ((removeCommaSeparatedTags.compareTo("") == 0)?"":",") + "eng_services";
	            }            
	        }
	
	        {
	            String product_reviews = request.getParameter("product_reviews");
	            if ((product_reviews != null) && (product_reviews.compareToIgnoreCase("y") == 0)) {
	                addCommaSeparatedTags += ((addCommaSeparatedTags.compareTo("") == 0)?"":",") + "product_reviews";
	            } else {
	                removeCommaSeparatedTags += ((removeCommaSeparatedTags.compareTo("") == 0)?"":",") + "product_reviews";
	            }            
	        }
	
	        {
	            String product_recommendations = request.getParameter("product_recommendations");
	            if ((product_recommendations != null) && (product_recommendations.compareToIgnoreCase("y") == 0)) {
	                addCommaSeparatedTags += ((addCommaSeparatedTags.compareTo("") == 0)?"":",") + "product_recommendations";
	            } else {
	                removeCommaSeparatedTags += ((removeCommaSeparatedTags.compareTo("") == 0)?"":",") + "product_recommendations";
	            }            
	        }
	
	        {
	            String surveys = request.getParameter("surveys");
	            if ((surveys != null) && (surveys.compareToIgnoreCase("y") == 0)) {
	                addCommaSeparatedTags += ((addCommaSeparatedTags.compareTo("") == 0)?"":",") + "surveys";
	            } else {
	                removeCommaSeparatedTags += ((removeCommaSeparatedTags.compareTo("") == 0)?"":",") + "surveys";
	            }            
	        }
	
	        {
	            String new_videos = request.getParameter("new_videos");
	            if ((new_videos != null) && (new_videos.compareToIgnoreCase("y") == 0)) {
	                addCommaSeparatedTags += ((addCommaSeparatedTags.compareTo("") == 0)?"":",") + "new_videos";
	            } else {
	                removeCommaSeparatedTags += ((removeCommaSeparatedTags.compareTo("") == 0)?"":",") + "new_videos";
	            }            
	        }
	
	        {
	            String receive_daily = request.getParameter("receive_daily");
	            if ((receive_daily != null) && (receive_daily.compareToIgnoreCase("y") == 0)) {
	                addCommaSeparatedTags += ((addCommaSeparatedTags.compareTo("") == 0)?"":",") + "receive_daily";
	            } else {
	                removeCommaSeparatedTags += ((removeCommaSeparatedTags.compareTo("") == 0)?"":",") + "receive_daily";
	            }            
	        }
	
	        {
	            String receive_weekly = request.getParameter("receive_weekly");
	            if ((receive_weekly != null) && (receive_weekly.compareToIgnoreCase("y") == 0)) {
	                addCommaSeparatedTags += ((addCommaSeparatedTags.compareTo("") == 0)?"":",") + "receive_weekly";
	            } else {
	                removeCommaSeparatedTags += ((removeCommaSeparatedTags.compareTo("") == 0)?"":",") + "receive_weekly";
	            }            
	        }
	
	        {
	            String receive_monthly = request.getParameter("receive_monthly");
	            if ((receive_monthly != null) && (receive_monthly.compareToIgnoreCase("y") == 0)) {
	                addCommaSeparatedTags += ((addCommaSeparatedTags.compareTo("") == 0)?"":",") + "receive_monthly";
	            } else {
	                removeCommaSeparatedTags += ((removeCommaSeparatedTags.compareTo("") == 0)?"":",") + "receive_monthly";
	            }            
	        }
	        
            log.debug("Adding tags  :" + addCommaSeparatedTags);
            log.debug("Removing tags:" + removeCommaSeparatedTags);

            Email_profile email_profile = null;
    	    if ((email_profileAL == null) || (email_profileAL.size() == 0)) {
    			ArrayList<Contact> contactAL = Contact.getContact(dbConnector, userSession.getCust_num(), userSession.getCont_no());
    			if ((contactAL != null) && (contactAL.size() > 0)) {
    				/*
    				if (addCutomer(username, contactAL.get(0).getCo_name_f(), contactAL.get(0).getCo_name_l()) == false) {
                    	log.debug("Getvero trouble. addCutomer");

                    	Email_profile_err email_profile_err = new Email_profile_err(username, contactAL.get(0).getCo_name_f() + "," + contactAL.get(0).getCo_name_l() + addCommaSeparatedTags, "", "new", "n");
            			email_profile_err.persist(dbConnector);
    				}
    				*/

    				email_profile = new Email_profile(username, addCommaSeparatedTags, true);
        	    	email_profile.setAudit_date(null);
        	    	email_profile.setAudit_time(null);
        	    	email_profile.setAudit_userid(null);    	    				
    				email_profile.persist(dbConnector);
    			} else {
    				msgFromRedirect = "Unable to update e-mail preferences, please contact customer service.";
    				log.debug("Contact record is missing.");
    			}
    	    } else {
    	    	email_profile = email_profileAL.get(0);
    	    	
    	    	/*
                if (addAndRemoveTags(username, addCommaSeparatedTags, removeCommaSeparatedTags) == false) {
                	log.debug("Getvero trouble. addAndRemoveTags");

                	Email_profile_err email_profile_err = new Email_profile_err(username, addCommaSeparatedTags, removeCommaSeparatedTags, "update", "n");
        			email_profile_err.persist(dbConnector);
                }
                */
                
    	    	email_profile.setTags(addCommaSeparatedTags);
    	    	email_profile.setAudit_date(null);
    	    	email_profile.setAudit_time(null);
    	    	email_profile.setAudit_userid(null);
    			email_profile.persist(dbConnector);  
    	    }
    	    	
    	    /*
            Email_profile email_profile;
    	    if ((email_profileAL != null) && (email_profileAL.size() > 0)) {
    	    	email_profile = email_profileAL.get(0);
    	    	
                if (addAndRemoveTags(username, addCommaSeparatedTags, removeCommaSeparatedTags)) {
        	    	email_profile.setTags(addCommaSeparatedTags);
        	    	email_profile.setAudit_date(null);
        	    	email_profile.setAudit_time(null);
        	    	email_profile.setAudit_userid(null);
        			email_profile.persist(dbConnector);  
                } else {
                	msgFromRedirect = "Unable to update e-mail preferences, please contact customer service.";
                	log.debug("Getvero trouble. addAndRemoveTags");
                }
    	    } else {
    			ArrayList<Contact> contactAL = Contact.getContact(dbConnector, userSession.getCust_num(), userSession.getCont_no());
    			if ((contactAL != null) && (contactAL.size() > 0)) {
    				if (addCutomer(username, contactAL.get(0).getCo_name_f(), contactAL.get(0).getCo_name_l())) {
    					if (addTags(username, addCommaSeparatedTags)) {
    	    				email_profile = new Email_profile(username, addCommaSeparatedTags, true);
    	        	    	email_profile.setAudit_date(null);
    	        	    	email_profile.setAudit_time(null);
    	        	    	email_profile.setAudit_userid(null);    	    				
    	    				email_profile.persist(dbConnector);    						
    					} else {
    	                	msgFromRedirect = "Unable to update e-mail preferences, please contact customer service.";
    	                	log.debug("Getvero trouble. addTags");
    					}
    				} else {
	                	msgFromRedirect = "Unable to update e-mail preferences, please contact customer service.";
	                	log.debug("Getvero trouble. addCutomer");
    				}
    			} else {
                	msgFromRedirect = "Unable to update e-mail preferences, please contact customer service.";
                	log.debug("Contact record is missing.");
    			}
    	    }
    	    */
        }
        
        log.debug("msgFromRedirect:" + msgFromRedirect);

		HttpSession session = request.getSession(true);
		session.setAttribute("msgFromRedirect", msgFromRedirect);
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", "/portal/controller?formFunction=OrderList");
	}

	public static void anonymousUnsubscribe(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
        String email = request.getParameter("email");
        String tagToRemove = request.getParameter("preference");
        String unsubscribe = request.getParameter("unsubscribe");
        
        if ((email != null) && (unsubscribe != null) && (unsubscribe.compareToIgnoreCase("yes") == 0)) {
		    ArrayList<Email_profile> email_profileAL = Email_profile.getEmail_profile(dbConnector, email);
    	    if ((email_profileAL != null) && (email_profileAL.size() > 0)) {
    	    	Email_profile email_profile = email_profileAL.get(0);

    	    	/*
                if (unsubscribe(email) == false) {
                	log.debug("Getvero trouble. unsubscribe");

                	Email_profile_err email_profile_err = new Email_profile_err(email, email_profile.getTags(), "", "unsub", "n");
        			email_profile_err.persist(dbConnector);
                }
                */
                
    	    	email_profile.setActive(false);
    	    	email_profile.setAudit_date(null);
    	    	email_profile.setAudit_time(null);
    	    	email_profile.setAudit_userid(null);        	    	
    			email_profile.persist(dbConnector);                    	
    			
    			HttpSession session = request.getSession(true);
    			session.setAttribute("msgFromRedirect", null);
    	        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
    	        response.setHeader("Location", "/portal/controller?formFunction=EmailPreferenceChangeSuccess");
    	        
    	        return;
    	    }
		} else if ((email != null) && (tagToRemove != null)) {
		    ArrayList<Email_profile> email_profileAL = Email_profile.getEmail_profile(dbConnector, email);

		    if ((email_profileAL != null) && (email_profileAL.size() > 0)) {
    	    	Email_profile email_profile = email_profileAL.get(0);
    	    	
    	    	String tags = email_profile.getTags();
    	    	if ((tags != null && tags.length() > 0)) {
	        		List<String> items = Arrays.asList(tags.split("\\s*,\\s*"));
	        		String newTags = "";
	        		boolean removedSuccessfully = false;
	        		
	        		for (Iterator<String> iterator = items.iterator(); iterator.hasNext();) {
	        			String tag = iterator.next();
	        			
	        			if (tag.compareToIgnoreCase(tagToRemove) == 0) {
	        				/*
	        				if (removeTags(email, tagToRemove)) {
	        					removedSuccessfully = true;
	        				}
	        				*/
        					removedSuccessfully = true;
	        			} else {
	        				newTags += (((newTags.length() == 0)?"":",") + tag);
	        			}
	        		}
	        		
	        		if (removedSuccessfully == true) {
	        	    	email_profile.setTags(newTags);
	        	    	email_profile.setAudit_date(null);
	        	    	email_profile.setAudit_time(null);
	        	    	email_profile.setAudit_userid(null);        	    	
	        			email_profile.persist(dbConnector);
	        			
	        			HttpSession session = request.getSession(true);
	        			session.setAttribute("msgFromRedirect", null);
	        	        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	        	        response.setHeader("Location", "/portal/controller?formFunction=EmailPreferenceChangeSuccess");
	        	        
	        	        return;
	        		}
    	    	}
		    }
        }

		HttpSession session = request.getSession(true);
		session.setAttribute("msgFromRedirect", null);
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", "/portal/controller?formFunction=EmailPreferenceChangeFailure");
	}

	public static void main(String[] args) {
		// installTM();
		
		/*
		try {
			String eMail = "ZZZ_Test_sati06191519@galco.com";
			addCutomer(eMail, "ZZZ_Test_subb_06191519", "ZZZ_Test_ati_06191519");
			// addTags(eMail, "dcba");
			// removeTags(eMail, "tag20000,tag2000,tag200,tag20");
			// removeTags(eMail, "tag10000,tag1000,tag100,tag10");
			// addAndRemoveTags(eMail, "tag20000,tag2000,tag200,tag20", "tag10000,tag1000,tag100,tag10");
			// addAndRemoveTags(eMail, "tag10000,tag1000,tag100,tag10", "tag20000,tag2000,tag200,tag20");
			// addAndRemoveTags(eMail, "tag30000,tag3000,tag300,tag30", "tag10000,tag1000,tag100,tag10");
			// unsubscribe(eMail);
			// resubscribe(eMail);
		} catch (PortalException e) {
			log.debug("Exception occurred.", e.getE());
		}
		*/

		/*
		try {
			addCutomer("sati1@galco.com", "subbarao1", "ati1");
		} catch (PortalException e) {
			log.debug("Exception occurred.", e.getE());
		}
		*/

		/*
		List<String> items = Arrays.asList("a   ,   b, c   , d".split("\\s*,\\s*"));
		for (Iterator iterator = items.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			log.debug("*" + string + "*");
		}
		*/
	}

}
