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

public class Ps_parm_data {
	private static Logger log = Logger.getLogger(Ps_parm_data.class);

    String part_num;
    int seq_num;
    String parm_value;

    private static String getQueryFieldSelectionString() {
        return "part_num,seq_num,parm_value";
    }

    public static ArrayList<Ps_parm_data> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Ps_parm_data> al = new ArrayList<Ps_parm_data>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
                Ps_parm_data ps_parm_data = new Ps_parm_data();

                ps_parm_data.setPart_num(JDBCUtils.getStringFromResultSet(rs, "part_num"));
                ps_parm_data.setSeq_num(rs.getInt("seq_num"));
                ps_parm_data.setParm_value(JDBCUtils.getStringFromResultSet(rs, "parm_value"));

                al.add(ps_parm_data);
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

    public static void printDAOObjects(ArrayList<Ps_parm_data> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Ps_parm_data> iterator = al.iterator(); iterator.hasNext();) {
            Ps_parm_data ps_parm_data = iterator.next();

			log.debug("part_num   : " + ps_parm_data.getPart_num());
			log.debug("seq_num    : " + ps_parm_data.getSeq_num());
			log.debug("parm_value : " + ps_parm_data.getParm_value());
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Ps_parm_data> getPs_parm_data(DBConnector dbConnector, String part_num, int seq_num) throws PortalException {
    	String replacedPart_num = part_num.replaceAll("'", "''"); 

        String queryString = "select " + getQueryFieldSelectionString() + " from pub.ps_parm_data " +
                             " where part_num = '" + replacedPart_num + "' " +
                             "   and seq_num = " + seq_num;

        ArrayList<Ps_parm_data> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
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
    public int getSeq_num() {
        return seq_num;
    }
    public void setSeq_num(int seq_num) {
        this.seq_num = seq_num;
    }
    public String getParm_value() {
        return parm_value;
    }
    public void setParm_value(String parm_value) {
        this.parm_value = parm_value;
    }

}
