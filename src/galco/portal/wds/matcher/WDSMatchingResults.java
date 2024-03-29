package galco.portal.wds.matcher;

import galco.portal.utils.Utils;

import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class WDSMatchingResults {
	private static Logger log = Logger.getLogger(WDSMatchingResults.class);

	private int matchingStatus;
		public static final int EMAIL_MATCHED = 1;
		public static final int EMAIL_MATCHED__AND__PHONE_MATCHED_CONTACT = 2;
		public static final int EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT = 3;
		public static final int EMAIL_MATCHED__AND__PHONE_MATCHED_CUST = 4;
		public static final int EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST = 5;
		public static final int EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUST = 6;
		public static final int EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST = 7;
		public static final int EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUSTBILL = 8;
		public static final int EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL = 9;

		public static final int EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_MATCHED_CUSTSHIP = 10;
		public static final int EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_DID_NOT_MATCH_CUSTSHIP = 11;

		public static final int EMAIL_DID_NOT_MATCH = 12;
		public static final int EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT = 13;
		public static final int EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_MATCHED_CUST = 14;
		public static final int EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT = 15;
		public static final int EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST = 16;
		public static final int EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_MATCHED_CUST = 17;
		public static final int EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUSTBILL = 18;
		public static final int EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL = 19;
		public static final int EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_MATCHED_CUSTSHIP = 20;
		public static final int EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_DID_NOT_MATCH_CUSTSHIP = 21;
		public static final int EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST = 22;

		public static final int EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUSTBILL = 23;
		public static final int EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL = 24;
		public static final int EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_MATCHED_CUSTSHIP = 25;
		public static final int EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_DID_NOT_MATCH_CUSTSHIP = 26;


	private HashSet<String> distinctMatchedCustNums_HS;

	// -------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------------

	public void print() {
		if (matchingStatus == EMAIL_MATCHED) {
			log.debug("EMAIL_MATCHED");
		} else if (matchingStatus == EMAIL_MATCHED__AND__PHONE_MATCHED_CONTACT) {
			log.debug("EMAIL_MATCHED__AND__PHONE_MATCHED_CONTACT");
		} else if (matchingStatus == EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT) {
			log.debug("EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT");
		} else if (matchingStatus == EMAIL_MATCHED__AND__PHONE_MATCHED_CUST) {
			log.debug("EMAIL_MATCHED__AND__PHONE_MATCHED_CUST");
		} else if (matchingStatus == EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST) {
			log.debug("EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST");
		} else if (matchingStatus == EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUST) {
			log.debug("EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUST");
		} else if (matchingStatus == EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST) {
			log.debug("EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST");
		} else if (matchingStatus == EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUSTBILL) {
			log.debug("EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUSTBILL");
		} else if (matchingStatus == EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL) {
			log.debug("EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL");
		} else if (matchingStatus == EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_MATCHED_CUSTSHIP) {
			log.debug("EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_MATCHED_CUSTSHIP");
		} else if (matchingStatus == EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_DID_NOT_MATCH_CUSTSHIP) {
			log.debug("EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_DID_NOT_MATCH_CUSTSHIP");
		} else if (matchingStatus == EMAIL_DID_NOT_MATCH) {
			log.debug("EMAIL_DID_NOT_MATCH");
		} else if (matchingStatus == EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT) {
			log.debug("EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT");
		} else if (matchingStatus == EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_MATCHED_CUST) {
			log.debug("EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_MATCHED_CUST");
		} else if (matchingStatus == EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT) {
			log.debug("EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT");
		} else if (matchingStatus == EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST) {
			log.debug("EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST");
		} else if (matchingStatus == EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_MATCHED_CUST) {
			log.debug("EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_MATCHED_CUST");
		} else if (matchingStatus == EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUSTBILL) {
			log.debug("EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUSTBILL");
		} else if (matchingStatus == EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL) {
			log.debug("EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL");
		} else if (matchingStatus == EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_MATCHED_CUSTSHIP) {
			log.debug("EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_MATCHED_CUSTSHIP");
		} else if (matchingStatus == EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_DID_NOT_MATCH_CUSTSHIP) {
			log.debug("EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_DID_NOT_MATCH_CUSTSHIP");
		} else if (matchingStatus == EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST) {
			log.debug("EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST");
		} else if (matchingStatus == EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUSTBILL) {
			log.debug("EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUSTBILL");
		} else if (matchingStatus == EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL) {
			log.debug("EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL");
		} else if (matchingStatus == EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_MATCHED_CUSTSHIP) {
			log.debug("EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_MATCHED_CUSTSHIP");
		} else if (matchingStatus == EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_DID_NOT_MATCH_CUSTSHIP) {
			log.debug("EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_DID_NOT_MATCH_CUSTSHIP");
		}

		if (distinctMatchedCustNums_HS == null) {
			log.debug("\nThere are no matches.\n");
		} else {
			log.debug("\nMatched customer numbers:\n");
			for (Iterator<String> iterator = distinctMatchedCustNums_HS.iterator(); iterator.hasNext();) {
				String custNum = iterator.next();
				log.debug(custNum);
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------------

	public int getMatchingStatus() {
		return matchingStatus;
	}
	public void setMatchingStatus(int matchingStatus) {
		this.matchingStatus = matchingStatus;
	}
	public HashSet<String> getDistinctMatchedCustNums_HS() {
		return distinctMatchedCustNums_HS;
	}
	public void setDistinctMatchedCustNums_HS(
			HashSet<String> distinctMatchedCustNums_HS) {
		this.distinctMatchedCustNums_HS = distinctMatchedCustNums_HS;
	}
}
