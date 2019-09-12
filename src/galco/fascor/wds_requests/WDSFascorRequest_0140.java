package galco.fascor.wds_requests;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class WDSFascorRequest_0140 extends WDSFascorRequestCommon implements WDSFascorRequest {
	private static Logger log = Logger.getLogger(WDSFascorRequest_0140.class);
	
	String vendor_num;
	
    // ------------------------------------------------------------------------------------------------
		
	public WDSFascorRequest_0140(HashMap<String, Object> rowHashMap) throws Exception {
		super(rowHashMap);

		/*
		if (data.length() < 31) {
			throw new Exception("Invalid data, must be atleast 31 bytes long, data: " + data);
		}
		*/
		
		vendor_num = key.trim();
		if (StringUtils.isBlank(vendor_num) == true) {
			throw new Exception("Invalid data, vendor_num is blanks, vendor_num: " + vendor_num);
		}
	}

	public WDSFascorRequest_0140(WDSFascorRequestCommon wdsFascorRequestCommon) throws Exception {
		super(wdsFascorRequestCommon);

		/*
		if (data.length() < 31) {
			throw new Exception("Invalid data, must be atleast 31 bytes long, data: " + data);
		}
		*/
		
		vendor_num = key.trim();
		if (StringUtils.isBlank(vendor_num) == true) {
			throw new Exception("Invalid data, vendor_num is blanks, vendor_num: " + vendor_num);
		}
	}

	// ------------------------------------------------------------------------------------------------

	public int compareTo(Object object) {
		if (object == null) {
			return 1;
		}

		if (object instanceof WDSFascorRequest_0140) {
			WDSFascorRequest_0140 wdsFascorRequest_0140Other = (WDSFascorRequest_0140) object;
			
			if (getVendor_num().compareTo(wdsFascorRequest_0140Other.getVendor_num()) != 0) {
				return getVendor_num().compareTo(wdsFascorRequest_0140Other.getVendor_num());
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
		log.debug("vendor_num    : " + vendor_num);
	}

    // ------------------------------------------------------------------------------------------------

	public String getVendor_num() {
		return vendor_num;
	}

	public void setVendor_num(String Vendor_num) {
		this.vendor_num = vendor_num;
	}
}
