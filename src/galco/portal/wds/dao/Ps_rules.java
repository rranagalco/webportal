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

public class Ps_rules {
	private static Logger log = Logger.getLogger(Ps_rules.class);

    String family_code;
    String subfamily_code;
    String parm_status;
    String image_type;
    int seq_num;

    private static String getQueryFieldSelectionString() {
        return "family_code,subfamily_code,parm_status,image_type,seq_num";
    }

    public static ArrayList<Ps_rules> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Ps_rules> al = new ArrayList<Ps_rules>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
                Ps_rules ps_rules = new Ps_rules();

                ps_rules.setFamily_code(JDBCUtils.getStringFromResultSet(rs, "family_code"));
                ps_rules.setSubfamily_code(JDBCUtils.getStringFromResultSet(rs, "subfamily_code"));
                ps_rules.setParm_status(JDBCUtils.getStringFromResultSet(rs, "parm_status"));
                ps_rules.setImage_type(JDBCUtils.getStringFromResultSet(rs, "image_type"));
                ps_rules.setSeq_num(rs.getInt("seq_num"));

                al.add(ps_rules);
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

    public static void printDAOObjects(ArrayList<Ps_rules> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Ps_rules> iterator = al.iterator(); iterator.hasNext();) {
            Ps_rules ps_rules = iterator.next();

			log.debug("family_code    : " + ps_rules.getFamily_code());
			log.debug("subfamily_code : " + ps_rules.getSubfamily_code());
			log.debug("parm_status    : " + ps_rules.getParm_status());
			log.debug("image_type     : " + ps_rules.getImage_type());
			log.debug("seq_num        : " + ps_rules.getSeq_num());
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Ps_rules> getPs_rules(DBConnector dbConnector, String family_code,
    											  String subfamily_code,
    											  String parm_status,
    											  String image_type) throws PortalException {
        String queryString = "select " + getQueryFieldSelectionString() + " from pub.ps_rules " +
        					 " where family_code = '" + family_code + "' " +
        					 "   and subfamily_code = '" + subfamily_code + "' " +
        					 "   and parm_status = '" + parm_status + "' " +
        					 "   and image_type = '" + image_type + "'";
        ArrayList<Ps_rules> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
        // printDAOObjects(al);
        return al;
    }

    // -------------------------------------------------------------------------------------------------------------

    public String getFamily_code() {
        return family_code;
    }
    public void setFamily_code(String family_code) {
        this.family_code = family_code;
    }
    public String getSubfamily_code() {
        return subfamily_code;
    }
    public void setSubfamily_code(String subfamily_code) {
        this.subfamily_code = subfamily_code;
    }
    public String getParm_status() {
        return parm_status;
    }
    public void setParm_status(String parm_status) {
        this.parm_status = parm_status;
    }
    public String getImage_type() {
        return image_type;
    }
    public void setImage_type(String image_type) {
        this.image_type = image_type;
    }
    public int getSeq_num() {
        return seq_num;
    }
    public void setSeq_num(int seq_num) {
        this.seq_num = seq_num;
    }

}
