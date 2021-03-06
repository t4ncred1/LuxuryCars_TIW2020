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

import it.polimi.tiw.beans.OptionBean;

public class OptionDAO {
	private Connection con;
	public OptionDAO(Connection connection) {
		this.con = connection;
	}
	
	/* This function returns all the options associated to a given quotation.
	 * The string language specifies the language in which the names of the options
	 * should be saved.
	 */
	public List<OptionBean> getOptionByQuotation(int qID, String language) throws SQLException{
		String query = "SELECT O.name, O.name_it, O.optionId, O.Stato, O.productId "
						+ "FROM `option` AS O, quotation_option AS QO, quotation AS Q "
						+ "WHERE QO.quotationId = Q.quotationId "
						+ "AND QO.optionId = O.optionId "
						+ "AND O.productId = Q.productId "
						+ "AND Q.quotationId = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, qID);
			try (ResultSet result = pstatement.executeQuery();) {
				List<OptionBean> options = new ArrayList<>();
				while(result.next()) {
					OptionBean option = new OptionBean();
					option.setId(result.getInt("O.optionId"));
					option.setName(result.getString("O.name" + language));
					String stato = result.getString("O.Stato");
					if (stato.equals("in offerta")) {
						option.setInOffer(true);
					}
					else{
						option.setInOffer(false);
					}
					option.setProduct(result.getInt("O.productId"));
					options.add(option);
				}
				closeConnection(result, pstatement);
				return options;
			}
		}
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
