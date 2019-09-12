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
import galco.fascor.messages.Message_1110;
import galco.fascor.messages.Message_1110;
import galco.fascor.requests.control.FascorInboundMessageHandler;
import galco.fascor.utils.FasUtils;
import galco.fascor.wds_requests.WDSFascorRequestCommon;
import galco.portal.config.Parms;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.Contact;
import galco.portal.wds.dao.FascorMessage;

public class PartLoader {
	private static Logger log = Logger.getLogger(PartLoader.class);

	public static void main(String[] args) {
		/*
		System.out.println("Make sure that the vendor -NONE- is there.");
		System.out.println("Make sure that the vendor -NONE- is there.");
		System.out.println("Make sure that the vendor -NONE- is there.");
		System.exit(0);
		*/
		
		/* ------------------------------------------------------------------------------------------- */
		
	    SimpleDateFormat yyyyMMddHHmmssDisplay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		try {
		    SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

			Logger.getRootLogger().removeAppender("A1"); // stops console logging
			
			FileAppender fa = new FileAppender();
			fa.setName("FileLogger");
			  
			if (Parms.FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX == true) {
				fa.setFile("/home/sva0604/ZFascorProcess/ZLoadParts-" + yyyyMMddHHmmss.format(new Date()) + ".log");
			} else {
				fa.setFile("C:\\0Ati\\ZLoadParts-" + yyyyMMddHHmmss.format(new Date()) + ".log");
			}			
			
			// fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
			// fa.setLayout(new PatternLayout("%-4r [%t] %-5p %c %x - %m%n"));
			fa.setLayout(new PatternLayout("%m%n"));
			  
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
			log.error("Load starting time: " + yyyyMMddHHmmssDisplay.format(new Date()));
					
			Connection wdsConnection = FascorProcessor.dbConnector8.getConnectionWDS();
			
			ArrayList<HashMap<String, Object>> partAl = null;
	        try {
	            // String sqlStmt = "select top 10 part_num from pub.part where part_num <> '' WITH (READPAST NOWAIT)";
	            String sqlStmt = "select part_num from pub.part where part_num <> '' WITH (READPAST NOWAIT)";
	    	    // String sqlStmt = 
	            //         "select part_num from pub.part as prt where part_num <> '' and not exists " +   
	            //  	   "(select key1 from pub.FascorMessageCache as cache where cache.messageType = '1110' and cache.key1 = prt.part_num) WITH (READPAST NOWAIT)";
	            partAl = JDBCUtils.runQuery(wdsConnection, sqlStmt);
		    } catch (SQLException e ) {
				e.printStackTrace();
				FascorProcessor.closeDBConnections();
				System.exit(0);
		    }

	        int msgBuildSuccessCount_ZZ = 0, msgBuildExceptionCount_ZZ = 0, 
		        batchCommitSuccessCount_ZZ = 0, batchCommitFailureCount_ZZ = 0;
	        
			int processed = 0;
			int noOfErrors = 0;
			int commitCount = 0;
			
			long startTime = System.currentTimeMillis();
			
            for (Iterator<HashMap<String, Object>> iterator = partAl.iterator(); iterator.hasNext();) {
            	String part_num = "";
            	
            	try {
	            	HashMap<String, Object> partHM = (HashMap<String, Object>) iterator.next();
	            	part_num = ((String) partHM.get("part_num")).trim();
	            	part_num = part_num.toUpperCase();
	
	            	/*
	    	        try {
		            	ArrayList<HashMap<String, Object>> fascorMessageCacheAl = null;
		            	fascorMessageCacheAl = JDBCUtils.runQuery(wdsConnection, "select key1 from pub.FascorMessageCache where messageType = '1110' and key1 = '" + part_num + "' WITH (READPAST NOWAIT)");
		            	if ((fascorMessageCacheAl != null) && (fascorMessageCacheAl.size() == 1)) {
			                log.error(part_num + ": skipped.");
			            	continue;
		            	}
	    		    } catch (Exception e) {
						log.debug("Exception while querying FascorMessageCache", e);
	    		    }
	    		    */

					Message_1110 message_1110;
					try {
						message_1110 = new Message_1110(wdsConnection, part_num, "A");
						FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20);
		            	CacheManager.sendFascorMessages(wdsConnection, FascorProcessor.sqlServerConnection, message_1110);

		        		JDBCUtils.commitFascorChanges_Fascor(FascorProcessor.sqlServerConnection);
		                JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
		                
		                msgBuildSuccessCount_ZZ++;
					} catch (Exception e) {
						// log.error(part_num + ": Failed to build message, exception: " + e.getMessage());
						log.error("ZYX - Failure - " + part_num + " - " + e.getMessage());
						
						try {
							JDBCUtils.rollbackFascorChanges_Fascor(FascorProcessor.sqlServerConnection);
			                JDBCUtils.rollbackWDSChanges_Fascor(wdsConnection);
						} catch (Exception e2) {
							log.debug("Exception while rolling back...", e2);
						}
						
						msgBuildExceptionCount_ZZ++;

						continue;
					}

	            	commitCount++;
	            	if (commitCount >= 400) {
			        	try {
							JDBCUtils.executeStatementsThatAreInTheBatch_BATCH(JDBCUtils.BATCH_NAME_WDS, wdsConnection, true);    			
							JDBCUtils.executeStatementsThatAreInTheBatch_BATCH(JDBCUtils.BATCH_NAME_FSCR, FascorProcessor.sqlServerConnection, true);
							wdsConnection.commit();            		
							FascorProcessor.sqlServerConnection.commit();   

							log.error(" -1201ZZ- . Successfully committed a batch. ");
							
							batchCommitSuccessCount_ZZ += commitCount;
						} catch (Exception e) {
							log.error(" -1201ZZ- . Failed to process a batch. ", e);
							
							JDBCUtils.discardBatch_BATCH(JDBCUtils.BATCH_NAME_WDS, wdsConnection);
							JDBCUtils.discardBatch_BATCH(JDBCUtils.BATCH_NAME_FSCR, FascorProcessor.sqlServerConnection);
							
							batchCommitFailureCount_ZZ += commitCount;
							
							FascorProcessor.closeDBConnections();
			        		System.exit(0);							
						}
		                commitCount = 0;
		                
		            	FascorProcessor.getDBConnections(20);
		        		wdsConnection = FascorProcessor.dbConnector8.getConnectionWDS();		                
	            	}
	                
	                log.error(part_num + ": Sent successfully.");
	                
				} catch (Exception e) {
					log.error(part_num + ": Failed to send, exception: " + e.getMessage());
					
					noOfErrors++;
					if (noOfErrors > 10000) {
	               		log.error("More than 10000 errors, exiting ...");						

	               		JDBCUtils.discardBatch_BATCH(JDBCUtils.BATCH_NAME_WDS, wdsConnection);
						JDBCUtils.discardBatch_BATCH(JDBCUtils.BATCH_NAME_FSCR, FascorProcessor.sqlServerConnection);
						commitCount = 0;

						FascorProcessor.closeDBConnections();

		        		System.exit(0);
					}
				}
            	
            	processed++;
            	if ((processed % 1000) == 0) {
        			log.error("Current time      " + yyyyMMddHHmmssDisplay.format(new Date()));
    				log.error("debug_time_fascor " + CacheManager.debug_time_fascor);
    				log.error("debug_time_wds    " + CacheManager.debug_time_wds);
            		log.error("Processed         " + processed + ", Average time taken (millis) per record: " + ((System.currentTimeMillis() - startTime) / processed));
            		System.out.println("Processed         " + processed + ", Average time taken (millis) per record: " + ((System.currentTimeMillis() - startTime) / processed));
            	}
            	
            	if ((processed % 100000) == 0) {
            		try {
		    			// Utils.sendMailJustLogError("rajuati@yahoo.com", "WDSFascorIntegration@galco.com", "Part load status", "Processed " + processed);						
					} catch (Exception e) {
						log.debug("Exception while sending email");
					}
            	}            	
            }

            try {
            	if (commitCount > 0) {
		        	try {
						JDBCUtils.executeStatementsThatAreInTheBatch_BATCH(JDBCUtils.BATCH_NAME_WDS, wdsConnection, true);    			
						JDBCUtils.executeStatementsThatAreInTheBatch_BATCH(JDBCUtils.BATCH_NAME_FSCR, FascorProcessor.sqlServerConnection, true);
						wdsConnection.commit();            		
						FascorProcessor.sqlServerConnection.commit();
						
						log.error(" -1201ZZ- . Successfully committed a batch. ");		
						
						batchCommitSuccessCount_ZZ += commitCount;
					} catch (Exception e) {
						log.error(" -1201ZZ- . Failed to process a batch. ");
						
						JDBCUtils.discardBatch_BATCH(JDBCUtils.BATCH_NAME_WDS, wdsConnection);
						JDBCUtils.discardBatch_BATCH(JDBCUtils.BATCH_NAME_FSCR, FascorProcessor.sqlServerConnection);
						
						batchCommitFailureCount_ZZ += commitCount;
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

			try {
    			// Utils.sendMailJustLogError("rajuati@yahoo.com", "WDSFascorIntegration@galco.com", "Part load status", "Finished processing...");						
			} catch (Exception e) {
			}

			log.error("No. of messages that were built successfully     - " + msgBuildSuccessCount_ZZ);
			log.error("No. of messages that failed in building stage    - " + msgBuildExceptionCount_ZZ);
			log.error("No. of messages that were committed successfully - " + batchCommitSuccessCount_ZZ);
			log.error("No. of messages that failed to commit            - " + batchCommitFailureCount_ZZ);

			System.out.println("Done, exiting.");
			log.error("Done, exiting.");
			System.exit(0);
		}
	}
}

