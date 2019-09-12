package galco.portal.wds.dao;

import galco.portal.utils.JDBCUtils;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class Urlmap {

    String name;
    String mfg_code;

    private static String getQueryFieldSelectionString() {
        return "name,mfg_code";
    }

    public static ArrayList<Urlmap> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Urlmap> al = new ArrayList<Urlmap>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWEB();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
                Urlmap urlmap = new Urlmap();

                urlmap.setName(JDBCUtils.getStringFromResultSet(rs, "name"));
                urlmap.setMfg_code(JDBCUtils.getStringFromResultSet(rs, "mfg_code"));

                al.add(urlmap);
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

    public static void printDAOObjects(ArrayList<Urlmap> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Urlmap> iterator = al.iterator(); iterator.hasNext();) {
            Urlmap urlmap = iterator.next();
            System.out.println  (
                    "name: " + urlmap.getName() + "\n" +
                    "mfg_code: " + urlmap.getMfg_code() + "\n"
                                );
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Urlmap> getUrlmap(DBConnector dbConnector, String name, String mfg_code) throws PortalException {
        String queryString = "select " + getQueryFieldSelectionString() + " from pub.urlmap " +
                "where name = '" + name + "'" +
        		"  and mfg_code = '" + mfg_code + "'";

        ArrayList<Urlmap> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
        // printDAOObjects(al);
        return al;
    }

    // -------------------------------------------------------------------------------------------------------------

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMfg_code() {
        return mfg_code;
    }
    public void setMfg_code(String mfg_code) {
        this.mfg_code = mfg_code;
    }

}
