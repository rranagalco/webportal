
package galco.portal.wds.dao;

import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class Ps_subfamily {
	private static Logger log = Logger.getLogger(Ps_subfamily.class);

    String family_code;
    String keywords_plural;
    String subfamily_name;
    String subfamily_code;

    private static String getQueryFieldSelectionString() {
        return "family_code,keywords_plural,subfamily_name,subfamily_code";
    }

    public static ArrayList<Ps_subfamily> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Ps_subfamily> al = new ArrayList<Ps_subfamily>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

			while (rs.next()) {
			    Ps_subfamily ps_subfamily = new Ps_subfamily();

			    ps_subfamily.setFamily_code(JDBCUtils.getStringFromResultSet(rs, "family_code"));
			    ps_subfamily.setKeywords_plural(JDBCUtils.getStringFromResultSet(rs, "keywords_plural"));
			    ps_subfamily.setSubfamily_name(JDBCUtils.getStringFromResultSet(rs, "subfamily_name"));
			    ps_subfamily.setSubfamily_code(JDBCUtils.getStringFromResultSet(rs, "subfamily_code"));

			    al.add(ps_subfamily);
			}

			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		} catch (SQLException e) {
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

    public static void printDAOObjects(ArrayList<Ps_subfamily> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Ps_subfamily> iterator = al.iterator(); iterator.hasNext();) {
            Ps_subfamily ps_subfamily = iterator.next();

			log.debug("family_code     : " + ps_subfamily.getFamily_code());
			log.debug("keywords_plural : " + ps_subfamily.getKeywords_plural());
			log.debug("subfamily_name  : " + ps_subfamily.getSubfamily_name());
			log.debug("subfamily_code  : " + ps_subfamily.getSubfamily_code());
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Ps_subfamily> getPs_subfamily(DBConnector dbConnector, String family_code, String subfamily_code) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.ps_subfamily " +
							 "where family_code = '" + family_code + "' and subfamily_code = '" + subfamily_code + "'";

		ArrayList<Ps_subfamily> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

    // -------------------------------------------------------------------------------------------------------------

    public String getFamily_code() {
        return family_code;
    }
    public void setFamily_code(String family_code) {
        this.family_code = family_code;
    }
    public String getKeywords_plural() {
        return keywords_plural;
    }
    public void setKeywords_plural(String keywords_plural) {
        this.keywords_plural = keywords_plural;
    }
    public String getSubfamily_name() {
        return subfamily_name;
    }
    public void setSubfamily_name(String subfamily_name) {
        this.subfamily_name = subfamily_name;
    }
    public String getSubfamily_code() {
        return subfamily_code;
    }
    public void setSubfamily_code(String subfamily_code) {
        this.subfamily_code = subfamily_code;
    }

}
