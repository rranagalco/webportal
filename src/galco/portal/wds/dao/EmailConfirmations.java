package galco.portal.wds.dao;

import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

public class EmailConfirmations {
	private static Logger log = Logger.getLogger(EmailConfirmations.class);

	public static final long SESSION_DURATION_IN_MILLIS = 1800 * 1000;

    private String request_time;
	private String random_key;
	private String username;

    public EmailConfirmations() {
    }

    public EmailConfirmations(String request_time, String random_key, String username) {
		super();
		this.request_time = request_time;
		this.random_key = random_key;
		this.username = username;
	}

    private static String getQueryFieldSelectionString() {
        return "request_time,random_key,username";
    }

    public static ArrayList<EmailConfirmations> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<EmailConfirmations> al = new ArrayList<EmailConfirmations>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementSRO();
            rs = stmt.executeQuery(queryString);

			while (rs.next()) {
				EmailConfirmations emailConfirmations = new EmailConfirmations();

				emailConfirmations.setRequest_time(JDBCUtils.getStringFromResultSet(rs, "request_time"));
				emailConfirmations.setRandom_key(JDBCUtils.getStringFromResultSet(rs, "random_key"));
				emailConfirmations.setUsername(JDBCUtils.getStringFromResultSet(rs, "username"));

			    al.add(emailConfirmations);
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

    public static void printDAOObjects(ArrayList<EmailConfirmations> al) {
        if (al == null) {
            return;
        }

        for (Iterator<EmailConfirmations> iterator = al.iterator(); iterator.hasNext();) {
        	EmailConfirmations emailConfirmations = iterator.next();

			log.debug("request_time : " + emailConfirmations.getRequest_time());
			log.debug("random_key   : " + emailConfirmations.getRandom_key());
			log.debug("username     : " + emailConfirmations.getUsername() );
        }
    }

    // -------------------------------------------------------------------------------------------------------------

	public static ArrayList<EmailConfirmations> getEmailConfirmations(DBConnector dbConnector, String username) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.EmailConfirmations " +
							 "where username = '" + username + "'";
		ArrayList<EmailConfirmations> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

	// -------------------------------------------------------------------------------------------------------------

	public static void delete(DBConnector dbConnector, String username) throws PortalException {
		String deleteQueryString =
		        "delete from pub.EmailConfirmations where username = '" + username + "'";

		JDBCUtils.executeUpdateSRO(dbConnector, deleteQueryString);
		// JDBCUtils.commit();
	}

	// -------------------------------------------------------------------------------------------------------------

	public void persist(DBConnector dbConnector) throws PortalException {
		String insertQueryString =
		        "insert into pub.EmailConfirmations "                         +
				"(request_time, random_key, username) "        +
				"values ('" + request_time + "', " + "'" + random_key + "', " + "'" + username + "')";

		JDBCUtils.executeUpdateSRO(dbConnector, insertQueryString);
		// JDBCUtils.commit();
	}

	// -------------------------------------------------------------------------------------------------------------

    public static void createTable(DBConnector dbConnector) throws PortalException {
        JDBCUtils.executeUpdateSRO(dbConnector, "drop table pub.EmailConfirmations");

        String createTable =
                "CREATE TABLE pub.EmailConfirmations ( " +
                "   request_time varchar(30), " +
                "   random_key varchar(35), " +
                "   username varchar(60), PRIMARY KEY (username) " +
                ")";
        JDBCUtils.executeUpdateSRO(dbConnector, createTable);
    }

    // -------------------------------------------------------------------------------------------------------------

    public String getRequest_time() {
        return request_time;
    }
    public void setRequest_time(String request_time) {
        this.request_time = request_time;
    }
    public String getRandom_key() {
        return random_key;
    }
    public void setRandom_key(String random_key) {
        this.random_key = random_key;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
