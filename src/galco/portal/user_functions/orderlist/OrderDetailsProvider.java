package galco.portal.user_functions.orderlist;

import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.Order;
import galco.portal.wds.dao.UserSession;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class OrderDetailsProvider {
	private static Logger log = Logger.getLogger(OrderDetailsProvider.class);

	public static void returnOrderDetails(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		try {
			UserSession userSession = Utils.checkIfUserIsAuthenticated(request, dbConnector, response);
			if (userSession == null) {
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();

				JSONObject jsonObject = new JSONObject();

				jsonObject.put("session_expired", "YES");
				out.print(jsonObject);
				out.flush();

				log.debug("Session expired");

				return;
			}

			String order_num = request.getParameter("order_num");
			String ship_loc = request.getParameter("ship_loc");

			JSONObject jsonObject = Order.getOitemDetails(request, dbConnector, order_num, ship_loc);

			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			log.debug(jsonObject);
			out.print(jsonObject);
			out.flush();
		} catch (JSONException | IOException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}
	}
}
