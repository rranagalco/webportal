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

public class PortalLog {
	private static Logger log = Logger.getLogger(PortalLog.class);

	private String username;
    private String logMsg;

    public PortalLog() {
    }

    public PortalLog(String username, String logMsg) {
		super();
		this.username = username;
		this.logMsg = logMsg;
	}

    private static String getQueryFieldSelectionString() {
        return "username, logMsg";
    }

    public static ArrayList<PortalLog> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<PortalLog> al = new ArrayList<PortalLog>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

			while (rs.next()) {
			    PortalLog portalLog = new PortalLog();

			    portalLog.setUsername(JDBCUtils.getStringFromResultSet(rs, "username"));
			    portalLog.setLogMsg(JDBCUtils.getStringFromResultSet(rs, "logMsg"));

			    al.add(portalLog);
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

    public static void printDAOObjects(ArrayList<PortalLog> al) {
        if (al == null) {
            return;
        }

        for (Iterator<PortalLog> iterator = al.iterator(); iterator.hasNext();) {
            PortalLog portalLog = iterator.next();

			log.debug("username : " + portalLog.getUsername());
			log.debug("logMsg   : " + portalLog.getLogMsg());						
        }
    }

    // -------------------------------------------------------------------------------------------------------------

	public static ArrayList<PortalLog> getPortalLog(DBConnector dbConnector, String username) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.PortalLog " +
							 "where username = '" + username + "'";
		ArrayList<PortalLog> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

    // -------------------------------------------------------------------------------------------------------------

	public static ArrayList<PortalLog> writeToLog(DBConnector dbConnector, String username) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.PortalLog " +
							 "where username = '" + username + "'";
		ArrayList<PortalLog> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

	// -------------------------------------------------------------------------------------------------------------

	public void persist(DBConnector dbConnector) throws PortalException {
		String insertQueryString =
		        "insert into pub.PortalLog "                         +
				"(username, logMsg) "        +
				"values ('" + username + "', '" + Utils.replaceSingleQuotesIfNotNull(logMsg) + "')";

		JDBCUtils.executeUpdateWDS(dbConnector, insertQueryString);
		// JDBCUtils.commit();
	}

	// -------------------------------------------------------------------------------------------------------------

    public static void createTable(DBConnector dbConnector) throws PortalException {
        // JDBCUtils.executeUpdateWDS(dbConnector, "drop table pub.PortalLog");

        String createTable =
                "CREATE TABLE pub.PortalLog ( " +
                "   username varchar(60), " +
                "   logMsg varchar(5000), " +
                "   PRIMARY KEY (username) " +
                ")";
        JDBCUtils.executeUpdateWDS(dbConnector, createTable);
    }

	// -------------------------------------------------------------------------------------------------------------

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getLogMsg() {
        return logMsg;
    }
    public void setLogMsg(String logMsg) {
        this.logMsg = logMsg;
    }
    
}
