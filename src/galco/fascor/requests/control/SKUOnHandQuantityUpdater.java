package galco.fascor.requests.control;

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

public class SKUOnHandQuantityUpdater {
	private static Logger log = Logger.getLogger(SKUOnHandQuantityUpdater.class);

	static String lastProcessedDate = "";
	static long previousAttemptTime = 0;

	public static void buildAndSend1420Message(Connection wdsConnection, Connection sqlServerConnection, String part_num, int adjustment_qty) {
		try {
			// log.debug("Sending 1420 for part " + part_num + " qty adj: " + adjustment_qty);
			Message_1420 message_1420 = new Message_1420(wdsConnection, sqlServerConnection, part_num, adjustment_qty, "C");
			// log.debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

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

	public static void sendUpdatedOnhandQuantitiesToFascor(Connection wdsConnection, Connection sroConnection, Connection sqlServerConnection) {
		// Danger
		String todayCCYYMMDD = new SimpleDateFormat("yyyyMMdd").format(new Date());
		// String todayCCYYMMDD = "20190712";


		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

		if (System.currentTimeMillis() < (previousAttemptTime + 600000)) {
			// log.debug("It's not yet time to update on-hand quantities. - Millis");
			return;
		}
		previousAttemptTime = System.currentTimeMillis();

		log.debug("Checking if we need to update OnHand quantities, " + todayCCYYMMDD + " " + hour + " " + System.currentTimeMillis());
		
		if (lastProcessedDate.compareTo(todayCCYYMMDD) == 0) {
			// log.debug("It's not yet time to update on-hand quantities. - Date");
			previousAttemptTime = System.currentTimeMillis();
			return;
		}
		// Danger
		// if (hour == 0) {
		if ((hour != 21) && (hour != 22)) {
			// log.debug("It's not yet time to update on-hand quantities. - Hour, " + hour + ", is not 21 or 22");
			previousAttemptTime = System.currentTimeMillis();
			return;
		}

		
		// ----------------------------------------------------------------------------------------------------------
		// ----------------------------------------------------------------------------------------------------------

		
		try {
			// Danger
			String todayMMDDYY = new SimpleDateFormat("MM/dd/yy").format(new Date());
			// String todayMMDDYY = "07/12/2019";

			{
				ArrayList<HashMap<String, Object>> shipmentAL = JDBCUtils.runQuery(sroConnection, "select carrier from pub.shipment where location = '100' and pickup_date = '" + todayMMDDYY + "' and carrier in ('N/A', 'UPS', 'FEDEXP', 'C/P', 'S/D', 'O/D') and curr_status = 'OPEN' WITH (READPAST NOWAIT)");
				if ((shipmentAL != null) && (shipmentAL.size() > 0)) {
					log.debug("Shipping Stations are still open.");
					return;
				} else {
					log.debug("Shipping Stations are all closed.");
				}
			}

			ArrayList<HashMap<String, Object>> parttranAL = JDBCUtils.runQuery(sroConnection, "select part_num, trans_type, qty, order_num from pub.parttran where location = '100' and trans_date = '" + todayMMDDYY + "' order by part_num, trans_type WITH (READPAST NOWAIT)");
			if ((parttranAL != null) && (parttranAL.size() > 0)) {
				int adjustment_qty = 0;

				String part_num_prev = "";

				for (Iterator<HashMap<String, Object>> iterator = parttranAL.iterator(); iterator.hasNext();) {
					HashMap<String, Object> parttranMap = (HashMap<String, Object>) iterator.next();

					String part_num = (String) parttranMap.get("part_num");
					String trans_type = (String) parttranMap.get("trans_type");
					int qty = ((Number) parttranMap.get("qty")).intValue();
					String order_num = (String) parttranMap.get("order_num");

					try {
						if (part_num.compareToIgnoreCase(part_num_prev) != 0) {
							if ((part_num_prev.compareToIgnoreCase("") != 0) && (adjustment_qty != 0)) {
								log.debug("    Sending 1420 for part " + part_num_prev + ", qty adjustment: " + adjustment_qty);
								// Danger
								buildAndSend1420Message(wdsConnection, sqlServerConnection, part_num_prev, adjustment_qty);
							} else {
								log.debug("    Not sending 1420 for part " + part_num_prev + " because qty adj is " + adjustment_qty);
							}

		        			part_num_prev = part_num;
		        			adjustment_qty = 0;
		        			
		        			log.debug(part_num_prev);		        			
						}

						if (part_num.compareToIgnoreCase(part_num_prev) == 0) {

							// ----------------------------------------------------------------

							
							if (trans_type.compareToIgnoreCase("RCPT") == 0) {
			        			log.debug("    Ignoring    " + trans_type + ", qty: " + qty);
								adjustment_qty += 0;
								
								
							} else if (trans_type.compareToIgnoreCase("CADJ") == 0) {
			        			log.debug("    Ignoring    " + trans_type + ", qty: " + qty);
								adjustment_qty += 0;
								
								
							} else if (trans_type.compareToIgnoreCase("VOID") == 0) {
			        			// Voided receipts, mostly have -ve quantities, ignoring these because
								//    Intregration sends 1420 for these
								log.debug("    Ignoring    " + trans_type + ", qty: " + qty);
			        			adjustment_qty += 0;

			        			
							} else if (trans_type.compareToIgnoreCase("KREQ") == 0) {
			        			log.debug("    Ignoring    " + trans_type + ", qty: " + qty);
			        			// Kits used by Requisitions, doesn't reduce the inventory
								// adjustment_qty -= qty;
			        			adjustment_qty += 0;
								
								
			        		// ----------------------------------------------------------------
			        			
							} else if (trans_type.compareToIgnoreCase("QADJ") == 0) {
								if ((order_num != null											) && 
									(order_num.length() >= 2									) &&
									(order_num.substring(0, 2).compareToIgnoreCase("KB") == 0	)    ) {
				        			log.debug("    Considering " + trans_type + ", qty: " + qty + ", order_num: " + order_num);
									adjustment_qty += qty;
								} else {
				        			log.debug("    Ignoring    " + trans_type + ", qty: " + qty + ", order_num: " + order_num);
									adjustment_qty += 0;
								}
							} else if (trans_type.compareToIgnoreCase("KUSE") == 0) {
			        			log.debug("    Considering " + trans_type + ", qty: " + qty + ", order_num: " + order_num);
								adjustment_qty -= qty;
								
								
							} else if (trans_type.compareToIgnoreCase("PHYS") == 0) {
			        			log.debug("    Considering " + trans_type + ", qty: " + qty);
			        			// Qty Adj done in WDS.
								adjustment_qty += qty;
								
								
							} else if (trans_type.compareToIgnoreCase("SALE") == 0) {
			        			log.debug("    Considering " + trans_type + ", qty: " + qty);
			        			// If qty > 0 then send -ve adj, else send +ve adj
			        			// qty > 0 means a sale
			        			// qty < 0 means a credit memo
								adjustment_qty -= qty;
								
								
							} else if (trans_type.compareToIgnoreCase("USED") == 0) {
			        			log.debug("    Considering " + trans_type + ", qty: " + qty);
			        			// Supplies used by Reqs etc. If qty is positive then subtract, or else add.
								adjustment_qty -= qty;
								
								
							} else if (trans_type.compareToIgnoreCase("RTRN") == 0) {
			        			log.debug("    Considering " + trans_type + ", qty: " + qty);
			        			// Vendor returns and so they decrease the inventory
								adjustment_qty -= qty;

								
							// ----------------------------------------------------------------

							}
						}

					} catch (Exception e) {
						log.debug("Exception while processing night time inv adj message 1420. part_num_prev: " + part_num_prev + ", part_num: " + part_num + ", trans_type: " + trans_type, e);
					}
				}

				try {
					if ((part_num_prev.compareToIgnoreCase("") != 0) && (adjustment_qty != 0)) {
						log.debug("    Sending 1420 for part " + part_num_prev + ", qty adjustment: " + adjustment_qty);						
						// Danger
						buildAndSend1420Message(wdsConnection, sqlServerConnection, part_num_prev, adjustment_qty);
					} else {
						log.debug("    Not sending 1420 for part " + part_num_prev + " because qty adj is " + adjustment_qty);
						log.debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					}
				} catch (Exception e) {
					log.debug("Exception while processing night time inv adj message 1420. part_num_prev: " + part_num_prev, e);
				}
			}

			// -----------------------------------------------------------------------------------------------------------------

			// lastProcessedDate = todayCCYYMMDD;
			// previousAttemptTime = 0;

			// return;
		} catch (Exception e) {
			String exceptionMessage = e.getMessage();
			if (e instanceof SQLException) {
				String sqlExceptionInfo = FasUtils.getSQLExceptionInfo((SQLException) e);
				log.debug(sqlExceptionInfo);
				exceptionMessage = sqlExceptionInfo;
			} else {
				log.debug(e);
			}

			String msg = "Error occurred while trying to update on-hand quantities. Exception message: " + exceptionMessage;
			log.debug(msg);
			galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Problem in FASCOR Batch Process of " + Parms.HOST_NAME, msg);

			// lastProcessedDate = todayCCYYMMDD;
			// previousAttemptTime = 0;

			// throw e;
			// return;
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
	// ----------------------------------------------------------------------------------------------------------

	public static void sendUpdatedOnhandQuantitiesToFascorO2(Connection wdsConnection, Connection sroConnection, Connection sqlServerConnection) throws Exception {
		String todayCCYYMMDD = new SimpleDateFormat("yyyyMMdd").format(new Date());
		// String todayCCYYMMDD = "20180629";


		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

		log.debug(todayCCYYMMDD + " " + hour + " " + System.currentTimeMillis());

		if (System.currentTimeMillis() < (previousAttemptTime + 600000)) {
			log.debug("It's not yet time to update on-hand quantities. - Millis");
			previousAttemptTime = System.currentTimeMillis();
			return;
		}
		if (lastProcessedDate.compareTo(todayCCYYMMDD) == 0) {
			log.debug("It's not yet time to update on-hand quantities. - Date");
			previousAttemptTime = System.currentTimeMillis();
			return;
		}

		// if (hour == 0) {
		if ((hour != 21) && (hour != 22)) {
			log.debug("It's not yet time to update on-hand quantities. - Hour");
			previousAttemptTime = System.currentTimeMillis();
			return;
		}


		try {
			{
				String todayMMDDYY = new SimpleDateFormat("MM/dd/yy").format(new Date());
				// String todayMMDDYY = "06/29/18";

				ArrayList<HashMap<String, Object>> shipmentAL = JDBCUtils.runQuery(sroConnection, "select carrier from pub.shipment where location = '100' and pickup_date = '" + todayMMDDYY + "' and carrier in ('N/A', 'UPS', 'FEDEXP', 'C/P', 'S/D', 'O/D') and curr_status = 'OPEN' WITH (READPAST NOWAIT)");
				if ((shipmentAL != null) && (shipmentAL.size() > 0)) {
					log.debug("Shipping Stations are still open.");
					return;
				} else {
					log.debug("Shipping Stations are all closed.");
				}
			}


			ArrayList<HashMap<String, Object>> fascorOnhandQtyChangesAL = JDBCUtils.runQuery(wdsConnection, "select part_num, changedDate, changedTime, qty_onhand from pub.FascorOnhandQtyChanges where changedDate = '" + todayCCYYMMDD + "' WITH (READPAST NOWAIT)");
			FasUtils.print_AL_Of_HMs(fascorOnhandQtyChangesAL);


			JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "delete from [dbo].[WDS_Sales]");


			for (Iterator<HashMap<String, Object>> iterator = fascorOnhandQtyChangesAL.iterator(); iterator.hasNext();) {
				HashMap<String, Object> fascorOnhandQtyChangesHM = (HashMap<String, Object>) iterator.next();
				String part_num = (String) fascorOnhandQtyChangesHM.get("part_num");

				ArrayList<HashMap<String, Object>> partlocAL = JDBCUtils.runQuery(wdsConnection, "select qty_commit, qty_onhand from pub.partloc where location = '100' and part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' WITH (READPAST NOWAIT)");
				if ((partlocAL != null) && (partlocAL.size() > 0)) {
					HashMap<String, Object> partlocHM = (HashMap<String, Object>) partlocAL.get(0);
					// log.debug(String.format("%1$40s", part_num) + " - " + partlocHM.get("qty_onhand"));
					log.debug(part_num + "#$#" + partlocHM.get("qty_onhand"));

					String part_num_30 = (part_num.length() > 30)?part_num.substring(0, 30):part_num;

					String sqlStatement = "INSERT INTO [dbo].[WDS_Sales] ([SKU],[WDSQty]) VALUES ('" + part_num_30 + "', " + partlocHM.get("qty_onhand") + ")";
					log.debug(sqlStatement);
					JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, sqlStatement);
				} else {
					// log.debug(String.format("%1$40s", part_num) + " - Partloc record not found.");
					log.debug(part_num + "#$#Partloc record not found.");
				}
			}

			// -----------------------------------------------------------------------------------------------------------------

			log.debug("\n\n\nWriting onhand_qty > 0 to WDS_QTYOH\n\n\n");

			JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "delete from [dbo].[WDS_QTYOH]");

			ArrayList<HashMap<String, Object>> partlocOHQtyGTZeroAL = JDBCUtils.runQuery(wdsConnection, "select part_num, qty_onhand from pub.partloc where location = '100' and qty_onhand > 0 WITH (READPAST NOWAIT)");
			if ((partlocOHQtyGTZeroAL != null) && (partlocOHQtyGTZeroAL.size() > 0)) {
				for (Iterator<HashMap<String, Object>> iterator  = partlocOHQtyGTZeroAL.iterator(); iterator.hasNext();) {
					HashMap<String, Object> partlocHM = (HashMap<String, Object>) iterator.next();

					String part_num = (String) partlocHM.get("part_num");
					String part_num_30 = (part_num.length() > 30)?part_num.substring(0, 30):part_num;
					part_num_30 = part_num_30.replaceAll("'", "''");

					log.debug(part_num_30 + " " + partlocHM.get("qty_onhand"));

					String sqlStatement = "INSERT INTO [dbo].[WDS_QTYOH] ([SKU],[WDSQty]) VALUES ('" + part_num_30 + "', " + partlocHM.get("qty_onhand") + ")";
					JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, sqlStatement);
				}
			}

			// -----------------------------------------------------------------------------------------------------------------

            JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);

			lastProcessedDate = todayCCYYMMDD;
			previousAttemptTime = System.currentTimeMillis();

			return;
		} catch (Exception e) {
			String exceptionMessage = e.getMessage();
			if (e instanceof SQLException) {
				String sqlExceptionInfo = FasUtils.getSQLExceptionInfo((SQLException) e);
				log.debug(sqlExceptionInfo);
				exceptionMessage = sqlExceptionInfo;
			} else {
				log.debug(e);
			}

			String msg = "Error occurred while trying to update on-hand quantities. Exception message: " + exceptionMessage;
			log.debug(msg);
			galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Problem in FASCOR Batch Process of " + Parms.HOST_NAME, msg);

			lastProcessedDate = todayCCYYMMDD;
			previousAttemptTime = System.currentTimeMillis();

			// throw e;

			// return;
		}
	}

	public static void sendUpdatedOnhandQuantitiesToFascorO1(Connection wdsConnection, Connection sroConnection) throws Exception {
		// String todayMMDDYY = new SimpleDateFormat("MM/dd/yy").format(new Date());
		String todayMMDDYY = "06/06/17";


		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

		log.debug(todayMMDDYY + " " + hour + " " + System.currentTimeMillis());

		if (System.currentTimeMillis() < (previousAttemptTime + 600000)) {
			log.debug("It's not yet time to update on-hand quantities. - Millis");
			previousAttemptTime = System.currentTimeMillis();
			return;
		}
		if (lastProcessedDate.compareTo(todayMMDDYY) == 0) {
			log.debug("It's not yet time to update on-hand quantities. - Date");
			previousAttemptTime = System.currentTimeMillis();
			return;
		}

		if (hour == 0) {
		// if ((hour != 21) && (hour != 22)) {
			log.debug("It's not yet time to update on-hand quantities. - Hour");
			previousAttemptTime = System.currentTimeMillis();
			return;
		}

		try {
			{
				ArrayList<HashMap<String, Object>> shipmentAL = JDBCUtils.runQuery(sroConnection, "select carrier from pub.shipment where location = '100' and pickup_date = '" + todayMMDDYY + "' and carrier in ('N/A', 'UPS', 'FEDEXP', 'C/P', 'S/D', 'O/D') and curr_status = 'OPEN' WITH (READPAST NOWAIT)");
				if ((shipmentAL != null) && (shipmentAL.size() > 0)) {
					log.debug("Shipping Stations are still open.");
					return;
				} else {
					log.debug("Shipping Stations are all closed.");
				}
			}

			ArrayList<HashMap<String, Object>> partsShippedTodayAL = JDBCUtils.runQuery(wdsConnection, "select invi.part_num from pub.invoice inv, pub.invitem invi where inv.inv_date = '" + todayMMDDYY + "' and inv.inv_num = invi.inv_num WITH (READPAST NOWAIT)");
			FasUtils.print_AL_Of_HMs(partsShippedTodayAL);

			HashMap<String, String> uniquePartsHM = new HashMap<String, String>(5000);

			for (Iterator iterator = partsShippedTodayAL.iterator(); iterator.hasNext();) {
				HashMap<String, Object> partShippedTodayHM = (HashMap<String, Object>) iterator.next();
				String part_num = (String) partShippedTodayHM.get("part_num");

				if (uniquePartsHM.get(part_num) != null) {
					log.debug("Part " + part_num + " is a duplicate.");
					continue;
				}

				ArrayList<HashMap<String, Object>> partlocAL = JDBCUtils.runQuery(wdsConnection, "select qty_commit, qty_onhand from pub.partloc where location = '100' and part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' WITH (READPAST NOWAIT)");
				if ((partlocAL != null) && (partlocAL.size() > 0)) {
					HashMap<String, Object> partlocHM = (HashMap<String, Object>) partlocAL.get(0);
					// log.debug(String.format("%1$40s", part_num) + " - " + partlocHM.get("qty_onhand"));
					log.debug(part_num + "#$#" + partlocHM.get("qty_onhand"));
				} else {
					// log.debug(String.format("%1$40s", part_num) + " - Partloc record not found.");
					log.debug(part_num + "#$#Partloc record not found.");
				}

				uniquePartsHM.put(part_num, "");
			}



			lastProcessedDate = todayMMDDYY;
			previousAttemptTime = 0;

			return;
		} catch (Exception e) {
			String exceptionMessage = e.getMessage();
			if (e instanceof SQLException) {
				String sqlExceptionInfo = FasUtils.getSQLExceptionInfo((SQLException) e);
				log.debug(sqlExceptionInfo);
				exceptionMessage = sqlExceptionInfo;
			} else {
				log.debug(e);
			}

			String msg = "Error occurred while trying to update on-hand quantities. Exception message: " + exceptionMessage;
			log.debug(msg);
			galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Problem in FASCOR Batch Process of " + Parms.HOST_NAME, msg);

			lastProcessedDate = todayMMDDYY;

			throw e;

			// return;
		}
	}

	public static void main(String[] args) {
		DBConnector dbConnector8 = null;
		try {
			if (FascorProcessor.getDBConnections() == true) {
			    JDBCUtils.UPDATE_FASCOR_DB = true;
			    JDBCUtils.UPDATE_WDS_DB = true;

				for (int i = 0; i < 1; i++) {
					sendUpdatedOnhandQuantitiesToFascor(FascorProcessor.dbConnector8.getConnectionWDS(), FascorProcessor.dbConnector8.getConnectionSRO(), FascorProcessor.sqlServerConnection);
				}

				FascorProcessor.closeDBConnections();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (FascorProcessor.dbConnector8 != null) {
				try {
					FascorProcessor.closeDBConnections();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}
}

