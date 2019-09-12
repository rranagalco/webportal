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
import org.apache.log4j.SimpleLayout;
import org.h2.util.IntArray;

import galco.fascor.requests.control.FascorInboundMessageHandler;
import galco.fascor.requests.control.FascorOutboundMessageHandler;
import galco.fascor.requests.control.SKUOnHandQuantityUpdater;
import galco.fascor.utils.FasUtils;
import galco.fascor.wds_requests.WDSFascorRequestCommon;
import galco.portal.config.Parms;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.Contact;

public class FascorProcessorLinux {
	private static Logger log = Logger.getLogger(FascorProcessorLinux.class);

	static long lastTimeDBConnectionWasMade = Long.MIN_VALUE;
	
	public static DBConnector dbConnector8 = null;
	public static Connection sqlServerConnection = null;	
	static boolean dbExceptionOccurred = false;

	static final long UTC_OFFSET = TimeZone.getDefault().getOffset(System.currentTimeMillis());
	
	public static void handleDBException(Exception e, String where) {
		dbExceptionOccurred = true;
		Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Serious connection issue in FASCOR Batch Process of " + Parms.HOST_NAME, "Where: " + where + ". Exception message: " + e.getMessage());
        log.debug(e);
		lastTimeDBConnectionWasMade = System.currentTimeMillis();
	}
	public static void handleDBException(PortalException e, String where) {
		handleDBException(e.getE(), where);
	}
	
	public static boolean getDBConnections() {
		if (dbExceptionOccurred == true) {
			if (System.currentTimeMillis() <= (lastTimeDBConnectionWasMade + 300000)) {
				return false;
			}
		}
		
		if ((dbExceptionOccurred == true) || ((dbConnector8 == null) || (sqlServerConnection == null)) || (System.currentTimeMillis() >= (lastTimeDBConnectionWasMade + 1200000))) {
			dbExceptionOccurred = false;
			
			if (dbConnector8 != null) {
				try {
					dbConnector8.closeConnections();							
				} catch (PortalException e) {
					handleDBException(e, "Closing WDS Connections");
				}
			}
			try {
				dbConnector8 = new DBConnector(false);
				
				dbConnector8.getConnectionWDS().setAutoCommit(false);
				dbConnector8.getConnectionSRO().setAutoCommit(false);
				dbConnector8.getConnectionWEB().setAutoCommit(false);
			} catch (SQLException e) {
				handleDBException(e, "Getting WDS Connections");
				return false;
			} catch (PortalException e) {
				handleDBException(e, "Getting WDS Connections");
				return false;
			}
			
			if (sqlServerConnection != null) {
				try {
					sqlServerConnection.close();							
				} catch (Exception e) {
					handleDBException(e, "Closing SQL Server Connection");
				}
			}
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				sqlServerConnection = DriverManager.getConnection("jdbc:sqlserver://db10dev10.galco.com:1433;databaseName=DC01GAL;", "sa", "0ms1Bad1m{}");
				// sqlServerConnection = DriverManager.getConnection("jdbc:sqlserver://104.196.46.103:1433;databaseName=DC01GAL;", "sa", "0ms1Bad1m{}");
				sqlServerConnection.setAutoCommit(false);
			} catch (Exception e) {
				handleDBException(e, "Getting SQL Server Connections");
				return false;
			}
			
			return true;
		}

		return true;
	}

	public static void closeDBConnections() {
		if (dbConnector8 != null) {
			try {
				dbConnector8.closeConnections();							
			} catch (PortalException e) {
				handleDBException(e, "Closing WDS Connections.");
			}
		}
		
		if (sqlServerConnection != null) {
			try {
				sqlServerConnection.close();							
			} catch (Exception e) {
				handleDBException(e, "Closing SQL Server Connection.");
			}
		}
	}

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

	// Linux Batch Job
    
	public static void main(String[] args) {

		
		try {
			Logger.getRootLogger().removeAppender("A1"); // stops console logging
			
			FileAppender fa = new FileAppender();
			fa.setName("FileLogger");
			  
			fa.setFile("/home/mmm0521/fascor/logs/ZLog4j-" + yyyyMMddHHmmss.format(new Date()) + ".log");
			
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


		
		// DANGER DANGER - Don't change these, will cause infinite loop
	    JDBCUtils.UPDATE_FASCOR_DB = true;
	    JDBCUtils.UPDATE_WDS_DB = true;
	    Parms.FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX = true;

		
		while (true) {
			if (new File("/home/mmm0521/fascor/ExitIntegrationProcess.txt").exists() == true) {
				try {
					log.debug("Exiting because of exit file...");
					closeDBConnections();
					new File("/home/mmm0521/fascor/ExitIntegrationProcess.txt").delete();
				} catch (Exception e) {
					log.debug("Exception while trying to exit...", e);
				}
				System.exit(0);
			}
			
			
			if (getDBConnections() == true) {
				log.debug("Acquired DB connections successfully.");
				closeDBConnections();
				log.debug("Exiting...");
				log.debug("Exiting...");
				System.exit(0);				
				
				
				
				
				ArrayList<HashMap<String, Object>> fascorRequestsAL = null;
		        try {
		            fascorRequestsAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select sequenceNumber, processed, functionCode, data from pub.FascorRequests where processed = 'N' WITH (READPAST NOWAIT)");
		            
		            if ((fascorRequestsAL != null) && (fascorRequestsAL.size() > 0)) {
		            	FascorInboundMessageHandler.processFascorRequests(dbConnector8.getConnectionWDS(), sqlServerConnection, fascorRequestsAL);
		            } else {
		            	log.debug("Nothing in FascorRequests table to process.");
		            }
		        } catch (SQLException e ) {
			    	handleDBException(e, "While processing inbound messages - 1.");
	        	} catch (Exception e ) {
			    	handleDBException(e, "While processing inbound messages - 2.");
		    	}
			}

			
			closeDBConnections();
			log.debug("Exiting...");
			log.debug("Exiting...");
			System.exit(0);


			if (getDBConnections() == true) {
				ArrayList<HashMap<String, Object>> outboundMessagesAL = null;
		        try {
		        	outboundMessagesAL = JDBCUtils.runQuery(sqlServerConnection, "select trans, seqnbr, text from [dbo].[OutBound] WITH (READPAST) where processed = 'N' and trans in ('0135', '0310', '0210') and seqnbr > '81541'");

		            if ((outboundMessagesAL != null) && (outboundMessagesAL.size() > 0)) {
			        	FascorOutboundMessageHandler.processFascorOutboundMessages(dbConnector8.getConnectionWDS(), sqlServerConnection, outboundMessagesAL);
		            } else {
		            	log.debug("Nothing in Fascor OutBound table to process.");
		            }
		        	
		        } catch (SQLException e ) {
			    	handleDBException(e, "While processing outbound messages.");
	        	} catch (Exception e ) {
			    	handleDBException(e, "While processing outbound messages.");
		    	}
			}
			
			
			try {
				Thread.sleep(20000);			
			} catch (InterruptedException e) {
				Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Problem in Batch Process of " + Parms.HOST_NAME, "InterruptedException occurred.");
				log.debug("Thread was interrupted");
				log.debug(e);
			} catch (Exception e) {
				Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Problem in Batch Process of" + Parms.HOST_NAME, "Exception occurred while sleeping.");
				log.debug("Exception occurred.");
				log.debug(e);
			}
		}
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	
}

