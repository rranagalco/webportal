
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

public class Catalogitem {
	private static Logger log = Logger.getLogger(Catalogitem.class);

    String list_type;
    String part_num;
    String description;
    String family_code;
    String mfg_name;
    String parent_mfg_code;
    String sales_cat;
    String sales_subcat;
    String series;
    String subfamily_code;

    private static String getQueryFieldSelectionString() {
        return "list_type, part_num, SUBSTR(description, 1, 36) as description_sub,family_code,mfg_name,parent_mfg_code,sales_cat,sales_subcat,SUBSTR(series, 1, 8) as series_sub,subfamily_code";
    }

    public static ArrayList<Catalogitem> buildDAOObjectsFromResultSet(DBConnector dbConnector, String queryString) throws PortalException {
        ArrayList<Catalogitem> al = new ArrayList<Catalogitem>();

		Statement stmt = null;
		ResultSet rs = null;

        try {
			stmt = dbConnector.getStatementWEB();
            rs = stmt.executeQuery(queryString);

			while (rs.next()) {
			    Catalogitem catalogitem = new Catalogitem();

			    catalogitem.setList_type(JDBCUtils.getStringFromResultSet(rs, "list_type"));
			    catalogitem.setPart_num(JDBCUtils.getStringFromResultSet(rs, "part_num"));
			    catalogitem.setDescription(JDBCUtils.getStringFromResultSet(rs, "description_sub"));
                catalogitem.setFamily_code(JDBCUtils.getStringFromResultSet(rs, "family_code"));
                catalogitem.setMfg_name(JDBCUtils.getStringFromResultSet(rs, "mfg_name"));
                catalogitem.setParent_mfg_code(JDBCUtils.getStringFromResultSet(rs, "parent_mfg_code"));
                catalogitem.setSales_cat(JDBCUtils.getStringFromResultSet(rs, "sales_cat"));
                catalogitem.setSales_subcat (JDBCUtils.getStringFromResultSet(rs, "sales_subcat"));
                catalogitem.setSeries(JDBCUtils.getStringFromResultSet(rs, "series_sub"));
                catalogitem.setSubfamily_code(JDBCUtils.getStringFromResultSet(rs, "subfamily_code"));

			    al.add(catalogitem);
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

    public static void printDAOObjects(ArrayList<Catalogitem> al) {
        if (al == null) {
            return;
        }

        for (Iterator<Catalogitem> iterator = al.iterator(); iterator.hasNext();) {
            Catalogitem catalogitem = iterator.next();

			log.debug("list_type      : " + catalogitem.getList_type());
			log.debug("part_num       : " + catalogitem.getPart_num());
			log.debug("description    : " + catalogitem.getDescription());
			log.debug("family_code    : " + catalogitem.getFamily_code());
			log.debug("mfg_name       : " + catalogitem.getMfg_name());
			log.debug("parent_mfg_code: " + catalogitem.getParent_mfg_code());
			log.debug("sales_cat      : " + catalogitem.getSales_cat());
			log.debug("sales_subcat   : " + catalogitem.getSales_subcat());
			log.debug("series         : " + catalogitem.getSeries());
			log.debug("subfamily_code : " + catalogitem.getSubfamily_code());
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ArrayList<Catalogitem> getCatalogitem(DBConnector dbConnector, String part_num) throws PortalException {
    	String replacedPart_num = part_num.replaceAll("'", "''"); 
    	
		String queryString = "select " + getQueryFieldSelectionString() + " from pub.catalogitem " +
							 "where list_type = 'catalog' and part_num = '" + replacedPart_num + "'";

		// log.debug("Catalogitem queryString: " + queryString);

		ArrayList<Catalogitem> al = buildDAOObjectsFromResultSet(dbConnector, queryString);
		// printDAOObjects(al);
		return al;
	}

    // -------------------------------------------------------------------------------------------------------------

    public String getList_type() {
        return list_type;
    }
    public void setList_type(String list_type) {
        this.list_type = list_type;
    }
    public String getPart_num() {
        return part_num;
    }
    public void setPart_num(String part_num) {
        this.part_num = part_num;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getFamily_code() {
        return family_code;
    }
    public void setFamily_code(String family_code) {
        this.family_code = family_code;
    }
    public String getMfg_name() {
        return mfg_name;
    }
    public void setMfg_name(String mfg_name) {
        this.mfg_name = mfg_name;
    }
    public String getParent_mfg_code() {
        return parent_mfg_code;
    }
    public void setParent_mfg_code(String parent_mfg_code) {
        this.parent_mfg_code = parent_mfg_code;
    }
    public String getSales_cat() {
        return sales_cat;
    }
    public void setSales_cat(String sales_cat) {
        this.sales_cat = sales_cat;
    }
    public String getSales_subcat() {
        return sales_subcat;
    }
    public void setSales_subcat(String sales_subcat) {
        this.sales_subcat = sales_subcat;
    }
    public String getSeries() {
        return series;
    }
    public void setSeries(String series) {
        this.series = series;
    }
    public String getSubfamily_code() {
        return subfamily_code;
    }
    public void setSubfamily_code(String subfamily_code) {
        this.subfamily_code = subfamily_code;
    }

}
