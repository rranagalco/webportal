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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Dashboard {
	private static Logger log = Logger.getLogger(Dashboard.class);

    String cust_num;
    int cont_no;
    String name;
    String co_name_f;
    String co_name_l;
    String e_mail_address;
    String terms;
    String hasWebAccount;
    String order_num;
    boolean on_cr_hold;
    boolean on_hold;    

    /*
    private static String getQueryFieldSelectionString() {
        return "cust_num, cont_no,name,co_name_f,co_name_l,e_mail_address,terms,hasWebAccount";
    }
    */
    
	public static ArrayList<Dashboard> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString, String order_num) throws PortalException {
        ArrayList<Dashboard> al = new ArrayList<Dashboard>();

        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
                Dashboard dashboard = new Dashboard();

                String cust_num;
                int cont_no;
                
                cust_num = JDBCUtils.getStringFromResultSet(rs, "cust_num");
                dashboard.setCust_num(cust_num);
                cont_no = rs.getInt("cont_no");
                dashboard.setCont_no(cont_no);
                dashboard.setName(JDBCUtils.getStringFromResultSet(rs, "name"));
                dashboard.setCo_name_f(JDBCUtils.getStringFromResultSet(rs, "co_name_f"));
                dashboard.setCo_name_l(JDBCUtils.getStringFromResultSet(rs, "co_name_l"));
                dashboard.setE_mail_address(JDBCUtils.getStringFromResultSet(rs, "e_mail_address"));
                dashboard.setTerms(JDBCUtils.getStringFromResultSet(rs, "terms"));
                
                if (StringUtils.isBlank(order_num) == false) {
                    dashboard.setOrder_num(JDBCUtils.getStringFromResultSet(rs, "order_num"));
                    dashboard.setOn_cr_hold(rs.getBoolean("on_cr_hold"));
                    dashboard.setOn_hold(rs.getBoolean("on_hold"));
                } else {
                    dashboard.setOrder_num("");
                    dashboard.setOn_cr_hold(false);
                    dashboard.setOn_hold(false);                	
                }
                
    			ArrayList<Spolicy> spolicyAL = Spolicy.getSpolicyRecordForTheGivenCustContact(dbConnector, cust_num, cont_no);
    			if ((spolicyAL != null) && (spolicyAL.size() > 0)) {
                    dashboard.setHasWebAccount("Yes");    				
    			} else {
                    dashboard.setHasWebAccount("No");    				
    			}

                al.add(dashboard);
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
    public static void printDAOObjects(ArrayList<Dashboard> al) {
        if (al == null) {
            return;
        }
        
        for (Iterator<Dashboard> iterator = al.iterator(); iterator.hasNext();) {
            Dashboard dashboard = iterator.next();
            System.out.println  (
                    "cust_num      : " + dashboard.getCust_num() + "\n" +
                    "cont_no       : " + dashboard.getCont_no() + "\n" +
                    "name          : " + dashboard.getName() + "\n" + 
                    "co_name_f     : " + dashboard.getCo_name_f() + "\n" + 
                    "co_name_l     : " + dashboard.getCo_name_l() + "\n" + 
                    "e_mail_address: " + dashboard.getE_mail_address() + "\n" + 
                    "terms         : " + dashboard.getTerms() + "\n" + 
                    "hasWebAccount : " + dashboard.getHasWebAccount() + "\n"
                                );
        }
    }
       
    // -------------------------------------------------------------------------------------------------------------

    /*
    public static ArrayList<Dashboard> getDashboard(DBConnector dbConnector, String yyy) throws PortalException {
        String queryString = "select cust.cust_num, contact.cont_no, cust.name, contact.cont_no, contact.co_name_f, contact.co_name_l, contact.e_mail_address, cust.terms from pub.dashboard " +
                             "where yyy = '" + yyy + "'";
        ArrayList<Dashboard> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
        printDAOObjects(al);
        return al;
    }
    */

    public static ArrayList<Dashboard> getDashboardData(DBConnector dbConnector, String cust_num, String name, String co_name_f, String co_name_l, String e_mail_address, String order_num) throws PortalException {
    	String whereClause;
    	if ((order_num != null) && (order_num.startsWith("A") || (order_num.startsWith("W")))) {
    		whereClause = "order.order_num like '" + order_num + "%' and " +
		 			      "order.cust_num = cust.cust_num and " + 
					      "cust.cust_num = contact.pros_cd and " + 
					      "order.cont_no = contact.cont_no ";
    	} else {
    		whereClause = "order.order_num like '%" + order_num + "%' and " +
		 			      "order.cust_num = cust.cust_num and " + 
					      "cust.cust_num = contact.pros_cd and " + 
					      "order.cont_no = contact.cont_no ";
    	}
    	
    	if (StringUtils.isBlank(cust_num) == false) {
    		if (whereClause == null) {
    			whereClause = "cust.cust_num like '%" + cust_num + "%'";
    		} else {
    			whereClause += " and cust.cust_num like '%" + cust_num + "%'";
    		}
    	}
    			
    	if (StringUtils.isBlank(name) == false) {
    		if (whereClause == null) {
    			whereClause = "cust.name like '%" + name + "%'";
    		} else {
    			whereClause += " and cust.name like '%" + name + "%'";
    		}
    	}
    			
    	if (StringUtils.isBlank(co_name_f) == false) {
    		if (whereClause == null) {
    			whereClause = "contact.co_name_f like '%" + co_name_f + "%'";
    		} else {
    			whereClause += " and contact.co_name_f like '%" + co_name_f + "%'";
    		}
    	}
    			
    	if (StringUtils.isBlank(co_name_l) == false) {
    		if (whereClause == null) {
    			whereClause = "contact.co_name_l like '%" + co_name_l + "%'";
    		} else {
    			whereClause += " and contact.co_name_l like '%" + co_name_l + "%'";
    		}
    	}
    			
    	if (StringUtils.isBlank(e_mail_address) == false) {
    		if (whereClause == null) {
    			whereClause = "contact.e_mail_address like '%" + e_mail_address + "%'";
    		} else {
    			whereClause += " and contact.e_mail_address like '%" + e_mail_address + "%'";
    		}
    	}
    	    	
    	String queryString = "select top 20 cust.cust_num, cust.name, contact.cont_no, contact.co_name_f, contact.co_name_l, contact.e_mail_address, cust.terms, order.order_num, order.on_cr_hold, order.on_hold from pub.cust, pub.contact, pub.order " +
    						  " where " + whereClause;
    	
    	log.debug("queryString:" + queryString);

        ArrayList<Dashboard> al = buildDAOObjectsFromResultSet(dbConnector, queryString, order_num);
        // printDAOObjects(al);
        return al;
    }

    public static ArrayList<Dashboard> getDashboardData(DBConnector dbConnector, String cust_num, String name, String co_name_f, String co_name_l, String e_mail_address) throws PortalException {
    	String whereClause = "cust.cust_num = contact.pros_cd";
    	
    	if (StringUtils.isBlank(cust_num) == false) {
    		if (whereClause == null) {
    			whereClause = "cust.cust_num like '%" + cust_num + "%'";
    		} else {
    			whereClause += " and cust.cust_num like '%" + cust_num + "%'";
    		}
    	}
    			
    	if (StringUtils.isBlank(name) == false) {
    		if (whereClause == null) {
    			whereClause = "cust.name like '%" + name + "%'";
    		} else {
    			whereClause += " and cust.name like '%" + name + "%'";
    		}
    	}
    			
    	if (StringUtils.isBlank(co_name_f) == false) {
    		if (whereClause == null) {
    			whereClause = "contact.co_name_f like '%" + co_name_f + "%'";
    		} else {
    			whereClause += " and contact.co_name_f like '%" + co_name_f + "%'";
    		}
    	}
    			
    	if (StringUtils.isBlank(co_name_l) == false) {
    		if (whereClause == null) {
    			whereClause = "contact.co_name_l like '%" + co_name_l + "%'";
    		} else {
    			whereClause += " and contact.co_name_l like '%" + co_name_l + "%'";
    		}
    	}
    			
    	if (StringUtils.isBlank(e_mail_address) == false) {
    		if (whereClause == null) {
    			whereClause = "contact.e_mail_address like '%" + e_mail_address + "%'";
    		} else {
    			whereClause += " and contact.e_mail_address like '%" + e_mail_address + "%'";
    		}
    	}
    	
    	String queryString = "select top 20 cust.cust_num, cust.name, contact.cont_no, contact.co_name_f, contact.co_name_l, contact.e_mail_address, cust.terms from pub.cust, pub.contact " +
    						  " where " + whereClause;
    	
    	log.debug("queryString:" + queryString);

        ArrayList<Dashboard> al = buildDAOObjectsFromResultSet(dbConnector, queryString, null);
        // printDAOObjects(al);
        return al;
    }

    // -------------------------------------------------------------------------------------------------------------

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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCo_name_f() {
        return co_name_f;
    }
    public void setCo_name_f(String co_name_f) {
        this.co_name_f = co_name_f;
    }
    public String getCo_name_l() {
        return co_name_l;
    }
    public void setCo_name_l(String co_name_l) {
        this.co_name_l = co_name_l;
    }
    public String getE_mail_address() {
        return e_mail_address;
    }
    public void setE_mail_address(String e_mail_address) {
        this.e_mail_address = e_mail_address;
    }
    public String getTerms() {
        return terms;
    }
    public void setTerms(String terms) {
        this.terms = terms;
    }
    public String getHasWebAccount() {
        return hasWebAccount;
    }
    public void setHasWebAccount(String hasWebAccount) {
        this.hasWebAccount = hasWebAccount;
    }

    public String getOrder_num() {
		return order_num;
	}
	public void setOrder_num(String order_num) {
		this.order_num = order_num;
	}
	public boolean isOn_cr_hold() {
		return on_cr_hold;
	}
	public void setOn_cr_hold(boolean on_cr_hold) {
		this.on_cr_hold = on_cr_hold;
	}
	public boolean isOn_hold() {
		return on_hold;
	}
	public void setOn_hold(boolean on_hold) {
		this.on_hold = on_hold;
	}
}
