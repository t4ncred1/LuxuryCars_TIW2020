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

package it.polimi.tiw.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.*;

public class ProductDAO {
	private Connection con;

	public ProductDAO(Connection connection) {
		this.con = connection;
	}
	
	/* This function returns a list containing a ProductBean
	 * for each product available found in the DB. The language string allows to
	 * select a language.
	 */
	public List<ProductBean> getAvailableProducts(String language) throws SQLException {
		List<ProductBean> products = new ArrayList<>();
		String query = "SELECT * FROM product";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					ProductBean product = new ProductBean();
					product.setId(result.getInt("productId"));
					product.setName(result.getString("name"));
					String subquery = "SELECT * FROM `option` WHERE productId = ?";
					try (PreparedStatement subpstatement = con.prepareStatement(subquery);) {
						subpstatement.setInt(1, product.getId());
						try (ResultSet subresult = subpstatement.executeQuery();) {
							List<OptionBean> options = new ArrayList<>();
							while(subresult.next()) {
								OptionBean option = new OptionBean();
								option.setId(subresult.getInt("optionId"));
								option.setProduct(subresult.getInt("productId"));
								/* Notice: if the string language is not valid the 
								 * function will result in an SQLException, that must be
								 * managed by the caller. */
								option.setName(subresult.getString("name" + language));
								if(subresult.getString("Stato").equals("normale"))
									option.setInOffer(false);
								else option.setInOffer(true);
								options.add(option);
							}
							product.setOptions(options);
						}
					}
					products.add(product);
				}
				closeConnection(result, pstatement);
			}
		}
		return products;
	}
	
	
	/* This function retrieves from the DB the information about
	 * the product whose id is passed as parameter, returning the relative
	 * ProductBean. The string language allows to select the language in which
	 * the option name must be written.
	 */
	public ProductBean getProductById(int productId, String language) throws SQLException{
		ProductBean product = null;
		String query = "SELECT * FROM product WHERE productId = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, productId);
			try (ResultSet result = pstatement.executeQuery();) {
				if(result.next()) {
					product = new ProductBean();
					product.setId(result.getInt("productId"));
					product.setName(result.getString("name"));
					String subquery = "SELECT * FROM `option` WHERE productId = ?";
					try (PreparedStatement subpstatement = con.prepareStatement(subquery);) {
						subpstatement.setInt(1, product.getId());
						try (ResultSet subresult = subpstatement.executeQuery();) {
							List<OptionBean> options = new ArrayList<>();
							while(subresult.next()) {
								OptionBean option = new OptionBean();
								option.setId(subresult.getInt("optionId"));
								option.setProduct(subresult.getInt("productId"));
								option.setName(subresult.getString("name" + language));
								if(subresult.getString("Stato").equals("normale"))
									option.setInOffer(false);
								else option.setInOffer(true);
								options.add(option);
							}
							product.setOptions(options);
						}
					}
					closeConnection(result, pstatement);
					return product;
				}
				else {
					closeConnection(result, pstatement);
					throw new SQLException();
				}
			}
		}
	}
	
	
	
	
	/* This function returns the image name of the product whose id
	 * is specified in the parameter.
	 */
	public String getImagePath(int productId) throws SQLException {
		String query = "SELECT img_path FROM product WHERE productId = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, productId);
			try (ResultSet result = pstatement.executeQuery();) {
				if(result.next()) {
					String path = result.getString("img_path");
					closeConnection(result, pstatement);
					return path;
				}
				else {
					closeConnection(result, pstatement);
					throw new SQLException("invalid product");
				}
			}
		}
	}
	
	/* This function returns true if the id specified in the parameter
	 * corresponds to a product in the DB.
	 */
	public boolean existsProduct(int productId) throws SQLException{
		String query = "SELECT productId FROM product";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					if(result.getInt("productId")==productId) {
						closeConnection(result, pstatement);
						return true;
					}
				}
				closeConnection(result, pstatement);
			}
		}
		return false;
	}
	
	
	private void closeConnection(ResultSet res, PreparedStatement pStatement) throws SQLException {
		try {
			res.close();
		} catch (SQLException e) {
			throw new SQLException("Cannot close result");
		}
		try {
			pStatement.close();
		} catch (SQLException e) {
			throw new SQLException("Cannot close statement");
		}
	}
	
}