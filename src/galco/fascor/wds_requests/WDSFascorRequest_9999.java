package galco.fascor.wds_requests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class WDSFascorRequest_9999 extends WDSFascorRequestCommon implements WDSFascorRequest {
	private static Logger log = Logger.getLogger(WDSFascorRequest_9999.class);
	
	String receiver_num;
	String order_num;
	int line_num;
	int qty_voided;

    // ------------------------------------------------------------------------------------------------

	public WDSFascorRequest_9999(HashMap<String, Object> rowHashMap) throws Exception {
		super(rowHashMap);
		parseData();
	}

	public WDSFascorRequest_9999(WDSFascorRequestCommon wdsFascorRequestCommon) throws Exception {
		super(wdsFascorRequestCommon);
		parseData();
	}
	
	private void parseData()  throws Exception {
		String receiver_numANDorder_numANDline_numANDQtyVoided = "";
		
		try {
			receiver_numANDorder_numANDline_numANDQtyVoided = key.trim();

			List<String> list = Arrays.asList(receiver_numANDorder_numANDline_numANDQtyVoided.split(","));
			if ((list == null) || (list.size() != 4)) {
				throw new Exception("Invalid data, need to have receiver_num, comma, order_num, comma, line_num, comma, qty_voided. Data found: " + receiver_numANDorder_numANDline_numANDQtyVoided);
			}

			receiver_num = list.get(0).trim();
			if (StringUtils.isBlank(receiver_num) == true) {
				throw new Exception("Invalid data, receiver_num is blanks. receiver_numANDorder_numANDline_numANDQtyVoided: " + receiver_numANDorder_numANDline_numANDQtyVoided);
			}

			order_num = list.get(1).trim();
			if (StringUtils.isBlank(order_num) == true) {
				throw new Exception("Invalid data, order_num is blanks. receiver_numANDorder_numANDline_numANDQtyVoided: " + receiver_numANDorder_numANDline_numANDQtyVoided);
			}

			try {
				line_num = new Integer(list.get(2).trim());							
			} catch (Exception e) {
				throw new Exception("Invalid data, line_num is invalid. receiver_numANDorder_numANDline_numANDQtyVoided: " + receiver_numANDorder_numANDline_numANDQtyVoided);
			}

			try {
				qty_voided = new Integer(list.get(3).trim());
			} catch (Exception e) {
				throw new Exception("Invalid data, qty_voided is invalid. receiver_numANDorder_numANDline_numANDQtyVoided: " + receiver_numANDorder_numANDline_numANDQtyVoided);
			}
		} catch (Exception e) {
			throw new Exception("Invalid data for Msg 9999. receiver_numANDorder_numANDline_numANDQtyVoided: " + receiver_numANDorder_numANDline_numANDQtyVoided);
		}
	}

	// ------------------------------------------------------------------------------------------------

	public int compareTo(Object object) {
		if (object == null) {
			return 1;
		}

		if (object instanceof WDSFascorRequest_9999) {
			WDSFascorRequest_9999 WDSFascorRequest_9999Other = (WDSFascorRequest_9999) object;

			if (getReceiver_num().compareTo(WDSFascorRequest_9999Other.getReceiver_num()) != 0) {
				return getReceiver_num().compareTo(WDSFascorRequest_9999Other.getReceiver_num());
			} else {
				if (getOrder_num().compareTo(WDSFascorRequest_9999Other.getOrder_num()) != 0) {
					return getOrder_num().compareTo(WDSFascorRequest_9999Other.getOrder_num());
				} else {
					if (getLine_num() != WDSFascorRequest_9999Other.getLine_num()) {
						return ((getLine_num() > WDSFascorRequest_9999Other.getLine_num())?1:-1);
					} else {
						if (getQty_voided() != WDSFascorRequest_9999Other.getQty_voided()) {
							return ((getQty_voided() > WDSFascorRequest_9999Other.getQty_voided())?1:-1);
						} else {
							return super.compareTo(object);
						}
					}
				}
			}
		} else {
			return super.compareTo(object);
		}		
	}

	// ------------------------------------------------------------------------------------------------

	public void print() {
		super.print();
		
		log.debug("receiver_num  : " + receiver_num);
		log.debug("order_num     : " + order_num);
		log.debug("line_num      : " + line_num);
		log.debug("qty_voided    : " + qty_voided);
	}

    // ------------------------------------------------------------------------------------------------

	public String getReceiver_num() {
		return receiver_num;
	}
	public void setReceiver_num(String receiver_num) {
		this.receiver_num = receiver_num;
	}

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

	public int getQty_voided() {
		return qty_voided;
	}
	public void setQty_voided(int qty_voided) {
		this.qty_voided = qty_voided;
	}

}
