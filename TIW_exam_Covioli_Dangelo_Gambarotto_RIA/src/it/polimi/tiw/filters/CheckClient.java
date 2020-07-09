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

package it.polimi.tiw.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.UserBean;

@WebFilter("/CheckClient")
public class CheckClient implements Filter {

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
			/* Check if the user is logged in or not. If the user is not logged the
			 * instruction in the try clause will result in a NullPointerException,
			 * whose effect is to redirect the user to the index page. */
			UserBean uBean = null;
			try {
				uBean = (UserBean) request.getSession(false).getAttribute("user");
			}
			catch(NullPointerException e) {
				response.setStatus(403);
				response.getWriter().println(req.getServletContext().getContextPath());
				return;
			}
			/* Check if the user is a client. In case it is not, if it is a worker
			 * it is automatically redirected to the worker page, otherwise is is redirected
			 * to the index page. This case has been introduced to manage the error even
			 * in future expansions of the application, speculating the possibility of
			 * introducing new types of users */
			if (!uBean.getRole().equals("client")) {
				switch (uBean.getRole()) {
				case "worker":
					response.setStatus(403);
					response.getWriter().println(req.getServletContext().getContextPath() + "/Worker");
					return;
				default:
					response.setStatus(403);
					response.getWriter().println(req.getServletContext().getContextPath());
					return;
				}
			}
			chain.doFilter(request, response);
		} catch (IOException e) {
			response.sendError(555, "I/O Exception: Something wrong in filter chain");
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		return;
	}

}
