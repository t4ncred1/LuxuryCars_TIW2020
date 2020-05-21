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
	
	public List<ProductBean> getAvailableProducts() throws SQLException {
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
								option.setName(subresult.getString("name"));
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
			}
		}
		return products;
	}
	
	
}