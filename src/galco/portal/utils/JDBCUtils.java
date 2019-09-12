package galco.portal.utils;

import galco.portal.config.Parms;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.wds.dao.Contact;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.opencsv.CSVWriter;

public class JDBCUtils {
    private static Logger log = Logger.getLogger(JDBCUtils.class);

	public static int BATCH_SIZE_BATCH = 1000;

    public static ResultSet executeQueryWDS_O(DBConnector dbConnector, String queryString) throws PortalException {
        try {
            Statement stmt = dbConnector.getStatementWDS();
            ResultSet rs = stmt.executeQuery(queryString);
            return rs;
        } catch (Exception e) {
            throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
        }
    }

    public static int executeUpdateWDS(DBConnector dbConnector, String queryString) throws PortalException {
		Statement stmt = null;
        try {
        	// subb 02-09-2017
        	// dbConnector.setIsolationLevel(dbConnector.getConnectionWDS(), Connection.TRANSACTION_READ_COMMITTED);
        	if (dbConnector.isIgnoreIsolationLevel() == false) {
        		// log.debug("subb 02-09-2017 b4" + dbConnector.isIgnoreIsolationLevel());
        		dbConnector.setIsolationLevel(dbConnector.getConnectionWDS(), Connection.TRANSACTION_SERIALIZABLE);        		
        	}
        	
        	// log.debug("subb 02-09-2017 dbConnector.getConnectionWDS().getAutoCommit() " + dbConnector.getConnectionWDS().getAutoCommit());
        	// log.debug("subb 02-09-2017 dbConnector.getConnectionWDS().getTransactionIsolation() " + dbConnector.getConnectionWDS().getTransactionIsolation());

        	stmt = dbConnector.getStatementWDS();

    		// log.debug("subb 02-21-2017 going to execute: " + queryString);
        	
            int result = stmt.executeUpdate(queryString);

    		// log.debug("subb 02-21-2017 successfully executed: " + queryString);
            
            stmt.close();
            stmt = null;

            // subb 02-09-2017
            // dbConnector.getConnectionWDS().commit();
        	            
            // subb 02-09-2017
            // dbConnector.setIsolationLevel(dbConnector.getConnectionWDS(), Connection.TRANSACTION_READ_UNCOMMITTED);
        	if (dbConnector.isIgnoreIsolationLevel() == false) {
        		// log.debug("subb 02-09-2017 aft" + dbConnector.isIgnoreIsolationLevel());        		
        		dbConnector.setIsolationLevel(dbConnector.getConnectionWDS(), Connection.TRANSACTION_READ_UNCOMMITTED);        		
        	}
            
            return result;
        } catch (Exception e) {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e3) {
			}
            throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ResultSet executeQuerySRO_O(DBConnector dbConnector, String queryString) throws PortalException {
        try {
            Statement stmt = dbConnector.getStatementSRO();
            ResultSet rs = stmt.executeQuery(queryString);
            return rs;
        } catch (Exception e) {
            throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
        }
    }

    public static int executeUpdateSRO(DBConnector dbConnector, String queryString) throws PortalException {
		Statement stmt = null;
        try {
            // subb 02-09-2017
        	// dbConnector.setIsolationLevel(dbConnector.getConnectionSRO(), Connection.TRANSACTION_READ_COMMITTED);
        	if (dbConnector.isIgnoreIsolationLevel() == false) {
        		dbConnector.setIsolationLevel(dbConnector.getConnectionSRO(), Connection.TRANSACTION_SERIALIZABLE);        		
        	}
        	
            stmt = dbConnector.getStatementSRO();

    		// log.debug("subb 02-21-2017 going to execute: " + queryString);
            
    		int result = stmt.executeUpdate(queryString);
    		
            // log.debug("subb 02-21-2017 successfully executed: " + queryString);

    		stmt.close();
            stmt = null;
            
            // subb 02-09-2017
        	// dbConnector.setIsolationLevel(dbConnector.getConnectionSRO(), Connection.TRANSACTION_READ_UNCOMMITTED);
        	if (dbConnector.isIgnoreIsolationLevel() == false) {
        		dbConnector.setIsolationLevel(dbConnector.getConnectionSRO(), Connection.TRANSACTION_READ_UNCOMMITTED);        		
        	}
            
            return result;
        } catch (Exception e) {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e3) {
			}
            throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static ResultSet executeQueryWEB_O(DBConnector dbConnector, String queryString) throws PortalException {
        try {
            Statement stmt = dbConnector.getStatementWEB();
            ResultSet rs = stmt.executeQuery(queryString);
            return rs;
        } catch (Exception e) {
            throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
        }
    }

    public static int executeUpdateWEB(DBConnector dbConnector, String queryString) throws PortalException {
		Statement stmt = null;
        try {
            // subb 02-09-2017
        	// dbConnector.setIsolationLevel(dbConnector.getConnectionWEB(), Connection.TRANSACTION_READ_COMMITTED);
        	if (dbConnector.isIgnoreIsolationLevel() == false) {
        		dbConnector.setIsolationLevel(dbConnector.getConnectionWEB(), Connection.TRANSACTION_SERIALIZABLE);        		
        	}        	
        	
            stmt = dbConnector.getStatementWEB();

            // log.debug("subb 02-21-2017 going to execute: " + queryString);
            int result = stmt.executeUpdate(queryString);
            // log.debug("subb 02-21-2017 successfully executed: " + queryString);
            
            stmt.close();
            stmt = null;

            // subb 02-09-2017            
        	// dbConnector.setIsolationLevel(dbConnector.getConnectionWEB(), Connection.TRANSACTION_READ_UNCOMMITTED);
        	if (dbConnector.isIgnoreIsolationLevel() == false) {
        		dbConnector.setIsolationLevel(dbConnector.getConnectionWEB(), Connection.TRANSACTION_READ_UNCOMMITTED);        		
        	}
            
            return result;
        } catch (Exception e) {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e3) {
			}

            throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static String getStringFromResultSet(ResultSet rs, String fieldName) throws PortalException {
        String fieldValue;
        try {
            fieldValue = rs.getString(fieldName);
        } catch (SQLException e) {
            throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
        }
        // log.debug("getStringFromResultSet: " + fieldValue);
        return (fieldValue != null)?fieldValue:"";
    }
    
    public static HashMap<String, Object> runQuery_GetSingleRowOnly(Connection connection, String queryString) throws SQLException {
		ArrayList<HashMap<String, Object>> alOfHMs = JDBCUtils.runQuery(connection, queryString);
    	if ((alOfHMs == null) || (alOfHMs.size() == 0)) {
    		return null;
    	}
    	return alOfHMs.get(0);
    }
    
    public static ArrayList<HashMap<String, Object>> runQuery(Connection connection, String queryString) throws SQLException {
    	Statement stmt = null;
        ResultSet rs = null;
        
        try {
			stmt = connection.createStatement();
            rs = stmt.executeQuery(queryString);

        	ResultSetMetaData md = rs.getMetaData();
        	int columns = md.getColumnCount();
        	
        	ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>(200);
        	
        	while (rs.next()){
        		HashMap<String, Object> row = new HashMap<String, Object>(columns);
        		for (int i = 1; i <= columns; i++) {
        			row.put(md.getColumnName(i), rs.getObject(i));
        			
        			/*
        			if (rs.getObject(i) instanceof String) {
            			System.out.println(md.getColumnName(i) + " String");        				
        			} else if (rs.getObject(i) instanceof Double) {
            			System.out.println(md.getColumnName(i) + " Double");        				
        			} else if (rs.getObject(i) instanceof Number) {
            			System.out.println(md.getColumnName(i) + " Number");        				
        			} else if (rs.getObject(i) instanceof Float) {
            			System.out.println(md.getColumnName(i) + " Float");        				
        			} else if (rs.getObject(i) instanceof Integer) {
            			System.out.println(md.getColumnName(i) + " Integer");
        			} else if (rs.getObject(i) instanceof Boolean) {
            			System.out.println(md.getColumnName(i) + " Boolean");
        			}
        			*/
        		}
        		list.add(row);
        	}

			rs.close();
			rs = null;
            stmt.close();
            stmt = null;

        	return list;
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

			throw e;
		}    	
	}

	// ---------------------------------------------------------------------------------------------------
    
    public static ArrayList<String>[] runQuery_ReturnStrALs(Connection connection, String queryString) throws SQLException {
    	Statement stmt = null;
        ResultSet rs = null;
        
        try {
			stmt = connection.createStatement();
            rs = stmt.executeQuery(queryString);

        	ResultSetMetaData md = rs.getMetaData();
        	int columns = md.getColumnCount();
        	
        	ArrayList<String>[] list = new ArrayList[columns];
    		for (int i = 1; i <= columns; i++) {
    			list[i - 1] = new ArrayList<String>();
    		}
        	
        	while (rs.next()){
        		for (int i = 1; i <= columns; i++) {
        			list[i - 1].add(rs.getObject(i).toString());
        		}
        	}

			rs.close();
			rs = null;
            stmt.close();
            stmt = null;

        	return list;
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

			throw e;
		}    	
    }

	// ---------------------------------------------------------------------------------------------------
    
    public static ArrayList<Object>[] runQuery_ReturnObjALs(Connection connection, String queryString) throws SQLException {
    	Statement stmt = null;
        ResultSet rs = null;
        
        try {
			stmt = connection.createStatement();
            rs = stmt.executeQuery(queryString);

        	ResultSetMetaData md = rs.getMetaData();
        	int columns = md.getColumnCount();
        	
        	ArrayList<Object>[] list = new ArrayList[columns];
    		for (int i = 1; i <= columns; i++) {
    			list[i - 1] = new ArrayList<Object>();
    		}
        	
        	while (rs.next()) {
        		for (int i = 1; i <= columns; i++) {
        			list[i - 1].add(rs.getObject(i));
        		}
        	}

			rs.close();
			rs = null;
            stmt.close();
            stmt = null;

        	return list;
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

			throw e;
		}    	
    }

    // -------------------------------------------------------------------------------------------------------------

    public static Object runQuery_GetSingleValue(Connection connection, String queryString, String fieldName) throws SQLException {
		ArrayList<HashMap<String, Object>> alOfHMs = JDBCUtils.runQuery(connection, queryString);
    	if ((alOfHMs == null) || (alOfHMs.size() == 0)) {
    		return null;
    	}
    	return (alOfHMs.get(0)).get(fieldName);
    }
    
	// ---------------------------------------------------------------------------------------------------

    public static int runUpdateQuery(Connection connection, String queryString) throws SQLException {
    	Statement stmt = null;
        
        try {
			stmt = connection.createStatement();

            int result = stmt.executeUpdate(queryString);
			
            stmt.close();
            stmt = null;

        	return result;
        } catch (SQLException e) {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e3) {
			}

			throw e;
		}    	
    }
   
	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------

    // qqq

    public static class BatchExecParms {
    	public Statement statement;
        public ArrayList<String> currentSqlStatementsInTheBatchAL;
        public ArrayList<String> sqlStatementsThatAreYetToBeAddedToBatchAL;
        
        public BatchExecParms(Connection connection) throws SQLException {
    		statement = connection.createStatement();
        	currentSqlStatementsInTheBatchAL = new ArrayList<String>(3000);
        	sqlStatementsThatAreYetToBeAddedToBatchAL = new ArrayList<String>(20);
        }
        
		public Statement getStatement() {
			return statement;
		}
		public void setStatement(Statement statement) {
			this.statement = statement;
		}
		public ArrayList<String> getCurrentSqlStatementsInTheBatchAL() {
			return currentSqlStatementsInTheBatchAL;
		}
		public void setCurrentSqlStatementsInTheBatchAL(ArrayList<String> currentSqlStatementsInTheBatchAL) {
			this.currentSqlStatementsInTheBatchAL = currentSqlStatementsInTheBatchAL;
		}
		public ArrayList<String> getSqlStatementsThatAreYetToBeAddedToBatchAL() {
			return sqlStatementsThatAreYetToBeAddedToBatchAL;
		}
		public void setSqlStatementsThatAreYetToBeAddedToBatchAL(ArrayList<String> sqlStatementsThatAreYetToBeAddedToBatchAL) {
			this.sqlStatementsThatAreYetToBeAddedToBatchAL = sqlStatementsThatAreYetToBeAddedToBatchAL;
		}
    }
    
    public static boolean UPDATE_WDS_DB_IN_BATCH = false;
    public static boolean UPDATE_FASCOR_DB_IN_BATCH = false;
    
    static HashMap<String, BatchExecParms> batchExecParmsHM = new HashMap<String, BatchExecParms>(10); 
    
    public static final String BATCH_NAME_WDS = "wds", BATCH_NAME_FSCR = "fscr"; 
    
    public static void discardBatch_BATCH(String batchName, Connection connection) {
    	try {
        	BatchExecParms bep = batchExecParmsHM.get(batchName);
        	if (bep != null) {
        		bep.getStatement().clearBatch();
        		bep.getStatement().close();        		
        		batchExecParmsHM.put(batchName, null);   		
        	}			
		} catch (Exception e) {
			log.error(" -1201ZZ- . Exception while discarding batch " + batchName, e);
		}
    }
    
    public static int executeStatementsThatAreInTheBatch_BATCH(String batchName, Connection connection, boolean executeBatch) throws SQLException {
    	BatchExecParms bep = batchExecParmsHM.get(batchName);

        try {
        	if (bep == null) {
        		throw new RuntimeException("BatchExecParms is null for batchName: " + batchName);
        	}
        	
        	for (Iterator<String> iterator = bep.getSqlStatementsThatAreYetToBeAddedToBatchAL().iterator(); iterator.hasNext();) {
				String queryString = (String) iterator.next();

				bep.getStatement().addBatch(queryString);
				bep.getCurrentSqlStatementsInTheBatchAL().add(queryString);
			}
        	bep.setSqlStatementsThatAreYetToBeAddedToBatchAL(new ArrayList<String>(20));
        	
        	if (executeBatch == true) {
        		if (bep.getCurrentSqlStatementsInTheBatchAL().size() > 0) {
            		int[] results = bep.getStatement().executeBatch();
            		
            		int result = 0;
            		for (int i = 0; i < results.length; i++) {
            			if (results[i] == Statement.EXECUTE_FAILED) {
            				log.error("Execution of the following statement failed -1201ZZ-: ");
            				log.error("\t-1201ZZ-\t" + bep.getCurrentSqlStatementsInTheBatchAL().get(i));
            			}
            			
    					result += results[i];
    				}
            		
            		bep.getStatement().close();  
            		// connection.commit();            		
            		batchExecParmsHM.put(batchName, null);

            		return result;
        		} else {
            		bep.getStatement().close();        		
            		batchExecParmsHM.put(batchName, null);

                	return 0;
        		}
        	}
        	
        	return 0;
        } catch (SQLException e) {
        	log.error("Exception in Batch Execution.  -1201ZZ- ", e);
			try {
				if (bep != null) {
	        		bep.getStatement().close();        		
	        		batchExecParmsHM.put(batchName, null);
				}
			} catch (SQLException e3) {
	        	log.error("2nd Exception in Batch Execution.  -1201ZZ- ", e);				
			}

			throw e;
		}    	
    }

    public static void addSqlTo_SqlStatementsThatAreYetToBeAddedToBatchAL_BATCH(String batchName, Connection connection, String sqlStatement) throws SQLException {
    	BatchExecParms bep = batchExecParmsHM.get(batchName);

        try {
        	if (bep == null) {
        		bep = new BatchExecParms(connection);
            	batchExecParmsHM.put(batchName, bep);  
        	}
        	bep.getSqlStatementsThatAreYetToBeAddedToBatchAL().add(sqlStatement);
        } catch (SQLException e) {
        	log.error("Exception in Batch Execution.  -1201ZZ- ", e);
			try {
				if (bep != null) {
	        		bep.getStatement().close();        		
	        		batchExecParmsHM.put(batchName, null);
				}
			} catch (SQLException e3) {
	        	log.error("2nd Exception in Batch Execution.  -1201ZZ- ", e);				
			}

			throw e;
		}    	
    }
   
	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------

    public static boolean UPDATE_FASCOR_DB;
    public static boolean UPDATE_WDS_DB;
    
	public static boolean updatedFascorDB = false, updatedWDSDB = false;

    public static int runUpdateQueryAgainstWDS_Fascor(Connection connection, String queryString) throws SQLException {
    	if (UPDATE_WDS_DB == true) {
    		log.debug("Updating WDS."); 
    		log.debug("Query: " + queryString);

    		int rc;
    		if (JDBCUtils.UPDATE_WDS_DB_IN_BATCH == true) {
        		addSqlTo_SqlStatementsThatAreYetToBeAddedToBatchAL_BATCH(BATCH_NAME_WDS, connection, queryString);
        		rc = 0;
    		} else {
        		rc = runUpdateQuery(connection, queryString);    			
    		}

    		updatedWDSDB = true;
    		return rc;    		
    	} else {
    		log.debug("NOT Updating WDS."); 
    		log.debug("Query: " + queryString);
    		return 1;
    	}
    }
    public static int runUpdateQueryAgainstFascor_Fascor(Connection connection, String queryString) throws SQLException {
    	if (UPDATE_FASCOR_DB == true) {
    		log.debug("Updating Fascor.");  
    		log.debug("Query: " + queryString);
    		
    		int rc;
    		if (JDBCUtils.UPDATE_FASCOR_DB_IN_BATCH == true) {
        		addSqlTo_SqlStatementsThatAreYetToBeAddedToBatchAL_BATCH(BATCH_NAME_FSCR, connection, queryString);
        		rc = 0;
    		} else {
        		rc = runUpdateQuery(connection, queryString);    			
    		}
    		
    		updatedFascorDB = true;
    		return rc;    		
    	} else {
    		log.debug("NOT Updating Fascor.");  
    		log.debug("Query: " + queryString);
    		return 1;
    	}
    }
    
    public static void commitWDSChanges_Fascor(Connection connection)  throws SQLException {
    	if ((UPDATE_WDS_DB == true) && (updatedWDSDB == true)) {
    		if (JDBCUtils.UPDATE_WDS_DB_IN_BATCH == true) {
    			executeStatementsThatAreInTheBatch_BATCH(BATCH_NAME_WDS, connection, false);    			
    		} else {
    			connection.commit();
    		}    		    		
    	}
    	updatedWDSDB = false;
    }
    
    public static void commitFascorChanges_Fascor(Connection connection)  throws SQLException {
    	if ((UPDATE_FASCOR_DB == true) && (updatedFascorDB == true)) {
    		if (JDBCUtils.UPDATE_FASCOR_DB_IN_BATCH == true) {
    			executeStatementsThatAreInTheBatch_BATCH(BATCH_NAME_FSCR, connection, false);
    		} else {
    			connection.commit();
    		}
    	}
    	updatedFascorDB = false;
    }

    public static void rollbackWDSChanges_Fascor(Connection connection)  throws SQLException {
    	if ((UPDATE_WDS_DB == true) && (updatedWDSDB == true)) {
    		if (JDBCUtils.UPDATE_WDS_DB_IN_BATCH == true) {
    			if (batchExecParmsHM.get(BATCH_NAME_WDS) != null) {
    				batchExecParmsHM.get(BATCH_NAME_WDS).setSqlStatementsThatAreYetToBeAddedToBatchAL(new ArrayList<String>(20));
    			}
    		} else {
	    		connection.rollback();    	
    		}
    	}
    	updatedWDSDB = false;
    }
    
    public static void rollbackFascorChanges_Fascor(Connection connection)  throws SQLException {
    	if ((UPDATE_FASCOR_DB == true) && (updatedFascorDB == true)) {
    		if (JDBCUtils.UPDATE_FASCOR_DB_IN_BATCH == true) {
    			if (batchExecParmsHM.get(BATCH_NAME_FSCR) != null) {
    				batchExecParmsHM.get(BATCH_NAME_FSCR).setSqlStatementsThatAreYetToBeAddedToBatchAL(new ArrayList<String>(20));
    			}
    		} else {
    			connection.rollback();    		
    		}
    	}
    	updatedFascorDB = false;
    }
    
	// ---------------------------------------------------------------------------------------------------

	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------

    static Statement stmt_BATCH_O = null;
    static int currentBatchSize_BATCH_O = 0;
    
    public static int closeOutFinalBatch_BATCH_O(Connection connection) throws SQLException {
        try {
        	if (stmt_BATCH_O == null) {
        		return 0;
        	}

        	if (currentBatchSize_BATCH_O > 0) {
        		int[] results = stmt_BATCH_O.executeBatch();
        		stmt_BATCH_O.close();     
        		stmt_BATCH_O = null;
        		currentBatchSize_BATCH_O = 0;
        		
        		int result = 0;
        		for (int i = 0; i < results.length; i++) {
					result += results[i];
				}
        		
        		return result;
        	}

        	return 0;
        } catch (SQLException e) {
			try {
				if (stmt_BATCH_O != null) {
					stmt_BATCH_O.close();
					stmt_BATCH_O = null;
					currentBatchSize_BATCH_O = 0;
				}
			} catch (SQLException e3) {
			}

			throw e;
		}    	
    }

    public static int runUpdateQuery_BATCH_O(Connection connection, String queryString) throws SQLException {
//    	int BATCH_SIZE_BATCH = 1000;
    	
        try {
        	if (stmt_BATCH_O == null) {
        		stmt_BATCH_O = connection.createStatement();
        		currentBatchSize_BATCH_O = 0;        		
        	}

        	stmt_BATCH_O.addBatch(queryString);
        	currentBatchSize_BATCH_O++;
        	
        	if (currentBatchSize_BATCH_O >= BATCH_SIZE_BATCH) {
        		int[] results = stmt_BATCH_O.executeBatch();
        		stmt_BATCH_O.close();     
        		stmt_BATCH_O = null;
        		currentBatchSize_BATCH_O = 0;
        		
        		int result = 0;
        		for (int i = 0; i < results.length; i++) {
					result += results[i];
				}
        		
        		return result;
        	}

        	return 0;
        } catch (SQLException e) {
			try {
				if (stmt_BATCH_O != null) {
					stmt_BATCH_O.close();
					stmt_BATCH_O = null;
					currentBatchSize_BATCH_O = 0;
				}
			} catch (SQLException e3) {
			}

			throw e;
		}    	
    }
   
	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------
    
}
