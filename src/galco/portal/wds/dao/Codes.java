package galco.portal.wds.dao;

import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class Codes {
	private static Logger log = Logger.getLogger(Codes.class);

    String code_type;
    String valid_code;
    String misc_alpha4;
    boolean misc_log2;
    String description;

    private static String getQueryFieldSelectionString() {
        return "code_type,valid_code,misc_alpha4,misc_log2,SUBSTR(description,1,80) AS description_sub";
    }

    public static ArrayList<Codes> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Codes> al = new ArrayList<Codes>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
                Codes codes = new Codes();

                codes.setCode_type(JDBCUtils.getStringFromResultSet(rs, "code_type"));
                codes.setValid_code(JDBCUtils.getStringFromResultSet(rs, "valid_code"));
                codes.setMisc_alpha4(JDBCUtils.getStringFromResultSet(rs, "misc_alpha4"));
                codes.setMisc_log2(rs.getBoolean("misc_log2"));
                codes.setDescription(JDBCUtils.getStringFromResultSet(rs, "description_sub"));

                al.add(codes);
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

    public static void printDAOObjects(ArrayList<Codes> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Codes> iterator = al.iterator(); iterator.hasNext();) {
            Codes codes = iterator.next();

			log.debug("code_type  : " + codes.getCode_type());
			log.debug("valid_code : " + codes.getValid_code());
			log.debug("misc_alpha4 : " + codes.getMisc_alpha4());
			log.debug("misc_log2  : " + codes.getMisc_log2());
			log.debug("description: " + codes.getDescription());
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Codes> getCodes(DBConnector dbConnector, String code_type, String valid_code, boolean misc_log2) throws PortalException {
        String queryString = "select " + getQueryFieldSelectionString() + " from pub.codes " +
                			 " where code_type = '" + code_type + "' " +
                             "   and valid_code = '" + valid_code + "' " +
                             "   and misc_log2 = '" + (misc_log2?"1":"0") + "'";
        ArrayList<Codes> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
        // printDAOObjects(al);
        return al;
    }

    public static ArrayList<Codes> getCodes_VC_NotEq(DBConnector dbConnector, String code_type, String valid_code, boolean misc_log2) throws PortalException {
        String queryString = "select " + getQueryFieldSelectionString() + " from pub.codes " +
                			 " where code_type = '" + code_type + "' " +
                             "   and valid_code <> '" + valid_code + "' " +
                             "   and misc_log2 = '" + (misc_log2?"1":"0") + "'";
        ArrayList<Codes> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
        // printDAOObjects(al);
        return al;
    }

    public static ArrayList<Codes> getCodesGiven_code_type_And_valid_vode(DBConnector dbConnector, String code_type, String valid_code) throws PortalException {
        String queryString = "select " + getQueryFieldSelectionString() + " from pub.codes " +
                			 " where code_type = '" + code_type + "'" + 
                             "   and valid_code = '" + valid_code + "'";
        ArrayList<Codes> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
        // printDAOObjects(al);
        return al;
    }
    
    // -------------------------------------------------------------------------------------------------------------

    private static int getEmailLen(DBConnector dbConnector, String queryString) throws PortalException {
        int length = 0;

        Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
            	length = new Integer(JDBCUtils.getStringFromResultSet(rs, "description_len"));
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

        return length;
    }

    private static String getEmailO(DBConnector dbConnector, String queryString) throws PortalException {
        String email = "";

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
            	email = JDBCUtils.getStringFromResultSet(rs, "description_email");
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

        return email;
    }

    private static String getEmail(DBConnector dbConnector, String salesRepCode, int salesRepEmailLength) throws PortalException {
        String email = "";

		Statement stmt = null;
		ResultSet rs = null;

        try {
        	String queryString = "select substr(description,1," + salesRepEmailLength + ") as description_email from pub.codes " +
 			 		  " where code_type = 'EMAILADDRESS'" + 
 			 		  "   and valid_code = '" + salesRepCode + "'";
            
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
            	email = JDBCUtils.getStringFromResultSet(rs, "description_email");
            }

            rs.close();
            rs = null;
            stmt.close();
            stmt = null;
        } catch (SQLException e) {
        	log.error("SALES_REP_EMAIL_LENGTH, " + SALES_REP_EMAIL_LENGTH + ", is too small.");
			Utils.sendMail("sati@galco.com", "WebPortal@galco.com", "SALES_REP_EMAIL_LENGTH, " + SALES_REP_EMAIL_LENGTH + ", is too small.", "SALES_REP_EMAIL_LENGTH, " + SALES_REP_EMAIL_LENGTH + ", is too small.");
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

            return "9999999999";
        }

        return email;
    }
    
    
    static int SALES_REP_EMAIL_LENGTH = 120;
    public static String getEmailOfSalesRep(DBConnector dbConnector, String salesRepCode) throws PortalException {
        while (true) {
        	String email = getEmail(dbConnector, salesRepCode, SALES_REP_EMAIL_LENGTH);
        	if ((email != null) && (email.compareTo("9999999999") == 0)) {
        		SALES_REP_EMAIL_LENGTH += 20;
        	} else {
        		return (email == null)?"":email.trim();
        	}
        }
    }
    
    // -------------------------------------------------------------------------------------------------------------

    public String getCode_type() {
        return code_type;
    }
    public void setCode_type(String code_type) {
        this.code_type = code_type;
    }
    public String getValid_code() {
        return valid_code;
    }
    public void setValid_code(String valid_code) {
        this.valid_code = valid_code;
    }
    public String getMisc_alpha4() {
    	return misc_alpha4;
    }
    public void setMisc_alpha4(String misc_alpha4) {
        this.misc_alpha4 = misc_alpha4;
    }
    public boolean getMisc_log2() {
        return misc_log2;
    }
    public void setMisc_log2(boolean misc_log2) {
        this.misc_log2 = misc_log2;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}
