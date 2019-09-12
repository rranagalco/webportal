package galco.portal.wds.dao;

import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;
import galco.portal.wds.matcher.Has_Cust_Num_Address_Address2_Zip;

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

public class Custbill implements Has_Cust_Num_Address_Address2_Zip {
	private static Logger log = Logger.getLogger(Custbill.class);

    String cust_num;
    String billto_num;
	int cont_no;
    String name;
    String address;
    String address2;
    String city;
    String state;
    String zip;
    String country;
    String phone;
    String faxnum_inv;
    String audit_date;
    String audit_time;
    String audit_userid;
    boolean changed_online;

	// -------------------------------------------------------------------------------------------------------------

	public Custbill() {
		super();
	}
	
	public Custbill(String cust_num, String billto_num, int cont_no, String name, String address, String address2, String city, String state, String zip, String country, String phone, String faxnum_inv, boolean changed_online) {
        this(cust_num, billto_num, cont_no, name, address, address2, city, state, zip, country, phone, faxnum_inv, new SimpleDateFormat("yyyy/MM/dd").format(new Date()), new SimpleDateFormat("HH:mm:ss").format(new Date()), "WEB", changed_online);
	}
	
	public Custbill(String cust_num, String billto_num, int cont_no, String name, String address, String address2, String city, String state, String zip, String country, String phone, String faxnum_inv, String audit_date, String audit_time, String audit_userid, boolean changed_online) {
		super();
		
	    this.cust_num = cust_num;
	    this.billto_num = billto_num;
	    this.cont_no = cont_no;
	    this.name = name;
	    this.address = address;
	    this.address2 = address2;
	    this.city = city;
	    this.state = state;
	    this.zip = zip;
	    this.country = country;
	    this.phone = phone;
	    this.faxnum_inv = faxnum_inv;
	    this.audit_date = audit_date;
	    this.audit_time = audit_time;
	    this.audit_userid = audit_userid;
	    this.changed_online = changed_online;
	}

	// -------------------------------------------------------------------------------------------------------------

	private static String getQueryFieldSelectionString() {
        return "cust_num,billto_num,cont_no,name,address,address2,city,state,zip,country,phone,faxnum_inv,audit_date,audit_time,audit_userid,changed_online ";
	}

	public static ArrayList<Custbill> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
		ArrayList<Custbill> al = new ArrayList<Custbill>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

			while (rs.next()) {
				Custbill custbill = new Custbill();

                custbill.setCust_num(JDBCUtils.getStringFromResultSet(rs, "cust_num"));
                custbill.setBillto_num(JDBCUtils.getStringFromResultSet(rs, "billto_num"));
                custbill.setCont_no(rs.getInt("cont_no"));
                custbill.setName(JDBCUtils.getStringFromResultSet(rs, "name"));
                custbill.setAddress(JDBCUtils.getStringFromResultSet(rs, "address"));
                custbill.setAddress2(JDBCUtils.getStringFromResultSet(rs, "address2"));
                custbill.setCity(JDBCUtils.getStringFromResultSet(rs, "city"));
                custbill.setState(JDBCUtils.getStringFromResultSet(rs, "state"));
                custbill.setZip(JDBCUtils.getStringFromResultSet(rs, "zip"));
                custbill.setCountry(JDBCUtils.getStringFromResultSet(rs, "country"));
                custbill.setPhone(JDBCUtils.getStringFromResultSet(rs, "phone"));
                custbill.setFaxnum_inv(JDBCUtils.getStringFromResultSet(rs, "faxnum_inv"));
                custbill.setAudit_date(JDBCUtils.getStringFromResultSet(rs, "audit_date"));
                custbill.setAudit_time(JDBCUtils.getStringFromResultSet(rs, "audit_time"));
                custbill.setAudit_userid (JDBCUtils.getStringFromResultSet(rs, "audit_userid"));
                custbill.setChanged_online(rs.getBoolean("changed_online"));

				al.add(custbill);
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

	public static void printDAOObjects(ArrayList<Custbill> al) {
		if (al == null) {
			return;
		}

		for (Iterator<Custbill> iterator = al.iterator(); iterator.hasNext();) {
			Custbill custbill = iterator.next();

            System.out.println  (
                    "cust_num: " + custbill.getCust_num() + "\n" + 
                    "billto_num: " + custbill.getBillto_num() + "\n" +
                    "cont_no: " + custbill.getCont_no() + "\n" +
                    "name: " + custbill.getName() + "\n" + 
                    "address: " + custbill.getAddress() + "\n" + 
                    "address2: " + custbill.getAddress2() + "\n" + 
                    "city: " + custbill.getCity() + "\n" + 
                    "state: " + custbill.getState() + "\n" + 
                    "zip: " + custbill.getZip() + "\n" + 
                    "country: " + custbill.getCountry() + "\n" + 
                    "phone: " + custbill.getPhone() + "\n" + 
                    "faxnum_inv: " + custbill.getFaxnum_inv() + "\n" + 
                    "audit_date: " + custbill.getAudit_date() + "\n" + 
                    "audit_time: " + custbill.getAudit_time() + "\n" + 
                    "audit_userid : " + custbill.getAudit_userid () + "\n" +
                    "changed_online : " + custbill.getChanged_online() + "\n"
                                );
        }
	}

	// -------------------------------------------------------------------------------------------------------------

	public static ArrayList<Custbill> getCustbillsForGivenCustNos(DBConnector dbConnector, HashSet<String> hsDistinctCustNums) throws PortalException {
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

		String queryString = "select " + getQueryFieldSelectionString() + " from pub.custbill " +
					 		 "where cust_num in (" + custNosString + ") " +
							 "  and ((active = '1'				) or    " +	
							 "       (active IS NULL            )    )  " ;
		
		ArrayList<Custbill> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

	// -------------------------------------------------------------------------------------------------------------

	public static ArrayList<Custbill> getCustbillssForGivenZip(DBConnector dbConnector, String zip) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.custbill " +
					 		 "where zip = '" + zip + "' " +
							 "  and ((active = '1'				) or   " +	
							 "       (active IS NULL            )    ) " +	
					 		 "order by billto_num";
		ArrayList<Custbill> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

	// -------------------------------------------------------------------------------------------------------------

	public static ArrayList<Custbill> getCustbillsForGivenCustNoContNo(DBConnector dbConnector, String custNum, int cont_no) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.custbill " +
					 		 "where cust_num = '" + custNum + "' " +
							 "  and ((active = '1'				 ) or   " +	
							 "       (active IS NULL             )    ) " +	
					 		 "  and ((cont_no = '" + cont_no + "') or   " +	
					 		 "       (cont_no = '0'              ) or   " +				
					 		 "       (cont_no IS NULL            )    ) " +				
					 		 "order by billto_num";
		ArrayList<Custbill> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

	public static ArrayList<Custbill> getCustbillsForGivenCustNo(DBConnector dbConnector, String custNum) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.custbill " +
					 		 "where cust_num = '" + custNum + "' " +
							 "  and ((active = '1'				) or   " +	
							 "       (active IS NULL            )    ) " +	
					 		 "order by billto_num";
		ArrayList<Custbill> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

	public static ArrayList<Custbill> getCustbillsForGivenCustNoAndBillToNum(DBConnector dbConnector, String custNum, String billto_num) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.custbill " +
					 		 "where cust_num = '" + custNum + "' " +
					 		 "  and ((active = '1'				) or   " +	
							 "       (active IS NULL            )    ) " +					
					 		 "  and billto_num = '" + billto_num + "'";
		ArrayList<Custbill> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

	// -------------------------------------------------------------------------------------------------------------
	
	public static void inactivate(DBConnector dbConnector, String custNum, String billto_num) throws PortalException {
		String updateQueryString = 
				"update pub.custbill " +
				"set active = '0' " +
				"where cust_num = '" + custNum + "' " +
		 		"  and ((active = '1'				) or   " +	
				"       (active IS NULL            )    ) " +					
		 		"  and billto_num = '" + billto_num + "'";

		log.debug(updateQueryString);

		JDBCUtils.executeUpdateWDS(dbConnector, updateQueryString);
	}	
	
// -------------------------------------------------------------------------------------------------------------
	
	public static String getNextBilltoNumForGivenCustNo(DBConnector dbConnector, String custNum) throws PortalException {
		/*
		String queryString = "select MAX(billto_num) as max_billto_num from pub.custbill " +
		 		 "where cust_num = '" + custNum + "' ";
		*/
		
		String queryString = "select MAX(cast(billto_num as integer)) as max_billto_num from pub.custbill " +
							 " where cust_num = '" + custNum + "' " +
							 "   and billto_num >= '0' " +
							 "   and billto_num <= '9999'";		
		
		Statement stmt = null;
		ResultSet rs = null;
		String returnValue;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

            if (rs.next()) {
            	String max_billto_num = JDBCUtils.getStringFromResultSet(rs, "max_billto_num");
            	if (StringUtils.isBlank(max_billto_num)) {
            		returnValue = "1";
            	} else {
            		returnValue = (new Integer(max_billto_num).intValue() + 1) + "";
            	}
            } else {
            	returnValue = "1";
			}

			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
			
			return returnValue;
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

	}	

	public void persist(DBConnector dbConnector) throws PortalException {
    	if (StringUtils.isBlank(billto_num) == false) {
    		inactivate(dbConnector, cust_num, billto_num);
    	}

		billto_num = Custbill.getNextBilltoNumForGivenCustNo(dbConnector, cust_num);
		
		System.out.println("billto_num: " + billto_num);
		
		/*
		String insertQueryString = 
				"insert into pub.custbill " +
				"(cust_num, billto_num, name, address, address2, city, state, zip, country, phone, faxnum_inv, audit_date, audit_time, audit_userid, " + 
				 "   phone, fax_num, modem_num, contact, currency, use_for_stmt, misc_alpha1, misc_alpha2, misc_num1, misc_num2, misc_log1, misc_log2) " +
				"values 	(" +
					"'" + cust_num + "', " +
					"'" + billto_num + "', " +
					"'" + name + "', " +
					"'" + address + "', " +
					"'" + address2 + "', " +
					"'" + city + "', " +
					"'" + state + "', " +
					"'" + zip + "', " +
					"'" + country + "', " +
					"'" + phone + "', " +
					"'" + faxnum_inv + "', " +
					"'" + audit_date + "', " +
					"'" + audit_time + "', " +
					"'" + audit_userid + "', " +
					"'',    '',      '',        '',      '',       '1',          '',          '',          0,         0,         '0',       '0'" +
				"			)";
		*/

		String insertQueryString = 
				"insert into pub.custbill " +
				"(cust_num, billto_num, cont_no, name, address, address2, city, state, zip, country, phone, faxnum_inv, audit_date, audit_time, audit_userid, changed_online) " +
				"values 	(" +
					"'" + cust_num + "', " +
					"'" + billto_num + "', " +
					"'" + cont_no + "', " +
					
					// "'" + name + "', " +
					"'" + Utils.replaceSingleQuotesIfNotNull(name) + "', " +
					
					// "'" + address + "', " +
					"'" + Utils.replaceSingleQuotesIfNotNull(address) + "', " +
					
					"'" + address2 + "', " +
					"'" + city + "', " +
					"'" + state + "', " +
					"'" + zip + "', " +
					"'" + country + "', " +
					"'" + phone + "', " +
					"'" + faxnum_inv + "', " +
					"'" + audit_date + "', " +
					"'" + audit_time + "', " +
					"'" + audit_userid + "', " +
					"'" + (changed_online?"1":"0") + "'" +
				"			)";
		log.debug(insertQueryString);

		JDBCUtils.executeUpdateWDS(dbConnector, insertQueryString);
	}

	// -------------------------------------------------------------------------------------------------------------

    public String getCust_num() {
        return cust_num;
    }
    public void setCust_num(String cust_num) {
        this.cust_num = cust_num;
    }
    public String getBillto_num() {
        return billto_num;
    }
    public void setBillto_num(String billto_num) {
        this.billto_num = billto_num;
    }
    public int getCont_no() {
        return cont_no;
    }
    public void setCont_no(int cont_no) {
        this.cont_no = cont_no;
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
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getFaxnum_inv() {
        return faxnum_inv;
    }
    public void setFaxnum_inv(String faxnum_inv) {
        this.faxnum_inv = faxnum_inv;
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
    public String getAudit_userid () {
        return audit_userid ;
    }
    public void setAudit_userid (String audit_userid ) {
        this.audit_userid  = audit_userid ;
    }
    public boolean getChanged_online () {
        return changed_online ;
    }
    public void setChanged_online (boolean changed_online ) {
        this.changed_online  = changed_online ;
    }
}
