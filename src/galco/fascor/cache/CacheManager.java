package galco.fascor.cache;

import galco.fascor.messages.Message;
import galco.fascor.process.FascorProcessor;
import galco.fascor.utils.FasUtils;
import galco.portal.config.Parms;
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.FascorMessage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class CacheManager {
	private static Logger log = Logger.getLogger(CacheManager.class);
	
	public static String get_FascorMessage_FixedPart(String messageType, String action) {
		String newFascorMessage_FixedPart;
		
		if ((messageType.compareTo("3052") == 0) || (messageType.compareTo("3057") == 0)) {
			newFascorMessage_FixedPart = messageType + Parms.FASCOR_FACILITY_NBR + action;
		} else {
			newFascorMessage_FixedPart = messageType + action + Parms.FASCOR_FACILITY_NBR;			
		}
		
		return newFascorMessage_FixedPart;
	}

	// ------------------------------------------------------------------------------------------------------

	public static long debug_time_wds = 0, debug_time_fascor = 0;
	
	public static void sendFascorMessages(Connection wdsConnection, Connection sqlServerConnection, Message message) throws SQLException {
	    ArrayList<Message> messageAL = new ArrayList<Message>(10);
	    messageAL.add(message);
		sendFascorMessages(wdsConnection, sqlServerConnection, messageAL);
	}
	
	public static void sendFascorMessages(Connection wdsConnection, Connection sqlServerConnection, ArrayList<Message> messageAL) throws SQLException {
		if ((messageAL == null) || (messageAL.size() == 0)) {
			return;
		}
		
		String messageType, key1;
		{
			Message message = messageAL.get(0);
			messageType = message.getMessageType();
			key1= message.getKey1();
		}
		
		ArrayList<String> newKey2AL = new ArrayList<String>(20);
		for (Iterator<Message> iterator = messageAL.iterator(); iterator.hasNext();) {
			Message message = (Message) iterator.next();
			newKey2AL.add(message.getKey2());
		}
		
		ArrayList<String> oldKey2AL, oldFascorMessageStr_VarPart_AL;
		{
			String queryString = "select key2, fascorMessage from pub.FascorMessageCache " +
								 " where messageType = '" + messageType + "' and key1 = '" + Utils.replaceSingleQuotesIfNotNull(key1) + "' " +
								 " order by key2";
			ArrayList<String>[] arrayLists = JDBCUtils.runQuery_ReturnStrALs(wdsConnection, queryString);
			if ((arrayLists == null) || (arrayLists[0] == null)) {
				oldKey2AL = new ArrayList<String>(1);
				oldFascorMessageStr_VarPart_AL =  new ArrayList<String>(1);
			} else {
				oldKey2AL = arrayLists[0];
				oldFascorMessageStr_VarPart_AL = arrayLists[1];
			}
		}
		
		// log.error("ZYX - Cache select took " + FasUtils.trackTimeE());
		
		for (int i = 0; i < oldKey2AL.size(); i++) {
			String oldKey2 = oldKey2AL.get(i);
			String oldFascorMessageStr_VarPart = oldFascorMessageStr_VarPart_AL.get(i);
			
			if (newKey2AL.indexOf(oldKey2) < 0) {
				log.debug("CACHE--- Found in cache, but is not in current messages, so deleting key1:" + key1 + ", oldkey2:" + oldKey2);
				
				String fullFascorMessage = get_FascorMessage_FixedPart(messageType, "D") + oldFascorMessageStr_VarPart;
				FasUtils.sendMessageToFascor(sqlServerConnection, fullFascorMessage);
				
				String deleteQueryString =
				       "delete from pub.FascorMessageCache where messageType = '" + messageType + "' and " +
				       									        "key1 = '" + Utils.replaceSingleQuotesIfNotNull(key1) + "' and " +
				        				        				"key2 = '" + Utils.replaceSingleQuotesIfNotNull(oldKey2) + "'"; 
				JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, deleteQueryString);				
			}
		}
		
		// log.error("ZYX - Cache delete took " + FasUtils.trackTimeE());
		
		for (int i = 0; i < newKey2AL.size(); i++) {		
			String newKey2 = newKey2AL.get(i);
			Message message_new = messageAL.get(i);
			
			int indexIntoOldKey2AL = oldKey2AL.indexOf(newKey2);
			
			if (indexIntoOldKey2AL < 0) {
				log.debug("CACHE--- Didn't find cache for key1:" + key1 + ", key2:" + newKey2);
				
				String newFascorMessageStr_VariablePart = message_new.getFascorMessage_VariablePart();
				
				String fullFascorMessageStr = get_FascorMessage_FixedPart(messageType, "A") + newFascorMessageStr_VariablePart;
				
				long startTime = System.currentTimeMillis();
				FasUtils.sendMessageToFascor(sqlServerConnection, fullFascorMessageStr);
				debug_time_fascor += (System.currentTimeMillis() - startTime);
				
				String insertQueryString =
				        "insert into pub.FascorMessageCache (messageType, key1, key2, fascorMessage) " +
						"values ('" + messageType + "', " + "'" + Utils.replaceSingleQuotesIfNotNull(key1) + "', "     +
						        "'" +    Utils.replaceSingleQuotesIfNotNull(newKey2) + "', " + "'" + Utils.replaceSingleQuotesIfNotNull(newFascorMessageStr_VariablePart) + "')";
				startTime = System.currentTimeMillis();
				JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, insertQueryString);
				debug_time_wds += (System.currentTimeMillis() - startTime);			
				
				// log.error("ZYX - Cache insert-1 took " + FasUtils.trackTimeE());
			} else {
				String newFascorMessageStr_VariablePart = message_new.getFascorMessage_VariablePart();
				String oldFascorMessageStr_VariablePart = oldFascorMessageStr_VarPart_AL.get(indexIntoOldKey2AL);
				
				if (newFascorMessageStr_VariablePart.compareTo(oldFascorMessageStr_VariablePart) != 0) {
					log.debug("CACHE--- Cache didn't match for key1:" + key1 + ", key2:" + newKey2);

					String fullFascorMessage = get_FascorMessage_FixedPart(messageType, "C") + newFascorMessageStr_VariablePart;
					FasUtils.sendMessageToFascor(sqlServerConnection, fullFascorMessage);
					
					String insertQueryString =
					        "update pub.FascorMessageCache set fascorMessage = '" + Utils.replaceSingleQuotesIfNotNull(newFascorMessageStr_VariablePart) + "' "+
							 " where messageType = '" + messageType + "' and " +
							 "       key1 = '" + Utils.replaceSingleQuotesIfNotNull(key1) + "' and " +
							 "       key2 = '" + Utils.replaceSingleQuotesIfNotNull(newKey2) + "'";
					JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, insertQueryString);

					// log.error("ZYX - Cache insert-2 took " + FasUtils.trackTimeE());					
				} else {
					log.debug("CACHE--- Cache matched for key1:" + key1 + ", key2:" + newKey2);					
				}
			}
		}
	}

	// ------------------------------------------------------------------------------------------------------
	
	public static void deleteFascorMessages(Connection wdsConnection, Connection sqlServerConnection, String messageType, String key1) throws SQLException {
		ArrayList<String> oldKey2AL, oldFascorMessageStr_VarPart_AL;
		{
			String queryString = "select key2, fascorMessage from pub.FascorMessageCache " +
								 " where messageType = '" + messageType + "' and key1 = '" + Utils.replaceSingleQuotesIfNotNull(key1) + "' " +
								 " order by key2";
			ArrayList<String>[] arrayLists = JDBCUtils.runQuery_ReturnStrALs(wdsConnection, queryString);
			if ((arrayLists == null) || (arrayLists[0] == null)) {
				return;
			} else {
				oldKey2AL = arrayLists[0];
				oldFascorMessageStr_VarPart_AL = arrayLists[1];
			}
		}
		
		for (int i = 0; i < oldKey2AL.size(); i++) {
			String oldKey2 = oldKey2AL.get(i);
			String oldFascorMessageStr_VarPart = oldFascorMessageStr_VarPart_AL.get(i);
			
			String fullFascorMessage = get_FascorMessage_FixedPart(messageType, "D") + oldFascorMessageStr_VarPart;
			FasUtils.sendMessageToFascor(sqlServerConnection, fullFascorMessage);
			
			String deleteQueryString =
			       "delete from pub.FascorMessageCache where messageType = '" + messageType + "' and " +
			       									        "key1 = '" + Utils.replaceSingleQuotesIfNotNull(key1) + "' and " +
			        				        				"key2 = '" + Utils.replaceSingleQuotesIfNotNull(oldKey2) + "'"; 
			JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, deleteQueryString);				
		}
	}

}
