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

public class Partloc {
	private static Logger log = Logger.getLogger(Partloc.class);

    String part_num;
    String location;
	int qty_onhand;
    int qty_commit;
    int qty_backord;

    private static String getQueryFieldSelectionString() {
        return "part_num,location,qty_onhand,qty_commit,qty_backord";
    }

    public static ArrayList<Partloc> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Partloc> al = new ArrayList<Partloc>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

			while (rs.next()) {
			    Partloc partloc = new Partloc();

			    partloc.setPart_num(JDBCUtils.getStringFromResultSet(rs, "part_num"));
			    partloc.setLocation(JDBCUtils.getStringFromResultSet(rs, "location"));
			    partloc.setQty_onhand(new Integer(JDBCUtils.getStringFromResultSet(rs, "qty_onhand")));
			    partloc.setQty_commit(new Integer(JDBCUtils.getStringFromResultSet(rs, "qty_commit")));
			    partloc.setQty_backord(new Integer(JDBCUtils.getStringFromResultSet(rs, "qty_backord")));

			    al.add(partloc);
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

    public static void printDAOObjects(ArrayList<Partloc> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Partloc> iterator = al.iterator(); iterator.hasNext();) {
            Partloc partloc = iterator.next();

			log.debug("part_num    : " + partloc.getPart_num());
			log.debug("location    : " + partloc.getLocation());
			log.debug("qty_onhand  : " + partloc.getQty_onhand());
			log.debug("qty_commit  : " + partloc.getQty_commit());
			log.debug("qty_backord : " + partloc.getQty_backord());
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Partloc> getPartloc(DBConnector dbConnector, String part_num, String ship_loc) throws PortalException {
    	String replacedPart_num = part_num.replaceAll("'", "''"); 
    	// String replacedPart_num = part_num; 
    	
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.partloc " +
							 "where part_num = '" + replacedPart_num + "' and location = '" + ship_loc + "'";
		// log.debug("Partloc queryString: " + queryString);
		
		ArrayList<Partloc> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

    // -------------------------------------------------------------------------------------------------------------

    public String getPart_num() {
        return part_num;
    }
    public void setPart_num(String part_num) {
        this.part_num = part_num;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public int getQty_onhand() {
        return qty_onhand;
    }
    public void setQty_onhand(int qty_onhand) {
        this.qty_onhand = qty_onhand;
    }
    public int getQty_commit() {
        return qty_commit;
    }
    public void setQty_commit(int qty_commit) {
        this.qty_commit = qty_commit;
    }
    public int getQty_backord() {
        return qty_backord;
    }
    public void setQty_backord(int qty_backord) {
        this.qty_backord = qty_backord;
    }

}
