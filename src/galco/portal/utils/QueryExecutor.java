package galco.portal.utils;

import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.wds.dao.Contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

public class QueryExecutor {

	Statement stmt = null;
	ResultSet rs = null;

	public QueryExecutor(DBConnector dbConnector, int wdsSroOrWeb, String query) throws PortalException {
        try {
        	switch (wdsSroOrWeb) {
				case 1:
					stmt = dbConnector.getStatementWDS();
					break;
				case 2:
					stmt = dbConnector.getStatementSRO();
					break;
				case 3:
					stmt = dbConnector.getStatementWEB();
					break;
				default:
					throw new PortalException(new Exception("wdsSroOrWeb should be 1,2, or 3."), PortalException.SEVERITY_LEVEL_1);
			}
            rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException e3) {
			}
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			} catch (SQLException e3) {
			}

			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}		
	}
	
	public void close() throws PortalException {
        try {
			rs.close();
			rs = null;
            stmt.close();
            stmt = null;
		} catch (SQLException e) {
			try {
				if (rs != null) {
					rs.close();
					rs = null;					
				}
			} catch (SQLException e3) {
			}
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;					
				}
			} catch (SQLException e3) {
			}

			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}		
	}
	
	public boolean next() throws PortalException {
        try {
            boolean result = rs.next();
            if (result == false) {
            	close();
            }
            return result;
		} catch (SQLException e) {
			try {
				if (rs != null) {
					rs.close();
					rs = null;					
				}
			} catch (SQLException e3) {
			}
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;					
				}
			} catch (SQLException e3) {
			}

			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}		
	}
	
	public String getString(String col) throws PortalException {
        try {
			return JDBCUtils.getStringFromResultSet(rs, col);
		} catch (PortalException e) {
			try {
				if (rs != null) {
					rs.close();
					rs = null;					
				}
			} catch (SQLException e3) {
			}
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;					
				}
			} catch (SQLException e3) {
			}

			throw e;
		}		
	}	

	public int getInt(String col) throws PortalException {
        try {
			return rs.getInt(col);
		} catch (SQLException e) {
			try {
				if (rs != null) {
					rs.close();
					rs = null;					
				}
			} catch (SQLException e3) {
			}
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;					
				}
			} catch (SQLException e3) {
			}

			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}		
	}	
}
