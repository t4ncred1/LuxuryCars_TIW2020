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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.DAO.UserDAO;
import it.polimi.tiw.beans.*;

@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public Login() {
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		/* Note about usrn and pwd: if these two parameters are not specified
		 * in the request these two variables are set to a null value. Once the
		 * existence of the user will be checked the userDAO will return a null bean,
		 * whose behavior is managed in the first if branch. This is why usrn and
		 * pwd are not explicitly checked not to be null. */
		String usrn = request.getParameter("username");
		String pwd = request.getParameter("pwd");
		/*	
		 * 	A DAO to perform credential check is instantiated
		 * 	passing as parameter the db connection to be used
		 */
		UserDAO usr = new UserDAO(connection);
		UserBean u = null;
		try {
			/*
			 * The check of the credentials is performed using the DAO
			 * previously created
			 */
			u = usr.checkCredentials(usrn, pwd);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking");
			return;
		}
		String path = getServletContext().getContextPath();
		if (u == null) {
			Cookie error = new Cookie("userpasserror", "true");
			response.addCookie(error);
			response.setCharacterEncoding("UTF-8");
			response.sendRedirect(path+"/Index");
			return;
		} else {
			request.getSession().setAttribute("user", u);
			String target = (u.getRole().equals("worker")) ? "/HomeWorker" : "/HomeClient";
			path = path + target;
			response.setCharacterEncoding("UTF-8");
			response.sendRedirect(path);
			return;
		}
		/*
		 * This servlet does not send back a webpage but redirect the user
		 * to the right servlet that send back the required page (home admin or
		 * home worker)
		 */
		
	}

	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
			System.err.println("There was an error while trying to close the connection to the database.");
		}
	}
}
