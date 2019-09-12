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

public class Codes_s {
	private static Logger log = Logger.getLogger(Codes_s.class);

    String code_type;
    String description;
    String misc_alpha4;
    String valid_code;
    boolean misc_log2;

    private static String getQueryFieldSelectionString() {
        return "code_type,description,misc_alpha4,valid_code,misc_log2";
    }

    public static ArrayList<Codes_s> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Codes_s> al = new ArrayList<Codes_s>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementSRO();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
                Codes_s codes_s = new Codes_s();

                codes_s.setCode_type(JDBCUtils.getStringFromResultSet(rs, "code_type"));
                codes_s.setDescription(JDBCUtils.getStringFromResultSet(rs, "description"));
                codes_s.setMisc_alpha4(JDBCUtils.getStringFromResultSet(rs, "misc_alpha4"));
                codes_s.setValid_code(JDBCUtils.getStringFromResultSet(rs, "valid_code"));
                codes_s.setMisc_log2(rs.getBoolean("misc_log2"));

                al.add(codes_s);
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

    public static void printDAOObjects(ArrayList<Codes_s> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Codes_s> iterator = al.iterator(); iterator.hasNext();) {
            Codes_s codes_s = iterator.next();

			log.debug("code_type  : " + codes_s.getCode_type());
			log.debug("description: " + codes_s.getDescription());
			log.debug("misc_alpha4: " + codes_s.getMisc_alpha4());
			log.debug("valid_code : " + codes_s.getValid_code());
			log.debug("misc_log2  : " + codes_s.getMisc_log2());
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Codes_s> getCodes_s_CV(DBConnector dbConnector, String code_type, String valid_code) throws PortalException {
        String queryString = "select " + getQueryFieldSelectionString() + " from pub.codes_s " +
                             "where code_type = '" + code_type + "' and valid_code = '" + valid_code + "'";

        ArrayList<Codes_s> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
        // printDAOObjects(al);
        return al;
    }

    public static ArrayList<Codes_s> getCodes_s_CM(DBConnector dbConnector, String code_type, String misc_alpha4) throws PortalException {
        String queryString = "select " + getQueryFieldSelectionString() + " from pub.codes_s " +
                             "where code_type = '" + code_type + "' and misc_alpha4 = '" + misc_alpha4 + "'";

        ArrayList<Codes_s> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
        // printDAOObjects(al);
        return al;
    }

    // -------------------------------------------------------------------------------------------------------------

    public String getCode_type() {
        return code_type;
    }
    public void setCode_type(String code_type) {
        this.code_type = code_type;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getMisc_alpha4() {
        return misc_alpha4;
    }
    public void setMisc_alpha4(String misc_alpha4) {
        this.misc_alpha4 = misc_alpha4;
    }
    public String getValid_code() {
        return valid_code;
    }
    public void setValid_code(String valid_code) {
        this.valid_code = valid_code;
    }
    public boolean getMisc_log2() {
        return misc_log2;
    }
    public void setMisc_log2(boolean misc_log2) {
        this.misc_log2 = misc_log2;
    }

}
