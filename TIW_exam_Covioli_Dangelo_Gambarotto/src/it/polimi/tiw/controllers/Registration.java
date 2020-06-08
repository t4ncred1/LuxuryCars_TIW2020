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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.DAO.UserDAO;
import it.polimi.tiw.utils.RegexpChecker;
import it.polimi.tiw.utils.SharedPropertyMessageResolver;

@WebServlet("/Registration")
public class Registration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public Registration() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		this.templateEngine
				.setMessageResolver(new SharedPropertyMessageResolver(servletContext, "i18n", "registration"));
		templateResolver.setSuffix(".html");
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		if (request.getCookies() != null) {
			for (Cookie c : request.getCookies()) {
				if (c.getName().equals("registrationError")) {
					switch(c.getValue()) {
					case "emptyField": 
						ctx.setVariable("errorMsg", "emptyField");
						break;
					case "usedUsername": 
						ctx.setVariable("errorMsg", "usedUsername");
						break;
					case "wrongRole": 
						ctx.setVariable("errorMsg", "wrongRole");
						break;
					case "wrongPassword": 
						ctx.setVariable("errorMsg", "wrongPassword");
						break;
					case "wrongEmail": 
						ctx.setVariable("errorMsg", "wrongEmail");
						break;
					case "success": 
						ctx.setVariable("errorMsg", "success");
						break;
					default:
						System.out.println("The user tried to tamper with the cookie \"errorMsg\"");
					}
					
					Cookie eliminate = new Cookie("registrationError", "");
					eliminate.setMaxAge(0);
					response.addCookie(eliminate);
				}
			}
		}
		try {
			String path = "/WEB-INF/registration.html";
			templateEngine.process(path, ctx, response.getWriter());
		} catch (IOException e) {
			response.sendError(555, "I/O Exception: cannot load response.getWriter()");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String username = request.getParameter("username");
		String password1 = request.getParameter("pwd1");
		String password2 = request.getParameter("pwd2");
		String role = request.getParameter("role");
		String mail = request.getParameter("mail");

		UserDAO uDao = new UserDAO(connection);

		try {
			if (firstName == "" || lastName == "" || username == "" || password1 == "" || password2 == "" || role == "" || mail == "") {
				Cookie error = new Cookie("registrationError", "emptyField");
				response.addCookie(error);
				response.sendRedirect(getServletContext().getContextPath() + "/Registration");
				return;
			}
			if (uDao.existsUser(username)) {
				Cookie error = new Cookie("registrationError", "usedUsername");
				response.addCookie(error);
				response.sendRedirect(getServletContext().getContextPath() + "/Registration");
				return;
			}
			if (!role.equals("client") && !role.equals("worker")) {
				Cookie error = new Cookie("registrationError", "wrongRole");
				response.addCookie(error);
				response.sendRedirect(getServletContext().getContextPath() + "/Registration");
				return;
			}
			if(!password1.equals(password2)) {
				Cookie error = new Cookie("registrationError", "wrongPassword");
				response.addCookie(error);
				response.sendRedirect(getServletContext().getContextPath() + "/Registration");
				return;
			}
			if(!RegexpChecker.checkExpression(RegexpChecker.EMAIL, mail)) {
				Cookie error = new Cookie("registrationError", "wrongEmail");
				response.addCookie(error);
				response.sendRedirect(getServletContext().getContextPath() + "/Registration");
				return;
			}
			
			uDao.registerUser(username, password1, firstName, lastName, role, mail);
			Cookie success = new Cookie("registrationError", "success");
			response.addCookie(success);
			response.sendRedirect(getServletContext().getContextPath() + "/Registration");
			return;
		} catch (SQLException e) {
			response.sendError(500, "Database access failed");
			e.printStackTrace();
		} catch (IOException e) {
			response.sendError(555, "I/O Exception: cannot load response.getWriter()");
		}
	}

}
