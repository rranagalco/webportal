package galco.fascor.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.swing.JOptionPane;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.SimpleLayout;
import org.h2.util.IntArray;

import galco.fascor.bin_change.ChangeBin;
import galco.fascor.cache.CacheManager;
import galco.fascor.messages.Message;
import galco.fascor.messages.Message_0140;
import galco.fascor.messages.Message_1110;
import galco.fascor.messages.Message_1210;
import galco.fascor.messages.Message_1220;
import galco.fascor.messages.Message_1230;
import galco.fascor.messages.Message_1420;
import galco.fascor.messages.Message_3052;
import galco.fascor.messages.Message_3057;
import galco.fascor.requests.control.FascorInboundMessageHandler;
import galco.fascor.requests.control.FascorOutboundMessageHandler;
import galco.fascor.requests.control.NightlyBinUpdater;
import galco.fascor.requests.control.NightlyBordItemSyncJob;
import galco.fascor.requests.control.SKUOnHandQuantityUpdater;
import galco.fascor.utils.FasUtils;
import galco.fascor.wds_requests.WDSFascorRequestCommon;
import galco.portal.config.Parms;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.Contact;

public class FascorProcessor {
	private static Logger log = Logger.getLogger(FascorProcessor.class);

	static long lastTimeDBConnectionWasMade = Long.MIN_VALUE;

	public static DBConnector dbConnector8 = null;
	public static Connection sqlServerConnection = null;
	static boolean dbExceptionOccurred = false;

	static final long UTC_OFFSET = TimeZone.getDefault().getOffset(System.currentTimeMillis());

	public static boolean fascorDidNotProcessInboundMessage = false;
	public static long lastTimeAtWhichFascorFailedToProcessInboundTableMessage = 0;

	public static void handleDBException(Exception e, String where) {
		dbExceptionOccurred = true;
		Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Serious connection issue in FASCOR Batch Process of " + Parms.HOST_NAME, "Where: " + where + ". Exception message: " + e.getMessage());
        log.debug(e);
		lastTimeDBConnectionWasMade = System.currentTimeMillis();
	}
	public static void handleDBException(PortalException e, String where) {
		handleDBException(e.getE(), where);
	}

	// ---------------------------------------------------------------------------------------------------------

	public static boolean getDBConnections() {
		return getDBConnections(20);
	}

	public static boolean getDBConnections(int openNewConnectionEveryThisManyMinutes) {
		if (Parms.WARN_ABOUT_RUNNING_AGAINST_PRODUCTION == true) {
			String osName = System.getProperty("os.name");
			if (osName.indexOf("Windows") >= 0) {
				if ((Parms.SERVER_NAME.indexOf("app1") >= 0) || (Parms.FASCOR_DATABASE_NAME.indexOf("DC01GAL") >= 0)) {
					Parms.WARN_ABOUT_RUNNING_AGAINST_PRODUCTION = false;

					Object[] possibilities = {"0 - RUN AGAINST PRODUCTION", "1 - No, exit."};
					int OPTION = JOptionPane.showOptionDialog(null, "Are you sure you want to connect to PRODUCTION?","FascorQueriesExecutor", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, possibilities, possibilities[0]);
		        	// log.error(OPTION);

		        	if (OPTION != 0) {
		        		log.error("Exiting as requested...");
		        		System.exit(0);
		        	}
				}
			}
		}

		int openNewConnectionEveryThisManyMilliSeconds = openNewConnectionEveryThisManyMinutes * 60000;

		if (dbExceptionOccurred == true) {
			if (System.currentTimeMillis() <= (lastTimeDBConnectionWasMade + 300000)) {
				return false;
			}
		}

		if ((dbExceptionOccurred == true) || ((dbConnector8 == null) || (sqlServerConnection == null)) || (System.currentTimeMillis() >= (lastTimeDBConnectionWasMade + openNewConnectionEveryThisManyMilliSeconds))) {
			log.error("Opening new database connections.");

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
															// Older stuff		// sqlServerConnection = DriverManager.getConnection("jdbc:sqlserver://104.196.46.103:1433;databaseName=DC01GAL;", "sa", "0ms1Bad1m{}");
										// sqlServerConnection = DriverManager.getConnection("jdbc:sqlserver://db10dev10.galco.com:1433;databaseName=DC01GAL;", "sa", "0ms1Bad1m{}");
						
															// Didn't work	sqlServerConnection = DriverManager.getConnection("jdbc:sqlserver://az-fascor-adb1.galco.com:1433;databaseName=DC01GAL;", "sa", "0ms1Bad1m{}");
										// THE FOLLOWING WORKED
										// sqlServerConnection = DriverManager.getConnection("jdbc:sqlserver://Az-fascor-db1:1433;databaseName=DC01GAL;", "sa", "0ms1Bad1m{}");

				// sqlServerConnection = DriverManager.getConnection("jdbc:sqlserver://Az-fascor-db1:1433;databaseName=" + Parms.FASCOR_DATABASE_NAME + ";", "sa", "0ms1Bad1m{}");
				sqlServerConnection = DriverManager.getConnection("jdbc:sqlserver://" + Parms.FASCOR_SERVER_NAME + ":1433;databaseName=" + Parms.FASCOR_DATABASE_NAME + ";", "sa", "0ms1Bad1m{}");

				sqlServerConnection.setAutoCommit(false);
			} catch (Exception e) {
				handleDBException(e, "Getting SQL Server Connections");
				return false;
			}

			lastTimeDBConnectionWasMade = System.currentTimeMillis();

			return true;
		}

		return true;
	}

	// ---------------------------------------------------------------------------------------------------------

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

	public static WDSFascorRequestCommon[] createFascorRequests(Connection wdsConnection, ArrayList<String> messageTypeIndAl, ArrayList<String> dataAl) throws SQLException {
		WDSFascorRequestCommon[] wdsFascorRequestCommonArray = new WDSFascorRequestCommon[messageTypeIndAl.size()];

		int processed = 0;

		for (int i = 0; i < messageTypeIndAl.size(); i++) {
			String messageType;
			String msgTypeORMessageTypeInd = messageTypeIndAl.get(i);

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

			wdsFascorRequestCommonArray[i] = FasUtils.createFascorRequest(wdsConnection, messageType, dataAl.get(i));

			processed++;
			if ((processed % 100) == 0) {
		        JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
			}
		}

        JDBCUtils.commitWDSChanges_Fascor(wdsConnection);

        return wdsFascorRequestCommonArray;
	}

	public static WDSFascorRequestCommon[] createFascorRequests(Connection wdsConnection, String[][] msgTypeORMessageTypeInd_data_array) throws SQLException {
		WDSFascorRequestCommon[] wdsFascorRequestCommonArray = new WDSFascorRequestCommon[msgTypeORMessageTypeInd_data_array[0].length];

		for (int i = 0; i < msgTypeORMessageTypeInd_data_array[0].length; i++) {
			String messageType;
			String msgTypeORMessageTypeInd = msgTypeORMessageTypeInd_data_array[0][i];

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

			wdsFascorRequestCommonArray[i] = FasUtils.createFascorRequest(wdsConnection, messageType, msgTypeORMessageTypeInd_data_array[1][i]);

			if ((i % 100) == 0) {
		        JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
			}
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

	// ---------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------

	public static boolean isVendorUploadHappening(Connection wdsConnection) {
		boolean uploadIsHappening = false;
		ArrayList<HashMap<String, Object>> vendorUploadAL = null;
		try {
			vendorUploadAL = JDBCUtils.runQuery(wdsConnection, "select top 1 sequenceNumber from pub.FascorRequests where processed = 'V' WITH (NOLOCK)");
		} catch (SQLException e) {
			log.error(" -1201ZZ- . Exception while executing select top 1 sequenceNumber from pub.FascorRequests where processed = 'V' ....", e);
			e.printStackTrace();
			return false;
		}

		if ((vendorUploadAL != null) && (vendorUploadAL.size() > 0)) {
			log.debug("Vendor upload is happening -1201ZZ- ");
			uploadIsHappening = true;
		}

		return uploadIsHappening;
	}

	public static int getUnprocessedInboundMessageCount(Connection sqlServerConnection) {
		long queryStartTime = System.currentTimeMillis();

		int unprocessedInbound = 0;
		ArrayList<HashMap<String, Object>> unprocessedInboundAL = null;
		// unprocessedInboundAL = JDBCUtils.runQuery(sqlServerConnection, "select top 1 Processed from InBound where Processed = 'N'");
		try {
			unprocessedInboundAL = JDBCUtils.runQuery(sqlServerConnection, "select count(*) as count from InBound where Processed = 'N'");
		} catch (SQLException e) {
			log.error(" -1201ZZ- . Exception while executing select count(*) as count from InBound where Processed ....", e);
			e.printStackTrace();
			return 0;
		}
		
		if ((unprocessedInboundAL != null) && (unprocessedInboundAL.size() > 0)) {
			unprocessedInbound = ((Number) (unprocessedInboundAL.get(0).get("count"))).intValue();
		}

		log.debug("Time taken by unprocessedInbound query (in Millis): " + (System.currentTimeMillis() - queryStartTime));

		return unprocessedInbound;
	}

	public static ArrayList<HashMap<String, Object>> buildVendorUploadMessages(Connection wdsConnection, Connection sqlServerConnection) {
		ArrayList<HashMap<String, Object>> fascorRequestsAL;
		try {
			fascorRequestsAL = JDBCUtils.runQuery(wdsConnection, "select top 300 sequenceNumber, processed, functionCode, data, \"key\" from pub.FascorRequests where processed = 'V' order by sequenceNumber WITH (READPAST NOWAIT)");
		} catch (SQLException e) {
			log.error(" -1201ZZ- . Exception while executing select top 300 sequenceNumber, processed, functionCode ....", e);
			return null;			
		}

		if ((fascorRequestsAL != null) && (fascorRequestsAL.size() > 0)) {
			JDBCUtils.UPDATE_WDS_DB_IN_BATCH = true;
			JDBCUtils.UPDATE_FASCOR_DB_IN_BATCH = true;

			FascorInboundMessageHandler.processFascorRequests(wdsConnection, sqlServerConnection, fascorRequestsAL);

			JDBCUtils.UPDATE_WDS_DB_IN_BATCH = false;
			JDBCUtils.UPDATE_FASCOR_DB_IN_BATCH = false;

			return fascorRequestsAL;
		} else {
			return null;
		}
	}

	public static void executeBatch(Connection wdsConnection, Connection sqlServerConnection, ArrayList<HashMap<String, Object>> vendorUploadFascorRequestsAL) {
		JDBCUtils.UPDATE_WDS_DB_IN_BATCH = true;
		JDBCUtils.UPDATE_FASCOR_DB_IN_BATCH = true;

		try {
			JDBCUtils.executeStatementsThatAreInTheBatch_BATCH(JDBCUtils.BATCH_NAME_WDS, wdsConnection, true);
			JDBCUtils.executeStatementsThatAreInTheBatch_BATCH(JDBCUtils.BATCH_NAME_FSCR, sqlServerConnection, true);
			
			wdsConnection.commit();            		
			sqlServerConnection.commit();            		
		} catch (Exception e) {
			log.error(" -1201ZZ- . Failed to process a batch. ");

			JDBCUtils.discardBatch_BATCH(JDBCUtils.BATCH_NAME_WDS, wdsConnection);
			JDBCUtils.discardBatch_BATCH(JDBCUtils.BATCH_NAME_FSCR, sqlServerConnection);

			for (Iterator<HashMap<String, Object>> iterator = vendorUploadFascorRequestsAL.iterator(); iterator.hasNext();) {
				HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();
				log.error(" -1201ZZ- . Failed sequence number: " + hashMap.get("sequenceNumber"));
			}
		}

		JDBCUtils.UPDATE_WDS_DB_IN_BATCH = false;
		JDBCUtils.UPDATE_FASCOR_DB_IN_BATCH = false;
	}

	// ---------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------

    static final SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
    static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

	public static void main(String[] args) {

		/*
		try {
			String localHost = InetAddress.getLocalHost().toString();

			if ((localHost.indexOf(Parms.SERVER_NAME) < 0) && (localHost.indexOf("60950-d-mh") < 0)) {
				System.out.println("Running on wrong server, Parms.SERVER_NAME " + Parms.SERVER_NAME + ", local host: " + localHost);
				System.exit(0);
			}

			if ((Parms.SERVER_NAME.indexOf("app1") >= 0) && (Parms.FASCOR_DATABASE_NAME.indexOf("01") < 0)) {
				System.out.println("Connecting to wrong Fascor server, Parms.SERVER_NAME " + Parms.SERVER_NAME + ", Parms.FASCOR_DATABASE_NAME: " + Parms.FASCOR_DATABASE_NAME);
				System.exit(0);
			}

			if ((Parms.SERVER_NAME.indexOf("app3") >= 0) && (Parms.FASCOR_DATABASE_NAME.indexOf("99") < 0)) {
				System.out.println("Connecting to wrong Fascor server, Parms.SERVER_NAME " + Parms.SERVER_NAME + ", Parms.FASCOR_DATABASE_NAME: " + Parms.FASCOR_DATABASE_NAME);
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		*/
		
		// =====================================================================================================
		// =====================================================================================================

		{ 	String osName = System.getProperty("os.name");
			if ((osName.indexOf("Windows") >= 0) && (Parms.FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX == true)) {
				log.debug("Running on Windows, but  FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX is true, exiting ...");
				System.out.println("Running on Windows, but  FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX is true, exiting ...");
				System.exit(0);
			}
			if ((osName.indexOf("Windows") < 0) && (Parms.FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX == false)) {
				log.debug("Running on Linux, but  FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX is false, exiting ...");
				System.out.println("Running on Linux, but  FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX is false, exiting ...");
				System.exit(0);
			}
		}

		// =====================================================================================================
		// =====================================================================================================
		
		if (Parms.FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX == true) {
			if (new File(Parms.FASCOR_PROCESSOR_SINGLE_INSTANCE_FILE).exists() == true) {
				log.debug("Exiting because another instance of this process is already running ...");
				System.out.println("Exiting because another instance of this process is already running ...");
				System.exit(0);
			}

			try {
				new FileOutputStream(Parms.FASCOR_PROCESSOR_SINGLE_INSTANCE_FILE).close();
			} catch (Exception e) {
				System.out.println("Exception while trying to create file " + Parms.FASCOR_PROCESSOR_SINGLE_INSTANCE_FILE + "  ...");
				log.debug("Exception while trying to create file " + Parms.FASCOR_PROCESSOR_SINGLE_INSTANCE_FILE + "  ...", e);
				System.exit(0);
			}
		}

		// =====================================================================================================
		// =====================================================================================================

		try {
			// PropertyConfigurator.configure("/home/sva0604/ZFascorProcess/log4j.properties");

			Logger.getRootLogger().removeAppender("A1"); // stops console logging

			FileAppender fa = new FileAppender();
			fa.setName("FileLogger");

			if (Parms.FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX == true) {
				fa.setFile("/home/sva0604/ZFascorProcess/logs/ZLog4j-" + yyyyMMddHHmmss.format(new Date()) + ".log");
				// fa.setFile("/home/sva0604/ZFascorProcess/logs/ZLog4j-" + yyyyMMdd.format(new Date()) + ".log");
			} else {
				fa.setFile("C:\\0Ati\\ZLog4j-" + yyyyMMddHHmmss.format(new Date()) + ".log");
			}

			// fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
			// fa.setLayout(new PatternLayout("%-4r [%t] %-5p %c %x - %m%n"));
			// fa.setLayout(new PatternLayout("%d{dd MMM yyyy HH:mm:ss,SSS} %-4r [%t] %-5p %c{1} %x - %m%n"));
			fa.setLayout(new PatternLayout("%d{dd MMM yyyy HH:mm:ss,SSS} %-5p %c{1} %x - %m%n"));
			
			fa.setThreshold(Level.DEBUG);
			fa.setAppend(true);
			fa.activateOptions();

			//add appender to any Logger (here is root)
			Logger.getRootLogger().addAppender(fa);

			if (Parms.FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX == true) {
				for (int i = 0; i < 10; i++) {
					log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				}
			}
		} catch (Exception e) {
			System.out.println("Exception while trying to set log4j properties.");
			e.printStackTrace();
			System.exit(0);
		}

		// =====================================================================================================
		// =====================================================================================================

		int whichMessageHandlerToRun = 0;
			final int RUN_INBOUND_MESSAGE_HANDLER = 0;
			final int RUN_OUTBOUND_MESSAGE_HANDLER = 1;
			final int RUN_NIGHTLY_QUANTITY_UPDATE_PROCESS = 2;
			final int ONE_TIME_UPDATE_PROCESS = 3;

		// qqq

		if (Parms.FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX == true) {
			whichMessageHandlerToRun = Utils.setBit(whichMessageHandlerToRun, RUN_INBOUND_MESSAGE_HANDLER);
			whichMessageHandlerToRun = Utils.setBit(whichMessageHandlerToRun, RUN_OUTBOUND_MESSAGE_HANDLER);
			whichMessageHandlerToRun = Utils.setBit(whichMessageHandlerToRun, RUN_NIGHTLY_QUANTITY_UPDATE_PROCESS);
			// whichMessageHandlerToRun = Utils.setBit(whichMessageHandlerToRun, ONE_TIME_UPDATE_PROCESS);

			// DANGER DANGER - Don't change these, will cause infinite loop
			JDBCUtils.UPDATE_FASCOR_DB = true;
		    JDBCUtils.UPDATE_WDS_DB = true;
		} else {
			// whichMessageHandlerToRun = Utils.setBit(whichMessageHandlerToRun, RUN_INBOUND_MESSAGE_HANDLER);
			// whichMessageHandlerToRun = Utils.setBit(whichMessageHandlerToRun, RUN_OUTBOUND_MESSAGE_HANDLER);
			// whichMessageHandlerToRun = Utils.setBit(whichMessageHandlerToRun, RUN_NIGHTLY_QUANTITY_UPDATE_PROCESS);
			whichMessageHandlerToRun = Utils.setBit(whichMessageHandlerToRun, ONE_TIME_UPDATE_PROCESS);

		    JDBCUtils.UPDATE_FASCOR_DB = true;
		    JDBCUtils.UPDATE_WDS_DB = true;
		    // JDBCUtils.UPDATE_FASCOR_DB = false;
		    // JDBCUtils.UPDATE_WDS_DB = false;
		}

		// =====================================================================================================
		// =====================================================================================================

		/*
		if (true) {
			System.out.println("Entered just fine, exiting right now...");
			log.debug("Entered just fine, exiting right now...");
			System.exit(0);
		}
		*/

		// =====================================================================================================
		// =====================================================================================================

		long totalNoOfProcessedRecords = 0;

		boolean vendorUploadIsHappening = false;
		boolean messagesForABatchAreAlreadyBuilt = false;
		ArrayList<HashMap<String, Object>> vendorUploadFascorRequestsAL = null;
		long lastVendorUploadSpottedTime = -1;
		long processingStartTime = System.currentTimeMillis();

		while (true) {
			log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			log.debug("FascorProcessor is beginning another iterarion, time: " + yyyyMMddHHmmss.format(new Date()) + " .....");
			log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

			// =====================================================================================================
			// =====================================================================================================

			if (Parms.FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX == true) {
				if (new File(Parms.FASCOR_PROCESSOR_TERMINATION_SIGNAL_FILE).exists() == true) {
					try {
						if ((messagesForABatchAreAlreadyBuilt == true) || (vendorUploadIsHappening == true)) {
							log.debug("Vendor upload is happening, will exit as soon as it is done...");							
						} else {
							log.debug("Exiting because of termination signal file ...");
							closeDBConnections();
	
							new File(Parms.FASCOR_PROCESSOR_TERMINATION_SIGNAL_FILE).delete();
							new File(Parms.FASCOR_PROCESSOR_SINGLE_INSTANCE_FILE).delete();
						}
					} catch (Exception e) {
						log.debug("Exception while trying to exit...", e);
					}
					System.exit(0);
				}
			}

			// =====================================================================================================
			// =====================================================================================================

			if (messagesForABatchAreAlreadyBuilt == false) {
				if (getDBConnections() == false) {
					log.error("Failed to get database connections");
					// Wait 2 minites and try again
					Utils.sleep(120000);	
					continue;
				}
			}
			
			// =====================================================================================================
			// =====================================================================================================

			if (Utils.getBit(whichMessageHandlerToRun, RUN_INBOUND_MESSAGE_HANDLER)) {
		        try {

					// =====================================================================================================
					// =====================================================================================================
					// =====================================================================================================

							        	// fascorRequestsAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select sequenceNumber, processed, functionCode, data, key from pub.FascorRequests where processed = 'A' and functionCode = '1110C' and sequenceNumber <= '2018112806173034531075' order by sequenceNumber WITH (READPAST NOWAIT)");
							        	// fascorRequestsAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select sequenceNumber, processed, functionCode, data, key from pub.FascorRequests where processed = 'A' order by sequenceNumber WITH (READPAST NOWAIT)");
							        	// fascorRequestsAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select top 100 sequenceNumber, processed, functionCode, data, key from pub.FascorRequests where processed = 'B' and functionCode <> '1110C' order by sequenceNumber WITH (READPAST NOWAIT)");
		        						// fascorRequestsAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select sequenceNumber, processed, functionCode, data, key from pub.FascorRequests where processed = 'N' order by sequenceNumber WITH (READPAST NOWAIT)");

										// fascorRequestsAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select sequenceNumber, processed, functionCode, data, key from pub.FascorRequests where ((processed = 'A') OR (processed = 'B') OR (processed = 'C') OR (processed = 'D')) order by sequenceNumber WITH (READPAST NOWAIT)");

		    							// String yyyyMmDdHmMmSs = FasUtils.convertDateTo_yyyyMmDdHmMmSs(FasUtils.addMinutes(new Date(), 1));

		        	JDBCUtils.UPDATE_WDS_DB_IN_BATCH = false;
		        	JDBCUtils.UPDATE_FASCOR_DB_IN_BATCH = false;

		        	String yyyyMmDdHmMmSs = FasUtils.convertDateTo_yyyyMmDdHmMmSs(new Date());

		        	
		        	
		        												// ArrayList<HashMap<String, Object>> fascorRequestsAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select sequenceNumber, processed, functionCode, data, \"key\" from pub.FascorRequests where processed = 'N' and functionCode <> '9999C' and sequenceNumber <= '" + yyyyMmDdHmMmSs + "' order by sequenceNumber WITH (READPAST NOWAIT)");
																// ArrayList<HashMap<String, Object>> fascorRequestsAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select sequenceNumber, processed, functionCode, data, \"key\" from pub.FascorRequests where sequenceNumber = '201906241105124323406288166' order by sequenceNumber WITH (READPAST NOWAIT)");
																// ArrayList<HashMap<String, Object>> fascorRequestsAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select sequenceNumber, processed, functionCode, data, \"key\" from pub.FascorRequests where functionCode = '9999C' order by sequenceNumber WITH (READPAST NOWAIT)");

		        						// ArrayList<HashMap<String, Object>> fascorRequestsAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select top 500 sequenceNumber, processed, functionCode, data, \"key\" from pub.FascorRequests where processed = 'N' and sequenceNumber <= '" + yyyyMmDdHmMmSs + "' order by sequenceNumber WITH (READPAST NOWAIT)");

					// qqq
		        	ArrayList<HashMap<String, Object>> fascorRequestsAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select sequenceNumber, processed, functionCode, data, \"key\" from pub.FascorRequests where processed = 'N' and sequenceNumber <= '" + yyyyMmDdHmMmSs + "' order by sequenceNumber WITH (READPAST NOWAIT)");
					
					if ((fascorRequestsAL != null) && (fascorRequestsAL.size() > 0)) {
		            	FascorInboundMessageHandler.processFascorRequests(dbConnector8.getConnectionWDS(), sqlServerConnection, fascorRequestsAL);

		            	totalNoOfProcessedRecords += fascorRequestsAL.size();
		            	// System.out.println("Total no. of processed records: " + totalNoOfProcessedRecords);
		            	log.debug("Total no. of processed records: " + totalNoOfProcessedRecords);
		            } else {
		            	// System.out.println("Nothing in FascorRequests table to process.");
		            	log.debug("Nothing in FascorRequests table, of type 'N', to process.");
		            }

					// =====================================================================================================
					// =====================================================================================================
					// =====================================================================================================

					if (messagesForABatchAreAlreadyBuilt == true) {
						int unprocessedInbound = getUnprocessedInboundMessageCount(sqlServerConnection);
			        	if (unprocessedInbound < 50) {
							executeBatch(dbConnector8.getConnectionWDS(), sqlServerConnection, vendorUploadFascorRequestsAL);
							messagesForABatchAreAlreadyBuilt = false;

							totalNoOfProcessedRecords += vendorUploadFascorRequestsAL.size();
			            	log.debug("Total no. of processed records for vendor upload: " + totalNoOfProcessedRecords);
			            	log.debug("Time taken, in millis, per record: " + (System.currentTimeMillis() - processingStartTime) / totalNoOfProcessedRecords);
			        	}
					} else {
						vendorUploadIsHappening = isVendorUploadHappening(dbConnector8.getConnectionWDS());
						if (vendorUploadIsHappening == true) {
							if (lastVendorUploadSpottedTime < 0) {
								lastVendorUploadSpottedTime = System.currentTimeMillis();
							}
							
							vendorUploadFascorRequestsAL = buildVendorUploadMessages(dbConnector8.getConnectionWDS(), sqlServerConnection);
							if (vendorUploadFascorRequestsAL != null) {
								messagesForABatchAreAlreadyBuilt = true;
							}
						} else {
							lastVendorUploadSpottedTime = -1;									
			            	log.debug("Nothing in FascorRequests table, of type 'V', to process.");								
						}
					}

					// =====================================================================================================
					// =====================================================================================================
					// =====================================================================================================

	        	} catch (Exception e ) {
			    	handleDBException(e, "While processing inbound messages - 2.");
		    	}
			}

			// =====================================================================================================
			// =====================================================================================================

			if (Utils.getBit(whichMessageHandlerToRun, RUN_OUTBOUND_MESSAGE_HANDLER)) {
				ArrayList<HashMap<String, Object>> outboundMessagesAL = null;
		        try {
		        										// outboundMessagesAL = JDBCUtils.runQuery(sqlServerConnection, "select trans, seqnbr, text from [dbo].[OutBound] WITH (READPAST) where processed = 'N' and trans in ('0135', '0310')");

											        	// String processThis_seqnbr_Only = "122136";
											        	// outboundMessagesAL = JDBCUtils.runQuery(sqlServerConnection, "select trans, seqnbr, text from [dbo].[OutBound] WITH (READPAST) where processed = 'N' and trans in ('0135', '0310', '0210') and seqnbr = '" + processThis_seqnbr_Only + "'");



											        	// outboundMessagesAL = JDBCUtils.runQuery(sqlServerConnection, "select trans, seqnbr, text from [dbo].[OutBound] WITH (READPAST) where processed = 'N' and trans in ('0135', '0310') and seqnbr in ('78757')");


					        	// Testing
					        	// HashMap<String, Object> testHM = new HashMap<String, Object>();
		                        // testHM.put("trans", "0135");
		                        // testHM.put("seqnbr", 99999);
		                        // testHM.put("text", "0135                          10000                 1844232          410403-24AWL-NBR                        0000000300000          ");
											        	//testHM.put("trans", "0210");
											        	//testHM.put("seqnbr", 99999);
											        	//testHM.put("text", "0210                          S9876                 AA99549          410403-24AWL-NBR                        0000000300000          ");
											        	//testHM.put("text", "0210                          S9876                 OL18073110540979 410403-24AWL-NBR                        0000000100000          ");
					        	// outboundMessagesAL = new ArrayList<HashMap<String,Object>>(3);
					        	// outboundMessagesAL.add(testHM);
					        	// if (1 == 0) {throw new SQLException("");};

		        	// outboundMessagesAL = JDBCUtils.runQuery(sqlServerConnection, "select trans, seqnbr, text from [dbo].[OutBound] WITH (READPAST) where SeqNbr in ('130419')");
		        	// outboundMessagesAL = JDBCUtils.runQuery(sqlServerConnection, "select trans, seqnbr, text from [dbo].[OutBound] WITH (READPAST) where processed = 'N' and trans in ('0135', '0310', '0210')");
		        	outboundMessagesAL = JDBCUtils.runQuery(sqlServerConnection, "select trans, seqnbr, text from [dbo].[OutBound] WITH (READPAST) where processed = 'N' and trans in ('0135', '0310')");
					
		            if ((outboundMessagesAL != null) && (outboundMessagesAL.size() > 0)) {
			        	FascorOutboundMessageHandler.processFascorOutboundMessages(dbConnector8.getConnectionWDS(), sqlServerConnection, outboundMessagesAL);
		            } else {
		            	log.debug("Nothing in OutBound table to process.");
		            }
		        } catch (SQLException e ) {
			    	handleDBException(e, "While processing outbound messages.");
	        	} catch (Exception e ) {
			    	handleDBException(e, "While processing outbound messages.");
		    	}
			}

			// =====================================================================================================
			// =====================================================================================================

			if (Utils.getBit(whichMessageHandlerToRun, RUN_NIGHTLY_QUANTITY_UPDATE_PROCESS)) {
		        try {
					ChangeBin.processFailedUpdate(dbConnector8.getConnectionWDS());
	        	} catch (Exception e ) {
			    	handleDBException(e, "While processing queued bin update failures updates.");
		    	}

				// =====================================================================================================
		        
		        try {
					SKUOnHandQuantityUpdater.sendUpdatedOnhandQuantitiesToFascor(dbConnector8.getConnectionWDS(), dbConnector8.getConnectionSRO(), sqlServerConnection);
	        	} catch (Exception e ) {
			    	handleDBException(e, "While processing nightly inventory updates.");
		    	}
		        
				// =====================================================================================================
		        
		        try {
		        	NightlyBinUpdater.updateBins(dbConnector8.getConnectionWDS(), dbConnector8.getConnectionSRO(), sqlServerConnection);
	        	} catch (Exception e ) {
			    	handleDBException(e, "While processing nightly bin updates.");
		    	}

		        // =====================================================================================================
		        
		        try {
	        		NightlyBordItemSyncJob.synchInventoryForBordItems(dbConnector8.getConnectionWDS(), FascorProcessor.sqlServerConnection);
	        	} catch (Exception e ) {
			    	handleDBException(e, "While processing nightly Inventory Sync of Bord Items.");
		    	}

		        // =====================================================================================================
		        
			}

			// =====================================================================================================
			// =====================================================================================================

			if (Utils.getBit(whichMessageHandlerToRun, ONE_TIME_UPDATE_PROCESS)) {
				
				// qqq
				
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	// 					Bulk update inventory
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------

				/*

		    	JDBCUtils.UPDATE_WDS_DB_IN_BATCH = true;
		    	JDBCUtils.UPDATE_FASCOR_DB_IN_BATCH = true;

	        	ArrayList<HashMap<String, Object>> partlocAL = null;
	        	try {
		        	String qtyGTZeroSQL = "select part_num, qty_onhand from pub.partloc where qty_onhand > 0 order by part_num";
		        	// String qtyGTZeroSQL = "select top 60 part_num, qty_onhand from pub.partloc where part_num like '%BUSS' and qty_onhand > 0 order by part_num";
					partlocAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), qtyGTZeroSQL);
				} catch (SQLException e) {
					e.printStackTrace();
					closeDBConnections();
					System.exit(0);
				}
	        	
	        	JDBCUtils.UPDATE_FASCOR_DB_IN_BATCH = true;

	        	int processed = 0, commitCount = 0;
	        	
	            for (Iterator<HashMap<String, Object>> iterator  = partlocAL.iterator(); iterator.hasNext();) {
					HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();

					String part_num = ((String) hashMap.get("part_num")).trim();
					int qty_onhand = ((Number) hashMap.get("qty_onhand")).intValue();

					SKUOnHandQuantityUpdater.buildAndSend1420Message(dbConnector8.getConnectionWDS(), sqlServerConnection, part_num, qty_onhand);
					log.error("1420 sent for " + part_num);

					processed++;
					
	            	commitCount++;
	            	if (commitCount >= 300) {
			        	try {
							JDBCUtils.executeStatementsThatAreInTheBatch_BATCH(JDBCUtils.BATCH_NAME_FSCR, FascorProcessor.sqlServerConnection, true);
							FascorProcessor.sqlServerConnection.commit();
							
			            	FascorProcessor.getDBConnections(20);	                								
						} catch (Exception e) {
							log.error(" -1201ZZ- . Failed to process a batch. ");
							
							JDBCUtils.discardBatch_BATCH(JDBCUtils.BATCH_NAME_FSCR, FascorProcessor.sqlServerConnection);
						}
		                commitCount = 0;
	            	}
				}

	            if (commitCount > 0) {
		        	try {
						JDBCUtils.executeStatementsThatAreInTheBatch_BATCH(JDBCUtils.BATCH_NAME_FSCR, FascorProcessor.sqlServerConnection, true);
						FascorProcessor.sqlServerConnection.commit();
					} catch (Exception e) {
						log.error(" -1201ZZ- . Failed to process a batch. ");
						
						JDBCUtils.discardBatch_BATCH(JDBCUtils.BATCH_NAME_FSCR, FascorProcessor.sqlServerConnection);
					}
	                commitCount = 0;
            	}

	            log.error("Total number of records processed: " + processed);

				closeDBConnections();
				System.exit(0);
				
				*/
				
				//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	// Update PO received quantities
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------

				/*
	        	
	        	try {
		        	ArrayList<HashMap<String, Object>> porder_bomAl = null;
		        	String openPOsSQL =
		        			"select po.order_num                                         " +
		        			"from pub.porder as po, pub.pitem as line                    " +
		        			"where                                                       " +
		        			"po.date_closed is null                                  and " +
		        			"po.date_issued is not null                              and " +
		        			"po.order_num = line.order_num                           and " +
		        			"line.drop_ship = '0'                                    and " + 
																				        			// "line.qty_ord > 0                                        and " +
																				        			// "(line.qty_ord - line.qty_cancel) > line.qty_received        " +
		        			"(line.qty_ord - line.qty_cancel) > 0                        " +
		        			"order by po.order_num";
		        	porder_bomAl = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), openPOsSQL);

		        	ArrayList<String> messageTypeIndAl = new ArrayList<String>(1000),
        				              dataAl = new ArrayList<String>(1000);

		        	int processed = 0;
		        	String prev_order_num = "", order_num;
		            for (Iterator<HashMap<String, Object>> iterator  = porder_bomAl.iterator(); iterator.hasNext();) {
						HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();
						order_num = ((String) hashMap.get("order_num")).trim();

						if (prev_order_num.compareToIgnoreCase(order_num) == 0) {
						 	continue;
						}
						prev_order_num = order_num;

						// =====================   UPDATE PO Lines   ====================== 
						
			        	ArrayList<HashMap<String, Object>> pitemAl = null;
			        	String linesWithReceivedQuantitySQL =
			        			"select line_num, part_num, qty_ord, qty_cancel, qty_received from pub.pitem "   +
			        			"where order_num = '" + order_num + "' "                    +
			        			"  and drop_ship = '0' "                                    +
			        			"  and qty_ord > 0 "                                        +
			        			"  and (qty_ord - qty_cancel) > 0 "                         +
			        			"  and qty_received > 0 "                                   +
			        			"order by line_num";
			        	pitemAl = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), linesWithReceivedQuantitySQL);
			        	if ((pitemAl != null) && (pitemAl.size() > 0)) {
				            for (Iterator<HashMap<String, Object>> iterator2  = pitemAl.iterator(); iterator2.hasNext();) {
								HashMap<String, Object> pitemHashMap = (HashMap<String, Object>) iterator2.next();		            	
				            	
				            	int line_num = ((Number) pitemHashMap.get("line_num")).intValue();
								String part_num = ((String) pitemHashMap.get("part_num")).trim();
				            	int qty_ord = ((Number) pitemHashMap.get("qty_ord")).intValue();
				            	int qty_cancel = ((Number) pitemHashMap.get("qty_cancel")).intValue();
				            	int qty_received = ((Number) pitemHashMap.get("qty_received")).intValue();
				            	
				            	String updateSQL = "update PO_Line set Received_Qty = " + qty_received +
				            	                   " where PO_Nbr = '" + order_num + "' "          +
				            			           "   and PO_Line_Nbr = '" + line_num + "'";
				            	log.debug("ZZZ - Updating PO " + order_num + ", Line " + line_num + ", qty_received " + qty_received + ", qty_ord " + qty_ord + ", qty_cancel " + qty_cancel);


				            	log.debug(updateSQL);
				            	try {
									int rc = JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, updateSQL);								
								} catch (SQLException e) {
									log.error("Failed to update Received_Qty for PO " + order_num + ", line: " + line_num, e);
								}
				            }
			        	}
			        	
						// ==================   DO NOT UPDATE PO Lines  =================== 

			        	String linesWithNoReceivedQuantitySQL =
			        			"select line_num, part_num, qty_ord, qty_cancel, qty_received from pub.pitem "   +
			        			"where order_num = '" + order_num + "' "                    +
			        			"  and drop_ship = '0' "                                    +
			        			"  and (qty_ord - qty_cancel) > 0 "              +
			        			"  and qty_received = 0 "                                   +
			        			"order by line_num";
			        	pitemAl = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), linesWithNoReceivedQuantitySQL);
			        	if ((pitemAl != null) && (pitemAl.size() > 0)) {
				            for (Iterator<HashMap<String, Object>> iterator2  = pitemAl.iterator(); iterator2.hasNext();) {
								HashMap<String, Object> pitemHashMap = (HashMap<String, Object>) iterator2.next();		            	
				            	
				            	int line_num = ((Number) pitemHashMap.get("line_num")).intValue();
								String part_num = ((String) pitemHashMap.get("part_num")).trim();
				            	int qty_ord = ((Number) pitemHashMap.get("qty_ord")).intValue();
				            	int qty_cancel = ((Number) pitemHashMap.get("qty_cancel")).intValue();
				            	int qty_received = ((Number) pitemHashMap.get("qty_received")).intValue();
				            	
				            	log.debug("ZZZ - Not updating PO " + order_num + ", Line " + line_num + ", qty_received " + qty_received + ", qty_ord " + qty_ord + ", qty_cancel " + qty_cancel);
				            }
			        	}

						// ================================================================ 

			            //processed++;
			            //if (processed > 20) {
			            //	break;			            	
			            //}
					}

		        	JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);

					closeDBConnections();
				} catch (Exception e) {
					closeDBConnections();
					log.debug("Exception occurred", e);
				}

	        	System.exit(0);

				*/
				
				//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	// 					SENDS OPEN POs TO FASCOR
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------

				/*

	        	try {
		        	ArrayList<HashMap<String, Object>> part_bomAl = null;
		        	String openPOsSQL =
		        			"select po.order_num                                         " +
		        			"from pub.porder as po, pub.pitem as line                    " +
		        			"where                                                       " +
		        			"po.date_closed is null                                  and " +
		        			"po.date_issued is not null                              and " +
		        			"po.order_num = line.order_num                           and " +
		        			"line.drop_ship = '0'                                    and " + 
																				        			// "line.qty_ord > 0                                        and " +
																				        			// "(line.qty_ord - line.qty_cancel) > line.qty_received        " +
		        			"(line.qty_ord - line.qty_cancel) > 0                        " +
		        			"order by po.order_num";
		        	part_bomAl = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), openPOsSQL);

		        	ArrayList<String> messageTypeIndAl = new ArrayList<String>(1000),
        				              dataAl = new ArrayList<String>(1000);

		        	String prev_order_num = "", order_num;
		            for (Iterator<HashMap<String, Object>> iterator  = part_bomAl.iterator(); iterator.hasNext();) {
						HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();
						order_num = ((String) hashMap.get("order_num")).trim();

						if (prev_order_num.compareToIgnoreCase(order_num) == 0) {
							continue;
						}
						prev_order_num = order_num;

						log.error("Sending order " + order_num);
						
						// ===============================================================================
						// Just for debugging 
	                    ArrayList<HashMap<String, Object>> pitemAL = null;
	                    // pitemAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select order_num, line_num, part_num from pub.pitem where order_num = '" + order_num + "' and drop_ship = '0' and (qty_ord > 0) and ((qty_ord - qty_cancel) > qty_received) order by line_num WITH (NOLOCK)");
	                    pitemAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select order_num, line_num, part_num from pub.pitem where order_num = '" + order_num + "' and drop_ship = '0' and ((qty_ord - qty_cancel) > 0) order by line_num WITH (NOLOCK)");
	                    if ((pitemAL != null) && (pitemAL.size() > 0)) {
		                    for (Iterator<HashMap<String, Object>>  iterator2 = pitemAL.iterator(); iterator2.hasNext();) {
	                            HashMap<String, Object> pitemHM = iterator2.next();
	    						log.error("Sending line " + ((Number) pitemHM.get("line_num")).intValue() + ", order " + order_num);
	                        }
	                    }
						// ===============================================================================
	                    
						messageTypeIndAl.add("P");
						dataAl.add(order_num);
					}

		        	WDSFascorRequestCommon[] wdsFascorRequestCommonArray = createFascorRequests(dbConnector8.getConnectionWDS(), messageTypeIndAl, dataAl);
		        	JDBCUtils.commitWDSChanges_Fascor(dbConnector8.getConnectionWDS());

					closeDBConnections();
				} catch (Exception e) {
					closeDBConnections();
					log.debug("Exception occurred", e);
				}
				System.exit(0);
				
				*/
				
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	// Send a few messages to Fascor
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------

				/*

	    		try {
	    		    JDBCUtils.UPDATE_FASCOR_DB = true;

					Message_0140 message_0140;
					message_0140 = new Message_0140(dbConnector8.getConnectionWDS(), "12736", "C");
					String fullFascorMessageStr = CacheManager.get_FascorMessage_FixedPart("0140", "A") + message_0140.getFascorMessage_VariablePart();
		        	FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20);
					FasUtils.sendMessageToFascor(sqlServerConnection, fullFascorMessageStr);
					JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);
	    		    
					Message_1110 message_1110;
					message_1110 = new Message_1110(dbConnector8.getConnectionWDS(), "PC1300-DURA", "C");
					fullFascorMessageStr = CacheManager.get_FascorMessage_FixedPart("1110", "A") + message_1110.getFascorMessage_VariablePart();
		        	FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20);
					FasUtils.sendMessageToFascor(sqlServerConnection, fullFascorMessageStr);
					JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);
					

					Connection wdsConnection = dbConnector8.getConnectionWDS();

					String order_num = "1857748";
					Message_1210 message_1210;
					message_1210 = new Message_1210(wdsConnection, order_num, "C");
					fullFascorMessageStr = CacheManager.get_FascorMessage_FixedPart("1210", "A") + message_1210.getFascorMessage_VariablePart();
		        	FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20);
					FasUtils.sendMessageToFascor(sqlServerConnection, fullFascorMessageStr);
					
                    ArrayList<HashMap<String, Object>> pitemAL = null;
                    pitemAL = JDBCUtils.runQuery(wdsConnection, "select order_num, line_num, part_num from pub.pitem where order_num = '" + order_num + "' and (qty_ord - qty_cancel) > 0 and drop_ship = '0' order by line_num WITH (NOLOCK)");
                    if ((pitemAL != null) && (pitemAL.size() > 0)) {
	                    for (Iterator<HashMap<String, Object>>  iterator2 = pitemAL.iterator(); iterator2.hasNext();) {
                            HashMap<String, Object> pitemHM = iterator2.next();

                            Message_1220 message_1220 = new Message_1220(wdsConnection, order_num, ((Number) pitemHM.get("line_num")).intValue(), "A");										
        					fullFascorMessageStr = CacheManager.get_FascorMessage_FixedPart("1220", "A") + message_1220.getFascorMessage_VariablePart();
        		        	FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20);
        					FasUtils.sendMessageToFascor(sqlServerConnection, fullFascorMessageStr);
                        }
                    }

                    ArrayList<HashMap<String, Object>> pcommentAL = null;
                    pcommentAL = JDBCUtils.runQuery(wdsConnection, "select distinct pc.line_num from pub.pcomment pc, pub.pitem pi where pc.order_num = '" + order_num + "' and pc.prt_on_rcvr = '1' and pc.order_num = pi.order_num and pc.line_num = pi.line_num and (pi.qty_ord - pi.qty_cancel) > 0 and pi.drop_ship = '0' order by pc.line_num WITH (NOLOCK)");
                    if ((pcommentAL != null) && (pcommentAL.size() > 0)) {
	                    for (Iterator<HashMap<String, Object>>  iterator2 = pcommentAL.iterator(); iterator2.hasNext();) {
                            HashMap<String, Object> pcommentHM = iterator2.next();

                            Message_1230 message_1230 = new Message_1230(wdsConnection, order_num, ((Number) pcommentHM.get("line_num")).intValue(), "A");
        					fullFascorMessageStr = CacheManager.get_FascorMessage_FixedPart("1230", "A") + message_1230.getFascorMessage_VariablePart();
        		        	FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20);
        					FasUtils.sendMessageToFascor(sqlServerConnection, fullFascorMessageStr);
                        }
                    }
					
   					JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);

					
					
					
					
					
					
					
					
					
					
					
	    		} catch (Exception e) {
					closeDBConnections();
	    			e.printStackTrace();
					System.exit(0);
	    		}

				closeDBConnections();
				System.exit(0);
				
				*/
				
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	// 					Print SKUs that map to FIX0000000
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------

				/*
				
				ArrayList<HashMap<String, Object>> partlocAL = null;
	        	try {
		        	String qtyGTZeroSQL = "select part_num, qty_onhand from pub.partloc where qty_onhand > 0 order by part_num";
					partlocAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), qtyGTZeroSQL);
					
		            for (Iterator<HashMap<String, Object>> iterator  = partlocAL.iterator(); iterator.hasNext();) {
						HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();

						String part_num = ((String) hashMap.get("part_num")).trim();
						int qty_onhand = ((Number) hashMap.get("qty_onhand")).intValue();
						
			    		String normal_binnum = "";

						String fascorBinNum = "";
						ArrayList<String> partloc2AL = JDBCUtils.runQuery_ReturnStrALs(dbConnector8.getConnectionWDS(), "select normal_binnum from pub.partloc where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "'")[0];
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
				    		log.error(part_num + " --- " + normal_binnum);
				    	}
		            }
				} catch (SQLException e) {
					e.printStackTrace();
				}
	        	
				closeDBConnections();
				System.exit(0);
				
				*/

				//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	// Find WDS and Fascor locations of certain parts
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------

				/*

	    		try {
	    			BufferedReader reader = new BufferedReader(new FileReader("C:\\0Ati\\000-000\\LocationsMissing.txt"));

	    			String part_num = reader.readLine();
	    			while (part_num != null) {
	    				part_num = part_num.trim();

    					try {
    						String fascorBinNum = "", normal_binnum = "";
    						{
    					    	ArrayList<String> partlocAL = JDBCUtils.runQuery_ReturnStrALs(dbConnector8.getConnectionWDS(), "select normal_binnum from pub.partloc where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "'")[0];
    					    	if ((partlocAL == null) || (partlocAL.size() == 0)) {
    					    		fascorBinNum = "";
    					    	} else {
    					    		normal_binnum = partlocAL.get(0);

    					    		if ((normal_binnum != null) && (normal_binnum.compareTo("") != 0)) {
    							    	ArrayList<String> wdsToFCLocMapAL = JDBCUtils.runQuery_ReturnStrALs(sqlServerConnection, "select FASCOR from WDSToFCLocMap where WDS = '" + normal_binnum + "'")[0];
    							    	if ((wdsToFCLocMapAL == null) || (wdsToFCLocMapAL.size() != 1)) {
    							    		fascorBinNum = "";
    							    	} else {
    							    		fascorBinNum = wdsToFCLocMapAL.get(0);
    							    	}
    					    		} else {
    						    		fascorBinNum = "";
    					    		}
    					    	}
    						}
    						log.debug(String.format("%-30s", part_num) + "," + String.format("%-15s", normal_binnum) + "," + String.format("%-15s", fascorBinNum));
    					} catch (Exception e) {
    						log.error(part_num + ": Failed to send message, exception: " + e.getMessage());
		    				part_num = reader.readLine();
    						continue;
    					}

	    				part_num = reader.readLine();
	    			}

					reader.close();

					closeDBConnections();
	    		} catch (Exception e) {
					closeDBConnections();
	    			e.printStackTrace();
					System.exit(0);
	    		}

				System.exit(0);
				
				*/
				
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	// Send over 1110 messages for SKUs that weren't sent due to vendor -NONE- missing
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------

				/*

	    		try {
	    		    JDBCUtils.UPDATE_FASCOR_DB = true;

	    		    int processed = 0;
	    			BufferedReader reader = new BufferedReader(new FileReader("C:\\0Ati\\000-000\\NONE.txt"));

	    			String part_num = reader.readLine();
	    			while (part_num != null) {
	    				part_num = part_num.trim();
	    				// System.out.println(part_num);

    					Message_1110 message_1110;
    					try {
    						message_1110 = new Message_1110(dbConnector8.getConnectionWDS(), part_num, "C");
    						String fullFascorMessageStr = CacheManager.get_FascorMessage_FixedPart("1110", "A") + message_1110.getFascorMessage_VariablePart();
    			        	FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20);
    						FasUtils.sendMessageToFascor(sqlServerConnection, fullFascorMessageStr);

    						JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);
    					} catch (Exception e) {
    						log.error(part_num + ": Failed to send message, exception: " + e.getMessage());
		    				part_num = reader.readLine();
    						continue;
    					}

    	        		log.error("Sent " + part_num);

    	        		processed++;
						if ((processed % 500) == 0) {
							log.error("Processed " + processed);
	    				}

	    				part_num = reader.readLine();
	    			}

					reader.close();

					closeDBConnections();
	    		} catch (Exception e) {
					closeDBConnections();
	    			e.printStackTrace();
					System.exit(0);
	    		}

				System.exit(0);
				
				*/

	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	// 					Compare WDS with Fascor, vendor
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------

				/*

				try {
					
		        	ArrayList<HashMap<String, Object>> vendorAL = null;
		        	try {
			        	// String vendorSQL = "select top 1000 vendor_num from pub.vendor";
			        	String vendorSQL = "select vendor_num from pub.vendor";
			        	vendorAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), vendorSQL);
					} catch (SQLException e) {
						e.printStackTrace();
						closeDBConnections();
						System.exit(0);
					}
		        	
		            for (Iterator<HashMap<String, Object>> iterator  = vendorAL.iterator(); iterator.hasNext();) {
		            	String vendor_num = null;
		            	
		            	try {
							HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();

							vendor_num = ((String) hashMap.get("vendor_num")).trim();
							
							Message_0140 message_0140;
							message_0140 = new Message_0140(dbConnector8.getConnectionWDS(), vendor_num, "C");
							String compareResult = message_0140.compareFieldsWithFascor(sqlServerConnection);
							if (compareResult == null) {
								log.debug(vendor_num + " MATCHED");
							} else {
								log.debug(vendor_num + " DIDN'T MATCH, " + compareResult);							
							}
						} catch (Exception e) {
							log.debug(vendor_num + " Exception, " + e.getMessage());
						}
		            }
				} catch (Exception e) {
					log.debug("", e);
				}					

				closeDBConnections();
				System.exit(0);
				
				*/

	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	// 					Compare WDS with Fascor, PO
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------

				/*

				try {
					
		        	ArrayList<HashMap<String, Object>> porderAL = null;
		        	try {
			        	// String openPOsSQL = "select order_num from pub.porder order by order_num";			        		
			        	// String openPOsSQL = "select order_num from pub.porder where order_num = '1844237' order by order_num";
			        	String openPOsSQL =
			        			"select distinct po.order_num                                         " +
			        			"from pub.porder as po, pub.pitem as line                    " +
			        			"where                                                       " +
			        			"po.date_closed is null                                  and " +
			        			"po.date_issued is not null                              and " +
			        			"po.order_num = line.order_num                           and " +
			        			"line.qty_ord > 0                                        and " +
			        			"(line.qty_ord - line.qty_cancel) > line.qty_received        " +
			        			"order by po.order_num";
			        	porderAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), openPOsSQL);
					} catch (SQLException e) {
						e.printStackTrace();
						closeDBConnections();
						System.exit(0);
					}
		        	
		        	int processed = 0;
					
		            for (Iterator<HashMap<String, Object>> iterator  = porderAL.iterator(); iterator.hasNext();) {
		            	String order_num = null;
		            	
		            	try {
							HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();
							order_num = ((String) hashMap.get("order_num")).trim();
							
							Message_1210 message_1210;
							message_1210 = new Message_1210(dbConnector8.getConnectionWDS(), order_num, "C");
							String compareResult = message_1210.compareFieldsWithFascor(sqlServerConnection);
							if (compareResult == null) {
								
			                    ArrayList<HashMap<String, Object>> pitemAL = null;
			                    // pitemAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select line_num from pub.pitem where order_num = '" + order_num + "' order by line_num WITH (NOLOCK)");
			                    pitemAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select line_num from pub.pitem where order_num = '" + order_num + "' and (qty_ord - qty_received - qty_cancel) > 0 and drop_ship = '0' order by line_num WITH (NOLOCK)");				                    
			                    if ((pitemAL != null) && (pitemAL.size() > 0)) {
			                    	boolean matched = true;
			                    	
				                    for (Iterator<HashMap<String, Object>> iterator2 = pitemAL.iterator(); iterator2.hasNext();) {
			                            HashMap<String, Object> pitemHM = iterator2.next();
			                            int line_num = ((Number) pitemHM.get("line_num")).intValue();

			                	        Message_1220 message_1220 = new Message_1220(dbConnector8.getConnectionWDS(), order_num, line_num, "A");
										compareResult = message_1220.compareFieldsWithFascor(sqlServerConnection);
										if (compareResult != null) {
											log.debug(order_num + "Line No. " + line_num + " DIDN'T MATCH, " + compareResult);
											matched = false;
											break;
										}
			                        }
				                    if (matched == true) {
										log.debug(order_num + " MATCHED");
				                    }
			                    } else {
									log.debug(order_num + " MATCHED");			                    	
			                    }
							} else {
								log.debug(order_num + " DIDN'T MATCH, " + compareResult);							
							}
						} catch (Exception e) {
							log.debug(order_num + " Exception, " + e.getMessage());
						}
						
						processed++;
						if (processed >= 100) {
							// break;
						}
		            }
				} catch (Exception e) {
					log.debug("", e);
				}					

				closeDBConnections();
				System.exit(0);
				
				*/

	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	// 					Compare WDS with Fascor, BOM
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------

				/*

				try {
					
		        	ArrayList<HashMap<String, Object>> bomAL = null;
		        	try {
			        	// String bomSQL = "select distinct partnum_assm from pub.part_bom where partnum_assm = '410403-24AWL-NBR'";
			        	// String bomSQL = "select distinct partnum_assm from pub.part_bom";
			        	String bomSQL = "select partnum_assm from pub.part_bom order by partnum_assm";			        		
			        	bomAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), bomSQL);
					} catch (SQLException e) {
						e.printStackTrace();
						closeDBConnections();
						System.exit(0);
					}
		        	
		        	int processed = 0;
					String prev_partnum_assm = "";
					
		            for (Iterator<HashMap<String, Object>> iterator  = bomAL.iterator(); iterator.hasNext();) {
		            	String partnum_assm = null;
		            	
		            	try {
							HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();
							partnum_assm = ((String) hashMap.get("partnum_assm")).trim();
							if (prev_partnum_assm.compareToIgnoreCase(partnum_assm) == 0) {
								continue;						
							}
							prev_partnum_assm = partnum_assm;
							
							String eng_rls_num = Message_3052.get_eng_rls_num(dbConnector8.getConnectionWDS(), partnum_assm);
							
							Message_3052 message_3052;
							message_3052 = new Message_3052(dbConnector8.getConnectionWDS(), partnum_assm, eng_rls_num, "C");
							String compareResult = message_3052.compareFieldsWithFascor(sqlServerConnection);
							if (compareResult == null) {
			                    ArrayList<HashMap<String, Object>> oneBomAL = null;
			                    oneBomAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select seq_num, partnum_comp from pub.part_bom where partnum_assm = '" + partnum_assm + "' and eng_rls_num  = '" + eng_rls_num + "' order by seq_num WITH (NOLOCK)");
			                    if ((oneBomAL != null) && (oneBomAL.size() > 0)) {
			                    	boolean matched = true;
			                    	
				                    for (Iterator<HashMap<String, Object>>  iterator2 = oneBomAL.iterator(); iterator2.hasNext();) {
			                            HashMap<String, Object> oneBomHM = iterator2.next();

			                	        Message_3057 message_3057 = new Message_3057(dbConnector8.getConnectionWDS(), partnum_assm, eng_rls_num, ((Number) oneBomHM.get("seq_num")).intValue(), "A");
										compareResult = message_3057.compareFieldsWithFascor(sqlServerConnection);
										if (compareResult != null) {
											log.debug(partnum_assm + "Seq No. " + ((Number) oneBomHM.get("seq_num")).intValue() + " DIDN'T MATCH, " + compareResult);
											matched = false;
											break;
										}
			                        }
				                    if (matched == true) {
										log.debug(partnum_assm + " MATCHED");
				                    }
			                    } else {
									log.debug(partnum_assm + " MATCHED");			                    	
			                    }
							} else {
								log.debug(partnum_assm + " DIDN'T MATCH, " + compareResult);							
							}
						} catch (Exception e) {
							log.debug(partnum_assm + " Exception, " + e.getMessage());
						}
						
						processed++;
						if (processed >= 100) {
							break;
						}
		            }
				} catch (Exception e) {
					log.debug("", e);
				}					

				closeDBConnections();
				System.exit(0);
				
				*/

	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	// 					Compare WDS with Fascor, Part
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------					

				/*

				try {
					
		        	ArrayList<HashMap<String, Object>> partAL = null;
		        	try {
			        	String partSQL = "select part_num from pub.part where part_num like '%BUSS' order by part_num";
						partAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), partSQL);
					} catch (SQLException e) {
						e.printStackTrace();
						closeDBConnections();
						System.exit(0);
					}
		            for (Iterator<HashMap<String, Object>> iterator  = partAL.iterator(); iterator.hasNext();) {
		            	String part_num = null;
		            	try {
							HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();

							part_num = ((String) hashMap.get("part_num")).trim();
							
							Message_1110 message_1110;
							message_1110 = new Message_1110(dbConnector8.getConnectionWDS(), part_num, "C");
							String compareResult = message_1110.compareFieldsWithFascor(sqlServerConnection);
							if (compareResult == null) {
								log.debug(part_num + " " + "MATCHED");
							} else {
								log.debug(part_num + " " + "DIDN'T MATCH, " + compareResult);							
							}
						} catch (Exception e) {
							log.debug(part_num + " Exception, " + e.getMessage());
						}
		            }
				} catch (Exception e) {
					log.debug("", e);
				}					

				closeDBConnections();
				System.exit(0);
				
				*/
				
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	// 					Create FascorRequest
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------

				/*
		      	
		      	try {
					ArrayList<String> messageTypeIndAl = new ArrayList<String>(10),
		            		  dataAl = new ArrayList<String>(10);
					messageTypeIndAl.add("S");
					dataAl.add("PC1300-DURA");
					WDSFascorRequestCommon[] wdsFascorRequestCommonArray = createFascorRequests(dbConnector8.getConnectionWDS(), messageTypeIndAl, dataAl);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				closeDBConnections();
				System.exit(0);
				
				*/

	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	// Send over 1110 messages for SKUs that weren't sent due to a bug in the batch update process
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------

				/*
				
	    		try {
	    		    JDBCUtils.UPDATE_FASCOR_DB = true;

	    		    int processed = 0;
	    			// BufferedReader reader = new BufferedReader(new FileReader("C:\\0Ati\\000-000\\DidNotFindCache.txt"));
	    			BufferedReader reader = new BufferedReader(new FileReader("C:\\0Ati\\000-000\\CacheDidNotMatch.txt"));

	    			String part_num = reader.readLine();
	    			while (part_num != null) {
	    				part_num = part_num.trim();
	    				// System.out.println(part_num);

	    	        	ArrayList<HashMap<String, Object>> inBoundMessagesIJustPutInAL = JDBCUtils.runQuery(sqlServerConnection, "select * from InBound where substring(Text, 8, 30) = '" + part_num + "'");
	    	        	if ((inBoundMessagesIJustPutInAL == null) || (inBoundMessagesIJustPutInAL.size() == 0)) {
		    				// log.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	    	        		log.error("Sending " + part_num);

	    					Message_1110 message_1110;
	    					try {
	    						message_1110 = new Message_1110(dbConnector8.getConnectionWDS(), part_num, "C");
	    						String fullFascorMessageStr = CacheManager.get_FascorMessage_FixedPart("1110", "C") + message_1110.getFascorMessage_VariablePart();
	    			        	FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20);
	    						FasUtils.sendMessageToFascor(sqlServerConnection, fullFascorMessageStr);

	    						JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);
	    					} catch (Exception e) {
	    						log.error(part_num + ": Failed to send message, exception: " + e.getMessage());
	    						continue;
	    					}
	    	        	}

						processed++;
						if ((processed % 500) == 0) {
							log.error("Processed " + processed);
	    				}

	    				part_num = reader.readLine();
	    			}

					reader.close();

					closeDBConnections();
	    		} catch (Exception e) {
					closeDBConnections();
	    			e.printStackTrace();
					System.exit(0);
	    		}

				System.exit(0);
				
				*/

				//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	// Send over a few messages
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------

				/*

				try {
				    JDBCUtils.UPDATE_FASCOR_DB = true;
				    JDBCUtils.UPDATE_WDS_DB = true;

				    // V - Vendor				S - Sku						P - PurchaseOrder
					// O - Order				K - Kit build 3052			KS - Kits for stock 2015
		        	ArrayList<String> messageTypeIndAl = new ArrayList<String>(100),
		        				      dataAl = new ArrayList<String>(100);

		        	messageTypeIndAl.add("S");
		        	dataAl.add("CH270-CHGP");

					WDSFascorRequestCommon[] wdsFascorRequestCommonArray = createFascorRequests(dbConnector8.getConnectionWDS(), messageTypeIndAl, dataAl);
		        	String processSequenceNumbersInThisSet = buildCommaSeparatedSequenceNumberString(wdsFascorRequestCommonArray);
					ArrayList<HashMap<String, Object>> fascorRequestsAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select sequenceNumber, processed, functionCode, data, key from pub.FascorRequests where processed = 'N' and sequenceNumber in (" + processSequenceNumbersInThisSet + ") order by functionCode WITH (READPAST NOWAIT)");

		            if ((fascorRequestsAL != null) && (fascorRequestsAL.size() > 0)) {
		            	FascorInboundMessageHandler.processFascorRequests(dbConnector8.getConnectionWDS(), sqlServerConnection, fascorRequestsAL);
		            }
				} catch (Exception e) {
					log.error("Exception occurred", e);
				}

				closeDBConnections();

				System.exit(0);

				*/

				//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	// Delete cache records for the SKUs that were not sent due to vendor
	        	// 12736 not being there
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------

				/*

	    		try {
	    		    JDBCUtils.UPDATE_WDS_DB = true;

	    		    int processed = 0;
	    			BufferedReader reader = new BufferedReader(new FileReader("C:\\0Ati\\Projects\\FASCOR\\ZZZ-SKUsAffectedBy12736.txt"));
	    			String part_num = reader.readLine();
	    			while (part_num != null) {
	    				part_num = part_num.trim();
	    				// System.out.println(part_num);

	    				log.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

		        		// String sqlStatement = "select * from pub.FascorMessageCache where messageType = '1110' and key1 = '" + part_num + "'";
		        		// ArrayList<HashMap<String, Object>> al = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), sqlStatement);
		        		// FasUtils.printArrayListOfHashMaps(al);

	    				try {
	    					String sqlStatement = "delete from pub.FascorMessageCache where messageType = '1110' and key1 = '" + part_num + "'";
							int rc = JDBCUtils.runUpdateQueryAgainstWDS_Fascor(dbConnector8.getConnectionWDS(), sqlStatement);
						} catch (Exception e) {
							log.debug("Exception processing " + part_num, e);
						}

						processed++;
						if ((processed % 200) == 0) {
							JDBCUtils.commitWDSChanges_Fascor(dbConnector8.getConnectionWDS());
						}

	    				part_num = reader.readLine();
	    			}
					JDBCUtils.commitWDSChanges_Fascor(dbConnector8.getConnectionWDS());

					reader.close();

					closeDBConnections();
	    		} catch (Exception e) {
					closeDBConnections();
	    			e.printStackTrace();
					System.exit(0);
	    		}

				System.exit(0);

				*/

	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	// Test the speed of sending a few thousand messages
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------

				/*
				
				String sqlStatement = "select top 2000 part_num from pub.part WITH (NOLOCK)";
        		ArrayList<HashMap<String, Object>> part_num_al = null;
				try {
					part_num_al = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), sqlStatement);
				} catch (SQLException e1) {
					closeDBConnections();
					System.exit(0);
				}

        		long timeBeforeStartingProcessing = System.currentTimeMillis();

        		int currentRecNo = 0;

        		for (Iterator<HashMap<String, Object>> iterator = part_num_al.iterator(); iterator.hasNext();) {
        			currentRecNo++;
        			if (currentRecNo <= 0) {
        				continue;
        			}


					HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();
					String part_num = (String) hashMap.get("part_num");

					log.debug("NNNN Processing " + part_num);

					Message_1110 message_1110;
					try {
						message_1110 = new Message_1110(dbConnector8.getConnectionWDS(), part_num, "A");
					} catch (Exception e) {
						log.error(part_num + ": Failed to build message, exception: " + e.getMessage());
						continue;
					}

		        	FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20);
	            	try {
						CacheManager.sendFascorMessages(dbConnector8.getConnectionWDS(), FascorProcessor.sqlServerConnection, message_1110);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

        		try {
    				JDBCUtils.commitWDSChanges_Fascor(dbConnector8.getConnectionWDS());
	                JDBCUtils.commitFascorChanges_Fascor(FascorProcessor.sqlServerConnection);
				} catch (Exception e) {
				}

        		log.debug("Time taken: in millis: " + (System.currentTimeMillis() - timeBeforeStartingProcessing));

        		closeDBConnections();
				System.exit(0);
				
				*/

	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------

				/*

	        	ArrayList<HashMap<String, Object>> partvendAL = Message_1110.get_vend_partnum(dbConnector8.getConnectionWDS(), "10-32 TAP-SPIRAL POINT-MISC", null);
	        	FasUtils.print_AL_Of_HMs(partvendAL);

				*/

	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------
	        	//-----------------------------------------------------------------------------

	        	/*
				ArrayList<HashMap<String, Object>> partAndVendornumAL = null;
				partAndVendornumAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select part.part_num, partvend.vendor_num from pub.part,  pub.partvend where part.part_num = partvend.part_num and part.audit_date >= '10/31/2018' WITH (READPAST NOWAIT)");

				String[][] msgTypeORMessageTypeInd_data_array_temp = new String[2][100000];
				int i = 0;
				for (Iterator<HashMap<String, Object>> iterator = partAndVendornumAL.iterator(); iterator.hasNext();) {
					HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();

					String part_num = (String) hashMap.get("part_num");
					String vendor_num = (String) hashMap.get("vendor_num");

					msgTypeORMessageTypeInd_data_array_temp[0][i] = "S";
					msgTypeORMessageTypeInd_data_array_temp[1][i] = part_num.trim();
					i++;

					msgTypeORMessageTypeInd_data_array_temp[0][i] = "V";
					msgTypeORMessageTypeInd_data_array_temp[1][i] = vendor_num.trim();
					i++;
				}
				String[][] msgTypeORMessageTypeInd_data_array = new String[2][i];
				for (int j = 0; j < msgTypeORMessageTypeInd_data_array[0].length; j++) {
					msgTypeORMessageTypeInd_data_array[0][j] = msgTypeORMessageTypeInd_data_array_temp[0][j];
				}
				for (int j = 0; j < msgTypeORMessageTypeInd_data_array[1].length; j++) {
					msgTypeORMessageTypeInd_data_array[1][j] = msgTypeORMessageTypeInd_data_array_temp[1][j];
				}

				WDSFascorRequestCommon[] wdsFascorRequestCommonArray = createFascorRequests(dbConnector8.getConnectionWDS(), msgTypeORMessageTypeInd_data_array);
                JDBCUtils.commitWDSChanges_Fascor(dbConnector8.getConnectionWDS());

				log.debug("Done");

				*/

	        	/*

            	ArrayList<String> changedPartsUsedAL = JDBCUtils.runQuery_ReturnStrALs(dbConnector8.getConnectionWDS(), "select part_num from pub.part where audit_date >= '10/31/2018'")[0];
				String[][] msgTypeORMessageTypeInd_data_array = new String[2][changedPartsUsedAL.size()];

				int i = 0;
				for (Iterator<String> iterator = changedPartsUsedAL.iterator(); iterator.hasNext();) {
					String part_num = (String) iterator.next();

					msgTypeORMessageTypeInd_data_array[0][i] = "S";
					msgTypeORMessageTypeInd_data_array[1][i] = part_num.trim();
				}
				WDSFascorRequestCommon[] wdsFascorRequestCommonArray = createFascorRequests(dbConnector8.getConnectionWDS(), msgTypeORMessageTypeInd_data_array);
                JDBCUtils.commitWDSChanges_Fascor(dbConnector8.getConnectionWDS());

                */

                /*
                fascorRequestsAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select sequenceNumber, processed, functionCode, data, key from pub.FascorRequests where processed = 'N' and sequenceNumber >= '20181031' WITH (READPAST NOWAIT)");
	            if ((fascorRequestsAL != null) && (fascorRequestsAL.size() > 0)) {
	            	FascorInboundMessageHandler.processFascorRequests(dbConnector8.getConnectionWDS(), sqlServerConnection, fascorRequestsAL);
	            } else {
	            	log.debug("Nothing in FascorRequests table to process.");
	            }
	            */
				// =====================================================================================================
				// =====================================================================================================

				/*

				// For testing SQLs
			        try {
						ArrayList<HashMap<String, Object>> pitemAL = null;
			        	// pitemAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select comment_text[1] from pub.pcomment where order_num = '1798605' and line_num = 47");

						pitemAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select comment_text[1], comment_text[2], comment_text[3], comment_text[4], comment_text[5], comment_text[6], comment_text[7], comment_text[8], comment_text[9] from pub.pcomment where order_num = '1826020' and line_num = 2 order by comment_num WITH (READPAST NOWAIT)");

						// pitemAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select distinct pc.line_num from pub.pcomment pc, pub.pitem pi where pc.order_num = '1798605' and pc.order_num = pi.order_num and pc.line_num = pi.line_num and prt_on_rcvr = '1' order by pc.line_num WITH (NOLOCK)");
						FasUtils.print_AL_Of_HMs(pitemAL);
			        } catch (SQLException e ) {
			        	System.out.println(e.getMessage());
				    	// handleDBException(e, "Exception-1.");
		        	} catch (Exception e ) {
			        	System.out.println(e.getMessage());
				    	// handleDBException(e, "Exception-2.");
			    	}

			        closeDBConnections();
					log.debug("Exiting...");
					log.debug("Exiting...");
					System.exit(0);

				*/
			}

			// =====================================================================================================
			// =====================================================================================================

			/*
			if ((messagesForABatchAreAlreadyBuilt == false) && (vendorUploadIsHappening == false)) {	
				int unprocessedInbound = 999;
				while (unprocessedInbound > 0) {
					Utils.sleep(2000);
					unprocessedInbound = getUnprocessedInboundMessageCount(sqlServerConnection);
				}
				
				closeDBConnections();

				log.debug("Exiting...");
				log.debug("Exiting...");

				System.exit(0);
			}
			*/

			// qqq

			/*
			closeDBConnections();
			log.debug("Exiting...");
			log.debug("Exiting...");
			System.exit(0);
			*/

			if (Parms.FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX == false) {
				closeDBConnections();
				log.debug("Exiting...");
				log.debug("Exiting...");
				System.exit(0);
			}
			
			if ((messagesForABatchAreAlreadyBuilt == true) || (vendorUploadIsHappening == false)) {
				Utils.sleep(2000);
			}

			// =====================================================================================================
			// =====================================================================================================

		}
	}

	// ---------------------------------------------------------------------------------------------------------------------

}

