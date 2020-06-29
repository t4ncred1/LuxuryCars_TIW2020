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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.tiw.DAO.ProductDAO;
import it.polimi.tiw.beans.ProductBean;

/**
 * Servlet implementation class HomeWorker
 */
@WebServlet("/GetAvailableProducts")
public class GetAvailableProducts extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAvailableProducts() {
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
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ProductDAO productDAO = new ProductDAO(connection);
		List<ProductBean> products = new ArrayList<ProductBean>();

		try {
			products = productDAO. getAvailableProducts("_it");
		} catch (SQLException e) {
			e.printStackTrace();
			//TODO: sistemare gestione degli errori
			/*response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover missions");*/
			return;
		}
				
		Gson gson = new Gson();
		String json = gson.toJson(products);
		
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		
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
			sqle.printStackTrace();
			System.out.println("There was an error while trying to close the connection to the database.");
		}
	}

}
