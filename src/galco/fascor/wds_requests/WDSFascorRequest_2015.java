package galco.fascor.wds_requests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class WDSFascorRequest_2015 extends WDSFascorRequestCommon implements WDSFascorRequest {
	private static Logger log = Logger.getLogger(WDSFascorRequest_2015.class);
	
	String order_num;
	String part_num;
	int quantityToBuild;

    // ------------------------------------------------------------------------------------------------

	public WDSFascorRequest_2015(HashMap<String, Object> rowHashMap) throws Exception {
		super(rowHashMap);
		parseData();
	}

	public WDSFascorRequest_2015(WDSFascorRequestCommon wdsFascorRequestCommon) throws Exception {
		super(wdsFascorRequestCommon);
		parseData();
	}
	
	private void parseData()  throws Exception {
		/*
		if (data.length() < 31) {
			throw new Exception("Invalid data, must be atleast 31 bytes long, data: " + data);
		}
		*/

		String part_numANDquantityToBuild = key.trim();

		try {
			List<String> list = Arrays.asList(part_numANDquantityToBuild.split(","));
			if ((list == null) || ((list.size() != 2) && (list.size() != 3))) {
				throw new Exception("Invalid data, need to have (order_num, a comma) (optional), part_num, a comma, and a quantityToBuild. Data: " + part_numANDquantityToBuild);
			}

			if (list.size() == 2) {
				order_num = null;
				part_num = list.get(0).trim();
				quantityToBuild = new Integer(list.get(1).trim());							
			} else {
				order_num = list.get(0).trim();
				part_num = list.get(1).trim();
				quantityToBuild = new Integer(list.get(2).trim());
			}
		} catch (Exception e) {
			throw new Exception("Invalid data. part_numANDquantityToBuild: " + part_numANDquantityToBuild);
		}
	}

	// ------------------------------------------------------------------------------------------------

	public int compareTo(Object object) {
		if (object == null) {
			return 1;
		}

		if (object instanceof WDSFascorRequest_2015) {
			WDSFascorRequest_2015 wdsFascorRequest_2015Other = (WDSFascorRequest_2015) object;
			
			if (getPart_num().compareTo(wdsFascorRequest_2015Other.getPart_num()) != 0) {
				return getPart_num().compareTo(wdsFascorRequest_2015Other.getPart_num());
			} else {
				if (getQuantityToBuild() != wdsFascorRequest_2015Other.getQuantityToBuild()) {
					return ((getQuantityToBuild() > wdsFascorRequest_2015Other.getQuantityToBuild())?1:-1);
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
		log.debug("part_num        : " + part_num);
		log.debug("quantityToBuild : " + quantityToBuild);
	}

    // ------------------------------------------------------------------------------------------------

	public String getOrder_num() {
		return order_num;
	}
	public void setOrder_num(String order_num) {
		this.order_num = order_num;
	}

	public String getPart_num() {
		return part_num;
	}
	public void setPart_num(String part_num) {
		this.part_num = part_num;
	}
	
	public int getQuantityToBuild() {
		return quantityToBuild;
	}
	public void setQuantityToBuild(int quantityToBuild) {
		this.quantityToBuild = quantityToBuild;
	}

}
