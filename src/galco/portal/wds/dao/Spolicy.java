package galco.portal.wds.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.progress.common.util.Port;

import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;

public class Spolicy {
	private static Logger log = Logger.getLogger(Spolicy.class);

	private String user_name;
	private String cust_num;
	private int cont_no;
	private String order;
	private String quote;
	private String reorder;
	private String wishlist;
	private String convertquote;

	private boolean cod_ok;
	private boolean open_account;
	private boolean view_price;
	
	private String accessrole;
	
	private String dfltAddrBill;
	private String dfltAddrShip;

	// -------------------------------------------------------------------------------------------------------------


	public Spolicy() {
	}

	public Spolicy(String user_name, String cust_num, int cont_no,
			String order, String quote, String reorder, String wishlist, String convertquote,
			boolean cod_ok, boolean open_account, boolean view_price, String accessrole, 
			String dfltAddrBill, String dfltAddrShip) {
		this.user_name = user_name;
		this.cust_num = cust_num;
		this.cont_no = cont_no;
		this.order = order;
		this.quote = quote;
		this.reorder = reorder;
		this.wishlist = wishlist;
		this.convertquote = convertquote;
		this.cod_ok = cod_ok;
		this.open_account = open_account;
		this.view_price = view_price;
		this.accessrole = accessrole;
		this.dfltAddrBill = dfltAddrBill;
		this.dfltAddrShip = dfltAddrShip;
	}

	// -------------------------------------------------------------------------------------------------------------

	private static String getQueryFieldSelectionString() {
		return "\"user_name\", cust_num, cont_no, order, quote, reorder, wishlist, convertquote, cod_ok, open_account, view_price, accessrole, dfltAddrBill, dfltAddrShip ";
	}

	// -------------------------------------------------------------------------------------------------------------

	public static ArrayList<Spolicy> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
		ArrayList<Spolicy> al = new ArrayList<Spolicy>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

			while (rs.next()) {
				Spolicy spolicy = new Spolicy();

				spolicy.setUser_name(JDBCUtils.getStringFromResultSet(rs, "user_name"));
				spolicy.setCust_num(JDBCUtils.getStringFromResultSet(rs, "cust_num"));
				spolicy.setCont_no(rs.getInt("cont_no"));
				spolicy.setOrder(JDBCUtils.getStringFromResultSet(rs, "order"));
				spolicy.setQuote(JDBCUtils.getStringFromResultSet(rs, "quote"));
				spolicy.setReorder(JDBCUtils.getStringFromResultSet(rs, "reorder"));
				spolicy.setWishlist(JDBCUtils.getStringFromResultSet(rs, "wishlist"));
				spolicy.setConvertquote(JDBCUtils.getStringFromResultSet(rs, "convertquote"));
	            spolicy.setCod_ok(rs.getBoolean("cod_ok"));
	            spolicy.setOpen_account(rs.getBoolean("open_account"));
	            spolicy.setView_price(rs.getBoolean("view_price"));
	            spolicy.setAccessrole(JDBCUtils.getStringFromResultSet(rs, "accessrole"));	            
	            spolicy.setDfltAddrBill(JDBCUtils.getStringFromResultSet(rs, "dfltAddrBill"));	            
	            spolicy.setDfltAddrShip(JDBCUtils.getStringFromResultSet(rs, "dfltAddrShip"));	            
	            
				al.add(spolicy);
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

	public static void printDAOObjects(ArrayList<Spolicy> al) {
		if (al == null) {
			return;
		}

		for (Iterator<Spolicy> iterator = al.iterator(); iterator.hasNext();) {
			Spolicy spolicy = iterator.next();
			log.debug("user_name    : " + spolicy.getUser_name());
			log.debug("cust_num     : " + spolicy.getCust_num());
			log.debug("cont_no      : " + spolicy.getCont_no());
			log.debug("order        : " + spolicy.getOrder());
			log.debug("quote        : " + spolicy.getQuote());
			log.debug("reorder      : " + spolicy.getReorder());
			log.debug("wishlist     : " + spolicy.getWishlist());
			log.debug("convertquote : " + spolicy.getConvertquote());
			log.debug("cod_ok       : " + spolicy.isCod_ok());
			log.debug("open_account : " + spolicy.isOpen_account());
			log.debug("view_price   : " + spolicy.isView_price());
			log.debug("accessrole   : " + spolicy.getAccessrole());
			log.debug("dfltAddrBill : " + spolicy.getDfltAddrBill());
			log.debug("dfltAddrShip : " + spolicy.getDfltAddrShip());
        }
	}

	// -------------------------------------------------------------------------------------------------------------

	public static ArrayList<Spolicy> getSpolicyRecordForTheGivenUsername(DBConnector dbConnector, String user_name) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.spolicy " +
							 "where lcase(\"user_name\") = '" + user_name + "'";

		ArrayList<Spolicy> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

	// -------------------------------------------------------------------------------------------------------------

	public static ArrayList<Spolicy> getAllSpolicyRecords(DBConnector dbConnector) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.spolicy";
		
		ArrayList<Spolicy> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		
		return al;
	}

	// -------------------------------------------------------------------------------------------------------------

	public static String[] getUserNamesWThatHaveTheGivenEmail(DBConnector dbConnector, String e_mail_address) throws PortalException {
		String queryString = "select pub.spolicy.\"user_name\" from pub.spolicy, pub.contact " +
				             " where contact.e_mail_address = '" + e_mail_address.trim() + "' " +
				             "  and spolicy.cust_num = contact.pros_cd " +
				             "  and spolicy.cont_no = contact.cont_no";
		
		ArrayList<String> al = new ArrayList<String>(10);
		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

			while (rs.next()) {
				Spolicy spolicy = new Spolicy();

				al.add(JDBCUtils.getStringFromResultSet(rs, "user_name"));
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
        
        String[] alSA = new String[al.size()];
        for (int i = 0; i < alSA.length; i++) {
			alSA[i] = al.get(i);
		}
	
        return alSA;
	}

	// -------------------------------------------------------------------------------------------------------------

	public static ArrayList<Spolicy> getSpolicyRecordForTheGivenCustContact(DBConnector dbConnector, String cust_num, int cont_no) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.spolicy " +
				 "where cust_num = '" + cust_num + "' " +
				 "  and cont_no = '" + cont_no + "'";

		ArrayList<Spolicy> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

	// -------------------------------------------------------------------------------------------------------------

	public static void createTables(DBConnector dbConnector) throws PortalException {
		// JDBCUtils.executeUpdate("drop table pub.spolicy");

		String createTable =
				"CREATE TABLE pub.spolicy ( " +
				"   \"user_name\" varchar(255), " +
				"   cust_num varchar(10), " +
				"   cont_no int, " +
				"   order varchar(10), " +
				"   quote varchar(10), " +
				"   reorder varchar(10), " +
				"   wishlist varchar(10), " +
				"   convertquote varchar(10), " +
				"   cod_ok bit, " +
				"   open_account bit, " +
				"   view_price bit, PRIMARY KEY (\"user_name\") " +
				")";
		log.debug(createTable);
		JDBCUtils.executeUpdateWDS(dbConnector, createTable);

		/*
		String createIndex1 =
				"CREATE UNIQUE INDEX spolicy_user_name " +
				"   ON pub.spolicy (\"USER_NAME\")";
		JDBCUtils.executeUpdate(createIndex1);

		String createIndex2 =
				"CREATE UNIQUE INDEX spolicy_cust_num_and_cont_no " +
				"   ON pub.spolicy (cust_num, cont_no)";
		JDBCUtils.executeUpdate(createIndex2);
		*/
	}

	// -------------------------------------------------------------------------------------------------------------

	public static void update_dfltAddrBill(DBConnector dbConnector, String user_name, String dfltAddrBill) throws PortalException {
		String updateQueryString = 
				"update pub.spolicy " +
				"set dfltAddrBill = '" + dfltAddrBill + "' " +
				"where user_name = '" + user_name + "'";
		log.debug(updateQueryString);

		JDBCUtils.executeUpdateWDS(dbConnector, updateQueryString);
	}	

	public static void update_dfltAddrShip(DBConnector dbConnector, String user_name, String dfltAddrShip) throws PortalException {
		String updateQueryString = 
				"update pub.spolicy " +
				"set dfltAddrShip = '" + dfltAddrShip + "' " +
				"where user_name = '" + user_name + "'";
		log.debug(updateQueryString);

		JDBCUtils.executeUpdateWDS(dbConnector, updateQueryString);
	}	
	
	public static void updateCust_numAndCont_no(DBConnector dbConnector, String user_name, String cust_num, int cont_no) throws PortalException {
		String updateQueryString = 
				"update pub.spolicy " +
				"  set cust_num = '" + cust_num + "', " +
				"      cont_no = " + cont_no + " " +
				"where user_name = '" + user_name + "'";
		log.debug(updateQueryString);

		JDBCUtils.executeUpdateWDS(dbConnector, updateQueryString);
	}	
	
	// -------------------------------------------------------------------------------------------------------------
	
	public static void delete(DBConnector dbConnector, String user_name) throws PortalException {
		String updateQueryString = 
				"delete from pub.spolicy " +
				"where user_name = '" + user_name + "'";
		log.debug(updateQueryString);

		JDBCUtils.executeUpdateWDS(dbConnector, updateQueryString);
	}	
	
	// -------------------------------------------------------------------------------------------------------------
	
	public void persist(DBConnector dbConnector) throws PortalException {
		String insertQueryString =
		        "insert into pub.spolicy "                                                               +
				"(\"user_name\", cust_num, cont_no, order, quote, reorder, wishlist, convertquote, cod_ok, open_account, view_price, accessrole, dfltAddrBill, dfltAddrShip) "     +
				"values ('" + user_name + "', " + "'" + cust_num + "', " + "'" + cont_no + "', "         +
				        "'" + order + "', " + "'" + quote + "', " + "'" + reorder + "', "                +
				        "'" + wishlist + "', " + "'" + convertquote + "', " +
				        "'" + (cod_ok?"1":"0") + "', " + "'" + (open_account?"1":"0") + "', " + "'" + (view_price?"1":"0") + "', '" + accessrole + "', '" + dfltAddrBill + "', '" + dfltAddrShip + "')";

		JDBCUtils.executeUpdateWDS(dbConnector, insertQueryString);
	}

	// -------------------------------------------------------------------------------------------------------------

	public static Spolicy duplicateSpolicyRecord(DBConnector dbConnector, String oldUserID, String newUserID) throws PortalException {
		try {
			ArrayList<Spolicy> spolicyNewAL = Spolicy.getSpolicyRecordForTheGivenUsername(dbConnector, newUserID);
			if (spolicyNewAL != null) {
				return null;
			}
			
			ArrayList<Spolicy> spolicyOldAL = Spolicy.getSpolicyRecordForTheGivenUsername(dbConnector, oldUserID);
			if ((spolicyOldAL == null) || (spolicyOldAL.size() != 1)) {
				throw new PortalException(new Exception("Spolicy record is missing."), PortalException.SEVERITY_LEVEL_1);
			}
			
			Spolicy spolicy = spolicyOldAL.get(0);
			spolicy.setUser_name(newUserID);
			spolicy.persist(dbConnector);
			
			return spolicy;
		} catch (PortalException e) {
			log.debug(e.getE());
			throw e;
		}
	}

	// -------------------------------------------------------------------------------------------------------------

	public static Spolicy changeUserName(DBConnector dbConnector, String oldUserName, String newUserName) throws PortalException {
		try {
			ArrayList<Spolicy> spolicyNewAL = Spolicy.getSpolicyRecordForTheGivenUsername(dbConnector, newUserName);
			if ((spolicyNewAL != null) && (spolicyNewAL.size() > 0)) {
				return null;
			}
			
			ArrayList<Spolicy> spolicyOldAL = Spolicy.getSpolicyRecordForTheGivenUsername(dbConnector, oldUserName);
			if ((spolicyOldAL == null) || (spolicyOldAL.size() != 1)) {
				throw new PortalException(new Exception("Spolicy record is missing."), PortalException.SEVERITY_LEVEL_1);
			}
			
			Spolicy.delete(dbConnector, oldUserName);

			Spolicy spolicy = spolicyOldAL.get(0);
			spolicy.setUser_name(newUserName);
			spolicy.persist(dbConnector);
			
			return spolicy;
		} catch (PortalException e) {
			log.debug(e.getE());
			throw e;
		}
	}

	// -------------------------------------------------------------------------------------------------------------

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getCust_num() {
		return cust_num;
	}

	public void setCust_num(String cust_num) {
		this.cust_num = cust_num;
	}

	public int getCont_no() {
		return cont_no;
	}

	public void setCont_no(int cont_no) {
		this.cont_no = cont_no;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public String getReorder() {
		return reorder;
	}

	public void setReorder(String reorder) {
		this.reorder = reorder;
	}

	public String getWishlist() {
		return wishlist;
	}

	public void setWishlist(String wishlist) {
		this.wishlist = wishlist;
	}

	public String getConvertquote() {
		return convertquote;
	}

	public void setConvertquote(String convertquote) {
		this.convertquote = convertquote;
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
	

	public String getAccessrole() {
		return accessrole;
	}

	public void setAccessrole(String accessrole) {
		this.accessrole = accessrole;
	}
	
	public String getDfltAddrBill() {
		return dfltAddrBill;
	}

	public void setDfltAddrBill(String dfltAddrBill) {
		this.dfltAddrBill = dfltAddrBill;
	}

	public String getDfltAddrShip() {
		return dfltAddrShip;
	}

	public void setDfltAddrShip(String dfltAddrShip) {
		this.dfltAddrShip = dfltAddrShip;
	}
	
}
