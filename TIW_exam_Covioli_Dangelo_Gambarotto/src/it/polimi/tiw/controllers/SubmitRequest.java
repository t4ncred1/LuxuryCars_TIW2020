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
import java.util.ArrayList;
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

import it.polimi.tiw.DAO.ProductDAO;
import it.polimi.tiw.DAO.QuotationDAO;
import it.polimi.tiw.beans.ProductBean;
import it.polimi.tiw.beans.UserBean;

/**
 * Servlet implementation class SubmitQuotation
 */
@WebServlet("/SubmitRequest")
public class SubmitRequest extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubmitRequest() {
        super();
    }

	public void init() throws ServletException {
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

	/*
	 * Retrieves the informations sent through the form for the request of a new quotation
	 * and creates the new quotation after some checks are performed.
	 * The parameters sent with the form are:
	 * 1) productId
	 * 2) options selected
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		UserBean u = null;
		HttpSession s = request.getSession(false);
		u = (UserBean) s.getAttribute("user");
		
		int productId = Integer.parseInt(request.getParameter("productId"));
		
		ProductDAO pDAO = new ProductDAO(connection);
		QuotationDAO qDAO = new QuotationDAO(connection);
		ProductBean product;
		
		try {
			product = pDAO.getProductById(productId, "");
			List<Integer> options = new ArrayList<>();
			String[] selectedOptions = request.getParameterValues("options");
			if(selectedOptions==null) {
				// as in other parts of the backend, a cookie is sent
				// with the redirection to the page to transmit
				// non sensitive data.
				Cookie success = new Cookie("success","false");
				response.addCookie(success);
				response.setCharacterEncoding("UTF-8");
				response.sendRedirect(response.encodeRedirectURL(getServletContext().getContextPath()+"/HomeClient"));
				return;
			}
			// A check is performed on the options sent to only add an option to the option list of 
			// a quotation request if the option is valid for the product selected.
			for(String o : selectedOptions) {
				int oId = Integer.parseInt(o);
				if(product.isAValidOption(oId)) options.add(oId);
			}
			// Here is made the check for the number of options added to the quotation request.
			if(options.size()==0) {
				Cookie success = new Cookie("success","false");
				response.addCookie(success);
				response.setCharacterEncoding("UTF-8");
				response.sendRedirect(response.encodeRedirectURL(getServletContext().getContextPath()+"/HomeClient"));
				return;
			}
			qDAO.addQuotation(u.getUserid(), productId, options);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "The database encountered an error.");
			return;
		}
		Cookie success = new Cookie("success","true");
		response.addCookie(success);
		response.setCharacterEncoding("UTF-8");
		response.sendRedirect(response.encodeRedirectURL(getServletContext().getContextPath()+"/HomeClient"));
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
