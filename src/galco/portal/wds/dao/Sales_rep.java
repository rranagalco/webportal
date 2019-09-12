package galco.portal.wds.dao;

import galco.portal.utils.JDBCUtils;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

public class Sales_rep {

    String sales_rep;
    String sr_name_f;
    String sr_name_l;

    private static String getQueryFieldSelectionString() {
        return "sales_rep,sr_name_f,sr_name_l";
    }

    public static ArrayList<Sales_rep> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Sales_rep> al = new ArrayList<Sales_rep>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
                Sales_rep sales_rep = new Sales_rep();

                sales_rep.setSales_rep(JDBCUtils.getStringFromResultSet(rs, "sales_rep"));
                sales_rep.setSr_name_f(JDBCUtils.getStringFromResultSet(rs, "sr_name_f"));
                sales_rep.setSr_name_l(JDBCUtils.getStringFromResultSet(rs, "sr_name_l"));

                al.add(sales_rep);
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

    public static void printDAOObjects(ArrayList<Sales_rep> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Sales_rep> iterator = al.iterator(); iterator.hasNext();) {
            Sales_rep sales_rep = iterator.next();
            System.out.println  (
                    "sales_rep: " + sales_rep.getSales_rep() + "\n" +
                    "sr_name_f: " + sales_rep.getSr_name_f() + "\n" +
                    "sr_name_l: " + sales_rep.getSr_name_l() + "\n"
                                );
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Sales_rep> getSales_rep(DBConnector dbConnector, String sales_rep) throws PortalException {
        String queryString = "select " + getQueryFieldSelectionString() + " from pub.sales_rep " +
                             "where sales_rep = '" + sales_rep + "'";

        ArrayList<Sales_rep> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
        // printDAOObjects(al);
        return al;
    }

    // -------------------------------------------------------------------------------------------------------------

    public static String getSales_repName(DBConnector dbConnector, String sales_rep) throws PortalException {
		if (StringUtils.isBlank(sales_rep) == true) {
			return null;
		}

		ArrayList<Sales_rep> sales_repAL = Sales_rep.getSales_rep(dbConnector, sales_rep);
		if ((sales_repAL != null) && (sales_repAL.size() > 0)) {
			return sales_repAL.get(0).getSr_name_f() + " " + sales_repAL.get(0).getSr_name_l();
		} else {
			return null;
		}
    }

    // -------------------------------------------------------------------------------------------------------------

    public String getSales_rep() {
        return sales_rep;
    }
    public void setSales_rep(String sales_rep) {
        this.sales_rep = sales_rep;
    }
    public String getSr_name_f() {
        return sr_name_f;
    }
    public void setSr_name_f(String sr_name_f) {
        this.sr_name_f = sr_name_f;
    }
    public String getSr_name_l() {
        return sr_name_l;
    }
    public void setSr_name_l(String sr_name_l) {
        this.sr_name_l = sr_name_l;
    }

}
