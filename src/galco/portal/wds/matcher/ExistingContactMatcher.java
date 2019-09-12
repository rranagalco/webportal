package galco.portal.wds.matcher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.Contact;
import galco.portal.wds.dao.Cust;
import galco.portal.wds.dao.Custbill;
import galco.portal.wds.dao.Custship;
import galco.portal.wds.matcher.Has_Cust_Num_Address_Address2_Zip;

public class ExistingContactMatcher {
	private static Logger log = Logger.getLogger(ExistingContactMatcher.class);

	private static ArrayList<Contact> findContactsThatMatchedPhone(ArrayList<Contact> contactsThatMatchedEmailAL, String phone1, String phone2, String phone3) {
		ArrayList<Contact> alMatchingPhone = new ArrayList<Contact>();

		for (Iterator<Contact> iterator = contactsThatMatchedEmailAL.iterator(); iterator.hasNext();) {
			Contact contact = iterator.next();

            if ((StringUtils.isBlank(phone1) == false            ) &&
                ((contact.getPhone1().compareTo(phone1) == 0) ||
                 (contact.getPhone2().compareTo(phone1) == 0)    )    ) {
				alMatchingPhone.add(contact);
            } else if ((StringUtils.isBlank(phone2) == false            ) &&
                       ((contact.getPhone1().compareTo(phone2) == 0) ||
                        (contact.getPhone2().compareTo(phone2) == 0)    )    ) {
				alMatchingPhone.add(contact);
            } else if ((StringUtils.isBlank(phone3) == false            ) &&
                       ((contact.getPhone1().compareTo(phone3) == 0) ||
                        (contact.getPhone2().compareTo(phone3) == 0)    )    ) {
				alMatchingPhone.add(contact);
            }

            /*
			if ((contact.getPhone1().compareTo(phone1) == 0) || (contact.getPhone2().compareTo(phone1) == 0) ||
				(contact.getPhone1().compareTo(phone2) == 0) || (contact.getPhone2().compareTo(phone2) == 0) ||
				(contact.getPhone1().compareTo(phone3) == 0) || (contact.getPhone2().compareTo(phone3) == 0)    ) {
				alMatchingPhone.add(contact);
			}
			*/
		}

		if (alMatchingPhone.size() == 0) {
			return null;
		}
		return alMatchingPhone;
	}

	private static ArrayList<Cust> findCustsThatMatchedPhone(ArrayList<Cust> custsThatMatchedEmailAL, String phone1, String phone2, String phone3) {
		ArrayList<Cust> alMatchingPhone = new ArrayList<Cust>();

		for (Iterator<Cust> iterator = custsThatMatchedEmailAL.iterator(); iterator.hasNext();) {
			Cust cust = iterator.next();
			// log.debug("**" + cust.getPhone_800num() + "**");

            if ((StringUtils.isBlank(phone1) == false               ) &&
                ((cust.getPhone().compareTo(phone1)        == 0) ||
                 (cust.getPhone_800num().compareTo(phone1) == 0)    )    ) {
				alMatchingPhone.add(cust);
            } else if ((StringUtils.isBlank(phone2) == false               ) &&
                       ((cust.getPhone().compareTo(phone2)        == 0) ||
                        (cust.getPhone_800num().compareTo(phone2) == 0)    )    ) {
				alMatchingPhone.add(cust);
            } else if ((StringUtils.isBlank(phone3) == false               ) &&
                       ((cust.getPhone().compareTo(phone3)        == 0) ||
                        (cust.getPhone_800num().compareTo(phone3) == 0)    )    ) {
				alMatchingPhone.add(cust);
            }

            /*
			if ((cust.getPhone().compareTo(phone1) == 0) || (cust.getPhone_800num().compareTo(phone1) == 0) ||
				(cust.getPhone().compareTo(phone2) == 0) || (cust.getPhone_800num().compareTo(phone2) == 0) ||
				(cust.getPhone().compareTo(phone3) == 0) || (cust.getPhone_800num().compareTo(phone3) == 0)    ) {
				alMatchingPhone.add(cust);
			}
			*/
		}

		if (alMatchingPhone.size() == 0) {
			return null;
		}
		return alMatchingPhone;
	}

	public static HashSet<String> getDistinctCustnumsOfRecordsThatMatchedAddress(ArrayList<? extends Has_Cust_Num_Address_Address2_Zip> al, String address, String address2, String zip) {
		if (al == null) {
			return null;
		}

		HashSet<String> hsDistinct = new HashSet<String>();

		for (Iterator<? extends Has_Cust_Num_Address_Address2_Zip> iterator = al.iterator(); iterator.hasNext();) {
			Has_Cust_Num_Address_Address2_Zip has_Cust_Num_Address_Address2_Zip = iterator.next();

			if (Utils.addressesMatched(	has_Cust_Num_Address_Address2_Zip.getAddress(),
					   					has_Cust_Num_Address_Address2_Zip.getZip(), address, zip ) ||
				Utils.addressesMatched(	has_Cust_Num_Address_Address2_Zip.getAddress2(),
										has_Cust_Num_Address_Address2_Zip.getZip(), address, zip ) ||
				Utils.addressesMatched(	has_Cust_Num_Address_Address2_Zip.getAddress(),
										has_Cust_Num_Address_Address2_Zip.getZip(), address2, zip) ||
				Utils.addressesMatched(	has_Cust_Num_Address_Address2_Zip.getAddress2(),
										has_Cust_Num_Address_Address2_Zip.getZip(), address2, zip)    ) {
				hsDistinct.add(has_Cust_Num_Address_Address2_Zip.getCust_num());
			}
		}

		if (hsDistinct.size() == 0) {
			return null;
		}
		return hsDistinct;
	}

	// -------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------------

	public static WDSMatchingResults findContact(DBConnector dbConnector, String email, String phone1, String phone2, String phone3, String address, String address2, String zip) throws PortalException {
		WDSMatchingResults wdsMatchingResults = new WDSMatchingResults();

		ArrayList<Contact> contacts_MatchedEmail_AL = null;
		if (email != null) {
			contacts_MatchedEmail_AL = Contact.getContactsThatMatchedEmail(dbConnector, email);
		}

		// qqq
		// EMAIL_MATCHED
		if (contacts_MatchedEmail_AL != null) {
			wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_MATCHED);

			ArrayList<Contact> contacts_MatchedEmailAndPhoneMatchedContact_AL = findContactsThatMatchedPhone(contacts_MatchedEmail_AL, phone1, phone2, phone3);

			// EMAIL_MATCHED__AND__PHONE_MATCHED_CONTACT
			if (contacts_MatchedEmailAndPhoneMatchedContact_AL != null) {
				wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_MATCHED__AND__PHONE_MATCHED_CONTACT);
				wdsMatchingResults.setDistinctMatchedCustNums_HS(Cust.getTheListOfDistinctCustNos(contacts_MatchedEmailAndPhoneMatchedContact_AL));
			// EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT
			} else {
				wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT);

				HashSet<String> distinctCustNums_ContactsThatMatchedEmail_HS = Cust.getTheListOfDistinctCustNos(contacts_MatchedEmail_AL);
				ArrayList<Cust> custs_MatchedEmail_AL = Cust.getCustsForGivenCustNos(dbConnector, distinctCustNums_ContactsThatMatchedEmail_HS);
				ArrayList<Cust> custs_MatchedEmailAndPhoneMatchedCust_AL = findCustsThatMatchedPhone(custs_MatchedEmail_AL, phone1, phone2, phone3);

				// EMAIL_MATCHED__AND__PHONE_MATCHED_CUST
				if (custs_MatchedEmailAndPhoneMatchedCust_AL != null) {
					wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_MATCHED__AND__PHONE_MATCHED_CUST);
					wdsMatchingResults.setDistinctMatchedCustNums_HS(Cust.getTheListOfDistinctCustNos(custs_MatchedEmailAndPhoneMatchedCust_AL));

				// EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST
				} else {
					wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST);

					HashSet<String> custNums_EmailMatchedAndAddressMatchedCust_HS = getDistinctCustnumsOfRecordsThatMatchedAddress(custs_MatchedEmail_AL, address, address2, zip);

					// EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUST
					if (custNums_EmailMatchedAndAddressMatchedCust_HS != null) {
						wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUST);
						wdsMatchingResults.setDistinctMatchedCustNums_HS(custNums_EmailMatchedAndAddressMatchedCust_HS);

					// EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST
					} else {
						wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST);

						ArrayList<Custbill> custbills_MatchedEmail_AL = Custbill.getCustbillsForGivenCustNos(dbConnector, distinctCustNums_ContactsThatMatchedEmail_HS);
						HashSet<String> custNumsOfEmailAndAddressMatchedCustbillsHS = getDistinctCustnumsOfRecordsThatMatchedAddress(custbills_MatchedEmail_AL, address, address2, zip);

						// EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUSTBILL
						if (custNumsOfEmailAndAddressMatchedCustbillsHS != null) {
							wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUSTBILL);
							wdsMatchingResults.setDistinctMatchedCustNums_HS(custNumsOfEmailAndAddressMatchedCustbillsHS);

						// EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL
						} else {
							wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL);

							ArrayList<Custship> custships_MatchedEmail_AL = Custship.getCustshipsForGivenCustNos(dbConnector, distinctCustNums_ContactsThatMatchedEmail_HS);
							HashSet<String> custNums_EmailMatchedAndAddressMatchedCustship_HS = getDistinctCustnumsOfRecordsThatMatchedAddress(custships_MatchedEmail_AL, address, address2, zip);

							// EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_MATCHED_CUSTSHIP
							if (custNums_EmailMatchedAndAddressMatchedCustship_HS != null) {
								wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_MATCHED_CUSTSHIP);
								wdsMatchingResults.setDistinctMatchedCustNums_HS(custNums_EmailMatchedAndAddressMatchedCustship_HS);
							// EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_DID_NOT_MATCH_CUSTSHIP
							} else {
								wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_MATCHED__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_DID_NOT_MATCH_CUSTSHIP);
								wdsMatchingResults.setDistinctMatchedCustNums_HS(null);
							}
						}
					}
				}
			}

		// EMAIL_DID_NOT_MATCH
		} else {
			wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_DID_NOT_MATCH);

			log.debug(phone1 + "**" + phone2 + "**" + phone3);

			ArrayList<Contact> contacts_MatchedPhone_AL = Contact.getContactsThatMatchedPhone(dbConnector, phone1, phone2, phone3);

			// EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT
			if (contacts_MatchedPhone_AL != null) {
				wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT);

				HashSet<String> distinctCustNumsOfContactsThatMatchedPhoneHS = Cust.getTheListOfDistinctCustNos(contacts_MatchedPhone_AL);
				ArrayList<Cust> custs_MatchedPhoneOfContact_AL = Cust.getCustsForGivenCustNos(dbConnector, distinctCustNumsOfContactsThatMatchedPhoneHS);
				HashSet<String> custNums_PhoneMatchedContactAndAddressMatchedCust_HS = getDistinctCustnumsOfRecordsThatMatchedAddress(custs_MatchedPhoneOfContact_AL, address, address2, zip);

				// EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_MATCHED_CUST
				if (custNums_PhoneMatchedContactAndAddressMatchedCust_HS != null) {
					wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_MATCHED_CUST);
					wdsMatchingResults.setDistinctMatchedCustNums_HS(custNums_PhoneMatchedContactAndAddressMatchedCust_HS);

				// EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST
				} else {
					HashSet<String> distinctCustNums_PhoneMatchedContact_HS = Cust.getTheListOfDistinctCustNos(contacts_MatchedPhone_AL);

					ArrayList<Custbill> custbills_PhoneMatchedContact_AL = Custbill.getCustbillsForGivenCustNos(dbConnector, distinctCustNums_PhoneMatchedContact_HS);
					HashSet<String> custNums_PhoneMatchedContactAndAddressMatchedCustbill_HS = getDistinctCustnumsOfRecordsThatMatchedAddress(custbills_PhoneMatchedContact_AL, address, address2, zip);

					// EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUSTBILL
					if (custNums_PhoneMatchedContactAndAddressMatchedCustbill_HS != null) {
						wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUSTBILL);
						wdsMatchingResults.setDistinctMatchedCustNums_HS(custNums_PhoneMatchedContactAndAddressMatchedCustbill_HS);

					// EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL
					} else {
						wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL);

						ArrayList<Custship> custships_PhoneMatchedContact_AL = Custship.getCustshipsForGivenCustNos(dbConnector, distinctCustNums_PhoneMatchedContact_HS);
						HashSet<String> custNum_PhoneMatchedContactAndAddressMatchedCustship_HS = getDistinctCustnumsOfRecordsThatMatchedAddress(custships_PhoneMatchedContact_AL, address, address2, zip);

						// EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_MATCHED_CUSTSHIP
						if (custNum_PhoneMatchedContactAndAddressMatchedCustship_HS != null) {
							wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_MATCHED_CUSTSHIP);
							wdsMatchingResults.setDistinctMatchedCustNums_HS(custNum_PhoneMatchedContactAndAddressMatchedCustship_HS);
						// EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_DID_NOT_MATCH_CUSTSHIP
						} else {
							wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_DID_NOT_MATCH__AND__PHONE_MATCHED_CONTACT__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_DID_NOT_MATCH_CUSTSHIP);
							wdsMatchingResults.setDistinctMatchedCustNums_HS(null);
						}
					}
				}

			// EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT
			} else {
				wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT);

				ArrayList<Cust> custs_MatchedPhoneOfCust_AL = Cust.getCustsThatMatchedPhone(dbConnector, phone1, phone2, phone3);

				// EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST
				if (custs_MatchedPhoneOfCust_AL != null) {
					wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST);

					HashSet<String> custNums_PhoneMatchedCustAndAddressMatchedCust_HS = getDistinctCustnumsOfRecordsThatMatchedAddress(custs_MatchedPhoneOfCust_AL, address, address2, zip);

					// EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_MATCHED_CUST
					if (custNums_PhoneMatchedCustAndAddressMatchedCust_HS != null) {
						wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_MATCHED_CUST);
						wdsMatchingResults.setDistinctMatchedCustNums_HS(custNums_PhoneMatchedCustAndAddressMatchedCust_HS);

					// EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST
					} else {
						HashSet<String> distinctCustNums_PhoneMatchedCust_HS = Cust.getTheListOfDistinctCustNos(custs_MatchedPhoneOfCust_AL);

						ArrayList<Custbill> custbills_PhoneMatchedCust_AL = Custbill.getCustbillsForGivenCustNos(dbConnector, distinctCustNums_PhoneMatchedCust_HS);
						HashSet<String> custNums_PhoneMatchedCustAndAddressMatchedCustbill_HS = getDistinctCustnumsOfRecordsThatMatchedAddress(custbills_PhoneMatchedCust_AL, address, address2, zip);

						// EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUSTBILL
						if (custNums_PhoneMatchedCustAndAddressMatchedCustbill_HS != null) {
							wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_MATCHED_CUSTBILL);
							wdsMatchingResults.setDistinctMatchedCustNums_HS(custNums_PhoneMatchedCustAndAddressMatchedCustbill_HS);

						// EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL
						} else {
							wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL);

							ArrayList<Custship> custships_PhoneMatchedCust_AL = Custship.getCustshipsForGivenCustNos(dbConnector, distinctCustNums_PhoneMatchedCust_HS);
							HashSet<String> custNum_PhoneMatchedCustAndAddressMatchedCustship_HS = getDistinctCustnumsOfRecordsThatMatchedAddress(custships_PhoneMatchedCust_AL, address, address2, zip);

							// EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_MATCHED_CUSTSHIP
							if (custNum_PhoneMatchedCustAndAddressMatchedCustship_HS != null) {
								wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_MATCHED_CUSTSHIP);
								wdsMatchingResults.setDistinctMatchedCustNums_HS(custNum_PhoneMatchedCustAndAddressMatchedCustship_HS);
							// EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_DID_NOT_MATCH_CUSTSHIP
							} else {
								wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_MATCHED_CUST__AND__ADDRESS_DID_NOT_MATCH_CUST__AND__ADDRESS_DID_NOT_MATCH_CUSTBILL__AND__ADDRESS_DID_NOT_MATCH_CUSTSHIP);
								wdsMatchingResults.setDistinctMatchedCustNums_HS(null);
							}
						}
					}

				// EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST
				} else {
					wdsMatchingResults.setMatchingStatus(WDSMatchingResults.EMAIL_DID_NOT_MATCH__AND__PHONE_DID_NOT_MATCH_CONTACT__AND__PHONE_DID_NOT_MATCH_CUST);
					wdsMatchingResults.setDistinctMatchedCustNums_HS(null);
				}
			}
		}

		return wdsMatchingResults;
	}

}
