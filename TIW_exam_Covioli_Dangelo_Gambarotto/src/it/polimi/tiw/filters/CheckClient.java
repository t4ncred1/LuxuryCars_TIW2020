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

/* OptionBean class
 * This class represents a single option
 */

package it.polimi.tiw.filters;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.DAO.UserDAO;
import it.polimi.tiw.beans.UserBean;

@WebFilter("/CheckClient")
public class CheckClient implements Filter {
	private ServletContext servletContext;
	private Connection connection = null;

	public CheckClient() {
		return;
	}

	public void destroy() {
		return;
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		try {
			UserBean uBean = (UserBean) request.getSession(false).getAttribute("user");

			if (!uBean.getRole().equals("client")) {
				response.setCharacterEncoding("UTF-8");
				switch (uBean.getRole()) {
				case "worker":
					response.sendRedirect(req.getServletContext().getContextPath() + "/HomeWorker");
					return;
				default:
					response.sendRedirect(req.getServletContext().getContextPath());
					return;
				}
			}

			UserDAO uDao = new UserDAO(connection);

			if (!uDao.existsClient(uBean.getUsername())) {
				response.setCharacterEncoding("UTF-8");
				switch (uBean.getRole()) {
				case "worker":
					response.sendRedirect(req.getServletContext().getContextPath() + "/HomeWorker");
					return;
				default:
					response.sendRedirect(req.getServletContext().getContextPath());
					return;
				}
			}
			chain.doFilter(request, response);
		} catch (SQLException e) {
			response.setCharacterEncoding("UTF-8");
			response.sendError(500, "Database access failed");
			return;
		} catch (IOException e) {
			response.setCharacterEncoding("UTF-8");
			response.sendError(555, "I/O Exception: Something wrong in filter chain");
			return;
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		servletContext = fConfig.getServletContext();
		try {
			String driver = servletContext.getInitParameter("dbDriver");
			String url = servletContext.getInitParameter("dbUrl");
			String user = servletContext.getInitParameter("dbUser");
			String password = servletContext.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}

}
