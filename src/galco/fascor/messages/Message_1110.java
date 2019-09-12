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

public class Message_1110 extends MessageAbs implements Message {
	private static Logger log = Logger.getLogger(Message_1110.class);

	String part_num;

	// -------------------------------------------------------------------------------------------

	public Message_1110(Connection wdsConnection, String part_num, String action) throws SQLException, RuntimeException {
		super("1110", action);
		this.part_num = part_num.trim();
		
		if ((this.part_num == null) || (this.part_num.length() > 30)) {
			throw new RuntimeException("Part number is more than 30 bytes, part: " + part_num);
		}

		getDataFromWDS(wdsConnection, part_num, action);
		fascorMessage = FasUtils.buildFascorMessage(this);
	}

	// -------------------------------------------------------------------------------------------

	public static ArrayList<HashMap<String, Object>> get_vend_partnum(Connection wdsConnection, String part_num, HashMap<String, Object> partHM) throws SQLException {
		if (partHM == null) {
			ArrayList<HashMap<String, Object>> partAL = null;

			// partAL = JDBCUtils.runQuery(wdsConnection, "select part_num, vendor_num from pub.part where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' FOR UPDATE WITH (READPAST NOWAIT)");
			// partAL = JDBCUtils.runQuery(wdsConnection, "select part_num, vendor_num from pub.part where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' WITH (READPAST NOWAIT)");
			partAL = JDBCUtils.runQuery(wdsConnection, "select part_num, vendor_num from pub.part where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' WITH (NOLOCK)");

			if ((partAL == null) || (partAL.size() != 1)) {
				throw new RuntimeException("Part data is missing for part: " + part_num);
			}
			partHM = partAL.get(0);
		}

		String vendor_num_from_part = (String) partHM.get("vendor_num");

		ArrayList<HashMap<String, Object>> partvendAL = null;
		
		// partvendAL = JDBCUtils.runQuery(wdsConnection, "select partvend.vendor_num, partvend.vend_partnum, partvend.vend_desc1, partvend.vend_desc2 from pub.partvend, pub.vendor where partvend.part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and partvend.vendor_num = '" + vendor_num_from_part + "' and vendor.vendor_num = '" + vendor_num_from_part + "' WITH (READPAST NOWAIT)");
		partvendAL = JDBCUtils.runQuery(wdsConnection, "select partvend.vendor_num, partvend.vend_partnum, partvend.vend_desc1, partvend.vend_desc2 from pub.partvend, pub.vendor where partvend.part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and partvend.vendor_num = '" + vendor_num_from_part + "' and vendor.vendor_num = '" + vendor_num_from_part + "' WITH (NOLOCK)");
		if ((partvendAL == null) || (partvendAL.size() == 0)) {
			// partvendAL = JDBCUtils.runQuery(wdsConnection, "select partvend.vendor_num, partvend.vend_partnum, partvend.vend_desc1, partvend.vend_desc2 from pub.partvend, pub.vendor where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and partvend.vendor_num = vendor.vendor_num WITH (READPAST NOWAIT)");
			partvendAL = JDBCUtils.runQuery(wdsConnection, "select partvend.vendor_num, partvend.vend_partnum, partvend.vend_desc1, partvend.vend_desc2 from pub.partvend, pub.vendor where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and partvend.vendor_num = vendor.vendor_num WITH (NOLOCK)");
			if ((partvendAL == null) || (partvendAL.size() == 0)) {
				ArrayList<HashMap<String, Object>> vendorAL = null;
				// vendorAL = JDBCUtils.runQuery(wdsConnection, "select vendor_num, name from pub.vendor where vendor_num = '" + vendor_num_from_part + "' WITH (READPAST NOWAIT)");
				vendorAL = JDBCUtils.runQuery(wdsConnection, "select vendor_num, name from pub.vendor where vendor_num = '" + vendor_num_from_part + "' WITH (NOLOCK)");
				if ((vendorAL != null) && (vendorAL.size() == 1)) {
					log.debug("Partvend is not found, and so using vendor from part record");

					HashMap<String, Object> vendorHM = vendorAL.get(0);

					partvendAL = new ArrayList<HashMap<String,Object>>(1);
					HashMap<String, Object> partvendHM = new HashMap<String, Object>();
					partvendHM.put("vendor_num", vendor_num_from_part);
					partvendHM.put("vend_partnum", part_num);
					partvendHM.put("vend_desc1", vendorHM.get("name"));
					partvendHM.put("vend_desc2", "");
					partvendAL.add(partvendHM);
				} else {
					log.debug("Partvend is not found, and so using the dummy vendor '-NONE-'");

					partvendAL = new ArrayList<HashMap<String,Object>>(1);
					HashMap<String, Object> partvendHM = new HashMap<String, Object>();
					partvendHM.put("vendor_num", "-NONE-");
					partvendHM.put("vend_partnum", part_num);
					partvendHM.put("vend_desc1", "");
					partvendHM.put("vend_desc2", "");
					partvendAL.add(partvendHM);
				}
			} else {
				log.debug("Partvend found using just partnum of the part record without using vendor_num.");
			}
		} else {
			log.debug("Partvend found using partnum, and vendor_num, of the part record.");
		}
		
		for (Iterator<HashMap<String, Object>> iterator = partvendAL.iterator(); iterator.hasNext();) {
			HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();
			
			String vend_partnum = (String) hashMap.get("vend_partnum");
			if ((vend_partnum == null) || (vend_partnum.compareTo("") == 0)) {
				hashMap.put("vend_partnum", part_num);
			}
		}
    	FasUtils.print_AL_Of_HMs(partvendAL);

		return partvendAL;
	}

	// -------------------------------------------------------------------------------------------

	public void getDataFromWDS(Connection wdsConnection, String part_num, String action) throws SQLException, RuntimeException {

		ArrayList<HashMap<String, Object>> partAL = null;

		// partAL = JDBCUtils.runQuery(wdsConnection, "select sales_subcat, altbill_qty, altbill_unit, ave_cost, billing_unit , description, description2, description3, gross_weight, weight_unit, must_serialnum, part_num, pkg_depth, pkg_height, pkg_width, upc_code, vendor_num from pub.part where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' WITH (READPAST NOWAIT)");
		// partAL = JDBCUtils.runQuery(wdsConnection, "select sales_subcat, altbill_qty, altbill_unit, ave_cost, billing_unit , description, description2, description3, gross_weight, weight_unit, must_serialnum, part_num, pkg_depth, pkg_height, pkg_width, upc_code, vendor_num from pub.part where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' FOR UPDATE WITH (READPAST NOWAIT)");
		partAL = JDBCUtils.runQuery(wdsConnection, "select sales_subcat, altbill_qty, altbill_unit, ave_cost, billing_unit , description, description2, description3, gross_weight, weight_unit, must_serialnum, part_num, pkg_depth, pkg_height, pkg_width, upc_code, vendor_num from pub.part where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' WITH (NOLOCK)");

		if ((partAL == null) || (partAL.size() != 1)) {
			throw new RuntimeException("Part data is missing for part: " + part_num);
			// throw new FascorLockedRecordException("Part data is missing for part: " + part_num);			
		}
		HashMap<String, Object> partHM = partAL.get(0);

		ArrayList<HashMap<String, Object>> partlocAL = null;

		// partlocAL = JDBCUtils.runQuery(wdsConnection, "select abc_class, vendor_num from pub.partloc where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' FOR UPDATE WITH (READPAST NOWAIT)");
		// partlocAL = JDBCUtils.runQuery(wdsConnection, "select abc_class, vendor_num from pub.partloc where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' WITH (READPAST NOWAIT)");
		partlocAL = JDBCUtils.runQuery(wdsConnection, "select abc_class, vendor_num from pub.partloc where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' WITH (NOLOCK)");

		if ((partlocAL == null) || (partlocAL.size() != 1)) {
			throw new RuntimeException("Partloc data is missing for part: " + part_num);
		}
		HashMap<String, Object> partlocHM = partlocAL.get(0);

		String vendor_num = (String) partlocHM.get("vendor_num");

		log.debug("part.vendor_num    : " + partHM.get("vendor_num"));

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		ArrayList<HashMap<String, Object>> partvendAL = get_vend_partnum(wdsConnection, part_num, partHM);

		/*

		// This was the original code, but modified it, on 08/17/2018, to mimic C:\0Ati\ZWDS2\GALCO-QA1-08072017\in\in700a.p

		// find partvend where
		// partvend.part_num   = pbuf.part_num and
		// partvend.vendor_num = pbuf.vendor_num no-lock no-error.
	    // if not available partvend then
		// leave GET-PVEND.
	    // w-vend_partnum = partvend.vend_partnum.

		ArrayList<HashMap<String, Object>> partvendAL = null;
		// partvendAL = JDBCUtils.runQuery(wdsConnection, "select vend_partnum from pub.partvend where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and vendor_num = '" + vendor_num + "' FOR UPDATE WITH (READPAST NOWAIT)");
		partvendAL = JDBCUtils.runQuery(wdsConnection, "select vend_partnum, vendor_num from pub.partvend where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' WITH (READPAST NOWAIT)");
		if ((partvendAL == null) || (partvendAL.size() == 0)) {
			String vendor_num_from_part = (String) partHM.get("vendor_num");
			if ((vendor_num_from_part != null) && (vendor_num_from_part.compareTo("") != 0)) {
				partvendAL = JDBCUtils.runQuery(wdsConnection, "select top 1 vend_partnum, vendor_num from pub.partvend where vendor_num = '" + vendor_num_from_part + "' WITH (READPAST NOWAIT)");
				if ((partvendAL == null) || (partvendAL.size() == 0)) {
					throw new RuntimeException("Partvend data is missing for part: " + part_num + ", and vendpart is missing for part record vend_partnum also.");
				}

				partvendAL = new ArrayList<HashMap<String,Object>>(1);
				HashMap<String, Object> partvendHM = new HashMap<String, Object>();
				partvendHM.put("vendor_num", vendor_num_from_part);
				partvendHM.put("vend_partnum", part_num);
				partvendAL.add(partvendHM);

				log.debug("Partvend found with part.vendor_num");
			} else {
				log.debug("Partvend not found with both");
				throw new RuntimeException("Partvend data is missing for part: " + part_num + ", and vend_partnum is missing from part record.");
			}
		} else {
			log.debug("Partvend found with partloc");
		}
		*/

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		dataHM.put("Message_Type", "1110");

		dataHM.put("Mode", action);

		dataHM.put("Facility_Nbr", Parms.FASCOR_FACILITY_NBR);

		// dataHM.put("SKU", partHM.get("part_num"));
		dataHM.put("SKU", ((String) partHM.get("part_num")).toUpperCase());

		dataHM.put("UPC", partHM.get("upc_code"));

		dataHM.put("Class", "01");

		dataHM.put("Environment_Code", "");

		dataHM.put("Storage_Velocity_Code", "");

		dataHM.put("Cycle_Count_Velocity_Code", partlocHM.get("abc_class"));

		String[] fiftyByteStringArray = FasUtils.splitInto50ByteStrings((String) partHM.get("description"), (String) partHM.get("description2"), (String) partHM.get("description3"));
		

		
		dataHM.put("Description1", fiftyByteStringArray[0]);
		
		/*
		String changedDescription = fiftyByteStringArray[0];
		if ((changedDescription != null) && (changedDescription.length() >= 2)) {
			if (changedDescription.substring(0, 2).compareTo("$#") == 0) {
				changedDescription = "$@" + changedDescription.substring(2);
			}
		}
		dataHM.put("Description1", changedDescription);
		*/
		
		
		
		dataHM.put("Description2", fiftyByteStringArray[1]);

		dataHM.put("Bulk_Loc_Type", "RACK");

		dataHM.put("Unit_Loc_Type", "SHELF");

		dataHM.put("Length", partHM.get("pkg_height"));

		dataHM.put("Width", partHM.get("pkg_width"));

		dataHM.put("Height", partHM.get("pkg_depth"));

		dataHM.put("Cube", "");


		{
			String weight_unit = (String) partHM.get("weight_unit");

			if ((StringUtils.isBlank(weight_unit) == true) || (weight_unit.trim().compareToIgnoreCase("LB") != 0)) {
				try {
					Number weight = ((Number) partHM.get("gross_weight")).doubleValue() / 16.0;
					dataHM.put("Weight", weight);
				} catch (Exception e) {
					dataHM.put("Weight", new Double(0));
				}
			}  else {
				dataHM.put("Weight", partHM.get("gross_weight"));
			}
		}

		// dataHM.put("Serial_No_Flag", partHM.get("must_serialnum"));
		dataHM.put("Serial_No_Flag", new Boolean(false));

		if (partvendAL.size() >= 3) {
			HashMap<String, Object> partvendHM = partvendAL.get(2);
			dataHM.put("Vendor_Part_3", partvendHM.get("vend_partnum"));
			dataHM.put("Vendor_ID_3", partvendHM.get("vendor_num"));
		} else {
			dataHM.put("Vendor_Part_3", "");
			dataHM.put("Vendor_ID_3", "");
		}
		if (partvendAL.size() >= 2) {
			HashMap<String, Object> partvendHM = partvendAL.get(1);
			dataHM.put("Vendor_Part_2", partvendHM.get("vend_partnum"));
			dataHM.put("Vendor_ID_2", partvendHM.get("vendor_num"));
		} else {
			dataHM.put("Vendor_Part_2", "");
			dataHM.put("Vendor_ID_2", "");
		}
		{
			HashMap<String, Object> partvendHM = partvendAL.get(0);
			dataHM.put("Vendor_Part_1", partvendHM.get("vend_partnum"));
			dataHM.put("Vendor_ID_1", partvendHM.get("vendor_num"));
		}


		dataHM.put("High_Quantity", "");

		dataHM.put("Tie_Quantity", "");

		dataHM.put("Master_Pack", partHM.get("altbill_qty"));

		dataHM.put("Master_Pack_UoM", partHM.get("altbill_unit"));

		dataHM.put("Shippable_Unit", "N");

		dataHM.put("Preemptive_Putaway_Flag", "N");

		dataHM.put("Cost", partHM.get("ave_cost"));

		dataHM.put("Substitute_SKU", "");

		dataHM.put("Unit_of_Measure", partHM.get("billing_unit"));

		if ((partHM.get("altbill_unit") == null) || (((String) partHM.get("altbill_unit")).compareTo("") == 0)) {
			dataHM.put("Bulk_Pick_Flag", "N");
		} else {
			dataHM.put("Bulk_Pick_Flag", "Y");
		}

		dataHM.put("Lot_Tracking", "N");

		// dataHM.put("New_SKU", ((action.compareToIgnoreCase("A") == 0)?"Y":"N"));
		dataHM.put("New_SKU", "N");

		// dataHM.put("Age_Control", "Y");
		dataHM.put("Age_Control", "N");

		dataHM.put("Rule_ID", "");

		if ((FasUtils.getNumberAsDouble(partHM.get("pkg_height")) == 0) 			||
			(FasUtils.getNumberAsDouble(partHM.get("pkg_height")) == Double.NaN) 	||
			(FasUtils.getNumberAsDouble(partHM.get("pkg_width"))  == 0) 			||
			(FasUtils.getNumberAsDouble(partHM.get("pkg_width"))  == Double.NaN) 	||
			(FasUtils.getNumberAsDouble(partHM.get("pkg_depth"))  == 0) 			||
			(FasUtils.getNumberAsDouble(partHM.get("pkg_depth"))  == Double.NaN) 	   ) {
			dataHM.put("Preferred_Zone", "PICT");
		} else {
			/*
			String imageFilePath = Utils.getImageFilePath(part_num, (String) partHM.get("sales_subcat"));
			if (imageFilePath == null) {
				dataHM.put("Preferred_Zone", "PICT");
			} else {
				dataHM.put("Preferred_Zone", "PICT");
			}
			*/
			dataHM.put("Preferred_Zone", "    ");
		}

		dataHM.put("Track_Manufacturing_Date", "N");

		dataHM.put("Track_Expiration_Date", "N");
	}

	// -------------------------------------------------------------------------------------------

	public String getKey1() {
		return part_num;
	}

    public String getKey2() {
		return "";
    }

	// -------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------

	public static ArrayList<HashMap<String, Object>> get_vend_partnum_O1(Connection wdsConnection, String part_num, HashMap<String, Object> partHM) throws SQLException {
		if (partHM == null) {
			ArrayList<HashMap<String, Object>> partAL = null;

			// partAL = JDBCUtils.runQuery(wdsConnection, "select part_num, vendor_num from pub.part where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' FOR UPDATE WITH (READPAST NOWAIT)");
			partAL = JDBCUtils.runQuery(wdsConnection, "select part_num, vendor_num from pub.part where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' WITH (READPAST NOWAIT)");

			if ((partAL == null) || (partAL.size() != 1)) {
				throw new RuntimeException("Part data is missing for part: " + part_num);
			}
			partHM = partAL.get(0);
		}

		ArrayList<HashMap<String, Object>> partvendAL = null;
		partvendAL = JDBCUtils.runQuery(wdsConnection, "select vendor_num, vend_partnum, vend_desc1, vend_desc2 from pub.partvend where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and vendor_num = '" + ((String) partHM.get("vendor_num")) + "' WITH (READPAST NOWAIT)");
		if ((partvendAL == null) || (partvendAL.size() == 0)) {
			partvendAL = JDBCUtils.runQuery(wdsConnection, "select vendor_num, vend_partnum, vend_desc1, vend_desc2 from pub.partvend where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' WITH (READPAST NOWAIT)");
			if ((partvendAL == null) || (partvendAL.size() == 0)) {
				log.debug("Partvend is not found, and so using the dummy vendor '-NONE-'");

				partvendAL = new ArrayList<HashMap<String,Object>>(1);
				HashMap<String, Object> partvendHM = new HashMap<String, Object>();
				partvendHM.put("vendor_num", "-NONE-");
				partvendHM.put("vend_partnum", part_num);
				partvendHM.put("vend_desc1", "");
				partvendHM.put("vend_desc2", "");
				partvendAL.add(partvendHM);

				return partvendAL;
			} else {
				log.debug("Partvend found using just partnum of the part record.");
				return partvendAL;
			}
		} else {
			log.debug("Partvend found using partnum, and vendor_num, of the part record.");
			return partvendAL;
		}
	}

	// -------------------------------------------------------------------------------------------

	public static HashMap<String, String> FASCOR_TO_WDS_FIELD_MAPPING_HM = new HashMap<String, String>(20); 
	public static String FASCOR_FIELD_LIST_FOR_COMPARISON;
	static {
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("SKU", "SKU");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Class", "Class");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Storage_Velocity_Code", "Storage_Velocity_Code");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("CC_Velocity_Code", "Cycle_Count_Velocity_Code");
		// FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Description1", "Description1");
		// FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Description2", "Description2");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Length", "Length");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Width", "Width");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Height", "Height");
							// FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Cube", "Cube");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Weight", "Weight");
							// FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Tie_Qty", "Tie_Quantity");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Cost", "Cost");
							// FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Substitute_SKU", "Substitute_SKU");
		FASCOR_TO_WDS_FIELD_MAPPING_HM.put("Unit_Desc", "Unit_of_Measure");
		
		FASCOR_FIELD_LIST_FOR_COMPARISON = "";
		Iterator<Entry<String, String>> it = FASCOR_TO_WDS_FIELD_MAPPING_HM.entrySet().iterator();
		
		while (it.hasNext()) {
			Map.Entry<String, String> pair = (Entry<String, String>) it.next();
			String key = pair.getKey();		
			FASCOR_FIELD_LIST_FOR_COMPARISON += ((FASCOR_FIELD_LIST_FOR_COMPARISON.compareTo("") != 0)?", ":"") + key;		
		}
	};

	public String compareFieldsWithFascor(Connection sqlServerConnection) throws SQLException {
    	ArrayList<HashMap<String, Object>> fascorAL = JDBCUtils.runQuery(sqlServerConnection, "select " + FASCOR_FIELD_LIST_FOR_COMPARISON + " from SKU_Master where sku = '" + part_num + "'");
    	if ((fascorAL != null) && (fascorAL.size() > 0)) {
    		return 	compareWDSAndFascorFields(dataHM, fascorAL.get(0), FASCOR_TO_WDS_FIELD_MAPPING_HM);
    	} else {
    		return "not found in Fascor";
    	}
	}

	// -------------------------------------------------------------------------------------------

}
