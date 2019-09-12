import galco.portal.utils.JDBCUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class ZTest {
	public static ArrayList<String> loadFileIntoArrayList_ChangeToUpperCase_Trim(String filePath) {
		try {
			FileInputStream fstream = new FileInputStream(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String curLine;
			ArrayList<String> arrayList = new ArrayList<String>(130000);

			while ((curLine = br.readLine()) != null) {
				// System.out.println(curLine);
				arrayList.add(curLine.toUpperCase().trim());
			}
			br.close();
			
			// System.out.println (arrayList.size());
			return arrayList;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new ArrayList<String>();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		ArrayList<String> unprocessedSQLs = new ArrayList<String>(100);
		
		unprocessedSQLs.add("a");
		unprocessedSQLs.add("b");
		unprocessedSQLs.add("c");
		
		Iterator<String> iterator = unprocessedSQLs.iterator();
		while (iterator.hasNext()) {
		   String sqlStmt = iterator.next();

		   if (sqlStmt.compareTo("b") == 0) {
			   iterator.remove();        		   
    	   }
		}

		iterator = unprocessedSQLs.iterator();
		while (iterator.hasNext()) {
		   String sqlStmt = iterator.next();
		   System.out.println(sqlStmt);
		}
		System.exit(0);
		
		
		/*

		String po_nbr = "po1234";
		String wds_receiver_nbr = "rcvr1234";
		
		SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
 		String outputFileName = "/reports/galco/log/Z_I_POST_Output_" + yyyyMMddHHmmssSSS.format(new Date()) + "_" + ((int) (Math.random() * 999999)) + ".txt";
 		System.out.println(outputFileName);

		PrintWriter printWriter;
		try {
			printWriter = new PrintWriter("C:\\0Ati\\ZZZ_FasIP302Parm.txt", "UTF-8");
			printWriter.println(po_nbr + "," + wds_receiver_nbr + "," + outputFileName + ",");
			printWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		System.exit(0);

 		*/ 		
 		
 		
		/*
		SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
 		String outputFileName = "/reports/galco/log/Z_I_POST_Output_" + yyyyMMddHHmmssSSS.format(new Date()) + "_" + ((int) (Math.random() * 999999)) + ".txt";
 		System.out.println(outputFileName);
 		System.exit(0);
		*/
		
		
		

 		
		/*
		
		SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
 		Date curDate = new Date();
 		long generatedRequestNo = 1;
 		String sequenceNumber = yyyyMMddHHmmssSSS.format(curDate) + "-" + (generatedRequestNo++) + "-" + ((int) (Math.random() * 999999));
 		System.out.println(sequenceNumber);
 		System.exit(0);
		*/
		
		/*
		ArrayList<String> al1 = loadFileIntoArrayList_ChangeToUpperCase_Trim("C:\\0Ati\\000-Linux-Fascor\\Bins\\Fascor-WDSLocationsInFascor.txt");
		ArrayList<String> al2 = loadFileIntoArrayList_ChangeToUpperCase_Trim("C:\\0Ati\\000-Linux-Fascor\\Bins\\WDS-NormalBins.txt");
		
		for (Iterator<String> iterator = al1.iterator(); iterator.hasNext();) {
			String curLine = (String) iterator.next();
			
			if (al2.indexOf(curLine) < 0) {
				System.out.println(curLine);
			}
		}
		*/
	}

}
