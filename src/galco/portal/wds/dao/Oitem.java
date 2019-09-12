package galco.portal.wds.dao;

import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class Oitem {
	private static Logger log = Logger.getLogger(Oitem.class);

	String order_num;
	String part_num;
	String sales_subcat;
	String billing_unit;
	int line_num;
	int priceper_qty;
	int qty_bal;
	int qty_cancel;
	int qty_commit;
	int qty_ord;
	int qty_shipped;
	float unit_price;

    // -------------------------------------------------------------------------------------------------------------

	public Oitem() {
		super();
	}

	public Oitem(String order_num, String part_num, String sales_subcat,
			String billing_unit, int line_num, int priceper_qty, int qty_bal,
			int qty_cancel, int qty_commit, int qty_ord, int qty_shipped,
			float unit_price) {
		super();
		this.order_num = order_num;
		this.part_num = part_num;
		this.sales_subcat = sales_subcat;
		this.billing_unit = billing_unit;
		this.line_num = line_num;
		this.priceper_qty = priceper_qty;
		this.qty_bal = qty_bal;
		this.qty_cancel = qty_cancel;
		this.qty_commit = qty_commit;
		this.qty_ord = qty_ord;
		this.qty_shipped = qty_shipped;
		this.unit_price = unit_price;
	}

    // -------------------------------------------------------------------------------------------------------------

    private static String getQueryFieldSelectionString() {
        return "order_num, part_num, sales_subcat, billing_unit, line_num, priceper_qty, qty_bal, qty_cancel, qty_commit, qty_ord, qty_shipped, unit_price";
    }

    public static ArrayList<Oitem> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Oitem> al = new ArrayList<Oitem>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

			while (rs.next()) {
			    Oitem oitem = new Oitem();

			    oitem.setOrder_num(JDBCUtils.getStringFromResultSet(rs, "order_num"));
			    oitem.setPart_num(JDBCUtils.getStringFromResultSet(rs, "part_num"));
			    oitem.setSales_subcat(JDBCUtils.getStringFromResultSet(rs, "sales_subcat"));
			    oitem.setBilling_unit(JDBCUtils.getStringFromResultSet(rs, "billing_unit"));
			    oitem.setLine_num(new Integer(JDBCUtils.getStringFromResultSet(rs, "line_num")));
			    oitem.setPriceper_qty(new Integer(JDBCUtils.getStringFromResultSet(rs, "priceper_qty")));
			    oitem.setQty_bal(new Integer(JDBCUtils.getStringFromResultSet(rs, "qty_bal")));
			    oitem.setQty_cancel(new Integer(JDBCUtils.getStringFromResultSet(rs, "qty_cancel")));
			    oitem.setQty_commit(new Integer(JDBCUtils.getStringFromResultSet(rs, "qty_commit")));
			    oitem.setQty_ord(new Integer(JDBCUtils.getStringFromResultSet(rs, "qty_ord")));
			    oitem.setQty_shipped(new Integer(JDBCUtils.getStringFromResultSet(rs, "qty_shipped")));
			    oitem.setUnit_price(new Float(JDBCUtils.getStringFromResultSet(rs, "unit_price")));

			    al.add(oitem);
			}

			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		} catch (NumberFormatException | SQLException e) {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e3) {
			}
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e3) {
			}

			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}

        if (al.size() == 0) {
            return null;
        }

        return al;
    }

    public static void printDAOObjects(ArrayList<Oitem> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Oitem> iterator = al.iterator(); iterator.hasNext();) {
            Oitem oitem = iterator.next();

			log.debug("order_num    : " + oitem.getOrder_num());
			log.debug("part_num     : " + oitem.getPart_num());
			log.debug("sales_subcat : " + oitem.getSales_subcat());
			log.debug("billing_unit : " + oitem.getBilling_unit());
			log.debug("line_num     : " + oitem.getLine_num());
			log.debug("priceper_qty : " + oitem.getPriceper_qty());
			log.debug("qty_bal      : " + oitem.getQty_bal());
			log.debug("qty_cancel   : " + oitem.getQty_cancel());
			log.debug("qty_commit   : " + oitem.getQty_commit());
			log.debug("qty_ord      : " + oitem.getQty_ord());
			log.debug("qty_shipped  : " + oitem.getQty_shipped());
			log.debug("unit_price   : " + oitem.getUnit_price());
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Oitem> getOitemsOfAnOrder(DBConnector dbConnector, String order_num) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.oitem " +
							 "where order_num = '" + order_num + "'";
		ArrayList<Oitem> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

    // -------------------------------------------------------------------------------------------------------------

    public String getOrder_num() {
		return order_num;
	}
	public void setOrder_num(String order_num) {
		this.order_num = order_num;
	}
	public String getPart_num() {
		return part_num;
	}
	public void setPart_num(String part_num) {
		this.part_num = part_num;
	}
	public String getSales_subcat() {
		return sales_subcat;
	}
	public void setSales_subcat(String sales_subcat) {
		this.sales_subcat = sales_subcat;
	}
	public String getBilling_unit() {
		return billing_unit;
	}
	public void setBilling_unit(String billing_unit) {
		this.billing_unit = billing_unit;
	}
	public int getLine_num() {
		return line_num;
	}
	public void setLine_num(int line_num) {
		this.line_num = line_num;
	}
	public int getPriceper_qty() {
		return priceper_qty;
	}
	public void setPriceper_qty(int priceper_qty) {
		this.priceper_qty = priceper_qty;
	}
	public int getQty_bal() {
		return qty_bal;
	}
	public void setQty_bal(int qty_bal) {
		this.qty_bal = qty_bal;
	}
	public int getQty_cancel() {
		return qty_cancel;
	}
	public void setQty_cancel(int qty_cancel) {
		this.qty_cancel = qty_cancel;
	}
	public int getQty_commit() {
		return qty_commit;
	}
	public void setQty_commit(int qty_commit) {
		this.qty_commit = qty_commit;
	}
	public int getQty_ord() {
		return qty_ord;
	}
	public void setQty_ord(int qty_ord) {
		this.qty_ord = qty_ord;
	}
	public int getQty_shipped() {
		return qty_shipped;
	}
	public void setQty_shipped(int qty_shipped) {
		this.qty_shipped = qty_shipped;
	}
	public float getUnit_price() {
		return unit_price;
	}
	public void setUnit_price(float unit_price) {
		this.unit_price = unit_price;
	}

}
