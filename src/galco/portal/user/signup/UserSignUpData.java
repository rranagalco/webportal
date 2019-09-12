package galco.portal.user.signup;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;

import galco.portal.utils.Utils;

public class UserSignUpData {
	private static Logger log = Logger.getLogger(UserSignUpData.class);

	String email;
	String password;
	String company;
	String firstName;
	String lastName;
	String middleInitial;
	String address;
	String address2;
	String city;
	String state;
	String zip;
	String country;
	String phoneWork;
	String phoneWorkExt;
	String phoneCompany;
	String phoneCompanyExt;
	String phoneCell;
	String phoneCellExt;
	String fax;

	String sameAsBilling;
	String firstNameShip;
	String lastNameShip;
    String addressShip;
    String address2Ship;
    String cityShip;
    String stateShip;
    String zipShip;
    String countryShip;
    String phoneShip;
    String phoneExtShip;

	String stateName;

	int invalidDataIndicatorFlags = 0;

	private final int EMAIL_VALIDATIION_FLAG_POS = 0;
	private final int PASSWORD_VALIDATIION_FLAG_POS = 1;
	private final int COMPANY_VALIDATIION_FLAG_POS = 2;
	private final int FIRSTNAME_VALIDATIION_FLAG_POS = 3;
	private final int LASTNAME_VALIDATIION_FLAG_POS = 4;
	private final int MIDDLEINITIAL_VALIDATIION_FLAG_POS = 5;
	private final int ADDRESS_VALIDATIION_FLAG_POS = 6;
	private final int ADDRESS2_VALIDATIION_FLAG_POS = 7;
	private final int CITY_VALIDATIION_FLAG_POS = 8;
	private final int STATE_VALIDATIION_FLAG_POS = 9;
	private final int ZIP_VALIDATIION_FLAG_POS = 10;
	private final int COUNTRY_VALIDATIION_FLAG_POS = 11;
	private final int PHONEWORK_VALIDATIION_FLAG_POS = 12;
	private final int PHONEWORKEXT_VALIDATIION_FLAG_POS = 13;
	private final int PHONECOMPANY_VALIDATIION_FLAG_POS = 14;
	private final int PHONECOMPANYEXT_VALIDATIION_FLAG_POS = 15;
	private final int PHONECELL_VALIDATIION_FLAG_POS = 16;
	private final int PHONECELLEXT_VALIDATIION_FLAG_POS = 17;
	private final int FAX_VALIDATIION_FLAG_POS = 18;
	
	private final int ADDRESSSHIP_VALIDATIION_FLAG_POS = 19;
	private final int ADDRESS2SHIP_VALIDATIION_FLAG_POS = 20;
	private final int CITYSHIP_VALIDATIION_FLAG_POS = 21;
	private final int STATESHIP_VALIDATIION_FLAG_POS = 22;
	private final int ZIPSHIP_VALIDATIION_FLAG_POS = 23;
	private final int COUNTRYSHIP_VALIDATIION_FLAG_POS = 24;
	private final int PHONESHIP_VALIDATIION_FLAG_POS = 25;
	private final int PHONEEXTSHIP_VALIDATIION_FLAG_POS = 26;	
	private final int FIRSTNAMESHIP_VALIDATIION_FLAG_POS = 27;	
	private final int LASTNAMESHIP_VALIDATIION_FLAG_POS = 28;	
	
	// -------------------------------------------------------------------------------------------------------------

	public UserSignUpData(String email, String password, String company, String firstName, String lastName, String middleInitial,
						  String address, String address2, String city, String state, String zip, String country,
						  String phoneWork, String phoneWorkExt, String phoneCompany, String phoneCompanyExt,
						  String phoneCell, String phoneCellExt, String fax) {
		this.email = email;
		this.password = password;
		this.company = company;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleInitial = middleInitial;
		this.address = address;
		this.address2 = address2;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.country = country;

		this.phoneWork = (phoneWork != null)?phoneWork:"";
		this.phoneWorkExt = (phoneWorkExt != null)?phoneWorkExt:"";

		this.phoneCompany = (phoneCompany != null)?phoneCompany:"";
		this.phoneCompanyExt = (phoneCompanyExt != null)?phoneCompanyExt:"";

		this.phoneCell = (phoneCell != null)?phoneCell:"";
		this.phoneCellExt = (phoneCellExt != null)?phoneCellExt:"";

		this.fax = (fax != null)?fax:"";

		this.sameAsBilling = null;
		this.firstNameShip = null;
		this.lastNameShip = null;
		this.addressShip = null;
		this.address2Ship = null;
		this.cityShip = null;
		this.stateShip = null;
		this.zipShip = null;
		this.countryShip = null;
		this.phoneShip = null;
		this.phoneExtShip = null;		

		validateDataBill();
	}

	// -------------------------------------------------------------------------------------------------------------

	public void setShiptoFields(String sameAsBilling, String firstNameShip, String lastNameShip,
								String addressShip, String address2Ship,
							    String cityShip, String stateShip, String zipShip,
							    String countryShip, String phoneShip, String phoneExtShip) {
		this.sameAsBilling = sameAsBilling;
		this.firstNameShip = firstNameShip;
		this.lastNameShip = lastNameShip;
		this.addressShip = addressShip;
		this.address2Ship = address2Ship;
		this.cityShip = cityShip;
		this.stateShip = stateShip;
		this.zipShip = zipShip;
		this.countryShip = countryShip;
		this.phoneShip = phoneShip;
		this.phoneExtShip = phoneExtShip;
		
		validateDataShip();
	}

	// -------------------------------------------------------------------------------------------------------------

	public void validateDataBill() {
		if (EmailValidator.getInstance().isValid(email) == false) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, EMAIL_VALIDATIION_FLAG_POS);
		}
		if (!true) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, PASSWORD_VALIDATIION_FLAG_POS);
		}
		if (!true) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, COMPANY_VALIDATIION_FLAG_POS);
		}
		if (StringUtils.isEmpty(firstName)) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, FIRSTNAME_VALIDATIION_FLAG_POS);
		}
		if (StringUtils.isEmpty(lastName)) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, LASTNAME_VALIDATIION_FLAG_POS);
		}
		if (!true) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, MIDDLEINITIAL_VALIDATIION_FLAG_POS);
		}
		if (StringUtils.isEmpty(address)) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, ADDRESS_VALIDATIION_FLAG_POS);
		}
		if (!true) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, ADDRESS2_VALIDATIION_FLAG_POS);
		}
		if (StringUtils.isEmpty(city)) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, CITY_VALIDATIION_FLAG_POS);
		}
		if (StringUtils.isEmpty(state)) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, STATE_VALIDATIION_FLAG_POS);
		}
		if (!Utils.isZipCodeValid(zip, country)) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, ZIP_VALIDATIION_FLAG_POS);
		}
		if (StringUtils.isEmpty(country)) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, COUNTRY_VALIDATIION_FLAG_POS);
		}
		Long tempPhone = Utils.isPhoneNumberValid(phoneWork);
		if (tempPhone == null) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, PHONEWORK_VALIDATIION_FLAG_POS);
		} else {
			phoneWork = tempPhone.toString();
		}
		if (StringUtils.isNotEmpty(phoneWorkExt) && !NumberUtils.isNumber(phoneWorkExt)) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, PHONEWORKEXT_VALIDATIION_FLAG_POS);
		}

		if (!StringUtils.isBlank(phoneCompany)) {
			tempPhone = Utils.isPhoneNumberValid(phoneCompany);
			if (tempPhone == null) {
				invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, PHONECOMPANY_VALIDATIION_FLAG_POS);
			} else {
				phoneCompany = tempPhone.toString();
			}
			if (StringUtils.isNotEmpty(phoneCompanyExt) && !NumberUtils.isNumber(phoneCompanyExt)) {
				invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, PHONECOMPANYEXT_VALIDATIION_FLAG_POS);
			}
		}

		if (!StringUtils.isBlank(phoneCell)) {
			tempPhone = Utils.isPhoneNumberValid(phoneCell);
			if (tempPhone == null) {
				invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, PHONECELL_VALIDATIION_FLAG_POS);
			} else {
				phoneCell = tempPhone.toString();
			}
			if (StringUtils.isNotEmpty(phoneCellExt) && !NumberUtils.isNumber(phoneCellExt)) {
				invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, PHONECELLEXT_VALIDATIION_FLAG_POS);
			}
		}

		if (!StringUtils.isBlank(fax)) {
			tempPhone = Utils.isPhoneNumberValid(fax);
			if (tempPhone == null) {
				invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, FAX_VALIDATIION_FLAG_POS);
			} else {
				fax = tempPhone.toString();
			}
		}

		log.debug(is_email_Valid() + " <--- EMAIL," + email + ", is valid?");
		// log.debug(is_password_Valid() + " <--- PASSWORD," + password + ", is valid?");
		log.debug(is_company_Valid() + " <--- COMPANY," + company + ", is valid?");
		log.debug(is_firstName_Valid() + " <--- FIRSTNAME," + firstName + ", is valid?");
		log.debug(is_lastName_Valid() + " <--- LASTNAME," + lastName + ", is valid?");
		log.debug(is_middleInitial_Valid() + " <--- MIDDLEINITIAL," + middleInitial + ", is valid?");
		log.debug(is_address_Valid() + " <--- ADDRESS," + address + ", is valid?");
		log.debug(is_address2_Valid() + " <--- ADDRESS2," + address2 + ", is valid?");
		log.debug(is_city_Valid() + " <--- CITY," + city + ", is valid?");
		log.debug(is_state_Valid() + " <--- STATE," + state + ", is valid?");
		log.debug(is_zip_Valid() + " <--- ZIP," + zip + ", is valid?");
		log.debug(is_country_Valid() + " <--- COUNTRY," + country + ", is valid?");
		log.debug(is_phoneWork_Valid() + " <--- PHONEWORK," + phoneWork + ", is valid?");
		log.debug(is_phoneWorkExt_Valid() + " <--- PHONEWORKEXT," + phoneWorkExt + ", is valid?");
		log.debug(is_phoneCompany_Valid() + " <--- PHONECOMPANY," + phoneCompany + ", is valid?");
		log.debug(is_phoneCompanyExt_Valid() + " <--- PHONECOMPANYEXT," + phoneCompanyExt + ", is valid?");
		log.debug(is_phoneCell_Valid() + " <--- PHONECELL," + phoneCell + ", is valid?");
		log.debug(is_phoneCellExt_Valid() + " <--- PHONECELLEXT," + phoneCellExt + ", is valid?");
		log.debug(is_fax_Valid() + " <--- FAX," + fax + ", is valid?");
	}

	public void validateDataShip() {
		if (StringUtils.isEmpty(firstNameShip)) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, FIRSTNAMESHIP_VALIDATIION_FLAG_POS);
		}
		if (StringUtils.isEmpty(lastNameShip)) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, LASTNAMESHIP_VALIDATIION_FLAG_POS);
		}
		if (StringUtils.isEmpty(addressShip)) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, ADDRESSSHIP_VALIDATIION_FLAG_POS);
		}
		if (!true) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, ADDRESS2SHIP_VALIDATIION_FLAG_POS);
		}
		if (StringUtils.isEmpty(cityShip)) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, CITYSHIP_VALIDATIION_FLAG_POS);
		}
		if (StringUtils.isEmpty(stateShip)) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, STATESHIP_VALIDATIION_FLAG_POS);
		}
		if (!Utils.isZipCodeValid(zipShip, countryShip)) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, ZIPSHIP_VALIDATIION_FLAG_POS);
		}
		if (StringUtils.isEmpty(countryShip)) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, COUNTRYSHIP_VALIDATIION_FLAG_POS);
		}
		Long tempPhone = Utils.isPhoneNumberValid(phoneShip);
		if (tempPhone == null) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, PHONESHIP_VALIDATIION_FLAG_POS);
		} else {
			phoneShip = tempPhone.toString();
		}
		if (StringUtils.isNotEmpty(phoneExtShip) && !NumberUtils.isNumber(phoneExtShip)) {
			invalidDataIndicatorFlags = Utils.setBit(invalidDataIndicatorFlags, PHONEEXTSHIP_VALIDATIION_FLAG_POS);
		}

        log.debug(is_firstNameShip_Valid() + " <--- firstNameShip," + firstNameShip + ", is valid?");
        log.debug(is_lastNameShip_Valid() + " <--- lastNameShip," + lastNameShip + ", is valid?");
        log.debug(is_addressShip_Valid() + " <--- addressShip," + addressShip + ", is valid?");
        log.debug(is_address2Ship_Valid() + " <--- address2Ship," + address2Ship + ", is valid?");
        log.debug(is_cityShip_Valid() + " <--- cityShip," + cityShip + ", is valid?");
        log.debug(is_stateShip_Valid() + " <--- stateShip," + stateShip + ", is valid?");
        log.debug(is_zipShip_Valid() + " <--- zipShip," + zipShip + ", is valid?");
        log.debug(is_countryShip_Valid() + " <--- countryShip," + countryShip + ", is valid?");
        log.debug(is_phoneShip_Valid() + " <--- phoneShip," + phoneShip + ", is valid?");
        log.debug(is_phoneExtShip_Valid() + " <--- phoneExtShip," + phoneExtShip + ", is valid?");
	}

	// -----------------------------------------------------------------------------------------------------
	
	public boolean isAllUserEnteredDataValid() {
		 return (invalidDataIndicatorFlags == 0);
	}

	public boolean is_email_Valid() {
		return !Utils.getBit(invalidDataIndicatorFlags, EMAIL_VALIDATIION_FLAG_POS);
	}
	public boolean is_password_Valid() {
		return !Utils.getBit(invalidDataIndicatorFlags, PASSWORD_VALIDATIION_FLAG_POS);
	}
	public boolean is_company_Valid() {
		return !Utils.getBit(invalidDataIndicatorFlags, COMPANY_VALIDATIION_FLAG_POS);
	}
	public boolean is_firstName_Valid() {
		return !Utils.getBit(invalidDataIndicatorFlags, FIRSTNAME_VALIDATIION_FLAG_POS);
	}
	public boolean is_lastName_Valid() {
		return !Utils.getBit(invalidDataIndicatorFlags, LASTNAME_VALIDATIION_FLAG_POS);
	}
	public boolean is_middleInitial_Valid() {
		return !Utils.getBit(invalidDataIndicatorFlags, MIDDLEINITIAL_VALIDATIION_FLAG_POS);
	}
	public boolean is_address_Valid() {
		return !Utils.getBit(invalidDataIndicatorFlags, ADDRESS_VALIDATIION_FLAG_POS);
	}
	public boolean is_address2_Valid() {
		return !Utils.getBit(invalidDataIndicatorFlags, ADDRESS2_VALIDATIION_FLAG_POS);
	}
	public boolean is_city_Valid() {
		return !Utils.getBit(invalidDataIndicatorFlags, CITY_VALIDATIION_FLAG_POS);
	}
	public boolean is_state_Valid() {
		return !Utils.getBit(invalidDataIndicatorFlags, STATE_VALIDATIION_FLAG_POS);
	}
	public boolean is_zip_Valid() {
		return !Utils.getBit(invalidDataIndicatorFlags, ZIP_VALIDATIION_FLAG_POS);
	}
	public boolean is_country_Valid() {
		return !Utils.getBit(invalidDataIndicatorFlags, COUNTRY_VALIDATIION_FLAG_POS);
	}
	public boolean is_phoneWork_Valid() {
		return !Utils.getBit(invalidDataIndicatorFlags, PHONEWORK_VALIDATIION_FLAG_POS);
	}
	public boolean is_phoneWorkExt_Valid() {
		return !Utils.getBit(invalidDataIndicatorFlags, PHONEWORKEXT_VALIDATIION_FLAG_POS);
	}
	public boolean is_phoneCompany_Valid() {
		return !Utils.getBit(invalidDataIndicatorFlags, PHONECOMPANY_VALIDATIION_FLAG_POS);
	}
	public boolean is_phoneCompanyExt_Valid() {
		return !Utils.getBit(invalidDataIndicatorFlags, PHONECOMPANYEXT_VALIDATIION_FLAG_POS);
	}
	public boolean is_phoneCell_Valid() {
		return !Utils.getBit(invalidDataIndicatorFlags, PHONECELL_VALIDATIION_FLAG_POS);
	}
	public boolean is_phoneCellExt_Valid() {
		return !Utils.getBit(invalidDataIndicatorFlags, PHONECELLEXT_VALIDATIION_FLAG_POS);
	}
	public boolean is_fax_Valid() {
		return !Utils.getBit(invalidDataIndicatorFlags, FAX_VALIDATIION_FLAG_POS);
	}

	// -------------------------------------------------------------------------------------------------------------

    public boolean is_firstNameShip_Valid() {
        return !Utils.getBit(invalidDataIndicatorFlags, FIRSTNAMESHIP_VALIDATIION_FLAG_POS);
    }
    public boolean is_lastNameShip_Valid() {
        return !Utils.getBit(invalidDataIndicatorFlags, LASTNAMESHIP_VALIDATIION_FLAG_POS);
    }
    public boolean is_addressShip_Valid() {
        return !Utils.getBit(invalidDataIndicatorFlags, ADDRESSSHIP_VALIDATIION_FLAG_POS);
    }
    public boolean is_address2Ship_Valid() {
        return !Utils.getBit(invalidDataIndicatorFlags, ADDRESS2SHIP_VALIDATIION_FLAG_POS);
    }
    public boolean is_cityShip_Valid() {
        return !Utils.getBit(invalidDataIndicatorFlags, CITYSHIP_VALIDATIION_FLAG_POS);
    }
    public boolean is_stateShip_Valid() {
        return !Utils.getBit(invalidDataIndicatorFlags, STATESHIP_VALIDATIION_FLAG_POS);
    }
    public boolean is_zipShip_Valid() {
        return !Utils.getBit(invalidDataIndicatorFlags, ZIPSHIP_VALIDATIION_FLAG_POS);
    }
    public boolean is_countryShip_Valid() {
        return !Utils.getBit(invalidDataIndicatorFlags, COUNTRYSHIP_VALIDATIION_FLAG_POS);
    }
    public boolean is_phoneShip_Valid() {
        return !Utils.getBit(invalidDataIndicatorFlags, PHONESHIP_VALIDATIION_FLAG_POS);
    }
    public boolean is_phoneExtShip_Valid() {
        return !Utils.getBit(invalidDataIndicatorFlags, PHONEEXTSHIP_VALIDATIION_FLAG_POS);
    }        
	
	// -------------------------------------------------------------------------------------------------------------

	public String getCombinedErrorMessage() {
		return getCombinedErrorMessageBill() + getCombinedErrorMessageShip();
	}
	
	public String getCombinedErrorMessageBill() {
		if (isAllUserEnteredDataValid()) {
			return null;
		}

		String message = "";

	    if (!is_email_Valid()) {
	        message += ((message != null)?"<p/>":"") + "Email entered is not valid.";
	    }
	    if (!is_password_Valid()) {
	        message += ((message != null)?"<p/>":"") + "Password entered is not valid.";
	    }
	    if (!is_company_Valid()) {
	        message += ((message != null)?"<p/>":"") + "Company entered is not valid.";
	    }
	    if (!is_firstName_Valid()) {
	        message += ((message != null)?"<p/>":"") + "First name entered is not valid.";
	    }
	    if (!is_lastName_Valid()) {
	        message += ((message != null)?"<p/>":"") + "Last name entered is not valid.";
	    }
	    if (!is_middleInitial_Valid()) {
	        message += ((message != null)?"<p/>":"") + "Middle initial entered is not valid.";
	    }
	    if (!is_address_Valid()) {
	        message += ((message != null)?"<p/>":"") + "Address entered is not valid.";
	    }
	    if (!is_address2_Valid()) {
	        message += ((message != null)?"<p/>":"") + "Address2 entered is not valid.";
	    }
	    if (!is_city_Valid()) {
	        message += ((message != null)?"<p/>":"") + "City entered is not valid.";
	    }
	    if (!is_state_Valid()) {
	        message += ((message != null)?"<p/>":"") + "State entered is not valid.";
	    }
	    if (!is_zip_Valid()) {
	        message += ((message != null)?"<p/>":"") + "Postal code entered is not valid.";
	    }
	    if (!is_country_Valid()) {
	        message += ((message != null)?"<p/>":"") + "Country entered is not valid.";
	    }
	    if (!is_phoneWork_Valid()) {
	        message += ((message != null)?"<p/>":"") + "Phone number entered is not valid.";
	    }
	    if (!is_phoneWorkExt_Valid()) {
	        message += ((message != null)?"<p/>":"") + "Phone number extension entered is not valid.";
	    }
	    if (!is_phoneCompany_Valid()) {
	        message += ((message != null)?"<p/>":"") + "Phone (Company) number entered is not valid.";
	    }
	    if (!is_phoneCompanyExt_Valid()) {
	        message += ((message != null)?"<p/>":"") + "Phone (Company) number extension entered is not valid.";
	    }
	    if (!is_phoneCell_Valid()) {
	        message += ((message != null)?"<p/>":"") + "Phone (Cell) number entered is not valid.";
	    }
	    if (!is_phoneCellExt_Valid()) {
	        message += ((message != null)?"<p/>":"") + "Phone (Cell) number extension entered is not valid.";
	    }
	    if (!is_fax_Valid()) {
	        message += ((message != null)?"<p/>":"") + "Fax number entered is not valid.";
	    }

	    return message;
	}

	// -------------------------------------------------------------------------------------------------------------

	public String getCombinedErrorMessageShip() {
		if (isAllUserEnteredDataValid()) {
			return null;
		}

		String message = "";

        if (!is_firstNameShip_Valid()) {
            message += ((message != null)?"<p/>":"") + "First Name entered is not valid.";
        }
        if (!is_lastNameShip_Valid()) {
            message += ((message != null)?"<p/>":"") + "Last Name entered is not valid.";
        }
        if (!is_addressShip_Valid()) {
            message += ((message != null)?"<p/>":"") + "Address entered is not valid.";
        }
        if (!is_address2Ship_Valid()) {
            message += ((message != null)?"<p/>":"") + "Address2 entered is not valid.";
        }
        if (!is_cityShip_Valid()) {
            message += ((message != null)?"<p/>":"") + "City entered is not valid.";
        }
        if (!is_stateShip_Valid()) {
            message += ((message != null)?"<p/>":"") + "State entered is not valid.";
        }
        if (!is_zipShip_Valid()) {
            message += ((message != null)?"<p/>":"") + "Postal code entered is not valid.";
        }
        if (!is_countryShip_Valid()) {
            message += ((message != null)?"<p/>":"") + "Country entered is not valid.";
        }
        if (!is_phoneShip_Valid()) {
            message += ((message != null)?"<p/>":"") + "Phone number entered is not valid.";
        }
        if (!is_phoneExtShip_Valid()) {
            message += ((message != null)?"<p/>":"") + "Phone number extension entered is not valid.";
        }

	    return message;
	}

// -------------------------------------------------------------------------------------------------------------

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleInitial() {
		return middleInitial;
	}

	public void setMiddleInitial(String middleInitial) {
		this.middleInitial = middleInitial;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPhoneWork() {
		return phoneWork;
	}

	public void setPhoneWork(String phoneWork) {
		this.phoneWork = phoneWork;
	}

	public String getPhoneWorkExt() {
		return phoneWorkExt;
	}

	public void setPhoneWorkExt(String phoneWorkExt) {
		this.phoneWorkExt = phoneWorkExt;
	}

	public String getPhoneCompany() {
		return phoneCompany;
	}

	public void setPhoneCompany(String phoneCompany) {
		this.phoneCompany = phoneCompany;
	}

	public String getPhoneCompanyExt() {
		return phoneCompanyExt;
	}

	public void setPhoneCompanyExt(String phoneCompanyExt) {
		this.phoneCompanyExt = phoneCompanyExt;
	}

	public String getPhoneCell() {
		return phoneCell;
	}

	public void setPhoneCell(String phoneCell) {
		this.phoneCell = phoneCell;
	}

	public String getPhoneCellExt() {
		return phoneCellExt;
	}

	public void setPhoneCellExt(String phoneCellExt) {
		this.phoneCellExt = phoneCellExt;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getStateName() {
		if (country != null) {
			if ((country.compareToIgnoreCase("United States") == 0) ||
				(country.compareToIgnoreCase("USA") 		  == 0)    ) {
				if (state.compareToIgnoreCase("AL") == 0) {
				    return "Alabama";
				} else if (state.compareToIgnoreCase("AK") == 0) {
				    return "Alaska";
				} else if (state.compareToIgnoreCase("AS") == 0) {
				    return "American Samoa";
				} else if (state.compareToIgnoreCase("AZ") == 0) {
				    return "Arizona";
				} else if (state.compareToIgnoreCase("AR") == 0) {
				    return "Arkansas";
				} else if (state.compareToIgnoreCase("CA") == 0) {
				    return "California";
				} else if (state.compareToIgnoreCase("CO") == 0) {
				    return "Colorado";
				} else if (state.compareToIgnoreCase("CT") == 0) {
				    return "Connecticut";
				} else if (state.compareToIgnoreCase("DE") == 0) {
				    return "Delaware";
				} else if (state.compareToIgnoreCase("DC") == 0) {
				    return "District Of Columbia";
				} else if (state.compareToIgnoreCase("FM") == 0) {
				    return "Federated States Of Micronesia";
				} else if (state.compareToIgnoreCase("FL") == 0) {
				    return "Florida";
				} else if (state.compareToIgnoreCase("GA") == 0) {
				    return "Georgia";
				} else if (state.compareToIgnoreCase("GU") == 0) {
				    return "Guam";
				} else if (state.compareToIgnoreCase("HI") == 0) {
				    return "Hawaii";
				} else if (state.compareToIgnoreCase("ID") == 0) {
				    return "Idaho";
				} else if (state.compareToIgnoreCase("IL") == 0) {
				    return "Illinois";
				} else if (state.compareToIgnoreCase("IN") == 0) {
				    return "Indiana";
				} else if (state.compareToIgnoreCase("IA") == 0) {
				    return "Iowa";
				} else if (state.compareToIgnoreCase("KS") == 0) {
				    return "Kansas";
				} else if (state.compareToIgnoreCase("KY") == 0) {
				    return "Kentucky";
				} else if (state.compareToIgnoreCase("LA") == 0) {
				    return "Louisiana";
				} else if (state.compareToIgnoreCase("ME") == 0) {
				    return "Maine";
				} else if (state.compareToIgnoreCase("MH") == 0) {
				    return "Marshall Islands";
				} else if (state.compareToIgnoreCase("MD") == 0) {
				    return "Maryland";
				} else if (state.compareToIgnoreCase("MA") == 0) {
				    return "Massachusetts";
				} else if (state.compareToIgnoreCase("MI") == 0) {
				    return "Michigan";
				} else if (state.compareToIgnoreCase("MN") == 0) {
				    return "Minnesota";
				} else if (state.compareToIgnoreCase("MS") == 0) {
				    return "Mississippi";
				} else if (state.compareToIgnoreCase("MO") == 0) {
				    return "Missouri";
				} else if (state.compareToIgnoreCase("MT") == 0) {
				    return "Montana";
				} else if (state.compareToIgnoreCase("NE") == 0) {
				    return "Nebraska";
				} else if (state.compareToIgnoreCase("NV") == 0) {
				    return "Nevada";
				} else if (state.compareToIgnoreCase("NH") == 0) {
				    return "New Hampshire";
				} else if (state.compareToIgnoreCase("NJ") == 0) {
				    return "New Jersey";
				} else if (state.compareToIgnoreCase("NM") == 0) {
				    return "New Mexico";
				} else if (state.compareToIgnoreCase("NY") == 0) {
				    return "New York";
				} else if (state.compareToIgnoreCase("NC") == 0) {
				    return "North Carolina";
				} else if (state.compareToIgnoreCase("ND") == 0) {
				    return "North Dakota";
				} else if (state.compareToIgnoreCase("MP") == 0) {
				    return "Northern Mariana Islands";
				} else if (state.compareToIgnoreCase("OH") == 0) {
				    return "Ohio";
				} else if (state.compareToIgnoreCase("OK") == 0) {
				    return "Oklahoma";
				} else if (state.compareToIgnoreCase("OR") == 0) {
				    return "Oregon";
				} else if (state.compareToIgnoreCase("PW") == 0) {
				    return "Palau";
				} else if (state.compareToIgnoreCase("PA") == 0) {
				    return "Pennsylvania";
				} else if (state.compareToIgnoreCase("PR") == 0) {
				    return "Puerto Rico";
				} else if (state.compareToIgnoreCase("RI") == 0) {
				    return "Rhode Island";
				} else if (state.compareToIgnoreCase("SC") == 0) {
				    return "South Carolina";
				} else if (state.compareToIgnoreCase("SD") == 0) {
				    return "South Dakota";
				} else if (state.compareToIgnoreCase("TN") == 0) {
				    return "Tennessee";
				} else if (state.compareToIgnoreCase("TX") == 0) {
				    return "Texas";
				} else if (state.compareToIgnoreCase("UT") == 0) {
				    return "Utah";
				} else if (state.compareToIgnoreCase("VT") == 0) {
				    return "Vermont";
				} else if (state.compareToIgnoreCase("VI") == 0) {
				    return "Virgin Islands";
				} else if (state.compareToIgnoreCase("VA") == 0) {
				    return "Virginia";
				} else if (state.compareToIgnoreCase("WA") == 0) {
				    return "Washington";
				} else if (state.compareToIgnoreCase("WV") == 0) {
				    return "West Virginia";
				} else if (state.compareToIgnoreCase("WI") == 0) {
				    return "Wisconsin";
				} else if (state.compareToIgnoreCase("WY") == 0) {
				    return "Wyoming";
				}
			} else if (country.compareToIgnoreCase("Canada") == 0) {
				if (state.compareToIgnoreCase("AB") == 0) {
				    return "Alberta";
				} else if (state.compareToIgnoreCase("BC") == 0) {
				    return "British Columbia";
				} else if (state.compareToIgnoreCase("MB") == 0) {
				    return "Manitoba";
				} else if (state.compareToIgnoreCase("NB") == 0) {
				    return "New Brunswick";
				} else if (state.compareToIgnoreCase("NL") == 0) {
				    return "Newfoundland and Labrador";
				} else if (state.compareToIgnoreCase("NS") == 0) {
				    return "Nova Scotia";
				} else if (state.compareToIgnoreCase("NT") == 0) {
				    return "Northwest Territories";
				} else if (state.compareToIgnoreCase("NU") == 0) {
				    return "Nunavut";
				} else if (state.compareToIgnoreCase("ON") == 0) {
				    return "Ontario";
				} else if (state.compareToIgnoreCase("PE") == 0) {
				    return "Prince Edward Island";
				} else if (state.compareToIgnoreCase("QC") == 0) {
				    return "Quebec";
				} else if (state.compareToIgnoreCase("SK") == 0) {
				    return "Saskatchewan";
				} else if (state.compareToIgnoreCase("YT") == 0) {
				    return "Yukon Territory";
				}
			}
		}

		return ((state != null)?state:"");
	}
	
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	// -----------------------------------------------------------------------------------------------------

	public String getSameAsBilling() {
		return sameAsBilling;
	}

	public void setSameAsBilling(String sameAsBilling) {
		this.sameAsBilling = sameAsBilling;
	}

	public String getFirstNameShip() {
		return firstNameShip;
	}

	public void setFirstNameShip(String firstNameShip) {
		this.firstNameShip = firstNameShip;
	}

	public String getLastNameShip() {
		return lastNameShip;
	}

	public void setLastNameShip(String lastNameShip) {
		this.lastNameShip = lastNameShip;
	}
	
	public String getAddressShip() {
		return addressShip;
	}

	public void setAddressShip(String addressShip) {
		this.addressShip = addressShip;
	}

	public String getAddress2Ship() {
		return address2Ship;
	}

	public void setAddress2Ship(String address2Ship) {
		this.address2Ship = address2Ship;
	}

	public String getCityShip() {
		return cityShip;
	}

	public void setCityShip(String cityShip) {
		this.cityShip = cityShip;
	}

	public String getStateShip() {
		return stateShip;
	}

	public void setStateShip(String stateShip) {
		this.stateShip = stateShip;
	}

	public String getZipShip() {
		return zipShip;
	}

	public void setZipShip(String zipShip) {
		this.zipShip = zipShip;
	}

	public String getCountryShip() {
		return countryShip;
	}

	public void setCountryShip(String countryShip) {
		this.countryShip = countryShip;
	}

	public String getPhoneShip() {
		return phoneShip;
	}

	public void setPhoneShip(String phoneShip) {
		this.phoneShip = phoneShip;
	}

	public String getPhoneExtShip() {
		return phoneExtShip;
	}

	public void setPhoneExtShip(String phoneExtShip) {
		this.phoneExtShip = phoneExtShip;
	}	
	
}