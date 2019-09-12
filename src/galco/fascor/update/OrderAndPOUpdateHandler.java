package galco.fascor.update;

import galco.fascor.messages.Message_1310;
import galco.fascor.requests.control.FascorInboundMessageHandler;
import galco.fascor.utils.FasUtils;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;
import galco.portal.wds.dao.FascorMessage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class OrderAndPOUpdateHandler {
	private static HashMap<String, FascorMessage> buildHMFromKey2AndKey3(ArrayList<FascorMessage> fascorMessageAL) {
		HashMap<String, FascorMessage> key2Key3HM = new HashMap<String, FascorMessage>(20);

		for (Iterator<FascorMessage> iterator = fascorMessageAL.iterator(); iterator.hasNext();) {
			FascorMessage fascorMessage = iterator.next();
			key2Key3HM.put(fascorMessage.getKey2() + fascorMessage.getKey3(), fascorMessage);
		}

		return key2Key3HM;
	}

	public static void update(Connection wdsConnection, Connection sqlServerConnection, String messageType, String key1, FascorMessage newFascorMessageForOrder, ArrayList<FascorMessage> newFascorMessagesForLinesAL) throws SQLException {
		String messageTypeLine = "";
		int actualFascorMessageStartingPosition = 5;
		if (messageType.compareTo("1210") == 0) {
			 messageTypeLine = "1220";
			 actualFascorMessageStartingPosition = 5;
		} else if (messageType.compareTo("1310") == 0) {
			 messageTypeLine = "1320";
			 actualFascorMessageStartingPosition = 5;
		} else if (messageType.compareTo("3052") == 0) {
			 messageTypeLine = "3057";
			 actualFascorMessageStartingPosition = 7;
		}

		FascorMessage oldFascorMessageForOrder = null;
		ArrayList<FascorMessage> oldFascorMessagesForLinesAL = new ArrayList<FascorMessage>(1);

		ArrayList<FascorMessage> oldFascorMessageAL = FascorMessage.getDistinctFascorMessages(wdsConnection, messageType, key1);
		if ((oldFascorMessageAL != null) && (oldFascorMessageAL.size() > 0)) {
			oldFascorMessageForOrder = oldFascorMessageAL.get(0);

            if (newFascorMessageForOrder.getFascormessage().substring(actualFascorMessageStartingPosition).compareTo(oldFascorMessageForOrder.getFascormessage().substring(actualFascorMessageStartingPosition)) != 0) {
				String changeFascorMessageStr = newFascorMessageForOrder.getFascormessage().substring(0, actualFascorMessageStartingPosition - 1) + "C" + newFascorMessageForOrder.getFascormessage().substring(actualFascorMessageStartingPosition);
				FasUtils.sendMessageToFascor(sqlServerConnection, newFascorMessageForOrder.getSequencenumber(), changeFascorMessageStr);
				newFascorMessageForOrder.setFascormessage(changeFascorMessageStr);
				newFascorMessageForOrder.persist(wdsConnection);

				oldFascorMessageForOrder.delete(wdsConnection);
            }

			oldFascorMessagesForLinesAL = FascorMessage.getDistinctFascorMessages(wdsConnection, messageTypeLine, key1);
		} else {
			String addFascorMessageStr = newFascorMessageForOrder.getFascormessage().substring(0, actualFascorMessageStartingPosition - 1) + "A" + newFascorMessageForOrder.getFascormessage().substring(actualFascorMessageStartingPosition);
			FasUtils.sendMessageToFascor(sqlServerConnection, newFascorMessageForOrder.getSequencenumber(), addFascorMessageStr);
			newFascorMessageForOrder.setFascormessage(addFascorMessageStr);
			newFascorMessageForOrder.persist(wdsConnection);
		}


		HashMap<String, FascorMessage> key2Key3FMOldHM = buildHMFromKey2AndKey3(oldFascorMessagesForLinesAL);
		HashMap<String, FascorMessage> key2Key3FMNewHM = buildHMFromKey2AndKey3(newFascorMessagesForLinesAL);


		// delete old FascorMessage line records for lines that were deleted from the order
		for (Iterator<FascorMessage> iterator = oldFascorMessagesForLinesAL.iterator(); iterator.hasNext();) {
			FascorMessage fascorMessageForLineOld = iterator.next();
			String key2Key3Old = fascorMessageForLineOld.getKey2() + fascorMessageForLineOld.getKey3();

			if (key2Key3FMNewHM.get(key2Key3Old) == null) {
				String deleteFascorMessageStr = fascorMessageForLineOld.getFascormessage().substring(0, actualFascorMessageStartingPosition - 1) + "D" + fascorMessageForLineOld.getFascormessage().substring(actualFascorMessageStartingPosition);
				FasUtils.sendMessageToFascor(sqlServerConnection, fascorMessageForLineOld.getSequencenumber(), deleteFascorMessageStr);

				fascorMessageForLineOld.delete(wdsConnection);
			}
		}


		for (Iterator<FascorMessage> iterator = newFascorMessagesForLinesAL.iterator(); iterator.hasNext();) {
			FascorMessage fascorMessageForLineNew = iterator.next();
			String key2Key3New = fascorMessageForLineNew.getKey2() + fascorMessageForLineNew.getKey3();

			FascorMessage fascorMessageForLineOld = key2Key3FMOldHM.get(key2Key3New);

			if (fascorMessageForLineOld == null) {
				String addFascorMessageStr = fascorMessageForLineNew.getFascormessage().substring(0, actualFascorMessageStartingPosition - 1) + "A" + fascorMessageForLineNew.getFascormessage().substring(actualFascorMessageStartingPosition);
				FasUtils.sendMessageToFascor(sqlServerConnection, fascorMessageForLineNew.getSequencenumber(), addFascorMessageStr);
				fascorMessageForLineNew.setFascormessage(addFascorMessageStr);
				fascorMessageForLineNew.persist(wdsConnection);
			} else {
				if (fascorMessageForLineNew.getFascormessage().substring(actualFascorMessageStartingPosition).compareTo(fascorMessageForLineOld.getFascormessage().substring(actualFascorMessageStartingPosition)) != 0) {
					String changeFascorMessageStr = fascorMessageForLineNew.getFascormessage().substring(0, actualFascorMessageStartingPosition - 1) + "C" + fascorMessageForLineNew.getFascormessage().substring(actualFascorMessageStartingPosition);
					FasUtils.sendMessageToFascor(sqlServerConnection, fascorMessageForLineNew.getSequencenumber(), changeFascorMessageStr);
					fascorMessageForLineNew.setFascormessage(changeFascorMessageStr);
					fascorMessageForLineNew.persist(wdsConnection);

					fascorMessageForLineOld.delete(wdsConnection);
				}
			}
		}
	}
}
