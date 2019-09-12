package galco.portal.config;

public class Parms {
	public static final long SESSION_DURATION_IN_MILLIS = 1800 * 1000;
 
	// ==================================================================================================
	// ==================================================================================================
	// ==================================================================================================

	public static final int RUN_TYPE;
		public static final int RUN_TYPE_QA = 1;
		public static final int RUN_TYPE_PROD = 2;
		

	// ==================================================================================================
	// ==================================================================================================
	// ==================================================================================================

	public static final String HOST_NAME;
	public static final String SERVER_NAME;

	public static final String FASCOR_FACILITY_NBR;
	public static final String FASCOR_DATABASE_NAME;
	public static final String FASCOR_SERVER_NAME;
	
	public static final String GLUU_SERVER_NAME;
	public static final String GLUU_CLIENT;
	public static final String GLUU_CLIENT_SECRET;

	// ==================================================================================================
	// ==================================================================================================
	// ==================================================================================================

	public static final boolean FASCOR_INTEGRATION_IS_RUNNING = true;
	
	// public static final boolean FASCOR_INTEGRATION_IS_RUNNING = false;

	// --------------------------------------------------------------------------------------------------
	
	static {
		
		
		
		
		
		
		// RUN_TYPE = RUN_TYPE_PROD;
		RUN_TYPE = RUN_TYPE_QA;
		
		
		
		
		
		
		if (RUN_TYPE == RUN_TYPE_PROD) {
			
			// ------------------------------------------------------------------------------------------------------------
			
			HOST_NAME = "www.galco.com";												// prod	web1
			SERVER_NAME = "app1.galco.com";												// prod	app1
			
			FASCOR_FACILITY_NBR = "01";													// prod
			FASCOR_DATABASE_NAME = "DC01GAL";											// prod
			FASCOR_SERVER_NAME = "AZ-Fascor-PDB";										// prod
			
			GLUU_SERVER_NAME = "idm.galco.com";											// prod
			GLUU_CLIENT = "@!C032.849B.2FA5.5E8C!0001!BCB6.4A42!0008!4ACB.875B";		// prod
			GLUU_CLIENT_SECRET = "987654321";											// prod
			
			// ------------------------------------------------------------------------------------------------------------
			
		} else {
			
			// ------------------------------------------------------------------------------------------------------------

			HOST_NAME = "localhost:8080";										// QA
			SERVER_NAME = "lambda-app1.galco.com";

//			HOST_NAME = "lambda-web1.galco.com";										// QA
//			SERVER_NAME = "lambda-app1.galco.com";
//			HOST_NAME = "FascorQA-Web1.galco.com";										// QA
//			SERVER_NAME = "FascorQA-App1.galco.com";									// QA
			
			FASCOR_FACILITY_NBR = "01";													// QA
			FASCOR_DATABASE_NAME = "DC01QA1";											// QA
			FASCOR_SERVER_NAME = "AZ-Fascor-QADB";										// QA
	
			GLUU_SERVER_NAME = "idmdev1.galco.com";										// QA
			GLUU_CLIENT = "@!C059.F8E8.DDA6.4B45!0001!1745.0B55!0008!75AD.8805";		// QA
			GLUU_CLIENT_SECRET = "abc1234";												// QA
			
			// ------------------------------------------------------------------------------------------------------------
			
		}
		
		
	}
	
	
	// --------------------------------------------------------------------------------------------------

	public static boolean FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX = true;
	// public static boolean FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX = false;

	// --------------------------------------------------------------------------------------------------
	
	public static boolean WARN_ABOUT_RUNNING_AGAINST_PRODUCTION = false;

	// ==================================================================================================
	// ==================================================================================================
	// ==================================================================================================

	
	public static final String FASCOR_PROCESSOR_SINGLE_INSTANCE_FILE = "/home/sva0604/ZFascorProcess/FascorProcessor_RunningNow.txt";
	public static final String FASCOR_PROCESSOR_TERMINATION_SIGNAL_FILE = "/home/sva0604/ZFascorProcess/FascorProcessor_ExitNow.txt";


	// ==================================================================================================
	// ==================================================================================================
	// ==================================================================================================


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// OLD STUFF
	
	// public static final String HOST_NAME = "web1dev1.galco.com";
	// public static final String SERVER_NAME = "app1dev1.galco.com";

	// public static final String HOST_NAME = "www.galcotv.com";
	
	// public static final long SESSION_DURATION_IN_MILLIS = 60 * 1000;
	// public static final long SESSION_DURATION_IN_MILLIS = 60 * 1000;
	// public static final long SESSION_DURATION_IN_MILLIS = 6 * 3600 * 1000;

	
	
	/*

	public static final String HOST_NAME = "www.galco.com";
	public static final String SERVER_NAME = "app1.galco.com";	

	public static final String FASCOR_FACILITY_NBR = "01";	
	public static final String FASCOR_DATABASE_NAME = "DC01GAL";	
	public static boolean FASCOR_PROCESSOR_IS_RUNNING_ON_LINUX = true;	
	public static boolean WARN_ABOUT_RUNNING_AGAINST_PRODUCTION = false;
	public static final boolean FASCOR_INTEGRATION_IS_RUNNING = true;
	
	public static final String GLUU_SERVER_NAME = "idm.galco.com";
	public static final String GLUU_CLIENT = "@!C032.849B.2FA5.5E8C!0001!BCB6.4A42!0008!4ACB.875B";
	public static final String GLUU_CLIENT_SECRET = "987654321";
	
	*/
	
}
