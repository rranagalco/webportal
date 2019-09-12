package galco.portal.wds.dao;

import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;
import galco.portal.wds.matcher.Has_Cust_Num_Address_Address2_Zip;
import galco.portal.wds.matcher.Has_Cust_num;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Cust implements Has_Cust_Num_Address_Address2_Zip {
	private static Logger log = Logger.getLogger(Cust.class);

    private String cust_num;
    private String name;
    private String address;
    private String address2;
    private String city;
    private String state;
    private String zip;
    private String country;
    private String contact;
	private String cust_alpha;
    private String phone;
    private String phone_800num;
    private String fax_num;
    private String sales_rep2;    
    private boolean is_active;    

    // -------------------------------------------------------------------------------------------------------------

	public Cust() {
    }

	public Cust(String cust_num, String name, String address, String address2, String city,
			String state, String zip, String country, String contact, String phone,
			String phone_800num, String fax_num) {
		this(cust_num, name, address, address2, city, state, zip, country, contact, (!StringUtils.isBlank(name)?name:contact),
			 phone, phone_800num, fax_num);
	}

	public Cust(String cust_num, String name, String address, String address2, String city,
			String state, String zip, String country, String contact, String cust_alpha, String phone,
			String phone_800num, String fax_num) {
		this.cust_num = cust_num;
		this.name = name;
		this.address = address;
		
		if (StringUtils.isBlank(address2)) {
			this.address2 = "";
		} else {
			this.address2 = address2;
		}		
		
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.country = country;
		this.contact = contact;
		this.cust_alpha = cust_alpha;
		this.phone = phone;
		this.phone_800num = phone_800num;
		this.fax_num = fax_num;
	}

	// -------------------------------------------------------------------------------------------------------------]

	private static String getQueryFieldSelectionString() {
		return "cust_num, name, address, address2, city, state, zip, country, contact, cust_alpha, phone, phone_800num, sales_rep2, is_active";
	}

	public static ArrayList<Cust> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
		ArrayList<Cust> al = new ArrayList<Cust>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

			while (rs.next()) {
				Cust cust = new Cust();

				cust.setCust_num(JDBCUtils.getStringFromResultSet(rs, "cust_num"));
				cust.setName(JDBCUtils.getStringFromResultSet(rs, "name"));
				cust.setAddress(JDBCUtils.getStringFromResultSet(rs, "address"));
				cust.setAddress2(JDBCUtils.getStringFromResultSet(rs, "address2"));
				cust.setCity(JDBCUtils.getStringFromResultSet(rs, "city"));
				cust.setState(JDBCUtils.getStringFromResultSet(rs, "state"));
				cust.setZip(JDBCUtils.getStringFromResultSet(rs, "zip"));
				cust.setCountry(JDBCUtils.getStringFromResultSet(rs, "country"));
				cust.setContact(JDBCUtils.getStringFromResultSet(rs, "contact"));
				cust.setCust_alpha(JDBCUtils.getStringFromResultSet(rs, "cust_alpha"));
				cust.setPhone(JDBCUtils.getStringFromResultSet(rs, "phone"));
				cust.setPhone_800num(JDBCUtils.getStringFromResultSet(rs, "phone_800num"));
				cust.setSales_rep2(JDBCUtils.getStringFromResultSet(rs, "sales_rep2"));
				cust.setIs_active(rs.getBoolean("is_active"));

				al.add(cust);
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

	public static void printDAOObjects(ArrayList<Cust> al) {
		if (al == null) {
			return;
		}

		for (Iterator<Cust> iterator = al.iterator(); iterator.hasNext();) {
			Cust cust = iterator.next();

			log.debug("cust_num     : " + cust.getCust_num());
			log.debug("name         : " + cust.getName());
			log.debug("address      : " + cust.getAddress());
			log.debug("address2     : " + cust.getAddress2());
			log.debug("city         : " + cust.getCity());
			log.debug("state        : " + cust.getState());
			log.debug("zip          : " + cust.getZip());
			log.debug("country      : " + cust.getCountry());
			log.debug("contact      : " + cust.getContact());
			log.debug("cust_alpha   : " + cust.getCust_alpha());
			log.debug("phone        : " + cust.getPhone());
			log.debug("phone_800num : " + cust.getPhone_800num());
			log.debug("sales_rep2   : " + cust.getSales_rep2());						
			log.debug("is_active    : " + cust.getIs_active());			
        }
	}

	// -------------------------------------------------------------------------------------------------------------

	public static ArrayList<Cust> getCustsThatMatchedPhone(DBConnector dbConnector, String phone1, String phone2, String phone3) throws PortalException {
		if ((phone1 == null) && (phone2 == null) && (phone3 == null)) {
			return null;
		}

		String queryString = "select " + getQueryFieldSelectionString() + " from pub.cust where ";

		String whereClause = null;
		if (StringUtils.isBlank(phone1) == false) {
			whereClause = ("phone = '" + phone1 + "' or phone_800num = '" + phone1 + "'");
		}
		if (StringUtils.isBlank(phone2) == false) {
			if (whereClause == null) {
				whereClause = ("phone = '" + phone2 + "' or phone_800num = '" + phone2 + "'");
			} else {
				whereClause += (" or " + ("phone = '" + phone2 + "' or phone_800num = '" + phone2 + "'"));
			}
		}
		if (StringUtils.isBlank(phone3) == false) {
			if (whereClause == null) {
				whereClause = ("phone = '" + phone3 + "' or phone_800num = '" + phone3 + "'");
			} else {
				whereClause += (" or " + ("phone = '" + phone3 + "' or phone_800num = '" + phone3 + "'"));
			}
		}

		queryString += whereClause;

		ArrayList<Cust> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

	public static ArrayList<Cust> getCustsWithMatchingPhoneNumber(DBConnector dbConnector, String phone) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.cust " +
							 "where phone = '" + phone + "' or phone_800num = '" + phone + "'";
		ArrayList<Cust> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

	public static ArrayList<Cust> getCustForGivenCustNo(DBConnector dbConnector, String cust_num) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.cust " +
					 		 "where cust_num = '" + cust_num + "'";
		ArrayList<Cust> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		return al;
	}

	public static ArrayList<Cust> getCustsForGivenCustNos(DBConnector dbConnector, HashSet<String> hsDistinctCustNums) throws PortalException {
		if ((hsDistinctCustNums == null) || (hsDistinctCustNums.size() == 0)){
			return null;
		}

		String custNosString = null;
		for (Iterator<String> iterator = hsDistinctCustNums.iterator(); iterator.hasNext();) {
			if (custNosString != null) {
				custNosString += ", ";
			} else {
				custNosString = "";
			}

			custNosString += "'" + iterator.next() + "'";
		}

		String queryString = "select " + getQueryFieldSelectionString() + " from pub.cust " +
					 		 "where cust_num in (" + custNosString + ")";
		ArrayList<Cust> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

	public static HashSet<String> getCustsWithMatchingAddresses(DBConnector dbConnector, HashSet<String> hsDistinctCustNums, String address, String zip) throws PortalException {
		if ((hsDistinctCustNums == null) || (hsDistinctCustNums.size() == 0)){
			return null;
		}


		String custNosString = "";
		for (Iterator iterator = hsDistinctCustNums.iterator(); iterator.hasNext();) {
			custNosString += "'" + iterator.next() + "'";
		}


		HashSet<String> custsWithMatchingAddresses = new HashSet<String>();

		Statement stmt = dbConnector.getStatementWDS();
		ResultSet rs = null;

        try {
    		String custQueryString = "select cust_num, address, address2, zip from pub.cust " +
    								 " where cust_num in (" + custNosString + ")";
            rs = stmt.executeQuery(custQueryString);

			while (rs.next()) {
				String addressCust, address2Cust, zipCust;

				addressCust = JDBCUtils.getStringFromResultSet(rs, "address") + " ";
				address2Cust = JDBCUtils.getStringFromResultSet(rs, "address2") + " ";
				zipCust = JDBCUtils.getStringFromResultSet(rs, "zip") + "     ";

				String streetNoCust, streetNo2Cust, zip5Cust;
				streetNoCust = addressCust.substring(0, addressCust.indexOf(' '));
				streetNo2Cust = address2Cust.substring(0, address2Cust.indexOf(' '));
				zip5Cust = zipCust.substring(0, 5);

				String streetNo, zip5;
				streetNo = addressCust.substring(0, (address + " ").indexOf(' '));
				zip5 = (zip + "     ").substring(0, 5);

				if ((zip5Cust.compareTo(zip5) == 0				 ) &&
					((streetNoCust.compareTo(streetNo)  == 0) ||
			         (streetNo2Cust.compareTo(streetNo) == 0)    )    ) {
					custsWithMatchingAddresses.add(JDBCUtils.getStringFromResultSet(rs, "cust_num"));
				}
			}

			rs.close();
			rs = null;
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


		try {
			String custshipQueryString = "select cust_num, address, address2, zip from pub.custship " +
										 " where cust_num in (" + custNosString + ")";
       		stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(custshipQueryString);

			while (rs.next()) {
				String addressCustship, address2Custship, zipCustship;

				addressCustship = JDBCUtils.getStringFromResultSet(rs, "address") + " ";
				address2Custship = JDBCUtils.getStringFromResultSet(rs, "address2") + " ";
				zipCustship = JDBCUtils.getStringFromResultSet(rs, "zip") + "     ";

				String streetNoCustship, streetNo2Custship, zip5Custship;
				streetNoCustship = addressCustship.substring(0, addressCustship.indexOf(' '));
				streetNo2Custship = address2Custship.substring(0, address2Custship.indexOf(' '));
				zip5Custship = zipCustship.substring(0, 5);

				String streetNo, zip5;
				streetNo = addressCustship.substring(0, (address + " ").indexOf(' '));
				zip5 = (zip + "     ").substring(0, 5);

				if ((zip5Custship.compareTo(zip5) == 0				 ) &&
				    ((streetNoCustship.compareTo(streetNo)  == 0) ||
				     (streetNo2Custship.compareTo(streetNo) == 0)    )    ) {
					custsWithMatchingAddresses.add(JDBCUtils.getStringFromResultSet(rs, "cust_num"));
				}
			}

			rs.close();
			rs = null;
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


		try {
			String custbillQueryString = "select cust_num, address, address2, zip from pub.custbill " +
										 " where cust_num in (" + custNosString + ")";
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(custbillQueryString);

            while (rs.next()) {
				String addressCustbill, address2Custbill, zipCustbill;

				addressCustbill = JDBCUtils.getStringFromResultSet(rs, "address") + " ";
				address2Custbill = JDBCUtils.getStringFromResultSet(rs, "address2") + " ";
				zipCustbill = JDBCUtils.getStringFromResultSet(rs, "zip") + "     ";

				String streetNoCustbill, streetNo2Custbill, zip5Custbill;
				streetNoCustbill = addressCustbill.substring(0, addressCustbill.indexOf(' '));
				streetNo2Custbill = address2Custbill.substring(0, address2Custbill.indexOf(' '));
				zip5Custbill = zipCustbill.substring(0, 5);

				String streetNo, zip5;
				streetNo = addressCustbill.substring(0, (address + " ").indexOf(' '));
				zip5 = (zip + "     ").substring(0, 5);

				if ((zip5Custbill.compareTo(zip5) == 0				 ) &&
				    ((streetNoCustbill.compareTo(streetNo)  == 0) ||
				     (streetNo2Custbill.compareTo(streetNo) == 0)    )    ) {
					custsWithMatchingAddresses.add(JDBCUtils.getStringFromResultSet(rs, "cust_num"));
				}
			}

			rs.close();
			rs = null;
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

		try {
			stmt.close();
		} catch (SQLException e) {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e3) {
			}

            throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}

        if (custsWithMatchingAddresses.size() == 0) {
        	return null;
        }
        return custsWithMatchingAddresses;
	}

	public static ArrayList<Cust> getCustsForGivenZip(DBConnector dbConnector, String zip) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.cust " +
					 		 "where zip = '" + zip + "'";
		ArrayList<Cust> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		return al;
	}

	// -------------------------------------------------------------------------------------------------------------

	public static HashSet<String> getTheListOfDistinctCustNos(ArrayList<? extends Has_Cust_num> al) {
		if (al == null) {
			return null;
		}

		HashSet<String> hsDistinct = new HashSet<String>();

		for (Iterator<? extends Has_Cust_num> iterator = al.iterator(); iterator.hasNext();) {
			Has_Cust_num has_Cust_num = iterator.next();

			hsDistinct.add(has_Cust_num.getCust_num());
		}

		return hsDistinct;
	}

    // -------------------------------------------------------------------------------------------------------------
    
    public static String getCustomerWhoWasMostRecentlyCreated(DBConnector dbConnector, HashSet<String> hsDistinctCustNums) throws PortalException {
		if ((hsDistinctCustNums == null) || (hsDistinctCustNums.size() == 0)){
			return null;
		}

		String custNosString = null;
		for (Iterator<String> iterator = hsDistinctCustNums.iterator(); iterator.hasNext();) {
			if (custNosString != null) {
				custNosString += ", ";
			} else {
				custNosString = "";
			}

			custNosString += "'" + iterator.next() + "'";
		}

		String queryString = "select " + getQueryFieldSelectionString() + ", cust_add_date from pub.cust " + 
							 " where cust_num in (" + custNosString + ") " +
							 "   and cust.cust_add_date in (select max(cust_add_date) as max_date from pub.cust " +
							 							 " where cust_num in (" + custNosString + "))";
		
		System.out.println(queryString);
		
		ArrayList<Cust> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		
		if ((al != null) && (al.size() > 0)) {
			return al.get(0).getCust_num();
		} else {
			return null;
		}
	}
    	
// -------------------------------------------------------------------------------------------------------------

	// This method increments the cust_num, of ardef table, by one.
	private static String getNextCustomerNum(DBConnector dbConnector) throws PortalException {
		String ardefQueryString = "select cust_num from pub.ardef";

		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = dbConnector.getStatementWDS();
	        rs = stmt.executeQuery(ardefQueryString);

			try {
				if (!rs.next()) {
					rs.close();
					rs = null;
			        stmt.close();
			        stmt = null;

					throw new RuntimeException("Ardef table is empty.");
				}
			} catch (SQLException e) {
				throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
			}

			int cust_num = new Integer(JDBCUtils.getStringFromResultSet(rs, "cust_num")).intValue();

	        rs.close();
			rs = null;
	        stmt.close();
			stmt = null;

			String ardefUpdateString = "update pub.ardef SET cust_num = '" + (cust_num + 1) + "'";
			JDBCUtils.executeUpdateWDS(dbConnector, ardefUpdateString);

			return String.format("%06d", cust_num);
		} catch (SQLException e1) {
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

            throw new PortalException(e1, PortalException.SEVERITY_LEVEL_1);
		}
	}

	public void persist(DBConnector dbConnector) throws PortalException {
		cust_num = getNextCustomerNum(dbConnector);

		log.debug("New customer number: " + cust_num);

        String curDateYYYYMMDD = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        // String curDateYYYYMMDD = new SimpleDateFormat("MM/dd/yy").format(new Date());
        String curDateHHMMSS = new SimpleDateFormat("HH:mm:ss").format(new Date());
        // curDateHHMMSS.replaceAll(":", "\\:");

        String tax_auth = ";;;", tax_code = "N";
        if (country != null) {
        	if ((country.compareToIgnoreCase("USA"			) == 0) ||
        		(country.compareToIgnoreCase("United States") == 0)    ) {
        		if (state != null) {
        	        ArrayList<Tax> taxAL = Tax.getTax(dbConnector, state);
        	        if ((taxAL != null) && (taxAL.size() > 0)) {
	        	        tax_auth = state + ";;;";
	        			tax_code = "T";
        	        }
        		}
        	}
        }

		String insertQueryString = "insert into pub.cust " +
				"(cust_num, name, name2, address, address2, city, state, zip, country, phone, fax_num, modem_num, " +
				"phone_800num, edi_num, edi_policy, contact, cust_alpha, territory, sales_rep, sales_rep2, serv_rep, " +
				"tax_code, tax_auth, tax_exemptnum, tax_ex_rsn, cust_class, order_class, po_num_reqd, job_num_reqd, " +
				"shp_complete, commit_hold, lead_time, bo_policy, bo_priority, subs_policy, use_partcust, " +
				"msds_always, terms, shipvia, route_num, drop_num, ups_zone, fob, addon, addon_value, comment_cd, " +
				"comment_hdr, currency, copies_oa, copies_ps, price_on_ps, copies_inv, do_stmt, bal_method, stmt_seq, " +
				"stmt_cycle, master_cust, do_fin_chg, cust_status, sic_code, price_level, price_class, use_min_ord, " +
				"location, sales_acct, cog_acct, mail_permit, bulk_feenum, del_svc, comments, is_active, " +
				"misc_alpha1, misc_alpha2, misc_num1, misc_num2, misc_log1, misc_log2, misc_date1, misc_date2, " +
				"audit_date, audit_time, audit_userid, faxnum_inv, web_addr, ups_acctnum, sh_attn_to, ok_restrict, " +
			 // "ignore_zipalert, inv_email_addr, last_inv_upd, ap_cont_id, cust_add_date, cust_add_time) " +
			 // "ignore_zipalert, inv_email_addr, last_inv_upd, ap_cont_id, cust_add_date, cust_add_time, fdx_acctnum) " +				
			    "ignore_zipalert, inv_email_addr,               ap_cont_id, cust_add_date, cust_add_time, fdx_acctnum) " +				
				"select " +
				"'" + cust_num + "', '" + name + "', name2, '" + address + "', '" + address2 + "', '" + city + "', '" + state + "', '" + zip + "', '" + country + "', '" + phone + "', '" + fax_num + "', modem_num, " +

       		// qqq
        
			//  "'" + phone_800num + "', edi_num, edi_policy, '" + contact + "', '" + cust_alpha + "', territory, sales_rep, sales_rep2, serv_rep, " + 
				"'" + phone_800num + "', edi_num, edi_policy, '" + contact + "', '" + cust_alpha + "', territory,           '',         '', serv_rep, " +

			//  "tax_code          ,      tax_auth     , tax_exemptnum, tax_ex_rsn, cust_class, order_class, po_num_reqd, job_num_reqd, " + 
				"'" + tax_code + "', '" + tax_auth + "', tax_exemptnum, tax_ex_rsn, cust_class, order_class, po_num_reqd, job_num_reqd, " +
				
				"shp_complete, commit_hold, lead_time, bo_policy, bo_priority, subs_policy, use_partcust, " +
				"msds_always, terms, shipvia, route_num, drop_num, ups_zone, fob, addon, addon_value, comment_cd, " +
				"comment_hdr, currency, copies_oa, copies_ps, price_on_ps, copies_inv, do_stmt, bal_method, stmt_seq, " +
				"stmt_cycle, master_cust, do_fin_chg, cust_status, sic_code, price_level, price_class, use_min_ord, " +
				"location, sales_acct, cog_acct, mail_permit, bulk_feenum, del_svc, comments, is_active, " +
				"substr(misc_alpha1,1,8), substr(misc_alpha2,1,8), misc_num1, misc_num2, misc_log1, misc_log2, misc_date1, misc_date2, " +
			 // "audit_date               , audit_time            , audit_userid , faxnum_inv, web_addr, ' ', sh_attn_to, ok_restrict, " +
			    "'" + curDateYYYYMMDD + "', '" + curDateHHMMSS +"', 'WEB'        , faxnum_inv, web_addr, ' ', sh_attn_to, ok_restrict, " +
				
			 // "ignore_zipalert, inv_email_addr, last_inv_upd, ap_cont_id, '" + curDateYYYYMMDD + "', cust_add_time " + 
			 // "ignore_zipalert, inv_email_addr, '',           ap_cont_id, '" + curDateYYYYMMDD + "', '" + curDateHHMMSS +"', '' " +
			 // "ignore_zipalert, inv_email_addr, '',           ap_cont_id, '" + curDateYYYYMMDD + "', '" + curDateHHMMSS +"' " +
			 // "ignore_zipalert, inv_email_addr, '',           ap_cont_id, '" + curDateYYYYMMDD + "', '10:10:10' " +
			 // "ignore_zipalert, inv_email_addr, null        , ap_cont_id, '" + curDateYYYYMMDD + "', '', '' " +
			 // "ignore_zipalert, inv_email_addr, 'null'      , ap_cont_id, '" + curDateYYYYMMDD + "', '" + curDateHHMMSS +"', '' " +
			    "ignore_zipalert, inv_email_addr,               ap_cont_id, '" + curDateYYYYMMDD + "', '" + curDateHHMMSS +"', '' " +
			 
				
				"from pub.cust where cust_num = '036329'";
		
		log.debug(insertQueryString);

		JDBCUtils.executeUpdateWDS(dbConnector, insertQueryString);
	}

	public void update(DBConnector dbConnector) throws PortalException {
		String updateQueryString =
				"update pub.cust " +
				"set " +
				"name = '" + name + "', " +
				"address = '" + address + "', " +
				"city = '" + city + "', " +
				"state = '" + state + "', " +
				"zip = '" + zip + "', " +
				"country = '" + country + "', " +
				"phone = '" + phone + "', " +
				"phone_800num = '" + phone_800num + "' " +
				"where cust_num = '" + cust_num + "'";

		log.debug(updateQueryString);

		JDBCUtils.executeUpdateWDS(dbConnector, updateQueryString);
	}

	public static void updateCompanyName(DBConnector dbConnector, String cust_num, String name) throws PortalException {
		String updateQueryString =
				"update pub.cust " +
				"set " +
				"name = '" + name + "' " +
				"where cust_num = '" + cust_num + "'";

		log.debug(updateQueryString);

		JDBCUtils.executeUpdateWDS(dbConnector, updateQueryString);
	}

	// -------------------------------------------------------------------------------------------------------------

	public String getCust_num() {
		return cust_num;
	}
	public void setCust_num(String cust_num) {
		this.cust_num = cust_num;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getCust_alpha() {
		return cust_alpha;
	}
	public void setCust_alpha(String cust_alpha) {
		this.cust_alpha = cust_alpha;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPhone_800num() {
		return phone_800num;
	}
	public void setPhone_800num(String phone_800num) {
		this.phone_800num = phone_800num;
	}
    public String getFax_num() {
		return fax_num;
	}
	public void setFax_num(String fax_num) {
		this.fax_num = fax_num;
	}
	
    public String getSales_rep2() {
		return sales_rep2;
	}
	public void setSales_rep2(String sales_rep2) {
		this.sales_rep2 = sales_rep2;
	}
	
    public boolean getIs_active() {    	
		return is_active;		
	}

    public void setIs_active(boolean is_active) {			
		this.is_active = is_active;		
	}

}
