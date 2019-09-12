package galco.fascor.process;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;
import org.h2.util.IntArray;

import galco.fascor.bin_change.ChangeBin;
import galco.fascor.cache.CacheManager;
import galco.fascor.messages.Message_1110;
import galco.fascor.messages.Message_1230;
import galco.fascor.messages.Message_3052;
import galco.fascor.requests.control.FascorInboundMessageHandler;
import galco.fascor.requests.control.FascorOutboundMessageHandler;
import galco.fascor.requests.control.NightlyBordItemSyncJob;
import galco.fascor.requests.control.POReceiptHandler;
import galco.fascor.requests.control.SKUOnHandQuantityUpdater;
import galco.fascor.utils.FasUtils;
import galco.fascor.wds_requests.WDSFascorRequestCommon;
import galco.portal.config.Parms;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.Contact;

public class ZZZRunFascorQueries {
	private static Logger log = Logger.getLogger(ZZZRunFascorQueries.class);

	static long lastTimeDBConnectionWasMade = Long.MIN_VALUE;

	
	// ------------------------------------------------------------------------------------------

	public static WDSFascorRequestCommon[] createFascorRequests(Connection wdsConnection, String[][] msgTypeORMessageTypeInd_data_array) throws SQLException {
		WDSFascorRequestCommon[] wdsFascorRequestCommonArray = new WDSFascorRequestCommon[msgTypeORMessageTypeInd_data_array.length];
		
		for (int i = 0; i < msgTypeORMessageTypeInd_data_array.length; i++) {
			String messageType;
			String msgTypeORMessageTypeInd = msgTypeORMessageTypeInd_data_array[i][0];
			
			if (msgTypeORMessageTypeInd.compareToIgnoreCase("V") == 0) {
				messageType = "0140C";
			} else if (msgTypeORMessageTypeInd.compareToIgnoreCase("S") == 0) {
				messageType = "1110C";
			} else if (msgTypeORMessageTypeInd.compareToIgnoreCase("P") == 0) {
				messageType = "1210C";
			} else if (msgTypeORMessageTypeInd.compareToIgnoreCase("O") == 0) {
				messageType = "1310C";
			} else if (msgTypeORMessageTypeInd.compareToIgnoreCase("K") == 0) {
				messageType = "3052C";
			} else if (msgTypeORMessageTypeInd.compareToIgnoreCase("KS") == 0) {
				messageType = "2015C";
			} else {
				messageType = msgTypeORMessageTypeInd;
			}

			wdsFascorRequestCommonArray[i] = FasUtils.createFascorRequest(wdsConnection, messageType, msgTypeORMessageTypeInd_data_array[i][1]);
		}
		
        JDBCUtils.commitWDSChanges_Fascor(wdsConnection);

        return wdsFascorRequestCommonArray;
	}
	
	public static String buildCommaSeparatedSequenceNumberString(WDSFascorRequestCommon[] wdsFascorRequestCommonArray) {
		String processSequenceNumbersInThisSet = null;
		for (int i = 0; i < wdsFascorRequestCommonArray.length; i++) {
			if (processSequenceNumbersInThisSet != null) {
				processSequenceNumbersInThisSet += ", ";
			} else {
				processSequenceNumbersInThisSet = "";
			}
			processSequenceNumbersInThisSet += "'" + wdsFascorRequestCommonArray[i].getSequenceNumber() + "'";
		}
		return processSequenceNumbersInThisSet;
	}

	// ------------------------------------------------------------------------------------------
	
    static final SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

    public static void sendASKU_WithoutCheckingAgainstCache(String part_num) throws SQLException, RuntimeException {
		JDBCUtils.UPDATE_FASCOR_DB = true;
	    JDBCUtils.UPDATE_WDS_DB = true;

		Message_1110 message_1110;
		message_1110 = new Message_1110(FascorProcessor.dbConnector8.getConnectionWDS(), part_num, "A");
		FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20);
		FasUtils.sendMessageToFascor(FascorProcessor.sqlServerConnection, message_1110.getFascorMessage());
	
	    JDBCUtils.commitFascorChanges_Fascor(FascorProcessor.sqlServerConnection);
    }

    // =================================================================================================================
    
    public static String[] getUserIDsWithAGivenEmail(Connection wdsConnection, String email) throws SQLException {
    	if ((email == null) || (email.trim().compareTo("") == 0)) {
    		return null;
    	}
    	if (Utils.isEmailAddressValid(email) == false) {
    		return null;
    	}
    	
    	email = email.trim().toLowerCase();
    	
        ArrayList<String> userIDsAL = new ArrayList<String>();

        ArrayList<HashMap<String, Object>> spolicyAL = null;
        spolicyAL = JDBCUtils.runQuery(wdsConnection, "select user_name from pub.spolicy where lower(user_name) = '" + email + "'  WITH (NOLOCK)");
        if ((spolicyAL != null) && (spolicyAL.size() == 1)) {
			for (Iterator<HashMap<String, Object>> iterator = spolicyAL.iterator(); iterator.hasNext();) {
				HashMap<String, Object> po_Line_HM = (HashMap<String, Object>) iterator.next();

				String user_name = (String) po_Line_HM.get("user_name");
				userIDsAL.add(user_name);
			}
			
            return userIDsAL.toArray(new String[0]);			
        }
        
        spolicyAL = JDBCUtils.runQuery(wdsConnection, 
						        	   "select user_name " 													+
						        	   "  from pub.contact as c, pub.spolicy as s " 						+
						        	   " where c.e_mail_address = '" + email + "' " 						+
						        	   "   and c.pros_cd = s.cust_num " 									+
						               "   and c.cont_no = s.cont_no " 										+
						               "WITH (NOLOCK)"
        							  );
        if ((spolicyAL != null) && (spolicyAL.size() == 1)) {
			for (Iterator<HashMap<String, Object>> iterator = spolicyAL.iterator(); iterator.hasNext();) {
				HashMap<String, Object> po_Line_HM = (HashMap<String, Object>) iterator.next();

				String user_name = (String) po_Line_HM.get("user_name");
				userIDsAL.add(user_name.toLowerCase());
			}
			
            return userIDsAL.toArray(new String[0]);			
        } else {
            if ((spolicyAL != null) && (spolicyAL.size() > 1)) {
	        	log.debug("Multiple spolicy records matched for password reset request - ");
	        	
	        	for (Iterator<HashMap<String, Object>> iterator = spolicyAL.iterator(); iterator.hasNext();) {
					HashMap<String, Object> po_Line_HM = (HashMap<String, Object>) iterator.next();
	
					String user_name = (String) po_Line_HM.get("user_name");
					log.debug(user_name);
				}
            }

        	return null;
        }
    }
    
    // =================================================================================================================
    
	public static void main(String[] args) {
		if (FascorProcessor.getDBConnections() == true) {
			Connection wdsConnection = FascorProcessor.dbConnector8.getConnectionWDS();

			int OPTION = 1;
			
			Object[] possibilities = {"0 - FascorRequests with processed = N", "1 - update FascorRequests", "2 - Query FascorMessageCache", "3 - FascorRequestErrors"};
			// OPTION = JOptionPane.showOptionDialog(null, "Choose one","FascorQueriesExecutor", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, possibilities, possibilities[0]);
			OPTION = 0;
			
			// String selection = (String) JOptionPane.showInputDialog(null,"Choose","Choose option", JOptionPane.PLAIN_MESSAGE, null, possibilities, "1 - FascorRequests with processed = N");
			// OPTION = Integer.parseInt(selection.substring(0, 1));
			
	        try {
	        	if (OPTION == 0) {
	        		

	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	    			JDBCUtils.UPDATE_FASCOR_DB = true;
	        		NightlyBordItemSyncJob.synchInventoryForBordItems(wdsConnection, FascorProcessor.sqlServerConnection);
	        		/*
	        		*/
        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		/*
	        		
	        		String po_nbr = "abcd";
	        		String sku = "PC1300-DURA";

	        		
	        		
                	boolean rfReceiptIsOff = false;
                	int rfReciptIsOffBy = 0;
                    ArrayList<HashMap<String, Object>> po_LineZAL = null;
                    
                    
                    
                    
                    // po_LineZAL = JDBCUtils.runQuery(sqlServerConnection, "select Ordered_Qty, Received_Qty, cast(po_line_nbr as int) as po_line_nbr from PO_Line where PO_Nbr = '" + po_nbr + "' and SKU = '" + Utils.replaceSingleQuotesIfNotNull(sku) + "' order by po_line_nbr");
                    po_LineZAL = new ArrayList<HashMap<String,Object>>(100);                    
                    
                    HashMap<String, Object> hashMapZ = new HashMap<String, Object>();
                    hashMapZ.put("po_line_nbr",  (Number) 6);
                    hashMapZ.put("Ordered_Qty",  (Number) 1);
                    hashMapZ.put("Received_Qty",  (Number) 1);
                    po_LineZAL.add(hashMapZ);
                    
                    hashMapZ = new HashMap<String, Object>();
                    hashMapZ.put("po_line_nbr",  (Number) 7);
                    hashMapZ.put("Ordered_Qty",  (Number) 1);
                    hashMapZ.put("Received_Qty",  (Number) 1);
                    po_LineZAL.add(hashMapZ);

                    hashMapZ = new HashMap<String, Object>();
                    hashMapZ.put("po_line_nbr",  (Number) 8);
                    hashMapZ.put("Ordered_Qty",  (Number) 2);
                    hashMapZ.put("Received_Qty",  (Number) 0);
                    po_LineZAL.add(hashMapZ);

                    hashMapZ = new HashMap<String, Object>();
                    hashMapZ.put("po_line_nbr",  (Number) 9);
                    hashMapZ.put("Ordered_Qty",  (Number) 1);
                    hashMapZ.put("Received_Qty",  (Number) 0);
                    po_LineZAL.add(hashMapZ);

                    hashMapZ = new HashMap<String, Object>();
                    hashMapZ.put("po_line_nbr",  (Number) 10);
                    hashMapZ.put("Ordered_Qty",  (Number) 1);
                    hashMapZ.put("Received_Qty",  (Number) 1);
                    po_LineZAL.add(hashMapZ);

                    
                    
                    
                    if ((po_LineZAL != null) && (po_LineZAL.size() > 1)) {
                    	for (Iterator iterator3 = po_LineZAL.iterator(); iterator3.hasNext();) {
							HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator3.next();
							
    						int Ordered_Qty = ((Number) hashMap.get("Ordered_Qty")).intValue();
    						int Received_Qty = ((Number) hashMap.get("Received_Qty")).intValue();
    						int po_line_nbr = ((Number) hashMap.get("po_line_nbr")).intValue();

                            if (Received_Qty > Ordered_Qty) {
                            	rfReceiptIsOff = true;
                            	rfReciptIsOffBy += (Received_Qty - Ordered_Qty);
                            }
						}
                    }
                    if (rfReceiptIsOff == true) {
                    	for (Iterator iterator3 = po_LineZAL.iterator(); iterator3.hasNext();) {
							HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator3.next();
							
    						int Ordered_Qty = ((Number) hashMap.get("Ordered_Qty")).intValue();
    						int Received_Qty = ((Number) hashMap.get("Received_Qty")).intValue();
    						int po_line_nbr = ((Number) hashMap.get("po_line_nbr")).intValue();

                            if (Received_Qty > Ordered_Qty) {
                            	log.debug("Mismatched RF quantities - update PO_Line set Received_Qty = " + Ordered_Qty + " where where PO_Nbr = '" + po_nbr + "' and PO_Line_Nbr = '" + po_line_nbr + "'");
                            	// JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "update PO_Line set Received_Qty = " + Ordered_Qty + " where where PO_Nbr = '" + po_nbr + "' and PO_Line_Nbr = '" + po_line_nbr + "'");
                            } else {
                            	if (rfReciptIsOffBy > 0) {
                            		if (Ordered_Qty > Received_Qty) {
                            			int adjustment = (rfReciptIsOffBy > (Ordered_Qty - Received_Qty))?(Ordered_Qty - Received_Qty):rfReciptIsOffBy;
                            			log.debug("Mismatched RF quantities - update PO_Line set Received_Qty = " + (Received_Qty + adjustment) + " where where PO_Nbr = '" + po_nbr + "' and PO_Line_Nbr = '" + po_line_nbr + "'");
                                    	// JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "update PO_Line set Received_Qty = " + (Received_Qty + adjustment) + " where where PO_Nbr = '" + po_nbr + "' and PO_Line_Nbr = '" + po_line_nbr + "'");
                                    	rfReciptIsOffBy -= adjustment;
                            		}
                            	}
                            }
						}
                    	
                    	if (rfReciptIsOffBy > 0) {
							throw new RuntimeException("For the PO " + po_nbr + ", and for the SKU " + sku + ", RF mismatched quantities didn't add up.");			    									                    		
                    	}
                    	
    	                // JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);
                    	
                    }
                    
                    */
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    

                    
                    
	        		
	        		
	        		/*
	        		
	        		String sqlStatement = "select loc_id, sku, actual_qty from Loc_Allocation where Loc_ID like 'BORD%' order by sku";
	        		ArrayList<HashMap<String, Object>> al = JDBCUtils.runQuery(FascorProcessor.sqlServerConnection, sqlStatement);
	        		// FasUtils.printArrayListOfHashMaps(al);
	        		
	        		String prevSku = "ZZZZZZZZZZZZZZZZZZZZZZZZZZ";
                    int actual_qty = 0;	        		
	        		
	            	for (Iterator<HashMap<String, Object>> iterator = al.iterator(); iterator.hasNext();) {
	            		HashMap<String, Object> loacAlHashMap = (HashMap<String, Object>) iterator.next();
	            		
                        String loc_id = (String) loacAlHashMap.get("loc_id");
                        String sku = ((String) loacAlHashMap.get("sku")).trim();

                        if (prevSku.compareToIgnoreCase("ZZZZZZZZZZZZZZZZZZZZZZZZZZ") == 0) {
    		                prevSku = sku;                        	
                            actual_qty = ((Number) loacAlHashMap.get("actual_qty")).intValue();                        	
                        	continue;                        	
                        }

                        if (sku.compareToIgnoreCase(prevSku) == 0) {
                            actual_qty += ((Number) loacAlHashMap.get("actual_qty")).intValue();                        	
                        	continue;
                        } 

	            		ArrayList<HashMap<String, Object>> partlocAL = null;
		                try {
		                	partlocAL = JDBCUtils.runQuery(wdsConnection, "select qty_onhand from pub.partloc where part_num = '" + prevSku + "' and qty_onhand <> " + actual_qty);
		                	if ((partlocAL != null) && (partlocAL.size() == 1)) {
		                        int qty_onhand = ((Number) (partlocAL.get(0)).get("qty_onhand")).intValue();

		    					// SKUOnHandQuantityUpdater.buildAndSend1420Message(wdsConnection, FascorProcessor.sqlServerConnection, prevSku, qty_onhand - actual_qty);

		                        log.debug("sku: " + prevSku + ", loc_id: " + loc_id + ", actual_qty: " + actual_qty + ", qty_onhand: " + qty_onhand + ", correction: " + (qty_onhand - actual_qty));
		                	} else {
		                        // log.debug("sku: " + prevSku + " not found in WDS.");		                		
		                	}
		        		} catch (SQLException e1) {
		        			log.debug("Exception: " + e1.getMessage(), e1);
		        		}
		                
		                prevSku = sku;
                        actual_qty = ((Number) loacAlHashMap.get("actual_qty")).intValue();
	            	}

	        		
            		ArrayList<HashMap<String, Object>> partlocAL = null;
	                try {
	                	partlocAL = JDBCUtils.runQuery(wdsConnection, "select qty_onhand from pub.partloc where part_num = '" + prevSku + "' and qty_onhand <> " + actual_qty);
	                	if ((partlocAL != null) && (partlocAL.size() == 1)) {
	                        int qty_onhand = ((Number) (partlocAL.get(0)).get("qty_onhand")).intValue();

	    					// SKUOnHandQuantityUpdater.buildAndSend1420Message(wdsConnection, FascorProcessor.sqlServerConnection, prevSku, qty_onhand - actual_qty);

	                        log.debug("sku: " + prevSku + ", actual_qty: " + actual_qty + ", qty_onhand: " + qty_onhand + ", correction: " + (qty_onhand - actual_qty));
	                	} else {
	                        // log.debug("sku: " + prevSku + " not found in WDS.");		                		
	                	}
	        		} catch (SQLException e1) {
	        			log.debug("Exception: " + e1.getMessage(), e1);
	        		}
	                
	                */
	        		
	        		
	        		
	        		
	        		
	        		/*

	        		ArrayList<HashMap<String, Object>> partlocAL = null;
	                try {
	                	// DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER 
	                	// DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER 
	                	// DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER 
	                	// partlocAL = JDBCUtils.runQuery(wdsConnection, "select part_num, normal_binnum, overstk_binnum from pub.partloc where (qty_onhand = 0) and (normal_binnum like 'BORD%' or overstk_binnum like 'BORD%') WITH (NOLOCK)");
	                	partlocAL = JDBCUtils.runQuery(wdsConnection, "select part_num, normal_binnum, overstk_binnum from pub.partloc where (qty_onhand = 0) and (normal_binnum like 'BORD%' or overstk_binnum like 'BORD%') WITH (NOLOCK)");
	        		} catch (SQLException e1) {
	        			log.debug("Exception occurred while nightly updating bins." + e1.getMessage(), e1);
	        		}
	                if ((partlocAL != null) && (partlocAL.size() > 0)) {
	                    for (Iterator<HashMap<String, Object>>  iterator2 = partlocAL.iterator(); iterator2.hasNext();) {
	                        HashMap<String, Object> partlocHM = iterator2.next();
	                        
	                        String part_num = (String) partlocHM.get("part_num");
	                        String old_normal_binnum = (String) partlocHM.get("normal_binnum");
	                        String old_overstk_binnum = (String) partlocHM.get("overstk_binnum");

	                        old_normal_binnum = (old_normal_binnum == null)?"":old_normal_binnum.trim();
	                        old_overstk_binnum = (old_overstk_binnum == null)?"":old_overstk_binnum.trim();
	                        
	                        String new_normal_binnum = "";
	                        String new_overstk_binnum = "";

	                        if ((old_normal_binnum.length() >= 4									) && 
	                        	(old_normal_binnum.substring(0,4).compareToIgnoreCase("BORD") == 0	)    ) {
	                        	new_normal_binnum = "";
	                        } else {
	                        	new_normal_binnum = old_normal_binnum;
	                        }

	                        if ((old_overstk_binnum.length() >= 4									) && 
	                        	(old_overstk_binnum.substring(0,4).compareToIgnoreCase("BORD") == 0	)    ) {
	                        	new_overstk_binnum = "";
	                        } else {
	                        	new_overstk_binnum = old_overstk_binnum;
	                        }
	                        
	                        try {
		                        // ChangeBin.changeSingleBin(wdsConnection, FascorProcessor.dbConnector8.getConnectionSRO(), part_num, new_normal_binnum, new_overstk_binnum, old_normal_binnum, old_overstk_binnum);
							} catch (Exception e) {
								log.debug(e);
							}
	                    }
	                }

	        		*/
	        		
	                
	                


	        		/*
					JDBCUtils.UPDATE_FASCOR_DB = true;
				    JDBCUtils.UPDATE_WDS_DB = true;	
	                ChangeBin.changeSingleBin(wdsConnection, FascorProcessor.dbConnector8.getConnectionSRO(), "PC1300-DURA","BORD123456","BORD654321","Z7Z6Z","");
	        		*/
	        		
	                        
	                        
	                /*
	        		
	        		String receiver_nbr = "249", po_nbr = "1857692";
	        		String outputFileName = "/home/sva0604/ztest.txt";
	        		
					ArrayList<String>[] partNorOverALArray = FascorOutboundMessageHandler.getWDSBinLocaionChanges(wdsConnection, FascorProcessor.sqlServerConnection, receiver_nbr, po_nbr);


					if (partNorOverALArray != null) {
						for (int i = 0; i < partNorOverALArray[0].size(); i++) {
							log.debug(partNorOverALArray[0].get(i) + "," + partNorOverALArray[1].get(i) + "," + partNorOverALArray[2].get(i) + "," + partNorOverALArray[3].get(i) + "," + partNorOverALArray[4].get(i));
						}
					}
					
					JDBCUtils.UPDATE_FASCOR_DB = true;
				    JDBCUtils.UPDATE_WDS_DB = true;					
					ChangeBin.changeBins(wdsConnection, FascorProcessor.dbConnector8.getConnectionSRO(), partNorOverALArray);				

					Thread.sleep(5000);
					ChangeBin.processFailedUpdate(wdsConnection);
					Thread.sleep(5000);
					ChangeBin.processFailedUpdate(wdsConnection);
					Thread.sleep(5000);
					ChangeBin.processFailedUpdate(wdsConnection);
					*/
	                        
	                        
	        		
	        		/*
	        		String receiver_nbr = "285", po_nbr = "1861422";
	        		String outputFileName = "/home/sva0604/ztest.txt";
	        		
					ArrayList<String>[] partNorOverALArray = FascorOutboundMessageHandler.getWDSBinLocaionChanges(wdsConnection, FascorProcessor.sqlServerConnection, receiver_nbr, po_nbr);

					String dataToWDS = po_nbr + "," + receiver_nbr + "," + outputFileName + ",";
					if (partNorOverALArray != null) {
						for (int i = 0; i < partNorOverALArray[0].size(); i++) {
							dataToWDS = dataToWDS + partNorOverALArray[0].get(i) + "," + partNorOverALArray[1].get(i) + "," + partNorOverALArray[2].get(i) + ",";
						}
					}
					log.debug("dataToWDS: " + dataToWDS);
	        		*/
	        		
	        		/*
	        		String sqlStatement = "select * from vendor";
	        		ArrayList<HashMap<String, Object>> al = JDBCUtils.runQuery(FascorProcessor.sqlServerConnection, sqlStatement);
	        		FasUtils.printArrayListOfHashMaps(al);
	        		*/
	        		
	        		
	        		
	        		
	        		
	        		/*

	        		String pocomment = "";
	        		{
	        			ArrayList<HashMap<String, Object>> pocommentAL = null;
	        			pocommentAL = JDBCUtils.runQuery(wdsConnection, "select comment_text[1], comment_text[2], comment_text[3], comment_text[4], comment_text[5], comment_text[6], comment_text[7], comment_text[8], comment_text[9] from pub.pcomment where order_num = '1857686' and line_num = 0 and prt_on_rcvr = '1' order by comment_num WITH (NOLOCK)");
	        	    	if ((pocommentAL != null) && (pocommentAL.size() > 0)) {
	        				for (Iterator<HashMap<String, Object>> iterator = pocommentAL.iterator(); iterator.hasNext();) {
	        					HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();
	        					
	        					for (int i = 1; i <= 9; i++) {
	        						String line = (String) hashMap.get("comment_text[" + i + "]");
	        						if (line != null) {
	        							line = line.trim();
	        							if (line.length() > 0) {
	        								pocomment += (line + " ");
	        							}
	        						}
	        					}
	        				}
	        			}			
	        		}	        		
	        		
	        		System.out.println(pocomment);
	        		
	        		*/
	        		
	        		
	        		/*
                    boolean poIsClosed = false;
        			String queryString = "select po.date_closed from pub.FascorMessageCache as c, pub.porder as po " +
							 " where c.messageType = '1210' and c.key1 = '1857687' and po.order_num = c.key1 and po.date_closed  is not null WITH (NOLOCK)";
        			
		
					ArrayList<HashMap<String, Object>> closedDate_AL = null;
					closedDate_AL = JDBCUtils.runQuery(wdsConnection, queryString);
					if ((closedDate_AL != null) && (closedDate_AL.size() > 0)) {
						poIsClosed = true;
					}
					*/
					
       				/*
	    			galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com,kmesser@galco.com", "WDSFascorIntegration@galco.com", "Test mail", "A test email, please acknowledge, thanks.");
	    			*/

	        		/*
					FasUtils.trackTimeS();
					for (int i = 0; i < 30; i++) {
		    			// String queryString = "select part_num from pub.part where part_num = 'PC1300-DURA'";
		    			String queryString = "select key2, fascorMessage from pub.FascorMessageCache " +
								 " where messageType = '0140' and key1 = '0025'" +
								 " order by key2";
		    			ArrayList<String>[] arrayLists = JDBCUtils.runQuery_ReturnStrALs(wdsConnection, queryString);						
					}
					System.out.println("ZYX - time took " + FasUtils.trackTimeE());
					*/
		
	        		/*
	        		String po_nbr = "1837357", sku = "12X8X4-GALC";
	        		int qty = 2700;
	        		
	        		
	        		
                    ArrayList<HashMap<String, Object>> pitem2AL = null;
                    pitem2AL = JDBCUtils.runQuery(wdsConnection, "select order_num, line_num, part_num, qty_ord, qty_received, qty_cancel from pub.pitem where order_num = '" + po_nbr + "' and part_num = '" + Utils.replaceSingleQuotesIfNotNull(sku) + "' order by line_num WITH (NOLOCK)");
                    if ((pitem2AL != null) && (pitem2AL.size() > 0)) {
	                    ArrayList<HashMap<String, Object>> po_LineAL = null;
	                    po_LineAL = JDBCUtils.runQuery(FascorProcessor.sqlServerConnection, "select Ordered_Qty, Received_Qty, cast(po_line_nbr as int) as po_line_nbr from PO_Line where PO_Nbr = '" + po_nbr + "' and SKU = '" + Utils.replaceSingleQuotesIfNotNull(sku) + "' order by po_line_nbr");
	                    if ((po_LineAL != null) && (po_LineAL.size() > 0)) {
	                    	HashMap<Integer, HashMap<String, Object>> pitem2ByLineNoHM = new HashMap<Integer, HashMap<String, Object>>(po_LineAL.size());
	    					for (Iterator<HashMap<String, Object>> iterator4 = pitem2AL.iterator(); iterator4.hasNext();) {
	    						HashMap<String, Object> pitem_HM = (HashMap<String, Object>) iterator4.next();
	    						pitem2ByLineNoHM.put(((Number) pitem_HM.get("line_num")).intValue(), pitem_HM);
	    					}
	                    	
	                    	int totalQuantityPostedInThisPO = 0;
	    					for (Iterator<HashMap<String, Object>> iterator3 = po_LineAL.iterator(); iterator3.hasNext();) {
	    						HashMap<String, Object> po_Line_HM = (HashMap<String, Object>) iterator3.next();

	    						int Ordered_Qty = ((Number) po_Line_HM.get("Ordered_Qty")).intValue();
	    						int Received_Qty = ((Number) po_Line_HM.get("Received_Qty")).intValue();
	    						int po_line_nbr = ((Number) po_Line_HM.get("po_line_nbr")).intValue();

	    						HashMap<String, Object> pitem_HM = (HashMap<String, Object>) pitem2ByLineNoHM.get(po_line_nbr);
	    						if (pitem_HM == null) {
	    							throw new RuntimeException("For the PO " + po_nbr + ", and for the SKU " + sku + ", line_num " + po_line_nbr + " doesn't exist in WDS.");
	    						}
	    						int qty_received = ((Number) pitem_HM.get("qty_received")).intValue();
	    						
	    						if (Received_Qty > qty_received) {
	    							totalQuantityPostedInThisPO += (Received_Qty - qty_received);
	    						}
	    					}
	    					
	    					if (totalQuantityPostedInThisPO != qty) {
    							throw new RuntimeException("For the PO " + po_nbr + ", and for the SKU " + sku + ", quantity to be posted, which is " + qty + ", doesn't match calculated quantity, which is " + totalQuantityPostedInThisPO);			    						
	    					}
	                    	
	    					for (Iterator<HashMap<String, Object>> iterator3 = po_LineAL.iterator(); iterator3.hasNext();) {
	    						HashMap<String, Object> po_Line_HM = (HashMap<String, Object>) iterator3.next();

	    						int Ordered_Qty = ((Number) po_Line_HM.get("Ordered_Qty")).intValue();
	    						int Received_Qty = ((Number) po_Line_HM.get("Received_Qty")).intValue();
	    						int po_line_nbr = ((Number) po_Line_HM.get("po_line_nbr")).intValue();

	    						HashMap<String, Object> pitem_HM = (HashMap<String, Object>) pitem2ByLineNoHM.get(po_line_nbr);
	    						int qty_received = ((Number) pitem_HM.get("qty_received")).intValue();
	    						
	    						if (Received_Qty > qty_received) {
	    							System.out.println("Building PI_RCPT with Line " + po_line_nbr + ", Quantity: " + (Received_Qty - qty_received));
		    						// POReceiptHandler.build_PI_RCPT_Record(wdsConnection, po_nbr, po_line_nbr, (Received_Qty - qty_received));			    							
	    						}
	    					}
	                    } else {
	                    	throw new RuntimeException("For the PO " + po_nbr + ", and for the SKU " + sku + ", there are no lines Fascor.");
	                    }
                    } else {
                    	throw new RuntimeException("For the PO " + po_nbr + ", and for the SKU " + sku + ", there are no lines in WDS.");
                    }
	        		*/                    
                    
                    

                    /*
	        		System.out.println("Before " + System.currentTimeMillis());
	        		String sqlStatement = "select part_num from pub.part where part_num = 'PC1300-DURA' WITH (NOLOCK)";
	        		ArrayList<HashMap<String, Object>> al = JDBCUtils.runQuery(wdsConnection, sqlStatement);
	        		FasUtils.printArrayListOfHashMaps(al);
	        		System.out.println("After  " + System.currentTimeMillis());
	        		*/

	        		/*
					sendASKU_WithoutCheckingAgainstCache("200486402-CLCI");
	        		*/
	        		
	        		
					// SKUOnHandQuantityUpdater.buildAndSend1420Message(wdsConnection, FascorProcessor.sqlServerConnection, "G07C0000-DEMO-RDL", 1);
	        		
						        		/*
					                    ArrayList<HashMap<String, Object>> pcommentAL = null;
					                    pcommentAL = JDBCUtils.runQuery(wdsConnection, "select distinct pc.line_num from pub.pcomment pc, pub.pitem pi where pc.order_num = '1844000' and pc.prt_on_rcvr = '1' and pc.order_num = pi.order_num and pc.line_num = pi.line_num and (pi.qty_ord - pi.qty_received - pi.qty_cancel) > 0 and pi.drop_ship = '0' order by pc.line_num WITH (NOLOCK)");
					                    if ((pcommentAL != null) && (pcommentAL.size() > 0)) {
							        		FasUtils.printArrayListOfHashMaps(pcommentAL);
					                    }
					                    */
	        		
						        		/*
						        		// String sqlStatement = "select * from pub.FascorRequests where processed = 'E' order by functionCode, sequenceNumber";
						        		String sqlStatement = "select * from pub.FascorRequests where processed = 'N' order by functionCode, sequenceNumber WITH (READPAST NOWAIT)";
						        		// String sqlStatement = "select part_num from pub.part where part_num = 'KENKRUZ-ABBG' WITH (READPAST NOWAIT)";
						        		ArrayList<HashMap<String, Object>> al = JDBCUtils.runQuery(wdsConnection, sqlStatement);
						        		FasUtils.printArrayListOfHashMaps(al);
						        		
						        		// System.out.println("***" + Message_3052.get_eng_rls_num(wdsConnection, "05P06-00004-NBR") + "***");
						        		*/

	        		/*
	        		// northstarservice@earthlink.net      ---- debbie@northstarservice.net 
	        	    String[] userIDs = getUserIDsWithAGivenEmail(wdsConnection, "arpad.koroskenyi@invitel.hu");
	        	    if (userIDs != null) {
	        	    	for (int i = 0; i < userIDs.length; i++) {
	        	    		System.out.println("Matched spolicy user_names:");							
	        	    		System.out.println(userIDs[i]);							
						}
	        	    }
	        	    */
	        		
	        		/*
	        		
		        	ArrayList<HashMap<String, Object>> partlocAL = null;
		        	try {
			        	String qtyGTZeroSQL = "select part_num, qty_onhand from pub.partloc where qty_onhand > 0 order by part_num";
						partlocAL = JDBCUtils.runQuery(wdsConnection, qtyGTZeroSQL);
						
			            for (Iterator<HashMap<String, Object>> iterator  = partlocAL.iterator(); iterator.hasNext();) {
							HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();

							String part_num = ((String) hashMap.get("part_num")).trim();
							int qty_onhand = ((Number) hashMap.get("qty_onhand")).intValue();
							
				    		String normal_binnum = "";

							String fascorBinNum = "";
							ArrayList<String> partloc2AL = JDBCUtils.runQuery_ReturnStrALs(wdsConnection, "select normal_binnum from pub.partloc where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "'")[0];
					    	if ((partloc2AL == null) || (partloc2AL.size() == 0)) {
					    		fascorBinNum = "FIX0000000";
					    	} else {
					    		normal_binnum = partloc2AL.get(0);

					    		if ((normal_binnum != null) && (normal_binnum.compareTo("") != 0)) {
							    	ArrayList<String> wdsToFCLocMapAL = JDBCUtils.runQuery_ReturnStrALs(FascorProcessor.sqlServerConnection, "select FASCOR from WDSToFCLocMap where WDS = '" + Utils.replaceSingleQuotesIfNotNull(normal_binnum) + "'")[0];
							    	if ((wdsToFCLocMapAL == null) || (wdsToFCLocMapAL.size() != 1)) {
							    		fascorBinNum = "FIX0000000";
							    	} else {
							    		fascorBinNum = wdsToFCLocMapAL.get(0);
							    	}
					    		} else {
						    		fascorBinNum = "FIX0000000";
					    		}
					    	}			
					    	
					    	if (fascorBinNum.compareTo("FIX0000000") == 0) {
					    		System.out.println(part_num + " " + normal_binnum);
					    	}
			            }
					} catch (SQLException e) {
						e.printStackTrace();
					}

		        	*/
	        		
	        	} else if (OPTION == 1) {
	    		    JDBCUtils.UPDATE_WDS_DB = true;
	    		    
	    		    /*
		    		// String sqlStatement = "update pub.part set description = 'Battery, Subb - 11/20 09:35' where part_num = 'pc1300-dura'";
	    		    
		    		String sqlStatement = "update pub.FascorRequests set processed = 'Y' where processed = 'N'";
								    		// String sqlStatement = "update pub.FascorRequests set processed = 'Y' where sequenceNumber in " +
						                    //        "('201811081520361')";
								    		// String sqlStatement = "delete from pub.FascorRequests";
					
	    		    JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStatement);
					
					JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
					*/
	        	} else if (OPTION == 2) {
	        		String sqlStatement = "select messageType, key1, key2, fascorMessage from pub.FascorMessageCache where messageType = '1210' and key1 = '1844228'";
	        		// String sqlStatement = "select messageType, key1, key2, fascorMessage from pub.FascorMessageCache where messageType = '3052' and key1 = '410403-24AWL-NBR'";
	        		ArrayList<HashMap<String, Object>> al = JDBCUtils.runQuery(wdsConnection, sqlStatement);
	        		FasUtils.printArrayListOfHashMaps(al);
	        		/*
	        		*/
	        		
	        		/*
	    		    JDBCUtils.UPDATE_WDS_DB = true;
					JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, "delete from pub.FascorMessageCache where  messageType = '1220' and key1 = '1844228'");
					JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
	        		*/
	        	} else if (OPTION == 3) {
	        		String sqlStatement = "select * from pub.FascorRequestErrors where three_keys = '212''60.06-RELI'";
	        		
	        		ArrayList<HashMap<String, Object>> al = JDBCUtils.runQuery(wdsConnection, sqlStatement);

	        		FasUtils.printArrayListOfHashMaps(al);
	        	} else {
	        		
	        	}

	        	FascorProcessor.closeDBConnections();
	        } catch (SQLException e ) {
		    	// handleDBException(e, "Exception-1.");
	        	e.printStackTrace();
	        	FascorProcessor.closeDBConnections();

	        	System.exit(0);
        	} catch (Exception e ) {
		    	// handleDBException(e, "Exception-2.");
	        	e.printStackTrace();
	        	FascorProcessor.closeDBConnections();
	        	
	        	System.exit(0);
	    	}
		}
	}
		
}

