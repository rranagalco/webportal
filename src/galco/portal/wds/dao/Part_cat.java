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

public class Part_cat {
	private static Logger log = Logger.getLogger(Part_cat.class);

    String category;
    String description;
    String subcategory;
    boolean misc_log2;

    private static String getQueryFieldSelectionString() {
        return "category,description,subcategory,misc_log2";
    }

    public static ArrayList<Part_cat> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Part_cat> al = new ArrayList<Part_cat>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
                Part_cat part_cat = new Part_cat();

                part_cat.setCategory(JDBCUtils.getStringFromResultSet(rs, "category"));
                part_cat.setDescription(JDBCUtils.getStringFromResultSet(rs, "description"));
                part_cat.setSubcategory(JDBCUtils.getStringFromResultSet(rs, "subcategory"));
                part_cat.setMisc_log2(rs.getBoolean("misc_log2"));

                al.add(part_cat);
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

    public static void printDAOObjects(ArrayList<Part_cat> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Part_cat> iterator = al.iterator(); iterator.hasNext();) {
            Part_cat part_cat = iterator.next();

			log.debug("category    : " + part_cat.getCategory());
			log.debug("description : " + part_cat.getDescription());
			log.debug("subcategory : " + part_cat.getSubcategory());
			log.debug("misc_log2   : " + part_cat.getMisc_log2());
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Part_cat> getPart_cat(DBConnector dbConnector, String subcategory, boolean misc_log2) throws PortalException {
        String queryString = "select " + getQueryFieldSelectionString() + " from pub.part_cat " +
                             "where subcategory = '" + subcategory + "' and misc_log2 = '" + (misc_log2?"1":"0") + "'";

        ArrayList<Part_cat> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
        // printDAOObjects(al);
        return al;
    }

    public static ArrayList<Part_cat> getPart_cat(DBConnector dbConnector, String category, String subcategory) throws PortalException {
        String queryString = "select " + getQueryFieldSelectionString() + " from pub.part_cat " +
                             "where category = '" + category + "' and subcategory = '" + subcategory + "'";

        ArrayList<Part_cat> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
        // printDAOObjects(al);
        return al;
    }

    // -------------------------------------------------------------------------------------------------------------

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getSubcategory() {
        return subcategory;
    }
    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }
    public boolean getMisc_log2() {
        return misc_log2;
    }
    public void setMisc_log2(boolean misc_log2) {
        this.misc_log2 = misc_log2;
    }

}
