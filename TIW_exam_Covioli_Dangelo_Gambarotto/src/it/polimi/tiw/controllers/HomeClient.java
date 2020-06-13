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

/**
 * Servlet implementation class HomeWorker
 */
@WebServlet("/HomeClient")
public class HomeClient extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;   
    /**
     * @see HttpServlet#HttpServlet()
     */
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
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserBean u = null;
		HttpSession s = request.getSession(false);
		u = (UserBean) s.getAttribute("user");

		String language="";
		if(request.getLocale().toLanguageTag().contains("it")) language="_it";
		
		QuotationDAO qDAO = new QuotationDAO(connection);
		List<QuotationBean> clientQuotations = null;
		try {
			clientQuotations = qDAO.getClientQuotations(u.getUserid(), language);
		} catch (SQLException e1) {
			e1.printStackTrace();
			response.setCharacterEncoding("UTF-8");
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,"The database encountered an error.");
			return;
		}
		
		String prod = request.getParameter("product");
		int selProd = 0;
		try {
			if(prod!=null) selProd = Integer.parseInt(prod);
		} catch (NumberFormatException e) {
			response.sendError(422,"The product id inserted is not a valid number.");
			return;
		}
		ProductDAO pDAO = new ProductDAO(connection);
		
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
			System.out.println(selProd);
			ctx.setVariable("quotations", clientQuotations);
			ctx.setVariable("name", " " + u.getName());
			if(success!=null && success.equals("true")){
				ctx.setVariable("showSuccessMessage", true);
			}
			else if(success!=null) {
				ctx.setVariable("showErrorMessage", true);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.setCharacterEncoding("UTF-8");
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "The database encountered an error.");
			return;
		}
		String path = "/WEB-INF/homeclient.html";
		response.setCharacterEncoding("UTF-8");
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			System.out.println("There was an error while trying to close the connection to the database.");
		}
	}

}
