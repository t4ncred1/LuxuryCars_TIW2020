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
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.DAO.ProductDAO;
import it.polimi.tiw.DAO.QuotationDAO;
import it.polimi.tiw.beans.ProductBean;
import it.polimi.tiw.beans.UserBean;



@WebServlet("/SubmitRequest")
@MultipartConfig
public class SubmitRequest extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection;
       

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
    

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
		
		UserBean u = null;
		HttpSession s = null;
		
		/* Check if the user is authenticated */
		try {
			s = request.getSession(false);
		} catch(NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Utente non autenticato");
			return;
		}
		u = (UserBean) s.getAttribute("user");

		int productId = 0;
		/* Check if the productId is not null and if it is a valid number */
		try {
			productId = Integer.parseInt(request.getParameter("productId"));
		} catch(NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Id prodotto non valido");
			return;
		}
		
		ProductDAO pDAO = new ProductDAO(connection);
		QuotationDAO qDAO = new QuotationDAO(connection);
		ProductBean product;
		try {
			product = pDAO.getProductById(productId, "");
			/* Check if exists a product with the specified productId */
			if(product == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Il prodotto richiesto non esiste");
				return;
			}
			List<Integer> options = new ArrayList<>();
			String[] selectedOptions = request.getParameterValues("options");
			/* Check if the user specified a list of options */
			if(selectedOptions==null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Seleziona almeno una opzione");
				return;
			}
			for(String o : selectedOptions) {
				int oId = Integer.parseInt(o);
				if(product.isAValidOption(oId)) options.add(oId);
			}
			/* Check if the size of the list of valid options selected is != 0;*/
			if(options.size()==0) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Seleziona almeno una opzione");
				return;
			}
			qDAO.addQuotation(u.getUserid(), productId, options);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			response.getWriter().println("Errore del server");
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println("Richiesta inserita correttamente");
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
