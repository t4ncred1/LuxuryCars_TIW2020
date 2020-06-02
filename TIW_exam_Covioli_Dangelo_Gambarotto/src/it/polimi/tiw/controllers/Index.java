package it.polimi.tiw.controllers;

import java.io.IOException;

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

import it.polimi.tiw.utils.SharedPropertyMessageResolver;

/**
 * Servlet implementation class Index
 */
@WebServlet("/Index")
public class Index extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Index() {
        super();
    }
    
    public void init() throws UnavailableException {
    	ServletContext context = getServletContext();
    	ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		this.templateEngine.setMessageResolver(new SharedPropertyMessageResolver(context, "i18n", "login"));
		templateResolver.setSuffix(".html");
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		if (request.getCookies()!=null) {
			for (Cookie c : request.getCookies()) {
				if (c.getName().equals("userpasserror") && c.getValue().equals("true")) {
					ctx.setVariable("userpasserror", true);
					Cookie eliminate = new Cookie("userpasserror","");
					eliminate.setMaxAge(0);
					response.addCookie(eliminate);
				}
			}	
		}
		response.setCharacterEncoding("UTF-8");
		templateEngine.process("/index.html", ctx, response.getWriter());	}

}
