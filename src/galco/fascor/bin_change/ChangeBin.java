package galco.fascor.bin_change;

import galco.fascor.cache.CacheManager;
import galco.fascor.messages.Message_1220;
import galco.fascor.requests.control.FascorOutboundMessageHandler;
import galco.portal.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class ChangeBin {
	private static Logger log = Logger.getLogger(ChangeBin.class);
	
	// ------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------

	static ArrayList<String> unprocessedSQLs = new ArrayList<String>(100);
	
	public static void changeSingleBin(Connection wdsConnection, Connection sroConnection, String sku, String new_normal_binnum, String new_overstk_binnum, String old_normal_binnum, String old_overstk_binnum) {
		log.debug("BinChange running for part " + sku + ", new " + new_normal_binnum + ", new ovr " + new_overstk_binnum + ", old " + old_normal_binnum + ", old ovr " + old_overstk_binnum);
		
		String errorMessage = "BinChange-Error: For part " + sku + ", couldn't change bin to new " + new_normal_binnum + ", new ovr " + new_overstk_binnum + ", old " + old_normal_binnum + ", old ovr " + old_overstk_binnum + " ";
		String sqlStmt ="";
		
		try {
			String physpart = (String) JDBCUtils.runQuery_GetSingleValue(sroConnection, "select top 1 part_num from pub.phypart where part_num = '" + sku + "' and type = 'F' and date_posted IS NULL WITH (NOLOCK)", "part_num");
			if (physpart != null) {
				log.debug(errorMessage + "as there are frozen parts in this bin.");
				return;
		    }
		} catch (SQLException e) {
			log.debug("Exception occurred, " + errorMessage + ", " + e.getMessage(), e);			
	    	return;
		}
		
		try {
			sqlStmt = "update pub.partloc set normal_binnum = '" + new_normal_binnum + "' , overstk_binnum = '" + new_overstk_binnum + "' where part_num = '" + sku + "'";
			log.debug(sqlStmt);
			JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStmt);
			JDBCUtils.commitWDSChanges_Fascor(wdsConnection);			
		} catch (SQLException e) {
			log.debug("Exception occurred, " + errorMessage + ", " + e.getMessage(), e);			
	    	return;
		}


		try {
			String todayMMDDYY = new SimpleDateFormat("MM/dd/yy").format(new Date());
			String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
			
			sqlStmt =
				"insert into pub.bin_chg " +
						"(part_num, location, new_binnum, old_binnum, new_ovr_binnum, old_ovr_binnum, program, audit_userid, audit_date, audit_time) " +
				"values  (" + 
						  "'" + sku + "', " +
						  "'" + 100 + "', " +
						  "'" + new_normal_binnum + "', " +
						  "'" + old_normal_binnum + "', " +
						  "'" + new_overstk_binnum + "', " +
						  "'" + old_overstk_binnum + "', " +
						  "'BINASGN(SINGLE)', " +
						  "'FASCOR', " +
						  "'" + todayMMDDYY + "', " +
						  "'" + time + "')";
			log.debug(sqlStmt);
			JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStmt);
			JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
		} catch (SQLException e) {
			log.debug("Exception occurred, queing, " + errorMessage + ", " + e.getMessage(), e);
			unprocessedSQLs.add(sqlStmt);
		}

		
		if (new_normal_binnum.compareToIgnoreCase(old_normal_binnum) != 0) {
			if (new_normal_binnum.compareToIgnoreCase("") != 0) {
				try {
					sqlStmt = "update pub.bins set is_primary = '1' , is_overflow = '0' , part_num = '" + sku + "' where location = '100' and binnum = '" + new_normal_binnum + "'";
					log.debug(sqlStmt);
					JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStmt);			
					JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
				} catch (SQLException e) {
					log.debug("Exception occurred, queing, " + errorMessage + ", " + e.getMessage(), e);
					unprocessedSQLs.add(sqlStmt);
				}
			}

			if (old_normal_binnum.compareToIgnoreCase("") != 0) {
				try {
					sqlStmt = "update pub.bins set part_num = '' where location = '100' and binnum = '" + old_normal_binnum + "'";
					log.debug(sqlStmt);
					JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStmt);			
					JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
				} catch (SQLException e) {
					log.debug("Exception occurred, queing, " + errorMessage + ", " + e.getMessage(), e);
					unprocessedSQLs.add(sqlStmt);
				}
			}
		}

		if (new_overstk_binnum.compareToIgnoreCase(old_overstk_binnum) != 0	) {
			if (new_overstk_binnum.compareToIgnoreCase("") != 0) { 
				try {
					sqlStmt = "update pub.bins set is_primary = '0' , is_overflow = '1' , part_num = '" + sku + "' where location = '100' and binnum = '" + new_overstk_binnum + "'";
					log.debug(sqlStmt);				
					JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStmt);			
					JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
				} catch (SQLException e) {
					log.debug("Exception occurred, queing, " + errorMessage + ", " + e.getMessage(), e);
					unprocessedSQLs.add(sqlStmt);
				}
			}

			if (old_overstk_binnum.compareToIgnoreCase("") != 0) { 
				try {
					sqlStmt = "update pub.bins set part_num = '' where location = '100' and binnum = '" + old_overstk_binnum + "'";
					log.debug(sqlStmt);				
					JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStmt);			
					JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
				} catch (SQLException e) {
					log.debug("Exception occurred, queing, " + errorMessage + ", " + e.getMessage(), e);
					unprocessedSQLs.add(sqlStmt);
				}
			}
		}
		

		// --------------------------------------------------------------------------------------------------
		
        ArrayList<HashMap<String, Object>> oitemAL = null;
        try {
			oitemAL = JDBCUtils.runQuery(wdsConnection, "select order_num, line_num, binnum from pub.oitem where part_num = '" + sku + "' and date_closed IS NULL WITH (NOLOCK)");
		} catch (SQLException e1) {
			log.debug("Exception occurred, ignoring, " + errorMessage + ", " + e1.getMessage(), e1);
		}
        if ((oitemAL != null) && (oitemAL.size() > 0)) {
            for (Iterator<HashMap<String, Object>>  iterator2 = oitemAL.iterator(); iterator2.hasNext();) {
                HashMap<String, Object> oitemHM = iterator2.next();
                
                String order_num = (String) oitemHM.get("order_num");
                int line_num = ((Number) oitemHM.get("line_num")).intValue();
                String binnum = (String) oitemHM.get("binnum");
                binnum = (binnum == null)?"":binnum.trim();

    	        if (((binnum.compareToIgnoreCase(old_normal_binnum) == 0		) ||
       	    	     (binnum.compareToIgnoreCase("ZZZZZZZZ") 		== 0		) ||
       	    	     (binnum.compareToIgnoreCase("") 				== 0		)    ) && 
       	    	     (new_normal_binnum.compareToIgnoreCase(old_normal_binnum) 	!= 0 )    ) {
    	        	// long curTime = System.currentTimeMillis();
                    try {
                    	log.debug("BinChange changing oitem order_num " + order_num + " line_num = " + line_num);
                    	
        				sqlStmt = "update pub.oitem set binnum = '" + new_normal_binnum + "' where order_num = '" + order_num + "' and line_num = " + line_num;
        				log.debug(sqlStmt);        				
        				JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStmt);			
        				JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
        			} catch (SQLException e) {
        				// log.debug("Failed to get a lock after Waiting " + (System.currentTimeMillis() - curTime) + " milliseconds for the lock.");
        				log.debug("Exception occurred, queing, " + errorMessage + ", " + e.getMessage(), e);
        				unprocessedSQLs.add(sqlStmt);
        			}
    	        } else if (((binnum.compareToIgnoreCase(old_overstk_binnum) == 0		) ||
              	    	    (binnum.compareToIgnoreCase("ZZZZZZZZ") 		== 0		) ||
              	    	    (binnum.compareToIgnoreCase("") 				== 0		)    ) && 
              	    	   (new_overstk_binnum.compareToIgnoreCase(old_overstk_binnum) 	!= 0 )    ) {
                    try {
                    	log.debug("BinChange changing oitem order_num " + order_num + " line_num = " + line_num);

                    	sqlStmt = "update pub.oitem set binnum = '" + new_overstk_binnum + "' where order_num = '" + order_num + "' and line_num = " + line_num;
        				log.debug(sqlStmt);        				
        				JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStmt);			
        				JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
        			} catch (SQLException e) {
        				log.debug("Exception occurred, queing, " + errorMessage + ", " + e.getMessage(), e);
        				unprocessedSQLs.add(sqlStmt);
        			}
    	        }
            }
        }
		
		// --------------------------------------------------------------------------------------------------
		
        ArrayList<HashMap<String, Object>> pitemAL = null;
        try {
        	pitemAL = JDBCUtils.runQuery(wdsConnection, "select order_num, line_num, binnum from pub.pitem where part_num = '" + sku + "' and date_closed IS NULL WITH (NOLOCK)");
		} catch (SQLException e1) {
			log.debug("Exception occurred, ignoring, " + errorMessage + ", " + e1.getMessage(), e1);
		}
        if ((pitemAL != null) && (pitemAL.size() > 0)) {
            for (Iterator<HashMap<String, Object>>  iterator2 = pitemAL.iterator(); iterator2.hasNext();) {
                HashMap<String, Object> pitemHM = iterator2.next();
                
                String order_num = (String) pitemHM.get("order_num");
                int line_num = ((Number) pitemHM.get("line_num")).intValue();
                String binnum = (String) pitemHM.get("binnum");
                binnum = (binnum == null)?"":binnum.trim();

    	        if (((binnum.compareToIgnoreCase(old_normal_binnum) == 0		) ||
       	    	     (binnum.compareToIgnoreCase("ZZZZZZZZ") 		== 0		) ||
       	    	     (binnum.compareToIgnoreCase("") 				== 0		)    ) && 
       	    	     (new_normal_binnum.compareToIgnoreCase(old_normal_binnum) 	!= 0 )    ) {
                    try {
                    	log.debug("BinChange changing pitem order_num " + order_num + " line_num = " + line_num);
                    	
        				sqlStmt = "update pub.pitem set binnum = '" + new_normal_binnum + "' where order_num = '" + order_num + "' and line_num = " + line_num;
        				log.debug(sqlStmt);        				
        				JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStmt);			
        				JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
        			} catch (SQLException e) {
        				log.debug("Exception occurred, queing, " + errorMessage + ", " + e.getMessage(), e);
        				unprocessedSQLs.add(sqlStmt);
        			}
    	        } else if (((binnum.compareToIgnoreCase(old_overstk_binnum) == 0		) ||
              	    	    (binnum.compareToIgnoreCase("ZZZZZZZZ") 		== 0		) ||
              	    	    (binnum.compareToIgnoreCase("") 				== 0		)    ) && 
              	    	   (new_overstk_binnum.compareToIgnoreCase(old_overstk_binnum) 	!= 0 )    ) {
                    try {
                    	log.debug("BinChange changing pitem order_num " + order_num + " line_num = " + line_num);
                    	
        				sqlStmt = "update pub.pitem set binnum = '" + new_overstk_binnum + "' where order_num = '" + order_num + "' and line_num = " + line_num;
        				log.debug(sqlStmt);        				
        				JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStmt);			
        				JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
        			} catch (SQLException e) {
        				log.debug("Exception occurred, queing, " + errorMessage + ", " + e.getMessage(), e);
        				unprocessedSQLs.add(sqlStmt);
        			}
    	        }
            }
        }

		// --------------------------------------------------------------------------------------------------
		
        ArrayList<HashMap<String, Object>> partdetAL = null;
        try {
        	partdetAL = JDBCUtils.runQuery(wdsConnection, "select order_num, line_num, binnum from pub.partdet where location = '100' and part_num = '" + sku + "' WITH (NOLOCK)");
		} catch (SQLException e1) {
			log.debug("Exception occurred, ignoring, " + errorMessage + ", " + e1.getMessage(), e1);
		}
        if ((partdetAL != null) && (partdetAL.size() > 0)) {
            for (Iterator<HashMap<String, Object>>  iterator2 = partdetAL.iterator(); iterator2.hasNext();) {
                HashMap<String, Object> partdetHM = iterator2.next();
                
                String order_num = (String) partdetHM.get("order_num");
                int line_num = ((Number) partdetHM.get("line_num")).intValue();
                String binnum = (String) partdetHM.get("binnum");
                binnum = (binnum == null)?"":binnum.trim();

    	        if (((binnum.compareToIgnoreCase(old_normal_binnum) == 0		) ||
       	    	     (binnum.compareToIgnoreCase("ZZZZZZZZ") 		== 0		) ||
       	    	     (binnum.compareToIgnoreCase("") 				== 0		)    ) && 
       	    	     (new_normal_binnum.compareToIgnoreCase(old_normal_binnum) 	!= 0 )    ) {
                    try {
                    	log.debug("BinChange changing partdet order_num " + order_num + " line_num = " + line_num);
                    	
        				sqlStmt = "update pub.partdet set binnum = '" + new_normal_binnum + "' where location = '100' and binnum = '" + binnum + "' and part_num = '" + sku + "' and order_num = '" + order_num + "' and line_num = " + line_num;
        				log.debug(sqlStmt);        				
        				JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStmt);			
        				JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
        			} catch (SQLException e) {
        				log.debug("Exception occurred, queing, " + errorMessage + ", " + e.getMessage(), e);
        				unprocessedSQLs.add(sqlStmt);
        			}
    	        } else if (((binnum.compareToIgnoreCase(old_overstk_binnum) == 0		) ||
              	    	    (binnum.compareToIgnoreCase("ZZZZZZZZ") 		== 0		) ||
              	    	    (binnum.compareToIgnoreCase("") 				== 0		)    ) && 
              	    	   (new_overstk_binnum.compareToIgnoreCase(old_overstk_binnum) 	!= 0 )    ) {
                    try {
                    	log.debug("BinChange changing partdet order_num " + order_num + " line_num = " + line_num + " binnum " + binnum + " part_num = " + sku);
                    	
        				// sqlStmt = "update pub.partdet set binnum = '" + new_overstk_binnum + "' where order_num = '" + order_num + "' and line_num = " + line_num;
        				sqlStmt = "update pub.partdet set binnum = '" + new_overstk_binnum + "' where location = '100' and binnum = '" + binnum + "' and part_num = '" + sku + "' and order_num = '" + order_num + "' and line_num = " + line_num;
        				log.debug(sqlStmt);        				
        				JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStmt);			
        				JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
        			} catch (SQLException e) {
        				log.debug("Exception occurred, queing, " + errorMessage + ", " + e.getMessage(), e);
        				unprocessedSQLs.add(sqlStmt);
        			}
    	        }
            }
        }
        
		// --------------------------------------------------------------------------------------------------

        log.debug("BinChange completed for part " + sku + ", new " + new_normal_binnum + ", new ovr " + new_overstk_binnum + ", old " + old_normal_binnum + ", old ovr " + old_overstk_binnum);

		// --------------------------------------------------------------------------------------------------

        /*
        
        try {
			JDBCUtils.rollbackWDSChanges_Fascor(wdsConnection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/

	}

	// ------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------
	
	public static void changeBins(Connection wdsConnection, Connection sroConnection, ArrayList<String>[] partNorOverALArray) {
		// changeSingleBin(wdsConnection, sroConnection, "PC1300-DURA","Z7Z6Z","","BORD123456","BORD654321");
		// changeSingleBin(wdsConnection, sroConnection, "PC1300-DURA", "Z7Z6Z", "", "BORD123456", "BORD654321");
		// changeSingleBin(wdsConnection, sroConnection, "PC1300-DURA","BORD123456","BORD654321","Z7Z6Z","");
		
		if (partNorOverALArray != null) {
			for (int i = 0; i < partNorOverALArray[0].size(); i++) {
				log.debug("ChangeBin - " + partNorOverALArray[0].get(i) + "," + partNorOverALArray[1].get(i) + "," + partNorOverALArray[2].get(i) + "," + partNorOverALArray[3].get(i) + "," + partNorOverALArray[4].get(i));
				changeSingleBin(wdsConnection, sroConnection, partNorOverALArray[0].get(i), partNorOverALArray[1].get(i), partNorOverALArray[2].get(i), partNorOverALArray[3].get(i), partNorOverALArray[4].get(i));
			}
		}
	}

	// ------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------

	static long previousAttemptTime = 0;

	public static void processFailedUpdate(Connection wdsConnection) {
		if (System.currentTimeMillis() < (previousAttemptTime + 600000)) {
			return;
		}
		previousAttemptTime = System.currentTimeMillis();
		
		
		if ((unprocessedSQLs == null) || (unprocessedSQLs.size() == 0)) {
			log.debug("ChangeBin - There are no unprocessed SQL statements.");
		}

		
		Iterator<String> iterator = unprocessedSQLs.iterator();
		while (iterator.hasNext()) {
		   String sqlStmt = iterator.next();
           try {
				log.debug(sqlStmt);
				JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStmt);			
				JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
				
				iterator.remove();
				
				log.debug("Successfully processed previously failed SQL statement. SQL: " + sqlStmt);				
			} catch (SQLException e) {
				log.debug("Exception occurred while running: " + sqlStmt + ", " + e.getMessage(), e);
			}
		}		
	}
	
	// ------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------	

}
