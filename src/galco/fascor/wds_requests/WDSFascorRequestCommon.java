package galco.fascor.wds_requests;

import galco.fascor.messages.Message_1110;
import galco.fascor.utils.FasUtils;
import galco.portal.config.Parms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class WDSFascorRequestCommon implements Comparable {
	private static Logger log = Logger.getLogger(WDSFascorRequestCommon.class);

	protected String sequenceNumber, processed, messageType, action, data, transTime, key;

    // ------------------------------------------------------------------------------------------------

	public static SimpleDateFormat eeeMmmDateFormatWFRC = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
    
	public WDSFascorRequestCommon(HashMap<String, Object> rowHashMap) throws Exception {
		String functionCode;
		
		sequenceNumber = (String) rowHashMap.get("sequenceNumber");
		processed = (String) rowHashMap.get("processed");
		functionCode = (String) rowHashMap.get("functionCode");
		data = (String) rowHashMap.get("data");
		key = (String) rowHashMap.get("key");
		
		if (StringUtils.isBlank(sequenceNumber) == true) {
			throw new Exception("sequenceNumber is blanks, sequenceNumber: " + sequenceNumber);
		}
		if (StringUtils.isBlank(processed) == true) {
			throw new Exception("processed is blanks, processed: " + processed);
		}
		if (StringUtils.isBlank(functionCode) == true) {
			throw new Exception("functionCode is blanks, functionCode: " + functionCode);
		}
		if (StringUtils.isBlank(data) == true) {
			throw new Exception("data is blanks, data: " + data);
		}
		if (StringUtils.isBlank(key) == true) {
			throw new Exception("key is blanks, key: " + key);
		}

		if (functionCode.length() < 5) {
			throw new Exception("Invalid functionCode, functionCode: " + functionCode);
		}
		/*
		if (data.length() < 30) {
			throw new Exception("Invalid data, must be atleast 30 bytes long, data: " + data);
		}
		*/

		messageType = functionCode.substring(0, 4);
		action = functionCode.substring(4, 5);
		
		
		
		
		
		
		
		// transTime = data.substring(0, 30).trim();
		if (data == null) {
			transTime = String.format("%1$30s", eeeMmmDateFormatWFRC.format(new Date()));
		} else {
			if (data.length() >= 30) {
				transTime = data.substring(0, 30).trim();				
			} else {
				transTime = data.trim();
			}
		}
		
		
		
		
		
		
		
		/*
        log.debug("sequenceNumber : " + sequenceNumber);
        log.debug("processed      : " + processed);
        log.debug("functionCode   : " + functionCode);
        log.debug("messageType    : " + messageType);
        log.debug("action         : " + action);
        log.debug("data           : " + data);
        log.debug("transTime      : " + transTime);
        */
    }

	public WDSFascorRequestCommon(String sequenceNumber, String processed, String messageType, String action, String data, String transTime, String key) {
		this.sequenceNumber = sequenceNumber;
		this.processed = processed;
		this.messageType = messageType;
		this.action = action;
		this.data = data;
		this.transTime  = transTime;
		this.key = key;
	}

	public WDSFascorRequestCommon(WDSFascorRequestCommon wdsFascorRequestCommon) {
		this(wdsFascorRequestCommon.getSequenceNumber(),
			  wdsFascorRequestCommon.getProcessed(),
			  wdsFascorRequestCommon.getMessageType(),
			  wdsFascorRequestCommon.getAction(),
			  wdsFascorRequestCommon.getData(),
			  wdsFascorRequestCommon.getTransTime(),
			  wdsFascorRequestCommon.getKey());
	}

	// ------------------------------------------------------------------------------------------------

	public void print() {
		/*
		log.debug("sequenceNumber: " + sequenceNumber);
		log.debug("processed     : " + processed);
		log.debug("messageType   : " + messageType);
		log.debug("action        : " + action);
		log.debug("data          : " + data);
		log.debug("transTime     : " + transTime);
		*/

		log.debug(FasUtils.convertToFixedLength(sequenceNumber, 20) + " " + processed + " " + messageType + " " + action + " " + FasUtils.convertToFixedLength(data, 60) + " " + transTime);
	}
	
	public String toString() {
		return FasUtils.convertToFixedLength(sequenceNumber, 20) + " , " + processed + " , " + messageType + " , " + action + " , " + FasUtils.convertToFixedLength(data, 60) + " , " + transTime;
	}
	
	// ------------------------------------------------------------------------------------------------

    public String getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

    public String getProcessed() {
		return processed;
	}

	public void setProcessed(String processed) {
		this.processed = processed;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	// ------------------------------------------------------------------------------------------------

	public static ArrayList<String> SORT_ORDER_AL = new ArrayList<String>(Arrays.asList(new String[] {"0001", "0140", "1110", "3052", "2015", "1210", "1220", "1310", "1320"}));
	
	public int compareTo(Object object) {
		if (object == null) {
			return 1;
		}

		if (object instanceof WDSFascorRequestCommon) {
			WDSFascorRequestCommon wdsFascorRequestCommonOther = (WDSFascorRequestCommon) object;
			
			if (getMessageType().compareTo(wdsFascorRequestCommonOther.getMessageType()) != 0) {
				// return getMessageType().compareTo(wdsFascorRequestCommonOther.getMessageType());
				
				int thisIndex = SORT_ORDER_AL.indexOf(getMessageType());
				int otherIndex = SORT_ORDER_AL.indexOf(wdsFascorRequestCommonOther.getMessageType());
				
				if (thisIndex > otherIndex) {
					return 1;
				} else {
					return -1;
				}
			} else {
				return getAction().compareTo(wdsFascorRequestCommonOther.getAction());
			}
		} else {
			return 1;
		}		
	}
	
	// ------------------------------------------------------------------------------------------------

	public static void main(String[] args) {
		/*
		log.debug(SORT_ORDER_AL.indexOf("0001"));
		log.debug(SORT_ORDER_AL.indexOf("0140"));
		log.debug(SORT_ORDER_AL.indexOf("1110"));
		log.debug(SORT_ORDER_AL.indexOf("1210"));
		log.debug(SORT_ORDER_AL.indexOf("1310"));
		log.debug(SORT_ORDER_AL.indexOf("3052"));
		*/
		
		try {
			String oldUserVerificationURL = "https://www.galco.com/scripts/cgiip.exe/wa/wcat/portal-user-verification.htm?cust_num=001832&cont_no=5&password=2321wolcott&secret_code=MroEgINdiveeNA";
			log.debug("oldUserVerificationURL: " + oldUserVerificationURL);

			HttpsURLConnection con = (HttpsURLConnection) new URL(oldUserVerificationURL).openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	        String responseFromVerProcess = in.readLine();
	        log.debug("Response from Verification Webspeed Script: " + responseFromVerProcess);
	        in.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
        
        
	}
}
