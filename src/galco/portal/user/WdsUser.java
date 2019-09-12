package galco.portal.user;

import org.apache.log4j.Logger;

import galco.portal.wds.dao.Contact;
import galco.portal.wds.dao.Credit;
import galco.portal.wds.dao.Cust;
import galco.portal.wds.dao.Spolicy;

public class WdsUser {
	private static Logger log = Logger.getLogger(WdsUser.class);

	Cust cust;
	Contact contact;
	Credit credit;
	Spolicy spolicy;

	// -------------------------------------------------------------------------------------------------------------

	public WdsUser() {
	}

	public WdsUser(Cust cust, Contact contact, Credit credit, Spolicy spolicy) {
		this.cust = cust;
		this.contact = contact;
		this.credit = credit;
		this.spolicy = spolicy;
	}

	// -------------------------------------------------------------------------------------------------------------

	public Cust getCust() {
		return cust;
	}
	public void setCust(Cust cust) {
		this.cust = cust;
	}
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	public Credit getCredit() {
		return credit;
	}
	public void setCredit(Credit credit) {
		this.credit = credit;
	}
	public Spolicy getSpolicy() {
		return spolicy;
	}
	public void setSpolicy(Spolicy spolicy) {
		this.spolicy = spolicy;
	}
}
