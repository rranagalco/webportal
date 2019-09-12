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

public class Message_1320 extends MessageAbs implements Message {
	private static Logger log = Logger.getLogger(Message_1320.class);

	String order_num;
	int line_num;
	String part_num;

	// -------------------------------------------------------------------------------------------

	public Message_1320(Connection wdsConnection, String order_num, int line_num, String action) throws SQLException, RuntimeException {
		super("1320", action);
		this.order_num = order_num;
		this.line_num = line_num;

		getDataFromWDS(wdsConnection, order_num, line_num, action);
		fascorMessage = FasUtils.buildFascorMessage(this);
	}

	// -------------------------------------------------------------------------------------------

	public void getDataFromWDS(Connection wdsConnection, String order_num, int line_num, String action) throws SQLException, RuntimeException {

		ArrayList<HashMap<String, Object>> oitemAL = null;
		// oitemAL = JDBCUtils.runQuery(wdsConnection, "select order_num, line_num, part_num, qty_ord, qty_cancel, qty_commit, priceper_qty, billing_unit, cust_partnum, unit_price from pub.oitem where order_num = '" + order_num + "' and line_num = " + line_num + " FOR UPDATE WITH (READPAST NOWAIT)");
		oitemAL = JDBCUtils.runQuery(wdsConnection, "select order_num, line_num, part_num, qty_ord, qty_cancel, qty_commit, priceper_qty, billing_unit, cust_partnum, unit_price from pub.oitem where order_num = '" + order_num + "' and line_num = " + line_num + " WITH (NOLOCK)");

		if ((oitemAL == null) || (oitemAL.size() != 1)) {
			throw new RuntimeException("oitem data is missing for order_num: " + order_num + ", line_num: " + line_num);
		}
		HashMap<String, Object> oitemHM = oitemAL.get(0);

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		dataHM.put("Message_Type", "1320");
		dataHM.put("Mode", action);
		dataHM.put("Facility_Nbr", Parms.FASCOR_FACILITY_NBR);


		dataHM.put("Order_ID", "" + order_num);
		dataHM.put("Detail_Seq_Nbr", "" + line_num);
		
        // dataHM.put("SKU", oitemHM.get("part_num"));
        dataHM.put("SKU", ((String) oitemHM.get("part_num")).toUpperCase());
		this.part_num = ((String) oitemHM.get("part_num")).toUpperCase();

		int priceper_qty = (oitemHM.get("priceper_qty") == null)?1:(((Number) oitemHM.get("priceper_qty")).intValue());
		priceper_qty = (priceper_qty < 1)?1:priceper_qty;
        {
			int qty_ord = ((Number) oitemHM.get("qty_ord")).intValue();
			int qty_cancel = ((Number) oitemHM.get("qty_cancel")).intValue();
			
			// dataHM.put("SKU_Original_Qty", new Integer(qty_ord - qty_cancel));
			
			int current_qty_ord = qty_ord - qty_cancel;
			
			dataHM.put("SKU_Original_Qty", new Integer(current_qty_ord / priceper_qty));
		}
		dataHM.put("SKU_Ship_Qty", new Integer(((Number) oitemHM.get("qty_commit")).intValue() / priceper_qty));

        dataHM.put("SKU_Use_Reserve_Qty", "N");
        dataHM.put("SKU_Class", "01");
        dataHM.put("SKU_UoM", oitemHM.get("billing_unit"));
        dataHM.put("SKU_Allocation_Control", "L");
        dataHM.put("Partial_Shipment_Control", "Y");
        
        // dataHM.put("SKU_Fill_Percentage", "");
        dataHM.put("SKU_Fill_Percentage", 100);
        
        dataHM.put("SKU_Substitution_Control", "N");
        dataHM.put("SKU_Substitution_Mix", "N");
        dataHM.put("Full_Case_Only", "N");
        dataHM.put("SKU_Abbrev_Description", "");
        dataHM.put("Print_Line_Only", "N");

// Kim will get back to me
dataHM.put("Print_Comments_Crtl_Pack_List", "");
// Kim will get back to me
dataHM.put("Print_Comments_Crtl_BOL", "");


		{
			ArrayList<HashMap<String, Object>> ocommentAL = null;
			// ocommentAL = JDBCUtils.runQuery(wdsConnection, "select substr(comment_text,1,270) from pub.ocomment where order_num = '" + order_num + "' and line_num = " + line_num + " FOR UPDATE WITH (READPAST NOWAIT)");
			ocommentAL = JDBCUtils.runQuery(wdsConnection, "select substr(comment_text,1,270) from pub.ocomment where order_num = '" + order_num + "' and line_num = " + line_num + " WITH (NOLOCK)");
			if ((ocommentAL == null) || (ocommentAL.size() == 0)) {
				dataHM.put("Line_Comments_#1", "");
				dataHM.put("Line_Comments_#2", "");
				dataHM.put("Line_Comments_#3", "");
			} else {
				HashMap<String, Object> ocommentHM = ocommentAL.get(0);

				String comments = (String) ocommentHM.get("substr(comment_text,1,270)");
				if (comments.length() >= 181) {
					dataHM.put("Line_Comments_#1", comments.substring(0, 90));
					dataHM.put("Line_Comments_#2", comments.substring(90, 180));
					dataHM.put("Line_Comments_#3", comments.substring(180, 270));
				} else if (comments.length() >= 91) {
					dataHM.put("Line_Comments_#1", comments.substring(0, 90));
					dataHM.put("Line_Comments_#2", comments.substring(90, 180));
					dataHM.put("Line_Comments_#3", "");
				} else {
					dataHM.put("Line_Comments_#1", comments);
					dataHM.put("Line_Comments_#2", "");
					dataHM.put("Line_Comments_#3", "");
				}
			}
		}


// Kim will get back to me
dataHM.put("Class_of_Goods_Major", "");
// Kim will get back to me
dataHM.put("Class_of_Goods_Minor", "");
// Kim will get back to me; This is 2 decimals implied value.
dataHM.put("SKU_Insure_Value", "");


        dataHM.put("Customer_SKU_ID", oitemHM.get("cust_partnum"));
        dataHM.put("Customer_SKU_Description1", "");
        dataHM.put("Customer_SKU_Description2", "");
        dataHM.put("Retail_Unit_of_Measure", "");
        dataHM.put("SKU_Catalog_Page", "");
        dataHM.put("SKU_Assortment_Nbr", "");
        dataHM.put("SKU_Retail_Price", oitemHM.get("unit_price"));
        dataHM.put("SKU_Retail_UPC_Code", "");
        dataHM.put("Quantity_of_Stickers", "");
        dataHM.put("Price_Sticker_Object", "");
        dataHM.put("Lot_Mix", "");
        dataHM.put("Lot_ID", "");
        dataHM.put("Lot_Mfg_Age", "");
        dataHM.put("Lot_Mfg_Date", "");
        dataHM.put("Lot_Mfg_Date_Range", "");
        dataHM.put("Lot_Exp_Age", "");
        dataHM.put("Lot_Exp_Date", "");
        dataHM.put("Lot_Exp_Date_Range", "");
        dataHM.put("Lot_Distribution_Rule_ID", "");
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
