package galco.fascor.messages;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public interface Message {
    public HashMap<Integer, String> getFieldsHM();
    public HashMap<Integer, Integer> getLengthsHM();
    public HashMap<Integer, Integer> getFieldFormatsHM();
    public HashMap<Integer, Integer> getStartingPosHM();

    public HashMap<String, Object> getDataHM();
    
    public String getMessageType();
    public void setMessageType(String messageType);
    public String getAction();
    public void setAction(String action);
    
    public String getKey1();
    public String getKey2();
    public String getFascorMessage();
    public String getFascorMessage_VariablePart();    
}
