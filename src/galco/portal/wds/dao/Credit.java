package galco.portal.wds.dao;

import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

public class Credit {
	private static Logger log = Logger.getLogger(Credit.class);

	private String cust_num;
	private String start_date;
	private boolean on_hold;
	private String hold_date;
	private String comments;
	private String dun_brad;
	private double cr_limit;
	private String audit_date;
	private String audit_time;
	private String audit_userid;

	// -------------------------------------------------------------------------------------------------------------

	public Credit(String cust_num, String start_date, boolean on_hold,
			String hold_date, String comments, String dun_brad,
			double cr_limit, String audit_date, String audit_time,
			String audit_userid) {
		this.cust_num = cust_num;
		this.start_date = start_date;
		this.on_hold = on_hold;
		this.hold_date = hold_date;
		this.comments = comments;
		this.dun_brad = dun_brad;
		this.cr_limit = cr_limit;
		this.audit_date = audit_date;
		this.audit_time = audit_time;
		this.audit_userid = audit_userid;
	}

	public Credit(String cust_num, String comments) {
		String curDateYYYYMMDD = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
		String curTime = new SimpleDateFormat("hh:mm:ss").format(new Date());

		this.cust_num = cust_num;
		this.start_date = curDateYYYYMMDD;
		
		/*
		this.on_hold = true;
		this.hold_date = curDateYYYYMMDD;
		*/
		this.on_hold = false;
		this.hold_date = null;
		
		this.comments = comments;
		this.dun_brad = "";
		this.cr_limit = 0;
		this.audit_date = curDateYYYYMMDD;
		this.audit_time = curTime;
		this.audit_userid = "";
	}

	// -------------------------------------------------------------------------------------------------------------

	public void persist(DBConnector dbConnector) throws PortalException {
		String insertQueryString =
		        "insert into pub.credit "                                                               +
				"(cust_num, start_date, on_hold, hold_date, comments, "                                 +
				"dun_brad, cr_limit, audit_date, audit_time, audit_userid) "                            +
				"values ('" + cust_num + "', " + "'" + start_date + "', " + "'" + (on_hold?1:0) + "', " +
						
						((hold_date != null)?("'" + hold_date + "'"):("NULL")) +				
				
						", " + "'" + comments + "', " + "'" + dun_brad + "', "    +
				        "'" + cr_limit + "', " + "'" + audit_date + "', " + "'" + audit_time + "', "    +
				        "'" + audit_userid + "')";

		JDBCUtils.executeUpdateWDS(dbConnector, insertQueryString);
	}

	// -------------------------------------------------------------------------------------------------------------

	public String getCust_num() {
		return cust_num;
	}
	public void setCust_num(String cust_num) {
		this.cust_num = cust_num;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public boolean isOn_hold() {
		return on_hold;
	}
	public void setOn_hold(boolean on_hold) {
		this.on_hold = on_hold;
	}
	public String getHold_date() {
		return hold_date;
	}
	public void setHold_date(String hold_date) {
		this.hold_date = hold_date;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getDun_brad() {
		return dun_brad;
	}
	public void setDun_brad(String dun_brad) {
		this.dun_brad = dun_brad;
	}
	public double getCr_limit() {
		return cr_limit;
	}
	public void setCr_limit(double cr_limit) {
		this.cr_limit = cr_limit;
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
