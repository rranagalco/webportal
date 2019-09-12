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

public class Part_img {
	private static Logger log = Logger.getLogger(Part_img.class);

    String part_num;
    String img_type;
    String img_location;

    private static String getQueryFieldSelectionString() {
        return "part_num,img_type,img_location";
    }

    public static ArrayList<Part_img> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Part_img> al = new ArrayList<Part_img>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWEB();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
                Part_img part_img = new Part_img();

                part_img.setPart_num(JDBCUtils.getStringFromResultSet(rs, "part_num"));
                part_img.setImg_type(JDBCUtils.getStringFromResultSet(rs, "img_type"));
                part_img.setImg_location(JDBCUtils.getStringFromResultSet(rs, "img_location"));

                al.add(part_img);
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

    public static void printDAOObjects(ArrayList<Part_img> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Part_img> iterator = al.iterator(); iterator.hasNext();) {
            Part_img part_img = iterator.next();

			log.debug("part_num     : " + part_img.getPart_num());
			log.debug("img_type     : " + part_img.getImg_type());
			log.debug("img_location : " + part_img.getImg_location());
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Part_img> getPart_img(DBConnector dbConnector, String part_num, String img_type) throws PortalException {
    	String replacedPart_num = part_num.replaceAll("'", "''"); 

        String queryString = "select " + getQueryFieldSelectionString() + " from pub.part_img " +
                             " where part_num = '" + replacedPart_num + "' " +
                             "   and img_type = '" + img_type + "'";
        ArrayList<Part_img> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
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
    public String getImg_type() {
        return img_type;
    }
    public void setImg_type(String img_type) {
        this.img_type = img_type;
    }
    public String getImg_location() {
        return img_location;
    }
    public void setImg_location(String img_location) {
        this.img_location = img_location;
    }

}
