package galco.portal.user_functions.orderlist;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.Order;
import galco.portal.wds.dao.UserSession;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class SubsequentOrderFetcher {
	private static Logger log = Logger.getLogger(SubsequentOrderFetcher.class);

	public static void returnSubsequentOrders(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		try {
			PrintWriter printWriter = response.getWriter();
			response.setContentType("application/json");
			JSONObject mainJsonObject = new JSONObject();
			
			
			UserSession userSession = Utils.checkIfUserIsAuthenticated(request, dbConnector, response);
			if (userSession == null) {
				JSONObject jsonObject = new JSONObject();

				jsonObject.put("session_expired", "YES");
				printWriter.print(jsonObject);
				printWriter.flush();

				log.debug("Session expired");

				return;
			}
			{
				mainJsonObject.put("session_expired", "NO");
			}			


			String lastOrderDisplayed = request.getParameter("lastOrderDisplayed");
			String dateOfLastOrderDisplayed = request.getParameter("dateOfLastOrderDisplayed");
			String lastQuoteDisplayed = request.getParameter("lastQuoteDisplayed");
			String dateOfLastQuoteDisplayed = request.getParameter("dateOfLastQuoteDisplayed");
			String cust_num = request.getParameter("cust_num");
			int cont_no = new Integer(request.getParameter("cont_no")).intValue();
			boolean returnQuotes = (request.getParameter("quo").compareTo("Y") == 0);

			log.debug("lastOrderDisplayed: " + lastOrderDisplayed);
			log.debug("lastQuoteDisplayed: " + lastQuoteDisplayed);
			log.debug("returnQuotes      : " + returnQuotes);

			ArrayList<Order> ordersAL = Order.fetchNextBatchOfOrders(dbConnector, cust_num, cont_no, returnQuotes, ((returnQuotes)?dateOfLastQuoteDisplayed:dateOfLastOrderDisplayed), ((returnQuotes)?lastQuoteDisplayed:lastOrderDisplayed), OrderListDataHandler.NO_OF_ORDERS_TO_SEND_WITH_EACH_REQUEST);

			JSONArray jsonArray = new JSONArray();

			if ((ordersAL != null) && (ordersAL.size() > 0)) {
			// if (ordersAL != null) {
				for (Iterator<Order> iterator = ordersAL.iterator(); iterator.hasNext();) {
					Order order = iterator.next();

					JSONObject jsonObject = new JSONObject();

					jsonObject.put("order_date", order.getOrder_date());
					jsonObject.put("order_num", order.getOrder_num());
					jsonObject.put("ship_loc", order.getShip_loc());
					jsonObject.put("cust_ponum", order.getCust_ponum());
					jsonObject.put("ordered_by", order.getOrdered_by());

					// DANGER  DANGER  DANGER  DANGER  DANGER  DANGER
					jsonObject.put("orderStatus", order.getOrderStatus());
					// jsonObject.put("orderStatus", (Math.random() < 0.3)?"Expired":((Math.random() < 0.6)?"Open":"Pending"));

					// String expire_date = order.getExpire_date();
					// expire_date = ((expire_date != null) && (expire_date.compareTo("") != 0))?expire_date:"N/A";
					// jsonObject.put("expire_date", expire_date);
					String date_closed = order.getDate_closed();
					date_closed = ((date_closed != null) && (date_closed.compareTo("") != 0))?date_closed:"";
					jsonObject.put("date_closed", date_closed);

					jsonArray.put(jsonObject);
				}

				log.debug(jsonArray);
				mainJsonObject.put("ord_quo_Array", jsonArray);				
				
				if (ordersAL.size() < OrderListDataHandler.NO_OF_ORDERS_TO_SEND_WITH_EACH_REQUEST) {
					mainJsonObject.put("reachedLastOrder", "YES");				
				} else {
					mainJsonObject.put("reachedLastOrder", "NO");				
				}
			} else {
				mainJsonObject.put("reachedLastOrder", "YES");				
			}

			printWriter.print(mainJsonObject);
			printWriter.flush();
		} catch (JSONException | IOException e) {
			throw new PortalException(e, PortalException.SEVERITY_LEVEL_1);
		}
	}
}
