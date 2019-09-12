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
import galco.fascor.update.OrderAndPOUpdateHandler;
import galco.fascor.utils.FasUtils;
import galco.fascor.wds_requests.WDSFascorRequestCommon;
import galco.fascor.wds_requests.WDSFascorRequest_3052;
import galco.portal.config.Parms;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;
import galco.portal.wds.dao.Contact;
import galco.portal.wds.dao.FascorMessage;

public class KitLoader {
	private static Logger log = Logger.getLogger(KitLoader.class);

	static long lastTimeDBConnectionWasMade = Long.MIN_VALUE;

	static DBConnector dbConnector8 = null;
	static Connection sqlServerConnection = null;
	static boolean dbExceptionOccurred = false;

	static final long UTC_OFFSET = TimeZone.getDefault().getOffset(System.currentTimeMillis());

	public static void handleDBException(Exception e, int messageCode) {
		dbExceptionOccurred = true;
		galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com", "WebPortal@galco.com", "Problem in FASCOR Batch Process of " + Parms.HOST_NAME, e.getMessage() + ". Error Code - " + messageCode);
        log.error(e);
		lastTimeDBConnectionWasMade = System.currentTimeMillis();
	}
	public static void handleDBException(PortalException e, int messageCode) {
		dbExceptionOccurred = true;
		galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com", "WebPortal@galco.com", "Problem in FASCOR Batch Process of " + Parms.HOST_NAME, e.getE().getMessage() + ". Error Code - " + messageCode);
        log.error(e.getE());
		lastTimeDBConnectionWasMade = System.currentTimeMillis();
	}

	public static boolean getDBConnections() {
		if (dbExceptionOccurred == true) {
			if (System.currentTimeMillis() >= (lastTimeDBConnectionWasMade + 300000)) {
				return false;
			}
		}

		if ((dbExceptionOccurred == true) || ((dbConnector8 == null) || (sqlServerConnection == null)) || (System.currentTimeMillis() >= (lastTimeDBConnectionWasMade + 1200000))) {
			if (dbConnector8 != null) {
				try {
					dbConnector8.closeConnections();
				} catch (PortalException e) {
					handleDBException(e, 1);
				}
			}
			try {
				dbConnector8 = new DBConnector(false);
				
				dbConnector8.getConnectionWDS().setAutoCommit(false);
				dbConnector8.getConnectionSRO().setAutoCommit(false);
				dbConnector8.getConnectionWEB().setAutoCommit(false);
			} catch (Exception e) {
				handleDBException(e, 1);
				dbExceptionOccurred = true;
				return false;
			} catch (PortalException e) {
				handleDBException(e, 1);
				dbExceptionOccurred = true;
				return false;
			}

			if (sqlServerConnection != null) {
				try {
					sqlServerConnection.close();
				} catch (Exception e) {
					handleDBException(e, 1);
				}
			}
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				// sqlServerConnection = DriverManager.getConnection("jdbc:sqlserver://db10dev10.galco.com:1433;databaseName=DC01GAL;", "sa", "0ms1Bad1m{}");
				sqlServerConnection = DriverManager.getConnection("jdbc:sqlserver://Az-fascor-db1:1433;databaseName=DC01GAL;", "sa", "0ms1Bad1m{}");				
				sqlServerConnection.setAutoCommit(false);				
			} catch (Exception e) {
				handleDBException(e, 1);
				dbExceptionOccurred = true;
				return false;
			}

			dbExceptionOccurred = false;
			return true;
		}

		return true;
	}

	public static void closeDBConnections() {
		if (dbConnector8 != null) {
			try {
				dbConnector8.closeConnections();
			} catch (PortalException e) {
				handleDBException(e, 1);
			}
		}

		if (sqlServerConnection != null) {
			try {
				sqlServerConnection.close();
			} catch (Exception e) {
				handleDBException(e, 1);
			}
		}
	}

	public static void main(String[] args) {
		try {
		    SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

			Logger.getRootLogger().removeAppender("A1"); // stops console logging
			
			FileAppender fa = new FileAppender();
			fa.setName("FileLogger");
			  
			fa.setFile("C:\\0Ati\\ZLoadKits-" + yyyyMMddHHmmss.format(new Date()) + ".log");
			
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
		
	    JDBCUtils.UPDATE_FASCOR_DB = true;
	    JDBCUtils.UPDATE_WDS_DB = true;

	    
		if (getDBConnections() == true) {
			Connection wdsConnection = dbConnector8.getConnectionWDS();
			
			ArrayList<HashMap<String, Object>> partsAl = null;
	        try {
	            partsAl = JDBCUtils.runQuery(wdsConnection, "select top 2 partnum_assm, eng_rls_num from pub.part_bom as pbp where eng_rls_num = (select max(eng_rls_num) from pub.part_bom as pbc where pbc.partnum_assm = pbp.partnum_assm)");
	            // partsAl = JDBCUtils.runQuery(wdsConnection, "select distinct partnum_assm, eng_rls_num from pub.part_bom as pbp where eng_rls_num = (select max(eng_rls_num) from pub.part_bom as pbc where pbc.partnum_assm = pbp.partnum_assm)");
		    } catch (SQLException e ) {
				e.printStackTrace();
				closeDBConnections();
				System.exit(0);
		    }

	        for (Iterator<HashMap<String, Object>> iterator = partsAl.iterator(); iterator.hasNext();) {

            	HashMap<String, Object> partHM = (HashMap<String, Object>) iterator.next();
				String partnum_assm = ((String) partHM.get("partnum_assm")).trim();
				partnum_assm = partnum_assm.toUpperCase();
				String eng_rls_num = ((String) partHM.get("eng_rls_num")).trim();
				eng_rls_num = eng_rls_num.toUpperCase();

				if (partnum_assm.length() > 30) {
					log.error(partnum_assm + ": Failed to send, length is too big");
					continue;
				}

				boolean updatedDatabases = false;
				try {
					
					// --------------------------------------------------------------------------------------------
					
                	Message_3052 message_3052 = new Message_3052(wdsConnection, partnum_assm, eng_rls_num, "A");
                	
		        	FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20); 
	            	updatedDatabases = true;
                	CacheManager.sendFascorMessages(wdsConnection, sqlServerConnection, message_3052);
                	
                	ArrayList<Message> messageAL = new ArrayList<Message>(10);
                    ArrayList<HashMap<String, Object>> part_bomAL = null;
                	part_bomAL = JDBCUtils.runQuery(wdsConnection, "select seq_num, partnum_comp from pub.part_bom where partnum_assm = '" + partnum_assm + "' and eng_rls_num  = '" + eng_rls_num + "' order by seq_num WITH (NOLOCK)");
                    if ((part_bomAL != null) && (part_bomAL.size() > 0)) {
	                    for (Iterator<HashMap<String, Object>>  iterator2 = part_bomAL.iterator(); iterator2.hasNext();) {
                            HashMap<String, Object> part_bomHM = iterator2.next();

                	        Message_3057 message_3057 = new Message_3057(wdsConnection, partnum_assm, eng_rls_num, ((Number) part_bomHM.get("seq_num")).intValue(), "A");
                            messageAL.add(message_3057);
                        }
                    	CacheManager.sendFascorMessages(wdsConnection, sqlServerConnection, messageAL);			                    
                    }	            	
	            	
					// --------------------------------------------------------------------------------------------

	            	JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
	                JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);
	                
	                log.error(partnum_assm + ": Sent successfully.");
				} catch (Exception e) {
					log.error(partnum_assm + ": Failed to send, exception: " + e.getMessage());

    	        	if (updatedDatabases == true) {
    	        		try {
    		                JDBCUtils.rollbackWDSChanges_Fascor(wdsConnection);
							JDBCUtils.rollbackFascorChanges_Fascor(sqlServerConnection);
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
    	        	}
				}
            }
            
			try {
				closeDBConnections();
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

