package galco.portal.wds.dao;

import galco.portal.utils.JDBCUtils;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class Oaddon {

    String order_num;
    int line_num;
    float add_amt_bal;

    private static String getQueryFieldSelectionString() {
        return "order_num,line_num,add_amt_bal";
    }

    public static ArrayList<Oaddon> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Oaddon> al = new ArrayList<Oaddon>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
                Oaddon oaddon = new Oaddon();

                oaddon.setOrder_num(JDBCUtils.getStringFromResultSet(rs, "order_num"));
                oaddon.setLine_num(new Integer(JDBCUtils.getStringFromResultSet(rs, "line_num")));
                oaddon.setAdd_amt_bal(new Float(JDBCUtils.getStringFromResultSet(rs, "add_amt_bal")));

                al.add(oaddon);
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

    public static void printDAOObjects(ArrayList<Oaddon> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Oaddon> iterator = al.iterator(); iterator.hasNext();) {
            Oaddon oaddon = iterator.next();
            System.out.println  (
                    "order_num: " + oaddon.getOrder_num() + "\n" +
                    "line_num: " + oaddon.getLine_num() + "\n" +
                    "add_amt_bal: " + oaddon.getAdd_amt_bal() + "\n"
                                );
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Oaddon> getOaddon(DBConnector dbConnector, String order_num, int line_num) throws PortalException {
        String queryString = "select " + getQueryFieldSelectionString() + " from pub.oaddon " +
                "where order_num = '" + order_num + "' " +
                "  and line_num = '" + line_num + "'";

        ArrayList<Oaddon> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
        // printDAOObjects(al);
        return al;
    }

    // -------------------------------------------------------------------------------------------------------------

    public String getOrder_num() {
        return order_num;
    }
    public void setOrder_num(String order_num) {
        this.order_num = order_num;
    }
    public int getLine_num() {
        return line_num;
    }
    public void setLine_num(int line_num) {
        this.line_num = line_num;
    }
    public float getAdd_amt_bal() {
        return add_amt_bal;
    }
    public void setAdd_amt_bal(float add_amt_bal) {
        this.add_amt_bal = add_amt_bal;
    }

}
