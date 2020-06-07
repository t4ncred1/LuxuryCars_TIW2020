package it.polimi.tiw.controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.DAO.ProductDAO;


@WebServlet("/ProductImage")
public class ProductImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private String archivePath = null;


	public ProductImage() {
		super();
	}

	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			archivePath = context.getInitParameter("imagesFolderPath");
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

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
		int productId = Integer.parseInt(request.getParameter("product"));
		
		ProductDAO pDAO = new ProductDAO(connection);
		
		try {
				String imgPath;
				imgPath = pDAO.getImagePath(productId);
				String thumb = request.getParameter("thumbnail");
				if(thumb!=null && thumb.equals("true")) {
					imgPath = "thumbnail_" + imgPath;
				}
					
		    	response.setContentType("image/png");
		
		        ServletOutputStream outStream;
		        outStream = response.getOutputStream();
		        FileInputStream fin = new FileInputStream(archivePath+imgPath);
		
		        BufferedInputStream bin = new BufferedInputStream(fin);
		        BufferedOutputStream bout = new BufferedOutputStream(outStream);
		        int ch =0; ;
		        while((ch=bin.read())!=-1)
		            bout.write(ch);
		
		        bin.close();
		        fin.close();
		        bout.close();
		        outStream.close();
		} catch (SQLException e) {
			//TODO come gestiamo questo errore?
		
			e.printStackTrace();
			return;
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