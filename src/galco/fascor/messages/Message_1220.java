package galco.fascor.messages;

import galco.fascor.process.FascorProcessor;
import galco.fascor.utils.FasUtils;
import galco.portal.config.Parms;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Message_1220 extends MessageAbs implements Message {
	private static Logger log = Logger.getLogger(Message_1220.class);

	String order_num;
	int line_num;
	String part_num;

	// -------------------------------------------------------------------------------------------

	public Message_1220(Connection wdsConnection, String order_num, int line_num, String action) throws SQLException, RuntimeException {
		super("1220", action);
		this.order_num = order_num;
		this.line_num = line_num;

		getDataFromWDS(wdsConnection, order_num, line_num, action);
		fascorMessage = FasUtils.buildFascorMessage(this);
	}

	// -------------------------------------------------------------------------------------------

	public void getDataFromWDS(Connection wdsConnection, String order_num, int line_num, String action) throws SQLException, RuntimeException {

		ArrayList<HashMap<String, Object>> pitemAL = null;
		// pitemAL = JDBCUtils.runQuery(wdsConnection, "select order_num, line_num, part_num, purch_unit, purch_qty, qty_ord, qty_cancel, vend_desc1, vend_desc2, vend_desc3, vend_partnum, cust_ordnum from pub.pitem where order_num = '" + order_num + "' and line_num = " + line_num + " FOR UPDATE WITH (READPAST NOWAIT)");
		pitemAL = JDBCUtils.runQuery(wdsConnection, "select order_num, line_num, part_num, purch_unit, purch_qty, qty_ord, qty_received, qty_cancel, vend_desc1, vend_desc2, vend_desc3, vend_partnum, cust_ordnum from pub.pitem where order_num = '" + order_num + "' and line_num = " + line_num + " WITH (NOLOCK)");
		if ((pitemAL == null) || (pitemAL.size() != 1)) {
			throw new RuntimeException("pitem data is missing for order_num: " + order_num + ", line_num: " + line_num);		
		}
		HashMap<String, Object> pitemHM = pitemAL.get(0);

		ArrayList<HashMap<String, Object>> porderAL = null;
		// porderAL = JDBCUtils.runQuery(wdsConnection, "select rush_shpmnt from pub.porder where order_num = '" + order_num + "' FOR UPDATE WITH (READPAST NOWAIT)");
		porderAL = JDBCUtils.runQuery(wdsConnection, "select rush_shpmnt from pub.porder where order_num = '" + order_num + "' WITH (NOLOCK)");
		if ((porderAL == null) || (porderAL.size() != 1)) {
			throw new RuntimeException("porder data is missing for order_num: " + order_num);
		}
		HashMap<String, Object> porderHM = porderAL.get(0);

		String part_num = (String) pitemHM.get("part_num");
		this.part_num = part_num;
		ArrayList<HashMap<String, Object>> partAL = null;
		// partAL = JDBCUtils.runQuery(wdsConnection, "select upc_code, description, description2, description3 from pub.part where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' FOR UPDATE WITH (READPAST NOWAIT)");
		partAL = JDBCUtils.runQuery(wdsConnection, "select upc_code, description, description2, description3 from pub.part where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' WITH (NOLOCK)");
		if ((partAL == null) || (partAL.size() != 1)) {
			throw new RuntimeException("order_num: " + order_num + ", line_num: " + line_num + ". Part data is missing for part: " + part_num);
		}
		HashMap<String, Object> partHM = partAL.get(0);

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		dataHM.put("Message_Type", "1220");

		dataHM.put("Mode", action);

		dataHM.put("Facility_Nbr", Parms.FASCOR_FACILITY_NBR);

		// order_num, line_num, part_num, qty_ord, qty_cancel, vend_desc1, vend_desc2, vend_desc3, vend_partnum

		dataHM.put("PO_Nbr", pitemHM.get("order_num"));
		dataHM.put("PO_Line_Nbr", pitemHM.get("line_num"));

		dataHM.put("SKU", part_num.toUpperCase());
		// log.debug("DANGER DANGER DANGER - Modifying part_num");
		// log.debug("DANGER DANGER DANGER - Modifying part_num");
		// log.debug("DANGER DANGER DANGER - Modifying part_num");
		// dataHM.put("SKU", part_num + "-DANGER");


		dataHM.put("UPC", partHM.get("upc_code"));

		dataHM.put("Class", "01");

		dataHM.put("Unit_Of_Measure", pitemHM.get("purch_unit"));

		try {
			int qty_ord = ((Number) pitemHM.get("qty_ord")).intValue();
			// int qty_received = ((Number) pitemHM.get("qty_received")).intValue();
			int qty_cancel = ((Number) pitemHM.get("qty_cancel")).intValue();
			
			int current_qty_ord = qty_ord - qty_cancel;
			// int current_qty_ord = qty_ord - qty_received - qty_cancel;

			Number purch_qtyNum = (Number) pitemHM.get("purch_qty");
			if ((purch_qtyNum == null) || (purch_qtyNum.intValue() <= 1)) {
				dataHM.put("Ordered_Qty", current_qty_ord);
			} else {
				dataHM.put("Ordered_Qty", new Integer(current_qty_ord / purch_qtyNum.intValue()));
			}
		} catch (Exception e) {
			dataHM.put("Ordered_Qty", new Integer(0));
		}

		String[] fiftyByteStringArray = FasUtils.splitInto50ByteStrings((String) partHM.get("description"), (String) partHM.get("description2"), (String) partHM.get("description3"));
		dataHM.put("Description1", fiftyByteStringArray[0]);
		dataHM.put("Description2", fiftyByteStringArray[1]);

		dataHM.put("Vendor_Part_Nbr", pitemHM.get("vend_partnum"));

		/*
		Boolean rush_shpmnt = (Boolean) porderHM.get("rush_shpmnt");
		if ((rush_shpmnt == null) || (rush_shpmnt == false)) {
			dataHM.put("Hot", "N");
		} else {
			dataHM.put("Hot", "Y");
		}
		*/

		ArrayList<HashMap<String, Object>> oitemAL = null;
		oitemAL = JDBCUtils.runQuery(wdsConnection, "select top 1 oitem.order_num, oitem.part_num from pub.oitem, pub.order where oitem.part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and oitem.backorder = 1 and oitem.order_num = order.order_num and order.date_closed IS NULL and order.on_cr_hold = 0 and order.on_hold = 0 and order.drop_ship = 0 and order.pros_status[3] <> 'v' WITH (NOLOCK)");
		if ((oitemAL != null) && (oitemAL.size() > 0)) {
			dataHM.put("Hot", "Y");
			log.debug("Hot : Y, one order waiting for this part: " + (oitemAL.get(0).get("order_num")));
		} else {
			boolean reqWaiting = false;

			String cust_ordnum = (String) pitemHM.get("cust_ordnum");
			if ((cust_ordnum != null) && (cust_ordnum.compareTo("") != 0)) {
				String openReqsSQLStmt =
					"select top 1 part_num from pub.roitem where " +
							"req_num = '" + cust_ordnum + "' and " +
							"part_num = '" + part_num + "' and " +
							"on_hold = 0 and date_closed IS NULL and qty_in_pick = 0 and qty_bal > 0 WITH (NOLOCK)";
				ArrayList<HashMap<String, Object>> roitemAL = null;
				roitemAL = JDBCUtils.runQuery(FascorProcessor.dbConnector8.getConnectionSRO(), openReqsSQLStmt);
				if ((roitemAL != null) && (roitemAL.size() > 0)) {
					reqWaiting = true;

					dataHM.put("Hot", "Y");
					log.debug("Hot : Y, req is waiting for this part, req: " + cust_ordnum + ", part: " + part_num);
				}
			}

			if (reqWaiting == false) {
				log.debug("Hot : N");
				dataHM.put("Hot", "N");

			}
		}

	}

	// -------------------------------------------------------------------------------------------

	public static HashMap<String, String> FASCOR_TO_WDS_FIELD_MAPPING_HM = new HashMap<String, String>(20); 
	public static String FASCOR_FIELD_LIST_FOR_COMPARISON;
	static {
        FASCOR_TO_WDS_FIELD_MAPPING_HM.put("PO_Nbr", "PO_Nbr");
        FASCOR_TO_WDS_FIELD_MAPPING_HM.put("PO_Line_Nbr", "PO_Line_Nbr");
        FASCOR_TO_WDS_FIELD_MAPPING_HM.put("SKU", "SKU");
        FASCOR_TO_WDS_FIELD_MAPPING_HM.put("SKU_Desc1", "Description1");
        FASCOR_TO_WDS_FIELD_MAPPING_HM.put("SKU_Desc2", "Description2");
        FASCOR_TO_WDS_FIELD_MAPPING_HM.put("UoM", "Unit_Of_Measure");
        FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Ordered_Qty", "Ordered_Qty");
        // FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Hot_Flag", "Hot_Flag");
		
		FASCOR_FIELD_LIST_FOR_COMPARISON = "";
		Iterator<Entry<String, String>> it = FASCOR_TO_WDS_FIELD_MAPPING_HM.entrySet().iterator();
		
		while (it.hasNext()) {
			Map.Entry<String, String> pair = (Entry<String, String>) it.next();
			String key = pair.getKey();		
			FASCOR_FIELD_LIST_FOR_COMPARISON += ((FASCOR_FIELD_LIST_FOR_COMPARISON.compareTo("") != 0)?", ":"") + key;		
		}
	};

	public String compareFieldsWithFascor(Connection sqlServerConnection) throws SQLException {
    	ArrayList<HashMap<String, Object>> fascorAL = JDBCUtils.runQuery(sqlServerConnection, "select " + FASCOR_FIELD_LIST_FOR_COMPARISON + " from PO_Line where PO_Nbr = '" + order_num + "' and PO_Line_Nbr = '" + line_num + "'");
    	if ((fascorAL != null) && (fascorAL.size() > 0)) {
    		return 	compareWDSAndFascorFields(dataHM, fascorAL.get(0), FASCOR_TO_WDS_FIELD_MAPPING_HM);
    	} else {
    		return "not found in Fascor";
    	}
	}
	
	// -------------------------------------------------------------------------------------------

	public String getKey1() {
		return order_num;
	}

    public String getKey2() {
		return line_num + "|" + part_num;
    }

	// -------------------------------------------------------------------------------------------

}
