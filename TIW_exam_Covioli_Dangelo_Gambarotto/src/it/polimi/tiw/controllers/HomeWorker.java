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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.DAO.QuotationDAO;
import it.polimi.tiw.beans.UserBean;
import it.polimi.tiw.utils.SharedPropertyMessageResolver;

/**
 * Servlet implementation class HomeWorker
 */
@WebServlet("/HomeWorker")
public class HomeWorker extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HomeWorker() {
        super();
    }
    
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCharacterEncoding("UTF-8");
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		this.templateEngine.setMessageResolver(new SharedPropertyMessageResolver(servletContext, "i18n", "homeworker"));
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
    
	//
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
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		try {
			//set the necessary elements for the worker page.
			ctx.setVariable("workerQuotations", qDAO.getWorkerQuotations(u.getUserid(), language));
			ctx.setVariable("freeQuotations", qDAO.getFreeQuotations(language));
			ctx.setVariable("name", u.getName());
		} catch (SQLException e) {
			e.printStackTrace();
			response.setCharacterEncoding("UTF-8");
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Database access failed");
			return;
		}
		String path = "/WEB-INF/homeworker.html";
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
