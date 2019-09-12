package galco.fascor.messages;

import galco.fascor.utils.FasUtils;
import galco.portal.config.Parms;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Message_1420 extends MessageAbs implements Message {
	private static Logger log = Logger.getLogger(Message_1420.class);

	String part_num;
	int qty_onhand_change;

	// -------------------------------------------------------------------------------------------

	public Message_1420(Connection wdsConnection, Connection sqlServerConnection, String part_num, int qty_onhand_change, String action) throws SQLException, RuntimeException {
		super("1420", action);
		this.part_num = part_num;
		this.qty_onhand_change = qty_onhand_change;

		getDataFromWDS(wdsConnection, sqlServerConnection, part_num, action, null);
		fascorMessage = FasUtils.buildFascorMessage(this);
	}

	public Message_1420(Connection wdsConnection, Connection sqlServerConnection, String part_num, int qty_onhand_change, String action, String fascorBinNum) throws SQLException, RuntimeException {
		super("1420", action);
		this.part_num = part_num;
		this.qty_onhand_change = qty_onhand_change;

		getDataFromWDS(wdsConnection, sqlServerConnection, part_num, action, fascorBinNum);
		fascorMessage = FasUtils.buildFascorMessage(this);
	}

	// -------------------------------------------------------------------------------------------

	public void getDataFromWDS(Connection wdsConnection, Connection sqlServerConnection, String part_num, String action, String fascorBinNum) throws SQLException, RuntimeException {
		dataHM.put("Message_Type", "1420");
        dataHM.put("Timestamp", FasUtils.convertDateTo_yyyyMmDdHmMmSs(new Date()));
		dataHM.put("User_ID", "");
		dataHM.put("Mode", action);
		dataHM.put("Message_ID", "");
		dataHM.put("Facility_Nbr", Parms.FASCOR_FACILITY_NBR);

		
		
		if (fascorBinNum == null) {
			fascorBinNum = "";
				
	    	ArrayList<String> partlocAL = JDBCUtils.runQuery_ReturnStrALs(wdsConnection, "select normal_binnum from pub.partloc where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "'")[0];
	    	if ((partlocAL == null) || (partlocAL.size() == 0)) {
	    		// fascorBinNum = "";
	    		fascorBinNum = "FIX0000000";
	    	} else {
	    		String normal_binnum = partlocAL.get(0);

	    		if ((normal_binnum != null) && (normal_binnum.compareTo("") != 0)) {
			    	// ArrayList<String> wdsToFCLocMapAL = JDBCUtils.runQuery_ReturnStrALs(sqlServerConnection, "select FASCOR from WDSToFCLocMap where WDS = '" + normal_binnum + "'")[0];
			    	ArrayList<String> wdsToFCLocMapAL = JDBCUtils.runQuery_ReturnStrALs(sqlServerConnection, "select FASCOR from WDSToFCLocMap where WDS = '" + Utils.replaceSingleQuotesIfNotNull(normal_binnum) + "'")[0];
			    	if ((wdsToFCLocMapAL == null) || (wdsToFCLocMapAL.size() != 1)) {
			    		// fascorBinNum = "";
			    		fascorBinNum = "FIX0000000";
			    	} else {
			    		fascorBinNum = wdsToFCLocMapAL.get(0);
			    	}
	    		} else {
		    		// fascorBinNum = "";
		    		fascorBinNum = "FIX0000000";
	    		}
	    	}
		}
		dataHM.put("Location", fascorBinNum);
		dataHM.put("MU_ID", fascorBinNum);

		
		
		dataHM.put("SKU", part_num);
		dataHM.put("Class", "01");
		dataHM.put("Qty", Math.abs(qty_onhand_change));
		dataHM.put("Qty_Sign", ((qty_onhand_change >= 0)?"+":"-"));
		dataHM.put("Function", "");
		dataHM.put("Reason_Code", "66");
		dataHM.put("Comments", "Automatic inventory adjustment from WDS");
		dataHM.put("Lot_ID", "");
		dataHM.put("Vendor_ID", "");
		dataHM.put("Manufacturing_Date", "");
		dataHM.put("Expiration_Date", "");
		dataHM.put("Function_ID", "");
		dataHM.put("Serial_Number", "");
	}

	// -------------------------------------------------------------------------------------------

	public String getKey1() {
		return part_num;
	}

    public String getKey2() {
		return qty_onhand_change + "";
    }

	// -------------------------------------------------------------------------------------------

}
