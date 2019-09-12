package galco.portal.user;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jettison.json.JSONException;

import galco.portal.db.DBConnector;
import galco.portal.emailpref.EmailPref;
import galco.portal.exception.GluuCreateFailureException;
import galco.portal.exception.InvalidUserDataException;
import galco.portal.exception.MoreThanOneCustMatchException;
import galco.portal.exception.OtherException;
import galco.portal.exception.PortalException;
import galco.portal.exception.UserAlreadyExistsInSpolicyException;
import galco.portal.exception.UserExistsInGluuException;
import galco.portal.user.signup.UserSignUpData;
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.Codes;
import galco.portal.wds.dao.Contact;
import galco.portal.wds.dao.Credit;
import galco.portal.wds.dao.Cust;
import galco.portal.wds.dao.Custship;
import galco.portal.wds.dao.Email_profile;
import galco.portal.wds.dao.Email_profile_err;
import galco.portal.wds.dao.Order;
import galco.portal.wds.dao.PortalLog;
import galco.portal.wds.dao.Sales_rep;
import galco.portal.wds.dao.Spolicy;
import galco.portal.wds.dao.UserSession;
import galco.portal.wds.matcher.CustNumBasedMatcherResult;
import galco.portal.wds.matcher.ExistingContactMatcher;
import galco.portal.wds.matcher.WDSMatchingResults;

public class User {
	private static Logger log = Logger.getLogger(User.class);

	private GluuUser gluuUser;
	private WdsUser wdsUser;

	// -------------------------------------------------------------------------------------------------------------

	private User() {
	}

	// -------------------------------------------------------------------------------------------------------------

	public static User createAUser(DBConnector dbConnector, UserSignUpData userSignUpData, CustNumBasedMatcherResult custNumBasedMatcherResult, HashMap<String, String> matchResultsHM) throws InvalidUserDataException, UserExistsInGluuException, MoreThanOneCustMatchException, GluuCreateFailureException, PortalException, UserAlreadyExistsInSpolicyException {
		if (userSignUpData.isAllUserEnteredDataValid() != true) {
			throw new InvalidUserDataException("Data entered by the user has errors in them.");
		}
		if (GluuUser.userExists(userSignUpData.getEmail()) == true) {
			throw new UserExistsInGluuException("User already exists in GLUU.");
		}

		Connection connection = null;
		
		try {
			connection = dbConnector.getConnectionWDS();
			
			// subb 02-09-2017
			dbConnector.setIsolationLevel(connection, Connection.TRANSACTION_READ_COMMITTED);
			dbConnector.setIgnoreIsolationLevel(true);
			connection.setAutoCommit(false);

        	// log.debug("subb 02-09-2017 USER 1 connection.getAutoCommit() " + connection.getAutoCommit());			

			Cust cust = null;
			Contact contact = null;
			Credit credit = null;
			Spolicy spolicy = null;
			Custship custship = null;
			boolean terms = false; 

			if ((custNumBasedMatcherResult != null																	 ) &&
				((custNumBasedMatcherResult.getReturnCode() == CustNumBasedMatcherResult.MATCHED_FOR_TERMS		) ||
				 (custNumBasedMatcherResult.getReturnCode() == CustNumBasedMatcherResult.MATCHED_FOR_NO_TERMS	)    )    ) {
				cust = Cust.getCustForGivenCustNo(dbConnector, custNumBasedMatcherResult.getCust_num()).get(0);

				int contact_no = 1;
				if (custNumBasedMatcherResult.getCont_no() == 0) {
					contact_no = Contact.findMaxContactNumUsed(dbConnector, custNumBasedMatcherResult.getCust_num()) + 1;

			        String curDateYYYYMMDD = new SimpleDateFormat("yyyy/MM/dd").format(new Date());

			        
			        /*
					contact = new Contact(cust.getCust_num(), contact_no, userSignUpData.getFirstName(),
							userSignUpData.getMiddleInitial(), userSignUpData.getLastName(),
							userSignUpData.getEmail(),
							userSignUpData.getPhoneWork(),
							StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCell():userSignUpData.getPhoneCompany(),
							userSignUpData.getPhoneWorkExt(),
							StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCellExt():userSignUpData.getPhoneCompanyExt(),
							"webuser", curDateYYYYMMDD);
					*/

			        
                    contact = new Contact(cust.getCust_num(), contact_no, 
                            			  Utils.replaceSingleQuotesIfNotNull(userSignUpData.getFirstName()),
                            			  Utils.replaceSingleQuotesIfNotNull(userSignUpData.getMiddleInitial()), 
                            			  Utils.replaceSingleQuotesIfNotNull(userSignUpData.getLastName()),
                            			  Utils.replaceSingleQuotesIfNotNull(userSignUpData.getEmail()),
                            			  userSignUpData.getPhoneWork(),
                            			  StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCell():userSignUpData.getPhoneCompany(),
                            			  userSignUpData.getPhoneWorkExt(),
                            			  StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCellExt():userSignUpData.getPhoneCompanyExt(),
                            			  "webuser", curDateYYYYMMDD);
					contact.persist(dbConnector);
					
					// String emailAddresses = "sati@galco.com,webreport@galco.com";
					String emailAddresses = "sati@galco.com";
					String repEmail = "";
					String salesRep2 = cust.getSales_rep2();
					if (StringUtils.isBlank(salesRep2) == false) {
						repEmail = Codes.getEmailOfSalesRep(dbConnector, salesRep2);
						if (StringUtils.isBlank(repEmail) == false) {
							repEmail = "," + repEmail;
						} else {
							repEmail = "";
						}
					}
					emailAddresses = emailAddresses + repEmail;
					// emailAddresses = "sati@galco.com,rajuati@yahoo.com,subbaraoati1@gmail.com";
					
					String eMailText = "New registered user has been added to an existing customer as a new Contact.\n" +
							   		   "Customer Number: " + cust.getCust_num() + "\n" +
							   		   "Contact Number : " + contact_no + "\n" +
							   		   "Email          : " + userSignUpData.getEmail() + "\n" +
							   		   "Work Phone     : " + userSignUpData.getPhoneWork() + "\n" +
							   		   "Company Name   : " + cust.getName() + "\n\n\n";
					// eMailText = eMailText + "***" + repEmail + "***";
					Utils.sendMail(emailAddresses, "WebPortal@galco.com", "New registered user has been added to an existing customer as a new Contact.", eMailText);						
				} else {
					contact_no = custNumBasedMatcherResult.getCont_no();
					contact = Contact.getContact(dbConnector, custNumBasedMatcherResult.getCust_num(), custNumBasedMatcherResult.getCont_no()).get(0);
					
					ArrayList<Spolicy> spolicyAL = Spolicy.getSpolicyRecordForTheGivenCustContact(dbConnector, cust.getCust_num(), contact_no);
					if ((spolicyAL != null) && (spolicyAL.size() > 0)) {
						throw new UserAlreadyExistsInSpolicyException("User already exists in Spolicy.");
					}

					// String emailAddresses = "sati@galco.com,webreport@galco.com";
					String emailAddresses = "sati@galco.com";
					String repEmail = "";
					String salesRep2 = cust.getSales_rep2();
					if (StringUtils.isBlank(salesRep2) == false) {
						repEmail = Codes.getEmailOfSalesRep(dbConnector, salesRep2);
						if (StringUtils.isBlank(repEmail) == false) {
							repEmail = "," + repEmail;
						} else {
							repEmail = "";
						}
					}		
					emailAddresses = emailAddresses + repEmail;						
					
					String eMailText = "New registered user has been matched with an existing cust/contact.\n" +
					   		   "Customer Number: " + cust.getCust_num() + "\n" +
					   		   "Contact Number : " + contact_no + "\n" +
					   		   "Email          : " + userSignUpData.getEmail() + "\n" +
					   		   "Work Phone     : " + userSignUpData.getPhoneWork() + "\n" +
					   		   "Company Name   : " + cust.getName() + "\n\n\n";
					// eMailText = eMailText + "***" + repEmail + "***";
					Utils.sendMail(emailAddresses, "WebPortal@galco.com", "New registered user has been matched with an existing cust/contact.", eMailText);
				}


				terms = (custNumBasedMatcherResult.getReturnCode() == CustNumBasedMatcherResult.MATCHED_FOR_TERMS);				
				// spolicy = new Spolicy(userSignUpData.getEmail(), cust.getCust_num(), contact_no, "c,v", "c,v", "y", "c,v,m,d", "y", false, false, true);
				spolicy = new Spolicy(Utils.replaceSingleQuotesIfNotNull(userSignUpData.getEmail()), custNumBasedMatcherResult.getCust_num(), contact_no, "c,v", "c,v", "y", "c,v,m,d", "y", false, terms, true, (terms?"1":"0"), "", "");
				spolicy.persist(dbConnector);
			} else {
				String country = userSignUpData.getCountry();
				if ((country != null) && (country.compareToIgnoreCase("United States") == 0)) {
					country = "USA";
					log.debug("Found United States, changed to " + country);
				}

				String state = userSignUpData.getState();
				if ((state != null) && (state.length() > 2)) {
					log.debug("State has length > 2. Truncating... State b4: " + state + ", state after truncation: " + state.substring(0, 2));					
					state = state.substring(0, 2);					
				}

				// qqq
				String faxNo = userSignUpData.getFax();
				if (StringUtils.isBlank(faxNo)) {
					faxNo = "NOFAX";
				}

				
				/*
				cust = new Cust("", userSignUpData.getCompany(), userSignUpData.getAddress(), (StringUtils.isBlank(userSignUpData.getAddress2())?"":userSignUpData.getAddress2()),
						 // userSignUpData.getCity(), userSignUpData.getState(), userSignUpData.getZip(),
						 userSignUpData.getCity(), state, userSignUpData.getZip(),
						 // userSignUpData.getCountry(), userSignUpData.getFirstName() + " " + userSignUpData.getLastName(), userSignUpData.getPhoneWork(),
						 country, userSignUpData.getFirstName() + " " + userSignUpData.getLastName(), userSignUpData.getPhoneWork(),
						 StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCell():userSignUpData.getPhoneCompany(),
						 // userSignUpData.getFax());
								 faxNo);
				*/

				// Utils.replaceSingleQuotesIfNotNull(

				/*
				cust = new Cust("", userSignUpData.getCompany(), 
							    userSignUpData.getAddress(), 
							    (StringUtils.isBlank(userSignUpData.getAddress2())?"":userSignUpData.getAddress2()),
							    userSignUpData.getCity(), 
							    state, 
							    userSignUpData.getZip(),
							    country, userSignUpData.getFirstName() + " " + userSignUpData.getLastName(),
							    userSignUpData.getPhoneWork(),
							    StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCell():userSignUpData.getPhoneCompany(),
								faxNo);
				*/
				
				// log.debug("Subb0-01-17-2017 Trying to insert cust. 01-17-2017 email: " + userSignUpData.getEmail());

				// log.debug("subb 02-09-2017 USER 3 connection.getAutoCommit() " + connection.getAutoCommit());

                cust = new Cust("", 
                        Utils.replaceSingleQuotesIfNotNull(userSignUpData.getCompany()), 
                        Utils.replaceSingleQuotesIfNotNull(userSignUpData.getAddress()), 
                        Utils.replaceSingleQuotesIfNotNull((StringUtils.isBlank(userSignUpData.getAddress2())?"":userSignUpData.getAddress2())), 
                        Utils.replaceSingleQuotesIfNotNull(userSignUpData.getCity()), 
                        state, 
                        userSignUpData.getZip(), 
                        Utils.replaceSingleQuotesIfNotNull(country), 
                        Utils.replaceSingleQuotesIfNotNull(userSignUpData.getFirstName() + " " + userSignUpData.getLastName()), 
                        userSignUpData.getPhoneWork(), 
                        StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCell():userSignUpData.getPhoneCompany(), 
                        faxNo);

    			// log.debug("subb 02-09-2017 USER 4 connection.getAutoCommit() " + connection.getAutoCommit());
                
				cust.persist(dbConnector);

				// if (true) throw new RuntimeException("Subban Abcd " + cust.getCust_num());

				log.debug("Subb0-01-17-2017 Successfully iserted cust. 01-17-2017 email: " + userSignUpData.getEmail() + ". cust_num: " + cust.getCust_num());

				String curDateYYYYMMDD = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
		        
		        /*
				contact = new Contact(cust.getCust_num(), 1, userSignUpData.getFirstName(),
						userSignUpData.getMiddleInitial(), userSignUpData.getLastName(),
						userSignUpData.getEmail(),
						userSignUpData.getPhoneWork(),
						StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCell():userSignUpData.getPhoneCompany(),
						userSignUpData.getPhoneWorkExt(),
						StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCellExt():userSignUpData.getPhoneCompanyExt(),
						"webuser", curDateYYYYMMDD);
				*/
		        
                contact = new Contact(cust.getCust_num(), 1, 
                                      Utils.replaceSingleQuotesIfNotNull(userSignUpData.getFirstName()),
                                      Utils.replaceSingleQuotesIfNotNull(userSignUpData.getMiddleInitial()), 
                                      Utils.replaceSingleQuotesIfNotNull(userSignUpData.getLastName()),
                                      Utils.replaceSingleQuotesIfNotNull(userSignUpData.getEmail()),
                                      userSignUpData.getPhoneWork(),
                                      StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCell():userSignUpData.getPhoneCompany(),
                                      userSignUpData.getPhoneWorkExt(),
                                      StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCellExt():userSignUpData.getPhoneCompanyExt(),
                                      "webuser", curDateYYYYMMDD);			
				contact.persist(dbConnector);

				// if (true) throw new RuntimeException("Throwing a runtime exception for testing.");

				credit = new Credit(cust.getCust_num(), "Customer added through O/E ---- WEB");
				credit.persist(dbConnector);
				
				String sameAsBilling = userSignUpData.getSameAsBilling();
				// if ((sameAsBilling != null) && (sameAsBilling.compareTo("yes") != 0)) {
				if ((sameAsBilling == null) || (sameAsBilling.compareToIgnoreCase("yes") != 0)) {
					String modifiedCountry = userSignUpData.getCountryShip();
					if (modifiedCountry.compareToIgnoreCase("United States") == 0 ) {
						modifiedCountry = "USA";
					}
					
		            // subb 02-09-2017
					String stateShip = userSignUpData.getStateShip();
					if ((stateShip != null) && (stateShip.length() > 2)) {
						log.debug("stateShip has length > 2. Truncating... stateShip b4: " + stateShip + ", stateShip after truncation: " + stateShip.substring(0, 2));
						stateShip = stateShip.substring(0, 2);
					}					
					
					// custship = new Custship(cust.getCust_num(), null, contact.getCont_no(), userSignUpData.getFirstNameShip(), userSignUpData.getAddressShip(), userSignUpData.getAddress2Ship(), userSignUpData.getCityShip(), userSignUpData.getStateShip(), userSignUpData.getZipShip(), userSignUpData.getCountryShip(), userSignUpData.getPhoneShip(), false);
					custship = new Custship(cust.getCust_num(), null, contact.getCont_no(), userSignUpData.getFirstNameShip(), userSignUpData.getAddressShip(), userSignUpData.getAddress2Ship(), userSignUpData.getCityShip(), stateShip, userSignUpData.getZipShip(), modifiedCountry, userSignUpData.getPhoneShip(), false);

					custship.persist(dbConnector);
				}

				
				log.debug("Subb0-01-17-2017 Trying to insert Spolicy. 01-17-2017 email: " + userSignUpData.getEmail());
				// spolicy = new Spolicy(userSignUpData.getEmail(), cust.getCust_num(), 1, "c,v", "c,v", "y", "c,v,m,d", "y", false, false, true);
				spolicy = new Spolicy(Utils.replaceSingleQuotesIfNotNull(userSignUpData.getEmail()), cust.getCust_num(), 1, "c,v", "c,v", "y", "c,v,m,d", "y", false, false, true, "0", "", ((custship != null)?custship.getShipto_num():""));
				try {
					spolicy.persist(dbConnector);					
					log.debug("Subb0-01-17-2017 Spolicy inserted successfully. 01-17-2017 email: " + userSignUpData.getEmail());					
				} catch (PortalException e) {
					log.debug("Subb0-01-17-2017 Spolicy insert failed. 01-17-2017 email: " + userSignUpData.getEmail());
					throw e;
				}
			}
						
			log.debug("Subb0-01-17-2017 Going to create GLUU Account. 01-17-2017 email: " + userSignUpData.getEmail());

			GluuUser gluuUser = new GluuUser("", userSignUpData.getEmail(), userSignUpData.getPassword(),
					userSignUpData.getFirstName(), userSignUpData.getLastName(),
					userSignUpData.getEmail(),
					userSignUpData.getPhoneWork(), userSignUpData.getPhoneWorkExt(),
					userSignUpData.getPhoneCompany(), userSignUpData.getPhoneCompanyExt(),
					userSignUpData.getPhoneCell(), userSignUpData.getPhoneCellExt(),
					userSignUpData.getFax(),
					userSignUpData.getAddress(), userSignUpData.getCity(), userSignUpData.getZip(),
					userSignUpData.getState(), userSignUpData.getCountry(),
					"order,quote,wishlist,reorder,convertquote",
					cust.getCust_num(), contact.getCont_no(),
					// "ZZZ", ((custship == null)?"ZZZ":custship.getShipto_num()), (terms?"1":"0"), false);
					"", ((custship == null)?"":custship.getShipto_num()), (terms?"1":"0"), false);
			gluuUser.persist();

			log.debug("Subb0-01-17-2017 GLUU Account created successfully. 01-17-2017 email: " + userSignUpData.getEmail());

			if (matchResultsHM != null) {
			    PortalLog portalLog = new PortalLog(userSignUpData.getEmail(), Utils.convertHashMapToXML(matchResultsHM));
			    portalLog.persist(dbConnector);				
			}
			
			connection.commit();
            // subb 02-09-2017			
			dbConnector.setIgnoreIsolationLevel(false);			
			dbConnector.setIsolationLevel(connection, Connection.TRANSACTION_READ_UNCOMMITTED);
			connection.setAutoCommit(true);

			EmailPref.createNewProfile(dbConnector, userSignUpData.getEmail(), userSignUpData.getFirstName(), userSignUpData.getLastName());
			
			log.debug("Subb0-01-17-2017 Commit successful. 01-17-2017 email: " + userSignUpData.getEmail());

			User user = new User();
			user.setGluuUser(gluuUser);
			user.setWdsUser(new WdsUser(cust, contact, credit, spolicy));

			return user;
		// } catch (Exception e) {
		} catch (PortalException e) {
			log.debug("Exception occurred while trying to create a user.");
			try {
				connection.rollback();
	            // subb 02-09-2017			
				dbConnector.setIgnoreIsolationLevel(false);			
				dbConnector.setIsolationLevel(connection, Connection.TRANSACTION_READ_UNCOMMITTED);
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				log.debug("Exception occurred while trying to rollback.");				
				throw e;				
			}
			log.debug("Successfully rolled back changes after an exception.");

			throw e;
		} catch (Exception e) {
				log.debug("Exception occurred while trying to create a user.");
				try {
					connection.rollback();
		            // subb 02-09-2017			
					dbConnector.setIgnoreIsolationLevel(false);			
					dbConnector.setIsolationLevel(connection, Connection.TRANSACTION_READ_UNCOMMITTED);
					connection.setAutoCommit(true);
				} catch (SQLException e1) {
					log.debug("Exception occurred while trying to rollback.");				
					throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);					
				}
				log.debug("Successfully rolled back changes after an exception.");

				throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}
	}

	// -------------------------------------------------------------------------------------------------------------

	public static User createAUserP1(DBConnector dbConnector, UserSignUpData userSignUpData) throws InvalidUserDataException, UserExistsInGluuException, MoreThanOneCustMatchException, GluuCreateFailureException, PortalException, UserAlreadyExistsInSpolicyException {
		if (userSignUpData.isAllUserEnteredDataValid() != true) {
			throw new InvalidUserDataException("Data entered by the user has errors in them.");
		}
		if (GluuUser.userExists(userSignUpData.getEmail()) == true) {
			throw new UserExistsInGluuException("User already exists in GLUU.");
		}

		Connection connection = null;
		
		try {
			connection = dbConnector.getConnectionWDS();
			connection.setAutoCommit(false);

			Cust cust = null;
			Contact contact = null;
			Credit credit = null;
			Spolicy spolicy = null;

			
			
			// WDSMatchingResults wdsMatchingResults = ExistingContactMatcher.findContact("david@bestindustrialservices.net", "8325267655", "1234567890", "1234567890", "806 BRACANA CT", "123 Bhavanam Veedhi", "77406");
			// WDSMatchingResults wdsMatchingResults = ExistingContactMatcher.findContact(dbConnector, userSignUpData.getEmail(), userSignUpData.getPhoneWork(), userSignUpData.getPhoneCompany(), userSignUpData.getPhoneCell(), userSignUpData.getAddress(), userSignUpData.getAddress2(), userSignUpData.getZip());
			WDSMatchingResults wdsMatchingResults = ExistingContactMatcher.findContact(dbConnector, Utils.replaceSingleQuotesIfNotNull(userSignUpData.getEmail()), userSignUpData.getPhoneWork(), userSignUpData.getPhoneCompany(), userSignUpData.getPhoneCell(), userSignUpData.getAddress(), userSignUpData.getAddress2(), userSignUpData.getZip());

			

			HashSet<String> distinctMatchedCustNums_HS = wdsMatchingResults.getDistinctMatchedCustNums_HS();
			if (distinctMatchedCustNums_HS != null) {
				if (distinctMatchedCustNums_HS.size() > 1) {
					throw new MoreThanOneCustMatchException("Matched more than one customer.");
				} else {
					ArrayList<Cust> matchedCusts = Cust.getCustsForGivenCustNos(dbConnector, distinctMatchedCustNums_HS);
					cust = matchedCusts.get(0);

					ArrayList<Contact> matchedContacts = Contact.getContactsOfACustomer(dbConnector, cust.getCust_num());
					contact = Contact.findContactWithMatchingFirstAndLastNames(matchedContacts, userSignUpData.getFirstName(), userSignUpData.getLastName());

					int contact_no = 1;
					if (contact == null) {
						contact_no = Contact.findMaxContactNumUsed(matchedContacts) + 1;

				        String curDateYYYYMMDD = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
				        

				        
				        /*
						contact = new Contact(cust.getCust_num(), contact_no, userSignUpData.getFirstName(),
								userSignUpData.getMiddleInitial(), userSignUpData.getLastName(),
								userSignUpData.getEmail(),
								userSignUpData.getPhoneWork(),
								StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCell():userSignUpData.getPhoneCompany(),
								userSignUpData.getPhoneWorkExt(),
								StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCellExt():userSignUpData.getPhoneCompanyExt(),
								"webuser", curDateYYYYMMDD);
						*/
				        
                        contact = new Contact(cust.getCust_num(), contact_no, 
                                			  Utils.replaceSingleQuotesIfNotNull(userSignUpData.getFirstName()),
                                			  Utils.replaceSingleQuotesIfNotNull(userSignUpData.getMiddleInitial()), 
                                			  Utils.replaceSingleQuotesIfNotNull(userSignUpData.getLastName()),
                                			  Utils.replaceSingleQuotesIfNotNull(userSignUpData.getEmail()),
                                			  userSignUpData.getPhoneWork(),
                                			  StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCell():userSignUpData.getPhoneCompany(),
                                			  userSignUpData.getPhoneWorkExt(),
                                			  StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCellExt():userSignUpData.getPhoneCompanyExt(),
                                			  "webuser", curDateYYYYMMDD);
						
						
						
						contact.persist(dbConnector);
						
						// String emailAddresses = "sati@galco.com,webreport@galco.com";
						String emailAddresses = "sati@galco.com";
						String repEmail = "";
						String salesRep2 = cust.getSales_rep2();
						if (StringUtils.isBlank(salesRep2) == false) {
							repEmail = Codes.getEmailOfSalesRep(dbConnector, salesRep2);
							if (StringUtils.isBlank(repEmail) == false) {
								repEmail = "," + repEmail;
							} else {
								repEmail = "";
							}
						}
						emailAddresses = emailAddresses + repEmail;
						// emailAddresses = "sati@galco.com,rajuati@yahoo.com,subbaraoati1@gmail.com";
						
						String eMailText = "New registered user has been added to an existing customer as a new Contact.\n" +
								   		   "Customer Number: " + cust.getCust_num() + "\n" +
								   		   "Contact Number : " + contact_no + "\n" +
								   		   "Email          : " + userSignUpData.getEmail() + "\n" +
								   		   "Work Phone     : " + userSignUpData.getPhoneWork() + "\n" +
								   		   "Company Name   : " + cust.getName() + "\n\n\n";
						// eMailText = eMailText + "***" + repEmail + "***";
						Utils.sendMail(emailAddresses, "WebPortal@galco.com", "New registered user has been added to an existing customer as a new Contact.", eMailText);						
					} else {
						contact_no = contact.getCont_no();
						
						ArrayList<Spolicy> spolicyAL = Spolicy.getSpolicyRecordForTheGivenCustContact(dbConnector, cust.getCust_num(), contact_no);
						if ((spolicyAL != null) && (spolicyAL.size() > 0)) {
							throw new UserAlreadyExistsInSpolicyException("User already exists in Spolicy.");
						}

						// String emailAddresses = "sati@galco.com,webreport@galco.com";
						String emailAddresses = "sati@galco.com";
						String repEmail = "";
						String salesRep2 = cust.getSales_rep2();
						if (StringUtils.isBlank(salesRep2) == false) {
							repEmail = Codes.getEmailOfSalesRep(dbConnector, salesRep2);
							if (StringUtils.isBlank(repEmail) == false) {
								repEmail = "," + repEmail;
							} else {
								repEmail = "";
							}
						}		
						emailAddresses = emailAddresses + repEmail;						
						
						String eMailText = "New registered user has been matched with an existing cust/contact.\n" +
						   		   "Customer Number: " + cust.getCust_num() + "\n" +
						   		   "Contact Number : " + contact_no + "\n" +
						   		   "Email          : " + userSignUpData.getEmail() + "\n" +
						   		   "Work Phone     : " + userSignUpData.getPhoneWork() + "\n" +
						   		   "Company Name   : " + cust.getName() + "\n\n\n";
						// eMailText = eMailText + "***" + repEmail + "***";						
						Utils.sendMail(emailAddresses, "WebPortal@galco.com", "New registered user has been matched with an existing cust/contact.", eMailText);
					}


					
					// spolicy = new Spolicy(userSignUpData.getEmail(), cust.getCust_num(), contact_no, "c,v", "c,v", "y", "c,v,m,d", "y", false, false, true);
					spolicy = new Spolicy(Utils.replaceSingleQuotesIfNotNull(userSignUpData.getEmail()), cust.getCust_num(), contact_no, "c,v", "c,v", "y", "c,v,m,d", "y", false, false, true, "0", "", "");
					
					
					
					spolicy.persist(dbConnector);
				}
			} else {
				String country = userSignUpData.getCountry();
				if ((country != null) && (country.compareToIgnoreCase("United States") == 0)) {
					country = "USA";
					log.debug("Found United States, changed to " + country);
				}

				String state = userSignUpData.getState();
				if ((state != null) && (state.length() > 2)) {
					log.debug("State has length > 2. Truncating... State b4: " + state + ", state after truncation: " + state.substring(0, 2));					
					state = state.substring(0, 2);					
				}

				// qqq
				String faxNo = userSignUpData.getFax();
				if (StringUtils.isBlank(faxNo)) {
					faxNo = "NOFAX";
				}
				
				
				
				

				/*
				cust = new Cust("", userSignUpData.getCompany(), userSignUpData.getAddress(), (StringUtils.isBlank(userSignUpData.getAddress2())?"":userSignUpData.getAddress2()),
						 // userSignUpData.getCity(), userSignUpData.getState(), userSignUpData.getZip(),
						 userSignUpData.getCity(), state, userSignUpData.getZip(),
						 // userSignUpData.getCountry(), userSignUpData.getFirstName() + " " + userSignUpData.getLastName(), userSignUpData.getPhoneWork(),
						 country, userSignUpData.getFirstName() + " " + userSignUpData.getLastName(), userSignUpData.getPhoneWork(),
						 StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCell():userSignUpData.getPhoneCompany(),
						 // userSignUpData.getFax());
								 faxNo);
				*/

				// Utils.replaceSingleQuotesIfNotNull(

				/*
				cust = new Cust("", userSignUpData.getCompany(), 
							    userSignUpData.getAddress(), 
							    (StringUtils.isBlank(userSignUpData.getAddress2())?"":userSignUpData.getAddress2()),
							    userSignUpData.getCity(), 
							    state, 
							    userSignUpData.getZip(),
							    country, userSignUpData.getFirstName() + " " + userSignUpData.getLastName(),
							    userSignUpData.getPhoneWork(),
							    StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCell():userSignUpData.getPhoneCompany(),
								faxNo);
				*/
				
                cust = new Cust("", 
                        Utils.replaceSingleQuotesIfNotNull(userSignUpData.getCompany()), 
                        Utils.replaceSingleQuotesIfNotNull(userSignUpData.getAddress()), 
                        Utils.replaceSingleQuotesIfNotNull((StringUtils.isBlank(userSignUpData.getAddress2())?"":userSignUpData.getAddress2())), 
                        Utils.replaceSingleQuotesIfNotNull(userSignUpData.getCity()), 
                        state, 
                        userSignUpData.getZip(), 
                        Utils.replaceSingleQuotesIfNotNull(country), 
                        Utils.replaceSingleQuotesIfNotNull(userSignUpData.getFirstName() + " " + userSignUpData.getLastName()), 
                        userSignUpData.getPhoneWork(), 
                        StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCell():userSignUpData.getPhoneCompany(), 
                        faxNo);

				

				
				
				cust.persist(dbConnector);

		        String curDateYYYYMMDD = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
		        
		        
		        
		        /*
				contact = new Contact(cust.getCust_num(), 1, userSignUpData.getFirstName(),
						userSignUpData.getMiddleInitial(), userSignUpData.getLastName(),
						userSignUpData.getEmail(),
						userSignUpData.getPhoneWork(),
						StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCell():userSignUpData.getPhoneCompany(),
						userSignUpData.getPhoneWorkExt(),
						StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCellExt():userSignUpData.getPhoneCompanyExt(),
						"webuser", curDateYYYYMMDD);
				*/
		        
                contact = new Contact(cust.getCust_num(), 1, 
                                      Utils.replaceSingleQuotesIfNotNull(userSignUpData.getFirstName()),
                                      Utils.replaceSingleQuotesIfNotNull(userSignUpData.getMiddleInitial()), 
                                      Utils.replaceSingleQuotesIfNotNull(userSignUpData.getLastName()),
                                      Utils.replaceSingleQuotesIfNotNull(userSignUpData.getEmail()),
                                      userSignUpData.getPhoneWork(),
                                      StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCell():userSignUpData.getPhoneCompany(),
                                      userSignUpData.getPhoneWorkExt(),
                                      StringUtils.isEmpty(userSignUpData.getPhoneCompany())?userSignUpData.getPhoneCellExt():userSignUpData.getPhoneCompanyExt(),
                                      "webuser", curDateYYYYMMDD);			
				
                
				
				contact.persist(dbConnector);

				// if (true) throw new RuntimeException("Throwing a runtime exception for testing.");

				credit = new Credit(cust.getCust_num(), "Customer added through O/E ---- WEB");
				credit.persist(dbConnector);


				
				// spolicy = new Spolicy(userSignUpData.getEmail(), cust.getCust_num(), 1, "c,v", "c,v", "y", "c,v,m,d", "y", false, false, true);
				spolicy = new Spolicy(Utils.replaceSingleQuotesIfNotNull(userSignUpData.getEmail()), cust.getCust_num(), 1, "c,v", "c,v", "y", "c,v,m,d", "y", false, false, true, "0", "", "");
				
								
				
				spolicy.persist(dbConnector);
			}


			GluuUser gluuUser = new GluuUser("", userSignUpData.getEmail(), userSignUpData.getPassword(),
					userSignUpData.getFirstName(), userSignUpData.getLastName(),
					userSignUpData.getEmail(),
					userSignUpData.getPhoneWork(), userSignUpData.getPhoneWorkExt(),
					userSignUpData.getPhoneCompany(), userSignUpData.getPhoneCompanyExt(),
					userSignUpData.getPhoneCell(), userSignUpData.getPhoneCellExt(),
					userSignUpData.getFax(),
					userSignUpData.getAddress(), userSignUpData.getCity(), userSignUpData.getZip(),
					userSignUpData.getState(), userSignUpData.getCountry(),
					"order,quote,wishlist,reorder,convertquote",
					cust.getCust_num(), contact.getCont_no(),
					"", "", "0", false);			
			gluuUser.persist();

			connection.commit();
			connection.setAutoCommit(true);

			User user = new User();
			user.setGluuUser(gluuUser);
			user.setWdsUser(new WdsUser(cust, contact, credit, spolicy));

			return user;
		// } catch (Exception e) {
		} catch (PortalException e) {
			log.debug("Exception occurred while trying to create a user.");
			try {
				connection.rollback();
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				log.debug("Exception occurred while trying to rollback.");				
				throw e;				
			}
			log.debug("Successfully rolled back changes after an exception.");

			throw e;
		} catch (SQLException e) {
				log.debug("Exception occurred while trying to create a user.");
				try {
					connection.rollback();
					connection.setAutoCommit(true);
				} catch (SQLException e1) {
					log.debug("Exception occurred while trying to rollback.");				
					throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);					
				}
				log.debug("Successfully rolled back changes after an exception.");

				throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}
	}

	// -------------------------------------------------------------------------------------------------------------

	// -------------------------------------------------------------------------------------------------------------

	public static void updateGluu_emailConfirmed_flag(DBConnector dbConnector, String email, boolean emailConfirmed) throws PortalException {
		try {
			GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(email, null, true);
			gluuUser.setEmailConfirmed(emailConfirmed);
			
			gluuUser.modify(false);
		} catch (PortalException e) {
			log.debug("Exception occurred while trying to update emailConfirmed. email: " + email);
			throw e;
		}
	}

	public static void updateGluuAndContact(DBConnector dbConnector, UserSession userSession, UserSignUpData userSignUpData) throws InvalidUserDataException, PortalException {
		if ((userSignUpData.is_email_Valid() == false			) 	||
			(userSignUpData.is_firstName_Valid() == false		)	||
			(userSignUpData.is_lastName_Valid() == false		) 	||
			(userSignUpData.is_middleInitial_Valid() == false	)      ) {
			String message = "";

		    if (!userSignUpData.is_email_Valid()) {
		        message += ((message != null)?"<p/>":"") + "Email entered is not valid.";
		    }
		    if (!userSignUpData.is_firstName_Valid()) {
		        message += ((message != null)?"<p/>":"") + "First name entered is not valid.";
		    }
		    if (!userSignUpData.is_lastName_Valid()) {
		        message += ((message != null)?"<p/>":"") + "Last name entered is not valid.";
		    }
		    if (!userSignUpData.is_middleInitial_Valid()) {
		        message += ((message != null)?"<p/>":"") + "Middle initial entered is not valid.";
		    }

			throw new InvalidUserDataException(message);
		}

		Connection connection = null;
		
		try {
			connection = dbConnector.getConnectionWDS();
            // subb 02-09-2017			
			dbConnector.setIgnoreIsolationLevel(true);				
			dbConnector.setIsolationLevel(connection, Connection.TRANSACTION_READ_COMMITTED);
			connection.setAutoCommit(false);

			/*
			Cust.updateCompanyName(userSession.getCust_num(), userSignUpData.getCompany());
			*/

			// qqq
			// Contact.updateNamesAndEmail(dbConnector, userSession.getCust_num(), userSession.getCont_no(),
			// 		Utils.replaceSingleQuotesIfNotNull(userSignUpData.getFirstName()), Utils.replaceSingleQuotesIfNotNull(userSignUpData.getMiddleInitial()), Utils.replaceSingleQuotesIfNotNull(userSignUpData.getLastName()),
			// 		Utils.replaceSingleQuotesIfNotNull(userSignUpData.getEmail()));
			Contact.updateNames(dbConnector, userSession.getCust_num(), userSession.getCont_no(),
					Utils.replaceSingleQuotesIfNotNull(userSignUpData.getFirstName()), Utils.replaceSingleQuotesIfNotNull(userSignUpData.getMiddleInitial()), Utils.replaceSingleQuotesIfNotNull(userSignUpData.getLastName()));
			
			GluuUser gluuUserOld = GluuUser.retrieveUserInfoFromGluu(userSession.getUsername(), null, true);

			// -------------------------------------------------------------------------------------
			
			Cust cust;
			Contact contact;
			cust = (Cust.getCustForGivenCustNo(dbConnector, userSession.getCust_num())).get(0);
			contact = (Contact.getContact(dbConnector, userSession.getCust_num(), userSession.getCont_no())).get(0);
			GluuUser gluuUser = new GluuUser(gluuUserOld.getInum(), userSignUpData.getEmail(),
					null,
					userSignUpData.getFirstName(), userSignUpData.getLastName(),
					userSignUpData.getEmail(),
					cust.getPhone(), "",
					cust.getPhone_800num(), "",
					contact.getPhone2(), contact.getPhone2_ex(),
					cust.getFax_num(),
					cust.getAddress(), cust.getCity(), cust.getZip(),
					cust.getState(), cust.getCountry(),
					gluuUserOld.getSecurityType(),
					userSession.getCust_num(), userSession.getCont_no(),
					gluuUserOld.getDfltAddrBill(),
					gluuUserOld.getDfltAddrShip(),
					gluuUserOld.getAccessRole(),
					gluuUserOld.getEmailConfirmed());			
			
			//GluuUser gluuUser = new GluuUser(gluuUserOld.getInum(), userSignUpData.getEmail(),
			//		null,
			//		userSignUpData.getFirstName(), userSignUpData.getLastName(),
			//		userSignUpData.getEmail(),
			//		gluuUserOld.getPhoneWork(), gluuUserOld.getPhoneWorkExt(),
			//		gluuUserOld.getPhoneCompany(), gluuUserOld.getPhoneCompanyExt(),
			//		gluuUserOld.getPhoneCell(), gluuUserOld.getPhoneCellExt(),
			//		gluuUserOld.getFax(),
			//		gluuUserOld.getStreetAddress(), gluuUserOld.getLocality(), gluuUserOld.getPostalCode(),
			//		gluuUserOld.getRegion(), gluuUserOld.getCountry(),
			//		gluuUserOld.getSecurityType(),
			//		userSession.getCust_num(), userSession.getCont_no());

			// -------------------------------------------------------------------------------------
			
			gluuUser.modify(false);

			connection.commit();

			// subb 02-09-2017			
			dbConnector.setIgnoreIsolationLevel(false);			
			dbConnector.setIsolationLevel(connection, Connection.TRANSACTION_READ_UNCOMMITTED);
			connection.setAutoCommit(true);
		// } catch (Exception e) {
		} catch (PortalException e) {
			log.debug("Exception occurred while trying to update a user.");
			try {
				connection.rollback();
	            // subb 02-09-2017			
				dbConnector.setIgnoreIsolationLevel(false);			
				dbConnector.setIsolationLevel(connection, Connection.TRANSACTION_READ_UNCOMMITTED);
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				log.debug("Exception occurred while trying to rollback.");	
				throw e;
			}
			log.debug("Successfully rolled back changes after an exception.");

			throw e;
		} catch (Exception e) {
				log.debug("Exception occurred while trying to update a user.");
				try {
					connection.rollback();
		            // subb 02-09-2017			
					dbConnector.setIgnoreIsolationLevel(false);			
					dbConnector.setIsolationLevel(connection, Connection.TRANSACTION_READ_UNCOMMITTED);
					connection.setAutoCommit(true);
				} catch (SQLException e1) {
					log.debug("Exception occurred while trying to rollback.");					
					throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
				}
				log.debug("Successfully rolled back changes after an exception.");

				throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}
	}

	public static void updateEmailAddress(DBConnector dbConnector, UserSession userSession, String newEmail) throws PortalException {
		Connection connection = null;
		
		try {
			connection = dbConnector.getConnectionWDS();
			dbConnector.setIgnoreIsolationLevel(true);				
			dbConnector.setIsolationLevel(connection, Connection.TRANSACTION_READ_COMMITTED);
			connection.setAutoCommit(false);

			Contact.updateEmail( dbConnector, userSession.getCust_num(), userSession.getCont_no(), newEmail);
			
			GluuUser gluuUserOld = GluuUser.retrieveUserInfoFromGluu(userSession.getUsername(), null, true);

			Cust cust;
			Contact contact;
			cust = (Cust.getCustForGivenCustNo(dbConnector, userSession.getCust_num())).get(0);
			contact = (Contact.getContact(dbConnector, userSession.getCust_num(), userSession.getCont_no())).get(0);

			GluuUser gluuUser = new GluuUser(gluuUserOld.getInum(), gluuUserOld.getUserName(),
					null,
					gluuUserOld.getFirstName(), gluuUserOld.getLastName(),
					newEmail,
					cust.getPhone(), "",
					cust.getPhone_800num(), "",
					contact.getPhone2(), contact.getPhone2_ex(),
					cust.getFax_num(),
					cust.getAddress(), cust.getCity(), cust.getZip(),
					cust.getState(), cust.getCountry(),
					gluuUserOld.getSecurityType(),
					userSession.getCust_num(), userSession.getCont_no(),
					gluuUserOld.getDfltAddrBill(),
					gluuUserOld.getDfltAddrShip(),
					gluuUserOld.getAccessRole(),
					gluuUserOld.getEmailConfirmed());			
			gluuUser.modify(false);

			connection.commit();

			dbConnector.setIgnoreIsolationLevel(false);			
			dbConnector.setIsolationLevel(connection, Connection.TRANSACTION_READ_UNCOMMITTED);
			connection.setAutoCommit(true);
		// } catch (Exception e) {
		} catch (PortalException e) {
			log.debug("Exception occurred while trying to change email.");
			try {
				connection.rollback();
	            // subb 02-09-2017			
				dbConnector.setIgnoreIsolationLevel(false);			
				dbConnector.setIsolationLevel(connection, Connection.TRANSACTION_READ_UNCOMMITTED);
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				log.debug("Exception occurred while trying to rollback.");	
				throw e;
			}
			log.debug("Successfully rolled back changes after an exception.");

			throw e;
		} catch (Exception e) {
				log.debug("Exception occurred while trying to change email.");
				try {
					connection.rollback();
		            // subb 02-09-2017			
					dbConnector.setIgnoreIsolationLevel(false);			
					dbConnector.setIsolationLevel(connection, Connection.TRANSACTION_READ_UNCOMMITTED);
					connection.setAutoCommit(true);
				} catch (SQLException e1) {
					log.debug("Exception occurred while trying to rollback.");					
					throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
				}
				log.debug("Successfully rolled back changes after an exception.");

				throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}
	}

	public static boolean changeEmail(DBConnector dbConnector, UserSession userSession, String oldUserID, String newUserID, String password) throws PortalException {
		Connection connection = null;
		
		try {
			connection = dbConnector.getConnectionWDS();
			dbConnector.setIgnoreIsolationLevel(true);				
			dbConnector.setIsolationLevel(connection, Connection.TRANSACTION_READ_COMMITTED);
			connection.setAutoCommit(false); 

			/*
			Spolicy newSpolicy = Spolicy.duplicateSpolicyRecord(dbConnector, oldUserID, newUserID);
			if (newSpolicy == null) {
				log.debug("Failed to duplicate.");
				return false;
			}
			Spolicy.delete(dbConnector, oldUserID);
			*/
			Spolicy newSpolicy = Spolicy.changeUserName(dbConnector, oldUserID, newUserID);
			if (newSpolicy == null) {
				log.debug("Failed to duplicate.");
				return false;
			}

			Contact.updateEmail(dbConnector, userSession.getCust_num(), userSession.getCont_no(),
								Utils.replaceSingleQuotesIfNotNull(newUserID));

			GluuUser newGluuUser = GluuUser.duplicateGLUUAccount(oldUserID, newUserID, password);

			log.debug("Deleting Gluu Account: " + oldUserID);
			GluuUser oldGluuUser = GluuUser.retrieveUserInfoFromGluu(oldUserID, "", true);
			GluuUser.delete(oldGluuUser.getInum());
			
			connection.commit();

			dbConnector.setIgnoreIsolationLevel(false);			
			dbConnector.setIsolationLevel(connection, Connection.TRANSACTION_READ_UNCOMMITTED);
			connection.setAutoCommit(true);
			
			return true;
		} catch (PortalException e) {
			log.debug("Exception occurred while trying to update a user.");
			try {
				connection.rollback();
	            // subb 02-09-2017			
				dbConnector.setIgnoreIsolationLevel(false);			
				dbConnector.setIsolationLevel(connection, Connection.TRANSACTION_READ_UNCOMMITTED);
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				log.debug("Exception occurred while trying to rollback.");	
				throw e;
			}
			log.debug("Successfully rolled back changes after an exception.");

			throw e;
		} catch (Exception e) {
				log.debug("Exception occurred while trying to update a user.");
				try {
					connection.rollback();
		            // subb 02-09-2017			
					dbConnector.setIgnoreIsolationLevel(false);			
					dbConnector.setIsolationLevel(connection, Connection.TRANSACTION_READ_UNCOMMITTED);
					connection.setAutoCommit(true);
				} catch (SQLException e1) {
					log.debug("Exception occurred while trying to rollback.");					
					throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
				}
				log.debug("Successfully rolled back changes after an exception.");

				throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}
	}

	// -------------------------------------------------------------------------------------------------------------

	public GluuUser getGluuUser() {
		return gluuUser;
	}

	public void setGluuUser(GluuUser gluuUser) {
		this.gluuUser = gluuUser;
	}

	public WdsUser getWdsUser() {
		return wdsUser;
	}

	public void setWdsUser(WdsUser wdsUser) {
		this.wdsUser = wdsUser;
	}

}
