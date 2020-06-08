package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import it.polimi.tiw.DAO.UserDAO;
import it.polimi.tiw.utils.RegexpChecker;

@WebServlet("/Registration")
@MultipartConfig
public class Registration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public Registration() {
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


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String username = request.getParameter("username");
		String mail = request.getParameter("mail");
		String password1 = request.getParameter("pwd1");
		String password2 = request.getParameter("pwd2");
		String role = request.getParameter("role");

		UserDAO uDao = new UserDAO(connection);

		try {
			if (firstName == "" || lastName == "" || username == "" || mail == "" || password1 == "" || password2 == ""
					|| role == "") {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Inserire credenziali");
				return;
			}
			if (uDao.existsUser(username)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Lo username e' gia' in uso!");
				return;
			}
			if (!role.equals("client") && !role.equals("worker")) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Seleziona un ruolo corretto!");
				return;
			}
			if (!password1.equals(password2)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Le password inserite non combaciano!");
				return;
			}
			if(!RegexpChecker.checkExpression(RegexpChecker.EMAIL, mail)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("La mail inserita non e' valida!");
				return;
			}
			
			uDao.registerUser(username, password1, firstName, lastName, role, mail);
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println("Utente inserito correttamente. Premi qui per tornare alla pagina di login.");
			return;
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Errore interno del server.");
			e.printStackTrace();
		} catch (IOException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Errore interno del server.");
		}
	}

}
