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

import com.google.gson.Gson;

import it.polimi.tiw.DAO.QuotationDAO; 
import it.polimi.tiw.beans.QuotationBean;
import it.polimi.tiw.beans.UserBean;


@WebServlet("/GetQuotation")
public class GetQuotation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    public GetQuotation() {
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
		
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
		
		HttpSession session = request.getSession();
		UserBean user = (UserBean) session.getAttribute("user");
		
		if(user==null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Utente non autenticato!");
			return;
		}
		
		QuotationDAO quotationDAO = new QuotationDAO(connection);

		String qIds = request.getParameter("id");
		
		if(qIds==null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Richiesta non valida");
			return;
		}
		
		int qId;
		try {
			qId = Integer.parseInt(request.getParameter("id"));
		}
		catch (NumberFormatException e) {
			qId = 0;
		}
		
		QuotationBean quotation = null;
		
		try {
			quotation = quotationDAO.getQuotationById(qId, "_it");
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Impossibile recuperare la richiesta");
			return;
		}
		
		if(quotation!=null && user.getUserid()==quotation.getClientId()) {
			Gson gson = new Gson();
			String json = gson.toJson(quotation);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
		}
		else {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().println("Accesso non autorizzato");
		}

		
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
