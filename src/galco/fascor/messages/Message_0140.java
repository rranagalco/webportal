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

public class Message_0140 extends MessageAbs implements Message {
	private static Logger log = Logger.getLogger(Message_0140.class);

	String vendor_num;

	// -------------------------------------------------------------------------------------------

	public Message_0140(Connection wdsConnection, String vendor_num, String action) throws SQLException, RuntimeException {
		super("0140", action);
		this.vendor_num = vendor_num;

		getDataFromWDS(wdsConnection, vendor_num, action);
		fascorMessage = FasUtils.buildFascorMessage(this);
	}

	// -------------------------------------------------------------------------------------------

	public void getDataFromWDS(Connection wdsConnection, String vendor_num, String action) throws SQLException, RuntimeException {

		ArrayList<HashMap<String, Object>> vendorAL = null;
		log.debug("Before " + System.currentTimeMillis());
		
												// orig - vendorAL = JDBCUtils.runQuery(wdsConnection, "select vendor_num, name, address, address2, city, state, zip, country from pub.vendor where vendor_num = '" + vendor_num + "' FOR UPDATE NOWAIT");
		// vendorAL = JDBCUtils.runQuery(wdsConnection, "select vendor_num, name, address, address2, city, state, zip, country from pub.vendor where vendor_num = '" + vendor_num + "' WITH (READPAST NOWAIT)");
		// vendorAL = JDBCUtils.runQuery(wdsConnection, "select vendor_num, name, address, address2, city, state, zip, country from pub.vendor where vendor_num = '" + vendor_num + "' FOR UPDATE WITH (READPAST NOWAIT)");
		vendorAL = JDBCUtils.runQuery(wdsConnection, "select vendor_num, name, address, address2, city, state, zip, country from pub.vendor where vendor_num = '" + vendor_num + "' WITH (NOLOCK)");	
		
		if ((vendorAL == null) || (vendorAL.size() != 1)) {
			throw new RuntimeException("Vendor data is missing for vendor: " + vendor_num);
			// throw new FascorLockedRecordException("Vendor data is missing/incorrect/locked for vendor: " + vendor_num);			
		}
		HashMap<String, Object> vendorHM = vendorAL.get(0);

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		dataHM.put("Message_Type", "0140");

		dataHM.put("Mode", action);

		dataHM.put("Facility_Nbr", Parms.FASCOR_FACILITY_NBR);

		dataHM.put("Vendor_ID", vendorHM.get("vendor_num"));
		dataHM.put("Vendor_Name", vendorHM.get("name"));
		dataHM.put("Vendor_Address1", vendorHM.get("address"));
		dataHM.put("Vendor_Address2", vendorHM.get("address2"));
		dataHM.put("Vendor_Address3", "");
		dataHM.put("Vendor_City", vendorHM.get("city"));
		dataHM.put("Vendor_State", vendorHM.get("state"));
		dataHM.put("Vendor_Zip", vendorHM.get("zip"));
		dataHM.put("Vendor_Country", vendorHM.get("country"));
	}

	// -------------------------------------------------------------------------------------------

	public static HashMap<String, String> FASCOR_TO_WDS_FIELD_MAPPING_HM = new HashMap<String, String>(20); 
	public static String FASCOR_FIELD_LIST_FOR_COMPARISON;
	static {
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Vendor_ID", "Vendor_ID");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Vendor_Name", "Vendor_Name");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Vendor_Address1", "Vendor_Address1");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Vendor_Address2", "Vendor_Address2");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Vendor_City", "Vendor_City");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Vendor_State", "Vendor_State");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Vendor_Zip", "Vendor_Zip");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Vendor_Country", "Vendor_Country");
		
		FASCOR_FIELD_LIST_FOR_COMPARISON = "";
		Iterator<Entry<String, String>> it = FASCOR_TO_WDS_FIELD_MAPPING_HM.entrySet().iterator();
		
		while (it.hasNext()) {
			Map.Entry<String, String> pair = (Entry<String, String>) it.next();
			String key = pair.getKey();		
			FASCOR_FIELD_LIST_FOR_COMPARISON += ((FASCOR_FIELD_LIST_FOR_COMPARISON.compareTo("") != 0)?", ":"") + key;		
		}
	};

	public String compareFieldsWithFascor(Connection sqlServerConnection) throws SQLException {
    	ArrayList<HashMap<String, Object>> fascorAL = JDBCUtils.runQuery(sqlServerConnection, "select " + FASCOR_FIELD_LIST_FOR_COMPARISON + " from Vendor where Vendor_ID = '" + vendor_num + "'");
    	if ((fascorAL != null) && (fascorAL.size() > 0)) {
    		return 	compareWDSAndFascorFields(dataHM, fascorAL.get(0), FASCOR_TO_WDS_FIELD_MAPPING_HM);
    	} else {
    		return "not found in Fascor";
    	}
	}
	
	// -------------------------------------------------------------------------------------------
	
	public String getKey1() {
		return vendor_num;
	}
	
    public String getKey2() {
		return "";    	
    }
       
	// -------------------------------------------------------------------------------------------

	public static void main(String[] args) {
		DBConnector dbConnector8 = null;
		try {
			dbConnector8 = new DBConnector(false);

			Message_0140 message_0140 = new Message_0140(dbConnector8.getConnectionWDS(), "12736", "A");
			String fascorMessageStr = FasUtils.buildFascorMessage(message_0140);

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
