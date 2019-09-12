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
import galco.fascor.messages.Message_0140;
import galco.fascor.messages.Message_1110;
import galco.fascor.requests.control.FascorInboundMessageHandler;
import galco.fascor.utils.FasUtils;
import galco.fascor.wds_requests.WDSFascorRequestCommon;
import galco.portal.config.Parms;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;
import galco.portal.wds.dao.Contact;
import galco.portal.wds.dao.FascorMessage;

public class VendorLoader {
	private static Logger log = Logger.getLogger(VendorLoader.class);

	public static void main(String[] args) {
		try {
		    SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

			Logger.getRootLogger().removeAppender("A1"); // stops console logging
			
			FileAppender fa = new FileAppender();
			fa.setName("FileLogger");


			if (Parms.FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX == true) {
				fa.setFile("/home/sva0604/ZFascorProcess/ZLoadVendors-" + yyyyMMddHHmmss.format(new Date()) + ".log");
			} else {
				fa.setFile("C:\\0Ati\\ZLoadVendors-" + yyyyMMddHHmmss.format(new Date()) + ".log");				
			}
			
			// fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
			fa.setLayout(new PatternLayout("%-4r [%t] %-5p %c %x - %m%n"));
			  
			fa.setThreshold(Level.DEBUG);
			fa.setAppend(true);
			fa.activateOptions();
			
			//add appender to any Logger (here is root)
			Logger.getRootLogger().addAppender(fa);
		} catch (Exception e) {
			System.out.println("Exception while trying to set log4j properties.");
			e.printStackTrace();
			System.exit(0);
		}
		
		System.out.println("Subb - Starting ...");

		JDBCUtils.UPDATE_FASCOR_DB = true;
	    JDBCUtils.UPDATE_WDS_DB = true;
	    
    	JDBCUtils.UPDATE_WDS_DB_IN_BATCH = true;
    	JDBCUtils.UPDATE_FASCOR_DB_IN_BATCH = true;
	    
		if (FascorProcessor.getDBConnections(60) == true) {
			Connection wdsConnection = FascorProcessor.dbConnector8.getConnectionWDS();
			
			ArrayList<HashMap<String, Object>> vendorAl = null;
	        try {
	        											// vendorAl = JDBCUtils.runQuery(wdsConnection, "select top 1 vendor_num from pub.vendor");
	            										// vendorAl = JDBCUtils.runQuery(wdsConnection, "select vendor_num from pub.vendor where vendor_num = '12736' WITH (READPAST NOWAIT)");
	            // vendorAl = JDBCUtils.runQuery(wdsConnection, "select top 10 vendor_num from pub.vendor WITH (READPAST NOWAIT)");
	            vendorAl = JDBCUtils.runQuery(wdsConnection, "select vendor_num from pub.vendor WITH (READPAST NOWAIT)");
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
			
            for (Iterator<HashMap<String, Object>> iterator = vendorAl.iterator(); iterator.hasNext();) {
            	String vendor_num = "";
            	
            	try {
	            	HashMap<String, Object> partHM = (HashMap<String, Object>) iterator.next();
					vendor_num = ((String) partHM.get("vendor_num")).trim();
					vendor_num = vendor_num.toUpperCase();
	
					log.debug("Subb - vendor_num " + vendor_num);

					Message_0140 message_0140;
					try {
						FasUtils.trackTimeS();
						message_0140 = new Message_0140(wdsConnection, vendor_num, "A");	
						log.error("ZYX - Message build took " + FasUtils.trackTimeE());

						FasUtils.trackTimeS();
						FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20);
		            	CacheManager.sendFascorMessages(wdsConnection, FascorProcessor.sqlServerConnection, message_0140);
						log.error("ZYX - Message sending took " + FasUtils.trackTimeE());
		            	
						FasUtils.trackTimeS();
		        		JDBCUtils.commitFascorChanges_Fascor(FascorProcessor.sqlServerConnection);
		                JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
						log.error("ZYX - Message commit took " + FasUtils.trackTimeE());
		                
		                msgBuildSuccessCount_ZZ++;
					} catch (Exception e) {
						log.error(vendor_num + ": Failed to build message, exception: " + e.getMessage());

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
							FasUtils.trackTimeS();
							JDBCUtils.executeStatementsThatAreInTheBatch_BATCH(JDBCUtils.BATCH_NAME_WDS, wdsConnection, true);    			
							JDBCUtils.executeStatementsThatAreInTheBatch_BATCH(JDBCUtils.BATCH_NAME_FSCR, FascorProcessor.sqlServerConnection, true);
							wdsConnection.commit();            		
							FascorProcessor.sqlServerConnection.commit();     
							log.error("ZYX - Batch commit took " + FasUtils.trackTimeE());
							
							batchCommitSuccessCount_ZZ += commitCount;
						} catch (Exception e) {
							log.error(" -1201ZZ- . Failed to process a batch. ");
							
							JDBCUtils.discardBatch_BATCH(JDBCUtils.BATCH_NAME_WDS, wdsConnection);
							JDBCUtils.discardBatch_BATCH(JDBCUtils.BATCH_NAME_FSCR, FascorProcessor.sqlServerConnection);
							
							batchCommitFailureCount_ZZ += commitCount;
						}
		                commitCount = 0;
		                
		            	FascorProcessor.getDBConnections(20);
		        		wdsConnection = FascorProcessor.dbConnector8.getConnectionWDS();		                
	            	}

	                log.error(vendor_num + ": Sent successfully.");
	                
				} catch (Exception e) {
					log.error(vendor_num + ": Failed to send, exception: " + e.getMessage());
					
					noOfErrors++;
					if (noOfErrors > 500) {
	               		log.error("More than 500 errors, exiting ...");						
					
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

			log.error("No. of messages that were built successfully     - " + msgBuildSuccessCount_ZZ);
			log.error("No. of messages that failed in building stage    - " + msgBuildExceptionCount_ZZ);
			log.error("No. of messages that were committed successfully - " + batchCommitSuccessCount_ZZ);
			log.error("No. of messages that failed to commit            - " + batchCommitFailureCount_ZZ);
	        
			System.out.println("Subb - Done");
			log.error("Subb - Done");
			System.exit(0);
		}
	}
}

