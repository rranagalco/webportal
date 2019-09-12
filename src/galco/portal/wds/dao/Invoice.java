package galco.portal.wds.dao;

import galco.portal.utils.JDBCUtils;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class Invoice {

    String order_num;
    String inv_num;
    String inv_type;
    String ref_inv_num;
    String cust_num;

    private static String getQueryFieldSelectionString() {
        return "order_num,inv_num,inv_type,ref_inv_num,cust_num";
    }

    public static ArrayList<Invoice> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Invoice> al = new ArrayList<Invoice>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
                Invoice invoice = new Invoice();

                invoice.setOrder_num(JDBCUtils.getStringFromResultSet(rs, "order_num"));
                invoice.setInv_num(JDBCUtils.getStringFromResultSet(rs, "inv_num"));
                invoice.setInv_type(JDBCUtils.getStringFromResultSet(rs, "inv_type"));
                invoice.setRef_inv_num(JDBCUtils.getStringFromResultSet(rs, "ref_inv_num"));
                invoice.setCust_num(JDBCUtils.getStringFromResultSet(rs, "cust_num"));

                al.add(invoice);
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

    public static void printDAOObjects(ArrayList<Invoice> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Invoice> iterator = al.iterator(); iterator.hasNext();) {
            Invoice invoice = iterator.next();
            System.out.println  (
                    "order_num: " + invoice.getOrder_num() + "\n" +
                    "inv_num: " + invoice.getInv_num() + "\n" +
                    "inv_type: " + invoice.getInv_type() + "\n" +
                    "ref_inv_num: " + invoice.getRef_inv_num() + "\n" +
                    "cust_num: " + invoice.getCust_num() + "\n"
                                );
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Invoice> getInvoice(DBConnector dbConnector, String inv_num) throws PortalException {
        String queryString = "select " + getQueryFieldSelectionString() + " from pub.invoice " +
                             "where inv_num = '" + inv_num + "'";

        ArrayList<Invoice> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
        // printDAOObjects(al);
        return al;
    }

    // -------------------------------------------------------------------------------------------------------------

    public static int getInvoiceCount(DBConnector dbConnector, String order_num) throws PortalException {
		Statement stmt = null;
		ResultSet rs = null;

    	try {
	        String queryString = "select count(*) from pub.invoice " +
	                             "where order_num = '" + order_num + "'";

			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

	        rs.next();
	        int count = rs.getInt(1);

            rs.close();
            rs = null;
            stmt.close();
            stmt = null;

            return count;
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
    }

    // -------------------------------------------------------------------------------------------------------------

    public String getOrder_num() {
        return order_num;
    }
    public void setOrder_num(String order_num) {
        this.order_num = order_num;
    }
    public String getInv_num() {
        return inv_num;
    }
    public void setInv_num(String inv_num) {
        this.inv_num = inv_num;
    }
    public String getInv_type() {
        return inv_type;
    }
    public void setInv_type(String inv_type) {
        this.inv_type = inv_type;
    }
    public String getRef_inv_num() {
        return ref_inv_num;
    }
    public void setRef_inv_num(String ref_inv_num) {
        this.ref_inv_num = ref_inv_num;
    }
    public String getCust_num() {
        return cust_num;
    }
    public void setCust_num(String cust_num) {
        this.cust_num = cust_num;
    }

}
