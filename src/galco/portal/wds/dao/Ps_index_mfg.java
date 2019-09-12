package galco.portal.wds.dao;

import galco.portal.utils.JDBCUtils;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class Ps_index_mfg {
	private static Logger log = Logger.getLogger(Ps_index_mfg.class);

    String mfg_code;

    private static String getQueryFieldSelectionString() {
        return "mfg_code";
    }

    public static ArrayList<Ps_index_mfg> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Ps_index_mfg> al = new ArrayList<Ps_index_mfg>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWEB();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
                Ps_index_mfg ps_index_mfg = new Ps_index_mfg();

                ps_index_mfg.setMfg_code(JDBCUtils.getStringFromResultSet(rs, "mfg_code"));

                al.add(ps_index_mfg);
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

    public static void printDAOObjects(ArrayList<Ps_index_mfg> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Ps_index_mfg> iterator = al.iterator(); iterator.hasNext();) {
            Ps_index_mfg ps_index_mfg = iterator.next();

			log.debug("mfg_code: " + ps_index_mfg.getMfg_code());
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Ps_index_mfg> getPs_index_mfg(DBConnector dbConnector, String mfg_code) throws PortalException {
        String queryString = "select " + getQueryFieldSelectionString() + " from pub.ps_index_mfg " +
                             "where mfg_code = '" + mfg_code + "'";

        ArrayList<Ps_index_mfg> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
        // printDAOObjects(al);
        return al;
    }

    // -------------------------------------------------------------------------------------------------------------

    public String getMfg_code() {
        return mfg_code;
    }
    public void setMfg_code(String mfg_code) {
        this.mfg_code = mfg_code;
    }

}
