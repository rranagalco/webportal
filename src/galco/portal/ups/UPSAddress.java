package galco.portal.ups;

import galco.portal.control.ControlServlet;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jsslutils.extra.apachehttpclient.SslContextedSecureProtocolSocketFactory;

public class UPSAddress {
	private static Logger log = Logger.getLogger(UPSAddress.class);
	
	public static final String UPS_JSON_REQUEST_STATIC_STRING =
			"{" +
			"    \"UPSSecurity\": {" +
			"        \"UsernameToken\": {" +
			"            \"Username\": \"ncirullo9\"," +
			"            \"Password\": \"*NwxLu]EUJdd_2u7\"" +
			"        }," +
			"        \"ServiceAccessToken\": {" +
			"            \"AccessLicenseNumber\": \"7D17BD4506043788\"" +
			"        }" +
			"    }," +
			"    \"XAVRequest\": {" +
			"        \"Request\": {" +
			"            \"RequestOption\": \"1\"," +
			"            \"TransactionReference\": {" +
			"                \"CustomerContext\": \"PortalTest\"" +
			"            }" +
			"        }," +
			"        \"MaximumListSize\": \"10\"," +
			"        \"AddressKeyFormat\": {" +
			"            \"ConsigneeName\": \"Consignee Name\"," +
			"            \"BuildingName\": \"Building Name\",";

	public static void installTM_UPS() {		
        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        	
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
	        ctx.init(new KeyManager[0], new TrustManager[] {tm}, new SecureRandom());			
	        SSLContext.setDefault(ctx);		
            SslContextedSecureProtocolSocketFactory secureProtocolSocketFactory =
                    new SslContextedSecureProtocolSocketFactory(ctx);
            Protocol.registerProtocol("https", new Protocol("https", (ProtocolSocketFactory) secureProtocolSocketFactory, 443));	        
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}
	
	private String addressLine;
	private String politicalDivision2;
	private String politicalDivision1;
	private String postcodePrimaryLow;
	private String postcodeExtendedLow;
	private String region;
	private String countryCode;
	
	// qqq
	
	// UPS Validation return values
	public static final int GOOD_ADDRESS = 1;
	public static final int AMBIGUOUS_ADDRESS = 2;
	public static final int CODE_IS_MISSING = 3;
	public static final int NO_CANDIDATES = 4;
	public static final int UNKNOWN_CODE = 5;
	public static final int JSON_EXCEPTION = 6;
	public static final int EXCEPTIION_OCCURRED = 7;
	
	// --------------------------------------------------------------------------------------------

	public UPSAddress() {
	}
	
	public void print() {
		/*
		Code       : 1
		Description: Success
		
		AddressLine        : 1587 CANTERBURY RD N
		PoliticalDivision2 : SAINT PETERSBURG
		PoliticalDivision1 : FL
		PostcodePrimaryLow : 33710
		PostcodeExtendedLow: 5659
		Region             : SAINT PETERSBURG FL 33710-5659
		CountryCode        : US
		*/	
		
		log.debug("AddressLine         : " + getAddressLine());
		log.debug("PoliticalDivision2  : " + getPoliticalDivision2());
		log.debug("PoliticalDivision1  : " + getPoliticalDivision1());
		log.debug("PostcodePrimaryLow  : " + getPostcodePrimaryLow());
		log.debug("PostcodeExtendedLow : " + getPostcodeExtendedLow());
		log.debug("Region              : " + getRegion());
		log.debug("CountryCode         : " + getCountryCode() + "\n");
	}
	// --------------------------------------------------------------------------------------------
	
	public String getAddressLine() {
		return addressLine;
	}
	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}
	public String getPoliticalDivision2() {
		return politicalDivision2;
	}
	public void setPoliticalDivision2(String politicalDivision2) {
		this.politicalDivision2 = politicalDivision2;
	}
	public String getPoliticalDivision1() {
		return politicalDivision1;
	}
	public void setPoliticalDivision1(String politicalDivision1) {
		this.politicalDivision1 = politicalDivision1;
	}
	public String getPostcodePrimaryLow() {
		return postcodePrimaryLow;
	}
	public void setPostcodePrimaryLow(String postcodePrimaryLow) {
		this.postcodePrimaryLow = postcodePrimaryLow;
	}
	public String getPostcodeExtendedLow() {
		return postcodeExtendedLow;
	}
	public void setPostcodeExtendedLow(String postcodeExtendedLow) {
		this.postcodeExtendedLow = postcodeExtendedLow;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	// --------------------------------------------------------------------------------------------
					
	public String getAddress() {
		return addressLine;
	}
	public String getCity() {
		return politicalDivision2;
	}
	public String getState() {
		return politicalDivision1;
	}
	public String getZip() {
		return postcodePrimaryLow;
	}
	public String getZip4() {
		return postcodeExtendedLow;
	}
	public String getCountry() {
		return countryCode;
	}	
	
	// --------------------------------------------------------------------------------------------
	
	public boolean areTheAddressesSame(String address, String city, String state, String zip, String zip4, String country) {
		if ((address.compareToIgnoreCase(addressLine)         == 0) &&
		    (city.compareToIgnoreCase(politicalDivision2)     == 0) &&
		    (state.compareToIgnoreCase(politicalDivision1)    == 0) &&
		    (zip.compareToIgnoreCase(postcodePrimaryLow)      == 0) &&
		    (country.compareToIgnoreCase(countryCode)         == 0)    ) {
			if (StringUtils.isBlank(zip4) == false) {
				return (zip4.compareToIgnoreCase(postcodeExtendedLow) == 0);
			} else {
				return true;
			}
		}
		
		return false;
	}

	// --------------------------------------------------------------------------------------------
		
	public static Object[] validateAddressWithUPS(String name, String address, String city, String state, String zip, String zip4, String country) {
        HttpClient httpClient = new HttpClient();
        Object[] addressValidationResults = new Object[3];
        JSONObject xavResponseJSONObject = new JSONObject();
        
        PostMethod post = null;
        		
		try {
			String jsonRequest = 
					UPS_JSON_REQUEST_STATIC_STRING +
					
					/*
					"            \"AddressLine\": \"26010 Pinehurst Drive\"," +
					"            \"PoliticalDivision2\": \"Madison Heights\"," +
					"            \"PoliticalDivision1\": \"MI\"," +
					"            \"PostcodePrimaryLow\": \"48071\"," +
					*/
					
					/*
					"            \"AddressLine\": \"12380 Morris Road\"," +
					"            \"PoliticalDivision2\": \"Alpharetta\"," +
					"            \"PoliticalDivision1\": \"GA\"," +
					"            \"PostcodePrimaryLow\": \"30009\"," +
					*/
					
					
					
					
					"            \"ConsigneeName\": \"" + name + "\"," +
					"            \"AddressLine\": \"" + address + "\"," +
					"            \"PoliticalDivision2\": \"" + city + "\"," +
					"            \"PoliticalDivision1\": \"" + state + "\"," +
					"            \"PostcodePrimaryLow\": \"" + zip + "\"," +
					
					(	(StringUtils.isBlank(zip4) == false)?
						("            \"PostcodeExtendedLow\": \"" + zip4 + "\","):
						""
					) +
					
					"            \"CountryCode\": \"" + country + "\"" +
					"        }" +
					"    }" +
					"}";			

			// log.debug(jsonRequest);
			// if (true) return 1;
	        
	        /*
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost("https://wwwcie.ups.com/rest/XAV");
			StringEntity params =new StringEntity(jsonRequest);
			request.addHeader("content-type", "application/json");
			request.addHeader("Accept","application/json");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			log.debug(response);
			*/
			
			addressValidationResults[2] = "{}";

            // PostMethod post = new PostMethod("https://wwwcie.ups.com/rest/XAV");
            post = new PostMethod("https://onlinetools.ups.com/rest/XAV");
            
            StringRequestEntity requestEntity = new StringRequestEntity(
                    jsonRequest,
                    "application/json",
                    "UTF-8");
            post.setRequestEntity(requestEntity);
                        
            httpClient.executeMethod(post);
            
            // log.debug(new String(post.getResponseBody()));
            // String responseBodyString = "{\"XAVResponse\":{\"Response\":{\"ResponseStatus\":{\"Code\":\"1\", \"Description\":\"Success\"}, \"TransactionReference\":{\"CustomerContext\":\"PortalTest\"}}, \"ValidAddressIndicator\":\"\", \"Candidate\":{\"AddressKeyFormat\":{\"AddressLine\":\"1587 CANTERBURY RD N\", \"PoliticalDivision2\":\"SAINT PETERSBURG\", \"PoliticalDivision1\":\"FL\", \"PostcodePrimaryLow\":\"33710\", \"PostcodeExtendedLow\":\"5659\", \"Region\":\"SAINT PETERSBURG FL 33710-5659\", \"CountryCode\":\"US\"}}}}";
            // "{"XAVResponse":{"Response":{"ResponseStatus":{"Code":"1", "Description":"Success"}, "TransactionReference":{"CustomerContext":"PortalTest"}}, "AmbiguousAddressIndicator":"", "Candidate":[{"AddressKeyFormat":{"AddressLine":"1300-1398 CANTERBURY RD N", "PoliticalDivision2":"SAINT PETERSBURG", "PoliticalDivision1":"FL", "PostcodePrimaryLow":"33710", "PostcodeExtendedLow":"6303", "Region":"SAINT PETERSBURG FL 33710-6303", "CountryCode":"US"}}, {"AddressKeyFormat":{"AddressLine":"1301-1399 CANTERBURY RD N", "PoliticalDivision2":"SAINT PETERSBURG", "PoliticalDivision1":"FL", "PostcodePrimaryLow":"33710", "PostcodeExtendedLow":"6302", "Region":"SAINT PETERSBURG FL 33710-6302", "CountryCode":"US"}}, {"AddressKeyFormat":{"AddressLine":"1400-1498 CANTERBURY RD N", "PoliticalDivision2":"SAINT PETERSBURG", "PoliticalDivision1":"FL", "PostcodePrimaryLow":"33710", "PostcodeExtendedLow":"6305", "Region":"SAINT PETERSBURG FL 33710-6305", "CountryCode":"US"}}, {"AddressKeyFormat":{"AddressLine":"1401-1499 CANTERBURY RD N", "PoliticalDivision2":"SAINT PETERSBURG", "PoliticalDivision1":"FL", "PostcodePrimaryLow":"33710", "PostcodeExtendedLow":"6304", "Region":"SAINT PETERSBURG FL 33710-6304", "CountryCode":"US"}}, {"AddressKeyFormat":{"AddressLine":"1500-1510 CANTERBURY RD N", "PoliticalDivision2":"SAINT PETERSBURG", "PoliticalDivision1":"FL", "PostcodePrimaryLow":"33710", "PostcodeExtendedLow":"6339", "Region":"SAINT PETERSBURG FL 33710-6339", "CountryCode":"US"}}, {"AddressKeyFormat":{"AddressLine":"1501-1513 CANTERBURY RD N", "PoliticalDivision2":"SAINT PETERSBURG", "PoliticalDivision1":"FL", "PostcodePrimaryLow":"33710", "PostcodeExtendedLow":"6338", "Region":"SAINT PETERSBURG FL 33710-6338", "CountryCode":"US"}}, {"AddressKeyFormat":{"AddressLine":"1512-1598 CANTERBURY RD N", "PoliticalDivision2":"SAINT PETERSBURG", "PoliticalDivision1":"FL", "PostcodePrimaryLow":"33710", "PostcodeExtendedLow":"5658", "Region":"SAINT PETERSBURG FL 33710-5658", "CountryCode":"US"}}, {"AddressKeyFormat":{"AddressLine":"1515-1599 CANTERBURY RD N", "PoliticalDivision2":"SAINT PETERSBURG", "PoliticalDivision1":"FL", "PostcodePrimaryLow":"33710", "PostcodeExtendedLow":"5659", "Region":"SAINT PETERSBURG FL 33710-5659", "CountryCode":"US"}}, {"AddressKeyFormat":{"AddressLine":"1600-1698 CANTERBURY RD N", "PoliticalDivision2":"SAINT PETERSBURG", "PoliticalDivision1":"FL", "PostcodePrimaryLow":"33710", "PostcodeExtendedLow":"5606", "Region":"SAINT PETERSBURG FL 33710-5606", "CountryCode":"US"}}, {"AddressKeyFormat":{"AddressLine":"1601-1699 CANTERBURY RD N", "PoliticalDivision2":"SAINT PETERSBURG", "PoliticalDivision1":"FL", "PostcodePrimaryLow":"33710", "PostcodeExtendedLow":"5605", "Region":"SAINT PETERSBURG FL 33710-5605", "CountryCode":"US"}}]}}"
            // {"Response":{"ResponseStatus":{"Code":"1","Description":"Success"},"TransactionReference":{"CustomerContext":"PortalTest"}},"NoCandidatesIndicator":""}
            
    		try {
				addressValidationResults[2] = new String(post.getResponseBody());
    			
    			xavResponseJSONObject = (new JSONObject(new String(post.getResponseBody()))).getJSONObject("XAVResponse");

    			boolean validAddress = false, ambiguousAddress = false, noCandidates = false;
    			
    			if (xavResponseJSONObject.has("ValidAddressIndicator") == true) {
    				validAddress = true;
    			}
    			if (xavResponseJSONObject.has("AmbiguousAddressIndicator") == true) {
    				ambiguousAddress = true;
    			}
    			if (xavResponseJSONObject.has("NoCandidatesIndicator") == true) {
    				noCandidates = true;
    			}
    			
    			// NoCandidatesIndicator
    			
    			String validAddressIndicator = null;
    			try {
        			validAddressIndicator = xavResponseJSONObject.getString("ValidAddressIndicator");
    			} catch (JSONException e) {
    			}
    			
    			if (validAddress) {
        			JSONObject responseStatusJSONObject = (xavResponseJSONObject.getJSONObject("Response")).getJSONObject("ResponseStatus");

    				String code = responseStatusJSONObject.getString("Code");
        			if ((code == null) || (code.compareTo("1") != 0)) {
        				log.debug(xavResponseJSONObject.toString());

        				addressValidationResults[0] = new Integer(UPSAddress.CODE_IS_MISSING);
        				
        				return addressValidationResults;
        			}

        			JSONObject addressKeyFormatJSONObject = (xavResponseJSONObject.getJSONObject("Candidate")).getJSONObject("AddressKeyFormat");


    				addressValidationResults[0] = new Integer(UPSAddress.GOOD_ADDRESS);

        			UPSAddress[] upsAddresses = new UPSAddress[1];
        			upsAddresses[0] = new UPSAddress();

        			upsAddresses[0].setAddressLine(addressKeyFormatJSONObject.getString("AddressLine"));
    				upsAddresses[0].setPoliticalDivision2(addressKeyFormatJSONObject.getString("PoliticalDivision2"));
    				upsAddresses[0].setPoliticalDivision1(addressKeyFormatJSONObject.getString("PoliticalDivision1"));
    				upsAddresses[0].setPostcodePrimaryLow(addressKeyFormatJSONObject.getString("PostcodePrimaryLow"));
    				upsAddresses[0].setPostcodeExtendedLow(addressKeyFormatJSONObject.getString("PostcodeExtendedLow"));
    				upsAddresses[0].setRegion(addressKeyFormatJSONObject.getString("Region"));
    				upsAddresses[0].setCountryCode(addressKeyFormatJSONObject.getString("CountryCode"));
    				
    				addressValidationResults[1] = upsAddresses;

    				return addressValidationResults;    				
    				
    			} else if (ambiguousAddress) {
        			JSONObject responseStatusJSONObject = (xavResponseJSONObject.getJSONObject("Response")).getJSONObject("ResponseStatus");

    				String code = responseStatusJSONObject.getString("Code");
        			if ((code == null) || (code.compareTo("1") != 0)) {
        				log.debug(xavResponseJSONObject.toString());

        				addressValidationResults[0] = new Integer(UPSAddress.CODE_IS_MISSING);
        		                    				
        				return addressValidationResults;
        			}

    				addressValidationResults[0] = new Integer(UPSAddress.AMBIGUOUS_ADDRESS);

        			// JSONArray addressKeyFormatJSONArray = (xavResponseJSONObject.getJSONObject("Candidate")).getJSONArray("AddressKeyFormat");
        			
        			JSONArray candidateJSONArray = xavResponseJSONObject.getJSONArray("Candidate");
        			UPSAddress[] upsAddresses = new UPSAddress[candidateJSONArray.length()];
        			
        			for (int i = 0; i < candidateJSONArray.length(); i++) {
        				upsAddresses[i] = new UPSAddress();
        		        
                        JSONObject jsonObject = ((JSONObject) candidateJSONArray.get(i)).getJSONObject("AddressKeyFormat");

                        upsAddresses[i].setAddressLine(jsonObject.getString("AddressLine"));
                        upsAddresses[i].setPoliticalDivision2(jsonObject.getString("PoliticalDivision2"));
                        upsAddresses[i].setPoliticalDivision1(jsonObject.getString("PoliticalDivision1"));
                        upsAddresses[i].setPostcodePrimaryLow(jsonObject.getString("PostcodePrimaryLow"));
                        upsAddresses[i].setPostcodeExtendedLow(jsonObject.getString("PostcodeExtendedLow"));
                        upsAddresses[i].setRegion(jsonObject.getString("Region"));
                        upsAddresses[i].setCountryCode(jsonObject.getString("CountryCode"));
					}

    				addressValidationResults[1] = upsAddresses;
    				
    				return addressValidationResults;

    			} else if (noCandidates) { 
    				log.debug(xavResponseJSONObject.toString());

    				addressValidationResults[0] = new Integer(UPSAddress.NO_CANDIDATES);
    				
    				return addressValidationResults;
    			} else { 
    				log.debug(xavResponseJSONObject.toString());

    				addressValidationResults[0] = new Integer(UPSAddress.UNKNOWN_CODE);
    				
    				return addressValidationResults;
    			}
    		} catch (JSONException e) {
    			e.printStackTrace();
				
    			log.debug(xavResponseJSONObject.toString());

				addressValidationResults[0] = new Integer(UPSAddress.JSON_EXCEPTION);
				// addressValidationResults[2] = post.getResponseBody();
				
				return addressValidationResults;
    		}
		} catch (Exception e) {
			e.printStackTrace();

			log.debug(xavResponseJSONObject.toString());

			// addressValidationResults[0] = new Integer(UPSAddress.EXCEPTIION_OCCURRED);
			addressValidationResults[0] = new Integer(UPSAddress.EXCEPTIION_OCCURRED);
			
			return addressValidationResults;
		}
	}	
}

