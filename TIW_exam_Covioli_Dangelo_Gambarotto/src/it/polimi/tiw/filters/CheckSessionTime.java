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
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter("/CheckSessionTime")
public class CheckSessionTime implements Filter {

	public CheckSessionTime() {
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
			if (request.getSession(false) == null) {
				response.setCharacterEncoding("UTF-8");
				response.sendRedirect(req.getServletContext().getContextPath());
				return;
			}

			chain.doFilter(request, response);
		} catch (IOException e) {
			response.setCharacterEncoding("UTF-8");
			response.sendError(555, "I/O Exception: Something wrong in filter chain");
			return;
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		return;
	}

}
