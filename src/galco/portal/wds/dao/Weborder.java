package galco.portal.wds.dao;

import galco.portal.utils.JDBCUtils;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class Weborder {

    String pid;

    private static String getQueryFieldSelectionString() {
        return "pid";
    }
    
    public static ArrayList<Weborder> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Weborder> al = new ArrayList<Weborder>();
        
		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
                Weborder weborder = new Weborder();
                
                weborder.setPid(JDBCUtils.getStringFromResultSet(rs, "pid"));

                al.add(weborder);
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

    public static void printDAOObjects(ArrayList<Weborder> al) {
        if (al == null) {
            return;
        }
        
        for (Iterator<Weborder> iterator = al.iterator(); iterator.hasNext();) {
            Weborder weborder = iterator.next();
            System.out.println  (
                    "pid: " + weborder.getPid() + "\n"
                                );
        }
    }
       
    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Weborder> getWeborder(DBConnector dbConnector, String pid) throws PortalException {
        String queryString = "select " + getQueryFieldSelectionString() + " from pub.weborder " +
                             "where pid = '" + pid + "'";

        ArrayList<Weborder> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
        // printDAOObjects(al);
        return al;
    }

    // -------------------------------------------------------------------------------------------------------------

    public static void deleteWeborder(DBConnector dbConnector, String pid) throws PortalException {
        String deleteString = "delete from pub.weborder " +
                               "where pid = '" + pid + "'";
        JDBCUtils.executeUpdateWDS(dbConnector, deleteString);        
    }

    // -------------------------------------------------------------------------------------------------------------

    public String getPid() {
        return pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }

}
