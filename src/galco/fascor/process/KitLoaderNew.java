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

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.h2.util.IntArray;

import galco.fascor.cache.CacheManager;
import galco.fascor.messages.Message;
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

public class KitLoaderNew {
	private static Logger log = Logger.getLogger(KitLoaderNew.class);

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
	    
    	JDBCUtils.UPDATE_WDS_DB_IN_BATCH = true;
    	JDBCUtils.UPDATE_FASCOR_DB_IN_BATCH = true;
	    
		if (FascorProcessor.getDBConnections(60) == true) {
			Connection wdsConnection = FascorProcessor.dbConnector8.getConnectionWDS();

			ArrayList<HashMap<String, Object>> part_bomAl = null;
	        try {
													            // part_bomAl = JDBCUtils.runQuery(wdsConnection, "select top 2 partnum_assm, eng_rls_num from pub.part_bom as pbp where eng_rls_num = (select max(eng_rls_num) from pub.part_bom as pbc where pbc.partnum_assm = pbp.partnum_assm)");	            
													            // part_bomAl = JDBCUtils.runQuery(wdsConnection, "select partnum_assm, eng_rls_num from pub.part_bom as pbp where eng_rls_num = (select max(eng_rls_num) from pub.part_bom as pbc where pbc.partnum_assm = pbp.partnum_assm) order by partnum_assm");
													        	// part_bomAl = JDBCUtils.runQuery(wdsConnection, "select partnum_assm, CASE WHEN eng_rls_num = 'blank' THEN ' ' ELSE eng_rls_num END as eng_rls_num_new from pub.part_bom order by partnum_assm, eng_rls_num_new desc");
	            // part_bomAl = JDBCUtils.runQuery(wdsConnection, "select top 5 partnum_assm from pub.part_bom order by partnum_assm");
	            // part_bomAl = JDBCUtils.runQuery(wdsConnection, "select partnum_assm from pub.part_bom where partnum_assm = '410403-24AWL-NBR' order by partnum_assm");
	            // part_bomAl = JDBCUtils.runQuery(wdsConnection, "select partnum_assm from pub.part_bom where partnum_assm > 'DIB001JCYV1K12DKY1BBBXXX-DIB' order by partnum_assm");
	            part_bomAl = JDBCUtils.runQuery(wdsConnection, "select partnum_assm from pub.part_bom order by partnum_assm");

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

	                ArrayList<HashMap<String, Object>> textAl = null;
	                textAl = JDBCUtils.runQuery(wdsConnection, "select SKU from Bill_of_Material where SKU = '" + partnum_assm + "'");
					if (textAl.size() > 0) {
						continue;
					}
					
					// --------------------------------------------------------------------------------------------
					
					String eng_rls_num = Message_3052.get_eng_rls_num(wdsConnection, partnum_assm);

					try {
	                	Message_3052 message_3052 = new Message_3052(wdsConnection, partnum_assm, eng_rls_num, "A");
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
	                    
		        		JDBCUtils.commitFascorChanges_Fascor(FascorProcessor.sqlServerConnection);
		                JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
					} catch (Exception e) {
						log.error(partnum_assm + ": Failed to build message, exception: " + e.getMessage());
						
						try {
							JDBCUtils.rollbackFascorChanges_Fascor(FascorProcessor.sqlServerConnection);
			                JDBCUtils.rollbackWDSChanges_Fascor(wdsConnection);
						} catch (Exception e2) {
							log.debug("Exception while rolling back...", e2);
						}
						
						continue;
					}
					
					// --------------------------------------------------------------------------------------------
					
	            	commitCount++;
	            	if (commitCount >= 300) {
			        	try {
							JDBCUtils.executeStatementsThatAreInTheBatch_BATCH(JDBCUtils.BATCH_NAME_WDS, wdsConnection, true);    			
							JDBCUtils.executeStatementsThatAreInTheBatch_BATCH(JDBCUtils.BATCH_NAME_FSCR, FascorProcessor.sqlServerConnection, true);
							wdsConnection.commit();            		
							FascorProcessor.sqlServerConnection.commit();            									
						} catch (Exception e) {
							log.error(" -1201ZZ- . Failed to process a batch. ", e);
							
							JDBCUtils.discardBatch_BATCH(JDBCUtils.BATCH_NAME_WDS, wdsConnection);
							JDBCUtils.discardBatch_BATCH(JDBCUtils.BATCH_NAME_FSCR, FascorProcessor.sqlServerConnection);
							
							FascorProcessor.closeDBConnections();
			        		System.exit(0);
						}
		                commitCount = 0;
		                
		            	FascorProcessor.getDBConnections(60);
		        		wdsConnection = FascorProcessor.dbConnector8.getConnectionWDS();		                
	            	}

	                log.error(partnum_assm + ": Sent successfully.");
	                
				} catch (Exception e) {
					log.error(partnum_assm + ": Failed to send, exception: " + e.getMessage());
					
					noOfErrors++;
					if (noOfErrors > 1000) {
	               		log.error("More than 1000 errors, exiting ...");						

	               		JDBCUtils.discardBatch_BATCH(JDBCUtils.BATCH_NAME_WDS, wdsConnection);
						JDBCUtils.discardBatch_BATCH(JDBCUtils.BATCH_NAME_FSCR, FascorProcessor.sqlServerConnection);
						commitCount = 0;

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
            }

            try {
            	if (commitCount > 0) {
		        	try {
						JDBCUtils.executeStatementsThatAreInTheBatch_BATCH(JDBCUtils.BATCH_NAME_WDS, wdsConnection, true);    			
						JDBCUtils.executeStatementsThatAreInTheBatch_BATCH(JDBCUtils.BATCH_NAME_FSCR, FascorProcessor.sqlServerConnection, true);
						wdsConnection.commit();
						FascorProcessor.sqlServerConnection.commit();
					} catch (Exception e) {
						log.error(" -1201ZZ- . Failed to process a batch. ");
						
						JDBCUtils.discardBatch_BATCH(JDBCUtils.BATCH_NAME_WDS, wdsConnection);
						JDBCUtils.discardBatch_BATCH(JDBCUtils.BATCH_NAME_FSCR, FascorProcessor.sqlServerConnection);
					}
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

