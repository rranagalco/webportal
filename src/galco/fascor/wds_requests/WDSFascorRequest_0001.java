package galco.fascor.wds_requests;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class WDSFascorRequest_0001 extends WDSFascorRequestCommon implements WDSFascorRequest {
	private static Logger log = Logger.getLogger(WDSFascorRequest_0001.class);
	
	String part_num_new, part_num_old;
	
    // ------------------------------------------------------------------------------------------------

	public WDSFascorRequest_0001(HashMap<String, Object> rowHashMap) throws Exception {
		super(rowHashMap);

		/*
		if (data.length() < 31) {
			throw new Exception("Invalid data, must be atleast 31 bytes long, data: " + data);
		}
		*/
		
        String[] keyArr = key.split("\\|", 2); 

		if ((keyArr == null) || (keyArr.length < 2)) {
			throw new Exception("Invalid key, key needs to be like part_num_new|part_nul_old		. key: " + key);
		}
		
		part_num_new = keyArr[0].trim();
		part_num_old = keyArr[1].trim();
        
		if (StringUtils.isBlank(part_num_new) == true) {
			throw new Exception("Invalid data, part_num_new is blanks, part_num_new: " + part_num_new);
		}
		if (StringUtils.isBlank(part_num_old) == true) {
			throw new Exception("Invalid data, part_num_old is blanks, part_num_old: " + part_num_old);
		}
	}

	public WDSFascorRequest_0001(WDSFascorRequestCommon wdsFascorRequestCommon) throws Exception {
		super(wdsFascorRequestCommon);

		/*
		if (data.length() < 31) {
			throw new Exception("Invalid data, must be atleast 31 bytes long, data: " + data);
		}
		*/
		
        String[] keyArr = key.split("\\|", 2); 

		if ((keyArr == null) || (keyArr.length < 2)) {
			throw new Exception("Invalid key, key needs to be like part_num_new|part_nul_old		. key: " + key);
		}
		
		part_num_new = keyArr[0].trim();
		part_num_old = keyArr[1].trim();
        
		if (StringUtils.isBlank(part_num_new) == true) {
			throw new Exception("Invalid data, part_num_new is blanks, part_num_new: " + part_num_new);
		}
		if (StringUtils.isBlank(part_num_old) == true) {
			throw new Exception("Invalid data, part_num_old is blanks, part_num_old: " + part_num_old);
		}
	}

	// ------------------------------------------------------------------------------------------------

	public int compareTo(Object object) {
		if (object == null) {
			return 1;
		}

		if (object instanceof WDSFascorRequest_0001) {
			WDSFascorRequest_0001 wdsFascorRequest_0001Other = (WDSFascorRequest_0001) object;

			if (getPart_num_new().compareTo(wdsFascorRequest_0001Other.getPart_num_new()) != 0) {
				return getPart_num_new().compareTo(wdsFascorRequest_0001Other.getPart_num_new());
			} else {
				if (getPart_num_old().compareTo(wdsFascorRequest_0001Other.getPart_num_old()) != 0) {
					return getPart_num_old().compareTo(wdsFascorRequest_0001Other.getPart_num_old());
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
		log.debug("part_num_new  : " + part_num_new);
		log.debug("part_num_old  : " + part_num_old);
	}
	
    // ------------------------------------------------------------------------------------------------

	public String getPart_num_new() {
		return part_num_new;
	}

	public void setPart_num_new(String part_num_new) {
		this.part_num_new = part_num_new;
	}

	// ------------------------------------------------------------------------------------------------

	public String getPart_num_old() {
		return part_num_old;
	}

	public void setPart_num_old(String part_num_old) {
		this.part_num_old = part_num_old;
	}
	
	// ------------------------------------------------------------------------------------------------

	public static void main(String[] args) {
		String data = "subbarao-2pc1300-dura|subbarao-1-pc1300-dura";
		String[] dataArr = data.split("\\|", 2); 
		System.out.println(dataArr[0]);
		System.out.println(dataArr[1]);
	}
}
