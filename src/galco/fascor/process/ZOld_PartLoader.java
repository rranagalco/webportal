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

import galco.portal.utils.Utils;
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

public class ZOld_PartLoader {
    private static Logger log = Logger.getLogger(ZOld_PartLoader.class);

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

            fa.setFile("C:\\0Ati\\ZLoadParts-" + yyyyMMddHHmmss.format(new Date()) + ".log");

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
                // partsAl = JDBCUtils.runQuery(wdsConnection, "select substring(part_num, 1, 50) as part_num, vendor_num from pub.part where part_num like '410403%'");
                partsAl = JDBCUtils.runQuery(wdsConnection, "select top 3 substring(part_num, 1, 50) as part_num, vendor_num from pub.part where part_num like '410403%'");
            } catch (SQLException e ) {
                e.printStackTrace();
                closeDBConnections();
                System.exit(0);
            }

            for (Iterator<HashMap<String, Object>> iterator = partsAl.iterator(); iterator.hasNext();) {

                HashMap<String, Object> partHM = (HashMap<String, Object>) iterator.next();
                String part_num = ((String) partHM.get("PART_NUM")).trim();
                part_num = part_num.toUpperCase();

                if (part_num.length() > 30) {
                    log.error(part_num + ": Failed to send, length is too big");
                    continue;
                }

                boolean updatedDatabases = false;

                try {
                    String vendor_numFromPart = ((String) partHM.get("vendor_num")).trim();

                    if ((vendor_numFromPart != null) && (vendor_numFromPart.compareTo("") != 0)) {
                        ArrayList<HashMap<String, Object>> vendorAL = null;
                        vendorAL = JDBCUtils.runQuery(sqlServerConnection, "select Vendor_ID from [dbo].[Vendor] where Vendor_ID = '" + vendor_numFromPart + "'");
                        if ((vendorAL == null) || (vendorAL.size() == 0)) {
                            Message_0140 message_0140 = new Message_0140(wdsConnection, vendor_numFromPart, "A");

                            FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20);
                            CacheManager.sendFascorMessages(wdsConnection, sqlServerConnection, message_0140);
                            updatedDatabases = true;
                        }
                    }

                    ArrayList<HashMap<String, Object>> partvendAL = null;
                    partvendAL = JDBCUtils.runQuery(wdsConnection, "select vendor_num from pub.partvend where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "'");
                    if ((partvendAL == null) || (partvendAL.size() == 0)) {
                        log.error(part_num + ": No vendors found for this part.");
                    } else {
                        for (Iterator<HashMap<String, Object>> iterator2 = partvendAL.iterator(); iterator2.hasNext();) {
                            HashMap<String, Object> partvendHM = (HashMap<String, Object>) iterator2.next();
                            String vendor_num = ((String) partvendHM.get("vendor_num")).trim();

                            ArrayList<HashMap<String, Object>> vendorAL = null;
                            vendorAL = JDBCUtils.runQuery(sqlServerConnection, "select Vendor_ID from [dbo].[Vendor] where Vendor_ID = '" + vendor_num + "'");
                            if ((vendorAL == null) || (vendorAL.size() == 0)) {
                                Message_0140 message_0140 = new Message_0140(wdsConnection, vendor_num, "A");

                                FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20);
                                CacheManager.sendFascorMessages(wdsConnection, sqlServerConnection, message_0140);
                                updatedDatabases = true;
                            }
                        }
                    }

                    if (updatedDatabases == true) {
                        JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);
                    }

                    Message_1110 message_1110 = new Message_1110(wdsConnection, part_num, "A");

                    FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES = new ArrayList<String>(20);
                    CacheManager.sendFascorMessages(wdsConnection, sqlServerConnection, message_1110);
                    updatedDatabases = true;

                    JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
                    JDBCUtils.commitFascorChanges_Fascor(sqlServerConnection);

                    log.error(part_num + ": Sent successfully.");
                } catch (Exception e) {
                    log.error(part_num + ": Failed to send, exception: " + e.getMessage());

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

