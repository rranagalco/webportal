package galco.portal.utils;

import galco.fascor.utils.FasUtils;
import galco.portal.config.Parms;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.wds.dao.UserSession;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class Utils {
	private static Logger log = Logger.getLogger(Utils.class);

	// -------------------------------------------------------------------------------------------------------------

    public static boolean isHostNameOK(String host) {
        try {
            InetAddress inetHost = InetAddress.getByName(host);
            return true;
        } catch(UnknownHostException ex) {
            return false;
        }
    }

	// -------------------------------------------------------------------------------------------------------------

    public static boolean isZipCodeValid(String zip, String country) {
    	// System.out.println(zip + " " + country);
    	
    	if (StringUtils.isEmpty(zip)) {
    		return false;
    	}

    	// log.debug(zip);

    	/*
	    US ZIP code (U.S. postal code) allow both the five-digit and nine-digit (called ZIP+4) formats. A valid postal code should match 12345 and 12345-6789, but not 1234, 123456, 123456789, or 1234-56789.

	    Regex : ^[0-9]{5}(?:-[0-9]{4})?$
	    ^           # Assert position at the beginning of the string.
	    [0-9]{5}    # Match a digit, exactly five times.
	    (?:         # Group but don't capture:
	      -         #   Match a literal "-".
	      [0-9]{4}  #   Match a digit, exactly four times.
	    )           # End the non-capturing group.
	      ?         #   Make the group optional.
	    $           # Assert position at the end of the string.
	    */

    	if (country.compareToIgnoreCase("United States") == 0) {
		    String regex = "^[0-9]{5}(?:-[0-9]{4})?$";
		    Pattern pattern = Pattern.compile(regex);
	
	        Matcher matcher = pattern.matcher(zip);
		    if(matcher.matches()) {
		    	return true;
		    }
	
		    return false;
    	} else if (country.compareToIgnoreCase("Canada") == 0) {
    		String zipUC = zip.toUpperCase();
    		
    		String regex = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";
		    Pattern pattern = Pattern.compile(regex);
	
	        Matcher matcher = pattern.matcher(zipUC);
		    if(matcher.matches()) {
		    	return true;
		    }
	
		    return false;    		
    	} else {
    		return true;
    	}
	}

	// -------------------------------------------------------------------------------------------------------------

    /** isPhoneNumberValid: Validate phone number using Java reg ex.
    * This method checks if the input string is a valid phone number.
    * @param phoneNumber String. Phone number to validate
    * @return boolean: true if phone number is valid, false otherwise.
    */
    public static Long isPhoneNumberValid(String phoneNumber) {
    	// log.debug(phoneNumber);

	    /* Phone Number formats: (nnn)nnn-nnnn; nnnnnnnnnn; nnn-nnn-nnnn
	        ^\\(? : May start with an optional "(" .
	        (\\d{3}): Followed by 3 digits.
	        \\)? : May have an optional ")"
	        [- ]? : May have an optional "-" after the first 3 digits or after optional blank character.
	        (\\d{3}) : Followed by 3 digits.
	         [- ]? : May have another optional "-" after numeric digits.
	         (\\d{4})$ : ends with four digits.

	             Examples: Matches following phone numbers:
	             (123)456-7890, 123-456-7890, 1234567890, (123)-456-7890

	    */

    	if (phoneNumber == null) {
    		return null;
    	}

	    //Initialize reg ex for phone number.
	    String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";
	    CharSequence inputStr = phoneNumber;
	    Pattern pattern = Pattern.compile(expression);
	    Matcher matcher = pattern.matcher(inputStr);


		if (matcher.find()) {
			// log.debug(matcher.group(0) + "**" + matcher.group(1) + "**" + matcher.group(2));
			return new Long(matcher.group(1) + matcher.group(2) + matcher.group(3));
		} else {
	    	return null;
	    }
    }

	// -------------------------------------------------------------------------------------------------------------

	public static boolean isEmailAddressValid(String email) {
    	log.debug(email);

		/*
		Email format: A valid email address will have following format:
		        [\\w\\.-]+: Begins with word characters, (may include periods and hypens).
		    @: It must have a '@' symbol after initial characters.
		    ([\\w\\-]+\\.)+: '@' must follow by more alphanumeric characters (may include hypens.).
		This part must also have a "." to separate domain and subdomain names.
		    [A-Z]{2,4}$ : Must end with two to four alaphabets.
		(This will allow domain names with 2, 3 and 4 characters e.g pa, com, net, wxyz)

		Examples: Following email addresses will pass validation
		abc@xyz.net; ab.c@tx.gov
		*/
		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		//Make the comparison case-insensitive.
		Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		if(!matcher.matches()) {
			return false;
		}

		/*
		if ((!StringUtils.isNotBlank(email)				) ||
			(email.length() 	<  5					) ||
			(email.indexOf('@') == 0					) ||
			(email.substring(0,1).compareTo("@") == 0	) ||
			(email.indexOf('.') == 0					) ||
			(email.endsWith("@")						)    ) {
			return false;
		}
		*/

		String domain = email.substring(email.indexOf('@') + 1);
		if (isHostNameOK(domain) == false) {
			log.debug(domain + " is a bad domain.");
			return false;
		}

		return true;
	}

	// -------------------------------------------------------------------------------------------------------------

	public static Integer getPOBoxNumber(String address) {
		// log.debug(address);

		/*
		String poboxPattern =   "p(ost|\\.)?" 						// p p. post
		               			+ "\\s*" + "o\\.?(ff(ice|\\.)?)?" 	// o o. off off. office
		               			+ "\\s*" + "(b(ox|in|\\.)?)?" 		// b b. box bin
		               			+ "\\s*" + "((\\d)+)"; 				// Any number
		*/
		// String poboxPattern = "(?i)^\\s*P(OST)?.?\\s*(O(FF(ICE)?)?)?.?\\s+(B(IN|OX))?)|B(IN|OX))\\s*((\\d)+)";
		// String poboxPattern = "^\\s*((P(OST)?.?\\s*(O(FF(ICE)?)?)?.?\\s+(B(IN|OX))?)|B(IN|OX))\\s*((\\d)+)";
		String poboxPattern = "^\\s*(?:P(?:OST|\\.)?\\s*O(?:FF\\s+|FFICE\\s+|\\.)?)?\\s*BOX\\s+(\\d+)";

		Pattern r = Pattern.compile(poboxPattern, Pattern.CASE_INSENSITIVE);

		Matcher m = r.matcher(address);
		if (m.find()) {
			return new Integer(m.group(1));
		} else {
			return null;
		}
	}

	// -------------------------------------------------------------------------------------------------------------

	public static boolean addressesMatched(String address1, String zip1, String address2, String zip2) {
		// log.debug(address1 + "|" + zip1 + "|" + address2 + "|" + zip2);

		if ((StringUtils.isBlank(address1)	) ||
			(StringUtils.isBlank(zip1)		) ||
			(StringUtils.isBlank(address2)	) ||
			(StringUtils.isBlank(zip2)		)    ) {
			return false;
		}

		String streetNo1, zip51, streetNo2, zip52;

		zip51 = (zip1.trim() + "     ").substring(0, 5);
		zip52 = (zip2.trim() + "     ").substring(0, 5);

		Integer pobox1 = getPOBoxNumber(address1);
		if (pobox1 != null) {
			Integer pobox2 = Utils.getPOBoxNumber(address2);
			if (pobox2 != null) {
				if ((zip51.compareTo(zip52) == 0			) &&
					(pobox1.intValue() == pobox2.intValue()	)    ) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

		streetNo1 = address1.substring(0, (address1 + " ").indexOf(' '));
		streetNo2 = address2.substring(0, (address2 + " ").indexOf(' '));

		if ((zip51.compareTo(zip52) == 0			) &&
		    (streetNo1.compareTo(streetNo2)  == 0	)    ) {
			return true;
		} else {
			return false;
		}
	}

	// -------------------------------------------------------------------------------------------------------------

	public static int setBit(int c, int bitNo) {
		return (int) (c | (1 << bitNo));
	}
	public static int unsetBit(int c, int bitNo) {
		return (int) (c & ~(1 << bitNo));
	}
	public static boolean getBit(int c, int bitNo) {
		return ((c & (1 << bitNo)) != 0);
	}

	// -------------------------------------------------------------------------------------------------------------

	public static boolean findInCommaSeparatedList(String haystack, String needle) {
		StringTokenizer tokenizer = new StringTokenizer(haystack, ",");
		while (tokenizer.hasMoreTokens()) {
			if (tokenizer.nextToken().compareToIgnoreCase(needle) == 0) {
				return true;
			}
		}
		return false;
	}

	// -------------------------------------------------------------------------------------------------------------

	public static void forwardToJSP(HttpServletRequest request, HttpServletResponse response, String jspPage, String messageToUser) throws PortalException {
		request.setAttribute("messageToUser", messageToUser);
		try {
			request.getRequestDispatcher(jspPage).forward(request, response);
		} catch (ServletException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		} catch (IOException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}
	}

	public static void forwardToJSP(HttpServletRequest request, HttpServletResponse response, String jspPage, String messageToUser, Boolean messageIsNotAnError) throws PortalException {
		request.setAttribute("messageToUser", messageToUser);
		request.setAttribute("messageIsNotAnError", messageIsNotAnError);
		try {
			request.getRequestDispatcher(jspPage).forward(request, response);
		} catch (ServletException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		} catch (IOException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}
	}

	
	// -------------------------------------------------------------------------------------------------------------

	public static void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url, String messageToUser) throws PortalException {
		try {
		    log.debug("Sending redirect TO: " + url);

			if ((messageToUser != null) && (messageToUser.compareTo("") != 0)) {
			    HttpSession session = request.getSession();
			    session.setAttribute("messageToUser", messageToUser);
			}
			response.sendRedirect(url);
		} catch (IOException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}
	}
	
	// -------------------------------------------------------------------------------------------------------------

	public static UserSession checkAndRediredtUnauthenticatedUsers(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = null;
		userSession = UserSession.getUserSession(request, dbConnector, response);

		if (userSession != null) {
			return userSession;
		} else {
			log.debug("Session expired, and so forwarding to SignIn page.");
			Utils.forwardToJSP(request, response, "SignIn.jsp", null);
			return null;
		}
	}

	// -------------------------------------------------------------------------------------------------------------

	public static UserSession checkIfUserIsAuthenticated(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = null;
		userSession = UserSession.getUserSession(request, dbConnector, response);

		return userSession;
	}

	// -------------------------------------------------------------------------------------------------------------

	public static String replaceSingleQuotesIfNotNull(String s) {
		if (s == null) {
			return s;
		} else {
			return s.replaceAll("'", "''");
		}
	}

	// -------------------------------------------------------------------------------------------------------------

	public static String[] PUBLIC_EMAIL_DOMAINS = new String[] 
			{
	        "aol.com",
	        "att.net",
	        "bellsouth.net",
	        "comcast.net",
	        "gmail.com",
	        "hotmail.com",
	        "icloud.com",
	        "mail.com",
	        "msn.com",
	        "outlook.com",
	        "protonmail.com",
	        "sbcglobal.net",
	        
	        // "state.gov",
	        							
	        "verizon.net",
	        "yahoo.com",
	        "zoho.com"  
		    };	
	
	public static boolean isDomainAPublicEmailDomain(String domain) {
		return (Arrays.binarySearch(PUBLIC_EMAIL_DOMAINS, domain.toLowerCase()) >= 0);
	}
	
	// -------------------------------------------------------------------------------------------------------------
	
	public static String getEmailDomain(String email) {
		System.out.println(email);
		
		email = email.trim();
		if (StringUtils.isBlank(email)) {
			return null;			
		}
	
		int index = email.indexOf('@');
		if (index < 0) {
			return null;
		}
		
		if (email.length() <= (index + 1)) {
			return null;			
		}
		
		return email.substring(index + 1);
	}
	
	// -------------------------------------------------------------------------------------------------------------

	public static HashSet<String> mergeHashSets(HashSet<String>... hashSets) {
		HashSet<String> mergedHashSet = new HashSet<String>();
		
	    for (HashSet<String> hashSet : hashSets) { 
	    	if (hashSet != null) {
	    		mergedHashSet.addAll(hashSet);
	    	}
	    }
	    
	    return mergedHashSet;
	}

	// -------------------------------------------------------------------------------------------------------------

	public static String encrypt(String pw, String encryptThis) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(pw);
		return encryptor.encrypt(encryptThis);
	}	

	// -------------------------------------------------------------------------------------------------------------

	public static String decrypt(String pw, String decryptThis) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(pw);
		return encryptor.decrypt(decryptThis);
	}
	
	// -------------------------------------------------------------------------------------------------------------

	public static void debug(String message) {
		Utils.sendMail("sati@galco.com", "web2-app3-Portal@galco.com", "Debug", message);
	}
	
	// -------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------------

	private static final String SENDGRID_SMTP_HOST_NAME = "smtp.sendgrid.net";
	private static final String SENDGRID_SMTP_AUTH_USER = "galcoindustrial";
	private static final String SENDGRID_SMTP_AUTH_PWD = "Samtron!550";
	private static final Authenticator SENDGRID_AUTHENTICATOR = 
    new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(SENDGRID_SMTP_AUTH_USER, SENDGRID_SMTP_AUTH_PWD);
        }
    					};
    					
	public static void sendMailInANewThread(String to, String from, String subject, String body, boolean html) {
		// qqq
		
    	long startTime = System.currentTimeMillis();

        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", SENDGRID_SMTP_HOST_NAME);
        properties.put("mail.smtp.port", 587);
        properties.put("mail.smtp.auth", "true");			

	    Session session = Session.getDefaultInstance(properties, SENDGRID_AUTHENTICATOR);

	    try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, true));

			message.setSubject(subject);
			if (html == true) {
				message.setContent(body, "text/html; charset=utf-8");
			} else {
				message.setText(body);
			}

			Transport.send(message);

			log.debug("Took " + (System.currentTimeMillis() - startTime) + " milliseconds to send email to " + to);
		} catch (MessagingException e) {
			// throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
			log.error("Exception trying to send email to: " + to + ", subject: " + subject + ", body: " + body, e);			
		}
	}

	public static void sendMailInANewThread_O_UsesBlueRockAndThisIsBeforeAzureSendgrid(String to, String from, String subject, String body, boolean html) {
		final String toF = to, fromF = from, subjectF = subject, bodyF = body;
		final boolean htmlF = html;
		Thread thread = new Thread() {
								public void run() {
									try {
										log.debug("Trying to send email to: " + toF + ", subject: " + subjectF + ", body: " + bodyF);

										sendMailNew(toF, fromF, subjectF, bodyF, htmlF);
									} catch (PortalException e) {
										log.error("Exception trying to send email to: " + toF + ", subject: " + subjectF + ", body: " + bodyF, e.getE());
									}
								}
						};

		thread.start();
	}

	public static void sendMailNew(String to, String from, String subject, String body, boolean html) throws PortalException {
    	long startTime = System.currentTimeMillis();

		Properties props = new Properties();

	    props.put("mail.smtp.port", "25");
	    // props.put("mail.smtp.host", "galco-com.mail.protection.outlook.com");
	    props.put("mail.smtp.host", "mx2.bluerocktech.com");
	    
	    // log.debug("ZSendHtmlMail - sending to: " + to);

	    Session session = Session.getDefaultInstance(props);

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, true));

			message.setSubject(subject);
			if (html == true) {
				message.setContent(body, "text/html; charset=utf-8");
			} else {
				message.setText(body);
			}

			Transport.send(message);

			log.debug("Took " + (System.currentTimeMillis() - startTime) + " milliseconds to send email to " + to);
		} catch (MessagingException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}
	}

	public static void sendMailJustLogError(String to, String from, String subject, String body) {
		sendMailInANewThread(to, from, subject, body, false);
	}

	public static void sendMail(String to, String from, String subject, String body) {
		sendMailInANewThread(to, from, subject, body, false);
	}
	
	public static void sendHtmlMail(String to, String from, String subject, String body) {
		sendMailInANewThread(to, from, subject, body, true);
	}

	// -------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------------
	
	public static void sendMailJustLogErrorO1(String to, String from, String subject, String body) {
		sendMail(to, from, subject, body);
	}

	public static void sendMailO1(String to, String from, String subject, String body) throws PortalException {
		log.debug("ZSendMail-Bfr : " + FasUtils.convertDateTo_mmDdYyHhMmSsSss_DisplayFormat(new Date()));
		
		Properties props = new Properties();
			
	    props.put("mail.smtp.port", "25");
	    // props.put("mail.smtp.host", "galco-com.mail.protection.outlook.com");
	    props.put("mail.smtp.host", "mx2.bluerocktech.com");

	    log.debug("ZSendMail - sending to: " + to);
	   	    
	    Session session = Session.getDefaultInstance(props);

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, true));

			message.setSubject(subject);
			message.setText(body);

			Transport.send(message);
			
			log.debug("ZSendMail-Aft : " + FasUtils.convertDateTo_mmDdYyHhMmSsSss_DisplayFormat(new Date()));			
		} catch (MessagingException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}
	}
	
	public static void sendHtmlMailO1(String to, String from, String subject, String body) throws PortalException {
		log.debug("ZSendHtmlMail-Bfr : " + FasUtils.convertDateTo_mmDdYyHhMmSsSss_DisplayFormat(new Date()));

		Properties props = new Properties();

	    props.put("mail.smtp.port", "25");
	    // props.put("mail.smtp.host", "galco-com.mail.protection.outlook.com");
	    props.put("mail.smtp.host", "mx2.bluerocktech.com");
	    
	    log.debug("ZSendHtmlMail - sending to: " + to);

	    Session session = Session.getDefaultInstance(props);

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, true));

			message.setSubject(subject);
			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			log.debug("ZSendHtmlMail-Aft : " + FasUtils.convertDateTo_mmDdYyHhMmSsSss_DisplayFormat(new Date()));
		} catch (MessagingException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}
	}
	
	// -------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------------

	public static void sendMail_Bkup_07_18_2018(String to, String from, String subject, String body) throws PortalException {
		Properties props = new Properties();
		
	    props.put("mail.smtp.port", "25");
	    // props.put("mail.smtp.host", "galco-com.mail.protection.outlook.com");
	    props.put("mail.smtp.host", "mx2.bluerocktech.com");

	    log.debug("subb 00601 17:21 mx2.bluerocktech.com");
	    log.debug("subb 00601 17:21 from: " + from);
	    log.debug("subb 00601 17:21 to  : " + to);
	    
	    Session session = Session.getDefaultInstance(props);

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, true));

			message.setSubject(subject);
			message.setText(body);

			Transport.send(message);
		} catch (MessagingException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}
	}

	public static void sendMailOld3(String to, String from, String subject, String body) throws PortalException {
		Properties props = new Properties();
		
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.port", "25");
	    props.put("mail.smtp.host", "galco-com.mail.protection.outlook.com");
	    props.put("mail.smtp.auth", "true");
	    from = "webportal@galco.com";
	    Session session = Session.getInstance(props, new Authenticator() {
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication("webportal@galco.com",
	                    "durPADsujYLM1");
	        }
	    });		

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, true));

			message.setSubject(subject);
			message.setText(body);

			Transport.send(message);
		} catch (MessagingException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}
	}
	
	public static void sendHtmlMailOld3(String to, String from, String subject, String body) throws PortalException {
		Properties props = new Properties();
		
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.port", "25");
	    props.put("mail.smtp.host", "galco-com.mail.protection.outlook.com");
	    props.put("mail.smtp.auth", "true");
	    from = "webportal@galco.com";
	    Session session = Session.getInstance(props, new Authenticator() {
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication("webportal@galco.com",
	                    "durPADsujYLM1");
	        }
	    });		

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, true));

			message.setSubject(subject);
			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);
		} catch (MessagingException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}
	}

	public static void sendHtmlMailOld(String to, String from, String subject, String body) throws PortalException {
		Properties props = new Properties();
		
		// props.put("mail.smtp.host", "sigma.galco.com");
		props.put("mail.smtp.host", "galco-com.mail.protection.outlook.com");

		// props.put("mail.smtp.socketFactory.port", "465");
		// props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		// props.put("mail.smtp.auth", "true");
		// props.put("mail.smtp.port", "465");
		// props.put("mail.smtp.port", "25");

		Session session = Session.getDefaultInstance(props);
		/*
		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("root","");
					}
				});
		*/

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, true));

			message.setSubject(subject);
			
			message.setContent(body, "text/html; charset=utf-8");
			// message.setText(body);

			Transport.send(message);
		} catch (MessagingException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}
	}

	public static void sendMailOld2(String to, String from, String subject, String body) throws PortalException {
		Properties props = new Properties();
		props.put("mail.smtp.host", "sigma.galco.com");
		// props.put("mail.smtp.socketFactory.port", "465");
		// props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		// props.put("mail.smtp.auth", "true");
		// props.put("mail.smtp.port", "465");
		// props.put("mail.smtp.port", "25");

		Session session = Session.getDefaultInstance(props);
		/*
		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("root","");
					}
				});
		*/

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, true));

			message.setSubject(subject);
			message.setText(body);

			Transport.send(message);
		} catch (MessagingException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}
	}

	public static void sendMailOld(String to, String from, String subject, String body) throws PortalException {
		Properties props = new Properties();
		props.put("mail.smtp.host", "sigma.galco.com");
		// props.put("mail.smtp.socketFactory.port", "465");
		// props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		// props.put("mail.smtp.auth", "true");
		// props.put("mail.smtp.port", "465");
		// props.put("mail.smtp.port", "25");

		Session session = Session.getDefaultInstance(props);
		/*
		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("root","");
					}
				});
		*/

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			// message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("sati@galco.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);
			message.setText(body);

			Transport.send(message);
		} catch (MessagingException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}
	}

	// -------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------------

	public static String convertHashMapToXML(HashMap<String, String> hashMap) throws PortalException {
		try {
			// hashMap = new HashMap<String, String>();
			// hashMap.put("a", "1");
			// hashMap.put("b", "2");
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("logMsg");
			doc.appendChild(rootElement);
	
			for (Map.Entry<String,String> element : hashMap.entrySet() ) {
				System.out.println(element.getKey() + " " + element.getValue());
			    rootElement.setAttribute(element.getKey(), element.getValue());
			}

			StringWriter sw = new StringWriter();
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.transform(new DOMSource(doc), new StreamResult(sw));

			System.out.println(sw.toString());
			return sw.toString();
		} catch (Exception e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}
	}
	
	public static ArrayList<String> loadFileIntoArrayList(String filePath) {
		try {
			FileInputStream fstream = new FileInputStream(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String curLine;
			ArrayList<String> arrayList = new ArrayList<String>(130000);

			while ((curLine = br.readLine()) != null) {
				// System.out.println(curLine);
				arrayList.add(curLine);
			}
			br.close();
			
			// System.out.println (arrayList.size());
			return arrayList;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new ArrayList<String>();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Long extractPhoneNumberFromString(String phoneNumberString) {
		String regEx = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";
		Pattern p = Pattern.compile(regEx);
		Matcher matcher = p.matcher(phoneNumberString);
	    if (matcher.matches()) {
	        System.out.printf("%s is valid%n", phoneNumberString);		
		    System.out.println("\tNumber is: " + matcher.group(1) + matcher.group(2) + matcher.group(3));		        
		    return new Long(matcher.group(1) + matcher.group(2) + matcher.group(3));		        
	    } else {
	        System.out.printf("%s is not valid%n", phoneNumberString);    
	        return null;
	    }
	}
	
	// ---------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------

	public static long startTime_TrackTime;
	public static HashMap<String, Long> hashMap_TrackTime = new HashMap<String, Long>(100);
	
	public static void trackTime(String id, boolean start) {
		if (hashMap_TrackTime.get(id) == null) {
			hashMap_TrackTime.put(id,  System.currentTimeMillis());
		} else {
			if (start) {
				hashMap_TrackTime.put(id,  hashMap_TrackTime.get(id) + System.currentTimeMillis());
			} else {
				hashMap_TrackTime.put(id,  hashMap_TrackTime.get(id) - System.currentTimeMillis());				
			}
		}
	}
	
	public static void trackTime_Print() {
		Iterator<Entry<String, Long>> it = hashMap_TrackTime.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, Long> pair = (Map.Entry<String, Long>) it.next();

			log.debug(pair.getKey() + " - time took in seconds - " + (-1 * pair.getValue()) / 1000);
		}
	}

	// ---------------------------------------------------------------------------------------------------------

	public static void sleep(long timeToSleepInMillis) {
		try {
			Thread.sleep(timeToSleepInMillis);
		} catch (InterruptedException e) {
			Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Problem in Batch Process of " + Parms.HOST_NAME, "InterruptedException occurred.");
			log.debug("Thread was interrupted");
			log.debug(e);
		} catch (Exception e) {
			Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Problem in Batch Process of" + Parms.HOST_NAME, "Exception occurred while sleeping.");
			log.debug("Exception occurred.");
			log.debug(e);
		}
	}
	
	// ---------------------------------------------------------------------------------------------------------
	
	public static void main(String[] args) {
		try {
			sendMailInANewThread("rajuati@yahoo.com", "portal@galco.com", "Test1", "Data1", false);
			// sendMailNew("sati@galco.com", "portal@galco.com", "Test2", "Data2", false);
			// sendMailNew("sati@galco.com", "portal@galco.com", "Test3", "Data3", false);
			// sendMailNew("sati@galco.com", "portal@galco.com", "Test4", "Data4", false);
			// sendMailNew("sati@galco.com", "portal@galco.com", "Test5", "<h1>Data5, howdy?</h1>", true);
			
			// System.out.println(System.currentTimeMillis());
			// sendMail("sati@galco.com", "portal@galco.com", "Test", "");
			// System.out.println(System.currentTimeMillis());
		// } catch (PortalException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
