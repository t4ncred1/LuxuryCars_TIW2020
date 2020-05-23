package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.DAO.ProductDAO;
import it.polimi.tiw.DAO.QuotationDAO;
import it.polimi.tiw.beans.QuotationBean;
import it.polimi.tiw.beans.UserBean;
import it.polimi.tiw.utils.SharedPropertyMessageResolver;

/**
 * Servlet implementation class HomeWorker
 */
@WebServlet("/HomeClient")
public class HomeClient extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HomeClient() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		this.templateEngine.setMessageResolver(new SharedPropertyMessageResolver(servletContext, "i18n", "homeclient"));
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
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		UserBean u = null;
		HttpSession s = request.getSession();
		u = (UserBean) s.getAttribute("user");

		QuotationDAO qDAO = new QuotationDAO(connection);
		List<QuotationBean> clientQuotations = null;
		try {
			clientQuotations = qDAO.getClientQuotations(u.getUserid());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String success = request.getParameter("success");
		
		String prod = request.getParameter("product");
		int selProd = 0;
		if(prod!=null) selProd = Integer.parseInt(prod);
		
		ProductDAO pDAO = new ProductDAO(connection);
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		try {
			ctx.setVariable("products", pDAO.getAvailableProducts());
			ctx.setVariable("selProd", selProd);
			ctx.setVariable("quotations", clientQuotations);
			if(success!=null && success.equals("true")){
				ctx.setVariable("showSuccessMessage", true);
			}
			else if(success!=null) {
				ctx.setVariable("showErrorMessage", true);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String path = "/WEB-INF/homeclient.html";
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}

}
