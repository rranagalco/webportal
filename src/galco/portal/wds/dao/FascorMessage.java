package galco.portal.wds.dao;

import galco.fascor.messages.Message_1310;
import galco.fascor.requests.control.FascorInboundMessageHandler;
import galco.portal.utils.JDBCUtils;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class FascorMessage {
	private static Logger log = Logger.getLogger(FascorMessage.class);

	String sequenceNumber;
    String messageType;
    String key1;
    String key2;
    String key3;
    String fascorMessage;

    public FascorMessage() {
    }

    public FascorMessage(String sequenceNumber, String messageType, String key1, String key2, String key3, String fascorMessage) {
		this.sequenceNumber = sequenceNumber;
		this.messageType = messageType;
		this.key1 = key1;
		this.key2 = key2;
		this.key3 = key3;
		this.fascorMessage = fascorMessage;
	}

    private static String getQueryFieldSelectionString() {
        return "sequenceNumber, messageType, key1, key2, key3, fascorMessage";
    }

    public static ArrayList<FascorMessage> buildDAOObjectsFromResultSet(Connection wdsConnection, String queryString) throws SQLException {
        ArrayList<FascorMessage> al = new ArrayList<FascorMessage>();

        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = wdsConnection.createStatement();
            rs = stmt.executeQuery(queryString);

            while (rs.next()) {
                FascorMessage fascorMessage = new FascorMessage();

                fascorMessage.setSequencenumber(getStringFromResultSet(rs, "sequenceNumber"));
                fascorMessage.setMessagetype(getStringFromResultSet(rs, "messageType"));
                fascorMessage.setKey1(getStringFromResultSet(rs, "key1"));
                fascorMessage.setKey2(getStringFromResultSet(rs, "key2"));
                fascorMessage.setKey3(getStringFromResultSet(rs, "key3"));
                fascorMessage.setFascormessage(getStringFromResultSet(rs, "fascorMessage"));

                al.add(fascorMessage);
            }

            rs.close();
            rs = null;
            stmt.close();
            stmt = null;
        } catch (SQLException e) {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e3) {
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e3) {
            }

            throw e;
        }

        if (al.size() == 0) {
            return null;
        }

        return al;
    }

    public static String getStringFromResultSet(ResultSet rs, String fieldName) throws SQLException {
        String fieldValue = rs.getString(fieldName);
        return (fieldValue != null)?fieldValue:"";
    }

    public static void printDAOObjects(ArrayList<FascorMessage> al) {
        if (al == null) {
            return;
        }

        for (Iterator<FascorMessage> iterator = al.iterator(); iterator.hasNext();) {
            FascorMessage fascorMessage = iterator.next();
           log.debug	(
                    "sequenceNumber: " + fascorMessage.getSequencenumber() + "\n" +
                    "messageType: " + fascorMessage.getMessagetype() + "\n" +
                    "key1: " + fascorMessage.getKey1() + "\n" +
                    "key2: " + fascorMessage.getKey2() + "\n" +
                    "key3: " + fascorMessage.getKey3() + "\n" +
                    "fascorMessage: " + fascorMessage.getFascormessage() + "\n"
                    	);
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<FascorMessage> getDistinctFascorMessages(Connection wdsConnection, String messageType, String key1) throws SQLException {
        String queryString = "select distinct " + getQueryFieldSelectionString() +
        		             "  from pub.FascorMessage " +
                             " where messageType = '" + messageType + "' and key1 = '" + key1 + "' " +
        		             "order by sequenceNumber desc";

        ArrayList<FascorMessage> al = buildDAOObjectsFromResultSet(wdsConnection, queryString);
        printDAOObjects(al);
        return al;
    }

    public static ArrayList<FascorMessage> getFascorMessages(Connection wdsConnection, String messageType, String key1) throws SQLException {
        String queryString = "select " + getQueryFieldSelectionString() + " from pub.FascorMessage " +
                             "where messageType = '" + messageType + "' and key1 = '" + key1 + "'";

        ArrayList<FascorMessage> al = buildDAOObjectsFromResultSet(wdsConnection, queryString);
        printDAOObjects(al);
        return al;
    }

    public void delete(Connection wdsConnection) throws SQLException {
        String sqlStatement = "delete from pub.FascorMessage " +
                              "where sequenceNumber = '" + sequenceNumber + "' and messageType = '" + messageType + "' and key1 = '" + key1 + "' and key2 = '" + key2 + "' and key3 = '" + key3 + "'";
		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStatement);
    }

	public void persist(Connection wdsConnection) throws SQLException {
		String insertQueryString =
		        "insert into pub.FascorMessage "                                                    +
				"(sequenceNumber, messageType, key1, key2, key3, fascorMessage) " +
				"values ('" + sequenceNumber + "', " + "'" + messageType + "', " + "'" + key1 + "', "     +
				        "'" +    key2 + "', " + "'" + key3 + "', " + "'" + fascorMessage + "')";
		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, insertQueryString);
	}

    // -------------------------------------------------------------------------------------------------------------

    public String getSequencenumber() {
        return sequenceNumber;
    }
    public void setSequencenumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
    public String getMessagetype() {
        return messageType;
    }
    public void setMessagetype(String messageType) {
        this.messageType = messageType;
    }
    public String getKey1() {
        return key1;
    }
    public void setKey1(String key1) {
        this.key1 = key1;
    }
    public String getKey2() {
        return key2;
    }
    public void setKey2(String key2) {
        this.key2 = key2;
    }
    public String getKey3() {
        return key3;
    }
    public void setKey3(String key3) {
        this.key3 = key3;
    }
    public String getFascormessage() {
        return fascorMessage;
    }
    public void setFascormessage(String fascorMessage) {
        this.fascorMessage = fascorMessage;
    }

}
