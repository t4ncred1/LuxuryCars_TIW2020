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

@WebFilter("/CheckSession")
public class CheckSession implements Filter {

	public CheckSession() {
		return;
	}

	public void destroy() {
		return;
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		/* Check if the user is logged in (i.e. an active session is stored in the server)
		 * This also allows to redirect to the home all the users whose session is
		 * expired due to count-down end. */
		try {
			if (request.getSession(false) == null || !request.isRequestedSessionIdValid()) {
				response.sendRedirect(req.getServletContext().getContextPath());
				return;
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
