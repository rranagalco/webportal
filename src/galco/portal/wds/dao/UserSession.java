package galco.portal.wds.dao;

import galco.portal.config.Parms;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

public class UserSession {
	private static Logger log = Logger.getLogger(UserSession.class);

	private String sessionId;
    private String cust_num;
    private int cont_no;
    private String logonTime;
	private String email;
	private String username;

    public UserSession() {
    }

    public UserSession(String sessionId, String cust_num, int cont_no, String logonTime, String email, String username) {
		super();
		this.sessionId = sessionId;
		this.cust_num = cust_num;
		this.cont_no = cont_no;
		this.logonTime = logonTime;
		this.email = email;
		this.username = username;
	}

    private static String getQueryFieldSelectionString() {
        return "sessionId,cust_num,cont_no,logonTime,email,username";
    }

    public static ArrayList<UserSession> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<UserSession> al = new ArrayList<UserSession>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

			while (rs.next()) {
			    UserSession userSession = new UserSession();

			    userSession.setSessionid(JDBCUtils.getStringFromResultSet(rs, "sessionId"));
			    userSession.setCust_num(JDBCUtils.getStringFromResultSet(rs, "cust_num"));
			    userSession.setCont_no(new Integer(JDBCUtils.getStringFromResultSet(rs, "cont_no")).intValue());
			    userSession.setLogontime(JDBCUtils.getStringFromResultSet(rs, "logonTime"));
			    userSession.setEmail(JDBCUtils.getStringFromResultSet(rs, "email"));
			    userSession.setUsername(JDBCUtils.getStringFromResultSet(rs, "username"));

			    al.add(userSession);
			}

			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		} catch (NumberFormatException | SQLException e) {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e3) {
			}
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e3) {
			}

			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}

        if (al.size() == 0) {
            return null;
        }

        return al;
    }

    public static void printDAOObjects(ArrayList<UserSession> al) {
        if (al == null) {
            return;
        }

        for (Iterator<UserSession> iterator = al.iterator(); iterator.hasNext();) {
            UserSession userSession = iterator.next();

			log.debug("sessionId   : " + userSession.getSessionid());
			log.debug("cust_num    : " + userSession.getCust_num());
			log.debug("cont_no     : " + userSession.getCont_no());
			log.debug("logonTime   : " + userSession.getLogontime());
			log.debug("email   	   : " + userSession.getEmail());
			log.debug("username    : " + userSession.getUsername());
        }
    }

    // -------------------------------------------------------------------------------------------------------------

	public static String generateSessionID() {
		return RandomStringUtils.random(30, true, true);
	}

    // -------------------------------------------------------------------------------------------------------------

	public static ArrayList<UserSession> getUserSession(DBConnector dbConnector, String sessionId) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.UserSession " +
							 "where sessionId = '" + sessionId + "'";
		ArrayList<UserSession> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

	// -------------------------------------------------------------------------------------------------------------

	public void persist(DBConnector dbConnector) throws PortalException {
		String insertQueryString =
		        "insert into pub.UserSession "                         +
				"(sessionId, cust_num, cont_no, logonTime, email, username) "        +
				"values ('" + sessionId + "', " + "'" + cust_num     + "', " + "'" + cont_no + "', " + "'" + logonTime + "', " + "'" + Utils.replaceSingleQuotesIfNotNull(email) + "', " + "'" + Utils.replaceSingleQuotesIfNotNull(username) + "')";

		JDBCUtils.executeUpdateWDS(dbConnector, insertQueryString);
		// JDBCUtils.commit();
	}

	// -------------------------------------------------------------------------------------------------------------

    public static void createTable(DBConnector dbConnector) throws PortalException {
        // JDBCUtils.executeUpdateWDS(dbConnector, "drop table pub.UserSession");

        String createTable =
                "CREATE TABLE pub.UserSession ( " +
                "   sessionId varchar(30), " +
                "   cust_num varchar(10), " +
                "   cont_no int, " +
                "   logonTime varchar(30), " +
                "   email varchar(60), " +
                "   username varchar(60), PRIMARY KEY (sessionId) " +
                ")";
        JDBCUtils.executeUpdateWDS(dbConnector, createTable);
    }

    // -------------------------------------------------------------------------------------------------------------

    public static UserSession trackUserSession(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response,
    				String cust_num, int cont_no, String email, String username) throws PortalException {
    	String sessionId = RandomStringUtils.random(30, true, true);
		String logonTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());

		UserSession userSession = new UserSession(sessionId, cust_num, cont_no, logonTime, email, username);
		userSession.persist(dbConnector);

		Cookie[] requestCookies = request.getCookies();

		/*
		if ((requestCookies != null) && (requestCookies.length > 0)) {
	        for(Cookie c : requestCookies) {
	            log.debug("Name=" + c.getName() + ", Value=" + c.getValue() + ", Comment=" + c.getComment()
	                              + ", Domain=" + c.getDomain() + ", MaxAge=" + c.getMaxAge() + ", Path=" + c.getPath()
	                              + ", Version=" + c.getVersion());
	        }
		}
		*/

        Cookie sessionIdCookie = new Cookie("SessionId", sessionId);
        sessionIdCookie.setPath("/");
        // log.debug("Setting cookie path as root");
        sessionIdCookie.setComment("SessionId");
        sessionIdCookie.setMaxAge(7*24*60*60);
        response.addCookie(sessionIdCookie);

        /*
        Cookie pidCookie = new Cookie("pid", "ZYX-" + RandomStringUtils.random(20, true, true));
        pidCookie.setPath("/");
        pidCookie.setComment("pid");
    	pidCookie.setMaxAge(0);
        response.addCookie(pidCookie);
        */        
        
        return userSession;
    }

    public static UserSession getUserSession(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
    	Cookie sessionIdCookie = null;
    	Cookie[] requestCookies = request.getCookies();

    	if (requestCookies == null) {
    		return null;
    	}
        for(Cookie c : requestCookies) {
        	/*
            log.debug("Name=" + c.getName() + ", Value=" + c.getValue() + ", Comment=" + c.getComment()
                    + ", Domain=" + c.getDomain() + ", MaxAge=" + c.getMaxAge() + ", Path=" + c.getPath()
                    + ", Version=" + c.getVersion());
            */
            if (c.getName().compareTo("SessionId") == 0) {
            	sessionIdCookie = c;
            	break;
            }
        }
        if (sessionIdCookie == null) {
        	return null;
        }

        String sessionId = sessionIdCookie.getValue();

        ArrayList<UserSession> al = UserSession.getUserSession(dbConnector, sessionId);
        if (al == null) {
        	return null;
        }

        UserSession userSession = al.get(0);

        Date loginDate;
		try {
			loginDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(userSession.getLogontime());
		} catch (ParseException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}

		// aaa
		// log.debug("Cur date : " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date()));
		// log.debug("loginDate: " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(loginDate));


        if (new Date().getTime() > (loginDate.getTime() + (Parms.SESSION_DURATION_IN_MILLIS))) {
        	return null;
        }
        userSession.setLogontime(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date()));

        JDBCUtils.executeUpdateWDS(dbConnector, "UPDATE PUB.UserSession SET logonTime = '" + userSession.getLogontime() + "' WHERE sessionId = '" + userSession.getSessionid() + "'");
		// JDBCUtils.commit();

        // log.debug("Sessionid: " + userSession.getSessionid());

        return userSession;
    }

    public static void expireUserSession(HttpServletRequest request, HttpServletResponse response, DBConnector dbConnector) throws PortalException {
    	Cookie sessionIdCookie = null, pidCookie = null;
    	Cookie[] requestCookies = request.getCookies();

    	if (requestCookies == null) {
    		return;
    	}
        for(Cookie c : requestCookies) {
            // log.debug("Name=" + c.getName() + ", Value=" + c.getValue() + ", Comment=" + c.getComment() + ", Domain=" + c.getDomain() + ", MaxAge=" + c.getMaxAge() + ", Path=" + c.getPath() + ", Version=" + c.getVersion());
            if (c.getName().compareTo("SessionId") == 0) {
            	sessionIdCookie = c;
            }
            if (c.getName().compareTo("pid") == 0) {
            	pidCookie = c;
            }
            
            if ((sessionIdCookie != null) && (pidCookie != null)) {
            	break;
            }
        }
        
        if (pidCookie != null) {
            pidCookie = new Cookie("pid", "ZYX-" + RandomStringUtils.random(20, true, true));
            pidCookie.setPath("/");
            pidCookie.setComment("pid");
        	pidCookie.setMaxAge(0);
            response.addCookie(pidCookie);
        	// Weborder.deleteWeborder(dbConnector, pidCookie.getValue());        	
        }
        
        if (sessionIdCookie == null) {
        	return;
        }
        sessionIdCookie.setMaxAge(0);
        response.addCookie(sessionIdCookie);        

        String sessionId = sessionIdCookie.getValue();

        ArrayList<UserSession> al = UserSession.getUserSession(dbConnector, sessionId);
        if (al == null) {
        	return;
        }

        UserSession userSession = al.get(0);

        JDBCUtils.executeUpdateWDS(dbConnector, "DELETE FROM PUB.UserSession WHERE sessionId = '" + userSession.getSessionid() + "'");
		// JDBCUtils.commit();
    }

	// -------------------------------------------------------------------------------------------------------------

    public String getSessionid() {
        return sessionId;
    }
    public void setSessionid(String sessionId) {
        this.sessionId = sessionId;
    }
    public String getCust_num() {
        return cust_num;
    }
    public void setCust_num(String cust_num) {
        this.cust_num = cust_num;
    }
    public int getCont_no() {
        return cont_no;
    }
    public void setCont_no(int cont_no) {
        this.cont_no = cont_no;
    }
    public String getLogontime() {
        return logonTime;
    }
    public void setLogontime(String logonTime) {
        this.logonTime = logonTime;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
