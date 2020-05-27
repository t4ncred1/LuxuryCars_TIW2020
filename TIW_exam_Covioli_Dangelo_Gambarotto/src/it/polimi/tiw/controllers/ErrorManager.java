package it.polimi.tiw.controllers;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.UserBean;
import it.polimi.tiw.utils.SharedPropertyMessageResolver;

/**
 * Servlet implementation class Errors
 */
@WebServlet("/Error")
public class ErrorManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ErrorManager() {
        super();
    }

    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCharacterEncoding("UTF-8");
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		this.templateEngine.setMessageResolver(new SharedPropertyMessageResolver(servletContext, "i18n", "error"));
		templateResolver.setSuffix(".html");
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Integer baseCode = HttpServletResponse.SC_SERVICE_UNAVAILABLE;
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
		String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
		
		System.out.println("An Error was thrown:");
		if (throwable!=null) {
			throwable.printStackTrace(); //print the stack trace in the console anyway
			ctx.setVariable("thrown", throwable.getClass().toString());
		}
		if (statusCode!=null) {
			baseCode = statusCode;
			System.out.println(" The error had a status code "+baseCode+".");
		}
		if (servletName!=null) {
			System.out.println(" The error was brought up by servlet "+servletName+".");
		}
		if (errorMessage!=null) {
			ctx.setVariable("errorMessage", errorMessage);
			System.out.println(" The error message was "+errorMessage+".");
		}
		if (request.getSession(false)!=null && request.getSession(false).getAttribute("user")!=null) {
			
			switch(((UserBean)request.getSession(false).getAttribute("user")).getRole()) {
			case "worker":
				ctx.setVariable("worker", true);
				break;
			case "client":
				ctx.setVariable("client", true);
				break;
			}
		}
		System.out.println(request.getSession(false)!=null? (request.getSession(false).getAttribute("user")!=null? "L'utente c'è" : "L'utente non c'è") : "la sessione non c'è");//TODO remove this
		ctx.setVariable("errorCode", baseCode);
		System.out.println("End of error report.");
		
		
		String path = "/WEB-INF/error.html";
		templateEngine.process(path, ctx, response.getWriter());
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		      
		doGet(request, response);
	}

}
