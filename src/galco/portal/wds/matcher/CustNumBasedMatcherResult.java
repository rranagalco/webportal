package galco.portal.wds.matcher;

import galco.portal.user.signup.UserSignUpHandler;

import org.apache.log4j.Logger;

public class CustNumBasedMatcherResult {
	private static Logger log = Logger.getLogger(CustNumBasedMatcherResult.class);

	int returnCode;
		public static final int MATCHED_FOR_NO_TERMS = 1;
		public static final int MATCHED_FOR_TERMS = 2;
		
		public static final int ERROR_CUSTOMER_NUMBER_ENTERED_IS_NOT_FOUND = -1;
		public static final int ERROR_EMAIL_MATCHED_MULTIPLE_CUSTOMERS = -2;
		public static final int ERROR_PHONE_MATCHED_MULTIPLE_CUSTOMERS = -3;
		public static final int ERROR_BOTH_PHONE_AND_ADDRESS_DID_NOT_MATCH = -4;
		public static final int ERROR_EMAIL_DOMAIN_DID_NOT_MATCH = -5;
		public static final int ERROR_MISMATCH_BETWEEN_CUSTOMER_NUMBERS = -6;
		public static final int ERROR_NO_COMMON_MATCHED_CUST_NUM = -7;
		public static final int ERROR_MISMATCH_BETWEEN_CUSTOMER_NUMBER_ENTERED_AND_PHONE_OR_ADDRESS_MATCHED = -8;		
		public static final int ERROR_MATCHED_CUSTOMER_IS_INACTIVE = -9;
		public static final int ERROR_BOTH_PHONE_AND_ADDRESS_DID_NOT_MATCH_AND_DOMAIN_DID_NOT_MATCH = -10;		
		public static final int ERROR_BOTH_PHONE_AND_ADDRESS_DID_NOT_MATCH_AND_CUST_NUM_NOT_SUPPLIED = -11;		
		public static final int ERROR_BOTH_PHONE_AND_ADDRESS_DID_NOT_MATCH_AND_PUBLIC_DOMAIN_EMAIL_DID_NOT_MATCH = -12;		
		public static final int ERROR_EXCEPTION_OCCURRED = -99;
	
	String cust_num = "";
	int cont_no = 0;
	
	public CustNumBasedMatcherResult(int returnCode, String cust_num, int cont_no) {
		this.returnCode = returnCode;
		this.cust_num = cust_num;
		this.cont_no = cont_no;
	}
	
	public void printRC() {
		if (returnCode == MATCHED_FOR_NO_TERMS) {
		    log.debug("MatRes Matcher return code: MATCHED_FOR_NO_TERMS");
		} else if (returnCode == MATCHED_FOR_TERMS) {
			log.debug("MatRes Matcher return code: MATCHED_FOR_TERMS");
		} else if (returnCode == ERROR_CUSTOMER_NUMBER_ENTERED_IS_NOT_FOUND) {
			log.debug("MatRes Matcher return code: ERROR_CUSTOMER_NUMBER_ENTERED_IS_NOT_FOUND");
		} else if (returnCode == ERROR_EMAIL_MATCHED_MULTIPLE_CUSTOMERS) {
			log.debug("MatRes Matcher return code: ERROR_EMAIL_MATCHED_MULTIPLE_CUSTOMERS");
		} else if (returnCode == ERROR_PHONE_MATCHED_MULTIPLE_CUSTOMERS) {
			log.debug("MatRes Matcher return code: ERROR_PHONE_MATCHED_MULTIPLE_CUSTOMERS");
		} else if (returnCode == ERROR_BOTH_PHONE_AND_ADDRESS_DID_NOT_MATCH) {
			log.debug("MatRes Matcher return code: ERROR_BOTH_PHONE_AND_ADDRESS_DID_NOT_MATCH");
		} else if (returnCode == ERROR_EMAIL_DOMAIN_DID_NOT_MATCH) {
			log.debug("MatRes Matcher return code: ERROR_EMAIL_DOMAIN_DID_NOT_MATCH");
		} else if (returnCode == ERROR_MISMATCH_BETWEEN_CUSTOMER_NUMBERS) {
			log.debug("MatRes Matcher return code: ERROR_MISMATCH_BETWEEN_CUSTOMER_NUMBERS");
		} else if (returnCode == ERROR_NO_COMMON_MATCHED_CUST_NUM) {
			log.debug("MatRes Matcher return code: ERROR_NO_COMMON_MATCHED_CUST_NUM");
		} else if (returnCode == ERROR_MISMATCH_BETWEEN_CUSTOMER_NUMBER_ENTERED_AND_PHONE_OR_ADDRESS_MATCHED) {
			log.debug("MatRes Matcher return code: ERROR_MISMATCH_BETWEEN_CUSTOMER_NUMBER_ENTERED_AND_PHONE_OR_ADDRESS_MATCHED");
		} else if (returnCode == ERROR_MATCHED_CUSTOMER_IS_INACTIVE) {
			log.debug("MatRes Matcher return code: ERROR_MATCHED_CUSTOMER_IS_INACTIVE");
		} else if (returnCode == ERROR_BOTH_PHONE_AND_ADDRESS_DID_NOT_MATCH_AND_DOMAIN_DID_NOT_MATCH) {
			log.debug("MatRes Matcher return code: ERROR_BOTH_PHONE_AND_ADDRESS_DID_NOT_MATCH_AND_DOMAIN_DID_NOT_MATCH");
		} else if (returnCode == ERROR_BOTH_PHONE_AND_ADDRESS_DID_NOT_MATCH_AND_CUST_NUM_NOT_SUPPLIED) {
			log.debug("MatRes Matcher return code: ERROR_BOTH_PHONE_AND_ADDRESS_DID_NOT_MATCH_AND_CUST_NUM_NOT_SUPPLIED");
		} else if (returnCode == ERROR_BOTH_PHONE_AND_ADDRESS_DID_NOT_MATCH_AND_PUBLIC_DOMAIN_EMAIL_DID_NOT_MATCH) {
			log.debug("MatRes Matcher return code: ERROR_BOTH_PHONE_AND_ADDRESS_DID_NOT_MATCH_AND_PUBLIC_DOMAIN_EMAIL_DID_NOT_MATCH");
		} else if (returnCode == ERROR_EXCEPTION_OCCURRED) {
			log.debug("MatRes Matcher return code: ERROR_EXCEPTION_OCCURRED");
		} else {
			log.debug("MatRes Matcher return code: UNKNOWN.");
		}
	}

	public int getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}
	public String getCust_num() {
		return cust_num;
	}
	public void setCust_num(String cust_num) {
		this.cust_num = cust_num;
	}
	public int getCont_no() {
		return cont_no;
	}
	public void setCont_no(int cont_no) {
		this.cont_no = cont_no;
	}
}

