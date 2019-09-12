package galco.portal.control;

import galco.fascor.utils.FasUtils;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class Controler
 */
@WebServlet("/fascor/*")
public class FascorImageSupplier extends HttpServlet {
	private static Logger log = Logger.getLogger(FascorImageSupplier.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnector dbConnector = null;
		String formFunction = null;
		
		String part_num = "";
		
		try {
			// https://web1dev6.galco.com/portal/fascor/SomKaaRamSub/sku/pc1300-dura.jpg
			// https://web1dev6.galco.com/portal/fascor/SomKaaRamSub/sku/ACS550-U1-015A-4-ABBI.jpg
			
	        dbConnector = new DBConnector(true);

			String pathInfo = request.getPathInfo(); // /{value}/test
			log.debug("pathInfo: " + pathInfo);
			
			String imageFilePath = "null";
			if (pathInfo != null) {
				pathInfo = pathInfo.trim().toLowerCase();
				
				if ((pathInfo.indexOf("/") >= 0) && (pathInfo.indexOf("somkaaramsub") >= 0) && (pathInfo.indexOf("sku") >= 0)) {
					part_num = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
					if (part_num.indexOf(".jpg") >= 0) {
						log.debug("part jpg: " + part_num);
						part_num = part_num.substring(0, part_num.indexOf(".jpg"));
						log.debug("part_num: " + part_num);
						try {
							imageFilePath = FasUtils.getSmallImagePath(request, dbConnector, part_num);
						    log.debug("imageFilePath: " + imageFilePath);
						} catch (PortalException e) {
							log.debug("Exception while getting image for sku: " + part_num);
							log.debug("", e.getE());
							dbConnector.closeConnections();
							response.sendError(HttpServletResponse.SC_NOT_FOUND);
							return;
						}	
					}
				}
			}
			
			if (imageFilePath == null) {
				dbConnector.closeConnections();
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;				
			}
			
			try {
				File file = new File(imageFilePath);

				response.setContentType("image/gif");
				response.setContentLength((int) file.length());
				
				FileInputStream in = new FileInputStream(file);
				OutputStream out = response.getOutputStream();
				byte[] buf = new byte[1024];
				int count = 0;
				while ((count = in.read(buf)) >= 0) {
					out.write(buf, 0, count);
				}
				out.close();
				in.close();				
			} catch (Exception e) {
				dbConnector.closeConnections();
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
				
			dbConnector.closeConnections();
		} catch (PortalException e) {
			e.handleException(formFunction, dbConnector, request, response);
		} catch (Exception e) {
			new PortalException(e, PortalException.SEVERITY_LEVEL_1).handleException(formFunction, dbConnector, request, response);
		}
	}

	protected void doGet_O1(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnector dbConnector = null;
		String formFunction = null;
		
		String part_num = "";
		
		try {
			// https://web1dev6.galco.com/portal/fascor/SomKaaRamSub/sku/pc1300-dura.jpg
			// https://web1dev6.galco.com/portal/fascor/SomKaaRamSub/sku/ACS550-U1-015A-4-ABBI.jpg
			
	        dbConnector = new DBConnector(true);

			String pathInfo = request.getPathInfo(); // /{value}/test
			log.debug("pathInfo: " + pathInfo);
			
			String imageFilePath = "/var/www/www.galco.com/htdocs/images/catalog/picture-na_s.jpg";
			if (pathInfo != null) {
				pathInfo = pathInfo.trim().toLowerCase();
				
				if ((pathInfo.indexOf("/") >= 0) && (pathInfo.indexOf("somkaaramsub") >= 0) && (pathInfo.indexOf("sku") >= 0)) {
					part_num = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
					if (part_num.indexOf(".jpg") >= 0) {
						log.debug("part jpg: " + part_num);
						part_num = part_num.substring(0, part_num.indexOf(".jpg"));
						log.debug("part_num: " + part_num);
						try {
							imageFilePath = FasUtils.getSmallImagePath(request, dbConnector, part_num);
						    log.debug("imageFilePath: " + imageFilePath);
						} catch (PortalException e) {
							imageFilePath = "/var/www/www.galco.com/htdocs/images/catalog/picture-na_s.jpg";
							log.debug("Exception while getting image for sku: " + part_num);
							log.debug("", e.getE());
						}	
					}
				}
			}
			
			if (imageFilePath == null) {
				imageFilePath = "/var/www/www.galco.com/htdocs/images/catalog/picture-na_s.jpg";
			}
			
			try {
				File file = new File(imageFilePath);

				response.setContentType("image/gif");
				response.setContentLength((int) file.length());
				
				FileInputStream in = new FileInputStream(file);
				OutputStream out = response.getOutputStream();
				byte[] buf = new byte[1024];
				int count = 0;
				while ((count = in.read(buf)) >= 0) {
					out.write(buf, 0, count);
				}
				out.close();
				in.close();				
			} catch (Exception e) {
				imageFilePath = "/var/www/www.galco.com/htdocs/images/catalog/picture-na_s.jpg";
				try {
					File file = new File(imageFilePath);

					response.setContentType("image/gif");
					response.setContentLength((int) file.length());
					
					FileInputStream in = new FileInputStream(file);
					OutputStream out = response.getOutputStream();
					byte[] buf = new byte[1024];
					int count = 0;
					while ((count = in.read(buf)) >= 0) {
						out.write(buf, 0, count);
					}
					out.close();
					in.close();					
				} catch (Exception e2) {
			        PrintWriter printWriter = response.getWriter();
					response.setContentType("text/html");
					printWriter.print("Exception occurred while retrieving image for sku: " + part_num);
					printWriter.flush();
					printWriter.close();
				}
			}
				
			dbConnector.closeConnections();
		} catch (PortalException e) {
			e.handleException(formFunction, dbConnector, request, response);
		} catch (Exception e) {
			new PortalException(e, PortalException.SEVERITY_LEVEL_1).handleException(formFunction, dbConnector, request, response);
		}
	}

}
