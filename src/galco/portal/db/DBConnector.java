package galco.portal.db;

import galco.portal.config.Parms;
import galco.portal.exception.PortalException;
import galco.portal.utils.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
//import org.apache.tomcat.jdbc.pool.DataSource;
import javax.sql.DataSource;

public class DBConnector {
    private static Logger log = Logger.getLogger(DBConnector.class);

    private static DataSource dsWDS;
    private static DataSource dsSRO;
    private static DataSource dsWEB;    
    static {
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            dsWDS = (DataSource) envCtx.lookup("jdbc/connectWDS");
            dsSRO = (DataSource) envCtx.lookup("jdbc/connectSRO");
            dsWEB = (DataSource) envCtx.lookup("jdbc/connectWWW");      
        } catch (NamingException e) {
            log.error("Exception occurred while initializing. " + e.getMessage());
            log.error("\n\n\n" + ExceptionUtils.getStackTrace(e));
        }
    }
    
    Connection connWDS;
    Connection connSRO;
    Connection connWEB;
    
    boolean ignoreIsolationLevel = false;
    
	ArrayList<Statement> stmtAL = new ArrayList<Statement>(100);
    
    public DBConnector(boolean runningInServlets) throws PortalException {
        try {
            if (runningInServlets) {
                // Allocate and use a connection from the pool
                connWDS = dsWDS.getConnection();
                connSRO = dsSRO.getConnection();
                connWEB = dsWEB.getConnection();

                // log.debug("DBConnector instantiation connWDS: " + connWDS);

                // log.debug("TransactionIsolation b4 setting defaults: " + connWDS.getTransactionIsolation());
                
                setIsolationLevel(connWDS, Connection.TRANSACTION_READ_UNCOMMITTED);
                setIsolationLevel(connSRO, Connection.TRANSACTION_READ_UNCOMMITTED);
                setIsolationLevel(connWEB, Connection.TRANSACTION_READ_UNCOMMITTED);
                                
                // subb 02-09-2017
                connWDS.setAutoCommit(true);
                connSRO.setAutoCommit(true);
                connWEB.setAutoCommit(true);
                setIgnoreIsolationLevel(false);
            } else {
                Class.forName("com.ddtek.jdbc.openedge.OpenEdgeDriver");
                
                connWDS = DriverManager.getConnection("jdbc:datadirect:openedge://" + Parms.SERVER_NAME + ":2011;databaseName=wds-galco","root","");
                // junk connWDS = DriverManager.getConnection("jdbc:datadirect:openedge://" + Parms.SERVER_NAME + ":3011;databaseName=wds-galco","root","");
                
                connSRO = DriverManager.getConnection("jdbc:datadirect:openedge://" + Parms.SERVER_NAME + ":2012;databaseName=sro-galco","root","");
                connWEB = DriverManager.getConnection("jdbc:datadirect:openedge://" + Parms.SERVER_NAME + ":2017;databaseName=web-galco","root","");
            }
        } catch (Exception e) {
            throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
        }       
    }
    
    // ------------------------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------------------------

    public Statement getStatementWDS() throws PortalException {
    	try {
    		/*
    		StackTraceElement[] steArr = Thread.currentThread().getStackTrace();
    		for (int i = 0; i < steArr.length; i++) {
        		log.debug("getStatementWDS StackTraceWDS: " + steArr[i].getClassName() + "." + steArr[i].getMethodName());    		
			}
			*/
    		if (connWDS == null) {
    			throw new PortalException(new Exception("connWDS is null."), PortalException.SEVERITY_LEVEL_1);	
    		}
    		
			Statement stmt = connWDS.createStatement();
			// log.debug("returning statement: " + stmt);			
			stmtAL.add(stmt);
			return stmt;
		} catch (SQLException e) {
            throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);			
		}
    }
    

    public Statement getStatementSRO() throws PortalException {
    	try {
    		/*
    		StackTraceElement[] steArr = Thread.currentThread().getStackTrace();
    		for (int i = 0; i < steArr.length; i++) {
        		log.debug("getStatementSRO StackTraceSRO: " + steArr[i].getClassName() + "." + steArr[i].getMethodName());    		
			}
			*/
    		if (connSRO == null) {
    			throw new PortalException(new Exception("connSRO is null."), PortalException.SEVERITY_LEVEL_1);	
    		}
    		    		
			Statement stmt = connSRO.createStatement();
			// log.debug("returning statement: " + stmt);			
			stmtAL.add(stmt);
			return stmt;
		} catch (SQLException e) {
            throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);			
		}
    }

    public Statement getStatementWEB() throws PortalException {
    	try {
    		/*
    		StackTraceElement[] steArr = Thread.currentThread().getStackTrace();
    		for (int i = 0; i < steArr.length; i++) {
        		log.debug("getStatementWEB StackTraceWEB: " + steArr[i].getClassName() + "." + steArr[i].getMethodName());    		
			}
			*/
    		if (connWEB == null) {
    			throw new PortalException(new Exception("connWEB is null."), PortalException.SEVERITY_LEVEL_1);	
    		}    		
    		
			Statement stmt = connWEB.createStatement();
			// log.debug("returning statement: " + stmt);			
			stmtAL.add(stmt);
			return stmt;
		} catch (SQLException e) {
            throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);			
		}
    }

    // ------------------------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------------------------
    
    public void closeConnections() throws PortalException {
        try {
            // log.debug("Closing connections.");
            
            /*
            for (Iterator<Statement> iterator = stmtAL.iterator(); iterator.hasNext();) {
				Statement statement = (Statement) iterator.next();
				if (statement.isClosed() == false) {
					log.debug("Didn't close statement: " + statement);
					statement.close();
				}
			}
			*/
            

        	//log.debug("Subb 02-01-2017. closeConnections. Closing. Connection:" + connWDS);    	
            connWDS.close();
        	//log.debug("Subb 02-01-2017. closeConnections. Closed. Connection:" + connWDS);    	

        	//log.debug("Subb 02-01-2017. closeConnections. Closing. Connection:" + connSRO);    	
        	connSRO.close();
        	//log.debug("Subb 02-01-2017. closeConnections. Closed. Connection:" + connSRO);    	

        	//log.debug("Subb 02-01-2017. closeConnections. Closing. Connection:" + connWEB);    	
        	connWEB.close();            
        	//log.debug("Subb 02-01-2017. closeConnections. Closed. Connection:" + connWEB);              
            
            connWDS = null;
            connSRO = null;
            connWEB = null;            
        } catch (Exception e) {
            throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
        }       
    }

    public Connection getConnectionWDS() {
        // log.debug("DBConnector connWDS getConnectionWDS(): " + connWDS);
    	// log.debug("Subb 02-01-2017. getConnectionWDS. Connection:" + connWDS);    	
        return connWDS;
    }
    
    public Connection getConnectionSRO() {
    	// log.debug("Subb 02-01-2017. getConnectionSRO. Connection:" + connSRO);    	
        return connSRO;
    }
    
    public Connection getConnectionWEB() {
    	// log.debug("Subb 02-01-2017. getConnectionWEB. Connection:" + connWEB);    	
        return connWEB;
    }   
    
    public void setIsolationLevel(Connection conn, int isolationLevel) throws PortalException {
        if ((isolationLevel != Connection.TRANSACTION_NONE              ) &&
            (isolationLevel != Connection.TRANSACTION_READ_COMMITTED    ) &&
            (isolationLevel != Connection.TRANSACTION_READ_UNCOMMITTED  ) &&
            
            
            (isolationLevel != Connection.TRANSACTION_SERIALIZABLE  	) &&
                        
            
            (isolationLevel != Connection.TRANSACTION_REPEATABLE_READ   )    ) {
            throw new PortalException(new Exception("Invalid Isolation Level"), PortalException.SEVERITY_LEVEL_1);
        }

        try {
            conn.setAutoCommit(true);
        } catch (SQLException e) {
        	log.debug("Subb 02-01-2017 failing to set AutoCommit. Connection:" + conn);
            throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);         
        }

        try {
            conn.setTransactionIsolation(isolationLevel);
        } catch (SQLException e) {
        	log.debug("Failing to set TransactionIsolation.");        	
            log.error("Error Code:" + e.getErrorCode());
            log.error("Error occurred.", e);            
            // if (e.getErrorCode() == 13742) {
                try {
                    conn.commit();
                    conn.setTransactionIsolation(isolationLevel);                   
                } catch (SQLException e1) {
                    log.error("Error while trying to commit, code:" + e1.getErrorCode());
                    throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
                }
                            
            // }
        }
    }

    public boolean isIgnoreIsolationLevel() {
		return ignoreIsolationLevel;
	}

	public void setIgnoreIsolationLevel(boolean ignoreIsolationLevel) {
		this.ignoreIsolationLevel = ignoreIsolationLevel;
	}

}
