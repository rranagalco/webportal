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

public class Message_2015 extends MessageAbs implements Message {
	private static Logger log = Logger.getLogger(Message_2015.class);

	String order_num, part_num;
	int quantityToBuild;
	
	// -------------------------------------------------------------------------------------------

	public Message_2015(Connection wdsConnection, String order_num, String part_num, int quantityToBuild, String action) throws SQLException, RuntimeException {
		super("2015", action);
		this.order_num = order_num;
		this.part_num = part_num.toUpperCase();
		this.quantityToBuild = quantityToBuild;

		getDataFromWDS(wdsConnection, order_num, this.part_num, quantityToBuild, action);
	}

	// -------------------------------------------------------------------------------------------

	public void getDataFromWDS(Connection wdsConnection, String order_num, String part_num, int quantityToBuild, String action) throws SQLException, RuntimeException {
        ArrayList<HashMap<String, Object>> part_bomAL = JDBCUtils.runQuery(wdsConnection, "select top 1 eng_rls_num from pub.part_bom where partnum_assm = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and eng_rls_num = (select max(eng_rls_num) from pub.part_bom where partnum_assm = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "')");
        if ((part_bomAL == null) || (part_bomAL.size() != 1)) {
            throw new RuntimeException("part_bom record is missing/incorrect/locked for part: " + part_num);
        }
        String eng_rls_num = (String) ((part_bomAL.get(0)).get("eng_rls_num"));
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        dataHM.put("Message_Type", "2015");
        dataHM.put("Timestamp", FasUtils.convertDateTo_yyyyMmDdHmMmSs(new Date()));
        dataHM.put("User_ID", "");
        dataHM.put("Mode", action);
        dataHM.put("Message_ID", order_num);
        dataHM.put("Facility_Nbr", Parms.FASCOR_FACILITY_NBR);
        dataHM.put("SKU", part_num);
        dataHM.put("BOM_Name", part_num);
        dataHM.put("BOM_Revision", eng_rls_num);
        dataHM.put("Lot_ID", "");
        dataHM.put("Production_Date", FasUtils.convertDateTo_mmDdyyyy(new Date()));
        dataHM.put("Production_Shift", "");
        dataHM.put("Shop_Order_ID", order_num);
        dataHM.put("Sched_Build", quantityToBuild);
        dataHM.put("Expiration_Date", "");
        dataHM.put("Special_SO", "N");
	}

	// -------------------------------------------------------------------------------------------

	public String getKey1() {
		return "";
	}
	
    public String getKey2() {
		return "";    	
    }
    
	// -------------------------------------------------------------------------------------------

}
