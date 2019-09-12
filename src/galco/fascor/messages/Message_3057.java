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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Message_3057 extends MessageAbs implements Message {
	private static Logger log = Logger.getLogger(Message_3057.class);

	String part_num, eng_rls_num;
	int seq_num;
	String partnum_comp;

	// -------------------------------------------------------------------------------------------

	public Message_3057(Connection wdsConnection, String part_num, String eng_rls_num, int seq_num, String action) throws SQLException, RuntimeException {
		super("3057", action);
		this.part_num = part_num.toUpperCase();
		this.eng_rls_num = eng_rls_num;
		this.seq_num = seq_num;

		getDataFromWDS(wdsConnection, this.part_num, eng_rls_num, seq_num, action);
		fascorMessage = FasUtils.buildFascorMessage(this);
	}

	// -------------------------------------------------------------------------------------------

	public void getDataFromWDS(Connection wdsConnection, String part_num, String eng_rls_num, int seq_num, String action) throws SQLException, RuntimeException {
		// ArrayList<HashMap<String, Object>> part_bomAL = JDBCUtils.runQuery(wdsConnection, "select partnum_assm, eng_rls_num, seq_num, partnum_comp, qty_used from pub.part_bom where partnum_assm = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and eng_rls_num = '" + eng_rls_num + "' and seq_num = " + seq_num + " WITH (READPAST NOWAIT)");		
		// ArrayList<HashMap<String, Object>> part_bomAL = JDBCUtils.runQuery(wdsConnection, "select partnum_assm, eng_rls_num, seq_num, partnum_comp, qty_used from pub.part_bom where partnum_assm = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and eng_rls_num = '" + eng_rls_num + "' and seq_num = " + seq_num + " FOR UPDATE WITH (READPAST NOWAIT)");		
		ArrayList<HashMap<String, Object>> part_bomAL = JDBCUtils.runQuery(wdsConnection, "select partnum_assm, eng_rls_num, seq_num, partnum_comp, qty_used from pub.part_bom where partnum_assm = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and eng_rls_num = '" + eng_rls_num + "' and seq_num = " + seq_num + " WITH (NOLOCK)");		
		if ((part_bomAL == null) || (part_bomAL.size() != 1)) {
			throw new RuntimeException("part_bom record is missing for part: " + part_num + ", eng_rls_num: " + eng_rls_num + ", seq_num: " + seq_num);
		}
		HashMap<String, Object> part_bomHM = part_bomAL.get(0);
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        dataHM.put("Message_Type", "3057");
        dataHM.put("Facility_Nbr", Parms.FASCOR_FACILITY_NBR);
        dataHM.put("Mode", action);
        dataHM.put("BOM_Name", part_num);
        dataHM.put("BOM_Revision", eng_rls_num);
        dataHM.put("Seq_Nbr", seq_num + "");
        dataHM.put("SKU", ((String) part_bomHM.get("partnum_comp")).toUpperCase());
        
        this.partnum_comp = ((String) part_bomHM.get("partnum_comp")).toUpperCase();
                
        dataHM.put("Class", "01");
        dataHM.put("BOM_Qty", part_bomHM.get("qty_used"));
        dataHM.put("Capture_Lot_ID", "N");
        dataHM.put("Capture_Mfg_Date", "N");
        dataHM.put("Capture_Exp_Date", "N");

// Kim is going to talk to Fascor about this field
dataHM.put("Capture_Serial_Nbr", "N");

        dataHM.put("Non_Inventory", "N");
        
// Kim is going to talk to Fascor about this field        
dataHM.put("Display_Comments", "Y");
dataHM.put("Comments", "");
        
	}

	// -------------------------------------------------------------------------------------------

	public static HashMap<String, String> FASCOR_TO_WDS_FIELD_MAPPING_HM = new HashMap<String, String>(20); 
	public static String FASCOR_FIELD_LIST_FOR_COMPARISON;
	static {
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("BOM_Name", "BOM_Name");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("BOM_Revision", "BOM_Revision");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Seq_Nbr", "Seq_Nbr");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("SKU", "SKU");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Class", "Class");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("BOM_Qty", "BOM_Qty");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Display_Comments", "Display_Comments");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Comments", "Comments");
		
		FASCOR_FIELD_LIST_FOR_COMPARISON = "";
		Iterator<Entry<String, String>> it = FASCOR_TO_WDS_FIELD_MAPPING_HM.entrySet().iterator();
		
		while (it.hasNext()) {
			Map.Entry<String, String> pair = (Entry<String, String>) it.next();
			String key = pair.getKey();		
			FASCOR_FIELD_LIST_FOR_COMPARISON += ((FASCOR_FIELD_LIST_FOR_COMPARISON.compareTo("") != 0)?", ":"") + key;		
		}
	};

	public String compareFieldsWithFascor(Connection sqlServerConnection) throws SQLException {
    	ArrayList<HashMap<String, Object>> fascorAL = JDBCUtils.runQuery(sqlServerConnection, "select " + FASCOR_FIELD_LIST_FOR_COMPARISON + " from Bill_of_Material_Detail where BOM_Name = '" + part_num + "' and Seq_Nbr = " + seq_num);
    	if ((fascorAL != null) && (fascorAL.size() > 0)) {
    		return 	compareWDSAndFascorFields(dataHM, fascorAL.get(0), FASCOR_TO_WDS_FIELD_MAPPING_HM);
    	} else {
    		return "not found in Fascor";
    	}
	}
	
	// -------------------------------------------------------------------------------------------

	public String getKey1() {
		return part_num;
	}
	
    public String getKey2() {
		return eng_rls_num + "|" +  seq_num + "|" +  partnum_comp;
    }
    
	// -------------------------------------------------------------------------------------------

}
