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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.*;

import it.polimi.tiw.DAO.QuotationDAO;
import it.polimi.tiw.beans.QuotationBean;
import it.polimi.tiw.beans.UserBean;
/*
 * Servlet implementation class ManagedQuotations
 * Provides the past managed quotations of a worker.
 */
@WebServlet("/ManagedQuotations")
public class ManagedQuotations extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private Gson gson;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ManagedQuotations() {
        super();
    }
    
    public void init() throws ServletException {
    	// manage the connection to the DB.
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
		gson = new Gson();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		QuotationDAO qDAO = new QuotationDAO(connection);
		List<QuotationBean> qlist = null;
		String language="_it";
		HttpSession s = request.getSession(false); // should only return a valid session thanks to Session Filters
		UserBean u = (UserBean) s.getAttribute("user");
		response.setContentType("application/json");
		try{
			// get the previously managed quotations
			qlist = qDAO.getWorkerQuotations(u.getUserid(), language);
		} catch (SQLException e){
			// DB error
			String errormessage = "È stato riscontrato un errore cercando di ottenere dei risultati dal database.";
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write(errormessage);
		}
		String json = gson.toJson(qlist);
		response.getWriter().write(json);
		return;
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
