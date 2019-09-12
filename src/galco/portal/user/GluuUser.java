package galco.portal.user;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import galco.portal.config.Parms;
import galco.portal.control.ControlServlet;
import galco.portal.db.DBConnector;
import galco.portal.exception.GluuCreateFailureException;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.Spolicy;
import gluu.scim.client.ScimClient;
import gluu.scim.client.ScimResponse;
import gluu.scim.client.auth.OAuthScimClientImpl;
import gluu.scim.client.model.ScimCustomAttributes;
import gluu.scim.client.model.ScimPerson;
import gluu.scim.client.model.ScimPersonAddresses;
import gluu.scim.client.model.ScimPersonEmails;
import gluu.scim.client.model.ScimPersonPhones;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParseException;

public class GluuUser {
	private static Logger log = Logger.getLogger(GluuUser.class);

	ScimPerson scimPerson;

	private String inum;

	private String userName;
	private String password;

	private String firstName;
	private String lastName;

	private String email;

	String phoneWork;
	String phoneWorkExt;
	String phoneCompany;
	String phoneCompanyExt;
	String phoneCell;
	String phoneCellExt;
	String fax;

	private String streetAddress;
	private String locality;
	private String postalCode;
	private String region;
	private String country;

	private String securityType;
	private String custNum;
	private int contNo;
	
	private String dfltAddrBill;
	private String dfltAddrShip;
	private String accessRole;
	private boolean emailConfirmed;

	// -------------------------------------------------------------------------------------------------------------

	public GluuUser() {
	}

	public GluuUser(String inum, String userName, String password, String firstName, String lastName, String email,
					String phoneWork, String phoneWorkExt, String phoneCompany, String phoneCompanyExt,
					String phoneCell, String phoneCellExt, String fax,
					String streetAddress, String locality, String postalCode, String region,
					String country, String securityType,
					String custNum, int contNo) {
		this.inum = inum;
		this.userName = userName;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;

		this.phoneWork = phoneWork;
		this.phoneWorkExt = phoneWorkExt;
		this.phoneCompany = phoneCompany;
		this.phoneCompanyExt = phoneCompanyExt;
		this.phoneCell = phoneCell;
		this.phoneCellExt = phoneCellExt;
		this.fax = fax;

		this.streetAddress = streetAddress;
		this.locality = locality;
		this.postalCode = postalCode;
		this.region = region;
		this.country = country;

		this.securityType = securityType;
		this.custNum = custNum;
		this.contNo = contNo;
	}

	public GluuUser(String inum, String userName, String password, String firstName, String lastName, String email,
			String phoneWork, String phoneWorkExt, String phoneCompany, String phoneCompanyExt,
			String phoneCell, String phoneCellExt, String fax,
			String streetAddress, String locality, String postalCode, String region,
			String country, String securityType,
			String custNum, int contNo,
			String dfltAddrBill,
			String dfltAddrShip,
			String accessRole,
			boolean emailConfirmed) {
		
		this.inum = inum;
		this.userName = userName;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		
		this.phoneWork = phoneWork;
		this.phoneWorkExt = phoneWorkExt;
		this.phoneCompany = phoneCompany;
		this.phoneCompanyExt = phoneCompanyExt;
		this.phoneCell = phoneCell;
		this.phoneCellExt = phoneCellExt;
		this.fax = fax;
		
		this.streetAddress = streetAddress;
		this.locality = locality;
		this.postalCode = postalCode;
		this.region = region;
		this.country = country;
		
		this.securityType = securityType;
		this.custNum = custNum;
		this.contNo = contNo;
		
		this.dfltAddrBill = dfltAddrBill;
		this.dfltAddrShip = dfltAddrShip;
		this.accessRole = accessRole;
		this.emailConfirmed = emailConfirmed;		
	}
	
    // -------------------------------------------------------------------------------------------------------------

    public ScimPerson getScimPerson() {
        return scimPerson;
    }

    public void setScimPerson(ScimPerson scimPerson) {
        this.scimPerson = scimPerson;
    }

    public String getInum() {
        return inum;
    }

    public void setInum(String inum) {
        this.inum = inum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        // log.debug("GluuRemoval: in getFirstName.");
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
    	// log.debug("GluuRemoval: in getLastName.");
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneWork() {
    	// log.debug("GluuRemoval: in getPhoneWork.");
        return phoneWork;
    }
    public void setPhoneWork(String phoneWork) {
        this.phoneWork = phoneWork;
    }
    public String getPhoneWorkExt() {
    	// log.debug("GluuRemoval: in getPhoneWorkExt.");
        return phoneWorkExt;
    }
    public void setPhoneWorkExt(String phoneWorkExt) {
        this.phoneWorkExt = phoneWorkExt;
    }
    public String getPhoneCompany() {
    	// log.debug("GluuRemoval: in getPhoneCompany.");
        return phoneCompany;
    }
    public void setPhoneCompany(String phoneCompany) {
        this.phoneCompany = phoneCompany;
    }
    public String getPhoneCompanyExt() {
    	// log.debug("GluuRemoval: in getPhoneCompanyExt.");
        return phoneCompanyExt;
    }
    public void setPhoneCompanyExt(String phoneCompanyExt) {
        this.phoneCompanyExt = phoneCompanyExt;
    }
    public String getPhoneCell() {
    	// log.debug("GluuRemoval: in getPhoneCell.");
        return phoneCell;
    }
    public void setPhoneCell(String phoneCell) {
        this.phoneCell = phoneCell;
    }
    public String getPhoneCellExt() {
    	// log.debug("GluuRemoval: in getPhoneCellExt.");
        return phoneCellExt;
    }
    public void setPhoneCellExt(String phoneCellExt) {
        this.phoneCellExt = phoneCellExt;
    }
    public String getFax() {
    	// log.debug("GluuRemoval: in getFax.");
        return fax;
    }
    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getStreetAddress() {
    	// log.debug("GluuRemoval: in getStreetAddress.");
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getLocality() {
    	// log.debug("GluuRemoval: in getLocality.");
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getPostalCode() {
    	// log.debug("GluuRemoval: in getPostalCode.");
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getRegion() {
    	// log.debug("GluuRemoval: in getRegion.");
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
    	// log.debug("GluuRemoval: in getCountry.");
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSecurityType() {
        return securityType;
    }

    public void setSecurityType(String securityType) {
        this.securityType = securityType;
    }

    public String getCustNum() {
        return custNum;
    }

    public void setCustNum(String custNum) {
        this.custNum = custNum;
    }

    public int getContNo() {
        return contNo;
    }

    public void setContNo(int contNo) {
        this.contNo = contNo;
    }

	public String getDfltAddrBill() {
		return dfltAddrBill;
	}

	public void setDfltAddrBill(String dfltAddrBill) {
		this.dfltAddrBill = dfltAddrBill;
	}

	public String getDfltAddrShip() {
		return dfltAddrShip;
	}

	public void setDfltAddrShip(String dfltAddrShip) {
		this.dfltAddrShip = dfltAddrShip;
	}

	public String getAccessRole() {
		return accessRole;
	}

	public void setAccessRole(String accessRole) {
		this.accessRole = accessRole;
	}

	public boolean getEmailConfirmed() {
		return emailConfirmed;
	}

	public void setEmailConfirmed(boolean emailConfirmed) {
		this.emailConfirmed = emailConfirmed;
	}

    // -------------------------------------------------------------------------------------------------------------

	public static ScimClient createScimClient(String email, String password) {
		/*
		 * OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD 
		ScimClient client = ScimClient.oAuthInstance("admin", ControlServlet.GUPTA_GLUU, Parms.GLUU_CLIENT,
				 Parms.GLUU_CLIENT_SECRET, "https://" + Parms.GLUU_SERVER_NAME + "/identity/seam/resource/restv1",
				 "https://" + Parms.GLUU_SERVER_NAME + "/oxauth/seam/resource/restv1/oxauth/token");
		*/
		
		/*
		// PRODUCTION SERVER Sciim Client
		*/
		
		ScimClient client = ScimClient.oAuthInstance(email, password, Parms.GLUU_CLIENT,
				Parms.GLUU_CLIENT_SECRET, "https://" + Parms.GLUU_SERVER_NAME + "/identity/seam/resource/restv1",
				"https://" + Parms.GLUU_SERVER_NAME + "/oxauth/seam/resource/restv1/oxauth/token");
		
		/* 

		// New GLUU

		ScimClient client = ScimClient.oAuthInstance(email, password, "@!0231.3470.8EF9.420B!0001!819C.E9B6!0008!AA38.26BC",
				 Parms.GLUU_CLIENT_SECRET, "https://idp.galco.com/identity/seam/resource/restv1",
				 "https://idp.galco.com/oxauth/seam/resource/restv1/oxauth/token");
		
		*/
		
		
		/*
		
		ScimClient client = ScimClient.oAuthInstance(email, password, "@!0231.3470.8EF9.420B!0001!819C.E9B6!0008!C1EE.A592",
				 "847364", "https://idp.galco.com/identity/seam/resource/restv1",
				 "https://idp.galco.com/oxauth/seam/resource/restv1/oxauth/token");
		
		*/

		
		return client;
	}

	public static ScimClient createScimClientDEV(String email, String password) {
		/*
		 * OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD OLD 
		ScimClient client = ScimClient.oAuthInstance("admin", ControlServlet.GUPTA_GLUU, Parms.GLUU_CLIENT,
				 Parms.GLUU_CLIENT_SECRET, "https://" + Parms.GLUU_SERVER_NAME + "/identity/seam/resource/restv1",
				 "https://" + Parms.GLUU_SERVER_NAME + "/oxauth/seam/resource/restv1/oxauth/token");
		*/

		/* Production
		 * GOOD GOOD GOOD GOOD GOOD GOOD GOOD GOOD GOOD GOOD GOOD GOOD GOOD GOOD GOOD GOOD GOOD GOOD 
		ScimClient client = ScimClient.oAuthInstance(email, password, Parms.GLUU_CLIENT,
				 Parms.GLUU_CLIENT_SECRET, "https://" + Parms.GLUU_SERVER_NAME + "/identity/seam/resource/restv1",
				 "https://" + Parms.GLUU_SERVER_NAME + "/oxauth/seam/resource/restv1/oxauth/token");
		*/
		
		/*
		ScimClient client = ScimClient.oAuthInstance(email, password, Parms.GLUU_CLIENT,
				 Parms.GLUU_CLIENT_SECRET, "https://idmdev.galco.com/identity/seam/resource/restv1",
				 "https://idmdev.galco.com/oxauth/seam/resource/restv1/oxauth/token");
		*/
		
		/*
		ScimClient client = ScimClient.oAuthInstance(email, password, "@!90EF.269C.6784.3908!0001!B4FF.77F0!0008!F984.A1EE",
				 "Na67!*7HG0ka", "https://idp.galco.com/identity/seam/resource/restv1",
				 "https://idp.galco.com/oxauth/seam/resource/restv1/oxauth/token");
		 */

		/*
		ScimClient client = ScimClient.oAuthInstance(email, password, "@!0231.3470.8EF9.420B!0001!819C.E9B6!0008!AA38.26BC",
				 Parms.GLUU_CLIENT_SECRET, "https://idp.galco.com/identity/seam/resource/restv1",
				 "https://idp.galco.com/oxauth/seam/resource/restv1/oxauth/token");
		*/

		ScimClient client = ScimClient.oAuthInstance(email, password, Parms.GLUU_CLIENT,
				Parms.GLUU_CLIENT_SECRET, "https://idmdev1.galco.com/identity/seam/resource/restv1",
				"https://idmdev1.galco.com/oxauth/seam/resource/restv1/oxauth/token");

		// 
		
		return client;
	}

	// -------------------------------------------------------------------------------------------------------------

	public void persist() throws PortalException {
		scimPerson = new ScimPerson();

		List<String> schema = new ArrayList<String>();
		schema.add("urn:scim:schemas:core:1.0");
		scimPerson.setSchemas(schema);

		scimPerson.setUserName(userName);
		scimPerson.setDisplayName(firstName + "-" + lastName);
		scimPerson.setPassword(password);

		scimPerson.getName().setGivenName(firstName);
		scimPerson.getName().setFamilyName(lastName);

		ScimPersonEmails emailSCIM = new ScimPersonEmails();
		emailSCIM.setValue(email);
		emailSCIM.setType("Work");
		emailSCIM.setPrimary("True");
		scimPerson.getEmails().add(emailSCIM);



		ScimPersonPhones phoneSCIM;
		if (!StringUtils.isBlank(phoneWork)) {
			phoneSCIM = new ScimPersonPhones();
			phoneSCIM.setType("Work");
			phoneSCIM.setValue(phoneWork);
			scimPerson.getPhoneNumbers().add(phoneSCIM);
		}

		if (!StringUtils.isBlank(phoneWorkExt)) {
			phoneSCIM = new ScimPersonPhones();
			phoneSCIM.setType("WorkExt");
			phoneSCIM.setValue(phoneWorkExt);
			scimPerson.getPhoneNumbers().add(phoneSCIM);
		}

		if (!StringUtils.isBlank(phoneCompany)) {
			phoneSCIM = new ScimPersonPhones();
			phoneSCIM.setType("Company");
			phoneSCIM.setValue(phoneCompany);
			scimPerson.getPhoneNumbers().add(phoneSCIM);
		}

		if (!StringUtils.isBlank(phoneCompanyExt)) {
			phoneSCIM = new ScimPersonPhones();
			phoneSCIM.setType("CompanyExt");
			phoneSCIM.setValue(phoneCompanyExt);
			scimPerson.getPhoneNumbers().add(phoneSCIM);
		}

		if (!StringUtils.isBlank(phoneCell)) {
			phoneSCIM = new ScimPersonPhones();
			phoneSCIM.setType("Cell");
			phoneSCIM.setValue(phoneCell);
			scimPerson.getPhoneNumbers().add(phoneSCIM);
		}

		if (!StringUtils.isBlank(phoneCellExt)) {
			phoneSCIM = new ScimPersonPhones();
			phoneSCIM.setType("CellExt");
			phoneSCIM.setValue(phoneCellExt);
			scimPerson.getPhoneNumbers().add(phoneSCIM);
		}

		if (!StringUtils.isBlank(fax)) {
			phoneSCIM = new ScimPersonPhones();
			phoneSCIM.setType("Fax");
			phoneSCIM.setValue(fax);
			scimPerson.getPhoneNumbers().add(phoneSCIM);
		}



		ScimPersonAddresses address = new ScimPersonAddresses();
		address.setStreetAddress(streetAddress);
		address.setLocality(locality);
		address.setPostalCode(postalCode);
		address.setRegion(region);
		address.setCountry(country);
		address.setPrimary("true");
		address.setType("Work");
		address.setFormatted(address.getStreetAddress() + " " + address.getLocality() + " " + address.getPostalCode() + " " + address.getRegion() + " " + address.getCountry());
		scimPerson.getAddresses().add(address);

		scimPerson.setPreferredLanguage("US_en");



		/*
		// Old Code
		List<ScimCustomAttributes> scimCustomAttributesList = new ArrayList<ScimCustomAttributes>();
		ScimCustomAttributes scimCustomAttributes = new ScimCustomAttributes();
		scimCustomAttributes.setName("securityType");
		List<String> scimAttributeValuesList = new ArrayList<String>();
		scimAttributeValuesList.add(securityType);
		scimCustomAttributes.setValues(scimAttributeValuesList);
		scimCustomAttributesList.add(scimCustomAttributes);
		scimPerson.setCustomAttributes(scimCustomAttributesList);
		*/
		List<ScimCustomAttributes> scimCustomAttributesList = new ArrayList<ScimCustomAttributes>();

		ScimCustomAttributes scimCustomAttributes = new ScimCustomAttributes();
		scimCustomAttributes.setName("securityType");
		List<String> scimAttributeValuesList = new ArrayList<String>();
		scimAttributeValuesList.add(securityType);
		scimCustomAttributes.setValues(scimAttributeValuesList);
		scimCustomAttributesList.add(scimCustomAttributes);

		scimCustomAttributes = new ScimCustomAttributes();
		scimCustomAttributes.setName("custNum");
		scimAttributeValuesList = new ArrayList<String>();
		scimAttributeValuesList.add(custNum);
		scimCustomAttributes.setValues(scimAttributeValuesList);
		scimCustomAttributesList.add(scimCustomAttributes);

		scimCustomAttributes = new ScimCustomAttributes();
		scimCustomAttributes.setName("contNo");
		scimAttributeValuesList = new ArrayList<String>();
		scimAttributeValuesList.add("" + contNo);
		scimCustomAttributes.setValues(scimAttributeValuesList);
		scimCustomAttributesList.add(scimCustomAttributes);
		
		scimCustomAttributes = new ScimCustomAttributes();
		scimCustomAttributes.setName("dfltAddrBill");
		scimAttributeValuesList = new ArrayList<String>();
		scimAttributeValuesList.add(dfltAddrBill);
		scimCustomAttributes.setValues(scimAttributeValuesList);
		scimCustomAttributesList.add(scimCustomAttributes);

		scimCustomAttributes = new ScimCustomAttributes();
		scimCustomAttributes.setName("dfltAddrShip");
		scimAttributeValuesList = new ArrayList<String>();
		scimAttributeValuesList.add(dfltAddrShip);
		scimCustomAttributes.setValues(scimAttributeValuesList);
		scimCustomAttributesList.add(scimCustomAttributes);

		scimCustomAttributes = new ScimCustomAttributes();
		scimCustomAttributes.setName("accessRole");
		scimAttributeValuesList = new ArrayList<String>();
		scimAttributeValuesList.add(accessRole);
		scimCustomAttributes.setValues(scimAttributeValuesList);
		scimCustomAttributesList.add(scimCustomAttributes);

		scimCustomAttributes = new ScimCustomAttributes();
		scimCustomAttributes.setName("emailConfirmed");
		scimAttributeValuesList = new ArrayList<String>();
		scimAttributeValuesList.add((emailConfirmed)?"Y":"N");
		scimCustomAttributes.setValues(scimAttributeValuesList);
		scimCustomAttributesList.add(scimCustomAttributes);		

		scimCustomAttributes = new ScimCustomAttributes();
		scimCustomAttributes.setName("gluuStatus");
		scimAttributeValuesList = new ArrayList<String>();
		scimAttributeValuesList.add("active");
		scimCustomAttributes.setValues(scimAttributeValuesList);
		scimCustomAttributesList.add(scimCustomAttributes);

		scimPerson.setCustomAttributes(scimCustomAttributesList);

		ScimClient client = createScimClient("admin", ControlServlet.GUPTA_GLUU);
		ScimResponse response;
		try {
			response = client.createPerson(scimPerson, MediaType.APPLICATION_JSON);
		} catch (IOException | JAXBException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}

        String responseBodyJSONString = response.getResponseBodyString();
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(responseBodyJSONString);
		} catch (JSONException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}
		try {
			inum = jsonObject.getString("id");
		} catch (JSONException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}

		// log.debug(response.getStatusCode());
		byte[] bytes = response.getResponseBody();
		String responseStr = new String(bytes);
		// log.debug(responseStr);

		if(StringUtils.isBlank(inum)) {
			throw new PortalException(new GluuCreateFailureException("Failed to create GLUU user, inum came back as blanks."), PortalException.SEVERITY_LEVEL_2);
		}
	}

	// -------------------------------------------------------------------------------------------------------------

	public static GluuUser duplicateGLUUAccount(String oldUserID, String newUserID, String password) {
		GluuUser gluuUser;
		try {
			gluuUser = GluuUser.retrieveUserInfoFromGluu(oldUserID, "", true);
		} catch (PortalException e1) {
			log.debug("Exception while trying to duplicate GLUU accounts.");
			log.debug(e1.getE());
			return null;
		}
		if (gluuUser == null) {
			return null;
		}

		try {
			GluuUser gluuUserNew = new GluuUser		(
												"", 
												newUserID, 
												password,
												gluuUser.getFirstName(), gluuUser.getLastName(),
												newUserID,
												gluuUser.getPhoneWork(), gluuUser.getPhoneWorkExt(),
												gluuUser.getPhoneCompany(), gluuUser.getPhoneCompanyExt(),
												gluuUser.getPhoneCell(), gluuUser.getPhoneCellExt(),
												gluuUser.getFax(),
												gluuUser.getStreetAddress(), gluuUser.getLocality(), gluuUser.getPostalCode(),
												gluuUser.getRegion(), gluuUser.getCountry(),
												gluuUser.getSecurityType(),
												gluuUser.getCustNum(), gluuUser.getContNo(),
												gluuUser.getDfltAddrBill(),
												gluuUser.getDfltAddrShip(),
												gluuUser.getAccessRole(),
												// gluuUser.getEmailConfirmed()				
												false
													);
			/* 
			gluuUserNew.setEmailConfirmed(gluuUser.getEmailConfirmed());
			gluuUserNew.setDfltAddrBill(gluuUser.getDfltAddrBill());
			gluuUserNew.setDfltAddrShip(gluuUser.getDfltAddrShip());
			*/
			gluuUserNew.persist();
			
			return gluuUserNew;
		} catch (PortalException e) {
			log.debug("Exception while trying to duplicate GLUU accounts.");
			log.debug(e.getE());
			return null;
		}
	}
	
	// -------------------------------------------------------------------------------------------------------------
	
	public static void delete(String uid) throws PortalException {
		ScimClient client = createScimClient("admin", ControlServlet.GUPTA_GLUU);
		ScimResponse response;
		try {
			response = client.deletePerson(uid);
		} catch (Exception e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}

		log.debug(response.getStatusCode());
		byte[] bytes = response.getResponseBody();
		String responseStr = new String(bytes);
		log.debug(responseStr);
	}

	// -------------------------------------------------------------------------------------------------------------
	
	public boolean modify(boolean modifyPassword) throws PortalException {
		if (StringUtils.isEmpty(inum)) {
			log.debug("inum can't be empty. To avoid this, you need to get the User from Gluu by " +
							   "calling persist or retrieveUserInfoFromGluu method.");
			return false;
		}

		scimPerson = new ScimPerson();

		List<String> schema = new ArrayList<String>();
		schema.add("urn:scim:schemas:core:1.0");
		scimPerson.setSchemas(schema);

		scimPerson.setUserName(userName);
		scimPerson.setDisplayName(firstName + "-" + lastName);

		if (modifyPassword) {
			scimPerson.setPassword(password);
		}

		scimPerson.getName().setGivenName(firstName);
		scimPerson.getName().setFamilyName(lastName);

		ScimPersonEmails emailSCIM = new ScimPersonEmails();
		emailSCIM.setValue(email);
		emailSCIM.setType("Work");
		emailSCIM.setPrimary("True");
		scimPerson.getEmails().add(emailSCIM);



		ScimPersonPhones phoneSCIM;
		if (!StringUtils.isBlank(phoneWork)) {
			phoneSCIM = new ScimPersonPhones();
			phoneSCIM.setType("Work");
			phoneSCIM.setValue(phoneWork);
			scimPerson.getPhoneNumbers().add(phoneSCIM);
		}

		if (!StringUtils.isBlank(phoneWorkExt)) {
			phoneSCIM = new ScimPersonPhones();
			phoneSCIM.setType("WorkExt");
			phoneSCIM.setValue(phoneWorkExt);
			scimPerson.getPhoneNumbers().add(phoneSCIM);
		}

		if (!StringUtils.isBlank(phoneCompany)) {
			phoneSCIM = new ScimPersonPhones();
			phoneSCIM.setType("Company");
			phoneSCIM.setValue(phoneCompany);
			scimPerson.getPhoneNumbers().add(phoneSCIM);
		}

		if (!StringUtils.isBlank(phoneCompanyExt)) {
			phoneSCIM = new ScimPersonPhones();
			phoneSCIM.setType("CompanyExt");
			phoneSCIM.setValue(phoneCompanyExt);
			scimPerson.getPhoneNumbers().add(phoneSCIM);
		}

		if (!StringUtils.isBlank(phoneCell)) {
			phoneSCIM = new ScimPersonPhones();
			phoneSCIM.setType("Cell");
			phoneSCIM.setValue(phoneCell);
			scimPerson.getPhoneNumbers().add(phoneSCIM);
		}

		if (!StringUtils.isBlank(phoneCellExt)) {
			phoneSCIM = new ScimPersonPhones();
			phoneSCIM.setType("CellExt");
			phoneSCIM.setValue(phoneCellExt);
			scimPerson.getPhoneNumbers().add(phoneSCIM);
		}

		if (!StringUtils.isBlank(fax)) {
			phoneSCIM = new ScimPersonPhones();
			phoneSCIM.setType("Fax");
			phoneSCIM.setValue(fax);
			scimPerson.getPhoneNumbers().add(phoneSCIM);
		}



		ScimPersonAddresses address = new ScimPersonAddresses();
		address.setStreetAddress(streetAddress);
		address.setLocality(locality);
		address.setPostalCode(postalCode);
		address.setRegion(region);
		address.setCountry(country);
		address.setPrimary("true");
		address.setType("Work");
		address.setFormatted(address.getStreetAddress() + " " + address.getLocality() + " " + address.getPostalCode() + " " + address.getRegion() + " " + address.getCountry());
		scimPerson.getAddresses().add(address);

		scimPerson.setPreferredLanguage("US_en");

		List<ScimCustomAttributes> scimCustomAttributesList = new ArrayList<ScimCustomAttributes>();

		ScimCustomAttributes scimCustomAttributes = new ScimCustomAttributes();
		scimCustomAttributes.setName("securityType");
		List<String> scimAttributeValuesList = new ArrayList<String>();
		scimAttributeValuesList.add(securityType);
		scimCustomAttributes.setValues(scimAttributeValuesList);
		scimCustomAttributesList.add(scimCustomAttributes);

		scimCustomAttributes = new ScimCustomAttributes();
		scimCustomAttributes.setName("custNum");
		scimAttributeValuesList = new ArrayList<String>();
		scimAttributeValuesList.add(custNum);
		scimCustomAttributes.setValues(scimAttributeValuesList);
		scimCustomAttributesList.add(scimCustomAttributes);

		scimCustomAttributes = new ScimCustomAttributes();
		scimCustomAttributes.setName("contNo");
		scimAttributeValuesList = new ArrayList<String>();
		scimAttributeValuesList.add("" + contNo);
		scimCustomAttributes.setValues(scimAttributeValuesList);
		scimCustomAttributesList.add(scimCustomAttributes);
		
		scimCustomAttributes = new ScimCustomAttributes();
		scimCustomAttributes.setName("dfltAddrBill");
		scimAttributeValuesList = new ArrayList<String>();
		scimAttributeValuesList.add(dfltAddrBill);
		scimCustomAttributes.setValues(scimAttributeValuesList);
		scimCustomAttributesList.add(scimCustomAttributes);

		scimCustomAttributes = new ScimCustomAttributes();
		scimCustomAttributes.setName("dfltAddrShip");
		scimAttributeValuesList = new ArrayList<String>();
		scimAttributeValuesList.add(dfltAddrShip);
		scimCustomAttributes.setValues(scimAttributeValuesList);
		scimCustomAttributesList.add(scimCustomAttributes);

		scimCustomAttributes = new ScimCustomAttributes();
		scimCustomAttributes.setName("accessRole");
		scimAttributeValuesList = new ArrayList<String>();
		scimAttributeValuesList.add(accessRole);
		scimCustomAttributes.setValues(scimAttributeValuesList);
		scimCustomAttributesList.add(scimCustomAttributes);

		scimCustomAttributes = new ScimCustomAttributes();
		scimCustomAttributes.setName("emailConfirmed");
		scimAttributeValuesList = new ArrayList<String>();
		scimAttributeValuesList.add((emailConfirmed)?"Y":"N");
		scimCustomAttributes.setValues(scimAttributeValuesList);
		scimCustomAttributesList.add(scimCustomAttributes);		

		scimPerson.setCustomAttributes(scimCustomAttributesList);

		ScimClient client = createScimClient("admin", ControlServlet.GUPTA_GLUU);
		ScimResponse response;
		try {
			response = client.updatePerson(scimPerson, inum, MediaType.APPLICATION_JSON);
		} catch (IOException | JAXBException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}

		// log.debug(response.getStatusCode());
		byte[] bytes = response.getResponseBody();
		String responseStr = new String(bytes);
		// log.debug(responseStr);

		return true;
	}

	// -------------------------------------------------------------------------------------------------------------

	public void modifyCustomAttribute(String attribute, String attributeValue) throws PortalException {
		ScimPerson scimPerson = new ScimPerson();
		
		List<ScimCustomAttributes> scimCustomAttributesList = new ArrayList<ScimCustomAttributes>();

		ScimCustomAttributes scimCustomAttributes = new ScimCustomAttributes();
		scimCustomAttributes.setName(attribute);
		List<String> scimAttributeValuesList = new ArrayList<String>();
		scimAttributeValuesList.add(attributeValue);
		scimCustomAttributes.setValues(scimAttributeValuesList);
		scimCustomAttributesList.add(scimCustomAttributes);		

		scimPerson.setCustomAttributes(scimCustomAttributesList);

		ScimClient client = createScimClient("admin", ControlServlet.GUPTA_GLUU);
		ScimResponse response;
		try {
			response = client.updatePerson(scimPerson, getInum(), MediaType.APPLICATION_JSON);
		} catch (IOException | JAXBException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}

		// log.debug(response.getStatusCode());
		byte[] bytes = response.getResponseBody();
		String responseStr = new String(bytes);
		// log.debug(responseStr);
	}

	// -------------------------------------------------------------------------------------------------------------

	public static void modifyPassword(String userName, String newPassword) throws PortalException {
		GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(userName, null, true);
		if (gluuUser == null) {
			throw new PortalException(new Exception("User is not there in GLUU, UserName: " + userName), PortalException.SEVERITY_LEVEL_2);
		}

		ScimPerson scimPerson = new ScimPerson();

		scimPerson.setPassword(newPassword);

		ScimClient client = createScimClient("admin", ControlServlet.GUPTA_GLUU);
		ScimResponse response;
		try {
			response = client.updatePerson(scimPerson, gluuUser.getInum(), MediaType.APPLICATION_JSON);
		} catch (IOException | JAXBException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}

		// log.debug(response.getStatusCode());
		byte[] bytes = response.getResponseBody();
		String responseStr = new String(bytes);
		// log.debug(responseStr);
	}

	// -------------------------------------------------------------------------------------------------------------

    public static String[] getUserIDsWithAGivenEmail_UseContactAndSpolicy(Connection wdsConnection, String email) throws SQLException {
    	if ((email == null) || (email.trim().compareTo("") == 0)) {
    		return null;
    	}
    	if (Utils.isEmailAddressValid(email) == false) {
    		return null;
    	}
    	
    	email = email.trim().toLowerCase();
    	
        ArrayList<String> userIDsAL = new ArrayList<String>();

        ArrayList<HashMap<String, Object>> spolicyAL = null;
        spolicyAL = JDBCUtils.runQuery(wdsConnection, "select user_name from pub.spolicy where lower(user_name) = '" + email + "'  WITH (NOLOCK)");
        if ((spolicyAL != null) && (spolicyAL.size() == 1)) {
			for (Iterator<HashMap<String, Object>> iterator = spolicyAL.iterator(); iterator.hasNext();) {
				HashMap<String, Object> po_Line_HM = (HashMap<String, Object>) iterator.next();

				String user_name = (String) po_Line_HM.get("user_name");
				userIDsAL.add(user_name);
			}
			
            return userIDsAL.toArray(new String[0]);			
        }
        
        spolicyAL = JDBCUtils.runQuery(wdsConnection, 
						        	   "select user_name " 													+
						        	   "  from pub.contact as c, pub.spolicy as s " 						+
						        	   " where c.e_mail_address = '" + email + "' " 						+
						        	   "   and c.pros_cd = s.cust_num " 									+
						               "   and c.cont_no = s.cont_no " 										+
						               "WITH (NOLOCK)"
        							  );
        if ((spolicyAL != null) && (spolicyAL.size() == 1)) {
			for (Iterator<HashMap<String, Object>> iterator = spolicyAL.iterator(); iterator.hasNext();) {
				HashMap<String, Object> po_Line_HM = (HashMap<String, Object>) iterator.next();

				String user_name = (String) po_Line_HM.get("user_name");
				userIDsAL.add(user_name.toLowerCase());
			}
			
            return userIDsAL.toArray(new String[0]);			
        } else {
            if ((spolicyAL != null) && (spolicyAL.size() > 1)) {
	        	log.debug("Multiple spolicy records matched for password reset request - ");
	        	
	        	for (Iterator<HashMap<String, Object>> iterator = spolicyAL.iterator(); iterator.hasNext();) {
					HashMap<String, Object> po_Line_HM = (HashMap<String, Object>) iterator.next();
	
					String user_name = (String) po_Line_HM.get("user_name");
					log.debug(user_name);
				}
            }

        	return null;
        }
    }
    
    // =================================================================================================================

    public static String[] getUserIDsWithAGivenEmail_LDAP(DBConnector dbConnector, String email) {
        try {
            if (EmailValidator.getInstance().isValid(email) == false) {
                return null;
            }           
            
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
            
            
            
            
            env.put(Context.PROVIDER_URL, "ldap://" + Parms.GLUU_SERVER_NAME + ":1389");
            // env.put(Context.PROVIDER_URL, "ldap://" + Parms.GLUU_SERVER_NAME + ":1389");
            // env.put(Context.PROVIDER_URL, "ldap://172.16.0.31:1389");
            
            
            
            
            env.put(Context.SECURITY_AUTHENTICATION,"simple");
            env.put(Context.SECURITY_PRINCIPAL,"cn=Directory Manager"); // specify the username
            env.put(Context.SECURITY_CREDENTIALS,ControlServlet.GUPTA_GLUU);           // specify the password
            // env.put("java.naming.batchsize", "10");
            DirContext ctx = new InitialDirContext(env);
    
            /*
            Attributes matchAttrs = new BasicAttributes(true);
            matchAttrs.put(new BasicAttribute("mail", "sati@galco.com"));
            NamingEnumeration answer = ctx.search("ou=People,o=@!C032.849B.2FA5.5E8C!0001!BCB6.4A42,o=gluu",matchAttrs);
            */
    
            /*
            SearchControls ctrl = new SearchControls();
            ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration answer = ctx.search("ou=People,o=@!C032.849B.2FA5.5E8C!0001!BCB6.4A42,o=gluu", "mail=sati@galco.com", ctrl);
            */
    
            /*
            SearchControls sc = new SearchControls();
            String[] attributeFilter = { "cn", "uid", "mail" };
            sc.setReturningAttributes(attributeFilter);
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String filter = "mail=sati@galco.com";
            NamingEnumeration answer = ctx.search("ou=People,o=@!C032.849B.2FA5.5E8C!0001!BCB6.4A42,o=gluu", filter, sc);
            */
    
            SearchControls sc = new SearchControls();
            // sc.setCountLimit(10);
            // String[] attributeFilter = { "cn", "uid", "mail" };
            String[] attributeFilter = { "cn", "uid", "mail", "oxTrustEmail"};
            sc.setReturningAttributes(attributeFilter);
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String filter = "oxTrustEmail=*" + email + "*";
            NamingEnumeration answer = ctx.search("o=gluu", filter, sc);
    
            // qqq
         // log.debug("LDAP Search results");            
            ArrayList<String> userIDsAL = new ArrayList<String>();
            while (answer.hasMoreElements()) {
                SearchResult searchResult = (SearchResult) answer.nextElement();
                // System.out.println(searchResult);
                String userID = (String) searchResult.getAttributes().get("uid").get();
                // log.debug(userID);

                // userIDsAL.add(userID);
                String oxTrustEmail = (String) (searchResult.getAttributes().get("oxTrustEmail")).get();
                JSONArray jsonArray;
                try {
                    jsonArray = new JSONArray(oxTrustEmail);
                    JSONObject firstEmailJO = (JSONObject) jsonArray.get(0);
                    String returnedEmail = (String) firstEmailJO.get("value");
                    // System.out.println(returnedEmail);
                    // log.debug("userID" + userID + ", returnedEmail: " + returnedEmail);
                    if (email.compareToIgnoreCase(returnedEmail) == 0) {
                        userIDsAL.add(userID);
                    } else {
                        return null;                        
                    }
                } catch (JSONException e) {
                    // log.debug("oxTrustEmail GLUU response is not a valid JSON Object.", e);
                    log.debug("oxTrustEmail GLUU response is not a valid JSON Object.");
                    return null;
                }
            }
            
            if (userIDsAL.size() == 0) {
                return null;
            } else {
                // String[] userIDsArr = new String[userIDsAL.size()];
                return userIDsAL.toArray(new String[0]);
            }
    
            
            /*
            SearchResult searchResult = null;
            if(answer.hasMoreElements()) {
                 searchResult = (SearchResult) answer.nextElement();
                 // log.debug(searchResult);
                 System.out.println(searchResult.getAttributes().get("uid").get());
            }
            */
    
          
            /*
    
            // final String ldapSearchBase = "dc=ad,dc=my-domain,dc=com";
            // final String ldapSearchBase = "dc=idm,dc=galco,dc=com";
            // final String ldapSearchBase = "uid=sati@galco.com,ou=people,o=idm.galco.com";
    
            // GOOD GOOD GOOD final String ldapSearchBase = "inum=@!C032.849B.2FA5.5E8C!0001!BCB6.4A42!0000!89C2.6CCE,ou=people,o=@!C032.849B.2FA5.5E8C!0001!BCB6.4A42,o=gluu";
    
            
            final String ldapSearchBase = "ou=people,o=@!C032.849B.2FA5.5E8C!0001!BCB6.4A42,o=gluu";
    
            final String ldapAccountToLookup = "sati@galco.com";
    
    
            LDAPTest ldap = new LDAPTest();
    
            //1) lookup the ldap account
            SearchResult srLdapUser = ldap.findAccountByAccountName(ctx, ldapSearchBase, ldapAccountToLookup);
    
            // 2) get the SID of the users primary group
            // String primaryGroupSID = ldap.getPrimaryGroupSID(srLdapUser);
    
            // 3) get the users Primary Group
            // String primaryGroupName = ldap.findGroupBySID(ctx, ldapSearchBase, primaryGroupSID);
    
            */
    
            
            /*
            Hashtable env = new Hashtable();
        
            String sp = "com.sun.jndi.ldap.LdapCtxFactory";
            env.put(Context.INITIAL_CONTEXT_FACTORY, sp);
        
            String ldapUrl = "ldap://idm.galco.com:1389/dc=root, dc=com";
            env.put(Context.PROVIDER_URL, ldapUrl);
        
            DirContext dctx = new InitialDirContext(env);
        
            String base = "ou=People";
        
            SearchControls sc = new SearchControls();
            String[] attributeFilter = { "cn", "mail" };
            sc.setReturningAttributes(attributeFilter);
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        
            String filter = "(&(sn=W*)(l=Criteria*))";
        
            NamingEnumeration results = dctx.search(base, filter, sc);
            while (results.hasMore()) {
                SearchResult sr = (SearchResult) results.next();
                Attributes attrs = sr.getAttributes();
        
                Attribute attr = attrs.get("cn");
                System.out.print(attr.get() + ": ");
                attr = attrs.get("mail");
                log.debug(attr.get());
            }
            dctx.close();
        
            System.exit(0);
    
            */
            
            
            /*
    
            // final String ldapAdServer = "idm.galco.com:1389";
            final String ldapAdServer = "ldap://idm.galco.com:1389";
    
            final String ldapSearchBase = "dc=ad,dc=my-domain,dc=com";
    
            final String ldapUsername = "root";
            final String ldapPassword = "WaOne12";
    
            final String ldapAccountToLookup = "sati@galco.com";
    
    
            Hashtable<String, Object> env = new Hashtable<String, Object>();
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            if(ldapUsername != null) {
                // env.put(Context.SECURITY_PRINCIPAL, ldapUsername);
                env.put(Context.SECURITY_PRINCIPAL, "cn=root,UserSearch");
            }
            if(ldapPassword != null) {
                env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
            }
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, ldapAdServer);
    
            //ensures that objectSID attribute values
            //will be returned as a byte[] instead of a String
            env.put("java.naming.ldap.attributes.binary", "objectSID");
    
            // the following is helpful in debugging errors
            //env.put("com.sun.jndi.ldap.trace.ber", System.err);
    
            // LdapContext ctx = new InitialLdapContext();
            LdapContext ctx = new InitialLdapContext(env, null);
    
            LDAPTest ldap = new LDAPTest();
    
            //1) lookup the ldap account
            SearchResult srLdapUser = ldap.findAccountByAccountName(ctx, ldapSearchBase, ldapAccountToLookup);
    
            // 2) get the SID of the users primary group
            // String primaryGroupSID = ldap.getPrimaryGroupSID(srLdapUser);
    
            // 3) get the users Primary Group
            // String primaryGroupName = ldap.findGroupBySID(ctx, ldapSearchBase, primaryGroupSID);
    
            */
            
        } catch (Exception e) {
            log.error("Exception in LDAP Query", e);
            return null;
        }
    }   

    public static String[] getUserIDsWithAGivenEmail_LDAP_DEV(DBConnector dbConnector, String email) {
        try {
            if (EmailValidator.getInstance().isValid(email) == false) {
                return null;
            }           
            
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, "ldap://idp.galco.com:1389");
            env.put(Context.SECURITY_AUTHENTICATION,"simple");
            env.put(Context.SECURITY_PRINCIPAL,"cn=Directory Manager"); // specify the username
            env.put(Context.SECURITY_CREDENTIALS,"LnzzI7QWxV2G");           // specify the password
            // env.put("java.naming.batchsize", "10");
            DirContext ctx = new InitialDirContext(env);
    
            /*
            Attributes matchAttrs = new BasicAttributes(true);
            matchAttrs.put(new BasicAttribute("mail", "sati@galco.com"));
            NamingEnumeration answer = ctx.search("ou=People,o=@!C032.849B.2FA5.5E8C!0001!BCB6.4A42,o=gluu",matchAttrs);
            */
    
            /*
            SearchControls ctrl = new SearchControls();
            ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration answer = ctx.search("ou=People,o=@!C032.849B.2FA5.5E8C!0001!BCB6.4A42,o=gluu", "mail=sati@galco.com", ctrl);
            */
    
            /*
            SearchControls sc = new SearchControls();
            String[] attributeFilter = { "cn", "uid", "mail" };
            sc.setReturningAttributes(attributeFilter);
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String filter = "mail=sati@galco.com";
            NamingEnumeration answer = ctx.search("ou=People,o=@!C032.849B.2FA5.5E8C!0001!BCB6.4A42,o=gluu", filter, sc);
            */
    
            SearchControls sc = new SearchControls();
            // sc.setCountLimit(10);
            // String[] attributeFilter = { "cn", "uid", "mail" };
            String[] attributeFilter = { "cn", "uid", "mail", "oxTrustEmail"};
            sc.setReturningAttributes(attributeFilter);
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String filter = "oxTrustEmail=*" + email + "*";
            NamingEnumeration answer = ctx.search("o=gluu", filter, sc);
    
            // qqq
            ArrayList<String> userIDsAL = new ArrayList<String>();
            while (answer.hasMoreElements()) {
                SearchResult searchResult = (SearchResult) answer.nextElement();
                // System.out.println(searchResult);
                String userID = (String) searchResult.getAttributes().get("uid").get();
                // log.debug(userID);

                // userIDsAL.add(userID);
                String oxTrustEmail = (String) (searchResult.getAttributes().get("oxTrustEmail")).get();
                JSONArray jsonArray;
                try {
                    jsonArray = new JSONArray(oxTrustEmail);
                    JSONObject firstEmailJO = (JSONObject) jsonArray.get(0);
                    String returnedEmail = (String) firstEmailJO.get("value");
                    // System.out.println(returnedEmail);
                    if (email.compareToIgnoreCase(returnedEmail) == 0) {
                        userIDsAL.add(userID);
                    } else {
                        return null;                        
                    }
                } catch (JSONException e) {
                    // log.debug("oxTrustEmail GLUU response is not a valid JSON Object.", e);
                    log.debug("oxTrustEmail GLUU response is not a valid JSON Object.");
                    return null;
                }
            }
            
            if (userIDsAL.size() == 0) {
                return null;
            } else {
                // String[] userIDsArr = new String[userIDsAL.size()];
                return userIDsAL.toArray(new String[0]);
            }
    
            
            /*
            SearchResult searchResult = null;
            if(answer.hasMoreElements()) {
                 searchResult = (SearchResult) answer.nextElement();
                 // log.debug(searchResult);
                 System.out.println(searchResult.getAttributes().get("uid").get());
            }
            */
    
          
            /*
    
            // final String ldapSearchBase = "dc=ad,dc=my-domain,dc=com";
            // final String ldapSearchBase = "dc=idm,dc=galco,dc=com";
            // final String ldapSearchBase = "uid=sati@galco.com,ou=people,o=idm.galco.com";
    
            // GOOD GOOD GOOD final String ldapSearchBase = "inum=@!C032.849B.2FA5.5E8C!0001!BCB6.4A42!0000!89C2.6CCE,ou=people,o=@!C032.849B.2FA5.5E8C!0001!BCB6.4A42,o=gluu";
    
            
            final String ldapSearchBase = "ou=people,o=@!C032.849B.2FA5.5E8C!0001!BCB6.4A42,o=gluu";
    
            final String ldapAccountToLookup = "sati@galco.com";
    
    
            LDAPTest ldap = new LDAPTest();
    
            //1) lookup the ldap account
            SearchResult srLdapUser = ldap.findAccountByAccountName(ctx, ldapSearchBase, ldapAccountToLookup);
    
            // 2) get the SID of the users primary group
            // String primaryGroupSID = ldap.getPrimaryGroupSID(srLdapUser);
    
            // 3) get the users Primary Group
            // String primaryGroupName = ldap.findGroupBySID(ctx, ldapSearchBase, primaryGroupSID);
    
            */
    
            
            /*
            Hashtable env = new Hashtable();
        
            String sp = "com.sun.jndi.ldap.LdapCtxFactory";
            env.put(Context.INITIAL_CONTEXT_FACTORY, sp);
        
            String ldapUrl = "ldap://idm.galco.com:1389/dc=root, dc=com";
            env.put(Context.PROVIDER_URL, ldapUrl);
        
            DirContext dctx = new InitialDirContext(env);
        
            String base = "ou=People";
        
            SearchControls sc = new SearchControls();
            String[] attributeFilter = { "cn", "mail" };
            sc.setReturningAttributes(attributeFilter);
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        
            String filter = "(&(sn=W*)(l=Criteria*))";
        
            NamingEnumeration results = dctx.search(base, filter, sc);
            while (results.hasMore()) {
                SearchResult sr = (SearchResult) results.next();
                Attributes attrs = sr.getAttributes();
        
                Attribute attr = attrs.get("cn");
                System.out.print(attr.get() + ": ");
                attr = attrs.get("mail");
                log.debug(attr.get());
            }
            dctx.close();
        
            System.exit(0);
    
            */
            
            
            /*
    
            // final String ldapAdServer = "idm.galco.com:1389";
            final String ldapAdServer = "ldap://idm.galco.com:1389";
    
            final String ldapSearchBase = "dc=ad,dc=my-domain,dc=com";
    
            final String ldapUsername = "root";
            final String ldapPassword = "WaOne12";
    
            final String ldapAccountToLookup = "sati@galco.com";
    
    
            Hashtable<String, Object> env = new Hashtable<String, Object>();
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            if(ldapUsername != null) {
                // env.put(Context.SECURITY_PRINCIPAL, ldapUsername);
                env.put(Context.SECURITY_PRINCIPAL, "cn=root,UserSearch");
            }
            if(ldapPassword != null) {
                env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
            }
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, ldapAdServer);
    
            //ensures that objectSID attribute values
            //will be returned as a byte[] instead of a String
            env.put("java.naming.ldap.attributes.binary", "objectSID");
    
            // the following is helpful in debugging errors
            //env.put("com.sun.jndi.ldap.trace.ber", System.err);
    
            // LdapContext ctx = new InitialLdapContext();
            LdapContext ctx = new InitialLdapContext(env, null);
    
            LDAPTest ldap = new LDAPTest();
    
            //1) lookup the ldap account
            SearchResult srLdapUser = ldap.findAccountByAccountName(ctx, ldapSearchBase, ldapAccountToLookup);
    
            // 2) get the SID of the users primary group
            // String primaryGroupSID = ldap.getPrimaryGroupSID(srLdapUser);
    
            // 3) get the users Primary Group
            // String primaryGroupName = ldap.findGroupBySID(ctx, ldapSearchBase, primaryGroupSID);
    
            */
            
        } catch (Exception e) {
            log.error("Exception in LDAP Query", e);
            return null;
        }
    }   

    static final String oxTrustEmailSearchString = "oxTrustEmail: [{\"value\":\"";
    public static String[] getUserIDsWithAGivenEmail_LDAP_Parse(DBConnector dbConnector, String email) {
    	try {
    		if (EmailValidator.getInstance().isValid(email) == false) {
    			return null;
    		}
    		
	        Hashtable env = new Hashtable();
	        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
	        env.put(Context.PROVIDER_URL, "ldap://" + Parms.GLUU_SERVER_NAME + ":1389");
	        env.put(Context.SECURITY_AUTHENTICATION,"simple");
	        env.put(Context.SECURITY_PRINCIPAL,"cn=Directory Manager"); // specify the username
	        env.put(Context.SECURITY_CREDENTIALS,ControlServlet.GUPTA_GLUU);           // specify the password
	        // env.put("java.naming.batchsize", "10");
	        DirContext ctx = new InitialDirContext(env);
	
	        /*
	        Attributes matchAttrs = new BasicAttributes(true);
	        matchAttrs.put(new BasicAttribute("mail", "sati@galco.com"));
	        NamingEnumeration answer = ctx.search("ou=People,o=@!C032.849B.2FA5.5E8C!0001!BCB6.4A42,o=gluu",matchAttrs);
	        */
	
	        /*
	        SearchControls ctrl = new SearchControls();
	        ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
	        NamingEnumeration answer = ctx.search("ou=People,o=@!C032.849B.2FA5.5E8C!0001!BCB6.4A42,o=gluu", "mail=sati@galco.com", ctrl);
	        */
	
	        /*
	        SearchControls sc = new SearchControls();
	        String[] attributeFilter = { "cn", "uid", "mail" };
	        sc.setReturningAttributes(attributeFilter);
	        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
	        String filter = "mail=sati@galco.com";
	        NamingEnumeration answer = ctx.search("ou=People,o=@!C032.849B.2FA5.5E8C!0001!BCB6.4A42,o=gluu", filter, sc);
	        */
	
	        SearchControls sc = new SearchControls();
	        // sc.setCountLimit(10);
	        // String[] attributeFilter = { "cn", "uid", "mail" };
	        //String[] attributeFilter = { "cn", "uid", "mail"};
	        //sc.setReturningAttributes(attributeFilter);
	        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
	        String filter = "oxTrustEmail=*" + email + "*";
	        NamingEnumeration answer = ctx.search("o=gluu", filter, sc);
	
	        // qqq
	        ArrayList<String> userIDsAL = new ArrayList<String>();
	        while (answer.hasMoreElements()) {
	        	SearchResult searchResult = (SearchResult) answer.nextElement();
	        	
	        	if (searchResult != null) {
		            // log.debug(searchResult);

		            String searchResultStr = searchResult.toString();
	        		
		            int index = searchResultStr.indexOf(oxTrustEmailSearchString);
	        		if (index >= 0) {
	        			int index2 = searchResultStr.indexOf("\"", index + oxTrustEmailSearchString.length());
	        			String foundEmail = searchResultStr.substring(index + oxTrustEmailSearchString.length(), index2);
	        			// log.debug(foundEmail);
	        			if (email.compareToIgnoreCase(foundEmail) != 0) {
	        				return null;
	        			}

	        			String userID = (String) searchResult.getAttributes().get("uid").get();
	    	        	userIDsAL.add(userID);
	        		} else {
		        		return null;
		        	}
	        	} else {
	        		return null;
	        	}
	        }
	        
	        if (userIDsAL.size() == 0) {
	        	return null;
	        } else {
	        	// String[] userIDsArr = new String[userIDsAL.size()];
	        	return userIDsAL.toArray(new String[0]);
	        }
	
	        
			/*
	        SearchResult searchResult = null;
	        if(answer.hasMoreElements()) {
	             searchResult = (SearchResult) answer.nextElement();
	             // log.debug(searchResult);
	             System.out.println(searchResult.getAttributes().get("uid").get());
	        }
	        */
	
	      
	        /*
	
	        // final String ldapSearchBase = "dc=ad,dc=my-domain,dc=com";
	        // final String ldapSearchBase = "dc=idm,dc=galco,dc=com";
	        // final String ldapSearchBase = "uid=sati@galco.com,ou=people,o=idm.galco.com";
	
	    	// GOOD GOOD GOOD final String ldapSearchBase = "inum=@!C032.849B.2FA5.5E8C!0001!BCB6.4A42!0000!89C2.6CCE,ou=people,o=@!C032.849B.2FA5.5E8C!0001!BCB6.4A42,o=gluu";
	
	        
	        final String ldapSearchBase = "ou=people,o=@!C032.849B.2FA5.5E8C!0001!BCB6.4A42,o=gluu";
	
	        final String ldapAccountToLookup = "sati@galco.com";
	
	
	        LDAPTest ldap = new LDAPTest();
	
	        //1) lookup the ldap account
	        SearchResult srLdapUser = ldap.findAccountByAccountName(ctx, ldapSearchBase, ldapAccountToLookup);
	
	        // 2) get the SID of the users primary group
	        // String primaryGroupSID = ldap.getPrimaryGroupSID(srLdapUser);
	
	        // 3) get the users Primary Group
	        // String primaryGroupName = ldap.findGroupBySID(ctx, ldapSearchBase, primaryGroupSID);
	
	        */
	
	        
	    	/*
			Hashtable env = new Hashtable();
		
		    String sp = "com.sun.jndi.ldap.LdapCtxFactory";
		    env.put(Context.INITIAL_CONTEXT_FACTORY, sp);
		
		    String ldapUrl = "ldap://idm.galco.com:1389/dc=root, dc=com";
		    env.put(Context.PROVIDER_URL, ldapUrl);
		
		    DirContext dctx = new InitialDirContext(env);
		
		    String base = "ou=People";
		
		    SearchControls sc = new SearchControls();
		    String[] attributeFilter = { "cn", "mail" };
		    sc.setReturningAttributes(attributeFilter);
		    sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
		
		    String filter = "(&(sn=W*)(l=Criteria*))";
		
		    NamingEnumeration results = dctx.search(base, filter, sc);
		    while (results.hasMore()) {
		    	SearchResult sr = (SearchResult) results.next();
		      	Attributes attrs = sr.getAttributes();
		
		      	Attribute attr = attrs.get("cn");
		      	System.out.print(attr.get() + ": ");
		      	attr = attrs.get("mail");
		      	log.debug(attr.get());
		    }
		    dctx.close();
		
			System.exit(0);
	
	    	*/
	        
	        
	        /*
	
	        // final String ldapAdServer = "idm.galco.com:1389";
	        final String ldapAdServer = "ldap://idm.galco.com:1389";
	
	        final String ldapSearchBase = "dc=ad,dc=my-domain,dc=com";
	
	        final String ldapUsername = "root";
	        final String ldapPassword = "WaOne12";
	
	        final String ldapAccountToLookup = "sati@galco.com";
	
	
	        Hashtable<String, Object> env = new Hashtable<String, Object>();
	        env.put(Context.SECURITY_AUTHENTICATION, "simple");
	        if(ldapUsername != null) {
	            // env.put(Context.SECURITY_PRINCIPAL, ldapUsername);
	            env.put(Context.SECURITY_PRINCIPAL, "cn=root,UserSearch");
	        }
	        if(ldapPassword != null) {
	            env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
	        }
	        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	        env.put(Context.PROVIDER_URL, ldapAdServer);
	
	        //ensures that objectSID attribute values
	        //will be returned as a byte[] instead of a String
	        env.put("java.naming.ldap.attributes.binary", "objectSID");
	
	        // the following is helpful in debugging errors
	        //env.put("com.sun.jndi.ldap.trace.ber", System.err);
	
	        // LdapContext ctx = new InitialLdapContext();
	        LdapContext ctx = new InitialLdapContext(env, null);
	
	        LDAPTest ldap = new LDAPTest();
	
	        //1) lookup the ldap account
	        SearchResult srLdapUser = ldap.findAccountByAccountName(ctx, ldapSearchBase, ldapAccountToLookup);
	
	        // 2) get the SID of the users primary group
	        // String primaryGroupSID = ldap.getPrimaryGroupSID(srLdapUser);
	
	        // 3) get the users Primary Group
	        // String primaryGroupName = ldap.findGroupBySID(ctx, ldapSearchBase, primaryGroupSID);
	
	        */
			
		} catch (Exception e) {
			log.error("Exception in LDAP Query", e);
			return null;
		}
    }	
	
	// -------------------------------------------------------------------------------------------------------------

    public static String[] getUsernamesWithAGivenEmail(DBConnector dbConnector, String email) throws PortalException {
		/*
		if (true) {
			return Spolicy.getUserNamesWThatHaveTheGivenEmail(dbConnector, email);
		}
		*/
		
		ScimClient client = createScimClient("admin", ControlServlet.GUPTA_GLUU);
		// ScimClient client = createScimClient("directory manager", ControlServlet.GUPTA_GLUU);

		ScimResponse response;
		try {
			// qqq
			// response = client.personSearch("oxTrustEmail", "*" + email + "*", MediaType.APPLICATION_JSON);
			// response = client.personSearch("custNum", "154154", MediaType.APPLICATION_JSON);
			// String emailVal = "[{\"value\":\"sati20@galco.com\",\"type\":\"Work\",\"primary\":\"True\"}]";
			String emailVal = "sati20@galco.com,Work,True";
			System.out.println(emailVal);
			response = client.personSearch("emails", emailVal, MediaType.APPLICATION_JSON);
		} catch (IOException | JAXBException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}
        log.debug(response.getResponseBodyString());

        String responseBodyJSONString = response.getResponseBodyString();
		// String responseBodyJSONString = "{\"schemas\":[\"urn:scim:schemas:core:1.0\"],\"id\":\"@!C032.849B.2FA5.5E8C!0001!BCB6.4A42!0000!5C41.1FCB\",\"externalId\":\"\",\"userName\":\"sati_0627_1712@galco.com\",\"name\":{\"givenName\":\"Subbarao\",\"familyName\":\"Ati\",\"middleName\":\"\",\"honorificPrefix\":\"\",\"honorificSuffix\":\"\"},\"displayName\":\"Subbarao-Ati\",\"nickName\":\"\",\"profileUrl\":\"\",\"emails\":[{\"value\":\"sati_0627_1712@galco.com\",\"type\":\"Work\",\"primary\":\"True\"}],\"addresses\":[{\"type\":\"Work\",\"streetAddress\":\"123 Wolverine St.\",\"locality\":\"Madison Heights\",\"region\":\"MI\",\"postalCode\":\"54321\",\"country\":\"USA\",\"formatted\":\"123 Wolverine St. Madison Heights 54321 MI USA\",\"primary\":\"true\"}],\"phoneNumbers\":[{\"value\":\"123-456-7890\",\"type\":\"Work\"}],\"ims\":[],\"photos\":[],\"userType\":\"\",\"title\":\"\",\"preferredLanguage\":\"US_en\",\"locale\":\"\",\"timezone\":null,\"active\":null,\"password\":\"Hidden for Privacy Reasons\",\"groups\":[],\"roles\":[],\"entitlements\":[],\"x509Certificates\":[],\"meta\":{\"created\":\"\",\"lastModified\":\"\",\"version\":\"\",\"location\":\"\"},\"customAttributes\":[{\"name\":\"securityType\",\"values\":[\"YYYN\"]}]}";
        // {"errors":[{"description":"No result found for search pattern 'uid = sati_999999999999999999@galco.com' please try again or use another pattern.","code":404,"uri":""}]}
        // log.debug(responseBodyJSONString);
        System.out.println(responseBodyJSONString);

		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(responseBodyJSONString);

			String userNameResp = null;
			try {
				userNameResp = jsonObject.getString("userName");
			} catch (JSONException e) {
				return null;
			}

			if (userNameResp == null) {
				return null;
			}

			String[] userNames = new String[1];
			userNames[0] = userNameResp;

			return userNames;
		} catch (JSONException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}
	}

	// -------------------------------------------------------------------------------------------------------------

	public static boolean userExists(String userName) throws PortalException {
		ScimClient client = createScimClient("admin", ControlServlet.GUPTA_GLUU);

		ScimResponse response;
		try {
			response = client.personSearch("uid", userName, MediaType.APPLICATION_JSON);
		} catch (IOException | JAXBException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}
        // log.debug(response.getResponseBodyString());

        String responseBodyJSONString = response.getResponseBodyString();
		// String responseBodyJSONString = "{\"schemas\":[\"urn:scim:schemas:core:1.0\"],\"id\":\"@!C032.849B.2FA5.5E8C!0001!BCB6.4A42!0000!5C41.1FCB\",\"externalId\":\"\",\"userName\":\"sati_0627_1712@galco.com\",\"name\":{\"givenName\":\"Subbarao\",\"familyName\":\"Ati\",\"middleName\":\"\",\"honorificPrefix\":\"\",\"honorificSuffix\":\"\"},\"displayName\":\"Subbarao-Ati\",\"nickName\":\"\",\"profileUrl\":\"\",\"emails\":[{\"value\":\"sati_0627_1712@galco.com\",\"type\":\"Work\",\"primary\":\"True\"}],\"addresses\":[{\"type\":\"Work\",\"streetAddress\":\"123 Wolverine St.\",\"locality\":\"Madison Heights\",\"region\":\"MI\",\"postalCode\":\"54321\",\"country\":\"USA\",\"formatted\":\"123 Wolverine St. Madison Heights 54321 MI USA\",\"primary\":\"true\"}],\"phoneNumbers\":[{\"value\":\"123-456-7890\",\"type\":\"Work\"}],\"ims\":[],\"photos\":[],\"userType\":\"\",\"title\":\"\",\"preferredLanguage\":\"US_en\",\"locale\":\"\",\"timezone\":null,\"active\":null,\"password\":\"Hidden for Privacy Reasons\",\"groups\":[],\"roles\":[],\"entitlements\":[],\"x509Certificates\":[],\"meta\":{\"created\":\"\",\"lastModified\":\"\",\"version\":\"\",\"location\":\"\"},\"customAttributes\":[{\"name\":\"securityType\",\"values\":[\"YYYN\"]}]}";
        // {"errors":[{"description":"No result found for search pattern 'uid = sati_999999999999999999@galco.com' please try again or use another pattern.","code":404,"uri":""}]}
        // log.debug(responseBodyJSONString);

		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(responseBodyJSONString);
		} catch (JSONException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}
		return !(jsonObject.isNull("userName"));

		/*
		String userNameResp = jsonObject.getString("userName");
		jsonObject.
		if (userNameResp == null) {
			return false;
		}
        return true;
        */
	}

	public static GluuUser retrieveUserInfoFromGluu(String userName, String password, boolean useAdminAccount) throws PortalException {
		try {
			ScimClient client;
			if (useAdminAccount) {
				client = createScimClient("admin", ControlServlet.GUPTA_GLUU);
			} else {
				client = createScimClient(userName, password);
			}

			// ScimResponse response = client.retrievePerson("@!C032.849B.2FA5.5E8C!0001!BCB6.4A42!0000!6386.5FFE", MediaType.APPLICATION_JSON);
			// ScimResponse response = client.retrievePerson("@!C032.849B.2FA5.5E8C!0001!BCB6.4A42!0000!6386.5FFE", MediaType.APPLICATION_JSON);
			ScimResponse response = client.personSearch("uid", userName, MediaType.APPLICATION_JSON);
	        // log.debug(response.getResponseBodyString());

	        String responseBodyJSONString = response.getResponseBodyString();
			// postalCode missing
	        // String responseBodyJSONString = "{\"schemas\":[\"urn:scim:schemas:core:1.0\"],\"id\":\"@!C032.849B.2FA5.5E8C!0001!BCB6.4A42!0000!5C41.1FCB\",\"externalId\":\"\",\"userName\":\"sati_0627_1712@galco.com\",\"name\":{\"givenName\":\"Subbarao\",\"familyName\":\"Ati\",\"middleName\":\"\",\"honorificPrefix\":\"\",\"honorificSuffix\":\"\"},\"displayName\":\"Subbarao-Ati\",\"nickName\":\"\",\"profileUrl\":\"\",\"emails\":[{\"value\":\"sati_0627_1712@galco.com\",\"type\":\"Work\",\"primary\":\"True\"}],\"addresses\":[{\"type\":\"Work\",\"streetAddress\":\"123 Wolverine St.\",\"locality\":\"Madison Heights\",\"region\":\"MI\",\"country\":\"USA\",\"formatted\":\"123 Wolverine St. Madison Heights 54321 MI USA\",\"primary\":\"true\"}],\"phoneNumbers\":[{\"value\":\"123-456-7890\",\"type\":\"Work\"}],\"ims\":[],\"photos\":[],\"userType\":\"\",\"title\":\"\",\"preferredLanguage\":\"US_en\",\"locale\":\"\",\"timezone\":null,\"active\":null,\"password\":\"Hidden for Privacy Reasons\",\"groups\":[],\"roles\":[],\"entitlements\":[],\"x509Certificates\":[],\"meta\":{\"created\":\"\",\"lastModified\":\"\",\"version\":\"\",\"location\":\"\"},\"customAttributes\":[{\"name\":\"securityType\",\"values\":[\"YYYN\"]}]}";
	        // All good
			// String responseBodyJSONString = "{\"schemas\":[\"urn:scim:schemas:core:1.0\"],\"id\":\"@!C032.849B.2FA5.5E8C!0001!BCB6.4A42!0000!5C41.1FCB\",\"externalId\":\"\",\"userName\":\"sati_0627_1712@galco.com\",\"name\":{\"givenName\":\"Subbarao\",\"familyName\":\"Ati\",\"middleName\":\"\",\"honorificPrefix\":\"\",\"honorificSuffix\":\"\"},\"displayName\":\"Subbarao-Ati\",\"nickName\":\"\",\"profileUrl\":\"\",\"emails\":[{\"value\":\"sati_0627_1712@galco.com\",\"type\":\"Work\",\"primary\":\"True\"}],\"addresses\":[{\"type\":\"Work\",\"streetAddress\":\"123 Wolverine St.\",\"locality\":\"Madison Heights\",\"region\":\"MI\",\"postalCode\":\"54321\",\"country\":\"USA\",\"formatted\":\"123 Wolverine St. Madison Heights 54321 MI USA\",\"primary\":\"true\"}],\"phoneNumbers\":[{\"value\":\"123-456-7890\",\"type\":\"Work\"}],\"ims\":[],\"photos\":[],\"userType\":\"\",\"title\":\"\",\"preferredLanguage\":\"US_en\",\"locale\":\"\",\"timezone\":null,\"active\":null,\"password\":\"Hidden for Privacy Reasons\",\"groups\":[],\"roles\":[],\"entitlements\":[],\"x509Certificates\":[],\"meta\":{\"created\":\"\",\"lastModified\":\"\",\"version\":\"\",\"location\":\"\"},\"customAttributes\":[{\"name\":\"securityType\",\"values\":[\"YYYN\"]}]}";

	        JSONObject jsonObject;
	        try {
				jsonObject = new JSONObject(responseBodyJSONString);
			} catch (JSONException e) {
				// log.debug("GLUU response is not a valid JSON Object.", e);
				log.debug("GLUU response is not a valid JSON Object.");
				return null;
			}

	        String inum = null;
	        try {
	        	inum = jsonObject.getString("id");
			} catch (JSONException e) {
				// log.debug("JSON Object has id tag missing.", e);
				log.debug("JSON Object has id tag missing.");
				return null;
			}
		
			// log.debug("inum from Response:" + inum);

			String userNameResp = jsonObject.getString("userName");
			// log.debug("userName from Response:" + userNameResp);

			String firstName;
			String lastName;
			{
				JSONObject nameJO = jsonObject.getJSONObject("name");
				firstName = nameJO.getString("givenName");
				lastName = nameJO.getString("familyName");
				// log.debug("firstName:" + firstName);
				// log.debug("lastName:" + lastName);
			}

			String email;
			{
				JSONArray emailsJA = jsonObject.getJSONArray("emails");
				JSONObject emailJO = (JSONObject) emailsJA.get(0);
				email = emailJO.getString("value");
				// log.debug("email:" + email);
			}

			String streetAddress;
			String locality;
			String postalCode;
			String region;
			String country;
			{
				JSONArray addressesJA = jsonObject.getJSONArray("addresses");
				JSONObject addressesJO = (JSONObject) addressesJA.get(0);
				streetAddress = addressesJO.getString("streetAddress");
				locality = addressesJO.getString("locality");
				postalCode = addressesJO.getString("postalCode");
				region = addressesJO.getString("region");
				country = addressesJO.getString("country");

				// log.debug("streetAddress:" + streetAddress);
				// log.debug("locality:" + locality);
				// log.debug("postalCode:" + postalCode);
				// log.debug("region:" + region);
				// log.debug("country:" + country);
			}



			JSONArray phoneNumbersJA = jsonObject.getJSONArray("phoneNumbers");
			String phoneWork = "", phoneWorkExt = "", phoneCompany = "", phoneCompanyExt = "", phoneCell = "", phoneCellExt = "", fax = "";

			for (int i = 0; i < phoneNumbersJA.length(); i++) {
				JSONObject phoneJO = (JSONObject) phoneNumbersJA.get(i);

				if (phoneJO.getString("type").compareToIgnoreCase("Work") == 0) {
					phoneWork = phoneJO.getString("value");
					// log.debug("phoneWork:" + phoneWork);
				} else if (phoneJO.getString("type").compareToIgnoreCase("WorkExt") == 0) {
					phoneWorkExt = phoneJO.getString("value");
					// log.debug("phoneWorkExt:" + phoneWorkExt);
				} else if (phoneJO.getString("type").compareToIgnoreCase("Company") == 0) {
					phoneCompany = phoneJO.getString("value");
					// log.debug("phoneCompany:" + phoneCompany);
				} else if (phoneJO.getString("type").compareToIgnoreCase("CompanyExt") == 0) {
					phoneCompanyExt = phoneJO.getString("value");
					// log.debug("phoneCompanyExt:" + phoneCompanyExt);
				} else if (phoneJO.getString("type").compareToIgnoreCase("Cell") == 0) {
					phoneCell = phoneJO.getString("value");
					// log.debug("phoneCell:" + phoneCell);
				} else if (phoneJO.getString("type").compareToIgnoreCase("CellExt") == 0) {
					phoneCellExt = phoneJO.getString("value");
					// log.debug("phoneCellExt:" + phoneCellExt);
				} else if (phoneJO.getString("type").compareToIgnoreCase("Fax") == 0) {
					fax = phoneJO.getString("value");
					// log.debug("Fax:" + fax);
				}
			}



			String securityType = "", custNum = "";
			int contNo = 1;
			
			String dfltAddrBill = "", dfltAddrShip = "", accessRole = "";
			boolean emailConfirmed = true;
			
			JSONArray customAttributesJA = jsonObject.getJSONArray("customAttributes");
			for (int i = 0; i < customAttributesJA.length(); i++) {
				JSONObject customAttributeJO = (JSONObject) customAttributesJA.get(i);

				if (customAttributeJO.getString("name").compareToIgnoreCase("securityType") == 0) {
					securityType = customAttributeJO.getString("values").replace("[\"", "").replace("\"]", "");
					// log.debug("securityType:" + securityType);
				} else if (customAttributeJO.getString("name").compareToIgnoreCase("custNum") == 0) {
					custNum = customAttributeJO.getString("values").replace("[\"", "").replace("\"]", "");
					// log.debug("custNum:" + custNum);
				} else if (customAttributeJO.getString("name").compareToIgnoreCase("contNo") == 0) {
					contNo = new Integer(customAttributeJO.getString("values").replace("[\"", "").replace("\"]", "")).intValue();
					// log.debug("contNo:" + contNo);
				} else if (customAttributeJO.getString("name").compareToIgnoreCase("dfltAddrBill") == 0) {
					dfltAddrBill = customAttributeJO.getString("values").replace("[\"", "").replace("\"]", "");					
					// log.debug("dfltAddrBill:" + dfltAddrBill);
				} else if (customAttributeJO.getString("name").compareToIgnoreCase("dfltAddrShip") == 0) {
					dfltAddrShip = customAttributeJO.getString("values").replace("[\"", "").replace("\"]", "");
					// log.debug("dfltAddrShip:" + dfltAddrShip);
				} else if (customAttributeJO.getString("name").compareToIgnoreCase("accessRole") == 0) {
					accessRole = customAttributeJO.getString("values").replace("[\"", "").replace("\"]", "");
					// log.debug("accessRole:" + accessRole);
				} else if (customAttributeJO.getString("name").compareToIgnoreCase("emailConfirmed") == 0) {
					String emailConfirmedString = customAttributeJO.getString("values").replace("[\"", "").replace("\"]", "");
					if ((emailConfirmedString != null) && (emailConfirmedString.compareToIgnoreCase("N") == 0)) {
						emailConfirmed = false;
					} else {
						emailConfirmed = true;
					}
					// log.debug("emailConfirmed:" + emailConfirmed);
				}
			}

			GluuUser gluuUser = new GluuUser(inum, userName, password, firstName, lastName, email,
									 		 phoneWork, phoneWorkExt, phoneCompany, phoneCompanyExt, phoneCell, phoneCellExt, fax,
									 		 streetAddress, locality, postalCode, region,
									 		 country, securityType, custNum, contNo,
									 		 dfltAddrBill, dfltAddrShip, accessRole, emailConfirmed);
	        return gluuUser;
		} catch (IOException | JAXBException | NumberFormatException | JSONException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}
	}
	
	public static GluuUser retrieveUserInfoFromGluuDEV(String userName, String password, boolean useAdminAccount) throws PortalException {
		try {
			ScimClient client;
			if (useAdminAccount) {
				client = createScimClientDEV("admin", ControlServlet.GUPTA_GLUU);
			} else {
				client = createScimClientDEV(userName, password);
			}

			// ScimResponse response = client.retrievePerson("@!C032.849B.2FA5.5E8C!0001!BCB6.4A42!0000!6386.5FFE", MediaType.APPLICATION_JSON);
			// ScimResponse response = client.retrievePerson("@!C032.849B.2FA5.5E8C!0001!BCB6.4A42!0000!6386.5FFE", MediaType.APPLICATION_JSON);
			
			// ScimResponse response = client.personSearch("uid", userName, MediaType.APPLICATION_JSON);
			ScimResponse response = client.personSearch("uid", userName, MediaType.APPLICATION_JSON);
			
			// log.debug(response.getResponseBodyString());

	        String responseBodyJSONString = response.getResponseBodyString();
			// postalCode missing
	        // String responseBodyJSONString = "{\"schemas\":[\"urn:scim:schemas:core:1.0\"],\"id\":\"@!C032.849B.2FA5.5E8C!0001!BCB6.4A42!0000!5C41.1FCB\",\"externalId\":\"\",\"userName\":\"sati_0627_1712@galco.com\",\"name\":{\"givenName\":\"Subbarao\",\"familyName\":\"Ati\",\"middleName\":\"\",\"honorificPrefix\":\"\",\"honorificSuffix\":\"\"},\"displayName\":\"Subbarao-Ati\",\"nickName\":\"\",\"profileUrl\":\"\",\"emails\":[{\"value\":\"sati_0627_1712@galco.com\",\"type\":\"Work\",\"primary\":\"True\"}],\"addresses\":[{\"type\":\"Work\",\"streetAddress\":\"123 Wolverine St.\",\"locality\":\"Madison Heights\",\"region\":\"MI\",\"country\":\"USA\",\"formatted\":\"123 Wolverine St. Madison Heights 54321 MI USA\",\"primary\":\"true\"}],\"phoneNumbers\":[{\"value\":\"123-456-7890\",\"type\":\"Work\"}],\"ims\":[],\"photos\":[],\"userType\":\"\",\"title\":\"\",\"preferredLanguage\":\"US_en\",\"locale\":\"\",\"timezone\":null,\"active\":null,\"password\":\"Hidden for Privacy Reasons\",\"groups\":[],\"roles\":[],\"entitlements\":[],\"x509Certificates\":[],\"meta\":{\"created\":\"\",\"lastModified\":\"\",\"version\":\"\",\"location\":\"\"},\"customAttributes\":[{\"name\":\"securityType\",\"values\":[\"YYYN\"]}]}";
	        // All good
			// String responseBodyJSONString = "{\"schemas\":[\"urn:scim:schemas:core:1.0\"],\"id\":\"@!C032.849B.2FA5.5E8C!0001!BCB6.4A42!0000!5C41.1FCB\",\"externalId\":\"\",\"userName\":\"sati_0627_1712@galco.com\",\"name\":{\"givenName\":\"Subbarao\",\"familyName\":\"Ati\",\"middleName\":\"\",\"honorificPrefix\":\"\",\"honorificSuffix\":\"\"},\"displayName\":\"Subbarao-Ati\",\"nickName\":\"\",\"profileUrl\":\"\",\"emails\":[{\"value\":\"sati_0627_1712@galco.com\",\"type\":\"Work\",\"primary\":\"True\"}],\"addresses\":[{\"type\":\"Work\",\"streetAddress\":\"123 Wolverine St.\",\"locality\":\"Madison Heights\",\"region\":\"MI\",\"postalCode\":\"54321\",\"country\":\"USA\",\"formatted\":\"123 Wolverine St. Madison Heights 54321 MI USA\",\"primary\":\"true\"}],\"phoneNumbers\":[{\"value\":\"123-456-7890\",\"type\":\"Work\"}],\"ims\":[],\"photos\":[],\"userType\":\"\",\"title\":\"\",\"preferredLanguage\":\"US_en\",\"locale\":\"\",\"timezone\":null,\"active\":null,\"password\":\"Hidden for Privacy Reasons\",\"groups\":[],\"roles\":[],\"entitlements\":[],\"x509Certificates\":[],\"meta\":{\"created\":\"\",\"lastModified\":\"\",\"version\":\"\",\"location\":\"\"},\"customAttributes\":[{\"name\":\"securityType\",\"values\":[\"YYYN\"]}]}";

	        JSONObject jsonObject;
	        try {
				jsonObject = new JSONObject(responseBodyJSONString);
			} catch (JSONException e) {
				log.debug("GLUU response is not a valid JSON Object.", e);
				return null;
			}

	        String inum = null;
	        try {
	        	inum = jsonObject.getString("id");
			} catch (JSONException e) {
				log.debug("JSON Object has id tag missing.", e);
				return null;
			}
		
	        // log.debug("inum from Response:" + inum);

			String userNameResp = jsonObject.getString("userName");
			// log.debug("userName from Response:" + userNameResp);

			String firstName;
			String lastName;
			{
				JSONObject nameJO = jsonObject.getJSONObject("name");
				firstName = nameJO.getString("givenName");
				lastName = nameJO.getString("familyName");
				log.debug("firstName:" + firstName);
				log.debug("lastName:" + lastName);
			}

			String email;
			{
				JSONArray emailsJA = jsonObject.getJSONArray("emails");
				JSONObject emailJO = (JSONObject) emailsJA.get(0);
				email = emailJO.getString("value");
				log.debug("email:" + email);
			}

			String streetAddress;
			String locality;
			String postalCode;
			String region;
			String country;
			{
				JSONArray addressesJA = jsonObject.getJSONArray("addresses");
				JSONObject addressesJO = (JSONObject) addressesJA.get(0);
				streetAddress = addressesJO.getString("streetAddress");
				locality = addressesJO.getString("locality");
				postalCode = addressesJO.getString("postalCode");
				region = addressesJO.getString("region");
				country = addressesJO.getString("country");

				log.debug("streetAddress:" + streetAddress);
				log.debug("locality:" + locality);
				log.debug("postalCode:" + postalCode);
				log.debug("region:" + region);
				log.debug("country:" + country);
			}



			JSONArray phoneNumbersJA = jsonObject.getJSONArray("phoneNumbers");
			String phoneWork = "", phoneWorkExt = "", phoneCompany = "", phoneCompanyExt = "", phoneCell = "", phoneCellExt = "", fax = "";

			for (int i = 0; i < phoneNumbersJA.length(); i++) {
				JSONObject phoneJO = (JSONObject) phoneNumbersJA.get(i);

				if (phoneJO.getString("type").compareToIgnoreCase("Work") == 0) {
					phoneWork = phoneJO.getString("value");
					log.debug("phoneWork:" + phoneWork);
				} else if (phoneJO.getString("type").compareToIgnoreCase("WorkExt") == 0) {
					phoneWorkExt = phoneJO.getString("value");
					log.debug("phoneWorkExt:" + phoneWorkExt);
				} else if (phoneJO.getString("type").compareToIgnoreCase("Company") == 0) {
					phoneCompany = phoneJO.getString("value");
					log.debug("phoneCompany:" + phoneCompany);
				} else if (phoneJO.getString("type").compareToIgnoreCase("CompanyExt") == 0) {
					phoneCompanyExt = phoneJO.getString("value");
					log.debug("phoneCompanyExt:" + phoneCompanyExt);
				} else if (phoneJO.getString("type").compareToIgnoreCase("Cell") == 0) {
					phoneCell = phoneJO.getString("value");
					log.debug("phoneCell:" + phoneCell);
				} else if (phoneJO.getString("type").compareToIgnoreCase("CellExt") == 0) {
					phoneCellExt = phoneJO.getString("value");
					log.debug("phoneCellExt:" + phoneCellExt);
				} else if (phoneJO.getString("type").compareToIgnoreCase("Fax") == 0) {
					fax = phoneJO.getString("value");
					log.debug("Fax:" + fax);
				}
			}



			String securityType = "", custNum = "";
			int contNo = 1;
			JSONArray customAttributesJA = jsonObject.getJSONArray("customAttributes");
			for (int i = 0; i < customAttributesJA.length(); i++) {
				JSONObject customAttributeJO = (JSONObject) customAttributesJA.get(i);

				if (customAttributeJO.getString("name").compareToIgnoreCase("securityType") == 0) {
					securityType = customAttributeJO.getString("values").replace("[\"", "").replace("\"]", "");
					log.debug("securityType:" + securityType);
				} else if (customAttributeJO.getString("name").compareToIgnoreCase("custNum") == 0) {
					custNum = customAttributeJO.getString("values").replace("[\"", "").replace("\"]", "");
					log.debug("custNum:" + custNum);
				} else if (customAttributeJO.getString("name").compareToIgnoreCase("contNo") == 0) {
					contNo = new Integer(customAttributeJO.getString("values").replace("[\"", "").replace("\"]", "")).intValue();
					log.debug("contNo:" + contNo);
				}
			}

			GluuUser gluuUser = new GluuUser(inum, userName, password, firstName, lastName, email,
									 		 phoneWork, phoneWorkExt, phoneCompany, phoneCompanyExt, phoneCell, phoneCellExt, fax,
									 		 streetAddress, locality, postalCode, region,
									 		 country, securityType, custNum, contNo);
	        return gluuUser;
		} catch (IOException | JAXBException | NumberFormatException | JSONException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_2);
		}
	}	
}
