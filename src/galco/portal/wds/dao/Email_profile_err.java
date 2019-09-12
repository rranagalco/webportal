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

public class Email_profile_err {
	private static Logger log = Logger.getLogger(Email_profile_err.class);

	String address;
    String addTags;
    String removeTags;    
    String active;
    String processed;    
    String audit_date;
    String audit_time;
    String audit_userid;

    public Email_profile_err(String address, String addTags, String removeTags, 
    						 String active, String processed, 
    						 String audit_date, String audit_time, String audit_userid) {
		this.address = address;
		this.addTags = addTags;
		this.removeTags = removeTags;		
		this.active = active;
		this.processed = processed;		
		this.audit_date = audit_date;
		this.audit_time = audit_time;
		this.audit_userid = audit_userid;
	}

    public Email_profile_err(String address, String addTags, String removeTags, 
    						 String active, String processed) {
		this.address = address;
		this.addTags = addTags;
		this.removeTags = removeTags;		
		this.active = active;
		this.processed = processed;		
	}

    public Email_profile_err() {
	}

	public static String getQueryFieldSelectionString() {
        return "address,addTags,removeTags,active,processed,audit_date,audit_time,audit_userid";
    }
    
    public static ArrayList<Email_profile_err> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Email_profile_err> al = new ArrayList<Email_profile_err>();

        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = dbConnector.getStatementWEB();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
                Email_profile_err email_profile_err = new Email_profile_err();

                email_profile_err.setAddress(JDBCUtils.getStringFromResultSet(rs, "address"));
                email_profile_err.setAddTags(JDBCUtils.getStringFromResultSet(rs, "addTags"));
                email_profile_err.setRemoveTags(JDBCUtils.getStringFromResultSet(rs, "removeTags"));                
                email_profile_err.setActive(JDBCUtils.getStringFromResultSet(rs, "active"));
                email_profile_err.setProcessed(JDBCUtils.getStringFromResultSet(rs, "processed"));                
                email_profile_err.setAudit_date(JDBCUtils.getStringFromResultSet(rs, "audit_date"));
                email_profile_err.setAudit_time(JDBCUtils.getStringFromResultSet(rs, "audit_time"));
                email_profile_err.setAudit_userid(JDBCUtils.getStringFromResultSet(rs, "audit_userid"));

                al.add(email_profile_err);
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
    public static void printDAOObjects(ArrayList<Email_profile_err> al) {
        if (al == null) {
            return;
        }
        
        for (Iterator<Email_profile_err> iterator = al.iterator(); iterator.hasNext();) {
            Email_profile_err email_profile_err = iterator.next();
            System.out.println  (
                    "address: " + email_profile_err.getAddress() + "\n" + 
                    "addTags: " + email_profile_err.getAddTags() + "\n" + 
                    "removeTags: " + email_profile_err.getRemoveTags() + "\n" +                    		
                    "active: " + email_profile_err.getActive() + "\n" + 
                    "processed: " + email_profile_err.getProcessed() + "\n" +                    
                    "audit_date: " + email_profile_err.getAudit_date() + "\n" + 
                    "audit_time: " + email_profile_err.getAudit_time() + "\n" + 
                    "audit_userid: " + email_profile_err.getAudit_userid() + "\n"
                                );
        }
    }
       
    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Email_profile_err> getEmail_profile(DBConnector dbConnector, String address, String audit_date, String audit_time) throws PortalException {
        String queryString = "select " + getQueryFieldSelectionString() + " from pub.email_profile_err " +
                             "where address = '" + address + "' " +
                             "  and audit_date = '" + audit_date + "' " +
                             "  and audit_time = '" + audit_time + "'";

        ArrayList<Email_profile_err> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
        // printDAOObjects(al);
        return al;
    }

    // -------------------------------------------------------------------------------------------------------------

	public void update(DBConnector dbConnector) throws PortalException {
		/*
   		audit_date = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
   		audit_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
   		*/
		audit_userid = "Portal";
		
		String updateQueryString = 
				"update pub.email_profile_err " +
				"set " +
				" addTags = '" + addTags + "', " +
				" removeTags = '" + removeTags + "', " +				
				" active = '" + active + "', " +
				" processed = '" + processed + "', " +				
				" audit_userid = '" + audit_userid + "' " +
				"where address = '" + address + "' " +
                "  and audit_date = '" + audit_date + "' " +
                "  and audit_time = '" + audit_time + "'";
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
		
		
	    ArrayList<Email_profile_err> email_profileAL = Email_profile_err.getEmail_profile(dbConnector, address, audit_date, audit_time);
	    if ((email_profileAL != null) && (email_profileAL.size() > 0)) {
	    	update(dbConnector);
	    } else {
			String insertQueryString =
			        "insert into pub.email_profile_err "                         +
					"(address, addTags, removeTags, active, processed, audit_date, audit_time, audit_userid) "        +
					"values 	(" 						+
						"'" + address + "', " 			+
						"'" + addTags + "', " 			+
						"'" + removeTags + "', " 		+						
						"'" + active + "', " 			+
						"'" + processed + "', " 			+						
						"'" + audit_date + "', " 		+
						"'" + audit_time + "', " 		+
						"'" + audit_userid + "'" 		+
					"			)";
			
			JDBCUtils.executeUpdateWEB(dbConnector, insertQueryString);
	    }
	}
	
    // -------------------------------------------------------------------------------------------------------------

    public static void createTable(DBConnector dbConnector) throws PortalException {
    	/*
        JDBCUtils.executeUpdateWEB(dbConnector, "drop table pub.email_profile_err");
        */

        String createTable =
                "CREATE TABLE pub.email_profile_err ( " +
                "   address varchar(60), " +
                "   addTags varchar(512), " +
                "   removeTags varchar(512), " +                
                "   \"active\" varchar(10), " +
                "   processed varchar(10), " +                
                "   audit_date varchar(10), " +
                "   audit_time varchar(10), " +
                "   audit_userid varchar(20), PRIMARY KEY (address, audit_date, audit_time) " +
                ")";
        log.debug(createTable);
        JDBCUtils.executeUpdateWEB(dbConnector, createTable);

        String createIndex =
                "CREATE INDEX index_processed ON pub.email_profile_err (processed)";
        log.debug(createIndex);
        JDBCUtils.executeUpdateWEB(dbConnector, createIndex);
    }

    // -------------------------------------------------------------------------------------------------------------
    
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddTags() {
        return addTags;
    }
    public void setAddTags(String addTags) {
        this.addTags = addTags;
    }
    
    public String getRemoveTags() {    	
        return removeTags;        
    }
    public void setRemoveTags(String removeTags) {    	
    	this.removeTags = removeTags;
    }

    public String getActive() {
        return active;
    }
    public void setActive(String active) {
        this.active = active;
    }

    public String getProcessed() {    	
        return processed;        
    }
    public void setProcessed(String processed) {    	
        this.processed = processed;        
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
