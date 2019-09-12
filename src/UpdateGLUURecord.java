import net.sf.jasperreports.web.servlets.Controller;
import galco.portal.control.ControlServlet;
import galco.portal.exception.PortalException;
import galco.portal.user.GluuUser;

public class UpdateGLUURecord {
	public static void main(String[] args) {
		try {
			ControlServlet.installTM();
			
			// String oldUserID = "david.quin@powertechnicaragua.com";
			// String newUserID = "david.quin@live.com";
			// String newPassword = "Dsgqy2017*";
			

			// NEXT TIME, CHECK IF EmailConfirmed IS GETTING SET CORRECTLY OR NOT
			// NEXT TIME, CHECK IF EmailConfirmed IS GETTING SET CORRECTLY OR NOT
			// NEXT TIME, CHECK IF EmailConfirmed IS GETTING SET CORRECTLY OR NOT
			// NEXT TIME, CHECK IF EmailConfirmed IS GETTING SET CORRECTLY OR NOT
			// NEXT TIME, CHECK IF EmailConfirmed IS GETTING SET CORRECTLY OR NOT
			// NEXT TIME, CHECK IF EmailConfirmed IS GETTING SET CORRECTLY OR NOT
			
			
			
			
			String oldUserID = "todd.irvin@akzonobel.com";
			String newUserID = "rtirvin63@aol.com";
			String newPassword = "irvin157170";
			

			
			
			
			
			GluuUser gluuUser = GluuUser.retrieveUserInfoFromGluu(oldUserID, "", true);
			
			GluuUser gluuUserNew = new GluuUser(
												"", 
												newUserID, 
												newPassword,
												gluuUser.getFirstName(), gluuUser.getLastName(),
												newUserID,
												gluuUser.getPhoneWork(), gluuUser.getPhoneWorkExt(),
												gluuUser.getPhoneCompany(), gluuUser.getPhoneCompanyExt(),
												gluuUser.getPhoneCell(), gluuUser.getPhoneCellExt(),
												gluuUser.getFax(),
												gluuUser.getStreetAddress(), gluuUser.getLocality(), gluuUser.getPostalCode(),
												gluuUser.getRegion(), gluuUser.getCountry(),
												"order,quote,wishlist,reorder,convertquote",
												gluuUser.getCustNum(), gluuUser.getContNo());
			gluuUserNew.setEmailConfirmed(gluuUser.getEmailConfirmed());
			gluuUserNew.setDfltAddrBill(gluuUser.getDfltAddrBill());
			gluuUserNew.setDfltAddrShip(gluuUser.getDfltAddrShip());
			gluuUserNew.persist();
			
			
			
			/*
			gluuUser.setInum(null);
			gluuUser.setUserName("sati333@galco.com");
			gluuUser.setEmail("sati333@galco.com");
			gluuUser.persist();
			*/
		} catch (Exception e) {
			e.printStackTrace();
		} catch (PortalException e2) {
			e2.getE().printStackTrace();
		}
		System.exit(0);		
	}
}
