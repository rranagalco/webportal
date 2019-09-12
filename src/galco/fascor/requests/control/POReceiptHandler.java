package galco.fascor.requests.control;

import galco.fascor.process.FascorProcessor;
import galco.fascor.utils.FasUtils;
import galco.portal.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class POReceiptHandler {
	private static Logger log = Logger.getLogger(POReceiptHandler.class);

	public static HashMap<String, Object> po_rcpt;

	public static void build_PO_RCPT_Record(Connection wdsConnection, String order_num, String receiver_nbr, String receivedBy) throws SQLException {

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		/*
		String nextReceiver;
		{
			ArrayList<HashMap<String, Object>> codesAL = JDBCUtils.runQuery(wdsConnection, "select misc_alpha1 from pub.codes where code_type = 'NEXT-RCVR' WITH (NOLOCK)");
	    	if ((codesAL == null) || (codesAL.size() == 0)) {
	    		throw new RuntimeException("code_type 'NEXT-RCVR' is missing from pub.codes");
	    	}
	    	nextReceiver = "" + (new Integer((String) codesAL.get(0).get("misc_alpha1")) + 1);

			String sqlStatement = "update pub.codes set misc_alpha1 = '" + nextReceiver + "' where code_type = 'NEXT-RCVR'";
			log.debug(sqlStatement);
			JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStatement);

			if (nextReceiver.length() < 8) {
				nextReceiver = "00000000".substring(0, 8 - nextReceiver.length()) + nextReceiver;
			}
		}
		*/
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		HashMap<String, Object> porder;
		{
			ArrayList<HashMap<String, Object>> porderAL = JDBCUtils.runQuery(wdsConnection, "select order_type, date_issued, vendor_num, shipto_loc from pub.porder where order_num = '" + order_num + "' FOR UPDATE WITH (READPAST NOWAIT)");
	    	if ((porderAL == null) || (porderAL.size() != 1)) {
	    		throw new RuntimeException("PO, " + order_num + ", is missing/locked.");
	    	}

	    	porder = porderAL.get(0);
		}

		String addr_num = "";
		{
			ArrayList<HashMap<String, Object>> vendaddrAL = JDBCUtils.runQuery(wdsConnection, "select addr_num from pub.vendaddr where vendor_num = '" + porder.get("vendor_num") + "' and addr_type = 'Purchase' WITH (NOLOCK)");
	    	if ((vendaddrAL != null) && (vendaddrAL.size() > 0)) {
	    		addr_num = (String) vendaddrAL.get(0).get("addr_num");
	    	}
		}

		HashMap<String, Object> location;
		{
			ArrayList<HashMap<String, Object>> locationAL = JDBCUtils.runQuery(wdsConnection, "select company, div_num, dept_num from pub.location where location = '" + porder.get("shipto_loc") + "'");
	    	if ((locationAL == null) || (locationAL.size() == 0)) {
	    		throw new RuntimeException("Location " + porder.get("shipto_loc") + " is missing.");
	    	}
	    	location = locationAL.get(0);
		}

		po_rcpt = new HashMap<String, Object>(20);

		po_rcpt.put("rcvg_reportnum", "" + receiver_nbr);

		po_rcpt.put("company", location.get("company"));
		po_rcpt.put("div_num", location.get("div_num"));
		po_rcpt.put("dept_num", location.get("dept_num"));
		po_rcpt.put("proj_num", "");

		po_rcpt.put("location", porder.get("shipto_loc"));

		po_rcpt.put("order_num", order_num);
		po_rcpt.put("order_type", porder.get("order_type"));
		po_rcpt.put("date_issued", FasUtils.convertDateToMMDDYY((Date) porder.get("date_issued")));
		po_rcpt.put("vendor_num", porder.get("vendor_num"));
		po_rcpt.put("addr_num", addr_num);

		po_rcpt.put("date_receive", FasUtils.convertDateToMMDDYY(new Date()));

		// po_rcpt.put("received_by", "aja");
		// po_rcpt.put("received_by", receivedBy);
		if (receivedBy == null) {
			po_rcpt.put("received_by", "");
		} else {
			if (receivedBy.length() >= 3) {
				po_rcpt.put("received_by", receivedBy.substring(0, 3));					
			} else {
				po_rcpt.put("received_by", receivedBy);				
			}
		}
		
		po_rcpt.put("audit_userid", "Fascor");

		po_rcpt.put("rcvd_items", (int) 0);
		po_rcpt.put("qty_received", (int) 0);
		po_rcpt.put("amt_received", (double) 0.0);

		// qqq

		{
	    	HashMap<String, Object> maxLineNumHM = JDBCUtils.runQuery_GetSingleRowOnly(wdsConnection, "select order_num, max(line_num) from pub.pitem where order_num = '" + order_num + "' group by order_num");
	    	if (maxLineNumHM != null) {
	    		po_rcpt.put("orig_items", maxLineNumHM.get("max(line_num)"));
	    	}
		}

		FasUtils.printHashMap(po_rcpt);

    	String sqlInsertStmt = FasUtils.buildSQLInsertStatement(po_rcpt, "pub.po_rcpt");
    	log.debug(sqlInsertStmt);
		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlInsertStmt);
	}

	public static void build_PI_RCPT_Record(Connection wdsConnection, String order_num, int line_num, int qtyReceivedInThisReceipt) throws SQLException {
		HashMap<String, Object> pitem;
		{
			ArrayList<HashMap<String, Object>> pitemAL = JDBCUtils.runQuery(wdsConnection, "select part_num, unit_cost, qty_ord, qty_received, qty_cancel, purch_unit, priceper_qty, purch_qty from pub.pitem where order_num = '" + order_num + "' and line_num = " + line_num + " FOR UPDATE WITH (READPAST NOWAIT)");
	    	if ((pitemAL == null) || (pitemAL.size() != 1)) {
	    		throw new RuntimeException("PO line , " + order_num + " " + line_num + ", is missing/locked.");
	    	}
	    	pitem = pitemAL.get(0);

		}

        int orig_qty_due = ((Number) pitem.get("qty_ord")).intValue() - ((Number) pitem.get("qty_received")).intValue() - ((Number)pitem.get("qty_cancel")).intValue();
        // double w_ttl = Utils.roundTo2Decimals((orig_qty_due * ((Number) pitem.get("unit_cost")).doubleValue()) / ((Number) pitem.get("priceper_qty")).doubleValue());
        double w_ttl = FasUtils.roundTo2Decimals((qtyReceivedInThisReceipt * ((Number) pitem.get("unit_cost")).doubleValue()) / ((Number) pitem.get("priceper_qty")).doubleValue());

		HashMap<String, Object> pi_rcpt = new HashMap<String, Object>(20);

		pi_rcpt.put("company", po_rcpt.get("company"));
		pi_rcpt.put("div_num", po_rcpt.get("div_num"));
		pi_rcpt.put("dept_num", po_rcpt.get("dept_num"));
		pi_rcpt.put("proj_num", po_rcpt.get("proj_num"));
		pi_rcpt.put("rcvg_reportnum", po_rcpt.get("rcvg_reportnum"));
		pi_rcpt.put("order_num", po_rcpt.get("order_num"));
		pi_rcpt.put("line_type", "O");
		pi_rcpt.put("line_num", line_num);
		pi_rcpt.put("part_num", pitem.get("part_num"));
		pi_rcpt.put("qty_received", qtyReceivedInThisReceipt);
		pi_rcpt.put("qty_cancel", new Integer(0));
		pi_rcpt.put("unit_cost", pitem.get("unit_cost"));
		pi_rcpt.put("total_cost", w_ttl);
		pi_rcpt.put("rcpt_unit", pitem.get("purch_unit"));
		pi_rcpt.put("qty_per_unit", pitem.get("purch_qty"));
		pi_rcpt.put("binnum", "");
		pi_rcpt.put("prev_qtybal", orig_qty_due);

	    pi_rcpt.put("manuf_lotnum", "");
	    pi_rcpt.put("product_lotnum", "");
	    pi_rcpt.put("serial_num", "");

	    {
	    	HashMap<String, Object> porderHM = JDBCUtils.runQuery_GetSingleRowOnly(wdsConnection, "select terms from pub.porder where order_num = '" + order_num + "' WITH (NOLOCK)");
	    	if (porderHM != null) {
	    		HashMap<String, Object> termsHM = JDBCUtils.runQuery_GetSingleRowOnly(wdsConnection, "select misc_log1 from pub.terms where terms = '" + porderHM.get("terms") + "'");
		    	if (termsHM != null) {
		    		Boolean misc_log1 = (Boolean) termsHM.get("misc_log1");
		    		if ((misc_log1 != null) && (misc_log1 == true)) {
		    		    pi_rcpt.put("valid_stat", "C");
		    		}
		    	}
	    	}
	    }


		{
			ArrayList<HashMap<String, Object>> paddonAL = JDBCUtils.runQuery(wdsConnection, "select line_num, seq_num, addon, description, add_amt_bal, include_cost, inv_acct, value_type from pub.paddon where order_num = '" + order_num + "' and line_num = " + line_num + " WITH (NOLOCK)");
	    	if ((paddonAL != null) && (paddonAL.size() > 0)) {
	    		for (Iterator<HashMap<String, Object>> iterator = paddonAL.iterator(); iterator.hasNext();) {
					HashMap<String, Object> paddonHashMap = iterator.next();

					HashMap<String, Object> pa_rcpt = new HashMap<String, Object>(20);

					pa_rcpt.put("company", po_rcpt.get("company"));
					pa_rcpt.put("div_num", po_rcpt.get("div_num"));
					pa_rcpt.put("dept_num", po_rcpt.get("dept_num"));
					pa_rcpt.put("proj_num", po_rcpt.get("proj_num"));
					pa_rcpt.put("rcvg_reportnum", po_rcpt.get("rcvg_reportnum"));
					pa_rcpt.put("order_num", po_rcpt.get("order_num"));
					pa_rcpt.put("line_type", "O");
					pa_rcpt.put("line_num", paddonHashMap.get("line_num"));
					pa_rcpt.put("seq_num", paddonHashMap.get("seq_num"));
					pa_rcpt.put("addon", paddonHashMap.get("addon"));
					pa_rcpt.put("description", paddonHashMap.get("description"));
					pa_rcpt.put("include_cost", paddonHashMap.get("include_cost"));
					pa_rcpt.put("gl_acct", paddonHashMap.get("inv_acct"));


					double paddon_add_amt_bal = ((Number) paddonHashMap.get("add_amt_bal")).doubleValue();
					if (qtyReceivedInThisReceipt != orig_qty_due) {
						String value_type = (String) paddonHashMap.get("value_type");

						double adjusted_paddon_add_amt_bal = paddon_add_amt_bal;

						if ((value_type != null) && (value_type.compareToIgnoreCase("F") != 0)) {
							adjusted_paddon_add_amt_bal = FasUtils.roundTo2Decimals((paddon_add_amt_bal * qtyReceivedInThisReceipt) / orig_qty_due);
							pa_rcpt.put("addon_amt", adjusted_paddon_add_amt_bal);
						} else {
							pa_rcpt.put("addon_amt", paddon_add_amt_bal);
						}

						w_ttl = w_ttl + adjusted_paddon_add_amt_bal;
					} else {
						pa_rcpt.put("addon_amt", paddon_add_amt_bal);
						w_ttl = w_ttl + paddon_add_amt_bal;
					}

					FasUtils.printHashMap(pa_rcpt);
					String sqlInsertStmt = FasUtils.buildSQLInsertStatement(pa_rcpt, "pub.pa_rcpt");
			    	log.debug(sqlInsertStmt);
	    		}

			    pi_rcpt.put("total_cost", w_ttl);
	    	}
		}


		{
			String query =
					"select addon_amt from pub.pa_rcpt " +
					"where " +
					"rcvg_reportnum = '" + po_rcpt.get("rcvg_reportnum") + "' and " +
					"order_num = '" + po_rcpt.get("order_num") + "' and " +
					"line_type = 'O' and " +
					"line_num = " + line_num + " and " +
					"include_cost = 0 WITH (NOLOCK)";

			ArrayList<HashMap<String, Object>> pa_rcptAL = JDBCUtils.runQuery(wdsConnection, query);
	    	if ((pa_rcptAL != null) && (pa_rcptAL.size() > 0)) {
	    		double non_cost_add = 0;
	    		for (Iterator<HashMap<String, Object>> iterator = pa_rcptAL.iterator(); iterator.hasNext();) {
					HashMap<String, Object> pa_rcptHashMap = iterator.next();

					non_cost_add += ((Number) pa_rcptHashMap.get("addon_amt")).doubleValue();
	    		}
	    	    pi_rcpt.put("non_cost_add", non_cost_add);
	    	}
		}

		FasUtils.printHashMap(pi_rcpt);

    	String sqlInsertStmt = FasUtils.buildSQLInsertStatement(pi_rcpt, "pub.pi_rcpt");
    	log.debug(sqlInsertStmt);
		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlInsertStmt);

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		// po_rcpt.put("rcvd_items", ((Integer) po_rcpt.get("rcvd_items")) + 1);
		// po_rcpt.put("qty_received", ((Integer) po_rcpt.get("rcvd_items")) + qtyReceivedInThisReceipt);
		// po_rcpt.put("amt_received", ((Double) po_rcpt.get("amt_received")) + w_ttl);

		int rcvd_items = (int) po_rcpt.get("rcvd_items");
		int qty_received = (int) po_rcpt.get("qty_received");
		double amt_received = (double) po_rcpt.get("amt_received");

		rcvd_items += 1;
		qty_received += qtyReceivedInThisReceipt;
		amt_received += w_ttl;

		po_rcpt.put("rcvd_items", rcvd_items);
		po_rcpt.put("qty_received", qty_received);
		po_rcpt.put("amt_received", amt_received);

		String updateSQL = "update pub.po_rcpt set " + "rcvd_items = " + rcvd_items + ", " +
													   "qty_received = " + qty_received + ", " +
													   "amt_received = " + amt_received + " " +
								  "where rcvg_reportnum = '" + po_rcpt.get("rcvg_reportnum") + "'";
    	log.debug(updateSQL);

		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, updateSQL);
	}

	public static void receivePO(Connection wdsConnection, String order_num) throws SQLException {
		// build_PO_RCPT_Record(wdsConnection, order_num);
		// build_PI_RCPT_Record(wdsConnection, order_num, 1, 1);
		// build_PI_RCPT_Record(wdsConnection, order_num, 2, 2);
	}
}
