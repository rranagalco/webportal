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

public class Shippkg {
	private static Logger log = Logger.getLogger(Shippkg.class);

    String pkg_id_num;
    String ship_date;
    String trak_ctrlnum;
    String carrier;

    private static String getQueryFieldSelectionString() {
        return "pkg_id_num,ship_date,trak_ctrlnum,carrier";
    }

    public static ArrayList<Shippkg> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Shippkg> al = new ArrayList<Shippkg>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementSRO();
            rs = stmt.executeQuery(queryString);

			while (rs.next()) {
			    Shippkg shippkg = new Shippkg();

			    shippkg.setPkg_id_num(JDBCUtils.getStringFromResultSet(rs, "pkg_id_num"));
			    shippkg.setShip_date(JDBCUtils.getStringFromResultSet(rs, "ship_date"));
			    shippkg.setTrak_ctrlnum(JDBCUtils.getStringFromResultSet(rs, "trak_ctrlnum"));
			    shippkg.setCarrier(JDBCUtils.getStringFromResultSet(rs, "carrier"));

			    al.add(shippkg);
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

    public static void printDAOObjects(ArrayList<Shippkg> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Shippkg> iterator = al.iterator(); iterator.hasNext();) {
            Shippkg shippkg = iterator.next();

			log.debug("pkg_id_num   : " + shippkg.getPkg_id_num());
			log.debug("ship_date    : " + shippkg.getShip_date());
			log.debug("trak_ctrlnum : " + shippkg.getTrak_ctrlnum());
			log.debug("carrier      : " + shippkg.getCarrier());
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Shippkg> getShippkg(DBConnector dbConnector, String pkg_id_num) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.shippkg " +
							 "where pkg_id_num = '" + pkg_id_num + "'";

		ArrayList<Shippkg> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

    // -------------------------------------------------------------------------------------------------------------

    public String getPkg_id_num() {
        return pkg_id_num;
    }
    public void setPkg_id_num(String pkg_id_num) {
        this.pkg_id_num = pkg_id_num;
    }
    public String getShip_date() {
        return ship_date;
    }
    public void setShip_date(String ship_date) {
        this.ship_date = ship_date;
    }
    public String getTrak_ctrlnum() {
        return trak_ctrlnum;
    }
    public void setTrak_ctrlnum(String trak_ctrlnum) {
        this.trak_ctrlnum = trak_ctrlnum;
    }

    public String getCarrier() {
        return carrier;
    }
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

}
