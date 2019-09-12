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

public class Part {
	private static Logger log = Logger.getLogger(Part.class);

    String part_num;
    String family_code;
    String subfamily_code;
    String series;
    String description;
    String description2;
    String description3;
    String sales_subcat;
    String vendor_abv;
    String billing_unit;
    String altbill_unit;    

    private static String getQueryFieldSelectionString() {
        return "part_num,family_code,subfamily_code,series,description,description2,description3,sales_subcat,vendor_abv,billing_unit,altbill_unit";
    }

    public static ArrayList<Part> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Part> al = new ArrayList<Part>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWDS();
            rs = stmt.executeQuery(queryString);

			while (rs.next()) {
			    Part part = new Part();

			    part.setPart_num(JDBCUtils.getStringFromResultSet(rs, "part_num"));
			    part.setFamily_code(JDBCUtils.getStringFromResultSet(rs, "family_code"));
			    part.setSubfamily_code(JDBCUtils.getStringFromResultSet(rs, "subfamily_code"));
			    part.setSeries(JDBCUtils.getStringFromResultSet(rs, "series"));
			    part.setDescription(JDBCUtils.getStringFromResultSet(rs, "description"));
			    part.setDescription2(JDBCUtils.getStringFromResultSet(rs, "description2"));
			    part.setDescription3(JDBCUtils.getStringFromResultSet(rs, "description3"));
			    part.setSales_subcat(JDBCUtils.getStringFromResultSet(rs, "sales_subcat"));
			    part.setVendor_abv(JDBCUtils.getStringFromResultSet(rs, "vendor_abv"));
			    part.setBilling_unit(JDBCUtils.getStringFromResultSet(rs, "billing_unit"));
			    part.setAltbill_unit(JDBCUtils.getStringFromResultSet(rs, "altbill_unit"));
			    
			    al.add(part);
			}

			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		} catch (SQLException e) {
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

    public static void printDAOObjects(ArrayList<Part> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Part> iterator = al.iterator(); iterator.hasNext();) {
            Part part = iterator.next();

			log.debug("part_num       : " + part.getPart_num());
			log.debug("family_code    : " + part.getFamily_code());
			log.debug("subfamily_code : " + part.getSubfamily_code());
			log.debug("series         : " + part.getSeries());
			log.debug("description    : " + part.getDescription());
			log.debug("description2   : " + part.getDescription2());
			log.debug("description3   : " + part.getDescription3());
			log.debug("sales_subcat   : " + part.getSales_subcat());
			log.debug("vendor_abv     : " + part.getVendor_abv());
			log.debug("billing_unit   : " + part.getBilling_unit());
			log.debug("altbill_unit   : " + part.getAltbill_unit());			
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Part> getPart(DBConnector dbConnector, String part_num) throws PortalException {
    	String replacedPart_num = part_num.replaceAll("'", "''"); 
    	
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.part " +
							 "where part_num = '" + replacedPart_num + "'";

		// log.debug("Part queryString: " + queryString);
		
		ArrayList<Part> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

    public static ArrayList<Part> getPart(DBConnector dbConnector, String sales_subcat, String series) throws PortalException {
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.part " +
				             " where sales_subcat = '" + sales_subcat + "' " +
		                     "   and series = '" + series + "' " +
							 "   and family_code <> ''";
		ArrayList<Part> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

    // -------------------------------------------------------------------------------------------------------------

    public String getPart_num() {
        return part_num;
    }
    public void setPart_num(String part_num) {
        this.part_num = part_num;
    }
    public String getFamily_code() {
        return family_code;
    }
    public void setFamily_code(String family_code) {
        this.family_code = family_code;
    }
    public String getSubfamily_code() {
        return subfamily_code;
    }
    public void setSubfamily_code(String subfamily_code) {
        this.subfamily_code = subfamily_code;
    }
    public String getSeries() {
        return series;
    }
    public void setSeries(String series) {
        this.series = series;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription2() {
        return description2;
    }
    public void setDescription2(String description2) {
        this.description2 = description2;
    }
    public String getDescription3() {
        return description3;
    }
    public void setDescription3(String description3) {
        this.description3 = description3;
    }
    public String getSales_subcat() {
        return sales_subcat;
    }
    public void setSales_subcat(String sales_subcat) {
        this.sales_subcat = sales_subcat;
    }
    public String getVendor_abv() {
        return vendor_abv;
    }
    public void setVendor_abv(String vendor_abv) {
        this.vendor_abv = vendor_abv;
    }

    public String getBilling_unit() {
        return billing_unit;
    }
    public void setBilling_unit(String billing_unit) {
        this.billing_unit = billing_unit;
    }
    
    public String getAltbill_unit() {
        return altbill_unit;
    }
    public void setAltbill_unit(String altbill_unit) {
        this.altbill_unit = altbill_unit;
    }
    
}
