package galco.portal.wds.dao;

import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Order {
	private static Logger log = Logger.getLogger(Order.class);

	String cust_num;
    String order_date;
    String order_num;
    String proc_stage;
    String pmt_type;
    String cc_num;
	String cust_ponum;
    String ordered_by;
    String ship_loc;

    String order_type;
    String backorder;
    String date_closed;
    String misc_date2;
    String expire_date;
    String sales_rep;
    String sales_rep2;

    String orderStatus;

    // -------------------------------------------------------------------------------------------------------------

	public Order() {
		// TODO Auto-generated constructor stub
	}

	public Order(String cust_num, String order_date, String order_num, String proc_stage,
		         String pmt_type, String cc_num, String cust_ponum, String ordered_by, String ship_loc,
		         String order_type, String  backorder, String date_closed, String misc_date2, String expire_date, String sales_rep, String sales_rep2) {
		super();
		this.cust_num = cust_num;
		this.order_date = order_date;
		this.order_num = order_num;
		this.proc_stage = proc_stage;
		this.pmt_type = pmt_type;
		this.cc_num = cc_num;
		this.cust_ponum = cust_ponum;
		this.ordered_by = ordered_by;
		this.ship_loc = ship_loc;
		this.order_type = order_type;
		this.backorder = backorder;
		this.date_closed = date_closed;
		this.misc_date2 = misc_date2;
		this.expire_date = expire_date;
		this.sales_rep = sales_rep;
		this.sales_rep2 = sales_rep2;
	}

    // -------------------------------------------------------------------------------------------------------------

	private static String getQueryFieldSelectionString() {
        return "cust_num, order_date, order_num, proc_stage, pmt_type, cc_num, substr(cust_ponum, 1, 20) as cust_ponum_mod, ordered_by, ship_loc, order_type, backorder, date_closed, misc_date2, expire_date, sales_rep, sales_rep2";
    }

	// ddd
    public static ArrayList<Order> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Order> al = new ArrayList<Order>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

			while (rs.next()) {
			    Order order = new Order();

			    order.setCust_num(JDBCUtils.getStringFromResultSet(rs, "cust_num"));
			    order.setOrder_date(JDBCUtils.getStringFromResultSet(rs, "order_date"));
			    order.setOrder_num(JDBCUtils.getStringFromResultSet(rs, "order_num"));
			    order.setProc_stage(JDBCUtils.getStringFromResultSet(rs, "proc_stage"));
			    order.setPmt_type(JDBCUtils.getStringFromResultSet(rs, "pmt_type"));
			    order.setCc_num(JDBCUtils.getStringFromResultSet(rs, "cc_num"));
			    order.setCust_ponum(JDBCUtils.getStringFromResultSet(rs, "cust_ponum_mod"));
			    order.setOrdered_by(JDBCUtils.getStringFromResultSet(rs, "ordered_by"));
			    order.setShip_loc(JDBCUtils.getStringFromResultSet(rs, "ship_loc"));
			    order.setOrder_type(JDBCUtils.getStringFromResultSet(rs, "order_type"));
			    order.setBackorder(JDBCUtils.getStringFromResultSet(rs, "backorder"));

			    String dateClosedTemp = JDBCUtils.getStringFromResultSet(rs, "date_closed");
			    order.setDate_closed((dateClosedTemp != null)?dateClosedTemp:"");

			    order.setMisc_date2(JDBCUtils.getStringFromResultSet(rs, "misc_date2"));
			    order.setExpire_date(JDBCUtils.getStringFromResultSet(rs, "expire_date"));

			    order.setSales_rep(JDBCUtils.getStringFromResultSet(rs, "sales_rep"));
			    order.setSales_rep2(JDBCUtils.getStringFromResultSet(rs, "sales_rep2"));

			    order.calculateOrderStatus(dbConnector);

			    al.add(order);
			}

			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		} catch (SQLException e) {
			log.error("SQLException occurred. Query String: " + queryString);
			
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

    public static ArrayList<Order> buildDAOObjectsFromResultSetFNB(DBConnector dbConnector, String queryString, String cust_num, int cont_no, boolean getQuotes, String leThisDate, String ordersStrictlyBelowThisNum, int noOfRecs) throws PortalException {
        ArrayList<Order> al = new ArrayList<Order>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);
            
            if (rs == null) {
            	return al;
            }

			while (rs.next()) {
			    Order order = new Order();

			    order.setCust_num(JDBCUtils.getStringFromResultSet(rs, "cust_num"));
			    order.setOrder_date(JDBCUtils.getStringFromResultSet(rs, "order_date"));
			    order.setOrder_num(JDBCUtils.getStringFromResultSet(rs, "order_num"));
			    order.setProc_stage(JDBCUtils.getStringFromResultSet(rs, "proc_stage"));
			    order.setPmt_type(JDBCUtils.getStringFromResultSet(rs, "pmt_type"));
			    order.setCc_num(JDBCUtils.getStringFromResultSet(rs, "cc_num"));
			    order.setCust_ponum(JDBCUtils.getStringFromResultSet(rs, "cust_ponum_mod"));
			    order.setOrdered_by(JDBCUtils.getStringFromResultSet(rs, "ordered_by"));
			    order.setShip_loc(JDBCUtils.getStringFromResultSet(rs, "ship_loc"));
			    order.setOrder_type(JDBCUtils.getStringFromResultSet(rs, "order_type"));
			    order.setBackorder(JDBCUtils.getStringFromResultSet(rs, "backorder"));

			    String dateClosedTemp = JDBCUtils.getStringFromResultSet(rs, "date_closed");
			    order.setDate_closed((dateClosedTemp != null)?dateClosedTemp:"");

			    order.setMisc_date2(JDBCUtils.getStringFromResultSet(rs, "misc_date2"));
			    order.setExpire_date(JDBCUtils.getStringFromResultSet(rs, "expire_date"));

			    order.setSales_rep(JDBCUtils.getStringFromResultSet(rs, "sales_rep"));
			    order.setSales_rep2(JDBCUtils.getStringFromResultSet(rs, "sales_rep2"));

			    order.calculateOrderStatus(dbConnector);

			    al.add(order);
			}

			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		} catch (Exception e) {
			log.error("SQLException occurred. Query String: " + queryString);
			log.debug("cust_num                  : " + cust_num);
			log.debug("cont_no                   : " + cont_no);
			log.debug("getQuotes                 : " + getQuotes);
			log.debug("leThisDate                : " + leThisDate );
			log.debug("ordersStrictlyBelowThisNum: " + ordersStrictlyBelowThisNum);
			log.debug("noOfRecs                  : " + noOfRecs);			
			
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

    public static void printDAOObjects(ArrayList<Order> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Order> iterator = al.iterator(); iterator.hasNext();) {
        	Order order = iterator.next();

			log.debug("cust_num    : " + order.getCust_num());
			log.debug("order_date  : " + order.getOrder_date());
			log.debug("order_num   : " + order.getOrder_num());
			log.debug("proc_stage  : " + order.getProc_stage());
			log.debug("pmt_type    : " + order.getPmt_type());
			log.debug("cc_num      : " + order.getCc_num());
			log.debug("cust_ponum  : " + order.getCust_ponum());
			log.debug("ordered_by  : " + order.getOrdered_by());
			log.debug("ship_loc    : " + order.getShip_loc());
			log.debug("order_type  : " + order.getOrder_type());
			log.debug("backorder   : " + order.getBackorder());
			log.debug("date_closed : " + order.getDate_closed());
			log.debug("misc_date2  : " + order.getMisc_date2());
			log.debug("expire_date : " + order.getExpire_date());
			log.debug("sales_rep   : " + order.getSales_rep());
			log.debug("sales_rep2  : " + order.getSales_rep2());
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Order> getOrder(DBConnector dbConnector, String order_num) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.order " +
							 "where order_num = '" + order_num + "'";
		ArrayList<Order> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

    // -------------------------------------------------------------------------------------------------------------
    
    public static String getCustomerWithMostRecentOrder(DBConnector dbConnector, HashSet<String> hsDistinctCustNums) throws PortalException {
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

		String queryString = "select " + getQueryFieldSelectionString() + " from pub.order " + 
							 " where cust_num in (" + custNosString + ") " +
							 "   and order.order_date in (select max(order_date) as max_date from pub.order " +
							 							 " where cust_num in (" + custNosString + "))";
		
		System.out.println(queryString);
		
		ArrayList<Order> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		
		
		if ((al != null) && (al.size() > 0)) {
			return al.get(0).getCust_num();
		} else {
			return null;
		}
	}
    
    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Order> fetchNextBatchOfOrders(DBConnector dbConnector, String cust_num, int cont_no, boolean getQuotes, String leThisDate, String ordersStrictlyBelowThisNum, int noOfRecs) throws PortalException {
    	if (StringUtils.isBlank(leThisDate)) {
    		leThisDate = "2099-01-01";
    	}
    	if (ordersStrictlyBelowThisNum == null) {
    		ordersStrictlyBelowThisNum = "ZZZZZZZ";
    	}

    	// log.debug("fetchNextBatchOfOrders cust_num                   " + cust_num);
    	// log.debug("fetchNextBatchOfOrders cont_no                    " + cont_no);
    	// log.debug("fetchNextBatchOfOrders leThisDate                 " + leThisDate);
    	// log.debug("fetchNextBatchOfOrders ordersStrictlyBelowThisNum " + ordersStrictlyBelowThisNum);
    	// log.debug("fetchNextBatchOfOrders getQuotes                  " + getQuotes);

    	String queryString = "select top " + noOfRecs + " " + getQueryFieldSelectionString() + " from pub.order " +
				   "where cust_num = '" + cust_num + "' " +
				   "  and cont_no = '" + cont_no + "' " +
				   "  and order_type = '" + (getQuotes?"Q":"S") + "' " +

				   "  and (  (order_date < '" + leThisDate + "') " +
				   "         or " +
				   "         (  (order_date = '" + leThisDate + "') " +
				   "            and " +
				   "            (order_num < '" + ordersStrictlyBelowThisNum + "') " +
				   "         ) " +
				   "      ) " +

				   "order by order_date desc, order_num desc";
    	
    	/* 
    	if (cust_num.compareToIgnoreCase("150483") == 0) {
        	queryString = "select top " + noOfRecs + " " + getQueryFieldSelectionString() + " from pub.order " +
  				   // "where order_num in ('216083', '260914', '280734', '290327', '297491', '299659', '323404', '450862', '501801', '504752', '552387', '586914', '595901', '620348', '650176', '662067', '705236', '836780', 'A009464', 'A318829', 'A491945')";    		
  				   "where order_num in ('A104678', 'A104921', 'A105104', 'A105380', 'A105638', 'A106901', 'A108451', 'A110505', 'A117326', 'A226196', 'A318829', 'A491945', 'A527104', 'A787621', 'W743836', 'W743924', 'W743967', 'W744010', 'W744063', 'W744910', 'W788559', 'W845675')";    		        	
        }
        */

    	// log.debug(queryString + "\n\n\n");

		ArrayList<Order> al = buildDAOObjectsFromResultSetFNB(dbConnector, queryString, cust_num, cont_no, getQuotes, leThisDate, ordersStrictlyBelowThisNum, noOfRecs);
		// printDAOObjects(al);

		if ((al != null) && (al.size() > 0)) {
			for (Iterator iterator = al.iterator(); iterator.hasNext();) {
				Order order = (Order) iterator.next();
				// log.debug(order.getOrder_date() + " " + order.getOrder_num());
			}
			// log.debug("");
		}

		return al;
	}

    public static ArrayList<Order> fetchNextBatchOfOrders_DidNotWork(DBConnector dbConnector, String cust_num, int cont_no, boolean getQuotes, String ordersStrictlyAboveThisNum, int noOfRecs) throws PortalException {
    	String queryString = "select top " + noOfRecs + " " + getQueryFieldSelectionString() + " from pub.order " +
				   "where cust_num = '" + cust_num + "' and " +
				   "cont_no = '" + cont_no + "' and " +

				   (((ordersStrictlyAboveThisNum != null) && (ordersStrictlyAboveThisNum.compareTo("") !=0))?
				    ("order_num < '" + ordersStrictlyAboveThisNum + "' and "):
				    ("")
				   ) +

				   "order_type = '" + (getQuotes?"Q":"S") + "' " +
				   "order by order_num DESC";

		ArrayList<Order> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);

		if ((al != null) && (al.size() > 0)) {
			for (Iterator iterator = al.iterator(); iterator.hasNext();) {
				Order order = (Order) iterator.next();
				// log.debug(order.getOrder_num());
			}
			// log.debug("");
		}

		return al;
	}

    public static ArrayList<Order> fetchNextBatchOfOrders_O1(DBConnector dbConnector, String cust_num, int cont_no, boolean getQuotes, String ordersStrictlyAboveThisNum, int noOfRecs) throws PortalException {
    	String queryString = "select top " + noOfRecs + " " + getQueryFieldSelectionString() + " from pub.order " +
				   "where cust_num = '" + cust_num + "' and " +

				   "cont_no = '" + cont_no + "' and " +

				   "order_type = '" + (getQuotes?"Q":"S") + "' and " +
				   		 "order_num > '" + ordersStrictlyAboveThisNum + "' " +
				   "order by order_num";

		ArrayList<Order> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);

		if ((al != null) && (al.size() > 0)) {
			for (Iterator iterator = al.iterator(); iterator.hasNext();) {
				Order order = (Order) iterator.next();
				// log.debug(order.getOrder_num());
			}
			// log.debug("");
		}

		return al;
	}

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Order> fetchNextBatchOfOrders_O2(DBConnector dbConnector, String cust_num, int cont_no, boolean getQuotes, String leThisDate, String ordersStrictlyBelowThisNum, int noOfRecs) throws PortalException {
    	if (leThisDate == null) {
    		leThisDate = "2099-01-01";
    	}
    	if (ordersStrictlyBelowThisNum == null) {
    		ordersStrictlyBelowThisNum = "ZZZZZZZ";
    	}


    	String queryString = "select top " + noOfRecs + " " + getQueryFieldSelectionString() + " from pub.order " +
				   "where cust_num = '" + cust_num + "' " +
				   "  and cont_no = '" + cont_no + "' " +
				   "  and order_type = '" + (getQuotes?"Q":"S") + "' " +



				   "  and (  (order_date < '" + leThisDate + "') " +
				   "         or " +
				   "         (  (order_date = '" + leThisDate + "') " +
				   "            and " +
				   "            (order_num < '" + ordersStrictlyBelowThisNum + "') " +
				   "         ) " +
				   "      ) " +



				   "order by order_date desc, order_num desc";

    	// log.debug(queryString + "\n\n\n");

		ArrayList<Order> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);

		if ((al != null) && (al.size() > 0)) {
			for (Iterator iterator = al.iterator(); iterator.hasNext();) {
				Order order = (Order) iterator.next();
				// log.debug(order.getOrder_date() + " " + order.getOrder_num());
			}
			// log.debug("");
		}

		return al;
	}

    public static void main(String[] args) {
    	try {
    		ArrayList<Order> al;
    		// al = ZZZ_fetchNextBatchOfOrders("005085", 12, false, null, null, 15);
    		// while ((al != null) && (al.size() > 0)) {
    			// al = ZZZ_fetchNextBatchOfOrders("005085", 12, false, al.get(al.size() - 1).getOrder_date(), al.get(al.size() - 1).getOrder_num(), 15);
    		// }

    		// Order.getOrder("A670821").get(0).getOitemDetails();
    		System.exit(0);


    		/*
    		String ordersStrictlyAboveThisNum = "";

    	    ArrayList<Order> al = fetchNextBatchOfOrders("000017", 26, false, ordersStrictlyAboveThisNum, 15);
    	    while (al != null) {
    	    	for (int i = 0; i < al.size(); i++) {
    	        	log.debug(al.get(i).getOrder_num() + " " + al.get(i).getOrdered_by());
				}

    	    	ordersStrictlyAboveThisNum = al.get(al.size() - 1).getOrder_num();
        	    al = fetchNextBatchOfOrders("000017", 26, false, ordersStrictlyAboveThisNum, 15);

        	    // break;
    	    }
    	    */
		} catch (Exception e) {
			log.error("Error occurred.", e);
		}
	}

    // -------------------------------------------------------------------------------------------------------------

    public static JSONObject getOitemDetails(HttpServletRequest request, DBConnector dbConnector, String order_num, String ship_loc) throws PortalException, JSONException {
    	ArrayList<Oitem> OitemAL = Oitem.getOitemsOfAnOrder(dbConnector, order_num);
    	// DecimalFormat decimalFormat = new DecimalFormat("###.##");
    	NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
		JSONArray jsonArray = new JSONArray();
		boolean lineItemsHaveAtLeastOneInvoice = false;
		
		
		
		DecimalFormat decimalFormat2 = new DecimalFormat("#.#####");
		// decimalFormat2.setRoundingMode(RoundingMode.CEILING);
		
		

		double totalPrice = 0, totalPriceFromOaddons = 0;

		if (OitemAL != null) {
	    	for (Iterator<Oitem> iterator = OitemAL.iterator(); iterator.hasNext();) {
				Oitem oitem = iterator.next();

				String partNum = "";
				String availability = "";
				String billing_unit = "";
				String unit_price, extendedPrice;
				int qtyShipped, qtyBackOrder, qtyOrdered;
				String shippedDate, carrier;
				String imageFilePath, oldImageFilePath;
				String subFamilyAndSeries;

				partNum = oitem.getPart_num();

				double qtyBal = 0, qtyAvail = 0;

				qtyBal = oitem.getQty_bal() - oitem.getQty_commit();
				qtyAvail = 0;

				ArrayList<Partloc> partlocAL = Partloc.getPartloc(dbConnector, oitem.getPart_num(), ship_loc);
			    if ((partlocAL != null) && (partlocAL.size() > 0)) {
			         qtyAvail = partlocAL.get(0).getQty_onhand() - partlocAL.get(0).getQty_commit() - partlocAL.get(0).getQty_backord();
			    }

				if (qtyBal <= qtyAvail) {
					availability = "In stock";
				} else if ((qtyAvail > 0) && (qtyBal < qtyAvail)) {
					availability = "Partial";
				} else if ((qtyAvail <= 0) && (qtyBal > 0)) {
					availability = "Back Order";
				} else {
					availability = "";
				}

				if (totalPrice != -1) {
					if (oitem.getUnit_price() >= 0) {
						if (oitem.getPriceper_qty() <= 0) {
							totalPrice = -1;
						} else {
							// log.debug("oitem.getQty_ord()      :" + oitem.getQty_ord());
							// log.debug("oitem.getQty_cancel()   :" + oitem.getQty_cancel());
							// log.debug("oitem.getUnit_price()   :" + oitem.getUnit_price());
							// log.debug("oitem.getPriceper_qty() :" + oitem.getPriceper_qty());

							totalPrice += ((oitem.getQty_ord() - oitem.getQty_cancel()		) *
									       (oitem.getUnit_price() / oitem.getPriceper_qty()	)   );
						}
					} else {
						totalPrice = -1;
					}
				}
				if (totalPrice != -1) {
					ArrayList<Oaddon> oaddonAL = Oaddon.getOaddon(dbConnector, oitem.getOrder_num(), oitem.getLine_num());
					if ((oaddonAL != null) && (oaddonAL.size() > 0)) {
						for (Iterator iterator2 = oaddonAL.iterator(); iterator2.hasNext();) {
							Oaddon oaddon = (Oaddon) iterator2.next();
							totalPrice += oaddon.getAdd_amt_bal();
							totalPriceFromOaddons += oaddon.getAdd_amt_bal();
						}
					}
				}

	            
				subFamilyAndSeries = "";
	            ArrayList<Part> partAL = Part.getPart(dbConnector, oitem.getPart_num());
	            if ((partAL == null) || (partAL.size() == 0)) {
	            	continue;
	            }
	            Part part = partAL.get(0);

	            
	            int pricePerQty = (oitem.getPriceper_qty() > 0)?oitem.getPriceper_qty():1;

				billing_unit = oitem.getBilling_unit();
				if (oitem.getUnit_price() > 0) {
					unit_price = currencyFormatter.format(oitem.getUnit_price());
		            extendedPrice = currencyFormatter.format((oitem.getUnit_price() / pricePerQty		) *
															 (oitem.getQty_ord() - oitem.getQty_cancel())   );
				} else {
					unit_price = "TBD";
					extendedPrice = "TBD";
				}


	            // qqq
				qtyOrdered = oitem.getQty_ord() - oitem.getQty_cancel();
	            qtyShipped = oitem.getQty_shipped();
	            qtyBackOrder = oitem.getQty_bal();					
				if (pricePerQty > 1) {
					billing_unit = part.getBilling_unit();

					if (oitem.getUnit_price() > 0) {
						// unit_price = currencyFormatter.format(oitem.getUnit_price() / pricePerQty);
						// unit_price = "$" + String.format("%10.5f", oitem.getUnit_price() / pricePerQty);
						unit_price = "$" + decimalFormat2.format(oitem.getUnit_price() / pricePerQty).trim();
					}
				}				
				/*
				qtyOrdered = oitem.getQty_ord();
	            qtyShipped = oitem.getQty_shipped();
	            qtyBackOrder = oitem.getQty_bal();					
				if (pricePerQty > 1) {
					billing_unit = part.getBilling_unit();

					if (oitem.getUnit_price() > 0) {
						// unit_price = currencyFormatter.format(oitem.getUnit_price() / pricePerQty);
						// unit_price = "$" + String.format("%10.5f", oitem.getUnit_price() / pricePerQty);
						unit_price = "$" + decimalFormat2.format(oitem.getUnit_price() / pricePerQty).trim();
						
						qtyOrdered = qtyOrdered * pricePerQty;
			            qtyShipped = qtyShipped * pricePerQty;
			            qtyBackOrder = qtyBackOrder * pricePerQty;						
					}					
				}
				*/
	            
	    		JSONArray shipmentsJsonArray = new JSONArray();
	            shippedDate = "";
	            carrier = "";
	            ArrayList<Shipitem> shipitemAL = Shipitem.getShipitem(dbConnector, oitem.getOrder_num(), oitem.getLine_num());
	            if ((shipitemAL != null) && (shipitemAL.size() > 0)) {
	            	for (Iterator iterator2 = shipitemAL.iterator(); iterator2.hasNext();) {
						Shipitem shipitem = (Shipitem) iterator2.next();
						String inv_num = shipitem.getInv_num();
						if (StringUtils.isBlank(inv_num)) {
							inv_num = "";
						} else {
							lineItemsHaveAtLeastOneInvoice = true;
						}

						ArrayList<Shippkg> shippkgAL = Shippkg.getShippkg(dbConnector, shipitem.getPkg_id_num());
		                if ((shippkgAL != null) && (shippkgAL.size() > 0)) {
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("shippedDate", shippkgAL.get(0).getShip_date());
							jsonObject.put("trakCtrlnum", shippkgAL.get(0).getTrak_ctrlnum());
							jsonObject.put("carrier", shippkgAL.get(0).getCarrier());
							jsonObject.put("inv_num", inv_num);
							// jsonObject.put("inv_num", (Math.random() < 0.5)?inv_num:"");
							shipmentsJsonArray.put(jsonObject);
		                }
					}
	            }

	            
				ArrayList<Codes> codesBU_AL = Codes.getCodesGiven_code_type_And_valid_vode(dbConnector, "BILL UNIT", billing_unit);
	            if ((codesBU_AL != null) && (codesBU_AL.size() > 0)) {
	            	billing_unit = StringUtils.capitalize(codesBU_AL.get(0).getDescription().trim().toLowerCase());
	            }				

	    		if (part.getFamily_code().compareTo("") != 0) {
	    			ArrayList<Ps_subfamily> ps_subfamilyAL = Ps_subfamily.getPs_subfamily(dbConnector, part.getFamily_code(), part.getSubfamily_code());
	    			if ((ps_subfamilyAL != null) && (ps_subfamilyAL.size() > 0)) {
	        			if (ps_subfamilyAL.get(0).getKeywords_plural().indexOf(",") >= 0) {
	        				subFamilyAndSeries = ps_subfamilyAL.get(0).getKeywords_plural().substring(0, ps_subfamilyAL.get(0).getKeywords_plural().indexOf(","));
	        			} else {
	        				subFamilyAndSeries = ps_subfamilyAL.get(0).getSubfamily_name();
	        			}
	    			}

	        		if (part.getSeries().compareTo("") != 0) {
	        			subFamilyAndSeries = subFamilyAndSeries + ", " + part.getSeries() + " Series";
	        		}
	    		}


	            String stripPart;
	            {	int lIndex = oitem.getPart_num().lastIndexOf("-");
	            	if (lIndex > 0) {
	                    stripPart = oitem.getPart_num().substring(0, lIndex).toLowerCase().trim();
	            	} else {
	            		stripPart = oitem.getPart_num();
	            	}
	            }
	            stripPart = stripPart.replaceAll(" ", "_");
	            stripPart = stripPart.replaceAll("/", "_");
	            // oldImageFilePath = "/images/" + oitem.getSales_subcat().toLowerCase() + "/small/" + stripPart;
	            oldImageFilePath = "/images/" + oitem.getSales_subcat().toLowerCase() + "/" + stripPart;
	            oldImageFilePath = oldImageFilePath + "_s.jpg";
	            ServletContext context = request.getSession().getServletContext();
	            String absoluteDiskPath = context.getRealPath(oldImageFilePath);
	            // log.debug("oldImageFilePath   :" + oldImageFilePath);
	            // log.debug("absoluteDiskPath:" + absoluteDiskPath);
	            if (new File(absoluteDiskPath).exists() == false) {
		        // if (new File(oldImageFilePath).exists() == false) {
	            	oldImageFilePath = "/images/catalog/picture-na_s.jpg";
	            }
	    		imageFilePath = getSmallImage(request, dbConnector, part);
	            // log.debug("oldImageFilePath :" + oldImageFilePath);
	            // log.debug("imageFilePath    :" + imageFilePath);


				String itemDescription = "";
				String itemDetailHref = "";
				String[] brand_and_alternateName;
	            ArrayList<Catalogitem> catalogitemAL = Catalogitem.getCatalogitem(dbConnector, part.getPart_num());
	            if ((catalogitemAL == null) || (catalogitemAL.size() == 0)) {
	            	log.debug("This item has no corresponding record in catalogitem table. order_num: " + order_num + ", part_num: " + part.getPart_num());
	            	itemDescription = "Please call customer service to order this item.";
					itemDetailHref = "";
	            	brand_and_alternateName = new String[] {"", ""};
	            } else {
		            Catalogitem catalogitem = catalogitemAL.get(0);
		            if (catalogitem.getDescription().compareTo("") != 0) {
		       			itemDescription = catalogitem.getDescription();
		       		} else {
		    			itemDescription = part.getDescription() +
		    							  part.getDescription2() +
										  part.getDescription3();
		            }

		            itemDetailHref = "";
		            try {
						itemDetailHref = itemdtl_href(dbConnector, catalogitem);
						// log.debug("itemDetailHref:" + itemDetailHref);
					} catch (UnsupportedEncodingException e) {
						log.error("Error occurred.", e);
					}

		            brand_and_alternateName = Order.getBrand_and_AlternateName(dbConnector, part, part.getPart_num(), catalogitem);

	                if (catalogitem.getList_type().compareTo("Catalog") == 0) {
	                	int indexOfLastHyphen = catalogitem.getPart_num().lastIndexOf("-");
	                	if (indexOfLastHyphen >= 0) {
	                		partNum = catalogitem.getPart_num().substring(0, indexOfLastHyphen);
	                	} else {
	                		partNum = catalogitem.getPart_num();
	                	}
	                }
	            }

				JSONObject jsonObject = new JSONObject();


				jsonObject.put("partNum", partNum);
				jsonObject.put("availability", availability);
				jsonObject.put("billing_unit", billing_unit);
				jsonObject.put("unit_price", unit_price);
				jsonObject.put("extendedPrice", extendedPrice);
				jsonObject.put("qtyOrdered", qtyOrdered);
				jsonObject.put("qtyShipped", qtyShipped);
				jsonObject.put("qtyBackOrder", qtyBackOrder);
				// jsonObject.put("shippedDate", shippedDate);
				// jsonObject.put("carrier", carrier);
				jsonObject.put("shipments", shipmentsJsonArray);
				jsonObject.put("imageFilePath", imageFilePath);
				jsonObject.put("subFamilyAndSeries", subFamilyAndSeries);
				jsonObject.put("itemDescription", itemDescription);
				jsonObject.put("brand", brand_and_alternateName[0]);
				jsonObject.put("alternateName", brand_and_alternateName[1]);
				jsonObject.put("itemDetailHref", itemDetailHref);

				jsonArray.put(jsonObject);
	    	}

		}

		if (totalPrice != -1) {
			ArrayList<Oaddon> oaddonAL = Oaddon.getOaddon(dbConnector, order_num, 0);
			if ((oaddonAL != null) && (oaddonAL.size() > 0)) {
				for (Iterator iterator2 = oaddonAL.iterator(); iterator2.hasNext();) {
					Oaddon oaddon = (Oaddon) iterator2.next();
					totalPrice += oaddon.getAdd_amt_bal();
					totalPriceFromOaddons += oaddon.getAdd_amt_bal();
				}
			}
			// log.debug("totalPriceFromOaddons: " + totalPriceFromOaddons);
		}

		JSONObject jsonObject = new JSONObject();

		jsonObject.put("item_details", jsonArray);
		if (totalPrice == -1) {
			jsonObject.put("totalPrice", "N/A");
		} else {
			jsonObject.put("totalPrice", currencyFormatter.format(totalPrice));
		}

		{
			ArrayList<Order> orderAL = Order.getOrder(dbConnector, order_num);
			Order order = orderAL.get(0);

			String sales_repName = Sales_rep.getSales_repName(dbConnector, order.getSales_rep());
			if (sales_repName != null) {
				jsonObject.put("salesRep", sales_repName);
			} else {
				sales_repName = Sales_rep.getSales_repName(dbConnector, order.getSales_rep2());
				if (sales_repName != null) {
					jsonObject.put("salesRep", sales_repName);
				} else {
					jsonObject.put("salesRep", "");
				}
			}
		}

		int noOfAvailableInvoices = Invoice.getInvoiceCount(dbConnector, order_num);
		// log.debug("noOfAvailableInvoices: " + noOfAvailableInvoices);
		jsonObject.put("noInvoicesAreAvailable", ((noOfAvailableInvoices == 0)?"y":"n"));

    	// log.debug(jsonObject);
    	return jsonObject;
    }

    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------

    public static String getSmallImage(HttpServletRequest request, DBConnector dbConnector, Part part) throws PortalException {
       String pic_src;
       boolean series_img;

       series_img = false;
       pic_src = null;

       String fs_part, encoded_pic, encoded_url;

       ServletContext context = request.getSession().getServletContext();

       if (part != null) {
          int rIndex = part.getPart_num().lastIndexOf("-");
          if (rIndex >= 0) {
             fs_part = img_encode(part.getPart_num().substring(0, rIndex));
          } else {
            fs_part = img_encode(part.getPart_num());
          }
          pic_src = ("/images/" + part.getSales_subcat() + "/" + fs_part + "_s.jpg").toLowerCase();

          // log.debug("pic_src 1 : " + pic_src);
          if (new File(context.getRealPath(pic_src)).exists() == false) {
             pic_src = null;
          }

          if ((pic_src == null) && (part.getFamily_code().compareTo("") != 0) && (part.getSubfamily_code().compareTo("") != 0)) {
             ArrayList<Ps_rules> ps_rulesAL = Ps_rules.getPs_rules(dbConnector,
            		 											   part.getFamily_code(),
                                                                   part.getSubfamily_code(),
                                                                   "A",
                                                                   "P");
             Ps_rules ps_rules = null;
             if ((ps_rulesAL == null) || (ps_rulesAL.size() == 0)) {
                ps_rulesAL = Ps_rules.getPs_rules(dbConnector,
                								  part.getFamily_code(),
                                                  "*",
                                                  "A",
                                                  "P");
                if ((ps_rulesAL != null) && (ps_rulesAL.size() > 0)) {
                   ps_rules = ps_rulesAL.get(0);
                }
             } else {
                ps_rules = ps_rulesAL.get(0);
             }

             if (ps_rules != null) {
                ArrayList<Ps_parm_data> ps_parm_dataAL = Ps_parm_data.getPs_parm_data(dbConnector,
                																	  part.getPart_num(),
                                                                                      ps_rules.seq_num);
                if ((ps_parm_dataAL != null) && (ps_parm_dataAL.size() > 0)) {
                    Ps_parm_data ps_parm_data = ps_parm_dataAL.get(0);

                   encoded_pic = img_encode(ps_parm_data.parm_value);
                   encoded_url = "/images/" + part.getSales_subcat().toLowerCase() + "/" + encoded_pic;

                   if (get_image_cnt(dbConnector, context, encoded_url, "part") > 0) {
                      if (image_ok(context, "/images/" + part.getSales_subcat().toLowerCase() + "/" + encoded_pic + "_s.jpg")) {
                         pic_src = "/images/" + part.getSales_subcat().toLowerCase() + "/" + encoded_pic + "_s.jpg";
                         // log.debug("pic_src 2 : " + pic_src);
                      }
                   }
                }
             }
          }

          if (pic_src == null) {
             pic_src = ("/images/" + part.getSales_subcat() + "/" + fs_part + "_s.gif").toLowerCase();
             // log.debug("pic_src 3 : " + pic_src);

             if (new File(context.getRealPath(pic_src)).exists() == false) {
                pic_src = null;
             }

             if ((pic_src == null) && (part.getSeries().compareTo("") != 0) && (part.getSeries().compareTo(fs_part) != 0))  {
                 if ((part.getFamily_code().compareTo("") != 0) && (part.getSubfamily_code().compareTo("") != 0)) {
                     pic_src = ("/images/" + part.getSales_subcat() + "/" + img_encode(part.getFamily_code()) + "_" + img_encode(part.getSubfamily_code()) + "_" +
                                img_encode(part.getSeries()) + "_s.jpg").toLowerCase();
                     // log.debug("pic_src 4 : " + pic_src);
                     if (new File(context.getRealPath(pic_src)).exists() == false) {
                        pic_src = null;
                     } else {
                        ArrayList<Part> partAL = Part.getPart(dbConnector, part.getSales_subcat(), part.getSeries());
                        if ((partAL != null) && (partAL.size() > 0)) {
                           series_img = true;
                        } else {
                           series_img = false;
                        }
                     }
                 }
                 if (pic_src == null) {
                    pic_src = ("/images/" + part.getSales_subcat() + "/" + img_encode(part.getSeries()) + "_s.jpg").toLowerCase();
                    // log.debug("pic_src 5 : " + pic_src);
                    if (new File(context.getRealPath(pic_src)).exists() == false) {
                    	pic_src = pic_src.replaceAll("_s.jpg", "_s.gif");
                        // log.debug("pic_src 6 : " + pic_src);
                    }

                    if (new File(context.getRealPath(pic_src)).exists() == false) {
                       pic_src = null;
                    } else {
                        ArrayList<Part> partAL = Part.getPart(dbConnector, part.getSales_subcat(), part.getSeries());
                        if ((partAL != null) && (partAL.size() > 0)) {
                           series_img = true;
                        } else {
                           series_img = false;
                        }
                    }
                 }
             }
          }
       }

       if (pic_src == null) {
           ArrayList<Part_img> part_imgAL = Part_img.getPart_img(dbConnector, part.getPart_num(), "Picture-s");
           if ((part_imgAL != null) && (part_imgAL.size() > 0)) {
        	   Part_img part_img = part_imgAL.get(0);
               if (image_ok(context, ("/" +  (part_img.getImg_location()).toLowerCase()))) {
                   pic_src = ("/" + part_img.getImg_location()).toLowerCase();
                   // log.debug("pic_src 7 : " + pic_src);
               }
           }
       }
       if (pic_src == null) {
          pic_src = "/images/catalog/picture-na_s.jpg";
       }

       return pic_src;
    }

    public static String img_encode(String pic_name) {
       return ((pic_name.toLowerCase()).trim()).replaceAll(" ", "_").replaceAll("/", "_");
    }

    public static int get_image_cnt(DBConnector dbConnector, ServletContext context, String image_base, String image_type) throws PortalException {
        int cnt = 0, i;
        String pic_types = "", pic_descs = "";

        if (image_type.compareTo("Series") == 0) {
           while (true) {
               if (image_ok(context, image_base + "_" + (cnt + 1) + ".jpg")) {
                  cnt = cnt + 1;
               } else {
                  break;
               }
           }
        } else {/* image_type = "Part" */
           ArrayList<Codes> codesAL = Codes.getCodes(dbConnector, "TECH SHEET", "P", true);
           if ((codesAL != null) && (codesAL.size() > 0)) {
               Codes codes = codesAL.get(0);
               if (image_ok(context, image_base + "_" + (codes.getValid_code()).toLowerCase() + ".jpg")) {
                  cnt = 1;
                  pic_types = (codes.getValid_code()).toLowerCase();
                  pic_descs = codes.getDescription();
               }
           }

           codesAL = Codes.getCodes_VC_NotEq(dbConnector, "TECH SHEET", "P", true);
           if ((codesAL != null) && (codesAL.size() > 0)) {
              for (Iterator<Codes> iterator = codesAL.iterator(); iterator.hasNext();) {
                 Codes codes = iterator.next();
                 if (image_ok(context, image_base + "_" + (codes.getValid_code()).toLowerCase() + ".jpg")) {
                    cnt = cnt + 1;

                    if (pic_types.compareTo("") == 0) {
                    	pic_types = codes.getValid_code().toLowerCase();
                    } else {
                    	pic_types = pic_types + "," + codes.getValid_code().toLowerCase();
                    }
                    if (pic_descs.compareTo("") == 0) {
                    	pic_descs = codes.getDescription();
                    } else {
                    	pic_descs = pic_descs + "," + codes.getDescription();
                    }
                 }
              }
           }
        }

        return cnt;
    }

    public static boolean image_ok(ServletContext context, String imgname) {
        // RETURN SEARCH(get-cgi("DOCUMENT_ROOT") + imgname) <> ?.
        return new File(context.getRealPath(imgname)).exists();
    }

    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------

    public static String[] getBrand_and_AlternateName(DBConnector dbConnector, Part part, String part_num, Catalogitem catalogitem) throws PortalException {
		String[] itemDescription = new String[2];
		itemDescription[0] = "";
		itemDescription[1] = "";

		String w_mfgoutput, asterisk;

    	w_mfgoutput = "";
    	asterisk = (is_franchised_parent(dbConnector, part_num, catalogitem.getParent_mfg_code())?"":"*");

		if (part != null) {
	    	ArrayList<Ps_index_mfg> ps_index_mfgAL = Ps_index_mfg.getPs_index_mfg(dbConnector, part.getSales_subcat());
	    	if ((ps_index_mfgAL == null) || (ps_index_mfgAL.size() == 0)) {
	    		w_mfgoutput = m_descr(dbConnector, part.getVendor_abv(), part.getSales_subcat());
	    	}
		}

		if (w_mfgoutput.compareTo("") != 0) {
			int index = w_mfgoutput.indexOf(";");
			if ((index >= 0) && (w_mfgoutput.length() > (index + 1))) {
				w_mfgoutput = w_mfgoutput.substring(index + 1);
			}
			itemDescription[0] = w_mfgoutput + asterisk;
		} else {
			itemDescription[0] = catalogitem.getMfg_name() + asterisk;
		}

		if (catalogitem.getFamily_code().compareTo("") != 0) {
			if (catalogitem.getList_type().compareTo("Catalog") == 0) {
				ArrayList<Ps_subfamily> ps_subfamilyAL = Ps_subfamily.getPs_subfamily(dbConnector, catalogitem.getFamily_code(), catalogitem.getSubfamily_code());
				if ((ps_subfamilyAL != null) && (ps_subfamilyAL.size() > 0)) {
					Ps_subfamily ps_subfamily = ps_subfamilyAL.get(0);

					int indexOfComma = ps_subfamily.getKeywords_plural().indexOf(',');
					String firstWord;
					if (indexOfComma >= 0) {
						firstWord = ps_subfamily.getKeywords_plural().substring(0, ps_subfamily.getKeywords_plural().indexOf(','));
					} else {
						firstWord = ps_subfamily.getKeywords_plural();
					}

					itemDescription[1] = ((firstWord.compareTo("") != 0)?firstWord:ps_subfamily.getSubfamily_name());
				}
			} else {
				itemDescription[1] = catalogitem.getSubfamily_code() + " Repair";
			}

			if (catalogitem.getSeries().compareTo("") != 0) {
				itemDescription[1] = itemDescription[1] + ", " + catalogitem.getSeries() + " Series";
			}
		} else {
			if (catalogitem.getList_type().compareTo("RCM") != 0) {
				ArrayList<Part_cat> part_catAL = Part_cat.getPart_cat(dbConnector, catalogitem.getSales_cat(), catalogitem.getSales_subcat());
				if ((part_catAL != null) && (part_catAL.size() > 0)) {
					Part_cat part_cat = part_catAL.get(0);
					itemDescription[1] = part_cat.getDescription();
				}
			}
		}

		return itemDescription;
    }

    public static boolean is_franchised_parent(DBConnector dbConnector, String part_num, String mfg_code) throws PortalException {
    	ArrayList<Codes_s> codes_sAL = Codes_s.getCodes_s_CV(dbConnector, "PARENT MFGR", mfg_code);
        if ((codes_sAL != null) && (codes_sAL.size() > 0)) {
        	Codes_s codes_s = codes_sAL.get(0);
            String w_parent = codes_s.getValid_code();

            codes_sAL = Codes_s.getCodes_s_CM(dbConnector, "MANUFACTURER", w_parent);
            if ((codes_sAL != null) && (codes_sAL.size() > 0)) {
            	for (Iterator iterator = codes_sAL.iterator(); iterator.hasNext();) {
					codes_s = (Codes_s) iterator.next();
		            if (look_for_franchised_mfg(dbConnector, codes_s.getValid_code())) {
		            	return true;
		            }
				}
            }
            return false;
        } else {
        	return look_for_franchised_mfg(dbConnector, mfg_code);
        }
    }

    public static boolean look_for_franchised_mfg(DBConnector dbConnector, String mfg_code) throws PortalException {
        ArrayList<Part_cat> part_catAL = Part_cat.getPart_cat(dbConnector, mfg_code, true);
        if ((part_catAL == null) || (part_catAL.size() == 0)) {
        	return false;
        }
        return true;
    }

    public static Codes_s getCodesRec(DBConnector dbConnector, String code_type, String valid_code) throws PortalException {
        ArrayList<Codes_s> codes_sAL = Codes_s.getCodes_s_CV(dbConnector, code_type, valid_code);
        if ((codes_sAL != null) && (codes_sAL.size() > 0)) {
            return codes_sAL.get(0);
        } else {
            return null;
        }
    }

    public static String m_descr(DBConnector dbConnector, String w_ven_abv, String w_s_subcat) throws PortalException {
        Codes_s brandmfg = null, parentmfgname = null, parentbrandname = null, codesbuf = null;
        String codeval;

        if (w_ven_abv.compareTo("") != 0) {
            brandmfg = getCodesRec(dbConnector, "PARENT MFGR", w_ven_abv);
            if (brandmfg == null) {
               brandmfg = getCodesRec(dbConnector, "MANUFACTURER", w_ven_abv);
               if ((brandmfg != null) && (brandmfg.getMisc_alpha4().compareTo("") != 0)) {
                  parentbrandname = getCodesRec(dbConnector, "PARENT MFGR", brandmfg.getMisc_alpha4());
                  if (parentbrandname != null) {
                     codeval = brandmfg.getMisc_alpha4();
                     brandmfg = getCodesRec(dbConnector, "PARENT MFGR", codeval);
                  }
               }
            }
        }

        if (w_s_subcat.compareTo("") != 0) {
           codesbuf = getCodesRec(dbConnector, "MANUFACTURER", w_s_subcat);
        }

        if ((brandmfg != null) && (codesbuf != null) && (codesbuf.getValid_code().compareTo("NBR") == 0)) {
           if (brandmfg.getValid_code().compareTo(codesbuf.getValid_code()) != 0) {
              return brandmfg.getValid_code().trim() + ";" + codesbuf.getDescription().trim() + " for " + brandmfg.getDescription().trim();
           } else {
              return codesbuf.getValid_code().trim() + ";" + codesbuf.getDescription().trim();
           }
        } else {
           /*Check for available brand, mfg and mfg display brand flag */
           if ((brandmfg != null) && (codesbuf != null) && (codesbuf.getMisc_log2())) {
              /*Checks for parent mfg*/
              if (codesbuf.getMisc_alpha4().compareTo("") != 0) {
                 parentmfgname = getCodesRec(dbConnector, "PARENT MFGR", codesbuf.getMisc_alpha4());
                 if ((parentmfgname != null) && (parentmfgname.getDescription().compareTo("") != 0)) {
                    /*Output brand and parent mfg*/
                    if (brandmfg.getDescription().trim().compareTo(parentmfgname.getDescription().trim()) != 0) {
                        return brandmfg.getValid_code().trim() + ";" + brandmfg.getDescription().trim() + " - " + parentmfgname.getDescription().trim();
                    } else {
                        return parentmfgname.getValid_code().trim() + ";" + parentmfgname.getDescription().trim();
                    }
                 } else {
                    /*PARENT MFGR Name not available display brand and mfg*/
                    if (brandmfg.getDescription().trim().compareTo(codesbuf.getDescription().trim()) != 0) {
                       return brandmfg.getValid_code().trim() + ";" + brandmfg.getDescription().trim() + " - " + codesbuf.getDescription().trim();
                    } else {
                       return codesbuf.getValid_code().trim() + ";" + codesbuf.getDescription().trim();
                    }
                 }
              } else {
                 /*No parent mfg output brand and mfg*/
                 if (brandmfg.getDescription().trim().compareTo(codesbuf.getDescription().trim()) != 0) {
                    return brandmfg.getValid_code().trim() + ";" + brandmfg.getDescription().trim() + " - " + codesbuf.getDescription().trim();
                 } else {
                    return codesbuf.getValid_code().trim() + ";" + codesbuf.getDescription().trim();
                 }
              }
           } else {
              /* NO brand available checks for parent mfg*/
              if ((codesbuf != null) && (codesbuf.getMisc_alpha4().compareTo("") != 0)) {
                    parentmfgname = getCodesRec(dbConnector, "PARENT MFGR", codesbuf.getMisc_alpha4());
                    if ((parentmfgname != null) && (parentmfgname.getDescription().compareTo("") != 0)) {
                       /*Display parent mfg*/
                       return parentmfgname.getValid_code().trim() + ";" + parentmfgname.getDescription().trim();
                    } else {
                       /*Parent mfg not available display mfg*/
                       return codesbuf.getValid_code().trim() + ";" + codesbuf.getDescription().trim();
                    }
              } else {
                 if ((codesbuf != null) && (codesbuf.getValid_code().compareTo("") != 0) && (codesbuf.getDescription().compareTo("") != 0)) {
                    return codesbuf.getValid_code().trim() + ";" + codesbuf.getDescription().trim();
                 } else {
                    return "";
                 }
              }
           }
        }
    }

    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------

    public static String itemdtl_href(DBConnector dbConnector, Catalogitem catalogitem) throws PortalException, UnsupportedEncodingException {
	   String mfg_part, mfg_code, wrk;

	   if (catalogitem != null) {
	      if ((catalogitem.getPart_num().indexOf("#") >= 0) ||
	          (catalogitem.getPart_num().indexOf("%") >= 0) ||
	          (catalogitem.getPart_num().indexOf("&") >= 0) ||
	          (catalogitem.getPart_num().indexOf("+") >= 0)    ) {
	    	  // log.debug("itemdtl_href - 1");
	          return "";
	      }

	      if (catalogitem.getList_type().compareTo("Catalog") == 0) {
	         int lastIndexOfMinus = catalogitem.getPart_num().lastIndexOf('-');
	         if (lastIndexOfMinus < 0) {
                // log.debug("itemdtl_href - 2");
	            return "";
	         }

	         mfg_part = catalogitem.getPart_num().substring(0, lastIndexOfMinus).replace("&", "").replace("%", "").replace("\"", "%22");
	         mfg_code = catalogitem.getPart_num().substring(lastIndexOfMinus + 1);
	      } else {
	         mfg_part = catalogitem.getPart_num().replace("&", "").replace("%", "").replace("\"", "%22");
	         mfg_code = catalogitem.getSales_subcat();
	      }

	      Codes_s codes_s = null;
	      ArrayList<Codes_s> codes_sAL = Codes_s.getCodes_s_CV(dbConnector, "MANUFACTURER", mfg_code);
	      if ((codes_sAL != null) && (codes_sAL.size() > 0)) {
	         codes_s = codes_sAL.get(0);
	         if (codes_s.getMisc_alpha4().compareTo("") != 0) {
	            wrk = codes_s.getMisc_alpha4();
	            codes_sAL = Codes_s.getCodes_s_CV(dbConnector, "PARENT MFGR", wrk);
	            if ((codes_sAL != null) && (codes_sAL.size() > 0)) {
	               codes_s = codes_sAL.get(0);
	            }
	         }
	      }

	      if (codes_s != null) {
	         if (catalogitem.getList_type().compareTo("Catalog") == 0) {
	            ArrayList<Urlmap> urlmapAL = Urlmap.getUrlmap(dbConnector, encode_name(codes_s.getDescription()), codes_s.getValid_code());
	            if ((urlmapAL != null) && (urlmapAL.size() > 0)) {
	               Urlmap urlmap = urlmapAL.get(0);
		  	       // log.debug("itemdtl_href - 3");
		  	       // log.debug("mfg_part:" + mfg_part);
		  	       // log.debug("mfg_part after UrlEncoder:" + URLEncoder.encode(mfg_part,"UTF-8").replace("%2F", "/"));
	               return "/buy/" + urlmap.name + "/" + URLEncoder.encode(mfg_part,"UTF-8").replace("%2F", "/");
	            }
	         } else {
	            ArrayList<Surlmap> surlmapAL = Surlmap.getSurlmap(dbConnector, encode_name(codes_s.getDescription()), codes_s.getValid_code());
	            Surlmap surlmap = surlmapAL.get(0);
	            if ((surlmapAL != null) && (surlmapAL.size() > 0)) {
	  	    	   // log.debug("itemdtl_href - 4");
	               return "/repair/" + surlmap.name + "/" + URLEncoder.encode(mfg_part,"UTF-8").replace("%2F", "/");
	            }
	         }
	      }

    	  String appURL = "/scripts/cgiip.exe/";

    	  if (catalogitem.getList_type().compareTo("Catalog") == 0) {
	    	 // log.debug("itemdtl_href - 5");
	         return appURL + "/wa/wcat/itemdtl.htm?pnum=" + URLEncoder.encode(catalogitem.getPart_num(),"UTF-8").replace("%2F", "/");
	      } else {
	    	 // log.debug("itemdtl_href - 6");
	         return appURL + "/wa/wcat/repairdtl.htm?pnum=" + URLEncoder.encode(catalogitem.getPart_num(),"UTF-8").replace("%2F", "/") + "&mfgr=" + URLEncoder.encode(catalogitem.getSales_subcat(),"UTF-8").replace("%2F", "/");
	      }
	   }

	   return "";
	}

	public static String encode_name(String nm) {
	   nm = nm.replace("&trade;", "");
	   nm = nm.replace("&reg;", "");
	   nm = nm.replace(" - ", " ");
	   nm = nm.replace(", ", " ");
	   nm = nm.replace(",", " ");
	   nm = nm.replace(" & ", " and ");
	   nm = nm.replace("&", " and ");
	   nm = nm.replace(" ", "-");
	   nm = nm.replace("/", "-");
	   nm = nm.replace("---", "-");

	   return nm;
	}

    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Order> testFetch(DBConnector dbConnector) throws PortalException {
		// String queryString = "select top 2 " + getQueryFieldSelectionString() + " from pub.order where " +
		//		 "cust_num = '140766' and order_num > 'A677485'";
		// String queryString = "select " + getQueryFieldSelectionString() + " from pub.order where " +
		//		 "cust_num = '140766'";

    	String queryString = "select  top 2 " + getQueryFieldSelectionString() + " from pub.order " +
				   "where order_num in (select order_num from pub.order " +
				   						"where cust_num = '140766' and order_num >= 'A688506' order by order_num)";

    	// queryString = "select order_num from pub.order where cust_num = '140766' and order_num >= 'A688506' order by order_num";


    	queryString = "select  top 2 " + getQueryFieldSelectionString() + " from pub.order " +
				   "where cust_num = '140766' and order_num >= 'A675639' " +
 			   "order by order_num";

    	queryString = "select  top 15 " + getQueryFieldSelectionString() + " from pub.order " +
				   "where cust_num = '000017' and order_num > 'A547689' " +
				   "order by order_num";


		Statement stmt = null;
		ResultSet rs = null;

        try {
        	stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
				log.debug(JDBCUtils.getStringFromResultSet(rs, "order_num"));
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

        return null;

    	/*

    	queryString = "select top 3 cust_num, count(*) from pub.order group by cust_num having count(*) > 100";

    	ResultSet rs = JDBCUtils.executeQuery(queryString);

        while (rs.next()) {
        	log.debug(JDBCUtils.getStringFromResultSet(rs, "cust_num"));
        }

        return null;

        */

    	// JDBCUtils.executeQuery(queryString);
    	// return null;

		// ArrayList<Order> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		// return al;
	}

    // -------------------------------------------------------------------------------------------------------------

    // ddd
	public void calculateOrderStatus(DBConnector dbConnector) throws PortalException {
		if (orderStatus != null) {
			return;
		}

		if (order_type.compareTo("S") == 0) {
			if (proc_stage.compareTo("6") == 0) {
				setOrderStatus("Pending");
			} else if (proc_stage.compareTo("9") == 0) {
				// setOrderStatus("Shipped Complete");
				setOrderStatus("Closed");
			} else if (backorder.compareTo("YES") == 0) {
				setOrderStatus("Shipped Partially");
			} else {
				setOrderStatus("Open");
			}
		} else if (order_type.compareTo("Q") == 0) {
			if ((date_closed != null) && (date_closed.compareTo("") != 0)) {
				setOrderStatus("Expired");
			/*
			} else if (getMisc_date2() != null) {
				setOrderStatus("Pending");
			*/
			} else {
				if ((order_date != null) && (order_date.compareTo("") != 0)) {
					try {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						sdf.setLenient(false);
						Date orderDate = sdf.parse(order_date);

						Date today = new Date();
						Calendar cal = new GregorianCalendar();
						cal.setTime(today);
						cal.add(Calendar.DAY_OF_MONTH, -31);
						Date todayminus31 = cal.getTime();

						if (orderDate.compareTo(todayminus31) < 0) {
							// log.debug(order_num + " has expired because it is older than 31 days.");
							setOrderStatus("Expired");
							return;
						}
					} catch (ParseException e) {
					}
				}

				setOrderStatus("Open");
		    	ArrayList<Oitem> OitemAL;
		    	OitemAL = Oitem.getOitemsOfAnOrder(dbConnector, order_num);
				if (OitemAL != null) {
			    	for (Iterator<Oitem> iterator = OitemAL.iterator(); iterator.hasNext();) {
						Oitem oitem = iterator.next();

						if (oitem.getUnit_price() == 0) {
							setOrderStatus("Pending");
							break;
						}
			    	}
				}
			}
		}


		return;
	}

	public String getOrderStatus() throws PortalException {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

    // -------------------------------------------------------------------------------------------------------------

	public String getCust_num() {
		return cust_num;
	}
	public void setCust_num(String cust_num) {
		this.cust_num = cust_num;
	}
	public String getOrder_date() {
		return order_date;
	}
	public void setOrder_date(String order_date) {
		this.order_date = order_date;
	}
	public String getOrder_num() {
		return order_num;
	}
	public void setOrder_num(String order_num) {
		this.order_num = order_num;
	}
	public String getProc_stage() {
		return proc_stage;
	}
	public void setProc_stage(String proc_stage) {
		this.proc_stage = proc_stage;
	}
	public String getPmt_type() {
		return pmt_type;
	}
	public void setPmt_type(String pmt_type) {
		this.pmt_type = pmt_type;
	}
	public String getCc_num() {
		return cc_num;
	}
	public void setCc_num(String cc_num) {
		this.cc_num = cc_num;
	}
	public String getCust_ponum() {
		return cust_ponum;
	}
	public void setCust_ponum(String cust_ponum) {
		this.cust_ponum = cust_ponum;
	}
	public String getOrdered_by() {
		return ordered_by;
	}
	public void setOrdered_by(String ordered_by) {
		this.ordered_by = ordered_by;
	}
	public String getShip_loc() {
		return ship_loc;
	}
	public void setShip_loc(String ship_loc) {
		this.ship_loc = ship_loc;
	}

	public String getOrder_type() {
		return order_type;
	}
	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}
	public String getBackorder() {
		return backorder;
	}
	public void setBackorder(String backorder) {
		this.backorder = backorder;
	}
    public String getDate_closed() {
		return date_closed;
	}
	public void setDate_closed(String date_closed) {
		this.date_closed = date_closed;
	}
    public String getMisc_date2() {
        return misc_date2;
    }
    public void setMisc_date2(String misc_date2) {
        this.misc_date2 = misc_date2;
    }
    public String getExpire_date() {
        return expire_date;
    }
    public void setExpire_date(String expire_date) {
        this.expire_date = expire_date;
    }
    public String getSales_rep() {
        return sales_rep;
    }
    public void setSales_rep(String sales_rep) {
        this.sales_rep = sales_rep;
    }
    public String getSales_rep2() {
        return sales_rep2;
    }
    public void setSales_rep2(String sales_rep2) {
        this.sales_rep2 = sales_rep2;
    }
}
