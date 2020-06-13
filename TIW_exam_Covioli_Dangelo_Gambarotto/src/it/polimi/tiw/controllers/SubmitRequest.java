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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
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
				Cookie success = new Cookie("success","false");
				response.addCookie(success);
				response.setCharacterEncoding("UTF-8");
				response.sendRedirect(response.encodeRedirectURL(getServletContext().getContextPath()+"/HomeClient"));
				return;
			}
			for(String o : selectedOptions) {
				int oId = Integer.parseInt(o);
				if(product.isAValidOption(oId)) options.add(oId);
			}
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
