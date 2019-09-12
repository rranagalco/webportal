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

public class PasswordResetRequests {
	private static Logger log = Logger.getLogger(PasswordResetRequests.class);

	public static final long SESSION_DURATION_IN_MILLIS = 1800 * 1000;

    private String request_time;
	private String random_key;
	private String username;

    public PasswordResetRequests() {
    }

    public PasswordResetRequests(String request_time, String random_key, String username) {
		super();
		this.request_time = request_time;
		this.random_key = random_key;
		this.username = username;
	}

    private static String getQueryFieldSelectionString() {
        return "request_time,random_key,username";
    }

    public static ArrayList<PasswordResetRequests> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<PasswordResetRequests> al = new ArrayList<PasswordResetRequests>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

			while (rs.next()) {
				PasswordResetRequests passwordResetRequests = new PasswordResetRequests();

				passwordResetRequests.setRequest_time(JDBCUtils.getStringFromResultSet(rs, "request_time"));
				passwordResetRequests.setRandom_key(JDBCUtils.getStringFromResultSet(rs, "random_key"));
				passwordResetRequests.setUsername(JDBCUtils.getStringFromResultSet(rs, "username"));

			    al.add(passwordResetRequests);
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

    public static void printDAOObjects(ArrayList<PasswordResetRequests> al) {
        if (al == null) {
            return;
        }

        for (Iterator<PasswordResetRequests> iterator = al.iterator(); iterator.hasNext();) {
        	PasswordResetRequests passwordResetRequests = iterator.next();

			log.debug("request_time : " + passwordResetRequests.getRequest_time());
			log.debug("random_key   : " + passwordResetRequests.getRandom_key());
			log.debug("username     : " + passwordResetRequests.getUsername() );
        }
    }

    // -------------------------------------------------------------------------------------------------------------

	public static ArrayList<PasswordResetRequests> getPasswordResetRequests(DBConnector dbConnector, String username) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.PasswordResetRequests " +
							 "where username = '" + username + "'";
		ArrayList<PasswordResetRequests> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

	// -------------------------------------------------------------------------------------------------------------

	public static void delete(DBConnector dbConnector, String username) throws PortalException {
		String deleteQueryString =
		        "delete from pub.PasswordResetRequests where username = '" + username + "'";

		JDBCUtils.executeUpdateWDS(dbConnector, deleteQueryString);
		// JDBCUtils.commit();
	}

	// -------------------------------------------------------------------------------------------------------------

	public void persist(DBConnector dbConnector) throws PortalException {
		String insertQueryString =
		        "insert into pub.PasswordResetRequests "                         +
				"(request_time, random_key, username) "        +
				"values ('" + request_time + "', " + "'" + random_key + "', " + "'" + username + "')";

		JDBCUtils.executeUpdateWDS(dbConnector, insertQueryString);
		// JDBCUtils.commit();
	}

	// -------------------------------------------------------------------------------------------------------------

    public static void createTable(DBConnector dbConnector) throws PortalException {
        // JDBCUtils.executeUpdate(request, "drop table pub.PasswordResetRequests");

        String createTable =
                "CREATE TABLE pub.PasswordResetRequests ( " +
                "   request_time varchar(30), " +
                "   random_key varchar(35), " +
                "   username varchar(60), PRIMARY KEY (username) " +
                ")";
        JDBCUtils.executeUpdateWDS(dbConnector, createTable);
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
