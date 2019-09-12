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

public class Custship implements Has_Cust_Num_Address_Address2_Zip {
	private static Logger log = Logger.getLogger(Custship.class);

    String cust_num;
    String shipto_num;
	int cont_no;    
    String name;
    String address;
    String address2;
    String city;
    String state;
    String zip;
    String country;
    String phone;
    String audit_date;
    String audit_time;
    String audit_userid;
    boolean changed_online;

	// -------------------------------------------------------------------------------------------------------------

	public Custship() {
		super();
	}
	public Custship(String cust_num, String shipto_num, int cont_no, String name, String address, String address2, String city, String state, String zip, String country, String phone, boolean changed_online) {
        this(cust_num, shipto_num, cont_no, name, address, address2, city, state, zip, country, phone, new SimpleDateFormat("yyyy/MM/dd").format(new Date()), new SimpleDateFormat("HH:mm:ss").format(new Date()), "WEB", changed_online);
	}
	
	public Custship(String cust_num, String shipto_num, int cont_no, String name, String address, String address2, String city, String state, String zip, String country, String phone, String audit_date, String audit_time, String audit_userid, boolean changed_online) {
		super();
		
	    this.cust_num = cust_num;
	    this.shipto_num = shipto_num;
	    this.cont_no = cont_no;	    
	    this.name = name;
	    this.address = address;
	    this.address2 = address2;
	    this.city = city;
	    this.state = state;
	    this.zip = zip;
	    this.country = country;
	    this.phone = phone;
	    this.audit_date = audit_date;
	    this.audit_time = audit_time;
	    this.audit_userid = audit_userid;
	    this.changed_online = changed_online;	    
	}

	// -------------------------------------------------------------------------------------------------------------

	private static String getQueryFieldSelectionString() {
        return "cust_num,shipto_num,cont_no,name,address,address2,city,state,zip,country,phone,audit_date,audit_time,audit_userid,changed_online ";
	}

	public static ArrayList<Custship> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
		ArrayList<Custship> al = new ArrayList<Custship>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

			while (rs.next()) {
				Custship custship = new Custship();

				custship.setCust_num(JDBCUtils.getStringFromResultSet(rs, "cust_num"));
				custship.setShipto_num(JDBCUtils.getStringFromResultSet(rs, "shipto_num"));
				custship.setCont_no(rs.getInt("cont_no"));				
				custship.setName(JDBCUtils.getStringFromResultSet(rs, "name"));
				custship.setAddress(JDBCUtils.getStringFromResultSet(rs, "address"));
				custship.setAddress2(JDBCUtils.getStringFromResultSet(rs, "address2"));
				custship.setCity(JDBCUtils.getStringFromResultSet(rs, "city"));
				custship.setState(JDBCUtils.getStringFromResultSet(rs, "state"));
				custship.setZip(JDBCUtils.getStringFromResultSet(rs, "zip"));
				custship.setCountry(JDBCUtils.getStringFromResultSet(rs, "country"));
				custship.setPhone(JDBCUtils.getStringFromResultSet(rs, "phone"));
				custship.setAudit_date(JDBCUtils.getStringFromResultSet(rs, "audit_date"));
				custship.setAudit_time(JDBCUtils.getStringFromResultSet(rs, "audit_time"));
				custship.setAudit_userid (JDBCUtils.getStringFromResultSet(rs, "audit_userid"));
				custship.setChanged_online(rs.getBoolean("changed_online"));				

				al.add(custship);
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

	public static void printDAOObjects(ArrayList<Custship> al) {
		if (al == null) {
			return;
		}

		for (Iterator<Custship> iterator = al.iterator(); iterator.hasNext();) {
			Custship custship = iterator.next();

            System.out.println  (
                    "cust_num: " + custship.getCust_num() + "\n" + 
                    "shipto_num: " + custship.getShipto_num() + "\n" + 
                    "cont_no: " + custship.getCont_no() + "\n" +                    		
                    "name: " + custship.getName() + "\n" + 
                    "address: " + custship.getAddress() + "\n" + 
                    "address2: " + custship.getAddress2() + "\n" + 
                    "city: " + custship.getCity() + "\n" + 
                    "state: " + custship.getState() + "\n" + 
                    "zip: " + custship.getZip() + "\n" + 
                    "country: " + custship.getCountry() + "\n" + 
                    "phone: " + custship.getPhone() + "\n" + 
                    "audit_date: " + custship.getAudit_date() + "\n" + 
                    "audit_time: " + custship.getAudit_time() + "\n" + 
                    "audit_userid : " + custship.getAudit_userid () + "\n" +
                    "changed_online : " + custship.getChanged_online() + "\n"
                                );
        }
	}

	// -------------------------------------------------------------------------------------------------------------

	public static ArrayList<Custship> getCustshipsForGivenCustNos(DBConnector dbConnector, HashSet<String> hsDistinctCustNums) throws PortalException {
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

		String queryString = "select " + getQueryFieldSelectionString() + " from pub.custship " +
					 		 "where cust_num in (" + custNosString + ") " +
							 "  and ((active = '1'				) or    " +	
							 "       (active IS NULL            )    )  " ;
		ArrayList<Custship> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

	// -------------------------------------------------------------------------------------------------------------

	public static ArrayList<Custship> getCustshipsForGivenCustNoContNo(DBConnector dbConnector, String custNum, int cont_no) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.custship " +
					 		 "where cust_num = '" + custNum + "' " +
							 "  and ((active = '1'				 ) or   " +	
							 "       (active IS NULL             )    ) " +	
					 		 "  and ((cont_no = '" + cont_no + "') or   " +	
					 		 "       (cont_no = '0'              ) or   " +				
					 		 "       (cont_no IS NULL            )    ) " +				
					 		 "order by shipto_num";
		ArrayList<Custship> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}
	
	public static ArrayList<Custship> getCustshipsForGivenCustNo(DBConnector dbConnector, String custNum) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.custship " +
					 		 "where cust_num = '" + custNum + "'       " +
							 "  and ((active = '1'				) or   " +	
							 "       (active IS NULL            )    ) " +				
					 		 "order by shipto_num";
		ArrayList<Custship> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

	public static ArrayList<Custship> getCustshipsForGivenCustNoAndShipToNum(DBConnector dbConnector, String custNum, String shipto_num) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.custship " +
					 		 "where cust_num = '" + custNum + "' " +
					 		 "  and ((active = '1'				) or   " +	
							 "       (active IS NULL            )    ) " +					
					 		 "  and shipto_num = '" + shipto_num + "'";
		ArrayList<Custship> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}
	
	// -------------------------------------------------------------------------------------------------------------

	public static ArrayList<Custship> getCustshipsForGivenZip(DBConnector dbConnector, String zip) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.custship " +
					 		 "where zip = '" + zip + "' " +
							 "  and ((active = '1'				) or   " +	
							 "       (active IS NULL            )    ) " +
					 		 "order by shipto_num";
		ArrayList<Custship> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}
	
	// -------------------------------------------------------------------------------------------------------------

	public static void inactivate(DBConnector dbConnector, String custNum, String shipto_num) throws PortalException {
		String updateQueryString = 
				"update pub.custship " +
				"set active = '0' " +
				"where cust_num = '" + custNum + "' " +
		 		"  and ((active = '1'				) or   " +	
				"       (active IS NULL            )    ) " +					
		 		"  and shipto_num = '" + shipto_num + "'";

		log.debug(updateQueryString);

		JDBCUtils.executeUpdateWDS(dbConnector, updateQueryString);
	}
	
	// -------------------------------------------------------------------------------------------------------------
	
	public static String getNextShiptoNumForGivenCustNo(DBConnector dbConnector, String custNum) throws PortalException {
		/*
		String queryString = "select MAX(shipto_num) as max_shipto_num from pub.custship " +
		 		 "where cust_num = '" + custNum + "' ";
		*/
		
		String queryString = "select MAX(cast(shipto_num as integer)) as max_shipto_num from pub.custship " +
		 		 			 " where cust_num = '" + custNum + "' " +
		 		 			 "   and shipto_num >= '0' " +
		 		 			 "   and shipto_num <= '9999'";

		Statement stmt = null;
		ResultSet rs = null;
		String returnValue;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

            if (rs.next()) {
            	String max_shipto_num = JDBCUtils.getStringFromResultSet(rs, "max_shipto_num");
            	if (StringUtils.isBlank(max_shipto_num)) {
            		returnValue = "1";
            	} else {
            		returnValue = (new Integer(max_shipto_num).intValue() + 1) + "";
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
    	if (StringUtils.isBlank(shipto_num) == false) {
    		inactivate(dbConnector, cust_num, shipto_num);
    	}

    	shipto_num = Custship.getNextShiptoNumForGivenCustNo(dbConnector, cust_num);
		
		System.out.println("shipto_num: " + shipto_num);

		
		
		
		String insertQueryString = 
				"insert into pub.custship " +
				"(cust_num, shipto_num, cont_no, name, address, address2, city, state, zip, country, phone, audit_date, audit_time, audit_userid, changed_online) " +
				"values 	(" +
					"'" + cust_num + "', " +
					"'" + shipto_num + "', " +
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
					"'" + audit_date + "', " +
					"'" + audit_time + "', " +
					"'" + audit_userid + "', " +
					"'" + (changed_online?"1":"0") + "'" +
				"			)";
		log.debug(insertQueryString);
		
		log.debug("ShipTo Insert Query String: " + insertQueryString);

		JDBCUtils.executeUpdateWDS(dbConnector, insertQueryString);
	}	

	// -------------------------------------------------------------------------------------------------------------

    public String getCust_num() {
        return cust_num;
    }
    public void setCust_num(String cust_num) {
        this.cust_num = cust_num;
    }
    public String getShipto_num() {
        return shipto_num;
    }
    public void setShipto_num(String shipto_num) {
        this.shipto_num = shipto_num;
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

