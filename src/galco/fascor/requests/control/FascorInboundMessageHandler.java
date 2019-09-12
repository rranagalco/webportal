package galco.fascor.requests.control;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.h2.util.IntArray;

import galco.fascor.cache.CacheManager;
import galco.fascor.messages.Message;
import galco.fascor.messages.MessageAbs;
import galco.fascor.messages.Message_0140;
import galco.fascor.messages.Message_1110;
import galco.fascor.messages.Message_1210;
import galco.fascor.messages.Message_1220;
import galco.fascor.messages.Message_1230;
import galco.fascor.messages.Message_1310;
import galco.fascor.messages.Message_1320;
import galco.fascor.messages.Message_1420;
import galco.fascor.messages.Message_2015;
import galco.fascor.messages.Message_3052;
import galco.fascor.messages.Message_3057;
import galco.fascor.process.FascorProcessor;
import galco.fascor.update.OrderAndPOUpdateHandler;
import galco.fascor.utils.FasUtils;
import galco.fascor.wds_requests.WDSFascorRequest;
import galco.fascor.wds_requests.WDSFascorRequestCommon;
import galco.fascor.wds_requests.WDSFascorRequest_0001;
import galco.fascor.wds_requests.WDSFascorRequest_0140;
import galco.fascor.wds_requests.WDSFascorRequest_1110;
import galco.fascor.wds_requests.WDSFascorRequest_1210;
import galco.fascor.wds_requests.WDSFascorRequest_1220;
import galco.fascor.wds_requests.WDSFascorRequest_1310;
import galco.fascor.wds_requests.WDSFascorRequest_1320;
import galco.fascor.wds_requests.WDSFascorRequest_2015;
import galco.fascor.wds_requests.WDSFascorRequest_3052;
import galco.fascor.wds_requests.WDSFascorRequest_9999;
import galco.portal.config.Parms;
import galco.portal.db.DBConnector;
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.FascorMessage;

public class FascorInboundMessageHandler {
	private static Logger log = Logger.getLogger(FascorInboundMessageHandler.class);

	// ---------------------------------------------------------------------------------------------------

	static long lastTimeDBConnectionWasMade = Long.MIN_VALUE;

	static final long UTC_OFFSET = TimeZone.getDefault().getOffset(System.currentTimeMillis());

	public static ArrayList<String> KEYS_OF_INBOUND_MESSAGES;

	public static void deleteFascorMessages(Connection wdsConnection, ArrayList<FascorMessage> fascorMessageAL) throws SQLException {
    	if ((fascorMessageAL != null) && (fascorMessageAL.size() > 0)) {
			for (Iterator<FascorMessage> iterator2 = fascorMessageAL.iterator(); iterator2.hasNext();) {
				FascorMessage fascorMessage = iterator2.next();
				fascorMessage.delete(wdsConnection);
			}
    	}
	}

	public static void processsSingleKeyRequest(Connection wdsConnection, Connection sqlServerConnection, WDSFascorRequestCommon wdsCommon, String key) throws SQLException, RuntimeException {
		if (wdsCommon.getAction().compareToIgnoreCase("D") == 0) {
			FascorMessage oldFascorMessage = null;
			ArrayList<FascorMessage> oldFascorMessageAL = FascorMessage.getDistinctFascorMessages(wdsConnection, wdsCommon.getMessageType(), key);
			if ((oldFascorMessageAL != null) && (oldFascorMessageAL.size() > 0)) {
				oldFascorMessage = oldFascorMessageAL.get(0);

				String deleteMessage = wdsCommon.getMessageType() + "D" + oldFascorMessage.getFascormessage().substring(5);
	        	FasUtils.sendMessageToFascor(sqlServerConnection, wdsCommon.getSequenceNumber(), deleteMessage);

	        	deleteFascorMessages(wdsConnection, oldFascorMessageAL);
			}
		} else {
			String action = "A";
			FascorMessage oldFascorMessage = null;
			ArrayList<FascorMessage> oldFascorMessageAL = FascorMessage.getDistinctFascorMessages(wdsConnection, wdsCommon.getMessageType(), key);
			if ((oldFascorMessageAL != null) && (oldFascorMessageAL.size() > 0)) {
				action = "C";
				oldFascorMessage = oldFascorMessageAL.get(0);
			}
			wdsCommon.setAction(action);

			String newFascorMessageStr = null;
			if (wdsCommon.getMessageType().compareTo("0140") == 0) {
		        Message_0140 message_0140 = new Message_0140(wdsConnection, key, wdsCommon.getAction());
		        newFascorMessageStr = FasUtils.buildFascorMessage(message_0140);
			} else if (wdsCommon.getMessageType().compareTo("1110") == 0) {
		        Message_1110 message_1110 = new Message_1110(wdsConnection, key, wdsCommon.getAction());
		        newFascorMessageStr = FasUtils.buildFascorMessage(message_1110);
			}

	        if ((oldFascorMessage == null) || (newFascorMessageStr.substring(5).compareTo(oldFascorMessage.getFascormessage().substring(5)) != 0)) {
	        	log.debug("New message:" + newFascorMessageStr);
	        	log.debug("Old message:" + ((oldFascorMessage != null)?(oldFascorMessage.getFascormessage()):("")));

	        	FasUtils.sendMessageToFascor(sqlServerConnection, wdsCommon.getSequenceNumber(), newFascorMessageStr);
	        	FascorMessage fascorMessage = new FascorMessage(wdsCommon.getSequenceNumber(), wdsCommon.getMessageType(), key, "", "", newFascorMessageStr);
	        	fascorMessage.persist(wdsConnection);

	        	deleteFascorMessages(wdsConnection, oldFascorMessageAL);
	        } else {
	        	log.debug(wdsCommon.getMessageType() + " message didn't change from previous send.");
	        }
		}
	}

	public static void deleteOrderOrPO(Connection wdsConnection, Connection sqlServerConnection, WDSFascorRequestCommon wdsCommon, String key) throws SQLException {
		String messageTypeLine = "";
		int actualFascorMessageStartingPosition = 5;
		if (wdsCommon.getMessageType().compareTo("1210") == 0) {
			 messageTypeLine = "1220";
			 actualFascorMessageStartingPosition = 5;
		} else if (wdsCommon.getMessageType().compareTo("1310") == 0) {
			 messageTypeLine = "1320";
			 actualFascorMessageStartingPosition = 5;
		} else if (wdsCommon.getMessageType().compareTo("3052") == 0) {
			 messageTypeLine = "3057";
			 actualFascorMessageStartingPosition = 7;
		}

		ArrayList<FascorMessage> oldFascorMessageAL = FascorMessage.getDistinctFascorMessages(wdsConnection, wdsCommon.getMessageType(), key);
		if ((oldFascorMessageAL != null) && (oldFascorMessageAL.size() > 0)) {
			FascorMessage oldFascorMessageForOrder = oldFascorMessageAL.get(0);

			String changeFascorMessageStr = oldFascorMessageForOrder.getFascormessage().substring(0, actualFascorMessageStartingPosition - 1) + "D" + oldFascorMessageForOrder.getFascormessage().substring(actualFascorMessageStartingPosition);
			FasUtils.sendMessageToFascor(sqlServerConnection, oldFascorMessageForOrder.getSequencenumber(), changeFascorMessageStr);

			oldFascorMessageForOrder.delete(wdsConnection);
        }

		ArrayList<FascorMessage> oldFascorMessagesForLinesAL = FascorMessage.getDistinctFascorMessages(wdsConnection, messageTypeLine, key);
		for (Iterator<FascorMessage> iterator2 = oldFascorMessagesForLinesAL.iterator(); iterator2.hasNext();) {
			FascorMessage fascorMessageForLineOld = iterator2.next();

			String deleteFascorMessageStr = fascorMessageForLineOld.getFascormessage().substring(0, actualFascorMessageStartingPosition - 1) + "D" + fascorMessageForLineOld.getFascormessage().substring(actualFascorMessageStartingPosition);
			FasUtils.sendMessageToFascor(sqlServerConnection, fascorMessageForLineOld.getSequencenumber(), deleteFascorMessageStr);

			fascorMessageForLineOld.delete(wdsConnection);
		}
	}

	public static boolean postponeNewRequestAndSetProcessingFlagToT(Connection wdsConnection, Connection sqlServerConnection, WDSFascorRequestCommon wdsCommon) throws SQLException {
		
		if (true) {
			FasUtils.set_FascorRequest_Processed_Flag_CommitChanges(wdsConnection, wdsCommon.getSequenceNumber(), "T");
			return false;
		}
		
		String didIPostPoneBefore = wdsCommon.getData().substring(28, 29);
		if (didIPostPoneBefore.compareTo("Y") == 0) {
			FasUtils.set_FascorRequest_Processed_Flag_CommitChanges(wdsConnection, wdsCommon.getSequenceNumber(), "T");
			return false;
		}
		
		int minutesToPostpone = 5;
		
		String yyyyMmDdHmMmSsSss = FasUtils.convertDateTo_yyyyMmDdHmMmSsSss(FasUtils.addMinutes(new Date(), minutesToPostpone));
		String newSequenceNumber = yyyyMmDdHmMmSsSss + ((int) (Math.random() * 100000));

		log.debug("Postponing request for " + minutesToPostpone + " minutes. " + wdsCommon.getData() + ". Changing sequenceNumber " + wdsCommon.getSequenceNumber() + " to " + newSequenceNumber + " . -1201ZZ- ");
		
		String updateSQL =
		"update pub.FascorRequests set data = '" + wdsCommon.getData().substring(0, 28) + "Y" + wdsCommon.getData().substring(29) + "', " +
		                          "sequenceNumber = '" + newSequenceNumber + "' where sequenceNumber = '" +  wdsCommon.getSequenceNumber() + "'";
		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, updateSQL);
		JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
		
		return true;
	}

	public static int processFascorRequests(Connection wdsConnection, Connection sqlServerConnection, ArrayList<HashMap<String, Object>> fascorRequestsAL) {
		int totalNoOfErrorsFound = 0;

		ArrayList<WDSFascorRequestCommon> wdsFascorRequestCommonAL = new ArrayList<WDSFascorRequestCommon>(1200);

		for (Iterator<HashMap<String, Object>> iterator = fascorRequestsAL.iterator(); iterator.hasNext();) {
			HashMap<String, Object> fascorRequestHM = (HashMap<String, Object>) iterator.next();

			WDSFascorRequestCommon wdsFascorRequestCommon;

			try {
				wdsFascorRequestCommon = new WDSFascorRequestCommon(fascorRequestHM);
				wdsFascorRequestCommon.print();

				if (wdsFascorRequestCommon.getMessageType().compareTo("0001") == 0) {
					wdsFascorRequestCommonAL.add(new WDSFascorRequest_0001(wdsFascorRequestCommon));
				} else if (wdsFascorRequestCommon.getMessageType().compareTo("0140") == 0) {
					wdsFascorRequestCommonAL.add(new WDSFascorRequest_0140(wdsFascorRequestCommon));
				} else if (wdsFascorRequestCommon.getMessageType().compareTo("1110") == 0) {
					wdsFascorRequestCommonAL.add(new WDSFascorRequest_1110(wdsFascorRequestCommon));
				} else if (wdsFascorRequestCommon.getMessageType().compareTo("1210") == 0) {
					wdsFascorRequestCommonAL.add(new WDSFascorRequest_1210(wdsFascorRequestCommon));
				} else if (wdsFascorRequestCommon.getMessageType().compareTo("1310") == 0) {
					wdsFascorRequestCommonAL.add(new WDSFascorRequest_1310(wdsFascorRequestCommon));
				} else if (wdsFascorRequestCommon.getMessageType().compareTo("2015") == 0) {
					wdsFascorRequestCommonAL.add(new WDSFascorRequest_2015(wdsFascorRequestCommon));
				} else if (wdsFascorRequestCommon.getMessageType().compareTo("3052") == 0) {
					wdsFascorRequestCommonAL.add(new WDSFascorRequest_3052(wdsFascorRequestCommon));
				} else if (wdsFascorRequestCommon.getMessageType().compareTo("9999") == 0) {
					wdsFascorRequestCommonAL.add(new WDSFascorRequest_9999(wdsFascorRequestCommon));
				} else {
					totalNoOfErrorsFound++;

					log.debug("Error occurred while processing FascorRequest, invalid message type.");
					log.debug("\tFascorRequest: " + wdsFascorRequestCommon.toString());

					try {
						FasUtils.set_FascorRequest_Processed_Flag_CommitChanges(wdsConnection, wdsFascorRequestCommon.getSequenceNumber(), "E");
					} catch (Exception e) {
						log.debug("", e);
					}
				}
			} catch (Exception e) {
				totalNoOfErrorsFound++;

				log.debug("Error occurred while processing FascorRequest, parse exception occurred.");
				try {
					log.debug("FascorRequest: ");					
					FasUtils.printAHashMap(fascorRequestHM);
				} catch (Exception e2) {
					log.debug("Exception occurred.", e2);
				}
				log.debug("Exception message: " + e.getMessage());
				log.debug("\tsequenceNumber: " + (String) fascorRequestHM.get("sequenceNumber") + ", exception: " + e.getMessage());

				try {
					FasUtils.set_FascorRequest_Processed_Flag_CommitChanges(wdsConnection, (String) fascorRequestHM.get("sequenceNumber"), "E");
				} catch (SQLException e1) {
					log.debug("", e1);
				}
			}
		}


		Collections.sort(wdsFascorRequestCommonAL);		

		
		log.debug("====================================================================================");
		log.debug("====================================================================================");

		for (Iterator<WDSFascorRequestCommon> iterator = wdsFascorRequestCommonAL.iterator(); iterator.hasNext();) {
			WDSFascorRequestCommon wdsFascorRequestCommon = (WDSFascorRequestCommon) iterator.next();
			wdsFascorRequestCommon.print();
		}

		log.debug("====================================================================================");
		log.debug("====================================================================================");


		ArrayList<String> newPartNumbers_ChangedParts = new ArrayList<String>(20);
		ArrayList<String> oldPartNumbers_ChangedParts = new ArrayList<String>(20);
		ArrayList<String> sequenceNumbers_ChangedParts = new ArrayList<String>(20);
		ArrayList<String> erroredNewPartNumbers_ChangedParts = new ArrayList<String>(20);
		ArrayList<String> erroredOldPartNumbers_ChangedParts = new ArrayList<String>(20);
		ArrayList<String> erroredSequenceNumbers_ChangedParts = new ArrayList<String>(20);

		WDSFascorRequestCommon wdsCommon = null;
		WDSFascorRequestCommon wdsCommonPrev = null;

		boolean duplicateFascorRequest = false;

		String m, a, mp, ap;

		int processedThisManyInThisbatch = 0;
		
		long startTimeDebug = System.currentTimeMillis();
		int recordNoDebug = 0;
		
		for (Iterator<WDSFascorRequestCommon> iterator = wdsFascorRequestCommonAL.iterator(); iterator.hasNext();) {
			recordNoDebug++;
			log.debug("zzyyxx - Time taken for processing record no. " + (recordNoDebug - 1) + " (In Millis): " + (System.currentTimeMillis() - startTimeDebug));
			startTimeDebug = System.currentTimeMillis();

			// =====================================================================================================
			// =====================================================================================================
			
			processedThisManyInThisbatch++;
			log.debug("Processing request number " + processedThisManyInThisbatch + " in the current batch.");

			// =====================================================================================================
			// =====================================================================================================

			if (Parms.FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX == true) {
				if (new File(Parms.FASCOR_PROCESSOR_TERMINATION_SIGNAL_FILE).exists() == true) {
					try {
						log.debug("Exiting because of termination signal file ...");
						FascorProcessor.closeDBConnections();

						new File(Parms.FASCOR_PROCESSOR_TERMINATION_SIGNAL_FILE).delete();
						new File(Parms.FASCOR_PROCESSOR_SINGLE_INSTANCE_FILE).delete();
					} catch (Exception e) {
						log.debug("Exception while trying to exit...", e);
					}
					System.exit(0);
				}
			}
			
			// =====================================================================================================

			log.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		
	        String curSequenceNumber = null;
			wdsCommon = (WDSFascorRequestCommon) iterator.next();

			try {
				curSequenceNumber = wdsCommon.getSequenceNumber();

				m = wdsCommon.getMessageType();
				a = wdsCommon.getAction();
				mp = (wdsCommonPrev != null)?wdsCommonPrev.getMessageType():"";
				ap = (wdsCommonPrev != null)?wdsCommonPrev.getAction():"";

				duplicateFascorRequest = false;
				if (m.compareTo(mp) == 0) {
	                if (wdsCommon.getMessageType().compareTo("0001") == 0) {
	                	WDSFascorRequest_0001 wds0001 = (WDSFascorRequest_0001) wdsCommon;
	                	WDSFascorRequest_0001 wds0001Prev = (WDSFascorRequest_0001) wdsCommonPrev;

	                    if ((wds0001.getPart_num_new().compareTo(wds0001Prev.getPart_num_new()) == 0) &&
	                    	(wds0001.getPart_num_old().compareTo(wds0001Prev.getPart_num_old()) == 0)    ) {
	                        if ((a.compareTo(ap) == 0           ) ||
	                            (   (ap.compareTo("A") == 0) &&
	                                (a.compareTo("C") == 0 )    )    ) {
	                            duplicateFascorRequest = true;
	                        }
	                    }
	                } else if (wdsCommon.getMessageType().compareTo("0140") == 0) {
	                	WDSFascorRequest_0140 wds0140 = (WDSFascorRequest_0140) wdsCommon;
	                	WDSFascorRequest_0140 wds0140Prev = (WDSFascorRequest_0140) wdsCommonPrev;

	                    if (wds0140.getVendor_num().compareTo(wds0140Prev.getVendor_num()) == 0) {
	                        if ((a.compareTo(ap) == 0           ) ||
	                            (   (ap.compareTo("A") == 0) &&
	                                (a.compareTo("C") == 0 )    )    ) {
	                            duplicateFascorRequest = true;
	                        }
	                    }
	                } else if (wdsCommon.getMessageType().compareTo("1110") == 0) {
	                	WDSFascorRequest_1110 wds1110 = (WDSFascorRequest_1110) wdsCommon;
	                	WDSFascorRequest_1110 wds1110Prev = (WDSFascorRequest_1110) wdsCommonPrev;

	                    if (wds1110.getPart_num().compareTo(wds1110Prev.getPart_num()) == 0) {
	                        if ((a.compareTo(ap) == 0           ) ||
	                            (   (ap.compareTo("A") == 0) &&
	                                (a.compareTo("C") == 0 )    )    ) {
	                            duplicateFascorRequest = true;
	                        }
	                    }
	                } else if (wdsCommon.getMessageType().compareTo("1210") == 0) {
	                	WDSFascorRequest_1210 wds1210 = (WDSFascorRequest_1210) wdsCommon;
	                	WDSFascorRequest_1210 wds1210Prev = (WDSFascorRequest_1210) wdsCommonPrev;

	                    if (wds1210.getOrder_num().compareTo(wds1210Prev.getOrder_num()) == 0) {
	                        if ((a.compareTo(ap) == 0           ) ||
	                            (   (ap.compareTo("A") == 0) &&
	                                (a.compareTo("C") == 0 )    )    ) {
	                            duplicateFascorRequest = true;
	                        }
	                    }
	                } else if (wdsCommon.getMessageType().compareTo("1310") == 0) {
	                	WDSFascorRequest_1310 wds1310 = (WDSFascorRequest_1310) wdsCommon;
	                	WDSFascorRequest_1310 wds1310Prev = (WDSFascorRequest_1310) wdsCommonPrev;

	                    if (wds1310.getOrder_num().compareTo(wds1310Prev.getOrder_num()) == 0) {
	                        if ((a.compareTo(ap) == 0           ) ||
	                            (   (ap.compareTo("A") == 0) &&
	                                (a.compareTo("C") == 0 )    )    ) {
	                            duplicateFascorRequest = true;
	                        }
	                    }
	                /*
	                } else if (wdsCommon.getMessageType().compareTo("2015") == 0) {
	                	WDSFascorRequest_2015 wds2015 = (WDSFascorRequest_2015) wdsCommon;
	                	WDSFascorRequest_2015 wds2015Prev = (WDSFascorRequest_2015) wdsCommonPrev;

	                    if (wds2015.getPart_num().compareTo(wds2015Prev.getPart_num()) == 0) {
	                        if ((a.compareTo(ap) == 0           ) ||
	                            (   (ap.compareTo("A") == 0) &&
	                                (a.compareTo("C") == 0 )    )    ) {
	                            duplicateFascorRequest = true;
	                        }
	                    }
	                */
	                } else if (wdsCommon.getMessageType().compareTo("3052") == 0) {
	                	WDSFascorRequest_3052 wds3052 = (WDSFascorRequest_3052) wdsCommon;
	                	WDSFascorRequest_3052 wds3052Prev = (WDSFascorRequest_3052) wdsCommonPrev;

	                    if (wds3052.getPart_num().compareTo(wds3052Prev.getPart_num()) == 0) {
	                        if ((a.compareTo(ap) == 0           ) ||
	                            (   (ap.compareTo("A") == 0) &&
	                                (a.compareTo("C") == 0 )    )    ) {
	                            duplicateFascorRequest = true;
	                        }
	                    }
	                } else if (wdsCommon.getMessageType().compareTo("9999") == 0) {
	                	WDSFascorRequest_9999 wds9999 = (WDSFascorRequest_9999) wdsCommon;
	                	WDSFascorRequest_9999 wds9999Prev = (WDSFascorRequest_9999) wdsCommonPrev;

	                    if ((wds9999.getReceiver_num().compareTo(wds9999Prev.getReceiver_num()) == 0) &&
   	                        (wds9999.getOrder_num().compareTo(wds9999Prev.getOrder_num()) == 0      ) &&
	                    	(wds9999.getLine_num() == wds9999Prev.getLine_num()				        ) &&
	                    	(wds9999.getQty_voided() == wds9999Prev.getQty_voided()			        )    ) { 
	                        if ((a.compareTo(ap) == 0           ) ||
	                            (   (ap.compareTo("A") == 0) &&
	                                (a.compareTo("C") == 0 )    )    ) {
	                            duplicateFascorRequest = true;
	                        }
	                    }
	                }
				}

				// -------------------------------------------------------------------------------------------
				// -------------------------------------------------------------------------------------------

				if (duplicateFascorRequest == false) {
					/*
		        	long lastUsedSeqNbrInBound_Before = FasUtils.get_IDENT_CURRENT(sqlServerConnection, "InBound");
		        	long lastUsedSeqNbrOutBound_Before = FasUtils.get_IDENT_CURRENT(sqlServerConnection, "OutBound");
		        	long lastUsed_error_log_num_Pferrorlog_Before = FasUtils.get_IDENT_CURRENT(sqlServerConnection, "pferrorlog");
		        	*/

		        	KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20);

		        	if (wdsCommon.getMessageType().compareTo("0001") == 0) {
	                	WDSFascorRequest_0001 wds0001 = (WDSFascorRequest_0001) wdsCommon;
	                	String part_num_new = "", part_num_old = "",
	                		   generatedSequenceNumber = wdsCommon.getSequenceNumber();
        		        try {
		                	part_num_new = wds0001.getPart_num_new();
		                	part_num_old = wds0001.getPart_num_old();

	        				WDSFascorRequestCommon wdsFascorRequestCommon = FasUtils.createFascorRequest(wdsConnection, "1110C", part_num_new);
			                JDBCUtils.commitWDSChanges_Fascor(wdsConnection);

			            	ArrayList<String> saved_KEYS_OF_INBOUND_MESSAGES = KEYS_OF_INBOUND_MESSAGES;
	        				ArrayList<HashMap<String, Object>> createdFascorRequestsAL = null;
	        				createdFascorRequestsAL = JDBCUtils.runQuery(wdsConnection, "select sequenceNumber, processed, functionCode, data, \"key\" from pub.FascorRequests where processed = 'N' and sequenceNumber = '" + wdsFascorRequestCommon.getSequenceNumber()+ "' WITH (NOLOCk)");
	        		        int noOfErrors = FascorInboundMessageHandler.processFascorRequests(wdsConnection, sqlServerConnection, createdFascorRequestsAL);
	        		        if (noOfErrors > 0) {
	        		        	throw new RuntimeException("Failed to add changed part to Fascor.");
	        		        }
	        		        KEYS_OF_INBOUND_MESSAGES = saved_KEYS_OF_INBOUND_MESSAGES;

	                    	ArrayList<String> partlocAL = JDBCUtils.runQuery_ReturnStrALs(wdsConnection, "select part_num from pub.partloc where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num_new) + "' and qty_onhand > 0")[0];
	                    	if ((partlocAL != null) && (partlocAL.size() > 0)) {
				    			galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com,kmesser@galco.com", "WDSFascorIntegration@galco.com", "Part number changed, and this part has inventory.", "New part number: " + part_num_new+ ", Old part number: " + part_num_old);
	                    	}

	                    	newPartNumbers_ChangedParts.add(part_num_new);
			    			oldPartNumbers_ChangedParts.add(part_num_old);
			    			sequenceNumbers_ChangedParts.add(wdsFascorRequestCommon.getSequenceNumber());
        		        } catch(Exception e) {
        		        	erroredNewPartNumbers_ChangedParts.add(part_num_new);
        		        	erroredOldPartNumbers_ChangedParts.add(part_num_old);
        		        	erroredSequenceNumbers_ChangedParts.add(generatedSequenceNumber);

        		        	throw e;
        		        }
	                } else if (wdsCommon.getMessageType().compareTo("0140") == 0) {
	                	WDSFascorRequest_0140 wds0140 = (WDSFascorRequest_0140) wdsCommon;

	                    if (postponeNewRequestAndSetProcessingFlagToT(wdsConnection, sqlServerConnection, wdsCommon) == true) {
			            	wdsCommonPrev = wdsCommon;
			            	continue;
						}

	                    if (wdsCommon.getAction().compareToIgnoreCase("D") == 0) {
	                    	CacheManager.deleteFascorMessages(wdsConnection, sqlServerConnection, wdsCommon.getMessageType(), wds0140.getVendor_num());
	                    } else {
		    		        Message_0140 message_0140 = new Message_0140(wdsConnection, wds0140.getVendor_num(), wdsCommon.getAction());
	                    	CacheManager.sendFascorMessages(wdsConnection, sqlServerConnection, message_0140);
	                    }
	                } else if (wdsCommon.getMessageType().compareTo("1110") == 0) {
                        WDSFascorRequest_1110 wds1110 = (WDSFascorRequest_1110) wdsCommon;
                        
                        /* 
                        if (wds1110.getPart_num().trim().compareToIgnoreCase("PC1300-DURA") == 0) {
                        	throw new RuntimeException("Throwing runtime exception.");
                        }
                        */
                        
	                    if (postponeNewRequestAndSetProcessingFlagToT(wdsConnection, sqlServerConnection, wdsCommon) == true) {
			            	wdsCommonPrev = wdsCommon;
			            	continue;
						}

	                    if (wdsCommon.getAction().compareToIgnoreCase("D") == 0) {
	                    	CacheManager.deleteFascorMessages(wdsConnection, sqlServerConnection, wdsCommon.getMessageType(), wds1110.getPart_num());
	                    } else {
	        		        Message_1110 message_1110 = new Message_1110(wdsConnection, wds1110.getPart_num(), wdsCommon.getAction());	        		        
	                    	CacheManager.sendFascorMessages(wdsConnection, sqlServerConnection, message_1110);
	                    }
	                } else if (wdsCommon.getMessageType().compareTo("1210") == 0) {
	                    WDSFascorRequest_1210 wds1210 = (WDSFascorRequest_1210) wdsCommon;
	                    
	                    if (postponeNewRequestAndSetProcessingFlagToT(wdsConnection, sqlServerConnection, wdsCommon) == true) {
			            	wdsCommonPrev = wdsCommon;
			            	continue;
						}

	                    // =========================================================================================
	                    // =========================================================================================
	                    
	                    boolean poIsClosed = false;
	                    {
		        			String queryString = "select po.date_closed from pub.FascorMessageCache as c, pub.porder as po " +
		        								 " where c.messageType = '1210' and c.key1 = '" + wds1210.getOrder_num() + "' and po.order_num = c.key1 and po.date_closed is not null WITH (NOLOCK)";
		                    ArrayList<HashMap<String, Object>> closedDate_AL = null;
		                    closedDate_AL = JDBCUtils.runQuery(wdsConnection, queryString);
		                    if ((closedDate_AL != null) && (closedDate_AL.size() > 0)) {
		                    	poIsClosed = true;
							}
	                    }

	                    // =========================================================================================
	                    // =========================================================================================
	                    
						// if (wdsCommon.getAction().compareToIgnoreCase("D") == 0) {
	                    if ((wdsCommon.getAction().compareToIgnoreCase("D") == 0) || (poIsClosed == true)) {
	                    	CacheManager.deleteFascorMessages(wdsConnection, sqlServerConnection, "1230", wds1210.getOrder_num());
	                    	CacheManager.deleteFascorMessages(wdsConnection, sqlServerConnection, "1220", wds1210.getOrder_num());
	                    	CacheManager.deleteFascorMessages(wdsConnection, sqlServerConnection, wdsCommon.getMessageType(), wds1210.getOrder_num());
	                    } else {
		                    String order_num = wds1210.getOrder_num();

		                    {
		                    	for (int i = 0; i < erroredNewPartNumbers_ChangedParts.size(); i++) {
									String erroredNewPartNumber_ChangedParts = erroredNewPartNumbers_ChangedParts.get(i);

			                    	ArrayList<String> changedPartsUsedAL = JDBCUtils.runQuery_ReturnStrALs(wdsConnection, "select part_num from pub.pitem where order_num = '" + order_num + "' and part_num = '" + Utils.replaceSingleQuotesIfNotNull(erroredNewPartNumber_ChangedParts) + "'")[0];
			                    	if ((changedPartsUsedAL != null) && (changedPartsUsedAL.size() > 0)) {
			                    		String errMsg = "Error. Part name chage: New part no: " + erroredNewPartNumbers_ChangedParts.get(i) + ", old part no: " + erroredOldPartNumbers_ChangedParts.get(i) + ", parent seq no : " + erroredSequenceNumbers_ChangedParts.get(i) + ", child seq no: " + wds1210.getSequenceNumber() + ", affected PO no: " + wds1210.getOrder_num();
			                    		log.debug(errMsg);
			                    		throw new RuntimeException(errMsg);
			                    	}
								}

		                    	for (int i = 0; i < newPartNumbers_ChangedParts.size(); i++) {
									String newPartNumber_ChangedParts = newPartNumbers_ChangedParts.get(i);

			                    	ArrayList<String> changedPartsUsedAL = JDBCUtils.runQuery_ReturnStrALs(wdsConnection, "select part_num from pub.pitem where order_num = '" + order_num + "' and part_num = '" + Utils.replaceSingleQuotesIfNotNull(newPartNumber_ChangedParts) + "'")[0];
			                    	if ((changedPartsUsedAL != null) && (changedPartsUsedAL.size() > 0)) {
			                    		log.debug("Part name chage: New part no: " + newPartNumbers_ChangedParts.get(i) + ", old part no: " + oldPartNumbers_ChangedParts.get(i) + ", parent seq no : " + sequenceNumbers_ChangedParts.get(i) + ", child seq no: " + wds1210.getSequenceNumber() + ", affected PO no: " + wds1210.getOrder_num());
			                    	}
								}
		                    }
		                    
	                        Message_1210 message_1210 = new Message_1210(wdsConnection, wds1210.getOrder_num(), "A");								
	                    	CacheManager.sendFascorMessages(wdsConnection, sqlServerConnection, message_1210);

	                    	ArrayList<Message> messageAL = new ArrayList<Message>(10);
		                    ArrayList<HashMap<String, Object>> pitemAL = null;
		                    // pitemAL = JDBCUtils.runQuery(wdsConnection, "select order_num, line_num, part_num from pub.pitem where order_num = '" + order_num + "' and (qty_ord - qty_cancel) > 0 and drop_ship = '0' order by line_num WITH (NOLOCK)");
		                    // pitemAL = JDBCUtils.runQuery(wdsConnection, "select order_num, line_num, part_num from pub.pitem where order_num = '" + order_num + "' and (qty_ord - qty_received - qty_cancel) > 0 and drop_ship = '0' order by line_num WITH (NOLOCK)");
		                    pitemAL = JDBCUtils.runQuery(wdsConnection, "select order_num, line_num, part_num from pub.pitem where order_num = '" + order_num + "' and (qty_ord - qty_cancel) > 0 and drop_ship = '0' order by line_num WITH (NOLOCK)");
		                    if ((pitemAL != null) && (pitemAL.size() > 0)) {
			                    for (Iterator<HashMap<String, Object>>  iterator2 = pitemAL.iterator(); iterator2.hasNext();) {
		                            HashMap<String, Object> pitemHM = iterator2.next();

		                            Message_1220 message_1220 = new Message_1220(wdsConnection, wds1210.getOrder_num(), ((Number) pitemHM.get("line_num")).intValue(), "A");										
		                            messageAL.add(message_1220);
		                        }
			                    
		                    	CacheManager.sendFascorMessages(wdsConnection, sqlServerConnection, messageAL);
		                    }

	                    	messageAL = new ArrayList<Message>(10);
		                    ArrayList<HashMap<String, Object>> pcommentAL = null;
		                    // pcommentAL = JDBCUtils.runQuery(wdsConnection, "select distinct pc.line_num from pub.pcomment pc, pub.pitem pi where pc.order_num = '" + order_num + "' and pc.order_num = pi.order_num and pc.line_num = pi.line_num and prt_on_rcvr = '1' order by pc.line_num WITH (NOLOCK)");
		                    // pcommentAL = JDBCUtils.runQuery(wdsConnection, "select distinct pc.line_num from pub.pcomment pc, pub.pitem pi where pc.order_num = '" + order_num + "' and pc.prt_on_rcvr = '1' and pc.order_num = pi.order_num and pc.line_num = pi.line_num and (pi.qty_ord - pi.qty_received - pi.qty_cancel) > 0 and pi.drop_ship = '0' order by pc.line_num WITH (NOLOCK)");
		                    pcommentAL = JDBCUtils.runQuery(wdsConnection, "select distinct pc.line_num from pub.pcomment pc, pub.pitem pi where pc.order_num = '" + order_num + "' and pc.prt_on_rcvr = '1' and pc.order_num = pi.order_num and pc.line_num = pi.line_num and (pi.qty_ord - pi.qty_cancel) > 0 and pi.drop_ship = '0' order by pc.line_num WITH (NOLOCK)");
		                    if ((pcommentAL != null) && (pcommentAL.size() > 0)) {
			                    for (Iterator<HashMap<String, Object>>  iterator2 = pcommentAL.iterator(); iterator2.hasNext();) {
		                            HashMap<String, Object> pcommentHM = iterator2.next();

		                            Message_1230 message_1230 = new Message_1230(wdsConnection, wds1210.getOrder_num(), ((Number) pcommentHM.get("line_num")).intValue(), "A");
		                            messageAL.add(message_1230);
		                        }
			                    
		                    	CacheManager.sendFascorMessages(wdsConnection, sqlServerConnection, messageAL);
		                    }
	                    }
	                } else if (wdsCommon.getMessageType().compareTo("1310") == 0) {
	                	WDSFascorRequest_1310 wds1310 = (WDSFascorRequest_1310) wdsCommon;

	                	if (wdsCommon.getAction().compareToIgnoreCase("D") == 0) {
	                    	CacheManager.deleteFascorMessages(wdsConnection, sqlServerConnection, "1320", wds1310.getOrder_num());
	                    	CacheManager.deleteFascorMessages(wdsConnection, sqlServerConnection, wdsCommon.getMessageType(), wds1310.getOrder_num());
	                    } else {
		                	String order_num = wds1310.getOrder_num();

		                    {
		                    	for (int i = 0; i < erroredNewPartNumbers_ChangedParts.size(); i++) {
									String erroredNewPartNumber_ChangedParts = erroredNewPartNumbers_ChangedParts.get(i);

			                    	ArrayList<String> changedPartsUsedAL = JDBCUtils.runQuery_ReturnStrALs(wdsConnection, "select part_num from pub.oitem where order_num = '" + order_num + "' and part_num = '" + Utils.replaceSingleQuotesIfNotNull(erroredNewPartNumber_ChangedParts) + "'")[0];
			                    	if ((changedPartsUsedAL != null) && (changedPartsUsedAL.size() > 0)) {
			                    		String errMsg = "Error. Part name chage: New part no: " + erroredNewPartNumbers_ChangedParts.get(i) + ", old part no: " + erroredOldPartNumbers_ChangedParts.get(i) + ", parent seq no : " + erroredSequenceNumbers_ChangedParts.get(i) + ", child seq no: " + wds1310.getSequenceNumber() + ", affected order no: " + wds1310.getOrder_num();
			                    		log.debug(errMsg);
			                    		throw new RuntimeException(errMsg);
			                    	}
								}

		                    	for (int i = 0; i < newPartNumbers_ChangedParts.size(); i++) {
									String newPartNumber_ChangedParts = newPartNumbers_ChangedParts.get(i);

			                    	ArrayList<String> changedPartsUsedAL = JDBCUtils.runQuery_ReturnStrALs(wdsConnection, "select part_num from pub.oitem where order_num = '" + order_num + "' and part_num = '" + Utils.replaceSingleQuotesIfNotNull(newPartNumber_ChangedParts) + "'")[0];
			                    	if ((changedPartsUsedAL != null) && (changedPartsUsedAL.size() > 0)) {
			                    		log.debug("Part name chage: New part no: " + newPartNumbers_ChangedParts.get(i) + ", old part no: " + oldPartNumbers_ChangedParts.get(i) + ", parent seq no : " + sequenceNumbers_ChangedParts.get(i) + ", child seq no: " + wds1310.getSequenceNumber() + ", affected order no: " + wds1310.getOrder_num());
			                    	}
								}
		                    }

		                    ArrayList<Object> partsToBuildAL;
		                	ArrayList<Object> quanititiesToBuildAL;
	                		ArrayList<Object>[] alArr = JDBCUtils.runQuery_ReturnObjALs(wdsConnection, "select o.part_num, o.qty_com_asm from pub.oitem as o, pub.part as p where o.order_num = '" + order_num + "' and p.part_num = o.part_num and p.kit = 1 and o.qty_com_asm > 0  WITH (NOLOCK)");
	                		partsToBuildAL = alArr[0];
	                		quanititiesToBuildAL = alArr[1];
	                		if ((partsToBuildAL != null) && (partsToBuildAL.size() > 0)) {
			                	for (int i = 0; i < partsToBuildAL.size(); i++) {
			        				String part_num = (String) partsToBuildAL.get(i);
			        				int quanitityToBuild = (int) quanititiesToBuildAL.get(i);
			        				log.debug(part_num + " " + quanitityToBuild);

			        				WDSFascorRequestCommon wdsFascorRequestCommon = FasUtils.createFascorRequest(wdsConnection, "2015A", order_num.trim() + "," + part_num.trim() + "," + quanitityToBuild);

			        				String sqlStatement = "insert into pub.OrdersWaitingOnKits (order_num, sequenceNumber, sequenceNumberOfKitBuildRqst, part_num, quantityBuilt, origQuantity, buildComplete) values ('" + order_num + "', '" + wds1310.getSequenceNumber() + "', '" + wdsFascorRequestCommon.getSequenceNumber() + "', '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "', 0, " + quanitityToBuild + ", 'N')";
				        			// log.debug(sqlStatement);
				        			JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStatement);
			        			}

								FasUtils.set_FascorRequest_Processed_Flag(wdsConnection, curSequenceNumber, "H");

				                JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
				            	wdsCommonPrev = wdsCommon;
				            	continue;

	                		} else {
		                        Message_1310 message_1310 = new Message_1310(wdsConnection, wds1310.getOrder_num(), "A");
		                    	CacheManager.sendFascorMessages(wdsConnection, sqlServerConnection, message_1310);

		                    	ArrayList<Message> messageAL = new ArrayList<Message>(10);
			                    ArrayList<HashMap<String, Object>> oitemAL = null;
			                	oitemAL = JDBCUtils.runQuery(wdsConnection, "select order_num, line_num, part_num from pub.oitem where order_num = '" + order_num + "' and (qty_ord - qty_cancel) > 0 order by line_num  WITH (NOLOCK)");

			                    if ((oitemAL != null) && (oitemAL.size() > 0)) {
				                    for (Iterator<HashMap<String, Object>>  iterator2 = oitemAL.iterator(); iterator2.hasNext();) {
			                            HashMap<String, Object> oitemHM = iterator2.next();

			                            Message_1320 message_1320 = new Message_1320(wdsConnection, wds1310.getOrder_num(), ((Number) oitemHM.get("line_num")).intValue(), "A");
			                            messageAL.add(message_1320);
			                        }
			                    	CacheManager.sendFascorMessages(wdsConnection, sqlServerConnection, messageAL);
			                    }
	                    	}
	                    }

	                // ---------------------------------------------------------------------------------------------------

	                } else if (wdsCommon.getMessageType().compareTo("2015") == 0) {
	                	WDSFascorRequest_2015 wds2015 = (WDSFascorRequest_2015) wdsCommon;

	                	String part_num = wds2015.getPart_num();
	                	int quantityToBuild = wds2015.getQuantityToBuild();
	                	String yyyyMmDdHmMmSs = FasUtils.convertDateTo_yyyyMmDdHmMmSs(new Date());

	                	String order_num;
	                	if (wds2015.getOrder_num() == null) {
		                	order_num = "OL" + yyyyMmDdHmMmSs.substring(2) + (10 + ((int) (Math.random() * 89)));
	                	} else {
		                	order_num = wds2015.getOrder_num();
	                	}

	                	String eng_rls_num = Message_3052.get_eng_rls_num(wdsConnection, part_num);

	                	ArrayList<Object> componentAL;
	                	ArrayList<Object> qty_usedAL;
	                	{
	                		ArrayList<Object>[] alArr = JDBCUtils.runQuery_ReturnObjALs(wdsConnection, "select partnum_comp, qty_used from pub.part_bom where partnum_assm = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and eng_rls_num  = '" + eng_rls_num + "'");
	                		componentAL = alArr[0];
	                		qty_usedAL = alArr[1];
	                	}

	                	String debugCommittingMessage = order_num + " - " + part_num + " - " + eng_rls_num + " - Committs - ";

	                	for (int i = 0; i < componentAL.size(); i++) {
							String partnum_comp = (String) componentAL.get(i);
							int qty_used = (int) qty_usedAL.get(i);

							int quantityCommitting = quantityToBuild * qty_used;

							debugCommittingMessage += (partnum_comp + " " + quantityCommitting + " ");

	                		ArrayList<String> partAvailAL = JDBCUtils.runQuery_ReturnStrALs(wdsConnection, "select part_num from pub.partloc where part_num = '" + Utils.replaceSingleQuotesIfNotNull(partnum_comp) + "' and qty_onhand >= (qty_commit + " + quantityCommitting + ")")[0];
	                		if (partAvailAL.size() != 1) {
	            				throw new RuntimeException("Not enough quantity of " + partnum_comp + " is available to build " + quantityToBuild + " pieces of " + part_num);
	                		}

		            		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, "update pub.partloc set qty_commit = qty_commit + " + quantityCommitting + " where part_num = '" + partnum_comp + "'");

		            		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, "insert into pub.KitsForStock (order_num, partnum_assm, partnum_comp, eng_rls_num, qtyCommitted, qtyUsed, buildCompleteDate) values('" + order_num + "', '" + part_num + "', '" + partnum_comp + "', '" + eng_rls_num + "', " + quantityCommitting + ", 0, '')");
						}
	                    log.debug(debugCommittingMessage);

	    		        Message_2015 message_2015 = new Message_2015(wdsConnection, order_num, part_num, wds2015.getQuantityToBuild(), "A");
	    		        String fascorMessageStr = FasUtils.buildFascorMessage(message_2015);
	    	        	log.debug("Message:" + fascorMessageStr);
	    	        	FasUtils.sendMessageToFascor(sqlServerConnection, wdsCommon.getSequenceNumber(), fascorMessageStr);

	                // ---------------------------------------------------------------------------------------------------

	                } else if (wdsCommon.getMessageType().compareTo("3052") == 0) {
	                	WDSFascorRequest_3052 wds3052 = (WDSFascorRequest_3052) wdsCommon;

	                    if (postponeNewRequestAndSetProcessingFlagToT(wdsConnection, sqlServerConnection, wdsCommon) == true) {
			            	wdsCommonPrev = wdsCommon;
			            	continue;
						}	                	
	                	
	                	String partnum_asm = wds3052.getPart_num();
	                	
	                	String eng_rls_num = Message_3052.get_eng_rls_num(wdsConnection, partnum_asm);
	                	
	                	if (wdsCommon.getAction().compareToIgnoreCase("D") == 0) {
	                    	CacheManager.deleteFascorMessages(wdsConnection, sqlServerConnection, "3057", partnum_asm);
	                    	CacheManager.deleteFascorMessages(wdsConnection, sqlServerConnection, wdsCommon.getMessageType(), partnum_asm);
	                    } else {
		                    {
		                    	for (int i = 0; i < erroredNewPartNumbers_ChangedParts.size(); i++) {
									String erroredNewPartNumber_ChangedParts = erroredNewPartNumbers_ChangedParts.get(i);

									if (erroredNewPartNumber_ChangedParts.compareToIgnoreCase(partnum_asm) == 0) {
										String errMsg = "Error. Part name chage: New part no: " + erroredNewPartNumbers_ChangedParts.get(i) + ", old part no: " + erroredOldPartNumbers_ChangedParts.get(i) + ", parent seq no : " + erroredSequenceNumbers_ChangedParts.get(i) + ", child seq no: " + wds3052.getSequenceNumber() + ", affected kit part: " + partnum_asm + ", eng. rls. no: " + eng_rls_num;
			                    		log.debug(errMsg);
			                    		throw new RuntimeException(errMsg);
			                    	}
								}

		                    	for (int i = 0; i < newPartNumbers_ChangedParts.size(); i++) {
									String newPartNumber_ChangedParts = newPartNumbers_ChangedParts.get(i);

									if (newPartNumber_ChangedParts.compareToIgnoreCase(partnum_asm) == 0) {
			                    		log.debug("Part name chage: New part no: " + newPartNumbers_ChangedParts.get(i) + ", old part no: " + oldPartNumbers_ChangedParts.get(i) + ", parent seq no : " + sequenceNumbers_ChangedParts.get(i) + ", child seq no: " + wds3052.getSequenceNumber() + ", affected kit part: " + partnum_asm + ", eng. rls. no: " + eng_rls_num);
			                    	}
								}
		                    }

		                	Message_3052 message_3052 = new Message_3052(wdsConnection, partnum_asm, eng_rls_num, "A");
	                    	CacheManager.sendFascorMessages(wdsConnection, sqlServerConnection, message_3052);

	                    	ArrayList<Message> messageAL = new ArrayList<Message>(10);
		                    ArrayList<HashMap<String, Object>> part_bomAL = null;
		                	part_bomAL = JDBCUtils.runQuery(wdsConnection, "select seq_num, partnum_comp from pub.part_bom where partnum_assm = '" + partnum_asm + "' and eng_rls_num  = '" + eng_rls_num + "' order by seq_num WITH (NOLOCK)");
		                    if ((part_bomAL != null) && (part_bomAL.size() > 0)) {
			                    for (Iterator<HashMap<String, Object>>  iterator2 = part_bomAL.iterator(); iterator2.hasNext();) {
		                            HashMap<String, Object> part_bomHM = iterator2.next();

		                	        Message_3057 message_3057 = new Message_3057(wdsConnection, partnum_asm, eng_rls_num, ((Number) part_bomHM.get("seq_num")).intValue(), "A");
		                            messageAL.add(message_3057);
		                        }
			                    
		                    	CacheManager.sendFascorMessages(wdsConnection, sqlServerConnection, messageAL);
		                    }
	                    }

		            // ---------------------------------------------------------------------------------------------------

	                // qqq
	                } else if (wdsCommon.getMessageType().compareTo("9999") == 0) {

	                	WDSFascorRequest_9999 wds9999 = (WDSFascorRequest_9999) wdsCommon;
	                	
    					String order_num = wds9999.getOrder_num();
	    				int line_num = wds9999.getLine_num();
	    				int qty_voided = wds9999.getQty_voided();
	    				
						ArrayList<HashMap<String, Object>> partAL = null;
						partAL = JDBCUtils.runQuery(wdsConnection, "select part_num from pub.pitem where order_num = '" + wds9999.getOrder_num() + "' and line_num = " + line_num + " WITH (NOLOCk)");		    			
		    			if ((partAL != null) && (partAL.size() == 1)) {
		    				String part_num = (String) partAL.get(0).get("part_num");

		    				log.debug("Processing receipt void for Receiver_Nbr " + wds9999.getReceiver_num() + ", PO_Nbr = " + order_num + ", SKU = " + part_num + ", Line " + line_num + ", Qty Voided: " + qty_voided);
		    				
	    					String receiverWithoutHyphen = wds9999.getReceiver_num();
	    					receiverWithoutHyphen = receiverWithoutHyphen.substring(0, receiverWithoutHyphen.indexOf("-"));

							ArrayList<HashMap<String, Object>> locAL = null;
							locAL = JDBCUtils.runQuery(sqlServerConnection, "select location from [dbo].[UP_Recv_Arc] where Message_Type = '0210' and Receiver_Nbr = '" + receiverWithoutHyphen + "' and PO_Nbr = '" + order_num + "' and SKU = '" + part_num + "' and Qty = " + qty_voided);
			    			if ((locAL != null) && (locAL.size() == 1)) {
			    				String location = (String) locAL.get(0).get("location");

			    				Message_1420 message_1420 = new Message_1420(wdsConnection, sqlServerConnection, part_num, qty_voided * -1, "C", location);
			    				FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20);
			    				FasUtils.sendMessageToFascor(sqlServerConnection, message_1420.getFascorMessage());
			    				
				                JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "update [dbo].[PO_Line] set [Received_Qty] = [Received_Qty] - " + qty_voided + " where PO_Nbr = '" + order_num + "' and PO_Line_Nbr = " + line_num + " and [Received_Qty] >= " + qty_voided);
				                
			    				log.debug("Successfully processed the voidance of this receipt.");				                
			    			} else {
			    				throw new RuntimeException("Fascor location is not found for Receiver_Nbr " + wds9999.getReceiver_num() + ", PO_Nbr = " + order_num + ", SKU = " + part_num + ", Line " + line_num + ", Qty Voided: " + qty_voided);
			    			}
		    			} else {
		    				throw new RuntimeException("Line is not found in pitem table for Receiver_Nbr " + wds9999.getReceiver_num() + ", PO_Nbr = " + order_num + ", Line " + line_num);		    				
		    			}
		    					    
	                }
	                	
		            // ---------------------------------------------------------------------------------------------------

	                // Utils.set_FascorRequest_Processed_Flag(wdsConnection, wdsCommon.getSequenceNumber(), "Y");
	                // JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
	                // JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);

	                if ((JDBCUtils.UPDATE_FASCOR_DB == true) && (JDBCUtils.UPDATE_WDS_DB == true)) {
	                	/*
	        			if ((processedThisManyInThisbatch == 2) || (processedThisManyInThisbatch == 3)) {
	        				throw new RuntimeException("Throwing runtime exception");
	        			}
	        			*/
	        			
	        			FascorProcessor.fascorDidNotProcessInboundMessage = false;	                	
	                	// String errorMessage = checkAndHandleMsgError(wdsConnection, sqlServerConnection, wdsCommon, lastUsedSeqNbrInBound_Before, lastUsedSeqNbrOutBound_Before, lastUsed_error_log_num_Pferrorlog_Before);
	                	String errorMessage = checkAndHandleMsgError(wdsConnection, sqlServerConnection, wdsCommon);

	                	/*
	                	String errorMessage = null;
		                FasUtils.set_FascorRequest_Processed_Flag(wdsConnection, wdsCommon.getSequenceNumber(), "Y");
		                JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
	                	JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);
	                	*/

	                	if (errorMessage == null) {
	    					log.debug("Successfully processed FascorRequest. -1201ZZ- ");
	    					log.debug("\tFascorRequest: " + wdsCommon.toString());
	                	} else {
	                		if (errorMessage.compareToIgnoreCase("FASCOR-DID-NOT-PROCESS") == 0) {
		    					log.error("Fascor didn't process inbound messages.  -1201ZZ- ");
		    					log.error("\tFascorRequest: " + wdsCommon.toString());		    					
	                		} else {
		        				totalNoOfErrorsFound++;
		    					log.debug("Failed to process FascorRequest.  -1201ZZ- ");
		    					log.debug("\tFascorRequest: " + wdsCommon.toString());
	                		}
	                	}
	                } else {
	                    FasUtils.set_FascorRequest_Processed_Flag(wdsConnection, wdsCommon.getSequenceNumber(), "Y");
	                    JDBCUtils.commitWDSChanges_Fascor(wdsConnection);

	                    log.debug("Successfully processed FascorRequest. Update mode was off, and so no changes were done. -1201ZZ- ");
    					log.debug("\tFascorRequest: " + wdsCommon.toString());
	                }

	            	wdsCommonPrev = wdsCommon;
				} else {
					log.debug("Duplicate FascorRequest.  -1201ZZ- . sequenceNumber: " + wdsCommon.getSequenceNumber() + ", prevSequenceNumber: " + wdsCommonPrev.getSequenceNumber());
					log.debug("\tCur  FascorRequest" + wdsCommon.toString());
					log.debug("\tPrev FascorRequest" + wdsCommonPrev.toString());

					FasUtils.set_FascorRequest_Processed_Flag_CommitChanges(wdsConnection, wdsCommon.getSequenceNumber(), "Y");
				}

			} catch (Exception e) {
				totalNoOfErrorsFound++;
				log.debug("Error occurred while processing FascorRequest. -1201ZZ- ");
				log.debug("\tFascorRequest: " + wdsCommon.toString());

				try {
					// log.debug("After: " + System.currentTimeMillis());

					String exceptionMessage = e.getMessage();
					if (e instanceof SQLException) {
						String sqlExceptionInfo = FasUtils.getSQLExceptionInfo((SQLException) e);
						log.debug(sqlExceptionInfo);
						exceptionMessage = sqlExceptionInfo;
					} else {
						log.debug(e);
					}

	                JDBCUtils.rollbackWDSChanges_Fascor(wdsConnection);
	                JDBCUtils.rollbackFascorChanges_Fascor(sqlServerConnection);

	                String msg;

					try {
		    			msg = "Error occurred while processing sequenceNumber: " + curSequenceNumber + ", successfully rolled back changes. Exception message: " + exceptionMessage + ". FascorRequest: " + wdsCommon.toString();
						log.debug(msg, e);
		    			// SubbSendMail galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Problem in FASCOR Batch Process of " + Parms.HOST_NAME, msg);
					} catch (Exception e2) {
						log.debug("Exception.", e2);
					}

	    			if (curSequenceNumber != null) {
						try {
							FasUtils.set_FascorRequest_Processed_Flag_CommitChanges(wdsConnection, curSequenceNumber, "E");
						} catch (SQLException e1) {
							try {
								String sqlExceptionInfo = FasUtils.getSQLExceptionInfo((SQLException) e1);
								log.debug(sqlExceptionInfo);

				    			msg = "Error occurred while setting processed flag to E, sequenceNumber: " + curSequenceNumber + ". Exception message: " + sqlExceptionInfo  + ". FascorRequest: " + wdsCommon.toString();
								log.debug(msg, e1);
								// SubbSendMail galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Problem in FASCOR Batch Process of " + Parms.HOST_NAME, msg);
							} catch (Exception e2) {
								log.debug("Exception.", e2);
							}
						}
					}
				} catch (Exception e2) {
					log.debug("Error occurred, failed to roll back changes.", e);
					try {
						// SubbSendMail galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Problem in FASCOR Batch Process of " + Parms.HOST_NAME, "Error occurred while processing sequenceNumber:" + curSequenceNumber + ", failed to roll back changes. " + e.getMessage());
					} catch (Exception e3) {
						log.debug("", e3);
					}
				}
			}
		}

		log.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

		return totalNoOfErrorsFound;
	}

	static long TOTAL_TIME_TAKEN_BY_FASCOR = 0;
	static long TOTAL_NO_OF_FASCOR_REQUESTS = 0;
	
	public static boolean updateFascorUPCTable(Connection wdsConnection, Connection sqlServerConnection, String part_num) throws SQLException {
		boolean updatedUPCTable = false;
		
		ArrayList<HashMap<String, Object>> partvendAL = Message_1110.get_vend_partnum(wdsConnection, part_num, null);

        for (Iterator<HashMap<String, Object>> iterator2 = partvendAL.iterator(); iterator2.hasNext();) {
			HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator2.next();

			String vendor_num = (String) hashMap.get("vendor_num");
			String vend_partnum = (String) hashMap.get("vend_partnum");

			if ((vendor_num != null) && (vendor_num.compareToIgnoreCase("-NONE-") != 0) && (vend_partnum != null) && (vend_partnum.compareTo("") != 0)) {
				String vend_desc1 = (String) hashMap.get("vend_desc1");
				if (vend_desc1 == null) {
					vend_desc1 = "";
				} else if (vend_desc1.length() > 50) {
					vend_desc1 = vend_desc1.substring(0, 50);
				}

				String vend_desc2 = (String) hashMap.get("vend_desc2");
				if (vend_desc2 == null) {
					vend_desc2 = "";
				} else if (vend_desc2.length() > 50) {
					vend_desc2 = vend_desc2.substring(0, 50);
				}
				
				{
					/*
					ArrayList<HashMap<String, Object>> upcAL = null;
	    			// upcAL = JDBCUtils.runQuery(sqlServerConnection, "select SKU, UPC from [dbo].[UPC] where SKU = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and upc = '" + Utils.replaceSingleQuotesIfNotNull(vend_partnum) + "'");
	    			upcAL = JDBCUtils.runQuery(sqlServerConnection, "select top 1 SKU, UPC from [dbo].[UPC] where upc = '" + Utils.replaceSingleQuotesIfNotNull(vend_partnum) + "' and SKU = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "'");		    			
	    			if ((upcAL == null) || (upcAL.size() == 0)) {
	    				if ((vend_partnum != null) && (vend_partnum.length() > 0) && (vend_partnum.length() <= 15)) {
			                JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "insert into upc (UPC, SKU, UPC_DESC1, UPC_Desc2, Update_User_ID) values('" + Utils.replaceSingleQuotesIfNotNull(vend_partnum) + "', '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "', '" + Utils.replaceSingleQuotesIfNotNull(vend_desc1) + "', '" + Utils.replaceSingleQuotesIfNotNull(vend_desc2) + "', 'WDS_Int')");
	    				}
	    			}
	    			*/
	    			
    				if ((vend_partnum != null) && (vend_partnum.length() > 0) && (vend_partnum.length() <= 15)) {
    					/* UPC load code */
	    				// log.error(vend_partnum + "||" + part_num + "||" + vend_desc1 + "||" + vend_desc2);

    					String sql = "select SKU from [dbo].[SKU_Master] where SKU = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and not exists (select top 1 SKU, UPC from [dbo].[UPC] where upc = '" + Utils.replaceSingleQuotesIfNotNull(vend_partnum) + "' and SKU = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "')";
	    				ArrayList<HashMap<String, Object>> skuAL = null;
		    			skuAL = JDBCUtils.runQuery(sqlServerConnection, sql);		    			
		    			if ((skuAL != null) && (skuAL.size() > 0)) {
		    				JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "insert into upc (UPC, SKU, UPC_DESC1, UPC_Desc2, Update_User_ID) values('" + Utils.replaceSingleQuotesIfNotNull(vend_partnum) + "', '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "', '" + Utils.replaceSingleQuotesIfNotNull(vend_desc1) + "', '" + Utils.replaceSingleQuotesIfNotNull(vend_desc2) + "', 'WDS_Int')");
		    				log.error("Inserted into UPC table, sku: " + part_num + ", UPC: " + vend_partnum);
		    				updatedUPCTable = true;
		    			} else {
		    				log.error("SKU not found for UPC insertion, sku: " + part_num + ", UPC: " + vend_partnum);			    				
		    			}
    				} else {
	    				// log.debug("vend_partnum is not valid, sku: " + part_num + ", vend_partnum: " + vend_partnum);			    				    					
    				}
				}
			}
		}
		
		return updatedUPCTable;
	}
	
	public static String checkAndHandleMsgError(Connection wdsConnection, Connection sqlServerConnection, WDSFascorRequestCommon wdsCommon) throws SQLException {
		log.debug("wdsCommon - Action - ZYXW: " + wdsCommon.getAction());
		
		// if (wdsCommon.getMessageType().compareTo("1110") == 0) {
		if ((wdsCommon.getMessageType().compareTo("1110") == 0  ) &&
			(wdsCommon.getAction().compareToIgnoreCase("D") != 0)    ) {
			WDSFascorRequest_1110 wds1110 = (WDSFascorRequest_1110) wdsCommon;
	    	String part_num = wds1110.getPart_num().toUpperCase();

	    	updateFascorUPCTable(wdsConnection, sqlServerConnection, part_num);
		}	
		
		FasUtils.set_FascorRequest_Processed_Flag(wdsConnection, wdsCommon.getSequenceNumber(), "H");
		
		JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);
        JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
    	
    	return null;
	}
	
	public static String checkAndHandleMsgError_O(Connection wdsConnection, Connection sqlServerConnection, WDSFascorRequestCommon wdsCommon, long lastUsedSeqNbrInBound_Before, long lastUsedSeqNbrOutBound_Before, long lastUsed_error_log_num_Pferrorlog_Before) throws SQLException {
		JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);

        int waitTimeInMillisBetweenChecks = 250;
        int waitTimeInMillisForOneRequest = 3000;
        // int waitTimeInMillisForOneRequest = 200;

        long lastUsedSeqNbrInBound_After = FasUtils.get_IDENT_CURRENT(sqlServerConnection, "InBound");
        
        // if (lastUsedSeqNbrInBound_Before == lastUsedSeqNbrInBound_After) {
        if (true) {
            FasUtils.set_FascorRequest_Processed_Flag(wdsConnection, wdsCommon.getSequenceNumber(), "H");
            JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
        	
        	// log.debug("Nothing new in InBound table, cache must have matched.");
        	log.debug("Skipping waiting on Fascor to process messages.");
        	return null;
        }
        
        long totalWaitTimeInMillis = (((lastUsedSeqNbrInBound_After - lastUsedSeqNbrInBound_Before) <= 5) 							?
        	 						  (lastUsedSeqNbrInBound_After - lastUsedSeqNbrInBound_Before) * waitTimeInMillisForOneRequest  :
        							  10000
        							 );
        totalWaitTimeInMillis = (totalWaitTimeInMillis <= 0)?waitTimeInMillisForOneRequest:totalWaitTimeInMillis;
        
        boolean messagesWereProcessed = false;
    	
    	long fascorProcessCheckBeginTime = System.currentTimeMillis();
    	int noOfChecksAgainstFascor = 0;
    	
        long currentTimeMillis = System.currentTimeMillis();
    	try {Thread.sleep(waitTimeInMillisBetweenChecks);} catch (Exception e) {}

    	while ((System.currentTimeMillis() - currentTimeMillis) < totalWaitTimeInMillis) {
        	noOfChecksAgainstFascor++;

        	ArrayList<HashMap<String, Object>> inBoundMessagesIJustPutInAL = JDBCUtils.runQuery(sqlServerConnection, "select SeqNbr FROM [dbo].[InBound] where SeqNbr > " + lastUsedSeqNbrInBound_Before + " and SeqNbr <= " + lastUsedSeqNbrInBound_After + " and Processed = 'N'");
        	if ((inBoundMessagesIJustPutInAL == null) || (inBoundMessagesIJustPutInAL.size() == 0)) {
        		messagesWereProcessed = true;
        		break;
        	}

        	// waitTimeInMillis = (waitTimeInMillis == 300)?1000:3000;
        	        	
        	try {Thread.sleep(waitTimeInMillisBetweenChecks);} catch (Exception e) {}
        }
    	log.debug("No. of checks done against Fascor to see if Fascor processed or not: " + noOfChecksAgainstFascor);
    	log.debug("Time taken by Fascor to process messages (in Seonds)               : " + (System.currentTimeMillis() - fascorProcessCheckBeginTime) / 1000);
    	
    	TOTAL_NO_OF_FASCOR_REQUESTS++;
    	TOTAL_TIME_TAKEN_BY_FASCOR += (System.currentTimeMillis() - currentTimeMillis);
    	log.debug("Average no. of time taken by Fascor to process messages (in millis): " + (TOTAL_TIME_TAKEN_BY_FASCOR / TOTAL_NO_OF_FASCOR_REQUESTS));    	

        if (messagesWereProcessed == true) {

        	ArrayList<HashMap<String, Object>> pferrorlogAL = JDBCUtils.runQuery(sqlServerConnection, "SELECT data, error_num FROM [dbo].[pferrorlog] where error_log_num > " + lastUsed_error_log_num_Pferrorlog_Before);
        	if ((pferrorlogAL != null) && (pferrorlogAL.size() > 0)) {
				boolean errorFound = false;
				String errorMsgForEmail = "";

        		for (Iterator<HashMap<String, Object>> iterator2 = pferrorlogAL.iterator(); iterator2.hasNext();) {
					HashMap<String, Object> pferrorlogHM = (HashMap<String, Object>) iterator2.next();

					String messageCorrespondingToPferror = ((String) pferrorlogHM.get("data")).trim();
					String error_num = pferrorlogHM.get("error_num") + "";
					error_num = error_num.trim();

					if (error_num.compareTo("11249") == 0) {
						log.debug("Ignoring error 11249.");
						continue;
					}

					String keyFromPferrorlogRecord = MessageAbs.getKeyFromFascorInboundMessage(messageCorrespondingToPferror);

					int index = KEYS_OF_INBOUND_MESSAGES.indexOf(keyFromPferrorlogRecord);
					if (index >= 0) {
						if (errorFound == false) {
			                JDBCUtils.rollbackWDSChanges_Fascor(wdsConnection);

							errorFound = true;
						}

			        	String body_text = FasUtils.getFascorErrorDescription(sqlServerConnection, error_num);
			        	body_text = body_text.replaceAll("'", "''");
			        	if (body_text.length() > 200) {
			        		body_text = body_text.substring(0, 200);
			        	}

			        	errorMsgForEmail += (keyFromPferrorlogRecord + "; error_num: " + error_num + "; Error description: " + body_text);

			        	log.debug("Error occurred. " + errorMsgForEmail);

			        	String insertQueryString =
		        		        "insert into pub.FascorRequestErrors "                                                    +
		        				"(sequenceNumber, three_keys, error_num, body_text) " +
		        				"values ('" + wdsCommon.getSequenceNumber() + "', " +
		        				        "'" + Utils.replaceSingleQuotesIfNotNull(keyFromPferrorlogRecord) + "', " +
		        				        "" + error_num + ", " +
		        				        "'" + Utils.replaceSingleQuotesIfNotNull(body_text) + "')";
		        		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, insertQueryString);
					}
        		}

        		if (errorFound == true) {
	                FasUtils.set_FascorRequest_Processed_Flag(wdsConnection, wdsCommon.getSequenceNumber(), "E");
	                JDBCUtils.commitWDSChanges_Fascor(wdsConnection);

	             // SubbSendMail galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Problem in FASCOR Batch Process of " + Parms.HOST_NAME, "Failed to process sequenceNumber: " + wdsCommon.getSequenceNumber() + ". " + errorMsgForEmail);

					return errorMsgForEmail;
        		}
        	}


        	ArrayList<HashMap<String, Object>> outbound9000MessagesAL = JDBCUtils.runQuery(sqlServerConnection, "select trans, seqnbr, text from [dbo].[OutBound] where processed = 'N' and trans = '9000' and seqnbr > " + lastUsedSeqNbrOutBound_Before);
        	if ((outbound9000MessagesAL != null) && (outbound9000MessagesAL.size() > 0)) {
				boolean errorFound = false;
				String errorMsgForEmail = "";

        		for (Iterator iterator2 = outbound9000MessagesAL.iterator(); iterator2.hasNext();) {
					HashMap<String, Object> outbound9000MessageHM = (HashMap<String, Object>) iterator2.next();

					String trans = ((String) outbound9000MessageHM.get("trans")).trim();
					int seqnbr = ((Number) outbound9000MessageHM.get("seqnbr")).intValue();
					String text = ((String) outbound9000MessageHM.get("text")).trim();

					String message_Type = FasUtils.getOutboundMessageTextSubstring(text, 1, 4).trim();
					String return_Code = FasUtils.getOutboundMessageTextSubstring(text, 5, 5).trim();
					String timestamp = FasUtils.getOutboundMessageTextSubstring(text, 10, 14).trim(); // CCYYMMDDHHMMSS
					String messageCorrespondingTo9000Message = FasUtils.getOutboundMessageTextSubstring(text, 24, text.length() - 23).trim();

					if (return_Code.compareTo("00000") != 0) {
						String keyFromOutbound9000Message = MessageAbs.getKeyFromFascorInboundMessage(messageCorrespondingTo9000Message);

						int index = KEYS_OF_INBOUND_MESSAGES.indexOf(keyFromOutbound9000Message);
						if (index >= 0) {
							if (errorFound == false) {
				                JDBCUtils.rollbackWDSChanges_Fascor(wdsConnection);

								errorFound = true;
							}

				        	int error_num = new Integer(return_Code);

				        	String body_text = FasUtils.getFascorErrorDescription(sqlServerConnection, return_Code);
				        	body_text = body_text.replaceAll("'", "''");
				        	if (body_text.length() > 200) {
				        		body_text = body_text.substring(0, 200);
				        	}

				        	errorMsgForEmail += (keyFromOutbound9000Message + "; error_num: " + error_num + "; Error description: " + body_text);

				        	log.debug("Error occurred. " + errorMsgForEmail);

				        	String insertQueryString =
			        		        "insert into pub.FascorRequestErrors "                                                    +
			        				"(sequenceNumber, three_keys, error_num, body_text) " +
			        				"values ('" + wdsCommon.getSequenceNumber() + "', " +
			        				        "'" + Utils.replaceSingleQuotesIfNotNull(keyFromOutbound9000Message) + "', " +
			        				        "" + error_num + ", " +
			        				        "'" + Utils.replaceSingleQuotesIfNotNull(body_text) + "')";
			        		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, insertQueryString);

			                JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "update [dbo].[OutBound] set processed = 'Y' where processed = 'N' and SeqNbr = '" + seqnbr + "'");
						}
					}
        		}

        		if (errorFound == true) {
	                FasUtils.set_FascorRequest_Processed_Flag(wdsConnection, wdsCommon.getSequenceNumber(), "E");
	                JDBCUtils.commitWDSChanges_Fascor(wdsConnection);

	                JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);

	                // SubbSendMail galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Problem in FASCOR Batch Process of " + Parms.HOST_NAME, "Failed to process sequenceNumber: " + wdsCommon.getSequenceNumber() + ". " + errorMsgForEmail);

					return errorMsgForEmail;
        		}
        	}
        } else {

            // JDBCUtils.rollbackWDSChanges_Fascor(wdsConnection);

        	// Right now these do nothing
        	FascorProcessor.fascorDidNotProcessInboundMessage = true;
        	FascorProcessor.lastTimeAtWhichFascorFailedToProcessInboundTableMessage = System.currentTimeMillis();

            FasUtils.set_FascorRequest_Processed_Flag(wdsConnection, wdsCommon.getSequenceNumber(), "H");
            JDBCUtils.commitWDSChanges_Fascor(wdsConnection);

            // SubbSendMail galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "SERIOUS PROBLEM in FASCOR Batch Process of " + Parms.HOST_NAME, "Fascor didn't process within 10 seconds, seqNo: " + wdsCommon.getSequenceNumber());

           	return "FASCOR-DID-NOT-PROCESS";
        }


		if (wdsCommon.getMessageType().compareTo("1110") == 0) {
			WDSFascorRequest_1110 wds1110 = (WDSFascorRequest_1110) wdsCommon;

	    	String part_num = wds1110.getPart_num().toUpperCase();
			ArrayList<HashMap<String, Object>> partvendAL = Message_1110.get_vend_partnum(wdsConnection, part_num, null);

			/* SELECT [UPC] ,[SKU] ,[UPC_Desc1] ,[UPC_Desc2] ,[Update_Date] ,[Update_User_ID] ,[Update_PID] FROM [dbo].[UPC] */

	        boolean updatesMadeToFascorForUPC = false;

	        for (Iterator iterator2 = partvendAL.iterator(); iterator2.hasNext();) {
				HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator2.next();

				String vendor_num = (String) hashMap.get("vendor_num");
				String vend_partnum = (String) hashMap.get("vend_partnum");

				if ((vendor_num != null) && (vendor_num.compareToIgnoreCase("-NONE-") != 0) && (vend_partnum != null) && (vend_partnum.compareTo("") != 0)) {
					String vend_desc1 = (String) hashMap.get("vend_desc1");
					if (vend_desc1 == null) {
						vend_desc1 = "";
					} else if (vend_desc1.length() > 50) {
						vend_desc1 = vend_desc1.substring(0, 50);
					}

					String vend_desc2 = (String) hashMap.get("vend_desc2");
					if (vend_desc2 == null) {
						vend_desc2 = "";
					} else if (vend_desc2.length() > 50) {
						vend_desc2 = vend_desc2.substring(0, 50);
					}
					
					{

						ArrayList<HashMap<String, Object>> upcAL = null;
		    			upcAL = JDBCUtils.runQuery(sqlServerConnection, "select SKU, UPC from [dbo].[UPC] where SKU = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and upc = '" + Utils.replaceSingleQuotesIfNotNull(vend_partnum) + "'");
		    			if ((upcAL == null) || (upcAL.size() == 0)) {
		    				if ((vend_partnum != null) && (vend_partnum.length() > 0) && (vend_partnum.length() <= 15)) {
				                JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "insert into upc (UPC, SKU, UPC_DESC1, UPC_Desc2, Update_User_ID) values('" + Utils.replaceSingleQuotesIfNotNull(vend_partnum) + "', '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "', '" + Utils.replaceSingleQuotesIfNotNull(vend_desc1) + "', '" + Utils.replaceSingleQuotesIfNotNull(vend_desc2) + "', 'WDS_Int')");
				                updatesMadeToFascorForUPC = true;
		    				}
		    			}
						
						/*
						ArrayList<HashMap<String, Object>> upcAL = null;
		    			upcAL = JDBCUtils.runQuery(sqlServerConnection, "select SKU, UPC from [dbo].[UPC] where SKU = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and upc = '" + Utils.replaceSingleQuotesIfNotNull(vend_partnum) + "'");
		    			if ((upcAL == null) || (upcAL.size() == 0)) {
		    				if ((vend_partnum != null) && (vend_partnum.length() > 0) && (vend_partnum.length() <= 15)) {
				                JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "insert into upc (UPC, SKU, UPC_DESC1, UPC_Desc2, Update_User_ID) values('" + Utils.replaceSingleQuotesIfNotNull(vend_partnum) + "', '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "', '" + Utils.replaceSingleQuotesIfNotNull(vend_desc1) + "', '" + Utils.replaceSingleQuotesIfNotNull(vend_desc2) + "', 'WDS_Int')");
				                updatesMadeToFascorForUPC = true;
		    				} else {
		    					if (vend_partnum != null) {
						            String vend_partnum_without_man_suff;
						            {	int lIndex = vend_partnum.lastIndexOf("-");
						            	if (lIndex > 0) {
						            		vend_partnum_without_man_suff = vend_partnum.substring(0, lIndex).toLowerCase().trim();
						            	} else {
						            		vend_partnum_without_man_suff = vend_partnum;
						            	}
						            }					
				    				if ((vend_partnum_without_man_suff.length() > 0) && (vend_partnum_without_man_suff.length() <= 15)) {
						                JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "insert into upc (UPC, SKU, UPC_DESC1, UPC_Desc2, Update_User_ID) values('" + Utils.replaceSingleQuotesIfNotNull(vend_partnum_without_man_suff) + "', '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "', '" + Utils.replaceSingleQuotesIfNotNull(vend_desc1) + "', '" + Utils.replaceSingleQuotesIfNotNull(vend_desc2) + "', 'WDS_Int')");
						                updatesMadeToFascorForUPC = true;
				    				}
		    					}
		    				}
		    			}
		    			*/
					}
				}
			}

			if (updatesMadeToFascorForUPC == true) {
	        	JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);
	        }

		}

        FasUtils.set_FascorRequest_Processed_Flag(wdsConnection, wdsCommon.getSequenceNumber(), "Y");
        JDBCUtils.commitWDSChanges_Fascor(wdsConnection);

        return null;
	}

	
	// ==================================================================================================
	// ==================================================================================================
	// ==================================================================================================
	
	public static boolean postponeRequestOnceForSomeTime_Old(Connection wdsConnection, Connection sqlServerConnection, WDSFascorRequestCommon wdsCommon, int minutesToPostpone) throws SQLException {
		String didIPostPoneBefore = wdsCommon.getData().substring(28, 29);
		if (didIPostPoneBefore.compareTo("Y") == 0) {
			return false;
		}
		
		String yyyyMmDdHmMmSsSss = FasUtils.convertDateTo_yyyyMmDdHmMmSsSss(FasUtils.addMinutes(new Date(), minutesToPostpone));
		String newSequenceNumber = yyyyMmDdHmMmSsSss + ((int) (Math.random() * 100000));

		log.debug("Postponing request for " + minutesToPostpone + " minutes. " + wdsCommon.getData() + ". Changing sequenceNumber " + wdsCommon.getSequenceNumber() + " to " + newSequenceNumber + " . -1201ZZ- ");
		
		String updateSQL =
		"update pub.FascorRequests set data = '" + wdsCommon.getData().substring(0, 28) + "Y" + wdsCommon.getData().substring(29) + "', " +
		                          "sequenceNumber = '" + newSequenceNumber + "' where sequenceNumber = '" +  wdsCommon.getSequenceNumber() + "'";
		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, updateSQL);
		JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
		
		return true;
	}

	public static void postponeProcessingOfRequestDueToLocking_Old(Connection wdsConnection, Connection sqlServerConnection, WDSFascorRequestCommon wdsCommon) throws SQLException {
		int failedAttempts = 0;
		String failedAttemptsString = wdsCommon.getData().substring(29, 30);
		if ((failedAttemptsString == null) || (failedAttemptsString.compareTo(" ") == 0)) {
			failedAttempts = 0;
		} else {
			failedAttempts = new Integer(failedAttemptsString);
		}

		if (failedAttempts >= 8) {
			throw new RuntimeException("Message send failed 8 times for " + wdsCommon.getData().substring(30) + " due to locking, and so setting the flag to E");
		}
		failedAttempts++;

		String yyyyMmDdHmMmSsSss = FasUtils.convertDateTo_yyyyMmDdHmMmSsSss(FasUtils.addMinutes(new Date(), 5));
		String newSequenceNumber = yyyyMmDdHmMmSsSss + ((int) (Math.random() * 100000));

		log.debug("Postponing request due to locking. failedAttempts: " + failedAttempts + ". " + wdsCommon.getData() +". Changing sequenceNumber " + wdsCommon.getSequenceNumber() + " to " + newSequenceNumber + " . -1201ZZ- ");
		
		JDBCUtils.rollbackWDSChanges_Fascor(wdsConnection);

		String updateSQL =
		"update pub.FascorRequests set data = '" + wdsCommon.getData().substring(0, 29) + failedAttempts + wdsCommon.getData().substring(30) + "', " +
		                          "sequenceNumber = '" + newSequenceNumber + "' where sequenceNumber = '" +  wdsCommon.getSequenceNumber() + "'";
		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, updateSQL);
		JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
		
		JDBCUtils.rollbackFascorChanges_Fascor(sqlServerConnection);
	}
	
}



/*
//2018011217180019521841l
//20180112 17 18 00 195 21841
int secsFromMidNightOfRequest = (3600 * new Integer(sequenceNumber.substring(8, 10))	) +
					  			(60 * new Integer(sequenceNumber.substring(10, 12))		) +
					  			(new Integer(sequenceNumber.substring(12, 14))			);

int curSecsFromMidNight = (int) ((((System.currentTimeMillis() + UTC_OFFSET) % (24 * 3600 * 1000))) / 1000);

if (curSecsFromMidNight < (secsFromMidNightOfRequest + 15)) {
	return;
}
*/
