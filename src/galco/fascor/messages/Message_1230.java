package galco.fascor.messages;

import galco.fascor.utils.FasUtils;
import galco.portal.config.Parms;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Message_1230 extends MessageAbs implements Message {
	private static Logger log = Logger.getLogger(Message_1230.class);

	String order_num;
	int line_num;
	String part_num;
	
	// -------------------------------------------------------------------------------------------

	public Message_1230(Connection wdsConnection, String order_num, int line_num, String action) throws SQLException, RuntimeException {
		super("1230", action);
		this.order_num = order_num;
		this.line_num = line_num;

		getDataFromWDS(wdsConnection, order_num, line_num, action);
		fascorMessage = FasUtils.buildFascorMessage(this);
	}

	// -------------------------------------------------------------------------------------------

	public void getDataFromWDS(Connection wdsConnection, String order_num, int line_num, String action) throws SQLException, RuntimeException {


		
		
		
		
		
		
		
		
		
		
		
		
		

		// ======================================================================================
		// qqq
		
		String pocomment = "";
		{
			ArrayList<HashMap<String, Object>> pocommentAL = null;
			pocommentAL = JDBCUtils.runQuery(wdsConnection, "select comment_text[1], comment_text[2], comment_text[3], comment_text[4], comment_text[5], comment_text[6], comment_text[7], comment_text[8], comment_text[9] from pub.pcomment where order_num = '" + order_num + "' and line_num = 0 and prt_on_rcvr = '1' order by comment_num WITH (NOLOCK)");
	    	if ((pocommentAL != null) && (pocommentAL.size() > 0)) {
				for (Iterator<HashMap<String, Object>> iterator = pocommentAL.iterator(); iterator.hasNext();) {
					HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();
					
					for (int i = 1; i <= 9; i++) {
						String line = (String) hashMap.get("comment_text[" + i + "]");
						if (line != null) {
							line = line.trim();
							if (line.length() > 0) {
								pocomment += (line + " ");
							}
						}
					}
				}
			}			
		}

		// ======================================================================================

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		ArrayList<HashMap<String, Object>> pcommentAL = null;
    	
		/*

		// This is for locking
    	// pcommentAL = JDBCUtils.runQuery(wdsConnection, "select comment_text[1], comment_text[2], comment_text[3], comment_text[4], comment_text[5], comment_text[6], comment_text[7], comment_text[8], comment_text[9] from pub.pcomment where order_num = '" + order_num + "' and line_num = " + line_num + " FOR UPDATE WITH (READPAST NOWAIT)");
    	pcommentAL = JDBCUtils.runQuery(wdsConnection, "select comment_text[1], comment_text[2], comment_text[3], comment_text[4], comment_text[5], comment_text[6], comment_text[7], comment_text[8], comment_text[9] from pub.pcomment where order_num = '" + order_num + "' and line_num = " + line_num + " WITH (NOLOCK)");
    	if ((pcommentAL == null) || (pcommentAL.size() == 0)) {
			throw new RuntimeException("pcomment data is missing for order_num: " + order_num + ", line_num: " + line_num);
		}
		
		*/

    	// pcommentAL = JDBCUtils.runQuery(wdsConnection, "select comment_text[1], comment_text[2], comment_text[3], comment_text[4], comment_text[5], comment_text[6], comment_text[7], comment_text[8], comment_text[9] from pub.pcomment where order_num = '" + order_num + "' and line_num = " + line_num + " order by comment_num WITH (READPAST NOWAIT)");
    	pcommentAL = JDBCUtils.runQuery(wdsConnection, "select comment_text[1], comment_text[2], comment_text[3], comment_text[4], comment_text[5], comment_text[6], comment_text[7], comment_text[8], comment_text[9] from pub.pcomment where order_num = '" + order_num + "' and line_num = " + line_num + " order by comment_num WITH (NOLOCK)");
    	if ((pcommentAL == null) || (pcommentAL.size() == 0)) {
			throw new RuntimeException("pcomment data is missing for order_num: " + order_num + ", line_num: " + line_num);
		}
		
		String comment = "";
		for (Iterator<HashMap<String, Object>> iterator = pcommentAL.iterator(); iterator.hasNext();) {
			HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();
			
			for (int i = 1; i <= 9; i++) {
				String line = (String) hashMap.get("comment_text[" + i + "]");
				if (line != null) {
					line = line.trim();
					if (line.length() > 0) {
						comment += (line + " ");
					}
				}
			}
		}

    	FasUtils.print_AL_Of_HMs(pcommentAL);

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		dataHM.put("Message_Type", "1230");

		dataHM.put("Mode", action);

		dataHM.put("Facility_Nbr", Parms.FASCOR_FACILITY_NBR);

		dataHM.put("PO_Nbr", order_num);
		dataHM.put("PO_Line_Nbr", line_num + "");
		
		dataHM.put("Object_ID", "POCMNT");

		
		
		/*
		if (comment.length() > (278 - 10)) {
			comment = comment.substring(0, 268);
		}
		*/
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// ======================================================================================
		// qqq
		
		comment = pocomment + " " + comment + " ";
		comment = comment.replaceAll("\\[", "(");
		comment = comment.replaceAll("\\]", ")");
		
		// ======================================================================================
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		if (comment.length() > 250) {
			comment = comment.substring(0, 250);
		}
		
		
		
		dataHM.put("Object_Text", "COMMENT=[" + comment + "]");
	}

	// -------------------------------------------------------------------------------------------

	public String getKey1() {
		return order_num;
	}
	
    public String getKey2() {
		return (line_num + "");    	
    }
    
	// -------------------------------------------------------------------------------------------

}
