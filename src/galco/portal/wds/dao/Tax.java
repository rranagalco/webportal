package galco.portal.wds.dao;

import galco.portal.utils.JDBCUtils;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class Tax {

    String tax_auth;
    String tax_desc;

    private static String getQueryFieldSelectionString() {
        return "tax_auth,tax_desc";
    }

    public static ArrayList<Tax> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
		
		ArrayList<Tax> al = new ArrayList<Tax>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
                Tax tax = new Tax();
                
                tax.setTax_auth(JDBCUtils.getStringFromResultSet(rs, "tax_auth"));
                tax.setTax_desc(JDBCUtils.getStringFromResultSet(rs, "tax_desc"));

                al.add(tax);
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

    public static void printDAOObjects(ArrayList<Tax> al) {
        if (al == null) {
            return;
        }
        
        for (Iterator<Tax> iterator = al.iterator(); iterator.hasNext();) {
            Tax tax = iterator.next();
            System.out.println  (
                    "tax_auth: " + tax.getTax_auth() + "\n" + 
                    "tax_desc: " + tax.getTax_desc() + "\n"
                                );
        }
    }
       
    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Tax> getTax(DBConnector dbConnector, String tax_auth) throws PortalException {
        String queryString = "select " + getQueryFieldSelectionString() + " from pub.tax " +
                             "where tax_auth = '" + tax_auth + "'";

        ArrayList<Tax> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
        // printDAOObjects(al);
        return al;
    }

    // -------------------------------------------------------------------------------------------------------------

    public String getTax_auth() {
        return tax_auth;
    }
    public void setTax_auth(String tax_auth) {
        this.tax_auth = tax_auth;
    }
    public String getTax_desc() {
        return tax_desc;
    }
    public void setTax_desc(String tax_desc) {
        this.tax_desc = tax_desc;
    }

}
