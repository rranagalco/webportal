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

public class Message_3052 extends MessageAbs implements Message {
	private static Logger log = Logger.getLogger(Message_3052.class);

	String part_num, eng_rls_num;

	// -------------------------------------------------------------------------------------------

	public Message_3052(Connection wdsConnection, String part_num, String eng_rls_num, String action) throws SQLException, RuntimeException {
		super("3052", action);
		this.part_num = part_num.toUpperCase();
		this.eng_rls_num = eng_rls_num;

		getDataFromWDS(wdsConnection, this.part_num, this.eng_rls_num, action);
		fascorMessage = FasUtils.buildFascorMessage(this);		
	}

	// -------------------------------------------------------------------------------------------

	public void getDataFromWDS(Connection wdsConnection, String part_num, String eng_rls_num, String action) throws SQLException, RuntimeException {
		// ArrayList<HashMap<String, Object>> part_bomAL = JDBCUtils.runQuery(wdsConnection, "select partnum_assm, eng_rls_num from pub.part_bom where partnum_assm = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and eng_rls_num = '" + eng_rls_num + "' WITH (READPAST NOWAIT)");		
		// ArrayList<HashMap<String, Object>> part_bomAL = JDBCUtils.runQuery(wdsConnection, "select partnum_assm, eng_rls_num from pub.part_bom where partnum_assm = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and eng_rls_num = '" + eng_rls_num + "' FOR UPDATE WITH (READPAST NOWAIT)");		
		ArrayList<HashMap<String, Object>> part_bomAL = JDBCUtils.runQuery(wdsConnection, "select partnum_assm, eng_rls_num from pub.part_bom where partnum_assm = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and eng_rls_num = '" + eng_rls_num + "' WITH (NOLOCK)");		
		if ((part_bomAL == null) || (part_bomAL.size() == 0)) {
			throw new RuntimeException("part_bom record is missing for part: " + part_num);				
		}
		HashMap<String, Object> part_bomHM = part_bomAL.get(0);
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        dataHM.put("Message_Type", "3052");
        dataHM.put("Facility_Nbr", Parms.FASCOR_FACILITY_NBR);
        dataHM.put("Mode", action);
        dataHM.put("BOM_Name", part_num);
        dataHM.put("BOM_Revision", eng_rls_num);
        dataHM.put("Type", "");
        dataHM.put("SKU", part_num);
        dataHM.put("Class", "01");
        dataHM.put("UPC_USA", "");
        dataHM.put("UPC_EAN", "");
        dataHM.put("Interpack_UPC", "");
        dataHM.put("Outerpack_UPC", "");
        dataHM.put("Kit_Prt_Object", "");
        dataHM.put("Interpack_Prt_Object", "");
        dataHM.put("Outerpack_Prt_Object", "");
        dataHM.put("Display_Comments", "");
        dataHM.put("Comments", "");
	}

	// -------------------------------------------------------------------------------------------

	public static String get_eng_rls_num(Connection wdsConnection, String part_num) throws SQLException, RuntimeException {
		// ArrayList<HashMap<String, Object>> part_bomAl = JDBCUtils.runQuery(wdsConnection, "select partnum_assm, eng_rls_num, CASE WHEN eng_rls_num = 'blank' THEN ' ' ELSE eng_rls_num END as eng_rls_num_new from pub.part_bom where partnum_assm = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' order by eng_rls_num_new desc WITH (READPAST NOWAIT)");
		ArrayList<HashMap<String, Object>> part_bomAl = JDBCUtils.runQuery(wdsConnection, "select partnum_assm, eng_rls_num, CASE WHEN eng_rls_num = 'blank' THEN ' ' ELSE eng_rls_num END as eng_rls_num_new from pub.part_bom where partnum_assm = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' order by eng_rls_num_new desc WITH (NOLOCK)");
		
		if ((part_bomAl == null) || (part_bomAl.size() == 0)) {
		    throw new RuntimeException("eng_rls_num is missing for part: " + part_num);					
		}
		HashMap<String, Object> part_bomHM = part_bomAl.get(0);

		return (String) part_bomHM.get("eng_rls_num");
	}

	// -------------------------------------------------------------------------------------------

	public static HashMap<String, String> FASCOR_TO_WDS_FIELD_MAPPING_HM = new HashMap<String, String>(20); 
	public static String FASCOR_FIELD_LIST_FOR_COMPARISON;
	static {
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("BOM_Name", "BOM_Name");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("BOM_Revision", "BOM_Revision");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Type", "Type");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("SKU", "SKU");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Class", "Class");
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
    	ArrayList<HashMap<String, Object>> fascorAL = JDBCUtils.runQuery(sqlServerConnection, "select " + FASCOR_FIELD_LIST_FOR_COMPARISON + " from Bill_of_Material where BOM_Name = '" + part_num + "'");
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
		return eng_rls_num;    	
    }
        
	// -------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------
	
	public static String get_eng_rls_num_o1(Connection wdsConnection, String part_num) throws SQLException, RuntimeException {
		ArrayList<HashMap<String, Object>> part_bomAL = JDBCUtils.runQuery(wdsConnection, "select eng_rls_num from pub.part_bom where partnum_assm = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and eng_rls_num = (select max(eng_rls_num) from pub.part_bom where partnum_assm = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "')");
		if ((part_bomAL == null) || (part_bomAL.size() == 0)) {
		    throw new RuntimeException("eng_rls_num is missing for par: " + part_num);
		}
		HashMap<String, Object> part_bomHM = part_bomAL.get(0);

		return (String) part_bomHM.get("eng_rls_num");
	}

	// -------------------------------------------------------------------------------------------
}
