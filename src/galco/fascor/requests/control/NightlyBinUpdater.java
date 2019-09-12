package galco.fascor.requests.control;

import galco.fascor.bin_change.ChangeBin;
import galco.fascor.messages.Message_1420;
import galco.fascor.process.FascorProcessor;
import galco.fascor.utils.FasUtils;
import galco.portal.config.Parms;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class NightlyBinUpdater {
	private static Logger log = Logger.getLogger(NightlyBinUpdater.class);

	static String lastProcessedDate = "";
	static long previousAttemptTime = 0;

	public static void updateBins(Connection wdsConnection, Connection sroConnection, Connection sqlServerConnection) {
		// Danger
		String todayCCYYMMDD = new SimpleDateFormat("yyyyMMdd").format(new Date());
		// String todayCCYYMMDD = "20190712";


		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

		if (System.currentTimeMillis() < (previousAttemptTime + 600000)) {
			// log.debug("It's not yet time to update on-hand quantities. - Millis");
			return;
		}
		previousAttemptTime = System.currentTimeMillis();

		log.debug("Checking if we need to update Bins, " + todayCCYYMMDD + " " + hour + " " + System.currentTimeMillis());
		
		if (lastProcessedDate.compareTo(todayCCYYMMDD) == 0) {
			// log.debug("It's not yet time to update on-hand quantities. - Date");
			previousAttemptTime = System.currentTimeMillis();
			return;
		}
		// Danger
		// if (hour == 0) {
		if ((hour != 21) && (hour != 22)) {
			// log.debug("It's not yet time to update bins. - Hour, " + hour + ", is not 21 or 22");
			previousAttemptTime = System.currentTimeMillis();
			return;
		}

		
		// ----------------------------------------------------------------------------------------------------------
		// ----------------------------------------------------------------------------------------------------------

		
		try {
			log.debug("Starting nightly update of bins.");
			
    		ArrayList<HashMap<String, Object>> partlocAL = null;
            try {
            	partlocAL = JDBCUtils.runQuery(wdsConnection, "select part_num, normal_binnum, overstk_binnum from pub.partloc where (qty_onhand = 0) and (normal_binnum like 'BORD%' or overstk_binnum like 'BORD%') WITH (NOLOCK)");
                
            	if ((partlocAL != null) && (partlocAL.size() > 0)) {
                    for (Iterator<HashMap<String, Object>>  iterator2 = partlocAL.iterator(); iterator2.hasNext();) {
                        HashMap<String, Object> partlocHM = iterator2.next();
                        
                        String part_num = (String) partlocHM.get("part_num");
                        String old_normal_binnum = (String) partlocHM.get("normal_binnum");
                        String old_overstk_binnum = (String) partlocHM.get("overstk_binnum");

                        old_normal_binnum = (old_normal_binnum == null)?"":old_normal_binnum.trim();
                        old_overstk_binnum = (old_overstk_binnum == null)?"":old_overstk_binnum.trim();
                        
                        String new_normal_binnum = "";
                        String new_overstk_binnum = "";

                        if ((old_normal_binnum.length() >= 4									) && 
                        	(old_normal_binnum.substring(0,4).compareToIgnoreCase("BORD") == 0	)    ) {
                        	new_normal_binnum = "";
                        } else {
                        	new_normal_binnum = old_normal_binnum;
                        }

                        if ((old_overstk_binnum.length() >= 4									) && 
                        	(old_overstk_binnum.substring(0,4).compareToIgnoreCase("BORD") == 0	)    ) {
                        	new_overstk_binnum = "";
                        } else {
                        	new_overstk_binnum = old_overstk_binnum;
                        }
                        
                        try {
                            ChangeBin.changeSingleBin(wdsConnection, sroConnection, part_num, new_normal_binnum, new_overstk_binnum, old_normal_binnum, old_overstk_binnum);
    					} catch (Exception e) {
    						log.debug(e);
    					}
                    }
                    
        			log.debug("Successfully updated bins.");
                }
    		} catch (SQLException e1) {
    			log.debug("Exception occurred while nightly updating bins." + e1.getMessage(), e1);
    		}
		} catch (Exception e) {
			String exceptionMessage = e.getMessage();
			if (e instanceof SQLException) {
				String sqlExceptionInfo = FasUtils.getSQLExceptionInfo((SQLException) e);
				log.debug(sqlExceptionInfo);
				exceptionMessage = sqlExceptionInfo;
			} else {
				log.debug(e);
			}

			String msg = "Error occurred while trying to update Bin Locations. Exception message: " + exceptionMessage;
			log.debug(msg);
			galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Problem in FASCOR Batch Process of " + Parms.HOST_NAME, msg);
		}
		

		// ----------------------------------------------------------------------------------------------------------
		// ----------------------------------------------------------------------------------------------------------
		
		
		lastProcessedDate = todayCCYYMMDD;
		previousAttemptTime = 0;

		
		// ----------------------------------------------------------------------------------------------------------
		// ----------------------------------------------------------------------------------------------------------

		
	}

	// ----------------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------------

	public static void updateBinsO(Connection wdsConnection, Connection sroConnection, Connection sqlServerConnection) {
		// Danger
		String todayCCYYMMDD = new SimpleDateFormat("yyyyMMdd").format(new Date());
		// String todayCCYYMMDD = "20190712";


		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

		if (System.currentTimeMillis() < (previousAttemptTime + 600000)) {
			// log.debug("It's not yet time to update on-hand quantities. - Millis");
			return;
		}
		previousAttemptTime = System.currentTimeMillis();

		log.debug("Checking if we need to update Bins, " + todayCCYYMMDD + " " + hour + " " + System.currentTimeMillis());
		
		if (lastProcessedDate.compareTo(todayCCYYMMDD) == 0) {
			// log.debug("It's not yet time to update on-hand quantities. - Date");
			previousAttemptTime = System.currentTimeMillis();
			return;
		}
		// Danger
		// if (hour == 0) {
		if ((hour != 21) && (hour != 22)) {
			// log.debug("It's not yet time to update bins. - Hour, " + hour + ", is not 21 or 22");
			previousAttemptTime = System.currentTimeMillis();
			return;
		}

		
		// ----------------------------------------------------------------------------------------------------------
		// ----------------------------------------------------------------------------------------------------------

		
		try {
			log.debug("Starting nightly update of bins.");
			
			String outputFileName = "/reports/galco/log/FasNightlyBinUpd.txt";
			
			int rc = FasUtils.executeCommandLine("/apps/wds/GALCO/batch/FasBUP", 900000, outputFileName);

			String binUpdOutput = FasUtils.readFileContentsIntoAString(outputFileName);
			log.debug("Nightly bin update process output:");
			log.debug(binUpdOutput);			
			new File(outputFileName).delete();

			log.debug("Successfully updated bins.");
		} catch (Exception e) {
			String exceptionMessage = e.getMessage();
			if (e instanceof SQLException) {
				String sqlExceptionInfo = FasUtils.getSQLExceptionInfo((SQLException) e);
				log.debug(sqlExceptionInfo);
				exceptionMessage = sqlExceptionInfo;
			} else {
				log.debug(e);
			}

			String msg = "Error occurred while trying to update Bin Locations. Exception message: " + exceptionMessage;
			log.debug(msg);
			galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Problem in FASCOR Batch Process of " + Parms.HOST_NAME, msg);
		}
		

		// ----------------------------------------------------------------------------------------------------------
		// ----------------------------------------------------------------------------------------------------------
		
		
		lastProcessedDate = todayCCYYMMDD;
		previousAttemptTime = 0;

		
		// ----------------------------------------------------------------------------------------------------------
		// ----------------------------------------------------------------------------------------------------------

		
	}

	// ----------------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------------

	public static void main(String[] args) {
		DBConnector dbConnector8 = null;
		try {
			if (FascorProcessor.getDBConnections() == true) {

				FascorProcessor.closeDBConnections();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (FascorProcessor.dbConnector8 != null) {
				try {
					FascorProcessor.closeDBConnections();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}
}

