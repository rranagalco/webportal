package galco.fascor.messages;

import galco.fascor.utils.FasUtils;
import galco.portal.config.Parms;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;
import galco.portal.wds.dao.Oaddon;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Message_1310 extends MessageAbs implements Message {
	private static Logger log = Logger.getLogger(Message_1310.class);

	String order_num;

	// -------------------------------------------------------------------------------------------

	public Message_1310(Connection wdsConnection, String order_num, String action) throws SQLException, RuntimeException {
		super("1310", action);
		this.order_num = order_num;

		getDataFromWDS(wdsConnection, order_num, action);
		fascorMessage = FasUtils.buildFascorMessage(this);
	}

	// -------------------------------------------------------------------------------------------

	public void getDataFromWDS(Connection wdsConnection, String order_num, String action) throws SQLException, RuntimeException {

		ArrayList<HashMap<String, Object>> orderAL = null;
		// orderAL = JDBCUtils.runQuery(wdsConnection, "select order_num, cust_num, billto_num, shipto_num, order_type, order_date, ship_date, shipvia, terms, billto_num, sh_name, sh_address, sh_address2, sh_city, sh_state, sh_zip, sh_country, cust_ponum, comments, cc_first_name, cc_last_name, cc_address, cc_address2, cc_city, cc_state, cc_zip, cc_country from pub.order where order_num = '" + order_num + "' FOR UPDATE WITH (READPAST NOWAIT)");
		orderAL = JDBCUtils.runQuery(wdsConnection, "select order_num, cust_num, billto_num, shipto_num, order_type, order_date, ship_date, shipvia, terms, billto_num, sh_name, sh_address, sh_address2, sh_city, sh_state, sh_zip, sh_country, cust_ponum, comments, cc_first_name, cc_last_name, cc_address, cc_address2, cc_city, cc_state, cc_zip, cc_country from pub.order where order_num = '" + order_num + "' WITH (NOLOCK)");
		if ((orderAL == null) || (orderAL.size() != 1)) {
			throw new RuntimeException("order data is missing for order_num: " + order_num);
		}
		HashMap<String, Object> orderHM = orderAL.get(0);

		String billto_num = (String) orderHM.get("billto_num");
		String shipto_num = (String) orderHM.get("shipto_num");
		String cust_num = (String) orderHM.get("cust_num");

		ArrayList<HashMap<String, Object>> custAL = null;
		// custAL = JDBCUtils.runQuery(wdsConnection, "select cust_num, name, address, address2, city, state, zip, country from pub.cust where cust_num = '" + cust_num + "' FOR UPDATE WITH (READPAST NOWAIT)");
		custAL = JDBCUtils.runQuery(wdsConnection, "select cust_num, name, address, address2, city, state, zip, country from pub.cust where cust_num = '" + cust_num + "' WITH (NOLOCK)");
		if ((custAL == null) || (custAL.size() != 1)) {
			throw new RuntimeException("cust data is missing for cust_num: " + cust_num);
		}
		HashMap<String, Object> custHM = custAL.get(0);

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		String nameBill = "", addressBill = "", address2Bill = "", cityBill = "", stateBill = "", zipBill = "", countryBill = "";

		if ((order_num.substring(0,  1).compareToIgnoreCase("W") == 0) &&
			(cust_num.compareToIgnoreCase("036329") == 0			 )    ) {
			nameBill = orderHM.get("cc_first_name") + " " + orderHM.get("cc_last_name");
			addressBill = (String) orderHM.get("cc_address");
			address2Bill = (String) orderHM.get("cc_address2");
			cityBill = (String) orderHM.get("cc_city");
			stateBill = (String) orderHM.get("cc_state");
			zipBill = (String) orderHM.get("cc_zip");
			countryBill = (String) orderHM.get("cc_country");
		} else if (billto_num.compareToIgnoreCase("") != 0) {
			ArrayList<HashMap<String, Object>> custbillAL = null;
			// custbillAL = JDBCUtils.runQuery(wdsConnection, "select name, address, address2, city, state, zip, country from pub.custbill where cust_num = '" + cust_num + "' and billto_num = '" + billto_num + "' FOR UPDATE WITH (READPAST NOWAIT)");
			custbillAL = JDBCUtils.runQuery(wdsConnection, "select name, address, address2, city, state, zip, country from pub.custbill where cust_num = '" + cust_num + "' and billto_num = '" + billto_num + "' WITH (NOLOCK)");
			if ((custbillAL == null) || (custbillAL.size() != 1)) {
				throw new RuntimeException("custbill data is missing for cust_num: " + cust_num + ", billto_num: " + billto_num);
			}
			HashMap<String, Object> custbillHM = custbillAL.get(0);

			nameBill = (String) custbillHM.get("name");
			addressBill = (String) custbillHM.get("address");
			address2Bill = (String) custbillHM.get("address2");
			cityBill = (String) custbillHM.get("city");
			stateBill = (String) custbillHM.get("state");
			zipBill = (String) custbillHM.get("zip");
			countryBill = (String) custbillHM.get("country");
		} else {
			nameBill = (String) custHM.get("name");
			addressBill = (String) custHM.get("address");
			address2Bill = (String) custHM.get("address2");
			cityBill = (String) custHM.get("city");
			stateBill = (String) custHM.get("state");
			zipBill = (String) custHM.get("zip");
			countryBill = (String) custHM.get("country");
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		dataHM.put("Message_Type", "1310");
		dataHM.put("Mode", action);
		dataHM.put("Facility_Nbr", Parms.FASCOR_FACILITY_NBR);


		dataHM.put("Order_ID", orderHM.get("order_num"));
		try {
		    dataHM.put("Order_Date", FasUtils.convertDateToCCYYMMDD((java.util.Date) orderHM.get("order_date")));
		} catch (Exception e) {
		    dataHM.put("Order_Date", "");
		}
		    dataHM.put("Order_Type", "S");
		    dataHM.put("Order_Priority", "01");
		    dataHM.put("Order_Status", "OKAY");
		    dataHM.put("Order_Reason_Code", "");
		    dataHM.put("Order_Reason_Comments", "");
		try {
		    dataHM.put("Order_Ship_Date", FasUtils.convertDateToCCYYMMDD((java.util.Date) orderHM.get("ship_date")));
		} catch (Exception e) {
		    dataHM.put("Order_Ship_Date", "");
		}

	    dataHM.put("Order_Ship_Day", "");

		dataHM.put("Carrier_ID", orderHM.get("shipvia"));
		dataHM.put("Shipping_Terms", orderHM.get("terms"));
		    dataHM.put("Ship_WOG_Control", "N");
		    dataHM.put("ASN_Control", "N");

		    // dataHM.put("Route_ID", "");
		    // dataHM.put("Stop_ID", "");
		    dataHM.put("Route_ID", "1");
		    dataHM.put("Stop_ID", "1");
		    
		dataHM.put("Sold_to_customer_ID", orderHM.get("cust_num"));

		dataHM.put("Sold_to_customer_Name", nameBill);
		    dataHM.put("Sold_to_customer_Abbrev_Name", "");
		dataHM.put("Sold_to_customer_Addr1", addressBill);
		dataHM.put("Sold_to_customer_Addr2", address2Bill);
		dataHM.put("Sold_to_customer_Addr3", "");
		dataHM.put("Sold_to_customer_Addr4", "");
		dataHM.put("Sold_to_customer_City", cityBill);
		dataHM.put("Sold_to_customer_State", stateBill);
		dataHM.put("Sold_to_customer_Zip", zipBill);
		dataHM.put("Sold_to_customer_Country_Code", countryBill);

		dataHM.put("Sold_to_customer_PO_Ref", orderHM.get("cust_ponum"));
		    dataHM.put("Sold_to_customer_PO_Date", "");
		dataHM.put("Ship_to_customer_ID", cust_num + ((shipto_num.compareTo("") == 0)?"":(shipto_num)));
		dataHM.put("Ship_to_customer_Name", orderHM.get("sh_name"));
	    	dataHM.put("Ship_to_customer_Abbrev_Name", "");
		dataHM.put("Ship_to_customer_Addr1", orderHM.get("sh_address"));
		dataHM.put("Ship_to_customer_Addr2", orderHM.get("sh_address2"));
		dataHM.put("Ship_to_customer_Addr3", "");
		dataHM.put("Ship_to_customer_Addr4", "");
		dataHM.put("Ship_to_customer_City", orderHM.get("sh_city"));
		dataHM.put("Ship_to_customer_State", orderHM.get("sh_state"));
		dataHM.put("Ship_to_customer_Zip", orderHM.get("sh_zip"));
		dataHM.put("Ship_to_customer_Country_Code", orderHM.get("sh_country"));
		    dataHM.put("Tote_Control", "N");
		    dataHM.put("Tote_Type", "");
		    dataHM.put("Child_Type", "");
		    dataHM.put("Pallet_Control", "N");
		    dataHM.put("Pallet_Type", "");

		    // dataHM.put("Order_Fill_Control", "N");
		    dataHM.put("Order_Fill_Control", "Y");		    
		    // dataHM.put("Order_Fill_Percentage", "");		    
		    dataHM.put("Order_Fill_Percentage", 100);
		    // dataHM.put("Order_Fill_Calculation", "");
		    dataHM.put("Order_Fill_Calculation", "A");
		    //  dataHM.put("Order_Fill_Action", "");
		    dataHM.put("Order_Fill_Action", "CONT");
		    // dataHM.put("Partial_Shipment_Control", "Y");
		    dataHM.put("Partial_Shipment_Control", "N");
		    
		    dataHM.put("Partial_Shipment_Suffix", "00");
		    dataHM.put("Substitute_Control", "N");
		    dataHM.put("Substitute_Suffix", "00");
		    dataHM.put("Print_PackList_Control", "Y");
		    dataHM.put("Print_PackList_Object", "");
		    dataHM.put("Print_Shipping_Label_Control", "Y");
// "Print_Shipping_Label_Object", "8",
dataHM.put("Print_Shipping_Label_Object", "");
		    dataHM.put("Print_Price_Label_Control", "N");
		    dataHM.put("Print_Price_Label_Object", "");
		    dataHM.put("Print_Other_Doc_Control", "N");
		    dataHM.put("Print_Other_Doc_Object", "");

			dataHM.put("COD_Invoice_Amount", new Double(getOrderAmount(wdsConnection, order_num)));

			dataHM.put("Declared_Value", "");
		    dataHM.put("Post_Pick_Process_#1_Control", "N");
		    dataHM.put("Post_Pick_Process_#1_Profile", "");
		    dataHM.put("Post_Pick_Process_#2_Control", "N");
		    dataHM.put("Post_Pick_Process_#2_Profile", "");
		    dataHM.put("Post_Pick_Process_#3_Control", "N");
		    dataHM.put("Post_Pick_Process_#3_Profile", "");

		    {
				ArrayList<HashMap<String, Object>> ocommentAL = null;
				// ocommentAL = JDBCUtils.runQuery(wdsConnection, "select substr(comment_text, 1, 90) from pub.ocomment where order_num = '" + order_num + "' and line_num = 0 FOR UPDATE WITH (READPAST NOWAIT)");
				ocommentAL = JDBCUtils.runQuery(wdsConnection, "select substr(comment_text, 1, 90) from pub.ocomment where order_num = '" + order_num + "' and line_num = 0 WITH (NOLOCK)");
				if ((ocommentAL == null) || (ocommentAL.size() == 0)) {
				    dataHM.put("Order_Comments", "");
				} else {
					HashMap<String, Object> ocommentHM = ocommentAL.get(0);

				    dataHM.put("Order_Comments", ocommentHM.get("substr(comment_text,1,90)"));
				}
		    }
	}

	public static double getOrderAmount(Connection wdsConnection, String order_num) throws SQLException {
		try {
			ArrayList<HashMap<String, Object>> oitemAL = null;
			// oitemAL = JDBCUtils.runQuery(wdsConnection, "select line_num, qty_ord, qty_cancel, unit_price, priceper_qty from pub.oitem where order_num = '" + order_num + "' FOR UPDATE WITH (READPAST NOWAIT)");
			oitemAL = JDBCUtils.runQuery(wdsConnection, "select line_num, qty_ord, qty_cancel, unit_price, priceper_qty from pub.oitem where order_num = '" + order_num + "' WITH (NOLOCK)");
			if ((oitemAL == null) || (oitemAL.size() == 0)) {
				throw new RuntimeException("oitem data is missing for order_num: " + order_num);
			}

			double totalPrice = 0;
			for (Iterator<HashMap<String, Object>> iterator = oitemAL.iterator(); iterator.hasNext();) {
				HashMap<String, Object> oitemHM = (HashMap<String, Object>) iterator.next();
				double line_num = ((Number) oitemHM.get("line_num")).doubleValue();
				double qty_ord = ((Number) oitemHM.get("qty_ord")).doubleValue();
				double qty_cancel = ((Number) oitemHM.get("qty_cancel")).doubleValue();
				double unit_price = ((Number) oitemHM.get("unit_price")).doubleValue();
				double priceper_qty = ((Number) oitemHM.get("priceper_qty")).doubleValue();


				if ((unit_price >= 0) && (priceper_qty > 0)) {
					totalPrice += ((qty_ord - qty_cancel		) *
						           (unit_price / priceper_qty	)   );
				} else {
					return -1;
				}


				ArrayList<HashMap<String, Object>> oaddonAL = null;
				// oaddonAL = JDBCUtils.runQuery(wdsConnection, "select add_amt_bal from pub.oaddon where order_num = '" + order_num + "' and line_num = " + line_num + " FOR UPDATE WITH (READPAST NOWAIT)");
				oaddonAL = JDBCUtils.runQuery(wdsConnection, "select add_amt_bal from pub.oaddon where order_num = '" + order_num + "' and line_num = " + line_num + " WITH (NOLOCK)");
				if ((oaddonAL != null) &&  (oaddonAL.size() > 0)) {
					for (Iterator<HashMap<String, Object>> iterator2 = oaddonAL.iterator(); iterator2.hasNext();) {
						HashMap<String, Object> oaddonHM = (HashMap<String, Object>) iterator2.next();
						double add_amt_bal = ((Number) oaddonHM.get("add_amt_bal")).doubleValue();
						totalPrice += add_amt_bal;
					}
				}
			}

			ArrayList<HashMap<String, Object>> oaddonAL = null;
			// oaddonAL = JDBCUtils.runQuery(wdsConnection, "select add_amt_bal from pub.oaddon where order_num = '" + order_num + "' and line_num = 0 FOR UPDATE WITH (READPAST NOWAIT)");
			oaddonAL = JDBCUtils.runQuery(wdsConnection, "select add_amt_bal from pub.oaddon where order_num = '" + order_num + "' and line_num = 0 WITH (NOLOCK)");
			if ((oaddonAL != null) &&  (oaddonAL.size() > 0)) {
				for (Iterator<HashMap<String, Object>> iterator3 = oaddonAL.iterator(); iterator3.hasNext();) {
					HashMap<String, Object> oaddonHM = (HashMap<String, Object>) iterator3.next();
					double add_amt_bal = ((Number) oaddonHM.get("add_amt_bal")).doubleValue();
					totalPrice += add_amt_bal;
				}
			}

			return totalPrice;
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			log.debug("Exception occurred while calculating total price for order_num: " + order_num, e);
			return -1;
		}
	}

	// -------------------------------------------------------------------------------------------

	public String getKey1() {
		return order_num;
	}
	
    public String getKey2() {
		return "";    	
    }
    
	// -------------------------------------------------------------------------------------------

    public static void main(String[] args) {
		DBConnector dbConnector8 = null;
		try {
			dbConnector8 = new DBConnector(false);

			Message_1310 message_1310 = new Message_1310(dbConnector8.getConnectionWDS(), "12736", "A");
			String fascorMessageStr = FasUtils.buildFascorMessage(message_1310);

			dbConnector8.closeConnections();
		} catch (Exception e) {
			if (dbConnector8 != null) {
				try {
					dbConnector8.closeConnections();
				} catch (Exception | PortalException e2) {
				}
			}
			e.printStackTrace();
		} catch (PortalException e) {
			if (dbConnector8 != null) {
				try {
					dbConnector8.closeConnections();
				} catch (Exception | PortalException e2) {
				}
			}
			e.getE().printStackTrace();
		}
	}
}
