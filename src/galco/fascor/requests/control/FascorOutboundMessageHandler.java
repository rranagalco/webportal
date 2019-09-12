package galco.fascor.requests.control;

import galco.fascor.bin_change.ChangeBin;
import galco.fascor.messages.MessageAbs;
import galco.fascor.process.FascorProcessor;
import galco.fascor.utils.FasUtils;
import galco.fascor.wds_requests.WDSFascorRequestCommon;
import galco.portal.config.Parms;
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

public class FascorOutboundMessageHandler {
	private static Logger log = Logger.getLogger(FascorOutboundMessageHandler.class);

	public static String[] getGLAccountNumberGivenReasonCode(String reasonCode) {
		String fas_glje_o_acct = "40110",
			   fas_glje_o_div = "PLGA",
			   fas_glje_o_dept = "PA00",
		   	   reasonCodeDescription = "";

		if (reasonCode.compareTo("35") == 0) {
		    fas_glje_o_acct = "40110";
		    fas_glje_o_div = "PLGA";
		    fas_glje_o_dept = "PA00";
		    reasonCodeDescription = "Shipping Error";
		} else if (reasonCode.compareTo("34") == 0) {
		    fas_glje_o_acct = "40110";
		    fas_glje_o_div = "PLGA";
		    fas_glje_o_dept = "PA00";
		    reasonCodeDescription = "Parts for Galco West";
		} else if (reasonCode.compareTo("32") == 0) {
		    fas_glje_o_acct = "40110";
		    fas_glje_o_div = "PLGA";
		    fas_glje_o_dept = "PA00";
		    reasonCodeDescription = "Vendor Short Ship";
		} else if (reasonCode.compareTo("36") == 0) {
		    fas_glje_o_acct = "40110";
		    fas_glje_o_div = "PLGA";
		    fas_glje_o_dept = "PA00";
		    reasonCodeDescription = "Vendor Over Ship";
		} else if (reasonCode.compareTo("31") == 0) {
		    fas_glje_o_acct = "40110";
		    fas_glje_o_div = "PLGA";
		    fas_glje_o_dept = "PA00";
		    reasonCodeDescription = "Damaged by Vendor";
		} else if (reasonCode.compareTo("37") == 0) {
		    fas_glje_o_acct = "40110";
		    fas_glje_o_div = "PLGA";
		    fas_glje_o_dept = "PA00";
		    reasonCodeDescription = "RTS incorrectly coded";
		} else if (reasonCode.compareTo("38") == 0) {
		    fas_glje_o_acct = "40110";
		    fas_glje_o_div = "PLGA";
		    fas_glje_o_dept = "PA00";
		    reasonCodeDescription = "Bulk to piece";
		} else if (reasonCode.compareTo("60") == 0) {
		    fas_glje_o_acct = "53130";
		    fas_glje_o_div = "MRC";
		    fas_glje_o_dept = "MR00";
		    reasonCodeDescription = "MRC Cycle Count Adjustment";
		} else if (reasonCode.compareTo("61") == 0) {
		    fas_glje_o_acct = "53130";
		    fas_glje_o_div = "MRC";
		    fas_glje_o_dept = "MR00";
		    reasonCodeDescription = "Parts from Service";
		} else if (reasonCode.compareTo("62") == 0) {
		    fas_glje_o_acct = "53137";
		    fas_glje_o_div = "PLGA";
		    fas_glje_o_dept = "PA00";
		    reasonCodeDescription = "Obsolete Inventory";
		} else if (reasonCode.compareTo("63") == 0) {
		    fas_glje_o_acct = "62510";
		    fas_glje_o_div = "ICS";
		    fas_glje_o_dept = "IC01";
		    reasonCodeDescription = "ICS - IT Supplies";
		} else if (reasonCode.compareTo("64") == 0) {
		    fas_glje_o_acct = "50120";
		    fas_glje_o_div = "PLGA";
		    fas_glje_o_dept = "PA00";
		    reasonCodeDescription = "Janatorial Supplies";
		} else if (reasonCode.compareTo("65") == 0) {
		    fas_glje_o_acct = "50120";
		    fas_glje_o_div = "MRC";
		    fas_glje_o_dept = "MR00";
		    reasonCodeDescription = "Janatorial Supplies";
		} else if (reasonCode.compareTo("67") == 0) {
		    fas_glje_o_acct = "10310";
		    fas_glje_o_div = "CSS";
		    fas_glje_o_dept = "CS00";
		    reasonCodeDescription = "Offset Kitbuild Adjustments";
		} else if (reasonCode.compareTo("68") == 0) {
		    fas_glje_o_acct = "40110";
		    fas_glje_o_div = "RSGD";
		    fas_glje_o_dept = "RD00";
		    reasonCodeDescription = "Parts from Repair with cost";
		} else if (reasonCode.compareTo("69") == 0) {
		    fas_glje_o_acct = "40110";
		    fas_glje_o_div = "ESGB";
		    fas_glje_o_dept = "FB00";
		    reasonCodeDescription = "Parts from Field Service with cost";
		} else if (reasonCode.compareTo("70") == 0) {
		    fas_glje_o_acct = "40110";
		    fas_glje_o_div = "PLGA";
		    fas_glje_o_dept = "PA00";
		    reasonCodeDescription = "Parts used in BOM";
		} else if (reasonCode.compareTo("71") == 0) {
		    fas_glje_o_acct = "40110";
		    fas_glje_o_div = "PLGA";
		    fas_glje_o_dept = "PA00";
		    reasonCodeDescription = "Parts from Galco West";
		} else {
			return null;
		}

		String[] acctDivDeptDescript = new String[4];
		acctDivDeptDescript[0] = fas_glje_o_acct;
		acctDivDeptDescript[1] = fas_glje_o_div;
		acctDivDeptDescript[2] = fas_glje_o_dept;
		acctDivDeptDescript[3] = reasonCodeDescription;

		return acctDivDeptDescript;
	}

	public static ArrayList<String>[] getWDSBinLocaionChanges(Connection wdsConnection, Connection sqlServerConnection, String receiver_nbr, String po_nbr) throws SQLException {
		ArrayList<HashMap<String, Object>> messages_0210_AL = null;

		// DEBUG messages_0210_AL = JDBCUtils.runQuery(sqlServerConnection, "select trans, seqnbr, text from [dbo].[OutBound] where trans = '0210' and substring(text, 31, 7) = '" + receiver_nbr + "' and substring(text, 53, 17) = '" + po_nbr + "'");
		// messages_0210_AL = JDBCUtils.runQuery(sqlServerConnection, "select trans, seqnbr, text from [dbo].[OutBound] where processed = 'N' and trans = '0210' and substring(text, 31, 7) = '" + receiver_nbr + "' and substring(text, 53, 17) = '" + po_nbr + "'");
		messages_0210_AL = JDBCUtils.runQuery(sqlServerConnection, "select trans, seqnbr, text from [dbo].[OutBound] where trans = '0210' and substring(text, 31, 7) = '" + receiver_nbr + "' and substring(text, 53, 17) = '" + po_nbr + "'");
		
		ArrayList<String>[] partNorOverALArray = new ArrayList[5];
		partNorOverALArray[0] = new ArrayList<String>(100);
		partNorOverALArray[1] = new ArrayList<String>(100);
		partNorOverALArray[2] = new ArrayList<String>(100);
		partNorOverALArray[3] = new ArrayList<String>(100);
		partNorOverALArray[4] = new ArrayList<String>(100);
				
		for (Iterator<HashMap<String, Object>> iterator2 = messages_0210_AL.iterator(); iterator2.hasNext();) {
			HashMap<String, Object> messages_0210_HM = (HashMap<String, Object>) iterator2.next();

			String text_0210 = (String) messages_0210_HM.get("text");
			if ((text_0210 == null) || (text_0210.length() < 133)) {
				continue;
			}

			String location = text_0210.substring(123, 133).trim();
			if ((location.compareTo("") == 0						     	) ||
				(location.length() < 4									 	) ||
				(location.substring(0, 4).compareToIgnoreCase("BORD") != 0	)    ) {
				continue;
			}

			String sku = text_0210.substring(69, 99).trim();
			
			if (partNorOverALArray != null) {
				boolean skuIsAlreadyThere = false;
				for (int i = 0; i < partNorOverALArray[0].size(); i++) {
					if (partNorOverALArray[0].get(i).compareToIgnoreCase(sku) == 0) {
						skuIsAlreadyThere = true;
						break;
					}
				}
				if (skuIsAlreadyThere == true) {
					continue;
				}
			}
			
			ArrayList<HashMap<String, Object>> partlocAL = null;
			partlocAL = JDBCUtils.runQuery(wdsConnection, "select normal_binnum, overstk_binnum from pub.partloc where part_num = '" + Utils.replaceSingleQuotesIfNotNull(sku) + "' and (normal_binnum = '' or overstk_binnum = '') WITH (NOLOCK)");
			if ((partlocAL != null) && (partlocAL.size() == 1)) {
				HashMap<String, Object> partlocHM = (HashMap<String, Object>) partlocAL.get(0);

				String old_normal_binnum  = (String) partlocHM.get("normal_binnum");
				String old_overstk_binnum = (String) partlocHM.get("overstk_binnum");

				if (old_normal_binnum == null) {
					old_normal_binnum = "";
				}
				if (old_overstk_binnum == null) {
					old_overstk_binnum = "";
				}
				old_normal_binnum  = old_normal_binnum.trim();
				old_overstk_binnum = old_overstk_binnum.trim();

				if ((old_normal_binnum == null) || (old_normal_binnum.compareTo("") == 0)) {
					partNorOverALArray[0].add(sku);
					
					partNorOverALArray[1].add(location);
					partNorOverALArray[2].add(old_overstk_binnum);
					
					partNorOverALArray[3].add(old_normal_binnum);
					partNorOverALArray[4].add(old_overstk_binnum);

				} else if (((old_overstk_binnum == null) || (old_overstk_binnum.compareTo("") == 0) ) &&
					       (old_normal_binnum.compareToIgnoreCase(location) != 0				) && 
 				           (old_overstk_binnum.compareToIgnoreCase(location) != 0				)    ) {
					partNorOverALArray[0].add(sku);
					
					partNorOverALArray[1].add(old_normal_binnum);
					partNorOverALArray[2].add(location);

					partNorOverALArray[3].add(old_normal_binnum);
					partNorOverALArray[4].add(old_overstk_binnum);
					
				}
			}
		}
		
		if (partNorOverALArray[0].size() > 0) {
			return partNorOverALArray;
		} else {
			return null;
		}
	}

	public static void updateWDSBinLocaionO(Connection wdsConnection, Connection sqlServerConnection, String receiver_nbr, String po_nbr) throws SQLException {
		ArrayList<HashMap<String, Object>> messages_0210_AL = null;
		messages_0210_AL = JDBCUtils.runQuery(sqlServerConnection, "select trans, seqnbr, text from [dbo].[OutBound] where processed = 'N' and trans = '0210' and substring(text, 31, 7) = '" + receiver_nbr + "' and substring(text, 53, 17) = '" + po_nbr + "'");

		for (Iterator<HashMap<String, Object>> iterator2 = messages_0210_AL.iterator(); iterator2.hasNext();) {
			HashMap<String, Object> messages_0210_HM = (HashMap<String, Object>) iterator2.next();

			String text_0210 = (String) messages_0210_HM.get("text");
			if ((text_0210 == null) || (text_0210.length() < 133)) {
				continue;
			}

			String location = text_0210.substring(123, 133).trim();
			if ((location.compareTo("") == 0						     ) ||
				(location.length() < 4									 ) ||
				( (location.substring(0, 4).compareTo("0029") != 0	) &&
				  (location.substring(0, 4).compareTo("0060") != 0	) &&
				  (location.substring(0, 4).compareTo("0061") != 0	) &&
				  (location.substring(0, 4).compareTo("BORD") != 0	) &&
				  (location.substring(0, 4).compareTo("SCPL") != 0	) &&
				  (location.substring(0, 4).compareTo("BAGS") != 0	) 	 )    ) {
				continue;
			}

			String sku = text_0210.substring(69, 99).trim();

			ArrayList<HashMap<String, Object>> partlocAL = null;
			partlocAL = JDBCUtils.runQuery(wdsConnection, "select normal_binnum, overstk_binnum from pub.partloc where part_num = '" + Utils.replaceSingleQuotesIfNotNull(sku) + "' and (normal_binnum = '' or overstk_binnum = '') WITH (NOLOCK)");
			if ((partlocAL != null) && (partlocAL.size() == 1)) {
				HashMap<String, Object> partlocHM = (HashMap<String, Object>) partlocAL.get(0);

				String normal_binnum = (String) partlocHM.get("normal_binnum");
				String overstk_binnum = (String) partlocHM.get("overstk_binnum");

				if ((normal_binnum == null) || (normal_binnum.compareTo("") == 0)) {
					String sqlStmt = "update pub.partloc set normal_binnum = '" + location + "' where part_num = '" + sku + "'";
            		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStmt);
				} else if ((overstk_binnum == null) || (overstk_binnum.compareTo("") == 0)) {
					String sqlStmt = "update pub.partloc set overstk_binnum = '" + location + "' where part_num = '" + sku + "'";
            		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStmt);
				}
			}
		}
	}

	public static void processFascorOutboundMessages(Connection wdsConnection, Connection sqlServerConnection, ArrayList<HashMap<String, Object>> outboundMessagesAL) {
		ArrayList<WDSFascorRequestCommon> wdsFascorRequestCommonAL = new ArrayList<WDSFascorRequestCommon>(100);

		for (Iterator<HashMap<String, Object>> iterator = outboundMessagesAL.iterator(); iterator.hasNext();) {
			HashMap<String, Object> outboundMessagesHM = (HashMap<String, Object>) iterator.next();
			FasUtils.printAHashMap(outboundMessagesHM);

			String trans = ((String) outboundMessagesHM.get("trans")).trim();
			int seqnbr = ((Number) outboundMessagesHM.get("seqnbr")).intValue();
			String text = ((String) outboundMessagesHM.get("text")).trim();

			String currentProcessingRecordDetailsMessage = "seqnbr: " + seqnbr;

			try {

				// ------------------------------------------------------------------------------------------
				// ------------------------------------------------------------------------------------------

				if (trans.compareTo("0135") == 0) {
					String receiver_nbr = FasUtils.getOutboundMessageTextSubstring(text, 31, 7).trim();
					String po_nbr       = FasUtils.getOutboundMessageTextSubstring(text, 53, 17).trim();

					currentProcessingRecordDetailsMessage = "seqnbr: " + seqnbr + ", " + "receiver_nbr: " + receiver_nbr + ", " + "po_nbr: " + po_nbr;

					int poNumberInReceiver = 1;
					{
						ArrayList<HashMap<String, Object>> po_rcptAL = null;
						// po_rcptAL = JDBCUtils.runQuery(wdsConnection, "select rcvg_reportnum from pub.po_rcpt where substring(rcvg_reportnum, 1 , " + (receiver_nbr.length() + 1) + ") = '" + receiver_nbr + "-'");
						po_rcptAL = JDBCUtils.runQuery(wdsConnection, "select rcvg_reportnum from pub.po_rcpt where rcvg_reportnum like '" + receiver_nbr + "-%'");

						if ((po_rcptAL != null) && (po_rcptAL.size() > 0)) {
						    for (Iterator<HashMap<String, Object>> iterator4 = po_rcptAL.iterator(); iterator4.hasNext();) {
						        HashMap<String, Object> po_rcptHM = (HashMap<String, Object>) iterator4.next();

						        String prev_rcvg_reportnum = ((String) po_rcptHM.get("rcvg_reportnum"));

						        int poNo = new Integer(prev_rcvg_reportnum.substring(prev_rcvg_reportnum.indexOf("-") + 1));
						        poNumberInReceiver = Math.max(poNo, poNumberInReceiver);
						    }

						    poNumberInReceiver += 1;
						}
					}

					String wds_receiver_nbr = receiver_nbr + "-" + poNumberInReceiver;

					boolean poRcptRecordBuilt = false;
					
					ArrayList<HashMap<String, Object>> messages_0125_AL = null;
					messages_0125_AL = JDBCUtils.runQuery(sqlServerConnection, "select trans, seqnbr, text from [dbo].[OutBound] where processed = 'N' and trans = '0125' and substring(text, 31, 7) = '" + receiver_nbr + "' and substring(text, 54, 17) = '" + po_nbr + "'");

					for (Iterator<HashMap<String, Object>> iterator2 = messages_0125_AL.iterator(); iterator2.hasNext();) {
						HashMap<String, Object> messages_0125_HM = (HashMap<String, Object>) iterator2.next();

						String text_0125 = ((String) messages_0125_HM.get("text")).trim();
						int seqnbr_0125 = ((Number) messages_0125_HM.get("seqnbr")).intValue();

						String sku 			= FasUtils.getOutboundMessageTextSubstring(text_0125, 71, 30).trim();
						int qty 			= (new Integer(FasUtils.getOutboundMessageTextSubstring(text_0125, 111, 13).trim()) / 100000);
						String qty_sign 	= FasUtils.getOutboundMessageTextSubstring(text_0125, 124, 1);
						int exp_sku_qty 	= (new Integer(FasUtils.getOutboundMessageTextSubstring(text_0125, 125, 13).trim()) / 100000);
						String receivedBy   = FasUtils.getOutboundMessageTextSubstring(text_0125, 19, 10).trim();
						
						if (qty > 0) {
							if (poRcptRecordBuilt == false) {
								POReceiptHandler.build_PO_RCPT_Record(wdsConnection, po_nbr, wds_receiver_nbr, receivedBy);
								poRcptRecordBuilt = true;
							}
							
							// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
							// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
							// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
							
		                    ArrayList<HashMap<String, Object>> pitem2AL = null;
		                    pitem2AL = JDBCUtils.runQuery(wdsConnection, "select order_num, line_num, part_num, qty_ord, qty_received, qty_cancel from pub.pitem where order_num = '" + po_nbr + "' and part_num = '" + Utils.replaceSingleQuotesIfNotNull(sku) + "' order by line_num WITH (NOLOCK)");
		                    if ((pitem2AL != null) && (pitem2AL.size() > 0)) {
		                    	
		                    	
		                    	
		                    	
		                    	
		                    	
		                    	
		                    	
		                    	
		                    	
		                    	
		                    	

		                    	
		                    	
		                    	
		                    	
		                    	
		                    	
		                    	/*

		                    	boolean rfReceiptIsOff = false;
		                    	int rfReciptIsOffBy = 0;
			                    ArrayList<HashMap<String, Object>> po_LineZAL = null;
			                    po_LineZAL = JDBCUtils.runQuery(sqlServerConnection, "select Ordered_Qty, Received_Qty, cast(po_line_nbr as int) as po_line_nbr from PO_Line where PO_Nbr = '" + po_nbr + "' and SKU = '" + Utils.replaceSingleQuotesIfNotNull(sku) + "' order by po_line_nbr");
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
                                        	JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "update PO_Line set Received_Qty = " + Ordered_Qty + " where where PO_Nbr = '" + po_nbr + "' and PO_Line_Nbr = '" + po_line_nbr + "'");
                                        } else {
                                        	if (rfReciptIsOffBy > 0) {
                                        		if (Ordered_Qty > Received_Qty) {
                                        			int adjustment = (rfReciptIsOffBy > (Ordered_Qty - Received_Qty))?(Ordered_Qty - Received_Qty):rfReciptIsOffBy;
                                                	JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "update PO_Line set Received_Qty = " + (Received_Qty + adjustment) + " where where PO_Nbr = '" + po_nbr + "' and PO_Line_Nbr = '" + po_line_nbr + "'");
                                                	rfReciptIsOffBy -= adjustment;
                                        		}
                                        	} else {
                                        		break;
                                        	}
                                        }
									}
			                    	
			                    	if (rfReciptIsOffBy > 0) {
		    							throw new RuntimeException("For the PO " + po_nbr + ", and for the SKU " + sku + ", RF mismatched quantities didn't add up.");			    									                    		
			                    	}
			                    	
			                    }
			                    
			                    */
		                    	
		                    	
			                    
			                    
			                    
			                    
			                    
			                    
			                    
			                    
		                    	
		                    	
		                    	
		                    	
		                    	
		                    	
		                    	
			                    ArrayList<HashMap<String, Object>> po_LineAL = null;
			                    po_LineAL = JDBCUtils.runQuery(sqlServerConnection, "select Ordered_Qty, Received_Qty, cast(po_line_nbr as int) as po_line_nbr from PO_Line where PO_Nbr = '" + po_nbr + "' and SKU = '" + Utils.replaceSingleQuotesIfNotNull(sku) + "' order by po_line_nbr");
			                    if ((po_LineAL != null) && (po_LineAL.size() > 0)) {
			                    	HashMap<Integer, HashMap<String, Object>> pitem2ByLineNoHM = new HashMap<Integer, HashMap<String, Object>>(po_LineAL.size());
			    					for (Iterator<HashMap<String, Object>> iterator4 = pitem2AL.iterator(); iterator4.hasNext();) {
			    						HashMap<String, Object> pitem_HM = (HashMap<String, Object>) iterator4.next();
			    						pitem2ByLineNoHM.put(((Number) pitem_HM.get("line_num")).intValue(), pitem_HM);
			    					}
			                    	
			    					
			                    	int totalOfDifferencesOfWDSAndFascorReceivedQuantities = 0;
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
			    							totalOfDifferencesOfWDSAndFascorReceivedQuantities += (Received_Qty - qty_received);
			    						}
			    					}
			    					
			    					
			    					// =====================================================================================================
			    					// =====================================================================================================
			    					// =====================================================================================================

			    					
			    					// -----------------------------------------------------------------------------------------------------
			    					
			    					
			    					/*
			    					
			    					if (totalOfDifferencesOfWDSAndFascorReceivedQuantities != qty) {
		    							throw new RuntimeException("For the PO " + po_nbr + ", and for the SKU " + sku + ", quantity to be posted, which is " + qty + ", doesn't match calculated quantity, which is " + totalOfDifferencesOfWDSAndFascorReceivedQuantities);			    						
			    					}
			                    	
			    					for (Iterator<HashMap<String, Object>> iterator3 = po_LineAL.iterator(); iterator3.hasNext();) {
			    						HashMap<String, Object> po_Line_HM = (HashMap<String, Object>) iterator3.next();

			    						int Ordered_Qty = ((Number) po_Line_HM.get("Ordered_Qty")).intValue();
			    						int Received_Qty = ((Number) po_Line_HM.get("Received_Qty")).intValue();
			    						int po_line_nbr = ((Number) po_Line_HM.get("po_line_nbr")).intValue();

			    						HashMap<String, Object> pitem_HM = (HashMap<String, Object>) pitem2ByLineNoHM.get(po_line_nbr);
			    						int qty_received = ((Number) pitem_HM.get("qty_received")).intValue();
			    						
			    						if (Received_Qty > qty_received) {
			    							log.debug("Building PI_RCPT with receiver_nbr: " + receiver_nbr + ", po_nbr: " + po_nbr + ", line " + po_line_nbr + ", Quantity: " + (Received_Qty - qty_received));			    							
				    						POReceiptHandler.build_PI_RCPT_Record(wdsConnection, po_nbr, po_line_nbr, (Received_Qty - qty_received));			    							
			    						}
			    					}
			    					
			    					*/

			    					
			    					// -----------------------------------------------------------------------------------------------------

			    					
			    					if (qty > totalOfDifferencesOfWDSAndFascorReceivedQuantities) {
		    							throw new RuntimeException("For the PO " + po_nbr + ", and for the SKU " + sku + ", quantity to be posted, which is " + qty + ", is more than total of differences of WDS and Fascor received quantities, which is " + totalOfDifferencesOfWDSAndFascorReceivedQuantities);			    						
			    					}
			                    	
			    					int quantityYetToBeApportioned = qty;
			    					
			    					for (Iterator<HashMap<String, Object>> iterator3 = po_LineAL.iterator(); iterator3.hasNext();) {
			    						if (quantityYetToBeApportioned <= 0) {
			    							break;
			    						}
			    						
			    						HashMap<String, Object> po_Line_HM = (HashMap<String, Object>) iterator3.next();

			    						int Ordered_Qty = ((Number) po_Line_HM.get("Ordered_Qty")).intValue();
			    						int Received_Qty = ((Number) po_Line_HM.get("Received_Qty")).intValue();
			    						int po_line_nbr = ((Number) po_Line_HM.get("po_line_nbr")).intValue();

			    						HashMap<String, Object> pitem_HM = (HashMap<String, Object>) pitem2ByLineNoHM.get(po_line_nbr);
			    						int qty_received = ((Number) pitem_HM.get("qty_received")).intValue();
			    						
			    						if (Received_Qty > qty_received) {
			    							int postingQuantity = (quantityYetToBeApportioned >= (Received_Qty - qty_received))?(Received_Qty - qty_received):quantityYetToBeApportioned;
			    							
			    							log.debug("qty: " +  qty + ", quantityYetToBeApportioned: " + quantityYetToBeApportioned + ", postingQuantity: " + postingQuantity + ", po_line_nbr: " + po_line_nbr);			    							

			    							log.debug("Building PI_RCPT with receiver_nbr: " + receiver_nbr + ", po_nbr: " + po_nbr + ", line " + po_line_nbr + ", Quantity: " + (Received_Qty - qty_received));			    							
				    						POReceiptHandler.build_PI_RCPT_Record(wdsConnection, po_nbr, po_line_nbr, postingQuantity);
				    						
				    						quantityYetToBeApportioned -= postingQuantity;
			    						}
			    					}

			    					
			    					// =====================================================================================================
			    					// =====================================================================================================
			    					// =====================================================================================================
			    					
			    					
			                    } else {
			                    	throw new RuntimeException("For the PO " + po_nbr + ", and for the SKU " + sku + ", there are no lines Fascor.");
			                    }
		                    } else {
		                    	throw new RuntimeException("For the PO " + po_nbr + ", and for the SKU " + sku + ", there are no lines in WDS.");
		                    }

		                    // --------------------------------------------------------------------------------------------
		                    /*
							
							int qtyRemainingToBeAportioned = qty;
		                    ArrayList<HashMap<String, Object>> pitemAL = null;
		                    // pitemAL = JDBCUtils.runQuery(wdsConnection, "select order_num, line_num, part_num, qty_ord, qty_received, qty_cancel from pub.pitem where order_num = '" + po_nbr + "' and part_num = '" + Utils.replaceSingleQuotesIfNotNull(sku) + "' FOR UPDATE NOWAIT");
		                    pitemAL = JDBCUtils.runQuery(wdsConnection, "select order_num, line_num, part_num, qty_ord, qty_received, qty_cancel from pub.pitem where order_num = '" + po_nbr + "' and part_num = '" + Utils.replaceSingleQuotesIfNotNull(sku) + "' WITH (NOLOCK)");
	
		                    if ((pitemAL != null) && (pitemAL.size() > 0)) {
		    					for (Iterator<HashMap<String, Object>> iterator3 = pitemAL.iterator(); iterator3.hasNext();) {
		    						if (qtyRemainingToBeAportioned == 0) {
		    							break;
		    						}
	
		    						HashMap<String, Object> pitem_HM = (HashMap<String, Object>) iterator3.next();
	
		    						int line_num = ((Number) pitem_HM.get("line_num")).intValue();
		    						int qty_ord = ((Number) pitem_HM.get("qty_ord")).intValue();
		    						int qty_received = ((Number) pitem_HM.get("qty_received")).intValue();
		    						int qty_cancel = ((Number) pitem_HM.get("qty_cancel")).intValue();
	
		    						int qty_yet_to_receive = qty_ord - qty_cancel - qty_received;
	
		    						int qtyAportioned;
		    						if (qtyRemainingToBeAportioned >= qty_yet_to_receive) {
		    							qtyAportioned = qty_yet_to_receive;
		    							qtyRemainingToBeAportioned -= qty_yet_to_receive;
		    						} else {
		    							qtyAportioned = qtyRemainingToBeAportioned;
		    							qtyRemainingToBeAportioned = 0;
		    						}
	
		    						POReceiptHandler.build_PI_RCPT_Record(wdsConnection, po_nbr, line_num, qtyAportioned);
		    					}
	
		    					if (qtyRemainingToBeAportioned > 0) {
			                    	throw new RuntimeException("On the PO " + po_nbr + ", for the SKU " + sku + ", Fascor received more than what's on PO. Extra quantity: " + qtyRemainingToBeAportioned);
		    					}
		                    } else {
		                    	throw new RuntimeException("On the PO " + po_nbr + ", SKU " + sku + " is not there, or locked by someone.");
		                    }
		                    
		                    */
		                    // --------------------------------------------------------------------------------------------
		                    
							// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
							// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
							// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		                    
						} else {
							log.debug("receiver_nbr: " + receiver_nbr + ", po_nbr: " + po_nbr + ", skipped posting sku " + sku + " as qty is zero.");
						}
					}

					// updateWDSBinLocaion(wdsConnection, sqlServerConnection, receiver_nbr, po_nbr);
					// ArrayList<String>[] partNorOverALArray = null;

					JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "update [dbo].[OutBound] set processed = 'Y' where processed = 'N' and trans in ('0120', '0130', '0135', '0210') and substring(text, 31, 7) = '" + receiver_nbr + "' and substring(text, 53, 17) = '" + po_nbr + "'");
		        	JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "update [dbo].[OutBound] set processed = 'Y' where processed = 'N' and trans in ('0125') and substring(text, 31, 7) = '" + receiver_nbr + "' and substring(text, 54, 17) = '" + po_nbr + "'");
		        	JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "update [dbo].[OutBound] set processed = 'Y' where processed = 'N' and trans in ('0230') and substring(text, 31, 7) = '" + receiver_nbr + "' and substring(text, 38, 17) = '" + po_nbr + "'");
		        	JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "update [dbo].[OutBound] set processed = 'Y' where processed = 'N' and trans in ('0111') and substring(text, 32, 7) = '" + receiver_nbr + "' and substring(text, 39, 17) = '" + po_nbr + "'");

		        	JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "update [dbo].[OutBound] set processed = 'Y' where processed = 'N' and trans in ('0110') and substring(text, 32, 7) = '" + receiver_nbr + "'");

	                if ((JDBCUtils.UPDATE_FASCOR_DB == true	) &&
	    		    	(JDBCUtils.UPDATE_WDS_DB == true	)	 ) {
	                	
						if (poRcptRecordBuilt == true) {
		                	if (Parms.FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX == true) {
		    	                JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
		    	                JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);
	
		    	                new File("/reports/galco/log/FasIP302Parm.txt").delete();
								
								SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
						 		String outputFileName = "/reports/galco/log/Z_I_POST_Output_" + yyyyMMddHHmmssSSS.format(new Date()) + "_" + ((int) (Math.random() * 999999)) + ".txt";
						 		log.debug("wds_receiver_nbr: " +  wds_receiver_nbr + ", po_nbr: " + po_nbr + ", outputFileName: " + outputFileName);
								
								PrintWriter printWriter = new PrintWriter("/reports/galco/log/FasIP302Parm.txt", "UTF-8");
								
								printWriter.println(po_nbr + "," + wds_receiver_nbr + "," + outputFileName + ",");
								
								/*
								String dataToWDS = po_nbr + "," + wds_receiver_nbr + "," + outputFileName + ",";
								if (partNorOverALArray != null) {
									for (int i = 0; i < partNorOverALArray[0].size(); i++) {
										dataToWDS = dataToWDS + partNorOverALArray[0].get(i) + "," + partNorOverALArray[1].get(i) + "," + partNorOverALArray[2].get(i) + ",";
									}
								}
								printWriter.println(dataToWDS);
								*/
								
								printWriter.close();
	
								int rc = FasUtils.executeCommandLine("/apps/wds/GALCO/batch/FasIP", 60000, outputFileName);
								
								String iPostOutput = FasUtils.readFileContentsIntoAString(outputFileName);
								new File(outputFileName).delete();
	
								if (iPostOutput.indexOf("*+*success*+*") < 0) {
									throw new Exception("Didn't find the string *+*success*+*	, meaning I-Post process failed. \n iPostOutput: \n" + iPostOutput);
								}

								
								ArrayList<String>[] partNorOverALArray = getWDSBinLocaionChanges(wdsConnection, sqlServerConnection, receiver_nbr, po_nbr);
								ChangeBin.changeBins(wdsConnection, FascorProcessor.dbConnector8.getConnectionSRO(), partNorOverALArray);				
								
								
								log.debug("Successfully I-Posted wds_receiver_nbr: " +  wds_receiver_nbr + ", po_nbr: " + po_nbr);									
		                	} else {
		    	                JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
		    	                JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);
	
			                	String shellScriptOutput = FasUtils.executeShellScriptFromWindows("app3.galco.com", "sva0604", "TalliBujji1#", "/home/sva0604/ZRunTestIPostFromWin.sh " + po_nbr + " " + wds_receiver_nbr);
			                	log.debug("Shell script output: " + shellScriptOutput);
		                	}
						} else {
							log.debug("receiver_nbr: " + receiver_nbr + ", po_nbr: " + po_nbr + ", skipped posting this receiver as all SKUs have zero quantities.");							
						}
	    		    } else {
			        	log.debug("Skipped running Shell script.");
	    		    }

	                log.debug("Successfully processed the message 0135. " + currentProcessingRecordDetailsMessage);

				// ------------------------------------------------------------------------------------------
				// ------------------------------------------------------------------------------------------

				} else if (trans.compareTo("0310") == 0) {
					String sku 	   		= FasUtils.getOutboundMessageTextSubstring(text, 42, 30).trim();
					int    qty 	   		= (new Integer(FasUtils.getOutboundMessageTextSubstring(text, 82, 13).trim()) / 100000);
					String qtySign 		= FasUtils.getOutboundMessageTextSubstring(text, 95, 1).trim();
					String reasonCode 	= FasUtils.getOutboundMessageTextSubstring(text, 114, 5).trim();
					String comments 	= FasUtils.getOutboundMessageTextSubstring(text, 119, 90);

					currentProcessingRecordDetailsMessage = "seqnbr: " + seqnbr + ", " + "sku: " + sku + ", " + "qty: " + qty + ", " + "qtySign: " + qtySign + ", " + "reasonCode: " + reasonCode + ", " + "comments: " + comments;

					if (Math.abs(qty) > 0) {
						comments = comments.substring(0, 36);
	                    if (qtySign.compareTo("-") == 0) {
							qty = qty * -1;
						}
	
						String[] acctDivDeptDescript = getGLAccountNumberGivenReasonCode(reasonCode);
	
						if (acctDivDeptDescript != null) {
							String fas_glje_o_acct, fas_glje_o_div, fas_glje_o_dept,
							   	   reasonCodeDescription;
	
							fas_glje_o_acct = acctDivDeptDescript[0];
							fas_glje_o_div = acctDivDeptDescript[1];
							fas_glje_o_dept = acctDivDeptDescript[2];
							reasonCodeDescription = acctDivDeptDescript[3];
	
			                if ((JDBCUtils.UPDATE_FASCOR_DB == true	) &&
			    		    	(JDBCUtils.UPDATE_WDS_DB == true	)	 ) {
	
								String inventoryAdjustmentOutput;
			                	if (Parms.FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX == true) {
									new File("/reports/galco/log/FasIA310Parm.txt").delete();
									new File("/reports/galco/log/Z_InvAdjustment_Output.txt").delete();
	
									PrintWriter printWriter = new PrintWriter("/reports/galco/log/FasIA310Parm.txt", "UTF-8");
									printWriter.println(sku + "|" + qty + "|" + comments + "|" + fas_glje_o_acct + "|" + fas_glje_o_div + "|" + fas_glje_o_dept + "|");
									printWriter.close();
	
									int rc = FasUtils.executeCommandLine("/apps/wds/GALCO/batch/FasIA", 60000, "/reports/galco/log/Z_InvAdjustment_Output.txt");
	
									inventoryAdjustmentOutput = FasUtils.readFileContentsIntoAString("/reports/galco/log/Z_InvAdjustment_Output.txt");
	
									if (inventoryAdjustmentOutput.indexOf("*+*success*+*") < 0) {
										throw new Exception("Didn't find the string    *+*success*+*    , meaning inventory adjustment process failed. \n inventoryAdjustmentOutput: \n" + inventoryAdjustmentOutput);
									}
	
									log.debug("Successfully adjusted inventory for seqnbr: " +  seqnbr + ", sku: " + sku + ", qty: " + qty);
								} else {
				                	String shellScriptOutput = FasUtils.executeShellScriptFromWindows("app3.galco.com", "sva0604", "TalliBujji1#", "/home/sva0604/ZRunTestIAdjFromWin.sh " + "\"" + sku + "|" + qty + "|" + comments + "|" + fas_glje_o_acct + "|" + fas_glje_o_div + "|" + fas_glje_o_dept + "|\"");
				                	log.debug("Shell script output: " + shellScriptOutput);
				    		    }
			    		    } else {
					        	log.debug("Skipped running Shell script.");
			    		    }
						} else {
							log.debug("Invalid reason code, skipped message. " + currentProcessingRecordDetailsMessage);
						}
					} else {
						log.debug("Skipped inventory adjustment as quantity is zero, details --> " + currentProcessingRecordDetailsMessage);
					}

	                JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "update [dbo].[OutBound] set processed = 'Y' where processed = 'N' and SeqNbr = '" + seqnbr + "'");

	                JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);

	                log.debug("Successfully processed the message 0310. " +  currentProcessingRecordDetailsMessage);

					// ------------------------------------------------------------------------------------------
					// ------------------------------------------------------------------------------------------

				} else if (trans.compareTo("0210") == 0) {
					String receiver_Nbr = MessageAbs.getFieldValueFromInboundMessage("Receiver_Nbr", text);
					if ((receiver_Nbr != null) && (receiver_Nbr.length() >= 1) && (receiver_Nbr.substring(0, 1).compareToIgnoreCase("S") == 0)) {
						String order_num, part_num;
						int quantityBuiltNow;

						{
							int qty = new Integer((MessageAbs.getFieldValueFromInboundMessage("Qty", text)).replaceFirst("^0+(?!$)", ""));
							quantityBuiltNow = (qty / 100000);

							String po_Nbr = MessageAbs.getFieldValueFromInboundMessage("PO_Nbr", text);
							order_num = po_Nbr.trim();

							String sku = MessageAbs.getFieldValueFromInboundMessage("SKU", text);
							part_num = sku.trim();
						}

						currentProcessingRecordDetailsMessage = "seqnbr: " + seqnbr + ", " + "order_num: " + order_num + ", " + "part_num: " + part_num + ", quantityBuiltNow: " + quantityBuiltNow;

						if ((order_num != null) && (order_num.length() >= 2) && (order_num.substring(0, 2).compareToIgnoreCase("OL") == 0)) {
							String eng_rls_num;
		                	try {
			                	ArrayList<Object> eng_rls_num_AL;
		                		ArrayList<Object>[] alArr = JDBCUtils.runQuery_ReturnObjALs(wdsConnection, "select top 1 eng_rls_num from pub.KitsForStock where order_num = '" + order_num + "'");
		                		eng_rls_num_AL = alArr[0];
		                		eng_rls_num = (String) eng_rls_num_AL.get(0);
							} catch (Exception e) {
								throw new RuntimeException("Failed to find eng_rls_num for the order " + order_num);
							}


		                	{
			                	ArrayList<Object> componentAL;
			                	ArrayList<Object> qty_usedAL;
			                	{
			                		ArrayList<Object>[] alArr = JDBCUtils.runQuery_ReturnObjALs(wdsConnection, "select partnum_comp, qty_used from pub.part_bom where partnum_assm = '" + part_num + "' and eng_rls_num  = '" + eng_rls_num + "'");
			                		componentAL = alArr[0];
			                		qty_usedAL = alArr[1];
			                	}

			                	String debugDecommittingMessage = order_num + " - " + part_num + " - " + eng_rls_num + " - Decommitts - ";

			                	for (int i = 0; i < componentAL.size(); i++) {
									String partnum_comp = (String) componentAL.get(i);
									int qty_used = (int) qty_usedAL.get(i);

									int quantityDecommitting = quantityBuiltNow * qty_used;

									debugDecommittingMessage += (partnum_comp + " " + quantityDecommitting + " ");

									String sqlStmt = "update pub.partloc set qty_commit = qty_commit - " + quantityDecommitting + " where part_num = '" + partnum_comp + "'";
				            		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStmt);

				            		sqlStmt = "update pub.KitsForStock set qtyUsed = qtyUsed + " + quantityDecommitting + " where order_num = '" + order_num + "' and partnum_assm = '" + part_num + "' and partnum_comp = '" + partnum_comp + "'";
				            		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStmt);
								}
			                    log.debug(debugDecommittingMessage);
		                	}

		                	{
			                	ArrayList<Object> componentsYetToBeUsedAL;
			                	{
			                		ArrayList<Object>[] alArr = JDBCUtils.runQuery_ReturnObjALs(wdsConnection, "select partnum_comp from pub.KitsForStock where order_num = '" + order_num + "' and qtyUsed < qtyCommitted");
			                		componentsYetToBeUsedAL = alArr[0];
			                	}

			                	if ((componentsYetToBeUsedAL == null) || (componentsYetToBeUsedAL.size() == 0)) {
				            		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, "update pub.KitsForStock set buildCompleteDate = '" + FasUtils.convertDateToMMDDYY(new Date()) + "' where order_num = '" + order_num + "'");
			                	}
		                	}

			                if ((JDBCUtils.UPDATE_FASCOR_DB == true	) &&
			    		    	(JDBCUtils.UPDATE_WDS_DB == true	)	 ) {
								String kitBuildForStockOutput;
				                if (Parms.FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX == true) {
			    	                JDBCUtils.commitWDSChanges_Fascor(wdsConnection);

									new File("/reports/galco/log/FasKBFSParm.txt").delete();
									new File("/reports/galco/log/Z_KitBuildForStock_Output.txt").delete();

									PrintWriter printWriter = new PrintWriter("/reports/galco/log/FasKBFSParm.txt", "UTF-8");
									printWriter.println(part_num + "|" + quantityBuiltNow + "|" + eng_rls_num + "|");
									printWriter.close();

									int rc = FasUtils.executeCommandLine("/apps/wds/GALCO/batch/FasKB", 60000, "/reports/galco/log/Z_KitBuildForStock_Output.txt");

									kitBuildForStockOutput = FasUtils.readFileContentsIntoAString("/reports/galco/log/Z_KitBuildForStock_Output.txt");

									if (kitBuildForStockOutput.indexOf("*+*success*+*") < 0) {
										throw new Exception("Didn't find the string    *+*success*+*    , meaning kit-build-for-stock posting process failed. \n kitBuildForStockOutput: \n" + kitBuildForStockOutput);
									}

									JDBCUtils.commitWDSChanges_Fascor(wdsConnection);

									log.debug("Successfully posted kit for stock for seqnbr: " +  seqnbr + ", part_num: " + part_num + ", quantityBuiltNow: " + quantityBuiltNow + ", eng_rls_num: " + eng_rls_num);
								} else {
				                    JDBCUtils.commitWDSChanges_Fascor(wdsConnection);

					                String shellScriptOutput = FasUtils.executeShellScriptFromWindows("app3.galco.com", "sva0604", "TalliBujji1#", "/home/sva0604/ZRunTestKbldFromWin.sh " + "\"" + part_num + "|" + quantityBuiltNow + "|" + eng_rls_num + "|\"");
				                	log.debug("Shell script output: " + shellScriptOutput);
				    		    }
			    		    } else {
					        	log.debug("Skipped running Shell script.");
			    		    }
						} else {
		                	ArrayList<Object> sequenceNumber_YetToBuildForThisPart_AL;
		                	ArrayList<Object> sequenceNumberOfKitBuildRqst_YetToBuildForThisPart_AL;
		                	ArrayList<Object> quantityBuilt_YetToBuildForThisPart_AL;
		                	ArrayList<Object> origQuantity_YetToBuildForThisPart_AL;

	                		ArrayList<Object>[] alArr = JDBCUtils.runQuery_ReturnObjALs(wdsConnection, "select sequenceNumber, sequenceNumberOfKitBuildRqst, quantityBuilt, origQuantity from pub.OrdersWaitingOnKits where order_num = '" + order_num + "' and part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "' and (origQuantity - quantityBuilt) > 0");
	                		sequenceNumber_YetToBuildForThisPart_AL = alArr[0];
	                		sequenceNumberOfKitBuildRqst_YetToBuildForThisPart_AL = alArr[1];
	                		quantityBuilt_YetToBuildForThisPart_AL = alArr[2];
	                		origQuantity_YetToBuildForThisPart_AL = alArr[3];
	                		if ((sequenceNumberOfKitBuildRqst_YetToBuildForThisPart_AL != null) && (sequenceNumberOfKitBuildRqst_YetToBuildForThisPart_AL.size() > 0)) {
	                			int qtyToBeApportioned = quantityBuiltNow;

			                	for (int i = 0; i < sequenceNumberOfKitBuildRqst_YetToBuildForThisPart_AL.size(); i++) {
			        				String sequenceNumberOfKitBuildRqst = (String) sequenceNumberOfKitBuildRqst_YetToBuildForThisPart_AL.get(i);
			        				int quantityBuilt = ((Number) quantityBuilt_YetToBuildForThisPart_AL.get(i)).intValue();
			        				int origQuantity = ((Number) origQuantity_YetToBuildForThisPart_AL.get(i)).intValue();

			        				if ((origQuantity - quantityBuilt) <= qtyToBeApportioned) {
				        				String sqlStatement = "update pub.OrdersWaitingOnKits set quantityBuilt = " + origQuantity + ", buildComplete = 'Y' where order_num = '" + order_num + "' and part_num = '" + part_num + "' and sequenceNumberOfKitBuildRqst = '" + sequenceNumberOfKitBuildRqst + "'";
					        			JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStatement);
					        			qtyToBeApportioned -= (origQuantity - quantityBuilt);
			        				} else {
				        				String sqlStatement = "update pub.OrdersWaitingOnKits set quantityBuilt = " + (quantityBuilt + qtyToBeApportioned) + " where order_num = '" + order_num + "' and part_num = '" + part_num + "' and sequenceNumberOfKitBuildRqst = '" + sequenceNumberOfKitBuildRqst + "'";
					        			JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStatement);
					        			qtyToBeApportioned = 0;
			        				}

			        				if (qtyToBeApportioned == 0) {
			        					break;
			        				}
			                	}

			                	ArrayList<Object> yetTo_sequenceNumberOfKitBuildRqst_AL;
		                		ArrayList<Object>[] al2Arr = JDBCUtils.runQuery_ReturnObjALs(wdsConnection, "select sequenceNumberOfKitBuildRqst from pub.OrdersWaitingOnKits where order_num = '" + order_num + "' and buildComplete = 'N'");
		                		yetTo_sequenceNumberOfKitBuildRqst_AL = al2Arr[0];
		                		if ((yetTo_sequenceNumberOfKitBuildRqst_AL == null) || (yetTo_sequenceNumberOfKitBuildRqst_AL.size() == 0)) {
									FasUtils.set_FascorRequest_Processed_Flag(wdsConnection, (String) sequenceNumber_YetToBuildForThisPart_AL.get(0), "N");
		                		}
	                		}

			                JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
						}

		                JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "update [dbo].[OutBound] set processed = 'Y' where processed = 'N' and SeqNbr = '" + seqnbr + "'");

		                JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);

		                log.debug("Successfully processed the message 0210. " +  currentProcessingRecordDetailsMessage);
					}
				}

				// ------------------------------------------------------------------------------------------
				// ------------------------------------------------------------------------------------------

	        } catch (Exception e) {
				try {
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

	                String msg = "Error occurred while processing outbound message." + currentProcessingRecordDetailsMessage + ". Successfully rolled back changes. Exception message: " + exceptionMessage;
					log.debug(msg, e);
	    			galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Problem in FASCOR Batch Process of " + Parms.HOST_NAME, msg);
	    			log.debug("Sent email");

					try {
		                JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, "update [dbo].[OutBound] set processed = 'Y' where processed = 'N' and SeqNbr = '" + seqnbr + "'");
		                JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);
					} catch (SQLException e1) {
						String sqlExceptionInfo = FasUtils.getSQLExceptionInfo((SQLException) e);
						log.debug(sqlExceptionInfo);

		    			msg = "Error occurred while setting processed flag to E, seqnbr: " + seqnbr + ". Exception message: " + sqlExceptionInfo;
						log.debug(msg);
						galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Problem in FASCOR Batch Process of " + Parms.HOST_NAME, msg);
					}
					
	    			log.debug("Continuing...");
				} catch (Exception e2) {
					log.debug("Error occurred while processing outbound message." + currentProcessingRecordDetailsMessage + ". Failed to roll back changes.", e2);
					galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Problem in FASCOR Batch Process of " + Parms.HOST_NAME, "Error occurred while processing outbound message." + currentProcessingRecordDetailsMessage + ". Failed to roll back changes. Original exception: " + e.getMessage() + ". Subsequent exception: " + e2.getMessage());
				}
			}
		}
	}
}
