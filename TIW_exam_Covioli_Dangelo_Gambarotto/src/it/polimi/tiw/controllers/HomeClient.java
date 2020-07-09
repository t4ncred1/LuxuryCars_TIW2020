/*  _______ _______          __                                    
 * |__   __|_   _\ \        / /                                    
 *    | |    | |  \ \  /\  / /                                     
 *    | |    | |   \ \/  \/ /                                      
 *    | |   _| |_   \  /\  /                                       
 *    |_|  |_____|   \/  \/   
 * 
 * exam project - a.y. 2019-2020
 * Politecnico di Milano
 * 
 * Tancredi Covioli   mat. 944834
 * Alessandro Dangelo mat. 945149
 * Luca Gambarotto    mat. 928094
*/

package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.DAO.ProductDAO;
import it.polimi.tiw.DAO.QuotationDAO; 
import it.polimi.tiw.beans.QuotationBean;
import it.polimi.tiw.beans.UserBean;
import it.polimi.tiw.utils.SharedPropertyMessageResolver;


@WebServlet("/HomeClient")
public class HomeClient extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;   


    public HomeClient() {
        super();
    }
    
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCharacterEncoding("UTF-8");
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		this.templateEngine.setMessageResolver(new SharedPropertyMessageResolver(servletContext, "i18n", "homeclient"));
		templateResolver.setSuffix(".html");
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
    }
    

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserBean u = null;
		HttpSession s = request.getSession(false);
		u = (UserBean) s.getAttribute("user");

		/* The default language is english. If the string language is empty all
		 * the options will be retrieved using this language. If the tag in the locale
		 * contains it the string is filled with _it". Other languages can be added
		 * adding a column in the DB and adding the related branches in this part
		 * of the server code. */
		String language="";
		if(request.getLocale().toLanguageTag().contains("it")) language="_it";
		
		QuotationDAO qDAO = new QuotationDAO(connection);
		ProductDAO pDAO = new ProductDAO(connection);
		
		List<QuotationBean> clientQuotations = null;
		try {
			clientQuotations = qDAO.getClientQuotations(u.getUserid(), language);
		} catch (SQLException e1) {
			response.setCharacterEncoding("UTF-8");
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,"The database encountered an error.");
			return;
		}
		
		String prod = request.getParameter("product");
		int selProd = 0;
		try {
			if(prod!=null) {
				selProd = Integer.parseInt(prod);
				/* If the product is not a valid one, the servlet redirects to
				 * the mail HomeClient page to avoid showing bad parameters in the
				 * browser address bar. */
				if(!pDAO.existsProduct(selProd)) {
					response.sendRedirect(getServletContext().getContextPath() + "/HomeClient");
					return;
				}
			}
		} catch (NumberFormatException | SQLException e) {
			response.sendError(422,"The product id inserted is not a valid number.");
			return;
		}
		
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		String success = null;
		if (request.getCookies()!=null) {
			for (Cookie c : request.getCookies()) {
				if (c.getName().equals("success")) {
					success=c.getValue();
					Cookie eliminate = new Cookie("success","");
					eliminate.setMaxAge(0);
					response.addCookie(eliminate);
				}
			}
		}
		
		try {
			ctx.setVariable("products", pDAO.getAvailableProducts(language));
			ctx.setVariable("selProd", selProd);
			ctx.setVariable("quotations", clientQuotations);
			ctx.setVariable("name", " " + u.getName());
			if(success!=null && success.equals("true")){
				ctx.setVariable("showSuccessMessage", true);
			}
			else if(success!=null) {
				ctx.setVariable("showErrorMessage", true);
			}
		} catch (SQLException e) {
			response.setCharacterEncoding("UTF-8");
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "The database encountered an error.");
			return;
		}
		String path = "/WEB-INF/homeclient.html";
		response.setCharacterEncoding("UTF-8");
		templateEngine.process(path, ctx, response.getWriter());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
			System.err.println("There was an error while trying to close the connection to the database.");
		}
	}

}
