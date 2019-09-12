package galco.fascor.messages;

import galco.fascor.utils.FasUtils;
import galco.portal.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public abstract class MessageAbs implements Message {
	private static Logger log = Logger.getLogger(MessageAbs.class);
	
	static HashMap<String, HashMap> messageFieldRelatedHMs = new HashMap<String, HashMap>(100);

	protected HashMap<String, Object> dataHM = new HashMap<String, Object>(100); 
	protected String messageType, action;
	protected String fascorMessage;

	private static void buildHashMaps(HashMap<String, HashMap> messageFieldRelatedHMs, String[] MESSAGE_FIELDS_AND_LENGTHS, String messageType) {
		HashMap<Integer, String> fieldsHM = new HashMap<Integer, String>(100);
		HashMap<Integer, Integer> lengthsHM = new HashMap<Integer, Integer>(100);
		HashMap<Integer, Integer> fieldFormatsHM = new HashMap<Integer, Integer>(100);
		HashMap<Integer, Integer> startingPosHM = new HashMap<Integer, Integer>(100);
		HashMap<String, Integer> fields_NameToPosition_HM = new HashMap<String, Integer>(100);

		int startingPosition = 1;
		for (int i = 0, j = 0; i < MESSAGE_FIELDS_AND_LENGTHS.length; i += 2, j++) {
	        fieldsHM.put(j, MESSAGE_FIELDS_AND_LENGTHS[i]);
	        fields_NameToPosition_HM.put(MESSAGE_FIELDS_AND_LENGTHS[i], j);
	        
	        String[] length_format = (MESSAGE_FIELDS_AND_LENGTHS[i + 1] + "," + FasUtils.FF_DEFAULT).split(",");
	        int length = new Integer(length_format[0]);
	        int format = new Integer(length_format[1]);
	        lengthsHM.put(j, length);
	        fieldFormatsHM.put(j, format);
	        
	        startingPosHM.put(j, startingPosition);
	        
	        startingPosition += length;
		}
		messageFieldRelatedHMs.put(messageType + "f", fieldsHM);
		messageFieldRelatedHMs.put(messageType + "fmt", fieldFormatsHM);
		messageFieldRelatedHMs.put(messageType + "l", lengthsHM);
		messageFieldRelatedHMs.put(messageType + "s", startingPosHM);
		messageFieldRelatedHMs.put(messageType + "n", fields_NameToPosition_HM);
	}
	
	// -------------------------------------------------------------------------------------------
	
	static {
		String[] MESSAGE_FIELDS_AND_LENGTHS = new String[] { 
				"Message_Type", "4",
				"Mode", "1",
				"Facility_Nbr", "2",
				"Vendor_ID", "15",
				"Vendor_Name", "30",
				"Vendor_Address1", "30",
				"Vendor_Address2", "30",
				"Vendor_Address3", "30",
				"Vendor_City", "30",
				"Vendor_State", "4",
				"Vendor_Zip", "10",
				"Vendor_Country", "20"};

		buildHashMaps(messageFieldRelatedHMs, MESSAGE_FIELDS_AND_LENGTHS, "0140");		
	}
	
	// -------------------------------------------------------------------------------------------

	static {
		String[] MESSAGE_FIELDS_AND_LENGTHS = new String[] { 
			"Message_Type", "4",
			"Mode", "1",
			"Facility_Nbr", "2",
			"SKU", "30",
			"UPC", "15",
			"Class", "10",
			"Environment_Code", "5",
			"Storage_Velocity_Code", "2",
			"Cycle_Count_Velocity_Code", "2",
			"Description1", "50",
			"Description2", "50",
			"Bulk_Loc_Type", "5",
			"Unit_Loc_Type", "5",
			"Length", "8," + FasUtils.FF_DECIMAL_WITH_TWO_DECIMALS_AND_IMPLIED_DECIMAL,   
			"Width", "8," + FasUtils.FF_DECIMAL_WITH_TWO_DECIMALS_AND_IMPLIED_DECIMAL,
			"Height", "8," + FasUtils.FF_DECIMAL_WITH_TWO_DECIMALS_AND_IMPLIED_DECIMAL,
			"Cube", "8",
			"Weight", "8," + FasUtils.FF_DECIMAL_WITH_TWO_DECIMALS_AND_IMPLIED_DECIMAL,
			"Serial_No_Flag", "1," + FasUtils.FF_BOOLEAN_TO_Y_OR_N,
			"Vendor_Part_1", "30",
			"Vendor_ID_1", "15",
			"Vendor_Part_2", "30",
			"Vendor_ID_2", "15",
			"Vendor_Part_3", "30",
			"Vendor_ID_3", "15",
			"High_Quantity", "3",
			"Tie_Quantity", "3",
			"Master_Pack", "13," + FasUtils.FF_DECIMAL_WITH_FIVE_DECIMALS_AND_IMPLIED_DECIMAL,
			"Master_Pack_UoM", "8",
			"Shippable_Unit", "1",
			"Preemptive_Putaway_Flag", "1",
			"Cost", "8," + FasUtils.FF_DECIMAL_WITH_TWO_DECIMALS_AND_IMPLIED_DECIMAL,
			"Substitute_SKU", "30",
			"Unit_of_Measure", "8",
			"Bulk_Pick_Flag", "1",
			"Lot_Tracking", "1",
			"New_SKU", "1",
			"Age_Control", "1",
			"Rule_ID", "5",
			"Preferred_Zone", "5",
			"Track_Manufacturing_Date", "1",
			"Track_Expiration_Date", "1"};

		buildHashMaps(messageFieldRelatedHMs, MESSAGE_FIELDS_AND_LENGTHS, "1110");
	}
	
	// -------------------------------------------------------------------------------------------
	
	static {
		String[] MESSAGE_FIELDS_AND_LENGTHS = new String[] { 
				"Message_Type", "4",
				"Mode", "1",
				"Facility_Nbr", "2",
				"PO_Nbr", "17",
				"PO_Date", "8",
				"Vendor_Nbr", "15",
				"Carrier_Code", "15",
				"Terms_Code", "15",
				"Comments", "90",
				"Check_In_Required", "1",
				"Buyer_Ref_Nbr", "15"};
		
		buildHashMaps(messageFieldRelatedHMs, MESSAGE_FIELDS_AND_LENGTHS, "1210");
	}
	
	// -------------------------------------------------------------------------------------------
	
	static {
		String[] MESSAGE_FIELDS_AND_LENGTHS = new String[] { 
				"Message_Type", "4",
				"Mode", "1",
				"Facility_Nbr", "2",
				"PO_Nbr", "17",
				"PO_Line_Nbr", "9",
				"SKU", "30",
				"UPC", "15",
				"Class", "10",
				"Unit_Of_Measure", "8",
				"Ordered_Qty", "13" + "," + FasUtils.FF_DECIMAL_WITH_FIVE_DECIMALS_AND_IMPLIED_DECIMAL,
				"Description1", "50",
				"Description2", "50",
				"Vendor_Part_Nbr", "30",
				"Hot", "1"};
		
		buildHashMaps(messageFieldRelatedHMs, MESSAGE_FIELDS_AND_LENGTHS, "1220");		
	}

	// -------------------------------------------------------------------------------------------

	static {
        String[] MESSAGE_FIELDS_AND_LENGTHS = new String[] { 
                "Message_Type", "4",
                "Mode", "1",
                "Facility_Nbr", "2",
                "PO_Nbr", "17",
                "PO_Line_Nbr", "9",
                "Object_ID", "8",
                "Object_Text", "278"};
        
        buildHashMaps(messageFieldRelatedHMs, MESSAGE_FIELDS_AND_LENGTHS, "1230");      
    }
	
	// -------------------------------------------------------------------------------------------
	
	static {
		String[] MESSAGE_FIELDS_AND_LENGTHS = new String[] { 
				"Message_Type", "4",
				"Mode", "1",
				"Facility_Nbr", "2",
				"Order_ID", "17",
				"Order_Date", "8",
				"Order_Type", "5",
				"Order_Priority", "2",
				"Order_Status", "4",
				"Order_Reason_Code", "5",
				"Order_Reason_Comments", "30",
				"Order_Ship_Date", "8",
				"Order_Ship_Day", "2",
				"Carrier_ID", "15",
				"Shipping_Terms", "10",
				"Ship_WOG_Control", "1",
				"ASN_Control", "1",
				"Route_ID", "15",
				"Stop_ID", "5",
				"Sold_to_customer_ID", "15",
				"Sold_to_customer_Name", "30",
				"Sold_to_customer_Abbrev_Name", "15",
				"Sold_to_customer_Addr1", "30",
				"Sold_to_customer_Addr2", "30",
				"Sold_to_customer_Addr3", "30",
				"Sold_to_customer_Addr4", "30",
				"Sold_to_customer_City", "30",
				"Sold_to_customer_State", "4",
				"Sold_to_customer_Zip", "9",
				"Sold_to_customer_Country_Code", "5",
				"Sold_to_customer_PO_Ref", "17",
				"Sold_to_customer_PO_Date", "8",
				"Ship_to_customer_ID", "15",
				"Ship_to_customer_Name", "30",
				"Ship_to_customer_Abbrev_Name", "15",
				"Ship_to_customer_Addr1", "30",
				"Ship_to_customer_Addr2", "30",
				"Ship_to_customer_Addr3", "30",
				"Ship_to_customer_Addr4", "30",
				"Ship_to_customer_City", "30",
				"Ship_to_customer_State", "4",
				"Ship_to_customer_Zip", "9",
				"Ship_to_customer_Country_Code", "5",
				"Tote_Control", "1",
				"Tote_Type", "5",
				"Child_Type", "5",
				"Pallet_Control", "1",
				"Pallet_Type", "5",
				"Order_Fill_Control", "1",
				
				// "Order_Fill_Percentage", "8",
				"Order_Fill_Percentage", "8," + FasUtils.FF_DECIMAL_WITH_TWO_DECIMALS_AND_IMPLIED_DECIMAL,
				
				"Order_Fill_Calculation", "1",
				"Order_Fill_Action", "4",
				"Partial_Shipment_Control", "1",
				"Partial_Shipment_Suffix", "2",
				"Substitute_Control", "1",
				"Substitute_Suffix", "2",
				"Print_PackList_Control", "1",
				"Print_PackList_Object", "8",
				"Print_Shipping_Label_Control", "1",
				"Print_Shipping_Label_Object", "8",
				"Print_Price_Label_Control", "1",
				"Print_Price_Label_Object", "8",
				"Print_Other_Doc_Control", "1",
				"Print_Other_Doc_Object", "8",
				"COD_Invoice_Amount", "8," + FasUtils.FF_DECIMAL_WITH_TWO_DECIMALS_AND_IMPLIED_DECIMAL,
				"Declared_Value", "8",
				"Post_Pick_Process_#1_Control", "1",
				"Post_Pick_Process_#1_Profile", "8",
				"Post_Pick_Process_#2_Control", "1",
				"Post_Pick_Process_#2_Profile", "8",
				"Post_Pick_Process_#3_Control", "1",
				"Post_Pick_Process_#3_Profile", "8",
				"Order_Comments", "90"};
		
		buildHashMaps(messageFieldRelatedHMs, MESSAGE_FIELDS_AND_LENGTHS, "1310");		
	}
	
	// -------------------------------------------------------------------------------------------
	
	static {
		String[] MESSAGE_FIELDS_AND_LENGTHS = new String[] { 
				"Message_Type", "4",
				"Mode", "1",
				"Facility_Nbr", "2",
				"Order_ID", "17",
				"Detail_Seq_Nbr", "9",
				"SKU", "30",
				"SKU_Original_Qty", "13" + "," + FasUtils.FF_DECIMAL_WITH_FIVE_DECIMALS_AND_IMPLIED_DECIMAL,
				"SKU_Ship_Qty", "13" + "," + FasUtils.FF_DECIMAL_WITH_FIVE_DECIMALS_AND_IMPLIED_DECIMAL,
				"SKU_Use_Reserve_Qty", "1",
				"SKU_Class", "10",
				"SKU_UoM", "8",
				"SKU_Allocation_Control", "1",
				"Partial_Shipment_Control", "1",

				// "SKU_Fill_Percentage", "8",
				"SKU_Fill_Percentage", "8," + FasUtils.FF_DECIMAL_WITH_TWO_DECIMALS_AND_IMPLIED_DECIMAL,
				
				"SKU_Substitution_Control", "1",
				"SKU_Substitution_Mix", "1",
				"Full_Case_Only", "1",
				"SKU_Abbrev_Description", "10",
				"Print_Line_Only", "1",
				"Print_Comments_Crtl_Pack_List", "1",
				"Print_Comments_Crtl_BOL", "1",
				"Line_Comments_#1", "90",
				"Line_Comments_#2", "90",
				"Line_Comments_#3", "90",
				"Class_of_Goods_Major", "5",
				"Class_of_Goods_Minor", "5",
				"SKU_Insure_Value", "8",
				"Customer_SKU_ID", "30",
				"Customer_SKU_Description1", "40",
				"Customer_SKU_Description2", "40",
				"Retail_Unit_of_Measure", "8",
				"SKU_Catalog_Page", "12",
				"SKU_Assortment_Nbr", "10",
				"SKU_Retail_Price", "8",
				"SKU_Retail_UPC_Code", "15",
				"Quantity_of_Stickers", "4",
				"Price_Sticker_Object", "8",
				"Lot_Mix", "6",
				"Lot_ID", "15",
				"Lot_Mfg_Age", "6",
				"Lot_Mfg_Date", "8",
				"Lot_Mfg_Date_Range", "3",
				"Lot_Exp_Age", "6",
				"Lot_Exp_Date", "8",
				"Lot_Exp_Date_Range", "3",
				"Lot_Distribution_Rule_ID", "10"};
		
		buildHashMaps(messageFieldRelatedHMs, MESSAGE_FIELDS_AND_LENGTHS, "1320");		
	}

	
	// -------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------

	static {
		String[] MESSAGE_FIELDS_AND_LENGTHS = new String[] { 
				"Message_Type", "4",
				"Timestamp", "14",
				"User_ID", "10",
				"Mode", "1",
				"Message_ID", "10",
				"Facility_Nbr", "2",
				"SKU", "30",
				"BOM_Name", "32",
				"BOM_Revision", "15",
				"Lot_ID", "15",
				"Production_Date", "8",
				"Production_Shift", "1",
				"Shop_Order_ID", "16",
				"Sched_Build", "13," + FasUtils.FF_DECIMAL_WITH_FIVE_DECIMALS_AND_IMPLIED_DECIMAL,
				"Expiration_Date", "8",
				"Special_SO", "1"};
		
		buildHashMaps(messageFieldRelatedHMs, MESSAGE_FIELDS_AND_LENGTHS, "2015");		
	}	

	// -------------------------------------------------------------------------------------------

	static {
		String[] MESSAGE_FIELDS_AND_LENGTHS = new String[] { 
				"Message_Type", "4",
				"Timestamp", "14",
				"User_ID", "10",
				"Mode", "1",
				"Message_ID", "10",
				"Facility_Nbr", "2",
				"SKU", "30",
				"BOM_Name", "32",
				"BOM_Revision", "15",
				"Lot_ID", "15",
				"Production_Date", "8",
				"Production_Shift", "1",
				"Shop_Order_ID", "16",
				"Sched_Build", "15",
				"Expiration_Date", "8",
				"Special_SO", "1",
				"Reason_Code", "5",
				"Comments", "90"};
		
		buildHashMaps(messageFieldRelatedHMs, MESSAGE_FIELDS_AND_LENGTHS, "2016");		
	}	

	// -------------------------------------------------------------------------------------------

	static {
		String[] MESSAGE_FIELDS_AND_LENGTHS = new String[] { 
				"Message_Type", "4",
				"Facility_Nbr", "2",
				"Mode", "1",
				"BOM_Name", "32",
				"BOM_Revision", "15",
				"Type", "5",
				"SKU", "30",
				"Class", "10",
				"UPC_USA", "15",
				"UPC_EAN", "15",
				"Interpack_UPC", "15",
				"Outerpack_UPC", "15",
				"Kit_Prt_Object", "8",
				"Interpack_Prt_Object", "8",
				"Outerpack_Prt_Object", "8",
				"Display_Comments", "1",
				"Comments", "250"};
		
		buildHashMaps(messageFieldRelatedHMs, MESSAGE_FIELDS_AND_LENGTHS, "3052");		
	}	

	// -------------------------------------------------------------------------------------------

	static {
		String[] MESSAGE_FIELDS_AND_LENGTHS = new String[] { 
                "Message_Type", "4",
                "Timestamp", "14",
                "User_ID", "10",
                "Facility_Nbr", "2",
                "Receiver_Nbr", "7",
                "ASN_Nbr", "15",
                "PO_Nbr", "17",
                "SKU", "30",
                "Class", "10",
                "Qty", "13",
                "Qty_Sign", "1",
                "Location", "10",
                "Home_Location", "1",
                "Loc_Type", "5",
                "Lot_ID", "15",
                "Vendor_ID", "15",
                "Manufacturing_Date", "8",
                "Expiration_Date", "8",
                "Function_ID", "10",
                "Serial_Number", "20"};
		
		buildHashMaps(messageFieldRelatedHMs, MESSAGE_FIELDS_AND_LENGTHS, "0210");		
	}	

	// -------------------------------------------------------------------------------------------

	static {
		String[] MESSAGE_FIELDS_AND_LENGTHS = new String[] { 
				"Message_Type", "4",
				"Facility_Nbr", "2",
				"Mode", "1",
				"BOM_Name", "32",
				"BOM_Revision", "15",
				"Seq_Nbr", "9",
				"SKU", "30",
				"Class", "10",
				"BOM_Qty", "13" + "," + FasUtils.FF_DECIMAL_WITH_FIVE_DECIMALS_AND_IMPLIED_DECIMAL,
				"Capture_Lot_ID", "1",
				"Capture_Mfg_Date", "1",
				"Capture_Exp_Date", "1",
				"Capture_Serial_Nbr", "1",
				"Non_Inventory", "1",
				"Display_Comments", "1",
				"Comments", "250"};
		
		buildHashMaps(messageFieldRelatedHMs, MESSAGE_FIELDS_AND_LENGTHS, "3057");		
	}	
	
	// -------------------------------------------------------------------------------------------

	static {
		String[] MESSAGE_FIELDS_AND_LENGTHS = new String[] { 
				"Message_Type", "4",
				"Timestamp", "14",
				"User_ID", "10",
				"Mode", "1",
				"Message_ID", "10",
				"Facility_Nbr", "2",
				"Location", "10",
				"MU_ID", "20",
				"SKU", "30",
				"Class", "10",
				"Qty", "13" + "," + FasUtils.FF_DECIMAL_WITH_FIVE_DECIMALS_AND_IMPLIED_DECIMAL,
				"Qty_Sign", "1",
				"Function", "18",
				"Reason_Code", "5",
				"Comments", "90",
				"Lot_ID", "15",
				"Vendor_ID", "15",
				"Manufacturing_Date", "8",
				"Expiration_Date", "8",
				"Function_ID", "10",
				"Serial_Number", "20"};
		
		buildHashMaps(messageFieldRelatedHMs, MESSAGE_FIELDS_AND_LENGTHS, "1420");		
	}	
			
	// -------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------
	
	public MessageAbs(String messageType, String action) {
		this.messageType = messageType;
		this.action = action;		
	}

	// -------------------------------------------------------------------------------------------
		
	public HashMap<Integer, String> getFieldsHM() {
		return (HashMap<Integer, String>) messageFieldRelatedHMs.get(messageType + "f");
	}
	public HashMap<Integer, Integer> getLengthsHM() {
		return (HashMap<Integer, Integer>) messageFieldRelatedHMs.get(messageType + "l");
	}
	public HashMap<Integer, Integer> getFieldFormatsHM() {
		return (HashMap<Integer, Integer>) messageFieldRelatedHMs.get(messageType + "fmt");
	}
	public HashMap<Integer, Integer> getStartingPosHM() {
		return (HashMap<Integer, Integer>) messageFieldRelatedHMs.get(messageType + "s");
	}
	public HashMap<String, Integer> getFields_NameToPosition_HM() {
		return (HashMap<String, Integer>) messageFieldRelatedHMs.get(messageType + "n");
	}
	
	// -------------------------------------------------------------------------------------------
	
	public static HashMap<Integer, String> getFieldsHM(String messageType) {
		return (HashMap<Integer, String>) messageFieldRelatedHMs.get(messageType + "f");
	}
	public static HashMap<Integer, Integer> getLengthsHM(String messageType) {
		return (HashMap<Integer, Integer>) messageFieldRelatedHMs.get(messageType + "l");
	}
	public static HashMap<Integer, Integer> getFieldFormatsHM(String messageType) {
		return (HashMap<Integer, Integer>) messageFieldRelatedHMs.get(messageType + "fmt");
	}
	public static HashMap<Integer, Integer> getStartingPosHM(String messageType) {
		return (HashMap<Integer, Integer>) messageFieldRelatedHMs.get(messageType + "s");
	}
	public static HashMap<String, Integer> getFields_NameToPosition_HM(String messageType) {
		return (HashMap<String, Integer>) messageFieldRelatedHMs.get(messageType + "n");
	}
	
	// -------------------------------------------------------------------------------------------

	public HashMap<String, Object> getDataHM() {
		return dataHM;
	}

	// -------------------------------------------------------------------------------------------
	
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	// -------------------------------------------------------------------------------------------
	
	public static String getFieldValueFromInboundMessage(String fieldName, String inbound_Msg) {
		String messageTypeIn = FasUtils.getOutboundMessageTextSubstring(inbound_Msg, 1, 4).trim();
		
		HashMap<Integer, String> fieldsHM = MessageAbs.getFieldsHM(messageTypeIn);
		HashMap<Integer, Integer> lengthsHM = MessageAbs.getLengthsHM(messageTypeIn);
		HashMap<Integer, Integer> startingPosHM = MessageAbs.getStartingPosHM(messageTypeIn);
		HashMap<String, Integer> fields_NameToPosition_HM = MessageAbs.getFields_NameToPosition_HM(messageTypeIn);

		int fieldPos = fields_NameToPosition_HM.get(fieldName);
		
		return inbound_Msg.substring(startingPosHM.get(fieldPos) - 1, startingPosHM.get(fieldPos) - 1 + lengthsHM.get(fieldPos)).trim();
	}
	
	public static String getKeyFromFascorInboundMessage(String inbound_Msg) {
		if ((inbound_Msg == null) || (inbound_Msg.length() < 4)) {
			return null;
		}
		
		String messageTypeIn = FasUtils.getOutboundMessageTextSubstring(inbound_Msg, 1, 4).trim();

        if (messageTypeIn.compareTo("0140") == 0) {
            String vendor_num = getFieldValueFromInboundMessage("Vendor_ID", inbound_Msg);
            
            return vendor_num;
        } else if (messageTypeIn.compareTo("1110") == 0) {
            String part_num = getFieldValueFromInboundMessage("SKU", inbound_Msg);
            
            return part_num;
        } else if (messageTypeIn.compareTo("1210") == 0) {
            String order_num = getFieldValueFromInboundMessage("PO_Nbr", inbound_Msg);
            
            return order_num;
        } else if (messageTypeIn.compareTo("1220") == 0) {
            String order_num = getFieldValueFromInboundMessage("PO_Nbr", inbound_Msg);
            String line_num = getFieldValueFromInboundMessage("PO_Line_Nbr", inbound_Msg);
            String part_num = getFieldValueFromInboundMessage("SKU", inbound_Msg);

            return order_num + "|" +  line_num + "|" +  part_num;
        } else if (messageTypeIn.compareTo("1310") == 0) {
            String order_num = getFieldValueFromInboundMessage("Order_ID", inbound_Msg);
            
            return order_num;
        } else if (messageTypeIn.compareTo("1320") == 0) {
            String order_num = getFieldValueFromInboundMessage("Order_ID", inbound_Msg);
            String line_num = getFieldValueFromInboundMessage("Detail_Seq_Nbr", inbound_Msg);
            String part_num = getFieldValueFromInboundMessage("SKU", inbound_Msg);

            return order_num + "|" +  line_num + "|" +  part_num;
        } else if (messageTypeIn.compareTo("3052") == 0) {
            String partnum_assm = getFieldValueFromInboundMessage("SKU", inbound_Msg);
            String eng_rls_num = getFieldValueFromInboundMessage("BOM_Revision", inbound_Msg);
            
            return partnum_assm + "|" +  eng_rls_num;
        } else if (messageTypeIn.compareTo("3057") == 0) {
            String partnum_assm = getFieldValueFromInboundMessage("BOM_Name", inbound_Msg);
            String eng_rls_num = getFieldValueFromInboundMessage("BOM_Revision", inbound_Msg);            
            String seq_num = getFieldValueFromInboundMessage("Seq_Nbr", inbound_Msg);
            String partnum_comp = getFieldValueFromInboundMessage("SKU", inbound_Msg);

            return partnum_assm + "|" +  eng_rls_num + "|" +  seq_num + "|" +  partnum_comp;
        } else if (messageTypeIn.compareTo("1420") == 0) {
            String part_num = getFieldValueFromInboundMessage("SKU", inbound_Msg);
            
            return part_num;
        } else {
        	return null;
        }
	}

	// -------------------------------------------------------------------------------------------

	public String compareWDSAndFascorFields(HashMap<String, Object> wdsHM, HashMap<String, Object> fascorHM, HashMap<String, String> FASCOR_TO_WDS_FIELD_MAPPING_HM) {
		Iterator<Entry<String, Object>> it = fascorHM.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> pair = (Entry<String, Object>) it.next();
			
			String key;
			Object valueF, valueW;
			
			key = pair.getKey();
			valueF = pair.getValue();
			valueW = wdsHM.get(FASCOR_TO_WDS_FIELD_MAPPING_HM.get(key));
			
			if (valueF != null) {
				if (valueW != null) {
					if (valueF instanceof Number) {
						if (valueW instanceof Number) {
							if (Math.abs(((Number) valueF).doubleValue() - ((Number) valueW).doubleValue()) > 0.01) {
								return key + " - value didn't match, F " + valueF + " ; W " + valueW;
							}
						} else {
							try {
								int valueWInt = new Integer(valueW.toString().trim());
								if (Math.abs(((Number) valueF).doubleValue() - valueWInt) > 0.01) {
									return key + " - value didn't match, F " + valueF + " ; W " + valueW;
								}								
							} catch (Exception e) {
								return key + " - value didn't match, F " + valueF + " ; W " + valueW;
							}
						}
					} else {
						if (valueF.toString().trim().compareToIgnoreCase(valueW.toString().trim()) != 0) {
							return key + " - value didn't match, F " + valueF + " ; W " + valueW;
						}
					}
				} else {
					if (valueF.toString().trim().compareTo("") != 0) {
						return key + " - value didn't match - F Not Null, W Null";
					}
				}
			} else {
				if (valueW != null) {
					if (valueW.toString().trim().compareTo("") != 0) {					
						return key + " - value didn't match - F Null, W not Null";
					}
				}					
			}
		}

    	return null;    	
	}

	// -------------------------------------------------------------------------------------------

	public String getFascorMessage() {
		return fascorMessage;    	
    }

    public String getFascorMessage_VariablePart() {   
		return fascorMessage.substring(7);    	
	}

	// -------------------------------------------------------------------------------------------
	
	public static void main(String[] args) {
		String messageType = "3057";

		HashMap<Integer, String> fieldsHM = getFieldsHM(messageType);
		HashMap<Integer, Integer> lengthsHM = getLengthsHM(messageType);
		HashMap<Integer, Integer> fieldFormatsHM = getFieldFormatsHM(messageType);
		HashMap<Integer, Integer> startingPosHM = getStartingPosHM(messageType);
		HashMap<String, Integer> fields_NameToPosition_HM = getFields_NameToPosition_HM(messageType);
		
		for (int i = 0; i < fieldsHM.size(); i++) {
			System.out.println(fieldsHM.get(i) + " " + startingPosHM.get(i) + " " + lengthsHM.get(i));			
		}
	}
		
/*		
		ArrayList<ArrayList<String>> KEYS_OF_INBOUND_MESSAGES;
    	KEYS_OF_INBOUND_MESSAGES = new ArrayList<ArrayList<String>>(20);

		
		ArrayList<String> a = new ArrayList<String>(10);
		ArrayList<String> b = new ArrayList<String>(10);
		ArrayList<String> c = new ArrayList<String>(10);
		
		a.add("a");
		a.add("b");
		a.add("c");
		
		b.add("a");
		b.add("c");
		b.add("b");
		
		c.add("a");
		c.add("c");
		c.add("b");
		
		KEYS_OF_INBOUND_MESSAGES.add(a);
		KEYS_OF_INBOUND_MESSAGES.add(b);
		KEYS_OF_INBOUND_MESSAGES.add(c);
		
		System.out.println(KEYS_OF_INBOUND_MESSAGES.indexOf(c));
		System.exit(0);

		ArrayList<String> a = new ArrayList<String>(10);
		ArrayList<String> b = new ArrayList<String>(10);
		
		a.add("a");
		a.add("b");
		a.add("c");
		
		b.add("a");
		b.add("c");
		b.add("b");
		
		System.out.println(a.equals(b));
		System.exit(0);
		
		String msg1220 = "1220A011825881          1        PC1300-DURA                   66119152784    01        EA      0000000300000Battery, Procell, Alkaline, \"D\" Size, 1.5 Volts                                                     PC1300                        N";
		String msg1320 = "1320A01AA88513          1        T1N080TL-ABBG                 00000001000000000000000000N01        EA      LY        NNN          N                                                                                                                                                                                                                                                                                                                                                                                                                                              171.0100                                                                                            ";
		ArrayList<String> keys = getKeysFromFascorInboundMessage(msg1320);
		System.out.println(keys.get(0));
		System.out.println(keys.get(1));
		System.out.println(keys.get(2));
		System.exit(0);
	}
*/

}
