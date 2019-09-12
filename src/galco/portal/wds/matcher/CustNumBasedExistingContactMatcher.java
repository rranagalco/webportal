package galco.portal.wds.matcher;

import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.Contact;
import galco.portal.wds.dao.Cust;
import galco.portal.wds.dao.Custbill;
import galco.portal.wds.dao.Custship;
import galco.portal.wds.dao.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class CustNumBasedExistingContactMatcher {
	private static Logger log = Logger.getLogger(CustNumBasedExistingContactMatcher.class);
	
	public static CustNumBasedMatcherResult matchAgainstExistingCustomers(DBConnector dbConnector,
						String cust_num, String e_mail_address,
						String phone1, String phone2, String phone3,
						String address, String address2, 
						String zip,
						String firstName, String lastName, HashMap<String, String> matchResultsHM) {
		try {

            log.debug("CustNumBasedExistingContactMatcher is running.");
			log.debug("Checking out: " + e_mail_address);
            log.debug("cust_num      : " + "***" + cust_num + "***");
            log.debug("e_mail_address: " + "***" + e_mail_address + "***");
            log.debug("phone1        : " + "***" + phone1 + "***");
            log.debug("phone2        : " + "***" + phone2 + "***");
            log.debug("phone3        : " + "***" + phone3 + "***");
            log.debug("address       : " + "***" + address + "***");
            log.debug("address2      : " + "***" + address2 + "***");
            log.debug("zip           : " + "***" + zip + "***");
            log.debug("firstName     : " + "***" + firstName + "***");
            log.debug("lastName      : " + "***" + lastName + "***");
			
			boolean CUST_NUM_SUPPLIED = false;
			boolean CUST_NUM_MATCHED = false;
			boolean EMAIL_MATCHED = false;
			boolean PHONE_NUMBER_MATCHED = false;
			boolean ADDRESS_MATCHED = false;
			boolean NAME_MATCHED = false;
			boolean EMAIL_DOMAIN_MATCHED = false;
			boolean EMAIL_DOMAIN_IS_PUBLIC_DOMAIN = false;
			
			int cont_no = 0;
			
			
			/*
			// NEW TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED 
			// E-mail matches more than one cust/contact
			// cust_num is supplied 116080
			// E-mail matches more than one cust/contact (116080 and 085077)
			// phone matches [116080, 129406, 085077]
			// Address doesn't match the supplied cust, matches 085077 Cust
			// Name matches contact 1 of 116080
			cust_num = "116080";
			e_mail_address = "lomaxij@state.gov";
			phone1 = "2027367347"; phone2 = "2026472432"; phone3 = "";
			address = "12880 CLOVERDALE"; address2 = ""; zip = "48237";
			firstName = "INGRID";
			lastName = "LOMAX";
			*/

			
			/*
			// NEW TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED 
			// cust_num is not supplied
			// E-mail matches more than one cust/contact (116080 and 085077)	
			// phone matches [116080, 129406, 085077]
			// Address doesn't match
			// E-mail domain matches [116080, 085077]
			// Customer with most recent order 116080
			// Name matches contact 1 of 116080			
			cust_num = "";
			e_mail_address = "lomaxij@state.gov";
			phone1 = "2027367347"; phone2 = "2026472432"; phone3 = "";
			address = "12880 CLOVERDALE"; address2 = ""; zip = "48237";
			firstName = "INGRID";
			lastName = "LOMAX";
			*/

			
			/*
			// NEW TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED 
			// cust_num is not supplied
			// E-mail matches more than one cust/contact (116080 and 085077)
			// phone doesn't match
			// Address matches 085077 Cust
			// Name matches contact 2 of 085077
			cust_num = "";
			e_mail_address = "lomaxij@state.gov";
			phone1 = "2489876543"; phone2 = ""; phone3 = "";
			address = "2201 C STREET NW"; address2 = ""; zip = "20520";
			firstName = "INGRID";
			lastName = "LOMAX";
			*/
			
						
			/*
			// NEW TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED 
			// cust_num is not supplied
			// E-mail matches more than one cust/contact (116080 and 085077)
			// phone matches some other cust [042012]
			// Address matches 085077 Cust
			// E-mail domain matches [116080, 085077]			
			// Name matches cont 2 of 116080
			cust_num = "";
			e_mail_address = "lomaxij@state.gov";
			phone1 = "2489999999"; phone2 = ""; phone3 = "";
			address = "2201 C STREET NW"; address2 = ""; zip = "20520";
			firstName = "INGRID";
			lastName = "LOMAX";
			*/
			

			/*
			// NEW TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED 
			// cust_num is supplied
			// E-mail matches 005085
			// phone matches cust
			// Address matches 005085 Cust
			// E-mail domain matches 005085		
			// Name matches cont 9 of 005085
			cust_num = "005085";
			e_mail_address = "dhusken@kerrpump.com";
			phone1 = "2485433880"; phone2 = "2485433236"; phone3 = "";
			address = "12880 CLOVERDALE"; address2 = ""; zip = "48237";
			firstName = "DARICK";
			lastName = "HUSKEN";
			*/

			
			/*
			// NEW TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED 
			// cust_num is supplied
			// E-mail matches
			// phone doesn't match
			// address doesn't match cust but matches either billto or shipto
			// E-mail domain matches		
			// Name matches cont 4			
			cust_num = "047555";
			e_mail_address = "vjmckoon@platt.com";
			phone1 = "2489876543"; phone2 = ""; phone3 = "";
			// Matches Billto 
			address = "1825 ELLIS STREET"; address2 = ""; zip = "98225";
			// Matches Shipto
			// address = "1231 GROVE STREET"; address2 = ""; zip = "97365";
			firstName = "VANESSA";
			lastName = "MCKOON";
			*/
			

			/*
			// NEW TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED 
			// cust_num is supplied
			// E-mail doesn't match
			// phone matches
			// address matches cust
			// E-mail domain doesn't match		
			cust_num = "005085";
			e_mail_address = "dhusken@abcd.com";
			phone1 = "2485433880"; phone2 = "2485433236"; phone3 = "";
			address = "12880 CLOVERDALE"; address2 = ""; zip = "48237";
			firstName = "DARICK";
			lastName = "HUSKEN";
			*/
			
			
			/*
			 * YET TO TEST
			cust_num = "148333";
			e_mail_address = "sati3@galco.com";
			phone1 = ""; phone2 = ""; phone3 = "";
			address = ""; address2 = ""; zip = "";
			firstName = "manepalli";
			lastName = "hari";
			*/

			/*
			// TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED  
			// cust_num is supplied 116080
			// Domain is non public and matches the customer
			// Email doesn't match, and Name doesn't match
			cust_num = "116080";
			e_mail_address = "sub999@state.gov";
			phone1 = "9999999999"; phone2 = ""; phone3 = "";
			address = "99999 ABCD"; address2 = ""; zip = "99999";
			firstName = "zzz";
			lastName = "zzz";
			*/

			/*
			// TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED  
			// cust_num is supplied 116080
			// Domain is non public and matches the customer
			// Email matches, and Name doesn't match
			cust_num = "116080";
			e_mail_address = "sub45@state.gov";
			phone1 = "9999999999"; phone2 = ""; phone3 = "";
			address = "99999 ABCD"; address2 = ""; zip = "99999";
			firstName = "zzz";
			lastName = "ati241";
			*/

			/*
			// TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED  
			// cust_num is supplied 116080
			// Domain is non public and matches the customer
			// Email matches more than one contact (7 and 8), and Name matches contact 8
			cust_num = "116080";
			e_mail_address = "sub45@state.gov";
			phone1 = "9999999999"; phone2 = ""; phone3 = "";
			address = "99999 ABCD"; address2 = ""; zip = "99999";
			firstName = "subb";
			lastName = "ati242";
			*/

			/*
			// TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED  
			// cust_num is supplied 116080
			// Domain is non public and matches the customer
			// Email doesn't match, but Name  matches
			cust_num = "116080";
			e_mail_address = "sub9999@state.gov";
			phone1 = "9999999999"; phone2 = ""; phone3 = "";
			address = "99999 ABCD"; address2 = ""; zip = "99999";
			firstName = "subb241";
			lastName = "ati241";
			*/
						
			/*
			// TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED  
			// cust_num is supplied 116080
			// Domain is public and matches the customer. NOTE - For this testing I made
			//		state.gov a public domain site by adding it to the list
			// Email doesn't match
			cust_num = "116080";
			e_mail_address = "sub9999@state.gov";
			phone1 = "9999999999"; phone2 = ""; phone3 = "";
			address = "99999 ABCD"; address2 = ""; zip = "99999";
			firstName = "subb241";
			lastName = "ati241";
			*/
			
			/*
			// TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED  
			// cust_num is supplied 116080
			// Domain is public and matches the customer. NOTE - For this testing I made
			//		state.gov a public domain site by adding it to the list
			// Email matches and name matches
			cust_num = "116080";
			e_mail_address = "sub45@state.gov";
			phone1 = "9999999999"; phone2 = ""; phone3 = "";
			address = "99999 ABCD"; address2 = ""; zip = "99999";
			firstName = "subb241";
			lastName = "ati241";
			*/
		
			// sss
			/*
			// TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED TESTED  
			// cust_num is supplied 116080
			// Domain is public and matches the customer. NOTE - For this testing I made
			//		state.gov a public domain site by adding it to the list
			// Email matches more than one (7 and 8) and name matches contact 8
			cust_num = "116080";
			e_mail_address = "sub45@state.gov";
			phone1 = "9999999999"; phone2 = ""; phone3 = "";
			address = "99999 ABCD"; address2 = ""; zip = "99999";
			firstName = "subb";
			lastName = "ati242";
			*/
			
			if ((cust_num != null) && (cust_num.length() > 0) && (cust_num.matches("[0-9]+"))) {
				cust_num = String.format("%06d", Integer.parseInt(cust_num));
				log.debug("cust_num after prepending zeros: " + cust_num);
			}			
			
			Cust cust = null;
			// qqq
			HashSet<String> suppliedCustNums = null;
			HashSet<String> distinctCustNums_ThatMatchedEmail_HS = null;
			HashSet<String> distinctCustNums_ThatMatchedPhone_HS = null;
			HashSet<String> distinctCustNums_ThatMatchedAddress_HS = null;
			HashSet<String> distinctCustNums_ThatMatchedEmailDomain = null;

			
			if ((cust_num != null) && (cust_num.trim().compareTo("") != 0)) {
				ArrayList<Cust> custAL = Cust.getCustForGivenCustNo(dbConnector, cust_num);
				if ((custAL != null) && (custAL.size() == 1)) {
					CUST_NUM_SUPPLIED = true;
					cust = custAL.get(0);
					CUST_NUM_MATCHED = true;
					
					suppliedCustNums = new HashSet<String>();
					suppliedCustNums.add(cust_num);
				} else {
					System.out.println("ERROR_CUSTOMER_NUMBER_ENTERED_IS_NOT_FOUND " + cust_num + " " + cust_num);
					printMatchFlags(matchResultsHM, CUST_NUM_SUPPLIED, CUST_NUM_MATCHED, EMAIL_MATCHED, PHONE_NUMBER_MATCHED, ADDRESS_MATCHED, NAME_MATCHED, EMAIL_DOMAIN_MATCHED, EMAIL_DOMAIN_IS_PUBLIC_DOMAIN);
					return new CustNumBasedMatcherResult(CustNumBasedMatcherResult.ERROR_CUSTOMER_NUMBER_ENTERED_IS_NOT_FOUND, "", 0);
				}
			}	
			
			
			if ((e_mail_address != null) && (e_mail_address.trim().compareTo("") != 0)) {
				ArrayList<Contact> contacts_MatchedEmail_AL = Contact.getContactsThatMatchedEmail(dbConnector, e_mail_address);
				
				if ((contacts_MatchedEmail_AL != null) && (contacts_MatchedEmail_AL.size() > 0)) {
					distinctCustNums_ThatMatchedEmail_HS = Cust.getTheListOfDistinctCustNos(contacts_MatchedEmail_AL);

					if ((distinctCustNums_ThatMatchedEmail_HS != null) && (distinctCustNums_ThatMatchedEmail_HS.size() > 0)) {
						for (Iterator<String> iterator = distinctCustNums_ThatMatchedEmail_HS.iterator(); iterator.hasNext();) {
							log.debug("Email matching Customers: " + iterator.next());
						}						
						
						if (CUST_NUM_SUPPLIED == true) {
							if (distinctCustNums_ThatMatchedEmail_HS.contains(cust_num)) {
								EMAIL_MATCHED = true;
							} else {
								EMAIL_MATCHED = false;
								distinctCustNums_ThatMatchedEmail_HS = null;
							}
						} else {
							EMAIL_MATCHED = true;
						}
					}
				}
			}
			
            log.debug("EMAIL_MATCHED: " + EMAIL_MATCHED);
			
			if (((phone1 != null) && (phone1.trim().compareTo("") != 0)) ||
				((phone2 != null) && (phone2.trim().compareTo("") != 0)) || 
				((phone3 != null) && (phone3.trim().compareTo("") != 0))    ) {
				ArrayList<Contact> contacts_MatchedPhone_AL = Contact.getContactsThatMatchedPhone(dbConnector, phone1, phone2, phone3);

				if ((contacts_MatchedPhone_AL != null) && (contacts_MatchedPhone_AL.size() > 0)) {
					distinctCustNums_ThatMatchedPhone_HS = Cust.getTheListOfDistinctCustNos(contacts_MatchedPhone_AL);
	
					if ((distinctCustNums_ThatMatchedPhone_HS != null) && (distinctCustNums_ThatMatchedPhone_HS.size() > 0)) {
						if (CUST_NUM_SUPPLIED == true) {
							if (distinctCustNums_ThatMatchedPhone_HS.contains(cust_num)) {
								PHONE_NUMBER_MATCHED = true;
					            log.debug("PHONE matched contact.");
							} else {
								PHONE_NUMBER_MATCHED = false;
								distinctCustNums_ThatMatchedPhone_HS = null;
					            log.debug("PHONE didn't match contact.");
							}
						} else {
							PHONE_NUMBER_MATCHED = true;
				            log.debug("PHONE matched contact.");
						}
					}
				} else {
					ArrayList<Cust> custs_MatchedPhoneOfCust_AL = Cust.getCustsThatMatchedPhone(dbConnector, phone1, phone2, phone3);
					if ((custs_MatchedPhoneOfCust_AL != null) && (custs_MatchedPhoneOfCust_AL.size() > 0)) {
						distinctCustNums_ThatMatchedPhone_HS = Cust.getTheListOfDistinctCustNos(custs_MatchedPhoneOfCust_AL);
						
						if ((distinctCustNums_ThatMatchedPhone_HS != null) && (distinctCustNums_ThatMatchedPhone_HS.size() > 0)) {
							if (CUST_NUM_SUPPLIED == true) {
								if (distinctCustNums_ThatMatchedPhone_HS.contains(cust_num)) {
									PHONE_NUMBER_MATCHED = true;
						            log.debug("PHONE matched cust.");									
								} else {
									PHONE_NUMBER_MATCHED = false;
									distinctCustNums_ThatMatchedPhone_HS = null;
						            log.debug("PHONE didn't match cust.");									
								}
							} else {
								PHONE_NUMBER_MATCHED = true;
					            log.debug("PHONE matched cust.");									
							}
						}
					}
				}
			}
			
			
			if ((CUST_NUM_MATCHED == false		) &&
			    (EMAIL_MATCHED == false			) &&
			    (PHONE_NUMBER_MATCHED == false	)	 ) {
				ArrayList<Cust> zipMatchedCusts = Cust.getCustsForGivenZip(dbConnector, zip);
				if ((zipMatchedCusts != null) && (zipMatchedCusts.size() > 0)) {
					distinctCustNums_ThatMatchedAddress_HS = ExistingContactMatcher.getDistinctCustnumsOfRecordsThatMatchedAddress(zipMatchedCusts, address, address2, zip);
					
					if ((distinctCustNums_ThatMatchedAddress_HS != null) && (distinctCustNums_ThatMatchedAddress_HS.size() > 0)) {
						if (CUST_NUM_SUPPLIED == true) {
							if (distinctCustNums_ThatMatchedAddress_HS.contains(cust_num)) {
								ADDRESS_MATCHED = true;
					            log.debug("ADDRESS matched cust.");									
							} else {
								ADDRESS_MATCHED = false;
								distinctCustNums_ThatMatchedAddress_HS = null;
					            log.debug("ADDRESS didn't match cust.");									
							}
						} else {
							ADDRESS_MATCHED = true;
				            log.debug("ADDRESS matched cust.");									
						}
					}					
				}

				if (ADDRESS_MATCHED == false) {
					ArrayList<Custbill> zipMatchedCustbills = Custbill.getCustbillssForGivenZip(dbConnector, zip);					
					if ((zipMatchedCustbills != null) && (zipMatchedCustbills.size() > 0)) {
						distinctCustNums_ThatMatchedAddress_HS = ExistingContactMatcher.getDistinctCustnumsOfRecordsThatMatchedAddress(zipMatchedCustbills, address, address2, zip);
						if ((distinctCustNums_ThatMatchedAddress_HS != null) && (distinctCustNums_ThatMatchedAddress_HS.size() > 0)) {
							if (CUST_NUM_SUPPLIED == true) {
								if (distinctCustNums_ThatMatchedAddress_HS.contains(cust_num)) {
									ADDRESS_MATCHED = true;
						            log.debug("ADDRESS matched custbill.");									
								} else {
									ADDRESS_MATCHED = false;
									distinctCustNums_ThatMatchedAddress_HS = null;
						            log.debug("ADDRESS didn't match custbill.");									
								}
							} else {
								ADDRESS_MATCHED = true;
					            log.debug("ADDRESS matched custbill.");									
							}
						}	
					}
				}

				if (ADDRESS_MATCHED == false) {
					ArrayList<Custship> zipMatchedCustships = Custship.getCustshipsForGivenZip(dbConnector, zip);
					if ((zipMatchedCustships != null) && (zipMatchedCustships.size() > 0)) {
						distinctCustNums_ThatMatchedAddress_HS = ExistingContactMatcher.getDistinctCustnumsOfRecordsThatMatchedAddress(zipMatchedCustships, address, address2, zip);
						if ((distinctCustNums_ThatMatchedAddress_HS != null) && (distinctCustNums_ThatMatchedAddress_HS.size() > 0)) {
							if (CUST_NUM_SUPPLIED == true) {
								if (distinctCustNums_ThatMatchedAddress_HS.contains(cust_num)) {
									ADDRESS_MATCHED = true;
						            log.debug("ADDRESS matched custship.");																		
								} else {
									ADDRESS_MATCHED = false;
									distinctCustNums_ThatMatchedAddress_HS = null;
						            log.debug("ADDRESS didn't match custship.");																		
								}
							} else {
								ADDRESS_MATCHED = true;
					            log.debug("ADDRESS matched custship.");																		
							}
						}
					}
				}
			} else {
				HashSet<String> mergedHashSetOfAllCustNums = Utils.mergeHashSets(suppliedCustNums, distinctCustNums_ThatMatchedEmail_HS, distinctCustNums_ThatMatchedPhone_HS);
				
				ArrayList<Cust> matchedCusts = Cust.getCustsForGivenCustNos(dbConnector, mergedHashSetOfAllCustNums);
				if ((matchedCusts != null) && (matchedCusts.size() > 0)) {
					distinctCustNums_ThatMatchedAddress_HS = ExistingContactMatcher.getDistinctCustnumsOfRecordsThatMatchedAddress(matchedCusts, address, address2, zip);
					if ((distinctCustNums_ThatMatchedAddress_HS != null) && (distinctCustNums_ThatMatchedAddress_HS.size() > 0)) {
						if (CUST_NUM_SUPPLIED == true) {
							if (distinctCustNums_ThatMatchedAddress_HS.contains(cust_num)) {
								ADDRESS_MATCHED = true;
					            log.debug("ADDRESS matched cust_num suppiled, or email matched cust, or phone matched cust.");																										
							} else {
								ADDRESS_MATCHED = false;
								distinctCustNums_ThatMatchedAddress_HS = null;
					            log.debug("ADDRESS didn't match cust_num suppiled, and email matched cust, and phone matched cust.");																										
							}
						} else {
							ADDRESS_MATCHED = true;
				            log.debug("ADDRESS matched cust_num suppiled, or email matched cust, or phone matched cust.");																										
						}
					}
				}

					
				if (ADDRESS_MATCHED == false) {
					ArrayList<Custbill> custbillsOfMatchedCust = Custbill.getCustbillsForGivenCustNos(dbConnector, mergedHashSetOfAllCustNums);
					if ((custbillsOfMatchedCust != null) && (custbillsOfMatchedCust.size() > 0)) {
						distinctCustNums_ThatMatchedAddress_HS = ExistingContactMatcher.getDistinctCustnumsOfRecordsThatMatchedAddress(custbillsOfMatchedCust, address, address2, zip);
						if ((distinctCustNums_ThatMatchedAddress_HS != null) && (distinctCustNums_ThatMatchedAddress_HS.size() > 0)) {
							if (CUST_NUM_SUPPLIED == true) {
								if (distinctCustNums_ThatMatchedAddress_HS.contains(cust_num)) {
									ADDRESS_MATCHED = true;
						            log.debug("ADDRESS matched custbill of cust_num suppiled, or email matched cust, or phone matched cust.");																																			
								} else {
									ADDRESS_MATCHED = false;
									distinctCustNums_ThatMatchedAddress_HS = null;
						            log.debug("ADDRESS didn't match custbill of cust_num suppiled, or email matched cust, or phone matched cust.");																																			
								}
							} else {
								ADDRESS_MATCHED = true;
					            log.debug("ADDRESS matched custbill of cust_num suppiled, or email matched cust, or phone matched cust.");																																			
							}
						}
					}
				}
				
				if (ADDRESS_MATCHED == false) {
					ArrayList<Custship> custshipsOfMatchedCust = Custship.getCustshipsForGivenCustNos(dbConnector, mergedHashSetOfAllCustNums);
					if ((custshipsOfMatchedCust != null) && (custshipsOfMatchedCust.size() > 0)) {
						distinctCustNums_ThatMatchedAddress_HS = ExistingContactMatcher.getDistinctCustnumsOfRecordsThatMatchedAddress(custshipsOfMatchedCust, address, address2, zip);
						if ((distinctCustNums_ThatMatchedAddress_HS != null) && (distinctCustNums_ThatMatchedAddress_HS.size() > 0)) {
							if (CUST_NUM_SUPPLIED == true) {
								if (distinctCustNums_ThatMatchedAddress_HS.contains(cust_num)) {
									ADDRESS_MATCHED = true;
						            log.debug("ADDRESS matched custship of cust_num suppiled, or email matched cust, or phone matched cust.");																																												
								} else {
									ADDRESS_MATCHED = false;
									distinctCustNums_ThatMatchedAddress_HS = null;
						            log.debug("ADDRESS didn't match custship of cust_num suppiled, or email matched cust, or phone matched cust.");																																												
								}
							} else {
								ADDRESS_MATCHED = true;
					            log.debug("ADDRESS matched custship of cust_num suppiled, or email matched cust, or phone matched cust.");																																												
							}
						}
					}
				}
			}
			
			
			if ((PHONE_NUMBER_MATCHED == false) && (ADDRESS_MATCHED == false)) {
	            log.debug("ERROR_BOTH_PHONE_AND_ADDRESS_DID_NOT_MATCH.");	
	            
				// return new CustNumBasedMatcherResult(CustNumBasedMatcherResult.ERROR_BOTH_PHONE_AND_ADDRESS_DID_NOT_MATCH, "", 0);
	            
	            // sss
	            
				if (CUST_NUM_MATCHED == true) {
					String domain = Utils.getEmailDomain(e_mail_address);
					
					if (Utils.isDomainAPublicEmailDomain(domain) == false) {
						EMAIL_DOMAIN_IS_PUBLIC_DOMAIN = false;
						
						HashSet<String> enteredCustNumHS = new HashSet<String>();
						enteredCustNumHS.add(cust_num);
						
						ArrayList<Contact> contactsThatMatchedDomain = Contact.getContactsThatMatchedDomainWithinGivenCustomers(dbConnector, enteredCustNumHS, domain);
						if ((contactsThatMatchedDomain != null) && (contactsThatMatchedDomain.size() > 0)) {
							EMAIL_DOMAIN_MATCHED = true;
							
							ArrayList<Contact> emailMatchedContacts = Contact.getContactsThatMatchedEmailWithinACustomer(dbConnector, cust_num, e_mail_address);
							if ((emailMatchedContacts != null) && (emailMatchedContacts.size() > 0)) {
								if (emailMatchedContacts.size() > 1) {
									Contact contactTmp = Contact.findContactWithMatchingFirstInitialAndLastName(emailMatchedContacts, firstName.substring(0, 1), lastName);
									if (contactTmp != null) {
										cont_no = contactTmp.getCont_no();
										printMatchFlags(matchResultsHM, CUST_NUM_SUPPLIED, CUST_NUM_MATCHED, EMAIL_MATCHED, PHONE_NUMBER_MATCHED, ADDRESS_MATCHED, NAME_MATCHED, EMAIL_DOMAIN_MATCHED, EMAIL_DOMAIN_IS_PUBLIC_DOMAIN);										
										return new CustNumBasedMatcherResult(CustNumBasedMatcherResult.MATCHED_FOR_TERMS, cust_num, cont_no);																	
									} else {
										cont_no = emailMatchedContacts.get(0).getCont_no();										
										printMatchFlags(matchResultsHM, CUST_NUM_SUPPLIED, CUST_NUM_MATCHED, EMAIL_MATCHED, PHONE_NUMBER_MATCHED, ADDRESS_MATCHED, NAME_MATCHED, EMAIL_DOMAIN_MATCHED, EMAIL_DOMAIN_IS_PUBLIC_DOMAIN);
										return new CustNumBasedMatcherResult(CustNumBasedMatcherResult.MATCHED_FOR_TERMS, cust_num, cont_no);										
									}
								} else {
									cont_no = emailMatchedContacts.get(0).getCont_no();
									printMatchFlags(matchResultsHM, CUST_NUM_SUPPLIED, CUST_NUM_MATCHED, EMAIL_MATCHED, PHONE_NUMBER_MATCHED, ADDRESS_MATCHED, NAME_MATCHED, EMAIL_DOMAIN_MATCHED, EMAIL_DOMAIN_IS_PUBLIC_DOMAIN);
									return new CustNumBasedMatcherResult(CustNumBasedMatcherResult.MATCHED_FOR_TERMS, cust_num, cont_no);																										
								}
							} else {
								printMatchFlags(matchResultsHM, CUST_NUM_SUPPLIED, CUST_NUM_MATCHED, EMAIL_MATCHED, PHONE_NUMBER_MATCHED, ADDRESS_MATCHED, NAME_MATCHED, EMAIL_DOMAIN_MATCHED, EMAIL_DOMAIN_IS_PUBLIC_DOMAIN);
								return new CustNumBasedMatcherResult(CustNumBasedMatcherResult.MATCHED_FOR_TERMS, cust_num, 0);																										
							}
						} else {
							printMatchFlags(matchResultsHM, CUST_NUM_SUPPLIED, CUST_NUM_MATCHED, EMAIL_MATCHED, PHONE_NUMBER_MATCHED, ADDRESS_MATCHED, NAME_MATCHED, EMAIL_DOMAIN_MATCHED, EMAIL_DOMAIN_IS_PUBLIC_DOMAIN);
							return new CustNumBasedMatcherResult(CustNumBasedMatcherResult.ERROR_BOTH_PHONE_AND_ADDRESS_DID_NOT_MATCH_AND_DOMAIN_DID_NOT_MATCH, "", 0);							
						}
						
					} else {
						EMAIL_DOMAIN_IS_PUBLIC_DOMAIN = true;
						
						ArrayList<Contact> emailMatchedContacts = Contact.getContactsThatMatchedEmailWithinACustomer(dbConnector, cust_num, e_mail_address);
						if ((emailMatchedContacts != null) && (emailMatchedContacts.size() > 0)) {
							if (emailMatchedContacts.size() > 1) {
								Contact contactTmp = Contact.findContactWithMatchingFirstInitialAndLastName(emailMatchedContacts, firstName.substring(0, 1), lastName);
								if (contactTmp != null) {
									cont_no = contactTmp.getCont_no();
									printMatchFlags(matchResultsHM, CUST_NUM_SUPPLIED, CUST_NUM_MATCHED, EMAIL_MATCHED, PHONE_NUMBER_MATCHED, ADDRESS_MATCHED, NAME_MATCHED, EMAIL_DOMAIN_MATCHED, EMAIL_DOMAIN_IS_PUBLIC_DOMAIN);
									return new CustNumBasedMatcherResult(CustNumBasedMatcherResult.MATCHED_FOR_TERMS, cust_num, cont_no);																	
								} else {
									cont_no = emailMatchedContacts.get(0).getCont_no();										
									printMatchFlags(matchResultsHM, CUST_NUM_SUPPLIED, CUST_NUM_MATCHED, EMAIL_MATCHED, PHONE_NUMBER_MATCHED, ADDRESS_MATCHED, NAME_MATCHED, EMAIL_DOMAIN_MATCHED, EMAIL_DOMAIN_IS_PUBLIC_DOMAIN);
									return new CustNumBasedMatcherResult(CustNumBasedMatcherResult.MATCHED_FOR_TERMS, cust_num, cont_no);										
								}
							} else {
								cont_no = emailMatchedContacts.get(0).getCont_no();
								printMatchFlags(matchResultsHM, CUST_NUM_SUPPLIED, CUST_NUM_MATCHED, EMAIL_MATCHED, PHONE_NUMBER_MATCHED, ADDRESS_MATCHED, NAME_MATCHED, EMAIL_DOMAIN_MATCHED, EMAIL_DOMAIN_IS_PUBLIC_DOMAIN);
								return new CustNumBasedMatcherResult(CustNumBasedMatcherResult.MATCHED_FOR_TERMS, cust_num, cont_no);																										
							}
						} else {
							printMatchFlags(matchResultsHM, CUST_NUM_SUPPLIED, CUST_NUM_MATCHED, EMAIL_MATCHED, PHONE_NUMBER_MATCHED, ADDRESS_MATCHED, NAME_MATCHED, EMAIL_DOMAIN_MATCHED, EMAIL_DOMAIN_IS_PUBLIC_DOMAIN);
							return new CustNumBasedMatcherResult(CustNumBasedMatcherResult.ERROR_BOTH_PHONE_AND_ADDRESS_DID_NOT_MATCH_AND_PUBLIC_DOMAIN_EMAIL_DID_NOT_MATCH, "", 0);																										
						}
					}
				} else {
					printMatchFlags(matchResultsHM, CUST_NUM_SUPPLIED, CUST_NUM_MATCHED, EMAIL_MATCHED, PHONE_NUMBER_MATCHED, ADDRESS_MATCHED, NAME_MATCHED, EMAIL_DOMAIN_MATCHED, EMAIL_DOMAIN_IS_PUBLIC_DOMAIN);
					return new CustNumBasedMatcherResult(CustNumBasedMatcherResult.ERROR_BOTH_PHONE_AND_ADDRESS_DID_NOT_MATCH_AND_CUST_NUM_NOT_SUPPLIED, "", 0);
				}
			}
			
			
			HashSet<String> distinctCustNums_ThatMatchedPhoneOrAddress_HS = new HashSet<String>();
			if (distinctCustNums_ThatMatchedPhone_HS != null) {
				distinctCustNums_ThatMatchedPhoneOrAddress_HS.addAll(distinctCustNums_ThatMatchedPhone_HS);
				for (Iterator<String> iterator = distinctCustNums_ThatMatchedPhone_HS.iterator(); iterator.hasNext();) {
					log.debug("Phone matching Customers: " + iterator.next());
				}
			}
			if (distinctCustNums_ThatMatchedAddress_HS != null) {
				distinctCustNums_ThatMatchedPhoneOrAddress_HS.addAll(distinctCustNums_ThatMatchedAddress_HS);
				for (Iterator<String> iterator = distinctCustNums_ThatMatchedAddress_HS.iterator(); iterator.hasNext();) {
					log.debug("Address matching Customers: " + iterator.next());
				}
			}			
			
			if (CUST_NUM_SUPPLIED == true) {
				if (distinctCustNums_ThatMatchedPhoneOrAddress_HS.contains(cust_num) == false) {
					log.debug("ERROR_MISMATCH_BETWEEN_CUSTOMER_NUMBER_ENTERED_AND_PHONE_OR_ADDRESS_MATCHED");
					printMatchFlags(matchResultsHM, CUST_NUM_SUPPLIED, CUST_NUM_MATCHED, EMAIL_MATCHED, PHONE_NUMBER_MATCHED, ADDRESS_MATCHED, NAME_MATCHED, EMAIL_DOMAIN_MATCHED, EMAIL_DOMAIN_IS_PUBLIC_DOMAIN);
					return new CustNumBasedMatcherResult(CustNumBasedMatcherResult.ERROR_MISMATCH_BETWEEN_CUSTOMER_NUMBER_ENTERED_AND_PHONE_OR_ADDRESS_MATCHED, "", 0);													
				}
			}

			
			// qqq
			{
				String domain = Utils.getEmailDomain(e_mail_address);
				
				ArrayList<Contact> contactsThatMatchedDomain = Contact.getContactsThatMatchedDomainWithinGivenCustomers(dbConnector, distinctCustNums_ThatMatchedPhoneOrAddress_HS, domain);
				distinctCustNums_ThatMatchedEmailDomain = Cust.getTheListOfDistinctCustNos(contactsThatMatchedDomain);
				
				if (distinctCustNums_ThatMatchedEmailDomain != null) {
					for (Iterator<String> iterator = distinctCustNums_ThatMatchedEmailDomain.iterator(); iterator.hasNext();) {
						log.debug("EMail Domain matching Customers: " + iterator.next());
					}
				}
				
				if ((distinctCustNums_ThatMatchedEmailDomain != null) && (distinctCustNums_ThatMatchedEmailDomain.size() > 0)) {
					EMAIL_DOMAIN_MATCHED = true;
				}
			}
			if (EMAIL_DOMAIN_MATCHED == false) {
				log.debug("ERROR_EMAIL_DOMAIN_DID_NOT_MATCH");
				printMatchFlags(matchResultsHM, CUST_NUM_SUPPLIED, CUST_NUM_MATCHED, EMAIL_MATCHED, PHONE_NUMBER_MATCHED, ADDRESS_MATCHED, NAME_MATCHED, EMAIL_DOMAIN_MATCHED, EMAIL_DOMAIN_IS_PUBLIC_DOMAIN);
				return new CustNumBasedMatcherResult(CustNumBasedMatcherResult.ERROR_EMAIL_DOMAIN_DID_NOT_MATCH, "", 0);																	
			}
			
			
			if (CUST_NUM_SUPPLIED == false) {
				/*
				HashSet<String> customersInAllMatches = null;
				if (suppliedCustNums != null) {
				    customersInAllMatches = new HashSet<String>(suppliedCustNums);
				} else if (distinctCustNums_ThatMatchedEmail_HS != null) {
				    customersInAllMatches = new HashSet<String>(distinctCustNums_ThatMatchedEmail_HS);
				} else if (distinctCustNums_ThatMatchedPhone_HS != null) {
				    customersInAllMatches = new HashSet<String>(distinctCustNums_ThatMatchedPhone_HS);
				} else if (distinctCustNums_ThatMatchedAddress_HS != null) {
				    customersInAllMatches = new HashSet<String>(distinctCustNums_ThatMatchedAddress_HS);
				}

				if (suppliedCustNums != null) {
				    customersInAllMatches.retainAll(suppliedCustNums);
				}
				if (distinctCustNums_ThatMatchedEmail_HS != null) {
				    customersInAllMatches.retainAll(distinctCustNums_ThatMatchedEmail_HS);
				}
				if (distinctCustNums_ThatMatchedPhone_HS != null) {
				    customersInAllMatches.retainAll(distinctCustNums_ThatMatchedPhone_HS);
				}
				if (distinctCustNums_ThatMatchedAddress_HS != null) {
				    customersInAllMatches.retainAll(distinctCustNums_ThatMatchedAddress_HS);
				}
				*/
				
				// qqq
				if (distinctCustNums_ThatMatchedEmailDomain.size() == 1) {
					for (Iterator<String> iterator = distinctCustNums_ThatMatchedEmailDomain.iterator(); iterator.hasNext();) {
						cust_num = iterator.next();
						break;
					}
				} else {
				    String customerWithMostRecentOrder = Order.getCustomerWithMostRecentOrder(dbConnector, distinctCustNums_ThatMatchedEmailDomain);
				    if (customerWithMostRecentOrder != null) {
				    	log.debug("Customer with most recent order: " + customerWithMostRecentOrder);
						cust_num = customerWithMostRecentOrder;			    	
				    } else {
					    String customerWhoWasMostRecentlyCreated = Cust.getCustomerWhoWasMostRecentlyCreated(dbConnector, distinctCustNums_ThatMatchedEmailDomain);
					    if (customerWhoWasMostRecentlyCreated != null) {
					    	log.debug("Customer who was most recently created: " + customerWhoWasMostRecentlyCreated);
							cust_num = customerWhoWasMostRecentlyCreated;	
					    } else {
					    	log.debug("The matched customers didn't have orders, and they didn't have cust_added_date fields filled in, so taking the first matched customer.");
					    	cust_num = (String) (distinctCustNums_ThatMatchedEmailDomain.toArray())[0];
					    }
				    }
				}
			}
			
			
			ArrayList<Contact> contactsWithMatchingFirstInitialAndLastName = Contact.findContactWithMatchingFirstInitialAndLastName(dbConnector, cust_num, firstName.substring(0, 1), lastName);
			if ((contactsWithMatchingFirstInitialAndLastName != null) && (contactsWithMatchingFirstInitialAndLastName.size() == 1)) {
				NAME_MATCHED = true;
				cont_no = contactsWithMatchingFirstInitialAndLastName.get(0).getCont_no();
			}
			
			ArrayList<Cust> custAl = Cust.getCustForGivenCustNo(dbConnector, cust_num);
			if ((custAl == null) || (custAl.size() == 0) || (custAl.get(0).getIs_active() == false)) {
				printMatchFlags(matchResultsHM, CUST_NUM_SUPPLIED, CUST_NUM_MATCHED, EMAIL_MATCHED, PHONE_NUMBER_MATCHED, ADDRESS_MATCHED, NAME_MATCHED, EMAIL_DOMAIN_MATCHED, EMAIL_DOMAIN_IS_PUBLIC_DOMAIN);
				return new CustNumBasedMatcherResult(CustNumBasedMatcherResult.ERROR_MATCHED_CUSTOMER_IS_INACTIVE, "", 0);																	
			}
			
			if ((CUST_NUM_MATCHED == false	) &&
				(EMAIL_MATCHED == false		) &&
				(NAME_MATCHED == false		)	 ) {
				log.debug("Matched for NO terms.");
				printMatchFlags(matchResultsHM, CUST_NUM_SUPPLIED, CUST_NUM_MATCHED, EMAIL_MATCHED, PHONE_NUMBER_MATCHED, ADDRESS_MATCHED, NAME_MATCHED, EMAIL_DOMAIN_MATCHED, EMAIL_DOMAIN_IS_PUBLIC_DOMAIN);
				return new CustNumBasedMatcherResult(CustNumBasedMatcherResult.MATCHED_FOR_NO_TERMS, cust_num, cont_no);																	
			} else {
				log.debug("Matched for terms.");
				printMatchFlags(matchResultsHM, CUST_NUM_SUPPLIED, CUST_NUM_MATCHED, EMAIL_MATCHED, PHONE_NUMBER_MATCHED, ADDRESS_MATCHED, NAME_MATCHED, EMAIL_DOMAIN_MATCHED, EMAIL_DOMAIN_IS_PUBLIC_DOMAIN);
				return new CustNumBasedMatcherResult(CustNumBasedMatcherResult.MATCHED_FOR_TERMS, cust_num, cont_no);																	
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new CustNumBasedMatcherResult(CustNumBasedMatcherResult.ERROR_EXCEPTION_OCCURRED, "", 0);																				
		} catch (PortalException e) {
			e.getE().printStackTrace();
			return new CustNumBasedMatcherResult(CustNumBasedMatcherResult.ERROR_EXCEPTION_OCCURRED, "", 0);																				
		}
	}
	
	public static void printMatchFlags(HashMap<String, String> matchResultsHM, boolean CUST_NUM_SUPPLIED, boolean CUST_NUM_MATCHED, boolean EMAIL_MATCHED, boolean PHONE_NUMBER_MATCHED, boolean ADDRESS_MATCHED, boolean NAME_MATCHED, boolean EMAIL_DOMAIN_MATCHED, boolean EMAIL_DOMAIN_IS_PUBLIC_DOMAIN) {
		log.debug("MatRes CUST_NUM_SUPPLIED    " + CUST_NUM_SUPPLIED);
		log.debug("MatRes CUST_NUM_MATCHED     " + CUST_NUM_MATCHED);
		log.debug("MatRes EMAIL_MATCHED        " + EMAIL_MATCHED);
		log.debug("MatRes PHONE_NUMBER_MATCHED " + PHONE_NUMBER_MATCHED);
		log.debug("MatRes ADDRESS_MATCHED      " + ADDRESS_MATCHED);
		log.debug("MatRes NAME_MATCHED         " + NAME_MATCHED);
		log.debug("MatRes EMAIL_DOMAIN_MATCHED " + EMAIL_DOMAIN_MATCHED);
		
        matchResultsHM.put("CNUM_SUPP", "" + CUST_NUM_SUPPLIED);
        matchResultsHM.put("CNUM_MATCHED", "" + CUST_NUM_MATCHED);
        matchResultsHM.put("EMAIL_MATCHED", "" + EMAIL_MATCHED);
        matchResultsHM.put("PH_MATCHED", "" + PHONE_NUMBER_MATCHED);
        matchResultsHM.put("ADDR_MATCHED", "" + ADDRESS_MATCHED);
        matchResultsHM.put("NAME_MATCHED", "" + NAME_MATCHED);
        matchResultsHM.put("DOM_MATCHED", "" + EMAIL_DOMAIN_MATCHED);
        matchResultsHM.put("PUB_DOM", "" + EMAIL_DOMAIN_IS_PUBLIC_DOMAIN);        
	}
}
