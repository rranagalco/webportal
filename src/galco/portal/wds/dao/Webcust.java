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

public class Webcust {
	private static Logger log = Logger.getLogger(Webcust.class);

    String cust_num;
    String cont_no;
    String password;
    boolean pwd_verified;
	private boolean cod_ok;
	private boolean open_account;
	private boolean view_price;
	private String last_pid_date;

    public static String getQueryFieldSelectionString() {
        return "cust_num, cont_no, password, pwd_verified, cod_ok, open_account, view_price, last_pid_date";
    }

    public static ArrayList<Webcust> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Webcust> al = new ArrayList<Webcust>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWEB();
            rs = stmt.executeQuery(queryString);

	        while (rs.next()) {
	            Webcust webcust = new Webcust();

	            webcust.setCust_num(JDBCUtils.getStringFromResultSet(rs, "cust_num"));
	            webcust.setCont_no(JDBCUtils.getStringFromResultSet(rs, "cont_no"));
	            webcust.setPassword(JDBCUtils.getStringFromResultSet(rs, "password"));
	            webcust.setPwd_verified(rs.getBoolean("pwd_verified"));
	            webcust.setCod_ok(rs.getBoolean("cod_ok"));
	            webcust.setOpen_account(rs.getBoolean("open_account"));
	            webcust.setView_price(rs.getBoolean("view_price"));
	            webcust.setLast_pid_date(JDBCUtils.getStringFromResultSet(rs, "last_pid_date"));
	            // webcust.setLast_pid_date(rs.getDate("last_pid_date").toString());

	            al.add(webcust);
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

    public static void printDAOObjects(ArrayList<Webcust> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Webcust> iterator = al.iterator(); iterator.hasNext();) {
            Webcust webcust = iterator.next();

			log.debug("cust_num       : " + webcust.getCust_num());
			log.debug("cont_no        : " + webcust.getCont_no());
			log.debug("password       : " + webcust.getPassword());
			log.debug("pwd_verified   : " + webcust.isPwd_verified());
			log.debug("cod_ok         : " + webcust.isCod_ok());
			log.debug("open_account   : " + webcust.isOpen_account());
			log.debug("view_price     : " + webcust.isView_price());
			log.debug("last_pid_date  : " + webcust.getLast_pid_date());
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    /*
    public static ResultSet getRSForAllWebcusts(DBConnector dbConnector) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.webcust order by last_pid_date";
		// String queryString = "select " + getQueryFieldSelectionString() + " from pub.webcust order";
		return JDBCUtils.executeQueryWEB(dbConnector, queryString);
	}
	*/

    public static ArrayList<Webcust> getWebcust(DBConnector dbConnector, String cust_num, String cont_no) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.webcust " +
							 "where cust_num = '" + cust_num + "' and cont_no = '" + cont_no + "'";
		ArrayList<Webcust> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

	public void modifyPwd_verified(DBConnector dbConnector) throws PortalException {
	    JDBCUtils.executeUpdateWEB(dbConnector, "UPDATE PUB.webcust SET pwd_verified = '" + (pwd_verified?"1":"0") + "' WHERE cust_num = '" + cust_num + "' and cont_no = '" + cont_no + "'");
	}

    // -------------------------------------------------------------------------------------------------------------

    public String getCust_num() {
        return cust_num;
    }
    public void setCust_num(String cust_num) {
        this.cust_num = cust_num;
    }
    public String getCont_no() {
        return cont_no;
    }
    public void setCont_no(String cont_no) {
        this.cont_no = cont_no;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public boolean isPwd_verified() {
        return pwd_verified;
    }
    public void setPwd_verified(boolean pwd_verified) {
        this.pwd_verified = pwd_verified;
    }
	public boolean isCod_ok() {
		return cod_ok;
	}

	public void setCod_ok(boolean cod_ok) {
		this.cod_ok = cod_ok;
	}

	public boolean isOpen_account() {
		return open_account;
	}

	public void setOpen_account(boolean open_account) {
		this.open_account = open_account;
	}

	public boolean isView_price() {
		return view_price;
	}

	public void setView_price(boolean view_price) {
		this.view_price = view_price;
	}

    public String getLast_pid_date() {
        return last_pid_date;
    }
    public void setLast_pid_date(String last_pid_date) {
        this.last_pid_date = last_pid_date;
    }
}

