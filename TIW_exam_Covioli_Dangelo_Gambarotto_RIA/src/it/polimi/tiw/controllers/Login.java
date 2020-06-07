package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.UserBean;
import it.polimi.tiw.DAO.UserDAO;

import it.polimi.tiw.utils.DynamicJsonObject;

@WebServlet("/Login")
@MultipartConfig
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

		String usrn = null;
		String pwd = null;
		usrn = request.getParameter("username");
		pwd = request.getParameter("pwd");
		if (usrn == null || pwd == null || usrn.isEmpty() || pwd.isEmpty() ) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Inserire credenziali");
			return;
		}

		UserDAO userDao = new UserDAO(connection);
		UserBean user = null;
		try {
			user = userDao.checkCredentials(usrn, pwd);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Errore del server, riprova pi� tardi");
			return;
		}

		if (user == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Nome utente o password errati!");
		} else {
			request.getSession().setAttribute("user", user);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			DynamicJsonObject personal = new DynamicJsonObject();
			personal.addAttribute("name", user.getName());
			personal.addAttribute("role", user.getRole());
			if(user.getRole().equals("worker"))
				personal.addAttribute("page", "homeworker.html");
			else personal.addAttribute("page", "homeclient.html");
			String json = personal.getJsonString();
			System.out.println(json);
			response.getWriter().write(json);
			
		}
		
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

