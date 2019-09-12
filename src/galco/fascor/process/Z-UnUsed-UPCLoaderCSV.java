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

class UPCLoaderCSV {
	private static Logger log = Logger.getLogger(PartLoader.class);

	public static void temp_main(String[] args) {
		java.text.SimpleDateFormat yyyyMMddHHmmssDisplay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		try {
		    SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

			Logger.getRootLogger().removeAppender("A1"); // stops console logging
			
			FileAppender fa = new FileAppender();
			fa.setName("FileLogger");
			  
			fa.setFile("C:\\0Ati\\ZLoadUPC-" + yyyyMMddHHmmss.format(new Date()) + ".log");
			
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
		    
		if (FascorProcessor.getDBConnections(60) == true) {
			log.error("Load starting time: " + yyyyMMddHHmmssDisplay.format(new Date()));
					
			Connection wdsConnection = FascorProcessor.dbConnector8.getConnectionWDS();
			
			ArrayList<HashMap<String, Object>> partAl = null;
	        try {
	    	    // String sqlStmt = "select part_num from pub.part as prt where part_num >= '08025KA-12P-AT-00-NMBT' order by part_num WITH (READPAST NOWAIT)";
	    	    String sqlStmt = "select part_num from pub.part where part_num <> '' order by part_num WITH (READPAST NOWAIT)";
	            partAl = JDBCUtils.runQuery(wdsConnection, sqlStmt);
		    } catch (SQLException e ) {
				e.printStackTrace();
				FascorProcessor.closeDBConnections();
				System.exit(0);
		    }

			int processed = 0;

			long startTime = System.currentTimeMillis();
			
            for (Iterator<HashMap<String, Object>> iterator = partAl.iterator(); iterator.hasNext();) {
            	String part_num = "";
            	
            	HashMap<String, Object> partHM = (HashMap<String, Object>) iterator.next();
            	part_num = ((String) partHM.get("part_num")).trim();
            	part_num = part_num.toUpperCase();

				try {
					boolean updatedUPCTable = FascorInboundMessageHandler.updateFascorUPCTable(wdsConnection, FascorProcessor.sqlServerConnection, part_num);
				} catch (Exception e) {
					log.error(part_num + ": Failed, exception: " + e.getMessage());				
					continue;
				}

                processed++;

                if ((processed % 20000) == 0) {
            		FascorProcessor.getDBConnections(20);
            		wdsConnection = FascorProcessor.dbConnector8.getConnectionWDS();
            	}
            	
            	if ((processed % 10000) == 0) {
            		log.error("Processed         " + processed + ", Average time taken (millis) per record: " + ((System.currentTimeMillis() - startTime) / processed));
            	}
            	
            	if ((processed % 500000) == 0) {
            		try {
		    			Utils.sendMailJustLogError("rajuati@yahoo.com", "WDSFascorIntegration@galco.com", "UPC load status", "Processed " + processed);						
					} catch (Exception e) {
						log.debug("Exception while sending email");
					}
            	}            	
            }

			try {
				FascorProcessor.closeDBConnections();
			} catch (Exception e1) {
				e1.printStackTrace();
				System.exit(0);
			}

			try {
    			Utils.sendMailJustLogError("rajuati@yahoo.com", "WDSFascorIntegration@galco.com", "UPC load status", "Finished processing...");						
			} catch (Exception e) {
			}

			log.error("Exiting...");
			log.error("Exiting...");
			System.exit(0);
		}
	}
}

