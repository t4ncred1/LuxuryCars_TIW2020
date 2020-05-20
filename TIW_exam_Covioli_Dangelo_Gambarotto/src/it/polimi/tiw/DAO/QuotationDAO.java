package it.polimi.tiw.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.*;

public class QuotationDAO {
	private Connection con;

	public QuotationDAO(Connection connection) {
		this.con = connection;
	}
	
	public List<QuotationBean> getFreeQuotations() throws SQLException {
		List<QuotationBean> freeQuotations = new ArrayList<>();
		String query = "SELECT Q.quotationId, Q.date, Q.productId, P.name,	Q.clientId, C.username "
							+ "FROM quotation AS Q, client AS C, product AS P "
							+ "WHERE Q.productId = P.productId AND C.idclient = Q.clientId AND Q.workerid IS NULL";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					QuotationBean quotation = new QuotationBean();
					quotation.setQuotationId(result.getInt("Q.quotationId"));
					quotation.setClientId(result.getInt("Q.clientId"));
					quotation.setClientUsername(result.getString("C.username"));
					quotation.setDate(result.getString("Q.Date"));
					quotation.setProductId(result.getInt("Q.productId"));
					quotation.setProductName(result.getNString("P.name"));
					String subquery = "SELECT O.name "
										+ "FROM `option` AS O, quotation_option AS QO, quotation AS Q "
										+ "WHERE QO.quotationId = Q.quotationId "
											+ "AND QO.optionId = O.optionId "
											+ "AND O.productId = Q.productId "
											+ "AND Q.quotationId = ?";
					try (PreparedStatement subpstatement = con.prepareStatement(subquery);) {
						subpstatement.setInt(1, quotation.getQuotationId());
						System.out.println(quotation.getQuotationId());
						try (ResultSet subresult = subpstatement.executeQuery();) {
							List<String> options = new ArrayList<>();
							while(subresult.next()) {
								options.add(subresult.getString("O.name"));
							}
							quotation.setOptions(options);
						}
					}
					freeQuotations.add(quotation);
				}
			}
		}
		return freeQuotations;
	}
	
	
	public List<QuotationBean> getWorkerQuotations(int workerId) throws SQLException {
		List<QuotationBean> workerQuotations = new ArrayList<>();
		String query = "SELECT Q.quotationId, Q.price, Q.date, Q.productId, P.name,	Q.clientId, C.username, Q.workerId, W.username "
							+ "FROM quotation AS Q, client AS C, product AS P, worker AS W "
							+ "WHERE Q.productId = P.productId AND C.idclient = Q.clientId AND W.idworker = Q.workerId AND Q.workerid = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, workerId);
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					QuotationBean quotation = new QuotationBean();
					quotation.setQuotationId(result.getInt("Q.quotationId"));
					quotation.setClientId(result.getInt("Q.clientId"));
					quotation.setClientUsername(result.getString("C.username"));
					quotation.setDate(result.getString("Q.Date"));
					quotation.setProductId(result.getInt("Q.productId"));
					quotation.setProductName(result.getNString("P.name"));
					quotation.setValue(result.getInt("Q.price"));
					quotation.setWorkerId(result.getInt("Q.workerId"));
					quotation.setWorkerUsername(result.getString("W.username"));
					workerQuotations.add(quotation);
				}
			}
		}
		return workerQuotations;
	}
	
}