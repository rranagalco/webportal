package galco.fascor.requests.control;

import galco.fascor.bin_change.ChangeBin;
import galco.fascor.messages.Message_1420;
import galco.fascor.process.FascorProcessor;
import galco.fascor.utils.FasUtils;
import galco.portal.config.Parms;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class NightlyBordItemSyncJob {
	private static Logger log = Logger.getLogger(NightlyBordItemSyncJob.class);

	static String lastProcessedDate = "";
	static long previousAttemptTime = 0;

	public static void synchInventoryForBordItems(Connection wdsConnection, Connection sqlServerConnection) {
		String todayCCYYMMDD = new SimpleDateFormat("yyyyMMdd").format(new Date());


		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

		if (System.currentTimeMillis() < (previousAttemptTime + 600000)) {
			// log.debug("It's not yet time to update on-hand quantities. - Millis");
			return;
		}
		previousAttemptTime = System.currentTimeMillis();

		log.debug("Checking if we need to synch inventory for BORD inventory items, " + todayCCYYMMDD + " " + hour + " " + System.currentTimeMillis());
		
		if (lastProcessedDate.compareTo(todayCCYYMMDD) == 0) {
			// log.debug("It's not yet time to update on-hand quantities. - Date");
			previousAttemptTime = System.currentTimeMillis();
			return;
		}
		
		
		
        // DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER 

		hour = 21;
		
		
		
		
		
		
		// if (hour == 0) {
		if ((hour != 21) && (hour != 22)) {
			// log.debug("It's not yet time to update bins. - Hour, " + hour + ", is not 21 or 22");
			previousAttemptTime = System.currentTimeMillis();
			return;
		}

		
		// ----------------------------------------------------------------------------------------------------------
		// ----------------------------------------------------------------------------------------------------------

		
		try {
			log.debug("BORDSync - Starting nightly synch of inventory for BORD inventory items");

			// String sqlStatement = "select loc_id, sku, actual_qty from Loc_Allocation where Loc_ID like 'BORD%' order by sku";
    		String sqlStatement = "select loc_id, sku, actual_qty from Loc_Allocation " +
		                    	  " where sku in (select sku from Loc_Allocation where Loc_ID like 'BORD%') " +
							      // " where sku = 'PC1300-DURA'" +
    							  " order by sku";
    		
    		ArrayList<HashMap<String, Object>> al = JDBCUtils.runQuery(sqlServerConnection, sqlStatement);
    		// FasUtils.printArrayListOfHashMaps(al);

    		if ((al == null) || (al.size() == 0)) {
    			log.debug("Completed nightly synch of inventory for BORD inventory items");            
    			lastProcessedDate = todayCCYYMMDD;
	    		previousAttemptTime = 0;
	    		return;
    		}    		
    		
    		String prevSku = "ZZZZZZZZZZZZZZZZZZZZZZZZZZ";
            int actual_qty_fscr = 0;	   
            String loc_id = "";
    		
        	for (Iterator<HashMap<String, Object>> iterator = al.iterator(); iterator.hasNext();) {
        		HashMap<String, Object> loacAlHashMap = (HashMap<String, Object>) iterator.next();
        		
                loc_id = (String) loacAlHashMap.get("loc_id");
                String sku = ((String) loacAlHashMap.get("sku")).trim();
                
                log.debug("sku: " + sku + ", loc_id: " + loc_id + ", actual_qty_fscr: " + ((Number) loacAlHashMap.get("actual_qty")).intValue());

                if (prevSku.compareToIgnoreCase("ZZZZZZZZZZZZZZZZZZZZZZZZZZ") == 0) {
	                prevSku = sku;                        	
                    actual_qty_fscr = ((Number) loacAlHashMap.get("actual_qty")).intValue();                        	
                	continue;                        	
                }

                if (sku.compareToIgnoreCase(prevSku) == 0) {
                    actual_qty_fscr += ((Number) loacAlHashMap.get("actual_qty")).intValue();
                	continue;
                } 

        		ArrayList<HashMap<String, Object>> partlocAL = null;
                try {
                	// partlocAL = JDBCUtils.runQuery(wdsConnection, "select qty_onhand from pub.partloc where part_num = '" + prevSku + "' and qty_onhand <> " + actual_qty);
                	partlocAL = JDBCUtils.runQuery(wdsConnection, "select qty_onhand from pub.partloc where part_num = '" + prevSku + "'");
                	if ((partlocAL != null) && (partlocAL.size() == 1)) {
                        int qty_onhand_wds = ((Number) (partlocAL.get(0)).get("qty_onhand")).intValue();
                        
                        if (qty_onhand_wds < actual_qty_fscr) {
                            log.debug("BORDSync sku: " + prevSku + ", loc_id: " + loc_id + ", actual_qty_fscr: " + actual_qty_fscr + ", qty_onhand_wds: " + qty_onhand_wds + ", correction: " + (qty_onhand_wds - actual_qty_fscr));

        					reduceQuantityFromFascor(wdsConnection, sqlServerConnection, prevSku, qty_onhand_wds - actual_qty_fscr);
        					
                        } else {
                            log.debug("BORDSync sku: " + prevSku + ", loc_id: " + loc_id + ", actual_qty_fscr: " + actual_qty_fscr + ", qty_onhand_wds: " + qty_onhand_wds + ", NO correction.");
                        }
                	} else {
                        log.debug("BORDSync sku: " + prevSku + " not found in WDS.");		                		
                	}
        		} catch (SQLException e1) {
        			log.debug("BORDSync Exception: " + e1.getMessage(), e1);
        		}
                
                prevSku = sku;
                actual_qty_fscr = ((Number) loacAlHashMap.get("actual_qty")).intValue();
        	}

    		
    		ArrayList<HashMap<String, Object>> partlocAL = null;
            try {
            	// partlocAL = JDBCUtils.runQuery(wdsConnection, "select qty_onhand from pub.partloc where part_num = '" + prevSku + "' and qty_onhand <> " + actual_qty);
            	partlocAL = JDBCUtils.runQuery(wdsConnection, "select qty_onhand from pub.partloc where part_num = '" + prevSku + "'");
            	if ((partlocAL != null) && (partlocAL.size() == 1)) {
                    int qty_onhand_wds = ((Number) (partlocAL.get(0)).get("qty_onhand")).intValue();
                    
                    if (qty_onhand_wds < actual_qty_fscr) {
                        log.debug("BORDSync sku: " + prevSku + ", loc_id: " + loc_id + ", actual_qty_fscr: " + actual_qty_fscr + ", qty_onhand_wds: " + qty_onhand_wds + ", correction: " + (qty_onhand_wds - actual_qty_fscr));

    					reduceQuantityFromFascor(wdsConnection, sqlServerConnection, prevSku, qty_onhand_wds - actual_qty_fscr);
                    } else {
                        log.debug("BORDSync sku: " + prevSku + ", loc_id: " + loc_id + ", actual_qty_fscr: " + actual_qty_fscr + ", qty_onhand_wds: " + qty_onhand_wds + ", NO correction.");
                    }
            	} else {
                    log.debug("BORDSync sku: " + prevSku + " not found in WDS.");		                		
            	}
    		} catch (SQLException e1) {
    			log.debug("BORDSync Exception: " + e1.getMessage(), e1);
    		}
            
			log.debug("BORDSync - Completed nightly synch of inventory for BORD inventory items");            
		} catch (Exception e) {
			String exceptionMessage = e.getMessage();
			if (e instanceof SQLException) {
				String sqlExceptionInfo = FasUtils.getSQLExceptionInfo((SQLException) e);
				log.debug(sqlExceptionInfo);
				exceptionMessage = sqlExceptionInfo;
			} else {
				log.debug(e);
			}

			String msg = "Error occurred while trying to nightly synch inventory for BORD inventory items. Exception message: " + exceptionMessage;
			log.debug(msg);
			galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Problem in FASCOR Batch Process of " + Parms.HOST_NAME, msg);
		}
		

		// ----------------------------------------------------------------------------------------------------------
		// ----------------------------------------------------------------------------------------------------------
		
		
		lastProcessedDate = todayCCYYMMDD;
		previousAttemptTime = 0;

		
		// ----------------------------------------------------------------------------------------------------------
		// ----------------------------------------------------------------------------------------------------------

	
	}

	
	// ----------------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------------

	
	public static void reduceQuantityFromFascor(Connection wdsConnection, Connection sqlServerConnection, String part_num, int reduceThisMuchQuantityFromSku) {
		reduceThisMuchQuantityFromSku = Math.abs(reduceThisMuchQuantityFromSku);

		String sqlStatement = "select loc_id, sku, actual_qty from Loc_Allocation " +
							  " where sku = '" + part_num + "' and Loc_ID like 'BORD%'";

		ArrayList<HashMap<String, Object>> al;
		try {
			al = JDBCUtils.runQuery(sqlServerConnection, sqlStatement);
		} catch (SQLException e) {
			log.debug("Exception: " + e.getMessage() + " occurred while processing Nightly Bord Loc Sync, part_num " + part_num, e);
			return;
		}
		if (al != null) {
			for (Iterator<HashMap<String, Object>> iterator = al.iterator(); iterator.hasNext();) {
				HashMap<String, Object> loacAlHashMap = (HashMap<String, Object>) iterator.next();
				
	            String loc_id = (String) loacAlHashMap.get("loc_id");
	            int actual_qty = ((Number) loacAlHashMap.get("actual_qty")).intValue();
	            
	            int qtyToReduceFromThisLocation = (reduceThisMuchQuantityFromSku >= actual_qty)?actual_qty:reduceThisMuchQuantityFromSku; 
		
	        	send1420Message(wdsConnection, sqlServerConnection, part_num, qtyToReduceFromThisLocation * -1, loc_id);

				reduceThisMuchQuantityFromSku -= qtyToReduceFromThisLocation;

				if (reduceThisMuchQuantityFromSku <= 0) {
					return;
				}
			}
		}
		
		if (reduceThisMuchQuantityFromSku <= 0) {
			return;
		}


		// ----------------------------------------------------------------------------------------------------------


		sqlStatement = "select loc_id, sku, actual_qty from Loc_Allocation " +
		 			   " where sku = '" + part_num + "' and Loc_ID NOT like 'BORD%'";

		try {
			al = JDBCUtils.runQuery(sqlServerConnection, sqlStatement);
		} catch (SQLException e) {
			log.debug("Exception: " + e.getMessage() + " occurred while processing Nightly Bord Loc Sync, part_num " + part_num, e);
			return;
		}
		if (al != null) {
			for (Iterator<HashMap<String, Object>> iterator = al.iterator(); iterator.hasNext();) {
				HashMap<String, Object> loacAlHashMap = (HashMap<String, Object>) iterator.next();

				String loc_id = (String) loacAlHashMap.get("loc_id");
				int actual_qty = ((Number) loacAlHashMap.get("actual_qty")).intValue();

				int qtyToReduceFromThisLocation = (reduceThisMuchQuantityFromSku >= actual_qty)?actual_qty:reduceThisMuchQuantityFromSku; 

				send1420Message(wdsConnection, sqlServerConnection, part_num, qtyToReduceFromThisLocation * -1, loc_id);

				reduceThisMuchQuantityFromSku -= qtyToReduceFromThisLocation;

				if (reduceThisMuchQuantityFromSku <= 0) {
					return;
				}
			}
		}

		// ----------------------------------------------------------------------------------------------------------

		
	}

	
	// ----------------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------------

	
	public static void send1420Message(Connection wdsConnection, Connection sqlServerConnection, String part_num, int adjustment_qty, String location) {
		try {
			log.debug("Sending 1420 for part " + part_num + " qty adj: " + adjustment_qty);
			Message_1420 message_1420 = new Message_1420(wdsConnection, sqlServerConnection, part_num, adjustment_qty, "C", location);

			FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20);
			FasUtils.sendMessageToFascor(sqlServerConnection, message_1420.getFascorMessage());
            JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);
		} catch (Exception e) {
			log.debug("Exception occurred while sending 1420 for part " + part_num + ", qty adj: " + adjustment_qty + ", message: " + e.getMessage());
			try {
                JDBCUtils.rollbackFascorChanges_Fascor(sqlServerConnection);
			} catch (Exception e2) {
			}
		}
	}
		
	// ----------------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------------


}

