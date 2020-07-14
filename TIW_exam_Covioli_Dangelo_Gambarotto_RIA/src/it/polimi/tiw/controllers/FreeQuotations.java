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

import com.google.gson.Gson;

import it.polimi.tiw.DAO.QuotationDAO;
import it.polimi.tiw.beans.QuotationBean;


@WebServlet("/FreeQuotations")
public class FreeQuotations extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private Gson gson;
       

	public FreeQuotations() {
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
    
    // Gives back the list of pending requests for a quotation.
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		QuotationDAO qDAO = new QuotationDAO(connection);
		List<QuotationBean> qlist = null;
		String language="_it"; //the database is the same as the HTML counterpart, thus we need to pass a
								// language to each method.
		response.setContentType("application/json");
		try{
			qlist = qDAO.getFreeQuotations(language);
		} catch (SQLException e){
			//DB error.
			String errormessage = "E' stato riscontrato un errore cercando di ottenere dei risultati dal database.";
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write(errormessage);
		}
		String json = gson.toJson(qlist);
		response.getWriter().write(json);
		return;
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
