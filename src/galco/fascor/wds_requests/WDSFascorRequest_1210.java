package galco.fascor.wds_requests;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class WDSFascorRequest_1210 extends WDSFascorRequestCommon implements WDSFascorRequest {
	private static Logger log = Logger.getLogger(WDSFascorRequest_1210.class);
	
	String order_num;

    // ------------------------------------------------------------------------------------------------

	public WDSFascorRequest_1210(HashMap<String, Object> rowHashMap) throws Exception {
		super(rowHashMap);

		/*
		if (data.length() < 31) {
			throw new Exception("Invalid data, must be atleast 31 bytes long, data: " + data);
		}
		*/
		
		order_num = key.trim();
		if (StringUtils.isBlank(order_num) == true) {
			throw new Exception("Invalid data, order_num is blanks, order_num: " + order_num);
		}
	}

	public WDSFascorRequest_1210(WDSFascorRequestCommon wdsFascorRequestCommon) throws Exception {
		super(wdsFascorRequestCommon);

		/*
		if (data.length() < 31) {
			throw new Exception("Invalid data, must be atleast 31 bytes long, data: " + data);
		}
		*/
		
		order_num = key.trim();
		if (StringUtils.isBlank(order_num) == true) {
			throw new Exception("Invalid data, order_num is blanks, order_num: " + order_num);
		}
	}

	// ------------------------------------------------------------------------------------------------

	public int compareTo(Object object) {
		if (object == null) {
			return 1;
		}

		if (object instanceof WDSFascorRequest_1210) {
			WDSFascorRequest_1210 wdsFascorRequest_1210Other = (WDSFascorRequest_1210) object;
			
			if (getOrder_num().compareTo(wdsFascorRequest_1210Other.getOrder_num()) != 0) {
				return getOrder_num().compareTo(wdsFascorRequest_1210Other.getOrder_num());
			} else {
				return super.compareTo(object);
			}
		} else {
			return super.compareTo(object);
		}		
	}

	// ------------------------------------------------------------------------------------------------

	public void print() {
		super.print();
		log.debug("order_num     : " + order_num);
	}
	
    // ------------------------------------------------------------------------------------------------

	public String getOrder_num() {
		return order_num;
	}

	public void setOrder_num(String order_num) {
		this.order_num = order_num;
	}
}
