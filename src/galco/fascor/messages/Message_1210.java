package galco.fascor.messages;

import galco.fascor.utils.FasUtils;
import galco.portal.config.Parms;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Message_1210 extends MessageAbs implements Message {
	private static Logger log = Logger.getLogger(Message_1210.class);

	String order_num;

	// -------------------------------------------------------------------------------------------

	public Message_1210(Connection wdsConnection, String order_num, String action) throws SQLException, RuntimeException {
		super("1210", action);
		this.order_num = order_num;

		getDataFromWDS(wdsConnection, order_num, action);
		fascorMessage = FasUtils.buildFascorMessage(this);
	}

	// -------------------------------------------------------------------------------------------

	public void getDataFromWDS(Connection wdsConnection, String order_num, String action) throws SQLException, RuntimeException {

		ArrayList<HashMap<String, Object>> porderAL = null;
		// porderAL = JDBCUtils.runQuery(wdsConnection, "select order_num, date_entered, vendor_num, shipvia, terms, buyer, comments[1], comments[2], comments[3] from pub.porder where order_num = '" + order_num + "' FOR UPDATE WITH (READPAST NOWAIT)");
		porderAL = JDBCUtils.runQuery(wdsConnection, "select order_num, date_entered, vendor_num, shipvia, terms, buyer, comments[1], comments[2], comments[3] from pub.porder where order_num = '" + order_num + "' WITH (NOLOCK)");

		if ((porderAL == null) || (porderAL.size() != 1)) {
			throw new RuntimeException("porder data is missing for order_num: " + order_num);
		}
		HashMap<String, Object> porderHM = porderAL.get(0);

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		dataHM.put("Message_Type", "1210");

		dataHM.put("Mode", action);

		dataHM.put("Facility_Nbr", Parms.FASCOR_FACILITY_NBR);

		dataHM.put("PO_Nbr", porderHM.get("order_num"));
		try {
			dataHM.put("PO_Date", FasUtils.convertDateToCCYYMMDD((java.util.Date) porderHM.get("date_entered")));
		} catch (Exception e) {
			dataHM.put("PO_Date", "");
		}
		dataHM.put("Vendor_Nbr", porderHM.get("vendor_num"));
		dataHM.put("Carrier_Code", porderHM.get("shipvia"));
		dataHM.put("Terms_Code", porderHM.get("terms"));
		dataHM.put("Comments", porderHM.get("comments[1]") + " " + porderHM.get("comments[2]") + " " + porderHM.get("comments[3]"));
		dataHM.put("Check_In_Required", "Y");
		dataHM.put("Buyer_Ref_Nbr", porderHM.get("buyer"));
	}

	// -------------------------------------------------------------------------------------------

	public static HashMap<String, String> FASCOR_TO_WDS_FIELD_MAPPING_HM = new HashMap<String, String>(20); 
	public static String FASCOR_FIELD_LIST_FOR_COMPARISON;
	static {
        FASCOR_TO_WDS_FIELD_MAPPING_HM.put("PO_Nbr", "PO_Nbr");
        FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Carrier_Code", "Carrier_Code");
        FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Comments", "Comments");
        FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Vendor_ID", "Vendor_Nbr");
        FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Buyer_Ref_Nbr", "Buyer_Ref_Nbr");
		
		FASCOR_FIELD_LIST_FOR_COMPARISON = "";
		Iterator<Entry<String, String>> it = FASCOR_TO_WDS_FIELD_MAPPING_HM.entrySet().iterator();
		
		while (it.hasNext()) {
			Map.Entry<String, String> pair = (Entry<String, String>) it.next();
			String key = pair.getKey();
			FASCOR_FIELD_LIST_FOR_COMPARISON += ((FASCOR_FIELD_LIST_FOR_COMPARISON.compareTo("") != 0)?", ":"") + key;		
		}
	};

	public String compareFieldsWithFascor(Connection sqlServerConnection) throws SQLException {
    	ArrayList<HashMap<String, Object>> fascorAL = JDBCUtils.runQuery(sqlServerConnection, "select " + FASCOR_FIELD_LIST_FOR_COMPARISON + " from PO where PO_Nbr = '" + order_num + "'");
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
		return "";    	
    }
    
	// -------------------------------------------------------------------------------------------

	public static void main(String[] args) {
		DBConnector dbConnector8 = null;
		try {
			dbConnector8 = new DBConnector(false);

			Message_1210 message_1210 = new Message_1210(dbConnector8.getConnectionWDS(), "12736", "A");
			String fascorMessageStr = FasUtils.buildFascorMessage(message_1210);

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
