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
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.DAO.QuotationDAO;
import it.polimi.tiw.beans.UserBean;

/**
 * Servlet implementation class PricePost
 */

@WebServlet("/PricePost")
@MultipartConfig
public class PricePost extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PricePost() {
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
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/plain;charset=UTF-8");	
		
		String language="_it"; //the database is the same as the HTML counterpart, thus we need to pass a
								// language to each method.
		
		HttpSession s = request.getSession(false);	// should only return a valid session thanks to Session Filters
		UserBean u = (UserBean) s.getAttribute("user");
		Double price = 0.0;
		int intPrice = 0;
		
		QuotationDAO qDAO = new QuotationDAO(connection);
		int qID = 0;
		
		//check if parameter quotation is correct
		try {
			if( request.getParameter("quotation")!= null)
				qID = Integer.parseInt(request.getParameter("quotation"));
			else {
				System.out.println("id è nullo.");
				throw new NullPointerException();
			}
		} catch(NumberFormatException|NullPointerException e) {
			String errormessage = "L'id passato non è un valore valido";
			response.setStatus(422);
			response.getWriter().write(errormessage);
			return;
		}
		
		//check if parameter price is correct
		try {
			if( request.getParameter("price")!= null)
				price = Double.parseDouble(request.getParameter("price"));
			else throw new NullPointerException();
		} catch(NumberFormatException|NullPointerException e) {
			String errormessage = "Si prega di inserire un prezzo valido che rispetti il formato visualizzato";
			response.setStatus(422);
			response.getWriter().write(errormessage);
			return;
		}
		
		if(price<=0) {
			String errormessage = "Il prezzo inserito deve essere maggiore di 0";
			response.setStatus(422);
			response.getWriter().write(errormessage);
			return;
		}
		//check if the price float is in a correct format
		if(BigDecimal.valueOf(price).scale() > 2) {
			//redirect to the page, but with the additional parameter "error" set to true.
			String errormessage = "Si prega di inserire un prezzo valido che rispetti il formato visualizzato";
			response.setStatus(422);
			response.getWriter().write(errormessage);
			return;
		}
		
		//check if the quotation ID is set
		try {
			List<Integer> freeIDs = qDAO.getFreeQuotations(language).stream()
					.map(id -> id.getQuotationId())
					.collect(Collectors.toList());
			if(!freeIDs.contains(qID)) {
				String errormessage = "L'id passato non è tra gli ID liberi";
				response.setStatus(422);
				response.getWriter().write(errormessage);
				return;
			}
		} catch (SQLException e) {
			// database error
			e.printStackTrace();
			String errormessage = "Il database non funziona";
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write(errormessage);
			return;
		}
		
		Double newprice = price * 100; // The price is saved in cents.
		intPrice = newprice.intValue();
			
		//set the price for the quotation
		try {
			qDAO.setQuotationPrice(qID, intPrice, u.getUserid());
		} catch (SQLException e) {
			e.printStackTrace();
			String errormessage = "Il database non funziona";
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write(errormessage);
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write("Il nuovo prezzo è stato inserito correttamente!");
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
