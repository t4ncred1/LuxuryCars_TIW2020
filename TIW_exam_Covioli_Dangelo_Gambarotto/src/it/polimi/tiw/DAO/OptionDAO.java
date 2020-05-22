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
	
	public List<OptionBean> getOptionByQuotation(int qID) throws SQLException{
		String query = "SELECT O.name, O.optionId, O.Stato, O.productId "
				+ "FROM `option` AS O, quotation_option AS QO, quotation AS Q "
				+ "WHERE QO.quotationId = Q.quotationId "
					+ "AND QO.optionId = O.optionId "
					+ "AND O.productId = Q.productId "
							+ "AND Q.quotationId = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, qID);
			try (ResultSet result = pstatement.executeQuery();) {
				List<OptionBean> options = new ArrayList<>();
				OptionBean option = new OptionBean();
				while(result.next()) {
					option.setId(result.getInt("O.optionId"));
					option.setName(result.getString("O.name"));
					String stato = result.getString("O.Stato");
					if (stato.equals("normale")) option.setInOffer(false);
					else if (stato.equals("in offerta")) option.setInOffer(true);
					else throw new SQLException(); //TODO error management: in caso lo stato non sia una delle stringhe previste.
					option.setProduct(result.getInt("O.productId"));
					options.add(option);
				}
				return options;
			}
		}
	}
	
}
