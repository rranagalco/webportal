package galco.portal.wds.dao;

import galco.portal.utils.JDBCUtils;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class Email_profile {
	private static Logger log = Logger.getLogger(Email_profile.class);

	String address;
    String tags;
    boolean active;
    String audit_date;
    String audit_time;
    String audit_userid;


    public Email_profile(String address, String tags, boolean active,
    					String audit_date, String audit_time, String audit_userid) {
		this.address = address;
		this.tags = tags;
		this.active = active;
		this.audit_date = audit_date;
		this.audit_time = audit_time;
		this.audit_userid = audit_userid;
	}

    public Email_profile(String address, String tags, boolean active) {
		this.address = address;
		this.tags = tags;
		this.active = active;
	}

    public Email_profile() {
	}

	private static String getQueryFieldSelectionString() {
        return "address,tags,active,audit_date,audit_time,audit_userid";
    }
    
    public static ArrayList<Email_profile> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Email_profile> al = new ArrayList<Email_profile>();

        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = dbConnector.getStatementWEB();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
                Email_profile email_profile = new Email_profile();

                email_profile.setAddress(JDBCUtils.getStringFromResultSet(rs, "address"));
                email_profile.setTags(JDBCUtils.getStringFromResultSet(rs, "tags"));
                email_profile.setActive(rs.getBoolean("active"));
                email_profile.setAudit_date(JDBCUtils.getStringFromResultSet(rs, "audit_date"));
                email_profile.setAudit_time(JDBCUtils.getStringFromResultSet(rs, "audit_time"));
                email_profile.setAudit_userid(JDBCUtils.getStringFromResultSet(rs, "audit_userid"));

                al.add(email_profile);
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
    public static void printDAOObjects(ArrayList<Email_profile> al) {
        if (al == null) {
            return;
        }
        
        for (Iterator<Email_profile> iterator = al.iterator(); iterator.hasNext();) {
            Email_profile email_profile = iterator.next();
            System.out.println  (
                    "address: " + email_profile.getAddress() + "\n" + 
                    "tags: " + email_profile.getTags() + "\n" + 
                    "active: " + email_profile.getActive() + "\n" + 
                    "audit_date: " + email_profile.getAudit_date() + "\n" + 
                    "audit_time: " + email_profile.getAudit_time() + "\n" + 
                    "audit_userid: " + email_profile.getAudit_userid() + "\n"
                                );
        }
    }
       
    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Email_profile> getEmail_profile(DBConnector dbConnector, String address) throws PortalException {
        String queryString = "select " + getQueryFieldSelectionString() + " from pub.email_profile " +
                             "where address = '" + address + "'";

        ArrayList<Email_profile> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
        // printDAOObjects(al);
        return al;
    }

    // -------------------------------------------------------------------------------------------------------------

	public void update(DBConnector dbConnector) throws PortalException {
   		audit_date = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
   		audit_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		audit_userid = "Portal";
		
		String updateQueryString = 
				"update pub.email_profile " +
				"set " +
				" tags = '" + tags + "', " +
				" active = '" + (active?"1":"0") + "', " +
				" audit_date = '" + audit_date + "', " +
				" audit_time = '" + audit_time + "', " +
				" audit_userid = '" + audit_userid + "' " +
				"where address = '" + address + "'";
		log.debug(updateQueryString);

		JDBCUtils.executeUpdateWEB(dbConnector, updateQueryString);
	}	
	
    // -------------------------------------------------------------------------------------------------------------

	public void persist(DBConnector dbConnector) throws PortalException {
    	if (audit_date == null) {
    		audit_date = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
    	}
    	if (audit_time == null) {
    		audit_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
    	}
		if (audit_userid == null) {
			audit_userid = "Portal";
		}
		
		
	    ArrayList<Email_profile> email_profileAL = Email_profile.getEmail_profile(dbConnector, address);
	    if ((email_profileAL != null) && (email_profileAL.size() > 0)) {
	    	update(dbConnector);
	    } else {
			String insertQueryString =
			        "insert into pub.email_profile "                         +
					"(address, tags, active, audit_date, audit_time, audit_userid) "        +
					"values 	(" +
						"'" + address + "', " 			+
						"'" + tags + "', " 				+
						"'" + (active?"1":"0") + "', " 	+
						"'" + audit_date + "', " 		+
						"'" + audit_time + "', " 		+
						"'" + audit_userid + "'" 		+
					"			)";
			
			JDBCUtils.executeUpdateWEB(dbConnector, insertQueryString);
	    }
	}
	
    // -------------------------------------------------------------------------------------------------------------

    public static void createTable(DBConnector dbConnector) throws PortalException {
        JDBCUtils.executeUpdateWEB(dbConnector, "drop table pub.email_profile");
        /*
        */

        String createTable =
                "CREATE TABLE pub.email_profile ( " +
                "   address varchar(60), " +
                "   tags varchar(512), " +
                "   \"active\" BIT, " +
                "   audit_date varchar(10), " +
                "   audit_time varchar(10), " +
                "   audit_userid varchar(20), PRIMARY KEY (address) " +
                ")";
        JDBCUtils.executeUpdateWEB(dbConnector, createTable);
    }

    // -------------------------------------------------------------------------------------------------------------
    
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }
    public boolean getActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public String getAudit_date() {
        return audit_date;
    }
    public void setAudit_date(String audit_date) {
        this.audit_date = audit_date;
    }
    public String getAudit_time() {
        return audit_time;
    }
    public void setAudit_time(String audit_time) {
        this.audit_time = audit_time;
    }
    public String getAudit_userid() {
        return audit_userid;
    }
    public void setAudit_userid(String audit_userid) {
        this.audit_userid = audit_userid;
    }

}
