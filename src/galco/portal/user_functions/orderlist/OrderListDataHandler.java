package galco.portal.user_functions.orderlist;

import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.Contact;
import galco.portal.wds.dao.Order;
import galco.portal.wds.dao.UserSession;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class OrderListDataHandler {
	private static Logger log = Logger.getLogger(OrderListDataHandler.class);
	
	public static final int NO_OF_ORDERS_TO_SEND_WITH_EACH_REQUEST = 15;

	public static void displayOrderList(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response) throws PortalException {
		UserSession userSession = Utils.checkAndRediredtUnauthenticatedUsers(request, dbConnector, response);
		if (userSession == null) {
			return;
		}

		displayOrderList(request, dbConnector, response, userSession.getCust_num(), userSession.getCont_no());
	}

	public static void displayOrderList(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response, String cust_num, int cont_no) throws PortalException {
		displayOrderList(request, dbConnector, response, cust_num, cont_no, null);
	}

	public static void displayOrderList(HttpServletRequest request, DBConnector dbConnector, HttpServletResponse response, String cust_num, int cont_no, String messageToUser) throws PortalException {
		if (messageToUser == null) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				String msgFromRedirect = (String) session.getAttribute("msgFromRedirect");
				log.debug("msgFromRedirect, OrderListDataHandler: " + msgFromRedirect);
				messageToUser = msgFromRedirect;
	
				session.setAttribute("msgFromRedirect", null);
			}
		}

		ArrayList<Order> ordersAL = Order.fetchNextBatchOfOrders(dbConnector, cust_num, cont_no, false, null, null, OrderListDataHandler.NO_OF_ORDERS_TO_SEND_WITH_EACH_REQUEST);
		String lastOrderDisplayed = ((ordersAL != null) && (ordersAL.size() >= 1))?(ordersAL.get(ordersAL.size() - 1).getOrder_num()):"";
		String dateOfLastOrderDisplayed = ((ordersAL != null) && (ordersAL.size() >= 1))?(ordersAL.get(ordersAL.size() - 1).getOrder_date()):"";

		ArrayList<Order> quotesAL = Order.fetchNextBatchOfOrders(dbConnector, cust_num, cont_no, true, null, null, OrderListDataHandler.NO_OF_ORDERS_TO_SEND_WITH_EACH_REQUEST);
		String lastQuoteDisplayed = ((quotesAL != null) && (quotesAL.size() >= 1))?(quotesAL.get(quotesAL.size() - 1).getOrder_num()):"";
		String dateOfLastQuoteDisplayed = ((quotesAL != null) && (quotesAL.size() >= 1))?(quotesAL.get(quotesAL.size() - 1).getOrder_date()):"";

		String activeTab = request.getParameter("activeTab");
		if (activeTab == null) {
			activeTab = "orders";
		}
		request.setAttribute("activeTab", activeTab);
		
		/*
		ArrayList<Contact> contactAL = Contact.getContact(cust_num, cont_no);
		if ((contactAL != null) && (contactAL.size() > 0)) {
			request.setAttribute("userFnLn", contactAL.get(0).getCo_name_f() + " " + contactAL.get(0).getCo_name_l());
		} else {
			request.setAttribute("userFnLn", null);
		}
		*/
		
		request.setAttribute("cust_num", cust_num);
		request.setAttribute("cont_no", cont_no);
		request.setAttribute("ordersAL", ordersAL);
		request.setAttribute("lastOrderDisplayed", lastOrderDisplayed);
		request.setAttribute("dateOfLastOrderDisplayed", dateOfLastOrderDisplayed);
		request.setAttribute("quotesAL", quotesAL);
		request.setAttribute("lastQuoteDisplayed", lastQuoteDisplayed);
		request.setAttribute("dateOfLastQuoteDisplayed", dateOfLastQuoteDisplayed);
		Utils.forwardToJSP(request, response, "OrderListDisplay.jsp", messageToUser);
	}
}
