package galco.fascor.wds_requests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class WDSFascorRequest_1320 extends WDSFascorRequestCommon implements WDSFascorRequest {
	private static Logger log = Logger.getLogger(WDSFascorRequest_1320.class);
	
	String order_num;
	int line_num;

    // ------------------------------------------------------------------------------------------------

	public WDSFascorRequest_1320(HashMap<String, Object> rowHashMap) throws Exception {
		super(rowHashMap);
		parseData();
	}

	public WDSFascorRequest_1320(WDSFascorRequestCommon wdsFascorRequestCommon) throws Exception {
		super(wdsFascorRequestCommon);
		parseData();
	}
	
	private void parseData()  throws Exception {
		/*
		if (data.length() < 31) {
			throw new Exception("Invalid data, must be atleast 31 bytes long, data: " + data);
		}
		*/

		String order_numANDline_num = key.trim();

		try {
			List<String> list = Arrays.asList(order_numANDline_num.split(","));
			if ((list == null) || (list.size() != 2)) {
				throw new Exception("Invalid data, need to have order_num, a comma, and a line_num. Data: " + order_numANDline_num);
			}

			order_num = list.get(0).trim();
			if (StringUtils.isBlank(order_num) == true) {
				throw new Exception("Invalid data, order_num is blanks. order_numANDline_num: " + order_numANDline_num);
			}

			try {
				line_num = new Integer(list.get(1).trim());							
			} catch (Exception e) {
				throw new Exception("Invalid data, line_num is invalid. order_numANDline_num: " + order_numANDline_num);
			}
		} catch (Exception e) {
			throw new Exception("Invalid data. order_numANDline_num: " + order_numANDline_num);
		}
	}

	// ------------------------------------------------------------------------------------------------

	public int compareTo(Object object) {
		if (object == null) {
			return 1;
		}

		if (object instanceof WDSFascorRequest_1320) {
			WDSFascorRequest_1320 wdsFascorRequest_1320Other = (WDSFascorRequest_1320) object;
			
			if (getOrder_num().compareTo(wdsFascorRequest_1320Other.getOrder_num()) != 0) {
				return getOrder_num().compareTo(wdsFascorRequest_1320Other.getOrder_num());
			} else {
				if (getLine_num() != wdsFascorRequest_1320Other.getLine_num()) {
					return ((getLine_num() > wdsFascorRequest_1320Other.getLine_num())?1:-1);
				} else {
					return super.compareTo(object);
				}
			}
		} else {
			return super.compareTo(object);
		}		
	}

	// ------------------------------------------------------------------------------------------------

	public void print() {
		super.print();
		log.debug("order_num     : " + order_num);
		log.debug("line_num      : " + line_num);
	}

    // ------------------------------------------------------------------------------------------------

	public String getOrder_num() {
		return order_num;
	}
	public void setOrder_num(String order_num) {
		this.order_num = order_num;
	}

	public int getLine_num() {
		return line_num;
	}
	public void setLine_num(int line_num) {
		this.line_num = line_num;
	}

}
