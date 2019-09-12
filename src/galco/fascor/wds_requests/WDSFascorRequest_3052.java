package galco.fascor.wds_requests;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class WDSFascorRequest_3052 extends WDSFascorRequestCommon implements WDSFascorRequest {
	private static Logger log = Logger.getLogger(WDSFascorRequest_3052.class);
	
	String part_num;

    // ------------------------------------------------------------------------------------------------
		
	public WDSFascorRequest_3052(HashMap<String, Object> rowHashMap) throws Exception {
		super(rowHashMap);

		/*
		if (data.length() < 31) {
			throw new Exception("Invalid data, must be atleast 31 bytes long, data: " + data);
		}
		*/
		
		part_num = key.trim();
		if (StringUtils.isBlank(part_num) == true) {
			throw new Exception("Invalid data, part_num is blanks, part_num: " + part_num);
		}
	}

	public WDSFascorRequest_3052(WDSFascorRequestCommon wdsFascorRequestCommon) throws Exception {
		super(wdsFascorRequestCommon);

		/*
		if (data.length() < 31) {
			throw new Exception("Invalid data, must be atleast 31 bytes long, data: " + data);
		}
		*/
		
		part_num = key.trim();
		if (StringUtils.isBlank(part_num) == true) {
			throw new Exception("Invalid data, part_num is blanks, part_num: " + part_num);
		}
	}

	// ------------------------------------------------------------------------------------------------

	public int compareTo(Object object) {
		if (object == null) {
			return 1;
		}

		if (object instanceof WDSFascorRequest_3052) {
			WDSFascorRequest_3052 wdsFascorRequest_3052Other = (WDSFascorRequest_3052) object;
			
			if (getPart_num().compareTo(wdsFascorRequest_3052Other.getPart_num()) != 0) {
				return getPart_num().compareTo(wdsFascorRequest_3052Other.getPart_num());
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
		log.debug("part_num      : " + part_num);
	}
	
    // ------------------------------------------------------------------------------------------------

	public String getPart_num() {
		return part_num;
	}

	public void setPart_num(String part_num) {
		this.part_num = part_num;
	}
}
