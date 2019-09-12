package galco.fascor.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.h2.util.IntArray;

import galco.fascor.cache.CacheManager;
import galco.fascor.messages.Message;
import galco.fascor.messages.MessageAbs;
import galco.fascor.messages.Message_0140;
import galco.fascor.messages.Message_1110;
import galco.fascor.messages.Message_3052;
import galco.fascor.messages.Message_3057;
import galco.fascor.requests.control.FascorInboundMessageHandler;
import galco.fascor.utils.FasUtils;
import galco.fascor.wds_requests.WDSFascorRequestCommon;
import galco.portal.config.Parms;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;
import galco.portal.wds.dao.Contact;
import galco.portal.wds.dao.FascorMessage;

public class KitLoaderNew_Fix {
	private static Logger log = Logger.getLogger(KitLoaderNew_Fix.class);

	public static void main(String[] args) {
		
		try {
		    SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

			Logger.getRootLogger().removeAppender("A1"); // stops console logging
			
			FileAppender fa = new FileAppender();
			fa.setName("FileLogger");
			  
			fa.setFile("C:\\0Ati\\ZLoadKits-" + yyyyMMddHHmmss.format(new Date()) + ".log");
			
			// fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
			fa.setLayout(new PatternLayout("%-4r [%t] %-5p %c %x - %m%n"));
			  
			fa.setThreshold(Level.ERROR);
			fa.setAppend(true);
			fa.activateOptions();
			
			//add appender to any Logger (here is root)
			Logger.getRootLogger().addAppender(fa);
		} catch (Exception e) {
			System.out.println("Exception while trying to set log4j properties.");
			e.printStackTrace();
			System.exit(0);
		}
		
	    JDBCUtils.UPDATE_FASCOR_DB = true;
	    JDBCUtils.UPDATE_WDS_DB = true;
	    
	    JDBCUtils.UPDATE_FASCOR_DB_IN_BATCH = true;

	    
		if (FascorProcessor.getDBConnections(60) == true) {
			Connection wdsConnection = FascorProcessor.dbConnector8.getConnectionWDS();

			
			
			
			
			
			
			
			
			HashMap<String, String> sentKeys = new HashMap<String, String>(1000000);
	        try {
	            BufferedReader br = new BufferedReader(new FileReader(new File("C:\\0Ati\\000-111\\SQLServerData.txt")));
	            String text = null;
	            while ((text = br.readLine()) != null) {				
					String messageType = MessageAbs.getFieldValueFromInboundMessage("Message_Type", text);
					String bomName = MessageAbs.getFieldValueFromInboundMessage("BOM_Name", text);
					String sku = MessageAbs.getFieldValueFromInboundMessage("SKU", text);

					if (messageType.compareTo("3052") == 0) {
						sentKeys.put(bomName, "");						
					} else {
						sentKeys.put(bomName + "|" + sku, "");												
					}
	            }
	            
	            br.close();
	        } catch (IOException ioe) {
				FascorProcessor.closeDBConnections();
	        	log.error("Exception occurred", ioe);
				System.exit(0);
			}		
			
			
			
			
			
			
			
			

			
			
			
			
			
			
			
			
			
			
			
			ArrayList<HashMap<String, Object>> part_bomAl = null;
	        try {
													            // part_bomAl = JDBCUtils.runQuery(wdsConnection, "select top 2 partnum_assm, eng_rls_num from pub.part_bom as pbp where eng_rls_num = (select max(eng_rls_num) from pub.part_bom as pbc where pbc.partnum_assm = pbp.partnum_assm)");	            
													            // part_bomAl = JDBCUtils.runQuery(wdsConnection, "select partnum_assm, eng_rls_num from pub.part_bom as pbp where eng_rls_num = (select max(eng_rls_num) from pub.part_bom as pbc where pbc.partnum_assm = pbp.partnum_assm) order by partnum_assm");
													        	// part_bomAl = JDBCUtils.runQuery(wdsConnection, "select partnum_assm, CASE WHEN eng_rls_num = 'blank' THEN ' ' ELSE eng_rls_num END as eng_rls_num_new from pub.part_bom order by partnum_assm, eng_rls_num_new desc");
	            // part_bomAl = JDBCUtils.runQuery(wdsConnection, "select top 5 partnum_assm from pub.part_bom order by partnum_assm");
	            // part_bomAl = JDBCUtils.runQuery(wdsConnection, "select partnum_assm from pub.part_bom where partnum_assm = '410403-24AWL-NBR' order by partnum_assm");
	            // part_bomAl = JDBCUtils.runQuery(wdsConnection, "select partnum_assm from pub.part_bom where partnum_assm > 'DIB001JCYV1K12DKY1BBBXXX-DIB' order by partnum_assm");
	            part_bomAl = JDBCUtils.runQuery(wdsConnection, "select partnum_assm from pub.part_bom where order by partnum_assm");
	            
	            /*
	            String prev_partnum_assm = "", partnum_assm;
	            for (Iterator<HashMap<String, Object>> iterator  = part_bomAl.iterator(); iterator.hasNext();) {
					HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();
					partnum_assm = ((String) hashMap.get("partnum_assm")).trim();

					if (prev_partnum_assm.compareToIgnoreCase(partnum_assm) == 0) {
						continue;						
					}
					prev_partnum_assm = partnum_assm;

					String eng_rls_num = Message_3052.get_eng_rls_num(wdsConnection, partnum_assm);

					log.error(((String) hashMap.get("partnum_assm")).trim() + " " + eng_rls_num);
				}
				closeDBConnections();
				System.exit(0);
				*/
	            
	            
	            
		    } catch (SQLException e ) {
				e.printStackTrace();
				FascorProcessor.closeDBConnections();
				System.exit(0);
		    }

			int processed = 0;
			int noOfErrors = 0;
			int commitCount = 0;
			
			long startTime = System.currentTimeMillis();
			
			String prev_partnum_assm = "";
			
            for (Iterator<HashMap<String, Object>> iterator = part_bomAl.iterator(); iterator.hasNext();) {
            	String partnum_assm = "";
            	
            	try {
	            	HashMap<String, Object> partHM = (HashMap<String, Object>) iterator.next();
					partnum_assm = ((String) partHM.get("partnum_assm")).trim();
					
					if (prev_partnum_assm.compareToIgnoreCase(partnum_assm) == 0) {
						continue;						
					}
					prev_partnum_assm = partnum_assm;
					
					if (partnum_assm.length() > 30) {
						log.error(partnum_assm + ": Failed to send, length is too big");
						continue;
					}
					
					// --------------------------------------------------------------------------------------------
					
					String eng_rls_num = Message_3052.get_eng_rls_num(wdsConnection, partnum_assm);

					try {
	                	Message_3052 message_3052 = new Message_3052(wdsConnection, partnum_assm, eng_rls_num, "A");
	                	
	                	if (sentKeys.get(partnum_assm) == null) {
							String fullFascorMessageStr = CacheManager.get_FascorMessage_FixedPart("3052", "A") + message_3052.getFascorMessage_VariablePart();
				        	FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20);		    						
							FasUtils.sendMessageToFascor(FascorProcessor.sqlServerConnection, fullFascorMessageStr);	
	                	}	                	
	                	
			        	FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20); 
	                	CacheManager.sendFascorMessages(wdsConnection, FascorProcessor.sqlServerConnection, message_3052);
	                	
	                	ArrayList<Message> messageAL = new ArrayList<Message>(10);
	                    ArrayList<HashMap<String, Object>> part_bomAL = null;
	                	part_bomAL = JDBCUtils.runQuery(wdsConnection, "select seq_num, partnum_comp from pub.part_bom where partnum_assm = '" + partnum_assm + "' and eng_rls_num  = '" + eng_rls_num + "' order by seq_num WITH (NOLOCK)");
	                    if ((part_bomAL != null) && (part_bomAL.size() > 0)) {
		                    for (Iterator<HashMap<String, Object>>  iterator2 = part_bomAL.iterator(); iterator2.hasNext();) {
	                            HashMap<String, Object> part_bomHM = iterator2.next();

	                	        Message_3057 message_3057 = new Message_3057(wdsConnection, partnum_assm, eng_rls_num, ((Number) part_bomHM.get("seq_num")).intValue(), "A");
	                            messageAL.add(message_3057);
	                        }
		                    
				        	FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20); 		                    
	                    	CacheManager.sendFascorMessages(wdsConnection, FascorProcessor.sqlServerConnection, messageAL);			                    
	                    }						
					} catch (Exception e) {
						log.error(partnum_assm + ": Failed to build message, exception: " + e.getMessage());
						continue;
					}
					
					// --------------------------------------------------------------------------------------------
					
	            	commitCount++;
	            	if (commitCount >= JDBCUtils.BATCH_SIZE_BATCH) {
	    				JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
		                JDBCUtils.commitFascorChanges_Fascor(FascorProcessor.sqlServerConnection);
		                commitCount = 0;
	            	}

	                log.error(partnum_assm + ": Sent successfully.");
	                
				} catch (Exception e) {
					log.error(partnum_assm + ": Failed to send, exception: " + e.getMessage());
					
					noOfErrors++;
					if (noOfErrors > 1000) {
	               		log.error("More than 100 error, exiting ...");						
					
		        		try {
			                JDBCUtils.rollbackWDSChanges_Fascor(wdsConnection);
							JDBCUtils.rollbackFascorChanges_Fascor(FascorProcessor.sqlServerConnection);
							commitCount = 0;
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
	
		        		FascorProcessor.closeDBConnections();
	
		        		System.exit(0);
					}
				}
            	
            	processed++;
            	if ((processed % 1000) == 0) {
    				log.error("debug_time_fascor " + CacheManager.debug_time_fascor);
    				log.error("debug_time_wds    " + CacheManager.debug_time_wds);
            		log.error("Processed: " + processed + ", Average time taken (millis) per record: " + ((System.currentTimeMillis() - startTime) / processed));
            	}
            	
            	FascorProcessor.getDBConnections(60);
        		wdsConnection = FascorProcessor.dbConnector8.getConnectionWDS();
            }

            try {
            	if (commitCount > 0) {
    				JDBCUtils.closeOutFinalBatch_BATCH_O(FascorProcessor.sqlServerConnection);

    				JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
	                JDBCUtils.commitFascorChanges_Fascor(FascorProcessor.sqlServerConnection);
	                commitCount = 0;
            	}
			} catch (Exception e) {
        		log.error("DANGER : Failed to commit");
			}


			try {
				log.error("debug_time_fascor " + CacheManager.debug_time_fascor);
				log.error("debug_time_wds    " + CacheManager.debug_time_wds);
				 
				FascorProcessor.closeDBConnections();
			} catch (Exception e1) {
				e1.printStackTrace();
				System.exit(0);
			}

			log.error("Exiting...");
			log.error("Exiting...");
			System.exit(0);
		}
	}
}

