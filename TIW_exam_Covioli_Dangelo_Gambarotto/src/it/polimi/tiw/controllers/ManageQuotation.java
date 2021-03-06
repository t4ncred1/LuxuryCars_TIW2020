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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

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

import it.polimi.tiw.DAO.QuotationDAO;
import it.polimi.tiw.beans.QuotationBean;
import it.polimi.tiw.beans.UserBean;
import it.polimi.tiw.utils.SharedPropertyMessageResolver;

/**
 * Servlet implementation class ManageQuotation
 */
@WebServlet("/ManageQuotation")
public class ManageQuotation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ManageQuotation() {
        super();
    }
    
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCharacterEncoding("UTF-8");
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		this.templateEngine.setMessageResolver(new SharedPropertyMessageResolver(servletContext, "i18n", "managequotation"));
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
    // Gives back the form for the insertion of a price.
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String language="";
		if(request.getLocale().toLanguageTag().contains("it")) language="_it";
		
		QuotationDAO qDAO = new QuotationDAO(connection);
		int qID = 0;
		
		try {
			if( request.getParameter("quotation")!= null)
				qID = Integer.parseInt(request.getParameter("quotation"));
			else throw new NullPointerException();
		} catch(NumberFormatException|NullPointerException e) {
			//error handling: non ?? stato passato un id o 
			// l'id passato non ?? un numero
			response.setCharacterEncoding("UTF-8");
			response.sendError(422, "The passed ID is not a number");
			e.printStackTrace();
			return;
		}
		
		try {
			List<Integer> freeIDs = qDAO.getFreeQuotations(language).stream()
					.map(id -> id.getQuotationId())
					.collect(Collectors.toList());
			if(!freeIDs.contains(qID)) {
				response.setCharacterEncoding("UTF-8");
				response.sendError(422, "The passed ID is not in the list of free IDs");
				return;
			}
		} catch (SQLException e) {
			response.setCharacterEncoding("UTF-8");
			response.sendError(503, "The database in not currently working.");
			return;
		}
				
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		//if the error cookie was passed, set the context and delete it.
		// The cookie is passed from the POST method of this same servlet.
		if (request.getCookies()!=null) {
			for (Cookie c : request.getCookies()) {
				if (c.getName().equals("priceerror") && c.getValue().equals("true")) {
					ctx.setVariable("priceerror", true);
					Cookie eliminate = new Cookie("priceerror","");
					eliminate.setMaxAge(0);
					response.addCookie(eliminate);
				}
				if (c.getName().equals("pricesignerror") && c.getValue().equals("true")) {
					ctx.setVariable("pricesignerror", true);
					Cookie eliminate = new Cookie("pricesignerror","");
					eliminate.setMaxAge(0);
					response.addCookie(eliminate);
				}
			}
		}
		
		//send quotation object
		try {
			QuotationBean q = qDAO.getQuotationById(qID, language);
			if (q==null) {
				throw new SQLException();
			}
			ctx.setVariable("quotation", q);
		} catch (SQLException e) {
			response.setCharacterEncoding("UTF-8");
			response.sendError(503, "The database in not currently working.");
			return;
		}
		String path = "/WEB-INF/managequotation.html";
		response.setCharacterEncoding("UTF-8");
		templateEngine.process(path, ctx, response.getWriter());
		return;
	}

	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	// Manages the insertion of a price.
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String language="";
		if(request.getLocale().toLanguageTag().contains("it")) language="_it";
		
		HttpSession s = request.getSession(false);
		UserBean u = (UserBean) s.getAttribute("user");
		Double price = 0.0;
		int intPrice = 0;
		
		QuotationDAO qDAO = new QuotationDAO(connection);
		int qID = 0;
		
		//check if parameter quotation is correct
		try {
			if( request.getParameter("quotation")!= null)
				qID = Integer.parseInt(request.getParameter("quotation"));
			else throw new NullPointerException();
		} catch(NumberFormatException|NullPointerException e) {
			response.setCharacterEncoding("UTF-8");
			response.sendError(422, "The passed id is not a valid number.");
			e.printStackTrace();
			return;
		}
		
		//check if parameter price is correct
		try {
			if( request.getParameter("price")!= null)
				price = Double.parseDouble(request.getParameter("price"));
			else throw new NullPointerException();
		} catch(NumberFormatException|NullPointerException e) {
			Cookie error = new Cookie("priceerror","true");
			response.addCookie(error);
			response.setCharacterEncoding("UTF-8");
			response.sendRedirect(response.encodeRedirectURL(getServletContext().getContextPath() + "/ManageQuotation?quotation="+qID));
			return;
		}

		//check if the quotation ID is set and if it represents a pending quotations.
		try {
			List<Integer> freeIDs = qDAO.getFreeQuotations(language).stream()
					.map(id -> id.getQuotationId())
					.collect(Collectors.toList());
			if(!freeIDs.contains(qID)) {
				response.setCharacterEncoding("UTF-8");
				response.sendError(422, "The passed ID is not in the list of free IDs");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.setCharacterEncoding("UTF-8");
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Database Not Working.");
			return;
		}
		
		// Check if the value of price is acceptable.
		if(price<=0) {
			//redirect to the page, but with the additional parameter "error" set to true.
			System.out.println("error while checking the sign of the element " + Double.parseDouble(request.getParameter("price")));
			Cookie error = new Cookie("pricesignerror","true");
			response.addCookie(error);
			response.setCharacterEncoding("UTF-8");
			response.sendRedirect(response.encodeRedirectURL(getServletContext().getContextPath() + "/ManageQuotation?quotation="+qID));
			return;
		}
		//check if the price float is in a correct format
		if(BigDecimal.valueOf(price).scale() > 2) {
			//redirect to the page, but with the additional parameter "error" set to true.
			System.out.println("error while checking the scale of the element " + Double.parseDouble(request.getParameter("price")));
			Cookie error = new Cookie("priceerror","true");
			response.addCookie(error);
			response.setCharacterEncoding("UTF-8");
			response.sendRedirect(response.encodeRedirectURL(getServletContext().getContextPath() + "/ManageQuotation?quotation="+qID));
			return;
		}
		Double newprice = price * 100;
		intPrice = newprice.intValue();
				
		//set the price for the quotation
		try {
			qDAO.setQuotationPrice(qID, intPrice, u.getUserid());
		} catch (SQLException e) {
			response.setCharacterEncoding("UTF-8");
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "The database is currently not working.");
			e.printStackTrace();
			return;
			
		}
		response.setCharacterEncoding("UTF-8");
		response.sendRedirect(response.encodeRedirectURL(getServletContext().getContextPath()+"/HomeWorker"));
		return;
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
