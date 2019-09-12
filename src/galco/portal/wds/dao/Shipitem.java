
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

public class Shipitem {
	private static Logger log = Logger.getLogger(Shipitem.class);

    String source;
    String order_num;
    int ord_line_num;
    String pkg_id_num;
    String inv_num;

    private static String getQueryFieldSelectionString() {
        return "source,order_num,ord_line_num,pkg_id_num,inv_num";
    }

    public static ArrayList<Shipitem> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Shipitem> al = new ArrayList<Shipitem>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementSRO();
            rs = stmt.executeQuery(queryString);

			while (rs.next()) {
			    Shipitem shipitem = new Shipitem();

			    shipitem.setSource(JDBCUtils.getStringFromResultSet(rs, "source"));
			    shipitem.setOrder_num(JDBCUtils.getStringFromResultSet(rs, "order_num"));
			    shipitem.setOrd_line_num(new Integer(JDBCUtils.getStringFromResultSet(rs, "ord_line_num")));
			    shipitem.setPkg_id_num(JDBCUtils.getStringFromResultSet(rs, "pkg_id_num"));
			    shipitem.setInv_num(JDBCUtils.getStringFromResultSet(rs, "inv_num"));

			    al.add(shipitem);
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

    public static void printDAOObjects(ArrayList<Shipitem> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Shipitem> iterator = al.iterator(); iterator.hasNext();) {
            Shipitem shipitem = iterator.next();

			log.debug("source       : " + shipitem.getSource());
			log.debug("order_num    : " + shipitem.getOrder_num());
			log.debug("ord_line_num : " + shipitem.getOrd_line_num());
			log.debug("pkg_id_num   : " + shipitem.getPkg_id_num());
			log.debug("inv_num   : " + shipitem.getInv_num());
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Shipitem> getShipitem(DBConnector dbConnector, String order_num, int ord_line_num) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.shipitem " +
							 "where source = 'order' and order_num = '" + order_num + "' and ord_line_num = '" + ord_line_num + "'";

		ArrayList<Shipitem> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

    // -------------------------------------------------------------------------------------------------------------

    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getOrder_num() {
        return order_num;
    }
    public void setOrder_num(String order_num) {
        this.order_num = order_num;
    }
    public int getOrd_line_num() {
        return ord_line_num;
    }
    public void setOrd_line_num(int ord_line_num) {
        this.ord_line_num = ord_line_num;
    }
    public String getPkg_id_num() {
        return pkg_id_num;
    }
    public void setPkg_id_num(String pkg_id_num) {
        this.pkg_id_num = pkg_id_num;
    }

    public String getInv_num() {
        return inv_num;
    }
    public void setInv_num(String inv_num) {
        this.inv_num = inv_num;
    }
}
