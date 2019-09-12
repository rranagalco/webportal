package galco.fascor.wds_requests;

public interface WDSFascorRequest {
    public String getSequenceNumber();
	public void setSequenceNumber(String sequenceNumber);

    public String getProcessed();
	public void setProcessed(String processed);

	public String getMessageType();
	public void setMessageType(String messageType);

	public String getAction();
	public void setAction(String action);

	public String getData();
	public void setData(String data);
	
	public String getKey();
	public void setKey(String data);

	public String getTransTime();
	public void setTransTime(String transTime);
}
