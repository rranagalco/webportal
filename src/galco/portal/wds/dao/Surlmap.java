package galco.portal.wds.dao;

import galco.portal.utils.JDBCUtils;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class Surlmap {

    String name;
    String mfg_code;

    private static String getQueryFieldSelectionString() {
        return "name,mfg_code";
    }

    public static ArrayList<Surlmap> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Surlmap> al = new ArrayList<Surlmap>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWEB();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
            	Surlmap surlmap = new Surlmap();

            	surlmap.setName(JDBCUtils.getStringFromResultSet(rs, "name"));
            	surlmap.setMfg_code(JDBCUtils.getStringFromResultSet(rs, "mfg_code"));

                al.add(surlmap);
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

    public static void printDAOObjects(ArrayList<Surlmap> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Surlmap> iterator = al.iterator(); iterator.hasNext();) {
        	Surlmap surlmap = iterator.next();
            System.out.println  (
                    "name: " + surlmap.getName() + "\n" +
                    "mfg_code: " + surlmap.getMfg_code() + "\n"
                                );
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Surlmap> getSurlmap(DBConnector dbConnector, String name, String mfg_code) throws PortalException {
        String queryString = "select " + getQueryFieldSelectionString() + " from pub.surlmap " +
                "where name = '" + name + "'" +
        		"  and mfg_code = '" + mfg_code + "'";

        ArrayList<Surlmap> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
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
