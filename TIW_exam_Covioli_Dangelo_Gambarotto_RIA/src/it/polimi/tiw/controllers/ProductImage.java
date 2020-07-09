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
    	
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
		
		int productId;
		try {
			productId = Integer.parseInt(request.getParameter("product"));
		}
		catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("L'immagine richiesta non esiste");
			return;
		}
		
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
			if(e.getSQLState()!=null) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Errore interno del server");
				return;
			}
			else {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("L'immagine richiesta non esiste");
				return;	
			}
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